
/*
* This is a modified version of the ck editor integration from the public
* domain source in http://www.sencha.com/forum/showthread.php?79031.
*
* Fixed issues when loading multiple editors in multiple TabPanels, wrong calls
* to the superclass methods.
*/
 
 
Ext.namespace("Ext.ux.form");
Ext.ux.form.CKEditor = function(config){
	this.config = config;
	this.config.CKConfig = Ext.apply({
		uiColor: '#DFE8F6'
	}, this.config.CKConfig);
	Ext.ux.form.CKEditor.superclass.constructor.call(this, this.config);
};
 
Ext.extend(Ext.ux.form.CKEditor, Ext.form.TextArea, {
	onRender : function(ct, position){
		if(!this.el){
			this.defaultAutoCreate = {
					tag: "textarea",
					autocomplete: "off"
			};
		}
		Ext.form.TextArea.superclass.onRender.call(this, ct, position);
		CKEDITOR.replace(this.id, this.config.CKConfig);
	},
	setValue : function(value){
		Ext.form.TextArea.superclass.setValue.call(this,[value]);
		var ck = CKEDITOR.instances[this.id];
		if (ck){
			ck.setData( value );
		}
	},
 
	getValue : function(){
		var ck = CKEDITOR.instances[this.id];
		if (ck){
			ck.updateElement();
		}
		return Ext.form.TextArea.superclass.getValue.call(this);
	},
	isDirty: function () {
		if (this.disabled || !this.rendered) {
			return false;
		}
		return String(this.getValue()) !== String(this.originalValue);
	},
 
	getRawValue : function(){
		var ck = CKEDITOR.instances[this.id];
		if (ck){
			ck.updateElement();
		}
		return Ext.form.TextArea.superclass.getRawValue.call(this);
	},
	destroyInstance: function(){
		var ck = CKEDITOR.instances[this.id];
		if (ck){
			delete ck;
		}
	}
	});
 
Ext.reg('ckeditor', Ext.ux.form.CKEditor);