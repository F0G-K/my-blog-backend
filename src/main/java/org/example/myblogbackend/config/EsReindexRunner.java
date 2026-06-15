package org.example.myblogbackend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.myblogbackend.service.ArticleService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 启动时把已发布文章回灌 ES(幂等 upsert)。
 * 仅作 best-effort 回填:失败只告警,不阻断启动(运行期的同步/检索仍强依赖 ES)。
 */
@Slf4j
@Order(100)
@Component
@RequiredArgsConstructor
public class EsReindexRunner implements ApplicationRunner {

    private final ArticleService articleService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            int n = articleService.reindexAll();
            log.info("ES 回填完成,已索引 {} 篇已发布文章", n);
        } catch (Exception e) {
            log.warn("ES 回填失败(不影响启动): {}", e.getMessage());
        }
    }
}
