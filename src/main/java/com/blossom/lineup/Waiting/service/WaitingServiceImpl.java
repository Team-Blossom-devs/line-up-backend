package com.blossom.lineup.Waiting.service;

import com.blossom.lineup.Member.CustomerRepository;
import com.blossom.lineup.Member.entity.Customer;
import com.blossom.lineup.Organization.entity.Organization;
import com.blossom.lineup.Organization.repository.OrganizationRepository;
import com.blossom.lineup.Waiting.entity.Waiting;
import com.blossom.lineup.Waiting.entity.request.WaitingRequest;
import com.blossom.lineup.Waiting.entity.response.PendingResponse;
import com.blossom.lineup.Waiting.entity.response.WaitingResponse;
import com.blossom.lineup.Waiting.repository.WaitingRepository;
import com.blossom.lineup.Waiting.util.EntranceStatus;
import com.blossom.lineup.base.Code;
import com.blossom.lineup.base.Response;
import com.blossom.lineup.base.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WaitingServiceImpl implements WaitingService{

    private final WaitingRepository waitingRepository;
    private final CustomerRepository customerRepository;
    private final OrganizationRepository organizationRepository;

    // TODO : Context Holder 구현 후, 권한 검사 추가 (ex- 내 대기번호가 아닌데 조회하는 경우)
    private Waiting findWaiting(Long waitingId){
        return waitingRepository.findById(waitingId)
                .orElseThrow(()->new BusinessException(Code.WAITING_NOT_FOUND));
    }

    // todo : jwt로 구현한 후에 회원 정보 가져오는 부분 변경
    private Customer findCustomer(){
        return customerRepository.findById(1L)
                .orElseThrow(()-> new BusinessException(Code.CUSTOMER_NOT_FOUND));
    }

    private Organization findOrganization(long id){
        return organizationRepository.findById(id)
                .orElseThrow(()-> new BusinessException(Code.ORGANIZATION_NOT_FOUND));
    }


    @Override
    public void create(WaitingRequest request) {
        Customer customer = findCustomer();
        Organization organization = findOrganization(request.getOrganizationId());

        // 내가 기존에 Waiting을 걸어놓은 곳이 있는지 확인. -> 2곳이상에 대기하려고 하면 error.
        List<EntranceStatus> statuses = Arrays.asList(EntranceStatus.WAITING, EntranceStatus.PENDING);
        List<Waiting> checkBeforeWait = waitingRepository.findByCustomerAndEntranceStatusIn(customer,statuses);

        if (!checkBeforeWait.isEmpty()){
            throw new BusinessException(Code.WAITING_DUPLICATE);
        }

        log.info(customer.getUserName()+" 님이 "+organization.getName()+ "주점에 대기를 걸었습니다.");

        Waiting waiting = Waiting.newWaiting(organization, customer, request.getHeadCount());
        waitingRepository.save(waiting);
    }

    @Override
    public void delete(Long waitingId) {
        log.debug(waitingId+" 대기를 삭제했습니다.");

        Waiting w = findWaiting(waitingId);
        waitingRepository.delete(w);
    }

    @Override
    public Response<?> getWaitingStatus(Long organizationId) {
        Customer customer = findCustomer();

        // 대기 걸어져있는지 확인
        List<EntranceStatus> statuses = Arrays.asList(EntranceStatus.WAITING, EntranceStatus.PENDING);
        List<Waiting> checkBeforeWait = waitingRepository.findByCustomerAndEntranceStatusIn(customer,statuses);

        // 내가 대기 걸어둔 게 없을 때 -> 마지막 대기 기준으로 대기현황조회.
        if(checkBeforeWait.isEmpty()){
            // 마지막 대기자 기준으로 대기 현황 조회
            Waiting last = waitingRepository
                    .findFirstByEntranceStatusOrderByCreatedAtDesc(EntranceStatus.WAITING).orElse(null);
            return Response.ok(myCurrentWaiting(last, organizationId));

        } else if(checkBeforeWait.size()==1){ // 걸어둔 대기가 1개일 때, 상태값에 따라 다르게 처리.
            Waiting waiting = checkBeforeWait.get(0);
            // 1. WAITING 상태
            if(waiting.getEntranceStatus() == EntranceStatus.WAITING) {
                return Response.ok(myCurrentWaiting(waiting,organizationId));
            }
            // 2. PENDING 상태
            else {
                return Response.ok(getPendingStatus(waiting));
            }
        } else {
            // todo : 중복 저장되면 가장 마지막에 들어온 Waiting 삭제?
            throw new BusinessException(Code.WAITING_DUPLICATE);
        }
    }

    /**
     * 대기 현황 조회
     * @param waiting
     * @param organizationId
     * @return
     */
    private WaitingResponse myCurrentWaiting(Waiting waiting, Long organizationId) {

        Organization o = findOrganization(organizationId);
        int beforeMeCnt = 0; // 대기가 하나도 없으면 내 앞에 대기 0팀.
        String waitingStatus = "NOT-WAITING";
        Integer headCount = null;

        // waiting이 있으면 내 대기번호 반환
        if(waiting != null){
            log.debug("대기 현황 조회 : "+waiting.getId());
            headCount = waiting.getHeadCount();
            List<Waiting> beforeMe = waitingRepository.findBeforeMyWaiting(o, waiting.getId()); // 나 이전의 대기 명단
            beforeMeCnt = beforeMe.size();
            waitingStatus = EntranceStatus.WAITING.getEntranceStatus();
        }

        // 이용 중인 테이블 명단
        List<Waiting> usingTables = waitingRepository.findUsingTables(o);

        // 정렬된 각각의 테이블의 남은 이용 시간(분)
        List<Integer> remainTableTimes = new java.util.ArrayList<>(usingTables.stream()
            .flatMap(w ->{
                // 대기시간이 null인 테이블이 존재하면, exception
                if(w.getEntranceTime()==null){
                    throw new BusinessException(Code.ENTRANCE_TIME_IS_NULL);
                } else {
                    LocalDateTime entranceTime = w.getEntranceTime();
                    LocalDateTime currentTime = LocalDateTime.now();
                    Duration duration = Duration.between(entranceTime,currentTime);
                    int minutes = (int) duration.toMinutes(); // 분단위로 변환
                    int remainMinutes = Math.max(o.getTableTimeLimit() - minutes, 0);

                    return java.util.stream.IntStream.range(0, w.getTableCount())
                            .mapToObj(i -> remainMinutes); // 각 tableCnt 개수만큼 지속시간을 반복
                }
            }).collect(Collectors.toList()));

        // 주점에서 다루는 테이블보다 테이블 이용 개수가 적으면, List에 0분 남은 개수만큼 추가.
        while(remainTableTimes.size() < o.getTableCount()){
            remainTableTimes.add(0,0);
        }

        log.info("대기시간 : "+remainTableTimes+" / "+o.getTableCount());

        int quotient = beforeMeCnt / o.getTableCount();  // 몫 : 모든 테이블이 몇 번 빠져야 하는지
        int remainder = beforeMeCnt % o.getTableCount(); // 나머지 : 몇번째 테이블에 들어가게 될지

        int expactWaitingTime = quotient * o.getTableTimeLimit() + remainTableTimes.get(remainder);

        return new WaitingResponse(waitingStatus, beforeMeCnt, expactWaitingTime, headCount);
    }

    /**
     * 입장중 상태 조회
     * @param waiting
     * @return
     */
    private PendingResponse getPendingStatus(Waiting waiting) {
        String waitingStatus = EntranceStatus.PENDING.getEntranceStatus();

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, waiting.getUpdatedAt());
        long remainMinutes = Math.max(0, duration.toMinutes()); // 남은시간 or 0 (분)

        return new PendingResponse(waitingStatus, remainMinutes, waiting.getId());
    }
}
