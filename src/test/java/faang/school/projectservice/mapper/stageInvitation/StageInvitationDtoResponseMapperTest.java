package faang.school.projectservice.mapper.stageInvitation;

import faang.school.projectservice.dto.stageInvitation.StageInvitationDtoRequest;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StageInvitationDtoResponseMapperTest {

    @Spy
    private StageInvitationDtoMapperImpl mapper;

    private final static long STAGE_ID = 1L;
    private final static long  SIZE_DTOS = 2L;
    private final static String  DESCRIPTION = "Smth";

    private final static StageInvitationStatus STAGE_INVITATION_STATUS = StageInvitationStatus.ACCEPTED;

    private StageInvitation stageInvitation;

    @BeforeEach
    void init() {
        stageInvitation = StageInvitation.builder()
                .id(STAGE_ID)
                .invited(TeamMember.builder()
                        .id(STAGE_ID)
                        .build())
                .author(TeamMember.builder()
                        .id(STAGE_ID)
                        .build())
                .stage(Stage.builder()
                        .stageId(STAGE_ID)
                        .build())
                .status(STAGE_INVITATION_STATUS)
                .description(DESCRIPTION)
                .build();
    }

    @Nested
    class toEntity {

        @Test
        @DisplayName("If passed StageInvitationDto, we get StageInvitationDto entity")
        void whenDtoIsNotNullThenReturnEntity() {
            StageInvitationDtoRequest stageInvitationDtoResponse = StageInvitationDtoRequest.builder()
                    .id(STAGE_ID)
                    .description(DESCRIPTION)
                    .status(STAGE_INVITATION_STATUS)
                    .build();

            assertEquals(stageInvitationDtoResponse.getId(), stageInvitation.getId());
            assertEquals(stageInvitationDtoResponse.getDescription(), stageInvitation.getDescription());
            assertEquals(stageInvitationDtoResponse.getStatus(), stageInvitation.getStatus());
        }
    }

    @Nested
    class toDtos {

        @Test
        @DisplayName("If passed null")
        void whenListStageInvitationIsNullThenGetNull() {
            assertNull(mapper.toDtos(null));
        }

        @Test
        @DisplayName("Check size List<StageInvitation>")
        void whenListStageInvitationIsNotNullThenReturnListStageInvitationDtos() {
            List<StageInvitation> stageInvitations = List.of(
                    stageInvitation,
                    StageInvitation.builder()
                            .id(STAGE_ID)
                            .build());

            List<StageInvitationDtoRequest> stageInvitationDtoRequests = mapper.toDtos(stageInvitations);

            assertEquals(SIZE_DTOS, stageInvitationDtoRequests.size());
        }
    }
}