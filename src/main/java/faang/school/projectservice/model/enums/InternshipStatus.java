package faang.school.projectservice.model.enums;

import lombok.Getter;

@Getter
public enum InternshipStatus {

    COMPLETED(null),
    IN_PROGRESS(COMPLETED),
    PREPARATION(IN_PROGRESS);

    private final InternshipStatus nextStatus;

    InternshipStatus(InternshipStatus nextStatus) {
        this.nextStatus = nextStatus;
    }
}
