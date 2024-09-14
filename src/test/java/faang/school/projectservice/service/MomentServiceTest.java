package faang.school.projectservice.service;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.model.Moment;
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

    public MomentServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateMoment() {
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
    public void testUpdateMoment() {
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
    public void testGetAllMoments() {
        Moment moment = new Moment();
        when(momentRepository.findAll()).thenReturn(Collections.singletonList(moment));
        when(momentMapper.toDto(moment)).thenReturn(new MomentDto());

        List<MomentDto> result = momentService.getAllMoments();
        assertEquals(1, result.size());
    }

    @Test
    public void testGetMomentById() {
        Moment moment = new Moment();
        when(momentRepository.findById(1L)).thenReturn(Optional.of(moment));
        when(momentMapper.toDto(moment)).thenReturn(new MomentDto());

        MomentDto result = momentService.getMomentById(1L);
        assertNotNull(result);
    }

    @Test
    public void testGetMomentsByFilters() {
        Moment moment = new Moment();
        when(momentRepository.findAll()).thenReturn(Collections.singletonList(moment));
        when(momentMapper.toDto(moment)).thenReturn(new MomentDto());

        List<MomentDto> result = momentService.getMomentsByFilters(null, null);
        assertEquals(1, result.size());
    }
}
