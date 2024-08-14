package faang.school.projectservice.controller;

import faang.school.projectservice.dto.PageRequestDto;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.service.meet.MeetService;
import faang.school.projectservice.validator.meet.CreateValidation;
import faang.school.projectservice.validator.meet.UpdateValidation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
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

@RestController
@RequestMapping("/v1/meets")
@RequiredArgsConstructor
public class MeetController {

    private final MeetService meetService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MeetDto create(@Validated(CreateValidation.class) @RequestBody MeetDto meetDto) {
        return meetService.create(meetDto);
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.ACCEPTED)
    public MeetDto update(@Validated(UpdateValidation.class) @RequestBody MeetDto meetDto) {
        return meetService.update(meetDto);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        meetService.delete(id);
    }

    @GetMapping("/project/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<MeetDto> getProjectMeetsByFilter(@PathVariable Long id, @ParameterObject MeetFilterDto meetFilterDto) {
        return meetService.getProjectMeetsByFilter(id, meetFilterDto);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public Page<MeetDto> getAllPageable(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(defaultValue = "createdAt") String sortBy,
                                        @RequestParam(defaultValue = "desc") String sortDirection) {
        return meetService.getAllPageable(PageRequestDto.builder()
                .page(page).size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build());
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MeetDto getById(@PathVariable Long id) {
        return meetService.getById(id);
    }
}
