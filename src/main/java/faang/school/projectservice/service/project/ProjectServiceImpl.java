package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.exception.ExceptionMessages;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper mapper;

    @Override
    @Transactional
    public ProjectDto createProject(ProjectDto projectDto) {
        var isDuplicateProject = projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName());
        if (isDuplicateProject) {
            throw new IllegalArgumentException(ExceptionMessages.PROJECT_ALREADY_EXISTS_FOR_OWNER_ID);
        }
        Project savedProject;
        try {
            var projectToBeSaved = mapper.toEntity(projectDto);
            if (projectDto.getParentProjectId() != null) {
                var parentProject = projectRepository.getProjectById(projectDto.getParentProjectId());
                projectToBeSaved.setParentProject(parentProject);
            }
            savedProject = projectRepository.save(projectToBeSaved);
        } catch (DataIntegrityViolationException e) {
            throw new PersistenceException(ExceptionMessages.FAILED_PERSISTENCE, e);
        }
        return mapper.toDto(savedProject);
    }

    @Override
    @Transactional
    public ProjectDto updateProject(long id, ProjectUpdateDto projectUpdateDto) {
        var projectToUpdate = projectRepository.getProjectById(id);
        var updatedProject = mapper.update(projectUpdateDto, projectToUpdate);
        updatedProject.setUpdatedAt(LocalDateTime.now());
        projectRepository.save(updatedProject);
        return mapper.toDto(updatedProject);
    }

    @Override
    public ProjectDto retrieveProject(long id) {
        return mapper.toDto(projectRepository.getProjectById(id));
    }

    @Override
    public List<ProjectDto> getAllProjects() {
        List<Project> allProjects;
        try {
            allProjects = projectRepository.findAll();
        } catch (Exception e) {
            log.error("Error occurred while fetching all projects", e);
            throw new PersistenceException(ExceptionMessages.FAILED_RETRIEVAL, e);
        }
        return mapper.toDto(allProjects);
    }
}
