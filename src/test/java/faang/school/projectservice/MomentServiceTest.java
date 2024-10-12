package faang.school.projectservice;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.service.moment.MomentService;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.repository.MomentRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;
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

    private Validator validator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        factoryBean.afterPropertiesSet();
        validator = factoryBean.getValidator();
    }

    @Test
    public void givenValidMomentDto_whenCreateMoment_thenReturnCreatedMomentDto() {
        MomentDto momentDto = new MomentDto();
        momentDto.setName("Test Moment");
        momentDto.setDate(LocalDateTime.now());
        momentDto.setCreatedBy(1L);
        momentDto.setUpdatedBy(1L);
        Moment moment = new Moment();
        when(momentMapper.toEntity(momentDto)).thenReturn(moment);
        when(momentRepository.save(moment)).thenReturn(moment);
        when(momentMapper.toDto(moment)).thenReturn(momentDto);

        MomentDto result = momentService.createMoment(momentDto);

        assertEquals(momentDto, result);
    }

    @Test
    public void givenValidMomentDto_whenUpdateMoment_thenReturnUpdatedMomentDto() {
        MomentDto momentDto = new MomentDto();
        momentDto.setId(1L);
        momentDto.setName("Updated Moment");
        momentDto.setDate(LocalDateTime.now());
        momentDto.setCreatedBy(1L);
        momentDto.setUpdatedBy(1L);
        Moment moment = new Moment();
        when(momentMapper.toEntity(momentDto)).thenReturn(moment);
        when(momentRepository.save(moment)).thenReturn(moment);
        when(momentMapper.toDto(moment)).thenReturn(momentDto);

        MomentDto result = momentService.updateMoment(momentDto);

        assertEquals(momentDto, result);
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