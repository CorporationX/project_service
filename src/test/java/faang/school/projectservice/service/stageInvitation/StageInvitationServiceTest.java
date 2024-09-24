package faang.school.projectservice.service.stageInvitation;

import faang.school.projectservice.dto.client.stageInvitation.StageInvitationDto;
import faang.school.projectservice.dto.client.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.stageInvitation.StageInvitationDtoMapper;
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
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StageInvitationServiceTest {

    @InjectMocks
    private StageInvitationService stageInvitationService;

    @Mock
    private StageInvitationRepository stageInvitationRepository;

    @Mock
    private Filter<StageInvitationFilterDto, StageInvitation> filters;

    @Mock
    private List<Filter<StageInvitationFilterDto, StageInvitation>> stageInvitationFilters;

    @Mock
    private StageInvitationDtoMapper stageInvitationDtoMapper;

    @Mock
    private StageInvitation stageInvitation;

    private static final Long STAGE_INVITATION_ID = 1L;
    private static final String STAGE_INVITATION_DESCRIPTION = "something";

    private StageInvitationDto stageInvitationDto;

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
                    .build();

            stageInvitation = StageInvitation.builder()
                    .id(STAGE_INVITATION_ID)
                    .description(STAGE_INVITATION_DESCRIPTION)
                    .status(StageInvitationStatus.PENDING)
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
        @DisplayName("Success at create")
        void whenInvitationThenSuccessSave() {
            when(stageInvitationDtoMapper.toEntity(stageInvitationDto))
                    .thenReturn(stageInvitation);

            stageInvitationService.createInvitation(stageInvitationDto);

            assertEquals(StageInvitationStatus.PENDING, stageInvitationDto.getStatus());
            verify(stageInvitationDtoMapper).toEntity(any());
            verify(stageInvitationRepository).save(stageInvitation);
        }

        @Test
        @DisplayName("Success with accept")
        void whenInvitationAcceptThenSuccessSave() {
            when(stageInvitationRepository.findById(STAGE_INVITATION_ID))
                    .thenReturn(stageInvitation);

            stageInvitationService.acceptInvitation(STAGE_INVITATION_ID);

            assertEquals(StageInvitationStatus.ACCEPTED, stageInvitation.getStatus());
            verify(stageInvitationRepository).save(stageInvitation);
        }

        @Test
        @DisplayName("Success with reject")
        void whenInvitationRejectedThenSuccessSave() {
            when(stageInvitationDtoMapper.toEntity(stageInvitationDto))
                    .thenReturn(stageInvitation);

            stageInvitationDto.setDescription("smth");
            stageInvitationDto.setStatus(StageInvitationStatus.REJECTED);
            stageInvitationService.rejectInvitation(stageInvitationDto);


            assertEquals("smth", stageInvitationDto.getDescription());
            assertEquals(StageInvitationStatus.REJECTED, stageInvitationDto.getStatus());
            verify(stageInvitationDtoMapper).toEntity(any());
            verify(stageInvitationRepository).save(stageInvitation);
        }

        @Nested
        class GetInvitationsMethods {

            @Test
            @DisplayName("Success in the filter")
            void whenGetStageInvitationFilterThenSuccess() {

                var stageInvitationFilterDto = new StageInvitationFilterDto();
                var firstDto = new StageInvitationDto();
                firstDto.setId(STAGE_INVITATION_ID);
                firstDto.setStageId(STAGE_INVITATION_ID);
                firstDto.setInvitedId(STAGE_INVITATION_ID);
                firstDto.setDescription("Smth");
                firstDto.setStatus(StageInvitationStatus.ACCEPTED);

                var first = new StageInvitation();
                first.setId(STAGE_INVITATION_ID);

                var stage = new Stage();
                first.setStage(stage);

                var author = new TeamMember();
                first.setAuthor(author);
                first.setInvited(author);
                first.setDescription(STAGE_INVITATION_DESCRIPTION);
                first.setStatus(StageInvitationStatus.ACCEPTED);

                when(stageInvitationRepository.findAll()).thenReturn(List.of(first));
                when(filters.isApplicable(stageInvitationFilterDto)).thenReturn(true);
                when(filters.apply(any(Stream.class),
                        eq(stageInvitationFilterDto))).thenReturn(Stream.of(first));
                when(stageInvitationFilters.stream()).thenReturn(Stream.of(filters));
                when(stageInvitationDtoMapper.toDto(any(StageInvitation.class))).thenReturn(firstDto);

                List<StageInvitationDto> result = stageInvitationService.getInvitations(stageInvitationFilterDto);

                assertEquals(1L, result.size());
                assertEquals(firstDto, result.get(0));
                assertEquals("Smth",firstDto.getDescription());
                assertEquals(StageInvitationStatus.ACCEPTED, firstDto.getStatus());
                assertEquals(1L, first.getId());
                assertEquals(StageInvitationStatus.ACCEPTED, first.getStatus());
            }
        }
    }
}