package faang.school.projectservice.util.converter;

import org.springframework.stereotype.Component;

@Component
public class GigabyteConverter {
    private static final int POWER_OF_THREE = 3;
    private static final int THOUSAND_BYTES = 1000;
    public long byteToGigabyteConverter(long size) {
        return Math.round(size / Math.pow(THOUSAND_BYTES, POWER_OF_THREE));
    }
}
