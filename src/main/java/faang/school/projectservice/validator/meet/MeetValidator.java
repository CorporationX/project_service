package faang.school.projectservice.validator.meet;

import faang.school.projectservice.exception.DeniedInAccessException;
import faang.school.projectservice.jpa.MeetJpaRepository;
import faang.school.projectservice.model.meet.Meet;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class MeetValidator {

    private final MeetJpaRepository meetJpaRepository;

    public Meet verifyMeetExistence(long meetId) {
        Optional<Meet> meetOptional = meetJpaRepository.findById(meetId);
        if (meetOptional.isEmpty()) {
            String errMessage = "Could not find meet with ID: " + meetId;
            log.error(errMessage);
            throw new EntityNotFoundException(errMessage);
        }
        return meetOptional.get();
    }

    public void verifyUserIsCreatorOfMeet(long userId, Meet meet) {
        if (meet.getCreatedBy() != userId) {
            throw new DeniedInAccessException(userId);
        }
    }
}
