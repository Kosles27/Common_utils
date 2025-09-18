"""Python port of the ``propertyUtils`` package."""

from .property import Property
from .property_utils import get_global_property, get_global_property_entity

__all__ = ["Property", "get_global_property", "get_global_property_entity"]
