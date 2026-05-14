package com.example.stat.global.performance;

import java.util.concurrent.TimeUnit;

public final class SqlPerformanceContext {

	private static final ThreadLocal<SqlPerformanceStats> STATS = new ThreadLocal<>();

	private SqlPerformanceContext() {
	}

	public static void start() {
		STATS.set(new SqlPerformanceStats());
	}

	public static void clear() {
		STATS.remove();
	}

	public static void record(long elapsedNanos) {
		SqlPerformanceStats stats = STATS.get();
		if (stats == null) {
			return;
		}

		stats.incrementSqlCount();
		stats.addSqlTimeNanos(elapsedNanos);
	}

	public static SqlPerformanceSnapshot snapshot() {
		SqlPerformanceStats stats = STATS.get();
		if (stats == null) {
			return new SqlPerformanceSnapshot(0, 0);
		}

		return new SqlPerformanceSnapshot(
				stats.getSqlCount(),
				TimeUnit.NANOSECONDS.toMillis(stats.getSqlTimeNanos())
		);
	}

	private static class SqlPerformanceStats {

		private int sqlCount;
		private long sqlTimeNanos;

		void incrementSqlCount() {
			sqlCount++;
		}

		void addSqlTimeNanos(long elapsedNanos) {
			sqlTimeNanos += elapsedNanos;
		}

		int getSqlCount() {
			return sqlCount;
		}

		long getSqlTimeNanos() {
			return sqlTimeNanos;
		}
	}
}
