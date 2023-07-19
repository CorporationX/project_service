package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    @Transactional
    public ProjectDto createProject(ProjectDto projectDto, long userId) {
        if (projectRepository.existsByOwnerUserIdAndName(userId, projectDto.getName())) {
            throw new DataValidationException("The user has already created a project with this name");
        }
        return null;
    }
}
