#!/usr/bin/env python3
"""
players_csv_to_json.py — Convert a semicolon-delimited players CSV to JSON accurately.

- Preserves text exactly (no type guessing, no NA/null coercion).
- Defaults to cp1252 encoding (common for smart quotes like De’Andre).
- Defaults to semicolon as the delimiter and double-quote as the quote char.
- Supports:
    * array of records (default)
    * object keyed by a column (--key)
    * groups: map a column to list of records (--group-by)

Usage examples:
  # Basic conversion (array of records)
  python players_csv_to_json.py players.csv players.json

  # Explicitly set delimiter/encoding if needed
  python players_csv_to_json.py players.csv players.json --sep ';' --quotechar '"' --encoding cp1252

  # Key the JSON by 'id'
  python players_csv_to_json.py players.csv players_by_id.json --key id

  # Group players by 'team_id'
  python players_csv_to_json.py players.csv players_by_team.json --group-by team_id
"""

import argparse
import json
from typing import Dict, List, Any
import pandas as pd


def to_keyed(df: pd.DataFrame, key_col: str, dedupe: str = "error") -> Dict[str, Dict[str, Any]]:
    """
    Build a dict keyed by `key_col`.
    dedupe: 'error' (default) | 'first' | 'last' to handle duplicate keys.
    """
    if key_col not in df.columns:
        raise ValueError(f"Key column '{key_col}' not found. Columns: {list(df.columns)}")

    out: Dict[str, Dict[str, Any]] = {}
    for _, row in df.iterrows():
        key = str(row[key_col])
        rec = {col: row[col] for col in df.columns}
        if key in out:
            if dedupe == "first":
                continue
            elif dedupe == "last":
                out[key] = rec
            else:
                raise ValueError(f"Duplicate key encountered: {key!r}")
        else:
            out[key] = rec
    return out


def to_grouped(df: pd.DataFrame, group_col: str) -> Dict[str, List[Dict[str, Any]]]:
    """
    Build a dict mapping each distinct value of `group_col` to a list of row records.
    """
    if group_col not in df.columns:
        raise ValueError(f"Group column '{group_col}' not found. Columns: {list(df.columns)}")

    out: Dict[str, List[Dict[str, Any]]] = {}
    for val, grp in df.groupby(group_col, dropna=False, sort=False):
        key = "" if val is None else str(val)
        out[key] = grp.to_dict(orient="records")
    return out


def main():
    p = argparse.ArgumentParser(description="Convert players CSV to JSON with high fidelity.")
    p.add_argument("input", help="Path to the players CSV (semicolon-delimited).")
    p.add_argument("output", help="Path to the output JSON file.")
    p.add_argument("--encoding", default="cp1252",
                   help="File encoding (default: cp1252; try 'utf-8-sig' if needed).")
    p.add_argument("--sep", default=";", help="Field delimiter (default: ';').")
    p.add_argument("--quotechar", default='"', help='Quote character (default: ").')
    p.add_argument("--indent", type=int, default=2,
                   help="Pretty-print JSON with this indent (default: 2). Use 0 for compact.")
    p.add_argument("--ensure-ascii", dest="ensure_ascii", action="store_true",
                   help="Escape non-ASCII characters (default: False).")
    p.add_argument("--no-ensure-ascii", dest="ensure_ascii", action="store_false")
    p.set_defaults(ensure_ascii=False)

    # Output shape options
    p.add_argument("--key", default=None,
                   help="Column to key the JSON object by (e.g., id).")
    p.add_argument("--dedupe", choices=["error", "first", "last"], default="error",
                   help="When using --key, how to handle duplicate keys (default: error).")
    p.add_argument("--group-by", dest="group_by", default=None,
                   help="Column to group by (e.g., team_id) -> { value: [records...] }")

    args = p.parse_args()

    # Read CSV with high fidelity: keep strings as strings, no NA conversion
    df = pd.read_csv(
        args.input,
        dtype=str,
        keep_default_na=False,   # do NOT convert 'NA' to NaN
        na_filter=False,         # empty fields become empty strings
        sep=args.sep,
        quotechar=args.quotechar,
        encoding=args.encoding,
        engine="python",
    )

    # Build JSON structure
    if args.key and args.group_by:
        raise ValueError("Choose either --key or --group-by, not both.")
    if args.key:
        json_obj = to_keyed(df, args.key, dedupe=args.dedupe)
    elif args.group_by:
        json_obj = to_grouped(df, args.group_by)
    else:
        json_obj = df.to_dict(orient="records")

    # Write JSON
    with open(args.output, "w", encoding="utf-8") as f:
        if args.indent and args.indent > 0:
            json.dump(json_obj, f, ensure_ascii=args.ensure_ascii, indent=args.indent)
        else:
            json.dump(json_obj, f, ensure_ascii=args.ensure_ascii, separators=(",", ":"))

    # Console summary
    print(f"Wrote JSON to: {args.output}")
    if args.key:
        print(f"Output: object keyed by '{args.key}' (dedupe={args.dedupe})")
    elif args.group_by:
        print(f"Output: grouped by '{args.group_by}'")
    else:
        print("Output: array of records")


if __name__ == "__main__":
    main()
