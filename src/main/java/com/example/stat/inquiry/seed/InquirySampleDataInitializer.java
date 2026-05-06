package com.example.stat.inquiry.seed;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.stat.inquiry.mapper.InquiryStatMapper;
import com.example.stat.inquiry.type.InquiryCategory;
import com.example.stat.inquiry.type.InquiryStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InquirySampleDataInitializer implements CommandLineRunner {

	private static final int SAMPLE_SIZE = 3_200;
	private static final Random RANDOM = new Random(20260506);
	private static final String[] CHANNELS = {"WEB", "APP", "EMAIL", "PHONE"};
	private static final String[] PRIORITIES = {"LOW", "NORMAL", "HIGH", "URGENT"};
	private static final InquiryCategory[] CATEGORIES = {
			InquiryCategory.SERVICE_USAGE,
			InquiryCategory.PAYMENT_REFUND,
			InquiryCategory.BUG_ERROR,
			InquiryCategory.ACCOUNT_LOGIN,
			InquiryCategory.DELIVERY_BOOKING,
			InquiryCategory.ETC,
			InquiryCategory.PARTNERSHIP
	};
	private static final int[] CATEGORY_WEIGHTS = {25, 20, 17, 14, 12, 8, 4};

	private final InquiryStatMapper inquiryStatMapper;

	@Override
	public void run(String... args) {
		if (inquiryStatMapper.countAllInquiries() > 0) {
			return;
		}

		LocalDateTime anchorDateTime = LocalDateTime.of(2026, 5, 6, 12, 0);

		for (int i = 0; i < SAMPLE_SIZE; i++) {
			InquiryCategory category = pickCategory();
			InquiryStatus status = pickStatus(category);
			LocalDateTime createdAt = randomCreatedAt(anchorDateTime);
			LocalDateTime updatedAt = randomUpdatedAt(status, createdAt, anchorDateTime);

			inquiryStatMapper.insertSampleInquiry(
					category.getCode(),
					CHANNELS[RANDOM.nextInt(CHANNELS.length)],
					PRIORITIES[RANDOM.nextInt(PRIORITIES.length)],
					status.getCode(),
					category.getLabel() + " 문의 샘플 " + (i + 1),
					10_000L + RANDOM.nextInt(900),
					createdAt,
					updatedAt
			);
		}
	}

	private InquiryCategory pickCategory() {
		return pickWeighted(CATEGORIES, CATEGORY_WEIGHTS);
	}

	private InquiryStatus pickStatus(InquiryCategory category) {
		InquiryStatus[] statuses = {
				InquiryStatus.RESOLVED,
				InquiryStatus.IN_PROGRESS,
				InquiryStatus.RECEIVED,
				InquiryStatus.ON_HOLD,
				InquiryStatus.REOPENED,
				InquiryStatus.TRANSFERRED
		};

		int[] weights = switch (category) {
			case SERVICE_USAGE -> new int[] {72, 10, 6, 4, 4, 4};
			case PAYMENT_REFUND -> new int[] {62, 12, 7, 10, 5, 4};
			case BUG_ERROR -> new int[] {55, 17, 7, 6, 10, 5};
			case ACCOUNT_LOGIN -> new int[] {61, 14, 10, 5, 6, 4};
			case DELIVERY_BOOKING -> new int[] {66, 12, 8, 5, 4, 5};
			case PARTNERSHIP -> new int[] {60, 10, 6, 5, 4, 15};
			case ETC -> new int[] {63, 13, 9, 5, 5, 5};
			case ALL -> throw new IllegalArgumentException("ALL is not a stored inquiry category.");
		};

		return pickWeighted(statuses, weights);
	}

	private LocalDateTime randomCreatedAt(LocalDateTime anchorDateTime) {
		long minutesInYear = 365L * 24 * 60;
		return anchorDateTime
				.minusMinutes(RANDOM.nextLong(minutesInYear))
				.withSecond(RANDOM.nextInt(60));
	}

	private LocalDateTime randomUpdatedAt(InquiryStatus status, LocalDateTime createdAt, LocalDateTime anchorDateTime) {
		if (status == InquiryStatus.RECEIVED) {
			return null;
		}

		LocalDateTime updatedAt = createdAt.plusHours(1 + RANDOM.nextInt(240));
		if (updatedAt.isAfter(anchorDateTime)) {
			return anchorDateTime.minusMinutes(RANDOM.nextInt(180));
		}
		return updatedAt;
	}

	private <T> T pickWeighted(T[] values, int[] weights) {
		int totalWeight = 0;
		for (int weight : weights) {
			totalWeight += weight;
		}

		int point = RANDOM.nextInt(totalWeight);
		int cumulative = 0;
		for (int i = 0; i < values.length; i++) {
			cumulative += weights[i];
			if (point < cumulative) {
				return values[i];
			}
		}

		return values[values.length - 1];
	}
}
