package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.MomentDto;

import faang.school.projectservice.dto.client.MomentFilterDto;
import faang.school.projectservice.mapper.MomentFilterMapper;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.service.MomentService;
import faang.school.projectservice.validator.MomentDtoValidator;
import faang.school.projectservice.validator.MomentFilterDtoValidator;
import faang.school.projectservice.validator.MomentValidator;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequiredArgsConstructor
@RequestMapping("/moment")
@RestController
public class MomentController {
    private final MomentService momentService;
    private final MomentValidator momentValidator;
    private final MomentDtoValidator momentDtoValidator;
    private final MomentFilterDtoValidator momentFilterDtoValidator;
    private final MomentMapper mapper = Mappers.getMapper(MomentMapper.class);
    private final MomentFilterMapper filterMapper = Mappers.getMapper(MomentFilterMapper.class);

    @PostMapping("/create")
    public MomentDto createMoment(@RequestBody MomentDto momentDto) {
        momentDtoValidator.validateMomentDo(momentDto);
        return momentService.createMoment(momentDto);
    }

    @PutMapping("/update")
    public MomentDto updateMoment(@RequestBody MomentDto momentDto) {
        momentDtoValidator.validateMomentDo(momentDto);
        return momentService.updateMoment(momentDto);
    }

    @GetMapping("/filtered/{projectId}")
    public List<MomentDto> getMomentsFilteredByDateFromProjects(@PathVariable Long projectId, @RequestBody MomentFilterDto filter) {
        momentFilterDtoValidator.validateMomentFilterDto(filter);
        return momentService.getMomentsFilteredByDateFromProjects(projectId, filter);
    }

    @GetMapping("/all/{projectId}")
    public List<MomentDto> getAllMoments(@PathVariable Long projectId) {
        return momentService.getAllMoments(projectId);
    }

    @GetMapping("/{momentId}")
    public MomentDto getMomentById(@PathVariable Long momentId) {
        return momentService.getMomentById(momentId);
    }
}
