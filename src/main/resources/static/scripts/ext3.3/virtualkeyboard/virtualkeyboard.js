Ext.onReady(function(){

	var ta = new Ext.form.TextArea({
		width: 370,
		hideLabel: true
	});

	new Ext.FormPanel({
		title: 'Virtual keyboard as a control',
		renderTo: 'virtualkeyboard-control',
		width: 372,
		items: [
			ta,
			{
				xtype: 'virtualkeyboard',
				keyboardTarget: ta,
				language: 'French',
				languageSelection: true,
				listeners: {
					keypress: function(keyboard, keyValue){
						if(typeof console != 'undefined')
							console.log(keyValue);
					}
				}
			}
		]
	});
	
	var plugin = new Ext.ux.plugins.VirtualKeyboard();
	
	plugin.on({
		expand: function(plugin){
			if(console)
				console.log('Keyboard expanded');
		},
		collapse: function(plugin){
			if(console)
				console.log('Keyboard collapsed');
		},
		scope: this
	});
	
	new Ext.FormPanel({
		title: "Fields with virtual keyboards",
		labelWidth: 75, // label settings here cascade unless overridden
		bodyStyle:'padding:5px 5px 0',
		width: 350,
		frame: true,
		renderTo: 'virtualkeyboard-form',
		defaults: {width: 230},
		defaultType: 'textfield',
		items: [{
				fieldLabel: 'Lithuanian',
				allowBlank:false,
				anchor: '-40',
				keyboardConfig: {
					language: 'Lithuanian',
					showIcon: true
				},
				plugins: plugin
			},{
				fieldLabel: 'Arabic',
				allowBlank:false,
				anchor: '-40',
				keyboardConfig: {
					language: 'Arabic',
					showIcon: true
				},
				plugins: plugin
			},{
				fieldLabel: 'Norwegian',
				keyboardConfig: {
					language: 'Norwegian',
					showIcon: true
				},
				plugins: plugin
			},{
				xtype: 'textarea',
				fieldLabel: 'Any language',
				keyboardConfig: {
					showIcon: true,
					languageSelection: true
				},
				plugins: plugin
			}
		],

		buttons: [{
			text: 'Save'
		},{
			text: 'Cancel'
		}]
	});
	
	new Ext.FormPanel({
		title: "Fields with virtual keyboards in status bar",
		labelWidth: 75, // label settings here cascade unless overridden
		bodyStyle:'padding:5px 5px 0',
		width: 350,
		renderTo: 'virtualkeyboard-status',
		defaults: {width: 230},
		defaultType: 'textfield',
		bbar: new Ext.StatusBar({
			id: 'form-statusbar',
			defaultText: 'Ready'/*,
			plugins: new Ext.ux.ValidationStatus({form:'status-form'})*/
			, items: [{
				text: 'Show keyboard',
				iconCls: 'ux-virtualkeyboard-icon',
				tooltip: 'Shows the keyboard for the active text field',
				listeners: {
					'click': function(){
						plugin.expand();
					}
				}
			}]
		}),
		items: [{
				fieldLabel: 'Hebrew',
				anchor: '100%',
				keyboardConfig: {
					language: 'Hebrew'
				},
				plugins: plugin
			},{
				fieldLabel: 'Greek',
				allowBlank:false,
				anchor: '100%',
				keyboardConfig: {
					language: 'Greek'
				},
				plugins: plugin
			},{
				fieldLabel: 'Russian',
				allowBlank:false,
				anchor: '100%',
				keyboardConfig: {
					language: 'Russian'
				},
				plugins: plugin
			},{
				fieldLabel: 'Any language',
				anchor: '100%',
				keyboardConfig: {
					languageSelection: true
				},
				plugins: plugin
			}
		],

		buttons: [{
			text: 'Save'
		},{
			text: 'Cancel'
		}]
	});
});