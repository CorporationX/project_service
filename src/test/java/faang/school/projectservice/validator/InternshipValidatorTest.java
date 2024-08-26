package faang.school.projectservice.validator;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.IllegalEntityException;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.internship.InternshipValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InternshipValidatorTest {

    @InjectMocks
    private InternshipValidator internshipValidator;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    private int days;
    private int validInternshipDuration;
    private int invalidInternshipDuration;
    private InternshipDto internshipDto;

    @BeforeEach
    void setup() {
        long projectId = 1;
        long mentorId = 1;
        days = 1;
        validInternshipDuration = 3;
        invalidInternshipDuration = 4;
        internshipDto = InternshipDto.builder()
            .projectId(projectId)
            .mentorId(mentorId)
            .build();
    }

    @Test
    @DisplayName("Should validate DTO successfully when mentor and project are found")
    void validateDto_Success() {
        when(teamMemberRepository.existsById(anyLong())).thenReturn(true);
        when(projectRepository.existsById(anyLong())).thenReturn(true);

        assertDoesNotThrow(() -> internshipValidator.validateDto(internshipDto));

        verify(teamMemberRepository).existsById(anyLong());
        verify(projectRepository).existsById(anyLong());
    }

    @Test
    @DisplayName("Should throw exception when mentor is not found")
    void validateDto_MentorNotFound_ThrowsException() {
        when(teamMemberRepository.existsById(internshipDto.mentorId())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> internshipValidator.validateDto(internshipDto));

        verify(teamMemberRepository).existsById(internshipDto.mentorId());
    }

    @Test
    @DisplayName("Should throw exception when project is not found")
    void validateDto_ProjectNotFound_ThrowsException() {
        when(teamMemberRepository.existsById(internshipDto.mentorId())).thenReturn(true);
        when(projectRepository.existsById(internshipDto.projectId())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> internshipValidator.validateDto(internshipDto));

        verify(teamMemberRepository).existsById(internshipDto.mentorId());
        verify(projectRepository).existsById(internshipDto.projectId());
    }

    @Test
    @DisplayName("Should validate internship duration successfully when within limit")
    void validateInternshipDuration_Success() {
        internshipDto = InternshipDto.builder()
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now().plusMonths(validInternshipDuration))
            .build();

        assertDoesNotThrow(() -> internshipValidator.validateInternshipDuration(internshipDto));
    }

    @Test
    @DisplayName("Should throw exception when internship duration exceeds max duration")
    void validateInternshipDuration_DurationExceedsMax_ThrowsException() {
        internshipDto = InternshipDto.builder()
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now().plusMonths(invalidInternshipDuration))
            .build();

        assertThrows(IllegalEntityException.class, () -> internshipValidator.validateInternshipDuration(internshipDto));
    }

    @Test
    @DisplayName("Should validate internship has not started successfully")
    void validateInternshipNotStarted_NotStarted_Success() {
        internshipDto = InternshipDto.builder()
            .startDate(LocalDateTime.now().plusDays(days))
            .build();

        assertDoesNotThrow(() -> internshipValidator.validateInternshipNotStarted(internshipDto));
    }

    @Test
    @DisplayName("Should throw exception when internship has already started")
    void validateInternshipNotStarted_Started_ThrowsException() {
        internshipDto = InternshipDto.builder()
            .startDate(LocalDateTime.now().minusDays(days))
            .build();

        assertThrows(IllegalEntityException.class, () -> internshipValidator.validateInternshipNotStarted(internshipDto));
    }
}
