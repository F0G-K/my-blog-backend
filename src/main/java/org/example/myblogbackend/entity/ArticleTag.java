package org.example.myblogbackend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/** 文章-标签 关联(联合主键 article_id + tag_id)。 */
@Data
@TableName("article_tag")
public class ArticleTag {

    private Long articleId;

    private Long tagId;
}
