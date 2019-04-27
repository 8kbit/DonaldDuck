package ru.utkonos;

import org.apache.commons.codec.digest.DigestUtils;
import ru.utkonos.dto.DownloadedFile;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) throw new RuntimeException("File list URL is not specified");

        List<DownloadedFile> files = loadFileList(args[0]);

        files.stream().parallel().forEach(f -> {
            try {
                calcFileMd5(f);
                System.out.println(f);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

    /**
     * Download file list from URL and parse
     *
     * @param url file list url
     * @return list of file dto
     */
    public static List<DownloadedFile> loadFileList(String url) {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(new URL(url).openStream(), StandardCharsets.UTF_8))) {
            return r.lines().map(DownloadedFile::fromString).collect(Collectors.toList());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Wrong file list URL");
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File list not found");
        } catch (IOException e) {
            throw new RuntimeException("Error while loading file list");
        }
    }

    /**
     * Download file in memory and calculate md5 sum on the fly
     *
     * @param file file dto
     */
    public static void calcFileMd5(DownloadedFile file) {
        try (InputStream is = new URL(file.getUrl()).openStream()) {
            file.setReceivedMd5(DigestUtils.md5Hex(is));
        } catch (MalformedURLException e) {
            throw new RuntimeException("Wrong file URL: " + file.getUrl());
        } catch (IOException e) {
            throw new RuntimeException("Error while calculating md5 for file: " + file.getUrl());
        }
    }
}
