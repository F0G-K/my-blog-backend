package org.example.myblogbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 文章。 */
@Data
@TableName("article")
public class Article {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;

    private String summary;

    /** 正文(Markdown 原文)。 */
    private String content;

    /** 封面 URL(空 = 文字卡片)。 */
    private String coverUrl;

    private Long categoryId;

    /** 状态:0 草稿 / 1 已发布。 */
    private Integer status;

    private Integer viewCount;

    /** 逻辑删除:0 否 / 1 是。 */
    @TableLogic
    private Integer isDeleted;

    private LocalDateTime publishedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
