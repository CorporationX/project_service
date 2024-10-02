package faang.school.projectservice.controller.meet;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.meet.CreateMeetDto;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.service.meet.filter.MeetFilters;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.mapper.meet.MeetMapper;
import faang.school.projectservice.service.meet.MeetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping("/meets")
public class MeetController {
    private final MeetService meetService;
    private final MeetMapper meetMapper;
    private final UserContext userContext;

    @PostMapping
    public MeetDto createMeet(@RequestBody @Valid CreateMeetDto createMeetDto) {
        var userId = userContext.getUserId();
        var meet = meetMapper.toEntity(createMeetDto);
        meet = meetService.createMeet(userId, createMeetDto.getProjectId(), meet);
        return meetMapper.toDto(meet);
    }

    @PatchMapping("/{meetId}")
    public MeetDto updateMeet(@PathVariable Long meetId, @RequestBody @Valid UpdateMeetDto updateMeetDto) {
        var userId = userContext.getUserId();
        var entity = meetMapper.toEntity(updateMeetDto);
        var updatedMeet = meetService.updateMeet(userId, meetId, entity);
        return meetMapper.toDto(updatedMeet);
    }

    @PatchMapping("/{meetId}/cancel")
    public void cancelMeet(@PathVariable Long meetId) {
        var userId = userContext.getUserId();
        meetService.cancelMeet(userId, meetId);
    }

    @GetMapping("/{meetId}")
    public MeetDto getMeet(@PathVariable Long meetId) {
        var userId = userContext.getUserId();
        var foundMeet = meetService.getMeetById(userId, meetId);
        return meetMapper.toDto(foundMeet);
    }

    @GetMapping("/{projectId}/meets")
    public Collection<MeetDto> getMeetsByProjectId(@PathVariable Long projectId) {
        var userId = userContext.getUserId();
        var meets = meetService.getMeetsByProjectId(userId, projectId);
        return meetMapper.toDtos(meets);
    }

    @PostMapping("/{projectId}/filter")
    public Collection<MeetDto> getMeetsByProjectIdFiltered(@PathVariable Long projectId,
                                                           @RequestBody @Valid MeetFilters meetFilters) {
        var userId = userContext.getUserId();
        var meets = meetService.getMeetsByFilters(userId, projectId, meetFilters);
        return meetMapper.toDtos(meets);
    }

    @DeleteMapping("/{meetId}")
    public void deleteMeet(@PathVariable Long meetId) {
        var userId = userContext.getUserId();
        meetService.deleteMeet(userId, meetId);
    }
}
