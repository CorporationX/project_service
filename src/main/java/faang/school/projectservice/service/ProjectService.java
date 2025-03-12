package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public void createProject(Long userId, ProjectDto projectDto) {
        Project project = projectMapper.projectDtoToProject(projectDto);
    }

    public void updateProjectStatus(Long userId, Long projectId, ProjectStatus status) {

    }

    public void updateProjectDescription(Long userId, Long projectId, String description) {

    }

    public List<ProjectDto> findProjectsByFilters(Long userId, ProjectFilterDto projectFilterDto) {
        return null;
    }

    public List<ProjectDto> getAllProjects() {
        return projectMapper.projectListToProjectDtoList(projectRepository.findAll());
    }

    public ProjectDto getProjectById(Long projectId) {
        return projectMapper.projectToProjectDto(
                projectRepository.findById(projectId)
                        .orElseThrow(() -> new EntityNotFoundException("Project not found")));
    }
}
