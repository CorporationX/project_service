package faang.school.projectservice.controller;

import faang.school.projectservice.dto.meet.MeetCreateRequest;
import faang.school.projectservice.dto.meet.MeetFilterRequest;
import faang.school.projectservice.dto.meet.MeetResponse;
import faang.school.projectservice.dto.meet.MeetUpdateRequest;
import faang.school.projectservice.service.MeetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meets")
public class MeetController {

    private final MeetService meetService;

    @PostMapping
    public MeetResponse createMeet(@Valid @RequestBody MeetCreateRequest meetCreateRequest) {
        return meetService.createMeet(meetCreateRequest);
    }

    @PatchMapping
    public MeetResponse updateMeet(@Valid @RequestBody MeetUpdateRequest meetCreateRequest) {
        return meetService.updateMeet(meetCreateRequest);
    }

    @DeleteMapping()
    public void deleteMeet(@Valid @NotNull @RequestParam Long meetId,
                           @Valid @NotNull @RequestParam Long userId) {
        meetService.deleteMeet(meetId, userId);
    }

    @GetMapping("{meetId}")
    public MeetResponse getMeet(@Valid @NotNull @PathVariable Long meetId) {
        return meetService.getMeetById(meetId);
    }

    @GetMapping("/{projectId}")
    public List<MeetResponse> getMeetsByProject(@Valid @NotNull @PathVariable Long projectId) {
        return meetService.getMeetsByProjectId(projectId);
    }

    @PostMapping("/filter")
    public List<MeetResponse> getMeetsByFilter(@Valid @RequestBody MeetFilterRequest filterRequest) {
        return meetService.getMeetsByFilter(filterRequest);
    }
}
