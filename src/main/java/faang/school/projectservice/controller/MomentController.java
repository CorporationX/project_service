package faang.school.projectservice.controller;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.service.MomentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/moment")
public class MomentController {

    private final MomentService momentService;
    private final MomentMapper momentMapper;

    @PostMapping("/project/{projectId}")
    @ResponseStatus(HttpStatus.CREATED)
    public MomentDto create(@PathVariable long projectId, @RequestBody MomentDto moment) {
        return momentMapper.momentToDto(momentService.create(projectId, moment));
    }

    @PutMapping("/{momentId}")
    @ResponseStatus(HttpStatus.OK)
    public MomentDto update(@PathVariable long momentId, @RequestBody MomentDto momentDto) {
        return momentMapper.momentToDto(momentService.update(momentId, momentDto));
    }

    @GetMapping("/filtered/project/{projectId}")
    public List<MomentDto> getAllMomentsFiltered(@PathVariable long projectId,
                                                 @RequestBody MomentFilterDto filters) {
        return momentMapper.momentsToDtoList(momentService.getFilteredMomentsOfProject(projectId, filters));
    }

    @GetMapping("/project/{projectId}")
    public List<MomentDto> getAllMoments(@PathVariable long projectId) {
        return momentMapper.momentsToDtoList(momentService.getAllMoments(projectId));
    }

    @GetMapping("/{momentId}")
    public MomentDto getMoment(@PathVariable long momentId) {
        return momentMapper.momentToDto(momentService.getMoment(momentId));
    }

    @DeleteMapping("/{momentId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteMoment(@PathVariable long momentId) {
        momentService.delete(momentId);
    }
}


