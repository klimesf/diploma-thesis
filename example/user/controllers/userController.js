'use strict'

const users = require('../model/users')

exports.listUsers = (req, res) => {
    users.listUsers()
        .then(users => res.json(users))
}

exports.getUser = (req, res) => {
    users.getUser(req.params.userId)
        .then(user => res.json(user))
        .catch(err => {
            console.error(err)
            res.status(404)
            res.send()
        })
}

exports.register = (req, res) => {
    users.register(req.body.name, req.body.email)
        .then(user => res.json(user))
        .catch(err => {
            console.error(err)
            res.status(422)
            res.send()
        })
}
