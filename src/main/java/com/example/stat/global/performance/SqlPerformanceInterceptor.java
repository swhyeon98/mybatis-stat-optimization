package com.example.stat.global.performance;

import java.util.Properties;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

@Component
@Intercepts({
		@Signature(type = Executor.class, method = "query", args = {
				MappedStatement.class,
				Object.class,
				RowBounds.class,
				ResultHandler.class
		}),
		@Signature(type = Executor.class, method = "query", args = {
				MappedStatement.class,
				Object.class,
				RowBounds.class,
				ResultHandler.class,
				CacheKey.class,
				BoundSql.class
		}),
		@Signature(type = Executor.class, method = "update", args = {
				MappedStatement.class,
				Object.class
		})
})
public class SqlPerformanceInterceptor implements Interceptor {

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		long startNanos = System.nanoTime();
		try {
			return invocation.proceed();
		} finally {
			SqlPerformanceContext.record(System.nanoTime() - startNanos);
		}
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
	}
}
