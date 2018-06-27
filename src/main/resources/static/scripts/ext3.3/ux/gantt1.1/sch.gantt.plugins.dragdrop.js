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
 * @class Sch.gantt.plugins.DragDrop
 * @extends Ext.util.Observable
 * Plugin enabling drag and drop for event nodes
 * @constructor
 * @param {Object} config The object containing the configuration of this model.
 */
Sch.gantt.plugins.DragDrop = function(config) {
    Ext.apply(this, config);
    Sch.gantt.plugins.DragDrop.superclass.constructor.call(this);
};

Ext.extend(Sch.gantt.plugins.DragDrop, Ext.util.Observable, {
    /**
      * @cfg useTooltip {Boolean} false to not show a tooltip while dragging
      */
    useTooltip : true,
    
    /**
     * An empty function by default, but provided so that you can perform custom validation on 
     * the item being dragged. This function is called during the drag and drop process and also after the drop is made
     * @param {Ext.data.Record} record The record being dragged
     * @param {Date} date The date corresponding to the current start date
     * @param {Int} duration The duration of the item being dragged, in minutes
     * @param {Ext.EventObject} e The event object
     * @return {Boolean} true if the drop position is valid, else false to prevent a drop
     */
    validatorFn : function(record, date, duration, e) {
        return true;
    },
    
    /**
     * @cfg {Object} validatorFnScope
     * The scope for the validatorFn
     */
    validatorFnScope : null,
    

    // private
    init : function(g) {
        this.grid = g;
        this.ddGroup = g.id;
        g.onRender = g.onRender.createSequence(this.onRender, this);
        g.on('beforedestroy', this.cleanUp, this);
    },
        
    cleanUp : function() {
        this.grid.dragZone.destroy();
    },
    
    // private
    onRender : function() {
        this.setupDragZone();
    },
    
    getItemDepth : 5,
    
    setupDragZone : function() {
        var me = this,
            g = this.grid,
            v = g.getView(),
            sm = g.getSelectionModel();
            
        if (this.useTooltip) {
            this.tip = new Sch.gantt.plugins.Tooltip({}, g);
        }
        
        // The drag zone behaviour
        g.dragZone = new Sch.gantt.DragZone(v.scroller, {
            ddGroup : this.ddGroup,
            containerScroll : true,
            
            onDragOver: function(e, id){
                
                var x = this.proxy.el.getX() + (g.isMilestone(this.dragData.record) ? 8 : 0), // Adjust x position for milestones
                    start = g.getTimeFromX2(x),
                    data = this.dragData;
                
                // HACK, hiding element multiple times, but the overhead should be very little
                Ext.fly(data.sourceNode).hide();
                
                if (!start) return;
                
                var roundedStart = g.floorDate(start);
                data.start = roundedStart;
                
                if (me.useTooltip) {
                    var end = roundedStart.add(Date.MINUTE, this.dragData.duration),
                        valid = me.validatorFn.call(me.validatorFnScope || me, 
                                                    this.dragData.record, 
                                                    roundedStart, 
                                                    this.dragData.duration, 
                                                    e) !== false;
                    me.tip.update(start, end, valid);
                }
            },
            
            onStartDrag : function () {
                var nodeEl = Ext.get(this.dragData.sourceNode);
                if (me.useTooltip) {
                    var r = this.dragData.record,
                        start = r.get('StartDate'),
                        end = r.get('EndDate');
                    me.tip.show(nodeEl);
                    me.tip.update(start, end, true);
                }
                
                this.constrainTo(nodeEl.up('tr'));
                
                g.fireEvent('dndstart', g, this.dragData.record);
            },
            
            // On receipt of a mousedown event, see if it is within a draggable element.
            // Return a drag data object if so. The data object can contain arbitrary application
            // data, but it should also contain a DOM element in the ddel property to provide
            // a proxy to drag.
            getDragData: function(e) {
                var sourceNode = e.getTarget(g.eventSelector, me.getItemDepth);
                
                if (sourceNode) {
                    var sourceNodeEl = Ext.get(sourceNode);
                        eventEl = sourceNodeEl.is(g.eventSelector) ? sourceNode : sourceNodeEl.up(g.eventSelector).dom;
                        
                    if(!sm.isSelected(eventEl)){
                        sm.select(eventEl, sm.multiSelect, true);
                    }
                    
                    var sourceEventRecord = g.getEventRecordFromDomId(eventEl.id);
                    
                    if (e.getTarget().className.match('x-resizable-handle') || g.fireEvent('beforednd', g, sourceEventRecord, e) === false) {
                        return null;
                    }
                    
                    var copy = sourceNode.cloneNode(true);
                    copy.id = Ext.id();
                    
                    return {
                        sourceNode : sourceNode,
                        repairXY: Ext.fly(sourceNode).getXY(),
                        ddel: copy,
                        record : sourceEventRecord,
                        duration : Date.getDurationInMinutes(sourceEventRecord.get('StartDate'), sourceEventRecord.get('EndDate'))
                    };
                }
                return null;
            },
            
            // Override, get rid of weird highlight fx in default implementation
            afterRepair : function(){
                Ext.fly(this.dragData.sourceNode).show();
                this.dragging = false;
            },

            // Provide coordinates for the proxy to slide back to on failed drag.
            // This is the original XY coordinates of the draggable element.
            getRepairXY: function() {
                g.fireEvent('afterdnd', g);
                return this.dragData.repairXY;
            },
            
            onDragDrop: function(e, id){
                var target = this.cachedTarget || Ext.dd.DragDropMgr.getDDById(id),
                    data = this.dragData,
                    start = data.start;
                
                if (start) {
                    var end = start.add(Date.MINUTE, data.duration),
                        valid = false;
                
                    
                    if (start && me.validatorFn.call(me.validatorFnScope || me, data.record, start, data.duration) !== false) {
                        data.record.beginEdit();
                        data.record.set('StartDate', start);
                        data.record.set('EndDate', end);
                        data.record.endEdit();
                        valid = true;
                        
                        // Clear selections after succesful drag drop
                        g.getSelectionModel().clearSelections();
                        g.fireEvent('drop', g, data.record);
                    }
                }
                
                if (me.useTooltip) {
                    me.tip.hide();
                }
                    
                g.fireEvent('afterdnd', g);
                if(valid){ // valid drop?
                    this.onValidDrop(target, e, id);
                }else{
                    this.onInvalidDrop(target, e, id);
                }
            }
        });
    }
});


/**
 * @ignore
 * @class Sch.DragProxy
 * @extends Ext.dd.StatusProxy
 * A status proxy showing only a copy of the item being dragged. No status indicator etc.
 * @constructor
 * @param {Object} config The object containing the configuration of this model.
 */
Sch.DragProxy = Ext.extend(Ext.dd.StatusProxy, {
    constructor : function(config){
        Ext.apply(this, config);
        this.id = this.id || Ext.id();
        this.el = new Ext.Layer({
            dh: {
                id: this.id, tag: "div", cls: "sch-dragproxy x-dd-drag-proxy", children: [
                    {tag: "div", cls: "x-dd-drag-ghost"}
                ]
            }, 
            shadow: false
        });
        this.ghost = Ext.get(this.el.dom.childNodes[0]);
        this.dropStatus = this.dropNotAllowed;
    },
    
    // Overridden to prevent the proxy from resetting itself and resetting the class property
    reset : function(clearGhost){
        this.el.dom.className = "sch-dragproxy x-dd-drag-proxy " + this.dropNotAllowed;
        this.dropStatus = this.dropNotAllowed;
        if(clearGhost){
            this.ghost.update("");
        }
    }
});



/**
 * @ignore
 * @class Sch.gantt.DragZone
 * @extends Ext.dd.DragZone
 * Custom dragzone that also acts as the dropzone, and constrains the drag to the table row that contains the dragged element
 * @constructor
 * @param {Object} config The object containing the configuration of this model.
 */
Sch.gantt.DragZone = Ext.extend(Ext.dd.DragZone, {
    
    constructor : function(el, config){
        config.proxy = new Sch.DragProxy({
            shadow : false,
            dropAllowed : Ext.dd.StatusProxy.prototype.dropAllowed + " sch-dragproxy",
            dropNotAllowed : Ext.dd.StatusProxy.prototype.dropNotAllowed + " sch-dragproxy"
        });
        Sch.gantt.DragZone.superclass.constructor.apply(this, arguments);
        this.scroll = false;
        this.isTarget = true;
        this.ignoreSelf = false;
    },
    
    /**
    * Finds the offset of the click event relative to the card and sets this as the offset delta
    * for the ghost element so that the click point of the card is kept with the mouse pointer
    */
    autoOffset: function(x, y) {
        var xy = this.dragData.repairXY; // Original position of the element

        var xDelta = x - xy[0];
        var yDelta = y - xy[1];

        this.setDelta(xDelta, yDelta);
    },
    
    constrainTo : function(constrainTo){
        var b = Ext.get(this.dragData.sourceNode).getBox(),
            ce = Ext.get(constrainTo),
            cd = ce.dom,
            xy = ce.getXY(),
            c = {x : xy[0], y: xy[1], width: cd.clientWidth, height: cd.clientHeight};

        this.resetConstraints();
        this.initPageY = c.y;
        this.setYConstraint(0, 0, this.yTickSize);
    }
});

