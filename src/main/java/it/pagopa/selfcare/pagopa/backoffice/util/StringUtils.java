package it.pagopa.selfcare.pagopa.backoffice.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class StringUtils {

    private static final String VALID_REGEX = "[a-zA-Z0-9-]+";

    private StringUtils() {

    }

    public static String truncateString(String inputString, int maxLength) {
        if(inputString.length() <= maxLength) {
            return inputString;
        } else {
            return inputString.substring(0, maxLength);
        }
    }

    public static String validateAndReplace(String str, String replace) {
        if(str.matches(VALID_REGEX)) {
            return str;
        }
        return str.replaceAll("[^a-zA-Z0-9- ]", replace);
    }

    public static String generator(Set<String> codes, String entityCode) {
        List<String> validCodes = new LinkedList<>();
        IntStream.range(1, 100).forEach(i -> {
            String newCode = entityCode.concat("_").concat(i <= 9 ? "0" + i : "" + i);
            if(!codes.contains(newCode)) {
                validCodes.add(newCode);
            }
        });
        return validCodes.get(0);
    }
}
