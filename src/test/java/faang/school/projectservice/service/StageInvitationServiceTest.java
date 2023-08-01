package faang.school.projectservice.service;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.stage_invitation.StageInvitationMapperImpl;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.stage_invitation.StageInvitationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class StageInvitationServiceTest {
    @Mock
    private StageInvitationRepository SIRepository;
    @Mock
    private StageRepository stageRepository;
    @Mock
    private TeamMemberRepository TMRepository;
    @Spy
    private StageInvitationMapperImpl mapper;
    @InjectMocks
    private StageInvitationService service;

    private StageInvitationDto validInvitationDto;
    private StageInvitationDto invalidInvitationDto;

    @Test
    public void testSuccessCreate() {
        validInvitationDto = StageInvitationDto.builder()
                .stageId(1L)
                .invitedId(2L)
                .authorId(1L)
                .build();
        Mockito.when(stageRepository.getById(validInvitationDto.getStageId())).thenReturn(Stage.builder()
                .stageId(1L)
                .executors(List.of(TeamMember.builder().id(1L).build()))
                .build());
        service.create(validInvitationDto);
        Mockito.verify(SIRepository, Mockito.times(1)).save(mapper.toModel(validInvitationDto));
    }

    @Test
    public void testCreateAuthorNotFound() {
        invalidInvitationDto = StageInvitationDto.builder()
                .stageId(1L)
                .invitedId(2L)
                .authorId(1L)
                .build();
        Mockito.when(stageRepository.getById(invalidInvitationDto.getStageId())).thenReturn(Stage.builder().stageId(1L).executors(List.of()).build());
        Assertions.assertThrows(DataValidationException.class, () -> service.create(invalidInvitationDto));
    }
}
