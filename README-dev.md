# Sudoku

Soduko and Sudoku solver app.

## Copyright

(C) 2014-2023 Denis Meyer

## Development

### Prerequisites

* Java 17
* Gradle 7.3.3

## Exe + Setup

### Prerequisites

* Launch4j
* Inno Setup

### Wrapping

* Create folder "dist"
* Build the jar file via gradle task (build jar)
    * Put created jar file into folder "dist"
* Download JRE/JDK
    * Put folder into "dist/jre/jdk-name
* Build exe
    * Via Launch4j and file "resources/Sudoku-Launch4j.xml"
* Build Setup exe
    * Via Inno Setup and file "resources/Sudoku-Setup.iss"
