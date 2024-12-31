package faang.school.projectservice.controller.meet;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.MeetTitleDateFilter;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.service.meet.MeetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meets")
public class MeetController {
    private final MeetService meetService;

    @PostMapping("/projects/{projectId}")
    @ResponseStatus(HttpStatus.CREATED)
    public MeetDto createMeet(@PathVariable long projectId, @RequestBody @Valid MeetDto meetDto) {
        return meetService.createMeet(projectId, meetDto);
    }

    @PatchMapping("/{meetId}")
    @ResponseStatus(HttpStatus.OK)
    public MeetDto updateMeet(@PathVariable long meetId, @RequestBody @Valid UpdateMeetDto updateMeetDto) {
        return meetService.updateMeet(meetId, updateMeetDto);
    }

    @PatchMapping("/status/{meetId}")
    @ResponseStatus(HttpStatus.OK)
    public MeetDto changeMeetStatus(@PathVariable long meetId, @NotNull @PathParam("status") MeetStatus status) {
        return meetService.changeMeetStatus(meetId, status);
    }

    @PostMapping("/{meetId}/participants/{participantId}")
    public void addParticipant(@PathVariable long meetId, @PathVariable long participantId) {
        meetService.addParticipant(meetId, participantId);
    }

    @DeleteMapping("/{meetId}/participants/{participantId}")
    public void deleteParticipant(@PathVariable long meetId, @PathVariable long participantId) {
        meetService.deleteParticipant(meetId, participantId);
    }

    @PostMapping("/filtered/projects/{projectId}")
    public List<MeetDto> getForProjectFilteredByTitleAndDateRange(@PathVariable long projectId,
                                                                  @RequestBody MeetTitleDateFilter filter) {
        return meetService.getForProjectFilteredByTitleAndDateRange(projectId, filter);
    }

    @GetMapping("/projects/{projectId}")
    public List<MeetDto> getAllProjectMeets(@PathVariable long projectId) {
        return meetService.getAllProjectMeets(projectId);
    }

    @GetMapping("/{meetId}")
    public MeetDto getById(@PathVariable long meetId) {
        return meetService.getById(meetId);
    }
}
