package ru.utkonos;

import org.junit.Test;
import ru.utkonos.dto.DownloadedFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainTest {

    private String fileLineFormat = "%s, %s";

    @Test(expected = RuntimeException.class)
    public void testLoadFileListWrongURL() {
        Main.loadFileList("wrongURL");
    }

    @Test(expected = RuntimeException.class)
    public void testLoadFileListFileNotFound() {
        Main.loadFileList("file:/MissingFile.txt");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadFileListWrongLineFormat() throws FileNotFoundException {
        File fileList = new File(Paths.get(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString()).toUri());
        try (PrintWriter out = new PrintWriter(fileList)) {
            out.println("wrongLine");
        }

        Main.loadFileList(fileList.toURI().toString());
    }

    @Test
    public void testLoadFileList() throws FileNotFoundException, URISyntaxException {
        List<DownloadedFile> files = new ArrayList<>();
        files.add(new DownloadedFile(getClass().getClassLoader().getResource("file1.7z").toURI().toString(),
                "28fd486ed08c40d75bcdd6b749348692"));
        files.add(new DownloadedFile(getClass().getClassLoader().getResource("file2.7z").toURI().toString(),
                "9b6d12dde0d6c8f30236b58765358ofa"));
        File fileList = new File(Paths.get(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString()).toUri());
        try (PrintWriter out = new PrintWriter(fileList)) {
            files.stream().forEach(f -> {
                out.println(String.format(fileLineFormat, f.getUrl(), f.getExpectedMd5()));
            });
        }

        List<DownloadedFile> loadedFiles = Main.loadFileList(fileList.toURI().toString());
        assert files.equals(loadedFiles) : "loaded files distinct from initial";
    }

    @Test(expected = RuntimeException.class)
    public void testCalcFileMd5WrongURL() {
        DownloadedFile file = new DownloadedFile("wrongURL", "");
        Main.calcFileMd5(file);
    }

    @Test(expected = RuntimeException.class)
    public void testCalcFileMd5FileNotFound() {
        DownloadedFile file = new DownloadedFile("file:/MissingFile.txt", "");
        Main.calcFileMd5(file);
    }

    @Test
    public void testCalcFileMd5File() throws URISyntaxException {
        DownloadedFile file = new DownloadedFile(getClass().getClassLoader().getResource("file1.7z").toURI().toString(),
                "28fd486ed08c40d75bcdd6b749348692");

        Main.calcFileMd5(file);
        assert file.getExpectedMd5().equals(file.getReceivedMd5()) : "wrong md5 result";
    }
}
