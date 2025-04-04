package faang.school.projectservice.validator.campaign;

import faang.school.projectservice.dto.client.Campaign.CampaignDto;
import faang.school.projectservice.exception.ForbiddenException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CampaignValidator {

    public static void checkForbiddenValue(CampaignDto campaignDto, long updaterId) {
        if (campaignDto.getId() != null ||
                campaignDto.getCreatedBy() != null ||
                campaignDto.getCreatedAt() != null ||
                campaignDto.getAmountRaised() != null ||
                campaignDto.getGoal() != null ||
                campaignDto.getCurrency() != null)
        {
            log.error(ErrorMessages.FORBIDDEN_VALUE_ID.getMessage(), updaterId);
            throw new ForbiddenException(ErrorMessages.FORBIDDEN_VALUE.getMessage());
        }
    }

    public static void checkPermittedValue(CampaignDto campaignDto, long updaterId) {
        if (campaignDto.getTitle() != null && campaignDto.getTitle().isBlank()) {
            log.error(ErrorMessages.TITLE_EMPTY_ID.getMessage(), updaterId);
            throw new IllegalArgumentException(ErrorMessages.TITLE_EMPTY.getMessage());
        }

        if (campaignDto.getDescription() != null && campaignDto.getDescription().isBlank()) {
            log.error(ErrorMessages.DESCRIPTION_EMPTY_ID.getMessage(), updaterId);
            throw new IllegalArgumentException(ErrorMessages.DESCRIPTION_EMPTY.getMessage());
        }
    }
}
