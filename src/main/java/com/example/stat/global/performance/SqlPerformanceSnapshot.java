package com.example.stat.global.performance;

public record SqlPerformanceSnapshot(
		int sqlCount,
		long sqlTimeMs
) {
}
