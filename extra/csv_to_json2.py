#!/usr/bin/env python3

import csv
import json
import argparse
import sys

def read_csv_with_fallback(path, delimiter=';', quotechar='"', encodings=('utf-8', 'latin-1')):
    """
    Try to open the CSV file with the given list of encodings,
    returning a list of rows (as dicts) on first success.
    Exits with an error if none of the encodings work.
    """
    for enc in encodings:
        try:
            with open(path, newline='', encoding=enc) as csvfile:
                reader = csv.DictReader(csvfile, delimiter=delimiter, quotechar=quotechar)
                return list(reader)
        except UnicodeDecodeError:
            # try the next encoding
            continue

    print(f"Error: could not decode '{path}' using any of {encodings}", file=sys.stderr)
    sys.exit(1)

def main():
    parser = argparse.ArgumentParser(
        description="Convert a semicolon‑delimited players CSV to JSON, with encoding fallback."
    )
    parser.add_argument(
        "csv_file",
        help="Path to the input players CSV file"
    )
    parser.add_argument(
        "json_file",
        help="Path to the output JSON file"
    )
    args = parser.parse_args()

    # Read CSV, trying UTF-8 then Latin‑1
    rows = read_csv_with_fallback(args.csv_file)

    # Convert certain fields to integers
    numeric_fields = ("id", "ovr", "team_id")
    data = []
    for row in rows:
        for field in numeric_fields:
            if field in row and row[field] != "":
                try:
                    row[field] = int(row[field])
                except ValueError:
                    pass
        data.append(row)

    # Write JSON
    with open(args.json_file, "w", encoding="utf-8") as jsonfile:
        json.dump(data, jsonfile, indent=4)

if __name__ == "__main__":
    main()
