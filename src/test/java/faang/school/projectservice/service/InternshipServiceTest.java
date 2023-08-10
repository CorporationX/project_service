package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.exceptions.InternshipValidationException;
import faang.school.projectservice.filters.InternshipFilters.InternshipFilter;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
class InternshipServiceTest {

    @Mock
    private TeamMemberJpaRepository teamMemberJpaRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private InternshipRepository internshipRepository;
    @Spy
    private InternshipMapper internshipMapper;
    @Mock
    private List<InternshipFilter> internshipFilters;
    @InjectMocks
    private InternshipService internshipService;

    @Test
    void internshipCreation() {
    }

    @Test
    void testInternshipBusinessValidationFirst() {
        InternshipDto internshipDto = InternshipDto.builder().id(4L).projectId(null)
                .mentorId(4L).internsId(List.of(3L, 5L)).startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();

        assertThrows(InternshipValidationException.class,
                () -> internshipService.internshipCreation(internshipDto));
    }

    @Test
    void testInternshipBusinessValidationSecond() {
        List <Long> internTestList = List.of();
        InternshipDto internshipDto = InternshipDto.builder().id(4L).projectId(5L)
                .mentorId(4L).internsId(internTestList).startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();

        assertThrows(InternshipValidationException.class,
                () -> internshipService.internshipCreation(internshipDto));
    }

    @Test
    void testInternshipBusinessValidationThird() {
    }

    @Test
    void testInternshipBusinessValidationForth() {
    }


    @Test
    void gettingAllInternshipsAccordingToFilters() {
    }

    @Test
    void gettingAllInternships() {
    }

    @Test
    void getInternshipById() {
    }

    @Test
    void updateInternship() {
    }

    @Test
    void testInternshipCreation() {
    }

    @Test
    void testUpdateInternship() {
    }

    @Test
    void updateInternBeforeInternshipEnd() {
    }

    @Test
    void deleteIntern() {
    }

    @Test
    void testGettingAllInternshipsAccordingToFilters() {
    }

    @Test
    void testGettingAllInternships() {
    }

    @Test
    void testGetInternshipById() {
    }
}