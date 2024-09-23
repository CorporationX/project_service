package faang.school.projectservice.controller.stageInvitation;

import faang.school.projectservice.dto.client.stageInvitation.StageInvitationDto;
import faang.school.projectservice.dto.client.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.service.stageInvitation.StageInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("v1/stageInvitations")
@RequiredArgsConstructor
public class StageInvitationController {

    private final StageInvitationService stageInvitationService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public StageInvitationDto sendInvitation(@Valid @RequestBody StageInvitationDto stageInvitationDto) {
        return stageInvitationService.createInvitation(stageInvitationDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public StageInvitationDto acceptInvitation(@Valid @PathVariable long id) {
        return stageInvitationService.acceptInvitation(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public StageInvitationDto rejectInvitation(@Valid @PathVariable long id,
                                               @Valid @RequestBody String description) {
        return stageInvitationService.rejectInvitation(id, description);
    }

    @GetMapping
    public List<StageInvitationDto> getInvitations(@Valid @RequestParam(required = false)
                                                       StageInvitationFilterDto filter) {
        return stageInvitationService.getInvitations(filter);
    }
}

