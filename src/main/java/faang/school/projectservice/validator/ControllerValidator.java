package faang.school.projectservice.validator;

import org.springframework.stereotype.Component;

@Component
public class ControllerValidator {
    private static final String MESSAGE_DTO_IS_NULL = "Dto is null";
    private static final String MESSAGE_EXCEEDING_MAX_FILE_SIZE = "Exceeding the maximum file size";

    public void validateId(Long id, String message) {
        if (id < 0) {
            throw new RuntimeException(message);
        }
    }

    public void validateDto(Object dto) {
        if (dto == null) {
            throw new RuntimeException(MESSAGE_DTO_IS_NULL);
        }
    }

    public void validateMaximumSize(Long size, Long maximum) {
        if (size > maximum) {
            throw new RuntimeException(MESSAGE_EXCEEDING_MAX_FILE_SIZE);
        }
    }
}
