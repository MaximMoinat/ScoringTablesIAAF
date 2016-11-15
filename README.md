# Scoring Tables IAAF
Java methods for parsing the IAAF scoring tables. The original .pdf tables can be found on [the IAAF website](https://www.iaaf.org/about-iaaf/documents/technical)<sup>1</sup>. This scoring system can be used to compare different track and field events, and are also referred to as 'Hungarian Points'. Currently the script has only been used for the 2014 Revised Edition (2014-04-07), for both outdoor and indoor.

Note that the pdf to xls processing is done with a third party tool. Some manual alterations were also made to the .xls files, to, e.g., fix faulty headers.

The package depends on the [Apache poi package](https://poi.apache.org/), which should be included by Maven. 

<sup>1</sup>By Dr Bojidar Spiriev, copyright IAAF 2014.
