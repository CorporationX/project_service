package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.VacancyCandidateDto;
import faang.school.projectservice.model.Vacancy;

public abstract class VacancyCandidateMapper {
    public abstract VacancyCandidateDto toDto(Vacancy vacancy);
}
