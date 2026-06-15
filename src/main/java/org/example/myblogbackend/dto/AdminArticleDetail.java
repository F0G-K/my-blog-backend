package org.example.myblogbackend.dto;

import lombok.Data;

import java.util.List;

/** 管理端文章详情(供编辑回填)。 */
@Data
public class AdminArticleDetail {

    private Long id;

    private String title;

    private String summary;

    private String content;

    private String coverUrl;

    private Long categoryId;

    private List<Long> tagIds;

    /** draft / published。 */
    private String status;
}
