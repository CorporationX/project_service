package faang.school.projectservice.controller.stageInvitation;

import faang.school.projectservice.dto.filter.stageinvitation.StageInvitationFilterDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationDtoRequest;
import faang.school.projectservice.dto.stageInvitation.StageInvitationDtoResponse;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.service.stageInvitation.StageInvitationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/api/v1/stageInvitations")
@RequiredArgsConstructor
public class StageInvitationController {

    private final StageInvitationService stageInvitationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StageInvitationDtoResponse sendInvitation(@RequestBody StageInvitationDtoRequest stageInvDtoRequest) {
        return stageInvitationService.createInvitation(stageInvDtoRequest);
    }

    @PatchMapping("/{id}/accept")
    public StageInvitationDtoResponse acceptInvitation(@PathVariable("id") @Positive long id) {
        return stageInvitationService.acceptInvitation(id);
    }

    @PatchMapping("/{id}/reject")
    public StageInvitationDtoResponse rejectInvitation(@PathVariable("id") @Positive long id,
                                                       @Valid @RequestBody StageInvitationDtoRequest stageInvDtoRequest) {
        return stageInvitationService.rejectInvitation(id, stageInvDtoRequest);
    }

    @GetMapping("/{id}/filter")
    public List<StageInvitationDtoResponse> getInvitations(@PathVariable long id,
                                                           @RequestParam(value = "stageName", required = false)
                                                           String stageName,
                                                           @RequestParam(value = "status", required = false)
                                                           StageInvitationStatus status) {
        var filter = new StageInvitationFilterDto(id, stageName, status);
        return stageInvitationService.getInvitations(filter);
    }
}

