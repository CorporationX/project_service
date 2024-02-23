package faang.school.projectservice.service.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.project.ProjectNameExistException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectJpaRepository projectRepository;
    private final ProjectFilterService projectFilterService;
    private final UserContext userContext;
    private final ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);

    @Override
    @Transactional
    public ProjectDto create(ProjectDto projectDto) {
        ensureOwnerIsSet(projectDto);
        validateOwnerIdAndNameExist(projectDto);
        projectDto.setStatus(ProjectStatus.CREATED);
        Project createdProject = projectRepository.save(projectMapper.toProject(projectDto));
        return projectMapper.toDto(createdProject);
    }

    @Override
    @Transactional
    public ProjectDto update(ProjectDto projectDto) {
        Project project = getProjectFromDB(projectDto.getId());
        project.setUpdatedAt(LocalDateTime.now());
        projectMapper.updateProject(projectDto, project);
        return projectMapper.toDto(project);
    }

    @Override
    public List<ProjectDto> getAll() {
        List<Project> projects = projectRepository.findAll();
        return projectMapper.toDtos(projects);
    }

    @Override
    public ProjectDto findById(@PathVariable Long id) {
        Project project = getProjectFromDB(id);
        return projectMapper.toDto(project);
    }

    @Override
    public List<ProjectDto> getAllByFilter(ProjectFilterDto filterDto) {
        List<Project> projects = projectRepository.findAll();
        Stream<ProjectDto> projectStream = projectMapper.toDtos(projects).stream();
        return projectFilterService.applyFilters(projectStream, filterDto).toList();
    }

    private Project getProjectFromDB(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Project with id = %d not exist", id)));
    }

    private void ensureOwnerIsSet(ProjectDto projectDto) {
        if (projectDto.getOwnerId() == null) {
            projectDto.setOwnerId(userContext.getUserId());
        }
    }

    private void validateOwnerIdAndNameExist(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new ProjectNameExistException(
                    String.format("This user already have a project with name : %s", projectDto.getName()));
        }
    }

}
