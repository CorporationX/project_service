package faang.school.projectservice.service.vacancy.filter;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;

import java.util.List;

public interface VacancyFilter {

    boolean isApplicable(VacancyFilterDto filters);

    void apply(List<Vacancy> vacancies, VacancyFilterDto filter);
}
