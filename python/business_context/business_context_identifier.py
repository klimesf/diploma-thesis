import re


class BusinessContextIdentifier:
    _prefixed_pattern = re.compile('^([a-zA-Z0-9]+)\.([a-zA-Z0-9]+)$')
    _part_pattern = re.compile('^([a-zA-Z0-9]+)$')
    prefix: str
    name: str

    def __init__(self, prefix: str, name: str = None):
        if name is None:
            match = self._prefixed_pattern.match(prefix)
            if len(match.groups()) != 2:
                raise InvalidBusinessContextIdentifierException(prefix)
            self.prefix = match.group(1)
            self.name = match.group(2)
        else:
            if len(self._part_pattern.match(prefix).groups()) != 1:
                raise InvalidBusinessContextIdentifierException(prefix)
            if len(self._part_pattern.match(name).groups()) != 1:
                raise InvalidBusinessContextIdentifierException(name)
            self.prefix = prefix
            self.name = name


class InvalidBusinessContextIdentifierException(BaseException):
    identifier: str

    def __init__(self, identifier: str):
        self.identifier = identifier