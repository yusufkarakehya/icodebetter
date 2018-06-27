var scada = scada || {};

scada.Animator = function(projectId, animationId, stagePanel, svgFileId, backgroundColor, defaultDuration) {
    this.projectId = projectId;
	this.animationId = animationId;
    this.stagePanel = stagePanel;
    this.svgFileId = svgFileId;
    this.backgroundColor = backgroundColor || "#FFFFFF";
    this.defaultDuration = defaultDuration || 1000;
    this.svgContent = null;
    this.animationElements = [];
    this.isRunning = false;
    this.variableExprParser = new scada.VariableExpressionParser();
    this.svgParser = new scada.SvgParser();
    this.subscriberId = null;
    this.timer = null;
    this.interval = this.defaultDuration;
};

scada.Animator.prototype = function() {
    var load = function() {
    		var self = this;
	    	promisRequest({
    		  url: '../scada/svg/getFile',
    		  params: { file_id: self.svgFileId },
    		  requestWaitMsg: true,
    		  successCallback: function(result) {
    		    self.svgContent = result.content;
    		    self.stagePanel.bodyStyle = { "background-color": self.backgroundColor };
                self.stagePanel.update(adjustSvgContent.call(self));
                promisRequest({
    		      url: '../scada/animation/getElements',
    		      params: { animation_id: self.animationId },
    		      requestWaitMsg: true,
    		      successCallback: function(result2) {
    		    	var animElems = result2.animation_elements;
    		    	var stagePanelDomId = self.stagePanel.getEl().dom.id;
    		    	
    		    	var i = animElems.length;
    		    	while (i--) {
    		    		var animEl = animElems[i];
    		    		try {
	    		    		animEl.domEl = document.querySelector('#' + stagePanelDomId + ' #' + animEl.dom_id);
	    		    		animEl.bbox = animEl.domEl.getBBox();
	    		    		if (animEl.domEl == null) {
	    		    			animElems.splice(i, 1);
	    		    			console.warn(String.format('Element with the dom id of {0} removed from animations.', animEl.dom_id));
	    		    			continue;
	    		    		}
	    		    		animEl.props = JSON.parse(animEl.props);
	    		    		if (animEl.type == 'color') {
    	        				removeFillFromGroupElements(animEl); //Group color animation does not effect children already have fill style or attr specified.
    	        			}
    		    		} catch (err) {
    		    			animElems.splice(i, 1);
    		    			console.warn(String.format('Element with the dom id of {0} removed from animations.', animEl.dom_id));
    		    		}
    		    	}
    		    	
    		    	self.animationElements = animElems;
    		    	self.variableExprParser.load(animElems);
    		    	promisRequest({
    		    		url: '../scada/animation/getVariableValues',
    		    		params: { animation_id: self.animationId },
    		    		requestWaitMsg: true,
    		    		successCallback: function(result3) {
    		    			var animationVariableValues = result3.animationVariableValues;
    		    			if (animationVariableValues && animationVariableValues.length > 0) {
    		    				self.variableExprParser.feed(animationVariableValues);
    		    				applyChanges.call(self);
    		    			}
    		    			self.subscriberId = scada.Notificator.subscribeToVariablesChanged(function(changedVariables) { feed.call(self, changedVariables); });
    		    			self.timer = setInterval(function() {
    		    				applyChanges.call(self);
    		    			}, self.interval);
    		    		},
    		    		noSuccessCallback: function(err3) {
    		    			self.subscriberId = scada.Notificator.subscribeToVariablesChanged(function(changedVariables) { feed.call(self, changedVariables); });
    		    			self.timer = setInterval(function() {
    		    				applyChanges.call(self);
    		    			}, self.interval);
    		    		}
    		    	});
    		      },
    		      noSuccessCallback: function(err2) {
    		    	Ext.MessageBox.show({
    		    	  title: '${error}',
    		    	  msg: '${msg.error.loading_anim_elements_failed}',
    		    	  icon: Ext.MessageBox.ERROR,
    		    	  buttons: Ext.Msg.OK
    		    	});
    		      }
    		    });
    		  },
    		  noSuccessCallback: function(err) {
    		    Ext.MessageBox.show({
    		      title: '${error}', 
    		      msg: '${msg.error.loading_svg_file_failed}', 
    		      icon: Ext.MessageBox.ERROR,
    		      buttons: Ext.Msg.OK
    		    });
    		  }
    		});
        },
        adjustSvgContent = function() {
        	if (window.DOMParser) {
        		var parser = new DOMParser();
        		var doc = parser.parseFromString(this.svgContent, "text/xml");
        		var svg = doc.querySelector('svg');
        		if (svg) {
        			var width = svg.style.width;
        			var height = svg.style.height;
        			svg.style.width = "100%";
        			svg.style.height = "100%";
        			svg.style["background-color"] = this.backgroundColor;
        			svg.attributes.viewBox = "0 0 " + width + " " + height;
        			return svg.outerHTML;
        		}
        		return this.svgContent;
        	}
        	return this.svgContent;
        },
        removeFillFromGroupElements = function(animEl) {
          	if (animEl.domEl.nodeName.toLowerCase() == "g") {
          		var fill = animEl.domEl.style.fill;
          		var stroke = animEl.domEl.style.stroke;
          		var children = animEl.domEl.children;
          		if (fill) {
	      			 for (var j = 0; j < children.length; j++) {
	                     children[j].style.fill = "";
	                     children[j].attributes.fill = "";  
	                 }
          		}
                if (stroke) {
            	   	 for (var j = 0; j < children.length; j++) {
	                     children[j].style.stroke = "";
	                     children[j].attributes.stroke = "";  
	                 }
                }
            }
        },
        isRunning = function() {
            return this.isRunning;
        },
        start = function() {
        	this.isRunning = true;
            load.call(this);
        },
        stop = function() {
            if (this.isRunning) {
                this.isRunning = false;
                clearInterval(this.timer);
                scada.Notificator.unsubscribe(this.subscriberId);
                this.animationElements.forEach(function(animElement) {
                    if (animElement.blinker) {
                        animElement.blinker.stop();
                    }
                    
                    if (animElement.window && !animElement.window.isDestroyed()) {
                        animElement.window.close();
                    }
                    
                    Velocity(animElement.domEl, "stop");
                });
            }
        },
        feed = function(changedVariables) {
        	if (changedVariables) {
        		this.variableExprParser.feed(changedVariables.variables.map(function(el) {
                	var obj = {};
                    obj.name = el.variable.name;
                    obj.value = el.variable.value.value;
                    return obj;
                }));
        	}
        },
        applyChanges = function() {
        	for (var i = this.animationElements.length; i--;) {
        		switch (this.animationElements[i].type) {
	        		case 'rotate':
                    applyRotation.call(this, this.animationElements[i]);
                    break;

                    case 'color':
                    applyColor.call(this, this.animationElements[i]);
                    break;

                    case 'opac':
                    applyOpacity.call(this, this.animationElements[i]);
                    break;

                    case 'text':
                    applyText.call(this, this.animationElements[i]);
                    break;

                    case 'tooltips':
                    if (!this.animationElements[i].tooltip) {
                    	applyToolTips.call(this, this.animationElements[i]);
                    	this.animationElements.splice(i, 1);
                    }
                    break;

                    case 'get':
                    applyGet.call(this, this.animationElements[i]);
                    break;

                    case 'bar':
                    applyBar.call(this, this.animationElements[i]);
                    break;
                    
                    case 'set':
                    if (!this.animationElements[i].domEl.onclick) {
                    	applySet.call(this, this.animationElements[i]);
                    	this.animationElements.splice(i, 1);
                    }
                    break;
                    
                    default:
                    break;
        		}
        	}
        },
        checkValIsNotUndefinedAndDifferent = function(animEl) {
        	var val = this.variableExprParser.getValue(animEl.name);
        	if (val != undefined && val != animEl.prevVal) {
        		animEl.prevVal = val;
        		return { value: val };
        	}
        },
        checkValIsNumAndDifferent = function(animEl) {
        	var val = this.variableExprParser.getValue(animEl.name);
        	if (val != undefined && !isNaN(val) && val != animEl.prevVal) {
        		animEl.prevVal = val;
        		return { value: val };
        	}
        },
        applyRotation = function(animEl) {
        	var result = checkValIsNumAndDifferent.call(this, animEl);
        	if (!result) return;
        	
        	var val = result.value;
        	var degree = (animEl.props.min == animEl.props.max) ? 0 : (360 / (animEl.props.max - animEl.props.min) * val);
        	if (animEl.domEl.style.transformOrigin != "center center")
        		animEl.domEl.style.transformOrigin = "center center";
        	
        	Velocity(animEl.domEl, { rotateZ: degree }, this.defaultDuration);
        },
        applyOpacity = function(animEl) {
        	var result = checkValIsNumAndDifferent.call(this, animEl);
        	if (!result) return;
        	
        	var val = result.value;
        	var opacity = 1 * (val - animEl.props.min) / (animEl.props.max - animEl.props.min);
  		    Velocity(animEl.domEl, { opacity: opacity }, this.defaultDuration);
        },
        applyText = function(animEl) {
        	var result = checkValIsNotUndefinedAndDifferent.call(this, animEl);
        	if (!result) return;
        	
        	var val = result.value;
        	if (animEl.props.map) {
    			for (var i = 0; i < animEl.props.map.length; i++) {
                    var mapItem = animEl.props.map[i].split('=');
                    if (mapItem.length > 1 && mapItem[0] == val) {
                        animEl.domEl.innerHTML = mapItem[1];
                    }
                }
    		}
        },
        applyGet = function(animEl) {
        	var result = checkValIsNotUndefinedAndDifferent.call(this, animEl);
        	if (!result) return;
        	
        	var val = result.value;
        	animEl.domEl.innerHTML = val;
        },
        applyBar = function(animEl) {
        	var val = this.variableExprParser.getValue(animEl.name);
        	if (val != undefined && !isNaN(val) && val != animEl.prevVal) {
        		if (!animEl.bbox) return;
        		var height = animEl.bbox.height * (val - animEl.props.min) / (animEl.props.max - animEl.props.min);
        		Velocity(animEl.domEl, { height: height, y: (animEl.bbox.y + animEl.bbox.height - height) }, this.defaultDuration);
        		animEl.prevVal = val;
        	}
        },
        applyToolTips = function(animEl) {
        	if (!animEl.tooltip) {
        		var props = animEl.props;
                var tip = '';
                for (var i = 0; i < props.param.length; i++) {
                    tip += props.param[i] + '<br/>';
                }

                var style = '';
                if (props.size) {
                    style += 'font-size: ' + props.size + 'px;'
                }

                if (props.style) {
                    style += props.style;
                }
                
                animEl.tooltip = new Ext.ToolTip({
                    target: document.querySelector('#' + this.stagePanel.getEl().dom.id + " #" + animEl.domEl.id),
                    html: tip,
                    bodyStyle: style
                });
            }
        },
        promptRegex = /\?\((.*)\)/,
        applySet = function(animEl) {
            if (!animEl.domEl.onclick) {
                var self = this;
                var props = animEl.props;
                animEl.domEl.style.cursor = "pointer";

                if (!animEl.domEl.onmouseout) {
	                animEl.domEl.onmouseout = function() {
	                	animEl.domEl.style.stroke = animEl.prevStroke;
	                };
                }
                if (!animEl.domEl.onmouseover) {
	                animEl.domEl.onmouseover = function() {
	                	animEl.prevStroke = animEl.domEl.style.stroke;
	                	animEl.domEl.style.stroke = 'yellow';
	                };
                }
                
                animEl.domEl.onclick = function() {
                	var destination = self.variableExprParser.getValue(animEl.name);
                    var prompt = props.prompt || '';
                    var val = props.type == 'Variable' && props.src ? self.variableExprParser.getValue(props.src) :
                    		  props.type == 'Data' && props.src ? props.src : '';
                    
                    if (!destination) return;
                    if (prompt) {
                    	var match = promptRegex.exec(prompt);
                    	if (match) {
                    		Ext.Msg.show({
                          	   title:getLocMsg('change_variable_value'),
                      		   msg: getLocMsg(match[1]),
                      		   buttons: Ext.Msg.YESNO,
                      		   buttonText: { yes: getLocMsg('yes'), no: getLocMsg('no!') },
                      		   icon: Ext.MessageBox.QUESTION,
                      		   minWidth: 200,
                      		   fn: function(btn) {
                      			   if (btn == 'yes') {
                      				   promisRequest({
                                	     url: '../scada/communication/setVariableValueFromAnimation',
                                	     params: {
                                		   project_id: self.projectId,
                                		   variable_name: destination,
                                		   value: val
                                	     }
                                       });
                      			   }
                      		   }
                          	});
                    	} else {
                    		var tf = new Ext.form.TextField({
                        		anchor: '100%',
                        		height: 30,
                        		fieldLabel: prompt,
                        		labelStyle: 'font-size:14px',
                        		style:{
                        	      'font-size': '18px'
                        	    }
                        	});
                        	
                        	var promptWin = new Ext.Window({
                        		title: getLocMsg('change_variable_value'),
                        		layout: 'fit',
                        		width: 400,
                        		height: 450,
                        		minHeight: 450,
                        		items: [{
                        			layout: 'form',
                        			labelAlign: 'top',
                        			bodyStyle: 'padding:5px 5px 0',
                        			items: [tf, 
                        				{
                        					xtype: 'virtualkeyboard',
                        					keyboardTarget: tf,
                        					language: 'Numpad',
                        					deadKeysButtonText: ' '
                        				}
                        			]
                        		}],
                        		buttons: [{
                        			text: getLocMsg('js_tamam'),
                        			handler: function() {
                        				promisRequest({
                                      	   url: '../scada/communication/setVariableValueFromAnimation',
                                      	   params: {
                                      		   project_id: self.projectId,
                                      		   variable_name: destination,
                                      		   value: tf.getValue()
                                      	   },
                                      	   successCallback: function() {
                                      		   promptWin.close();
                                      	   }
                                         });
                        			}
                        		},
                        		{
                        			text: getLocMsg('js_iptal'),
                        			handler: function(btn) {
                        				promptWin.close();
                        			}
                        		}]
                        	});
                        	promptWin.show();
                    	}
                    } else {
                    	promisRequest({
                      	   url: '../scada/communication/setVariableValueFromAnimation',
                      	   params: {
                      		   project_id: self.projectId,
                      		   variable_name: destination,
                      		   value: val
                      	   }
                        });
                    }
                }
            }
        },
        applyColor = function(animEl) {
        	var result = checkValIsNotUndefinedAndDifferent.call(this, animEl);
        	if (!result) return;
        	
        	var val = result.value;
        	if (val == animEl.props.data) {
			    var colors = animEl.props.param.split('/');
	            var hexColor;
	            var hexColors = [];
	            colors.forEach(function(colorEl) {
	            	hexColor = parseColor(colorEl);
	                if (hexColor) {
	                    hexColors.push(hexColor);
	                }
	            });
	            
	            if (hexColors.length > 1) {
                   if (!animEl.domEl.blinker) {
                       animEl.domEl.blinker = new scada.Blinker(animEl.domEl, hexColors, this.defaultDuration * 2);
                       animEl.domEl.blinker.start();
                   } else {
                       if (compareHexColorArrays(animEl.domEl.blinker.colors, hexColors)) {
                           if (!animEl.domEl.blinker.isRunning) {
                               animEl.domEl.blinker.start();
                           }
                       } else {
                           animEl.domEl.blinker.changeColors(hexColors);
                       }
                   }
               } else if (hexColors.length > 0) {
                   if (animEl.domEl.blinker && animEl.domEl.blinker.isRunning) {
                       animEl.domEl.blinker.stop();
                   }
                   
                   var fillColor = hexColors[0], strokeColor = hexColors[0];
                   if (animEl.domEl.style.fill == "none") {
                	   fillColor = "none";
                   }
                   
                   if (animEl.domEl.style.stroke == "none") {
                	   strokeColor = "none";
                   }
                	   
                   Velocity(animEl.domEl, { fill: fillColor, stroke: strokeColor }, this.defaultDuration);
               }
    		}
        },
        hexColorRegex = /(^#[0-9A-F]{6}$)|(^#[0-9A-F]{3}$)/i,
        parseColor = function(color) {
        	return hexColorRegex.test(color) ? color : null;
        },
        compareHexColorArrays = function(colors1, colors2) {
            if (colors1.length != colors2.length) {
                return false;
            }
            for (var i = 0; i < colors1.length; i++) {
                if (colors1[i] != colors2[i]) {
                    return false;
                }
            }
            return true;
        };
        /*
        applyOpen = function(animEl, prop) {
            var val = prop.istag ? this.variableExprParser.getValue(prop.tag) : prop.src;
            if (val != undefined && animEl.snapEl) {
                if (!animEl.snapEl.node.onclick) {
                    animEl.snapEl.node.onclick = function() {
                       if (!animEl.window || animEl.window.isDestroyed()) {
                           animEl.window = new scada.IFrameWindow(animation.el, val, prop.width, prop.height, prop.x, prop.y);
                       }
                       animEl.window.show();
                    };
                }
            }
        },
        */

    return {
        start: start,
        stop: stop
    };
}();