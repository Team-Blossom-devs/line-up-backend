package com.blossom.lineup.LineManagement.controller;

import java.io.IOException;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.blossom.lineup.LineManagement.dto.request.EntranceCompleteRequest;
import com.blossom.lineup.LineManagement.dto.response.EntranceSuccessResponse;
import com.blossom.lineup.LineManagement.dto.response.LineListResponse;
import com.blossom.lineup.LineManagement.dto.response.WaitingDetailsResponse;
import com.blossom.lineup.LineManagement.service.LineManagementService;
import com.blossom.lineup.base.Response;
import com.google.zxing.WriterException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class LineManagementController {

	private final LineManagementService lineManagementService;

	@GetMapping("/waitings")
	Response<LineListResponse> findWaitingLists(
		@RequestParam(value = "cursor", required = false) Long cursor,
		@RequestParam(value = "size") int size) {

		return lineManagementService.getWaitingListWithCursor(cursor, size);
	}

	@PatchMapping("/complete")
	Response<EntranceSuccessResponse> entranceComplete(@Validated @RequestBody EntranceCompleteRequest request) {

		return lineManagementService.changeToComplete(request);
	}

	@PatchMapping("/entrance/{waitingId}")
	Response<WaitingDetailsResponse> waitingToPending(@PathVariable("waitingId") Long waitingId) throws
		IOException,
		WriterException {

		return lineManagementService.changeToPending(waitingId);
	}

	@PatchMapping("/restoration/{waitingId}")
	Response<WaitingDetailsResponse> revertPendingToWaiting(@PathVariable("waitingId") Long waitingId) {

		return lineManagementService.revertPendingToWaiting(waitingId);
	}

}
