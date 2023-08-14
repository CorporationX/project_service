package faang.school.projectservice.controller;

import faang.school.projectservice.dto.invitation.DtoStageInvitation;
import faang.school.projectservice.dto.invitation.DtoStageInvitationFilter;
import faang.school.projectservice.dto.invitation.DtoStatus;
import faang.school.projectservice.service.StageInvitationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project")
@Validated
public class StageInvitationController {

    private final StageInvitationService invitationService;

    @PostMapping("/send-Invitation")
    public DtoStageInvitation sendAnInvitation(@Valid @RequestBody DtoStageInvitation invitationDto) {
        return invitationService.invitationHasBeenSent(invitationDto);
    }

    @PostMapping("/process-invitation/")
    public DtoStatus acceptDeclineInvitation(@RequestParam @Pattern(regexp = "ACCEPTED|REJECTED|PENDING", message =
            "there is no such status, the status can be ACCEPTED REJECTED") String status,
                                             @Positive(message = "user id must be greater than 0")
                                             @Max(value = Long.MAX_VALUE, message = "user id the value cannot be empty or greater than 9223372036854775807") Long idInvitation) {

        return invitationService.acceptDeclineInvitation(status, idInvitation);
    }

    @GetMapping("/stage/{userId}")
    public List<DtoStageInvitation> getStageInvitation(@PathVariable("userId") @Positive(message = "user id must be greater than 0")
                                                       @Max(value = Long.MAX_VALUE, message = "user id the value cannot be empty or greater than 9223372036854775807") Long userId
            , @RequestBody @Valid DtoStageInvitationFilter filter) {

        return invitationService.getAllStageInvitation(userId, filter);
    }
}
