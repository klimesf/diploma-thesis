'use strict'

const express = require('express'),
    app = express(),
    port = process.env.PORT || 5503,
    bodyParser = require('body-parser'),
    userRoutes = require('./routes/userRoutes'),
    businessContext = require('./businessContext')

// Business context
businessContext.setUp()

// Middlewares
app.use(bodyParser.urlencoded({extended: true}));
app.use(bodyParser.json());

// Routes
userRoutes(app)
app.get('*', (req, res) => {
    res.status(404)
    res.send()
});


// Run the express server
app.listen(port)
console.log('User service listening on port ' + port)
