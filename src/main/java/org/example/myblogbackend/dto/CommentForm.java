package org.example.myblogbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentForm {

    @NotNull(message = "文章 ID 不能为空")
    private Long articleId;

    /** 空 = 顶级评论;有值 = 挂在该顶级下的二级回复。 */
    private Long parentId;

    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称过长")
    private String nickname;

    /** 邮箱(选填)。 */
    @Size(max = 100, message = "邮箱过长")
    private String email;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论内容过长(最多 1000 字)")
    private String content;
}
