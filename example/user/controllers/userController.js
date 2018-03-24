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
            res.status(404)
            res.send()
        })
}
