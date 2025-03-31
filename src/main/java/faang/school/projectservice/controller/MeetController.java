package faang.school.projectservice.controller;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.service.MeetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/v1/meets")
@RestController
@RequiredArgsConstructor
public class MeetController {
    private final MeetService meetService;

    @PostMapping
    public MeetDto createMeet(@RequestBody @NotNull MeetDto meet) {
        return meetService.createMeet(meet);
    }

    @PutMapping
    public MeetDto updateMeet(@RequestBody @NotNull MeetDto meet) {
        return meetService.updateMeet(meet);
    }

    @PutMapping("/{id}")
    public MeetDto cancelMeetById(@PathVariable @NotNull Long id) {
        return meetService.cancelMeetById(id);
    }

    @DeleteMapping("/{id}")
    public Long deleteMeetById(@PathVariable @Valid Long id) {
        return meetService.deleteMeetById(id);
    }

    @PostMapping("/project/{projectId}")
    public List<MeetDto> findMeetsByProject(@PathVariable @Valid Long projectId,
                                            @RequestBody(required = false) MeetFilterDto filter) {
        return meetService.findMeetsByProject(projectId, filter);
    }

    @GetMapping
    public List<MeetDto> getAllMeets() {
        return meetService.getAllMeets();
    }

    @GetMapping("/{id}")
    public MeetDto getMeetById(@NotNull @Positive @PathVariable Long id) {
        return meetService.getMeetById(id);
    }
}
