package faang.school.projectservice.mapper.stageInvitation;

import faang.school.projectservice.dto.client.stageInvitation.StageInvitationDto;

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
class StageInvitationDtoMapperTest {

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
        @DisplayName("If send null")
        void whenDtoIsNullThenReturnNull() {
            assertNull(mapper.toEntity(null));
        }

        @Test
        @DisplayName("If passed StageInvitationDto, we get StageInvitationDto entity")
        void whenDtoIsNotNullThenReturnEntity() {
            StageInvitationDto stageInvitationDto = StageInvitationDto.builder()
                    .id(STAGE_ID)
                    .invitedId(STAGE_ID)
                    .stageId(STAGE_ID)
                    .description(DESCRIPTION)
                    .status(STAGE_INVITATION_STATUS)
                    .build();

            assertEquals(stageInvitationDto.getId(), stageInvitation.getId());
            assertEquals(stageInvitationDto.getInvitedId(), stageInvitation.getInvited().getId());
            assertEquals(stageInvitationDto.getDescription(), stageInvitation.getDescription());
            assertEquals(stageInvitationDto.getStatus(), stageInvitation.getStatus());
            assertEquals(stageInvitationDto.getStageId(), stageInvitation.getStage().getStageId());
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

            List<StageInvitationDto> stageInvitationDtos = mapper.toDtos(stageInvitations);

            assertEquals(SIZE_DTOS, stageInvitationDtos.size());
        }
    }
}