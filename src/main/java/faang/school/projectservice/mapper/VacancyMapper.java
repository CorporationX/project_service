package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VacancyMapper {
    @Mapping(target = "projectId", source = "project.id")
    VacancyDto toDto(Vacancy vacancy);

    @Mapping(target = "project", source = "projectId")
    Vacancy toEntity(VacancyDto vacancyDto);

    void updateVacancyFromDto(VacancyDto vacancyDto, @MappingTarget Vacancy vacancy);

    default Project mapProjectIdToProject(Long projectId) {
        if (projectId == null) {
            return null;
        }
        Project project = new Project();
        project.setId(projectId);
        return project;
    }
}
