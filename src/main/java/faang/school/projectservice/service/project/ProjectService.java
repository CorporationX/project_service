package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.SubProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.subproject_filter.SubProjectFilter;
import faang.school.projectservice.service.project.update_subproject_param.UpdateSubProjectParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository repository;
    private final ProjectMapper mapper;
    private final List<UpdateSubProjectParam> updateSubProjectParams;
    private final List<SubProjectFilter> subProjectFilters;

    public ProjectDto createSubProject(ProjectDto projectDto) {
        validateCreateSubProject(projectDto);
        projectDto.setStatus(ProjectStatus.CREATED);
        Project project = mapper.toEntity(projectDto);
        return mapper.toDto(repository.save(project));
    }

    public ProjectDto updateSubProject(ProjectDto projectDto, UpdateSubProjectDto updateSubProjectDto) {
        List<UpdateSubProjectParam> updateParams = updateSubProjectParams.stream()
                .filter(param -> param.isExecutable(updateSubProjectDto))
                .toList();

        for (UpdateSubProjectParam param : updateParams) {
            if (param.isExecutable(updateSubProjectDto)) {
                param.execute(projectDto, updateSubProjectDto);
            }
        }

        if (!updateParams.isEmpty()) {
            Project returnProject = repository.save(mapper.toEntity(projectDto));
            projectDto = mapper.toDto(returnProject);
        }
        return projectDto;
    }

    public List<ProjectDto> getSubProject(ProjectDto projectDto, SubProjectFilterDto subProjectFilter) {
        Project project = repository.getProjectById(projectDto.getId());
        if (project.getChildren() == null) {
            return new ArrayList<>();
        }
        List<Project> projectChildren = project.getChildren();
        List<SubProjectFilter> filters = subProjectFilters.stream()
                .filter(filter -> filter.isApplecable(subProjectFilter))
                .toList();
        for(SubProjectFilter filter : filters) {
            projectChildren = filter.apply(projectChildren, subProjectFilter);
        }

        return mapper.toDto(projectChildren);
    }

    private void validateCreateSubProject(ProjectDto projectDto) {
        if (projectDto.getParentProject() == null || projectDto.getParentProject().getId() == 0) {
            throw new IllegalArgumentException("Подпроект должен иметь родительский проект");
        }

        if (projectDto.getVisibility() == ProjectVisibility.PRIVATE
                && projectDto.getParentProject().getVisibility() == ProjectVisibility.PUBLIC
        ) {
            throw new IllegalArgumentException("Нельзя создать приватный подпроект для публичного проекта");
        }

        if (projectDto.getVisibility() == ProjectVisibility.PUBLIC
                && projectDto.getParentProject().getVisibility() == ProjectVisibility.PRIVATE
        ) {
            throw new IllegalArgumentException("Нельзя создать публичный подпроект для приватного проекта");
        }
    }

    public ProjectDto getProject(long projectId) {
        Project project = repository.getProjectById(projectId);

        return mapper.toDto(project);
    }

    public List<ProjectDto> getProjectsByIds(List<Long> ids) {
        List<Project> projects = repository.findAllByIds(ids);

        return projects.stream().map(mapper::toDto).toList();
    }

    public Project getOneOrThrow(long projectId) {
        return repository.getProjectById(projectId);
    }
}
