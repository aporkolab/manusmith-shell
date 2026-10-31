package org.manusmith.shell.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Checks the content produced by {@link EngineBridge#quickConvert(File, File)}, not just that a file appears.
 */
class QuickConvertContentTest {

    private EngineBridge engineBridge;

    @BeforeEach
    void setUp() {
        ConfigurationService.instance = null;
        MetricsService.instance = null;
        engineBridge = new EngineBridge();
    }

    @Test
    void txtToDocxToTxt_roundTripKeepsUtf8TextAndLines(@TempDir Path tempDir) throws Exception {
        File txt = tempDir.resolve("in.txt").toFile();
        Files.writeString(txt.toPath(), "Árvíztűrő tükörfúrógép\nSecond line", StandardCharsets.UTF_8);
        File docx = tempDir.resolve("mid.docx").toFile();
        File back = tempDir.resolve("out.txt").toFile();

        engineBridge.quickConvert(txt, docx);
        engineBridge.quickConvert(docx, back);

        assertThat(Files.readString(back.toPath(), StandardCharsets.UTF_8))
                .isEqualTo("Árvíztűrő tükörfúrógép\nSecond line\n");
    }

    @Test
    void mdToTxt_stripsMarkdownSyntax(@TempDir Path tempDir) throws Exception {
        File md = tempDir.resolve("in.md").toFile();
        Files.writeString(md.toPath(), "# Title\n\nSome **bold** and *italic* text.", StandardCharsets.UTF_8);
        File txt = tempDir.resolve("out.txt").toFile();

        engineBridge.quickConvert(md, txt);

        String text = Files.readString(txt.toPath(), StandardCharsets.UTF_8);
        assertThat(text).contains("Title").contains("Some bold and italic text.");
        assertThat(text).doesNotContain("#").doesNotContain("*");
    }
}
