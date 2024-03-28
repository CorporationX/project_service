package faang.school.projectservice.stageinviation;

import faang.school.projectservice.dto.StageInvitationDto;
import faang.school.projectservice.dto.StageInvitationFilterDto;
import faang.school.projectservice.mapper.StageInvitationMapper;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.service.stage.filters.StageInvitationFilter;
import faang.school.projectservice.service.stage.impl.StageInvitationServiceImpl;
import faang.school.projectservice.validator.StageValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceImplTests {
    @Mock
    private StageInvitationRepository stageInvitationRepository;

    @Mock
    private StageInvitationMapper stageInvitationMapper;

    @Mock
    private StageValidator stageValidator;

    @InjectMocks
    private StageInvitationServiceImpl service;

    @Mock
    private StageInvitationFilter stageInvitationFilter;

    @Mock
    private List<StageInvitationFilter> stageInvitationFilters = new ArrayList<>();

    @Test
    void testCreateInvitation() {
        StageInvitationDto dto = new StageInvitationDto();
        dto.setAuthorId(1L);
        dto.setInvitedId(2L);
        dto.setStageId(3L);

        StageInvitation invitation = new StageInvitation();
        invitation.setId(1L);

        when(stageInvitationMapper.toEntity(any(StageInvitationDto.class))).thenReturn(invitation);
        when(stageInvitationRepository.save(any(StageInvitation.class))).thenReturn(invitation);
        when(stageInvitationMapper.toDto(any(StageInvitation.class))).thenReturn(dto);

        StageInvitationDto result = service.create(dto);

        assertNotNull(result);
        verify(stageInvitationRepository).save(invitation);
        verify(stageInvitationMapper).toDto(invitation);
    }

    @Test
    void testGetStageInvitations() {
        List<StageInvitation> invitations = Arrays.asList(new StageInvitation());
        when(stageInvitationRepository.findAll()).thenReturn(invitations);

        List<StageInvitationDto> dtos = service.getStageInvitations(1L, new StageInvitationFilterDto());

        assertNotNull(dtos);
        verify(stageInvitationRepository).findAll();
    }

    @Test
    void testAcceptInvitation() {
        Long invitationId = 1L;
        StageInvitation invitation = new StageInvitation();
        invitation.setId(invitationId);
        invitation.setStatus(StageInvitationStatus.PENDING);

        StageInvitationDto dto = new StageInvitationDto();
        dto.setId(invitationId);
        dto.setStatus(StageInvitationStatus.ACCEPTED);

        when(stageInvitationRepository.findById(invitationId)).thenReturn(invitation);
        when(stageInvitationMapper.toDto(any(StageInvitation.class))).thenReturn(dto);

        StageInvitationDto result = service.accept(dto);

        assertEquals(StageInvitationStatus.ACCEPTED, result.getStatus());
        verify(stageInvitationRepository).save(invitation);
    }

    @Test
    void testRejectInvitation() {
        StageInvitation invitation = new StageInvitation();
        invitation.setId(1L);
        invitation.setStatus(StageInvitationStatus.PENDING);

        StageInvitationDto dto = new StageInvitationDto();
        dto.setId(1L);
        dto.setDescription("Busy");
        dto.setStatus(StageInvitationStatus.REJECTED);

        when(stageInvitationRepository.findById(anyLong())).thenReturn(invitation);
        when(stageInvitationMapper.toDto(any(StageInvitation.class))).thenReturn(dto);

        StageInvitationDto result = service.reject(dto);

        assertEquals(StageInvitationStatus.REJECTED, result.getStatus());
        assertEquals("Busy", result.getDescription());
        verify(stageInvitationRepository).save(invitation);
    }
}
