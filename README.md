# Tetris Reloaded

Eine klassische Tetris-Implementierung als JavaFX-Anwendung der **HTL Steyr**.

## Inhaltsverzeichnis

- [Über das Projekt](#über-das-projekt)
- [Voraussetzungen](#voraussetzungen)
- [Installation und Start](#installation-und-start)
- [Spielanleitung](#spielanleitung)
- [Funktionen](#funktionen)
- [Standardsteuerung](#standardsteuerung)
- [Technologien](#technologien)
- [Projektstruktur](#projektstruktur)
- [Team](#team)

## Über das Projekt

In **Tetris Reloaded** werden fallende Spielsteine so angeordnet, dass vollständige
waagrechte Reihen entstehen. Eine vollständige Reihe wird entfernt und erhöht den
Punktestand. Mit zunehmendem Level fallen die Steine schneller. Das Spiel endet,
wenn kein Platz mehr für einen neuen Stein vorhanden ist.

Vor dem Spielen meldet sich der Benutzer an oder erstellt ein neues Konto. Die
persönlichen Daten und Einstellungen werden lokal im Projektverzeichnis gespeichert.

## Voraussetzungen

- Java Development Kit (JDK) **21**
- Maven (alternativ kann der mitgelieferte Maven Wrapper verwendet werden)
- Eine Desktop-Umgebung, die JavaFX ausführen kann

## Installation und Start

Repository klonen und in das Projektverzeichnis wechseln:

```bash
git clone https://github.com/arogantxr/TetrisReloaded.git
cd TetrisReloaded
```

Anwendung mit Maven starten:

```bash
mvn clean javafx:run
```

Unter Windows kann alternativ der Maven Wrapper verwendet werden:

```powershell
.\mvnw.cmd clean javafx:run
```

Nach dem Start öffnet sich der Anmeldebildschirm. Mit **Registrieren** wird ein
neues lokales Benutzerkonto erstellt; mit **Anmelden** kann ein bestehendes Konto
verwendet werden.

## Spielanleitung

1. Im Hauptmenü **Spielen** auswählen.
2. Die fallenden Steine während des Countdowns vorbereiten und im Spielfeld platzieren.
3. Vollständige Reihen bilden, um Punkte zu erhalten und im Level aufzusteigen.
4. Nach dem Spiel wird der erreichte Punktestand angezeigt und in die Bestenliste
   übernommen.

Im Menü **Optionen** können die Lautstärke, das Farbschema und die Tastenbelegung
angepasst werden. Außerdem lassen sich die Vorschau des nächsten Steins und die
Hold-Funktion ein- oder ausschalten. Änderungen werden pro Benutzer gespeichert.

## Funktionen

- Klassisches Tetris-Spielfeld mit fallenden Spielsteinen
- Punktestand, Linienanzeige und Levelsystem
- Vorschau auf den nächsten Stein
- Hold-Funktion zum Zwischenspeichern eines Steins
- Persönliche & Gerät Bestenliste
- Lokale Registrierung und Anmeldung
- Passwortspeicherung als Hash statt im Klartext
- Individuelle Tastenbelegung
- Musik- und Soundeffekt-Lautstärke
- Mehrere Hintergrundtracks
- Dark Mode und Light Mode
- Zurücksetzen persönlicher Einstellungen und der globalen Bestenliste
- Responsive Spielfeldgröße bei Änderung der Fenstergröße

## Standardsteuerung

| Aktion | Taste |
| --- | --- |
| Nach links bewegen | `A` |
| Nach rechts bewegen | `D` |
| Nach unten bewegen / Soft Drop | `S` |
| Nach links drehen | `Q` |
| Nach rechts drehen | `E` |
| Stein halten (Hold) | `C` |
| Stein sofort fallen lassen (Hard Drop) | `Leertaste` |
| Spiel pausieren | `P` |

Die Tasten können im Menü **Optionen** neu zugewiesen werden. Eine Taste kann
nicht gleichzeitig für mehrere Aktionen verwendet werden.

## Technologien

- **Java 21** – Spiellogik und Anwendungscode
- **JavaFX 21** – Benutzeroberfläche und Eingabeverarbeitung
- **FXML** – Struktur der Ansichten
- **CSS** – Dark Mode, Light Mode und visuelles Design
- **Gson** – Speichern und Laden der lokalen Benutzerdaten
- **Maven** – Abhängigkeiten und Build-Prozess
- **JUnit 5** – Testbasis

## Projektstruktur

```text
src/main/java/       Java-Quellcode und Spiellogik
src/main/resources/  FXML-Ansichten, Stylesheets und Mediendateien
users/               lokal gespeicherte Benutzerprofile
data/                lokal gespeicherte Bestenliste
pom.xml              Maven-Konfiguration und Abhängigkeiten
```

Die Verzeichnisse `users/` und `data/` enthalten lokale Spieldaten. Sie werden
beim Spielen automatisch verwendet beziehungsweise angelegt.

## Team

- Paul Platzer 
- Luca Kohberger
- Fabian Schmidberger
- Leon Milicevic 
- Seiler Clemens
- Fabian Obermayer
- Ganadene Erdenebayer

---

Dies ist ein Schulprojekt der **HTL Steyr**.

![Logo der HTL Steyr](https://upload.wikimedia.org/wikipedia/commons/8/8e/HTL_Steyr_Logo_quadr_blau_3c.jpg)
