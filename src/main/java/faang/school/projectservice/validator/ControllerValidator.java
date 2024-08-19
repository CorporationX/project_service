package faang.school.projectservice.validator;

import org.springframework.stereotype.Component;

@Component
public class ControllerValidator {
    private static final String MESSAGE_EXCEEDING_MAX_FILE_SIZE = "Exceeding the maximum file size";

    public void validateMaximumSize(Long size, Long maximum) {
        if (size > maximum) {
            throw new RuntimeException(MESSAGE_EXCEEDING_MAX_FILE_SIZE);
        }
    }
}
