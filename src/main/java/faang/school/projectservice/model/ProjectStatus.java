package faang.school.projectservice.model;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum ProjectStatus {
    @JsonEnumDefaultValue
    CREATED,
    IN_PROGRESS,
    COMPLETED,
    ON_HOLD,
    CANCELLED,
}