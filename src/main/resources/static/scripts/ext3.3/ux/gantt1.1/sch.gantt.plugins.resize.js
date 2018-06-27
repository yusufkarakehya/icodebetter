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
 * @class Sch.gantt.plugins.Resize
 * @extends Sch.plugins.Resize
 * Internal plugin enabling resizing of event items
 * @constructor
 * @param {Object} config The object containing the configuration of this model.
 */
Sch.gantt.plugins.Resize = function(config) {
    Ext.apply(this, config);
    Sch.gantt.plugins.Resize.superclass.constructor.call(this);
};
 
Ext.extend(Sch.gantt.plugins.Resize, Sch.plugins.Resize, {
    
    /**
     * @cfg {Boolean} showDuration true to show the duration instead of the end date when resizing a task
     */
    showDuration : true,
    
    /**
     * @cfg {String} startText The text to show before the start date during a resize operation. Defaults to 'Start:'.
     */
    startText : 'Start:',
    
    /**
     * @cfg {String} durationText The text to show before the duration text during a resize operation. Defaults to 'Duration:'.
     */
    durationText : 'Duration:',
    
    /**
     * @cfg {String} dayText The text to show after the duration text during a resize operation. Defaults to 'd'.
     */
    dayText : 'd',
    
    init:function(grid) {
        if (this.showDuration) {
            this.tipTemplate = new Ext.Template(
                '<div class="sch-timetipwrap {cls}">',
                    '<div>' + this.startText + ' {startText}</div>',
                    '<div>' + this.durationText + ' {duration} ' + this.dayText + '</div>',
                '</div>'
            ).compile();
            
            // Use a custom method to render the tip contents
            this.getTipContent = this.getDurationTipContent;
        }
        Sch.gantt.plugins.Resize.superclass.init.apply(this, arguments);
    },
    
    // private
    getDurationTipContent : function(start, end, valid) {
        var g = this.grid,
            roundedStart = g.floorDate(start),
            roundedEnd = g.roundDate(end),
            formattedStart = g.getFormattedDate(start, 'floor'),
            duration = g.getDuration(roundedStart, roundedEnd);
        
        return this.tipTemplate.apply({
            cls : valid ? 'sch-tip-ok' : 'sch-tip-notok',
            startText : formattedStart,
            duration : duration
        });
    }
}); 
