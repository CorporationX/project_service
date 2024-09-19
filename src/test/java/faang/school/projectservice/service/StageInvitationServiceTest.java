package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.stage.StageInvitationDto;
import faang.school.projectservice.dto.client.stage.StageInvitationFilterDto;
import faang.school.projectservice.filter.StageInvitation.StageInvitationFilter;
import faang.school.projectservice.mapper.StageInvitationDtoMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StageInvitationServiceTest {

    @InjectMocks
    private StageInvitationService stageInvitationService;

    @Mock
    private StageInvitationRepository stageInvitationRepository;

    @Mock
    private StageInvitationFilter stageInvitationFilter;

    @Mock
    private StageInvitationDtoMapper stageInvitationDtoMapper;

    private static final Long STAGE_INVITATION_ID = 1L;
    private static final String STAGE_INVITATION_DESCRIPTION = "Smth";
    private static final StageInvitationStatus STAGE_INVITATION_STATUS = StageInvitationStatus.ACCEPTED;

    @Mock
    private StageInvitation stageInvitation;
    private StageInvitationDto stageInvitationDto;
    private StageInvitationFilterDto stageInvitationFilterDto;
    private List<StageInvitationFilter> stageInvitationFilterList;

    @Nested
    class PositiveTests {

        @BeforeEach
        void init() {
            List<TeamMember> members = new ArrayList<>();
            members.add(TeamMember.builder()
                    .id(STAGE_INVITATION_ID)
                    .build());

            stageInvitationDto = StageInvitationDto.builder()
                    .stageId(STAGE_INVITATION_ID)
                    .description(STAGE_INVITATION_DESCRIPTION)
                    .status(STAGE_INVITATION_STATUS)
                    .build();

            stageInvitation = StageInvitation.builder()
                    .id(STAGE_INVITATION_ID)
                    .description(STAGE_INVITATION_DESCRIPTION)
                    .status(STAGE_INVITATION_STATUS)
                    .author(TeamMember.builder()
                            .id(STAGE_INVITATION_ID)
                            .build())
                    .stage(Stage.builder()
                            .stageId(STAGE_INVITATION_ID)
                            .executors(members)
                            .build())
                    .invited(TeamMember.builder()
                            .id(STAGE_INVITATION_ID)
                            .build())
                    .build();
        }

        @Test
        @DisplayName("Успех при сохранении")
        void whenInvitationThenSuccessSave() {
            when(stageInvitationDtoMapper.toEntity(stageInvitationDto))
                    .thenReturn(stageInvitation);

            stageInvitationService.createInvitation(stageInvitationDto);

            verify(stageInvitationDtoMapper).toEntity(any());
            verify(stageInvitationRepository).save(stageInvitation);
        }

        @Test
        @DisplayName("Успех при приглашении")
        void whenInvitationAcceptThenSuccessSave() {
            when(stageInvitationRepository.findById(STAGE_INVITATION_ID))
                    .thenReturn(stageInvitation);

            stageInvitationService.acceptInvitation(STAGE_INVITATION_ID);

            verify(stageInvitationRepository).save(stageInvitation);
        }

        @Test
        @DisplayName("Успех при отклонении приглашения")
        void whenInvitationRejectedThenSuccessSave() {
            when(stageInvitationDtoMapper.toEntity(stageInvitationDto))
                    .thenReturn(stageInvitation);

            stageInvitationService.rejectInvitation(stageInvitationDto);

            verify(stageInvitationDtoMapper).toEntity(any());
            verify(stageInvitationRepository).save(stageInvitation);
        }

        @Nested
        class GetInvitationsMethods {

            private List<StageInvitation> stageInvitations;

            @BeforeEach
            void init() {
                List<StageInvitation> invitations = new ArrayList<>();
                invitations.add(StageInvitation.builder()
                        .id(STAGE_INVITATION_ID)
                        .build());

                stageInvitations = List.of(StageInvitation.builder()
                        .id(STAGE_INVITATION_ID)
                        .invited(TeamMember.builder().id(STAGE_INVITATION_ID).build())
                        .status(STAGE_INVITATION_STATUS)
                        .description(STAGE_INVITATION_DESCRIPTION)
                        .stage(Stage.builder()
                                .stageId(STAGE_INVITATION_ID)
                                .stageName(STAGE_INVITATION_DESCRIPTION)
                                .project(Project.builder().id(STAGE_INVITATION_ID).build())
                                .stageName(STAGE_INVITATION_DESCRIPTION)

                                .build())
                        .author(TeamMember.builder().id(STAGE_INVITATION_ID).build())
                        .build());

                stageInvitationFilterDto = StageInvitationFilterDto.builder()
                        .invitedId(STAGE_INVITATION_ID)
                        .build();

                stageInvitationFilterList = List.of(stageInvitationFilter);
                stageInvitationService = new StageInvitationService(stageInvitationRepository,
                        stageInvitationDtoMapper,
                        stageInvitationFilterList);
            }

            @Test
            @DisplayName("Успех если не null в фильтре")
            void whenFilterIsNotNullThenSuccess() {
                when(stageInvitationRepository.findAll())
                        .thenReturn(stageInvitations);
                when(stageInvitationFilterList.get(0).isApplicable(stageInvitationFilterDto))
                        .thenReturn(true);
                when(stageInvitationFilterList.get(0).apply(any(), eq(stageInvitationFilterDto)))
                        .thenReturn(stageInvitations.stream().filter(stageInvitation ->
                                stageInvitation.getStage().getStageName()
                                        .equals(stageInvitationFilterDto.getInvitedStageName())));

                stageInvitationService.getInvitations(stageInvitationFilterDto);

                verify(stageInvitationRepository).findAll();
                verify(stageInvitationDtoMapper).toDtos((anyList()));
            }

            @Test
            @DisplayName("Успех если null в фильтре")
            void whenFilterIsNullThenSuccess() {
                when(stageInvitationRepository.findAll())
                        .thenReturn(stageInvitations);

                stageInvitationService.getInvitations(null);

                verify(stageInvitationRepository).findAll();
                verify(stageInvitationDtoMapper).toDtos(stageInvitations);
            }
        }
    }

    @Nested
    class NegativeTests {
        @Test
        @DisplayName("Ошибка если передали null при приглашении")
        void whenStageInvitationIdIsNullAcceptThenTrowNullPointerException() {
            when(stageInvitationRepository.findById(STAGE_INVITATION_ID))
                    .thenReturn(stageInvitation);

            assertThrows(NullPointerException.class,
                    () -> stageInvitationService.acceptInvitation(STAGE_INVITATION_ID),
                    "Stage invitation is null id: %s" + STAGE_INVITATION_ID);
        }
    }
}