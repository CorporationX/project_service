package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exceptions.DataProjectValidation;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidator {

    private final ProjectRepository projectRepository;

    public void checkIsNull(ProjectDto projectDto) {
        if (projectDto == null) {
            throw new DataProjectValidation("Аргумент projectDto не может быть null");
        }
    }

    public void checkIsNull(ProjectFilterDto projectFilterDto) {
        if (projectFilterDto == null) {
            throw new DataProjectValidation("Аргумент projectFilterDto не может быть null");
        }
    }

    public void checkIsNull(Long projectId) {
        if (projectId == null) {
            throw new DataProjectValidation("Аргумент projectId не может быть null");
        }
    }

    public void checkExistProject(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataProjectValidation("Проект с таким названием уже существует");
        }
    }
}