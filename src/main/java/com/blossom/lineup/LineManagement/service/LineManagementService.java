package com.blossom.lineup.LineManagement.service;

import java.io.IOException;

import com.blossom.lineup.LineManagement.dto.request.EntranceCompleteRequest;
import com.blossom.lineup.LineManagement.dto.response.EntranceSuccessResponse;
import com.blossom.lineup.LineManagement.dto.response.LineListResponse;
import com.blossom.lineup.LineManagement.dto.response.WaitingDetailsResponse;
import com.blossom.lineup.base.Response;
import com.google.zxing.WriterException;

public interface LineManagementService {

	Response<LineListResponse> getWaitingListWithCursor(Long cursor, int size);

	Response<WaitingDetailsResponse> changeToPending(Long waitingId) throws WriterException, IOException;

	Response<EntranceSuccessResponse> changeToComplete(EntranceCompleteRequest request);

	Response<WaitingDetailsResponse> revertPendingToWaiting(Long waitingId);

}
