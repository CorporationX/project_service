package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.service.InternshipService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class InternshipControllerTest {
    @InjectMocks
    private InternshipController controller;
    @Mock
    private InternshipService service;

    @Test
    public void testPositiveCreateInternship() {
        InternshipDto internshipDto = InternshipDto.builder().build();
        service.createInternship(internshipDto);
        verify(service, times(1)).createInternship(internshipDto);
    }

    @Test
    public void testPositiveFindInternshipsByFilter() {
        InternshipFilterDto filter = InternshipFilterDto.builder().build();
        service.getInternshipsFiltered(filter);
        verify(service, times(1)).getInternshipsFiltered(filter);
    }

    @Test
    public void testPositiveGetInternshipById() {
        Long id = 1L;
        service.getInternshipById(id);
        verify(service, times(1)).getInternshipById(id);
    }

    @Test
    public void testPositiveUpdateInternship() {
        Long id = 1L;
        InternshipDto internshipDto = InternshipDto.builder().build();
        service.updateInternship(internshipDto, id);
        verify(service, times(1)).updateInternship(internshipDto, id);
    }

    @Test
    public void testPositiveFindAllInternships() {
        service.getAllInternships();
        verify(service, times(1)).getAllInternships();

    }


}
