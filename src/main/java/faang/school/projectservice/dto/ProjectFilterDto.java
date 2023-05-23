package faang.school.projectservice.dto;

import lombok.Data;

@Data
public class ProjectFilterDto {
    private String namePattern;
    private String descriptionPattern;
    private int page;
    private int pageSize;
}
