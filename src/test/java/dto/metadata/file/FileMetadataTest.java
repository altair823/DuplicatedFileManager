package dto.metadata.file;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileMetadataTest {

    @Test
    void toStringTest() {
        FileMetadata fileMetadata = new FileMetadata(
                "C:\\Users\\altai\\Desktop\\test.txt",
                4321,
                1234,
                "123456789"
        );
        String expected = "FileMetadata{path='C:\\Users\\altai\\Desktop\\test.txt', " +
                "lastModified=4321, size=1234, hash='123456789'}";
        assertEquals(expected, fileMetadata.toString());
    }
}