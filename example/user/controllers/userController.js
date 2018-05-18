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
            res.status(404).json({message: err})
        })
}

exports.register = (req, res) => {
    const userId = req.get('X-User-Id') || null
    const userRole = req.get('X-User-Role') || null
    const user = userId !== null ? {id: userId, role: userRole} : null
    users.register(req.body.name, req.body.email, user)
        .then(user => res.json(user))
        .catch(err => {
            res.status(422).json({message: err})
        })
}

exports.createEmployee = (req, res) => {
    const userId = req.get('X-User-Id') || ""
    const userRole = req.get('X-User-Role') || ""
    users.createEmployee(req.body.name, req.body.email, {id: userId, role: userRole.toUpperCase()})
        .then(user => res.json(user))
        .catch(err => {
            res.status(422).json({message: err})
        })
}

exports.deleteUser = (req, res) => {
    const userId = req.get('X-User-Id') || ""
    const userRole = req.get('X-User-Role') || ""
    users.deleteUser(req.params.userId, {id: userId, role: userRole.toUpperCase()})
        .then(_ => res.json({ok: true}))
        .catch(err => {
            res.status(422).json({message: err})
        })
}
