package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.repository.InternshipRepository;
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class InternshipServiceTest {
    @InjectMocks
    private InternshipService internshipService;
    @Mock
    private InternshipServiceValidation internshipServiceValidation;
    @Mock
    private InternshipMapper internshipMapper;
    @Mock
    private InternshipRepository internshipRepository;

    private InternshipDto internshipDto;
    private Internship internship;
    private ArgumentCaptor<InternshipDto> internshipDtoArgumentCaptor;
    private ArgumentCaptor<Internship> internshipArgumentCaptor;

    @BeforeEach
    void init() {
        internshipDto = new InternshipDto();
        internship = new Internship();
        internshipDtoArgumentCaptor = ArgumentCaptor.forClass(InternshipDto.class);
        internshipArgumentCaptor = ArgumentCaptor.forClass(Internship.class);
    }

    @Nested
    class PositiveTests {
        @DisplayName("should call save internship when valid internship creation intended")
        @Test
        void shouldSaveInternshipWhenValidInternshipCreationIntended() {
            doNothing().when(internshipServiceValidation).validationCreate(internshipDto);
            doReturn(internship).when(internshipMapper).toEntity(internshipDto);

            internshipService.create(internshipDto);

            verify(internshipMapper).toEntity(internshipDtoArgumentCaptor.capture());
            verify(internshipRepository).save(internshipArgumentCaptor.capture());
            assertEquals(internship, internshipArgumentCaptor.getValue());
            assertEquals(internshipDto, internshipDtoArgumentCaptor.getValue());
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should throw DataValidationException when invalid internship creation intended")
        @Test
        void shouldThrowDataValidationExceptionWhenInvalidInternshipCreationIntended() {
            doThrow(new DataValidationException("Something went wrong"))
                    .when(internshipServiceValidation)
                    .validationCreate(internshipDto);

            assertThrows(DataValidationException.class, () -> internshipService.create(internshipDto));

            verifyNoInteractions(internshipMapper);
            verifyNoInteractions(internshipRepository);
        }
    }
}