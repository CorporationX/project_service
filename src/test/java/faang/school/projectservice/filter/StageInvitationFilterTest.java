package faang.school.projectservice.filter;

import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageInvitationFilterTest {
    private StageInvitationStatusFilter stageInvitationStatusFilter;
    private StageInvitationAuthorFilter stageInvitationAuthorFilter;
    private StageInvitationInvitedFilter stageInvitationInvitedFilter;
    private StageInvitationStageFilter stageInvitationStageFilter;
    private StageInvitationFilterDto filterDto;

    @BeforeEach
    public void setUp() {
        stageInvitationStatusFilter = new StageInvitationStatusFilter();
        stageInvitationAuthorFilter = new StageInvitationAuthorFilter();
        stageInvitationInvitedFilter = new StageInvitationInvitedFilter();
        stageInvitationStageFilter = new StageInvitationStageFilter();
        filterDto = new StageInvitationFilterDto();
    }

    @Test
    public void testIsApplicableWithStatusPattern() {
        filterDto.setStatusPattern(StageInvitationStatus.PENDING.toString());
        boolean isApplicable = stageInvitationStatusFilter.isApplicable(filterDto);
        assertTrue(isApplicable);
    }

    @Test
    public void testIsApplicableWithoutStatusPattern() {
        filterDto.setStatusPattern(null);
        boolean isApplicable = stageInvitationStatusFilter.isApplicable(filterDto);
        assertFalse(isApplicable);
    }

    @Test
    public void testApplyWithMatchingStatus() {
        StageInvitationFilterDto filterDto = new StageInvitationFilterDto();
        filterDto.setStatusPattern(StageInvitationStatus.ACCEPTED.toString());

        StageInvitation invitation1 = mock(StageInvitation.class);
        when(invitation1.getStatus()).thenReturn(StageInvitationStatus.ACCEPTED);

        StageInvitation invitation2 = mock(StageInvitation.class);
        when(invitation2.getStatus()).thenReturn(StageInvitationStatus.REJECTED);

        Stream<StageInvitation> invitationStream = Stream.of(invitation1, invitation2);
        Stream<StageInvitation> filteredStream = stageInvitationStatusFilter.apply(invitationStream, filterDto);

        long count = filteredStream.count();
        assertEquals(1L, count);
    }

    @Test
    public void testApplyWithNoMatchingStatus() {
        StageInvitationFilterDto filterDto = new StageInvitationFilterDto();
        filterDto.setStatusPattern(StageInvitationStatus.ACCEPTED.toString());

        StageInvitation invitation1 = mock(StageInvitation.class);
        when(invitation1.getStatus()).thenReturn(StageInvitationStatus.REJECTED);

        StageInvitation invitation2 = mock(StageInvitation.class);
        when(invitation2.getStatus()).thenReturn(StageInvitationStatus.PENDING);

        Stream<StageInvitation> invitationStream = Stream.of(invitation1, invitation2);
        Stream<StageInvitation> filteredStream = stageInvitationStatusFilter.apply(invitationStream, filterDto);

        long count = filteredStream.count();
        assertEquals(0L, count);
    }

    @Test
    public void testIsApplicableWithAuthorPattern() {
        filterDto.setAuthorPattern(10L);
        boolean isApplicable = stageInvitationAuthorFilter.isApplicable(filterDto);
        assertTrue(isApplicable);
    }

    @Test
    public void testIsApplicableWithoutAuthorPattern() {
        filterDto.setAuthorPattern(null);
        boolean isApplicable = stageInvitationAuthorFilter.isApplicable(filterDto);
        assertFalse(isApplicable);
    }

    @Test
    public void testApplyWithMatchingAuthor() {
        filterDto.setAuthorPattern(10L);

        TeamMember author = mock(TeamMember.class);
        when(author.getUserId()).thenReturn(10L);

        StageInvitation invitation1 = mock(StageInvitation.class);
        when(invitation1.getAuthor()).thenReturn(author);

        StageInvitation invitation2 = mock(StageInvitation.class);
        when(invitation2.getAuthor()).thenReturn(mock(TeamMember.class));

        Stream<StageInvitation> invitationStream = Stream.of(invitation1, invitation2);

        Stream<StageInvitation> filteredStream = stageInvitationAuthorFilter.apply(invitationStream, filterDto);
        long count = filteredStream.count();

        assertEquals(1L, count);
    }

    @Test
    public void testApplyWithNoMatchingAuthor() {
        StageInvitation stageInvitation = new StageInvitation();
        stageInvitation.setId(245L);
        stageInvitation.setAuthor(TeamMember.builder().userId(11L).build());

        filterDto.setAuthorPattern(10L);

        StageInvitation invitation1 = mock(StageInvitation.class);
        when(invitation1.getAuthor()).thenReturn(TeamMember.builder().userId(11L).build());

        StageInvitation invitation2 = mock(StageInvitation.class);
        when(invitation2.getAuthor()).thenReturn(TeamMember.builder().userId(12L).build());

        Stream<StageInvitation> invitationStream = Stream.of(invitation1, invitation2);

        Stream<StageInvitation> filteredStream = stageInvitationAuthorFilter.apply(invitationStream, filterDto);
        long count = filteredStream.count();

        assertEquals(0L, count);
    }

    @Test
    public void testIsApplicableWithInvitedPattern() {
        filterDto.setInvitedPattern(10L);
        boolean isApplicable = stageInvitationInvitedFilter.isApplicable(filterDto);
        assertTrue(isApplicable);
    }

    @Test
    public void testIsApplicableWithoutInvitedPattern() {
        filterDto.setInvitedPattern(null);
        boolean isApplicable = stageInvitationInvitedFilter.isApplicable(filterDto);
        assertFalse(isApplicable);
    }

    @Test
    public void testApplyWithMatchingInvited() {
        filterDto.setInvitedPattern(10L);

        StageInvitation invitation1 = mock(StageInvitation.class);
        TeamMember invited1 = TeamMember.builder().id(10L).build();
        when(invitation1.getInvited()).thenReturn(invited1);

        StageInvitation invitation2 = mock(StageInvitation.class);
        TeamMember invited2 = TeamMember.builder().id(11L).build();
        when(invitation2.getInvited()).thenReturn(invited2);

        Stream<StageInvitation> invitationStream = Stream.of(invitation1, invitation2);
        Stream<StageInvitation> filteredStream = stageInvitationInvitedFilter.apply(invitationStream, filterDto);

        long count = filteredStream.count();
        assertEquals(1L, count);
    }

    @Test
    public void testApplyWithNoMatchingInvited() {
        filterDto.setInvitedPattern(10L);

        StageInvitation invitation1 = mock(StageInvitation.class);
        TeamMember invited1 = TeamMember.builder().id(11L).build();
        when(invitation1.getInvited()).thenReturn(invited1);

        StageInvitation invitation2 = mock(StageInvitation.class);
        TeamMember invited2 = TeamMember.builder().id(12L).build();
        when(invitation2.getInvited()).thenReturn(invited2);

        Stream<StageInvitation> invitationStream = Stream.of(invitation1, invitation2);
        Stream<StageInvitation> filteredStream = stageInvitationInvitedFilter.apply(invitationStream, filterDto);

        long count = filteredStream.count();
        assertEquals(0L, count);
    }

    @Test
    public void testApplyWithMatchingStageName() {
        filterDto.setStagePattern("Stage 1");

        StageInvitation invitation1 = mock(StageInvitation.class);
        Stage stage1 = Stage.builder().stageName("Stage 1").build();
        when(invitation1.getStage()).thenReturn(stage1);

        StageInvitation invitation2 = mock(StageInvitation.class);
        Stage stage2 = Stage.builder().stageName("Stage 2").build();
        when(invitation2.getStage()).thenReturn(stage2);

        Stream<StageInvitation> invitationStream = Stream.of(invitation1, invitation2);
        Stream<StageInvitation> filteredStream = stageInvitationStageFilter.apply(invitationStream, filterDto);

        long count = filteredStream.count();
        assertEquals(1L, count);
    }

    @Test
    public void testApplyWithNoMatchingStageName() {
        filterDto.setStagePattern("Stage 1");

        StageInvitation invitation1 = mock(StageInvitation.class);
        Stage stage1 = Stage.builder().stageName("Stage 2").build();
        when(invitation1.getStage()).thenReturn(stage1);

        StageInvitation invitation2 = mock(StageInvitation.class);
        Stage stage2 = Stage.builder().stageName("Stage 3").build();
        when(invitation2.getStage()).thenReturn(stage2);

        Stream<StageInvitation> invitationStream = Stream.of(invitation1, invitation2);
        Stream<StageInvitation> filteredStream = stageInvitationStageFilter.apply(invitationStream, filterDto);

        long count = filteredStream.count();
        assertEquals(0L, count);
    }

    @Test
    public void testApplyWithNullStagePattern() {
        filterDto.setStagePattern("Stage 1");

        StageInvitation invitation1 = mock(StageInvitation.class);
        Stage stage1 = Stage.builder().stageName("Stage 1").build();
        when(invitation1.getStage()).thenReturn(stage1);

        StageInvitation invitation2 = mock(StageInvitation.class);
        Stage stage2 = Stage.builder().stageName(null).build();
        when(invitation2.getStage()).thenReturn(stage2);

        Stream<StageInvitation> invitationStream = Stream.of(invitation1, invitation2);
        Stream<StageInvitation> filteredStream = stageInvitationStageFilter.apply(invitationStream, filterDto);

        long count = filteredStream.count();
        assertEquals(1L, count);
    }
}