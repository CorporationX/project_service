package faang.school.projectservice.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CandidateStatus {
    ACCEPTED("Accepted", true),
    REJECTED("Rejected", false),
    WAITING_RESPONSE("Waiting response for", false);

    private final String actionText;
    private final boolean accepted;
}
