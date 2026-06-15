package org.example.myblogbackend.dto;

import lombok.Data;

import java.util.List;

/**
 * 文章新建 / 更新 / 草稿自动保存共用。
 * 不在此做 bean 校验(autosave 允许半成品),必填项由 service 按场景校验。
 */
@Data
public class ArticleForm {

    /** autosave 可带 id(无 id 新建草稿)。 */
    private Long id;

    private String title;

    private String summary;

    /** 正文 Markdown 原文。 */
    private String content;

    private String coverUrl;

    private Long categoryId;

    private List<Long> tagIds;

    /** draft / published。 */
    private String status;
}
