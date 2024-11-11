package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Vacancy;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {

    @Mapping(source = "project.id", target = "projectId")
    VacancyDto toVacancyDto(Vacancy vacancy);

    @Mapping(target = "id", ignore = true)
    Vacancy toEntity(CreateVacancyDto createVacancyDto);

    List<VacancyDto> toVacancyDtos(List<Vacancy> vacancies);
}

