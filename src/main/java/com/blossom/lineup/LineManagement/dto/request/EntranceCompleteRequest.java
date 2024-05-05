package com.blossom.lineup.LineManagement.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EntranceCompleteRequest {

	@NotNull
	private Long id;
	@NotNull
	private Integer tableNumber;
	private Integer tableCount;

}
