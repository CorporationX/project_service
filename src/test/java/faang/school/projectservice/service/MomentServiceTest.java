package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.filters.FilterMomentDto;
import faang.school.projectservice.filters.FiltersDto;
import faang.school.projectservice.filters.MomentMapper;
import faang.school.projectservice.filters.filtersForFilterMomentDto.MomentNameFilter;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MomentServiceTest {
    private FilterMomentDto filterMomentDto;
    private MomentService momentService;
    @Mock
    private MomentRepository momentRepository;
    @Mock
    private MomentMapper momentMapper;
    @Mock
    private List<FiltersDto> filtersDto;
    private Moment moment = new Moment();

    @BeforeEach
    void setUp() {
        FiltersDto momentNameFilter = new MomentNameFilter();
        filtersDto = List.of(momentNameFilter);
        momentService = new MomentService(momentRepository, momentMapper, filtersDto);
        moment.setName("first important moment");
        filterMomentDto = new FilterMomentDto();
        filterMomentDto.setNamePattern("first");
    }

    @Test
    void createMomentCallsRepositoryMethod() {
        MomentDto momentDto = new MomentDto();
        momentService.createMoment(momentDto);
        Mockito.verify(momentRepository, Mockito.times(1))
                .save(momentMapper.dtoToMoment(momentDto));
    }

    @Test
    void updateMomentCallsRepositoryMethod() {
        MomentDto momentDto = new MomentDto();
        momentService.updateMoment(momentDto);
        Mockito.verify(momentRepository, Mockito.times(1))
                .save(momentMapper.dtoToMoment(momentDto));
    }

    @Test
    void getFilteredMomentsReturnsValidList() {
        Moment invalidMoment = new Moment();
        invalidMoment.setName("second");
        Mockito.when(momentRepository.findAll()).thenReturn(List.of(moment, invalidMoment));
        assertEquals(1, momentService.getFilteredMoments(filterMomentDto).size());
        Mockito.verify(momentRepository, Mockito.times(1))
                .findAll();
    }

    @Test
    void getAllMomentsCallsRepositoryMethod() {
        momentService.getAllMoments();
        Mockito.verify(momentRepository, Mockito.times(1))
                .findAll();
    }

    @Test
    void getMomentCallsRepositoryMethod() {
        Optional<Moment> optionalMoment = Optional.of(moment);
        Mockito.when(momentRepository.findById(1L)).thenReturn(optionalMoment);
        momentService.getMoment(1L);
        Mockito.verify(momentRepository, Mockito.times(1))
                .findById(1L);
    }
}