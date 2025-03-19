package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.VacancyCandidateDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class VacancyCandidateMapper {
    public abstract VacancyCandidateDto toDto(Vacancy vacancy);
}
