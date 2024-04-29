package com.blossom.lineup.Waiting;

import com.blossom.lineup.Member.CustomerRepository;
import com.blossom.lineup.Member.entity.Customer;
import com.blossom.lineup.Organization.OrganizationRepository;
import com.blossom.lineup.Organization.entity.Organization;
import com.blossom.lineup.Waiting.entity.Waiting;
import com.blossom.lineup.Waiting.entity.request.WaitingRequest;
import com.blossom.lineup.Waiting.entity.response.CheckWaitingStatus;
import com.blossom.lineup.Waiting.util.EntranceStatus;
import com.blossom.lineup.base.Code;
import com.blossom.lineup.base.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

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
        List<Waiting> checkBeforeWait = waitingRepository.findByCustomerAndEntranceStatus(customer,EntranceStatus.WAITING);
        if (!checkBeforeWait.isEmpty()){
            throw new BusinessException(Code.WAITING_DUPLICATE);
        }

        log.debug(customer.getUserName()+" 님이 "+organization.getName()+ "주점에 대기를 걸었습니다.");

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
    public CheckWaitingStatus myCurrentWaiting(Long waitingId) {
        log.debug("대기 현황 조회 : "+waitingId);

        Waiting me = findWaiting(waitingId);
        Organization o = me.getOrganization();

        // 나 이전의 대기 명단
        List<Waiting> beforeMe = waitingRepository.findBeforeMyWaiting(o, waitingId);
        int beforeMeCnt = beforeMe.size();

        // 이용 중인 테이블 명단
        List<Waiting> usingTables = waitingRepository.findUsingTables(o);

        // 정렬된 각각의 테이블의 남은 이용 시간(분)
        List<Integer> remainTableTimes = new java.util.ArrayList<>(usingTables.stream()
                .map(waiting -> {
                    // 대기시간이 null인 테이블이 존재하면, exception.
                    if (waiting.getEntranceTime() == null) {
                        throw new BusinessException(Code.ENTRANCE_TIME_IS_NULL);
                    }
                    //
                    else {
                        LocalDateTime entranceTime = waiting.getEntranceTime();
                        LocalDateTime currentTime = LocalDateTime.now();
                        Duration duration = Duration.between(entranceTime, currentTime);

                        return (int) duration.toMinutes(); // 분단위로 변환
                    }
                }).toList());

        // 주점에서 다루는 테이블보다 테이블 이용 개수가 적으면, List에 0분 남은 개수만큼 추가.
        while(remainTableTimes.size() < o.getSeatCount()){
            remainTableTimes.add(0,0);
        }

        int quotient = beforeMeCnt / o.getSeatCount();  // 몫 : 모든 테이블이 몇 번 빠져야 하는지
        int remainder = beforeMeCnt % o.getSeatCount(); // 나머지 : 몇번째 테이블에 들어가게 될지

        int expactWaitingTime = quotient * o.getTableTimeLimit() + remainTableTimes.get(remainder);

        return CheckWaitingStatus.of(beforeMeCnt, expactWaitingTime, me.getHeadCount());
    }
}
