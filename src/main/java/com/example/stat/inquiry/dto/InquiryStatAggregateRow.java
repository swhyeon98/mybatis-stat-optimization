package com.example.stat.inquiry.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InquiryStatAggregateRow {

	private String categoryCode;
	private int totalCount;
	private int receivedCount;
	private int inProgressCount;
	private int resolvedCount;
	private int onHoldCount;
	private int reopenedCount;
	private int transferredCount;
	private int unresolvedCount;
}
