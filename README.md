## ManuSmith Shell – feladatai

### 🎯 Fő cél

Kényelmes, egyszerű **UI/CLI**, ami elrejti az engine részleteit, és 1–2 kattintással elvégezteti a munkát.

### ✅ Státusz

| Funkció | Státusz | Megjegyzés |
| :--- | :--- | :--- |
| **MVP** (Shunn formázás) |  Implementálva | Az `italic → underline` konverzió működik. |
| **Cover Letter** | Implementálva | A kísérőlevél-generátor működik. |
| **TypoKit** | Implementálva | Alapvető tipográfiai javítások (HU/DE/EN) és jelenettörés-normalizálás megvalósítva. |
| **QuickConvert** | Implementálva | Támogatott konverziók: `.txt`↔`.docx`, `.md`→`.txt`, `.odt`→`.txt`. |
| **CrossClip Lite** | Implementálva | Tálcaikon, téma váltó és vágólap-tisztító funkcióval. |

---

## Minimum (MVP)

1. **Fájl kiválasztása**

   * `.docx` input kiválasztása `FileChooser`-rel vagy drag&drop.
   * Felhasználó látja a kiválasztott fájl nevét.

2. **Metaadatok bekérése**

   * Form: szerző név, cím, email, telefon, mű címe.
   * Szószám → automatikusan kiszámolja az engine, de a mező felülírható.

3. **Formázási opciók**

   * Checkbox: *italic → underline* konverzió.
   * (Később: klasszikus vs modern Shunn).

4. **Kimenet helye**

   * Mentés dialógus vagy alapértelmezett fájlnév: `Author - Title.docx`.
   * Export gomb → engine dolgozik → „Sikeres mentés” üzenet.

5. **Alap státusz jelzés**

   * Állapotsáv vagy label: „Fájl beolvasva”, „Konverzió folyamatban”, „Kész”.

---

## Kényelmi funkciók (v1.0 fölött)

* **Cover Letter Generator tab**

  * Sablonból automatikus generálás `.txt`-be.
  * Piacnév, műfaj, simsub igen/nem.

* **TypoKit tab**

  * Fájl kiválasztás → profil kiválasztása (HU/EN/DE).
  * Preview panel (bal: eredeti, jobb: normalizált).
  * Mentés új fájlként.

* **QuickConvert tab**

  * Drag&drop → `.docx/.odt/.md/.txt` teljes round-trip konverzió.
  * ODT fájlok natív támogatása (olvasás és írás).
  * Italic formázás megőrzése ODT konverziók során.
  * Automatikus kimenet "out" mappába.

* **CrossClip Lite**

  * Tray ikon: [ ] auto-clean clipboard.
  * „Paste Clean” → mindig formázásmentes + tipófix.
  * Light/dark mód váltó.

---

## Shell logikai rétegei

1. **UI Controller** (JavaFX `Controller` osztályok)

   * Input bekérés, események (gombnyomás, checkbox).
   * Fájl dialógusok.

2. **Service réteg**

   * Hívja az engine API-ját (`DocxReader`, `ShunnFormatter`, `DocxWriter`).
   * Hibakezelés → UI-n alert dialógus.

3. **Integration**

   * A shell jelenleg önállóan fut: a dokumentumkezelést közvetlenül az Apache POI (DOCX), ODF Toolkit (ODT) és flexmark (Markdown) könyvtárak végzik.
   * A `manusmith-engine` nem build-függőség; ha visszakerül, az engine API-ját a `service` réteg mögé kell kötni.

---

## Felhasználói élmény kulcsa

* **Egyszerű workflow:** Input → Metaadat → Generate → Output.
* **Kattintásszám minimalizálása.**
* **„Kész, működik” érzés:** azonnali preview vagy log nem kell, csak *biztonságos output*.

---

👉 Tehát a **shell feladata nem más**, mint a **grafikus keret** az engine körül:

* Fájlkiválasztás
* Metaadat-bekérés
* Opcionális beállítások
* Mentés és státuszvisszajelzés

---

## Fejlesztés

### Stack

| Komponens | Verzió |
| :--- | :--- |
| Java | 21 (LTS) |
| JavaFX | 23.0.2 (az utolsó JDK 21-et támogató sor) |
| Maven | 3.9+ (a repóban lévő `./mvnw` 3.9.11-et használ) |
| Apache POI | 6.0.0 |
| ODF Toolkit (odfdom) | 0.13.0 |
| flexmark | 0.64.8 |
| JUnit / TestFX | 6.1 / 4.0.18 |

### Parancsok

```bash
./mvnw clean verify -Ddependency-check.skip=true   # build + tesztek + SpotBugs + fat JAR
./mvnw test                                         # csak a tesztek (headless Monocle)
./mvnw -Prun-javafx javafx:run                      # futtatás fejlesztői módban (a profilban állítsd a javafx.platform-ot: linux, win, mac-aarch64)
java -jar target/manusmith-shell-2.0.0-jar-with-dependencies.jar
./mvnw org.owasp:dependency-check-maven:check       # OWASP függőség-ellenőrzés
```

Ékezetes fájlnevekkel futó tesztekhez UTF-8 locale kell (pl. `LC_ALL=C.UTF-8`); ennek hiányában az érintett teszt kihagyásra kerül.

### Konfiguráció

* Alapértelmezett beállítások: `src/main/resources/application.yml`.
* Felhasználói felülírás: `~/.manusmith-shell/config/application.yml` (ha létezik, ez töltődik be).
* Környezeti változók: `NVD_API_KEY` (opcionális, csak az OWASP-ellenőrzés gyorsításához; CI-ban secretként).
