package faang.school.projectservice.utils.validations_utils;

import faang.school.projectservice.dto.campaign.CampaignCreateDto;
import faang.school.projectservice.dto.campaign.CampaignUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class CampaignValidator {

    private static final int TITLE_MAX_LENGTH = 128;
    private static final int DESCRIPTION_MAX_LENGTH = 4096;
    private static final BigDecimal GOAL_MIN_DECIMAL = BigDecimal.valueOf(0.01);
    public static final String CAMPAIGN_NULL_EXCEPTION = "Campaign dto can't be null";
    public static final String TITLE_NULL_EXCEPTION = "The title can't be null";
    public static final String TITLE_EMPTY_EXCEPTION = "The title can't be empty";
    public static final String PROJECT_ID_NULL_EXCEPTION = "Project id can't be null";
    public static final String GOAL_MIN_DECIMAL_EXCEPTION = "Goal must be at least " + GOAL_MIN_DECIMAL;
    public static final String TITLE_MAX_LENGTH_EXCEPTION = "The length of title can't be more than "
            + TITLE_MAX_LENGTH;
    public static final String DESCRIPTION_MAX_LENGTH_EXCEPTION = "The length of description can't be more than "
            + DESCRIPTION_MAX_LENGTH;

    public static void validationCampaignDto(CampaignCreateDto dto) {
        validateCampaignTittle(dto.getTitle());
        validateCampaignDescription(dto.getDescription());
        validateGoal(dto.getGoal());
        validateProjectId(dto.getProjectId());
    }

    public static void validateCampaignUpdateDto(CampaignUpdateDto dto) {
        validateCampaignUpdateNull(dto);
        validateCampaignTittle(dto.getTitle());
        validateCampaignDescription(dto.getDescription());
    }

    private static void validateCampaignUpdateNull(CampaignUpdateDto dto) {
        if (dto == null) {
            log.info(CAMPAIGN_NULL_EXCEPTION);
            throw new DataValidationException(CAMPAIGN_NULL_EXCEPTION);
        }
    }
    
    private static void validateCampaignTittle(String title) {
        if (title == null) {
            log.info(TITLE_NULL_EXCEPTION);
            throw new DataValidationException(TITLE_NULL_EXCEPTION);
        }

        if (title.isBlank()) {
            log.info(TITLE_EMPTY_EXCEPTION);
            throw new DataValidationException(TITLE_EMPTY_EXCEPTION);
        }

        if (title.length() > TITLE_MAX_LENGTH) {
            log.info(TITLE_MAX_LENGTH_EXCEPTION);
            log.info("Length of title: {}", title.length());
            throw new DataValidationException(TITLE_MAX_LENGTH_EXCEPTION);
        }
    }

    private static void validateCampaignDescription(String desc) {
        if (desc.length() > DESCRIPTION_MAX_LENGTH) {
            log.info(DESCRIPTION_MAX_LENGTH_EXCEPTION);
            throw new DataValidationException(DESCRIPTION_MAX_LENGTH_EXCEPTION);
        }
    }

    private static void validateGoal(BigDecimal goal) {
        if (goal.compareTo(GOAL_MIN_DECIMAL) < 0) {
            log.info(GOAL_MIN_DECIMAL_EXCEPTION);
            throw new DataValidationException(GOAL_MIN_DECIMAL_EXCEPTION);
        }
    }

    private static void validateProjectId(Long projectId) {
        if (projectId == null) {
            log.info(PROJECT_ID_NULL_EXCEPTION);
            throw new DataValidationException(PROJECT_ID_NULL_EXCEPTION);
        }
    }
}
