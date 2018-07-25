Ext.ns("Ext.ux.form");

Ext.ux.form.Monaco = Ext.extend(Ext.BoxComponent,{

  value: null,
  language: 'javascript',

  initComponent: function() {

    Ext.ux.form.Monaco.superclass.initComponent.apply(this, arguments);

    this.on({
        afterrender: function () {
          /*
          if(window.monacos != null) {
            console.log("hello there");
            window.monacos.getEl().remove();
          }*/
          var self = this;
          require.config({ paths: { 'vs': '/monaco/min/vs' }});
          require(['vs/editor/editor.main'], function() {
          self.editor = monaco.editor.create(self.el.dom, {
            value: self.value,
            language: self.language
          });
          monaco.editor.setTheme("vs-dark");

          window.monacos = self;

          var childrenCount = window.monacos.ownerCt.container.dom.children.length;
          console.log("window: ", window);

          console.log("element: ", self.getEl());
          console.log("ownerCt: ", window.monacos.ownerCt);
          console.log("container: ", window.monacos.ownerCt.container);
          console.log("children count: ", childrenCount);

          var childrenHeight = 0;
          for(var index =0; index < childrenCount; index++) {
            if(window.monacos.ownerCt.id !== window.monacos.ownerCt.container.dom.children[index].id) {
              var childHeight = window.monacos.ownerCt.container.dom.children[index].clientHeight + 40;
              childrenHeight += childHeight;
            }
          }

          //console.log("ownerCt id: ", window.monacos.ownerCt.id);
          //console.log("first child id: ", window.monacos.ownerCt.container.dom.children[0].id);
          console.log("childrenHeight: ", childrenHeight);

          var containerHeight = window.monacos.ownerCt.container.dom.clientHeight;
          console.log('container height: ', containerHeight);
          /*var trimmed = containerHeight.replace("px","");
          console.log("trimmed: ", trimmed);
          var intHeight = parseInt(trimmed);
          console.log("int: ", intHeight);
          var monacoInt = containerHeight - childrenHeight;
          var monacoHeight = monacoInt + "px";
          console.log("final height: ", monacoHeight);*/
          if(containerHeight<500){
            containerHeight=500;
          }
          window.monacos.setHeight(containerHeight);

          window.monacos.editor.layout();
          window.onresize = function() {
            console.log("hey it's resized... ");
            window.monacos.editor.layout();
            console.log("childHeight: ", childHeight);
            containerHeight = window.monacos.ownerCt.container.dom.clientHeight;
            console.log("containerHeight: ", containerHeight);
            /*trimmed = containerHeight.replace("px","");
            intHeight = parseInt(trimmed);
            monacoInt = intHeight - childrenHeight;
            monacoHeight = monacoInt + "px";*/
            window.monacos.setHeight(containerHeight);
            window.monacos.editor.layout();
          };

          });
        }
    }, this);
  },

  getValue : function() {
    return window.monacos.editor.getValue();
  }

});

Ext.reg("monacoeditor", Ext.ux.form.Monaco);


/*
console.log('height: ',window.monacos.ownerCt.el.dom.style.height);
console.log('width: ',window.monacos.ownerCt.el.dom.style.width);
window.monacos.setWidth(window.monacos.ownerCt.el.dom.style.width);
var w = window.monacos.ownerCt.el.dom.style.width;
var h = window.monacos.ownerCt.el.dom.style.width;

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
