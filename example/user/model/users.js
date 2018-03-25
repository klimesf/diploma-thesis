'use strict'

// Prototype fixtures
let nextId = 4;
const users = {
    1: {id: 1, name: 'John Doe', email: 'john.doe@example.com', role: 'CUSTOMER'},
    2: {id: 2, name: 'Jane Doe', email: 'jane.doe@example.com', role: 'EMPLOYEE'},
    3: {id: 3, name: 'Dr. Faust', email: 'faust@example.com', role: 'ADMINISTRATOR'},
}

exports.listUsers = () => {
    return new Promise((resolve, reject) => {
        resolve(Object.values(users))
    })
}

exports.getUser = (id) => {
    return new Promise((resolve, reject) => {
        if (!users.hasOwnProperty(id)) {
            reject("User not found")
        }
        resolve(users[id])
    })
}

exports.register = (name, email) => {
    return new Promise((resolve, reject) => {
        const id = nextId++;
        users[id] = {id: id, name: name, email: email, role: 'CUSTOMER'}
        console.log("Registered user with id " + id)
        resolve(users[id])
    })
}
