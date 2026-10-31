package org.manusmith.shell.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Regression tests for file name and path validation in {@link SecurityService}.
 */
class SecurityServiceFilenameTest {

    private SecurityService securityService;
    private String originalUserHome;

    @BeforeEach
    void setUp() {
        ConfigurationService.instance = null;
        originalUserHome = System.getProperty("user.home");
        securityService = new SecurityService();
    }

    @AfterEach
    void tearDown() {
        System.setProperty("user.home", originalUserHome);
        ConfigurationService.instance = null;
    }

    @Test
    void validateFileAccess_acceptsAccentedManuscriptNames(@TempDir Path tempDir) throws Exception {
        String name = "Porkoláb Ádám - Égi mű (Shunn).docx";
        // The JVM can only represent such paths when the platform file name encoding is Unicode-capable
        assumeTrue(Charset.forName(System.getProperty("sun.jnu.encoding", "UTF-8")).newEncoder().canEncode(name));
        File manuscript = Files.createFile(tempDir.resolve(name)).toFile();

        assertThatCode(() -> securityService.validateFileAccess(manuscript)).doesNotThrowAnyException();
    }

    @Test
    void validateFileAccess_acceptsNamesContainingDoubleDots(@TempDir Path tempDir) throws Exception {
        File file = Files.createFile(tempDir.resolve("Wait.. what.txt")).toFile();

        assertThatCode(() -> securityService.validateFileAccess(file)).doesNotThrowAnyException();
    }

    @Test
    void validateFileAccess_rejectsParentDirectorySegments(@TempDir Path tempDir) {
        File traversal = new File(tempDir.toFile(), "sub" + File.separator + ".." + File.separator + "secret.txt");

        assertThatThrownBy(() -> securityService.validateFileAccess(traversal))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Path traversal");
    }

    @Test
    void validateFileAccess_withHomeRestriction_rejectsSiblingDirectorySharingThePrefix(@TempDir Path tempDir) throws Exception {
        Path home = Files.createDirectory(tempDir.resolve("user"));
        Path sibling = Files.createDirectory(tempDir.resolve("user2"));
        System.setProperty("user.home", home.toString());
        ConfigurationService.getInstance().getSecurityConfig().getFileAccess().setRestrictToUserHome(true);

        File inside = Files.createFile(home.resolve("novel.docx")).toFile();
        File outside = Files.createFile(sibling.resolve("novel.docx")).toFile();

        assertThatCode(() -> securityService.validateFileAccess(inside)).doesNotThrowAnyException();
        assertThatThrownBy(() -> securityService.validateFileAccess(outside))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("restricted to user home");
    }

    @Test
    void validateFileAccess_blockedExtensionIsCaseInsensitive(@TempDir Path tempDir) throws Exception {
        File file = Files.createFile(tempDir.resolve("setup.EXE")).toFile();

        assertThatThrownBy(() -> securityService.validateFileAccess(file))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("File extension not allowed");
    }

    @Test
    void sanitizeFilename_keepsUnicodeLettersAndReplacesReservedCharacters() {
        assertThat(securityService.sanitizeFilename("Ádám: \"Mű\" a/b\\c.docx"))
                .isEqualTo("Ádám_ _Mű_ a_b_c.docx");
    }

    @Test
    void calculateChecksum_returnsLowercaseSha256Hex(@TempDir Path tempDir) throws Exception {
        File file = tempDir.resolve("abc.txt").toFile();
        Files.writeString(file.toPath(), "abc");

        assertThat(securityService.calculateChecksum(file))
                .isEqualTo("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad");
    }
}
