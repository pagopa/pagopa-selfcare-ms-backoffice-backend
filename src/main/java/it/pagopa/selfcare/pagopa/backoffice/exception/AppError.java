package it.pagopa.selfcare.pagopa.backoffice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AppError {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Something was wrong"),
    BAD_REQUEST(HttpStatus.INTERNAL_SERVER_ERROR, "Bad Request", "%s"),

    ACTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "Actor Not Found", "No Broker and No CreditorInstitution found with code %s"),
    APIM_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "APIM User Not Found", "No User found with code %s"),
    BROKER_NOT_FOUND(HttpStatus.NOT_FOUND, "Broker Not Found", "No broker found with code %s"),
    WRAPPER_NOT_FOUND(HttpStatus.NOT_FOUND, "Wrapper Entity not Found", "No wrapper entity found with code %s"),
    WRAPPER_STATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Wrapper Station not Found", "No wrapper station found with code %s"),
    WRAPPER_CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "Wrapper Channel not Found", "No wrapper channel found with code %s"),
    CREDITOR_INSTITUTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Creditor institution Not Found", "No creditor institution found with code %s"),
    OPERATIVE_TABLE_NOT_FOUND(HttpStatus.NOT_FOUND, "Operative Table Not Found", "No operative table found with code %s"),

    STATION_CONFLICT(HttpStatus.CONFLICT, "Station Conflict", "There is a Station not completed."),

    RESPONSE_NOT_READABLE(HttpStatus.BAD_GATEWAY, "Response Not Readable", "The response body is not readable"),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized", "Error during authentication"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Forbidden", "This method is forbidden"),
    UNKNOWN(null, null, null);

    public final HttpStatus httpStatus;
    public final String title;
    public final String details;

}
