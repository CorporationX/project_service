package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.internShip.InternshipDtoValidateException;
import faang.school.projectservice.service.internShip.InternshipService;
import faang.school.projectservice.validator.internShip.InternshipDtoValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InternshipControllerTest {
    @InjectMocks
    private InternshipController controller;
    @Mock
    private InternshipDtoValidator validator;
    @Mock
    private InternshipService internshipService;

    @Test
    public void testCreateWithInvalidDto() {
        InternshipDto invalidDto = new InternshipDto();
        when(validator.validateInternshipDto(invalidDto)).thenThrow(InternshipDtoValidateException.class);

        assertThrows(InternshipDtoValidateException.class, () -> controller.create(invalidDto));
    }

    @Test
    public void testCreateWithValidDto() {
        InternshipDto validDto = new InternshipDto();
        when(validator.validateInternshipDto(validDto)).thenReturn(true);

        controller.create(validDto);

        verify(internshipService, times(1)).create(validDto);
    }

    @Test
    public void testUpdateWithInvalidDto() {
        InternshipDto invalidDto = new InternshipDto();
        when(validator.validateInternshipDto(invalidDto)).thenThrow(InternshipDtoValidateException.class);

        assertThrows(InternshipDtoValidateException.class, () -> controller.update(invalidDto));
    }

    @Test
    public void testUpdateWithValidDto() {
        InternshipDto validDto = new InternshipDto();
        when(validator.validateInternshipDto(validDto)).thenReturn(true);

        controller.update(validDto);

        verify(internshipService, times(1)).update(validDto);
    }

    @Test
    public void testGetInternshipWithIdNull() {
        Long internshipId = null;
        doThrow(InternshipDtoValidateException.class).when(validator).validateInternshipId(internshipId);
        
        assertThrows(InternshipDtoValidateException.class, () -> controller.getInternship(internshipId));
    }

    @Test
    public void testGetInternshipWithValidId() {
        Long internshipId = 1L;

        controller.getInternship(internshipId);

        verify(internshipService, times(1)).getInternship(internshipId);
    }

}
