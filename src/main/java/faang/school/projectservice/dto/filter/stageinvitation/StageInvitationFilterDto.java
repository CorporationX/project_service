package faang.school.projectservice.dto.filter.stageinvitation;

import faang.school.projectservice.dto.filter.FilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageInvitationFilterDto extends FilterDto {
    private Long id;
    private String stageName;
    private StageInvitationStatus status;
}
