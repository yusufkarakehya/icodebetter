Ext.ns("Ext.ux.form");

Ext.ux.form.Monaco = Ext.extend(Ext.BoxComponent,{

  value: null,
  language: 'javascript',

  initComponent: function() {

    Ext.ux.form.Monaco.superclass.initComponent.apply(this, arguments);

    this.on({
        afterrender: function () {	
          console.log("after render...");
          var self = this;
          require.config({ paths: { 'vs': '/monaco/min/vs' }});
          require(['vs/editor/editor.main'], function() {
          self.editor = monaco.editor.create(self.el.dom, {
            value: self.value,
            language: self.language
          });
          monaco.editor.setTheme("vs-dark");

          //self = self;

          var childrenCount = self.ownerCt.container.dom.children.length;
          console.log("window: ", window);

          console.log("element: ", self.getEl());
          console.log("ownerCt: ", self.ownerCt);
          console.log("container: ", self.ownerCt.container);
          
          
          var containerHeight = self.ownerCt.container.dom.clientHeight;
          console.log('container height: ', containerHeight);
          if(containerHeight<500){
            containerHeight=500;
          }
          self.setHeight(containerHeight);
          
          console.log("render ownerCt height: ", self.el.dom.clientHeight);
          
          self.editor.layout();
          /*
          self.ownerCt.show = function() {
        	  var pnl = this;
        	  pnl.hidden = false;
        	  console.log("se pnl: ", pnl);
        	  containerHeight = pnl.container.dom.clientHeight;
        	  console.log("se container height: ", containerHeight);
			  if(containerHeight<500){
			    containerHeight=500;
			  }
			  pnl.items.items[0].setHeight(containerHeight);
			  console.log("se pnl after: ", pnl);
			  console.log("se ownerCt height: ", pnl.el.dom.clientHeight);
			  //self.editor.layout();
          }*/
         
          window.onresize = function() {
            console.log("hey it's resized... ");
            self.editor.layout();
            containerHeight = self.ownerCt.container.dom.clientHeight;
            console.log("containerHeight: ", containerHeight);
            self.setHeight(containerHeight);
            self.editor.layout();
          };

          });
        },
        show: function() {
        	console.log("hello there");
        	var self = this;
        	var containerHeight = self.ownerCt.container.dom.clientHeight;
            console.log("se container height: ", containerHeight);
            if(containerHeight<500){
              containerHeight=500;
            }
            self.setHeight(containerHeight);
            console.log("se ownerCt height: ", self.el.dom.clientHeight);
            self.editor.layout();
        }
    }, this);
  },

  getValue : function() {
    return this.editor.getValue();
  }

});

Ext.reg("monacoeditor", Ext.ux.form.Monaco);


/*
console.log('height: ',self.ownerCt.el.dom.style.height);
console.log('width: ',self.ownerCt.el.dom.style.width);
self.setWidth(self.ownerCt.el.dom.style.width);
var w = self.ownerCt.el.dom.style.width;
var h = self.ownerCt.el.dom.style.width;

console.log("dark before: ",window.document.getElementsByClassName("vs-dark")[0].style);
var darkStyle = "width: " + w + "; height: " + h + ";";
window.document.getElementsByClassName("vs-dark")[0].setAttribute("style", darkStyle);
console.log("dark after: ",window.document.getElementsByClassName("vs-dark")[0].style);


console.log("before: ",window.document.getElementsByClassName("overflow-guard")[0].style);
var overflow = "width: " + w + "; height: " + h + ";";
window.document.getElementsByClassName("overflow-guard")[0].setAttribute("style", overflow);
console.log("after: ",window.document.getElementsByClassName("overflow-guard")[0].style);


console.log("width: ", w);
var trimmed = w.replace("px","");
console.log("trimmed: ", trimmed);
var intWidth = parseInt(trimmed);
console.log("int: ", intWidth);
var left = intWidth - 50;
console.log("left: ",left);
var minimapStyle = "position: absolute; left: "+left+"px; width: 36px; height: "+ h +";";
window.document.getElementsByClassName("minimap")[0].setAttribute("style", minimapStyle);


var sWidth = intWidth - 64;
var scrollStyle =  "position: absolute; overflow: hidden; left: 64px; width: "+sWidth+"px; height: "+ h +";";
window.document.getElementsByClassName("monaco-scrollable-element")[0].setAttribute("style", scrollStyle);
*/
