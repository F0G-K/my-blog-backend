package org.example.myblogbackend.common.enums;

import lombok.Getter;

/**
 * 文章状态:对外字符串(draft/published) ↔ 数据库 TINYINT(0/1)。
 */
@Getter
public enum ArticleStatus {

    DRAFT(0, "draft"),
    PUBLISHED(1, "published");

    private final int code;
    private final String api;

    ArticleStatus(int code, String api) {
        this.code = code;
        this.api = api;
    }

    public static ArticleStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (ArticleStatus s : values()) {
            if (s.code == code) {
                return s;
            }
        }
        throw new IllegalArgumentException("未知的文章状态 code: " + code);
    }

    public static ArticleStatus fromApi(String api) {
        if (api == null) {
            return null;
        }
        for (ArticleStatus s : values()) {
            if (s.api.equalsIgnoreCase(api)) {
                return s;
            }
        }
        throw new IllegalArgumentException("未知的文章状态: " + api);
    }
}
