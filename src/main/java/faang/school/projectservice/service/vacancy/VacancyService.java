package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;

import java.util.List;

public interface VacancyService {

    VacancyDto create(VacancyDto dto);

    VacancyDto update(VacancyDto dto);

    void delete(Long id);

    List<VacancyDto> findAll(VacancyFilterDto filter);

    VacancyDto findById(Long id);
}