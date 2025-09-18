"""Utility helpers mirroring the Java ``PropertyUtils`` class."""

from __future__ import annotations

from typing import Optional

from .property import Property


def get_global_property_entity(path_to_property: Optional[str] = None) -> Property:
    """Return a :class:`Property` instance for the global properties file."""

    return Property(path_to_property)


def get_global_property(key: str) -> Optional[str]:
    """Fetch a value from the global properties file by ``key``."""

    return Property().get_property(key)


__all__ = ["get_global_property", "get_global_property_entity", "Property"]
