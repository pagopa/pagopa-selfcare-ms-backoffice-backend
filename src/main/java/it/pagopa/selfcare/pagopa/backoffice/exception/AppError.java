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

    OUR_RESPONSE_NOT_VALID(HttpStatus.INTERNAL_SERVER_ERROR, "Response is not compliance with our Openapi", "%s"),
    DEPENDENCY_RESPONSE_NOT_VALID(HttpStatus.BAD_GATEWAY, "Dependency Response is not valid", "dependency %s: %s"),
    SELFCARE_ROLE_NOT_FOUND(HttpStatus.BAD_GATEWAY, "SelfCare Role Not Found", "Unexpected role %s for %s institution from Selfcare response"),

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
    MAINTENANCE_MESSAGES_NOT_FOUND(HttpStatus.NOT_FOUND, "Maintenance messages Not Found", "No maintenance messages found"),
    SUBSCRIPTION_PREFIX_NOT_FOUND(HttpStatus.NOT_FOUND, "Subscription Prefix Not Found", "No prefix found with code %s"),

    WRAPPER_STATION_INVALID_STATUS(HttpStatus.BAD_REQUEST, "Invalid wrapper Station status", "The requested operation cannot be done. Wrapper station with code %s is not in the expected status"),
    PSP_CODE_NOT_FOUND(HttpStatus.BAD_REQUEST, "Psp Code Not Found", "No psp code found for taxCode %s"),
    INVALID_GET_PUBLIC_CI_BUNDLES_REQUEST(HttpStatus.BAD_REQUEST, "Invalid get CI bundles request", "Creditor institution's tax code is required to retrieve creditor institution's public bundles"),
    INVALID_GET_PRIVATE_CI_BUNDLES_REQUEST(HttpStatus.BAD_REQUEST, "Invalid get CI bundles request", "Creditor institution's tax code (%s) and subscription status (%s) are required to retrieve creditor institution's private bundles"),
    BUNDLE_SUBSCRIPTION_BAD_REQUEST(HttpStatus.BAD_REQUEST, "Invalid bundle type requested", "Global bundle do not have bundle subscription"),
    BUNDLE_SUBSCRIPTION_TYPE_NULL_BAD_REQUEST(HttpStatus.BAD_REQUEST, "Invalid bundle type requested", "Bundle type is required to retrieve bundle subscription"),
    INSTITUTION_TYPE_NOT_ALLOWED_ON_SUBSCRIPTION_BAD_REQUEST(HttpStatus.BAD_REQUEST, "Subscription not allowed for institution", "Institution %s of type %s can not operate on the requested subscription %s"),

    INVALID_OPERATOR_GENERATE_PSP_DASHBOARD_REQUEST(HttpStatus.BAD_REQUEST, "Invalid generate PSP dashboard request for pagoPa operator", "pagoPa operator must provide an institution id for PSP dashboard retrieval"),

    STATION_CONFLICT(HttpStatus.CONFLICT, "Station Conflict", "There is a Station not completed."),

    INSTITUTION_DATA_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Error uploading institution data",
            "Error occured while attempting to upload institution data"),
    INSTITUTION_DATA_UPLOAD_BAD_REQUEST(HttpStatus.BAD_REQUEST, "Error uploading institution data",
            "Invalid input"),

    LOGO_FILE_INPUT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Error managing logo input",
            "Exception has been thrown while managing the logo file passed as input," +
                    " could not create either the working directory or the file"),

    INSTITUTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Institution Not Found",
            "Required institution data has not been found on the storage"),

    INSTITUTION_RETRIEVE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Error while retrieving Institution data",
            "Unexpected error occurred while retrieving institution data"),

    STATION_MAINTENANCE_NOT_IN_PROGRESS(HttpStatus.INTERNAL_SERVER_ERROR, "Station maintenance not in progress", "Station maintenance must be in progress in order to finish it"),

    PSP_APIM_KEY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Error with PSP APIM key", "The %s product APIM key require that the PSP code is configured but the PSP code for institution %s is missing"),

    BROKER_CI_EXPORT_SCHEDULER_SETUP_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Error in brokerCiExport cron setup", "%s"),
    BROKER_CI_EXPORT_SCHEDULER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Error during brokerCiExport cron execution", "%s"),
    BROKER_IBAN_EXPORT_SCHEDULER_SETUP_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Error in brokerIbansExport cron setup", "%s"),
    BROKER_IBAN_EXPORT_SCHEDULER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Error during brokerIbansExport cron execution", "%s"),

    UNKNOWN(null, null, null);

    public final HttpStatus httpStatus;
    public final String title;
    public final String details;

}
