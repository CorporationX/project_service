package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.CreateVacancyDto;
import faang.school.projectservice.dto.DetailedVacancyDto;
import faang.school.projectservice.dto.UpdateVacancyDto;
import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VacancyMapper {

    @Mapping(target = "project", ignore = true)
    Vacancy toEntity(CreateVacancyDto dto);

    void update(@MappingTarget Vacancy vacancy, UpdateVacancyDto dto);

    VacancyDto toDto(Vacancy vacancy);

    @Mapping(target = "projectId", source = "project.id")
    DetailedVacancyDto toDetailedDto(Vacancy vacancy);
}
