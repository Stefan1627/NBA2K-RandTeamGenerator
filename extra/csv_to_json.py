#!/usr/bin/env python3
"""
csv_to_json.py — Convert a CSV to JSON with high fidelity.

- Preserves text exactly (no type guessing, no NA/null coercion).
- Handles UTF-8 with BOM (utf-8-sig) by default.
- Auto-detects common delimiters and quote chars.
- Can optionally key the JSON by a column (e.g., id).

Usage:
  python csv_to_json.py input.csv output.json
  python csv_to_json.py input.csv output.json --key id
  python csv_to_json.py input.csv output.json --sep ';' --quotechar '"'
  python csv_to_json.py input.csv output.json --indent 2 --no-ensure-ascii
"""

import argparse
import csv
import json
from typing import Optional
import pandas as pd


def sniff_dialect(path: str, encoding: str = "utf-8-sig") -> Optional[csv.Dialect]:
    """
    Try to detect the CSV dialect using csv.Sniffer on a sample of the file.
    Returns a Dialect or None if detection fails.
    """
    try:
        with open(path, "r", encoding=encoding, newline="") as f:
            sample = f.read(65536)
        # Limit to common delimiters for stability.
        return csv.Sniffer().sniff(sample, delimiters=[",", ";", "\t", "|"])
    except Exception:
        return None


def to_keyed_records(df: pd.DataFrame, key_col: str, dedupe: str = "error") -> dict:
    """
    Convert a DataFrame to a dict keyed by key_col.
    dedupe: "error" (default), "first", or "last" — how to handle duplicate keys.
    """
    if key_col not in df.columns:
        raise ValueError(f"Key column '{key_col}' not found. Available columns: {list(df.columns)}")

    out = {}
    for _, row in df.iterrows():
        key = row[key_col]
        # Treat None/NaN as invalid keys
        if key is None or (isinstance(key, float) and pd.isna(key)):
            raise ValueError("Encountered a missing key value while building keyed JSON.")
        key = str(key)

        record = {col: row[col] for col in df.columns}
        if key in out:
            if dedupe == "first":
                continue
            elif dedupe == "last":
                out[key] = record
            else:
                raise ValueError(f"Duplicate key encountered: '{key}'")
        else:
            out[key] = record
    return out


def main():
    parser = argparse.ArgumentParser(description="Convert CSV to JSON accurately.")
    parser.add_argument("input", help="Path to the input CSV file")
    parser.add_argument("output", help="Path to the output JSON file")
    parser.add_argument("--encoding", default="utf-8-sig",
                        help="File encoding (default: utf-8-sig to handle BOM)")
    parser.add_argument("--sep", default=None,
                        help="Field delimiter (leave empty to auto-detect)")
    parser.add_argument("--quotechar", default=None,
                        help="Quote character (leave empty to auto-detect)")
    parser.add_argument("--key", default=None,
                        help="Optional column name to key the JSON by (produces an object/dict)")
    parser.add_argument("--dedupe", choices=["error", "first", "last"], default="error",
                        help="When using --key, how to handle duplicate keys (default: error)")
    parser.add_argument("--indent", type=int, default=2,
                        help="Pretty-print JSON with this indent (default: 2). Use 0 for compact.")
    parser.add_argument("--ensure-ascii", dest="ensure_ascii", action="store_true",
                        help="Escape non-ASCII characters (default: False)")
    parser.add_argument("--no-ensure-ascii", dest="ensure_ascii", action="store_false")
    parser.set_defaults(ensure_ascii=False)

    args = parser.parse_args()

    # Dialect detection if not provided
    dialect = None
    if args.sep is None or args.quotechar is None:
        dialect = sniff_dialect(args.input, encoding=args.encoding)

    sep = args.sep if args.sep is not None else (getattr(dialect, "delimiter", ",") if dialect else ",")
    quotechar = args.quotechar if args.quotechar is not None else (getattr(dialect, "quotechar", '"') if dialect else '"')

    # Read CSV with high fidelity: keep strings as strings, no NA conversion
    df = pd.read_csv(
        args.input,
        dtype=str,
        keep_default_na=False,   # do NOT convert strings like "NA" to NaN
        na_filter=False,         # treat empty fields as empty strings
        sep=sep,
        quotechar=quotechar,
        engine="python",         # needed for sep=None sniffing and general robustness
        encoding=args.encoding,
    )

    # Build JSON structure
    if args.key:
        json_obj = to_keyed_records(df, args.key, dedupe=args.dedupe)
    else:
        # List of records, preserving column order and row order
        json_obj = df.to_dict(orient="records")

    # Write JSON
    with open(args.output, "w", encoding="utf-8") as f:
        if args.indent and args.indent > 0:
            json.dump(json_obj, f, ensure_ascii=args.ensure_ascii, indent=args.indent)
        else:
            json.dump(json_obj, f, ensure_ascii=args.ensure_ascii, separators=(",", ":"))

    # Optional: print a brief summary to stdout
    print(f"Read CSV: rows={len(df)}, columns={len(df.columns)}")
    print(f"Detected sep='{sep}' quotechar='{quotechar}'" + (" (auto)" if dialect and args.sep is None else ""))
    print(f"Wrote JSON to: {args.output}")
    if args.key:
        print(f"Output format: object keyed by '{args.key}' (dedupe={args.dedupe})")
    else:
        print("Output format: array of records")


if __name__ == "__main__":
    main()
