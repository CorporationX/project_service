package faang.school.projectservice.mapper.donation;

import faang.school.projectservice.dto.client.PaymentRequest;
import faang.school.projectservice.dto.donation.DonationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "targetCurrency", ignore = true)
    @Mapping(target = "paymentCurrency", source = "currency")
    PaymentRequest donationToPaymentRequest(DonationDto donationDto);
}
