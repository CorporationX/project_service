package faang.school.projectservice.service.project;

import faang.school.projectservice.repository.adapter.ProjectRepositoryAdapter;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataAlreadyExistException;
import faang.school.projectservice.exception.DataNotFoundException;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectRepositoryAdapter projectRepositoryAdapter;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;

    public ProjectDto createProject(ProjectDto projectDto) {
        projectDto.setName(nameAdjustment(projectDto.getName()));

        if (projectRepository.existsByOwnerIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataAlreadyExistException("The user already has a project with this name");
        }
        projectDto.setCreatedAt(LocalDateTime.now());
        projectDto.setStatus(ProjectStatus.CREATED);

        Project project = projectMapper.toEntity(projectDto);
        projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    public List<ProjectDto> getProjectsByIds(List<Long> ids) {
        return projectMapper.toDtoList(projectRepository.findAllById(ids));
    }

    @Transactional
    public ProjectDto updateProject(ProjectDto projectDto) {
        Project projectToUpdate = projectRepository.findById(projectDto.getId())
                .orElseThrow(() -> new DataNotFoundException("This project does not found"));

        projectMapper.update(projectDto, projectToUpdate);

        return projectMapper.toDto(projectToUpdate);
    }

    public List<ProjectDto> getAllAvailableProjectsForUserWithFilter(ProjectFilterDto filterDto, long userId) {
        Stream<Project> allAvailableProject = getAvailableProjectForUser(userId).stream();
        List<ProjectFilter> applicableFilter = projectFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .toList();
        for (ProjectFilter filter : applicableFilter) {
            allAvailableProject = filter.applyFilter(allAvailableProject, filterDto);
        }
        List<Project> projectsAfterFilters = allAvailableProject.toList();
        return projectsAfterFilters.stream().map(projectMapper::toDto).toList();
    }

    public List<ProjectDto> getAllAvailableProjectsForUser(long userId) {
        return getAvailableProjectForUser(userId).stream()
                .map(projectMapper::toDto)
                .toList();
    }

    public ProjectDto getProjectById(long id) {
        Project project = projectRepositoryAdapter.getById(id);
        return projectMapper.toDto(project);
    }

    public String nameAdjustment(String title) {
        return title.replaceAll("[^A-Za-zА-Яа-я0-9#/+-]", " ")
                .replaceAll("[\\s]+", " ")
                .trim()
                .toLowerCase();
    }

    private List<Project> getAvailableProjectForUser(long userId) {
        List<Project> allProjects = projectRepository.findAll();

        List<Project> availableProjects = new ArrayList<>();
        List<Project> privateProjects = new ArrayList<>();

        for (Project project : allProjects) {
            if (project.getVisibility() == ProjectVisibility.PUBLIC) {
                availableProjects.add(project);
            } else if (project.getVisibility() == ProjectVisibility.PRIVATE) {
                privateProjects.add(project);
            }
        }

        for (Project project : privateProjects) {
            boolean isAvailableCurrentUser = project.getTeams()
                    .stream()
                    .flatMap(team -> team.getTeamMembers()
                            .stream())
                    .anyMatch(teamMember -> teamMember.getUserId() == userId);

            if (isAvailableCurrentUser) {
                availableProjects.add(project);
            }
        }
        return availableProjects;
    }
}
