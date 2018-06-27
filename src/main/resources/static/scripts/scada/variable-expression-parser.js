var scada = scada || {};

scada.VariableExpressionParser = function() {
	this.variables = [];
    this.expressions = [];
    this.expressionFuncObjs = {};
};
scada.VariableExpressionParser.prototype = function() {
    var load = function(expressions) {    		
			this.expressions = expressions;
			for (var i = 0; i < this.expressions.length; i++) {
				createExpressionFuncObj.call(this, this.expressions[i]);
			}
        },
        addExpr = function(expression) {
        	createExpressionFuncObj.call(this, expression);
        },
        removeExpr = function(expression) {
        	if (expression && expression.name) {
        		for (var i = 0; i < this.expressions.length; i++) {
        			if (this.expressions[i].name == expression.name) {
        				this.expressions.splice(i, 1);
        			}
        		}
        	}
        },
        re = new RegExp(/#{(.*?)}#/gm),
        parseVariables = function(expr) {
            var match = re.exec(expr.expression);
            var variables = [];

            while (match != null) {
                if (match[1] != '') {
                    variables.push(match[1]);
                }
                match = re.exec(expr.expression);
            }
            return variables;
        },
        createExpressionFuncObj = function(expr) {
        	if (!expr.expression)
        		return;
        	
            var args = parseVariables(expr);
            var body = expr.expression.replace(/#{/g, '').replace(/}#/g, '');
            var func = new Function(args, body);
            var funcObj = {
                dependencies: args,
                func: func
            };
            this.expressionFuncObjs[expr.name] = funcObj;
        },
        extend = function(dest, src) {
        	for (var prop in src) {
        		if (src.hasOwnProperty(prop)) {
        			if (typeof src[prop] === "object" && src[prop] !== null && dest[prop]) { 
                        extend(dest[prop], src[prop]);
                    } else {
                        dest[prop] = src[prop];
                    }
        		}
        	}
        	return dest;
        },
        clone = function(obj) {
            return extend({}, obj);
        },
        feed = function(newVariables) {
            var self = this;
            for (var i =0 ; i < newVariables.length; i++) {
            	var newVariable = newVariables[i];
            	var variable = findVariableByName.call(self, newVariable.name);
                if (variable) {
                    variable.value = newVariable.value;
                } else {
                    self.variables.push(clone(newVariable));
                }
            }
        },
        findVariableByName = function(name) {
            for (var i = 0; i < this.variables.length; i++) {
                if (this.variables[i].name === name) {
                    return this.variables[i];
                }
            }
        },
        invokeExpressionFunc = function(name) {
            var self = this;
            var funcObj = this.expressionFuncObjs[name];
            if (funcObj) {
                try {
                    var dependencies = funcObj.dependencies;
                    if (dependencies.length > 0) {
                        var args = [];
                        for (var i = 0; i < dependencies.length; i++) {
                            var dependency = dependencies[i];
                            var variable = findVariableByName.call(self, dependency);
                            if (variable) {
                                //5 means string
                                args.push(variable.type == 5 ? ('' + variable.value) : variable.value);
                            }
                        }
                        return funcObj.func.apply(undefined, args);
                    }                                                 
                    return funcObj.func.apply(undefined);
                } catch(err) {
                    console.log(err);
                }
            }
        }

    return {
        load: load,
        addExpr: addExpr,
        removeExpr: removeExpr,
        feed: feed,
        getValue: invokeExpressionFunc
    };
}();