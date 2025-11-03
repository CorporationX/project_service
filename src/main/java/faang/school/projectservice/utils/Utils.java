package faang.school.projectservice.utils;

import org.slf4j.helpers.MessageFormatter;

public final class Utils {
    public static String stringFormatting(String formattedString, Object... args) {
        return MessageFormatter.arrayFormat(formattedString, args).getMessage();
    }
}