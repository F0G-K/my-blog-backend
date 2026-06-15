package org.example.myblogbackend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/** 评论(两级)。replies 仅顶级评论含,二级回复为 null。 */
@Data
public class CommentVO {

    private Long id;

    private String nickname;

    private String content;

    private LocalDateTime createdAt;

    private List<CommentVO> replies;
}
