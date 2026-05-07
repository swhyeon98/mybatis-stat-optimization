package com.example.stat;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.stat.inquiry.dto.InquiryStatSearchCondition;
import com.example.stat.inquiry.service.InquiryStatService;

@SpringBootTest
class ApplicationTests {

	@Autowired
	private InquiryStatService inquiryStatService;

	@Test
	void contextLoads() {
	}

	@Test
	void v2AndV3StatTablesHaveSameCounts() {
		InquiryStatSearchCondition condition = new InquiryStatSearchCondition();

		assertThat(inquiryStatService.getStatTableV3(condition))
				.isEqualTo(inquiryStatService.getStatTableV2(condition));
	}
}
