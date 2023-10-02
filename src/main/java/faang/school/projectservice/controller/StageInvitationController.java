package faang.school.projectservice.controller;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("/stage/invitations")
@Slf4j
public class StageInvitationController {
    private final StageInvitationService service;

    @PostMapping
    public StageInvitationDto create(@Valid @RequestBody StageInvitationDto invitationDto) {
        log.info("POST endpoint 'create()' was called successfully");
        validate(invitationDto);
        return service.create(invitationDto);
    }

    @PutMapping("/accept/{invitationId}")
    public StageInvitationDto accept(@PathVariable long invitationId) {
        log.info("PUT endpoint 'accept()' was called successfully with invitationId {}", invitationId);
        return service.accept(invitationId);
    }

    @PutMapping("/reject/{invitationId}")
    public StageInvitationDto reject(@PathVariable long invitationId, @RequestParam("message") String message) {
        log.info("PUT endpoint 'reject()' was called successfully with invitationId {}", invitationId);
        if (message == null || message.isBlank()) {
            throw new DataValidationException("Rejection must contains message");
        }
        return service.reject(invitationId, message);
    }

    @GetMapping("/filter")
    public List<StageInvitationDto> getStageInvitationsWithFilters(@RequestBody StageInvitationFilterDto filterDto) {
        log.info("GET endpoint 'getStageInvitationsWithFilters()' was called successfully");
        return service.getStageInvitationsWithFilters(filterDto);
    }

    @GetMapping("/invited/{invitedId}/filter")
    public List<StageInvitationDto> getStageInvitationsForTeamMemberWithFilters(@PathVariable long invitedId, @RequestBody StageInvitationFilterDto filterDto) {
        log.info("GET endpoint 'getStageInvitationsForTeamMemberWithFilters()' was called successfully with invitedId {}", invitedId);
        if (filterDto.getInvitedId() == null || filterDto.getInvitedId() != invitedId) {
            filterDto = StageInvitationFilterDto.builder()
                    .stageId(filterDto.getStageId())
                    .authorId(filterDto.getAuthorId())
                    .invitedId(invitedId)
                    .status(filterDto.getStatus())
                    .build();
        }
        return service.getStageInvitationsWithFilters(filterDto);
    }


    private void validate(StageInvitationDto invitationDto) {
        if (invitationDto == null) {
            throw new DataValidationException("StageInvitation can't be null");
        } else if (Objects.equals(invitationDto.getAuthorId(), invitationDto.getInvitedId())) {
            throw new DataValidationException("Author can't be invited");
        }
    }
}
