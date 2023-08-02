package faang.school.projectservice.controller;

import faang.school.projectservice.dto.invitation.DtoStageInvitation;
import faang.school.projectservice.service.StageInvitationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project")
@Validated
public class StageInvitationController {

    private final StageInvitationService invitationService;

    @PostMapping("/sendInvitation")
    public String sendAnInvitation(@Valid @RequestBody DtoStageInvitation invitationDto) {
        return invitationService.invitationHasBeenSent(invitationDto);
    }

    @PostMapping("/acceptInvitation/")
    public String acceptDeclineInvitation(@RequestParam @Pattern(regexp = "REJECTED|ACCEPTED"
            , message = "the status can only be REJECTED, ACCEPTED") String status, Long idInvitation) {
        return invitationService.acceptInvitation(status, idInvitation);
    }

}
