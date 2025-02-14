package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RoleFilterTest {
    private static final TeamRole ROLE = TeamRole.DEVELOPER;

    private final RoleFilter roleFilter = new RoleFilter();

    @Test
    void testIsApplicableIsTrue() {
        InternshipFilterDto filter = InternshipFilterDto.builder()
                .role(ROLE)
                .build();

        assertTrue(roleFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicableIsFalse() {
        InternshipFilterDto filter = InternshipFilterDto.builder().build();

        assertFalse(roleFilter.isApplicable(filter));
    }

    @Test
    void testApplyIsSuccess() {
        InternshipFilterDto filter = InternshipFilterDto.builder()
                .role(ROLE)
                .build();

        Internship internship = Internship.builder()
                .role(ROLE)
                .build();

        Stream<Internship> internshipStream = List.of(internship).stream();
        Stream<Internship> result = roleFilter.apply(internshipStream, filter);

        assertFalse(result.toList().isEmpty());
    }

    @Test
    void testApplyIsNotSuccess() {
        InternshipFilterDto filter = InternshipFilterDto.builder()
                .role(ROLE)
                .build();

        Internship internship = Internship.builder()
                .role(TeamRole.ANALYST)
                .build();

        Stream<Internship> internshipStream = List.of(internship).stream();
        Stream<Internship> result = roleFilter.apply(internshipStream, filter);

        assertTrue(result.toList().isEmpty());
    }
}