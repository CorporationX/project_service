package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stageInvitation.AcceptStageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.CreateStageInvitationDto;
import faang.school.projectservice.dto.stageInvitation.RejectStageInvitationDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class InvitationMapperTest {

    @Spy
    private InvitationMapperImpl invitationMapper;

    private CreateStageInvitationDto createStageInvitationDto;
    private StageInvitation stageInvitation;
    private RejectStageInvitationDto rejectStageInvitationDto;
    private AcceptStageInvitationDto acceptStageInvitationDto;

    @BeforeEach
    public void setUp(){
        TeamMember teamMember = TeamMember.builder().id(2L).userId(1L).build();
        Stage stage = Stage.builder().stageId(1L).stageName("Name").executors(List.of(teamMember)).build();
        createStageInvitationDto = CreateStageInvitationDto.builder().id(1L).stageId(1L).authorId(1L)
                .invitedId(1L).build();

        stageInvitation = StageInvitation.builder().id(1L).description("text").status(StageInvitationStatus.REJECTED)
                .stage(stage).author(teamMember).invited(teamMember).build();

        rejectStageInvitationDto = RejectStageInvitationDto.builder().id(1L).explanation("text").build();
        acceptStageInvitationDto = AcceptStageInvitationDto.builder().id(1L).build();
    }

    @Test
    public void testCreateDtoToEntityMapper() {
        StageInvitation entity = invitationMapper.createDtoToEntity(createStageInvitationDto);

        assertThat(entity.getId()).isEqualTo(createStageInvitationDto.getId());
        assertThat(entity.getStage().getStageId()).isEqualTo(createStageInvitationDto.getStageId());
        assertThat(entity.getAuthor().getId()).isEqualTo(createStageInvitationDto.getAuthorId());
        assertThat(entity.getInvited().getId()).isEqualTo(createStageInvitationDto.getInvitedId());

    }

    @Test
    public void testEntityToCreateDtoMapper() {
        CreateStageInvitationDto dto = invitationMapper.entityToCreateDto(stageInvitation);

        assertThat(dto.getId()).isEqualTo(stageInvitation.getId());
        assertThat(dto.getStageId()).isEqualTo(stageInvitation.getStage().getStageId());
        assertThat(dto.getAuthorId()).isEqualTo(stageInvitation.getAuthor().getId());
        assertThat(dto.getInvitedId()).isEqualTo(stageInvitation.getInvited().getId());
    }

    @Test
    public void testRejectDtoToEntityMapper() {
        StageInvitation entity = invitationMapper.rejectDtoToEntity(rejectStageInvitationDto);

        assertThat(entity.getId()).isEqualTo(rejectStageInvitationDto.getId());
        assertThat(entity.getDescription()).isEqualTo(rejectStageInvitationDto.getExplanation());
    }

    @Test
    public void testEntityToRejectDtoMapper() {
        RejectStageInvitationDto dto = invitationMapper.entityToRejectDto(stageInvitation);

        assertThat(dto.getId()).isEqualTo(stageInvitation.getId());
        assertThat(dto.getExplanation()).isEqualTo(stageInvitation.getDescription());
    }

    @Test
    public void testAcceptDtoToEntityMapper() {
        StageInvitation entity = invitationMapper.acceptDtoToEntity(acceptStageInvitationDto);

        assertThat(entity.getId()).isEqualTo(acceptStageInvitationDto.getId());
    }

    @Test
    public void testEntityToAcceptDtoMapper() {
        AcceptStageInvitationDto dto = invitationMapper.entityToAcceptDto(stageInvitation);

        assertThat(dto.getId()).isEqualTo(stageInvitation.getId());
    }
}