#!/usr/bin/env python3

import csv
import json
import argparse

def main():
    parser = argparse.ArgumentParser(
        description="Convert a semicolon‑delimited CSV file to a JSON array."
    )
    parser.add_argument(
        "csv_file",
        help="Path to the input CSV file"
    )
    parser.add_argument(
        "json_file",
        help="Path to the output JSON file"
    )
    args = parser.parse_args()

    data = []
    with open(args.csv_file, newline="", encoding="utf-8") as csvfile:
        # this CSV uses ';' as the separator and '"' as the quote character
        reader = csv.DictReader(csvfile, delimiter=";", quotechar='"')
        for row in reader:
            # convert the "id" field to an integer
            if "id" in row:
                try:
                    row["id"] = int(row["id"])
                except ValueError:
                    pass
            data.append(row)

    # write out a pretty‑printed JSON array
    with open(args.json_file, "w", encoding="utf-8") as jsonfile:
        json.dump(data, jsonfile, indent=4)

if __name__ == "__main__":
    main()
