package faang.school.projectservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class VacancyFilterDto {

    private String name;
    private List<Long> skillIds;
}
