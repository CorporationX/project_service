package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.CreateMomentDto;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.UpdateMomentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MomentService {
    MomentDto createMoment(CreateMomentDto createMomentDto);

    MomentDto updateMoment(Long momentId, UpdateMomentDto updateMomentDto);

    Page<MomentDto> getAllMoments(Pageable pageable);

    MomentDto getMomentById(Long momentId);

    Page<MomentDto> getMomentsByProject(Long projectId, Integer month, List<Long> partnerProjectIds, Pageable pageable);
}
