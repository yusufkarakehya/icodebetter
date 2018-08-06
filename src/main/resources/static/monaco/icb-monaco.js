Ext.ns("Ext.ux.form");

Ext.ux.form.Monaco = Ext.extend(Ext.BoxComponent,{

  value: null,
  language: 'javascript',

  initComponent: function() {

    Ext.ux.form.Monaco.superclass.initComponent.apply(this, arguments);

    this.on({
        afterrender: function () {	
          var self = this;
          require.config({ paths: { 'vs': '/monaco/min/vs' }});
          require(['vs/editor/editor.main'], function() {
          self.editor = monaco.editor.create(self.el.dom, {
            value: self.value,
            language: self.language
          });
          monaco.editor.setTheme("vs-dark");

          /*
          console.log("element: ", self.getEl());
          console.log("ownerCt: ", self.ownerCt);
          console.log("container: ", self.ownerCt.container);
          */
          
          
          var containerHeight = self.ownerCt.container.dom.clientHeight;
          //console.log('container height: ', containerHeight);
          if(containerHeight == 600){
        	  containerHeight = 500;
          }
          //console.log('container height: ', containerHeight);
          if(containerHeight<500){
            containerHeight=500;
            self.ownerCt.setHeight(500);
            self.ownerCt.container.setHeight(500);
          }
          self.setHeight(containerHeight);                   
          self.editor.layout();

          window.onresize = function() {
            self.editor.layout();
            containerHeight = self.ownerCt.container.dom.clientHeight;
            //console.log("containerHeight: ", containerHeight);
            self.setHeight(containerHeight);
            self.editor.layout();
          };

          });
        },
        show: function() {
        	var self = this;
        	var containerHeight = self.ownerCt.container.dom.clientHeight;
            if(containerHeight<500){
              containerHeight=500;
            }
            self.setHeight(containerHeight);
            self.editor.layout();
        }
    }, this);
  },

  getValue : function() {
    return this.editor.getValue();
  }

});

Ext.reg("monacoeditor", Ext.ux.form.Monaco);
