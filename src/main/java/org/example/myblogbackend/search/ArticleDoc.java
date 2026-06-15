package org.example.myblogbackend.search;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;

/** 文章的 ES 文档:检索 title + content + tags,其余字段用于直接拼装列表项。 */
@Data
@Document(indexName = "article")
public class ArticleDoc {

    @Id
    private Long id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Text)
    private List<String> tags;

    @Field(type = FieldType.Keyword)
    private String summary;

    @Field(type = FieldType.Keyword)
    private String coverUrl;

    @Field(type = FieldType.Keyword)
    private String categoryName;

    @Field(type = FieldType.Integer)
    private Integer viewCount;

    @Field(type = FieldType.Date,
            format = {DateFormat.date_hour_minute_second, DateFormat.date_optional_time})
    private LocalDateTime publishedAt;
}
