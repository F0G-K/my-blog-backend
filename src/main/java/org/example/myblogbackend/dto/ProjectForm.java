package org.example.myblogbackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectForm {

    @NotBlank(message = "项目名不能为空")
    private String name;

    private String description;

    private String techStack;

    private String githubUrl;

    private Integer sort;
}
