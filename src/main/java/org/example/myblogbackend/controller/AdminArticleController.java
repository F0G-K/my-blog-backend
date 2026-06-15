package org.example.myblogbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.myblogbackend.common.PageResult;
import org.example.myblogbackend.common.Result;
import org.example.myblogbackend.dto.AdminArticleDetail;
import org.example.myblogbackend.dto.AdminArticleItem;
import org.example.myblogbackend.dto.ArticleForm;
import org.example.myblogbackend.dto.IdVO;
import org.example.myblogbackend.dto.StatusRequest;
import org.example.myblogbackend.service.ArticleService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "文章管理")
@RestController
@RequestMapping("/api/admin/articles")
@RequiredArgsConstructor
public class AdminArticleController {

    private final ArticleService articleService;

    @Operation(summary = "文章列表(含草稿)")
    @GetMapping
    public Result<PageResult<AdminArticleItem>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize) {
        return Result.ok(articleService.adminPage(status, keyword, page, pageSize));
    }

    @Operation(summary = "文章详情(编辑回填)")
    @GetMapping("/{id}")
    public Result<AdminArticleDetail> detail(@PathVariable Long id) {
        return Result.ok(articleService.adminDetail(id));
    }

    @Operation(summary = "新建文章")
    @PostMapping
    public Result<IdVO> create(@RequestBody ArticleForm form) {
        return Result.ok(articleService.create(form));
    }

    @Operation(summary = "更新文章")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ArticleForm form) {
        articleService.update(id, form);
        return Result.ok();
    }

    @Operation(summary = "草稿自动保存")
    @PostMapping("/autosave")
    public Result<IdVO> autosave(@RequestBody ArticleForm form) {
        return Result.ok(articleService.autosave(form));
    }

    @Operation(summary = "发布 / 下架")
    @PatchMapping("/{id}/status")
    public Result<Void> changeStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        articleService.changeStatus(id, request.getStatus());
        return Result.ok();
    }

    @Operation(summary = "删除文章")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return Result.ok();
    }
}
