package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.DataValidateException;
import faang.school.projectservice.model.Internship;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InternshipValidationToolTest {
    @InjectMocks
    private InternshipValidationTool validationTool;
    @Mock
    private InternshipDtoValidator validator;

    private InternshipContainer container = new InternshipContainer();

    @Test
    void testValidationBeforeCreate() {
        // given
        Long id = container.internshipId();
        InternshipDto dtoWithInvalidMentor = InternshipDto.builder()
                .id(id++)
                .build();
        doThrow(DataValidateException.class).when(validator).validateMentorIsMember(dtoWithInvalidMentor);

        InternshipDto dtoWithInvalidDate = InternshipDto.builder()
                .id(id++)
                .build();
        doThrow(DataValidateException.class).when(validator).validateStartEndDate(dtoWithInvalidDate);

        InternshipDto validDto = InternshipDto.builder()
                .id(id)
                .build();


        // then
        assertThrows(DataValidateException.class, () -> validationTool.validationBeforeCreate(dtoWithInvalidMentor));
        assertThrows(DataValidateException.class, () -> validationTool.validationBeforeCreate(dtoWithInvalidDate));
        assertDoesNotThrow(() -> validationTool.validationBeforeCreate(validDto));
    }

    @Test
    void testValidationBeforeUpdateWithChangedInterns() {
        // given
        InternshipDto dtoWithChangedInterns = InternshipDto.builder()
                .id(container.internshipId())
                .build();

        Internship entity = Internship.builder()
                .id(container.internshipId())
                .build();

        doThrow(DataValidateException.class).when(validator).checkChangeInterns(dtoWithChangedInterns, entity);

        // then
        assertThrows(DataValidateException.class, () -> validationTool.validationBeforeUpdate(dtoWithChangedInterns, entity));
    }

    @Test
    void testValidationBeforeUpdateWithChangedMentor() {
        // given
        Long changeMentorId = container.mentorId() + 1;

        InternshipDto dtoWithChangedMentor = InternshipDto.builder()
                .id(container.internshipId())
                .mentorId(changeMentorId)
                .startDate(container.startDate())
                .endDate(container.endDate())
                .build();

        InternshipDto validDto = InternshipDto.builder()
                .id(container.internshipId())
                .mentorId(container.mentorId())
                .startDate(container.startDate())
                .endDate(container.endDate())
                .build();

        Internship entity = Internship.builder()
                .id(container.internshipId())
                .mentor(container.mentor())
                .startDate(container.startDate())
                .endDate(container.endDate())
                .build();

        // when
        validationTool.validationBeforeUpdate(dtoWithChangedMentor, entity);
        validationTool.validationBeforeUpdate(validDto, entity);

        // then
        verify(validator, times(1)).validateMentorIsMember(dtoWithChangedMentor);
        verify(validator, times(0)).validateMentorIsMember(validDto);
    }

    @Test
    void testValidationBeforeUpdateWithChangedDate() {
        // given
        InternshipDto dtoWithChangedStartDate = InternshipDto.builder()
                .id(container.internshipId())
                .mentorId(container.mentorId())
                .startDate(container.startDate().plusDays(1))
                .build();

        InternshipDto dtoWithChangedEndDate = InternshipDto.builder()
                .id(container.internshipId())
                .mentorId(container.mentorId())
                .startDate(container.startDate())
                .endDate(container.endDate().plusDays(1))
                .build();

        InternshipDto unchangedDto = InternshipDto.builder()
                .id(container.internshipId())
                .mentorId(container.mentorId())
                .startDate(container.startDate())
                .endDate(container.endDate())
                .build();

        Internship entity = Internship.builder()
                .id(container.internshipId())
                .mentor(container.mentor())
                .startDate(container.startDate())
                .endDate(container.endDate())
                .build();

        // when
        validationTool.validationBeforeUpdate(dtoWithChangedEndDate, entity);
        validationTool.validationBeforeUpdate(unchangedDto, entity);

        // then
        assertThrows(DataValidateException.class, () -> validationTool.validationBeforeUpdate(dtoWithChangedStartDate, entity));
        verify(validator, times(1)).validateStartEndDate(dtoWithChangedEndDate);
        verify(validator, times(0)).validateStartEndDate(unchangedDto);
        assertDoesNotThrow(() -> validationTool.validationBeforeUpdate(unchangedDto, entity));
    }

    @Test
    void validationStatus() {
        // given
        InternshipDto dtoCompleted = InternshipDto.builder()
                .id(container.internshipId())
                .status(container.statusCompleted())
                .build();

        Internship entityCompleted = Internship.builder()
                .id(container.internshipId())
                .status(container.statusCompleted())
                .build();

        Internship entityInProgress = Internship.builder()
                .id(container.internshipId())
                .status(container.statusInProgress())
                .build();

        boolean checkResult = true;

        doThrow(DataValidateException.class).when(validator).checkCompletedStatus(dtoCompleted, entityCompleted);
        when(validator.checkCompletedStatus(dtoCompleted, entityInProgress)).thenReturn(checkResult);

        // when
        validationTool.validationStatus(dtoCompleted, entityInProgress);

        // then
        assertThrows(DataValidateException.class, () -> validationTool.validationStatus(dtoCompleted, entityCompleted));
        verify(validator, times(1)).checkCompletedStatus(dtoCompleted, entityInProgress);
    }
}