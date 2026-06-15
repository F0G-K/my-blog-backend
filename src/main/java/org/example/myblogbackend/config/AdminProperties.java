package org.example.myblogbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** 管理员初始化配置(对应 application.yml 的 app.admin.*)。 */
@Data
@Component
@ConfigurationProperties(prefix = "app.admin")
public class AdminProperties {

    private String username;

    /** 明文密码,仅用于首次启动生成 BCrypt 哈希入库。 */
    private String password;
}
