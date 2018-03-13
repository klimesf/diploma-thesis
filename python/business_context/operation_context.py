class OperationContext:
    def __init__(self, name: str):
        self.name = name
        self._input_parameters = {}
        self._functions = {}
        self._output = None

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

    def get_output(self):
        return self._output

    def set_output(self, value: any):
        self._output = value


class UndefinedInputParameterException(BaseException):
    name: str

    def __init__(self, name: str):
        self.name = name


class UndefinedFunctionException(BaseException):
    name: str

    def __init__(self, name: str):
        self.name = name
