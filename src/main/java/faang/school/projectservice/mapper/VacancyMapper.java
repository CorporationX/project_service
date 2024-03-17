package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface VacancyMapper {
    @Mapping(source = "project", target = "projectId", qualifiedByName = "getProjectId")
    VacancyDto toDto(Vacancy vacancy);
    Vacancy toEntity(VacancyDto vacancyDto);

    @Named("getProjectId")
    default Long getProjectId(Project project) {
        return project.getId();
    }
}
