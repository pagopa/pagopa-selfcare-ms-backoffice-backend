package it.pagopa.selfcare.pagopa.backoffice.connector.utils;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StringUtilsTest {

    @Autowired
    StringUtils stringUtils;

    @Test
    void testValidateAndReplace() {
        String str = "Test123-";
        String replace = "-";
        String result = stringUtils.validateAndReplace(str, replace);
        assertNotNull(result);
        assertEquals("Test123-", result);

        str = "Test (123)";
        replace = "-";
        result = stringUtils.validateAndReplace(str, replace);
        assertNotNull(result);
        assertEquals("Test -123-", result);

        str = "Test   123";
        replace = "-";
        result = stringUtils.validateAndReplace(str, replace);
        assertNotNull(result);
        assertEquals("Test   123", result);
    }

    @Test
    void testGenerator() {
        List<String> codes = new ArrayList<>(Arrays.asList("String_01", "String_02", "String_03"));
        String retrievedCode = "String";
        String result = stringUtils.generator(codes, retrievedCode);
        assertNotNull(result);
        assertEquals("String_04", result);

        codes = new ArrayList<>(Arrays.asList("String_01", "String_02"));
        retrievedCode = "String";
        result = stringUtils.generator(codes, retrievedCode);
        assertNotNull(result);
        assertEquals("String_03", result);

        codes = new ArrayList<>();
        retrievedCode = "String";
        result = stringUtils.generator(codes, retrievedCode);
        assertNotNull(result);
        assertEquals("String_01", result);

        codes = Arrays.asList("test_n");
        retrievedCode = "test";
        result = stringUtils.generator(codes, retrievedCode);
        assertEquals("test_01", result);
    }
}