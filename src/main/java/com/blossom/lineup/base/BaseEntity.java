package com.blossom.lineup.base;

import java.time.LocalDateTime;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class BaseEntity {

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	@Enumerated(value = EnumType.STRING)
	private ActiveStatus activeStatus;

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
