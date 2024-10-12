package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Vacancy;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VacancyMapper {

    @Mapping(source = "project.id", target = "projectId")
    VacancyDto toVacancyDto(Vacancy vacancy);

    @Mapping(target = "id", ignore = true)
    Vacancy toEntity(CreateVacancyDto createVacancyDto);

    List<VacancyDto> toVacancyDtos(List<Vacancy> vacancies);
}

