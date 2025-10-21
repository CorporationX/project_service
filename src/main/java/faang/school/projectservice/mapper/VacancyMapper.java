package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {
    @Mapping(target = "project.id", source = "projectId")
    Vacancy toVacancy(CreateVacancyDto vacancyDto);

    void update(UpdateVacancyDto vacancyDto, @MappingTarget Vacancy vacancy);

    @Mapping(target = "projectId", source = "project.id")
    VacancyDto toVacancyDto(Vacancy vacancy);
}
