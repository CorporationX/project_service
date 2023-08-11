package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.mapper.stage_invitation.StageInvitationMapper;
import faang.school.projectservice.mapper.stage_invitation.StageInvitationMapperImpl;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class StageInvitationMapperTest {
    private StageInvitationMapper mapper = new StageInvitationMapperImpl();
    private StageInvitation invitation;
    private StageInvitationDto invitationDto;

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
    }

    @Test
    public void testEntityToDto() {
        Assertions.assertEquals(invitationDto, mapper.toDTO(invitation));
    }

    @Test
    public void testDtoToModel() {
        Assertions.assertEquals(invitation, mapper.toEntity(invitationDto));
    }

    @Test
    public void testEntityListToDtoList() {
        List<StageInvitation> invitationList = List.of(invitation);
        List<StageInvitationDto> invitationDtoList = List.of(invitationDto);
        Assertions.assertEquals(invitationDtoList.get(0), mapper.toDTOList(invitationList).get(0));
    }

    @Test
    public void testDtoListToModelList() {
        List<StageInvitation> invitationList = List.of(invitation);
        List<StageInvitationDto> invitationDtoList = List.of(invitationDto);
        Assertions.assertEquals(invitationList.get(0), mapper.toEntityList(invitationDtoList).get(0));
    }
}
