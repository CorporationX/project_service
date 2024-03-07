package faang.school.projectservice.filter;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.filter.stage_invitation.StageInvitationAuthorIdFilter;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StageInvitationAuthorIdFilterTest {
    private StageInvitationAuthorIdFilter filter;
    private StageInvitationFilterDto validFilterDto;
    private StageInvitationFilterDto invalidFilterDto;

    @BeforeEach
    public void setUp() {
        validFilterDto = StageInvitationFilterDto.builder().authorId(1L).build();
        invalidFilterDto = StageInvitationFilterDto.builder().build();
        filter = new StageInvitationAuthorIdFilter();
    }

    @Test
    public void testIsApplicable() {
        Assertions.assertTrue(filter.IsApplicable(validFilterDto));
        Assertions.assertFalse(filter.IsApplicable(invalidFilterDto));
    }

    @Test
    public void testApply() {
        List<StageInvitation> stageInvitations = Stream.of(
                StageInvitation.builder()
                        .author(TeamMember.builder().id(1L).build())
                        .build(),
                StageInvitation.builder()
                        .author(TeamMember.builder().id(2L).build())
                        .build(),
                StageInvitation.builder()
                        .author(TeamMember.builder().id(3L).build())
                        .build()
        ).collect(Collectors.toList());
        filter.apply(stageInvitations, validFilterDto);
        Assertions.assertEquals(1, stageInvitations.size());
        Assertions.assertEquals(1, stageInvitations.get(0).getAuthor().getId());
    }
}
