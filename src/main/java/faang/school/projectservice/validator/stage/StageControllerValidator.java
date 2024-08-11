package faang.school.projectservice.validator.stage;

import faang.school.projectservice.dto.stage.StageDto;
import org.springframework.stereotype.Component;

@Component
public class StageControllerValidator {

    public void validate(StageDto stage) {
        if (stage.getStageName().isEmpty()) {
            throw new RuntimeException("Нельзя передавать пустое названия этапа");
        }
        if (stage.getStageRoles().stream().allMatch(stageRoleDto -> stageRoleDto.getCount() != null)) {
            throw new RuntimeException("Нельзя передавать пустое количество какой-либо роли этапа");
        }
    }
}
