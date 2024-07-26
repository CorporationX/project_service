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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequiredArgsConstructor
@RestController
public class MomentController {
    private final MomentService momentService;
    private final MomentValidator momentValidator;
    private final MomentDtoValidator momentDtoValidator;
    private final MomentFilterDtoValidator momentFilterDtoValidator;
    private final MomentMapper mapper = Mappers.getMapper(MomentMapper.class);
    private final MomentFilterMapper filterMapper = Mappers.getMapper(MomentFilterMapper.class);

    public MomentDto createMoment(MomentDto momentDto) {
        momentDtoValidator.validateMomentDo(momentDto);
        return momentService.createMoment(momentDto);
    }

    public MomentDto updateMoment(MomentDto momentDto) {
        momentDtoValidator.validateMomentDo(momentDto);
        return momentService.updateMoment(momentDto);
    }

    public List<MomentDto> getMomentsFilteredByDateFromProjects(Long ProjectId, MomentFilterDto filter) {
        momentFilterDtoValidator.validateMomentFilterDto(filter);
        return momentService.getMomentsFilteredByDateFromProjects(ProjectId, filter);
    }

    public List<MomentDto> getAllMoments(Long projectId) {
        return momentService.getAllMoments(projectId);
    }

    public MomentDto getMomentById(Long momentId) {
        return momentService.getMomentById(momentId);
    }
}
