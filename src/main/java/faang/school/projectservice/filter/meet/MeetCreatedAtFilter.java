package faang.school.projectservice.filter.meet;

import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.model.Meet;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;


@Component
public class MeetCreatedAtFilter implements MeetFilter {
    public boolean isApplicable(MeetFilterDto filter) {
        return filter.getCreatedAt() != null;
    }

    public List<MeetDto> applyFilter(List<MeetDto> meets, MeetFilterDto filter) {
        return meets.stream()
                .filter(meet -> {
                    LocalDate meetDate = meet.getCreatedAt().toLocalDate();
                    LocalDate filterDate = filter.getCreatedAt().toLocalDate();
                    return meetDate.equals(filterDate);
                })
                .toList();
    }

}

