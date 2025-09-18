"""Tests for the Python ``Property`` implementation."""

from __future__ import annotations

from pathlib import Path

import pytest
from common_utils.propertyUtils import (
    Property,
    get_global_property,
    get_global_property_entity,
)


def test_default_properties_file_is_loaded() -> None:
    prop = Property()

    assert prop.get_property("timeout") == "30"
    assert prop.get_int_property("timeout") == 30


def test_custom_property_file_overrides_default(tmp_path: Path) -> None:
    custom_file = tmp_path / "custom.properties"
    custom_file.write_text("answer=42\nflag=true\nratio=3.5\n", encoding="utf-8")

    prop = Property(str(custom_file))

    assert prop.get_property("answer") == "42"
    assert prop.get_int_property("answer") == 42
    assert prop.get_double_property("ratio") == pytest.approx(3.5)
    assert prop.get_boolean_property("flag") is True
    assert prop.get_all_keys_in_property_file() == {"answer", "flag", "ratio"}


def test_missing_key_returns_none() -> None:
    prop = Property()

    assert prop.get_property("missing") is None
    assert prop.get_int_property("missing") is None
    assert prop.get_double_property("missing") is None
    assert prop.get_boolean_property("missing") is False


def test_invalid_numeric_values_return_none(tmp_path: Path) -> None:
    custom_file = tmp_path / "invalid.properties"
    custom_file.write_text("value=not-a-number\n", encoding="utf-8")

    prop = Property(str(custom_file))

    assert prop.get_int_property("value") is None
    assert prop.get_double_property("value") is None


def test_global_property_helpers() -> None:
    prop = get_global_property_entity()

    assert isinstance(prop, Property)
    assert get_global_property("timeout_page_load") == "120"
