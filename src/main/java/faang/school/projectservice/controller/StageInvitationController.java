package faang.school.projectservice.controller;

import faang.school.projectservice.dto.FilterInvitationDto;
import faang.school.projectservice.dto.RejectionDto;
import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.service.StageInvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stage/invitations")
@RequiredArgsConstructor
@Tag(name = "Управление приглашениями")
public class StageInvitationController {

    private final StageInvitationService stageInvitationService;

    @PostMapping("/{id}/sending")
    @Operation(summary = "Отправка приглашений")
    public StageInvitationDto sendInvitation(@RequestBody StageInvitationDto stageInvitationDto) {
        return stageInvitationService.sendInvitation(stageInvitationDto);
    }

    @PostMapping("/{id}/accept")
    @Operation(summary = "Принятие приглашений")
    public StageInvitationDto acceptInvitation(@PathVariable Long id) {
        return stageInvitationService.acceptInvitation(id);
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Отклонение приглашений")
    public StageInvitationDto rejectInvitation(@PathVariable Long id, @RequestBody RejectionDto rejectionReason) {
        return stageInvitationService.rejectInvitation(id, rejectionReason);
    }

    @PostMapping("/user/{userId}")
    @Operation(summary = "Просмотр приглашений для одного участника с фильтрами")
    public List<StageInvitationDto> getUserInvitations(@PathVariable Long userId, FilterInvitationDto filters) {
        return stageInvitationService.getUserInvitations(userId, filters);
    }
}
