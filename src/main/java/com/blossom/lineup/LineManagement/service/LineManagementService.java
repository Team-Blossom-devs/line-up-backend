package com.blossom.lineup.LineManagement.service;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;

import com.blossom.lineup.LineManagement.dto.request.EntranceCompleteRequest;
import com.blossom.lineup.LineManagement.dto.response.LineListResponse;
import com.blossom.lineup.LineManagement.dto.response.WaitingDetailsResponse;
import com.blossom.lineup.base.Response;
import com.google.zxing.WriterException;

public interface LineManagementService {

	Response<LineListResponse> getWaitingListWithCursor(Long cursor, int size);

	Response<WaitingDetailsResponse> changeToPending(Long waitingId) throws WriterException, IOException;

	ResponseEntity<Void> changeToComplete(EntranceCompleteRequest request);

	Response<WaitingDetailsResponse> revertPendingToWaiting(Long waitingId);

}
