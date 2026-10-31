package org.manusmith.shell.service;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocxReaderServiceTest {

    private final DocxReaderService service = new DocxReaderService();

    @Test
    void readText_returnsParagraphTextInOrder(@TempDir Path tempDir) throws IOException {
        File file = tempDir.resolve("story.docx").toFile();
        try (XWPFDocument document = new XWPFDocument(); FileOutputStream fos = new FileOutputStream(file)) {
            document.createParagraph().createRun().setText("Első bekezdés.");
            document.createParagraph().createRun().setText("Second paragraph.");
            document.write(fos);
        }

        assertThat(service.readText(file)).isEqualTo("Első bekezdés.\nSecond paragraph.\n");
    }

    @Test
    void readText_withMissingFile_throwsIOException(@TempDir Path tempDir) {
        File missing = tempDir.resolve("missing.docx").toFile();

        assertThatThrownBy(() -> service.readText(missing))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("File does not exist");
    }

    @Test
    void readText_withNullFile_throwsIOException() {
        assertThatThrownBy(() -> service.readText(null)).isInstanceOf(IOException.class);
    }
}
