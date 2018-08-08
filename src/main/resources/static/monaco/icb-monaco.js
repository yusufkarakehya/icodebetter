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

          
          console.log("element: ", self.getEl());
          console.log("ownerCt: ", self.ownerCt);
          console.log("container: ", self.ownerCt.container);
          
          
          
          var containerHeight = self.ownerCt.container.dom.clientHeight;
          //console.log('container height: ', containerHeight);
               
          if(containerHeight<500){
            containerHeight=500;
            self.ownerCt.setHeight(500);
            self.ownerCt.container.setHeight(500);
          }
          self.setHeight(containerHeight);                  
          self.editor.layout();
          
          var itemLength = self.ownerCt.items.items.length;
          var totalChildHeight = 0;
          if(itemLength > 1){
        	  for(var i=0; i<itemLength; i++){
        		  if(self.ownerCt.items.items[i].editor == null && self.ownerCt.items.items[i].el){
        			  totalChildHeight += self.ownerCt.items.items[i].el.dom.clientHeight;
        		  }       		  
        	  }
        	  console.log("total child height: ", totalChildHeight);
              var editorHeight = containerHeight - totalChildHeight - 70;
              console.log("editor height: ", editorHeight);
              self.setHeight(editorHeight);                  
              self.editor.layout();
          }
          

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
