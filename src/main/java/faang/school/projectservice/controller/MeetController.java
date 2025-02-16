package faang.school.projectservice.controller;


import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.service.MeetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
public class MeetController {
    private final MeetService meetService;

    @PostMapping("/meets")
    public MeetDto createMeet(@RequestBody @NotNull MeetDto meet) {
        return meetService.createMeet(meet);
    }

    @PutMapping("/meets")
    public MeetDto updateMeet(@RequestBody @NotNull MeetDto meet) {
        return meetService.updateMeet(meet);
    }

    @PutMapping("/meets/{meetId}")
    public MeetDto cancelMeetById(@PathVariable @NotNull Long meetId) {
        return meetService.cancelMeetById(meetId);
    }

    @DeleteMapping("/meets/{meetId}")
    public MeetDto deleteMeetById(@PathVariable @Valid Long meetId) {
        return meetService.deleteMeetById(meetId);
    }

    @PostMapping("/meets/{projectId}")
    public List<MeetDto> findMeetsByProject(@PathVariable @Valid Long projectId,
                                            @RequestBody(required = false) MeetFilterDto filter) {
        return meetService.findMeetsByProject(projectId, filter);
    }

    @GetMapping("/meets")
    public List<MeetDto> getAllMeets() {
        return meetService.getAllMeets();
    }

    @GetMapping("/meets/{meetId}")
    public MeetDto getMeetById(@NotNull @Positive @PathVariable Long meetId) {
        return meetService.getMeetById(meetId);
    }

}
