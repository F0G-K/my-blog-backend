package org.example.myblogbackend.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** JWT 配置(对应 application.yml 的 jwt.*)。 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /** 签名密钥(HS256 至少 256bit,即 32 字节)。 */
    private String secret;

    /** 过期时间(分钟)。 */
    private long expireMinutes = 1440;
}
