package it.pagopa.selfcare.pagopa.backoffice.util;

import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanOperation;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanOperationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IbanOperationsCsvUtilTest {

    @Test
    @DisplayName("Should convert operations list to CSV correctly")
    void convertOperationsToCsv_Success() throws IOException {

        String ciCode = "12345678901";
        IbanOperation op1 = IbanOperation.builder()
                .creditorInstitutionCode(ciCode)
                .description("Test Description")
                .ibanValue("IT12X0542403200000000012345")
                .validityDate("2023-12-31")
                .type(IbanOperationType.CREATE)
                .build();

        IbanOperation op2 = IbanOperation.builder()
                .creditorInstitutionCode(ciCode)
                .description("Update, with comma")
                .ibanValue("IT12X0542403200000000012346")
                .validityDate(null) // Test null safety
                .type(IbanOperationType.UPDATE)
                .build();

        List<IbanOperation> operations = Arrays.asList(op1, op2);

        MultipartFile result = IbanOperationsCsvUtil.convertOperationsToCsv(ciCode, operations);

        assertThat(result).isNotNull();
        assertThat(result.getContentType()).isEqualTo("text/csv");
        assertThat(result.getOriginalFilename()).isEqualTo("ibanOperations.csv");

        String content = new String(result.getBytes(), StandardCharsets.UTF_8);
        String[] lines = content.split("\n");

        assertThat(lines[0]).isEqualTo("iddominio,ragionesociale,descrizione,iban,dataattivazioneiban,operazione");

        assertThat(lines[1]).isEqualTo("12345678901,12345678901,Test Description,IT12X0542403200000000012345,2023-12-31,I");

        assertThat(lines[2]).contains("\"Update, with comma\"");
        assertThat(lines[2]).endsWith(",U");
    }

    @Test
    @DisplayName("Should handle empty list returning only header")
    void convertOperationsToCsv_EmptyList() throws IOException {

        List<IbanOperation> operations = Collections.emptyList();
        MultipartFile result = IbanOperationsCsvUtil.convertOperationsToCsv("999", operations);

        String content = new String(result.getBytes(), StandardCharsets.UTF_8);
        assertThat(content.trim()).isEqualTo("iddominio,ragionesociale,descrizione,iban,dataattivazioneiban,operazione");
    }

    @Test
    @DisplayName("Should handle 100 records and verify total row count")
    void convertOperationsToCsv_LargeList() throws IOException {

        String ciCode = "00000000000";
        int rowCount = 100;

        List<IbanOperation> operations = java.util.stream.IntStream.range(0, rowCount)
                .mapToObj(i -> IbanOperation.builder()
                        .creditorInstitutionCode(ciCode)
                        .description("Description number " + i)
                        .ibanValue("IT00X" + String.format("%022d", i))
                        .validityDate("2025-01-01")
                        .type(i % 2 == 0 ? IbanOperationType.CREATE :IbanOperationType.UPDATE)
                        .build())
                .toList();

        long startTime = System.currentTimeMillis();
        MultipartFile result = IbanOperationsCsvUtil.convertOperationsToCsv(ciCode, operations);
        long endTime = System.currentTimeMillis();

        long durationMs = (endTime - startTime);

        assertThat(result).isNotNull();

        String content = new String(result.getBytes(), StandardCharsets.UTF_8);
        String[] lines = content.split("\n");

        assertThat(lines).hasSize(rowCount + 1);

        assertThat(lines[rowCount]).contains("Description number 99");
        assertThat(lines[rowCount]).endsWith(",U");
        assertThat(durationMs).isLessThan(10);
    }
}