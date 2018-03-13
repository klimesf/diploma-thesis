from typing import Set, Dict
from business_context.context import BusinessContext
from business_context.identifier import Identifier


class LocalBusinessContextLoader:
    def __init__(self):
        pass

    def load(self) -> Set[BusinessContext]:
        # TODO: implement
        return {
            BusinessContext(Identifier('user.create'), set(), set(), set()),
            BusinessContext(Identifier('user.create'), set(), set(), set()),
        }


class RemoteLoader:
    def load_contexts(self, identifiers: Set[Identifier]) -> Set[BusinessContext]:
        pass


class RemoteBusinessContextLoader:
    def __init__(self, loader: RemoteLoader):
        self._loader = loader

    def load_contexts_by_identifier(self, identifiers: Set[Identifier]) -> Dict[Identifier, BusinessContext]:
        contexts = {}
        for context in self._loader.load_contexts(identifiers):
            contexts[context.identifier] = context
        return contexts


class Registry:
    def __init__(self, local_loader: LocalBusinessContextLoader, remote_loader: RemoteBusinessContextLoader):
        self._local_loader = local_loader
        self._remote_loader = remote_loader
        self._contexts = {}
        self.initialize()

    def initialize(self):
        """
        Initializes the registry in four steps
        - loads the local business contexts
        - finds out which included contexts are needed to be fetched from remote sources
        - fetches the remote contexts
        - merges them into the local ones

        :return: None
        """
        local_contexts = {}
        for context in self._local_loader.load():
            identifier = context.identifier
            if identifier in local_contexts:
                raise DuplicatedBusinessContext(context.identifier)
            local_contexts[identifier] = context

        required_contexts = set()
        for context in local_contexts.values():
            for included_identifier in context.included_contexts:
                if included_identifier not in local_contexts:
                    required_contexts.add(included_identifier)

        remote_contexts = self._remote_loader.load_contexts_by_identifier(required_contexts)
        for context in local_contexts.values():
            for included_identifier in context.included_contexts:
                if included_identifier in local_contexts:
                    context.merge(local_contexts[included_identifier])
                    continue
                if included_identifier not in remote_contexts:
                    raise BusinessContextNotFound(included_identifier)
                context.merge(remote_contexts[included_identifier])
            self._contexts[context.identifier] = context

    def get_context_by_identifier(self, identifier: Identifier) -> BusinessContext:
        """
        Finds and returns business context with given identifier or raises
        BusinessContextNotFound if such identifier is not defined within the registry.

        :param identifier: identifier of the business contexts
        :return: the business context
        """
        if identifier not in self._contexts:
            raise BusinessContextNotFound(identifier)
        return self._contexts[identifier]

    def get_contexts_by_identifiers(self, identifiers: iter) -> set:
        """
        Finds and returns business contexts with given identifiers or raises
        BusinessContextNotFound if any identifier is not defined within the registry.

        :param identifiers: identifiers of the business contexts
        :return: the business contexts
        """
        found = set()
        for identifier in identifiers:
            if identifier not in self._contexts:
                raise BusinessContextNotFound(identifier)
            found.add(self._contexts[identifier])
        return found


class DuplicatedBusinessContext(BaseException):
    def __init__(self, identifier: Identifier):
        self.identifier = identifier


class BusinessContextNotFound(BaseException):
    def __init__(self, identifier: Identifier):
        self.identifier = identifier
