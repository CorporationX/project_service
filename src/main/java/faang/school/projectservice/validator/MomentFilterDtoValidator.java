package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.MomentFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import org.springframework.stereotype.Component;

@Component
public class MomentFilterDtoValidator {
    public void validateMomentFilterDto(MomentFilterDto dto) {
        if (dto.getId() == null || dto.getId() < 0) {
            throw new DataValidationException("id is null or less than 0");
        }
        if (dto.getName().isBlank()) {
            throw new DataValidationException("name is empty");
        }
    }
}
