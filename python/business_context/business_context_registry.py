class BusinessContextRegistry:

    _local_loader: object
    _remote_loader: object
    _contexts = {}

    def __init__(self, local_loader, remote_loader):
        self._local_loader = local_loader
        self._remote_loader = remote_loader
        self.initialize()

    def initialize(self):
        # Load local contexts
        # Analyze and find out what to fetch
        # Fetch remote
        # Merge remote into local
        # TODO: implement
        pass
