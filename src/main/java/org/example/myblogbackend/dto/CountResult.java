package org.example.myblogbackend.dto;

import lombok.Data;

/** 分组计数结果(id → count),供分类/标签文章数统计。 */
@Data
public class CountResult {

    private Long id;

    private Long count;
}
