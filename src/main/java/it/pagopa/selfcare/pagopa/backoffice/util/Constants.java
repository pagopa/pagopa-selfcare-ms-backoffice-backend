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



}
