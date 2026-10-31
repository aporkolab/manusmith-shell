package org.manusmith.shell.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConvertControllerFilenameTest {

    @Test
    void sanitizeFilename_replacesReservedCharactersIncludingBackslash() {
        assertThat(ConvertController.sanitizeFilename("A/B\\C:D*E?F\"G<H>I|J.docx"))
                .isEqualTo("A_B_C_D_E_F_G_H_I_J.docx");
    }

    @Test
    void sanitizeFilename_collapsesWhitespaceAndKeepsAccents() {
        assertThat(ConvertController.sanitizeFilename("  Porkoláb   Ádám -\tMű.docx "))
                .isEqualTo("Porkoláb Ádám - Mű.docx");
    }

    @Test
    void sanitizeFilename_withNull_fallsBackToDefault() {
        assertThat(ConvertController.sanitizeFilename(null)).isEqualTo("output.docx");
    }
}
