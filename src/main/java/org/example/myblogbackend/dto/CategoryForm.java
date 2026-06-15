package org.example.myblogbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoryForm {

    @NotBlank(message = "分类名不能为空")
    @Size(max = 50, message = "分类名过长")
    private String name;
}
