package org.example.myblogbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.myblogbackend.common.Result;
import org.example.myblogbackend.dto.IdVO;
import org.example.myblogbackend.dto.ProjectForm;
import org.example.myblogbackend.dto.ProjectVO;
import org.example.myblogbackend.service.ProjectService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "开源项目")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "项目列表(按 sort)")
    @GetMapping("/projects")
    public Result<List<ProjectVO>> list() {
        return Result.ok(projectService.list());
    }

    @Operation(summary = "项目列表(管理)")
    @GetMapping("/admin/projects")
    public Result<List<ProjectVO>> adminList() {
        return Result.ok(projectService.list());
    }

    @Operation(summary = "新建项目")
    @PostMapping("/admin/projects")
    public Result<IdVO> create(@Valid @RequestBody ProjectForm form) {
        return Result.ok(projectService.create(form));
    }

    @Operation(summary = "更新项目")
    @PutMapping("/admin/projects/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ProjectForm form) {
        projectService.update(id, form);
        return Result.ok();
    }

    @Operation(summary = "删除项目")
    @DeleteMapping("/admin/projects/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        projectService.delete(id);
        return Result.ok();
    }
}
