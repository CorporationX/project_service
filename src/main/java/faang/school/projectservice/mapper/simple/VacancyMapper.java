package faang.school.projectservice.mapper.simple;

import faang.school.projectservice.dto.simple.TeamSimpleDto;
import faang.school.projectservice.dto.simple.VacancySimpleDto;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VacancyMapper {
    VacancySimpleDto toDto(Vacancy vacancy);
    Vacancy toEntity(VacancySimpleDto vacancyDto);
}
