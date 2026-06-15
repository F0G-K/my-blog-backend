package org.example.myblogbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.myblogbackend.common.BusinessException;
import org.example.myblogbackend.common.PageResult;
import org.example.myblogbackend.common.XssUtil;
import org.example.myblogbackend.common.enums.CommentStatus;
import org.example.myblogbackend.dto.AdminCommentItem;
import org.example.myblogbackend.dto.CommentForm;
import org.example.myblogbackend.dto.CommentVO;
import org.example.myblogbackend.entity.Article;
import org.example.myblogbackend.entity.Comment;
import org.example.myblogbackend.mapper.ArticleMapper;
import org.example.myblogbackend.mapper.CommentMapper;
import org.example.myblogbackend.service.CommentService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final int RATE_LIMIT = 5;
    private static final Duration RATE_WINDOW = Duration.ofSeconds(60);

    private final CommentMapper commentMapper;
    private final ArticleMapper articleMapper;
    private final StringRedisTemplate redis;

    @Override
    public List<CommentVO> listByArticle(Long articleId) {
        List<Comment> all = commentMapper.selectList(new QueryWrapper<Comment>()
                .eq("article_id", articleId)
                .eq("status", CommentStatus.APPROVED.getCode())
                .orderByAsc("created_at"));

        // 顶级评论保持顺序;二级回复按 parent_id 分组
        Map<Long, CommentVO> tops = new LinkedHashMap<>();
        Map<Long, List<CommentVO>> repliesByParent = new java.util.HashMap<>();
        for (Comment c : all) {
            if (c.getParentId() == null) {
                tops.put(c.getId(), toVO(c));
            }
        }
        for (Comment c : all) {
            if (c.getParentId() != null) {
                repliesByParent.computeIfAbsent(c.getParentId(), k -> new ArrayList<>()).add(toVO(c));
            }
        }
        tops.forEach((id, vo) -> vo.setReplies(repliesByParent.getOrDefault(id, new ArrayList<>())));
        return new ArrayList<>(tops.values());
    }

    @Override
    public void submit(CommentForm form, String clientIp) {
        // 同 IP 限流:60s 内最多 5 条
        String key = "comment:rate:" + clientIp;
        Long count = redis.opsForValue().increment(key);
        if (count != null && count == 1L) {
            redis.expire(key, RATE_WINDOW);
        }
        if (count != null && count > RATE_LIMIT) {
            throw new BusinessException("评论太频繁,请稍后再试");
        }

        Comment c = new Comment();
        c.setArticleId(form.getArticleId());
        c.setParentId(form.getParentId());
        c.setNickname(XssUtil.clean(form.getNickname()));
        c.setEmail(form.getEmail());
        c.setContent(XssUtil.clean(form.getContent()));
        c.setStatus(CommentStatus.PENDING.getCode());
        commentMapper.insert(c);
    }

    @Override
    public PageResult<AdminCommentItem> adminPage(String status, long page, long pageSize) {
        Page<Comment> p = new Page<>(page, pageSize);
        QueryWrapper<Comment> qw = new QueryWrapper<Comment>().orderByDesc("created_at");
        if (status != null && !status.isBlank()) {
            qw.eq("status", CommentStatus.fromApi(status).getCode());
        }
        commentMapper.selectPage(p, qw);

        Map<Long, String> titles = articleTitles(p.getRecords());
        List<AdminCommentItem> items = p.getRecords().stream().map(c -> {
            AdminCommentItem item = new AdminCommentItem();
            item.setId(c.getId());
            item.setNickname(c.getNickname());
            item.setContent(c.getContent());
            item.setStatus(CommentStatus.fromCode(c.getStatus()).getApi());
            item.setArticleTitle(titles.get(c.getArticleId()));
            item.setCreatedAt(c.getCreatedAt());
            return item;
        }).toList();
        return new PageResult<>(items, p.getTotal(), p.getCurrent(), p.getSize());
    }

    @Override
    public void changeStatus(Long id, String status) {
        Comment c = commentMapper.selectById(id);
        if (c == null) {
            throw new BusinessException("评论不存在");
        }
        c.setStatus(CommentStatus.fromApi(status).getCode());
        commentMapper.updateById(c);
    }

    @Override
    public void delete(Long id) {
        if (commentMapper.selectById(id) == null) {
            throw new BusinessException("评论不存在");
        }
        commentMapper.deleteById(id);
    }

    private Map<Long, String> articleTitles(List<Comment> comments) {
        Set<Long> ids = comments.stream().map(Comment::getArticleId).collect(Collectors.toSet());
        if (ids.isEmpty()) {
            return Map.of();
        }
        return articleMapper.selectBatchIds(ids).stream()
                .collect(Collectors.toMap(Article::getId, Article::getTitle));
    }

    private CommentVO toVO(Comment c) {
        CommentVO vo = new CommentVO();
        vo.setId(c.getId());
        vo.setNickname(c.getNickname());
        vo.setContent(c.getContent());
        vo.setCreatedAt(c.getCreatedAt());
        // replies 仅顶级评论设置(在外层填充);二级回复保持 null
        return vo;
    }
}
