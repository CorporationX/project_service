package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.repository.StageInvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StageInvitationService {

    private final StageInvitationMapper stageInvitationMapper;
    private final StageInvitationRepository stageInvitationRepository;

    public StageInvitationDto create(StageInvitationDto stageInvitationDto) {
        StageInvitation stageInvitation = stageInvitationRepository.save(stageInvitationMapper.toEntity(stageInvitationDto));
        return stageInvitationMapper.toDto(stageInvitation);
    }

    public void accept() {

    }

    public void reject() {

    }

    public List<StageInvitationDto> getAll(Long id) {
        return stageInvitationMapper.toDto(stageInvitationRepository
                .findAll()
                .stream()
                .filter(stageInvitation -> stageInvitation.getInvited().getUserId().equals(id))
                .toList());
    }

}
