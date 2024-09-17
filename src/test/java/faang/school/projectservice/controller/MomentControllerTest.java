package faang.school.projectservice.controller;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.service.MomentService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MomentControllerTest {
    private MomentDto momentDto;
    private MomentDto expectedMomentDto;
    private MomentDto momentDtoToReturn;
    private MomentFilterDto filterDto;
    private MomentFilterDto expectedFilterDto;
    private List<MomentDto> returnDtos;
    private List<MomentDto> expectedDtos;
    private long id;
    private Long expectedId;
    @InjectMocks
    private MomentController controller;
    @Mock
    private MomentService service;
    @Captor
    private ArgumentCaptor<MomentDto> captor;
    @Captor
    private ArgumentCaptor<MomentFilterDto> captorFilter;
    @Captor
    private ArgumentCaptor<Long> captorId;

    @BeforeEach
    public void setup() {
        momentDto = new MomentDto(
                1L,
                "Moment",
                "description",
                LocalDateTime.of(2024, 12, 31, 12, 0, 0),
                new ArrayList<>(List.of(1L, 2L)),
                new ArrayList<>(List.of(1L, 2L)),
                "imageId1",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        expectedMomentDto = new MomentDto(
                1L,
                "Moment",
                "description",
                LocalDateTime.of(2024, 12, 31, 12, 0, 0),
                new ArrayList<>(List.of(1L, 2L)),
                new ArrayList<>(List.of(1L, 2L)),
                "imageId1",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        momentDtoToReturn = new MomentDto(
                1L,
                "Moment",
                "description",
                LocalDateTime.of(2024, 12, 31, 12, 0, 0),
                new ArrayList<>(List.of(1L, 2L)),
                new ArrayList<>(List.of(1L, 2L)),
                "imageId1",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        filterDto = new MomentFilterDto(Month.AUGUST, new ArrayList<>());
        expectedFilterDto = new MomentFilterDto(filterDto.month(), filterDto.projectIds());
        returnDtos = new ArrayList<>(
                List.of(
                        momentDto,
                        new MomentDto(
                                2L,
                                "moment2",
                                "description2",
                                LocalDateTime.of(2024, 12, 31, 12, 0, 0),
                                null,
                                new ArrayList<>(List.of(10L, 20L)),
                                "imageId2",
                                null,
                                null
                        )
                )
        );
        expectedDtos = new ArrayList<>(
                List.of(
                        momentDto,
                        new MomentDto(
                                2L,
                                "moment2",
                                "description2",
                                LocalDateTime.of(2024, 12, 31, 12, 0, 0),
                                null,
                                new ArrayList<>(List.of(10L, 20L)),
                                "imageId2",
                                null,
                                null
                        )
                )
        );
        id = 1;
        expectedId = 1L;
    }

    @Test
    public void testCreate_nameIsNull() {
        // Arrange
        momentDto = new MomentDto(
                1L,
                null,
                "description",
                LocalDateTime.of(2024, 12, 31, 12, 0, 0),
                new ArrayList<>(List.of(1L, 2L)),
                new ArrayList<>(List.of(1L, 2L)),
                "imageId1",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // Act and Assert
        Exception exception = Assertions.assertThrows(DataValidationException.class, () -> controller.create(momentDto));
        Assertions.assertEquals("Наименование момента не может быть пустым", exception.getMessage());
    }

    @Test
    public void testCreate_projectIsNull() {
        // Arrange
        momentDto = new MomentDto(
                1L,
                "moment",
                "description",
                LocalDateTime.of(2024, 12, 31, 12, 0, 0),
                null,
                new ArrayList<>(List.of(1L, 2L)),
                "imageId1",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // Act and Assert
        Exception exception = Assertions.assertThrows(DataValidationException.class, () -> controller.create(momentDto));
        Assertions.assertEquals("Момент должен относиться к какому-нибудь проекту", exception.getMessage());
    }

    @Test
    public void testCreate_thereIsNoProject() {
        // Arrange
        momentDto = new MomentDto(
                1L,
                "moment",
                "description",
                LocalDateTime.of(2024, 12, 31, 12, 0, 0),
                new ArrayList<>(),
                new ArrayList<>(List.of(1L, 2L)),
                "imageId1",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // Act and Assert
        Exception exception = Assertions.assertThrows(DataValidationException.class, () -> controller.create(momentDto));
        Assertions.assertEquals("Момент должен относиться к какому-нибудь проекту", exception.getMessage());
    }

    @Test
    public void testCreate_captor() {
        // Act and Assert
        controller.create(momentDto);
        verify(service, times(1)).create(captor.capture());
        Assertions.assertEquals(expectedMomentDto, captor.getValue());
    }

    @Test
    public void testCreate() {
        // Arrange
        when(service.create(momentDto)).thenReturn(momentDtoToReturn);
        // Act
        MomentDto returnMomentDto = controller.create(momentDto);
        // Assert
        Assertions.assertEquals(expectedMomentDto, returnMomentDto);
    }

    @Test
    public void testUpdate_captor() {
        // Act and Assert
        controller.update(momentDto);
        verify(service, times(1)).update(captor.capture());
        Assertions.assertEquals(expectedMomentDto, captor.getValue());
    }

    @Test
    public void testUpdate() {
        // Arrange
        when(service.update(momentDto)).thenReturn(momentDtoToReturn);

        // Act
        MomentDto returnMomentDto = controller.update(momentDto);

        // Assert
        Assertions.assertEquals(expectedMomentDto, returnMomentDto);
    }

    @Test
    public void testGetMoments_captor() {
        // Act and Assert
        controller.getMoments(filterDto);
        verify(service, times(1)).getMoments(captorFilter.capture());
        Assertions.assertEquals(expectedFilterDto, captorFilter.getValue());
    }

    @Test
    public void testGetMoments() {
        // Assert
        when(service.getMoments(filterDto)).thenReturn(returnDtos);

        // Act
        List<MomentDto> dtos = controller.getMoments(filterDto);

        // Assert
        Assertions.assertEquals(expectedDtos, dtos);
    }

    @Test
    public void testGetAllMoments() {
        // Arrange
        when(service.getAllMoments()).thenReturn(returnDtos);

        // Act
        List<MomentDto> dtos = controller.getAllMoments();
        verify(service, times(1)).getAllMoments();

        // Assert
        Assertions.assertEquals(expectedDtos, dtos);
    }

    @Test
    public void testGetMoment_idIsWrong() {
        // Arrange
        long id = 0;

        // Act and Assert
        Exception exception = Assertions.assertThrows(DataValidationException.class,() -> controller.getMoment(id));
        Assertions.assertEquals("Передан некорректный id", exception.getMessage());
    }

    @Test
    public void testGetMoment_captor() {
        // Act and Assert
        service.getMoment(id);
        verify(service, times(1)).getMoment(captorId.capture());
        Assertions.assertEquals(expectedId, captorId.getValue());
    }

    @Test
    public void testGetMoment() {
        // Arrange
        when(service.getMoment(id)).thenReturn(momentDtoToReturn);

        // Act
        MomentDto returnDto = controller.getMoment(id);

        // Assert
        Assertions.assertEquals(expectedMomentDto, returnDto);
    }
}
