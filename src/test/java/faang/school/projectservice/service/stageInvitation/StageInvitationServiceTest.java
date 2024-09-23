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
import java.util.Optional;
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
                    .thenReturn(Optional.ofNullable(stageInvitation));

            stageInvitationService.acceptInvitation(STAGE_INVITATION_ID);

            assertEquals(StageInvitationStatus.ACCEPTED, stageInvitation.getStatus());
            verify(stageInvitationRepository).save(stageInvitation);
        }

        @Test
        @DisplayName("Success with reject")
        void whenInvitationRejectedThenSuccessSave() {
            when(stageInvitationRepository.findById(STAGE_INVITATION_ID))
                    .thenReturn(Optional.ofNullable(stageInvitation));

            stageInvitationService.rejectInvitation(STAGE_INVITATION_ID, "smth");

            assertEquals("smth", stageInvitation.getDescription());
            assertEquals(StageInvitationStatus.REJECTED, stageInvitation.getStatus());
            verify(stageInvitationRepository).save(stageInvitation);
        }

        @Nested
        class GetInvitationsMethods {

            @Test
            @DisplayName("Success in the filter")
            void whenGetStageInvitationFilterThenSuccess() {

                var stageInvitationFilterDto = new StageInvitationFilterDto();
                var firstDto = new StageInvitationDto();
                firstDto.setDescription("Smth");
                firstDto.setStatus(StageInvitationStatus.ACCEPTED);

                var secondDto = new StageInvitationDto();
                secondDto.setDescription(STAGE_INVITATION_DESCRIPTION);
                secondDto.setStatus(StageInvitationStatus.ACCEPTED);

                var first = new StageInvitation();
                first.setId(STAGE_INVITATION_ID);

                var stage = new Stage();
                first.setStage(stage);

                var author = new TeamMember();
                first.setAuthor(author);
                first.setInvited(author);
                first.setDescription(STAGE_INVITATION_DESCRIPTION);
                first.setStatus(StageInvitationStatus.ACCEPTED);

                var second = new StageInvitation();
                second.setDescription(STAGE_INVITATION_DESCRIPTION);
                second.setStatus(StageInvitationStatus.ACCEPTED);

                when(stageInvitationRepository.findAll()).thenReturn(List.of(first, second));
                when(stageInvitationDtoMapper.toDto(first)).thenReturn(firstDto);
                when(filters.isApplicable(stageInvitationFilterDto)).thenReturn(true);
                when(filters.apply(any(Stream.class),
                        eq(stageInvitationFilterDto))).thenReturn(Stream.of(first, second));
                when(stageInvitationFilters.stream()).thenReturn(Stream.of(filters));

                List<StageInvitationDto> result = stageInvitationService.getInvitations(stageInvitationFilterDto);

                assertEquals(2L, result.size());
                assertEquals("Smth",firstDto.getDescription());
                assertEquals(StageInvitationStatus.ACCEPTED, firstDto.getStatus());
                assertEquals(1L, first.getId());
                assertEquals(StageInvitationStatus.ACCEPTED, first.getStatus());

                assertEquals(STAGE_INVITATION_DESCRIPTION, second.getDescription());
                assertEquals("something", secondDto.getDescription());
                verify(stageInvitationRepository).findAll();
                verify(stageInvitationDtoMapper).toDto(first);
            }
        }
    }

}