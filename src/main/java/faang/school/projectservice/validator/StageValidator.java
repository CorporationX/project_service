package faang.school.projectservice.validator;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.repository.StageRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StageValidator {
    private final StageRepository stageRepository;

    public void checkIfStageExists(long stageId) {
        stageRepository.getById(stageId);
    }

    public void validate(StageInvitationDto stageInvitationDto) {
        checkIfStageExists(stageInvitationDto.getStageId());
    }
}
