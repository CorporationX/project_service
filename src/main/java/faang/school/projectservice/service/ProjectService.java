package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.RequestFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.repository.specification.ProjectSpecification;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectValidator projectValidator;
    private final ProjectSpecification projectSpecification;
    private final TeamRepository teamRepository;

    @Transactional
    public ProjectDto create(ProjectDto projectDto, long creatorId) {

        projectValidator.validateUserExists(creatorId);
        projectValidator.checkIfUserProjectName(projectDto.getName(), creatorId);

        if(projectDto.getVisibility() != ProjectVisibility.PRIVATE){
            projectDto.setVisibility(ProjectVisibility.PUBLIC);
        }

        projectDto.setStatus(ProjectStatus.CREATED);
        projectDto.setOwnerId(creatorId);

        Project savedProject = projectRepository.save(projectMapper.toEntity(projectDto));
        return projectMapper.toDto(savedProject);
    }

    @Transactional
    public ProjectDto update(long projectId, ProjectDto projectDto) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new DataValidationException("Проекта с таким " + projectId + " не существует!"));
        projectMapper.updateProjectFromDto(projectDto, project);

        if (projectDto.getTeams() != null && !projectDto.getTeams().isEmpty()) {
            List<Team> teams = teamRepository.findAllById(projectDto.getTeams());
            project.setTeams(teams);
        }
        return projectMapper.toDto(project);
    }

    public Page<ProjectDto> findProjectsByFiltersAndAccess(RequestFilterDto filter, Pageable pageable) {

        if (filter.getUserId() != null) {
            projectValidator.validateUserExists(filter.getUserId());
        }
        Specification<Project> spec = projectSpecification.buildFilter(filter);

        Page<Project> result = projectRepository.findAll(spec, pageable);
        return result.map(projectMapper::toDto);
    }

    public ProjectDto findByIdProject(long projectId) {

        Project project = projectRepository.findById(projectId).orElseThrow(
                () -> new DataValidationException("Проекта с этим id: " + projectId + " не существует!"));
        return projectMapper.toDto(project);
    }

    public Page<ProjectDto> getAllProjects(Pageable pageable) {
        Page<Project> result = projectRepository.findAll(pageable);
        return result.map(projectMapper::toDto);
    }
}