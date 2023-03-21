package it.pagopa.selfcare.pagopa.backoffice.connector.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static String validateAndReplace(String str, String replace) {
        String regex = "[a-zA-Z0-9-]+";
        String res = null;
        if (!str.matches(regex)) {
            res = str.replaceAll("[^a-zA-Z0-9- ]", replace);
        }
        return res;
    }

    public static String generator(List<String> codes, String retrievedCode) {
        Pattern pattern = Pattern.compile("^(.*?)(_([0-9]+))$"); // String_nn
        String newCode = retrievedCode.concat("_").concat("01");
        if (codes.isEmpty()) return newCode;
        Comparator<String> comparator = Comparator.comparingInt(s -> Integer.parseInt(s.split("_")[1]));
        Collections.sort(codes, comparator.reversed());
        String code = codes.get(0);

        Matcher matcher = pattern.matcher(code);
        if (matcher.matches()) {
            String prefix = matcher.group(1);
            String numberStr = matcher.group(3);
            int number = Integer.parseInt(numberStr);
            number++;
            newCode = prefix + String.format("_%0" + numberStr.length() + "d", number);
        }
        return newCode;
    }
}
