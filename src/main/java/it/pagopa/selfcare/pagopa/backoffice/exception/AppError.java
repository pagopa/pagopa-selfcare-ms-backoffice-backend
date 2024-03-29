package it.pagopa.selfcare.pagopa.backoffice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Enum that define the application error codes
 */
@Getter
@AllArgsConstructor
public enum AppError {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Something was wrong"),
    BAD_REQUEST(HttpStatus.INTERNAL_SERVER_ERROR, "Bad Request", "%s"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized", "Error during authentication"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Forbidden", "This method is forbidden"),
    RESPONSE_NOT_READABLE(HttpStatus.BAD_GATEWAY, "Response Not Readable", "The response body is not readable"),
    BAD_GATEWAY(HttpStatus.BAD_GATEWAY, "Error with external dependency", "%s"),
    SELFCARE_ROLE_NOT_FOUND(HttpStatus.BAD_GATEWAY, "SelfCare Role Not Found", "Unexpected role %s from Selfcare response"),

    ACTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "Actor Not Found", "No Broker and No CreditorInstitution found with code %s"),
    APIM_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "APIM User Not Found", "No User found with code %s"),
    APIM_KEY_NOT_FOUND(HttpStatus.NOT_FOUND, "APIM keys Not Found", "No API keys found with code %s"),
    AUTHORIZATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Authorization Not Found", "No Authorization found with code %s"),
    BROKER_NOT_FOUND(HttpStatus.NOT_FOUND, "Broker Not Found", "No broker found with code %s"),
    WRAPPER_NOT_FOUND(HttpStatus.NOT_FOUND, "Wrapper Entity not Found", "No wrapper entity found with code %s"),
    WRAPPER_STATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Wrapper Station not Found", "No wrapper station found with code %s"),
    WRAPPER_CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "Wrapper Channel not Found", "No wrapper channel found with code %s"),
    CREDITOR_INSTITUTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Creditor institution Not Found", "No creditor institution found with code %s"),
    OPERATIVE_TABLE_NOT_FOUND(HttpStatus.NOT_FOUND, "Operative Table Not Found", "No operative table found with code %s"),

    PSP_CODE_NOT_FOUND(HttpStatus.BAD_REQUEST, "Psp Code Not Found", "No psp code found for taxCode %s"),

    STATION_CONFLICT(HttpStatus.CONFLICT, "Station Conflict", "There is a Station not completed."),

    UNKNOWN(null, null, null);

    public final HttpStatus httpStatus;
    public final String title;
    public final String details;

}
