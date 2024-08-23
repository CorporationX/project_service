package faang.school.projectservice.service.meet.filter;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;

import java.util.stream.Stream;

public interface MeetFilter {

    boolean isApplicable(MeetFilterDto meetDto);

    Stream<MeetDto> apply(Stream<MeetDto> meets, MeetFilterDto filter);
}
