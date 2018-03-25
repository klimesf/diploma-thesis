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
app.use(bodyParser.urlencoded({extended: true}))
app.use(bodyParser.json())
app.use((req, res, next) => {
    res.on("finish", () => {
        const date = new Date().toISOString()
            .replace(/T/, ' ')
            .replace(/\..+/, '')
        console.log("* [" + date + "] \"" + req.method + " " + req.originalUrl + "\" " + res.statusCode)
    })
    return next()
})

// Routes
userRoutes(app)
app.get('*', (req, res) => {
    res.status(404)
    res.send()
});


// Run the express server
app.listen(port)
console.log('User service listening on port ' + port)
