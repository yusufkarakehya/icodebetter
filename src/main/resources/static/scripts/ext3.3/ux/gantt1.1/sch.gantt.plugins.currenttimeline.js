/*
 * Ext Gantt 1.1
 * Copyright(c) 2009-2010 Mats Bryntse Consulting
 * mats@ext-scheduler.com
 * http://www.ext-scheduler.com/license.html
 *
 */

Ext.ns('Sch.gantt.plugins');
 
/**
 * @class Sch.gantt.plugins.CurrentTimeLine
 * @extends Sch.plugins.Lines
 * Internal plugin indicating current time using a vertical line
 * @constructor
 * @param {Object} config The object containing the configuration for the instance.
 */
Sch.gantt.plugins.CurrentTimeLine = function(config, grid) {
    Ext.apply(this, config);
    this.grid = grid;
    Sch.gantt.plugins.CurrentTimeLine.superclass.constructor.call(this);
};
 
Ext.extend(Sch.gantt.plugins.CurrentTimeLine, Sch.plugins.Lines, {
    
    /**
     * @cfg {String} tooltipText The text to show in the tooltip next to the current time (defaults to 'Current time').
     */
    tooltipText : 'Current time',
    
    /**
     * @cfg {Int} updateInterval This value (in ms) defines how often the timeline shall be refreshed.
     */
    updateInterval : 60000,
    
    /**
     * @cfg {Boolean} autoUpdate True to automatically update the line position over time.
     */
    autoUpdate : true,
    
    init : function() {
        var store = new Ext.data.JsonStore({
            fields : ['Date', 'Cls', 'Text'],
            data : [{Date : new Date(), Cls : 'sch-todayLine', Text : this.tooltipText}]
        });
            
        if (this.autoUpdate) {
            var runner = new Ext.util.TaskRunner();
            runner.start({
                run: function() {
                    store.getAt(0).set('Date', new Date());
                },
                interval: this.updateInterval // Update line store every minute
            });
        }
        
        this.store = store;
        Sch.gantt.plugins.CurrentTimeLine.superclass.init.apply(this, arguments);
    }
}); 
