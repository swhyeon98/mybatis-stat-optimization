package com.example.stat.inquiry.dto;

import java.util.Map;

public record InquiryStatRow(
		String label,
		String categoryCode,
		Map<String, Integer> counts
) {
}
