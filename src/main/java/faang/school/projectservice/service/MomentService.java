package faang.school.projectservice.service;


import faang.school.projectservice.dto.moment.MomentCreateRequestDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.dto.moment.MomentResponseDto;
import faang.school.projectservice.dto.moment.MomentUpdateRequestDto;

import java.util.List;

public interface MomentService {
    MomentResponseDto createMoment(MomentCreateRequestDto momentCreateRequestDto);
    MomentResponseDto updateMoment(Long momentId, MomentUpdateRequestDto momentUpdateRequestDto);
    List<MomentResponseDto> getMoments(MomentFilterDto filter);
    List<MomentResponseDto> getAllMoments();
    MomentResponseDto getMoment(Long momentId);
}
