package faang.school.projectservice.validator;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import org.springframework.stereotype.Component;

@Component
public class MomentFilterValidator {
    public void validateFilter(MomentFilterDto momentFilterDto) {
        if (momentFilterDto.getStartDate() != null && momentFilterDto.getEndDate() != null) {
            if (momentFilterDto.getStartDate().isAfter(momentFilterDto.getEndDate())) {
                throw new DataValidationException("Start date can't be after end date");
            }
        }
    }
}
