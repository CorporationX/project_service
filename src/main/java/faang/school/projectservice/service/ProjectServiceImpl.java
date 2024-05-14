package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validation.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService{
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);
    private final ProjectValidator projectValidator;
    private final UserContext userContext;

    @Override
    public ProjectDto create(ProjectDto projectDto) {
        if (projectDto.getOwnerId() == null) {
            projectDto.setOwnerId(userContext.getUserId());
        }
        projectValidator.validateProjectByOwnerIdAndNameOfProject(projectDto);
        projectDto.setStatus(ProjectStatus.CREATED);
        Project createdProject = projectRepository.save(projectMapper.toProject(projectDto));
        return projectMapper.toDto(createdProject);
    }

    @Override
    public ProjectDto update(ProjectDto projectDto) {
        Project project = projectRepository.getProjectById(projectDto.getId());
        projectMapper.updateProject(projectDto, project);
        return projectMapper.toDto(projectRepository.save(project));
        //todo ѕри этом дл€ аудита необходимо проставл€ть TIMESTAMP на каждое последние изменение проекта.
    }

    @Override
    public List<ProjectDto> getAll() {
        List<Project> projects = projectRepository.findAll();
        return projectMapper.toDtos(projects);
    }

    @Override
    public ProjectDto findById(long id) {
        Project project = projectRepository.getProjectById(id);
        return projectMapper.toDto(project);
    }

    @Override
    public List<ProjectDto> getAllByFilter(Predicate<ProjectDto> predicate) {
        List<Project> projects = projectRepository.findAll();

        Stream<Project> filteredProjects = projects.stream()
                .filter(project -> {
                    boolean isParticipant = true;
                    if(project.getVisibility().equals(ProjectVisibility.PRIVATE)){
                        isParticipant = project.getTeams().stream()
                            .flatMap(team->team.getTeamMembers().stream())
                            .anyMatch(teamMember -> teamMember.getId().equals(userContext.getUserId()));
                    }
                    return isParticipant;
                });

        return filteredProjects.map(projectMapper::toDto)
                .filter(predicate)
                .toList();
    }
}
