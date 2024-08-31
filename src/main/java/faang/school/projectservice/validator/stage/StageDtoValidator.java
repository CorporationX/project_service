package faang.school.projectservice.validator.stage;

import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class StageDtoValidator {
    private final ProjectRepository projectRepository;

    public void validateProjectId(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "There is no project with id:\n" +
                            id + "\n" +
                            "in the database");
        }
    }

    public void validateStageRolesCount(List<StageRoles> stageRolesList) {
        if (stageRolesList.stream().anyMatch(stageRoles -> stageRoles.getCount() == null)) {
            throw new IllegalArgumentException(
                    "You should set the number of all stage roles\n" +
                            stageRolesList);
        }
    }
}
