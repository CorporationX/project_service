package faang.school.projectservice.filter;

import lombok.Data;

@Data
public class VacancyFilterDto {
    private String namePattern;
    private String descriptionPattern;
}
