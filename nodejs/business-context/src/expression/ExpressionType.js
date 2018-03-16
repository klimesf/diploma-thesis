const ExpressionType = Object.freeze({
    STRING: {
        name: 'string',
        serialize: value => value.toString(),
        deserialize: value => value
    },
    NUMBER: {
        name: 'number',
        serialize: value => value.toString(),
        deserialize: value => value
    },
    BOOL: {
        name: 'bool',
        serialize: value => value.toString(),
        deserialize: value => value == 'true'
    },
    VOID: {
        name: 'void',
        serialize: value => '',
        deserialize: value => null
    },
})

export default ExpressionType
