package faang.school.projectservice.internship;

import faang.school.projectservice.controller.InternshipController;
import faang.school.projectservice.model.dto.InternshipFilterDto;
import faang.school.projectservice.model.dto.InternshipDto;
import faang.school.projectservice.model.enums.InternshipStatus;
import faang.school.projectservice.model.enums.TeamRole;
import faang.school.projectservice.service.impl.InternshipServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InternshipControllerTest {

    @Mock
    private InternshipServiceImpl internshipService;

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

        InternshipDto savedInternshipDto = internshipController.create(new InternshipDto());

        assertEquals(internshipDto, savedInternshipDto);
        verify(internshipService, times(1)).create(any(InternshipDto.class));
    }

    @Test
    void testUpdate_ShouldReturnUpdatedInternship() {
        InternshipDto internshipDto = new InternshipDto();
        internshipDto.setId(1L);
        when(internshipService.update(anyLong(), any(InternshipDto.class))).thenReturn(internshipDto);

        InternshipDto updatedInternshipDto = internshipController.update(1L, new InternshipDto());

        assertEquals(internshipDto, updatedInternshipDto);
        verify(internshipService, times(1)).update(anyLong(), any(InternshipDto.class));
    }

    @Test
    void testGetInternshipsByProject_ShouldReturnListOfInternships() {
        InternshipDto internshipDto = new InternshipDto();
        internshipDto.setId(1L);
        when(internshipService.getInternshipsByProjectAndFilter(anyLong(), any(InternshipFilterDto.class)))
                .thenReturn(Collections.singletonList(internshipDto));

        InternshipFilterDto filterDto = new InternshipFilterDto();
        filterDto.setRolePattern(TeamRole.INTERN);
        filterDto.setStatusPattern(InternshipStatus.IN_PROGRESS);

        List<InternshipDto> internshipDtoList = internshipController.getInternshipsByProject(1L, filterDto);

        assertEquals(1, internshipDtoList.size());
        verify(internshipService, times(1)).getInternshipsByProjectAndFilter(anyLong(), any(InternshipFilterDto.class));
    }

    @Test
    void testGetAllInternships_ShouldReturnPageOfInternships() {
        InternshipDto internshipDto = new InternshipDto();
        internshipDto.setId(1L);
        Page<InternshipDto> page = new PageImpl<>(Arrays.asList(internshipDto));
        when(internshipService.getAllInternships(any(Pageable.class))).thenReturn(page);

        Page<InternshipDto> internshipDtoPage = internshipController.getAllInternships(PageRequest.of(0, 10));

        assertEquals(1, internshipDtoPage.getTotalElements());
        verify(internshipService, times(1)).getAllInternships(any(Pageable.class));
    }

    @Test
    void testGetInternshipById_ShouldReturnInternship() {
        InternshipDto internshipDto = new InternshipDto();
        internshipDto.setId(1L);
        when(internshipService.getInternshipById(anyLong())).thenReturn(internshipDto);

        InternshipDto internshipById = internshipController.getInternshipById(1L);

        assertEquals(internshipDto, internshipById);
        verify(internshipService, times(1)).getInternshipById(anyLong());
    }
}

