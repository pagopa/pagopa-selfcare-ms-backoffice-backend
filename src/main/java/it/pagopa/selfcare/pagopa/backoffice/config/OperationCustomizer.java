package it.pagopa.selfcare.pagopa.backoffice.config;

import io.swagger.v3.oas.models.Operation;
import it.pagopa.selfcare.pagopa.backoffice.util.OpenApiTableMetadata;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import static it.pagopa.selfcare.pagopa.backoffice.util.Utility.deNull;

@Component
public class OperationCustomizer implements org.springdoc.core.customizers.OperationCustomizer {

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        OpenApiTableMetadata annotation = handlerMethod.getMethodAnnotation(OpenApiTableMetadata.class);
        if(annotation != null) {
            String tableTemplate = """
                    Internal | External | Synchronous | Authorization | Authentication | TPS | Idempotency | Stateless | Read/Write Intense | Cacheable
                    -|-|-|-|-|-|-|-|-|-""";
            operation.description(tableTemplate + buildData(annotation) + "\n" + deNull(operation.getDescription()));
        }
        return operation;
    }

    private static String buildData(OpenApiTableMetadata annotation) {
        return parseBoolToYN(annotation.internal())
                + " | " + parseBoolToYN(annotation.external())
                + " | " + parseBoolToYN(annotation.synchronous())
                + " | " + annotation.authorization()
                + " | " + annotation.authentication()
                + " | " + annotation.tps() + "/sec"
                + " | " + parseBoolToYN(annotation.idempotency())
                + " | " + parseBoolToYN(annotation.stateless())
                + " | " + parseReadWrite(annotation.readWriteIntense())
                + " | " + parseBoolToYN(annotation.cacheable());
    }

    private static String parseReadWrite(OpenApiTableMetadata.ReadWrite readWrite) {
        return readWrite.getValue();
    }

    private static String parseBoolToYN(boolean value) {
        return value ? "Y" : "N";
    }
}
