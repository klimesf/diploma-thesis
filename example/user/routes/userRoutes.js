'use strict'

module.exports = app => {
    const userController = require('../controllers/userController')

    app.route('/users')
        .get(userController.listUsers)


    app.route('/users/:userId')
        .get(userController.getUser)
}
