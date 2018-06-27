var scada = scada || {};

scada.TrendChart = function(panel, options) {
	var self = this;
	options=options ||{};
	if(options.webix){
		this.panel = panel;
		this.panel.on('resize', function(p, w, h) {
			self.setHeight(h);
			self.setWidth(w);
			self.draw();
		});
		this.panel.on('beforedestroy', function() {
			if (self.subscriberId)
				scada.Notificator.unsubscribe(self.subscriberId);
			
			if (self.timer)
				clearInterval(self.timer);
		});
		this.selection = d3.select('#' + this.panel.getContentTarget().id);
	   this.selection = d3.select('#' + panel);
	}else {
		this.panel = panel;
		this.panel.on('resize', function(p, w, h) {
			self.setHeight(h);
			self.setWidth(w);
			self.draw();
		});
		this.panel.on('beforedestroy', function() {
			if (self.subscriberId)
				scada.Notificator.unsubscribe(self.subscriberId);
			
			if (self.timer)
				clearInterval(self.timer);
		});
		this.selection = d3.select('#' + this.panel.getContentTarget().id);
	}
	
	//this.selection = d3.select('#' + this.panel.getContentTarget().id);
	this.options = options;
	this.options.chartTitle = options.chartTitle || "";
	this.options.xTitle = options.xTitle || "";
	this.options.yTitle = options.yTitle || "";
	this.options.width = options.width || 700;
	this.options.height = options.height || 300;
	this.options.period = options.period || 5000;
	
	this.variableExpressionParser = null;
	this.subscriberId = null;
	
	this.height = this.width = 
	this.startTime = this.endTime = this.startTimeViewport = this.endTimeViewport =
	this.svg = this.main = this.defs = this.backRect = 
	this.lineG = this.xAxisG = this.yAxisG = this.y2AxisG = 
	this.xAxisText = this.yAxisText = this.chartText =
	this.x = this.y = this.y2 =
	this.xAxis = this.yAxis = this.y2Axis = 
    this.heightNav = this.widthNav = 
	this.nav = this.navDefs = this.navRect = this.navG = this.xAxisGNav =
	this.xNav = this.yNav =
	this.xAxisNav =
	this.viewportG = this.viewport = 
	this.timer = this.timerCallback =
	this.tooltip = this.focus = null;
	this.lines = [];
};

scada.TrendChart.prototype = function() {
	var margin = { top: 20, bottom: 20, left: 50, right: 40, topNav: 10, bottomNav: 20 },
    	dimension = { chartTitle: 20, xAxis: 20, yAxis: 20, xTitle: 20, yTitle: 20, navChart: 70 },
    	drawNavChart = true,
  	    drawBorder = true,
  	    chartBackgroundColor = "#f5f5f5", navBackgroundColor = "#f5f5f5",
  	    minY = 0, maxY = 100,
	    maxSeconds = 300, pixelsPerSecond = 10,
	    defaultLineColor = "black", defaultLineThickness = 1;
	
	var bisectDate = d3.bisector(function(d) { return d.date; }).left,
		draw = function() {
		  var self = this;
		
		  if (!this.selection) {
			  console.error("selection is null or not defined!");
			  return;	
		  }
		  
		  var chartTitleDim = this.options.chartTitle == "" ? 0 : dimension.chartTitle;
		  var xTitleDim = this.options.xTitle == "" ? 0 : dimension.xTitle;
		  var yTitleDim = this.options.yTitle == "" ? 0 : dimension.yTitle;
		  var xAxisDim = dimension.xAxis;
		  var yAxisDim = dimension.yAxis;
		  var navChartDim = !drawNavChart ? 0 : dimension.navChart;
		  
		  this.height = this.options.height - margin.top - margin.bottom - chartTitleDim - xTitleDim - navChartDim;
		  this.width = this.options.width - margin.left - margin.right;
		  var marginTop = margin.top + chartTitleDim;
		  this.widthNav = this.width;
		  this.heightNav = navChartDim - margin.topNav - margin.bottomNav;
		  var marginTopNav = this.options.height - margin.bottom - this.heightNav - margin.topNav;
		  
		  var ts = new Date().getTime();
		  this.endTime = new Date(ts);
		  this.startTime = new Date(this.endTime.getTime() - maxSeconds * 1000);
		  this.endTimeViewport = new Date(ts);
		  this.startTimeViewport = new Date(this.endTime.getTime() - this.width / pixelsPerSecond * 1000);
		  
		  if (!this.svg) {
			  this.svg = this.selection.append('svg')
			  	.attr("width", this.options.width)
			    .attr("height", this.options.height)
			    .style("border", function(d) {
			    	if (drawBorder) return "1px solid lightgray";
			    	else return null;
			    });
			    
			  this.main = this.svg.append("g")
			  	.attr("transform", "translate(" + margin.left + "," + marginTop + ")");
			    
			  this.defs = this.main.append("defs")
			    .append("clipPath")
			  	.attr("id", "myClip")
			    .append("rect")
			    .attr("x", 0)
			    .attr("y", 0)
			    .attr("width", this.width)
			    .attr("height", this.height);
			    
			  this.backRect = this.main.append("rect")
			  	.attr("x", 0)
			    .attr("y", 0)
			    .attr("width", this.width)
			    .attr("height", this.height)
			    .style("fill", chartBackgroundColor)
			    .on('mouseover', function() {
			    	self.focus.style('display', null);
			    })
			    .on('mouseout', function() {
			    	self.focus.style('display', 'none');
			        self.tooltip.style('opacity', 0);
			    });
			    
			  this.lineG = this.main.append("g")
			  	.attr("clip-path", "url(#myClip)")
			    .append("g");
			  
			  this.xAxisG = this.main.append("g")
			  	.attr("class", "x axis")
			    .attr("transform", "translate(0," + this.height + ")");
			    
			  this.yAxisG = this.main.append("g")
			  	.attr("class", "y axis");
			  
			  this.y2AxisG = this.main.append("g")
			  	.attr("class", "y2 axis")
			  	.attr("transform", "translate(" + this.width + ", 0)")   
			  	.style("fill", "red");
			  
			  this.xAxisText = this.xAxisG.append("text")
			  	.attr("class", "title")
			    .attr("x", this.width / 2)
			    .attr("y", 25)
			    .attr("dy", ".71em")
			    .text(function(d) {
			    	return self.options.xTitle;
			    });
			    
			  this.yAxisText = this.yAxisG.append("text")
			  	.attr("class", "title")
			    .attr("transform", "rotate(-90)")
			    .attr("x", - this.height / 2)
			    .attr("y", -35)
			    .attr("dy", ".71em")
			    .text(function(d) {
			    	return self.options.yTitle;
			    });
			    
			  this.chartText = this.main.append("text")
			    .attr("class", "chartTitle")
			    .attr("x", this.width / 2)
			    .attr("y", -35)
			    .attr("dy", ".71em")
			    .text(function(d) { 
			    	return self.options.chartTitle; 
			  });
			  
			  this.x = d3.time.scale().range([0, this.width]);
			  this.y = d3.scale.linear().domain([minY, maxY]).range([this.height, 0]);
			  this.y2 = d3.scale.linear().domain([minY, maxY]).range([this.height, 0]);
			  
			  this.xAxis = d3.svg.axis().orient("bottom");
			  this.yAxis = d3.svg.axis().orient("left");
			  this.y2Axis = d3.svg.axis().orient("right");
			  
			  this.nav = this.svg.append("g")
			  	.attr("transform", "translate(" + margin.left + "," + marginTopNav + ")");
			  
			  this.navDefs = this.nav.append("defs")
			    .append("clipPath")
			  	.attr("id", "myNavClip")
			    .append("rect")
			    .attr("x", 0)
			    .attr("y", 0)
			    .attr("width", this.widthNav)
			    .attr("height", this.heightNav);
			  
			  this.navRect = this.nav.append("rect")
			  	.attr("x", 0)
			    .attr("y", 0)
			    .attr("width", this.widthNav)
			    .attr("height", this.heightNav)
			    .style("fill", navBackgroundColor)
			    .style("shape-rendering", "crispEdges");
			  
			  this.navG = this.nav.append("g")
			    .attr("clip-path", "url(#myNavClip)")
			    .append("g")
			  	.attr("class", "nav");
			
			  this.xAxisGNav = this.nav.append("g")
			  	.attr("class", "x axis")
			  	.attr("transform", "translate(0," + this.heightNav + ")");
			
			  this.xNav = d3.time.scale().range([0, this.widthNav]);
			  this.yNav = d3.scale.linear().domain([minY, maxY]).range([this.heightNav, 0]);
			
			  this.xAxisNav = d3.svg.axis().orient("bottom");
			  
			  this.x.domain([this.startTimeViewport, this.endTimeViewport]);
			  this.xNav.domain([this.startTime, this.endTime]);
			  
			  this.xAxis.scale(this.x)(this.xAxisG);
			  this.yAxis.scale(this.y)(this.yAxisG);
			  this.y2Axis.scale(this.y2)(this.y2AxisG);
			  this.xAxisNav.scale(this.xNav)(this.xAxisGNav);
			  
			  this.viewport = d3.svg.brush()
			  	.x(this.xNav)
			    .extent([this.startTimeViewport, this.endTimeViewport])
			    .on("brush", function() {
			    	brushed.call(self);
			    });
					
			  this.viewportG = this.nav.append("g")
			  	.attr("class", "viewport")
			    .call(this.viewport)
			    .selectAll("rect")
			    .attr("height", this.heightNav);
			    
			  this.tooltip = this.selection.append('div')
			  	.style('position', 'absolute')
			    .style('padding', '0 10px')
			    .style('background', '#F79F81')
			    .style('opacity', 0);
			    
			  this.focus = this.main.append('g')
			  	.style('display', 'none');
			  
			  this.focus.append("circle") 
			    .attr("class", "y")
			    .style("fill", "none") 
			    .style("stroke", "blue")
			    .attr("r", 4);
		  } else {
			  this.svg.attr("width", this.options.width)
			  	.attr("height", this.options.height);
			  
			  this.main.attr("transform", "translate(" + margin.left + "," + marginTop + ")");
			  
			  this.defs.attr("x", 0)
			    .attr("y", 0)
			    .attr("width", this.width)
			    .attr("height", this.height);
			  
			  this.backRect.attr("x", 0)
			    .attr("y", 0)
			    .attr("width", this.width)
			    .attr("height", this.height);
			  
			  this.xAxisG.attr("transform", "translate(0," + this.height + ")");
			  this.y2AxisG.attr("transform", "translate(" + this.width + ", 0)")   
			  
			  this.xAxisText.attr("x", this.width / 2)
			    .attr("y", 25)
			    .attr("dy", ".71em")
			    .text(function(d) {
			    	return self.xTitle;
			    });
			    
			  this.yAxisText.attr("transform", "rotate(-90)")
			    .attr("x", - this.height / 2)
			    .attr("y", -35)
			    .attr("dy", ".71em")
			    .text(function(d) {
			    	return self.yTitle;
			    });
			    
			  this.chartText.attr("x", this.width / 2)
			    .attr("y", -35)
			    .attr("dy", ".71em")
			    .text(function(d) { 
			    	return self.chartTitle; 
			  });
			  
			  this.x.range([0, this.width]);
			  this.y.range([this.height, 0]);
			  this.y2.range([this.height, 0]);
			  
			  this.nav.attr("transform", "translate(" + margin.left + "," + marginTopNav + ")");
			  
			  this.navDefs.attr("x", 0)
			    .attr("y", 0)
			    .attr("width", this.widthNav)
			    .attr("height", this.heightNav);
			  
			  this.navRect.attr("x", 0)
			    .attr("y", 0)
			    .attr("width", this.widthNav)
			    .attr("height", this.heightNav);
			  
			  this.xAxisGNav.attr("transform", "translate(0," + this.heightNav + ")");
			
			  this.xNav.range([0, this.widthNav]);
			  this.yNav.range([this.heightNav, 0]);
			  
			  this.x.domain([this.startTimeViewport, this.endTimeViewport]);
			  this.xNav.domain([this.startTime, this.endTime]);
			  
			  this.xAxis.scale(this.x)(this.xAxisG);
			  this.yAxis.scale(this.y)(this.yAxisG);
			  this.y2Axis.scale(this.y2)(this.y2AxisG);
			  this.xAxisNav.scale(this.xNav)(this.xAxisGNav);

			  this.viewport.extent([this.startTimeViewport, this.endTimeViewport]);
			  this.svg.selectAll('.viewport').call(this.viewport);
		  }
		},
		//Private
		brushed = function() {
	        this.x.domain(this.viewport.empty() ? this.xNav.domain : this.viewport.extent());
	        this.xAxis.scale(this.x)(this.xAxisG);
	        refresh.call(this);
	    },
		//Private
		refresh = function() {
		  var self = this;
		  this.lines.forEach(function(line) {
		    line.data = line.data.filter(function(d) {
		    	if (d.date.getTime() >= self.startTime.getTime() && 
		          d.date.getTime() <= self.endTime.getTime())
		          return true;
		    });
		    line.path
		      .data([line.data])
		      .attr("d", line.lineFunc);
		    line.pathNav
		    	.data([line.data])
		      .attr("d", line.lineFuncNav);
		  });
		},
		//Private
		addLine = function(line) {
		  var self = this;
		  
		  if (!line.name)
			  return;
		  
		  line.minScale = line.min_scale || minY;
		  line.maxScale = line.max_scale || maxY;
		  line.color = line.color || defaultLineColor;
		  line.thickness = line.thickness || defaultLineThickness;
		  line.data = [];
		  line.scale = d3.scale.linear().domain([line.minScale, line.maxScale]).range([minY, maxY]); 
		  line.lineFunc = d3.svg.line()
		    .x(function(d, i) { return self.x(d.date); })
		    .y(function(d, i) {
		    	var val = typeof d.value === 'undefined' ? minY : line.scale(d.value);
		    	if (val > maxY) 
		    		val = maxY;
		    	else if (val < minY)
		    		val = minY; 
		    	return self.y(val);
		    });
	  
		  line.path = this.lineG.append("g").append("path")
		    .classed("trendline", true)
		    .attr({
		      "fill": "none",
		      "stroke": line.color,
		      "stroke-width": line.thickness
		  	});
	  
		  line.lineFuncNav = d3.svg.line()
		  	.interpolate("basis")
		    .x(function(d, i) { return self.xNav(d.date); })
		    .y(function(d, i) {
		    	var val = typeof d.value === 'undefined' ? minY : line.scale(d.value);
		    	if (val > maxY)
		    		val = maxY;
		    	else if (val < minY)
		    		val = minY;
		    	return self.yNav(val);
		    });
		  
		  line.pathNav = this.navG.append("g").append("path")
		  	.classed("trendline", true)
		    .attr({
		      "fill": "none",
		      "stroke": line.color,
		      "stroke-width": line.thickness
		    });
		
		  line.path.on('mousemove', function(d) {
		    var x0 = self.x.invert(d3.mouse(this)[0]);
		    var i = bisectDate(line.data, x0, 1);
		    var d0 = line.data[i - 1];
		    var d1 = line.data[i];
		    if (d0 && d1) {
		    	var d = x0 - d0.date > d1.date - x0 ? d1 : d0;
		    	var xPos = self.x(d.date);
		    	var val = typeof d.value === 'undefined' ? minY : line.scale(d.value);
		    	if (val > maxY)
		    		val = maxY;
		    	else if (val < minY)
		    		val = minY;
		    	
		    	var yPos = self.y(val);
		    	
		    	self.focus.select("circle.y")
			    	.attr("transform", "translate(" + xPos + "," + yPos + ")"); 
			    
			    self.tooltip.html("<b>" + line.name + " : " + line.scale(d.value) + " : (" + d.date.toLocaleString() + ")" + "</b>")
			    	.style('left', xPos + 30 + 'px')
			        .style('top', yPos + 40 + 'px')
			        .transition()
			        .style('opacity', .9)
			        .style('background', line.color)
			        .style('color', 'white');
		    } else {
		    	self.tooltip.style('opacity', 0);
		    }
		  });

		  this.lines.push(line);
		},
		setHeight = function(height) {
			this.options.height = height;
		},
		setWidth = function(width) {
			this.options.width = width;
		},
		changePeriod = function(period) {
			this.options.period = period || 5000;
			if (this.timer) {
				clearInterval(this.timer);
				this.timer = setInterval(function() { timeGoes.call(self); } , this.options.period);
			}
		},
		//private
		reset = function() {
			if (this.timer) {
			  	clearInterval(this.timer);
			    this.timer = null;
			}
			this.lines = [];
			this.svg.selectAll('.trendline').remove();
			scada.Notificator.unsubscribe(self.subscriberId);
		},
		//private
		feed = function(variablesChanged) {
			if (!variablesChanged)
			    return;

		    this.variableExpressionParser.feed(variablesChanged.variables.map(function(el) {
		      var obj = {};
		      obj.name = el.variable.name;
		      obj.value = el.variable.value.value;
		      return obj;
		    }));
		},
		//private
		timeGoes = function() {
			var self = this;
			var now = new Date();
			
			var extent = this.viewport.empty() ?  this.xNav.domain() :  this.viewport.extent();
			var interval = extent[1].getTime() - extent[0].getTime();
			var offset = extent[0].getTime() -  this.xNav.domain()[0].getTime();
			
			this.endTime = now;
			this.startTime = new Date(now.getTime() - maxSeconds * 1000);
			this.startTimeViewport = new Date(this.startTime.getTime() + offset);
			this.endTimeViewport = new Date(this.startTimeViewport.getTime() + interval);
			this.x.domain([this.startTimeViewport,  this.endTimeViewport]);
			this.xNav.domain([this.startTime,  this.endTime]);
			this.xAxis.scale(this.x)(this.xAxisG);
			this.xAxisNav.scale(this.xNav)(this.xAxisGNav);
			this.viewport.extent([this.startTimeViewport,  this.endTimeViewport]);
			
		    this.lines.forEach(function(line) {
				line.data.push({
					date: self.endTime,
					value: self.variableExpressionParser.getValue(line.name)
				});
			});
		    
			brushed.call(this);
		},
		liveMode = function(trendId, timerCallback) {
			var self = this;
			reset.call(this);
			promisRequest({
			    url: '../scada/trend/getTrend',
			    params: {
			      trend_id: trendId
			    },
			    requestWaitMsg: true,
			    successCallback: function(result) {
			      if (!result.trend_status || !result.trend_tags || result.trend_tags.length == 0)
			          return;
			      
			      self.variableExpressionParser = new scada.VariableExpressionParser();
			      self.variableExpressionParser.load(result.trend_tags);
			      result.trend_tags.forEach(function(trendTag) {
			        addLine.call(self, trendTag);
			      });
			      self.options.period = result.trend_period || 5000;
			      
			      var extent = self.viewport.empty() ?  self.xNav.domain() :  self.viewport.extent();
				  var interval = extent[1].getTime() - extent[0].getTime();
				  var offset = extent[0].getTime() -  self.xNav.domain()[0].getTime();
					
				  self.endTime = new Date();
				  self.startTime =  new Date(self.endTime.getTime() - maxSeconds * 1000);
				  self.startTimeViewport = new Date(self.startTime.getTime() + offset);
				  self.endTimeViewport = new Date(self.startTimeViewport.getTime() + interval);
				  self.x.domain([self.startTimeViewport,  self.endTimeViewport]);
				  self.xNav.domain([self.startTime,  self.endTime]);
				  self.xAxis.scale(self.x)(self.xAxisG);
				  self.xAxisNav.scale(self.xNav)(self.xAxisGNav);
				  self.viewport.extent([self.startTime,  self.endTime]);
				  self.selection.selectAll(".viewport").call(self.viewport);
				  brushed.call(self);
			      
			      if (typeof timerCallback === "function")
			    	  self.timerCallback = timerCallback;
			      
			      self.timer = setInterval(function() { 
			    	  timeGoes.call(self);
			    	  if (self.timerCallback) 
			    		  self.timerCallback();
			      }, self.options.period);
			     
			      self.subscriberId = scada.Notificator.subscribeToVariablesChanged(function(variablesChanged) {
			    	  feed.call(self, variablesChanged);
			      });
			    },
			    noSuccessCallback: function() {
			       Ext.Msg.alert('${error}', '${msg.error.loading_trend_tags_failed}');
			    }
			});
		},
		stopLiveMode = function() {
			if (this.timer) {
			  	clearInterval(this.timer);
			    this.timer = null;
			}
		},
		historyMode = function(trendId, startTime, endTime, successCallback) {
			var self = this;
			reset.call(this);
			promisRequest({
				url: '../scada/trend/getTrend',
			    params: {
			      trend_id: trendId
			    },
			    requestWaitMsg: true,
			    successCallback: function(firstResult) {
			       if (!firstResult.trend_status || !firstResult.trend_tags || firstResult.trend_tags.length == 0)
				       return;
			       
			       self.variableExpressionParser = new scada.VariableExpressionParser();
			       self.variableExpressionParser.load(firstResult.trend_tags);
			       firstResult.trend_tags.forEach(function(trendTag) {
			         addLine.call(self, trendTag);
			       });
			      
			       promisRequest({
			    	   url: self.options.trendVariableValueUrl || '../scada/communication/getTrendVariableValues',
					   params: {
					       trend_id: trendId,
						   start_dttm: startTime,
						   end_dttm: endTime
					   },
					   requestWaitMsg: true,
					   successCallback: function(secondResult) {
						   if (!secondResult.trendVariableValues || secondResult.trendVariableValues.length == 0)
							   return;
						   
						   var groupedByDate = groupBy(secondResult.trendVariableValues, "readDttm");
						   for (date in groupedByDate) {
							   self.variableExpressionParser.feed(groupedByDate[date]);
							   self.lines.forEach(function(line) {
								   line.data.push({
									   date: new Date(date),
									   value: self.variableExpressionParser.getValue(line.name)
								   });
							   });
						   }
						   
						   var extent = self.viewport.empty() ?  self.xNav.domain() :  self.viewport.extent();
						   var interval = extent[1].getTime() - extent[0].getTime();
						   var offset = extent[0].getTime() -  self.xNav.domain()[0].getTime();
							
						   self.endTime = endTime;
						   self.startTime = startTime;
						   self.startTimeViewport = new Date(self.startTime.getTime() + offset);
						   self.endTimeViewport = new Date(self.startTimeViewport.getTime() + interval);
						   self.x.domain([self.startTimeViewport,  self.endTimeViewport]);
						   self.xNav.domain([self.startTime,  self.endTime]);
						   self.xAxis.scale(self.x)(self.xAxisG);
						   self.xAxisNav.scale(self.xNav)(self.xAxisGNav);
						   self.viewport.extent([self.startTime,  self.endTime]);
						   self.selection.selectAll(".viewport").call(self.viewport);
						   brushed.call(self);
						   
						   if (typeof successCallback === "function")
							   successCallback.call(self);
					   },
					   noSuccessCallback: function() {
						   Ext.Msg.alert('${error}', '${operation_failed}');
					   }
			       });
			    },
			    noSuccessCallback: function() {
			    	Ext.Msg.alert('${error}', '${msg.error.loading_trend_tags_failed}');
			    }
			})
		},
		//Private
		groupBy = function(xs, key) {
			return xs.reduce(function(prev, curr) {
				(prev[curr[key]] = prev[curr[key]] || []).push(curr);
				return prev;
			}, {});
		},
		stats = function() {
			var self = this;
			var statsArr = [];
			this.lines.forEach(function(line) {
				statsArr.push({
					name: line.name,
					min: min.call(self, line.data),
					max: max.call(self, line.data),
					avg: avg.call(self, line.data)
				});
			});
			return statsArr;
		},
		//Private
		max = function(arr) {
			return d3.max(arr.filter(function(d) {
					return typeof d.value === "number";
				})
				.map(function(d) {
					return d.value;
				}));
		},
		//Private
		min = function(arr) {
			return d3.min(arr.filter(function(d) {
					return typeof d.value === "number";
				})
				.map(function(d) {
					return d.value;
				}));
		},
		//Private
		avg = function(arr) {
			var sum = arr.reduce(function(p, v) {
				return typeof v.value === "number" ? p + v.value : p;
			}, 0);
			return arr.length == 0 ? 0 : sum / arr.length;
		},
		//Private
		chartMax = function() {
			return d3.max(this.lines, function(line) {
				return d3.max(line.data.filter(function(d) {
						return typeof d.value === "number"
					})
					.map(function(d) {
						return d.value;
					}));
			})
		},
		//Private
		chartMin = function() {
			return d3.min(this.lines, function(line) {
				return d3.min(line.data.filter(function(d) {
						return typeof d.value === "number"
					})
					.map(function(d) {
						return d.value;
					}));
			})
		},
		//Private
		chartAvg = function() {
			var avgTotal = 0, count = 0;
			this.lines.forEach(function(line) {
				avgTotal += line.data.reduce(function(p, v) {
					count++;
					return typeof v.value === "number" ? p + v.value : p;
				}, 0);
			});
			return count == 0 ? null : avgTotal / count;
		},
		changeY2AxisLabels = function(pMin, pMax) {
			var formatScale = d3.scale.linear().domain([minY, maxY]).range([pMin, pMax]);

		    this.y2Axis.tickFormat(function(d) {
		   		return formatScale(d);
		    });

		    this.y2AxisG.call(this.y2Axis);
		};
	
	return {
		draw: draw,
		liveMode: liveMode,
		stopLiveMode: stopLiveMode,
		setHeight: setHeight,
		setWidth: setWidth,
		changePeriod: changePeriod,
		historyMode: historyMode,
		stats: stats,
		changeY2AxisLabels: changeY2AxisLabels
	};
}();
