package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class Validator {
    public boolean validateInputStageData(StageDto stageDto) {
        if (StringUtils.isBlank(stageDto.getStageName())
                || stageDto.getProject() == null
                || stageDto.getStageRoles().isEmpty()
                || !stageDto.getStageRoles().stream()
                .filter(stageRoles -> stageRoles.getCount() == 0)
                .toList()
                .isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
}
