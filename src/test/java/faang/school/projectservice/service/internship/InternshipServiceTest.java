package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.exception.internship.InternshipValidationException;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.mapper.internship.InternshipMapperImpl;
import faang.school.projectservice.mapper.internship.ProjectMapperImpl;
import faang.school.projectservice.mapper.internship.TeamMemberMapperImpl;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.service.teammember.TeamMemberService;
import faang.school.projectservice.validator.intership.InternshipValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InternshipServiceTest {

    @InjectMocks
    private InternshipService internshipService;
    @Mock
    private InternshipRepository internshipRepository;
    @Mock
    private InternshipMapperImpl internshipMapper;
    @Mock
    private InternshipValidator internshipValidator;
    @Mock
    private InternshipFilter filter;
    @Mock
    private List<InternshipFilter> internshipFilters;
    @Mock
    private TeamMemberService teamMemberService;
    private Internship internship;
    private InternshipDto internshipDto;
    private List<Internship> internships;
    private InternshipFilterDto internshipFilterDto;
    private static final Long INTERNSHIP_ID_ONE = 1L;
    private static final Long PROJECT_ID_ONE = 1L;
    private static final Long MENTOR_ID_ONE = 1L;
    private static final InternshipStatus INTERNSHIP_STATUS_IN_PROGRESS = InternshipStatus.IN_PROGRESS;
    private static final InternshipStatus INTERNSHIP_STATUS_COMPLETED = InternshipStatus.COMPLETED;
    private static final LocalDateTime VALID_START_DATE =
            LocalDateTime.of(2024, 5, 10, 10, 0);
    private static final LocalDateTime VALID_END_DATE =
            LocalDateTime.of(2024, 8, 5, 10, 0);

    private void customInternshipService() {
        internshipFilters = List.of(filter);
        internshipMapper = new InternshipMapperImpl(new TeamMemberMapperImpl(), new ProjectMapperImpl());
        internshipService = new InternshipService(internshipRepository,
                internshipMapper, internshipValidator, internshipFilters, teamMemberService);
    }

    @BeforeEach
    void setUp() {
        internshipDto = InternshipDto.builder()
                .id(INTERNSHIP_ID_ONE)
                .project(ProjectDto.builder()
                        .id(PROJECT_ID_ONE)
                        .build())
                .mentorId(TeamMemberDto.builder()
                        .id(MENTOR_ID_ONE)
                        .build())
                .interns(List.of(TeamMemberDto.builder().build(), TeamMemberDto.builder().build()))
                .startDate(VALID_START_DATE)
                .endDate(VALID_END_DATE)
                .build();

        internship = Internship.builder()
                .id(INTERNSHIP_ID_ONE)
                .project(Project.builder()
                        .id(PROJECT_ID_ONE)
                        .build())
                .mentorId(TeamMember.builder()
                        .id(MENTOR_ID_ONE)
                        .build())
                .interns(List.of(TeamMember.builder()
                                .stages(List.of(Stage.builder()
                                        .tasks(List.of(Task.builder()
                                                .status(TaskStatus.DONE)
                                                .build())).build()))
                                .build(),
                        TeamMember.builder()
                                .stages(List.of(Stage.builder()
                                        .tasks(List.of(Task.builder()
                                                .status(TaskStatus.IN_PROGRESS)
                                                .build()))
                                        .build()))

                                .build()))
                .status(INTERNSHIP_STATUS_IN_PROGRESS)
                .startDate(VALID_START_DATE)
                .endDate(VALID_END_DATE)
                .build();
        internshipFilterDto = InternshipFilterDto.builder()
                .status(InternshipStatus.COMPLETED)
                .build();

        internships = List.of(Internship.builder()
                        .status(InternshipStatus.IN_PROGRESS)
                        .build(),
                Internship.builder()
                        .status(InternshipStatus.COMPLETED)
                        .build());
    }

    @Nested
    class ServiceMethods {
        @Test
        @DisplayName("When dto pass three validations " +
                "then set progress status and interns role to inter then save and return it")
        public void whenValidDtoPassedThenSaveAndReturnIt() {
            when(internshipMapper.toDto(internship)).thenReturn(internshipDto);
            when(internshipMapper.toEntity(internshipDto)).thenReturn(internship);
            when(internshipRepository.save(internship)).thenReturn(internship);

            internshipService.create(internshipDto);

            verify(internshipValidator)
                    .validateInternship(internshipDto);
            verify(teamMemberService).getTeamMemberById(MENTOR_ID_ONE);
            verify(internshipMapper)
                    .toEntity(internshipDto);
            verify(internshipMapper)
                    .toDto(internship);
        }

        @Test
        @DisplayName("When dto is valid check it ended then set completed status," +
                "removes all inters who didn't pass and set new role to those who passed")
        public void whenValidDtoPassedUpdateItStatusAndInternsWhoPassedThenSave() {
            when(internshipValidator.validateInternshipExists(internshipDto.getId())).thenReturn(internship);
            when(internshipRepository.save(internship)).thenReturn(internship);
            when(internshipMapper.toDto(internship)).thenReturn(internshipDto);

            internshipService.update(internshipDto);

            verify(internshipValidator)
                    .validateInternshipExists(INTERNSHIP_ID_ONE);

            assertEquals(INTERNSHIP_STATUS_COMPLETED, internship.getStatus());
            assertEquals(1, internship.getInterns().size());
            assertEquals(TeamRole.DEVELOPER, internship.getInterns().get(0).getRoles().get(0));
        }

        @Test
        @DisplayName("When valid filter passed findAll internships filter it and return filtered dto list")
        public void whenValidFilterDtoPassedThenReturnFilteredDtoList() {
            customInternshipService();

            when(internshipRepository.findAll())
                    .thenReturn(internships);
            when(filter.isApplicable(internshipFilterDto))
                    .thenReturn(true);
            when(filter.applyFilter(any(), eq(internshipFilterDto)))
                    .thenReturn(internships.stream().filter(internship ->
                            internship.getStatus() == INTERNSHIP_STATUS_COMPLETED));
            List<InternshipDto> result = internshipService.getFilteredInternship(internshipFilterDto);
            assertEquals(1, result.size());
            verify(internshipRepository)
                    .findAll();
        }

        @Test
        @DisplayName("When called returns all internships list")
        public void whenRepositoryFindsAllThenMapItToDtoAndReturnList() {
            customInternshipService();
            when(internshipRepository.findAll())
                    .thenReturn(internships);
            List<InternshipDto> internshipsResult = internshipService.getAllInternship();
            verify(internshipRepository)
                    .findAll();
            assertEquals(internships.size(), internshipsResult.size());
        }

        @Test
        @DisplayName("When existing id passed the returns dto")
        public void whenValidIdPassedInternshipExistsThenReturn() {
            customInternshipService();
            when(internshipValidator.validateInternshipExists(internship.getId())).thenReturn(internship);
            internshipService.getInternship(internship.getId());
            verify(internshipValidator)
                    .validateInternshipExists(internship.getId());
        }

        @Test
        @DisplayName("When filters is null then throw exception")
        public void whenFilterDtoIsNullThenThrowException() {
            internshipService.getFilteredInternship(internshipFilterDto);
            assertThrows(InternshipValidationException.class, () ->
                    internshipService.getFilteredInternship(null));
        }

        @Test
        @DisplayName("When internshipDto is null then throw exception")
        public void whenInternshipDtoIsNullThenThrowException() {
            when(internshipMapper.toDto(internship)).thenReturn(internshipDto);
            when(internshipMapper.toEntity(internshipDto)).thenReturn(internship);
            when(internshipRepository.save(internship)).thenReturn(internship);
            internshipService.create(internshipDto);
            assertThrows(InternshipValidationException.class, () ->
                    internshipService.create(null));
        }
    }
}