package it.pagopa.selfcare.pagopa.backoffice.util;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class InMemoryMultipartFile implements MultipartFile {
    private final String name;
    private final String originalFilename;
    private final String contentType;
    private final byte[] content;

    public InMemoryMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
        this.name = name;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.content = content;
    }

    @Override public @NotNull String getName() { return name; }
    @Override public String getOriginalFilename() { return originalFilename; }
    @Override public String getContentType() { return contentType; }
    @Override public boolean isEmpty() { return content == null || content.length == 0; }
    @Override public long getSize() { return content.length; }
    @Override public byte @NotNull [] getBytes() { return content; }
    @Override public @NotNull InputStream getInputStream() { return new ByteArrayInputStream(content); }
    @Override public void transferTo(@NotNull File dest) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(dest)) { fos.write(content); }
    }
}