package com.blossom.lineup.Waiting.entity.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PendingResponse {
    long remainMinutes; // 입장까지 남은 시간(분)
    String qrCodeId; // qr코드 아이디
}
