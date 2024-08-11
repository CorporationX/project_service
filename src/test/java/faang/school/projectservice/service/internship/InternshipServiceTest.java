package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.mapper.InternshipMapperImpl;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.common.DataValidationException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InternshipServiceTest {
    @InjectMocks
    private InternshipService internshipService;
    @Mock
    private InternshipRepository internshipRepository;
    @Spy
    private InternshipMapperImpl internshipMapper;
    @Mock
    private InternshipValidationTool validationTool;
    @Mock
    private InternshipDataPreparer dataPreparer;

    private Internship entity;
    private InternshipDto dto;
    private InternshipContainer container = new InternshipContainer();

    @Test
    public void testCreateWithInvalidDto() {
        // given
        InternshipDto invalidDto = InternshipDto.builder()
                .name(container.name())
                .build();
        doThrow(DataValidationException.class).when(validationTool).validationBeforeCreate(invalidDto);

        // then
        Assertions.assertThrows(DataValidationException.class, () -> internshipService.create(invalidDto));
    }

    @Test
    public void testCreateWithValidDto() {
        // given
        prepareObjectsForCreate();
        Internship halfEntity = internshipMapper.toEntity(dto);
        when(dataPreparer.prepareEntityForCreate(dto, halfEntity)).thenReturn(entity);

        // when
        internshipService.create(dto);

        // then
        verify(dataPreparer, times(1)).prepareEntityForCreate(Mockito.eq(dto), Mockito.eq(halfEntity));
        verify(internshipRepository, times(1)).save(entity);
    }

    @Test
    void testUpdateNotExists() {
        // given
        InternshipDto invalidDto = InternshipDto.builder()
                .id(container.internshipId())
                .build();
        doThrow(EntityNotFoundException.class).when(internshipRepository).findById(invalidDto.getId());

        // then
        Assertions.assertThrows(EntityNotFoundException.class, () -> internshipService.update(invalidDto));
    }

    @DisplayName("Изменение уже завершённой стажировки")
    @Test
    void testUpdateAlreadyCompleted() {
        // given
        InternshipDto dto = InternshipDto.builder()
                .id(container.internshipId())
                .build();

        Internship completedEntity = Internship.builder()
                .id(container.internshipId())
                .build();
        when(internshipRepository.findById(dto.getId())).thenReturn(Optional.of(completedEntity));
        doThrow(DataValidationException.class).when(validationTool).validationStatus(dto, completedEntity);

        // then
        Assertions.assertThrows(DataValidationException.class, () -> internshipService.update(dto));
    }

    @DisplayName("Изменение стажировки с невалидными данными.")
    @Test
    void testUpdateWithInvalidData() {
        // given
        InternshipDto invalidDataDto = InternshipDto.builder()
                .id(container.internshipId())
                .build();

        Internship entity = Internship.builder()
                .id(container.internshipId())
                .build();

        boolean changedStatus = false;
        when(internshipRepository.findById(invalidDataDto.getId())).thenReturn(Optional.of(entity));
        when(validationTool.validationStatus(invalidDataDto, entity)).thenReturn(changedStatus);
        doThrow(DataValidationException.class).when(validationTool).validationBeforeUpdate(invalidDataDto, entity);

        // then
        Assertions.assertThrows(DataValidationException.class, () -> internshipService.update(invalidDataDto));
    }

    @DisplayName("Изменение стажировки с валидными данными.")
    @Test
    void testUpdateWithValidData() {
        // given
        prepareObjectsForUpdate();
        boolean changedStatus = false;
        when(internshipRepository.findById(dto.getId())).thenReturn(Optional.of(entity));
        when(validationTool.validationStatus(dto, entity)).thenReturn(changedStatus);
        when(dataPreparer.prepareEntityForUpdate(dto, entity)).thenReturn(entity);
        TeamRole role = TeamRole.DEVELOPER;

        // when
        internshipService.update(dto);

        // then
        verify(dataPreparer, times(1)).prepareEntityForUpdate(Mockito.eq(dto), Mockito.eq(entity));
        verify(dataPreparer, times(0)).evaluationInterns(entity, role);
        verify(internshipRepository, times(1)).save(entity);
    }

    @DisplayName("Изменение стажировки с валидными данными и её завершением.")
    @Test
    void testUpdateToComplete() {
        // given
        prepareObjectsForUpdate();
        boolean changedStatus = true;
        when(internshipRepository.findById(dto.getId())).thenReturn(Optional.of(entity));
        when(validationTool.validationStatus(dto, entity)).thenReturn(changedStatus);
        when(dataPreparer.prepareEntityForUpdate(dto, entity)).thenReturn(entity);
        TeamRole role = TeamRole.DEVELOPER;

        // when
        internshipService.update(dto);

        // then
        verify(dataPreparer, times(1)).prepareEntityForUpdate(Mockito.eq(dto), Mockito.eq(entity));
        verify(dataPreparer, times(1)).evaluationInterns(entity, role);
        verify(internshipRepository, times(1)).save(entity);
    }

    @Test
    void testGetInvalidInternship() {
        // given
        Long invalidId = 1L;
        when(internshipRepository.findById(invalidId)).thenThrow(EntityNotFoundException.class);

        // then
        Assertions.assertThrows(EntityNotFoundException.class, () -> internshipService.getInternship(invalidId));
    }

    @Test
    void testGetValidInternship() {
        // given
        prepareObjectsForCreate();
        Long validId = container.internshipId();
        dto.setId(validId);
        entity.setId(validId);
        when(internshipRepository.findById(validId)).thenReturn(Optional.of(entity));

        // when
        InternshipDto internshipActual = internshipService.getInternship(validId);

        // then
        assertEquals(dto, internshipActual);
    }

    @Test
    void testGetAllInternship() {
        // given
        Internship internship = Internship.builder()
                .id(container.internshipId())
                .build();

        List<Internship> internships = List.of(internship);
        when(internshipRepository.findAll()).thenReturn(internships);
        List<InternshipDto> expDtos = internships.stream().map(internshipMapper::toDto).toList();

        // when
        List<InternshipDto> actualDtos = internshipService.getAllInternships();

        // then
        assertEquals(expDtos, actualDtos);
    }

    @Test
    void testFilterInternships() {
        // given
        Internship internship = Internship.builder()
                .id(container.internshipId())
                .build();
        List<Internship> internships = List.of(internship);
        when(internshipRepository.findAll()).thenReturn(internships);

        InternshipDto dto = InternshipDto.builder()
                .id(container.internshipId())
                .internIds(new ArrayList<>())
                .build();
        List<InternshipDto> expListDto = List.of(dto);

        InternshipFilterDto filters = new InternshipFilterDto();
        when(dataPreparer.filterInternships(internships, filters)).thenReturn(internships);

        // when
        List<InternshipDto> actualListDto = internshipService.getFilteredInternships(filters);

        // then
        assertEquals(expListDto, actualListDto);
    }

    private Internship prepareObjectsForUpdate() {
        TeamMember mentor = TeamMember.builder()
                .id(container.mentorId())
                .build();

        TeamMember updatedMentor = TeamMember.builder()
                .id(mentor.getId() + 1)
                .build();

        Schedule schedule = Schedule.builder()
                .id(container.scheduleId())
                .build();

        Schedule updatedSchedule = Schedule.builder()
                .id(schedule.getId() + 1)
                .build();

        boolean mentorIsMember = true;
        Project project = container.project(mentor, mentorIsMember);

        List<TeamMember> interns = container.getInterns();
        List<Long> internIds = new ArrayList<>(List.of(interns.get(0).getId(), interns.get(1).getId()));

        LocalDateTime updatedEndDate = container.endDate().plusDays(1);
        String updatedDescription = container.description() + " update";
        String updatedName = container.name() + " update";

        dto = InternshipDto.builder()
                .id(container.internshipId())
                .mentorId(updatedMentor.getId())
                .projectId(container.projectId())
                .scheduleId(updatedSchedule.getId())
                .internIds(internIds)
                .startDate(container.startDate())
                .endDate(updatedEndDate)
                .status(container.statusCompleted())
                .description(updatedDescription)
                .name(updatedName)
                .createdBy(container.createdBy())
                .updatedBy(container.updatedBy())
                .build();

        entity = Internship.builder()
                .id(container.internshipId())
                .mentor(mentor)
                .project(project)
                .schedule(schedule)
                .interns(interns)
                .startDate(container.startDate())
                .endDate(container.endDate())
                .status(container.statusInProgress())
                .description(container.description())
                .name(container.name())
                .createdAt(container.createAt())
                .updatedAt(container.createAt())
                .createdBy(container.createdBy())
                .updatedBy(container.createdBy())
                .build();

        return Internship.builder()
                .id(entity.getId())
                .mentor(updatedMentor)
                .project(entity.getProject())
                .schedule(updatedSchedule)
                .interns(entity.getInterns())
                .startDate(entity.getStartDate())
                .endDate(dto.getEndDate())
                .status(dto.getStatus())
                .description(dto.getDescription())
                .name(dto.getName())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(dto.getUpdatedBy())
                .build();
    }

    private void prepareObjectsForCreate() {
        TeamMember mentor = TeamMember.builder()
                .id(container.mentorId())
                .build();

        Schedule schedule = Schedule.builder()
                .id(container.scheduleId())
                .build();

        boolean mentorIsMember = true;
        Project project = container.project(mentor, mentorIsMember);

        List<TeamMember> interns = container.getInterns();
        List<Long> internIds = new ArrayList<>(List.of(interns.get(0).getId(), interns.get(1).getId()));

        dto = InternshipDto.builder()
                .mentorId(container.mentorId())
                .projectId(container.projectId())
                .scheduleId(container.scheduleId())
                .internIds(internIds)
                .startDate(container.startDate())
                .endDate(container.endDate())
                .status(container.statusInProgress())
                .description(container.description())
                .name(container.name())
                .createdBy(container.createdBy())
                .updatedBy(container.createdBy())
                .build();

        entity = Internship.builder()
                .mentor(mentor)
                .project(project)
                .schedule(schedule)
                .interns(interns)
                .startDate(container.startDate())
                .endDate(container.endDate())
                .status(container.statusInProgress())
                .description(container.description())
                .name(container.name())
                .createdAt(container.createAt())
                .updatedAt(container.createAt())
                .createdBy(container.createdBy())
                .updatedBy(container.createdBy())
                .build();
    }
}
