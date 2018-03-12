class OperationContext:
    name: str
    _input_parameters = {}
    _functions = {}
    _output = None

    def __init__(self, name: str):
        self.name = name

    def get_input_parameter(self, name: str):
        if name not in self._input_parameters.keys():
            raise UndefinedInputParameterException(name)
        return self._input_parameters[name]

    def set_input_parameter(self, name: str, parameter):
        self._input_parameters[name] = parameter

    def get_function(self, name: str):
        if name not in self._functions.keys():
            raise UndefinedFunctionException(name)
        return self._functions[name]

    def set_function(self, name: str, func):
        self._functions[name] = func


class UndefinedInputParameterException(BaseException):
    name: str

    def __init__(self, name: str):
        self.name = name


class UndefinedFunctionException(BaseException):
    name: str

    def __init__(self, name: str):
        self.name = name
