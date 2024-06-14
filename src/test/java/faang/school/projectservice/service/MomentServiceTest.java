package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.jpa.MomentJpaRepository;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.service.project.filters.MomentDataFilter;
import faang.school.projectservice.service.project.filters.MomentFilter;
import faang.school.projectservice.validation.ProjectValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static faang.school.projectservice.util.TestMoment.MOMENTS;
import static faang.school.projectservice.util.TestMoment.MOMENT_1;
import static faang.school.projectservice.util.TestMoment.MOMENT_ID_1;
import static faang.school.projectservice.util.TestMoment.NOW;
import static faang.school.projectservice.util.TestProject.PROJECT_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MomentServiceTest {

    @Mock
    ProjectValidation projectValidation;
    @Mock
    MomentJpaRepository momentRepository;
    @Mock
    List<MomentFilter> momentFilters;
    @InjectMocks
    MomentService momentService;

    Moment moment;

    @BeforeEach
    void init() {
        MomentFilter filterMock = mock(MomentFilter.class);
        List<MomentFilter> momentFilters = List.of(filterMock);
    }


    @Test
    public void testCreateMoment() {
        momentRepository.save(moment);
        verify(momentRepository).save(moment);
    }

    @Test
    public void testUpdateMoment() {
        momentRepository.getReferenceById(MOMENT_ID_1);
        verify(momentRepository).getReferenceById(MOMENT_ID_1);
        momentRepository.save(moment);
        verify(momentRepository).save(moment);
    }

    @Test
    public void testGetAllFilteredMomentsOfProject() {
        MomentFilterDto momentFilterDto = new MomentFilterDto();
        momentFilterDto.setDate(NOW);

        List<Moment> momentList = List.of(MOMENT_1);

        when(momentRepository.findAllByProjectId(PROJECT_ID)).thenReturn(List.of(MOMENT_1));
        when(momentFilters.stream()).thenReturn(Stream.of(new MomentDataFilter()));

        List<Moment> filteredMoments = momentService.getFilteredMomentsOfProject(PROJECT_ID, momentFilterDto);

        assertIterableEquals(momentList, filteredMoments);
        verify(momentRepository, times(1)).findAllByProjectId(PROJECT_ID);
        verify(projectValidation, times(1)).checkProjectExists(PROJECT_ID);
    }

    @Test
    public void testGetAllMomentsOfProject() {
        when(momentRepository.findAllByProjectId(PROJECT_ID)).thenReturn(MOMENTS);
        List<Moment> moments = momentService.getAllMoments(PROJECT_ID);

        verify(momentRepository, times(1)).findAllByProjectId(PROJECT_ID);
        verify(projectValidation, times(1)).checkProjectExists(PROJECT_ID);
        assertNotNull(moments);
        assertEquals(3, moments.size());
    }
}