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
 * @class Sch.gantt.plugins.Tooltip
 * @extends Ext.ToolTip
 * Internal plugin showing event start/end information.
 * @constructor
 * @param {Object} config The object containing the configuration of this model.
 */
Sch.gantt.plugins.Tooltip = function(config, grid) {
    Ext.apply(this, config);
    this.grid = grid;
    Sch.gantt.plugins.Tooltip.superclass.constructor.call(this);
};
 
Ext.extend(Sch.gantt.plugins.Tooltip, Ext.ToolTip, {
    
    /**
     * @cfg {Boolean} showClock True to show clocks in front of the start/end dates
     */
    showClock : false,
    
    /**
     * @cfg {String} startText The text to show before the start date. Defaults to 'Starts:'.
     */
    startText : 'Starts: ',
    
    /**
     * @cfg {String} endText The text to show before the end date. Defaults to 'Ends:'.
     */
    endText : 'Ends: ',
    
    initComponent : function() {    
       
        
        if (!this.template) {
            if (this.showClock) {
                this.template = new Ext.Template(
                    '<div class="sch-timetipwrap {cls}">',
                    '<div class="sch-clock">',
                        '<img src="' + Ext.BLANK_IMAGE_URL + '" class="sch-hourIndicator" style="-moz-transform: rotate({startHourDegrees}deg);-webkit-transform: rotate({startHourDegrees}deg)"/>',
                        '<img src="' + Ext.BLANK_IMAGE_URL + '" class="sch-minuteIndicator" style="-moz-transform: rotate({startMinuteDegrees}deg);-webkit-transform: rotate({startMinuteDegrees}deg)"/>',
                        '{startText}',
                    '</div>',
                     '<div class="sch-clock">',
                        '<img src="' + Ext.BLANK_IMAGE_URL + '" class="sch-hourIndicator" style="-moz-transform: rotate({endHourDegrees}deg);-webkit-transform: rotate({endHourDegrees}deg)"/>',
                        '<img src="' + Ext.BLANK_IMAGE_URL + '" class="sch-minuteIndicator" style="-moz-transform: rotate({endMinuteDegrees}deg);-webkit-transform: rotate({endMinuteDegrees}deg)"/>',
                        '{endText}',
                    '</div>',
                '</div>'
                );
            } else {
                this.template = new Ext.Template(
                    '<div class="sch-timetipwrap {cls}">',
                    '<div>',
                        '{startText}',
                    '</div>',
                    '<div>',
                        '{endText}',
                    '</div>',
                '</div>'
                );
            }
        }
        
        this.template.compile();
        Sch.gantt.plugins.Tooltip.superclass.initComponent.apply(this, arguments);
    },
    
    cls : 'sch-tip',
    width: 145,
    height:40,
    autoHide : false,
    anchor : 'b-tl',
    
    update : function(start, end, valid) {
        var data = this.getTipData(start, end, valid);
        Sch.gantt.plugins.Tooltip.superclass.update.call(this, this.template.apply(data));
    },
     
    // private
    getTipData : function(start, end, valid) {
        var g = this.grid,
            roundedStart = g.floorDate(start),
            startText = this.startText + g.getFormattedDate(start, 'floor'),
            roundedEnd,
            endText = '&nbsp;';
        
        if (end - start > 0) {
            roundedEnd = g.floorDate(end);
            endText = this.endText + g.getFormattedEndDate(roundedEnd);
        }
        
        var retVal = {
            cls : valid ? 'sch-tip-ok' : 'sch-tip-notok',
            startText : startText,
            endText : endText
        };
        
        if (this.showClock) {
            Ext.apply(retVal, {
                startHourDegrees : roundedStart.getHours() * 30, 
                startMinuteDegrees : roundedStart.getMinutes() * 6
            });
            
            if (end) {
                Ext.apply(retVal, {
                    endHourDegrees : roundedEnd.getHours() * 30, 
                    endMinuteDegrees : roundedEnd.getMinutes() * 6
                });
            }
        }
        return retVal;
    },
    
    show : function(el) {
        this.anchorTarget = el;
        
        // Rendering is weird if the initial tooltip is empty, prepopulate it with some dummy html
        if (!this.rendered) {
            var dummyDate = new Date();
            this.html = this.template.apply(this.getTipData(dummyDate, dummyDate, true));
        }
        Sch.gantt.plugins.Tooltip.superclass.show.call(this);
    }
}); 
