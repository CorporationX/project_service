package faang.school.projectservice.filter.stageInvitation;

import faang.school.projectservice.dto.filter.stageinvitation.StageInvitationFilterDto;
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
class StageInvitationStatusFilterTest {

    @InjectMocks
    private StageInvitationStatusFilter stageInvitationStatusFilter;

    private StageInvitationFilterDto stageInvitationFilterDto;

    private final static StageInvitationStatus INVITATION_STATUS = StageInvitationStatus.ACCEPTED;

    @Nested
    class  PositiveTests {
        @Test
        @DisplayName("If StageInvitationFilterDto status is set, return true")
        void whenStageInvitationFilterDtoSpecifiedStatusIsNotNullThenReturnTrue() {
            stageInvitationFilterDto = StageInvitationFilterDto.builder()
                    .status(INVITATION_STATUS)
                    .build();

            assertTrue(stageInvitationStatusFilter.isApplicable(stageInvitationFilterDto));
        }

        @Test
        @DisplayName("If StageInvitationFilterDto has correctly filled status field return the filtered list")
        void whenStageInvitationFilterDtoSpecifiedStatusThenReturnList() {
            Stream<StageInvitation> stageInvitations = Stream.of(
                    StageInvitation.builder()
                            .status(INVITATION_STATUS)
                            .build(),
                    StageInvitation.builder()
                            .status(StageInvitationStatus.REJECTED)
                            .build());

            stageInvitationFilterDto = StageInvitationFilterDto.builder()
                    .status(INVITATION_STATUS)
                    .build();

            List<StageInvitation> stageInvitationsAfterFilter = List.of(
                    StageInvitation.builder()
                            .status(INVITATION_STATUS)
                            .build());

            assertEquals(stageInvitationsAfterFilter, stageInvitationStatusFilter.applyFilter(stageInvitations,
                    stageInvitationFilterDto).toList());
        }

        @Nested
        class NegativeTests {

            @Test
            @DisplayName("If StageInvitationFilterDto has status null field, return false")
            void whenStageInvitationFilterDtoStatusIsNullThenReturnFalse() {
                stageInvitationFilterDto = StageInvitationFilterDto.builder()
                        .status(null)
                        .build();

                assertFalse(stageInvitationStatusFilter.isApplicable(stageInvitationFilterDto));
            }
        }
    }
}