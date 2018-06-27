/*
 * Ext Gantt 1.1
 * Copyright(c) 2009-2010 Mats Bryntse Consulting
 * mats@ext-scheduler.com
 * http://www.ext-scheduler.com/license.html
 *
 */

Ext.ns('Sch.gantt.plugins');

/**
 * @ignore
 * @class Sch.gantt.plugins.LabelEditor
 * @extends Ext.util.Observable
 * Plugin for editing labels inline
 * @constructor
 * @param {Object} config The object containing the configuration 
 */
Sch.gantt.plugins.LabelEditor = function(config) {
    Ext.apply(this, config);
    Sch.gantt.plugins.LabelEditor.superclass.constructor.call(this, config);
};
 
Ext.extend(Sch.gantt.plugins.LabelEditor, Ext.util.Observable, {
    
    /**
     * Show editor for left task label
     * @param {Record} record the task record 
     */
    editLeft : function(record) {
        var wrap = this.grid.getElementFromEventRecord(record).up(this.grid.eventWrapSelector);
        this.editor.startEdit(wrap.child("." + this.leftLabelCls));
        this.record = record;
        this.editingLeft = true;
    },
    
    /**
     * Show editor for the right task label
     * @param {Record} record the task record 
     */
    editRight : function(record) {    
        var wrap = this.grid.getElementFromEventRecord(record).up(this.grid.eventWrapSelector);
        this.editor.startEdit(wrap.child("." + this.rightLabelCls));
        this.record = record;
        this.editingLeft = false;
    },
    
    // private
    delegate : '.sch-gantt-label',
    
    // private
    leftLabelCls : 'sch-gantt-label-left',
    
    // private
    rightLabelCls : 'sch-gantt-label-right',
    
    // Override this to provide your own configuration of the editor
    editorCfg : {
        shadow: false,
        completeOnEnter: true,
        cancelOnEsc: true,
        autoSize : 'width',
        ignoreNoChange: true
    },
    
    // Override this to provide your own configuration of the field
    fieldCfg : {
        allowBlank: false,
        xtype: 'textfield',
        selectOnFocus: true
    },

    init:function(grid) {
        this.editor = new Ext.Editor(Ext.apply({
            alignment: 'r-r',
            field: this.fieldCfg,
            listeners : {
                'beforecomplete' : function(editor, value, original) {
                    return grid.fireEvent('labeledit_beforecomplete', grid, value, original, this.record);
                },
                'complete' : {
                    fn : function(editor, value, original) {
                        this.record.set(this.editingLeft ? grid.leftLabelField : grid.rightLabelField, value);
                        grid.fireEvent('labeledit_complete', grid, value, original, this.record);
                    },
                    scope : this
                }
            }
        }, this.editorCfg));
        
        this.grid = grid; 
        grid.on('render', this.onGridRender, this);
    },
    
    onGridRender : function(g) {
        g.getView().mainBody.on('dblclick', function(e, t){
            var isLeft = t.className.match(this.leftLabelCls);
            this.editor.startEdit(t);
            this.record = g.getEventRecordFromElement(t);
            this.editingLeft = !!isLeft;
        }, this, {
            delegate: this.delegate
        });
    }
}); 
