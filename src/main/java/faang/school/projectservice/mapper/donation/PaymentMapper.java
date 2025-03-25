package faang.school.projectservice.mapper.donation;

import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.donation.DonationDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentRequest donationToPaymentRequest(DonationDto donationDto);
}
