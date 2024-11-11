package faang.school.projectservice.validator;

import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class StageInvitationValidator {

    public void validateInvitation(StageInvitationDto stageInvitationDto) {
        if (stageInvitationDto.getStage() == null) {
            throw new DataValidationException("Stage not specified");
        } else if(stageInvitationDto.getAuthor() == null) {
            throw new DataValidationException("Author not specified");
        } else if (stageInvitationDto.getInvited() == null) {
            throw new DataValidationException("Invited not specified");
        }
    }

    public void validateDescription(StageInvitationDto stageInvitationDto) {
        if (stageInvitationDto.getDescription().isBlank()) {
            throw new DataValidationException("The reason should not be empty. Fill in the reason");
        }
    }
}
