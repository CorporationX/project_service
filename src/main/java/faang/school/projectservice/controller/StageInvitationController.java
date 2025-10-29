package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stageInvitation.StageInvitationAcceptDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationCreateDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationDeclineDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.service.StageInvitationServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping(value = "api/v1/stage-invitations", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Validated
public class StageInvitationController {
    private final StageInvitationServiceImpl stageInvitationServiceImpl;

    @PostMapping
    public StageInvitationDto sendInvitation(@Valid @RequestBody StageInvitationCreateDto stageInvitationCreateDto) {
        return stageInvitationServiceImpl.sendInvitation(stageInvitationCreateDto);
    }

    @PutMapping("/accept")
    public void acceptInvitation(@Valid @RequestBody StageInvitationAcceptDto stageInvitationAcceptDto) {
        stageInvitationServiceImpl.acceptInvitation(stageInvitationAcceptDto);
    }

    @PutMapping("/decline")
    public void declineInvitation(@Valid @RequestBody StageInvitationDeclineDto stageInvitationDeclineDto) {
        stageInvitationServiceImpl.declineInvitation(stageInvitationDeclineDto);
    }

    @GetMapping("/all-invitation")
    public List<StageInvitationDto> viewAllInvitationsByFilter(
            @Valid @ModelAttribute StageInvitationFilterDto stageInvitationFilterDto) {
        return stageInvitationServiceImpl.viewAllInvitationsByFilter(stageInvitationFilterDto);
    }
}