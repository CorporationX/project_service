package faang.school.projectservice.service;

import faang.school.projectservice.dto.InternshipDto;
import faang.school.projectservice.exceptions.DataValidationInternshipException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.filter.InternshipFilter;
import faang.school.projectservice.validation.InternshipValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternshipServiceTest {

    private static final String INTERN_NAME = "Internship";
    private static final String INTERN_DESC = "Description";
    private static final Long PROJECT_ID = 1L;
    private static final Long MENTOR_ID = 1L;
    private static final Long INTERNSHIP_ID = 1L;
    private static final Long INTERN_ID = 2L;
    private static final LocalDateTime INTERN_START = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
    private static final LocalDateTime INTERN_END = LocalDateTime.of(2024, 4, 1, 0, 0, 0);
    private static final Long CREATED_BY = 1L;
    private static final Long NOT_FOUND_CANDIDATE_ID = 99L;
    private static final String NO_CANDIDATES_MSG = "Intern's list is empty";
    private static final String NOT_FOUND_CANDIDATE = "Candidate doesn't exist by id: ";
    private static final String NOT_FOUND_MEMBER = "Team member doesn't exist by id: ";
    private static final String NOT_FOUND_PROJECT = "Project not found by id: ";

    @Value("${internship.period}")
    private long INTERNSHIP_PERIOD;

    InternshipDto newInternshipDto;
    InternshipDto savedInternshipDto;
    Internship internship = new Internship();
    Candidate candidate;
    TeamMember mentor;
    TeamMember newIntern;
    TeamMember savedIntern;
    Project project;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private TeamMemberJpaRepository teamMemberJpaRepository;

    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private InternshipMapper internshipMapper;

    @Mock
    private InternshipValidator internshipValidator;

    @Mock
    private List<InternshipFilter> internshipFilters;

    @InjectMocks
    private InternshipService internshipService;

    @BeforeEach
    void init() {
        List<Long> CANDIDATE_LIST = new ArrayList<>();
        CANDIDATE_LIST.add(INTERN_ID);
        newInternshipDto = InternshipDto.builder()
                .name(INTERN_NAME)
                .description(INTERN_DESC)
                .projectId(PROJECT_ID)
                .mentorId(MENTOR_ID)
                .startDate(INTERN_START)
                .endDate(INTERN_END)
                .createdBy(CREATED_BY)
                .candidateIds(CANDIDATE_LIST)
                .build();
        savedInternshipDto = InternshipDto.builder()
                .id(INTERNSHIP_ID)
                .name(INTERN_NAME)
                .description(INTERN_DESC)
                .projectId(PROJECT_ID)
                .mentorId(MENTOR_ID)
                .startDate(INTERN_START)
                .endDate(INTERN_END)
                .createdBy(CREATED_BY)
                .candidateIds(CANDIDATE_LIST)
                .build();

        candidate = new Candidate();
        candidate.setId(INTERN_ID);
        mentor = TeamMember.builder()
                .id(MENTOR_ID).build();
        project = Project.builder()
                .id(PROJECT_ID)
                .tasks(new ArrayList<>())
                .build();
        newIntern = TeamMember.builder()
                .roles(Arrays.asList(TeamRole.INTERN))
                .build();

        List<TeamRole> internRoles = new ArrayList<>();
        internRoles.add(TeamRole.INTERN);
        savedIntern = TeamMember.builder()
                .id(INTERN_ID)
                .userId(INTERN_ID)
                .roles(internRoles)
                .build();

        internship.setId(INTERNSHIP_ID);
        internship.setName(INTERN_NAME);
        internship.setProject(project);
        internship.setMentorId(mentor);
        internship.setStartDate(INTERN_START);
        internship.setEndDate(INTERN_END);
        internship.setCreatedBy(CREATED_BY);
    }

    @Test
    public void createIfInternshipAlreadyExists() {
        String errMessage = "Internship " + INTERN_NAME + " already exists!";

        doThrow(new DataValidationInternshipException(errMessage))
                .when(internshipValidator).validateInternshipExistsByName(INTERN_NAME);

        DataValidationInternshipException exception = assertThrows(DataValidationInternshipException.class, () ->
                internshipService.create(newInternshipDto));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    public void createWhenCandidateNotFound() {
        newInternshipDto.setCandidateIds(List.of(NOT_FOUND_CANDIDATE_ID));

        doThrow(new EntityNotFoundException(NOT_FOUND_CANDIDATE + NOT_FOUND_CANDIDATE_ID))
                .when(candidateRepository).findById(NOT_FOUND_CANDIDATE_ID);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                internshipService.create(newInternshipDto));
        assertThat(exception.getMessage()).isEqualTo(NOT_FOUND_CANDIDATE + NOT_FOUND_CANDIDATE_ID);
    }

    @Test
    public void createWhenCandidateListIsEmpty() {
        newInternshipDto.setCandidateIds(Collections.emptyList());

        doThrow(new DataValidationInternshipException(NO_CANDIDATES_MSG))
                .when(internshipValidator).validateCandidatesList(newInternshipDto.getCandidateIds().size());

        DataValidationInternshipException exception = assertThrows(DataValidationInternshipException.class, () ->
                internshipService.create(newInternshipDto));
        assertThat(exception.getMessage()).isEqualTo(NO_CANDIDATES_MSG);
    }

    @Test
    public void createWhenMentorNotFound() {
        when(candidateRepository.findById(2L)).thenReturn(Optional.of(candidate));
        doThrow(new EntityNotFoundException(NOT_FOUND_MEMBER + MENTOR_ID))
                .when(teamMemberRepository).findById(MENTOR_ID);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                internshipService.create(newInternshipDto));
        assertThat(exception.getMessage()).isEqualTo(NOT_FOUND_MEMBER + MENTOR_ID);
    }

    @Test
    public void createWhenProjectNotFound() {
        when(candidateRepository.findById(2L)).thenReturn(Optional.of(candidate));
        when(teamMemberRepository.findById(MENTOR_ID)).thenReturn(mentor);

        doThrow(new EntityNotFoundException(NOT_FOUND_PROJECT + PROJECT_ID))
                .when(projectRepository).getProjectById(PROJECT_ID);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                internshipService.create(newInternshipDto));
        assertThat(exception.getMessage()).isEqualTo(NOT_FOUND_PROJECT + PROJECT_ID);
    }

    @Test
    public void createWhenMentorNotInProjectTeam() {
        String errMessage = "Mentor with ID: " + MENTOR_ID + " isn't from project team";

        when(candidateRepository.findById(2L)).thenReturn(Optional.of(candidate));
        when(teamMemberRepository.findById(MENTOR_ID)).thenReturn(mentor);
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);
        doThrow(new EntityNotFoundException(errMessage))
                .when(internshipValidator).validateMentorInTeamProject(mentor, project);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                internshipService.create(newInternshipDto));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    public void createWhenInternGoOnMoreThanPeriod() {
        String errMessage = "The internship cannot last more than " + INTERNSHIP_PERIOD + " months";

        when(candidateRepository.findById(2L)).thenReturn(Optional.of(candidate));
        when(teamMemberRepository.findById(MENTOR_ID)).thenReturn(mentor);
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);
        doThrow(new DataValidationInternshipException(errMessage))
                .when(internshipValidator).validateInternshipPeriod(newInternshipDto);


        DataValidationInternshipException exception = assertThrows(DataValidationInternshipException.class, () ->
                internshipService.create(newInternshipDto));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    public void createInternshipWhenSuccess() {

        when(candidateRepository.findById(2L)).thenReturn(Optional.of(candidate));
        when(teamMemberRepository.findById(MENTOR_ID)).thenReturn(mentor);
        when(projectRepository.getProjectById(PROJECT_ID)).thenReturn(project);
        when(internshipMapper.toEntity(newInternshipDto)).thenReturn(internship);
        when(internshipRepository.save(internship)).thenReturn(internship);
        when(internshipMapper.toDto(internship)).thenReturn(savedInternshipDto);
        when(teamMemberJpaRepository.save(newIntern)).thenReturn(savedIntern);

        InternshipDto actualResult = internshipService.create(newInternshipDto);
        assertEquals(savedInternshipDto, actualResult);
    }

    @Test
    public void updateWhenInternshipNotFound() {
        String errMessage = "Internship doesn't exist by id: " + INTERNSHIP_ID;
        doThrow(new EntityNotFoundException(errMessage))
                .when(internshipRepository).findById(INTERNSHIP_ID);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                internshipService.update(savedInternshipDto, INTERNSHIP_ID));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    public void updateWhenInternshipCompleted() {
        Task task = Task.builder().build();
        List<TeamMember> interns = new ArrayList<>();
        interns.add(savedIntern);
        internship.setInterns(interns);
        internship.getProject().getTasks().add(task);

        InternshipDto updatedInternshipDto = savedInternshipDto;
        updatedInternshipDto.setStatus(InternshipStatus.COMPLETED);

        when(internshipRepository.findById(INTERNSHIP_ID)).thenReturn(Optional.of(internship));
        when(internshipRepository.save(internship)).thenReturn(internship);
        when(internshipMapper.toDto(internship)).thenReturn(updatedInternshipDto);

        InternshipDto actualResult = internshipService.update(savedInternshipDto, INTERNSHIP_ID);

        assertEquals(updatedInternshipDto, actualResult);
    }

    @Test
    public void updateWhenInternshipCompletedAndInternCompleteTask() {
        Task task = Task.builder().performerUserId(INTERN_ID).status(TaskStatus.DONE).build();
        List<TeamMember> interns = new ArrayList<>();
        interns.add(savedIntern);
        internship.setInterns(interns);
        internship.getProject().getTasks().add(task);

        InternshipDto updatedInternshipDto = savedInternshipDto;
        updatedInternshipDto.setStatus(InternshipStatus.COMPLETED);

        when(internshipRepository.findById(INTERNSHIP_ID)).thenReturn(Optional.of(internship));
        when(internshipRepository.save(internship)).thenReturn(internship);
        when(internshipMapper.toDto(internship)).thenReturn(updatedInternshipDto);

        InternshipDto actualResult = internshipService.update(savedInternshipDto, INTERNSHIP_ID);

        assertEquals(updatedInternshipDto, actualResult);
        assertEquals(1, savedIntern.getRoles().size());
        assertTrue(savedIntern.getRoles().contains(TeamRole.DEVELOPER));
    }

    @Test
    public void updateWhenInternshipCompletedAndInternNotCompleteTask() {
        Task task = Task.builder().performerUserId(INTERN_ID).status(TaskStatus.IN_PROGRESS).build();
        List<TeamMember> interns = new ArrayList<>();
        interns.add(savedIntern);
        internship.setInterns(interns);
        internship.getProject().getTasks().add(task);

        InternshipDto updatedInternshipDto = savedInternshipDto;
        updatedInternshipDto.setStatus(InternshipStatus.COMPLETED);

        when(internshipRepository.findById(INTERNSHIP_ID)).thenReturn(Optional.of(internship));
        when(internshipRepository.save(internship)).thenReturn(internship);
        when(internshipMapper.toDto(internship)).thenReturn(updatedInternshipDto);

        InternshipDto actualResult = internshipService.update(savedInternshipDto, INTERNSHIP_ID);

        assertEquals(updatedInternshipDto, actualResult);
        assertFalse(interns.contains(savedIntern));
    }

    @Test
    public void updateWhenInternshipNotCompleted() {
        Task task = Task.builder().performerUserId(INTERN_ID).status(TaskStatus.IN_PROGRESS).build();
        internship.setEndDate(LocalDateTime.now().plusDays(1));
        List<TeamMember> interns = new ArrayList<>();
        interns.add(savedIntern);
        internship.setInterns(interns);
        internship.getProject().getTasks().add(task);

        InternshipDto updatedInternshipDto = savedInternshipDto;
        updatedInternshipDto.setStatus(InternshipStatus.COMPLETED);

        when(candidateRepository.findById(INTERN_ID)).thenReturn(Optional.of(candidate));

        when(internshipRepository.findById(INTERNSHIP_ID)).thenReturn(Optional.of(internship));
        when(internshipRepository.save(internship)).thenReturn(internship);
        when(internshipMapper.toDto(internship)).thenReturn(updatedInternshipDto);

        InternshipDto actualResult = internshipService.update(savedInternshipDto, INTERNSHIP_ID);

        assertEquals(updatedInternshipDto, actualResult);
    }

    @Test
    public void updateWhenInternshipNotCompletedAndCreateNewIntern() {
        Task task = Task.builder().performerUserId(INTERN_ID).status(TaskStatus.IN_PROGRESS).build();
        internship.setEndDate(LocalDateTime.now().plusDays(1));
        List<TeamMember> interns = new ArrayList<>();
        interns.add(savedIntern);
        internship.setInterns(interns);
        internship.getProject().getTasks().add(task);

        long newCandidateId = 3L;
        Candidate newCandidate = new Candidate();
        newCandidate.setId(newCandidateId);
        savedInternshipDto.setCandidateIds(List.of(newCandidateId));

        InternshipDto updatedInternshipDto = savedInternshipDto;
        updatedInternshipDto.setStatus(InternshipStatus.COMPLETED);

        when(candidateRepository.findById(newCandidateId)).thenReturn(Optional.of(newCandidate));
        when(internshipRepository.findById(INTERNSHIP_ID)).thenReturn(Optional.of(internship));
        when(internshipRepository.save(internship)).thenReturn(internship);
        when(internshipMapper.toDto(internship)).thenReturn(updatedInternshipDto);

        InternshipDto actualResult = internshipService.update(savedInternshipDto, INTERNSHIP_ID);
        assertEquals(updatedInternshipDto, actualResult);
    }

    @Test
    public void findByIdWhenInternshipExists() {
        when(internshipRepository.findById(INTERNSHIP_ID)).thenReturn(Optional.of(internship));
        when(internshipMapper.toDto(internship)).thenReturn(savedInternshipDto);

        InternshipDto actualResult = internshipService.findById(INTERNSHIP_ID);
        assertEquals(savedInternshipDto, actualResult);
    }

    @Test
    public void findByIdWhenInternshipNotExists() {
        String errMessage = "Internship doesn't exist by id: " + INTERNSHIP_ID;

        doThrow(new EntityNotFoundException(errMessage)).when(internshipRepository).findById(INTERNSHIP_ID);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                internshipService.findById(INTERNSHIP_ID));
        assertThat(exception.getMessage()).isEqualTo(errMessage);
    }

    @Test
    public void findAll() {
        when(internshipRepository.findAll()).thenReturn(List.of(internship));
        when(internshipMapper.toListDto(List.of(internship))).thenReturn(List.of(savedInternshipDto));

        List<InternshipDto> actualResult = internshipService.findAll();
        assertEquals(1, actualResult.size());
        assertEquals(savedInternshipDto, actualResult.get(0));
    }
}