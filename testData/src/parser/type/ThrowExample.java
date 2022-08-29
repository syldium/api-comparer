package parser.type;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

final class ThrowExample {

    void createFile() throws IOException {
        Files.createFile(Path.of("output.txt"));
    }
}
