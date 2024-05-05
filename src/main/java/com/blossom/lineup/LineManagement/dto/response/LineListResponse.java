package com.blossom.lineup.LineManagement.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LineListResponse {

	private boolean hasNext;
	private LocalDateTime cursorTime;
	private Long cursorId;
	private List<WaitingDetailsResponse> waitingDetails;

}
