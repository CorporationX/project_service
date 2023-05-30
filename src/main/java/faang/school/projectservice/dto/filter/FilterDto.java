package faang.school.projectservice.dto.filter;

import lombok.Data;

@Data
public class FilterDto {
    private String namePattern;
    private String descriptionPattern;
    private int page;
    private int pageSize;
}
