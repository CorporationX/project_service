package faang.school.projectservice.controller.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.service.resource.ResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceControllerTest {

    @InjectMocks
    ResourceController  resourceController;

    @Mock
    ResourceService resourceService;

    private ResourceDto resourceDto;
    private MultipartFile mockFile;
    private Long id;
    private byte[] fileContent;
    private String filename;

    @BeforeEach
    void setUp() {
        id = 1L;
        mockFile = new MockMultipartFile(
                "file",
                "testfile.txt",
                "text/plain",
                "Sample content".getBytes()
        );
        resourceDto = new ResourceDto();
        fileContent = "File content".getBytes();
        filename = "filename";

    }

    @Test
    void addResource_validRequest_serviceCalled() {
        when(resourceService.addResource(id, mockFile)).thenReturn(resourceDto);
        resourceController.addResource(id, mockFile);
        verify(resourceService).addResource(id, mockFile);
    }

    @Test
    void deleteResource_validRequest_serviceCalled() {
        resourceController.deleteResource(id);
        verify(resourceService).deleteResource(id);
    }

    @Test
    void downloadResource_validRequest_returnsResource(){
        InputStream mockInputStream = new ByteArrayInputStream(fileContent);
        when(resourceService.downloadResource(id)).thenReturn(mockInputStream);
        when(resourceService.getResourceName(id)).thenReturn(filename);
        ResponseEntity<byte[]> response = resourceController.downloadResource(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(fileContent, response.getBody());
        HttpHeaders headers = response.getHeaders();
        assertEquals("application/octet-stream", headers.getContentType().toString());
        assertEquals("attachment; filename=\"" + filename + "\"", headers.getContentDisposition().toString());
        verify(resourceService).downloadResource(1L);
        verify(resourceService).getResourceName(1L);
    }

    @Test
    void downloadResource_errorDownloading_throwsException() {
        when(resourceService.downloadResource(id)).thenThrow(new RuntimeException("Error downloading resource"));
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> resourceController.downloadResource(1L));
        assertEquals("Error downloading resource", thrown.getMessage());
        verify(resourceService).downloadResource(1L);
        verify(resourceService, never()).getResourceName(id);
    }
}
