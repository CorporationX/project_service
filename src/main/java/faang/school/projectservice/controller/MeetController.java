package faang.school.projectservice.controller;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.service.MeetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Controller
@RequestMapping("/api/v1/meet")
@RequiredArgsConstructor
public class MeetController {

    private final MeetService meetService;

    @PostMapping("/new")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public MeetDto createMeet(@RequestParam Long teamId,
                              @RequestBody @Valid MeetDto meetDto) {
        return meetService.createMeet(teamId, meetDto);
    }

    @PostMapping("/{meetId}")
    @ResponseStatus(HttpStatus.OK)
    public MeetDto updateMeet(@PathVariable Long meetId,
                              @RequestBody MeetDto meetDto) {
        return meetService.updateMeet(meetId, meetDto);
    }

    @DeleteMapping("/{meetId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteMeet(@PathVariable Long meetId) {
        meetService.deleteMeet(meetId);
    }

    @PostMapping("/filtered")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<MeetDto> getFilteredMeetsOfTeam(@RequestParam Long teamId,
                                                @RequestBody MeetFilterDto meetFilterDto) {
        return meetService.getFilteredMeetsOfTeam(teamId, meetFilterDto);
    }

    @GetMapping("/all")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<MeetDto> getAllMeetsOfUser() {
        return meetService.getAllMeetsOfUser();
    }

    @GetMapping("/{meetId}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public MeetDto getMeetById(@PathVariable Long meetId) {
        return meetService.getMeetById(meetId);
    }
}
