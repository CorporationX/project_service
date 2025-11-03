package faang.school.projectservice.service.meet;

import faang.school.projectservice.dto.meet.CreateMeetDto;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.meet.UpdateMeetDto;

import java.util.List;

public interface MeetService {

    MeetDto create(CreateMeetDto createMeetDto);

    MeetDto update(long meetId, UpdateMeetDto updateMeetDto);

    MeetDto cancel(long meetId);

    void delete(long meetId);

    List<MeetDto> getAll();

    MeetDto getById(long meetId);

    List<MeetDto> getByFilters(MeetFilterDto meetFilterDto);
}