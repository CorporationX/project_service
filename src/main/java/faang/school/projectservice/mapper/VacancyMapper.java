package faang.school.projectservice.mapper;


import faang.school.projectservice.dto.Vacancy.VacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface VacancyMapper {

    @Mapping(source = "project.id", target = "projectId")
    VacancyDto toDto(Vacancy vacancy);

    Vacancy toEntity(VacancyDto vacancyDto);

    List<VacancyDto> toDto(List<Vacancy> vacancyList);

    List<Vacancy> toEntity(List<VacancyDto> vacancyDtoList);

    void updateVacancy(@MappingTarget Vacancy vacancy, VacancyDto vacancyDto);
}
