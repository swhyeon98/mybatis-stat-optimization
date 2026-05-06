package com.example.stat.inquiry.mapper;

import java.time.LocalDateTime;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.stat.inquiry.dto.InquiryStatSearchCondition;

@Mapper
public interface InquiryStatMapper {

	int countStatV1(
			@Param("condition") InquiryStatSearchCondition condition,
			@Param("category") String category,
			@Param("status") String status
	);

	int countAllInquiries();

	void insertSampleInquiry(
			@Param("category") String category,
			@Param("channel") String channel,
			@Param("priority") String priority,
			@Param("status") String status,
			@Param("title") String title,
			@Param("customerId") long customerId,
			@Param("createdAt") LocalDateTime createdAt,
			@Param("updatedAt") LocalDateTime updatedAt
	);
}
