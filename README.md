# Common Utils

This repository contains the legacy Java automation toolkit alongside a nascent Python
port.  The Python port mirrors the Java package layout so that modules can be translated
incrementally without losing context or regression coverage.

## Python scaffolding

- `common_utils/` &ndash; placeholder Python packages that mirror `src/main/java`.
- `tests/` &ndash; initial pytest suite validating that the scaffolding stays aligned with the
  Java layout.
- `pyproject.toml` &ndash; dependency mapping and tooling configuration for the Python stack.

## Getting started

1. Create a virtual environment:
   ```bash
   python -m venv .venv
   source .venv/bin/activate
   ```
2. Install the project with the primary extras used by the automation toolkit:
   ```bash
   pip install -e .[dev,automation,file_formats,networking,reporting]
   ```
   The optional `datastores` extra pulls in `pyodbc`, which may require system-level ODBC
   headers (e.g., `apt-get install unixodbc-dev` on Debian/Ubuntu) before installation.
3. Run the quality gates:
   ```bash
   ruff check .
   pytest
   ```

## Continuous integration

A GitHub Actions workflow at `.github/workflows/python-ci.yml` installs the toolchain and
runs `ruff` plus `pytest` on every push and pull request that touches the Python files or
configuration.  This ensures that each newly ported module starts with regression
coverage from the outset.
