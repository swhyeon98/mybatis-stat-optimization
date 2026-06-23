import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const VERSION = __ENV.VERSION || 'v1';
const VUS = Number(__ENV.VUS || 1);
const DURATION = __ENV.DURATION || '30s';
const SLEEP = Number(__ENV.SLEEP ?? 1);

if (!/^v[123]$/.test(VERSION)) {
    throw new Error(`Invalid VERSION: ${VERSION}. Expected one of v1, v2, v3.`);
}

const appElapsedMs = new Trend('app_elapsed_ms');
const appSqlCount = new Trend('app_sql_count');
const appSqlTimeMs = new Trend('app_sql_time_ms');

export const options = {
    vus: VUS,
    duration: DURATION,
    thresholds: {
        http_req_failed: ['rate<0.01'],
    },
};

function getHeader(res, name) {
    const key = Object.keys(res.headers).find(
        (headerName) => headerName.toLowerCase() === name.toLowerCase()
    );

    return key ? res.headers[key] : undefined;
}

function addMetricIfPresent(metric, value) {
    const numberValue = Number(value);

    if (!Number.isNaN(numberValue)) {
        metric.add(numberValue);
    }
}

export default function () {
    const res = http.get(`${BASE_URL}/inquiries/stats/${VERSION}`, {
        tags: { version: VERSION },
    });

    check(res, {
        'status is 200': (r) => r.status === 200,
    });

    addMetricIfPresent(appElapsedMs, getHeader(res, 'X-Perf-Elapsed-Ms'));
    addMetricIfPresent(appSqlCount, getHeader(res, 'X-Perf-Sql-Count'));
    addMetricIfPresent(appSqlTimeMs, getHeader(res, 'X-Perf-Sql-Time-Ms'));

    sleep(SLEEP);
}
