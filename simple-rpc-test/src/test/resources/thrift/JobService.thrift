namespace java org.sagesource.test.api

enum ResponseStatus {
    SUCCESS,
    FAIL,
}

struct Response {
    // 响应状态
    1: required ResponseStatus code;
    2: optional string message;
}

service JobService {
    Response execute(1: string jobName);
}