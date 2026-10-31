# ManuSmith Shell

## 🎯 Main Goal

A comfortable, simple **UI/CLI** that hides the engine details and completes tasks with just 1-2 clicks.

## ✅ Status

| Feature | Status | Notes |
| :--- | :--- | :--- |
| **MVP** (Shunn formatting) | Implemented | The `italic → underline` conversion works. |
| **Cover Letter** | Implemented | The cover letter generator works. |
| **TypoKit** | Implemented | Basic typographic fixes (HU/DE/EN) and scene break normalization implemented. |
| **QuickConvert** | Implemented | Supported conversions: `.txt`↔`.docx`, `.md`→`.txt`, `.odt`→`.txt`. |
| **CrossClip Lite** | Implemented | System tray icon, theme switcher, and clipboard cleaning functionality. |

---

## Minimum Viable Product (MVP)

1. **File Selection**
   * Select `.docx` input with `FileChooser` or drag&drop.
   * User sees the name of the selected file.

2. **Metadata Input**
   * Form: author name, title, email, phone, work title.
   * Word count → automatically calculated by the engine, but the field can be overridden.

3. **Formatting Options**
   * Checkbox: *italic → underline* conversion.
   * (Later: classic vs. modern Shunn).

4. **Output Location**
   * Save dialog or default filename: `Author - Title.docx`.
   * Export button → engine works → "Successful save" message.

5. **Basic Status Indication**
   * Status bar or label: "File read", "Conversion in progress", "Done".

---

## Convenience Features (above v1.0)

* **Cover Letter Generator tab**
  * Automatic generation from template to `.txt`.
  * Market name, genre, simsub yes/no.

* **TypoKit tab**
  * File selection → profile selection (HU/EN/DE).
  * Preview panel (left: original, right: normalized).
  * Save as new file.

* **QuickConvert tab**
  * Drag&drop → `.docx/.odt/.md/.txt` full round-trip conversion.
  * Native ODT file support (reading and writing).
  * Preserves italic formatting during ODT conversions.
  * Automatic output to "out" folder.

* **CrossClip Lite**
  * Tray icon: [ ] auto-clean clipboard.
  * "Paste Clean" → always format-free + typo fix.
  * Light/dark mode toggle.

---

## Shell Logical Layers

1. **UI Controller** (JavaFX `Controller` classes)
   * Input collection, events (button press, checkbox).
   * File dialogs.

2. **Service Layer**
   * Calls the engine API (`DocxReader`, `ShunnFormatter`, `DocxWriter`).
   * Error handling → alert dialog in UI.

3. **Integration**
   * The shell currently runs standalone: document handling is done directly by Apache POI (DOCX), ODF Toolkit (ODT) and flexmark (Markdown).
   * `manusmith-engine` is not a build dependency; if it comes back, its API should be wired in behind the `service` layer.

---

## Key User Experience

* **Simple workflow:** Input → Metadata → Generate → Output.
* **Minimizing clicks.**
* **"Done, it works" feeling:** no need for immediate preview or log, just *safe output*.

---

👉 So the **shell's task is nothing more** than the **graphical frame** around the engine:

* File selection
* Metadata collection
* Optional settings
* Saving and status feedback

## Recent Updates

* Integrated with ManuSmith Engine v2.0.0
* Full ODT (OpenDocument Text) file support
* Complete round-trip conversions between formats
* Preserves formatting (including italic text) during conversions
* Improved stability and performance
* CI/CD integration for automated testing and deployment

---

## Development

### Stack

| Component | Version |
| :--- | :--- |
| Java | 21 (LTS) |
| JavaFX | 23.0.2 (last line supporting JDK 21) |
| Maven | 3.9+ (the bundled `./mvnw` uses 3.9.11) |
| Apache POI | 6.0.0 |
| ODF Toolkit (odfdom) | 0.13.0 |
| flexmark | 0.64.8 |
| JUnit / TestFX | 6.1 / 4.0.18 |

### Commands

```bash
./mvnw clean verify -Ddependency-check.skip=true   # build + tests + SpotBugs + fat JAR
./mvnw test                                         # tests only (headless Monocle)
./mvnw -Prun-javafx javafx:run                      # run in dev mode (set javafx.platform in the profile: linux, win, mac-aarch64)
java -jar target/manusmith-shell-2.0.0-jar-with-dependencies.jar
./mvnw org.owasp:dependency-check-maven:check       # OWASP dependency check
```

Tests that use accented file names need a UTF-8 locale (e.g. `LC_ALL=C.UTF-8`); otherwise that test is skipped.

### Configuration

* Defaults: `src/main/resources/application.yml`.
* User override: `~/.manusmith-shell/config/application.yml` (loaded instead of the defaults when present).
* Environment variables: `NVD_API_KEY` (optional, only speeds up the OWASP check; a secret in CI).
