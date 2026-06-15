package org.example.myblogbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.myblogbackend.common.Result;
import org.example.myblogbackend.common.ResultCode;
import org.example.myblogbackend.common.WebUtils;
import org.example.myblogbackend.dto.CommentForm;
import org.example.myblogbackend.dto.CommentVO;
import org.example.myblogbackend.service.CommentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "评论(公开)")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "文章评论列表(仅已审核,顶级嵌套 replies)")
    @GetMapping("/articles/{id}/comments")
    public Result<List<CommentVO>> list(@PathVariable Long id) {
        return Result.ok(commentService.listByArticle(id));
    }

    @Operation(summary = "提交评论(待审核)")
    @PostMapping("/comments")
    public Result<Void> submit(@Valid @RequestBody CommentForm form, HttpServletRequest request) {
        commentService.submit(form, WebUtils.clientIp(request));
        return new Result<>(ResultCode.SUCCESS.getCode(), "评论已提交,待审核", null);
    }
}
