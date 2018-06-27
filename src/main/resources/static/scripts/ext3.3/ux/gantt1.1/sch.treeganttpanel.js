/*
 * Ext Gantt 1.1
 * Copyright(c) 2009-2010 Mats Bryntse Consulting
 * mats@ext-scheduler.com
 * http://www.ext-scheduler.com/license.html
 *
 * This file is partially based on Maxim TreeGrid, BSD license:
 *   Copyright (c) 2009, Maxim G. Bazhenov
 *
 *   All rights reserved.
 *
 *   Redistribution and use in source and binary forms, with or without modification, 
 *   are permitted provided that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this 
 *    list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 *
 *   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 *   AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 *   IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 *   ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 *   FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 *   DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 *   SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 *   CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, 
 *   OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
 *   OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 
Ext.ns('Sch');

/**
 * @class Sch.TreeGanttPanel
 * @extends Sch.EditorSchedulerPanel
 * <p>The TreeGanttPanel panel, encapsulating the Gantt specific functionality. It is partially based on <a href="http://www.extjs.com/forum/showthread.php?76331-TreeGrid-%28Ext.ux.maximgb.tg%29-a-tree-grid-component-based-on-Ext-s-native-grid.">MaximGB's TreeGrid</a>. This means you'll need to use a certain type of store for your data, either a {@link Ext.ux.maximgb.tg.NestedSetStore NestedSetStore} or an {@link Ext.ux.maximgb.tg.AdjacencyListStore AdjacencyListStore}. </p>
 <pre><code>
        
var start = new Date(2010,0,1),
    end = start.add(Date.MONTH, 10);

var store = new Ext.ux.maximgb.tg.AdjacencyListStore({
    defaultExpanded : false,
    autoLoad : true,
    proxy : new Ext.data.HttpProxy({
        url : 'tasks.json',
        method:'GET'
    }),
    reader: new Ext.data.JsonReader({idProperty : 'Id'}, [
            // Mandatory fields
            {name:'Id'},
            {name:'Name', type:'string'},
            {name:'StartDate', type : 'date', dateFormat:'c'},
            {name:'EndDate', type : 'date', dateFormat:'c'},
            {name:'PercentDone'},
            {name:'ParentId', type: 'auto'},
            {name:'IsLeaf', type: 'bool'},

            // Your task meta data goes here
            {name:'Responsible'}
        ]
    )
});

var dependencyStore = new Ext.data.JsonStore({   
    autoLoad : true,
    proxy : new Ext.data.HttpProxy({
        url : 'dependencies.json',
        method:'GET'
    }),
    fields : [
        // 3 mandatory fields
        {name:'From'},
        {name:'To'},
        {name:'Type'}
    ]
});

var g = new Sch.TreeGanttPanel({
    height : 600,
    width: 1000,
    renderTo : Ext.getBody(),
    leftLabelField : 'Name',
    highlightWeekends : false,
    showTodayLine : true,
    loadMask : true,
    
    viewModel : {
        start : start, 
        end : end, 
        columnType : 'monthAndQuarters',
        viewBehaviour : Sch.ViewBehaviour.MonthView
    },
    
    // Setup your static columns
    colModel : new Ext.ux.grid.LockingColumnModel({
        columns : [
           {
                header : 'Tasks', 
                sortable:true, 
                dataIndex : 'Name', 
                locked : true,
                width:250, 
                editor : new Ext.form.TextField()
           }
        ]
    }),
    store : store,
    dependencyStore : dependencyStore,
    trackMouseOver : false,
    stripeRows : true
});
  </code></pre>
  <p>The columnType property maps to constructor functions used to create the actual grid columns (defined in Sch.ColumnFactory). Possible values:</p>
 <ul>
    <li>quarterMinutes</li>
    <li>hour</li>
    <li>hourAndDay</li>     
    <li>dayAndHours</li>    
    <li>day</li>
    <li>dayAndWeeks</li>    
    <li>dayAndMonths</li>   
    <li>dayWeekAndMonths</li>  
    <li>week</li>   
    <li>weekAndMonths</li>  
    <li>weekAndDays</li>    
    <li>month</li>
    <li>monthAndQuarters</li>   
    <li>year</li>   
 </ul>
 * @xtype gantt
 */
Sch.TreeGanttPanel = Ext.extend(Sch.EditorSchedulerPanel, {
    /**
     * @cfg {Boolean} highlightWeekends
     * True to highlight weekends
     */
    highlightWeekends : true,
    
    /**
     * @cfg {Boolean} enableTaskDragDrop
     * True to allow drag drop of tasks
     */
    enableTaskDragDrop : true,
    
    /**
     * @cfg {Boolean} enableDependencyDragDrop
     * True to allow creation of dependencies by using drag and drop between task terminals
     */
    enableDependencyDragDrop : true,
     
    /**
     * @cfg {Boolean} enableLabelEdit
     * True to allow inline editing of labels
     */
    enableLabelEdit : true,
    
    /**
     * @cfg {Boolean} toggleParentTasksOnClick
     * True to toggle the collapsed/expanded state when clicking a parent task bar
     */
    toggleParentTasksOnClick : true,
    
    /**
     * @cfg {Boolean} recalculateParentsAfterEdit
     * True to update parent start/end dates after a task has been updated
     */
    recalculateParentsAfterEdit: true,
    
    /**
     * @cfg {Boolean} cascadeChanges
     * True to cascade changes to dependent tasks 
     */
    cascadeChanges: false,
     
   /**
    * @cfg {Boolean} showTodayLine
    * True to show a line indicating current time
    */
    showTodayLine : false,
     
    /**
     * @cfg {Boolean} highlightAffectedTasks
     * True to highlight affected tasks while editing (only applicatble when cascadeChanges is set to true), whether it be inline editing, drag and drop or resize operations
     */
    highlightAffectedTasks: true,
    
    resizeHandles : 'both',
    
    /**
     * @cfg {Ext.Template} eventTemplate The template used to renderer leaf tasks in the gantt view. See {@link Ext.Template} for more information. 
     <p>Defaults to:</p>
     <pre><code>
&lt;div class="sch-event-wrap sch-gantt-task" style="left:{leftOffset}px;width:{width}px">
    // Left label 
    &lt;div class="sch-gantt-labelct-left">&lt;label class="sch-gantt-label sch-gantt-label-left">{leftLabel}&lt;/label>&lt;/div>
    
    // Task bar
    &lt;div id="{id}" class="sch-event sch-gantt-item sch-gantt-task-bar {cls}" style="width:{width}px">
        // Left terminal
        &lt;div class="sch-gantt-terminal sch-gantt-terminal-start">&lt;/div>
        
        // Left resize handle
        &lt;div class="x-resizable-handle x-resizable-handle-west">&lt;/div>
    
        &lt;div class="sch-gantt-progress-bar" style="width:{percentDone}%">&#160;&lt;/div>
        
        // Right resize handle
        &lt;div class="x-resizable-handle x-resizable-handle-east">&lt;/div>
        
        // Right terminal
        &lt;div class="sch-gantt-terminal sch-gantt-terminal-end">&lt;/div>
    &lt;/div>
    
    // Right label 
    &lt;div class="sch-gantt-labelct-right" style="left:{width}px">&lt;label class="sch-gantt-label sch-gantt-label-right">{rightLabel}&lt;/label>&lt;/div>
&lt;/div>
     </code></pre>
     <p>To provide your own custom template you must use this as a base.</p>
     */
      
    /**
     * @cfg {Ext.Template} parentEventTemplate The template used to renderer parent tasks in the gantt view. See {@link Ext.Template} for more information. Defaults to:
     <pre><code>
&lt;div class="sch-event-wrap sch-gantt-parent-task" style="left:{leftOffset}px;width:{width}px">
    // Left label 
    &lt;div class="sch-gantt-labelct-left">&lt;label class="sch-gantt-label sch-gantt-label-left">{leftLabel}&lt;/label>&lt;/div>
    
    // Task bar
    &lt;div id="{id}" class="sch-event sch-gantt-item sch-gantt-parenttask-bar {cls}" style="width:{width}px">
        &lt;div class="sch-gantt-parenttask-leftarrow">&lt;/div>
        &lt;div class="sch-gantt-progress-bar" style="width:{percentDone}%">&#160;&lt;/div>
        &lt;div class="sch-gantt-parenttask-rightarrow">&lt;/div>
    &lt;/div>
    
    // Right label 
    &lt;div class="sch-gantt-labelct-right" style="left:{width}px">&lt;label class="sch-gantt-label sch-gantt-label-right">{rightLabel}&lt;/label>&lt;/div>,
&lt;/div>
     </code></pre>
     <p>To provide your own custom template you must use this as a base.</p>
     */
    
    /**
     * @cfg {Ext.Template} milestoneTemplate The template used to renderer parent tasks in the gantt view. See {@link Ext.Template} for more information. Defaults to:
     <pre><code>
&lt;div class="sch-event-wrap sch-gantt-milestone" style="left:{leftOffset}px">
    // Left label 
    &lt;div class="sch-gantt-labelct-left">&lt;label class="sch-gantt-label sch-gantt-label-left">{leftLabel}&lt;/label>&lt;/div>
    
    // Milestone indicator
    &lt;div id="{id}" class="sch-event sch-gantt-item sch-gantt-milestone-diamond {cls}">
        // Left terminal
        &lt;div class="sch-gantt-terminal sch-gantt-terminal-start">&lt;/div>
        // Right terminal
        &lt;div class="sch-gantt-terminal sch-gantt-terminal-end">&lt;/div>
    &lt;/div>
    
    // Right label 
    &lt;div class="sch-gantt-labelct-right">&lt;label class="sch-gantt-label sch-gantt-label-right">{rightLabel}&lt;/label>&lt;/div>
&lt;/div>
     </code></pre>
     
     <p>To provide your own custom milestone template you must use this as a base.</p>
     */
    
    
    
    
    
    /**
     * Wrapper function returning the dependency manager instance
     * @return {Sch.DependencyManager} dependencyManager The dependency manager instance
     */
    getDependencyManager : function() {
        return this.dependencyManager;
    },
    
    /**
     * Toggle weekend highlighting
     * @param {Boolean} disabled 
     */
    disableWeekendHighlighting : function(disabled) {
        this.weekendZonesPlugin.setDisabled(disabled);
    },
    
    /**
     * @cfg {Ext.data.Store} eventStore
     * NOT USED
     */
    
    /**
     * @cfg {Ext.ux.maximgb.tg.AbstractTreeStore} store The {@link Ext.ux.maximgb.tg.AbstractTreeStore store} holding the tasks to be rendered into the gantt chart (required).
     */
      
    /**
     * This function updates only the column header width, could be used with a slider to give a preview of the new column width before making the change (which is a heavy operation)
     * @param {Int} width The new header width 
     */
    updateTimeColumnHeaderWidths : function(width) {
        var cm = this.getColumnModel();
        for (var i = this.nbrStaticColumns, l = cm.getColumnCount(); i < l; i++) {
	        cm.setColumnWidth(i, width, true);
	    }
	    
	    this.getView().updateHeaders();
    },
    
    /**
     * This function updates the time column widths of the gantt panel
     * @param {Int} width The new column width 
     */
    updateTimeColumnWidths : function(width) {
        this.getView().updateTimeColumnWidths(width);
    },
    
    /**
     * <p>Returns the event record for a DOM id or html element </p>
     * @param {Mixed} el The DOM node or Ext Element to lookup
     * @return {Record} The event record
     */
    getEventRecordFromElement : function(el) {
        var element = Ext.get(el);
        if (!element.is(this.eventWrapSelector)) {
            element = element.up(this.eventWrapSelector);
        }
        return this.getEventRecordFromDomId(element.child(this.eventSelector).id);
    },
    
    /**
     * <p>Tries to fit the time columns to the available view width</p>
     */
    fitTimeColumns : function() {
        this.getView().fitTimeColumns();
    },
  
    //Returns the duration in days between two dates
    getDuration : function(start, end) {
        return Math.round(Date.getDurationInDays(start, end)*10)/10;
    },
    
    // Number of pixels to offset a milestone diamond
    milestoneOffset : 8,
    
    // Number of pixels to offset a parent task 
    parentTaskOffset : 6,
    
    clicksToEdit : 1,
    
    columnLines : false,
    
    // private
    eventSelector : '.sch-gantt-item',
    
    eventWrapSelector : '.sch-event-wrap',
    
    enableColLock : false,
    
    /**
     * An empty function by default, but provided so that you can perform custom validation on 
     * the item being dragged. This function is called during the drag and drop process and also after the drop is made
     * @param {Ext.data.Record} record The record being dragged
     * @param {Date} date The date corresponding to the current start date
     * @param {Int} duration The duration of the item being dragged, in minutes
     * @param {Ext.EventObject} e The event object
     * @return {Boolean} true if the drop position is valid, else false to prevent a drop
     */
    dndValidatorFn : function(record, date, duration, e) {
        return true;
    },
    
    // private
    constructor : function(config) {
        this.addEvents(
            /**
             * @event labeledit_beforecomplete
             * Fires after a change has been made to a label field, but before the change is reflected in the underlying field.
             * @param {TreeGanttPanel} g The gantt panel object
             * @param {Mixed} value The current field value
             * @param {Mixed} startValue The original field value
             * @param {Record} record The affected record 
             */
            'labeledit_beforecomplete', 
            
            /**
             * @event labeledit_complete
             * Fires after editing is complete and any changed value has been written to the underlying field.
             * @param {TreeGanttPanel} g The gantt panel object
             * @param {Mixed} value The current field value
             * @param {Mixed} startValue The original field value
             * @param {Record} record The affected record 
             */
            'labeledit_complete'
        );
        config = config || {};
        Ext.applyIf(config, {
            plugins : []
        });
        
        Ext.apply(this, config);
        
        if (!config.eventTemplate) {
            config.eventTemplate = new Ext.Template(
                '<div class="sch-event-wrap sch-gantt-task" style="left:{leftOffset}px;width:{width}px">',
                    // Left label 
                    '<div class="sch-gantt-labelct-left"><label class="sch-gantt-label sch-gantt-label-left">{leftLabel}</label></div>',
                    
                    // Task bar
                    '<div id="{id}" class="sch-gantt-item sch-gantt-task-bar {cls}" style="width:{width}px;{style}">',
                        // Left terminal
                        config.enableDependencyDragDrop !== false ? '<div class="sch-gantt-terminal sch-gantt-terminal-start"></div>' : '',
                        (this.resizeHandles === 'both' || this.resizeHandles === 'left') ? String.format(this.resizeHandleHtml, 'west') : '',
                    
                        '<div class="sch-gantt-progress-bar" style="width:{percentDone}%">&#160;</div>',
                        (this.resizeHandles === 'both' || this.resizeHandles === 'right') ? String.format(this.resizeHandleHtml, 'east') : '',
                        // Right terminal
                        config.enableDependencyDragDrop !== false ? '<div class="sch-gantt-terminal sch-gantt-terminal-end"></div>' : '',
                    '</div>',
                    
                    // Right label 
                    '<div class="sch-gantt-labelct-right" style="left:{width}px"><label class="sch-gantt-label sch-gantt-label-right">{rightLabel}</label></div>',
                '</div>',
            {
                compiled: true,      
                disableFormats: true 
            });
        }
        
        if (!config.parentEventTemplate) {
            config.parentEventTemplate = new Ext.Template(
                '<div class="sch-event-wrap sch-gantt-parent-task" style="left:{leftOffset}px;width:{width}px">',
                    // Left label 
                    '<div class="sch-gantt-labelct-left"><label class="sch-gantt-label sch-gantt-label-left">{leftLabel}</label></div>',
                    
                    // Task bar
                    '<div id="{id}" class="sch-gantt-item sch-gantt-parenttask-bar {cls}" style="width:{width}px;{style}">',
                        '<div class="sch-gantt-parenttask-leftarrow"></div>',
                        
                        // Left terminal
                        config.enableDependencyDragDrop !== false ? '<div class="sch-gantt-terminal sch-gantt-terminal-start"></div>' : '',
                        
                        '<div class="sch-gantt-progress-bar" style="width:{percentDone}%">&#160;</div>',
                        
                        '<div class="sch-gantt-parenttask-rightarrow"></div>',
                        // Right terminal
                        config.enableDependencyDragDrop !== false ? '<div class="sch-gantt-terminal sch-gantt-terminal-end"></div>' : '',
                    '</div>',
                    
                    // Right label 
                    '<div class="sch-gantt-labelct-right" style="left:{width}px"><label class="sch-gantt-label sch-gantt-label-right">{rightLabel}</label></div>',
                '</div>',
            {
                compiled: true,      
                disableFormats: true 
            });
        }
    
        if (!config.milestoneTemplate) {
            config.milestoneTemplate = new Ext.Template(
                '<div class="sch-event-wrap sch-gantt-milestone" style="left:{leftOffset}px">',
                    // Left label 
                    '<div class="sch-gantt-labelct-left"><label class="sch-gantt-label sch-gantt-label-left">{leftLabel}</label></div>',
                    
                    // Milestone indicator
                    '<div id="{id}" class="sch-gantt-item sch-gantt-milestone-diamond {cls}" style="{style}">',
                        // Left terminal
                        config.enableDependencyDragDrop !== false ? '<div class="sch-gantt-terminal sch-gantt-terminal-start"></div>' : '',
                        
                        // Right terminal
                        config.enableDependencyDragDrop !== false ? '<div class="sch-gantt-terminal sch-gantt-terminal-end"></div>' : '',
                    '</div>',
                    
                    // Right label 
                    '<div class="sch-gantt-labelct-right"><label class="sch-gantt-label sch-gantt-label-right">{rightLabel}</label></div>',
                '</div>',
            {
                compiled: true,      
                disableFormats: true 
            });
        }
    
        Sch.TreeGanttPanel.superclass.constructor.call(this, config);
    },
    
    // private
    configureFunctionality : function() {
        var plugs = this.plugins;
        
        if (this.enableLabelEdit !== false) {
            this.labelEditor = new Sch.gantt.plugins.LabelEditor();
            plugs.push(this.labelEditor);
        }
        
        if (this.resizeHandles !== 'none' && !this.resizePlug) {
            this.resizePlug = new Sch.gantt.plugins.Resize({
                validatorFn : this.resizeValidatorFn || Ext.emptyFn,
                validatorFnScope : this.validatorFnScope || this
            });
            plugs.push(this.resizePlug);
        }
        
        if (this.enableTaskDragDrop && !this.dragdropPlug) {
            this.dragdropPlug = new Sch.gantt.plugins.DragDrop({
                validatorFn : this.dndValidatorFn  || Ext.emptyFn,
                validatorFnScope : this.validatorFnScope || this,
                getItemDepth : 8
            });
            
            this.on('beforednd', function(p, record, e) {
                // Stop task drag and drop when a resize handle, a terminal or a parent item is clicked
                var t = e.getTarget();
                return !t.className.match('x-resizable-handle') && !t.className.match('sch-gantt-terminal') && !e.getTarget('.sch-gantt-parenttask-bar');
            });
            plugs.push(this.dragdropPlug);
        }
        
        if (this.highlightWeekends) {
            this.weekendZonesPlugin = new Sch.plugins.Zones({
                store : new Ext.data.JsonStore({
                    fields : ['StartDate', 'EndDate']
                })
            });
            plugs.push(this.weekendZonesPlugin);
        }
        
        if (this.showTodayLine) {
            this.todayLinePlugin = new Sch.gantt.plugins.CurrentTimeLine();
            plugs.push(this.todayLinePlugin);
        }
        
        if (this.highlightAffectedTasks) {
            
            this.on('beforeedit', function(o) {
                if (o.field === 'StartDate' || o.field === 'EndDate' || o.field === 'Duration') {
                    this.doHighlightAffectedTasks(o.record.id);
                }
            }, this);
            
            this.on('afterrender', function() {
                var colConfig = this.getColumnModel().config;
                
                // Wire up 'hide' listeners for start date and end date editors to be able to unhighlight
                // dom nodes after user cancelled an edit
                for (var i = 0; i < colConfig.length; i++) {
                    if ((colConfig[i].dataIndex === 'StartDate' || colConfig[i].dataIndex === 'EndDate' || colConfig[i].dataIndex === 'Duration') && colConfig[i].editor) {
                        colConfig[i].editor.on('hide', this.clearSelectedTasksAndDependencies, this);
                    }
                }
            }, this);
            
            if (this.resizePlug) {
                this.on('resizestart', function(grid, record) {
                    this.doHighlightAffectedTasks(record.id);
                }, this);
                
                this.on('afterresize', function(grid, record) {
                    this.clearSelectedTasksAndDependencies();
                }, this);
            }
            
            if (this.dragdropPlug) {
                this.on('dndstart', function(grid, record) {
                    this.doHighlightAffectedTasks(record.id);
                }, this);
                
                this.on('afterdnd', function() {
                    this.clearSelectedTasksAndDependencies();
                }, this);
            }
        }
    },
    
    isMilestone : function(task) {
        return task.get('EndDate') - task.get('StartDate') === 0;
    },
    
    // private, override base class version to provide the gantt specific view
    internalRenderer : function(v, m, event, row, col, ds) {
        var cellResult = '',
            grid = this,
            viewStart = grid.getStart(),
            viewEnd = grid.getEnd(),
            cm = grid.getColumnModel(),
            colWidth = cm.getColumnWidth(col),
            colStart = grid.getColumnStart(col),
            colEnd = grid.getColumnEnd(col);
            
        // Call timeCellRenderer to be able to set css/style properties on individual grid cells
        grid.timeCellRenderer.call(this, event, m, row, col, ds, colStart, colEnd, grid);
            
        var start = event.get('StartDate'),
            end = event.get('EndDate'),
            startsInsideView = start.betweenLesser(colStart, colEnd);
        
        // Determine if the event should be rendered or not
        if (startsInsideView || (col == grid.nbrStaticColumns && start < colStart && end > colStart)) {
            
            var availableTimeInColumn = Date.getDurationInMinutes(colStart, colEnd),
                leftOffset = Math.floor((Date.getDurationInMinutes(colStart, startsInsideView ? start : colStart) / availableTimeInColumn) * colWidth),
                // Get data from user "renderer" 
                eventData = grid.eventRenderer.call(this, event, row, col, ds) || {};
                
            if (grid.isMilestone(event)) {
                Ext.apply(eventData, {
                    // Core properties
                    id : grid.eventPrefix + event.id,
                    cls : (eventData.cls || '') + (event.dirty ? ' sch-dirty' : ''),
                    leftOffset : leftOffset - grid.milestoneOffset, // Remove half of the diamond width
                    
                    // Gantt specific
                    leftLabel : event.get(grid.leftLabelField) || '',
                    rightLabel : event.get(grid.rightLabelField) || ''
                });
                
                cellResult += grid.milestoneTemplate.apply(eventData);
            } else {
                // Regular task
                var itemWidth = Math.floor(grid.getXFromDate(Date.min(end, viewEnd)) - grid.getXFromDate(startsInsideView ? start : viewStart)),
                    endsOutsideView = end > viewEnd,
                    isLeaf = ds.isLeafNode(event);
                
                if (!isLeaf) {
                    itemWidth += 2*grid.parentTaskOffset; // Add the down arrow width
                    leftOffset -= grid.parentTaskOffset; // Remove half of the down arrow width
                }
                
                // Apply scheduler specific properties
                Ext.apply(eventData, {
                    // Core properties
                    id : grid.eventPrefix + event.id,
                    cls : (eventData.cls || '') + (event.dirty ? ' sch-dirty' : '') + (endsOutsideView ? ' sch-event-endsoutside ' : '') + (startsInsideView ? '' : ' sch-event-startsoutside'),
                    width : Math.max(1, itemWidth - grid.eventBorderWidth),
                    leftOffset : leftOffset,
                    
                    // Gantt specific
                    percentDone : event.get('PercentDone'),
                    leftLabel : event.get(grid.leftLabelField) || '',
                    rightLabel : event.get(grid.rightLabelField) || ''
                });
                
                eventData.text = eventData.text || '&#160;';
                cellResult += grid[isLeaf ? "eventTemplate" : "parentEventTemplate"].apply(eventData);
            }
        }
        
        m.css += ' sch-timetd';
        
        // Z-index is trouble in IE, thanks Condor for this fix
        if (Ext.isIE) {
            m.attr = 'style="z-index:' + (grid.getColumnModel().getColumnCount() - col) + '"';
        }
        return cellResult;
    },
    
    // private
    populateWeekendZonesPlugin : function() {
        var data = [],
            gStart = this.getStart(),
            gEnd = this.getEnd(),
            c = gStart.clone();
        
        while(c.getDay() !== 6) {
            c = c.add(Date.DAY, 1);
        }
        
        while (c < gEnd) {
            data.push({
                StartDate : c,
                EndDate : c.add(Date.DAY, 2)
            });
            c = c.add(Date.WEEK, 1);
        }
        
        this.weekendZonesPlugin.store.loadData(data);
    },
    
    // private
    getView : function(){
        if(!this.view){
            this.viewConfig = this.viewConfig || {};
            Ext.applyIf(this.viewConfig, {
                cellSelectorDepth : 22,
                rowSelectorDepth : 12
            });
            this.view = new Sch.TreeGanttView(this.viewConfig);
        }
        return this.view;
    },
    
    // private
    initComponent : function() {
        // Make sure all side components relying on the eventStore property will still work
        this.eventStore = this.store;
        if (this.highlightWeekends) {
            this.on('viewchange', this.populateWeekendZonesPlugin, this);
        }
        
        if (!this.dependencyStore) {
            // Assign an empty store if one hasn't been provided
            this.dependencyStore = new Ext.data.Store();
        }
        
        // Don't clear selections when clicking outside a task, required for editors to be able to
        // highlight affected tasks properly when highlightAffectedTasks = true
        if(!this.selModel && !this.disableSelection){
            this.selModel = new Sch.EventSelectionModel({
                clearSelectionsOnBlur : false
            });
        }
        
        Sch.TreeGanttPanel.superclass.initComponent.call(this);
    },
    
    initEvents : function() {
        this.on('afterrender', this.onGanttRender, this);
        
        if (this.recalculateParentsAfterEdit) {
            this.store.on({
                'update' : this.onStoreUpdate, 
                'add' : this.onStoreAddRemove, 
                'remove' : this.onStoreAddRemove, 
                scope : this
            });
        }
        Sch.TreeGanttPanel.superclass.initEvents.call(this);
    },
    
    // private, Override to only count the time columns since the view is a locking view meaning a separate container element
    getXFromDate : function(date) {
       var retVal = -1,
           cm = this.getColumnModel(),
           count = cm.getColumnCount();
           
       for (var i = this.nbrStaticColumns; i < count; i++) {
            if (date <= this.getColumnEnd(i)) {
                var diff = date - this.getColumnStart(i),
                    timeInColumn = this.getColumnEnd(i) - this.getColumnStart(i),
                    cw = cm.getColumnWidth(i);
                    
                return (cw * ((i - this.nbrStaticColumns) + (diff / timeInColumn)));
            } 
       }
       
       return null;
    },
    
    // private
    onGanttRender : function() {    
        var v = this.getView();
        this.el.addClass('sch-ganttpanel' + (this.highlightWeekends ? ' sch-ganttpanel-highlightweekends' : ''));
        
        this.dependencyManager = new Sch.DependencyManager(this, {
            containerEl : v.scroller,
            checkVisible : true,
            enableDependencyDragDrop : this.enableDependencyDragDrop,
            store : this.dependencyStore
        });
        
        v.on({
            'refresh' : this.dependencyManager.renderDependencies,
            'togglerow' : this.dependencyManager.renderDependencies,
            scope : this.dependencyManager
        });
    },
    
    
    editLeftLabel : function(record) {
        this.labelEditor.editLeft(record);
    },
    
    editRightLabel : function(record) {
        this.labelEditor.editRight(record);
    },
    
    getDependenciesForTask : function(record) {
        return this.dependencyManager.getDependenciesForTask(record);
    },
    
    /**
     * Convenience method wrapping the dependency manager method which highlights the elements representing a particular dependency
     * @param {Mixed} record Either the id of a record or a record in the dependency store
     */
    highlightDependency : function(record) {
        this.dependencyManager.highlightDependency(record);
    },
    
    /**
     * Convenience method wrapping the dependency manager method which unhighlights the elements representing a particular dependency
     * @param {Mixed} depId Either the id of a record or a record in the dependency store
     */
    unhighlightDependency : function(record) {
        this.dependencyManager.unhighlightDependency(record);
    },
    
    getTaskById : function(id) {
        return this.store.getById(id);
    },
    
    doHighlightAffectedTasks : function(taskId) {
        if (!this.cascadeChanges) {
            return;
        }
        
        this.getSelectionModel().clearSelections();
        this.highlightTask(taskId);
    },
    
    /**
     * <p> Highlights a task and optionally any dependent tasks.</p>
     * @param {Mixed} task Either a task record or the id of a task
     * @return {Record} The event record
     */
    highlightTask : function(taskId, highlightDependentTasks) {
        if (taskId instanceof Ext.data.Record) {
            taskId = taskId.id;
        }
        
        var el = this.getElementFromEventId(taskId);
        
        // El might not exist in DOM
        if (el) {
            this.getSelectionModel().select(el, true);
        }
        
        if (highlightDependentTasks !== false) {
            this.dependencyStore.queryBy(function(dep) {
                if (dep.get('From') == taskId) {
                    this.highlightDependency(dep.id);
                    this.highlightTask(dep.get('To'), highlightDependentTasks);
                }
            }, this);
        }
    },
    
    clearSelectedTasksAndDependencies : function() {
        this.getSelectionModel().clearSelections();
        this.getView().el.select('.sch-dependency-selected').removeClass('sch-dependency-selected');
    },
    
    /* Maxim TreeGrid */
    /**
     * @cfg {String|Integer} master_column_id Master column id. Master column cells are nested.
     * Master column cell values are used to build breadcrumbs.
     */
    master_column_id : 0,

    /**
     * @access private
     */
    onClick : function(e)
    {
        var target = e.getTarget('.ux-maximgb-tg-elbow-active', 1) || 
                     (this.toggleParentTasksOnClick && e.getTarget('.sch-gantt-parenttask-bar'));
        
        // Row click
        if (target) {
            var view = this.getView(),
                row = view.findRowIndex(target),
                store = this.getStore(),
                record = store.getAt(row);
                
            if (store.isExpandedNode(record)) {
                store.collapseNode(record);
            }
            else {
                store.expandNode(record);
            }
        } 
        if (!target || target.className.match('sch-gantt-parenttask-bar')) {
            Sch.TreeGanttPanel.superclass.onClick.call(this, e);
        }
    },

    /**
     * @access private
     */
    onMouseDown : function(e)
    {
        if (!e.getTarget('.ux-maximgb-tg-elbow-active', 1)) {
            Sch.TreeGanttPanel.superclass.onMouseDown.call(this, e);
        }
    },
    
    onStoreAddRemove : function(store, rec) {
        this.store.suspendEvents();
        var updatedTasks = this.recalculateParents(rec);
        this.store.resumeEvents();
        
        var l = updatedTasks.length;
        
        var view = this.getView();
        for (var i = 0; i < l; i++) {
            view.refreshRow(updatedTasks[i]);
        }
    },
    
    onStoreUpdate : function(store, rec, operation, prev) {
        // No need to recalculate for commits
        if (operation !== Ext.data.Record.COMMIT && (!prev || prev.StartDate || prev.EndDate)) {
            this.onStoreAddRemove(store, rec);
        }
    },
    
    // Returns true if any parent was changed (triggering a view refresh)
    recalculateParents : function(rec) {
        rec = Ext.isArray(rec) ? rec[0] : rec;
        var earliest = new Date(9999,0,0), 
            latest = new Date(0),
            parent = this.store.getNodeParent(rec),
            updatedTasks = [];
        
        if (parent) {
            var children = this.store.getNodeChildren(parent);
            if (children.length > 0) {
                Ext.each(children, function(r) {
                    earliest = Date.min(earliest, r.get('StartDate'));
                    latest = Date.max(latest, r.get('EndDate'));
                });
                
                if (parent.get('StartDate') - earliest !== 0) {
                    parent.set('StartDate', earliest);
                    updatedTasks.push(parent);
                }
                if (parent.get('EndDate') - latest !== 0) {
                    parent.set('EndDate', latest);
                    updatedTasks.push(parent);
                }
            }
            
            if (this.cascadeChanges) {
                this.getDependencyManager().performCascade(parent);
            }
            
            updatedTasks = updatedTasks.concat(this.recalculateParents(parent));
        }
        return updatedTasks;
    },
    
    /**
     * Returns the critical path(s) that can affect the end date of the project
     * @return {Array} paths An array of arrays (containing task chains)
     */
    getCriticalPaths : function() {
        return this.getDependencyManager().getCriticalPaths();
    },
    
    /**
     * Highlights the critical path(s) that can affect the end date of the project
     */
    highlightCriticalPaths : function(useFade) {
        // First clear any selected tasks/dependencies
        this.clearSelectedTasksAndDependencies();
        
        var paths = this.getCriticalPaths(),
            dm = this.getDependencyManager(),
            ds = this.dependencyStore,
            opacity = 0.2,
            t,i,l, depRecord;
        
        Ext.each(paths, function(tasks) {
            for (i = 0, l = tasks.length; i < l; i++) {
                t = tasks[i];
                this.highlightTask(t, false);
                
                if (i < (l - 1)) {
                    depRecord = ds.getAt(ds.findBy(function(dep) { return dep.get('To') === t.id && dep.get('From') === tasks[i+1].id; }));
                    dm.highlightDependency(depRecord);
                }
            }
        }, this);
        
        if (paths.length > 0 && useFade) {
            this.getView().mainBody.select(this.eventSelector + ':not(.'+ this.getSelectionModel().selectedClass + ')').setOpacity(opacity);
            this.getView().mainBody.select('.sch-dependency' + ':not(.sch-dependency-selected)').setOpacity(opacity);
        }
    },
    
    unhighlightCriticalPaths : function(useFade) {
        this.clearSelectedTasksAndDependencies();
        
        // Restore any faded out task 
        if (useFade) {
            this.getView().mainBody.select(this.eventSelector + ':not(.'+ this.getSelectionModel().selectedClass + ')').setOpacity(1);
            this.getView().mainBody.select('.sch-dependency' + ':not(.sch-dependency-selected)').setOpacity(1);
        }
    }
}); 

Ext.reg('treegantt', Sch.TreeGanttPanel);