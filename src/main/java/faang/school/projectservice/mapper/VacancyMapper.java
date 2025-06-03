package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VacancyMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", expression = "java(mapProjectIdToProject(dto.getProjectId()))")
    @Mapping(target = "candidates", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "salary", ignore = true)
    @Mapping(target = "workSchedule", ignore = true)
    @Mapping(target = "requiredSkillIds", ignore = true)
    @Mapping(target = "coverImageKey", ignore = true)
    Vacancy toVacancyEntity(CreateVacancyDto dto);

    default Project mapProjectIdToProject(Long projectId) {
        if (projectId == null) {
            throw new IllegalArgumentException("Project ID cannot be null");
        }
        Project project = new Project();
        project.setId(projectId);
        return project;
    }

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "status", target = "status")
    VacancyResponseDto toVacancyDto(Vacancy vacancy);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "candidates", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "salary", ignore = true)
    @Mapping(target = "workSchedule", ignore = true)
    @Mapping(target = "requiredSkillIds", ignore = true)
    @Mapping(target = "coverImageKey", ignore = true)
    void updateVacancyEntityFromVacancyDto(UpdateVacancyDto dto, @MappingTarget Vacancy vacancy);
}