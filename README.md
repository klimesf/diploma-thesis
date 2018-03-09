# Framework for aspect-driven & context-aware services

[![Build Status](https://travis-ci.org/klimesf/diploma-thesis.svg?branch=master)](https://travis-ci.org/klimesf/diploma-thesis)

Implementation of the framework for aspect-driven & context-aware services in Java, Golang and JavaScript.

See framework in action in the [example e-commerce application](https://github.com/klimesf/diploma-thesis/tree/master/example).

## Modules

- `example` - Contains example e-commerce system implemented using the business context sharing framework.
- `java` - Java implementation of the framework following [Maven](https://maven.apache.org/) project structure
  - `business-context` - Implementation of framework for business context rules sharing.
    - `src/main/java` - Source files in Java
    - `src/test/java` - Test files in Java
  - `business-context-aspectj` - Business context AOP weaver implemented in [AspectJ](https://www.eclipse.org/aspectj/) for the Java framework.
    - `src/main/java` - Source files in Java
    - `src/test/java` - Test files in Java
  - `business-context-grpc` - Business context communication implementation using [GRPC](https://grpc.io/) for the Java framework.
    - `src/main/java` - Source files in Java
    - `src/main/proto` - Protobuffer schemas
    - `src/test/java` - Test files in Java
