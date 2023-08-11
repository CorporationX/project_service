package faang.school.projectservice.service.filters;

import faang.school.projectservice.dto.stage_invitation.StageInvitationFilterDto;
import faang.school.projectservice.filter.stage_invitation.StageInvitationAuthorIdFilter;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

public class StageInvitationAuthorIdFilterTest {
    private StageInvitationAuthorIdFilter filter = new StageInvitationAuthorIdFilter();
    private StageInvitationFilterDto validFilterDto;
    private StageInvitationFilterDto invalidFilterDto;

    @BeforeEach
    public void setUp() {
        validFilterDto = StageInvitationFilterDto.builder().authorId(1L).build();
        invalidFilterDto = StageInvitationFilterDto.builder().build();
    }

    @Test
    public void testIsApplicable() {
        Assertions.assertTrue(filter.isApplicable(validFilterDto));
        Assertions.assertFalse(filter.isApplicable(invalidFilterDto));
    }

    @Test
    public void testApply() {
        Stream<StageInvitation> stageInvitationStream = Stream.of(
                StageInvitation.builder()
                        .author(TeamMember.builder().id(1L).build())
                        .build(),
                StageInvitation.builder()
                        .author(TeamMember.builder().id(2L).build())
                        .build(),
                StageInvitation.builder()
                        .author(TeamMember.builder().id(3L).build())
                        .build()
        );
        List<StageInvitation> filteredList = filter.apply(stageInvitationStream, validFilterDto).toList();
        Assertions.assertEquals(1, filteredList.size());
        Assertions.assertEquals(1, filteredList.get(0).getAuthor().getId());
    }
}
