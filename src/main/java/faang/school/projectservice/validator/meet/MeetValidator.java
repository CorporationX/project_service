package faang.school.projectservice.validator.meet;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.meet.Meet;
import org.springframework.stereotype.Component;

@Component
public class MeetValidator {

    public void validateMeetToUpdate(Meet meet, Long creatorId) {
        if (meet.getCreatorId() != creatorId) {
            throw new DataValidationException("Only creator can update meet");
        }
    }

    public void validateMeetToDelete(Meet meet, Long creatorId) {
        if (meet.getCreatorId() != creatorId) {
            throw new DataValidationException("Only creator can delete meet and participants");
        }
    }
}