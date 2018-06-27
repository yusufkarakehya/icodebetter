var scada = scada || {};
runAnimation = function(animationId) {
	
	parent.mainPanel.loadTab({
		   attributes: {
		     href: 'showPage?_tid=1182',
		     animationId: animationId,
		     title: '${design}'
		   }
		 });
};
scada.IframeAnimator = function(animationId, stageId, projectId) {
	console.log("project id : "+ projectId);
	this.animationId = animationId;
	this.projectId = projectId;
    this.stagePanel = document.getElementById(stageId);
    this.backgroundColor = "#FFFFFF";
    this.defaultDuration = 1000;
    this.svgFileId = null;
    this.svgContent = null;
    this.animationElements = [];
    this.isRunning = false;
    this.variableExprParser = new scada.VariableExpressionParser();
    this.svgParser = new scada.SvgParser();
    this.subscriberId = null;
    this.timer = null;
    this.interval = 1000;
};

scada.IframeAnimator.prototype = function() {
    var loadAnimation = function() {
    		var self = this;
            iwb.request({
                url: '../scada/animation/getAnimation',
                params: {
                    animationId: self.animationId
                },
                success: function(result) {
                	console.log(result);
//                    result = JSON.parse(result.responseText);
                    self.animationId = result.animation_id;
                    self.svgFileId = result.svg_file_id;
                    self.backgroundColor = result.background_color;
                    self.interval = result.duration;
                    loadSVG.call(self);
                },
                failure: function() {
                    showError("Failed to load animation");
                }
            });
        },
        loadSVG = function() {
            var self = this;
            iwb.request({
              url: '../scada/svg/getFile',
              params: { file_id: self.svgFileId },
              requestWaitMsg: true,
              success: function(result) {
  //              result = JSON.parse(result.responseText);
                self.svgContent = result.content;
                self.stagePanel.style.background = self.backgroundColor;
                self.stagePanel.innerHTML = self.svgContent;
                var svg = document.getElementsByTagName("svg");
                var domSvg=svg[0];
                domSvg.style.width='100%';
                domSvg.style.height='100%';
                loadAnimationElements.call(self);
              },
              failure: function() {
                showError("Failed to load svg file");
              }
            });
        },
        loadAnimationElements = function() {
            var self = this;
            iwb.request({
                url: '../scada/animation/getElements',
                params: { animation_id: self.animationId},
                requestWaitMsg: true,
                success: function(result){
//                    result = JSON.parse(result.responseText);
                    var animElems = result.animation_elements;
                    var stagePanelDomId = self.stagePanel.id;
                    
                    var i = animElems.length;
                    while (i--) {
                        var animEl = animElems[i];
                        try {
                            animEl.domEl = document.querySelector('#' + stagePanelDomId + ' #' + animEl.dom_id);
                            animEl.bbox = animEl.domEl.getBBox();
                            if (animEl.domEl == null) {
                                animElems.splice(i, 1);
                                console.warn('Element with the dom id of '+animEl.dom_id+' removed from animations.');
                                continue;
                            }
                            animEl.props = JSON.parse(animEl.props);
                            if (animEl.type == 'color') {
                                removeFillFromGroupElements(animEl); //Group color animation does not effect children already have fill style or attr specified.
                            }
                        } catch (err) {
                            animElems.splice(i, 1);
                            console.warn('Element with the dom id of '+animEl.dom_id+' removed from animations.');
                        }
                    }
                    self.animationElements = animElems;
                    self.variableExprParser.load(animElems);
                    loadVariableValues.call(self);
                },
                failure: function(){
                    showError("Failed to load animation elements");
                }
            })
        },
        loadVariableValues = function(){
            var self = this;
            iwb.request({
                url: '../scada/animation/getVariableValues',
                params: {animation_id: self.animationId},
                requestWaitMsg: true,
                success: function(result){
  //                  result = JSON.parse(result.responseText);
                    var animationVariableValues = result.animationVariableValues;
                    if (animationVariableValues && animationVariableValues.length > 0) {
                        self.variableExprParser.feed(animationVariableValues);
                        applyChanges.call(self);
                    }
                    subscribe.call(self);
                },
                failure: function(error){
                    subscribe.call(self);
                    showError('Problem occured while loading variable values.');
                }
            });
        },
        subscribe = function() {
            var self = this;
            self.subscriberId = scada.Notificator.subscribeToVariablesChanged(function(changedVariables) { feed.call(self, changedVariables); });
            self.timer = setInterval(function() {
                applyChanges.call(self);
            }, self.interval);
        },
        showError = function(err) {
        	webix.alert(err || '${operation_failed}');
            /*Ext.MessageBox.show({
              title: '${error}', 
              msg: err || '${operation_failed}', 
              icon: Ext.MessageBox.ERROR,
              buttons: Ext.Msg.OK
            });*/
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
            loadAnimation.call(this);
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
                    applyToolTips.call(this, this.animationElements[i]);
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
                    
                    case 'script':
                    if (!this.animationElements[i].domEl.onclick) {
                    	runScript.call(this, this.animationElements[i]);
                    	this.animationElements.splice(i, 1);
                    }
                    break;
                    
                    case 'qrcodeGenerator':
                    applyQRcodeGenerator.call(this, this.animationElements[i]);
                    break;
                    
                    case 'qrcodeScanner':
                    applyQRcodeScanner.call(this, this.animationElements[i]);
                    this.animationElements.splice(i, 1);
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
        applyQRcodeGenerator = function(animEl) {
        	var result = checkValIsNotUndefinedAndDifferent.call(this, animEl);
        	if (!result) return;
        		
    		var val = result.value;
    		if(animEl.domEl.tagName =='g'){
			var rect = animEl.domEl.getElementsByTagName("rect")[0];
    			//alert('i am a g');
    			var qrcode = new QRCode(document.getElementById(animEl.domEl.id), {
            		width : 100,
            		height : 100,
            		useSVG: true
            	});
            	qrcode.makeCode(val);
            	var svg = animEl.domEl.getElementsByTagName("svg")[0];
	            if(rect.getAttribute('x')!=null){
	            	var x =(rect.getAttribute('x')-0+3);
	                var y =(rect.getAttribute('y')-0+3);
	    			svg.setAttribute("x", x);
	    			svg.setAttribute("y", y);
	    			svg.setAttribute("height", rect.getAttribute('height')-6);
	    			svg.setAttribute("width", rect.getAttribute('width')-6);
	    			animEl.domEl.setAttribute("x", rect.getAttribute('x'));
	    			animEl.domEl.setAttribute("y", rect.getAttribute('y'));
	    			animEl.domEl.setAttribute("height", rect.getAttribute('height'));
	    			animEl.domEl.setAttribute("width", rect.getAttribute('width'));
	    			
	            }
	            else{
	            	var x =(animEl.domEl.getAttribute('x')-0+3);
	                var y =(animEl.domEl.getAttribute('y')-0+3);
	    			svg.setAttribute("x",x);
	    			svg.setAttribute("y", y);
	    			svg.setAttribute("height", animEl.domEl.getAttribute('height')-6);
	    			svg.setAttribute("width", animEl.domEl.getAttribute('width')-6);
	            }
            
    		}
             
        },
applyQRcodeScanner = function(animEl) {
        	//alert('im scanner');
    		if(animEl.domEl.tagName =='g'){
    			var rect = animEl.domEl.getElementsByTagName("rect")[0];
    			var foreignObject = document.createElementNS("http://www.w3.org/2000/svg", 'foreignObject')
    			foreignObject.setAttribute("x", rect.getAttribute('x'));
    			foreignObject.setAttribute("y", rect.getAttribute('y'));
    			foreignObject.setAttribute("height", rect.getAttribute('height'));
    			foreignObject.setAttribute("width", rect.getAttribute('width'));
			
    			var body=document.createElement("body");
    			body.setAttribute("xmlns", "http://www.w3.org/1999/xhtml");
    			var div = document.createElement("div");
    			div.style.width='100%';
    			div.style.height='100%';
    			var vid = document.createElement("video");
    			vid.style.width="100%";
    			vid.setAttribute("id", "preview");
    			div.appendChild(vid);
    			body.appendChild(div);
    			foreignObject.appendChild(body);
    			animEl.domEl.appendChild(foreignObject);
    			var app = new Vue({el: '#app', data: {scanner: null, activeCameraId: null, cameras: [], scans: []},
    				  mounted: function () {
    				    var self = this;
    				    self.scanner = new Instascan.Scanner({ video: document.getElementById('preview'), scanPeriod: 5 });
    				    self.scanner.addListener('scan', function (content, image) {
    				      self.scans.unshift({ date: +(Date.now()), content: content });
    				      eval(content);
    				    });
    				    Instascan.Camera.getCameras().then(function (cameras) {
    				      self.cameras = cameras;
    				      if (cameras.length > 0) {
    				        self.activeCameraId = cameras[0].id;
    				        self.scanner.start(cameras[0]);
    				      } else {
    				        console.error('No cameras found.');
    				      }
    				    }).catch(function (e) {
    				      console.error(e);
    				    });
    				  },
    				  methods: {
    				    formatName: function (name) {
    				      return name || '(unknown)';
    				    },
    				    selectCamera: function (camera) {
    				      this.activeCameraId = camera.id;
    				      this.scanner.start(camera);
    				    }
    				  }
    				});
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
            if (animEl.props.min < animEl.props.max) {
                var opacity;
                if(val<animEl.props.min)
                    opacity=0;
                else if(val>animEl.props.max)
                    opacity=1;
                else{
                    opacity=(val-animEl.props.min)/(animEl.props.max - animEl.props.min);
                }
                Velocity(animEl.domEl, { opacity: opacity }, this.defaultDuration);
            } else {
                console.warn("opacity min should be less than max.");
            }
        },
        applyText = function(animEl) {
        	var result = checkValIsNotUndefinedAndDifferent.call(this, animEl);
        	if (!result) return;
        	
        	var val = result.value;
            animEl.domEl.innerHTML = val;
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
        		if(animEl.props.orientation == 'vertical'){
        			var height = animEl.bbox.height * (val - animEl.props.min) / (animEl.props.max - animEl.props.min);
            		Velocity(animEl.domEl, { height: height, y: (animEl.bbox.y + animEl.bbox.height - height) }, this.defaultDuration);
            		animEl.prevVal = val;
        		}
        		if(animEl.props.orientation == 'horizontal'){
        			var width = animEl.bbox.width * (val - animEl.props.min) / (animEl.props.max - animEl.props.min);
            		Velocity(animEl.domEl, { width: width, x: (animEl.bbox.x) }, this.defaultDuration);
            		animEl.prevVal = val;
        		}
        	}
        },
        applyToolTips = function(animEl) {
            var result = checkValIsNotUndefinedAndDifferent.call(this, animEl);
            if (!result) return;
            var props = animEl.props;
            var tip = result.value;
            
            if (!animEl.tooltip) {
                var style = '';
                if (props.size) {
                    style += 'font-size: ' + props.size + 'px;'
                }
                if (props.color) {
                    style +='color: '+ props.color+';';
                }
                /*
                animEl.tooltip = new Ext.ToolTip({
                    target: document.querySelector('#' + this.stagePanel.id + " #" + animEl.domEl.id),
                    html: tip,
                    bodyStyle: style,
                    title: props.title,
                    delay: 0,
                    trackMouse: true
                }); */ //Tooltip
            } else {
                animel.tooltip.html = tip;
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
                    var setVariableId = props.variable;
                    var title = props.title;
                    var prompt_type = props.prompt_type;
                    var message = props.message;
                    var val = self.variableExprParser.getValue(animEl.name);

                    if(prompt_type == 'auto') {
                        setVariableValue(setVariableId, val);
                    }
                    else if(prompt_type == 'yes/no'){
                        /*Ext.Msg.show({
                           title: title,
                           msg: message,
                           buttons: Ext.Msg.YESNO,
                           buttonText: { yes: getLocMsg('yes'), no: getLocMsg('no!') },
                           icon: Ext.MessageBox.QUESTION,
                           minWidth: 200,
                           fn: function(btn) {
                               if (btn == 'yes') {
                                   setVariableValue(setVariableId, val);
                               }
                           }
                        });*/
                    	webix.confirm(message, function(btnOk){if(btnOk)setVariableValue(setVariableId, val);})
                    }
                    else if(prompt_type == 'manual') {
                    	iwb.numpad({hasNegative:!0,hasDecimal:!0, initialValue:val||0, callback:function(xval){
                    	      if(xval!==false)setVariableValue(setVariableId, xval);
                        }});
/*
                    	//alert('ss');return;
                        var tf = new Ext.form.TextField({
                            anchor: '100%',
                            height: 30,
                            fieldLabel: message,
                            value: val,
                            labelStyle: 'font-size:14px',
                            style:{
                              'font-size': '18px'
                            }
                        });
                        
                        var promptWin = new Ext.Window({
                            title: title,
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
                                    setVariableValue(setVariableId, tf.getValue(),
                                        function() {
                                            promptWin.close();
                                        }
                                     );
                                }
                            },
                            {
                                text: getLocMsg('js_iptal'),
                                handler: function(btn) {
                                    promptWin.close();
                                }
                            }]
                        });
                        promptWin.show(); */
                    }
                    else{
                        console.warn("Prompt option is not appropriate");
                    }
                }
            }
        },
        applyColor = function(animEl) {
        	var result = checkValIsNotUndefinedAndDifferent.call(this, animEl);
        	if (!result) return;
        	
        	var val = result.value;
		    var colors = val.split('/');
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
        },
        runScript = function(animEl) { //TODO How to run script with variable values?
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
                	 try {
                		eval(animEl.expression);
                	 } catch(err) {
                		 console.error(err);
                	 }
                 }
        	}
        },
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

        // adjustSvgContent = function() {
        //  if (window.DOMParser) {
        //      var parser = new DOMParser();
        //      var doc = parser.parseFromString(this.svgContent, "text/xml");
        //      var svg = doc.querySelector('svg');
        //      if (svg) {
        //          var width = svg.style.width;
        //          var height = svg.style.height;
        //          svg.style.width = "100%";
        //          svg.style.height = "100%";
        //          svg.style["background-color"] = this.backgroundColor;
        //          svg.attributes.viewBox = "0 0 " + width + " " + height;
        //          return svg.outerHTML;
        //      }
        //      return this.svgContent;
        //  }
        //  return this.svgContent;
        // },

        setVariableValue = function(variableId, value, success, fail){
        	console.log(variableId, value);
            iwb.request({
               url: '../scada/communication/setVariableValueFromAnimation',
               params: {
            	   project_id: this.projectId,
                   variable_name: variableId,
                   value: value
               },
               success: function() {
                   if (success) { //Check if it is function
                        success();
                   } 
               },
               failure: function() {
                   if (fail) { //Check if it is function
                        fail();
                   }
               }
            });
        };

    return {
        start: start,
        stop: stop
    };
}();