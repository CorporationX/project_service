package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipToCreateDto;
import faang.school.projectservice.dto.internship.InternshipToUpdateDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.internship.filter.InternshipFilterService;
import faang.school.projectservice.validation.internship.InternshipValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InternshipServiceImplTest {

    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private InternshipValidator validator;

    @Mock
    private InternshipMapper internshipMapper;

    @Mock
    private InternshipFilterService internshipFilterService;

    @InjectMocks
    private InternshipServiceImpl internshipService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createInternship_success() {
        InternshipToCreateDto createDto = mock(InternshipToCreateDto.class);
        Internship internship = mock(Internship.class);
        InternshipDto internshipDto = mock(InternshipDto.class);

        when(internshipMapper.toEntity(createDto)).thenReturn(internship);
        when(internshipDto.getInternsId()).thenReturn(Collections.singletonList(1L));
        when(teamMemberRepository.findById(1L)).thenReturn((new TeamMember()));
        when(internshipMapper.toDto(internship)).thenReturn(internshipDto);

        InternshipDto result = internshipService.createInternship(1L, createDto);

        verify(internshipRepository).save(internship);
        verify(internshipMapper).toDto(internship);
        assertEquals(internshipDto, result);
    }

    @Test
    void getInternshipById_success() {
        Internship internship = mock(Internship.class);
        InternshipDto internshipDto = mock(InternshipDto.class);

        when(internshipRepository.findById(anyLong())).thenReturn(Optional.of(internship));
        when(internshipMapper.toDto(internship)).thenReturn(internshipDto);

        InternshipDto result = internshipService.getInternshipById(1L);

        assertEquals(internshipDto, result);
    }

    @Test
    void getAllInternshipsByProjectId_success() {
        Internship internship = mock(Internship.class);
        InternshipDto internshipDto = mock(InternshipDto.class);
        InternshipFilterDto filterDto = mock(InternshipFilterDto.class);

        when(internshipRepository.findByProjectId(anyLong())).thenReturn(Collections.singletonList(internship));
        when(internshipFilterService.applyFilters(any(), any())).thenReturn(Collections.singletonList(internship).stream());
        when(internshipMapper.toDto(any())).thenReturn(internshipDto);

        List<InternshipDto> result = internshipService.getAllInternshipsByProjectId(1L, filterDto);

        assertEquals(Collections.singletonList(internshipDto), result);
    }

    @Test
    void getAllInternships_success() {
        Internship internship = mock(Internship.class);
        InternshipDto internshipDto = mock(InternshipDto.class);
        InternshipFilterDto filterDto = mock(InternshipFilterDto.class);

        when(internshipRepository.findAll()).thenReturn(Collections.singletonList(internship));
        when(internshipFilterService.applyFilters(any(), any())).thenReturn(Collections.singletonList(internship).stream());
        when(internshipMapper.toDto(any())).thenReturn(internshipDto);

        List<InternshipDto> result = internshipService.getAllInternships(filterDto);

        assertEquals(Collections.singletonList(internshipDto), result);
    }

    @Test
    void updateInternship_completedStatus_throwsException() {
        InternshipToUpdateDto updateDto = mock(InternshipToUpdateDto.class);
        Internship internship = mock(Internship.class);

        when(internshipRepository.findById(anyLong())).thenReturn(Optional.of(internship));
        when(internship.getStatus()).thenReturn(InternshipStatus.COMPLETED);

        DataValidationException exception = assertThrows(DataValidationException.class, () ->
                internshipService.updateInternship(1L, 1L, updateDto));

        assertEquals("Completed internship cannot be updated", exception.getMessage());
    }
}
