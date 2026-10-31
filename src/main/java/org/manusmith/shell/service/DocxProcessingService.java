package org.manusmith.shell.service;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.manusmith.shell.dto.ConvertRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class DocxProcessingService {
    private static final Logger logger = LoggerFactory.getLogger(DocxProcessingService.class);

    public void processDocument(ConvertRequest request) throws IOException {
        try (FileInputStream fis = new FileInputStream(request.inputFile());
             XWPFDocument document = new XWPFDocument(fis)) {

            if (request.formattingPrefs() != null && request.formattingPrefs().italicToUnderline()) {
                logger.debug("Applying italic-to-underline conversion");
                convertItalicToUnderline(document.getBodyElements());
            }

            try (FileOutputStream fos = new FileOutputStream(request.outputFile())) {
                document.write(fos);
            }
        }
    }

    /**
     * Converts italic runs to underlined runs in place, so every other run property
     * (text with tabs and breaks, font, size, colour, bold) is preserved. Tables are
     * walked recursively because manuscripts sometimes keep text in layout tables.
     */
    private void convertItalicToUnderline(List<IBodyElement> elements) {
        for (IBodyElement element : elements) {
            if (element instanceof XWPFParagraph paragraph) {
                for (XWPFRun run : paragraph.getRuns()) {
                    if (run.isItalic()) {
                        run.setItalic(false);
                        run.setUnderline(UnderlinePatterns.SINGLE);
                    }
                }
            } else if (element instanceof XWPFTable table) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        convertItalicToUnderline(cell.getBodyElements());
                    }
                }
            }
        }
    }
}
