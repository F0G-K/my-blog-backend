package org.example.myblogbackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/** 状态变更:文章发布/下架、评论审核共用 { "status": "..." }。 */
@Data
public class StatusRequest {

    @NotBlank(message = "状态不能为空")
    private String status;
}
