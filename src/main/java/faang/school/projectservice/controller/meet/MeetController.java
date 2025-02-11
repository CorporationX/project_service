package faang.school.projectservice.controller.meet;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.service.meet.MeetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("/meets")
@RestController
public class MeetController {
    private final MeetMapper mapper;
    private final MeetService meetService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public MeetDto create(@Valid @RequestBody MeetDto dto) {
        log.info("Received DTO: {}", dto);
        Meet inputMeet = mapper.toEntity(dto);
        Meet meet = meetService.create(dto.projectId(), inputMeet);
        return mapper.toDto(meet);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{meetId}")
    public MeetDto update(
            @Positive @PathVariable Long meetId,
            @Valid @RequestBody MeetDto dto) {
        Meet inputMeet = mapper.toEntity(dto);
        Meet meet = meetService.update(meetId, dto.projectId(), inputMeet);
        return mapper.toDto(meet);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{meetId}/cancel")
    public void cancelMeet(
            @Positive @PathVariable Long meetId,
            @Positive @RequestParam Long creatorId) {
        meetService.cancelMeet(meetId, creatorId);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{meetId}/delete")
    public void deleteMeet(
            @Positive @PathVariable Long meetId,
            @Positive @RequestParam Long creatorId) {
        meetService.deleteMeet(meetId, creatorId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{meetId}")
    public MeetDto getById(@Positive @PathVariable Long meetId) {
        Meet meet = meetService.getById(meetId);
        return mapper.toDto(meet);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<MeetDto> findAll() {
        List<Meet> meets = meetService.findAll();
        return mapper.toDtoList(meets);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{projectId}/all")
    public List<MeetDto> getMeets(@PathVariable Long projectId, MeetFilterDto filterDto) {
        List<Meet> meets = meetService
                .getMeetsWithFilters(projectId, filterDto.title(), filterDto.startDate());
        return mapper.toDtoList(meets);
    }
}
