package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.internship.InternshipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class InternshipControllerTest {

    @InjectMocks
    private InternshipController internshipController;
    @Mock
    private InternshipService internshipService;
    @Mock
    private InternshipControllerValidation internshipControllerValidation;
    private InternshipDto internshipDto;
    private ArgumentCaptor<InternshipDto> internshipDtoArgumentCaptor;

    @BeforeEach
    void init() {
        internshipDto = new InternshipDto();
        internshipDtoArgumentCaptor = ArgumentCaptor.forClass(InternshipDto.class);
    }

    @Nested
    class PositiveTests {
        @DisplayName("should call internshipService.create() when valid dto is passed")
        @Test
        void shouldCallInternshipServiceCreateWhenValidDtoIsPassed() {
            doNothing().when(internshipControllerValidation).validationDto(internshipDto);

            internshipController.create(internshipDto);

            verify(internshipService).create(internshipDtoArgumentCaptor.capture());
            assertEquals(internshipDto, internshipDtoArgumentCaptor.getValue());
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should throw DataValidationException when invalid dto is passed")
        @Test
        void shouldThrowDataValidationExceptionWhenInvalidDtoIsPassed() {
            doThrow(new DataValidationException("Something went wrong"))
                    .when(internshipControllerValidation)
                    .validationDto(internshipDto);

            assertThrows(DataValidationException.class, () -> internshipController.create(internshipDto));

            verifyNoInteractions(internshipService);
        }
    }
}