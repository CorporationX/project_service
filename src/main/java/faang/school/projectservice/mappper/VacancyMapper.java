package faang.school.projectservice.mappper;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {

    @Mapping(source = "project.id", target = "projectId")
    VacancyDto toDto(Vacancy vacancy);

    @Mapping(target = "project", ignore = true)
    Vacancy toEntity(VacancyDto vacancyDto);
}
