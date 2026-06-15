package org.example.myblogbackend.dto;

import lombok.Data;

@Data
public class CategoryVO {

    private Long id;

    private String name;

    private long articleCount;
}
