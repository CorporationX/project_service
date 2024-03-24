package faang.school.projectservice.validator.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MomentValidator {

    public void MomentValidatorName(MomentDto momentDto) {
        if (StringUtils.isBlank(momentDto.getName())) {
            throw new EntityNotFoundException("Moment must have a non-blank name");
        }
    }

    public void MomentValidatorProject(MomentDto momentDto) {
        List<Long> projectIds = momentDto.getProjectIds();
        if (projectIds == null || projectIds.isEmpty()) {
            throw new IllegalArgumentException("Moment has not projectIds");
        }
        for (Long projectId : projectIds) {
            if (projectId == null) {
                throw new IllegalArgumentException("id does not null");
            }
        }
    }
}