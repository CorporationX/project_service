package faang.school.projectservice.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomGenerator {

    private static final Random random = new Random();

    public long getRandomNumber(long min, long max) {
        return random.nextLong(min, max);
    }

}
