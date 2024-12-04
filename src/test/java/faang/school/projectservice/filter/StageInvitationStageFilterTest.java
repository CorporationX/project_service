package faang.school.projectservice.filter;

import faang.school.projectservice.dto.stage.invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.team.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StageInvitationStageFilterTest {
    private StageInvitationStageFilter filter;
    private Long firstStageId;
    private Long secondStageId;
    private Long firstAuthorId;
    private Long secondAuthorId;
    private Stage firstStage;
    private Stage secondStage;
    private TeamMember firstAuthor;
    private TeamMember secondAuthor;
    private StageInvitation firstStageInvitation;
    private StageInvitation secondStageInvitation;
    private StageInvitation thirdStageInvitation;
    private StageInvitation fourthStageInvitation;
    private Stream<StageInvitation> stageInvitations;

    @BeforeEach
    public void setUp() {
        filter = new StageInvitationStageFilter();

        firstStageId = 5L;
        firstAuthorId = 10L;
        secondStageId = 15L;
        secondAuthorId = 20L;

        firstStage = Stage.builder()
                .stageId(firstStageId)
                .build();
        firstAuthor = TeamMember.builder()
                .id(firstAuthorId)
                .build();
        secondStage = Stage.builder()
                .stageId(secondStageId)
                .build();
        secondAuthor = TeamMember.builder()
                .id(secondAuthorId)
                .build();

        firstStageInvitation = StageInvitation.builder()
                .stage(firstStage)
                .author(firstAuthor)
                .build();
        secondStageInvitation = StageInvitation.builder()
                .stage(secondStage)
                .author(secondAuthor)
                .build();
        thirdStageInvitation = StageInvitation.builder()
                .stage(firstStage)
                .author(firstAuthor)
                .build();
        fourthStageInvitation = StageInvitation.builder()
                .stage(firstStage)
                .author(secondAuthor)
                .build();

        stageInvitations = Stream.of(firstStageInvitation,
                secondStageInvitation,
                thirdStageInvitation,
                fourthStageInvitation);
    }

    @Test
    public void testIsApplicable() {
        // arrange
        StageInvitationFilterDto filterDto = new StageInvitationFilterDto(firstStageId, firstAuthorId);

        boolean expected = true;

        // act
        boolean actual = filter.isApplicable(filterDto);

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void testIsNotApplicable() {
        // arrange
        Long invalidStageId = null;
        StageInvitationFilterDto filterDto = new StageInvitationFilterDto(invalidStageId, firstAuthorId);

        boolean expected = false;

        // act
        boolean actual = filter.isApplicable(filterDto);

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void testApply() {
        // arrange
        StageInvitationFilterDto filterDto = new StageInvitationFilterDto(firstStageId, firstAuthorId);

        List<StageInvitation> expected = List.of(firstStageInvitation,
                thirdStageInvitation,
                fourthStageInvitation);

        // act
        List<StageInvitation> actual = filter.apply(stageInvitations, filterDto).toList();

        // assert
        assertEquals(expected, actual);
    }

    @Test
    public void testApplyNoSuchStage() {
        // arrange
        Long fakeStageId = 777L;
        StageInvitationFilterDto filterDto = new StageInvitationFilterDto(fakeStageId, firstAuthorId);;

        List<StageInvitation> expected = new ArrayList<>();

        // act
        List<StageInvitation> actual = filter.apply(stageInvitations, filterDto).toList();

        // assert
        assertEquals(expected, actual);
    }
}
