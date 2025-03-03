package it.pagopa.selfcare.pagopa.backoffice.util;

import it.pagopa.selfcare.pagopa.backoffice.model.SelfCareUser;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.station.StationDetails;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public class Utility {

    private Utility() {
    }

    public static String extractUserIdFromAuth(Authentication authentication) {
        String userIdForAuth = "";
        if(authentication != null && authentication.getPrincipal() instanceof SelfCareUser user) {
            userIdForAuth = user.getId();
        }
        return userIdForAuth;
    }

    public static String extractInstitutionIdFromAuth(Authentication authentication) {
        String institutionId = "";
        if(authentication != null && authentication.getPrincipal() instanceof SelfCareUser user) {
            institutionId = user.getOrgId();
        }
        return institutionId;
    }

    /**
     * @param value value to deNullify.
     * @return return empty string if value is null
     */
    public static String deNull(String value) {
        return Optional.ofNullable(value).orElse("");
    }

    /**
     * @param value value to deNullify.
     * @return return empty string if value is null
     */
    public static String deNull(Object value) {
        return Optional.ofNullable(value).orElse("").toString();
    }

    /**
     * @param value value to deNullify.
     * @return return false if value is null
     */
    public static Boolean deNull(Boolean value) {
        return Optional.ofNullable(value).orElse(false);
    }

    /**
     * @param headers header of the CSV file
     * @param rows    data of the CSV file
     * @return byte array of the CSV using commas (;) as separator
     */
    public static byte[] createCsv(List<String> headers, List<List<String>> rows) {
        var csv = new StringBuilder();
        csv.append(String.join(";", headers));
        rows.forEach(row -> csv.append(System.lineSeparator()).append(String.join(";", row)));
        return csv.toString().getBytes();
    }

    public static long getTimelapse(long startTime) {
        return Calendar.getInstance().getTimeInMillis() - startTime;
    }

    /**
     * Utility method to sanitize log params
     *
     * @param logParam log param to be sanitized
     * @return the sanitized param
     */
    public static String sanitizeLogParam(String logParam) {
        if (logParam.matches("\\w*-*")) {
            return logParam;
        }
        return "suspicious log param";
    }

    public static boolean isConnectionSync(StationDetails model) {
        return (org.apache.commons.lang3.StringUtils.isNotBlank(model.getTargetPath()) && org.apache.commons.lang3.StringUtils.isNotBlank(model.getRedirectIp()))
                || StringUtils.isNotBlank(model.getTargetPathPof());
    }

}
