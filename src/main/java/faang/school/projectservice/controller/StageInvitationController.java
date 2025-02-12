package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.dto.stage.StageInvitationFilterDto;
import faang.school.projectservice.service.StageInvitationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/v1/invitations")
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StageInvitationDto createStageInvitation(@Valid @RequestBody StageInvitationDto stageInvitationDto) {
        return stageInvitationService.createStageInvitation(stageInvitationDto);
    }

    @PutMapping("/acceptance/{stageInvitationId}")
    public StageInvitationDto acceptStageInvitation(@PathVariable @Min(1) long stageInvitationId) {
        return stageInvitationService.acceptStageInvitation(stageInvitationId);
    }

    @PutMapping("/rejection/{participantId}")
    public StageInvitationDto rejectStageInvitation(@PathVariable @Min(1) long participantId,
                                                    @NotBlank @RequestBody String rejectionReason) {
        return stageInvitationService.rejectStageInvitation(participantId, rejectionReason);
    }

    @PostMapping("/participant/{participantId}")
    public List<StageInvitationDto> getAllInvitationsForOneParticipant(@PathVariable @Min(1) long participantId,
                                                                       @RequestBody StageInvitationFilterDto filter) {
        return stageInvitationService.getAllInvitationsForOneParticipant(participantId, filter);
    }
}
