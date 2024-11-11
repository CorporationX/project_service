package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.filter.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.service.internship.InternshipService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InternshipControllerTest {

    @InjectMocks
    private InternshipController internshipController;
    @Mock
    private InternshipService internshipService;
    private InternshipDto internshipDto;
    private static final long INTERNSHIP_ID_IS_ONE = 1L;
    private static final String INTERNSHIP_NAME = "ABC Internship";

    @Test
    @DisplayName("Controllers calls service.create one time return saved dto")
    public void whenControllerCallsServiceCreateOneTimeThenReturnSavedDto() {
        internshipDto = InternshipDto.builder()
                .id(INTERNSHIP_ID_IS_ONE)
                .build();
        InternshipDto internshipDtoResult = InternshipDto.builder()
                .id(INTERNSHIP_ID_IS_ONE)
                .build();

        when(internshipService.create(internshipDto)).thenReturn(internshipDto);
        internshipController.createInternship(internshipDto);
        verify(internshipService)
                .create(internshipDto);

        assertEquals(internshipDtoResult, internshipDto);
    }

    @Test
    @DisplayName("Controller calls service.updateCampaign one time and return updated dto")
    public void whenControllerCallsServiceUpdateOneTimeThenReturnUpdatedDto() {
        internshipDto = InternshipDto.builder()
                .id(INTERNSHIP_ID_IS_ONE)
                .name(INTERNSHIP_NAME)
                .build();
        InternshipDto updatedInternshipDto = InternshipDto.builder()
                .id(INTERNSHIP_ID_IS_ONE)
                .name(INTERNSHIP_NAME)
                .build();

        when(internshipService.update(internshipDto)).thenReturn(updatedInternshipDto);
        internshipController.updateInternship(internshipDto);
        verify(internshipService)
                .update(internshipDto);
    }

    @Test
    @DisplayName("Controller calls service.getFilteredInternship one time and return filtered dto list")
    public void whenControllerCallsServiceGetFilteredInternshipOneTimeThenReturnFilteredDtoList() {
        InternshipFilterDto filterDto = InternshipFilterDto.builder().build();
        List<InternshipDto> internships = List.of(InternshipDto.builder().build());

        when(internshipService.getFilteredInternship(filterDto)).thenReturn(internships);
        internshipController.filterInternship(filterDto);
        verify(internshipService)
                .getFilteredInternship(filterDto);

        assertEquals(1, internships.size());
    }

    @Test
    @DisplayName("Controllers calls service.getAllInternships and return list of dtos")
    public void whenControllerCallsServiceGetAllInternshipOneTimeThenReturnDtoList() {
        List<InternshipDto> internships = List.of(InternshipDto.builder().build(), InternshipDto.builder().build());
        when(internshipService.getAllInternship()).thenReturn(internships);
        internshipController.getInternships();
        verify(internshipService)
                .getAllInternship();

        assertEquals(2, internships.size());
    }

    @Test
    @DisplayName("Controller calls service.getInternship and returns one Dto by it's id")
    public void whenControllerCallsServiceGetInternshipOneTimeThenReturnDto() {
        when(internshipService.getInternshipById(INTERNSHIP_ID_IS_ONE)).thenReturn(internshipDto);
        internshipController.getInternship(INTERNSHIP_ID_IS_ONE);
        verify(internshipService)
                .getInternshipById(INTERNSHIP_ID_IS_ONE);
    }
}