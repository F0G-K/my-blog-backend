package org.example.myblogbackend.common.enums;

import lombok.Getter;

/**
 * 评论状态:对外字符串(pending/approved/rejected) ↔ 数据库 TINYINT(0/1/2)。
 */
@Getter
public enum CommentStatus {

    PENDING(0, "pending"),
    APPROVED(1, "approved"),
    REJECTED(2, "rejected");

    private final int code;
    private final String api;

    CommentStatus(int code, String api) {
        this.code = code;
        this.api = api;
    }

    public static CommentStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (CommentStatus s : values()) {
            if (s.code == code) {
                return s;
            }
        }
        throw new IllegalArgumentException("未知的评论状态 code: " + code);
    }

    public static CommentStatus fromApi(String api) {
        if (api == null) {
            return null;
        }
        for (CommentStatus s : values()) {
            if (s.api.equalsIgnoreCase(api)) {
                return s;
            }
        }
        throw new IllegalArgumentException("未知的评论状态: " + api);
    }
}
