package faang.school.projectservice.validator.stagerole.impl;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.jpa.StageRolesRepository;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.validator.stagerole.StageRolesValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StageRolesValidatorImpl implements StageRolesValidator {
    private final StageRolesRepository stageRolesRepository;


    @Override
    public StageRoles validateStageRolesExistence(long id) {
        var optional = stageRolesRepository.findById(id);
        return optional.orElseThrow(() -> {
            var message = String.format("a stage role with %d does not exist", id);

            return new DataValidationException(message);
        });
    }
}
