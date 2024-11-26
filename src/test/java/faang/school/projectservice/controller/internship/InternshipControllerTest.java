package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.filter.InternshipFilterDto;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    void setUp() {
        internshipDto = new InternshipDto();
        internshipDtoArgumentCaptor = ArgumentCaptor.forClass(InternshipDto.class);
    }

    @Nested
    class PositiveTests {
        @DisplayName("should call internshipService.create() when valid dto is passed")
        @Test
        void createTest() {
            doNothing().when(internshipControllerValidation).validateInternshipDuration(internshipDto);

            internshipController.create(internshipDto);

            verify(internshipService).create(internshipDtoArgumentCaptor.capture());
            assertEquals(internshipDto, internshipDtoArgumentCaptor.getValue());
        }

        @DisplayName("should call internshipService.update() when valid dto is passed")
        @Test
        void updateTest() {
            doNothing().when(internshipControllerValidation).validateInternshipDuration(internshipDto);
            internshipDto.setId(1L);

            internshipController.update(internshipDto);

            verify(internshipService).update(internshipDtoArgumentCaptor.capture());
            assertEquals(internshipDto, internshipDtoArgumentCaptor.getValue());
        }

        @DisplayName("should call internshipService.getInternshipsOfProject()")
        @Test
        void getInternshipsOfProjectTest() {
            var projectIdCaptor = ArgumentCaptor.forClass(Long.class);
            var filterCaptor = ArgumentCaptor.forClass(InternshipFilterDto.class);

            internshipController.getInternshipsOfProject(anyLong(), any(InternshipFilterDto.class));

            verify(internshipService).getInternshipsOfProject(projectIdCaptor.capture(), filterCaptor.capture());
        }

        @DisplayName("should call internshipService.getAllInternships()")
        @Test
        void getAllInternshipsTest() {
            internshipController.getAllInternships();

            verify(internshipService).getAllInternships();
        }

        @DisplayName("should call internshipService.getInternshipById()")
        @Test
        void getInternshipByIdTest() {
            var internshipIdCaptor = ArgumentCaptor.forClass(Long.class);


            internshipController.getInternshipById(anyLong());

            verify(internshipService).getInternshipById(internshipIdCaptor.capture());
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should throw DataValidationException when invalid dto is passed during internship creation")
        @Test
        void createTest() {
            doThrow(new DataValidationException("Something went wrong"))
                    .when(internshipControllerValidation)
                    .validateInternshipDuration(internshipDto);

            assertThrows(DataValidationException.class, () -> internshipController.create(internshipDto));

            verifyNoInteractions(internshipService);
        }

        @DisplayName("should throw DataValidationException when invalid dto is passed during internship updating")
        @Test
        void updateByInvalidDtoTest() {
            doThrow(new DataValidationException("Something went wrong"))
                    .when(internshipControllerValidation)
                    .validateInternshipDuration(internshipDto);

            assertThrows(DataValidationException.class, () -> internshipController.update(internshipDto));

            verifyNoInteractions(internshipService);
        }

        @DisplayName("should throw DataValidationException when id of non-existent internship is passed during internship updating")
        @Test
        void updateNonExistentInternshipTest() {
            doNothing().when(internshipControllerValidation).validateInternshipDuration(internshipDto);

            assertThrows(DataValidationException.class, () -> internshipController.update(internshipDto));

            verifyNoInteractions(internshipService);
        }
    }
}