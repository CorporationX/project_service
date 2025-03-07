package faang.school.projectservice.validations.validator;

import faang.school.projectservice.dto.vacancy.VacancyCreateDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.validations.annotations.IsCreateDataValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
    public class IsCreateDataValidValidator implements ConstraintValidator<IsCreateDataValid, VacancyCreateDto> {
    public VacancyCreateDto isDataValid(VacancyCreateDto vacancyDto) {
        if (vacancyDto.getPositionId() == null
                || vacancyDto.getRoleId() == null) {
            throw new NullPointerException("You are use illegal data: position and project must be not null");
        } else if (vacancyDto.getRoleId() != TeamRole.getAll().get(0).ordinal()
                && vacancyDto.getRoleId() != TeamRole.getAll().get(1).ordinal()) {
            throw new IllegalArgumentException("You are use illegal data: curator must be OWNER or MANAGER");
        }
        return vacancyDto;
    }

        @Override
        public boolean isValid(VacancyCreateDto dto, ConstraintValidatorContext context) {
            isDataValid(dto);
            return true;
    }}



