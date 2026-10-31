package org.manusmith.shell.service;

import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.manusmith.shell.dto.AuthorMeta;
import org.manusmith.shell.dto.ConvertRequest;
import org.manusmith.shell.dto.FormattingPrefs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class DocxProcessingServiceTest {

    private final DocxProcessingService service = new DocxProcessingService();

    @Test
    void italicToUnderline_preservesTextAndOtherRunProperties(@TempDir Path tempDir) throws IOException {
        File input = tempDir.resolve("in.docx").toFile();
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph paragraph = document.createParagraph();
            paragraph.createRun().setText("Plain ");
            XWPFRun italic = paragraph.createRun();
            italic.setItalic(true);
            italic.setBold(true);
            italic.setFontFamily("Courier New");
            italic.setFontSize(12);
            italic.setText("first part");
            italic.addTab();
            italic.setText("second part");

            XWPFTable table = document.createTable(1, 1);
            XWPFRun cellRun = table.getRow(0).getCell(0).getParagraphs().get(0).createRun();
            cellRun.setItalic(true);
            cellRun.setText("in a table");
            write(document, input);
        }

        File output = convert(input, true);

        try (XWPFDocument result = read(output)) {
            XWPFParagraph paragraph = result.getParagraphs().get(0);
            assertThat(paragraph.getText()).isEqualTo("Plain first part\tsecond part");

            XWPFRun plain = paragraph.getRuns().get(0);
            assertThat(plain.getUnderline()).isEqualTo(UnderlinePatterns.NONE);

            XWPFRun converted = paragraph.getRuns().get(1);
            assertThat(converted.isItalic()).isFalse();
            assertThat(converted.getUnderline()).isEqualTo(UnderlinePatterns.SINGLE);
            assertThat(converted.isBold()).isTrue();
            assertThat(converted.getFontFamily()).isEqualTo("Courier New");
            assertThat(converted.getFontSizeAsDouble()).isEqualTo(12.0);

            XWPFRun tableRun = result.getTables().get(0).getRow(0).getCell(0).getParagraphs().get(0).getRuns().get(0);
            assertThat(tableRun.isItalic()).isFalse();
            assertThat(tableRun.getUnderline()).isEqualTo(UnderlinePatterns.SINGLE);
        }
    }

    @Test
    void withoutItalicToUnderline_documentIsCopiedUnchanged(@TempDir Path tempDir) throws IOException {
        File input = tempDir.resolve("in.docx").toFile();
        try (XWPFDocument document = new XWPFDocument()) {
            XWPFRun run = document.createParagraph().createRun();
            run.setItalic(true);
            run.setText("stay italic");
            write(document, input);
        }

        File output = convert(input, false);

        try (XWPFDocument result = read(output)) {
            XWPFRun run = result.getParagraphs().get(0).getRuns().get(0);
            assertThat(run.isItalic()).isTrue();
            assertThat(run.getUnderline()).isEqualTo(UnderlinePatterns.NONE);
            assertThat(run.text()).isEqualTo("stay italic");
        }
    }

    private File convert(File input, boolean italicToUnderline) throws IOException {
        File output = new File(input.getParentFile(), "out.docx");
        AuthorMeta meta = new AuthorMeta("Author", "", "", "", "Title", "100");
        service.processDocument(new ConvertRequest(input, output, meta, new FormattingPrefs(italicToUnderline)));
        return output;
    }

    private static void write(XWPFDocument document, File file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            document.write(fos);
        }
    }

    private static XWPFDocument read(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return new XWPFDocument(fis);
        }
    }
}
