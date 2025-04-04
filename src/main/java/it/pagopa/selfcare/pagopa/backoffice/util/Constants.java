package it.pagopa.selfcare.pagopa.backoffice.util;

import lombok.experimental.UtilityClass;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

@UtilityClass
public class Constants {
    public static final String JWT_PROD_ISSUER = "https://api.platform.pagopa.it";


    public static final String HEADER_REQUEST_ID = "X-Request-Id";
    public static final String APIM_SUBSCRIPTION_KEY = "Ocp-Apim-Subscription-Key";

    public static final String REGEX_GENERATE = "^\\w+_\\d+$";

    public static final Marker CONFIDENTIAL_MARKER = MarkerFactory.getMarker("CONFIDENTIAL");

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String ZONED_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    public static final String BROKER_CODE_DB_FIELD = "brokerCode";

    public static final String PAGOPA_BROKER_CODE = "15376371009";

    public static final String PAGOPA_BACKOFFICE_PRODUCT_ID = "prod-pagopa";
    public static final String QUICKSIGHT_DASHBOARD_PRODUCT_ID = "prod-dashboard-psp";
}
