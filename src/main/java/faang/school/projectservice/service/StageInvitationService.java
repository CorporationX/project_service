package faang.school.projectservice.service;

import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validate.StageInvitationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StageInvitationService {
    private final StageInvitationRepository stageInvitationRepository;
    private final StageRepository stageRepository;
    private final StageInvitationMapper stageInvitationMapper;
    private final StageInvitationValidator stageInvitationValidator;

    public StageInvitationDto create(StageInvitationDto stageInvitationDto) {
        stageInvitationValidator.validateExecutors(stageInvitationDto);
        return stageInvitationMapper.toDto(stageInvitationRepository
                .save(stageInvitationMapper.toModel(stageInvitationDto)));
    }
}
