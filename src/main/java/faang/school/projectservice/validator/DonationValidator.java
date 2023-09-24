package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.PaymentException;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;

public class DonationValidator {
    public void validateCampaign(Campaign campaign) {
        if (!campaign.getStatus().equals(CampaignStatus.ACTIVE)) {
            throw new EntityNotFoundException("Campaign is finished");
        }
    }

    public void validatePaymentResponse(PaymentResponse paymentResponse, long paymentNumber) {
        if (!paymentResponse.getStatus().equals("success") && paymentResponse.getPaymentNumber() != paymentNumber) {
            throw new PaymentException("Payment failed or payment number does not match");
        }
    }
}
