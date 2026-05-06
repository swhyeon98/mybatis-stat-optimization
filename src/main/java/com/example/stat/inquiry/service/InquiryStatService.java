package com.example.stat.inquiry.service;

import org.springframework.stereotype.Service;

import com.example.stat.inquiry.dto.InquiryStatSearchCondition;
import com.example.stat.inquiry.mapper.InquiryStatMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InquiryStatService {

	private final InquiryStatMapper inquiryStatMapper;

	public int countStatV1(InquiryStatSearchCondition condition, String category, String status) {
		return inquiryStatMapper.countStatV1(condition, category, status);
	}
}
