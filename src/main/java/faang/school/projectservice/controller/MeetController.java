package faang.school.projectservice.controller;

import faang.school.projectservice.dto.meet.CreateMeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.meet.MeetResponseDto;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.service.MeetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/meets")
@Validated
public class MeetController {
    private final MeetService meetService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MeetResponseDto createMeet(@RequestBody @Valid CreateMeetDto createMeetDto) {
        return meetService.createMeet(createMeetDto);
    }

    @GetMapping
    public List<MeetResponseDto> findAll() {
        return meetService.findAll();
    }

    @GetMapping("/project/{projectId}")
    public List<MeetResponseDto> findProjectMeetsByFilter(@PathVariable long projectId, MeetFilterDto filter) {
        return meetService.findProjectMeetsByFilter(projectId, filter);
    }

    @GetMapping("/{id}")
    public MeetResponseDto findById(@PathVariable long id) {
        return meetService.findById(id);
    }

    @PutMapping
    public MeetResponseDto updateMeet(@RequestBody @Valid UpdateMeetDto updateMeetDto) {
        return meetService.updateMeet(updateMeetDto);
    }

    @PutMapping("/{id}")
    public MeetResponseDto cancelMeet(@PathVariable long id) {
        return meetService.cancelMeet(id);
    }

    @DeleteMapping("/{id}")
    public void deleteMeet(@PathVariable long id) {
        meetService.deleteMeet(id);
    }
}
