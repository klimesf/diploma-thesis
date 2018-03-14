# Framework for aspect-driven & context-aware services

[![Build Status](https://travis-ci.org/klimesf/diploma-thesis.svg?branch=master)](https://travis-ci.org/klimesf/diploma-thesis)

Implementation of the framework for aspect-driven & context-aware services in Java, Python and JavaScript.

See framework in action in the [example e-commerce application](https://github.com/klimesf/diploma-thesis/tree/master/example).

## Modules

- `example` - Contains example e-commerce system implemented using the business context sharing framework.
- `java` - Java implementation of the framework following [Maven](https://maven.apache.org/) project structure
  - `business-context` - Implementation of framework for business contexts sharing in Java.
    - `src/main/java` - Source files in Java
    - `src/test/java` - Test files in Java
  - `business-context-aspectj` - Business context AOP weaver implemented in [AspectJ](https://www.eclipse.org/aspectj/) for the Java framework.
    - `src/main/java` - Source files in Java
    - `src/test/java` - Test files in Java
  - `business-context-grpc` - Business context communication implementation using [GRPC](https://grpc.io/) for the Java framework.
    - `src/main/java` - Source files in Java
    - `src/main/proto` - Protobuffer schemas
    - `src/test/java` - Test files in Java
- `nodejs` - [Node.js](https://nodejs.org/en/) implementation of the framework 
  - `business-context` – Implementation of framework for business contexts sharing in Node.js.
    - `src/` – Source files of the implementation
    - `test/` – Test files for the implementation using [mocha.js](https://mochajs.org/) and [chai.js](http://www.chaijs.com/) 
  - `business-context-grpc` – Business context communication implementation using [GRPC](https://grpc.io/) for the Node.js framework.
    - `src/` – Source files of the implementation
    - `test/` – Test files for the implementation using [mocha.js](https://mochajs.org/) and [chai.js](http://www.chaijs.com/) 
- `proto` – GRPC and Protobuffer definitions for business context framework communication
- `python` - Python implementation of the framework
  - `business_context` - Implementation of framework for business contexts sharing in Python.
    - `test_*.py` – Python unit test files for the framework implementation.
  - `business_context_grpc` - Business context communication implementation using [GRPC](https://grpc.io/) for the Python framework.
