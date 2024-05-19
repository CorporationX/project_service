package faang.school.projectservice.service.vacancy.filter;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class VacancyProjectFilter implements VacancyFilter {

    @Override
    public boolean isApplicable(VacancyFilterDto vacancyFilterDto) {
        return vacancyFilterDto.getProjectId() != 0L;
    }

    @Override
    public Stream<VacancyDto> filter(Stream<VacancyDto> vacancies, VacancyFilterDto vacancyFilterDto) {
        return vacancies.filter(vacancy -> vacancy.getProjectId() == vacancyFilterDto.getProjectId());
    }
}