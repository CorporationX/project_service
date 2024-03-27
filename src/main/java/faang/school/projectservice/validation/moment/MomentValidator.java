package faang.school.projectservice.validation.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MomentValidator {

    public void validatorMomentName(MomentDto momentDto) {
        if ((momentDto.getName().isBlank())) {
            throw new DataValidationException("Moment must have a non-blank name");
        }
    }

    public void validatorProjectOfMoment(MomentDto momentDto) {
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