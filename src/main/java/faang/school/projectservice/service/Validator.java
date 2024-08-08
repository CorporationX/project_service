package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class Validator {
    @Value("${maxfilesize}")
    private long maxFileSize;
    @Value("${maxwidth}")
    private int maxWidth;
    @Value("${maxheight}")
    private int maxHeight;
    public boolean validateInputStageData(StageDto stageDto) {
        return !StringUtils.isBlank(stageDto.getStageName())
                && stageDto.getProject() != null
                && !stageDto.getStageRoles().isEmpty()
                && stageDto.getStageRoles().stream()
                .filter(stageRoles -> stageRoles.getCount() == 0)
                .toList()
                .isEmpty();
    }

    public boolean validateFileSize(long size) {
        return size <= maxFileSize;
    }

    public boolean validatesImageSize(int width, int height) {
        if (width > maxWidth) {
            return false;
        } else return height <= maxHeight;
    }
}
