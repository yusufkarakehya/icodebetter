var scada = scada || {};

scada.HistoricalVariableExpressionParser = function(data, expressionObjs) {
	var self = this;
	this.data = this.groupBy(data, "name"); //TODO Sort
	this.expressionObjs = expressionObjs;
	this.expressionFuncObjs = {};
	this.expressionObjs.forEach(function(expressionObj) {
		self.createExpressionFuncObj(expressionObj);
	});
};

scada.HistoricalVariableExpressionParser.prototype = function() {
	var re = new RegExp(/#{(.*?)}#/gm),
		parseArgs = function(expr) {
	        var match = re.exec(expr);
	        var args = [];
	
	        while (match != null) {
	            if (match[1] != '') {
	                args.push(match[1]);
	            }
	            match = re.exec(expr);
	        }
	        return args;
	    },
		createExpressionFuncObj = function(expressionObj) {
			if (!expressionObj.expression || !expressionObj.name) return;
	    	var expr = expressionObj.expression;
	    	var name = expressionObj.name;
	    	
	    	try {
	    		var args = parseArgs(expr);
		        var body = expr.replace(/#{/g, '').replace(/}#/g, '');
		        var func = new Function(args, body);
		        var funcObj = {
		            args: args,
		            func: func
		        };
		        this.expressionFuncObjs[name] = funcObj;
	    	} catch (err) {
	    		console.error("Historical Variable Expression Error: " + err);
	    	}
		},
		groupBy = function(xs, key) {
			return xs.reduce(function(prev, curr) {
				(prev[curr[key]] = prev[curr[key]] || []).push(curr);
				return prev;
			}, {});
		},
		getValue = function(name, date) {
			var funcObj = this.expressionFuncObjs[name];
			if (!funcObj) return;
			
			var self = this;
			try {
				if (funcObj.args.length > 0) {
					var args = [];
					funcObj.args.forEach(function(arg) {
						args.push(self.data[arg].find(function(d) {
							return d.date >= date;
						}));
					});
					return funcObj.func.apply(undefined, args);
				}
				return funcObj.func.apply(undefined);
			} catch (err) {
				console.error("Historical Variable Expression Parser Error: " + err);
				return;
			}
		},
		getValues = function(name) {
			var self = this;
			var values = [];
			var funcObj = this.expressionFuncObjs[name];
			var args = funcObj.args;
			var minDates = [];
			var maxDates = [];
			args.forEach(function(arg) {
				var len = self.data[arg].length;
				if (len > 0) {
					minDates.push(self.data[arg][0]);
					maxDates.push(self.data[arg][len - 1]);
				}
			});
			minDates = minDates.sort();
			maxDates = maxDates.sort();
			minDate
			return values;
		};
		
	return {
		getValue: getValue,
		getValues: getValues
	};
}();