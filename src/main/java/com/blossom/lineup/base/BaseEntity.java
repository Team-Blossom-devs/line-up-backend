package com.blossom.lineup.base;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public abstract class BaseEntity {

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	@Enumerated(value = EnumType.STRING)
	private ActiveStatus activeStatus = ActiveStatus.ACTIVATED;

	@PrePersist
	public void createdAt() {
		LocalDateTime now = LocalDateTime.now();
		this.createdAt = now;
		this.updatedAt = now;
	}

	@PreUpdate
	public void updatedAt() {
		this.updatedAt = LocalDateTime.now();
	}

	/**
	 * toggle active status
	 */
	public void updateActiveStatus() {
		if (this.activeStatus == ActiveStatus.ACTIVATED) {
			this.activeStatus = ActiveStatus.DELETED;
			return;
		}
		this.activeStatus = ActiveStatus.ACTIVATED;
	}

}
