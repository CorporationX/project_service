package faang.school.projectservice.controller.project;

import faang.school.projectservice.controller.subproject.SubProjectController;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.FilterSubProjectDto;
import faang.school.projectservice.service.subproject.SubProjectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubProjectControllerTest {
    @InjectMocks
    private SubProjectController controller;

    @Mock
    private SubProjectService subProjectService;

    @Test
    public void testCreateSubProject(){
        CreateSubProjectDto subProjectDto = CreateSubProjectDto.builder()
                .name("Test").build();
        controller.createSubProject(1L,subProjectDto);
        verify(subProjectService,times(1)).createSubProject(1L,subProjectDto);
    }

    @Test
    public void testGetProjectsByFilters(){
        FilterSubProjectDto filterDto = FilterSubProjectDto.builder().build();
        subProjectService.getProjectsByFilter(1L, filterDto);
        verify(subProjectService,times(1)).getProjectsByFilter(1L, filterDto);
    }
}