package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.mapper.StageInvitationMapperImpl;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StageInvitationMapperTest {
    private StageInvitation invitation;
    private StageInvitationDto invitationDto;
    private StageInvitationMapper mapper;
    @BeforeEach
    public void setUp() {
        invitation = StageInvitation.builder()
                .id(1L)
                .status(StageInvitationStatus.ACCEPTED)
                .author(TeamMember.builder().id(2L).build())
                .invited(TeamMember.builder().id(3L).build())
                .description("description")
                .stage(Stage.builder().stageId(4L).build())
                .build();
        invitationDto = StageInvitationDto.builder()
                .id(1L)
                .status(StageInvitationStatus.ACCEPTED)
                .authorId(2L)
                .invitedId(3L)
                .description("description")
                .stageId(4L)
                .build();
        mapper = new StageInvitationMapperImpl();
    }

    @Test
    public void testEntityToDto() {
        Assertions.assertEquals(invitationDto, mapper.toDto(invitation));
    }

    @Test
    public void testDtoToEntity() {
        Assertions.assertEquals(invitation, mapper.toEntity(invitationDto));
    }
}
