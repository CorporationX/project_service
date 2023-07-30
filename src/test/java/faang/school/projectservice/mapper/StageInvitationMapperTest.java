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
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StageInvitationMapperTest {
    private final StageInvitationMapper stageInvitationMapper = StageInvitationMapper.INSTANCE;

    private StageInvitationDto dto;
    private StageInvitation entity;

    @BeforeEach
    void setUp() {
        TeamMember invited = TeamMember.builder()
                .userId(1L)
                .build();

        Stage stage = Stage.builder()
                .stageName("Stage 1")
                .build();

        TeamMember author = TeamMember.builder()
                .userId(1L)
                .roles(List.of(TeamRole.OWNER))
                .build();

        StageInvitation stageInvitation = StageInvitation.builder()
                .author(author)
                .build();

        dto = StageInvitationDto.builder()
                .id(5L)
                .stage(stage)
                .invited(invited)
                .status(StageInvitationStatus.PENDING)
                .build();

        entity = StageInvitation.builder()
                .id(5L)
                .stage(stage)
                .invited(invited)
                .status(StageInvitationStatus.PENDING)
                .build();
    }

    @Test
    void testToDto() {
        StageInvitationDto resultDto = stageInvitationMapper.toDto(entity);

        assertNotNull(resultDto);
        assertEquals(dto.getId(), resultDto.getId());
        assertEquals(dto.getStage(), resultDto.getStage());
        assertEquals(dto.getInvited(), resultDto.getInvited());
        assertEquals(dto.getStatus(), resultDto.getStatus());
    }

    @Test
    void testToEntity() {
        StageInvitation resultEntity = stageInvitationMapper.toEntity(dto);

        assertNotNull(resultEntity);
        assertEquals(entity.getId(), resultEntity.getId());
        assertEquals(entity.getStage(), resultEntity.getStage());
        assertEquals(entity.getInvited(), resultEntity.getInvited());
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
        assertEquals(dto.getStage(), resultDto.getStage());
        assertEquals(dto.getInvited(), resultDto.getInvited());
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
        assertEquals(entity.getStage(), resultEntity.getStage());
        assertEquals(entity.getInvited(), resultEntity.getInvited());
        assertEquals(entity.getStatus(), resultEntity.getStatus());
    }

    @Test
    public void testUpdateDto() {
        TeamMember newInvited = TeamMember.builder()
                .userId(2L)
                .build();

        TeamMember newAuthor = TeamMember.builder()
                .userId(2L)
                .build();

        Stage newStage = Stage.builder()
                .stageName("Stage 2")
                .build();

        StageInvitationDto updatedDto = StageInvitationDto.builder()
                .id(10L)
                .stage(newStage)
                .invited(newInvited)
                .author(newAuthor)
                .status(StageInvitationStatus.ACCEPTED)
                .build();

        stageInvitationMapper.updateDto(updatedDto, entity);

        assertEquals(updatedDto.getId(), entity.getId());
        assertEquals(updatedDto.getStage(), entity.getStage());
        assertEquals(updatedDto.getInvited(), entity.getInvited());
        assertEquals(updatedDto.getStatus(), entity.getStatus());
    }
}