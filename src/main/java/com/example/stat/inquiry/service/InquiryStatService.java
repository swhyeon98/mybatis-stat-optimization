package com.example.stat.inquiry.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.stat.inquiry.dto.InquiryStatColumn;
import com.example.stat.inquiry.dto.InquiryStatRow;
import com.example.stat.inquiry.dto.InquiryStatSearchCondition;
import com.example.stat.inquiry.dto.InquiryStatTable;
import com.example.stat.inquiry.mapper.InquiryStatMapper;
import com.example.stat.inquiry.type.InquiryCategory;
import com.example.stat.inquiry.type.InquiryStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InquiryStatService {

	private final InquiryStatMapper inquiryStatMapper;

	public int countStatV1(InquiryStatSearchCondition condition, String category, String status) {
		return inquiryStatMapper.countStatV1(condition, category, status);
	}

	public InquiryStatTable getStatTableV2(InquiryStatSearchCondition condition) {
		List<InquiryStatColumn> columns = List.of(
				new InquiryStatColumn("total", InquiryStatus.TOTAL.getLabel(), InquiryStatus.TOTAL.getCode()),
				new InquiryStatColumn("received", InquiryStatus.RECEIVED.getLabel(), InquiryStatus.RECEIVED.getCode()),
				new InquiryStatColumn("inProgress", InquiryStatus.IN_PROGRESS.getLabel(), InquiryStatus.IN_PROGRESS.getCode()),
				new InquiryStatColumn("resolved", InquiryStatus.RESOLVED.getLabel(), InquiryStatus.RESOLVED.getCode()),
				new InquiryStatColumn("onHold", InquiryStatus.ON_HOLD.getLabel(), InquiryStatus.ON_HOLD.getCode()),
				new InquiryStatColumn("reopened", InquiryStatus.REOPENED.getLabel(), InquiryStatus.REOPENED.getCode()),
				new InquiryStatColumn("transferred", InquiryStatus.TRANSFERRED.getLabel(), InquiryStatus.TRANSFERRED.getCode()),
				new InquiryStatColumn("unresolved", InquiryStatus.UNRESOLVED.getLabel(), InquiryStatus.UNRESOLVED.getCode())
		);

		List<InquiryCategory> categories = List.of(
				InquiryCategory.ALL,
				InquiryCategory.PAYMENT_REFUND,
				InquiryCategory.ACCOUNT_LOGIN,
				InquiryCategory.DELIVERY_BOOKING,
				InquiryCategory.BUG_ERROR,
				InquiryCategory.SERVICE_USAGE,
				InquiryCategory.PARTNERSHIP,
				InquiryCategory.ETC
		);

		List<InquiryStatRow> rows = new ArrayList<>();
		for (InquiryCategory category : categories) {
			Map<String, Integer> counts = new LinkedHashMap<>();
			for (InquiryStatColumn column : columns) {
				int count = inquiryStatMapper.countStatV1(condition, category.getCode(), column.statusCode());
				counts.put(column.key(), count);
			}
			rows.add(new InquiryStatRow(category.getLabel(), category.getCode(), counts));
		}

		return new InquiryStatTable(columns, rows);
	}
}
