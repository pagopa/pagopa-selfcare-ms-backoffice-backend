package it.pagopa.selfcare.pagopa.backoffice.connector.logging;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class LogUtils {
    
    public static final Marker CONFIDENTIAL_MARKER = MarkerFactory.getMarker("CONFIDENTIAL");

}
