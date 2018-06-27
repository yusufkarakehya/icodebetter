var scada = scada || {};

scada.SvgParser = function(content) {
	this.content = content;
};

scada.SvgParser.prototype = function() {
	var parse = function() {
		var div = document.createElement('div');
		div.innerHTML = this.content;
	    var svgs = div.getElementsByTagName("svg");
	    var result = [];
	    for (var i = 0; i < svgs.length; i++) {
	    	result = result.concat(getInkscapeElems(svgs[i])); 
	    }
	    return result;
	};
	
	//Private
	var getInkscapeElems = function(svgNode) {
		var result = [];
		var elems = svgNode.querySelectorAll('[inkscape\\:label]');
		for (var i = 0; i < elems.length; i++) {
			var elem = elems[i];
			var attrVal = elem.getAttribute('inkscape:label');
			var id = elem.getAttribute('id');
			if (id && attrVal && attrVal[0] == '{') {
				result.push({
					id: id,
					animProps: eval("[" + attrVal + "]")
				});
			}
		}
		return result; 
	};
	
	var setContent = function(content) {
		this.content = content;
	};
	
	return {
		parse: parse,
		setContent: setContent
	};
}();