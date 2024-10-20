package faang.school.projectservice.controller;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.service.MeetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/meets")
@RequiredArgsConstructor
public class MeetController {
    private final MeetService meetService;

    @PostMapping
    public MeetDto createMeeting(@RequestBody MeetDto meetDto) {
        return meetService.createMeeting(meetDto);
    }

    @PutMapping("/{id}")
    public MeetDto updateMeeting(@PathVariable long id, @RequestBody MeetDto meetDto) {
        return meetService.updateMeeting(id, meetDto);
    }

    @DeleteMapping("/{id}")
    public void deleteMeeting(@PathVariable long id) {
        meetService.deleteMeeting(id);
    }

    @GetMapping
    public List<MeetDto> getAllMeetings(@RequestParam String title, @RequestParam String date) {
        return meetService.getAllMeetings(title, date);
    }

    @GetMapping("/{id}")
    public MeetDto getMeetingById(@PathVariable long id) {
        return meetService.getMeetingById(id);
    }
}
