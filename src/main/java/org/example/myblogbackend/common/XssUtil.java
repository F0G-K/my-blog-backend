package org.example.myblogbackend.common;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

/**
 * XSS 净化:用于评论等 UGC 内容入库前清洗。
 * 策略 Safelist.none() —— 去除所有 HTML 标签,只保留纯文本。
 */
public final class XssUtil {

    private XssUtil() {
    }

    public static String clean(String input) {
        if (input == null) {
            return null;
        }
        return Jsoup.clean(input, Safelist.none());
    }
}
