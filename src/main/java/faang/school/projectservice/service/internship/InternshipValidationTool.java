package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.DataValidateException;
import faang.school.projectservice.model.Internship;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class InternshipValidationTool {
    private final InternshipDtoValidator validator;

    public void validationBeforeCreate(InternshipDto dto) {
        validator.validateMentorIsMember(dto);
        validator.validateStartEndDate(dto);
    }

    public boolean validationStatus(InternshipDto dto, Internship entity) {
        return validator.checkCompletedStatus(dto, entity);
    }

    public void validationBeforeUpdate(InternshipDto dto, Internship entity) {
        validator.checkChangeInterns(dto, entity);
        checkChangeMentor(dto, entity);
        checkChangeDate(dto, entity);
    }

    private void checkChangeMentor(InternshipDto dto, Internship entity) {
        if (!Objects.equals(dto.getMentorId(), entity.getMentor().getId())) {
            validator.validateMentorIsMember(dto);
        }
    }

    private void checkChangeDate(InternshipDto dto, Internship entity) {
        LocalDateTime startDto = dto.getStartDate();
        LocalDateTime endDto = dto.getEndDate();
        LocalDateTime startEntity = entity.getStartDate();
        LocalDateTime endEntity = entity.getEndDate();
        if (!Objects.equals(startDto, startEntity)) {
            throw new DataValidateException("Менять дату начала стажировки нельзя.");
        }
        if (!Objects.equals(endDto, endEntity)) {
            validator.validateStartEndDate(dto);
        }
    }
}
