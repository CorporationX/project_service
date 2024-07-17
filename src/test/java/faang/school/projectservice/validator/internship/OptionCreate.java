package faang.school.projectservice.validator.internship;

public enum OptionCreate {
    WO_PROJECT_ID,
    WO_INTERN_IDS,
    WO_MENTOR_ID,
    INVALID_DURATION,
    START_DATE_NULL,
    END_DATE_NULL,
    END_DATE_BEFORE_START,
    END_DATE_BEFORE_NOW
}
