package org.example.myblogbackend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/** 文章列表项(对外)。coverUrl 非空 → 封面卡片,空 → 文字卡片。 */
@Data
public class ArticleListItem {

    private Long id;

    private String title;

    private String summary;

    private String coverUrl;

    private String categoryName;

    private List<String> tags;

    private Integer viewCount;

    private LocalDateTime publishedAt;
}
