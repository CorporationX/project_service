package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.vacancy.OpenVacancyRequestDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.mapper.vacancy.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public Optional<Project> getProjectByIdOrEmpty(long projectId) {
        return projectRepository.findById(projectId);
    }

    public Project validateAndGetProject(OpenVacancyRequestDto requestDto) {
        return getProjectByIdOrEmpty(requestDto.projectId())
                .orElseThrow(() -> new DataValidationException(
                        "Project with id %d is not found".formatted(requestDto.projectId())));
    }

    @Override
    public ProjectDto getProject(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.error("project with ID = {} not found".formatted(projectId));
                    throw new ProjectNotFoundException("project not found");
                });
        return projectMapper.toDto(project);
    }
}
