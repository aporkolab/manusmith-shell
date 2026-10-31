# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed
- Dependencies: JavaFX 23.0.2, Apache POI 6.0.0, Jackson 2.22, Micrometer 1.17, Hibernate Validator 9.1, Logback 1.6, JUnit 6.1; `flexmark-all` replaced by the `flexmark` core module (smaller fat JAR); unused picocli removed.
- Build: Maven wrapper 3.9.11, enforcer (Maven 3.9+, Java 21+), current Maven plugins, OWASP dependency-check 13.
- CI: current major versions of all GitHub Actions; the test job runs the full `verify` build.

### Fixed
- File names with accented letters or parentheses were rejected by the security check.
- `restrictToUserHome` accepted sibling directories sharing the home prefix.
- Typography: quotes at the start of a line were not converted, quotes paired across paragraphs, spaced `--` produced double spaces, and Shunn `---` scene breaks became em-dashes.
- Italic-to-underline lost text after tabs/breaks and some run formatting; tables are now covered too.
- Quick Convert broke the status bar for the rest of the session and lower-cased output file names.
- Closing the window without a system tray left an invisible, unclosable application.

### Removed
- Unused utility classes (AnimationUtil, AsyncTaskManager, DragDropHandler, ErrorHandlerUtil, FilePreviewUtil, KeyboardShortcutManager).

## [2.0.0] - 2025-09-15

### Added
- **Full ODT Support**: Complete integration with ManuSmith Engine v2.0.0 for native ODT file support
- **Round-trip Conversions**: ODT ↔ DOCX ↔ TXT conversion capabilities  
- **Format Preservation**: Maintains italic formatting during ODT conversions
- **CI/CD Pipeline**: Comprehensive GitHub Actions workflow with automated testing and releases
- **English Documentation**: Added English README for international users
- **Dependency Management**: Updated to Java 21 and cleaned up duplicate dependencies

### Changed
- **Engine Integration**: Updated from commented dependency to active ManuSmith Engine v2.0.0
- **Java Version**: Upgraded from Java 17 to Java 21 for consistency with engine
- **QuickConvert Status**: Updated from "Partially implemented" to "Fully implemented"
- **Documentation**: Enhanced README with ODT support information

### Improved  
- **Test Coverage**: All tests passing including ODT functionality tests
- **Performance**: Better stability through engine v2.0.0 integration
- **User Experience**: Complete document processing workflow with full format support

### Technical
- Added `-parameters` compiler flag for Spring Boot compatibility
- Removed duplicate document processing dependencies (now provided by engine)
- Fixed repository URLs in CI/CD configuration
- Consistent versioning across both projects

## [1.0.0] - Previous Release

### Features
- **MVP (Shunn formatting)**: Complete implementation with italic → underline conversion
- **Cover Letter Generator**: Automatic cover letter generation from templates
- **TypoKit**: Basic typographic fixes for HU/DE/EN and scene break normalization  
- **CrossClip Lite**: System tray integration with theme switcher and clipboard cleaning
- **JavaFX GUI**: Complete graphical user interface for all features

### Core Functionality
- File selection with drag & drop support
- Metadata input forms
- Document processing with real-time status updates
- Multi-language support (Hungarian/English/German)
- Theme switching (light/dark mode)
- Configuration management and persistence