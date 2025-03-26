package faang.school.projectservice.controller;

import faang.school.projectservice.dto.meet.CreateMeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.meet.MeetResponseDto;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.service.MeetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MeetControllerTest {

    @Mock
    private MeetService meetService;

    @InjectMocks
    private MeetController meetController;

    @Test
    void createMeetShouldReturnCreatedMeet() {

        CreateMeetDto createDto = new CreateMeetDto();
        MeetResponseDto expectedResponse = new MeetResponseDto();
        when(meetService.createMeet(createDto)).thenReturn(expectedResponse);
        MeetResponseDto response = meetController.createMeet(createDto);
        assertEquals(expectedResponse, response);
        verify(meetService).createMeet(createDto);
    }

    @Test
    void findAllShouldReturnAllMeets() {

        List<MeetResponseDto> expectedList = Collections.singletonList(new MeetResponseDto());
        when(meetService.findAll()).thenReturn(expectedList);
        List<MeetResponseDto> result = meetController.findAll();
        assertEquals(expectedList, result);
        verify(meetService).findAll();
    }

    @Test
    void findProjectMeetsByFilterShouldReturnFilteredMeets() {

        long projectId = 1L;
        MeetFilterDto filter = new MeetFilterDto();
        List<MeetResponseDto> expectedList = Collections.singletonList(new MeetResponseDto());
        when(meetService.findProjectMeetsByFilter(projectId, filter)).thenReturn(expectedList);
        List<MeetResponseDto> result = meetController.findProjectMeetsByFilter(projectId, filter);
        assertEquals(expectedList, result);
        verify(meetService).findProjectMeetsByFilter(projectId, filter);
    }

    @Test
    void findByIdShouldReturnMeet() {

        long meetId = 1L;
        MeetResponseDto expectedResponse = new MeetResponseDto();
        when(meetService.findById(meetId)).thenReturn(expectedResponse);
        MeetResponseDto result = meetController.findById(meetId);
        assertEquals(expectedResponse, result);
        verify(meetService).findById(meetId);
    }

    @Test
    void updateMeetShouldReturnUpdatedMeet() {

        UpdateMeetDto updateDto = new UpdateMeetDto();
        MeetResponseDto expectedResponse = new MeetResponseDto();
        when(meetService.updateMeet(updateDto)).thenReturn(expectedResponse);
        MeetResponseDto result = meetController.updateMeet(updateDto);
        assertEquals(expectedResponse, result);
        verify(meetService).updateMeet(updateDto);
    }

    @Test
    void cancelMeetShouldReturnCanceledMeet() {

        long meetId = 1L;
        MeetResponseDto expectedResponse = new MeetResponseDto();
        when(meetService.cancelMeet(meetId)).thenReturn(expectedResponse);
        MeetResponseDto result = meetController.cancelMeet(meetId);
        assertEquals(expectedResponse, result);
        verify(meetService).cancelMeet(meetId);
    }

    @Test
    void deleteMeetShouldCallService() {

        long meetId = 1L;
        meetController.deleteMeet(meetId);
        verify(meetService).deleteMeet(meetId);
    }
}