package faang.school.projectservice.validator.meet;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MeetValidator {

    public void userIsMeetCreator(Long userId, Long createdBy) {
        if (!userId.equals(createdBy)) {
            throw new DataValidationException(String.format("User %s is not the creator of the meeting %s", userId, createdBy));
        }
    }
}
