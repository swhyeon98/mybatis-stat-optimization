package com.example.stat.inquiry.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InquiryCategory {

	ALL("전체", null),
	PAYMENT_REFUND("결제/환불", "PAYMENT_REFUND"),
	ACCOUNT_LOGIN("계정/로그인", "ACCOUNT_LOGIN"),
	DELIVERY_BOOKING("배송/예약", "DELIVERY_BOOKING"),
	BUG_ERROR("오류/버그", "BUG_ERROR"),
	SERVICE_USAGE("서비스 이용", "SERVICE_USAGE"),
	PARTNERSHIP("제휴/광고", "PARTNERSHIP"),
	ETC("기타", "ETC");

	private final String label;
	private final String code;
}
