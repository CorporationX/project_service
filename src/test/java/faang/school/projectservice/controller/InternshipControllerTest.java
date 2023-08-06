package faang.school.projectservice.controller;

import faang.school.projectservice.dto.InternshipDto;
import faang.school.projectservice.service.InternshipService;
import faang.school.projectservice.validation.RequestValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class InternshipControllerTest {

    private static final long INTERNSHIP_ID = 1L;

    @InjectMocks
    private InternshipController internshipController;

    @Mock
    private InternshipService internshipService;

    @Mock
    private RequestValidator requestValidator;


    @Test
    void getAllInternshipsTest() {
        List<InternshipDto> internshipDtoList = List.of(
            InternshipDto.builder().id(1L).build(),
            InternshipDto.builder().id(2L).build(),
            InternshipDto.builder().id(3L).build()
        );

        when(internshipService.getAllInternships()).thenReturn(internshipDtoList);

        ResponseEntity<List<InternshipDto>> response = internshipController.getAllInternships();

        verify(internshipService).getAllInternships();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(internshipDtoList, response.getBody());
    }

    @Test
    void getInternshipByTest() {
        InternshipDto internshipDto = InternshipDto.builder().id(INTERNSHIP_ID).build();

        when(internshipService.getInternshipBy(INTERNSHIP_ID)).thenReturn(internshipDto);

        ResponseEntity<InternshipDto> response = internshipController.getInternshipBy(INTERNSHIP_ID);

        verify(internshipService).getInternshipBy(INTERNSHIP_ID);
        verify(requestValidator).validate(INTERNSHIP_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(internshipDto, response.getBody());
    }
}