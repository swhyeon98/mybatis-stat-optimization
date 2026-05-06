package com.example.stat.inquiry.dto;

import java.util.List;

public record InquiryStatTable(
		List<InquiryStatColumn> columns,
		List<InquiryStatRow> rows
) {
}
