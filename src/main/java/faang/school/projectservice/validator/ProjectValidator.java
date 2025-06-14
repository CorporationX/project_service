package faang.school.projectservice.validator;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.ProjectRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectValidator {

    private final ProjectRepository projectRepository;
    private final UserServiceClient userServiceClient;

    public void checkIfUserProjectName(String projectName, long creatorId) {
        if (projectRepository.existsByOwnerIdAndName(creatorId, projectName)) {
            throw new DataValidationException("Вы уже создали проект с таким названием");
        }
    }

    public void validateUserExists(long userId) {
        try {
            userServiceClient.checkUserExists(userId);
        } catch (FeignException.NotFound e) {
            throw new DataValidationException("Пользователя с данным id не существует");
        }
    }
}