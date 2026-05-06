package com.example.stat.inquiry.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InquiryStatSearchCondition {

	private LocalDate startDate;
	private LocalDate endDate;
	private String category;
	private String status;
}
