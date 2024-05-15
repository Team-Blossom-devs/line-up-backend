package com.blossom.lineup.LineManagement.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.blossom.lineup.LineManagement.dto.request.EntranceCompleteRequest;
import com.blossom.lineup.LineManagement.dto.response.EntranceSuccessResponse;
import com.blossom.lineup.LineManagement.dto.response.LineListResponse;
import com.blossom.lineup.LineManagement.dto.response.WaitingDetailsResponse;
import com.blossom.lineup.LineManagement.util.EntranceTimeLimit;
import com.blossom.lineup.Member.CustomUserDetails;
import com.blossom.lineup.Member.ManagerRepository;
import com.blossom.lineup.Member.entity.Manager;
import com.blossom.lineup.Member.util.Role;
import com.blossom.lineup.SecurityUtils;
import com.blossom.lineup.Waiting.entity.Waiting;
import com.blossom.lineup.Waiting.repository.WaitingRepository;
import com.blossom.lineup.Waiting.util.EntranceStatus;
import com.blossom.lineup.base.Code;
import com.blossom.lineup.base.Response;
import com.blossom.lineup.base.exceptions.BusinessException;
import com.blossom.lineup.redis.RedisService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(timeout = 10)
@RequiredArgsConstructor
public class LineManagementServiceImpl implements LineManagementService {

	private final WaitingRepository waitingRepository;
	private final ManagerRepository managerRepository;
	private final RedisService redisService;
	private final SecurityUtils securityUtils;

	// TODO: 해당 url들 환경변수화
	// 프론트의 관리자 페이지로 리다이렉션
	public static final String redirectUrl = "http://localhost:8080";
	public static final String qrCodeBaseUrl = "http://localhost:8080/admin/entrance-process/";
	public static final String redisMemberQrKey = "member:qr:";


	@Override
	public Response<LineListResponse> getWaitingListWithCursor(Long cursor, int size) {

		CustomUserDetails currentUserInfo = checkCurrentUserRole();

		Manager manager = managerRepository.findWithOrganization(currentUserInfo.getId()).stream()
			.findAny()
			.orElseThrow();

		List<Waiting> waitings = waitingRepository.findWaitingByCursor(cursor, manager.getOrganization(), size);

		int findPageSize = waitings.size();
		int lastPageElementIndex = findPageSize > size ? size - 1 : findPageSize - 1;

		return Response.ok(
			LineListResponse.builder()
			.hasNext(findPageSize > size)
			.cursorId(waitings.get(lastPageElementIndex).getId())
			.waitingDetails(
				waitings.stream()
				.limit(Math.min(size, findPageSize))
				.map(WaitingDetailsResponse::fromEntity)
				.toList()
			)
			.build()
		);
	}



	@Override
	public Response<WaitingDetailsResponse> changeToPending(Long id) throws WriterException, IOException {

		checkCurrentUserRole();

		Waiting waiting = waitingRepository.findById(id).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(Code.WAITING_NOT_FOUND));

		waiting.updateEntranceStatus(EntranceStatus.PENDING);

		int width = 200;
		int height = 200;

		String url = qrCodeBaseUrl + id;

		BitMatrix encode = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, width, height);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(encode, "PNG", out);

		redisService.setByteData(redisMemberQrKey + id, out.toByteArray(), Duration.ofMinutes(EntranceTimeLimit.TEMP.getTime()));

		return Response.ok(
			WaitingDetailsResponse.fromEntity(waiting)
		);
	}

	@Override
	public Response<EntranceSuccessResponse> changeToComplete(EntranceCompleteRequest request) {

		Waiting waiting = waitingRepository.findById(request.getId()).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(Code.WAITING_NOT_FOUND));

		waiting.updateTables(request.getTableNumber(), request.getTableCount());
		waiting.updateEntranceStatus(EntranceStatus.COMPLETE);

		redisService.deleteData(redisMemberQrKey + request.getId());

		return Response.ok(new EntranceSuccessResponse(redirectUrl));
	}

	@Override
	public Response<WaitingDetailsResponse> revertPendingToWaiting(Long waitingId) {

		checkCurrentUserRole();

		Waiting waiting = waitingRepository.findById(waitingId).stream()
			.findAny()
			.orElseThrow(() -> new BusinessException(Code.WAITING_NOT_FOUND));

		waiting.updateEntranceStatus(EntranceStatus.WAITING);
		redisService.deleteData(redisMemberQrKey + waitingId);

		return Response.ok(
			WaitingDetailsResponse.fromEntity(waiting)
		);
	}

	private CustomUserDetails checkCurrentUserRole() {
		CustomUserDetails currentUserInfo = securityUtils.getCurrentUserInfo();

		if (!currentUserInfo.getRole().equals(Role.MANAGER.getRole())) {
			throw new BusinessException(Code.ADMIN_UNAUTHORIZED);
		}
		return currentUserInfo;
	}

}
