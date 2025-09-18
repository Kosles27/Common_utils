"""Regression scaffolding for the Python port of the common utilities toolkit."""

from __future__ import annotations

import importlib
from pathlib import Path

import pytest

JAVA_ROOT = Path("src/main/java")
PYTHON_ROOT = Path("common_utils")


def _java_package_paths() -> list[Path]:
    """Collect all Java package directories relative to the Java source root."""
    packages: list[Path] = []
    for candidate in JAVA_ROOT.rglob("*"):
        if candidate.is_dir():
            relative = candidate.relative_to(JAVA_ROOT)
            if relative.parts:
                packages.append(relative)
    return packages


def test_python_package_structure_matches_java() -> None:
    """Each Java package should already have a Python placeholder."""
    missing: list[str] = []
    for relative in _java_package_paths():
        python_dir = PYTHON_ROOT / relative
        if not python_dir.is_dir():
            missing.append(str(relative))
            continue
        if not (python_dir / "__init__.py").exists():
            missing.append(f"{relative} (missing __init__.py)")
    assert not missing, (
        "The Python scaffolding is incomplete for: " + ", ".join(sorted(missing))
    )


@pytest.mark.parametrize(
    "package_name",
    [
        "common_utils.collectionUtils",
        "common_utils.propertyUtils",
        "common_utils.reportUtils",
        "common_utils.seleniumUtils",
    ],
)
def test_high_priority_packages_are_importable(package_name: str) -> None:
    """Basic smoke test confirming the high-priority packages can be imported."""
    module = importlib.import_module(package_name)
    assert module.__doc__, f"{package_name} is missing a module docstring"
