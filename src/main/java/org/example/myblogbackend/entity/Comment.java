package org.example.myblogbackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/** 评论(留言板式,最多二级)。 */
@Data
@TableName("comment")
public class Comment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long articleId;

    /** 父评论 ID:空 = 顶级,有值 = 挂在该顶级下的二级回复。 */
    private Long parentId;

    private String nickname;

    private String email;

    /** 内容(渲染前需 XSS 净化)。 */
    private String content;

    /** 状态:0 待审核 / 1 通过 / 2 拒绝。 */
    private Integer status;

    private LocalDateTime createdAt;
}
