package it.pagopa.selfcare.pagopa.backoffice.util;

import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanOperation;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class for IBAN CSV operations.
 * Contains pure functions for CSV generation and manipulation given a iban operation list.
 */
@Slf4j
@UtilityClass
public class IbanOperationsCsvUtil {

    /**
     * Converts a list of IBAN operations to a CSV MultipartFile.
     *
     * @param ciCode creditor institution code
     * @param operations List of IBAN operations
     * @return MultipartFile containing CSV data
     */
    public static MultipartFile convertOperationsToCsv(String ciCode, String ciName, List<IbanOperation> operations) {
        String sanitizedCiCodeForLogs = Utility.sanitizeLogParam(ciCode);

        String header = "iddominio,ragionesociale,descrizione,iban,dataattivazioneiban,operazione\n";

        String csvContent = operations.stream()
                .map( o -> operationToCsvRow(o, ciCode, ciName))
                .collect(Collectors.joining("\n", header, ""));

        byte[] csvBytes = csvContent.getBytes(StandardCharsets.UTF_8);

        log.info("Generated CSV content for CI {}: {} rows", sanitizedCiCodeForLogs, operations.size());

        return new InMemoryMultipartFile(
                "file",
                "ibanOperations.csv",
                "text/csv",
                csvBytes
        );
    }

   /**
     * Converts a single IBAN operation to a CSV row.
     * @param operation IBAN operation
     * @return csv row as string
     */
   private static String operationToCsvRow(IbanOperation operation, String ciCode, String ciName) {
        String apiConfigOperation = mapOperationType(String.valueOf(operation.getType()));

        return Stream.of(
                        ciCode,
                        ciName,
                        Optional.ofNullable(operation.getDescription()).orElse(""),
                        operation.getIbanValue(),
                        Optional.ofNullable(operation.getValidityDate()).orElse(""),
                        apiConfigOperation
                )
                .map(IbanOperationsCsvUtil::escapeCsvField)
                .collect(Collectors.joining(","));
   }

   private static String mapOperationType(String operation) {
        return switch (operation.toUpperCase()) {
            case "CREATE" -> "I";
            case "UPDATE" -> "U";
            case "DELETE" -> "D";
            default -> throw new IllegalStateException("Unexpected value: " + operation.toUpperCase());
        };
   }

   private static String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
   }
}