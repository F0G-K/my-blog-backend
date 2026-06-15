package org.example.myblogbackend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/** 搜索结果项:ArticleListItem 字段 + 命中高亮片段。 */
@Data
public class SearchItem {

    private Long id;

    private String title;

    private String summary;

    private String coverUrl;

    private String categoryName;

    private List<String> tags;

    private Integer viewCount;

    private LocalDateTime publishedAt;

    /** 高亮片段(标题 / 正文)。 */
    private Highlight highlight;

    @Data
    public static class Highlight {
        private List<String> title;
        private List<String> content;
    }
}
