package faang.school.projectservice.filter.stageInvitation;

import faang.school.projectservice.dto.client.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
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
class StageInvitationIdFilterTest {

    @InjectMocks
    private StageInvitationInvitedIdFilter stageInvitationInvitedIdFilter;

    private StageInvitationFilterDto stageInvitationFilterDto;

    private final static Long INVITED_ID = 1L;

    @Nested
    class  PositiveTests {
        @Test
        @DisplayName("If StageInvitationFilterDto invitedId is filled in correctly, return true")
        void whenStageInvitationFilterDtoSpecifiedInvitedIdIsNotNullThenReturnTrue() {
            stageInvitationFilterDto = StageInvitationFilterDto.builder()
                    .invitedId(INVITED_ID)
                    .build();

            assertTrue(stageInvitationInvitedIdFilter.isApplicable(stageInvitationFilterDto));
        }

        @Test
        @DisplayName("If the invitedId field of StageInvitationFilterDto is filled correctly, " +
                "we return the filtered list")
        void whenStageInvitationFilterDtoSpecifiedInvitedIdThenReturnList() {
            Stream<StageInvitation> stageInvitations = Stream.of(
                    StageInvitation.builder()
                            .invited(TeamMember.builder()
                                    .id(INVITED_ID)
                                    .build())
                            .build(),
                    StageInvitation.builder()
                            .status(StageInvitationStatus.REJECTED)
                            .build());

            stageInvitationFilterDto = StageInvitationFilterDto.builder()
                    .invitedId(INVITED_ID)
                    .build();

            List<StageInvitation> stageInvitationsAfterFilter = List.of(
                    StageInvitation.builder()
                            .invited(TeamMember.builder()
                                    .id(INVITED_ID)
                                    .build())
                            .build());

            assertEquals(stageInvitationsAfterFilter, stageInvitationInvitedIdFilter.apply(stageInvitations,
                    stageInvitationFilterDto).toList());
        }

        @Nested
        class NegativeTests {

            @Test
            @DisplayName("If StageInvitationFilterDto field invitedId null, return false")
            void whenStageInvitationFilterDtoStatusIsNullThenReturnFalse() {
                stageInvitationFilterDto = StageInvitationFilterDto.builder()
                        .invitedId(null)
                        .build();

                assertFalse(stageInvitationInvitedIdFilter.isApplicable(stageInvitationFilterDto));
            }
        }
    }
}