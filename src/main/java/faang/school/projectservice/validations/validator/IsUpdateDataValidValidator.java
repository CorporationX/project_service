package faang.school.projectservice.validations.validator;


import faang.school.projectservice.dto.vacancy.VacancyUpdateDto;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.validations.annotations.IsUpdateDataValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IsUpdateDataValidValidator implements ConstraintValidator<IsUpdateDataValid, VacancyUpdateDto> {
    public VacancyUpdateDto isUpdateDataValid(VacancyUpdateDto vacancyDto) {
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
    public boolean isValid(VacancyUpdateDto dto, ConstraintValidatorContext context) {
        isUpdateDataValid(dto);
        return true;
    }}
