package faang.school.projectservice.controller.meet;

import faang.school.projectservice.dto.meet.MeetCreateDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.meet.MeetResponseDto;
import faang.school.projectservice.dto.meet.MeetUpdateDto;
import faang.school.projectservice.exception.BadRequestArgumentException;
import faang.school.projectservice.exception.EntityIdMismatchException;
import faang.school.projectservice.service.meet.interfaces.MeetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Validated
public class MeetController {

    private final MeetService meetService;

    @PostMapping("/projects/{projectId}/meets")
    public ResponseEntity<MeetResponseDto> createMeet(@PathVariable("projectId") long projectId,
                                                      @RequestBody @Valid MeetCreateDto meetCreateDto,
                                                      @RequestHeader("x-user-id") long userId) {
        checkProjectIdAndUserId(projectId, userId);
        checkEntityIdWithDto(projectId, meetCreateDto.getProjectId(), "Project");

        MeetResponseDto meetResponseDto = meetService.createMeet(meetCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(meetResponseDto);
    }

    @PutMapping("/projects/{projectId}/meets/{meetId}")
    public ResponseEntity<MeetResponseDto> updateMeet(@PathVariable("projectId") long projectId,
                                                      @PathVariable("meetId") long meetId,
                                                      @RequestBody MeetUpdateDto meetUpdateDto,
                                                      @RequestHeader("x-user-id") long userId) {
        checkProjectIdAndUserIdAndMeetId(projectId, meetId, userId);
        checkEntityIdWithDto(projectId, meetUpdateDto.getProjectId(), "Project");
        checkEntityIdWithDto(meetId, meetUpdateDto.getId(), "Meet");

        MeetResponseDto meetResponseDto = meetService.updateMeet(meetUpdateDto);
        return ResponseEntity.ok(meetResponseDto);
    }

    @DeleteMapping("/projects/{projectId}/meets/{meetId}")
    public ResponseEntity<Void> deleteMeet(@PathVariable("projectId") long projectId,
                                           @PathVariable("meetId") long meetId,
                                           @RequestHeader("x-user-id") long userId) {
        checkProjectIdAndUserIdAndMeetId(projectId, meetId, userId);

        meetService.deleteMeet(meetId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/projects/{projectId}/meets/{meetId}")
    public ResponseEntity<MeetResponseDto> getMeet(@PathVariable("projectId") long projectId,
                                                   @PathVariable("meetId") long meetId,
                                                   @RequestHeader("x-user-id") long userId) {
        checkProjectIdAndUserIdAndMeetId(projectId, meetId, userId);

        return ResponseEntity.status(HttpStatus.OK).body(meetService.getMeet(meetId));
    }

    @GetMapping("/projects/{projectId}/meets")
    public ResponseEntity<List<MeetResponseDto>> getMeetsByProjectId(@PathVariable("projectId") long projectId,
                                                                     @ModelAttribute MeetFilterDto meetFilterDto,
                                                                     @RequestHeader("x-user-id") long userId) {
        checkProjectIdAndUserId(projectId, userId);

        return ResponseEntity.ok(meetService.getMeetsByProjectId(projectId, meetFilterDto));
    }

    private static void checkProjectIdAndUserId(long projectId, long userId) {
        checkEntityId(projectId, "Project");
        checkEntityId(userId, "User");
    }

    private static void checkProjectIdAndUserIdAndMeetId(long projectId, long meetId, long userId) {
        checkProjectIdAndUserId(projectId, userId);
        checkEntityId(meetId, "Meet");
    }

    private static void checkEntityId(long entityId, String entityName) {
        if (entityId <= 0) {
            throw new BadRequestArgumentException(String.format("%s id must be greater than 0", entityName));
        }
    }

    private static void checkEntityIdWithDto(long entityId, long dtoEntityId, String entityName) {
        if (dtoEntityId != entityId) {
            throw new EntityIdMismatchException(String.format("%s id in path and body do not match", entityName));
        }
    }
}
