package faang.school.projectservice.dto;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.NonNull;

@Builder
@Data
public class StageInvitationDto {
    @Min(0)
    private Long id;
    @NonNull
    private String description;
    @NonNull
    private Stage stage;
    @NonNull
    private TeamMember invited;
}
