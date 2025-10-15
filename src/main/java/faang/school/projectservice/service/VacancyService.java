package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.TeamRole;

import java.util.List;

public interface VacancyService {
    VacancyDto create(CreateVacancyDto vacancyDto);

    VacancyDto update(long vacancyId, UpdateVacancyDto vacancyDto);

    List<VacancyDto> filterVacancies(TeamRole position, String vacancyName);

    VacancyDto getVacancyById(Long vacancyId);


}