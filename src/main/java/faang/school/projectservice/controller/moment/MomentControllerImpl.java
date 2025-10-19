package faang.school.projectservice.controller.moment;

import faang.school.projectservice.dto.moment.CreateMomentDto;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.UpdateMomentDto;
import faang.school.projectservice.service.moment.MomentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MomentControllerImpl implements MomentController {

    private final MomentService momentService;

    @Override
    public MomentDto create(CreateMomentDto createMomentDto) {
        return momentService.createMoment(createMomentDto);
    }

    @Override
    public MomentDto update(Long momentId, UpdateMomentDto updateMomentDto) {
        return momentService.updateMoment(momentId, updateMomentDto);
    }

    @Override
    public Page<MomentDto> getAll(Pageable pageable) {
        return momentService.getAllMoments(pageable);
    }

    @Override
    public MomentDto getMomentById(Long momentId) {
        return momentService.getMomentById(momentId);
    }

    @Override
    public Page<MomentDto> getMomentsByProject(
            Long projectId,
            Integer month,
            List<Long> partnerProjectIds,
            Pageable pageable
    ) {
        return momentService.getMomentsByProject(projectId, month, partnerProjectIds, pageable);
    }
}
