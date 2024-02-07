package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface VacancyMapper {
    @Mapping(source = "project", target = "projectId", qualifiedByName = "getProjectId")
    VacancyDto toDto(Vacancy vacancy);
    @Mapping(target = "projectId", ignore = true)
    Vacancy toEntity(VacancyDto vacancyDto);

    default Long getProjectId(Vacancy vacancy) {
        return vacancy.getProject().getId();
    }
}
