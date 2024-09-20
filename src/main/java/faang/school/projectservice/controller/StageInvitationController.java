package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.stage.StageInvitationDto;
import faang.school.projectservice.dto.client.stage.StageInvitationFilterDto;
import faang.school.projectservice.service.StageInvitationService;
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
    public void sendInvitation(@Valid @RequestBody StageInvitationDto stageInvitationDto) {
        stageInvitationService.createInvitation(stageInvitationDto);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void acceptInvitation(@Valid @PathVariable long id) {
        stageInvitationService.acceptInvitation(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void rejectInvitation(@Valid @PathVariable long id) {
        stageInvitationService.rejectInvitation(id);
    }

    @GetMapping
    public List<StageInvitationDto> getInvitations(@Valid @ModelAttribute StageInvitationFilterDto filter) {
        return stageInvitationService.getInvitations(filter);
    }
}
