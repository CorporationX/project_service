package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.BeforeEach;

public class TestFiltersSetUp {
    InternshipFilterDto firstFilters;
    InternshipFilterDto secondFilters;
    InternshipFilterDto thirdFilters;
    InternshipFilterDto forthFilters;
    Internship firstInternship;
    Internship secondInternship;

    @BeforeEach
    void setUp(){
        firstInternship = Internship.builder()
                .role(TeamRole.DESIGNER)
                .status(InternshipStatus.IN_PROGRESS)
                .build();
        secondInternship = Internship.builder()
                .role(TeamRole.ANALYST)
                .status(InternshipStatus.COMPLETED)
                .build();

        firstFilters = InternshipFilterDto.builder()
                .role(TeamRole.ANALYST)
                .build();
        secondFilters = InternshipFilterDto.builder()
                .internshipStatus(InternshipStatus.COMPLETED)
                .build();
        thirdFilters = InternshipFilterDto.builder()
                .role(TeamRole.DESIGNER)
                .internshipStatus(InternshipStatus.IN_PROGRESS)
                .build();
        forthFilters = InternshipFilterDto.builder()
                .role(TeamRole.DESIGNER)
                .build();


    }
}
