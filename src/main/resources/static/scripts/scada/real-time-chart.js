var scada = scada || {};

scada.RealTimeChart = function() {
	var variableExprParser,
		container,
		svg,
	    inner,
	    innerSvg,
	    height = 300,
	    width = 300,
	    paddingLeft = 20,
	    paddingTop = 20,
	    xg,
	    yg,
	    xAxis,
	    yAxis,
	    xScale,
	    yScale,
	    lines = [],
	    period = 5000,
	    timer,
	    isRunning;
	
	function chart(pContainer) {
		container = pContainer;
		draw();
	}
	
	function draw() {
		var svgHeight = height - paddingTop;
	    var svgWidth = width - paddingLeft;
		
	    if (!svg) {
	      	svg = container.append('svg');
	    }
	    
	    svg.attr({height: svgHeight, width: svgWidth});
	    
	    var innerHeight = svgHeight - 40; 
	    var innerWidth = svgWidth - 40;
	    
	    if (!inner) {
	    	inner = svg.append("g").attr({height: innerHeight, width: innerWidth}).attr("transform", "translate(" + [35, 10] + ")");
	      	innerSvg = inner.append('svg').attr({height: innerHeight, width: innerWidth});
	      	xg = inner.append("g").classed("x axis", true);
	    	yg = inner.append("g").classed("y axis", true);
	      
	      	var now = new Date();
	    	xScale = d3.time.scale().domain([now, new Date(now.getTime() + 60000)]).range([0, innerWidth]);
	    	yScale = d3.scale.linear().domain([0, 100]).range([innerHeight, 0]);
	      	
	      	xAxis = d3.svg.axis().scale(xScale).orient("bottom").ticks(5).tickFormat(d3.time.format("%H:%M:%S"));
	    	yAxis = d3.svg.axis().scale(yScale).orient("left");

	    } else {
	    	inner.attr({height: innerHeight, width: innerWidth}).attr("transform", "translate(" + [35, 10] + ")");
	        innerSvg.attr({height: innerHeight, width: innerWidth});
	        xScale.range([0, innerWidth]);
	        yScale.range([innerHeight, 0]);
	    }
	    
	    xg.attr("transform", "translate(" + [0, innerHeight] + ")").call(xAxis);
	    yg.call(yAxis);
	}
	
	function drawLines() {
		if (lines.length === 0) return;
		
		var sampleSize = 20,
		    tickCount = 5,
		    now = new Date(),
		    minDate = d3.min(lines[0].data, function(d) { return d.date; }),
		    maxDate = now;
		
		xScale.domain([minDate, maxDate]);
		
		if (lines[0].data.length < sampleSize) {
			lines.forEach(function(l) {
				var value = variableExprParser.getValue(l.name);
				l.data.push({ date: now, value: value });
				l.path.data([l.data]).attr("d", l.lineFunc);
			});
		} else {
			var goLeft = xScale(lines[0].data[0].date) - xScale(lines[0].data[1].date);
			lines.forEach(function(l) {
				var value = variableExprParser.getValue(l.name);
				l.data.push({ date: now, value: value });
				l.path
		         .data([l.data])
		         .attr("d", l.lineFunc)
		         .attr("transform", null)
		         .transition()
		         .duration(500)
		         .attr("transform", "translate(" + [goLeft, 0] + ")");        
		        
		        l.data.shift();
			});
		}
		
		var dates = [];
		var ticks = [];
		lines[0].data.forEach(function(d) {
			dates.push(d.date);
		});
		
		if (dates.length > tickCount) {
			var chosen = d3.range(0, dates.length - 1, dates.length / 5);
			for (var i = 0 ; i < chosen.length; i++) {
				ticks.push(dates[parseInt(chosen[i])]);
			}      
		} else {
			ticks = dates;
		}
		
	    xAxis.tickValues(ticks);
	    xg.transition().duration(500).call(xAxis);
	}
	
	function changeYAxisLabels(min, max) {
	  	var formatScale = d3.scale.linear()
	      .domain([0, 100])
	      .range([min, max]);

	    yAxis.tickFormat(function(d) {
	   		return formatScale(d);
	    });

	    yg.call(yAxis);
	}
	
	chart.variableExpressionParser = function(val) {
		if (!arguments.length) return variableExprParser;
		variableExprParser = val;
		return chart;
	};
	
	chart.height = function(val) {
	  	if (!arguments.length) return height;
	    height = val;
	    return chart;
	};

	chart.width = function(val) {
		if (!arguments.length) return width;
		width = val;
		return chart;
	};

	chart.paddingLeft = function(val) {
		if (!arguments.length) return paddingLeft;
		paddingLeft = val;
		return chart;
	};
	  
	chart.paddingTop = function(val) {
		if (!arguments.length) return paddingTop;
		paddingTop = val;
		return chart;
	};
	
	chart.period = function(val) {
		if (!arguments.length) return period;
		period = val;
		return chart;
	};
	
	chart.addLine = function(line) {
		line.min_scale = line.min_scale || 0;
		line.max_scale = line.max_scale || 100;
		line.color = line.color || 'black';
		line.thickness = line.thickness || 1;
		line.interpolation = line.interpolation || 'basis';
		
		line.data = [];
	    line.scale = d3.scale.linear().domain([line.min_scale, line.max_scale]).range([0, 100]); 
	    line.lineFunc = d3.svg.line().interpolate(line.interpolation)
	    				  .x(function(d, i) { return xScale(d.date); })
	    				  .y(function(d, i) { return d.value ? yScale(line.scale(d.value)) : -10; });

	    line.path = innerSvg.append("g").append("path")
	    					.classed("trendline", true)
	                        .attr({
	                          "fill": "none",
	                          "stroke": line.color,
	                          "stroke-width": line.thickness
	                        });

	    line.path.node().onclick = function() {
	      changeYAxisLabels(line.min_scale, line.max_scale);
	    };
	    
	    lines.push(line);
	    return chart;
	};
	
	chart.clearLines = function() {
		lines = [];
		d3.selectAll('#' + container[0][0].id + " .trendline").remove(); //TODO
		return chart;
	};
	
	chart.start = function() {
		timer = setInterval(drawLines, period);
		isRunning = true;
		return chart;
	};
	
	chart.stop = function() {
		if (timer) {
			clearInterval(timer);
			isRunning = false;
		}
		return chart;
	};
	
	chart.isRunning = function() {
		return isRunning;
	}
	
	chart.draw = draw;
	chart.drawLines = drawLines;
	
	return chart;
};