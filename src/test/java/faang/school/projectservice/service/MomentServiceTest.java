package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.filters.moments.FilterMomentDto;
import faang.school.projectservice.filters.moments.MomentFilter;
import faang.school.projectservice.filters.moments.MomentMapper;
import faang.school.projectservice.filters.moments.MomentMapperImpl;
import faang.school.projectservice.filters.moments.filtersForFilterMomentDto.MomentDescriptionFilter;
import faang.school.projectservice.filters.moments.filtersForFilterMomentDto.MomentNameFilter;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MomentServiceTest {
    private FilterMomentDto filterMomentDto;
    @Mock
    private MomentRepository momentRepository;
    @Spy
    private MomentMapper momentMapper = new MomentMapperImpl();
    @Mock
    private List<MomentFilter> momentFilter;
    private MomentService momentService;
    private Moment moment = new Moment();

    @BeforeEach
    void setUp() {
        MomentFilter momentNameFilter = new MomentNameFilter();
        momentFilter = List.of(momentNameFilter, new MomentDescriptionFilter());
        momentService = new MomentService(momentRepository, momentMapper, momentFilter);
        moment.setName("first important moment");
        moment.setDate(LocalDateTime.now());
        moment.setDescription("description");
        filterMomentDto = new FilterMomentDto();
        filterMomentDto.setNamePattern("first");
        filterMomentDto.setDescriptionPattern("desc");
    }

    @Test
    void createMomentCallsRepositoryMethod() {
        MomentDto momentDto = new MomentDto(25L, "testDto", LocalDateTime.now());
        Moment moment =  momentMapper.dtoToMoment(momentDto);
        Mockito.when(momentRepository.save(momentMapper.dtoToMoment(momentDto))).thenReturn(moment);
        Mockito.when(momentRepository.findById(moment.getId())).thenReturn(Optional.of(moment));

        Moment createdMoment = momentService.createMoment(momentDto);
        Moment dbMoment = momentRepository.findById(momentDto.getId()).orElse(new Moment());

        Mockito.verify(momentRepository, Mockito.times(1))
                .save(momentMapper.dtoToMoment(momentDto));
        assertEquals(createdMoment.getId(), dbMoment.getId());
        assertEquals(createdMoment.getName(), dbMoment.getName());
        assertEquals(createdMoment.getDate(), dbMoment.getDate());
    }

    @Test
    void updateMomentCallsRepositoryMethod() {
        MomentDto momentDto = new MomentDto(25L, "new Name", LocalDateTime.now());
        Moment updatedMoment =  momentMapper.dtoToMoment(momentDto);
        Moment deprecatedMoment = Moment.builder().id(25L).name("old Name").build();

        Mockito.when(momentRepository.save(momentMapper.dtoToMoment(momentDto))).thenReturn(updatedMoment);
        Mockito.when(momentRepository.findById(momentDto.getId())).thenReturn(Optional.of(deprecatedMoment));
        Mockito.when(momentRepository.findById(updatedMoment.getId())).thenReturn(Optional.of(updatedMoment));

        momentService.updateMoment(momentDto);
        Moment dbMoment = momentRepository.findById(updatedMoment.getId()).orElse(new Moment());

        Mockito.verify(momentRepository, Mockito.times(1))
                .save(momentMapper.dtoToMoment(momentDto));
        assertEquals(updatedMoment.getName(), dbMoment.getName());
        assertNotEquals(deprecatedMoment.getName(), dbMoment.getName());
    }

    @Test
    void getFilteredMomentsReturnsValidList() {
        Moment invalidMoment = Moment.builder().name("First").description("invalid").build();
        Mockito.when(momentRepository.findAll()).thenReturn(List.of(moment, invalidMoment));

        assertEquals(1, momentService.getFilteredMoments(filterMomentDto).size());
        Mockito.verify(momentRepository, Mockito.times(1))
                .findAll();
    }

    @Test
    void getAllMomentsCallsRepositoryMethod() {
        Mockito.when(momentRepository.findAll()).thenReturn(List.of(moment));

        List<MomentDto> result = momentService.getAllMoments();

        Mockito.verify(momentRepository, Mockito.times(1))
                .findAll();
        assertEquals(List.of(momentMapper.momentToDto(moment)), result);
    }

    @Test
    void getMomentCallsRepositoryMethod() {
        Optional<Moment> optionalMoment = Optional.of(moment);
        Mockito.when(momentRepository.findById(1L)).thenReturn(optionalMoment);

        momentService.getMoment(1L);

        Mockito.verify(momentRepository, Mockito.times(1))
                .findById(1L);
        assertEquals(momentRepository.findById(1L).get().getName(), optionalMoment.get().getName());
    }
}