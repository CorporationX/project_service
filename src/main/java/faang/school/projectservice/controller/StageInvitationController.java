package faang.school.projectservice.controller;

import faang.school.projectservice.dto.FilterDto;
import faang.school.projectservice.dto.RejectionDto;
import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.service.StageInvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stage-invitations")
@RequiredArgsConstructor
@Tag(name = "Управление приглашениями")
public class StageInvitationController {

    private final StageInvitationService stageInvitationService;

    @PostMapping
    @Operation(summary = "Отправка приглашений")
    public StageInvitationDto sendInvitation(@RequestBody StageInvitationDto stageInvitationDto) {
        return stageInvitationService.sendInvitation(stageInvitationDto);
    }

    @PutMapping("/{id}/accept")
    @Operation(summary = "Принятие приглашений")
    public StageInvitationDto acceptInvitation(@PathVariable Long id) {
        return stageInvitationService.acceptInvitation(id);
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Отклонение приглашений")
    public StageInvitationDto rejectInvitation(@PathVariable Long id, @RequestBody RejectionDto rejectionReason) {
        return stageInvitationService.rejectInvitation(id, rejectionReason);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Просмотр приглашений для одного участника с фильтрами")
    public List<StageInvitationDto> getUserInvitations(@PathVariable Long userId, FilterDto filters) {
        return stageInvitationService.getUserInvitations(userId, filters);
    }
}
