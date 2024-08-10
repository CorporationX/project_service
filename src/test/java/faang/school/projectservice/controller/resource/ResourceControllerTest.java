package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ResourceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResourceControllerTest {
    @Mock
    private ResourceService resourceService;
    @InjectMocks
    private ResourceController resourceController;

    @Test
    void testAdd() {

        ResourceDto resourceDto = new ResourceDto();
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "some content".getBytes());
        when(resourceService.add(file, null, 1L)).thenReturn(resourceDto);

        ResourceDto result = resourceController.add(1L, "user123", null, file);
        assertEquals(resourceDto, result);
        verify(resourceService, times(1)).add(file, null, 1L);
    }

    @Test
    void testUpdate() {
        ResourceDto resourceDto = new ResourceDto();
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "some content".getBytes());
        when(resourceService.update(file, null, 1L, 1L, null)).thenReturn(resourceDto);

        ResourceDto result = resourceController.update(1L, 1L, "3", null, null, file);
        assertEquals(resourceDto, result);
        verify(resourceService, times(1)).update(file, null, 1L, 1L, null);
    }

    @Test
    void testGet() {
        ByteArrayInputStream inputStream = new ByteArrayInputStream("dummy content".getBytes());
        InputStreamResource resource = new InputStreamResource(inputStream);
        ResponseEntity<InputStreamResource> responseEntity = new ResponseEntity<>(resource, HttpStatus.OK);
        when(resourceService.get(anyLong(), anyLong())).thenReturn(responseEntity);

        ResponseEntity<InputStreamResource> result = resourceController.get(1L, "user123", 1L);
        assertEquals(responseEntity, result);
        verify(resourceService, times(1)).get(anyLong(), anyLong());
    }

    @Test
    void testRemove() {
        ResourceDto resourceDto = new ResourceDto();
        when(resourceService.remove(anyLong(), anyLong())).thenReturn(resourceDto);

        ResourceDto result = resourceController.remove(1L, "user123", 1L);
        assertEquals(resourceDto, result);
        verify(resourceService, times(1)).remove(anyLong(), anyLong());
    }
}

