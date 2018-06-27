/*
 * Ext Gantt 1.1
 * Copyright(c) 2009-2010 Mats Bryntse Consulting
 * mats@ext-scheduler.com
 * http://www.ext-scheduler.com/license.html
 *
 */

Ext.ns('Sch');

/**
 * @class Sch.DependencyDragDrop
 * @extends Ext.util.Observable
 * Internal class managing the interaction with the dependency terminals.
 * @constructor
 * @param {Object} config The object containing the configuration of this model.
 */
Sch.DependencyDragDrop = function(g, config) {
    this.addEvents(
        /**
         * @event beforednd
         * Fires before a drag and drop operation is initiated, return false to cancel it
         * @param {Plugin} plugin the dragdrop plugin
         * @param {HTMLNode} node The node that's about to be dragged
         * @param {EventObject} e The event object
         */ 
        'beforednd', 
        
        /**
         * @event dndstart
         * Fires when a drag and drop operation starts
         * @param {Plugin} plugin the dragdrop plugin
         */
        'dndstart',
        
        /**
         * @event afterdnd
         * Fires after a drag and drop operation, even when drop was performed on an invalid location
         * @param {SchedulerGrid} grid The grid object
         * @param {Mixed} fromId The source dependency record id
         * @param {Mixed} toId The target dependency record id
         * @param {Int} type The dependency type, see sch.dependencymanager.js for more information
         */
        'afterdnd'
    );
    
    this.grid = g;
    this.setupDragZone();
    this.setupDropZone();
    this.ddGroup = g.id + '-sch-dependency-dd';
    g.on('beforedestroy', this.cleanUp, this);
    
    Sch.DependencyDragDrop.superclass.constructor.call(this);
};

Ext.extend(Sch.DependencyDragDrop, Ext.util.Observable, {
    
    /**
     * @cfg {String} fromText The text to show before the from task when setting up a dependency. Defaults to 'From:'.
     */
    fromText : 'From: <strong>{0}</strong> {1}<br/>',
    
    /**
     * @cfg {String} toText The text to show before the to task when setting up a dependency. Defaults to 'From:'.
     */
    toText : 'To: <strong>{0}</strong> {1}',
    
    /**
     * @cfg {String} startText The text indicating that a dependency connector is a Start type.
     */
    startText : 'Start',
    
    /**
     * @cfg {String} endText The text indicating whether a dependency connector is an End type.
     */
    endText : 'End',
    
    // private, the terminal CSS selector
    terminalSelector : '.sch-gantt-terminal',
    
    // private, number of levels to search the dom
    getItemDepth : 6,
    
    isValidDrop : function(fromTaskId, toTaskId, checkCyclic) {
        var valid = true,
            dependencyStore = this.grid.dependencyStore;
        
        // Make sure a) the drop is not made on the same task, and b) no previous dependency exists between the two tasks
        var find = function(r) {
            return fromTaskId === toTaskId ||
                   r.get('From') === fromTaskId && r.get('To') === toTaskId ||
                   r.get('To') === fromTaskId && r.get('From') === toTaskId;
        };
        
        if (dependencyStore.findBy(find) >= 0) {
            valid = false;
        }
        
        if (valid && checkCyclic) {
            valid = this.findToTask(dependencyStore, toTaskId, fromTaskId) < 0;
        }
        
        return valid;
    },
    
    findToTask : function(store, sourceId, targetId) {
        return store.findBy(function(r) {
            if (r.get('From') === sourceId) {
                if (r.get('To') === targetId) {
                    return true;
                } else {
                    return this.findToTask(store, r.get('To'), targetId) >= 0; 
                }
            }
        }, this);
    },
    
    cleanUp : function() {
        this.dragZone.destroy();
        this.dropZone.destroy();
    },
    
    // private
    setupDragZone : function() {
        var me = this,
            g = this.grid,
            v = g.getView(),
            terminalSelector = this.terminalSelector,
            dependencyStore = g.dependencyStore;
        
        // The drag zone behaviour
        this.dragZone = new Ext.dd.DragZone(v.scroller, {
            ddGroup : this.ddGroup,
            
            onStartDrag : function () {
                if (g.tip) {
                    g.tip.disable();
                }
                v.mainBody.addClass('sch-gantt-terminal-showall');
                me.fireEvent('dndstart', me);
            },
            
            // On receipt of a mousedown event, see if it is within a draggable element.
            // Return a drag data object if so. The data object can contain arbitrary application
            // data, but it should also contain a DOM element in the ddel property to provide
            // a proxy to drag.
            getDragData: function(e) {
                var sourceNode = e.getTarget(terminalSelector, me.getItemDepth);
                if (sourceNode) {
                    if (me.fireEvent('beforednd', this, sourceNode, e) === false) {
                        return null;
                    }
                    
                    var isStart = !!sourceNode.className.match('sch-gantt-terminal-start'),
                        sourceEventRecord = g.getEventRecordFromElement(sourceNode);
                    
                    var ddel = Ext.DomHelper.createDom({
                        tag: 'div',
                        cls: 'sch-dd-dependency',
                        children: [
                            {
                                tag: 'span', 
                                cls: 'sch-dd-dependency-from', 
                                html: String.format(me.fromText, sourceEventRecord.get('Name'), isStart ? me.startText : me.endText)
                            },
                            {
                                tag: 'span', 
                                cls: 'sch-dd-dependency-to', 
                                html: String.format(me.toText, '', '')
                            }
                        ]
                    });
                    
                    
                    return {
                        fromId : sourceEventRecord.id,
                        isStart : isStart,
                        repairXY: Ext.fly(sourceNode).getXY(),
                        ddel: ddel
                    };
                }
                return false;
            },
            
            // Override, get rid of weird highlight fx in default implementation
            afterRepair : function(){
                v.mainBody.removeClass('sch-gantt-terminal-showall');
                // Enable tooltip after drag again
                if (g.tip) {
                    g.tip.enable();
                }
                this.dragging = false;
                me.fireEvent('afterdnd', this);
            },

            // Provide coordinates for the proxy to slide back to on failed drag.
            // This is the original XY coordinates of the draggable element.
            getRepairXY: function() {
                return this.dragData.repairXY;
            }
        });
    },
    
    // private
    setupDropZone : function () {
        var me = this,
            g = this.grid,
            v = g.getView(),
            store = g.store,
            terminalSelector = this.terminalSelector,
            dependencyStore = g.dependencyStore;
            
        // The drop zone behaviour
        this.dropZone = new Ext.dd.DropZone(v.mainBody, {
            ddGroup : this.ddGroup,
            
            getTargetFromEvent: function(e) {
                return e.getTarget(me.terminalSelector, me.getItemDepth);
            },
            
            // On entry into a target node, highlight that node.
            onNodeEnter : function(target, dd, e, data){
                var isTargetStart = target.className.match('sch-gantt-terminal-start');
                Ext.fly(target).addClass(isTargetStart ? 'sch-gantt-terminal-start-drophover' : 'sch-gantt-terminal-end-drophover');
            },

            // On exit from a target node, unhighlight that node.
            onNodeOut : function(target, dd, e, data){
                var isTargetStart = target.className.match('sch-gantt-terminal-start');
                Ext.fly(target).removeClass(isTargetStart ? 'sch-gantt-terminal-start-drophover' : 'sch-gantt-terminal-end-drophover');
            },
            
            onNodeOver : function(target, dd, e, data){
                var targetRecord = store.getAt(v.findRowIndex(target)),
                    targetId = targetRecord.id,
                    isTargetStart = target.className.match('sch-gantt-terminal-start'),
                    newText = String.format(me.toText, targetRecord.get('Name'), isTargetStart ? me.startText : me.endText);
                
                dd.proxy.el.child('.sch-dd-dependency-to').update(newText);
                
                if (me.isValidDrop.call(me, data.fromId, targetId)) {
                    return this.dropAllowed;
                } else {
                    return this.dropNotAllowed;
                }
            },
            
            onNodeDrop : function(target, dd, e, data){
                var type, retVal = true;
                v.mainBody.removeClass('sch-gantt-terminal-showall');
                
                // Enable tooltip after drag
                if (g.tip) {
                    g.tip.enable();
                }
                
                if (data.isStart) {
                    if (target.className.match('sch-gantt-terminal-start')) {
                        type = Sch.Dependency.StartToStart;
                    } else {
                        type = Sch.Dependency.StartToEnd;
                    }
                } else {
                    if (target.className.match('sch-gantt-terminal-start')) {
                        type = Sch.Dependency.EndToStart;
                    } else {
                        type = Sch.Dependency.EndToEnd;
                    }
                }
                
                var rec = g.getEventRecordFromElement(target),
                          targetId = rec.id;
                
                retVal = me.isValidDrop.call(me, data.fromId, targetId, true);
                
                if (retVal) {
                    me.fireEvent('afterdnd', this, data.fromId, rec.id, type);
                }
                return retVal;
            }
        });
    }
});