package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentCreateRequestDto;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class MomentServiceValidatorTest {
    @Mock
    ProjectRepository projectRepository;
    @InjectMocks
    MomentServiceValidator momentServiceValidator;

    private MomentCreateRequestDto validMomentCreateRequestDto;

    @BeforeEach
    void setUp() {
        validMomentCreateRequestDto = MomentCreateRequestDto.builder()
                .name("Cool moment")
                .description("It's a very cool moment")
                .projectIds(List.of(1L, 2L, 3L))
                .teamMemberIds(List.of(10L, 20L, 30L))
                .build();
    }

    @Test
    @DisplayName("Test active projects")
    void testValidateActiveProjects() {
        Mockito.when(projectRepository.findAllById(Mockito.anyList())).thenReturn(TestData.getSomeActiveProjects());
        momentServiceValidator.validateMomentProjectIds(validMomentCreateRequestDto.projectIds());
    }

    @Test
    @DisplayName("Test non-active projects")
    void testValidateNonActiveProjects() {
        Mockito.when(projectRepository.findAllById(Mockito.anyList())).thenReturn(TestData.getSomeNotActiveProjects());
        Assert.assertThrows(IllegalArgumentException.class,
                () -> momentServiceValidator.validateMomentProjectIds(validMomentCreateRequestDto.projectIds()));
    }
}