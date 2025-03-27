package faang.school.projectservice.service;

import faang.school.projectservice.mapper.InternshipMapper;

import faang.school.projectservice.repository.InternshipRepository;

import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class InternshipUpdateLogicServiceTest extends TestSetUp {
    @Mock
    InternshipRepository internshipRepository;
    @Mock
    InternshipMapper internshipMapper;
    @Mock
    TaskPerformance taskPerformance;
    @Mock
    Promotion promotion;

    @InjectMocks
    InternshipUpdateLogicService internshipUpdateLogicService;


    @Test
    @DisplayName("Testing if there is such an internship in repository by id")
    void testUpdateLogic_idNotFound() {
        Mockito.when(internshipRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.empty());
        Assert.assertThrows(EntityNotFoundException.class, () -> internshipUpdateLogicService.updateLogic(firstInternshipDto));
    }

    @Test
    @DisplayName("Testing updating method")
    void testUpdateLogic() {
        Mockito.when(internshipRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(firstInternship));
        Mockito.when(taskPerformance.partitionByStatusDone(firstInternship)).thenReturn(map1);
        Mockito.when(promotion.promoteSucceededInterns(firstInternship, succeededUserIds)).thenReturn(afterPromotion);
        Mockito.when(promotion.demoteFailedInterns(firstInternship, failedInterns)).thenReturn(afterDemotion);
        Mockito.when(internshipMapper.toDto(firstInternship)).thenReturn(firstInternshipDto);

        internshipUpdateLogicService.updateLogic(firstInternshipDto);
    }
}