
/*
* This is a modified version of the ck editor integration from the public
* domain source in http://www.sencha.com/forum/showthread.php?79031.
*
* Fixed issues when loading multiple editors in multiple TabPanels, wrong calls
* to the superclass methods.
*/
 
 
Ext.namespace("Ext.ux.form");
Ext.ux.form.CKEditor5 = function(config){
	this.config = config;
	this.config.CKConfig = Ext.apply({
	}, this.config.CKConfig);
	Ext.ux.form.CKEditor5.superclass.constructor.call(this, this.config);
};
 
Ext.extend(Ext.ux.form.CKEditor5, Ext.form.TextArea, {
	onRender : function(ct, position){
		if(!this.el){
			this.defaultAutoCreate = {
					tag: "textarea",
					autocomplete: "off"
			};
		}
		Ext.form.TextArea.superclass.onRender.call(this, ct, position);
		ClassicEditor
        .create( document.querySelector( '#'+this.id ), this.config.CKConfig )
        .then( editor => {
	        console.log( 'Editor was initialized', editor );
	        this.editor = editor;
	    } )
        .catch( error => {
            console.error( error );
        } );
//		ClassicEditor.replace(this.id, this.config.CKConfig);
	},
	setValue : function(value){
		var ck = this.editor;//ClassicEditor.instances[this.id];
		if (ck){
			ck.setData( value );
		} else setTimeout(()=>{
			var ck2 = this.editor;//ClassicEditor.instances[this.id];
			if (ck2){
				ck2.setData( value );
			}			
		},100);
	},
	setValue2 : function(value){
		Ext.form.TextArea.superclass.setValue.call(this,[value]);

	},
	getValue : function(){
		var ck = this.editor;//ClassicEditor.instances[this.id];
		if (ck){
			return ck.getData();
		}
		else 
			return Ext.form.TextArea.superclass.getValue.call(this);
	},
	isDirty: function () {
		if (this.disabled || !this.rendered) {
			return false;
		}
		return String(this.getValue()) !== String(this.originalValue);
	},
 
	getRawValue : function(){
		var ck = this.editor;//ClassicEditor.instances[this.id];
		if (ck){
			return ck.getData();
		}
		else return Ext.form.TextArea.superclass.getRawValue.call(this);
	},
	destroyInstance: function(){
        console.log( 'Editor destroying');
		var ck = this.editor;//ClassicEditor.instances[this.id];
		if (ck){
			ck.destroy();
			delete ck;
		}
	},
	onDestroy: function(){
		this.destroyInstance();
	}
});

 
Ext.reg('ckeditor5', Ext.ux.form.CKEditor5);