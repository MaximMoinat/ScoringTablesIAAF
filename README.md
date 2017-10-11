# Scoring Tables IAAF
This is a hobby project I started on at the end of 2016 and updated mid 2017 to include the 2017 tables. My goal was to once and for all elucidate the mysterious formula behind the 'Hungarian' scoring tables. In addition it gave me a nice practice invironment to implement my newly learned Java skills. Things I learned while developing this package:

 - Properly handling Java file input/output
 - Usage of the Apache POI package
 - Getting the most from Java Enums
 - Bit of regression analysis statistics
 - Basic usage of Maven to manage dependencies
 - Pdf as data input is terrible, but [Smallpdf](http://www.smallpdf.com) makes this easy.

## What does this package and how to use it?
Reads the IAAF scoring tables from excel files and writes the scoring tables per event in a machine readable format.
In addition, a second order polynomial regression is performed to calculate the constants of the underlying formula.
These constants are also written to a file. Output for the 2017 tables, both indoor and outdoor, is included.

After building, the script can be executed with the following code. 
```bash
java IaafScoring src/main/resources/input/ src/main/resources
```
The two parameters specificy the input path and output path, respectively.

The input is a `.xls` file containing the raw IAAF points table. This table is also given in this repository.

The script will create two folders in the output directory.
 - Tables of point-performance lookups per event/gender: [`src/main/resources/scoring_tables`](/src/main/resources/tables)
 - Tables with results of the regression analysis: [`src/main/resources/formula_constants`](/src/main/resources/constants) 

## Background and acknowledgements
The original tables in pdf format can be found on [the IAAF website](https://www.iaaf.org/about-iaaf/documents/technical).
This Hungarian scoring system can be used to compare the performances in different track and field events. They are e.g. used by all-athletics to score performances and are sometimes used at competitions to determine an all-round winner of the meet.

Note that the pdf to xls processing is done with Smallpdf. Some manual alterations were also made to the .xls files, to, e.g., fix faulty headers.

The package makes use of the PolynomialRegression class developed by Robert Sedgewick and Kevin Wayne and the [Apache poi package](https://poi.apache.org/)
