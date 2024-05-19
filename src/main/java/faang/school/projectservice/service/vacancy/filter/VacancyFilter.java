package faang.school.projectservice.service.vacancy.filter;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;

import java.util.stream.Stream;

public interface VacancyFilter {
    boolean isApplicable(VacancyFilterDto vacancyFilterDto);
    Stream<VacancyDto> filter(Stream<VacancyDto> vacancies, VacancyFilterDto vacancyFilterDto);
}