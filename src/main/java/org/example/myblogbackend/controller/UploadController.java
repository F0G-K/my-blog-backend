package org.example.myblogbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.myblogbackend.common.Result;
import org.example.myblogbackend.dto.UploadVO;
import org.example.myblogbackend.service.UploadService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "图片上传")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @Operation(summary = "上传图片(≤5MB)")
    @PostMapping("/upload")
    public Result<UploadVO> upload(@RequestParam("file") MultipartFile file) {
        return Result.ok(uploadService.upload(file));
    }
}
