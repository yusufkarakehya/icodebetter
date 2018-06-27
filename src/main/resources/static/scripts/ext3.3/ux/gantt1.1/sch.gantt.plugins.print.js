/*
 * Ext Gantt 1.2 alpha
 * Copyright(c) 2009-2010 Mats Bryntse Consulting
 * mats@ext-scheduler.com
 * http://www.ext-scheduler.com/license.html
 *
 */

/**
 * @class Sch.gantt.plugins.Print
 * @extends Object
 * Plugin for printing an Ext Gantt instance
 * @constructor
 * @param {Object} config The object containing the configuration of this plugin.
 */
Sch.gantt.plugins.Print = Ext.extend(Object, {
    
    /**
     * @cfg {String} headerBgImagePath The path to the background image for the scheduler header.
     */
    headerBgImagePath : 'http://extjs.cachefly.net/ext-3.1.1/resources/images/default/grid/grid3-hrow.gif',
    
    /**
     * @cfg {String} milestoneImagePath The path to the image for a milestone.
     */
    milestoneImagePath : '../../../images/ext3.3/ux/gantt1.1/milestone.png',
    
    /**
     * @cfg {String} downArrowImagePath The path to the image for a down arrow.
     */
    downArrowImagePath : '../../../images/ext3.3/ux/gantt1.1/parentdownarrow.png',
    
    /**
     * @cfg {String} downArrowImagePath The path to the image for a down arrow.
     */
    dependencyArrowImagePath : '../../../images/ext3.3/ux/gantt1.1/dependencyarrow{direction}.png',
    
    /**
     * @cfg {String} zoneImgUrl The path to the image for the zone background
     */
    zoneImgUrl : 'zonebg.png',
    
    /**
     * @cfg {String} docType This is the DOCTYPE to use for the print window. It must be the same DOCTYPE as on your application page.
     */
    docType : '<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">',
    
    /**
     * @cfg {Boolean} Setting this to true will fit the time columns to the viewport prior to printing.
     */
    fitColumns : true,
 
    /**
     * An empty function by default, but provided so that you can perform a custom action
     * before the print plugin extracts data from the scheduler.
     * @param {SchedulerPanel} scheduler The scheduler instance
     * @method beforePrint
     */
    beforePrint : Ext.emptyFn, 
    
    /**
     * An empty function by default, but provided so that you can perform a custom action
     * after the print plugin has extracted the data from the scheduler.
     * @param {SchedulerPanel} scheduler The scheduler instance
     * @method afterPrint
     */
    afterPrint : Ext.emptyFn, 

    /**
     * @cfg {Boolean} autoPrintAndClose True to automatically call print and close the new window after printing.
     */
    autoPrintAndClose : true,
   
    constructor : function(config) {
        Ext.apply(this, config);
        Sch.gantt.plugins.Print.superclass.constructor.call(this);
    },
    
    init : function(component) {
        component.print = this.print.createDelegate(this);
    },
    
    // private, the template for the new window
    mainTpl : '{docType}' +
          '<html class="{htmlClasses}">' +
            '<head>' +
              '<meta content="text/html; charset=UTF-8" http-equiv="Content-Type" />' +
              '<title>{title}</title>' +
              '{styles}' +
            '</head>' +
            '<body class="{bodyClasses} sch-gantt-printable">'+
                '<div id="print" class="{ganttClasses}" style="width:{width}px">'+
                    '<div class="x-grid3"  style="width:{width-2}px">'+
                        '<div class="x-grid3-header">'+
                            '{[this.insertHeaderBgs(values)]}' +
                            '{header}' +
                        '</div>' +
                        '<div class="x-grid3-body">'+
                            '{rows}'+
                        '</div>' +
                    '</div>'+
                '</div>' +
            '</body>'+
          '</html>'
    ,
    
    // private
    getGridContent : function(p) {
        var v = p.getView(),
            cm = p.getColumnModel(),
            lockedWidth = v.lockedBody.getWidth(),
            depMgr = p.getDependencyManager(),
            painter = depMgr.painter,
            origTaskTemplate = p.eventTemplate,
            origParentTaskTemplate = p.parentEventTemplate,
            origMilestoneTemplate = p.milestoneTemplate,
            origDependencyArrowTemplate = painter.arrowTpl,
            origZoneTpl, header, rows, width;
        
        this.beforePrint(p);
        
        p.eventTemplate = this.printableEventTpl || origTaskTemplate;
        p.parentEventTemplate = this.printableParentEventTpl || origParentTaskTemplate;
        p.milestoneTemplate = this.printableMilestoneTpl || origMilestoneTemplate;
        painter.arrowTpl = this.printableDependencyArrowTpl || origDependencyArrowTemplate;
        
        if (p.highlightWeekends) {
            // Order a redraw of the weekend zones explicitly since by default it is deferred
            origZoneTpl = p.weekendZonesPlugin.template;
            p.weekendZonesPlugin.template = this.printableZoneTpl;
        }

        var lockedCount = p.getColumnModel().getLockedCount(),
            oldColWidth = cm.getColumnWidth(p.nbrStaticColumns);
        
        if (this.fitColumns) {
            v.fitTimeColumns(true);
        }
        
        // Unlock all columns "silently"
        for (var i = 0; i < lockedCount; i++) {
            cm.setLocked(i, false, true);
        }
        
        // Render rows and header
        header = v.renderHeaders()[0];
        
        // Refresh view
        v.refresh(true);
        
        // Order a repaint explicitly since by default it is deferred
        p.getDependencyManager().renderDependencies();

        if (p.highlightWeekends) {
            // HACK, order a redraw of the weekend zones explicitly since by default it is deferred
            p.weekendZonesPlugin.createZonesInternal();

            // Move the zones/lines to adjust for the lack of locked columns.
            p.view.scroller.select('.sch-zone, .sch-verticalLine').move('right', lockedWidth);
        }
        
        // Now grab the rendered content
        rows = v.scroller.dom.innerHTML;
        
        // Restore templates
        p.eventTemplate = origTaskTemplate;
        p.parentEventTemplate = origParentTaskTemplate;
        p.milestoneTemplate = origMilestoneTemplate;
        painter.arrowTpl = origDependencyArrowTemplate;
        if (p.highlightWeekends) {
            // Order a redraw of the weekend zones explicitly since by default it is deferred
            p.weekendZonesPlugin.template = origZoneTpl;
        }

        width = p.getColumnModel().getTotalWidth();
        
        // Reset the locked flag
        for (i = 0; i < lockedCount; i++) {
            cm.setLocked(i, true, true);
        }
        
        if (this.fitColumns) {
            v.updateTimeColumnWidths(oldColWidth);
        }
        
        this.afterPrint(p);
        
        // Refresh view to restore original state
        v.refresh.defer(100, v, [true]);

        return {
            header : header,
            rows : rows,
            width : width
        };
    },
    
    getStylesheets : function() {
        return Ext.getDoc().select('link[rel="stylesheet"]');
    },
    
    initTemplates : function(p) {
        if (!(this.mainTpl instanceof Ext.Template)) {
            // Compile the templates upon first call
            var hdSrc = this.headerBgImagePath;

            this.mainTpl = new Ext.XTemplate(this.mainTpl, {
                    insertHeaderBgs : function(vals) {
                        var retVal = '';
                        for (var i = 0; i < vals.nbrHeaderRows; i++) {
                            retVal += String.format('<img class="sch-gantt-printable-headerbg" src="{0}" style="width:{1}px;top:{2};"/>', hdSrc, vals.width, i*22);
                        }
                        return retVal;
                    },
                    compiled : true,
                    disableFormats : true
                }
            );
        }
        
        if (!(this.printableEventTpl instanceof Ext.Template)) {
            this.printableEventTpl = new Ext.XTemplate(
                '<div class="sch-event-wrap sch-gantt-task" style="left:{leftOffset}px;width:{width}px">',
                    // Left label 
                    '<div class="sch-gantt-labelct-left"><label class="sch-gantt-label sch-gantt-label-left">{leftLabel}</label></div>',
                    
                    // Task bar
                    '<div id="{id}" class="sch-gantt-printable-gantt-item sch-gantt-item sch-gantt-task-bar {cls}" style="width:{width}px;{style}">',
                        '<div class="sch-gantt-printable-eventbg" style="border-left-width:{width}px">&#160;</div>',
                        '<div class="sch-gantt-progress-bar sch-gantt-printable-eventprogress" style="border-left-width:{[Math.round(values.width * values.percentDone/100 )]}px">&#160;</div>',
                    '</div>',
                    
                    // Right label 
                    '<div class="sch-gantt-labelct-right" style="left:{width}px"><label class="sch-gantt-label sch-gantt-label-right">{rightLabel}</label></div>',
                '</div>',
                {
                    compiled : true,
                    disableFormats : true
                }
            );
        }
        
        if (!(this.printableParentEventTpl instanceof Ext.Template)) {
            this.printableParentEventTpl = new Ext.XTemplate(
                '<div class="sch-event-wrap sch-gantt-parent-task" style="left:{leftOffset}px;width:{width}px">',
                    // Left label 
                    '<div class="sch-gantt-labelct-left"><label class="sch-gantt-label sch-gantt-label-left">{leftLabel}</label></div>',
                    
                    '<div style="overflow:hidden" class="sch-gantt-parenttask-leftarrow">',
                        '<img src="' + this.downArrowImagePath + '"/>',
                    '</div>',
                    
                    // Task bar
                    '<div id="{id}" class="sch-gantt-printable-gantt-item sch-gantt-item sch-gantt-parenttask-bar {cls}" style="width:{width}px;{style}">',
                        // The parent background color
                        '<div class="sch-gantt-printable-parentbg" style="border-left-width:{width}px">&#160;</div>',
                        
                        '<div class="sch-gantt-progress-bar sch-gantt-printable-parenteventprogress" style="border-left-width:{[Math.round(values.width *  values.percentDone/100)]}px">&#160;</div>',
                    '</div>',
                    
                    '<div style="overflow:hidden" class="sch-gantt-parenttask-rightarrow">',
                        '<img src="' + this.downArrowImagePath + '"/>',
                    '</div>',
                    
                    // Right label 
                    '<div class="sch-gantt-labelct-right" style="left:{width}px"><label class="sch-gantt-label sch-gantt-label-right">{rightLabel}</label></div>',
                '</div>', 
                {
                    compiled : true,
                    disableFormats : true
                }
            );
        }
        
        if (!(this.printableMilestoneTpl instanceof Ext.Template)) {
            this.printableMilestoneTpl = new Ext.XTemplate(
                 '<div class="sch-event-wrap sch-gantt-milestone" style="left:{leftOffset}px">',
                    // Left label 
                    '<div class="sch-gantt-labelct-left"><label class="sch-gantt-label sch-gantt-label-left">{leftLabel}</label></div>',
                    
                    // Milestone indicator
                    '<div id="{id}" style="overflow:hidden" class="sch-gantt-milestone-diamond">',
                        '<img src="' + this.milestoneImagePath + '" class="sch-gantt-printable-gantt-milestone sch-gantt-item {cls}" style="{style}" />',
                    '</div>',
                    
                    // Right label 
                    '<div class="sch-gantt-labelct-right"><label class="sch-gantt-label sch-gantt-label-right">{rightLabel}</label></div>',
                '</div>',
                {
                    compiled : true,
                    disableFormats : true
                }
            );
        }
        
        if (!this.printableDependencyArrowTpl) {
            this.printableDependencyArrowTpl = new Ext.XTemplate(
                '<div class="sch-gantt-printable-dependencyarrow sch-dependency sch-dep-{id} sch-dependency-arrow sch-dependency-arrow-{direction}" style="left:{left}px;top:{top}px">',
                    '<img src="' + this.dependencyArrowImagePath + '"/>',
                '</div>',
                {
                    compiled : true,
                    disableFormats : true
                }
            );
        }

        if (!this.printableZoneTpl && p.highlightWeekends) {
            this.printableZoneTpl = new Ext.Template(
                '<img class="sch-zone {Cls} ' + p.weekendZonesPlugin.cls + '" src="' + this.zoneImgUrl + '" style="left:{left}px;width:{width}px;height:{height}px"/>'
            ); 
        }
    },

    /**
     * Prints a Gantt panel
     * @param {Sch.SchedulerPanel} scheduler The scheduler instance
     * @private
     */
    print : function(p) {
        // Init templates lazily
        this.initTemplates(p);

        var v = p.getView(),
            styles = this.getStylesheets(),
            ctTmp = Ext.get(Ext.DomHelper.createDom({
                tag : 'div'
            })),
            styleFragment; 
        
        styles.each(function(s) {
            ctTmp.appendChild(s.dom.cloneNode(true));
        });
        
        styleFragment = ctTmp.dom.innerHTML + '';
        
        var gridContent = this.getGridContent(p),
            html = this.mainTpl.apply({
                waitText : this.waitText,
                docType : this.docType,
                htmlClasses : '', // todo
                bodyClasses : Ext.getBody().dom.className,
                ganttClasses : p.el.dom.className,
                header : gridContent.header,
                rows : gridContent.rows,
                extBasePath : this.extBasePath,
                title : (p.title || ''),
                styles : styleFragment,
                width : gridContent.width,
                headerBgImagePath : this.headerBgImagePath,
                nbrHeaderRows : p.getColumnModel().rows ? (p.getColumnModel().rows.length + 1) : 1
            });
        
        var win = window.open('', 'printgrid');
    
        win.document.write(html);
        win.document.close();
        
        if (this.autoPrintAndClose) {
            win.print();
            win.close();
        }
    }
});