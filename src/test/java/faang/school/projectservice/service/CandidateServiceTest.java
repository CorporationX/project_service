package faang.school.projectservice.service;

import faang.school.projectservice.model.candidate.Candidate;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.CandidateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CandidateServiceTest {

    @Mock
    private CandidateRepository candidateRepository;

    @InjectMocks
    private CandidateService candidateService;

    private Candidate candidate;
    private Vacancy vacancy;

    @BeforeEach
    public void setUp() {
        candidate = new Candidate();
        candidate.setId(1L);
        vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setCandidates(new ArrayList<>());
    }

    @Test
    public void testDeleteCandidate_Success() {
        // Act
        candidateService.deleteCandidate(1L);

        // Assert
        verify(candidateRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testFindCandidates_Success() {
        // Arrange
        when(candidateRepository.findAllById(List.of(1L))).thenReturn(List.of(candidate));

        // Act
        List<Candidate> result = candidateService.findCandidates(List.of(1L));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(candidate, result.get(0));
    }

    @Test
    public void testUpdateCandidatesWithVacancy_Success() {
        // Act
        candidateService.updateCandidatesWithVacancy(List.of(candidate), vacancy);

        // Assert
        assertEquals(vacancy, candidate.getVacancy());
        verify(candidateRepository, times(1)).save(candidate);
    }
}