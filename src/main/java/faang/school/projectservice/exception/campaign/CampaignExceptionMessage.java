package faang.school.projectservice.exception.campaign;

import faang.school.projectservice.model.CampaignStatus;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CampaignExceptionMessage {

    private static final String NOT_FOUND = "Campaign %d not found";
    private static final String CANCELED = "Campaign %d is canceled";
    private static final String COMPLETED = "Campaign %d already completed";
    private static final String UNKNOWN_STATUS = "Unknown campaign status - %s";

    public static String getNotFound(long id) {
        return String.format(NOT_FOUND, id);
    }

    public static String getCanceled(long id) {
        return String.format(CANCELED, id);
    }

    public static String getCompleted(long id) {
        return String.format(COMPLETED, id);
    }

    public static String getUnknownStatus(CampaignStatus status) {
        return String.format(UNKNOWN_STATUS, status);
    }
}
