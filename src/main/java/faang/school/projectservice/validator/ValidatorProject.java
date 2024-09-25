package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.ProjectDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class ValidatorProject {
    public void validateProject(ProjectDto projectDto) {
        if (projectDto.getDescription() == null || projectDto.getDescription().isBlank()) {
            throw new NoSuchElementException("Need project description");
        }

        if (projectDto.getName() == null || projectDto.getName().isBlank()) {
            throw new NoSuchElementException("Need project name");
        }
    }
}
