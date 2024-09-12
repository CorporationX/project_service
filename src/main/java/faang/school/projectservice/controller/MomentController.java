package faang.school.projectservice.controller;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.filter.moment.MomentFilterDto;
import faang.school.projectservice.service.MomentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MomentController {
    private final MomentService service;

    public MomentDto create(MomentDto momentDto) {
        validateMomentDto(momentDto);

        return service.create(momentDto);
    }

    public MomentDto update(MomentDto momentDto) {
        validateMomentDto(momentDto);

        return service.update(momentDto);
    }

    public List<MomentDto> getMoments(MomentFilterDto filterDto) {
        return service.getMoments(filterDto);
    }

    public List<MomentDto> getAllMoments() {
        return service.getAllMoments();
    }

    public MomentDto getMoment(long id) {
        if (id < 1) {
            throw new RuntimeException("Передан некорректный id");
        }

        return service.getMoment(id);
    }

    private void validateMomentDto(MomentDto momentDto) {
        if (momentDto.getName() == null) {
            throw new RuntimeException("Наименование момента не может быть пустым");
        }
        if (momentDto.getProjectIds() == null || momentDto.getProjectIds().isEmpty()) {
            throw new RuntimeException("Момент должен относиться к какому-нибудь проекту");
        }
    }
}
