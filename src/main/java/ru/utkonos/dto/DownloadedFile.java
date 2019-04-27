package ru.utkonos.dto;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
@EqualsAndHashCode
public class DownloadedFile {
    private String url;
    private String expectedMd5;
    private String receivedMd5;

    public DownloadedFile(String url, String expectedHash) {
        this.url = url;
        this.expectedMd5 = expectedHash;
    }

    public static DownloadedFile fromString(String s) {
        if (s == null) throw new IllegalArgumentException("file line is empty");

        String[] parts = s.split(",");
        if (parts.length != 2) throw new IllegalArgumentException("wrong file line format");

        return new DownloadedFile(StringUtils.trim(parts[0]), StringUtils.trim(parts[1]));
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Downloaded file: ")
                .append(url)
                .append(", expected md5: ")
                .append(expectedMd5)
                .append(", received md5: ")
                .append(receivedMd5)
                .toString();
    }
}
