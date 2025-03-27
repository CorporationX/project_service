package faang.school.projectservice.model.stage_invitation;

public enum StageInvitationStatus {
    PENDING(1L),
    REJECTED(2L),
    ACCEPTED(3L);

    private final Long id;

    StageInvitationStatus(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public static StageInvitationStatus fromId(Long id) {
        for (StageInvitationStatus status : values()) {
            if (status.getId().equals(id)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown StageInvitationStatus id: " + id);
    }
}
