package faang.school.project_service.service.meet;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.meet.CreateMeetDto;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.filter.meet.MeetAfterDateFilter;
import faang.school.projectservice.filter.meet.MeetExactDateFilter;
import faang.school.projectservice.filter.meet.MeetTitleFilter;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.meet.MeetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MeetServiceImplTest {

    private final MeetMapper meetMapper = Mappers.getMapper(MeetMapper.class);
    private final Project project = Project.builder().id(1L).build();
    private final CreateMeetDto createMeetDto = CreateMeetDto.builder().projectId(project.getId()).build();
    private final UpdateMeetDto updateMeetDto = UpdateMeetDto.builder().projectId(project.getId()).build();
    private final MeetFilterDto meetFilterDto = MeetFilterDto.builder()
            .title("Correct title")
            .startsAt(LocalDateTime.now().plusMonths(1))
            .build();
    private final Meet meetInCorrectOne = Meet.builder()
            .title(new StringBuilder(meetFilterDto.title()).reverse().toString())
            .startsAt(meetFilterDto.startsAt())
            .build();

    private final Meet meetInCorrectTwo = Meet.builder()
            .title(meetFilterDto.title())
            .startsAt(meetFilterDto.startsAt().minusDays(1))
            .build();
    private final Meet meet = new Meet();

    @Captor
    private ArgumentCaptor<Meet> meetArgumentCaptor;

    @Mock
    private MeetRepository meetRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserContext userContext;

    private MeetServiceImpl meetServiceWithExactDateFilter;
    private MeetServiceImpl meetServiceWithAfterDateFilter;
    private Meet wrongCreatorMeet;
    private Meet wrongStatusMeet;

    @BeforeEach
    void setup() {
        List<MeetStatus> meetStatusList = Arrays.stream(MeetStatus.values())
                .filter(meetStatus -> !meetStatus.equals(MeetStatus.PENDING))
                .toList();

        meetServiceWithExactDateFilter = new MeetServiceImpl(meetRepository, meetMapper, projectRepository, userContext,
                List.of(new MeetTitleFilter(), new MeetExactDateFilter()));
        meetServiceWithAfterDateFilter = new MeetServiceImpl(meetRepository, meetMapper, projectRepository, userContext,
                List.of(new MeetTitleFilter(), new MeetAfterDateFilter()));

        meet.setId(345L);
        meet.setCreatorId(userContext.getUserId());
        meet.setStatus(MeetStatus.PENDING);
        meet.setTitle(meetFilterDto.title());
        meet.setStartsAt(meetFilterDto.startsAt());

        wrongCreatorMeet = Meet.builder().id(235L).creatorId(userContext.getUserId() + 1).build();
        wrongStatusMeet = Meet.builder()
                .id(235L)
                .creatorId(userContext.getUserId())
                .status(meetStatusList.get(new Random().nextInt(meetStatusList.size())))
                .build();
    }

    @Test
    void testCreateThrowsExceptionIfProjectNotFound() {
        when(projectRepository.getByIdOrThrow(project.getId())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> meetServiceWithExactDateFilter.create(createMeetDto));
    }

    @Test
    void testCreatePositive() {
        when(projectRepository.getByIdOrThrow(project.getId())).thenReturn(project);
        when(meetRepository.save(Mockito.any(Meet.class))).thenReturn(meet);

        MeetDto meetDto = meetServiceWithExactDateFilter.create(createMeetDto);

        verify(meetRepository).save(meetArgumentCaptor.capture());
        Meet capturedMeet = meetArgumentCaptor.getValue();

        assertEquals(meet.getId(), meetDto.id());
        assertEquals(project.getId(), capturedMeet.getProject().getId());
        assertEquals(userContext.getUserId(), capturedMeet.getCreatorId());
        assertEquals(MeetStatus.PENDING, capturedMeet.getStatus());
    }

    @Test
    void testUpdateThrowsExceptionIfMeetNotFound() {
        when(meetRepository.getByIdOrThrow(meet.getId())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> meetServiceWithExactDateFilter.update(meet.getId(), UpdateMeetDto.builder().build()));
    }

    @Test
    void testUpdateThrowsExceptionIfMeetCreatorNotEqualsCurrentUser() {
        creatorValidatorTest(wrongCreatorMeet,
                () -> meetServiceWithExactDateFilter.update(wrongCreatorMeet.getId(), UpdateMeetDto.builder().build()));
    }

    @Test
    void testUpdateThrowsExceptionIfStatusNotPending() {
        statusValidatorTest(wrongStatusMeet,
                () -> meetServiceWithExactDateFilter.update(wrongStatusMeet.getId(), UpdateMeetDto.builder().build()));
    }

    @Test
    void testUpdateThrowsExceptionIfProjectNotFound() {
        getUpdateTestsCustomMocks();
        when(projectRepository.getByIdOrThrow(updateMeetDto.projectId())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> meetServiceWithExactDateFilter.update(meet.getId(), updateMeetDto));
    }

    @Test
    void testUpdatePositive() {
        getUpdateTestsCustomMocks();
        when(projectRepository.getByIdOrThrow(updateMeetDto.projectId())).thenReturn(project);

        MeetDto meetToUpdateDto = meetServiceWithExactDateFilter.update(meet.getId(), updateMeetDto);

        verify(meetRepository).save(meetArgumentCaptor.capture());
        Meet savedMeet = meetArgumentCaptor.getValue();

        assertEquals(meetToUpdateDto.id(), savedMeet.getId());
        assertEquals(updateMeetDto.projectId(), savedMeet.getProject().getId());
    }

    @Test
    void testCancelThrowsExceptionIfMeetNotFound() {
        when(meetRepository.getByIdOrThrow(meet.getId())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> meetServiceWithExactDateFilter.cancel(meet.getId()));
    }

    @Test
    void testCancelThrowsExceptionIfMeetCreatorNotEqualsCurrentUser() {
        creatorValidatorTest(wrongCreatorMeet, () -> meetServiceWithExactDateFilter.cancel(wrongCreatorMeet.getId()));
    }

    @Test
    void testCancelThrowsExceptionIfStatusNotPending() {
        statusValidatorTest(wrongStatusMeet, () -> meetServiceWithExactDateFilter.cancel(wrongStatusMeet.getId()));
    }

    @Test
    void testCancelPositive() {
        when(meetRepository.getByIdOrThrow(meet.getId())).thenReturn(meet);

        MeetDto canceledMeetDto = meetServiceWithExactDateFilter.cancel(meet.getId());

        verify(meetRepository).save(meetArgumentCaptor.capture());
        Meet canceledMeet = meetArgumentCaptor.getValue();

        assertEquals(MeetStatus.CANCELLED, canceledMeet.getStatus());
        assertEquals(MeetStatus.CANCELLED, canceledMeetDto.status());
        assertEquals(meet.getId(), canceledMeet.getId());
        assertEquals(meet.getId(), canceledMeetDto.id());
    }

    @Test
    void testDeleteThrowsExceptionIfMeetNotFound() {
        when(meetRepository.getByIdOrThrow(meet.getId())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> meetServiceWithExactDateFilter.delete(meet.getId()));
    }

    @Test
    void testDeleteThrowsExceptionIfMeetCreatorNotEqualsCurrentUser() {
        creatorValidatorTest(wrongCreatorMeet, () -> meetServiceWithExactDateFilter.delete(wrongCreatorMeet.getId()));
    }

    @Test
    void testDeletePositive() {
        when(meetRepository.getByIdOrThrow(meet.getId())).thenReturn(meet);

        meetServiceWithExactDateFilter.delete(meet.getId());

        verify(meetRepository).delete(meetArgumentCaptor.capture());
        Meet meetToDelete = meetArgumentCaptor.getValue();

        assertEquals(meet.getId(), meetToDelete.getId());
    }

    @Test
    void testGetAllReturnEmptyListIfNothingFound() {
        when(meetRepository.findAll()).thenReturn(List.of());

        assertTrue(meetServiceWithExactDateFilter.getAll().isEmpty());
    }

    @Test
    void testGetAllPositive() {
        when(meetRepository.findAll()).thenReturn(List.of(meet));

        List<MeetDto> allMeets = meetServiceWithExactDateFilter.getAll();

        assertEquals(1, allMeets.size());
        assertEquals(meet.getId(), allMeets.get(0).id());
    }

    @Test
    void testGetByIdThrowsExceptionIfMeetNotFound() {
        long meetId = 43L;
        when(meetRepository.getByIdOrThrow(meetId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> meetServiceWithExactDateFilter.getById(meetId));
    }

    @Test
    void testGetByIdPositive() {
        when(meetRepository.getByIdOrThrow(meet.getId())).thenReturn(meet);

        MeetDto meetDto = meetServiceWithExactDateFilter.getById(meet.getId());

        assertEquals(meet.getId(), meetDto.id());
        assertEquals(meet.getStatus(), meetDto.status());
    }

    @Test
    void testGetByFiltersReturnEmptyListIfMeetsNotFound() {
        when(meetRepository.findAll()).thenReturn(List.of());

        assertTrue(meetServiceWithExactDateFilter.getByFilters(meetFilterDto).isEmpty());
    }

    @Test
    void testGetByFiltersWithExactDateFilter() {
        Meet meetCorrectOne = Meet.builder()
                .title(meetFilterDto.title() + "some other words")
                .startsAt(meetFilterDto.startsAt())
                .build();

        Meet meetInCorrectThree = Meet.builder()
                .title(meetFilterDto.title())
                .startsAt(meetFilterDto.startsAt().plusMonths(1))
                .build();

        List<Meet> meets
                = new ArrayList<>(List.of(meet, meetCorrectOne, meetInCorrectThree, meetInCorrectOne, meetInCorrectTwo));
        when(meetRepository.findAll()).thenReturn(meets);

        List<MeetDto> meetsByFilters = meetServiceWithExactDateFilter.getByFilters(meetFilterDto);
        List<String> correctMeetsTitles = Stream.of(meet, meetCorrectOne).map(Meet::getTitle).toList();

        assertEquals(2, meetsByFilters.size());
        assertTrue(meetsByFilters.stream().map(MeetDto::title).toList().containsAll(correctMeetsTitles));
    }

    @Test
    void testGetByFiltersWithAfterDateFilter() {
        Meet meetCorrect = Meet.builder()
                .title(meetFilterDto.title())
                .startsAt(meetFilterDto.startsAt().plusMonths(1))
                .build();

        Meet meetInCorrectThree = Meet.builder()
                .title(meetFilterDto.title() + "some other words")
                .startsAt(meetFilterDto.startsAt())
                .build();

        List<Meet> meets
                = new ArrayList<>(List.of(meet, meetInCorrectThree, meetCorrect, meetInCorrectOne, meetInCorrectTwo));
        when(meetRepository.findAll()).thenReturn(meets);

        List<MeetDto> meetsByFilters = meetServiceWithAfterDateFilter.getByFilters(meetFilterDto);

        assertEquals(1, meetsByFilters.size());
        assertEquals(meetCorrect.getTitle(), meetsByFilters.get(0).title());
    }

    private void statusValidatorTest(Meet meet, Executable executable) {
        when(meetRepository.getByIdOrThrow(meet.getId())).thenReturn(meet);

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, executable);
        assertEquals("Not allowed. Meet %d not in status '%s'".formatted(meet.getId(), MeetStatus.PENDING.name()),
                dataValidationException.getMessage());
    }

    private void getUpdateTestsCustomMocks() {
        when(meetRepository.getByIdOrThrow(meet.getId())).thenReturn(meet);
    }

    private void creatorValidatorTest(Meet meet, Executable executable) {
        when(meetRepository.getByIdOrThrow(meet.getId())).thenReturn(meet);

        ForbiddenException forbiddenException = assertThrows(ForbiddenException.class, executable);
        assertEquals("Not allowed. You are not a creator of meet %d".formatted(meet.getId()),
                forbiddenException.getMessage());
    }
}