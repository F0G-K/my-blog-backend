package org.example.myblogbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.myblogbackend.common.Result;
import org.example.myblogbackend.dto.IdVO;
import org.example.myblogbackend.dto.TagForm;
import org.example.myblogbackend.dto.TagVO;
import org.example.myblogbackend.service.TagService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "标签")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @Operation(summary = "标签列表(含文章数,标签云)")
    @GetMapping("/tags")
    public Result<List<TagVO>> list() {
        return Result.ok(tagService.list());
    }

    @Operation(summary = "标签列表(管理)")
    @GetMapping("/admin/tags")
    public Result<List<TagVO>> adminList() {
        return Result.ok(tagService.list());
    }

    @Operation(summary = "新建标签")
    @PostMapping("/admin/tags")
    public Result<IdVO> create(@Valid @RequestBody TagForm form) {
        return Result.ok(tagService.create(form));
    }

    @Operation(summary = "删除标签")
    @DeleteMapping("/admin/tags/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return Result.ok();
    }
}
