package it.pagopa.selfcare.pagopa.backoffice.connector.utils;

public class StringUtils {

    public static String validateAndReplace(String str, String replace) {
        String regex = "[a-zA-Z0-9-]+";
        String res = null;
        if (!str.matches(regex)) {
            res = str.replaceAll("[^a-zA-Z0-9- ]", replace);
        }
        return res;
    }
}