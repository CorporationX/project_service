package faang.school.projectservice.controller;

import faang.school.projectservice.dto.MeetDto;
import faang.school.projectservice.service.MeetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/meets")
@RequiredArgsConstructor
public class MeetController {
    private final MeetService meetService;

    @GetMapping
    public List<MeetDto> getAllMeets() {
        return meetService.getAllMeets();
    }

    @GetMapping("/filter")
    public List<MeetDto> getMeetsByFilter(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) LocalDateTime startDate) {

        return meetService.getMeetsByFilter(title, startDate);
    }

    @GetMapping("/{id}")
    public MeetDto getMeetById(@PathVariable long id) {
        return meetService.getMeetById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MeetDto createMeet(@Valid @RequestBody MeetDto meetDto) {
        return meetService.createMeet(meetDto);
    }

    @PutMapping("/{id}")
    public MeetDto updateMeet(@PathVariable long id, @Valid @RequestBody MeetDto meetDto) {
        return meetService.updateMeet(id, meetDto);
    }

    @PatchMapping("/{id}/cancel")
    public MeetDto cancelMeet(@PathVariable long id) {
        return meetService.cancelMeet(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMeet(@PathVariable long id) {
        meetService.deleteMeet(id);
    }
}
