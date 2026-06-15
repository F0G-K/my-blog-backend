package org.example.myblogbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.myblogbackend.common.Result;
import org.example.myblogbackend.dto.CategoryForm;
import org.example.myblogbackend.dto.CategoryVO;
import org.example.myblogbackend.dto.IdVO;
import org.example.myblogbackend.service.CategoryService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "分类")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "分类列表(含文章数)")
    @GetMapping("/categories")
    public Result<List<CategoryVO>> list() {
        return Result.ok(categoryService.list());
    }

    @Operation(summary = "分类列表(管理)")
    @GetMapping("/admin/categories")
    public Result<List<CategoryVO>> adminList() {
        return Result.ok(categoryService.list());
    }

    @Operation(summary = "新建分类")
    @PostMapping("/admin/categories")
    public Result<IdVO> create(@Valid @RequestBody CategoryForm form) {
        return Result.ok(categoryService.create(form));
    }

    @Operation(summary = "更新分类")
    @PutMapping("/admin/categories/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CategoryForm form) {
        categoryService.update(id, form);
        return Result.ok();
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/admin/categories/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.ok();
    }
}
