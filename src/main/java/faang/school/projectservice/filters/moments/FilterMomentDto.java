package faang.school.projectservice.filters.moments;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class FilterMomentDto {
    private String namePattern;
    private String descriptionPattern;
    private LocalDateTime datePattern;
    private String resourcePattern;
    private String projectPattern;
    private String imagePattern;
    private LocalDateTime createdTimePattern;
    private LocalDateTime updatedTimePattern;
    private Long createdByPattern;
    private Long updatedByPattern;
}
