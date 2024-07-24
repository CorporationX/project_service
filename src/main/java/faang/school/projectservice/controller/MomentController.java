package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.MomentDto;

import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.service.MomentService;
import faang.school.projectservice.validator.MomentValidator;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
public class MomentController {
    private final MomentService momentService;
    private final MomentValidator momentValidator;
    private final MomentMapper mapper = Mappers.getMapper(MomentMapper.class);

    public MomentDto createMoment(MomentDto momentDto) {
        Moment moment = mapper.toEntity(momentDto);
        momentValidator.validateMoment(moment);
        return momentService.createMoment(momentDto);
    }

    public MomentDto updateMoment(MomentDto momentDto) {
        Moment moment = mapper.toEntity(momentDto);
        momentValidator.validateMoment(moment);
        return momentService.updateMoment(momentDto);
    }
}
