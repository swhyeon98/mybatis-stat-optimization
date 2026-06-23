package com.example.stat;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.stat.global.performance.SqlPerformanceContext;
import com.example.stat.inquiry.dto.InquiryStatSearchCondition;
import com.example.stat.inquiry.dto.InquiryStatColumn;
import com.example.stat.inquiry.dto.InquiryStatRow;
import com.example.stat.inquiry.dto.InquiryStatTable;
import com.example.stat.inquiry.service.InquiryStatService;
import com.example.stat.inquiry.type.InquiryCategory;
import com.example.stat.inquiry.type.InquiryStatus;

@SpringBootTest
class ApplicationTests {

	@Autowired
	private InquiryStatService inquiryStatService;

	@Test
	void contextLoads() {
	}

	@Test
	void v1V2AndV3StatTablesHaveSameCounts() {
		InquiryStatSearchCondition condition = new InquiryStatSearchCondition();
		InquiryStatTable statTableV1 = getStatTableV1(condition);

		assertThat(inquiryStatService.getStatTableV2(condition)).isEqualTo(statTableV1);
		assertThat(inquiryStatService.getStatTableV3(condition)).isEqualTo(statTableV1);
	}

	@Test
	void sqlExecutionCountMatchesEachVersionStrategy() {
		InquiryStatSearchCondition condition = new InquiryStatSearchCondition();

		assertSqlCount(64, () -> getStatTableV1(condition));
		assertSqlCount(8, () -> inquiryStatService.getStatTableV2(condition));
		assertSqlCount(1, () -> inquiryStatService.getStatTableV3(condition));
	}

	private void assertSqlCount(int expectedSqlCount, Runnable runnable) {
		SqlPerformanceContext.start();
		try {
			runnable.run();
			assertThat(SqlPerformanceContext.snapshot().sqlCount()).isEqualTo(expectedSqlCount);
		} finally {
			SqlPerformanceContext.clear();
		}
	}

	private InquiryStatTable getStatTableV1(InquiryStatSearchCondition condition) {
		List<InquiryStatColumn> columns = statColumns();
		List<InquiryStatRow> rows = new ArrayList<>();
		for (InquiryCategory category : statCategories()) {
			Map<String, Integer> counts = new LinkedHashMap<>();
			for (InquiryStatColumn column : columns) {
				counts.put(
						column.key(),
						inquiryStatService.countStatV1(condition, category.getCode(), column.statusCode())
				);
			}
			rows.add(new InquiryStatRow(category.getLabel(), category.getCode(), counts));
		}

		return new InquiryStatTable(columns, rows);
	}

	private List<InquiryStatColumn> statColumns() {
		return List.of(
				new InquiryStatColumn("total", InquiryStatus.TOTAL.getLabel(), InquiryStatus.TOTAL.getCode()),
				new InquiryStatColumn("received", InquiryStatus.RECEIVED.getLabel(), InquiryStatus.RECEIVED.getCode()),
				new InquiryStatColumn("inProgress", InquiryStatus.IN_PROGRESS.getLabel(), InquiryStatus.IN_PROGRESS.getCode()),
				new InquiryStatColumn("resolved", InquiryStatus.RESOLVED.getLabel(), InquiryStatus.RESOLVED.getCode()),
				new InquiryStatColumn("onHold", InquiryStatus.ON_HOLD.getLabel(), InquiryStatus.ON_HOLD.getCode()),
				new InquiryStatColumn("reopened", InquiryStatus.REOPENED.getLabel(), InquiryStatus.REOPENED.getCode()),
				new InquiryStatColumn("transferred", InquiryStatus.TRANSFERRED.getLabel(), InquiryStatus.TRANSFERRED.getCode()),
				new InquiryStatColumn("unresolved", InquiryStatus.UNRESOLVED.getLabel(), InquiryStatus.UNRESOLVED.getCode())
		);
	}

	private List<InquiryCategory> statCategories() {
		return List.of(
				InquiryCategory.ALL,
				InquiryCategory.PAYMENT_REFUND,
				InquiryCategory.ACCOUNT_LOGIN,
				InquiryCategory.DELIVERY_BOOKING,
				InquiryCategory.BUG_ERROR,
				InquiryCategory.SERVICE_USAGE,
				InquiryCategory.PARTNERSHIP,
				InquiryCategory.ETC
		);
	}
}
