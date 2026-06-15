package org.example.myblogbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.myblogbackend.common.Result;
import org.example.myblogbackend.dto.SiteInfoForm;
import org.example.myblogbackend.dto.SiteInfoVO;
import org.example.myblogbackend.entity.SiteInfo;
import org.example.myblogbackend.service.SiteInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "站点信息")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SiteInfoController {

    private final SiteInfoService siteInfoService;

    @Operation(summary = "站点信息(公开,含统计数)")
    @GetMapping("/site/info")
    public Result<SiteInfoVO> info() {
        return Result.ok(siteInfoService.getInfo());
    }

    @Operation(summary = "站点信息(管理读取)")
    @GetMapping("/admin/site-info")
    public Result<SiteInfo> adminGet() {
        return Result.ok(siteInfoService.getForAdmin());
    }

    @Operation(summary = "站点信息(管理更新)")
    @PutMapping("/admin/site-info")
    public Result<Void> update(@RequestBody SiteInfoForm form) {
        siteInfoService.update(form);
        return Result.ok();
    }
}
