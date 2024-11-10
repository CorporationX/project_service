package faang.school.projectservice;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.mappers.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.InternshipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InternshipServiceTest {

    @Mock
    private InternshipRepository internshipRepository;

    @Mock
    private InternshipMapper internshipMapper;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private InternshipService internshipService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateInternship() {
        InternshipDto dto = InternshipDto.builder()
                .id(1L)
                .name("Test Internship")
                .mentorId(2L)
                .internIds(Collections.singletonList(3L))
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .status("COMPLETED")
                .description("Test description")
                .build();

        Internship internship = new Internship();
        when(internshipMapper.toEntity(dto)).thenReturn(internship);
        when(internshipRepository.save(internship)).thenReturn(internship);
        when(internshipMapper.toDto(internship)).thenReturn(dto);

        InternshipDto result = internshipService.createInternship(dto);

        assertNotNull(result);
        verify(internshipRepository, times(1)).save(internship);
    }

    @Test
    void testUpdateInternship() {
        Internship internship = new Internship();
        internship.setId(1L);
        internship.setStatus(InternshipStatus.IN_PROGRESS);
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(internship));
        InternshipDto dto = InternshipDto.builder()
                .id(1L)
                .name("Test Internship")
                .mentorId(2L)
                .internIds(Collections.singletonList(3L))
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .status("COMPLETED")
                .description("Test description")
                .build();

        InternshipDto result = internshipService.updateInternship(1L, dto);

        assertEquals(InternshipStatus.COMPLETED, internship.getStatus());
        verify(internshipRepository, times(1)).save(internship);
    }

    @Test
    void testGetAllInternships() {
        Internship internship = new Internship();
        internship.setStatus(InternshipStatus.IN_PROGRESS);
        when(internshipRepository.findAll()).thenReturn(List.of(internship));
        when(internshipMapper.toDto(any(Internship.class))).thenReturn(new InternshipDto());

        List<InternshipDto> result = internshipService.getAllInternships(null, null);

        assertEquals(1, result.size());
        verify(internshipRepository, times(1)).findAll();
    }

    @Test
    void testGetInternshipById() {
        Internship internship = new Internship();
        internship.setId(1L);
        when(internshipRepository.findById(1L)).thenReturn(Optional.of(internship));
        when(internshipMapper.toDto(internship)).thenReturn(new InternshipDto());

        InternshipDto result = internshipService.getInternshipById(1L);

        assertNotNull(result);
        verify(internshipRepository, times(1)).findById(1L);
    }

}
