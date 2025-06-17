package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.vacancy.CreateVacancyDto;
import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyResponseDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {CandidateMapper.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", expression = "java(mapProjectIdToProject(dto.getProjectId()))")
    Vacancy toVacancyEntity(CreateVacancyDto dto);

    @Mapping(source = "project.id", target = "projectId")
    VacancyResponseDto toVacancyDto(Vacancy vacancy);

    @Mapping(target = "id", ignore = true)
    void updateVacancyEntityFromVacancyDto(UpdateVacancyDto dto, @MappingTarget Vacancy vacancy);

    default Project mapProjectIdToProject(Long projectId) {
        if (projectId == null) {
            throw new IllegalArgumentException("Project ID cannot be null");
        }
        Project project = new Project();
        project.setId(projectId);
        return project;
    }
}