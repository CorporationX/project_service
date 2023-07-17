package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.repository.StageInvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StageInvitationService {
  private StageInvitationRepository stageInvitationRepository;
  private StageInvitationMapper stageInvitationMapper;

  public StageInvitationDto create(StageInvitationDto invitationDto) {
    StageInvitation stageInvitation = stageInvitationRepository.save(stageInvitationMapper.toEntity(invitationDto));
    return stageInvitationMapper.toDto(stageInvitation);
  }
}
