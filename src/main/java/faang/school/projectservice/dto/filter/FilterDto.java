package faang.school.projectservice.dto.filter;

import lombok.Data;

@Data
public abstract class FilterDto {
    protected String namePattern;
    protected String descriptionPattern;
    protected int page;
    protected int pageSize;
}
