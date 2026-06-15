package org.example.myblogbackend.dto;

import lombok.Data;

@Data
public class ProjectVO {

    private Long id;

    private String name;

    private String description;

    private String techStack;

    private String githubUrl;
}
