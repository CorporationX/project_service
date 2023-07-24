package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.repository.StageInvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StageInvitationService {
  private final StageInvitationRepository stageInvitationRepository;
  private final StageInvitationMapper stageInvitationMapper;

  public StageInvitationDto create(StageInvitationDto invitationDto) {
    StageInvitation stageInvitation = stageInvitationRepository.save(stageInvitationMapper.toEntity(invitationDto));
    return stageInvitationMapper.toDto(stageInvitation);
  }

  public List<StageInvitationDto> getAllByInvitedUserId(Long userId) {
    return stageInvitationRepository.findAll().stream()
      .filter((stageInvitation -> Objects.equals(stageInvitation.getInvited().getUserId(), userId)))
      .map(stageInvitationMapper::toDto)
      .toList();
  }
}
