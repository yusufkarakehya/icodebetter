Ext.ns("Ext.ux.form");
var defineCompletionItemProvider = {
    triggerCharacters:['$'],
    provideCompletionItems: (model, position) => {
      return [
        {
          label: '$.getTableJSON',
          kind: monaco.languages.CompletionItemKind.Function,
          documentation: "Load Table Record as JSON",
          detail: 'JSON',insertText:'$.getTableJSON("table_name",pk)'
        },
        {
          label: '$.postForm',
          kind: monaco.languages.CompletionItemKind.Function,
          documentation: "Post Form",
          detail: 'W5FormResult',insertText:'$.postForm(formId,action,{params})'
        },
        {
          label: '$.sqlQuery',
          kind: monaco.languages.CompletionItemKind.Function,
          documentation: "Run SELECT SQL",
          detail: 'Array',insertText:'$.sqlQuery("SELECT * FROM x WHERE y", {params})'
        },
        {
          label: '$.runQuery',
          kind: monaco.languages.CompletionItemKind.Function,
          documentation: "Run iCodeBetter Query",
          detail: 'Array',insertText:'$.runQuery(queryId, {params})'
        },
        {
          label: '$.sqlExecute',
          kind: monaco.languages.CompletionItemKind.Function,
          documentation: "Execute INSERT/UPDATE/DELETE SQL",
          detail: 'int',insertText:'$.sqlExecute("UPDATE x set y WHERE z", {params})'
        },
        {
          label: '$.execFunc',
          kind: monaco.languages.CompletionItemKind.Function,
          documentation: "Execute iCodeBetter Global Funcction",
          detail: 'W5GlobalFuncResult',insertText:'$.execFunc(globalFuncId, {params})'
        },
        {
          label: '$.REST',
          kind: monaco.languages.CompletionItemKind.Function,
          documentation: "Call REST Method",
          detail: 'Map',insertText:'$.REST("serviceName.methodName", {params})'
        },
        {
          label: '$.console',
          kind: monaco.languages.CompletionItemKind.Function,
          documentation: "Console",
          detail: 'null',insertText:'$.console("Hellow World", "Warning", "warn")'
        }
      ];
    }
  };
/*
function sqlFmt(sql){
    var url = "ajaxFormatSQL?sql=" + encodeURIComponent(sql);
    return fetch(url).then((response)=>{
    	var f = response.text();
    	return f;
    	console.log('aa',f)
    	f = eval('('+f+')');
    	console.log('bb',f)
      return f.result;
    });
  };*/
  
Ext.ux.form.Monaco = Ext.extend(Ext.BoxComponent, {
  value: null,
  language: "javascript",html:'<span style="color:#ce9178">Loading Monaco Editor...<\span>',

  initComponent: function() {
    Ext.ux.form.Monaco.superclass.initComponent.apply(this, arguments);

    this.on(
      {
        afterrender: function() {
          var self = this;
          require.config({ paths: { vs: "/monaco/min/vs" } });
          require(["vs/editor/editor.main"], function() {
	    	  self.el.dom.innerHTML='';
	    	  self.editor = monaco.editor.create(self.el.dom, {
	              value: self.value,
	              language: self.language
	            });
	    	  monaco.editor.setTheme("vs-dark");
	    	  if(defineCompletionItemProvider && self.language=='javascript'){
	    		  monaco.languages.registerCompletionItemProvider('javascript', defineCompletionItemProvider);
	    		  defineCompletionItemProvider=false;
	    	  }
            /*
          console.log("element: ", self.getEl());
          console.log("ownerCt: ", self.ownerCt);
          console.log("container: ", self.ownerCt.container);
          console.log("self.editor: ", self.editor);
          */
            var containerHeight = self.ownerCt.container.dom.clientHeight;
            var containerWidth = self.ownerCt.container.dom.clientWidth;

            if (containerHeight < 500 || containerWidth < 500 ) {
              containerHeight = 500;
              containerWidth = 500;
              self.ownerCt.setHeight(500);
              self.ownerCt.setWidth(500);
              self.ownerCt.container.setHeight(500);
              self.ownerCt.container.setWidth(500);
            }
            self.setHeight(containerHeight);
            self.setWidth(containerWidth);
            self.editor.layout();

            var itemLength = self.ownerCt.items.items.length;
            var totalChildHeight = 0;
            if (itemLength > 1) {
              for (var i = 0; i < itemLength; i++) {
                if (
                  self.ownerCt.items.items[i].editor == null &&
                  self.ownerCt.items.items[i].el
                ) {
                  totalChildHeight +=
                    self.ownerCt.items.items[i].el.dom.clientHeight;
                }
              }
              var editorHeight = containerHeight - totalChildHeight - 70;
              self.setHeight(editorHeight);
              self.editor.layout();
            }

            function resizeEditor() {
              if (
                (self.el.dom &&
                  self.editor.getDomNode() &&
                  self.editor.getDomNode().clientHeight !=
                    self.el.dom.clientHeight) ||
                (self.el.dom &&
                  self.editor.getDomNode() &&
                  self.editor.getDomNode().clientWidth !=
                    self.el.dom.clientWidth)
              ) {
                self.editor.layout();
                setTimeout(resizeEditor, 100);
              } else {
                setTimeout(resizeEditor, 100);
              }
            }
            resizeEditor();

            window.onresize = function() {
              self.editor.layout();
              if(self.ownerCt.container != null){
            	  containerHeight = self.ownerCt.container.dom.clientHeight;
                  self.setHeight(containerHeight);
              }
/*              containerHeight = self.ownerCt.container.dom.clientHeight;
              containerWidth = self.ownerCt.container.dom.clientWidth;
              self.setHeight(containerHeight);
              self.setWidth(containerWidth);
*/
              self.editor.layout();
            };
          });
        },
        show: function() {
          var self = this;
          var containerHeight = self.ownerCt.container.dom.clientHeight;
          if (containerHeight < 500) {
            containerHeight = 500;
          }
          self.setHeight(containerHeight);
          self.editor.layout();
        }
      },
      this
    );
  },

  getValue: function() {
    return this.editor.getValue();
  }
});

Ext.reg("monacoeditor", Ext.ux.form.Monaco);
