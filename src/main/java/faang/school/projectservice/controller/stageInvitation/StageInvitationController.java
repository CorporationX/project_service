package faang.school.projectservice.controller.stageInvitation;

import faang.school.projectservice.dto.stageInvitation.AcceptStageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.CreateStageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.RejectStageInvitationDto;
import faang.school.projectservice.filter.InvitationFilterDto;
import faang.school.projectservice.service.StageInvitationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/invitations")
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    @PostMapping("/add")
    public CreateStageInvitationDto createInvitation(@RequestBody @Valid CreateStageInvitationDto createStageInvitationDto) {
        return stageInvitationService.createInvitation(createStageInvitationDto);
    }

    @PutMapping("/accept")
    public AcceptStageInvitationDto acceptInvitation(@RequestBody @Valid AcceptStageInvitationDto acceptStageInvitationDto) {
        return stageInvitationService.acceptInvitation(acceptStageInvitationDto);
    }

    @PutMapping("/reject")
    public RejectStageInvitationDto rejectInvitation(@RequestBody @Valid RejectStageInvitationDto rejectStageInvitationDto){
        return stageInvitationService.rejectInvitation(rejectStageInvitationDto);
    }

    @GetMapping("/filter/{userId}")
    public List<CreateStageInvitationDto> showAllInvitationForMember(@PathVariable @NotNull Long userId, @RequestBody InvitationFilterDto invitationFilterDto) {
        return stageInvitationService.showAllInvitationForMember(userId, invitationFilterDto);
    }
}
