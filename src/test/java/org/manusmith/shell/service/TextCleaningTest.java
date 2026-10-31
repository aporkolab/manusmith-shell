package org.manusmith.shell.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exact-output tests for the typography profiles of {@link EngineBridge#cleanText(String, String)}.
 */
class TextCleaningTest {

    private EngineBridge engineBridge;

    @BeforeEach
    void setUp() {
        ConfigurationService.instance = null;
        MetricsService.instance = null;
        engineBridge = new EngineBridge();
    }

    @Test
    void hungarianProfile_convertsQuotesAtTheStartOfALine() {
        String result = engineBridge.cleanText("\"Szia,\" mondta.\n\"Mi újság?\"", "HU");

        assertThat(result).isEqualTo("„Szia,” mondta.\n„Mi újság?”");
    }

    @Test
    void englishProfile_convertsDialogueAndInnerQuotes() {
        String result = engineBridge.cleanText("\"She said 'no' twice,\" he wrote.", "EN");

        assertThat(result).isEqualTo("“She said ‘no’ twice,” he wrote.");
    }

    @Test
    void germanProfile_usesLowHighQuotes() {
        assertThat(engineBridge.cleanText("Er sagte \"Hallo\".", "DE")).isEqualTo("Er sagte „Hallo“.");
    }

    @Test
    void quotesAreNotPairedAcrossParagraphs() {
        String input = "He opened a quote \"without closing it.\nNext \"paragraph\" here.";

        String result = engineBridge.cleanText(input, "EN");

        assertThat(result).isEqualTo("He opened a quote \"without closing it.\nNext “paragraph” here.");
    }

    @Test
    void apostrophesInsideWordsAreLeftAlone() {
        assertThat(engineBridge.cleanText("It's John's book.", "EN")).isEqualTo("It's John's book.");
    }

    @Test
    void generalFixes_convertDashesAndEllipsis() {
        assertThat(engineBridge.cleanText("well--maybe ... -- no", "UNKNOWN"))
                .isEqualTo("well—maybe … — no");
    }

    @Test
    void hungarianProfile_turnsSpacedEmDashIntoEnDash() {
        assertThat(engineBridge.cleanText("Igen -- mondta.", "HU")).isEqualTo("Igen – mondta.");
    }

    @Test
    void shunnProfile_normalisesSceneBreaks() {
        assertThat(engineBridge.cleanText("End.\n***\nStart.\n  ---  \nMore.", "Shunn"))
                .isEqualTo("End.\n # \nStart.\n # \nMore.");
    }
}
