package com.example.stat.global.performance;

import java.io.IOException;

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

	private boolean isStatRequest(HttpServletRequest request) {
		String uri = request.getRequestURI();
		return "/inquiries/stats/v1".equals(uri)
				|| "/inquiries/stats/v2".equals(uri)
				|| "/inquiries/stats/v3".equals(uri);
	}
}
