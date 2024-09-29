package faang.school.projectservice.controller.meet;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.meet.MeetRequestDto;
import faang.school.projectservice.dto.meet.MeetResponseDto;
import faang.school.projectservice.service.meet.MeetService;
import faang.school.projectservice.validator.meet.CreateMeet;
import faang.school.projectservice.validator.meet.UpdateMeet;
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
@RequestMapping("/api/v1/meets")
public class MeetController {

    private final MeetService meetService;
    private final UserContext userContext;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MeetResponseDto create(@Validated(CreateMeet.class) @RequestBody MeetRequestDto dto) {
        long creatorId = userContext.getUserId();
        return meetService.create(creatorId, dto);
    }

    @PutMapping
    public MeetResponseDto update(@Validated(UpdateMeet.class) @RequestBody MeetRequestDto dto) {
        long creatorId = userContext.getUserId();
        return meetService.update(creatorId, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        long creatorId = userContext.getUserId();
        meetService.delete(creatorId, id);
    }

    @GetMapping("/projects/{projectId}")
    public List<MeetResponseDto> findAllByProjectIdFilter(@PathVariable("projectId") Long projectId,
                                                          MeetFilterDto filter) {
        return meetService.findAllByProjectIdFilter(projectId, filter);
    }

    @GetMapping("/{id}")
    public MeetResponseDto findById(@PathVariable("id") Long id) {
        return meetService.findById(id);
    }
}