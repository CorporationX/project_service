package faang.school.projectservice.service;

import faang.school.projectservice.dto.vacancy.OpenVacancyRequestDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    public Optional<Project> getProjectByIdOrEmpty(long projectId) {
        return projectRepository.findById(projectId);
    }

    public Project validateAndGetProject(OpenVacancyRequestDto requestDto) {
        return getProjectByIdOrEmpty(requestDto.projectId())
                .orElseThrow(() -> new DataValidationException(
                        "Project with id %d is not found".formatted(requestDto.projectId())));
    }
}
