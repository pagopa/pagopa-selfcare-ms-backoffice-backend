package it.pagopa.selfcare.pagopa.backoffice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.when;

@UtilityClass
public class TestUtil {

    /**
     * @param relativePath Path from source root of the json file
     * @return the Json string read from the file
     * @throws IOException if an I/O error occurs reading from the file or a malformed or unmappable
     *                     byte sequence is read
     */
    public String readJsonFromFile(String relativePath) throws IOException {
        ClassLoader classLoader = TestUtil.class.getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(relativePath)).getPath());
        return Files.readString(file.toPath());
    }

    /**
     * @param json   JSON string
     * @param tClass class to convert
     * @param <T>
     * @return the converted object
     * @throws JsonProcessingException
     */
    public <T> T toObject(String json, Class<T> tClass) throws JsonProcessingException {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .readValue(json, tClass);
    }

    public <T> T toObject(String json, TypeReference<T> typeReference) throws JsonProcessingException {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .readValue(json, typeReference);
    }

    /**
     * @param relativePath Path from source root of the file
     * @return the requested file
     */
    public File readFile(String relativePath) {
        ClassLoader classLoader = TestUtil.class.getClassLoader();
        return new File(Objects.requireNonNull(classLoader.getResource(relativePath)).getFile());
    }

    /**
     * @param object to map into the Json string
     * @return object as Json string
     * @throws JsonProcessingException if there is an error during the parsing of the object
     */
    public String toJson(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    /**
     * Prepare a Mock for the class {@link Page}
     *
     * @param content    list of elements in the page to return by mock
     * @param limit      number of elements per page to return by mock
     * @param pageNumber number of page to return by mock
     * @param <T>        Class of the elements
     * @return a Mock of {@link Page}
     */
    public <T> Page<T> mockPage(List<T> content, int limit, int pageNumber) {
        @SuppressWarnings("unchecked")
        Page<T> page = Mockito.mock(Page.class);
        when(page.getTotalPages()).thenReturn((int) Math.ceil((double) content.size() / limit));
        when(page.getNumberOfElements()).thenReturn(content.size());
        when(page.getNumber()).thenReturn(pageNumber);
        when(page.getSize()).thenReturn(limit);
        when(page.getTotalElements()).thenReturn((long) content.size());
        when(page.getContent()).thenReturn(content);
        when(page.stream()).thenReturn(content.stream());
        return page;
    }


}
