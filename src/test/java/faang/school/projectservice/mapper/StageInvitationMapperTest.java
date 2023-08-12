package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.invitation.StageInvitationDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StageInvitationMapperTest {
    @Spy
    private StageInvitationMapperImpl stageInvitationMapper;
    private StageInvitationDto dto;
    private StageInvitation entity;

    @BeforeEach
    void setUp() {
        TeamMember invited = TeamMember.builder()
                .id(1L)
                .userId(1L)
                .build();

        Stage stage = Stage.builder()
                .stageId(1L)
                .build();

        TeamMember author = TeamMember.builder()
                .id(1L)
                .userId(1L)
                .roles(List.of(TeamRole.OWNER))
                .build();

        dto = StageInvitationDto.builder()
                .id(5L)
                .stageId(1L)
                .invitedId(invited.getId())
                .status("PENDING")
                .authorId(1L)
                .build();

        entity = StageInvitation.builder()
                .id(5L)
                .stage(stage)
                .invited(invited)
                .status(StageInvitationStatus.PENDING)
                .author(author)
                .build();
    }

    @Test
    void testToDto() {
        StageInvitationDto resultDto = stageInvitationMapper.toDto(entity);

        assertNotNull(resultDto);
        assertEquals(dto.getId(), resultDto.getId());
        assertEquals(dto.getStageId(), resultDto.getStageId());
        assertEquals(dto.getInvitedId(), resultDto.getInvitedId());
        assertEquals(dto.getStatus(), resultDto.getStatus());
    }

    @Test
    void testToEntity() {
        StageInvitation resultEntity = stageInvitationMapper.toEntity(dto);

        assertNotNull(resultEntity);
        assertEquals(entity.getId(), resultEntity.getId());
        assertEquals(entity.getStage().getStageId(), resultEntity.getStage().getStageId());
        assertEquals(entity.getInvited().getId(), resultEntity.getInvited().getId());
        assertEquals(entity.getStatus(), resultEntity.getStatus());
    }

    @Test
    void testListToDto() {
        List<StageInvitation> entities = new ArrayList<>();
        entities.add(entity);

        List<StageInvitationDto> resultDtoList = stageInvitationMapper.listToDto(entities);

        assertNotNull(resultDtoList);
        assertEquals(1, resultDtoList.size());

        StageInvitationDto resultDto = resultDtoList.get(0);
        assertEquals(dto.getId(), resultDto.getId());
        assertEquals(dto.getStageId(), resultDto.getStageId());
        assertEquals(dto.getInvitedId(), resultDto.getInvitedId());
        assertEquals(dto.getStatus(), resultDto.getStatus());
    }

    @Test
    void testListToEntity() {
        List<StageInvitationDto> dtos = new ArrayList<>();
        dtos.add(dto);

        List<StageInvitation> resultEntityList = stageInvitationMapper.listToEntity(dtos);

        assertNotNull(resultEntityList);
        assertEquals(1, resultEntityList.size());

        StageInvitation resultEntity = resultEntityList.get(0);
        assertEquals(entity.getId(), resultEntity.getId());
        assertEquals(entity.getStage().getStageId(), resultEntity.getStage().getStageId());
        assertEquals(entity.getInvited().getId(), resultEntity.getInvited().getId());
        assertEquals(entity.getStatus(), resultEntity.getStatus());
    }

    @Test
    public void testUpdateDto() {

        StageInvitationDto updatedDto = StageInvitationDto.builder()
                .id(10L)
                .stageId(1L)
                .invitedId(1L)
                .authorId(1L)
                .status("ACCEPTED")
                .build();

        stageInvitationMapper.updateDto(updatedDto, entity);

        assertEquals(updatedDto.getId(), entity.getId());
        assertEquals(updatedDto.getStageId(), entity.getStage().getStageId());
        assertEquals(updatedDto.getInvitedId(), entity.getInvited().getId());
        assertEquals(updatedDto.getStatus(), entity.getStatus().toString());
    }
}