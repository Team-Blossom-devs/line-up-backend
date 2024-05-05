package com.blossom.lineup.LineManagement.dto.response;

import java.time.LocalDateTime;

import com.blossom.lineup.Waiting.entity.Waiting;
import com.blossom.lineup.Waiting.util.EntranceStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WaitingDetailsResponse {

	private Long id;
	private String name;
	private Integer tableNumber;
	private LocalDateTime entranceTime;
	private Integer headCount;
	private String phoneNumber;
	private EntranceStatus entranceStatus;

	public WaitingDetailsResponse(Long id, String name, Integer headCount, Integer tableNumber, EntranceStatus entranceStatus,
		LocalDateTime entranceTime){
		this.id = id;
		this.name = name;
		this.headCount = headCount;
		this.tableNumber = tableNumber;
		this.entranceStatus = entranceStatus;
		this.entranceTime = entranceTime;
	}

	public WaitingDetailsResponse(Long id, String name, Integer headCount, String phoneNumber, EntranceStatus entranceStatus) {
		this.id = id;
		this.name = name;
		this.headCount = headCount;
		this.phoneNumber = phoneNumber;
		this.entranceStatus = entranceStatus;
	}

	public WaitingDetailsResponse(Long id, String name, Integer headCount, String phoneNumber,
		EntranceStatus entranceStatus, LocalDateTime entranceTime) {
		this(id, name, headCount, phoneNumber, entranceStatus);
		this.entranceTime = entranceTime;
	}

	/**
	 * EntranceStatus 에 따른 반환값 변동
	 * @param waiting : 엔티티
	 * @return : WAITING: id, name, tableCount, phoneNumber, entranceStatus, PENDING: + entranceTime, COMPLETE : + tableNumber
	 */
	public static WaitingDetailsResponse fromEntity(Waiting waiting) {
		switch (waiting.getEntranceStatus()){
			case PENDING -> {
				return new WaitingDetailsResponse(waiting.getId(),
					waiting.getCustomer().getUserName(),
					waiting.getHeadCount(),
					waiting.getCustomer().getPhoneNumber(),
					waiting.getEntranceStatus(),
					waiting.getEntranceTime());
			}
			case COMPLETE -> {
				return new WaitingDetailsResponse(waiting.getId(),
					waiting.getCustomer().getUserName(),
					waiting.getHeadCount(),
					waiting.getTableNumber(),
					waiting.getEntranceStatus(),
					waiting.getEntranceTime());
			}
		}
		return new WaitingDetailsResponse(waiting.getId(),
			waiting.getCustomer().getUserName(),
			waiting.getHeadCount(),
			waiting.getCustomer().getPhoneNumber(),
			waiting.getEntranceStatus());
	}
}
