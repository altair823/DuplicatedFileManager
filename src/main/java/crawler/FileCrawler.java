package crawler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileCrawler {

    public static List<Path> walk(Path rootPath, FileTime modifiedFileTime) throws IOException {
        if (!new File(rootPath.toUri()).isDirectory()) {
            throw new IllegalArgumentException("Argument is not a directory");
        }
        List<Path> fileList;
        try (Stream<Path> pathStream = Files.find(rootPath,
                Integer.MAX_VALUE,
                (p, basicFileAttributes) -> {

                    if(Files.isDirectory(p) || !Files.isReadable(p)){
                        return false;
                    }

                    FileTime fileTime = basicFileAttributes.lastModifiedTime();
                    // negative if less, positive if greater
                    // 1 means fileTime equals or after the provided instant argument
                    // -1 means fileTime before the provided instant argument
                    int i = fileTime.compareTo(modifiedFileTime);
                    return i > 0;
                }

        )) {
            fileList = pathStream.collect(Collectors.toList());
        }
        return fileList;
    }
}
