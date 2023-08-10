package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.ResponseProjectDto;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.project.ResponseProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.mapper.project.UpdateProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ResponseProjectDto;
import faang.school.projectservice.mapper.project.CreateProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ResponseProjectMapper responseProjectMapper;
    private final List<ProjectFilter> projectFilters;
      private final UpdateProjectMapper updateProjectMapper;
      private final TeamMemberRepository teamMemberRepository;
    private final CreateProjectMapper createProjectMapper;

    @Transactional(readOnly = true)
    public List<ResponseProjectDto> getAllByFilter(ProjectFilterDto dto, long userId) {
        Stream<Project> projects = projectRepository.findAllByVisibilityOrOwnerId(ProjectVisibility.PUBLIC, userId).stream();

        for (ProjectFilter filter : projectFilters) {
            if (filter.isApplicable(dto)) {
                projects = filter.apply(projects, dto);
            }
        };

        return responseProjectMapper.toDtoList(projects.collect(Collectors.toList()));
    }

    @Transactional(readOnly = true)
    public List<ResponseProjectDto> getAll() {
        return responseProjectMapper.toDtoList(projectRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ResponseProjectDto getById(Long id) {
        return responseProjectMapper.toDto(projectRepository.getProjectById(id));
    }

    @Transactional
    public UpdateProjectDto update(UpdateProjectDto dto) {
        Project project = projectRepository.getProjectById(dto.getId());

        updateEntityFields(dto, project);

        project.setUpdatedAt(LocalDateTime.now());

        return updateProjectMapper.toDto(project);
    }

    private void updateEntityFields(UpdateProjectDto dto, Project project) {
        if (dto.getDescription() != null) {
            project.setDescription(dto.getDescription());
        }

        if (dto.getStatus() != null) {
            project.setStatus(dto.getStatus());
        }


    @Transactional
    public ResponseProjectDto create(CreateProjectDto dto, long userId) {
        Project project = createProjectMapper.toEntity(dto);

        validateCreateDtoAndMap(dto, project, userId);

        project.setCreatedAt(LocalDateTime.now());
        project.setStatus(ProjectStatus.CREATED);

        if (dto.getParentProjectId() != null) {
            project.setParentProject(projectRepository.getProjectById(dto.getParentProjectId()));
        }

        if (dto.getChildrenIds() != null && !dto.getChildrenIds().isEmpty()) {
            project.setChildren(projectRepository.findAllByIds(dto.getChildrenIds()));
        }

        return createProjectMapper.toDto(projectRepository.save(project));
    }

    private void validateCreateDtoAndMap(CreateProjectDto dto, Project project, long userId) {
        if (projectRepository.existsByOwnerUserIdAndName(userId, dto.getName())) {
            throw new IllegalArgumentException("User with id " + userId + " already has a project with name " + dto.getName());
        }

        project.setOwnerId(userId);
    }
}
