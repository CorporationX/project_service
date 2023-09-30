package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.PaymentResponse;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.PaymentException;
import faang.school.projectservice.model.Campaign;
import faang.school.projectservice.model.CampaignStatus;
import org.springframework.stereotype.Component;

@Component
public class DonationValidator {
    public void validateCampaign(Campaign campaign) {
        if (campaign == null) {
            throw new EntityNotFoundException("Campaign not found");
        }
        if (!campaign.getStatus().equals(CampaignStatus.ACTIVE)) {
            throw new EntityNotFoundException("Campaign is finished");
        }
    }

    public void validatePaymentResponse(PaymentResponse paymentResponse, long paymentNumber) {
        if (!paymentResponse.status().equals("success") && paymentResponse.paymentNumber() != paymentNumber) {
            throw new PaymentException("Payment failed or payment number does not match");
        }
    }
}
