package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.dto.StageInvitationFilterDto;

import java.util.List;

public interface StageInvitationService {
    public StageInvitationDto create(StageInvitationDto stageInvitationDto);
    public List<StageInvitationDto> getStageInvitations(long id, StageInvitationFilterDto filter);

    public StageInvitationDto accept(StageInvitationDto stageInvitationDto);
    public StageInvitationDto reject(StageInvitationDto stageInvitationDto);
}
