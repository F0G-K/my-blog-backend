package org.example.myblogbackend.common;

import lombok.Getter;

/**
 * 业务返回码。code = 0 为成功,其余为业务/系统错误。
 * 数值与接口契约文档的 HTTP 语义对齐(400/401/403/404/500)。
 */
@Getter
public enum ResultCode {

    SUCCESS(0, "ok"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    ERROR(500, "服务器内部错误"),

    // 业务相关
    LOGIN_FAILED(1001, "用户名或密码错误"),
    TOKEN_INVALID(1002, "Token 无效或已过期");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
