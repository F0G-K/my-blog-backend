package org.example.myblogbackend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/** 文章详情(对外)。content 为 Markdown 原文,前端渲染。 */
@Data
public class ArticleDetail {

    private Long id;

    private String title;

    private String content;

    private String coverUrl;

    private String categoryName;

    private List<String> tags;

    private Integer viewCount;

    private LocalDateTime publishedAt;
}
