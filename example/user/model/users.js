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
        console.log("+ Registered user with id " + id)
        resolve(users[id])
    })
}

exports.createEmployee = (name, email) => {
    return new Promise((resolve, reject) => {
        const id = nextId++;
        users[id] = {id: id, name: name, email: email, role: 'EMPLOYEE'}
        console.log("+ Created employee with id " + id)
        resolve(users[id])
    })
}

exports.init = (registry) => {
    const BusinessContextWeaver = require('business-context-framework/dist/weaver/BusinessContextWeaver').default,
        BusinessOperationContext = require('business-context-framework/dist/weaver/BusinessOperationContext').default,
        weaver = new BusinessContextWeaver(registry)

    const wrapCall = (context, func) => {
        return new Promise((resolve, reject) => {
            try {
                weaver.evaluatePreconditions(context)
                console.log("# Evaluated preconditions of " + context.name)
                resolve()
            } catch (error) {
                console.log("# Preconditions of " + context.name + " failed")
                reject(error.getMessage())
            }
        })
            .then(_ => func())
            .then(result => {
                context.setOutput(result)
                weaver.applyPostConditions(context)
                console.log("# Applied post-conditions of " + context.name)
                return new Promise((resolve, reject) => resolve(context.getOutput()))
            })
    }

    // Decorate exports AOP style
    const register = exports.register
    exports.register = (name, email) => {
        const context = new BusinessOperationContext('user.register')
        context.setInputParameter('name', name)
        context.setInputParameter('email', email)
        return wrapCall(context, () => register(name, email))
    }

    const createEmployee = exports.createEmployee
    exports.createEmployee = (name, email, user) => {
        const context = new BusinessOperationContext('user.createEmployee')
        context.setInputParameter('name', name)
        context.setInputParameter('email', email)
        context.setInputParameter('user', user)
        return wrapCall(context, () => createEmployee(name, email))
    }

    const listUsers = exports.listUsers
    exports.listUsers = () => {
        const context = new BusinessOperationContext('user.listAll')
        return wrapCall(context, listUsers)
    }

}
