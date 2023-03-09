# Sudoku

Soduko and Sudoku solver app.

## Copyright

(C) 2014-2023 Denis Meyer

## Features

* Play Sudoku
* "Hint" a next possible step
* Check whether Sudoku is valid and solvable
* Solve entire Sudoku
* 4 difficulties
* Save and load games

## Screenshot

![Screenshot](img/screenshot.png?raw=true)

## Development

### Prerequisites

* Java 17
* Gradle 7.3.3

## Exe + Setup

### Prerequisites

* Launch4j
* Inno Setup

### Wrappign

* Create folder "dist"
* Build the jar file via gradle task
    * Put jar file into "dist"
* Download JRE/JDK
    * Put folder into "dist/jre/jdk-name
* Build exe
    * Via Launch4j and file "resources/Sudoku-Launch4j.xml"
* Build Setup exe
    * Via Inno Setup and file "resources/Sudoku-Setup.iss"
