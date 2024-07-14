package faang.school.projectservice.subproject;

import java.util.ArrayList;
import java.util.List;
import faang.school.projectservice.controller.subproject.SubProjectController;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.SubProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class SubProjectControllerTest {

    @InjectMocks
    private SubProjectController controller;
    @Mock
    private ProjectService service;
    @Captor
    private ArgumentCaptor<ProjectDto> captorDto;
    @Captor
    private ArgumentCaptor<UpdateSubProjectDto> captorUpd;
    @Captor
    private ArgumentCaptor<SubProjectFilterDto> captorFilter;
    private ProjectDto dto;
    private ProjectDto expectedDto;
    private UpdateSubProjectDto updateSubProjectDto;
    private UpdateSubProjectDto expectedUpdateSubProjectDto;
    private SubProjectFilterDto filterDto;
    private SubProjectFilterDto expectedFilterDto;



    @BeforeEach
    public void setup() {
        dto = new ProjectDto();
        dto.setId(10L);
        dto.setVisibility(ProjectVisibility.PRIVATE);
        dto.setStatus(ProjectStatus.CREATED);
        dto.setName("Проект");

        expectedDto = new ProjectDto();
        expectedDto.setId(10L);
        expectedDto.setVisibility(ProjectVisibility.PRIVATE);
        expectedDto.setStatus(ProjectStatus.CREATED);
        expectedDto.setName("Проект");

        updateSubProjectDto = new UpdateSubProjectDto();
        updateSubProjectDto.setVisibility(ProjectVisibility.PRIVATE);
        updateSubProjectDto.setStatus(ProjectStatus.CREATED);
        expectedUpdateSubProjectDto = new UpdateSubProjectDto();
        expectedUpdateSubProjectDto.setVisibility(ProjectVisibility.PRIVATE);
        expectedUpdateSubProjectDto.setStatus(ProjectStatus.CREATED);

        filterDto = new SubProjectFilterDto();
        filterDto.setStatus(ProjectStatus.CREATED);
        filterDto.setNamePattern("Проект 1");
        expectedFilterDto = new SubProjectFilterDto();
        expectedFilterDto.setStatus(ProjectStatus.CREATED);
        expectedFilterDto.setNamePattern("Проект 1");
    }


    @Test
    public void testCreateSubProjectNull() {
        ProjectDto dto = null;

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> controller.createSubProject(dto));
        Assertions.assertEquals("Подпроект не может быть пустым", exception.getMessage());
    }

    @Test
    public void testCreateSubProjectIdNull() {
        ProjectDto dto = new ProjectDto();

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> controller.createSubProject(dto));
        Assertions.assertEquals("Подпроект не может иметь пустой ID", exception.getMessage());
    }

    @Test
    public void testCreateSubProjectArgService() {
        service.createSubProject(dto);
        verify(service, times(1)).createSubProject(captorDto.capture());
        Assertions.assertEquals(expectedDto, captorDto.getValue());

    }

    @Test
    public void testCreateSubProjectReturn() {
        when(service.createSubProject(dto)).thenReturn(dto);
        ProjectDto returnDto = controller.createSubProject(dto);
        Assertions.assertEquals(expectedDto, returnDto);
    }

    @Test
    public void testUpdateSubProjectNull() {
        ProjectDto dto = null;

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            controller.updateSubProject(dto, null);
        });
        Assertions.assertEquals("Подпроект не может быть пустым", exception.getMessage());
    }

    @Test
    public void testUpdateSubProjectIdNull() {
        ProjectDto dto = new ProjectDto();

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            controller.updateSubProject(dto, null);
        });
        Assertions.assertEquals("Подпроект не может иметь пустой ID", exception.getMessage());
    }

    @Test
    public void testUpdateSubProjectArgService() {
        controller.updateSubProject(dto, updateSubProjectDto);
        verify(service, times(1)).updateSubProject(captorDto.capture(), captorUpd.capture());
        Assertions.assertEquals(expectedDto, captorDto.getValue());
        Assertions.assertEquals(expectedUpdateSubProjectDto, captorUpd.getValue());

    }

    @Test
    public void testUpdateSubProjectReturn() {
        when(controller.updateSubProject(dto, updateSubProjectDto)).thenReturn(expectedDto);
        ProjectDto returnDto = controller.updateSubProject(dto, updateSubProjectDto);
        Assertions.assertEquals(expectedDto, returnDto);
    }

    @Test
    public void testGetSubProjectNull() {
        ProjectDto dto = null;

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            controller.getSubProject(dto, null);
        });
        Assertions.assertEquals("Подпроект не может быть пустым", exception.getMessage());
    }

    @Test
    public void testGetSubProjectIdNull() {
        ProjectDto dto = new ProjectDto();

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            controller.getSubProject(dto, null);
        });
        Assertions.assertEquals("Подпроект не может иметь пустой ID", exception.getMessage());
    }

    @Test
    public void testGetSubjectProjectArgService() {
        controller.getSubProject(dto, filterDto);
        verify(service, times(1)).getSubProject(captorDto.capture(), captorFilter.capture());
        Assertions.assertEquals(expectedDto, captorDto.getValue());
        Assertions.assertEquals(expectedFilterDto, captorFilter.getValue());
    }

    @Test
    public void testGetSubjectProjectReturn() {
        ProjectDto dto1 = new ProjectDto();
        dto1.setId(10L);
        dto1.setName("Подпроект1");
        dto1.setStatus(ProjectStatus.CREATED);
        ProjectDto dto2 = new ProjectDto();
        dto2.setId(10L);
        dto2.setName("Подпроект1");
        dto2.setStatus(ProjectStatus.CREATED);

        List<ProjectDto> dtos = new ArrayList<>(List.of(dto1, dto2));
        List<ProjectDto> expectedDtos = new ArrayList<>(List.of(dto1, dto2));
        when(service.getSubProject(dto, filterDto)).thenReturn(dtos);
        List<ProjectDto> returnDtos = service.getSubProject(dto, filterDto);
        Assertions.assertEquals(expectedDtos, returnDtos);
    }
}
