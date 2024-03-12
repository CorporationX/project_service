package faang.school.projectservice.controller;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.service.StageInvitationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/stageInvitation")
public class StageInvitationController {

    private final StageInvitationService stageInvitationService;

    @PostMapping("/create")
    public StageInvitationDto createInvitation(@RequestBody @NonNull StageInvitationDto stageInvitationDto) {
        return stageInvitationService.create(stageInvitationDto);
    }

    @PostMapping("/accept")
    public StageInvitationDto acceptInvitation(@RequestParam("userId") @NonNull Long userId,
                                               @RequestParam("invitationId") @NonNull Long invitationId) {
        return stageInvitationService.accept(userId, invitationId);
    }

    @DeleteMapping("/reject")
    public StageInvitationDto rejectInvitation(@RequestParam("userId") @NonNull Long userId,
                                               @RequestParam("invitationId") @NonNull Long invitationId,
                                               @RequestParam("description") @NonNull String description) {
        return stageInvitationService.reject(userId, invitationId, description);
    }

    @GetMapping("/{id}")
    public List<StageInvitationDto> getAllInvitation(@NonNull Long id, @RequestBody @NonNull StageInvitationDto filter) {
        return stageInvitationService.getAll(id, filter);
    }

}
