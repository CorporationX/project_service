package faang.school.projectservice.internship;

import faang.school.projectservice.controller.InternshipController;
import faang.school.projectservice.dto.InternshipDto;
import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.InternshipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InternshipControllerTest {

    @Mock
    private InternshipService internshipService;

    @InjectMocks
    private InternshipController internshipController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreate_ShouldReturnCreatedInternship() {
        InternshipDto internshipDto = new InternshipDto();
        internshipDto.setId(1L);
        when(internshipService.create(any(InternshipDto.class))).thenReturn(internshipDto);

        ResponseEntity<InternshipDto> response = internshipController.create(new InternshipDto());

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(internshipDto, response.getBody());
        verify(internshipService, times(1)).create(any(InternshipDto.class));
    }

    @Test
    void testUpdate_ShouldReturnUpdatedInternship() {
        InternshipDto internshipDto = new InternshipDto();
        internshipDto.setId(1L);
        when(internshipService.update(anyLong(), any(InternshipDto.class))).thenReturn(internshipDto);

        ResponseEntity<InternshipDto> response = internshipController.update(1L, new InternshipDto());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(internshipDto, response.getBody());
        verify(internshipService, times(1)).update(anyLong(), any(InternshipDto.class));
    }

    @Test
    void testGetInternshipsByProject_ShouldReturnListOfInternships() {
        InternshipDto internshipDto = new InternshipDto();
        internshipDto.setId(1L);
        when(internshipService.getInternshipsByProjectAndFilter(anyLong(), any(InternshipFilterDto.class)))
                .thenReturn(Collections.singletonList(internshipDto));

        ResponseEntity<List<InternshipDto>> response = internshipController.getInternshipsByProject(1L, InternshipStatus.IN_PROGRESS, TeamRole.INTERN);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(internshipService, times(1)).getInternshipsByProjectAndFilter(anyLong(), any(InternshipFilterDto.class));
    }

    @Test
    void testGetAllInternships_ShouldReturnPageOfInternships() {
        InternshipDto internshipDto = new InternshipDto();
        internshipDto.setId(1L);
        Page<InternshipDto> page = new PageImpl<>(Arrays.asList(internshipDto));
        when(internshipService.getAllInternships(any(Pageable.class))).thenReturn(page);

        ResponseEntity<Page<InternshipDto>> response = internshipController.getAllInternships(PageRequest.of(0, 10));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getTotalElements());
        verify(internshipService, times(1)).getAllInternships(any(Pageable.class));
    }

    @Test
    void testGetInternshipById_ShouldReturnInternship() {
        InternshipDto internshipDto = new InternshipDto();
        internshipDto.setId(1L);
        when(internshipService.getInternshipById(anyLong())).thenReturn(internshipDto);

        ResponseEntity<InternshipDto> response = internshipController.getInternshipById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(internshipDto, response.getBody());
        verify(internshipService, times(1)).getInternshipById(anyLong());
    }

    @Test
    void testHandleDataValidationException_ShouldReturnBadRequest() {
        DataValidationException exception = new DataValidationException("Validation error");

        ResponseEntity<String> response = internshipController.handleDataValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation error", response.getBody());
    }

    @Test
    void testHandleNoSuchElementException_ShouldReturnNotFound() {
        NoSuchElementException exception = new NoSuchElementException("Element not found");

        ResponseEntity<String> response = internshipController.handleNoSuchElementException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Element not found", response.getBody());
    }
}

