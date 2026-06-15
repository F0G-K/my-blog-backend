package org.example.myblogbackend.dto;

import lombok.Data;

import java.time.LocalDateTime;

/** 归档项(前端按年/月分组)。 */
@Data
public class ArchiveItem {

    private Long id;

    private String title;

    private LocalDateTime publishedAt;
}
