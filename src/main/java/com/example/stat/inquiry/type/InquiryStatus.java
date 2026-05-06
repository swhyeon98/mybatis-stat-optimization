package com.example.stat.inquiry.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InquiryStatus {

	TOTAL("총계", "TOTAL"),
	RECEIVED("접수", "RECEIVED"),
	IN_PROGRESS("처리중", "IN_PROGRESS"),
	RESOLVED("해결", "RESOLVED"),
	ON_HOLD("보류", "ON_HOLD"),
	REOPENED("재문의", "REOPENED"),
	TRANSFERRED("이관", "TRANSFERRED"),
	UNRESOLVED("미처리", "UNRESOLVED");

	private final String label;
	private final String code;
}
