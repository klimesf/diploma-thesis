import Constant from './expression/Constant'
import IsNotNull from './expression/IsNotNull'
import ExpressionType from './expression/ExpressionType'
import BusinessOperationContext from './weaver/BusinessOperationContext'

const context = new BusinessOperationContext('user.create')
console.log(new IsNotNull(new Constant(true, ExpressionType.BOOL)).interpret(context))
