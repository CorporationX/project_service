package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageInvitationDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class StageInvitationMapperTest {

    private final StageInvitationMapper mapper = Mappers.getMapper(StageInvitationMapper.class);

    @Test
    void toDTO() {
        Stage stage = new Stage();
        stage.setStageId(1L);

        TeamMember teamMemberAuthor = new TeamMember();
        teamMemberAuthor.setId(2L);

        TeamMember teamMemberInvited = new TeamMember();
        teamMemberInvited.setId(3L);

        StageInvitation stageInvitation = StageInvitation.builder()
                .id(4L)
                .description("Test Description")
                .status(StageInvitationStatus.REJECTED)
                .stage(stage)
                .author(teamMemberAuthor)
                .invited(teamMemberInvited)
                .build();

        StageInvitationDto dto = mapper.toDto(stageInvitation);

        Assertions.assertNotNull(dto);
        Assertions.assertEquals(dto.getId(), 4L);
        Assertions.assertEquals(dto.getDescription(), "Test Description");
        Assertions.assertEquals(dto.getStatus(), StageInvitationStatus.REJECTED);
        Assertions.assertEquals(dto.getStageId(), 1L);
        Assertions.assertEquals(dto.getAuthorId(), 2L);
        Assertions.assertEquals(dto.getInvitedId(), 3L);

    }

    @Test
    void ToEntity() {
        StageInvitationDto dto = StageInvitationDto.builder()
                .id(4L)
                .description("Test Description")
                .status(StageInvitationStatus.PENDING)
                .stageId(1L)
                .authorId(2L)
                .invitedId(3L)
                .build();

        StageInvitation entity = mapper.toEntity(dto);

        Assertions.assertNotNull(entity);
        Assertions.assertEquals(entity.getId(), 4L);
        Assertions.assertEquals(entity.getDescription(), "Test Description");
        Assertions.assertEquals(entity.getStatus(), StageInvitationStatus.PENDING);
        Assertions.assertEquals(entity.getStage().getStageId(), 1L);
        Assertions.assertEquals(entity.getAuthor().getId(), 2L);
        Assertions.assertEquals(entity.getInvited().getId(), 3L);

    }


}