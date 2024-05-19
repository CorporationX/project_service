package faang.school.projectservice.service.candidate;

import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.repository.CandidateRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CandidateServiceImplTest {
    private static final long CANDIDATE_ID = 1L;

    @Mock
    private CandidateRepository candidateRepository;
    @InjectMocks
    private CandidateServiceImpl candidateService;
    private Candidate candidate;

    @BeforeEach
    void setUp() {
        candidate = new Candidate();
        candidate.setId(CANDIDATE_ID);
    }

    @Test
    public void whenFindByIdAndCandidateNotExistThenThrowException() {
        when(candidateRepository.findById(CANDIDATE_ID)).thenThrow(EntityNotFoundException.class);
        Assert.assertThrows(EntityNotFoundException.class,
                () -> candidateService.findById(CANDIDATE_ID));
    }

    @Test
    public void whenFindByIdAndCandidateExistsThenReturnCandidate() {
        when(candidateRepository.findById(CANDIDATE_ID)).thenReturn(Optional.of(candidate));
        Candidate actual = candidateService.findById(CANDIDATE_ID);
        assertThat(actual).isEqualTo(candidate);
    }
}