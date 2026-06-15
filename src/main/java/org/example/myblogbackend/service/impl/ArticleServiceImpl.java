package org.example.myblogbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.myblogbackend.common.BusinessException;
import org.example.myblogbackend.common.PageResult;
import org.example.myblogbackend.common.ResultCode;
import org.example.myblogbackend.common.enums.ArticleStatus;
import org.example.myblogbackend.dto.AdminArticleDetail;
import org.example.myblogbackend.dto.AdminArticleItem;
import org.example.myblogbackend.dto.ArchiveItem;
import org.example.myblogbackend.dto.ArticleDetail;
import org.example.myblogbackend.dto.ArticleForm;
import org.example.myblogbackend.dto.ArticleListItem;
import org.example.myblogbackend.dto.IdVO;
import org.example.myblogbackend.entity.Article;
import org.example.myblogbackend.entity.ArticleTag;
import org.example.myblogbackend.entity.Category;
import org.example.myblogbackend.entity.Tag;
import org.example.myblogbackend.mapper.ArticleMapper;
import org.example.myblogbackend.mapper.ArticleTagMapper;
import org.example.myblogbackend.mapper.CategoryMapper;
import org.example.myblogbackend.mapper.TagMapper;
import org.example.myblogbackend.search.ArticleDoc;
import org.example.myblogbackend.search.ArticleSearchService;
import org.example.myblogbackend.service.ArticleService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final ArticleTagMapper articleTagMapper;
    private final ArticleSearchService searchService;
    private final StringRedisTemplate redis;

    // ============ 公开 ============

    @Override
    public PageResult<ArticleListItem> pageList(long page, long pageSize, Long categoryId, Long tagId) {
        Page<Article> p = new Page<>(page, pageSize);
        QueryWrapper<Article> qw = new QueryWrapper<Article>().eq("status", ArticleStatus.PUBLISHED.getCode());
        if (categoryId != null) {
            qw.eq("category_id", categoryId);
        }
        if (tagId != null) {
            List<Long> ids = articleTagMapper.selectList(
                            new QueryWrapper<ArticleTag>().eq("tag_id", tagId)).stream()
                    .map(ArticleTag::getArticleId).toList();
            if (ids.isEmpty()) {
                return new PageResult<>(List.of(), 0, page, pageSize);
            }
            qw.in("id", ids);
        }
        qw.orderByDesc("published_at");
        articleMapper.selectPage(p, qw);
        return new PageResult<>(toListItems(p.getRecords()), p.getTotal(), p.getCurrent(), p.getSize());
    }

    @Override
    public ArticleDetail getDetail(Long id, String clientIp) {
        Article a = articleMapper.selectById(id);
        if (a == null || a.getStatus() == null || a.getStatus() != ArticleStatus.PUBLISHED.getCode()) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }
        int viewCount = a.getViewCount() == null ? 0 : a.getViewCount();
        // Redis 防刷:同一访客 1 小时内不重复计数
        Boolean first = redis.opsForValue()
                .setIfAbsent("article:view:" + id + ":" + clientIp, "1", Duration.ofHours(1));
        if (Boolean.TRUE.equals(first)) {
            articleMapper.update(null, new UpdateWrapper<Article>()
                    .eq("id", id).setSql("view_count = view_count + 1"));
            viewCount += 1;
        }

        ArticleDetail d = new ArticleDetail();
        d.setId(a.getId());
        d.setTitle(a.getTitle());
        d.setContent(a.getContent());
        d.setCoverUrl(a.getCoverUrl());
        d.setCategoryName(categoryName(a.getCategoryId()));
        d.setTags(tagNames(a.getId()));
        d.setViewCount(viewCount);
        d.setPublishedAt(a.getPublishedAt());
        return d;
    }

    @Override
    public PageResult<ArchiveItem> archives(long page, long pageSize) {
        Page<Article> p = new Page<>(page, pageSize);
        QueryWrapper<Article> qw = new QueryWrapper<Article>()
                .eq("status", ArticleStatus.PUBLISHED.getCode())
                .select("id", "title", "published_at")
                .orderByDesc("published_at");
        articleMapper.selectPage(p, qw);
        List<ArchiveItem> items = p.getRecords().stream().map(a -> {
            ArchiveItem it = new ArchiveItem();
            it.setId(a.getId());
            it.setTitle(a.getTitle());
            it.setPublishedAt(a.getPublishedAt());
            return it;
        }).toList();
        return new PageResult<>(items, p.getTotal(), p.getCurrent(), p.getSize());
    }

    // ============ 管理 ============

    @Override
    public PageResult<AdminArticleItem> adminPage(String status, String keyword, long page, long pageSize) {
        Page<Article> p = new Page<>(page, pageSize);
        QueryWrapper<Article> qw = new QueryWrapper<>();
        if (StringUtils.hasText(status)) {
            qw.eq("status", ArticleStatus.fromApi(status).getCode());
        }
        if (StringUtils.hasText(keyword)) {
            qw.like("title", keyword);
        }
        qw.orderByDesc("updated_at");
        articleMapper.selectPage(p, qw);

        Map<Long, String> cats = categoryNames(p.getRecords());
        List<AdminArticleItem> items = p.getRecords().stream().map(a -> {
            AdminArticleItem it = new AdminArticleItem();
            it.setId(a.getId());
            it.setTitle(a.getTitle());
            it.setStatus(ArticleStatus.fromCode(a.getStatus()).getApi());
            it.setCategoryName(cats.get(a.getCategoryId()));
            it.setViewCount(a.getViewCount());
            it.setUpdatedAt(a.getUpdatedAt());
            return it;
        }).toList();
        return new PageResult<>(items, p.getTotal(), p.getCurrent(), p.getSize());
    }

    @Override
    public AdminArticleDetail adminDetail(Long id) {
        Article a = articleMapper.selectById(id);
        if (a == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }
        AdminArticleDetail d = new AdminArticleDetail();
        d.setId(a.getId());
        d.setTitle(a.getTitle());
        d.setSummary(a.getSummary());
        d.setContent(a.getContent());
        d.setCoverUrl(a.getCoverUrl());
        d.setCategoryId(a.getCategoryId());
        d.setTagIds(tagIdsOf(id));
        d.setStatus(ArticleStatus.fromCode(a.getStatus()).getApi());
        return d;
    }

    @Override
    @Transactional
    public IdVO create(ArticleForm form) {
        int statusCode = resolveStatus(form.getStatus(), ArticleStatus.DRAFT.getCode());
        validate(form, statusCode);
        Article a = new Article();
        applyForm(a, form);
        ensureInsertDefaults(a);
        a.setStatus(statusCode);
        a.setViewCount(0);
        if (statusCode == ArticleStatus.PUBLISHED.getCode()) {
            a.setPublishedAt(LocalDateTime.now().withNano(0));
        }
        articleMapper.insert(a);
        saveTags(a.getId(), form.getTagIds());
        syncEs(a);
        return new IdVO(a.getId());
    }

    @Override
    @Transactional
    public void update(Long id, ArticleForm form) {
        Article a = articleMapper.selectById(id);
        if (a == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }
        int statusCode = resolveStatus(form.getStatus(), a.getStatus());
        validate(form, statusCode);
        applyForm(a, form);
        a.setStatus(statusCode);
        if (statusCode == ArticleStatus.PUBLISHED.getCode() && a.getPublishedAt() == null) {
            a.setPublishedAt(LocalDateTime.now().withNano(0));
        }
        articleMapper.updateById(a);
        rebindTags(id, form.getTagIds());
        syncEs(a);
    }

    @Override
    @Transactional
    public IdVO autosave(ArticleForm form) {
        if (form.getCategoryId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "自动保存前请先选择分类");
        }
        if (form.getId() == null) {
            Article a = new Article();
            applyForm(a, form);
            ensureInsertDefaults(a);
            a.setStatus(ArticleStatus.DRAFT.getCode());
            a.setViewCount(0);
            articleMapper.insert(a);
            saveTags(a.getId(), form.getTagIds());
            return new IdVO(a.getId());
        }
        Article a = articleMapper.selectById(form.getId());
        if (a == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "草稿不存在");
        }
        applyForm(a, form);   // 保持原状态(可能是草稿,也可能是已发布在编辑)
        articleMapper.updateById(a);
        rebindTags(form.getId(), form.getTagIds());
        return new IdVO(form.getId());
    }

    @Override
    @Transactional
    public void changeStatus(Long id, String status) {
        Article a = articleMapper.selectById(id);
        if (a == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }
        ArticleStatus st = ArticleStatus.fromApi(status);
        a.setStatus(st.getCode());
        if (st == ArticleStatus.PUBLISHED && a.getPublishedAt() == null) {
            a.setPublishedAt(LocalDateTime.now().withNano(0));
        }
        articleMapper.updateById(a);
        syncEs(a);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (articleMapper.selectById(id) == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文章不存在");
        }
        articleMapper.deleteById(id);   // 逻辑删除
        articleTagMapper.delete(new QueryWrapper<ArticleTag>().eq("article_id", id));
        searchService.delete(id);
    }

    // ============ 私有辅助 ============

    private int resolveStatus(String api, Integer fallback) {
        if (!StringUtils.hasText(api)) {
            return fallback == null ? ArticleStatus.DRAFT.getCode() : fallback;
        }
        return ArticleStatus.fromApi(api).getCode();
    }

    private void validate(ArticleForm form, int statusCode) {
        if (form.getCategoryId() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "请选择分类");
        }
        if (statusCode == ArticleStatus.PUBLISHED.getCode()) {
            if (!StringUtils.hasText(form.getTitle())) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "标题不能为空");
            }
            if (!StringUtils.hasText(form.getContent())) {
                throw new BusinessException(ResultCode.PARAM_ERROR, "正文不能为空");
            }
        }
    }

    private void applyForm(Article a, ArticleForm form) {
        a.setTitle(form.getTitle());
        a.setSummary(form.getSummary());
        a.setContent(form.getContent());
        a.setCoverUrl(form.getCoverUrl());
        a.setCategoryId(form.getCategoryId());
    }

    /** 新增时为 NOT NULL 且无默认值的列兜底(草稿可能没填 title/content)。 */
    private void ensureInsertDefaults(Article a) {
        if (a.getTitle() == null) {
            a.setTitle("");
        }
        if (a.getSummary() == null) {
            a.setSummary("");
        }
        if (a.getContent() == null) {
            a.setContent("");
        }
    }

    /** 已发布 → 写入/更新 ES;否则从 ES 移除。 */
    private void syncEs(Article a) {
        if (a.getStatus() != null && a.getStatus() == ArticleStatus.PUBLISHED.getCode()) {
            searchService.save(buildDoc(a));
        } else {
            searchService.delete(a.getId());
        }
    }

    private ArticleDoc buildDoc(Article a) {
        ArticleDoc doc = new ArticleDoc();
        doc.setId(a.getId());
        doc.setTitle(a.getTitle());
        doc.setContent(a.getContent());
        doc.setTags(tagNames(a.getId()));
        doc.setSummary(a.getSummary());
        doc.setCoverUrl(a.getCoverUrl());
        doc.setCategoryName(categoryName(a.getCategoryId()));
        doc.setViewCount(a.getViewCount());
        doc.setPublishedAt(a.getPublishedAt());
        return doc;
    }

    @Override
    public int reindexAll() {
        List<Article> published = articleMapper.selectList(
                new QueryWrapper<Article>().eq("status", ArticleStatus.PUBLISHED.getCode()));
        for (Article a : published) {
            searchService.save(buildDoc(a));
        }
        return published.size();
    }

    private void rebindTags(Long articleId, List<Long> tagIds) {
        articleTagMapper.delete(new QueryWrapper<ArticleTag>().eq("article_id", articleId));
        saveTags(articleId, tagIds);
    }

    private void saveTags(Long articleId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        for (Long tagId : tagIds.stream().filter(Objects::nonNull).distinct().toList()) {
            ArticleTag at = new ArticleTag();
            at.setArticleId(articleId);
            at.setTagId(tagId);
            articleTagMapper.insert(at);
        }
    }

    private String categoryName(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        Category c = categoryMapper.selectById(categoryId);
        return c == null ? null : c.getName();
    }

    private List<String> tagNames(Long articleId) {
        List<Long> tagIds = tagIdsOf(articleId);
        if (tagIds.isEmpty()) {
            return List.of();
        }
        Map<Long, String> names = tagMapper.selectBatchIds(tagIds).stream()
                .collect(Collectors.toMap(Tag::getId, Tag::getName));
        return tagIds.stream().map(names::get).filter(Objects::nonNull).toList();
    }

    private List<Long> tagIdsOf(Long articleId) {
        return articleTagMapper.selectList(new QueryWrapper<ArticleTag>().eq("article_id", articleId))
                .stream().map(ArticleTag::getTagId).toList();
    }

    private Map<Long, String> categoryNames(List<Article> articles) {
        Set<Long> ids = articles.stream().map(Article::getCategoryId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return Map.of();
        }
        return categoryMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));
    }

    /** 批量装配列表项:一次查分类名 + 一次查标签,避免 N+1。 */
    private List<ArticleListItem> toListItems(List<Article> articles) {
        if (articles.isEmpty()) {
            return List.of();
        }
        Map<Long, String> cats = categoryNames(articles);
        List<Long> articleIds = articles.stream().map(Article::getId).toList();
        List<ArticleTag> links = articleTagMapper.selectList(
                new QueryWrapper<ArticleTag>().in("article_id", articleIds));
        Set<Long> tagIds = links.stream().map(ArticleTag::getTagId).collect(Collectors.toSet());
        Map<Long, String> tagNames = tagIds.isEmpty() ? Map.of()
                : tagMapper.selectBatchIds(tagIds).stream()
                .collect(Collectors.toMap(Tag::getId, Tag::getName));
        Map<Long, List<String>> tagsByArticle = links.stream().collect(Collectors.groupingBy(
                ArticleTag::getArticleId,
                Collectors.mapping(l -> tagNames.get(l.getTagId()), Collectors.toList())));

        return articles.stream().map(a -> {
            ArticleListItem it = new ArticleListItem();
            it.setId(a.getId());
            it.setTitle(a.getTitle());
            it.setSummary(a.getSummary());
            it.setCoverUrl(a.getCoverUrl());
            it.setCategoryName(cats.get(a.getCategoryId()));
            it.setTags(tagsByArticle.getOrDefault(a.getId(), List.of()).stream()
                    .filter(Objects::nonNull).toList());
            it.setViewCount(a.getViewCount());
            it.setPublishedAt(a.getPublishedAt());
            return it;
        }).toList();
    }
}
