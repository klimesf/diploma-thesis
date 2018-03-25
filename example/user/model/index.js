exports.init = () => {
    const businessContext = require('./businessContext'),
        users = require('./users')

    businessContext.setUp()
    const registry = businessContext.businessContextRegistry

    users.init(registry)
}
