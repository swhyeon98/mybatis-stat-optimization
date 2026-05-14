package com.example.stat.global.performance;

import java.io.IOException;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RequestPerformanceLoggingFilter extends OncePerRequestFilter {

	private static final Pattern STAT_REQUEST_PATTERN =
			Pattern.compile("^/inquiries/stats/v\\d+$");

	private static final String HEADER_ELAPSED_MS = "X-Perf-Elapsed-Ms";
	private static final String HEADER_SQL_COUNT = "X-Perf-Sql-Count";
	private static final String HEADER_SQL_TIME_MS = "X-Perf-Sql-Time-Ms";

	@Value("${app.performance.log-enabled:false}")
	private boolean logEnabled;

	@Value("${app.performance.response-header-enabled:true}")
	private boolean responseHeaderEnabled;

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain
	) throws ServletException, IOException {
		if (!isStatRequest(request)) {
			filterChain.doFilter(request, response);
			return;
		}

		long startNanos = System.nanoTime();
		SqlPerformanceContext.start();

		try {
			filterChain.doFilter(request, response);
		} finally {
			long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000;
			SqlPerformanceSnapshot sql = SqlPerformanceContext.snapshot();
			SqlPerformanceContext.clear();

			if (responseHeaderEnabled) {
				addPerformanceHeaders(response, elapsedMs, sql);
			}

			if (logEnabled) {
				log.info(
						"[PERF] uri={} elapsedMs={} sqlCount={} sqlTimeMs={} thread={}",
						request.getRequestURI(),
						elapsedMs,
						sql.sqlCount(),
						sql.sqlTimeMs(),
						Thread.currentThread().getName()
				);
			}
		}
	}

	private void addPerformanceHeaders(
			HttpServletResponse response,
			long elapsedMs,
			SqlPerformanceSnapshot sql
	) {
		if (response.isCommitted()) {
			log.debug("Response already committed. Skip performance headers.");
			return;
		}

		response.setHeader(HEADER_ELAPSED_MS, String.valueOf(elapsedMs));
		response.setHeader(HEADER_SQL_COUNT, String.valueOf(sql.sqlCount()));
		response.setHeader(HEADER_SQL_TIME_MS, String.valueOf(sql.sqlTimeMs()));
	}

	private boolean isStatRequest(HttpServletRequest request) {
		String uri = request.getRequestURI();
		return STAT_REQUEST_PATTERN.matcher(uri).matches();
	}
}
