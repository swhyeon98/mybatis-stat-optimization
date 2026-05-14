import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const VERSION = __ENV.VERSION || 'v1';
const VUS = Number(__ENV.VUS || 1);
const DURATION = __ENV.DURATION || '30s';
const SLEEP = Number(__ENV.SLEEP ?? 1);

if (!/^v\d+$/.test(VERSION)) {
    throw new Error(`Invalid VERSION: ${VERSION}. Expected format: v1, v2, v3, ...`);
}

export const options = {
    vus: VUS,
    duration: DURATION,
    thresholds: {
        http_req_failed: ['rate<0.01'],
    },
};

export default function () {
    const res = http.get(`${BASE_URL}/inquiries/stats/${VERSION}`, {
        tags: { version: VERSION },
    });

    check(res, {
        'status is 200': (r) => r.status === 200,
    });

    sleep(SLEEP);
}
