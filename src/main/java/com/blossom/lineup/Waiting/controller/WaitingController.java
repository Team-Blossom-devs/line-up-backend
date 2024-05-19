package com.blossom.lineup.Waiting.controller;

import com.blossom.lineup.Waiting.entity.request.WaitingRequest;
import com.blossom.lineup.Waiting.service.WaitingService;
import com.blossom.lineup.base.Code;
import com.blossom.lineup.base.Response;
import com.blossom.lineup.base.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/waiting")
public class WaitingController {

    private final WaitingService waitingService;

    @PostMapping
    public Response<Void> create(@Validated @RequestBody WaitingRequest request){
        log.debug("대기 생성");

        waitingService.create(request);
        return Response.ok();
    }

    @DeleteMapping("/{waitingId}")
    public Response<Void> delete(@PathVariable("waitingId") long waitingId){
        log.debug("대기 삭제");

        waitingService.delete(waitingId);
        return Response.ok();
    }

    @GetMapping("/{organizationId}")
    public Response<?> getCurrentWaitingStatus(@PathVariable("organizationId") long organizationId){
        log.debug("organizationId : "+organizationId);

        return waitingService.getWaitingStatus(organizationId);
    }

    @GetMapping("/qr-code/{waitingId}")
    public ResponseEntity<Resource> getQrCode(@PathVariable("waitingId") long waitingId){
        log.debug("qrCode 요청 : {}" + waitingId);

        // Service에서 Resouece로 QR 코드 가져오기
        Resource qrCodeResource = waitingService.getQrCodeAsMultipartFile(waitingId);

        if(qrCodeResource == null){
            throw new BusinessException(Code.QRCODE_IS_NULL);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"qrCode.png\"")
                .body(qrCodeResource);
    }
}
