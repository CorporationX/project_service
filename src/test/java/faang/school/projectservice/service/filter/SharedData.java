package faang.school.projectservice.service.filter;

import faang.school.projectservice.model.Meet;
import faang.school.projectservice.service.meet.filter.MeetFilters;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SharedData {
    private String title = "first";
    private LocalDateTime begin = LocalDateTime.of(2024, 1, 10, 0, 0, 0);
    private LocalDateTime end = LocalDateTime.of(2024, 2, 10, 0, 0, 0);
    private MeetFilters meetFilters = new MeetFilters();
    private Meet firstMeet = new Meet();
    private Meet secondMeet = new Meet();
    private Meet thirdMeet = new Meet();
    private List<Meet> meets = List.of(firstMeet, secondMeet, thirdMeet);

    {
        meetFilters.setTitle(title);
        meetFilters.setBegin(begin);
        meetFilters.setEnd(end);

        firstMeet.setId(1L);
        firstMeet.setTitle("first title");
        firstMeet.setCreatedAt(LocalDateTime.of(2024, 1, 1, 0, 0, 0));

        secondMeet.setId(2L);
        secondMeet.setTitle("second title");
        secondMeet.setCreatedAt(LocalDateTime.of(2024, 2, 1, 0, 0, 0));

        thirdMeet.setId(3L);
        thirdMeet.setTitle("third title");
        thirdMeet.setCreatedAt(LocalDateTime.of(2024, 3, 1, 0, 0, 0));
    }
}
