package faang.school.projectservice.controller.meeting;

import faang.school.projectservice.dto.meeting.MeetDto;
import faang.school.projectservice.service.meeting.MeetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/meets")
public class MeetController {
    private final MeetService meetService;

    @PostMapping("/creators/{creatorId}")
    public MeetDto createMeet(@PathVariable long creatorId, @Valid @RequestBody MeetDto meet) {
        log.info("Create meet : {}", meet);
        return meetService.create(creatorId, meet);
    }

    @DeleteMapping("/{meetId}/members/{memberId}")
    public MeetDto cancelMeet(@PathVariable long meetId, @PathVariable long memberId) {
        return meetService.cancel(meetId, memberId);
    }

    @DeleteMapping("/{meetId}/participants/{participantId}/by/{memberId}")
    public MeetDto removeParticipant(@PathVariable long meetId,
                                     @PathVariable long participantId,
                                     @PathVariable long memberId) {
        return meetService.removeParticipant(meetId, memberId, participantId);
    }


    @PutMapping("/{meetId}/members/{memberId}")
    public MeetDto updateMeet(@PathVariable long meetId, @PathVariable long memberId,
                              @Valid @RequestBody MeetDto meet) {
        return meetService.update(meetId, memberId, meet);
    }

    @GetMapping("/{meetId}")
    public MeetDto getMeet(@PathVariable long meetId) {
        return meetService.findById(meetId);
    }

    @GetMapping("/all")
    public List<MeetDto> getMeets() {
        return meetService.findAll();
    }

    @GetMapping("/projects/{projectId}")
    public List<MeetDto> getMeetsByMemberId(@PathVariable long projectId,
                                            @Valid @RequestParam(required = false) String title,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime from,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime to) {
        return meetService.findProjectMeets(
                projectId,
                Optional.ofNullable(title),
                Optional.ofNullable(from),
                Optional.ofNullable(to));
    }
}
