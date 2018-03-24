# User service

## Building & running

Make sure you have [Node.js](https://nodejs.org/en/) and [Yarn](https://yarnpkg.com/) installed.
First, navigate to the node.js framework implementations and make sure you can link them
```bash
cd ../../nodejs/business-context
npm run-script build
yarn link
cd ../business-context-grpc
npm run-script build
yarn link
cd ../../example/user
```

Then you can install dependencies

```bash
yarn install
yarn link business-context-framework
yarn link business-context-grpc
```

Finally, you can start the application with `npm`

```bash
npm run-script start
```

User service should now be running on its respective port.

```
$> npm start
Business context server listening on port 5553
User service listening on port 5503
```
