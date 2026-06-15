package org.example.myblogbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.myblogbackend.common.PageResult;
import org.example.myblogbackend.common.Result;
import org.example.myblogbackend.dto.SearchItem;
import org.example.myblogbackend.search.ArticleSearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "搜索")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SearchController {

    private final ArticleSearchService searchService;

    @Operation(summary = "全文检索(标题 + 正文 + 标签,带高亮)")
    @GetMapping("/search")
    public Result<PageResult<SearchItem>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long pageSize) {
        return Result.ok(searchService.search(keyword, page, pageSize));
    }
}
