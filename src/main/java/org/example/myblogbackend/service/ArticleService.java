package org.example.myblogbackend.service;

import org.example.myblogbackend.common.PageResult;
import org.example.myblogbackend.dto.AdminArticleDetail;
import org.example.myblogbackend.dto.AdminArticleItem;
import org.example.myblogbackend.dto.ArchiveItem;
import org.example.myblogbackend.dto.ArticleDetail;
import org.example.myblogbackend.dto.ArticleForm;
import org.example.myblogbackend.dto.ArticleListItem;
import org.example.myblogbackend.dto.IdVO;

public interface ArticleService {

    // ---- 公开 ----
    PageResult<ArticleListItem> pageList(long page, long pageSize, Long categoryId, Long tagId);

    /** clientIp 用于 Redis 阅读量防刷(同访客时间窗内不重复计数)。 */
    ArticleDetail getDetail(Long id, String clientIp);

    PageResult<ArchiveItem> archives(long page, long pageSize);

    // ---- 管理 ----
    PageResult<AdminArticleItem> adminPage(String status, String keyword, long page, long pageSize);

    AdminArticleDetail adminDetail(Long id);

    IdVO create(ArticleForm form);

    void update(Long id, ArticleForm form);

    IdVO autosave(ArticleForm form);

    void changeStatus(Long id, String status);

    void delete(Long id);

    /** 把所有已发布文章回灌 ES,返回索引数量。 */
    int reindexAll();
}
