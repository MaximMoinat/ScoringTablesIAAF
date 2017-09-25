# Scoring Tables IAAF
Parses the 'Hungarian' scoring tables from excel files and writes the scoring tables per event. 
In addition, a second order polynomial regression is executed to derive the constants of the underlying formula.
These constants are also written to a file. Output for the 2017 tables, both indoor and outdoor, is included.

After building, the script can be executed with:
```bash
java IaafScoring src/main/resources/
```
Output will be written to [`src/main/resources/formula_constants`](/src/main/resources/constants) and [`src/main/resources/scoring_tables`](/src/main/resources/tables)

The original .pdf tables can be found on [the IAAF website](https://www.iaaf.org/about-iaaf/documents/technical)<sup>1</sup>. 
This scoring system can be used to compare different track and field events, and are also referred to as 'Hungarian Points'. Currently the script has only been used for the 2014 Revised Edition (2014-04-07), for both outdoor and indoor.

Note that the pdf to xls processing is done with a third party tool. Some manual alterations were also made to the .xls files, to, e.g., fix faulty headers.

The package depends on the [Apache poi package](https://poi.apache.org/), which should be included by Maven. 

<sup>1</sup>By Dr Bojidar Spiriev, copyright IAAF 2014/2017.
