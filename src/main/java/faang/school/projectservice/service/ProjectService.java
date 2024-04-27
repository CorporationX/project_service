package faang.school.projectservice.service;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectEvent;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.publishers.ProjectEventPublisher;
import faang.school.projectservice.validator.ProjectValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectMapper projectMapper;
    private final ProjectValidator projectValidator;

    @Value("#{new java.math.BigInteger('${storage.max_capacity}')}")
    private BigInteger maxCapacity;
    private final ProjectEventPublisher projectEventPublisher;

    private final List<ProjectFilter> projectFilters;

    public ProjectDto createProject(ProjectDto projectDto, long requestUserId) {
        //---------------------------------------------------------------------------
//        if (projectJpaRepository.existsByOwnerIdAndName(requestUserId, projectDto.getName())) {
//            throw new IllegalStateException("User ID " + requestUserId + " is already a member of the team associated with the existing project.");
//        }
        //---------------------------------------------------------------------------
        //That block of code looks quite suspicious and unreasonable to me.
        // As I have to count how many projects were created by one user.
        //But that line of code prevent form having an ability to create several projects, where the owner is the same user :(

        Project projectToSave = projectMapper.toEntity(projectDto, requestUserId);
        projectToSave.setMaxStorageSize(maxCapacity);
        Project savedProject = projectJpaRepository.save(projectToSave);

        ProjectEvent projectEvent = new ProjectEvent(projectDto.getOwnerId(), projectDto.getId());
        projectEventPublisher.publish(projectEvent);

        return projectMapper.toDto(savedProject);
    }

    public ProjectDto updateProject(long projectId, ProjectDto projectDto, long requestUserId) {
        Project existingProject = findProjectOrThrowException(projectId);

        projectValidator.checkUserIsMemberOrThrowException(existingProject, requestUserId);

        existingProject.setDescription(projectDto.getDescription() == null ? existingProject.getDescription() : projectDto.getDescription());
        existingProject.setStatus(projectDto.getStatus() == null ? existingProject.getStatus() : projectDto.getStatus());

        projectJpaRepository.save(existingProject);

        return projectMapper.toDto(existingProject);
    }

    @Transactional
    public List<ProjectDto> findAllProjectsByFilters(ProjectFilterDto filters, long requestUserId) {
        List<Project> projectList = projectJpaRepository.findProjectByOwnerIdAndTeamMember(requestUserId);
        if (projectList.isEmpty()) {
            throw new EntityNotFoundException("No projects found");
        }

        Stream<Project> projectStream = projectList.stream();

        for (ProjectFilter filter : projectFilters) {
            if (filter.isApplicable(filters)) {
                projectStream = filter.apply(projectStream, filters).stream();
            }
        }
        return projectStream.map(projectMapper::toDto).toList();
    }

    public List<ProjectDto> getAllProjects(long requestUserId) {
        List<Project> projectList = projectJpaRepository.findProjectByOwnerIdAndTeamMember(requestUserId);
        if (projectList.isEmpty()) {
            throw new EntityNotFoundException("No projects found");
        }
        return projectMapper.toDtoList(projectList);
    }

    public ProjectDto getProjectById(long projectId, long requestUserId) {
        Project project = findProjectOrThrowException(projectId);

        projectValidator.checkUserIsMemberOrThrowException(project, requestUserId);

        return projectMapper.toDto(project);
    }

    Project getProjectById(long projectId) {
        return projectJpaRepository.findById(projectId).orElseThrow(EntityNotFoundException::new);
    }

    Project save(Project project) {
        return projectJpaRepository.save(project);
    }


    private Project findProjectOrThrowException(long projectId) {
        return projectJpaRepository.findById(projectId).orElseThrow(() -> new EntityNotFoundException("Project with id " + projectId + " does not exist"));
    }


}
