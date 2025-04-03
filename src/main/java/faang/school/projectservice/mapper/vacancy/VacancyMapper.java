package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyCoverDto;
import faang.school.projectservice.dto.vacancy.VacancyCreateDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {
    Vacancy toEntity(VacancyCoverDto dto);

    VacancyCoverDto toCoverDto(Vacancy entity);

    void update(@MappingTarget Vacancy entity, VacancyCoverDto dto);

    @Mapping(source = "projectId", target = "project.id")
    Vacancy toEntity(VacancyCreateDto dto);

    @Mapping(source = "projectId", target = "project.id")
    void toEntityUpdateVacancy(VacancyUpdateDto dto, @MappingTarget Vacancy entity);

    VacancyDto toDtoVacancy(Vacancy vacancy);

}
