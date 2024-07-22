package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.internShip.InternshipDtoValidateException;
import faang.school.projectservice.filter.filterImpl.NamePatternFilter;
import faang.school.projectservice.filter.filterImpl.StatusFilter;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.filter.internship.InternshipFilterDto;
import faang.school.projectservice.jpa.ScheduleRepository;
import faang.school.projectservice.mapper.internship.InternshipMapperImpl;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.internShip.InternshipService;
import faang.school.projectservice.validator.internShip.InternshipDtoValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class InternshipServiceTest {
    private InternshipService internshipService;
    private InternshipRepository internshipRepository;
    private TeamMemberRepository teamMemberRepository;
    private ProjectRepository projectRepository;
    private ScheduleRepository scheduleRepository;
    private InternshipMapperImpl internshipMapper;
    private InternshipDtoValidator validator;
    @Captor
    private ArgumentCaptor<Internship> captorInternship;
    private Internship entity;
    private InternshipDto dto;

    @BeforeEach
    void setUp() {
        internshipRepository = mock(InternshipRepository.class);
        teamMemberRepository = mock(TeamMemberRepository.class);
        projectRepository = mock(ProjectRepository.class);
        scheduleRepository = mock(ScheduleRepository.class);
        internshipMapper = spy(InternshipMapperImpl.class);
        validator = mock(InternshipDtoValidator.class);

        InternshipFilter nameFilter = spy(NamePatternFilter.class);
        InternshipFilter statusFilter = spy(StatusFilter.class);
        List<InternshipFilter> internshipFilters = List.of(nameFilter, statusFilter);
        internshipService = new InternshipService(internshipRepository, teamMemberRepository, projectRepository,
                scheduleRepository, internshipMapper, validator, internshipFilters);

        createDtoAndEntity();
    }

    private void createDtoAndEntity() {
        Long projectId = 1L;
        Project project = new Project();
        project.setId(projectId);
        Long mentorId = 1L;
        TeamMember mentor = new TeamMember();
        mentor.setId(mentorId);
        Long internFirstId = 2L;
        Long internSecondId = 3L;
        TeamMember internFirst = new TeamMember();
        TeamMember internSecond = new TeamMember();
        internFirst.setId(internFirstId);
        internSecond.setId(internSecondId);
        List<Long> internIds = List.of(internFirstId, internSecondId);
        List<TeamMember> interns = List.of(internFirst, internSecond);
        LocalDateTime startDate = LocalDateTime.of(2024, 10, 10, 10, 10);
        LocalDateTime endDate = startDate.plusMonths(2);
        InternshipStatus status = InternshipStatus.IN_PROGRESS;
        String description = "description";
        String name = "name";
        LocalDateTime createAt = LocalDateTime.of(2024, 10, 9, 10, 10);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 10, 9, 10, 10);
        Long createdBy = 1L;
        Long updatedBy = 2L;
        Long scheduleId = 1L;
        Schedule schedule = new Schedule();
        schedule.setId(scheduleId);
        Long internshipId = 1L;

        dto = new InternshipDto(internshipId, projectId, mentorId, internIds, startDate,
                endDate, status, description, name, createdBy, updatedBy, scheduleId);
        entity = new Internship(internshipId, project, mentor, interns, startDate,
                endDate, status, description, name, createAt, updatedAt, createdBy, updatedBy, schedule);
    }

    @Test
    public void testCreateWithMentorIsMember() {
        prepareForCreate(true);
        dto.setId(null);

        // Action
        internshipService.create(dto);

        // Assert
        verify(internshipRepository, times(1)).save(captorInternship.capture());

        // mini Arrange
        Internship actualEntity = captorInternship.getValue();
        // Одинаковое время не получить. Просто переприсваиваю значения
        entity.setCreatedAt(actualEntity.getCreatedAt());
        entity.setUpdatedAt(actualEntity.getUpdatedAt());
        entity.setUpdatedBy(entity.getCreatedBy()); // при создании оба поля одинаковые.
        actualEntity.setId(entity.getId()); // мок-репо не генерирует id (имитация)

        assertEquals(entity, actualEntity);
    }

    @Test
    public void testCreateWithMentorIsNotMember() {
        prepareForCreate(false);

        // Assert
        Assertions.assertThrows(InternshipDtoValidateException.class, () -> internshipService.create(dto));
    }

    @DisplayName("""
            Метод для теста создания стажировки. Обрабатывает два сценария.
            1. Ментор - не член команды проекта
            2. Ментор - член команды
            """)
    private void prepareForCreate(boolean isMember) {
        Long mentorId = dto.getMentorId();
        TeamMember mentor = entity.getMentorId(); // В Internship mentor назван как mentorId
        when(teamMemberRepository.findById(mentorId)).thenReturn(entity.getMentorId());

        Long projectId = dto.getProjectId();
        Project project = entity.getProject();
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        // Если ментор член команды, то готовлю entity, которую поймаю captor'ом
        if (isMember) {
            when(validator.validateMentorIsMember(mentor, project)).thenReturn(true);
            Long scheduleId = dto.getScheduleId();
            Schedule schedule = entity.getSchedule();
            when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

            TeamMember internFirst = entity.getInterns().get(0);
            TeamMember internSecond = entity.getInterns().get(1);
            when(teamMemberRepository.findById(internFirst.getId())).thenReturn(internFirst);
            when(teamMemberRepository.findById(internSecond.getId())).thenReturn(internSecond);
        } else {
            // иначе валидатор бросает исключение
            when(validator.validateMentorIsMember(mentor, project)).thenThrow(InternshipDtoValidateException.class);
        }
    }

    @DisplayName("Тест на изменения ментора, который не член проекта")
    @Test
    void testUpdateWithChangeInvalidMentor() {
        when(internshipRepository.findById(dto.getId())).thenReturn(Optional.of(entity));
        Long mentorId = dto.getMentorId() + 1;
        TeamMember newMentorInvalid = new TeamMember();
        newMentorInvalid.setId(mentorId);
        dto.setMentorId(mentorId);
        Project project = entity.getProject();
        when(teamMemberRepository.findById(mentorId)).thenReturn(newMentorInvalid);
        when(projectRepository.getProjectById(project.getId())).thenReturn(project);
        when(validator.validateMentorIsMember(newMentorInvalid, project)).thenThrow(InternshipDtoValidateException.class);

        Assertions.assertThrows(InternshipDtoValidateException.class, () -> internshipService.update(dto));
    }

    @DisplayName("Тест на изменения полей, которые менять нельзя")
    @Test
    void testUpdateWithInvalidChanges() {
        when(internshipRepository.findById(dto.getId())).thenReturn(Optional.of(entity));
        when(validator.checkChanges(entity, dto)).thenThrow(InternshipDtoValidateException.class);

        Assertions.assertThrows(InternshipDtoValidateException.class, () -> internshipService.update(dto));
    }

    @Test
    void testUpdateWithBothCompleted() {
        InternshipStatus completeStatus = InternshipStatus.COMPLETED;
        dto.setStatus(completeStatus);
        entity.setStatus(completeStatus);
        when(internshipRepository.findById(dto.getId())).thenReturn(Optional.of(entity));
        doThrow(InternshipDtoValidateException.class).when(validator).checkCompletedStatus(dto, entity);

        Assertions.assertThrows(InternshipDtoValidateException.class, () -> internshipService.update(dto));
    }

    @Test
    void testUpdateWithCompleted() {
        InternshipStatus completeStatus = InternshipStatus.COMPLETED;
        dto.setStatus(completeStatus);
        entity.setStatus(completeStatus);
        when(internshipRepository.findById(dto.getId())).thenReturn(Optional.of(entity));
        doThrow(InternshipDtoValidateException.class).when(validator).checkCompletedStatus(dto, entity);

        Assertions.assertThrows(InternshipDtoValidateException.class, () -> internshipService.update(dto));
    }

    @Test
    void testUpdateWithValidChanges() {
        when(internshipRepository.findById(dto.getId())).thenReturn(Optional.of(entity));
        when(scheduleRepository.findById(dto.getScheduleId())).thenReturn(Optional.of(entity.getSchedule()));
        String newName = "newName";
        long newUpdateBy = dto.getUpdatedBy() + 1;
        dto.setName(newName);
        dto.setUpdatedBy(newUpdateBy);

        internshipService.update(dto);

        verify(internshipRepository, times(1)).save(captorInternship.capture());
        InternshipDto actualDto = internshipMapper.toDto(captorInternship.getValue());
        assertEquals(dto, actualDto);
    }

    @Test
    void testGetInvalidInternship() {
        Long invalidId = 1L;
        when(internshipRepository.findById(invalidId)).thenThrow(InternshipDtoValidateException.class);

        Assertions.assertThrows(InternshipDtoValidateException.class, () -> internshipService.getInternship(invalidId));
    }

    @Test
    void testGetValidInternship() {
        Long validId = dto.getId();
        when(internshipRepository.findById(validId)).thenReturn(Optional.of(entity));

        InternshipDto internshipActual = internshipService.getInternship(validId);

        assertEquals(dto, internshipActual);
    }

    @Test
    void testGetAllInternship() {
        List<Internship> internships = List.of(entity);
        when(internshipRepository.findAll()).thenReturn(internships);
        List<InternshipDto> expDtos = internships.stream().map(internshipMapper::toDto).toList();

        List<InternshipDto> actualDtos = internshipService.getAllInternships();

        assertEquals(expDtos, actualDtos);
    }

    @Test
    void testFilter() {
        InternshipStatus statusFilter = InternshipStatus.COMPLETED;
        String nameFilter = "filter";
        Internship entityFirst = new Internship();
        entityFirst.setId(1L);
        entityFirst.setName("nameFirst");
        entityFirst.setStatus(InternshipStatus.IN_PROGRESS);
        Internship entitySecond = new Internship();
        entitySecond.setId(2L);
        entitySecond.setName("nameSecond" + nameFilter);
        entitySecond.setStatus(statusFilter);
        Internship entityThird = new Internship();
        entityThird.setId(3L);
        entityThird.setName("nameThird filter" + nameFilter);
        entityThird.setStatus(InternshipStatus.IN_PROGRESS);
        List<Internship> entities = List.of(entityFirst, entitySecond, entityThird);
        when(internshipRepository.findAll()).thenReturn(entities);
        List<InternshipDto> expDto = List.of(internshipMapper.toDto(entitySecond));
        InternshipFilterDto filters = new InternshipFilterDto();
        filters.setName(nameFilter);
        filters.setStatus(statusFilter);

        List<InternshipDto> actualDto = internshipService.getFilteredInternship(filters);

        assertEquals(expDto, actualDto);
    }


}
