package com.blossom.lineup.Waiting.service;

import com.blossom.lineup.LineManagement.util.EntranceTimeLimit;
import com.blossom.lineup.Member.CustomUserDetails;
import com.blossom.lineup.Member.CustomerRepository;
import com.blossom.lineup.Member.entity.Customer;
import com.blossom.lineup.Member.util.Role;
import com.blossom.lineup.Organization.entity.Organization;
import com.blossom.lineup.Organization.repository.OrganizationRepository;
import com.blossom.lineup.SecurityUtils;
import com.blossom.lineup.Waiting.entity.Waiting;
import com.blossom.lineup.Waiting.entity.request.WaitingRequest;
import com.blossom.lineup.Waiting.entity.response.PendingResponse;
import com.blossom.lineup.Waiting.entity.response.WaitingResponse;
import com.blossom.lineup.Waiting.repository.WaitingRepository;
import com.blossom.lineup.Waiting.util.EntranceStatus;
import com.blossom.lineup.base.Code;
import com.blossom.lineup.base.Response;
import com.blossom.lineup.base.exceptions.BusinessException;
import com.blossom.lineup.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WaitingServiceImpl implements WaitingService{

    private final WaitingRepository waitingRepository;
    private final CustomerRepository customerRepository;
    private final OrganizationRepository organizationRepository;

    private final SecurityUtils securityUtils;
    private final RedisService redisService;

    @Value("${deploy.qrkey}")
    private String redisMemberQrKey;

    private Waiting findWaiting(Long waitingId){
        return waitingRepository.findById(waitingId)
                .orElseThrow(()->new BusinessException(Code.WAITING_NOT_FOUND));
    }

    private Customer findCustomer(){
        CustomUserDetails currentUserInfo = securityUtils.getCurrentUserInfo();

        if(!currentUserInfo.getRole().equals(Role.USER.getRole())){
            throw new BusinessException(Code.ROLE_ACCESS_DENIED);
        }
        return customerRepository.findById(currentUserInfo.getId())
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
        Optional<Waiting> checkBeforeWait = waitingRepository.findByCustomerAndEntranceStatusIn(customer,statuses);

        if (checkBeforeWait.isPresent()){
            throw new BusinessException(Code.WAITING_DUPLICATE);
        }

        log.info("[Waiting Create] {} 님이 {} 주점에 대기를 걸었습니다.", customer.getUserName(), organization.getName());

        Waiting waiting = Waiting.newWaiting(organization, customer, request.getHeadCount());
        waitingRepository.save(waiting);
    }

    @Override
    public void delete(Long waitingId) {
        Waiting w = findWaiting(waitingId);
        CustomUserDetails currentMember = securityUtils.getCurrentUserInfo();

        if(currentMember.getRole().equals(Role.MANAGER.getRole())){
            log.info("[Waiting Delete] ManagerId : {} - WaitingId : {}", currentMember.getId(), waitingId);
            waitingRepository.delete(w);
        }
        // USER이고, (대기를 건 사람)과 (현재 요청을 보낸사람)이 일치하는지 확인
        else if(currentMember.getRole().equals(Role.USER.getRole()) && currentMember.getId().equals(w.getCustomer().getId())){
            log.info("[Waiting Delete] CustomerId : {} ", w.getCustomer().getId());
            waitingRepository.delete(w);
        } else {
            log.error("[Waiting Delete] Role: {}, MemberId: {}, WaitingId: {}", currentMember.getRole(), currentMember.getId(), waitingId);
            throw new BusinessException(Code.WAITING_DELETE_DENIED);
        }
    }

    @Override
    public Resource getQrCodeAsMultipartFile(Long waitingId) {
        Waiting waiting = findWaiting(waitingId);

        // waiting이 "PENDING" 상태인지 확인
        if(waiting.getEntranceStatus()!=EntranceStatus.PENDING){
            throw new BusinessException(Code.WAITING_IS_NOT_PENDING);
        }

        // 요청하는 사용자 정보 일치 확인. (다른 Role 접근 거부)
        Customer customer = findCustomer();
        log.info("[QR-code] JwtCustomerId {} : WaitingCustomerId {}", customer.getId(), waiting.getCustomer().getId());

        if(!waiting.getCustomer().getId().equals(customer.getId())){
            throw new BusinessException(Code.WAITING_NOT_MATCH_USER);
        }

        // redis에서 qr코드를 가져오고, 없다면 에러처리.
        byte[] qrCodeData = redisService.getByteData(redisMemberQrKey + waitingId)
                .orElseThrow(()-> new BusinessException(Code.QRCODE_IS_NULL));

        // 바이트 데이터를 ByteArrayResource로 변환
        return new ByteArrayResource(qrCodeData);
    }

    /**
     * 대기를 걸었는지, 대기 상태에 따라 다른 응답 처리.
     * @return
     * 대기 안 걸은 USER, MANAGER, ADMIN, GUEST -> recentWaiting()
     * 대기 걸었고, WAITING 상태 -> myCurrentWaiting()
     * 대기 걸었고, PENDING 상태 -> getPendingStatus()
     */
    @Override
    public Response<?> getWaitingStatus(Long organizationId) {
        CustomUserDetails currentUserInfo = securityUtils.getCurrentUserInfo();

        // Customer가 아닌 다른 role을 가진 사용자이면, 마지막 대기자를 기준으로 대기 현황 조회 (NOT-WAITING)
        if(!currentUserInfo.getRole().equals(Role.USER.getRole())){
            log.info("[Waiting Status] Role: {}", currentUserInfo.getRole());
            return recentWaiting(organizationId); // NOT-WAITING
        }

        Customer customer = findCustomer();

        // 대기 걸어져있는지 확인
        List<EntranceStatus> statuses = Arrays.asList(EntranceStatus.WAITING, EntranceStatus.PENDING);
        Optional<Waiting> findWaiting = waitingRepository.findByCustomerAndEntranceStatusIn(customer,statuses);

        // 내가 대기 걸어둔 게 없을 때 -> NOT-WAITING
        if(findWaiting.isEmpty()){
            log.info("[Waiting Status] Customer: {} : NOT-WAITING", customer.getId());
            // 마지막 대기자 기준으로 대기 현황 조회
            return recentWaiting(organizationId);

        } else { // 걸어둔 대기가 있을 때, 상태값에 따라 다르게 처리.
            Waiting waiting = findWaiting.get();

            // 1. WAITING 상태
            if(waiting.getEntranceStatus() == EntranceStatus.WAITING) {
                log.info("[Waiting Status] Customer: {} : WAITING", customer.getId());
                return myCurrentWaiting(waiting,organizationId);
            }
            // 2. PENDING 상태
            else {
                log.info("[Waiting Status] Customer: {} : PENDING", customer.getId());
                return getPendingStatus(waiting);
            }
        }
    }

    /**
     * 대기 현황 조회
     * (NOT-WAITING)
     * -> 대기하지 않은 유저, 매니저, 어드민 등
     */
    private Response<WaitingResponse> recentWaiting(Long organizationId){
        String waitingStatus = "NOT-WAITING";
        Organization organization = findOrganization(organizationId);

        // 이전까지의 대기 팀 수
        long currentWaitingCount = waitingRepository.countWaitingByEntranceStatus(EntranceStatus.WAITING);

        // 주점 이용중인 팀 list.
        List<Waiting> usingTables = waitingRepository.findUsingTables(organization);
        // 모든 테이블 사용 개수
        int usingTableCount = usingTables.stream().map(Waiting::getTableCount).mapToInt(i->i).sum();

        // 테이블이 남는 경우 -> 바로 return (대기 시간 계산하지 않고 무조건 0분, 테이블이 남았으니까)
        if(organization.getTableCount()-usingTableCount>=1){
            return Response.ok(WaitingResponse.notWaiting(waitingStatus, currentWaitingCount, 0));
        }

        // 전체 테이블 남은 이용시간
        int[] remainTableTimes = new int[organization.getTableCount()];

        // 사용중인 테이블의 남는시간 계산(분)
        LocalDateTime now = LocalDateTime.now();
        int index = organization.getTableCount()-1;
        for(Waiting w : usingTables){
            LocalDateTime entranceTime = w.getUpdatedAt();
            Duration duration = Duration.between(entranceTime, now);
            int minutes = (int) duration.toMinutes();
            int remainMinutes = Math.max(organization.getTableTimeLimit() - minutes, 0);

            for(int i=0; i<w.getTableCount(); i++){
                if(index>=0){
                    remainTableTimes[index--] = remainMinutes;
                }
            }
        }

        log.info("대기시간 : "+Arrays.toString(remainTableTimes)+" / "+organization.getTableCount());

        int quotient = (int)currentWaitingCount / organization.getTableCount();  // 몫 : 모든 테이블이 몇 번 빠져야 하는지
        int remainder = (int)currentWaitingCount % organization.getTableCount(); // 나머지 : 몇번째 테이블에 들어가게 될지
        int expactWaitingTime = quotient * organization.getTableTimeLimit() + remainTableTimes[remainder];

        return Response.ok(WaitingResponse.notWaiting(waitingStatus, currentWaitingCount, expactWaitingTime));
    }

    /**
     * 대기 현황 조회
     * (WAITING)
     */
    private Response<WaitingResponse> myCurrentWaiting(Waiting waiting, Long organizationId) {

        Organization o = findOrganization(organizationId);
        long countBeforeMe = waitingRepository.countWaitingBeforeMe(waiting.getId());

        // 주점 이용중인 팀 list.
        List<Waiting> usingTables = waitingRepository.findUsingTables(o);
        // 모든 테이블 사용 개수
        int usingTableCount = usingTables.stream().map(Waiting::getTableCount).mapToInt(i->i).sum();

        // 테이블이 남는 경우 -> 바로 return (대기 시간 계산하지 않고 무조건 0분, 테이블이 남았으니까)
        if(o.getTableCount()-usingTableCount>=0){
            return  Response.ok(new WaitingResponse(waiting.getEntranceStatus().getEntranceStatus(), waiting.getId(), countBeforeMe, 0, waiting.getHeadCount()));
        }

        // 전체 테이블 남은 이용시간
        int[] remainTableTimes = new int[o.getTableCount()];

        // 사용중인 테이블의 남는시간 계산(분)
        LocalDateTime now = LocalDateTime.now();
        int index = o.getTableCount()-1;
        for(Waiting w : usingTables){
            LocalDateTime entranceTime = w.getUpdatedAt();
            Duration duration = Duration.between(entranceTime, now);
            int minutes = (int) duration.toMinutes();
            int remainMinutes = Math.max(o.getTableTimeLimit() - minutes, 0);

            for(int i=0; i<w.getTableCount(); i++){
                if(index>=0){
                    remainTableTimes[index--] = remainMinutes;
                }
            }
        }

        log.info("대기시간 : "+Arrays.toString(remainTableTimes)+" / "+o.getTableCount());

        int quotient = (int)countBeforeMe / o.getTableCount();  // 몫 : 모든 테이블이 몇 번 빠져야 하는지
        int remainder = (int)countBeforeMe % o.getTableCount(); // 나머지 : 몇번째 테이블에 들어가게 될지
        int expactWaitingTime = quotient * o.getTableTimeLimit() + remainTableTimes[remainder];

        return Response.ok(new WaitingResponse(waiting.getEntranceStatus().getEntranceStatus(), waiting.getId(), countBeforeMe, expactWaitingTime, waiting.getHeadCount()));
    }

    /**
     * 입장중 상태 조회
     * (PENDING)
     */
    private Response<PendingResponse> getPendingStatus(Waiting waiting) {

        long timeLimitInSeconds = EntranceTimeLimit.TEMP.getTime() *60; // 10분 -> 초단위

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(waiting.getUpdatedAt(), now);
        long durationInSeconds = duration.getSeconds();

        if(durationInSeconds > timeLimitInSeconds){ // PENDING 상태가 된지 10분이 넘어가면 에러.
            return Response.fail(Code.PENDING_TIME_LIMIT_EXPIRED.getCode(), Code.PENDING_TIME_LIMIT_EXPIRED.getMessage(), new PendingResponse("EXPIRED", waiting.getId(), 0L));
        }

        long remainMinutes = (timeLimitInSeconds - durationInSeconds) / 60; // 남은 시간을 분단위로 변경

        return Response.ok(new PendingResponse("PENDING", waiting.getId(), remainMinutes));
    }
}
