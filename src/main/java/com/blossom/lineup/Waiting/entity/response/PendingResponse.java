package com.blossom.lineup.Waiting.entity.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PendingResponse {
    String waitingStatus;
    long remainMinutes; // 입장까지 남은 시간(분)
    long waitingId;     // qr-code key를 위한 waitingId
}
