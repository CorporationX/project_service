package faang.school.projectservice.controller;

import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.dto.invitation.DtoStageInvitationFilter;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.service.StageInvitationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project")
public class StageInvitationController {

    private final StageInvitationService invitationService;

    @PostMapping("/send-invitation")
    public StageInvitationDto sendAnInvitation(@Valid @RequestBody StageInvitationDto invitationDto) {
        return invitationService.invitationHasBeenSent(invitationDto);
    }

    @PostMapping("/process-invitation/")
    public StageInvitationStatus acceptDeclineInvitation(@RequestParam @Pattern(regexp = "ACCEPTED|REJECTED|PENDING", message =
            "there is no such status, the status can be ACCEPTED REJECTED") String status,
                                                         @Positive(message = "user id must be greater than 0")
                                                         @Max(value = Long.MAX_VALUE, message = "user id the value cannot be empty or greater than 9223372036854775807") Long idInvitation) {

        return invitationService.acceptDeclineInvitation(status, idInvitation);
    }

    @GetMapping("/stage/{userId}")
    public List<StageInvitationDto> getStageInvitation(@PathVariable("userId") @Positive(message = "user id must be greater than 0")
                                                       @Max(value = Long.MAX_VALUE, message = "user id the value cannot be empty or greater than 9223372036854775807") Long userId
            , @RequestBody @Valid DtoStageInvitationFilter filter) {

        return invitationService.getAllStageInvitation(userId, filter);
    }
}
