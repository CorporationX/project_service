package faang.school.projectservice.service.meet;

import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.meet.MeetRequestDto;
import faang.school.projectservice.dto.meet.MeetResponseDto;

import java.util.List;

public interface MeetService {

    MeetResponseDto create(long creatorId, MeetRequestDto dto);

    MeetResponseDto update(long creatorId, MeetRequestDto dto);

    void delete(long creatorId, Long id);

    List<MeetResponseDto> findAllByProjectIdFilter(Long projectId, MeetFilterDto filter);

    MeetResponseDto findById(Long id);
}