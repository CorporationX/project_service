package faang.school.projectservice.validator.meet;

import faang.school.projectservice.exception.DeniedInAccessException;
import faang.school.projectservice.model.meet.Meet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MeetValidator {

    public void verifyUserIsCreatorOfMeet(long userId, Meet meet) {
        if (meet.getCreatedBy() != userId) {
            throw new DeniedInAccessException(userId);
        }
    }
}
