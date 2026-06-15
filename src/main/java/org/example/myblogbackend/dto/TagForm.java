package org.example.myblogbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TagForm {

    @NotBlank(message = "标签名不能为空")
    @Size(max = 50, message = "标签名过长")
    private String name;
}
