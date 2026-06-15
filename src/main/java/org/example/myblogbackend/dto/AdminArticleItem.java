package org.example.myblogbackend.dto;

import lombok.Data;

import java.time.LocalDateTime;

/** 管理端文章列表项(含草稿)。 */
@Data
public class AdminArticleItem {

    private Long id;

    private String title;

    /** draft / published。 */
    private String status;

    private String categoryName;

    private Integer viewCount;

    private LocalDateTime updatedAt;
}
