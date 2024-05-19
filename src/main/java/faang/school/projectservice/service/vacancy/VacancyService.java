package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;

import java.util.List;

public interface VacancyService {
    VacancyDto create(VacancyDto vacancyDto);
    VacancyDto update(VacancyDto vacancyDto);
    boolean delete(Long id);
    VacancyDto findById(Long id);
    List<VacancyDto> findAllDto();
    List<VacancyDto> findAllWithFilter(VacancyFilterDto vacancyFilterDto);
}