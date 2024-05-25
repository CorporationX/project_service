package faang.school.projectservice.controller.stageInvitation;

import faang.school.projectservice.dto.stageInvitation.AcceptStageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.CreateStageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.RejectStageInvitationDto;
import faang.school.projectservice.filter.invitationFilter.InvitationFilterDto;
import faang.school.projectservice.service.StageInvitationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/invitations")
public class StageInvitationController {
    private final StageInvitationService stageInvitationService;

    @PostMapping("/add")
    public CreateStageInvitationDto createInvitation(@RequestBody @Valid CreateStageInvitationDto createStageInvitationDto) {
        log.info("Received create invitation request {}", createStageInvitationDto);
        return stageInvitationService.createInvitation(createStageInvitationDto);
    }

    @PutMapping("/accept")
    public AcceptStageInvitationDto acceptInvitation(@RequestBody @Valid AcceptStageInvitationDto acceptStageInvitationDto) {
        log.info("Received accept invitation request {}", acceptStageInvitationDto);
        return stageInvitationService.acceptInvitation(acceptStageInvitationDto);
    }

    @PutMapping("/reject")
    public RejectStageInvitationDto rejectInvitation(@RequestBody @Valid RejectStageInvitationDto rejectStageInvitationDto){
        log.info("Received reject invitation request {}", rejectStageInvitationDto);
        return stageInvitationService.rejectInvitation(rejectStageInvitationDto);
    }

    @GetMapping("/filter")
    public List<CreateStageInvitationDto> showAllInvitationForMember(@RequestParam("userId") Long userId, @RequestBody InvitationFilterDto invitationFilterDto) {
        log.info("Received show all invitations request for userId: {}, with filter: {}", userId, invitationFilterDto);
        return stageInvitationService.showAllInvitationForMember(userId, invitationFilterDto);
    }
}
