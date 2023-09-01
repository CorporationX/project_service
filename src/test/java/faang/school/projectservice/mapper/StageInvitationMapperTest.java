package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stageInvitation.StageInvitationDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StageInvitationMapperTest {
    @Spy
    private StageInvitationMapper mapper = new StageInvitationMapperImpl();
    private StageInvitation invitation;
    private StageInvitationDto invitationDto;

    @BeforeEach
    public void setUp() {
        invitation = StageInvitation
                .builder()
                .id(1L)
                .description("description")
                .status(StageInvitationStatus.ACCEPTED)
                .stage(Stage.builder().stageId(2L).build())
                .author(TeamMember.builder().id(3L).build())
                .invited(TeamMember.builder().id(4L).build())
                .build();

        invitationDto = StageInvitationDto
                .builder()
                .id(1L)
                .description("description")
                .status(StageInvitationStatus.ACCEPTED)
                .stageId(2L)
                .authorId(3L)
                .invitedId(4L)
                .build();
    }

    @Test
    public void testModelToDto(){
        Assertions.assertEquals(invitationDto, mapper.toDto(invitation));
    }

    @Test
    public void testDtoToModel(){
        Assertions.assertEquals(invitation, mapper.toModel(invitationDto));
    }

    @Test
    public void testListModelToDto(){
        List<StageInvitation> invitationList = List.of(invitation);
        List<StageInvitationDto> invitationDtoList = List.of(invitationDto);
        Assertions.assertEquals(invitationDtoList.get(0), mapper.toDtoList(invitationList).get(0));
    }

    @Test
    public void testListDtoToModel(){
        List<StageInvitation> invitationList = List.of(invitation);
        List<StageInvitationDto> invitationDtoList = List.of(invitationDto);
        Assertions.assertEquals(invitationList.get(0), mapper.toModelList(invitationDtoList).get(0));
    }

    
}