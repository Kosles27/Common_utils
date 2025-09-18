"""Python implementation of the Java ``propertyUtils.Property`` helper."""

from __future__ import annotations

import logging
from pathlib import Path
from typing import Dict, Iterable, Optional, Set

_LOGGER = logging.getLogger(__name__)


class Property:
    """Utility class that loads and exposes ``.properties`` style files."""

    _DEFAULT_GLOBAL_FILE = "zim.global.properties"

    def __init__(self, path_to_property: Optional[str] = None) -> None:
        self._properties: Dict[str, str] = {}
        self._load_properties(path_to_property)

    def _load_properties(self, path_to_property: Optional[str]) -> None:
        candidate_paths = []
        if path_to_property:
            candidate_paths.append(Path(path_to_property))
        candidate_paths.append(self._default_property_path())

        for candidate in candidate_paths:
            if candidate and candidate.exists():
                try:
                    self._properties = self._parse_property_file(candidate)
                    return
                except OSError as exc:
                    _LOGGER.info("Could not initialize property file from %s", candidate)
                    _LOGGER.info("%s", exc)
        _LOGGER.info("Could not initialize property file")

    def _default_property_path(self) -> Path:
        project_root = Path(__file__).resolve().parents[2]
        return project_root / "resources" / self._DEFAULT_GLOBAL_FILE

    def _parse_property_file(self, path: Path) -> Dict[str, str]:
        properties: Dict[str, str] = {}
        with path.open("r", encoding="utf-8") as handle:
            for raw_line in handle:
                line = raw_line.strip()
                if not line or line.startswith("#") or line.startswith("!"):
                    continue
                if "=" in line:
                    key, value = line.split("=", 1)
                elif ":" in line:
                    key, value = line.split(":", 1)
                else:
                    parts = line.split()
                    if len(parts) < 2:
                        continue
                    key, value = parts[0], " ".join(parts[1:])
                properties[key.strip()] = value.strip()
        return properties

    def get_property(self, key: str) -> Optional[str]:
        return self._properties.get(key)

    def set_property(self, key: str, value: str) -> None:
        self._properties[key] = value

    def remove_property(self, key: str) -> None:
        self._properties.pop(key, None)

    def get_int_property(self, key: str) -> Optional[int]:
        value = self.get_property(key)
        if value is None:
            return None
        try:
            return int(value)
        except ValueError as exc:
            _LOGGER.info(
                "Value of key: %s is: %s can't parse it to int", key, value
            )
            _LOGGER.info("%s", exc)
            return None

    def get_double_property(self, key: str) -> Optional[float]:
        value = self.get_property(key)
        if value is None:
            return None
        try:
            return float(value)
        except ValueError as exc:
            _LOGGER.info(
                "Value of key: %s is: %s can't parse it to double", key, value
            )
            _LOGGER.info("%s", exc)
            return None

    def get_boolean_property(self, key: str) -> bool:
        value = self.get_property(key)
        return str(value).lower() == "true"

    def get_all_keys_in_property_file(self) -> Set[str]:
        return set(self._properties.keys())

    def keys(self) -> Iterable[str]:
        return self._properties.keys()

    def items(self) -> Iterable[tuple[str, str]]:
        return self._properties.items()

    def values(self) -> Iterable[str]:
        return self._properties.values()
