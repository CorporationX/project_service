package faang.school.projectservice.service.meet.interfaces;

import faang.school.projectservice.dto.meet.MeetCreateDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.meet.MeetResponseDto;
import faang.school.projectservice.dto.meet.MeetUpdateDto;

import java.util.List;

public interface MeetService {

    MeetResponseDto createMeet(MeetCreateDto meetCreateDto);

    MeetResponseDto updateMeet(MeetUpdateDto meetUpdateDto);

    void deleteMeet(long meetId);

    MeetResponseDto getMeet(long meetId);

    List<MeetResponseDto> getMeetsByProjectId(long projectId, MeetFilterDto meetFilterDto);
}
