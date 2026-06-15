package org.example.myblogbackend.dto;

import lombok.Data;

import java.time.LocalDateTime;

/** 管理端评论列表项。 */
@Data
public class AdminCommentItem {

    private Long id;

    private String nickname;

    private String content;

    /** pending / approved / rejected。 */
    private String status;

    private String articleTitle;

    private LocalDateTime createdAt;
}
