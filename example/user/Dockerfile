FROM filipklimes-diploma/nodejs

WORKDIR /usr/src/app
COPY . .
RUN yarn link "business-context-framework" && yarn link "business-context-grpc" && yarn link "business-context-xml" && yarn install

EXPOSE 5503
EXPOSE 5553
ENTRYPOINT ["npm", "run-script", "start"]
