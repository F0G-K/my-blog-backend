package org.example.myblogbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** MinIO 配置项(对应 application.yml 的 minio.*)。 */
@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {

    /** S3 API 地址,如 http://localhost:9000 */
    private String endpoint;

    private String accessKey;

    private String secretKey;

    /** 桶名(图床)。 */
    private String bucket;
}
