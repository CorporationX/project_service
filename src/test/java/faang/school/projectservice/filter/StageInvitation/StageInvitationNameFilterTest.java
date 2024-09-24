package faang.school.projectservice.filter.stageInvitation;

import faang.school.projectservice.dto.client.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StageInvitationNameFilterTest {

    @InjectMocks
    private StageInvitationNameFilter stageInvitationNameFilter;

    private StageInvitationFilterDto stageInvitationFilterDto;

    private final static String INVITER_STAGE_NAME_PATTERN = "Smth";

    @Test
    @DisplayName("If StageInvitationFilterDto invitedStageName is filled in correctly, return true")
    void whenStageInvitationFilterDtoSpecifiedInvitedNamePatternIsNotNullThenReturnTrue() {
        stageInvitationFilterDto = StageInvitationFilterDto.builder()
                .invitedStageName(INVITER_STAGE_NAME_PATTERN)
                .build();

        assertTrue(stageInvitationNameFilter.isApplicable(stageInvitationFilterDto));
    }

    @Test
    @DisplayName("If the invitedStageName field of StageInvitationFilterDto is filled correctly," +
            " we return the filtered list")
    void whenStageInvitationFilterDtoSpecifiedInvitedIdThenReturnList() {
        Stream<StageInvitation> stageInvitations = Stream.of(
                StageInvitation.builder()
                        .stage(Stage.builder()
                                .stageName(INVITER_STAGE_NAME_PATTERN)
                                .build())
                        .build(),
                StageInvitation.builder()
                        .stage(Stage.builder()
                                .stageName("false")
                                .build())
                        .build());

        stageInvitationFilterDto = StageInvitationFilterDto.builder()
                .invitedStageName(INVITER_STAGE_NAME_PATTERN)
                .build();


        List<StageInvitation> stageInvitationsAfterFilter = List.of(
                StageInvitation.builder()
                        .stage(Stage.builder()
                                .stageName(INVITER_STAGE_NAME_PATTERN)
                                .build())
                        .build());

        assertEquals(stageInvitationsAfterFilter, stageInvitationNameFilter.apply(stageInvitations,
                stageInvitationFilterDto).toList());
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("If StageInvitationFilterDto field invitedStageName null, return false")
        void whenStageInvitationFilterDtoStatusIsNullThenReturnFalse() {
            stageInvitationFilterDto = StageInvitationFilterDto.builder()
                    .invitedStageName(null)
                    .build();

            assertFalse(stageInvitationNameFilter.isApplicable(stageInvitationFilterDto));
        }

        @Test
        @DisplayName("If the StageInvitationFilterDto field invitedStageName is empty, return false")
        void whenStageInvitationFilterDtoStatusIsEmptyThenReturnFalse() {
            stageInvitationFilterDto = StageInvitationFilterDto.builder()
                    .invitedStageName("  ")
                    .build();

            assertFalse(stageInvitationNameFilter.isApplicable(stageInvitationFilterDto));
        }
    }
}




