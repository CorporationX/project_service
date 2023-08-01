package faang.school.projectservice.validator.subproject;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.subproject.SubProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SubProjectValidator {
    private final SubProjectService subProjectService;
    private final UserServiceClient userServiceClient;

    public void validateCreateProjectDto(CreateProjectDto createProjectDto) {
        validateId(createProjectDto.getId());
        validateDescription(createProjectDto.getDescription());

        String name = userServiceClient.getUser(createProjectDto.getOwnerId()).getUsername();
        subProjectService.existsByOwnerUserIdAndName(createProjectDto.getOwnerId(), name);

        validateTime(createProjectDto.getCreatedAt());
    }

    public void validateId(Long id) {
        if (id < 1 && id != null) {
            throw new DataValidationException("It's wrong id");
        }
    }

    private void validateDescription(String description) {
        if (description == null) {
            throw new DataValidationException("Description can't be null");
        }

        int discLen = description.length();
        if (discLen == 0 || discLen >= 4096) {
            throw new DataValidationException("You wrought wrong disruption, pls revise it");
        }
    }

    private void validateTime(LocalDateTime time) {
        LocalDateTime now = LocalDateTime.now();
        if (time.isBefore(now)) {
            throw new DataValidationException("Time can't be more that now");
        }
    }
}
