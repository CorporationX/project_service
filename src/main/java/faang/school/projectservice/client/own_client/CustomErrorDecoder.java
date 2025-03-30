package faang.school.projectservice.client.own_client;

import com.amazonaws.services.kms.model.NotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;

/*public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 404) {
            return new NotFoundException("User not found");
        }
        return new RuntimeException("Unknown error");
    }
}*/