# Duplicated File Manager

## Description

This is a simple tool to find duplicated files in a given directory. It uses the MD-5 hash algorithm to compare files.
Using remote MySQL database, it is possible to compare files in shorter time.

## Usage

```bash
$ java -jar <jar-file> -d <directory> [-u(for update), -a(scan all files)]
```

It records time that the last scan was done, and when user runs the program again, 
it will scan only files that were modified after the last scan.