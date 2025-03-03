package faang.school.projectservice.filter.stage;

import faang.school.projectservice.dto.stage.StageInvitationFilterDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StageInvitationAuthorIdFilterTest {

    private StageInvitationAuthorIdFilter filter;
    private StageInvitationFilterDto filterDto;
    private Stream<StageInvitation> stream;
    private StageInvitation stageInvitationOne;
    private StageInvitation stageInvitationTwo;
    private StageInvitation stageInvitationThree;

    @BeforeEach
    public void setUp() {
        stageInvitationOne = StageInvitation.builder()
                .author(TeamMember.builder().id(1L).build())
                .build();
        stageInvitationTwo = StageInvitation.builder()
                .author(TeamMember.builder().id(2L).build())
                .build();
        stageInvitationThree = StageInvitation.builder()
                .author(TeamMember.builder().id(3L).build())
                .build();

        filter = new StageInvitationAuthorIdFilter();
        filterDto = new StageInvitationFilterDto();
        stream = Stream.of(stageInvitationOne, stageInvitationTwo, stageInvitationThree);
    }

    @Test
    public void testApplyWithMatchingAuthorId() {
        filterDto.setAuthorId(stageInvitationOne.getAuthor().getId());

        List<StageInvitation> actual = filter.apply(stream, filterDto).toList();

        assertEquals(1, actual.size());
        assertEquals(stageInvitationOne, actual.get(0));
    }

    @Test
    public void testApplyWithNoMatchingAuthorId() {
        filterDto.setAuthorId(stageInvitationOne.getAuthor().getId());
        stageInvitationOne.setAuthor(TeamMember.builder().id(2L).build());

        List<StageInvitation> actual = filter.apply(stream, filterDto).toList();

        assertEquals(0, actual.size());
    }

    @Test
    public void testApplyWithManyMatchingAuthorIds() {
        filterDto.setAuthorId(stageInvitationOne.getAuthor().getId());
        stageInvitationTwo.setAuthor(TeamMember.builder().id(1L).build());

        List<StageInvitation> actual = filter.apply(stream, filterDto).toList();

        assertEquals(2, actual.size());
        assertEquals(stageInvitationOne, actual.get(0));
    }
}
