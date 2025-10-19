package faang.school.projectservice.service;


import faang.school.projectservice.dto.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationCreateDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationAcceptDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationDeclineDto;

import java.util.List;

public interface StageInvitationService {

    StageInvitationDto sendInvitation(StageInvitationCreateDto stageInvitationCreateDto);

    void acceptInvitation(StageInvitationAcceptDto stageInvitationAcceptDto);

    void declineInvitation(StageInvitationDeclineDto stageInvitationDeclineDto);

    List<StageInvitationDto> viewAllInvitationsByFilter(StageInvitationFilterDto stageInvitationFilterDto);
}