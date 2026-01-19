LuLuNiPhi 2.0

LuLuNiPhi 2.0 ist eine Java-Konsolenanwendung zur Verwaltung von Mitarbeitenden, deren Skills und Zertifikaten.
Das Projekt wurde im Rahmen einer Hochschulveranstaltung entwickelt und legt besonderen Wert auf saubere Architektur, Schichtentrennung und nachvollziehbares Domänenmodell.

Funktionsübersicht

Die Anwendung ermöglicht:

Verwalten von Mitarbeitenden

Anlegen, Anzeigen und Löschen

Automatische Generierung von E-Mail-Adressen

Anzeige von Rolle, Status und Arbeitszeit

Verwalten von Skills

Anlegen und Anzeigen

Kategorisierung von Skills

Zuweisen von Skills zu Mitarbeitenden

Festlegen von Skill-Level

Hinterlegen eines „since“-Datums

Verwalten von Zertifikaten

Zuordnung zu Mitarbeitenden

Anzeige von Aussteller und Gültigkeit

Übersichtliche tabellarische Konsolenausgabe

Statusanzeige farblich hervorgehoben (ANSI)


Das Projekt folgt einer klaren Schichtenarchitektur:

App → UI → Services → Repositories → Domain → SQLite


de.htwsaar
├── application        // Geschäftslogik (Services)
├── communication      // Konsolen-UI
├── domain             // Domänenklassen
├── repository         // Datenzugriff (SQLite)
├── util               // Hilfsklassen (Formatierung, Migration, Validierung)
└── App.java           // Programmeinstieg


Architekturprinzipien

Domain ist DB-agnostisch (keine SQL-Logik)

Services enthalten Geschäftslogik, keine UI-Ausgaben

Repositories kapseln den Datenbankzugriff

UI ist für Darstellung & Benutzereingaben zuständig

Eine zentrale Datenbankverbindung (SQLConnection) als Single Source of Truth


Domänenmodell
Mitarbeitende & Skills

Ein Mitarbeitender kann mehrere Skills besitzen, ein Skill kann mehreren Mitarbeitenden zugeordnet sein.

Da die Beziehung selbst zusätzliche Attribute besitzt (z. B. Level, Datum), wird sie als eigene Domänenklasse modelliert:

Employee

Skill

AssignedSkill / EmployeeSkill (Join-Domain)

➡️ Die Zuordnung selbst ist ein fachliches Objekt.

Datenbank

SQLite als relationale Datenbank

Datenbankdatei liegt außerhalb des Source-Verzeichnisses

Wichtige Punkte

Die DB wird nicht im src/-Ordner gespeichert

Alle Komponenten verwenden dieselbe Connection über SQLConnection

Initiales Schema wird über DatabaseMigration erzeugt

Starten der Anwendung
Voraussetzungen

Java 17 (oder kompatibel)

IntelliJ IDEA (empfohlen) oder andere Java-IDE

SQLite JDBC-Treiber (über Projekt eingebunden)

Start

Projekt öffnen

Sicherstellen, dass der Ordner data/ existiert

App.java ausführen

Hinweis:
Für farbige Statusanzeigen muss in IntelliJ die Option
“Enable ANSI color output” aktiviert sein.

Statusdarstellung

Mitarbeiterstatus wird in der Konsole farblich dargestellt:

Status	Farbe
ACTIVE	Grün
INACTIVE	Gelb
ON_LEAVE	Rot

Die Formatierung erfolgt ausschließlich in der UI-/Util-Schicht.

Verwendete Technologien

Java

SQLite

JDBC

ANSI Escape Codes (Konsolenformatierung)

Lern- & Projektziele

Anwendung einer sauberen Mehrschichtenarchitektur

Modellierung von Many-to-Many-Beziehungen mit Zusatzdaten

Trennung von Fachlogik, Persistenz und Darstellung

Praktische Arbeit mit SQLite und JDBC

Strukturierter, wartbarer Java-Code

Projektkontext

Das Projekt wurde im Rahmen einer Lehrveranstaltung an der
Hochschule für Technik und Wirtschaft des Saarlandes (HTW Saar) entwickelt.


@Lukas Dreistadt, Marc-Lukas Schäfer, Niklas Guth, Philipp Sornberger