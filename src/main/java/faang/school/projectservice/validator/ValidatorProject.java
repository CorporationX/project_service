package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.ProjectDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class ValidatorProject {
    public void validateProject(ProjectDto projectDto) {
        if (projectDto.description() == null || projectDto.description().isBlank()) {
            throw new NoSuchElementException("Need project description");
        }

        if (projectDto.name() == null || projectDto.name().isBlank()) {
            throw new NoSuchElementException("Need project name");
        }
    }
}
