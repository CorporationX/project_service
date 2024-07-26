package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import org.springframework.stereotype.Component;

@Component
public class MomentDtoValidator {
    public void validateMomentDo(MomentDto momentDto) {
        if (momentDto.getId() == null || momentDto.getId() < 0) {
            throw new DataValidationException("id is null or less than 0");
        }
        if (momentDto.getName().isBlank()) {
            throw new DataValidationException("name is empty");
        }
        if (momentDto.getProjectIds().isEmpty()) {
            throw new DataValidationException("dto has no projects");
        }
    }
}
