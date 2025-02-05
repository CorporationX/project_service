package faang.school.projectservice.service.candidate;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.CandidateRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {
    @Mock
    CandidateRepository candidateRepository;
    @InjectMocks
    CandidateService candidateService;

    @Test
    void deleteCandidatesByVacancyId() {
        doNothing().when(candidateRepository).deleteByVacancyId(anyLong());
        assertDoesNotThrow(() -> candidateService.deleteCandidatesByVacancyId(1L));
        verify(candidateRepository).deleteByVacancyId(anyLong());
    }

    @Test
    void deleteCandidatesByVacancyIdIsNull() {
        Assertions.assertThrows(DataValidationException.class, () -> candidateService.deleteCandidatesByVacancyId(null),
                "vacancyId is null");
    }
}