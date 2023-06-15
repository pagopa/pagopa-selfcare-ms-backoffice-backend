package it.pagopa.selfcare.pagopa.backoffice.connector.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    private StringUtils() {

    }

    private static final String VALID_REGEX = "[a-zA-Z0-9-]+";

    public static String truncateString(String inputString, int maxLength) {
        if (inputString.length() <= maxLength) {
            return inputString;
        } else {
            return inputString.substring(0, maxLength);
        }
    }
    public static String validateAndReplace(String str, String replace) {
        if (str.matches(VALID_REGEX)) {
            return str;
        }
        return str.replaceAll("[^a-zA-Z0-9- ]", replace);
    }


    public static String generator(List<String> codes, String retrievedCode) {
        Pattern pattern = Pattern.compile("^(.*?)(_\\d+)$"); // String_nn
        String newCode = retrievedCode.concat("_").concat("01");
        if (codes.isEmpty()) return newCode;
        Comparator<String> comparator = Comparator.comparingInt(s -> Integer.parseInt(s.split("_")[1]));
        Collections.sort(codes, comparator.reversed());
        String code = codes.get(0);

        Matcher matcher = pattern.matcher(code);
        if (matcher.matches()) {
            String prefix = matcher.group(1);
            int number = Integer.parseInt(matcher.group(2).substring(1)) + 1;
            newCode = String.format("%s_%02d", prefix, number);
        }
        return newCode;
    }
}
