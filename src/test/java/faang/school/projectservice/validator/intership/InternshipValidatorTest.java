package faang.school.projectservice.validator.intership;

import faang.school.projectservice.model.dto.internship.InternshipDto;
import faang.school.projectservice.exception.IllegalEntityException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class InternshipValidatorTest {

    @InjectMocks
    private InternshipValidator internshipValidator;

    @Mock
    private ProjectJpaRepository projectJpaRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    private int days;
    private int validInternshipDuration;
    private int invalidInternshipDuration;
    private InternshipDto internshipDto;

    @BeforeEach
    void setUp() {
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
        doReturn(true).when(teamMemberRepository).existsById(anyLong());
        doReturn(true).when(projectJpaRepository).existsById(anyLong());

        assertThatNoException().isThrownBy(() -> internshipValidator.validateDto(internshipDto));

        verify(teamMemberRepository).existsById(anyLong());
        verify(projectJpaRepository).existsById(anyLong());
    }

    @Test
    @DisplayName("Should throw exception when mentor is not found")
    void validateDto_MentorNotFound_ThrowsException() {
        doReturn(false).when(teamMemberRepository).existsById(internshipDto.mentorId());

        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() ->
                internshipValidator.validateDto(internshipDto));

        verify(teamMemberRepository).existsById(anyLong());
        verifyNoInteractions(projectJpaRepository);
    }

    @Test
    @DisplayName("Should throw exception when project is not found")
    void validateDto_ProjectNotFound_ThrowsException() {
        doReturn(true).when(teamMemberRepository).existsById(internshipDto.projectId());
        doReturn(false).when(projectJpaRepository).existsById(internshipDto.projectId());

        assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() ->
                internshipValidator.validateDto(internshipDto));

        verify(teamMemberRepository).existsById(anyLong());
        verify(projectJpaRepository).existsById(anyLong());
    }

    @Test
    @DisplayName("Should validate internship duration successfully when within limit")
    void validateInternshipDuration_Success() {
        internshipDto = InternshipDto.builder()
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(validInternshipDuration))
                .build();

        assertThatNoException().isThrownBy(() ->
                internshipValidator.validateInternshipDuration(internshipDto));
    }

    @Test
    @DisplayName("Should throw exception when internship duration exceeds max duration")
    void validateInternshipDuration_ExceedMaxDuration_ThrowsException() {
        internshipDto = InternshipDto.builder()
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(invalidInternshipDuration))
                .build();

        assertThatExceptionOfType(IllegalEntityException.class).isThrownBy(() ->
                internshipValidator.validateInternshipDuration(internshipDto));
    }

    @Test
    @DisplayName("Should validate internship has not started successfully")
    void validateInternshipNotStarted_Success() {
        internshipDto = InternshipDto.builder()
                .startDate(LocalDateTime.now().plusDays(days))
                .build();

        assertThatNoException().isThrownBy(() ->
                internshipValidator.validateInternshipNotStarted(internshipDto));
    }

    @Test
    @DisplayName("Should throw exception when internship has already started")
    void validateInternshipNotStarted_ThrowsException() {
        internshipDto = InternshipDto.builder()
                .startDate(LocalDateTime.now().minusDays(days))
                .build();

        assertThatExceptionOfType(IllegalEntityException.class).isThrownBy(() ->
                internshipValidator.validateInternshipNotStarted(internshipDto));
    }
}