package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.MeetDto;
import faang.school.projectservice.dto.filter.MeetFilterDto;
import faang.school.projectservice.service.MeetService;
import faang.school.projectservice.service.google.GoogleCalendarService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/meetings")
public class MeetController {

    private static final String ID_ERR_MESSAGE = "Meet id must be more then 0";

    private final GoogleCalendarService googleCalendarService;
    private final MeetService meetService;
    private final UserContext userContext;


    @GetMapping("/event/{eventId}")
    public MeetDto findByEventId(@PathVariable String eventId) {
        return googleCalendarService.getEventById(eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MeetDto create(@Valid @RequestBody MeetDto meetDto) {
        return meetService.create(meetDto);
    }

    @PutMapping("/{id}")
    public MeetDto update(@Valid @RequestBody MeetDto meetDto,
                               @PathVariable @Min(value = 1, message = ID_ERR_MESSAGE) Long id) {
        return meetService.update(meetDto, id);
    }

    @GetMapping("/{id}")
    public MeetDto findById(@PathVariable @Min(value = 1, message = ID_ERR_MESSAGE) Long id) {
        return meetService.findById(id);
    }

    @GetMapping("/project/{id}")
    public List<MeetDto> findAllByProjectId(@PathVariable @Min(value = 1, message = ID_ERR_MESSAGE) Long id) {
        return meetService.findAllByProjectId(id);
    }

    @GetMapping("/project/{id}/filter")
    public List<MeetDto> findAllByProjectIdAndFilter(@PathVariable @Min(value = 1, message = ID_ERR_MESSAGE) Long id,
                                                     @Valid @RequestBody MeetFilterDto meetFilterDto) {
        return meetService.findAllByProjectIdAndFilter(id, meetFilterDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteById(@PathVariable @Min(value = 1, message = ID_ERR_MESSAGE) Long id) {
        Long userId = userContext.getUserId();
        meetService.delete(id, userId);
    }

    @PostMapping("/acl/{email}")
    public void createAclForUserAccessForProjectEvents(@PathVariable String email) {
        meetService.createAcl(email);
    }

    @DeleteMapping("/acl/{email}")
    public void createAclForUserAccessForCalendar(@PathVariable String email) {
        meetService.deleteAcl(email);
    }

    @GetMapping("/acl/{email}")
    public String getAclForUserAccessForCalendar(@PathVariable String email) {
        return meetService.getAcl(email);
    }


}
