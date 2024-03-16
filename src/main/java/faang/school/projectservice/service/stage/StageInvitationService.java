package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.mapper.stage.StageInvitationMapper;
import faang.school.projectservice.repository.StageInvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StageInvitationService {
    private final StageInvitationRepository stageInvitationRepository;
    private final StageInvitationMapper stageInvitationMapper;
    public StageInvitationDto create(StageInvitationDto stageInvitationDto) {
        var stageInvitation = stageInvitationMapper.toEntity(stageInvitationDto);
        var stageInvitationSaved = stageInvitationRepository.save(stageInvitation);

        return stageInvitationMapper.toDto(stageInvitationSaved);
    }
}
