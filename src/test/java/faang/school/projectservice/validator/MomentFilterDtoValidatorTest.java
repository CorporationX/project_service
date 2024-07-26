package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.MomentFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class MomentFilterDtoValidatorTest {
    private MomentFilterDtoValidator validator;
    private MomentFilterDto dto;

    @BeforeEach
    public void setUp() {
        dto = new MomentFilterDto();
        validator = new MomentFilterDtoValidator();
    }

    @Test
    public void ValidateNullNameTest() {
        dto.setName(null);
        Assert.assertThrows(DataValidationException.class, () -> validator.validateMomentFilterDto(dto));
    }

    @Test
    public void ValidateEmptyNameTest() {
        dto.setName("");
        Assert.assertThrows(DataValidationException.class, () -> validator.validateMomentFilterDto(dto));
    }

    @Test
    public void ValidateNullIdTest() {
        dto.setId(null);
        Assert.assertThrows(DataValidationException.class, () -> validator.validateMomentFilterDto(dto));
    }

    @Test
    public void ValidateNegativeIdTest() {
        dto.setId(-1L);
        Assert.assertThrows(DataValidationException.class, () -> validator.validateMomentFilterDto(dto));
    }

}
