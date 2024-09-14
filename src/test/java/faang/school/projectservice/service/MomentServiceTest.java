package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MomentServiceTest {

    @Mock
    private MomentRepository momentRepository;

    @Mock
    private MomentMapper momentMapper;

    @InjectMocks
    private MomentService momentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void givenValidMomentDto_whenCreateMoment_thenReturnCreatedMomentDto() {
        MomentDto momentDto = new MomentDto();
        momentDto.setName("Test Moment");
        Moment moment = new Moment();
        when(momentMapper.toEntity(momentDto)).thenReturn(moment);
        when(momentRepository.save(moment)).thenReturn(moment);
        when(momentMapper.toDto(moment)).thenReturn(momentDto);

        MomentDto result = momentService.createMoment(momentDto);

        assertEquals(momentDto, result);
    }

    @Test
    public void givenMomentDtoWithNullName_whenCreateMoment_thenThrowIllegalArgumentException() {
        MomentDto momentDto = new MomentDto();
        momentDto.setName(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            momentService.createMoment(momentDto);
        });
        assertEquals("Moment name cannot be empty", exception.getMessage());
    }

    @Test
    public void givenValidMomentDto_whenUpdateMoment_thenReturnUpdatedMomentDto() {
        MomentDto momentDto = new MomentDto();
        momentDto.setId(1L);
        momentDto.setName("Updated Moment");
        Moment moment = new Moment();
        when(momentMapper.toEntity(momentDto)).thenReturn(moment);
        when(momentRepository.save(moment)).thenReturn(moment);
        when(momentMapper.toDto(moment)).thenReturn(momentDto);

        MomentDto result = momentService.updateMoment(momentDto);

        assertEquals(momentDto, result);
    }

    @Test
    public void givenMomentDtoWithNullId_whenUpdateMoment_thenThrowIllegalArgumentException() {
        MomentDto momentDto = new MomentDto();
        momentDto.setId(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            momentService.updateMoment(momentDto);
        });
        assertEquals("Moment ID cannot be null", exception.getMessage());
    }

    @Test
    public void givenMomentsInRepository_whenGetAllMoments_thenReturnListOfMomentDtos() {
        Moment moment = new Moment();
        when(momentRepository.findAll()).thenReturn(Collections.singletonList(moment));
        when(momentMapper.toDto(moment)).thenReturn(new MomentDto());

        List<MomentDto> result = momentService.getAllMoments();

        assertEquals(1, result.size());
    }

    @Test
    public void givenMomentId_whenGetMomentById_thenReturnMomentDto() {
        Moment moment = new Moment();
        when(momentRepository.findById(1L)).thenReturn(Optional.of(moment));
        when(momentMapper.toDto(moment)).thenReturn(new MomentDto());

        MomentDto result = momentService.getMomentById(1L);

        assertNotNull(result);
    }

    @Test
    public void givenInvalidMomentId_whenGetMomentById_thenReturnNull() {
        when(momentRepository.findById(1L)).thenReturn(Optional.empty());

        MomentDto result = momentService.getMomentById(1L);

        assertNull(result);
    }

    @Test
    public void givenFilters_whenGetMomentsByFilters_thenReturnFilteredMomentDtos() {
        Moment moment = new Moment();
        when(momentRepository.findAll()).thenReturn(Collections.singletonList(moment));
        when(momentMapper.toDto(moment)).thenReturn(new MomentDto());

        List<MomentDto> result = momentService.getMomentsByFilters(null, null);

        assertEquals(1, result.size());
    }
}