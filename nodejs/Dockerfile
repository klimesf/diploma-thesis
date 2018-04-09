FROM library/node:9.11.1

WORKDIR /usr/src/framework
COPY ./nodejs/business-context ./business-context
COPY ./nodejs/business-context-grpc ./business-context-grpc
COPY ./proto ./proto

RUN cd ./business-context \
    && yarn install \
    && yarn link \
    && npm run-script build \
    && cd ../business-context-grpc \
    && yarn install \
    && yarn link business-context-framework \
    && yarn link \
    && npm run-script build
