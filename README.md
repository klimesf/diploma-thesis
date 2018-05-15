# Framework for aspect-driven & context-aware services

[![Build Status](https://travis-ci.org/klimesf/diploma-thesis.svg?branch=master)](https://travis-ci.org/klimesf/diploma-thesis)

Library prototypes of the framework for aspect-driven & context-aware services in Java, Python and JavaScript.

See the framework in action in the [example e-commerce application](https://github.com/klimesf/diploma-thesis/tree/master/example).

For library user manual, please refer to the specific implementation folders `java/`, `nodejs/` or `python/`. 

## Modules

- `central-administration` - Central business context administration for the framework written in Java.
  - `src/main/java` - Source files in Java
  - `src/test/java` - Test files in Java
- `example` - Contains example e-commerce system implemented using the business context sharing framework.
  - `billing` - Billing service in Java using [Spring Boot](https://projects.spring.io/spring-boot/)
    - `src/main/java` - Source files in Java
    - `src/test/java` - Test files in Java
  - `order` - Order composite service in Java using [Spring Boot](https://projects.spring.io/spring-boot/)
      - `src/main/java` - Source files in Java
      - `src/test/java` - Test files in Java
  - `product` - Product service in Python using [Flask](http://flask.pocoo.org/)
  - `shipping` - Shipping service in Java using [Spring Boot](https://projects.spring.io/spring-boot/)
      - `src/main/java` - Source files in Java
      - `src/test/java` - Test files in Java
  - `ui` - User interface of the e-commerce system in Java using [Spring Boot](https://projects.spring.io/spring-boot/)
      - `src/main/java` - Source files in Java
      - `src/test/java` - Test files in Java
  - `user` - User service in Node.js using [Express.js](https://expressjs.com/)
- `java` - Java implementation of the framework following [Maven](https://maven.apache.org/) project structure
  - `business-context` - Implementation of framework for business contexts sharing in Java.
    - `src/main/java` - Source files in Java
    - `src/test/java` - Test files in Java
  - `business-context-aspectj` - Business context AOP weaver implemented in [AspectJ](https://www.eclipse.org/aspectj/) for the Java library.
    - `src/main/java` - Source files in Java
    - `src/test/java` - Test files in Java
  - `business-context-grpc` - Business context communication implementation using [GRPC](https://grpc.io/) for the Java library.
    - `src/main/java` - Source files in Java
    - `src/main/proto` - Protobuffer schemas
    - `src/test/java` - Test files in Java
  - `business-context-xml` - Business context XML description for the Java library.
    - `src/main/java` - Source files in Java
    - `src/test/java` - Test files in Java
- `nodejs` - [Node.js](https://nodejs.org/en/) implementation of the framework 
  - `business-context` – Implementation of framework for business contexts sharing in Node.js.
    - `src/` – Source files of the implementation
    - `test/` – Test files for the implementation using [mocha.js](https://mochajs.org/) and [chai.js](http://www.chaijs.com/) 
  - `business-context-grpc` – Business context communication implementation using [GRPC](https://grpc.io/) for the Node.js library.
    - `src/` – Source files of the implementation
    - `test/` – Test files for the implementation using [mocha.js](https://mochajs.org/) and [chai.js](http://www.chaijs.com/) 
  - `business-context-xml` – Business context XML description for the Node.js library.
    - `src/` – Source files of the implementation
    - `test/` – Test files for the implementation using [mocha.js](https://mochajs.org/) and [chai.js](http://www.chaijs.com/) 
- `proto` – GRPC and Protobuffer definitions for business context framework communication
- `python` - Python implementation of the framework
  - `business_context` - Implementation of framework for business contexts sharing in Python.
    - `test_*.py` – Python unit test files.
  - `business_context_grpc` - Business context communication implementation using [GRPC](https://grpc.io/) for the Python library.
    - `test_*.py` – Python unit test files.
  - `business_context_xml` - Business context XML description for the Python library.
    - `test_*.py` – Python unit test files.
- `xml` - XML schema and example of business context representation

## Building the libraries and running the example

You can build all the libraries at once and run the example e-commerce system simply
by running the `build-and-run-all.sh` script.

Make sure your Docker deamon is running and has enough RAM (about 2GB should suffice).

```bash
$ ./build-and-run-all.sh
```

## Testing

For testing, you can see the `.travis.yml` file and use the commands listed there.
Link to results of CI tests are on the top of this page.
You can also refer to the READMEs in the specific library for concrete testing commands.

## Licence

This software is provided under [MIT License](https://opensource.org/licenses/MIT).

