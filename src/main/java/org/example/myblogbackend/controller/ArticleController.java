package org.example.myblogbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.myblogbackend.common.PageResult;
import org.example.myblogbackend.common.Result;
import org.example.myblogbackend.common.WebUtils;
import org.example.myblogbackend.dto.ArchiveItem;
import org.example.myblogbackend.dto.ArticleDetail;
import org.example.myblogbackend.dto.ArticleListItem;
import org.example.myblogbackend.service.ArticleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "文章(公开)")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @Operation(summary = "文章列表(仅已发布)")
    @GetMapping("/articles")
    public Result<PageResult<ArticleListItem>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long tagId) {
        return Result.ok(articleService.pageList(page, pageSize, categoryId, tagId));
    }

    @Operation(summary = "文章详情(阅读量 +1,Redis 防刷)")
    @GetMapping("/articles/{id}")
    public Result<ArticleDetail> detail(@PathVariable Long id, HttpServletRequest request) {
        return Result.ok(articleService.getDetail(id, WebUtils.clientIp(request)));
    }

    @Operation(summary = "归档")
    @GetMapping("/archives")
    public Result<PageResult<ArchiveItem>> archives(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize) {
        return Result.ok(articleService.archives(page, pageSize));
    }
}
