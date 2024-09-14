package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class InternshipRoleFilterTest {
    @InjectMocks
    private InternshipRoleFilter internshipRoleFilter;
    private InternshipFilterDto internshipFilterDto;
    private static final TeamRole INTERN = TeamRole.INTERN;
    private static final TeamRole DEVELOPER = TeamRole.DEVELOPER;

    @Nested
    class PositiveTests {
        @Test
        public void validateInternshipFilterDtoRoleNotNullTest() {
            internshipFilterDto = InternshipFilterDto.builder()
                    .role(INTERN)
                    .build();

            assertTrue(internshipRoleFilter.isApplicable(internshipFilterDto));
        }

        @Test
        public void validateInternshipFilterDtoReturnsFilteredListTest() {
            Stream<Internship> internships = Stream.of(Internship.builder()
                            .interns(List.of(TeamMember.builder()
                                    .roles(List.of(INTERN))
                                    .build()))
                            .build(),
                    Internship.builder()
                            .interns(List.of(TeamMember.builder()
                                    .roles(List.of(DEVELOPER))
                                    .build()))
                            .build());

            internshipFilterDto = InternshipFilterDto.builder()
                    .role(DEVELOPER)
                    .build();

            List<Internship> filteredInternships = List.of(Internship.builder()
                    .interns(List.of(TeamMember.builder()
                            .roles(List.of(DEVELOPER))
                            .build()))
                    .build());

            assertEquals(filteredInternships, internshipRoleFilter.applyFilter(internships, internshipFilterDto).toList());
        }
    }

    @Nested
    class NegativeTests {
        @Nested
        class isApplicable {
            @Test
            public void validateRoleIsNullTest() {
                internshipFilterDto = InternshipFilterDto.builder()
                        .role(null)
                        .build();

                assertFalse(internshipRoleFilter.isApplicable(internshipFilterDto));
            }
        }
    }
}
