package crawler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileCrawler {

    private List<String> fileList;

    public List<String> getFileList() {
        return fileList;
    }

    public FileCrawler(Path path) throws IOException {
        if (!new File(path.toUri()).isDirectory()) {
            throw new IllegalArgumentException("Argument is not a directory");
        }
        fileList = new LinkedList<>();
        try (Stream<Path> stream = Files.list(path)) {
            fileList = stream
                    .filter(f -> !Files.isDirectory(f))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        }
    }
}
