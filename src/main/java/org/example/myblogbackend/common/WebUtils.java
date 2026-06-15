package org.example.myblogbackend.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

/** Web 相关小工具。 */
public final class WebUtils {

    private WebUtils() {
    }

    /** 取访客 IP:优先 X-Forwarded-For 首段(经 Nginx 反代时),否则 remoteAddr。 */
    public static String clientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xff)) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
