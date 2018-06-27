/*
 * Ext Gantt v1.0 
 * Copyright(c) 2009-2010 Mats Bryntse Consulting
 * 
 * info@ext-scheduler.com
 * 
 * http://www.ext-scheduler.com/license.html
 * 
 * 
 * 
 */


 
Ext.ns('Sch');


Sch.DependencyPainter = Ext.extend(Ext.util.Observable, {
    
    // private, true to check for visibility (needed when using grouping view, since we should only draw lines between two visible elements)
    checkVisible : false,
    
    constructor : function (g, cfg) {    
        cfg = cfg || {};
        
        this.grid = g;
        
        Ext.apply(this, cfg, {
            xOffset : 8,
            midRowOffset : 4,
            yOffset : 7,
            horizontalArrowWidthOffset : 8,
            horizontalArrowHeightOffset : 4,
            verticalArrowWidthOffset : 4,
            verticalArrowHeightOffset : 8,
            lineWidth : 2
        });
    },
    
    drawSingleDependency : function(depRecord) {
        
        var toEl = this.grid.getElementFromEventId(depRecord.get('To')),
            fromEl = this.grid.getElementFromEventId(depRecord.get('From'));
        
        if (!toEl || !fromEl || (this.checkVisible && (!fromEl.isVisible(true) || !toEl.isVisible(true)))) {
            return;
        }
        
        var type = depRecord.get('Type');
    
        switch(type) {
            case Sch.Dependency.StartToEnd:
                this.drawStartToEnd(depRecord.id, fromEl, toEl);
            break;
            
            case Sch.Dependency.StartToStart:
                this.drawStartToStart(depRecord.id, fromEl, toEl);
            break;
            
            case Sch.Dependency.EndToStart:
                this.drawEndToStart(depRecord.id, fromEl, toEl);
            break;
            
            case Sch.Dependency.EndToEnd:
                this.drawEndToEnd(depRecord.id, fromEl, toEl);
            break;
            
            default:
                throw 'Invalid case statement';
            break;
        }
    },
    
    drawEndToStart : function(dependencyId, fromEl, toEl) {
        
        var fromTop = fromEl.getTop(),
            toTop = toEl.getTop(),
            container = fromTop < toTop ? fromEl : toEl,
            containerX = container.getX(),
            containerY = container.getY(),
            x1 = fromEl.getRight() - containerX, 
            y1 = fromTop - containerY + this.yOffset, 
            x2 = toEl.getLeft() - containerX, 
            y2Top = toTop - containerY,
            y2 = y2Top + this.yOffset,
            y2offset = fromTop < toTop ? (y2 - this.yOffset - this.midRowOffset) : (y2 + this.yOffset + this.midRowOffset);
         
         if (x2 >= (x1 - 5) && (toEl.getRight() - containerX > (x1 + 10)) && y2 > y1)
         { 
            var leftOffset = x2 + this.xOffset;
            // To after from
            // ---
            //   |
            this.drawLines(container, dependencyId, [
                {
                    x1 : x1, 
                    y1 : y1, 
                    x2 : leftOffset,
                    y2 : y1
                },
                {
                    x1 : leftOffset, 
                    y1 : y1, 
                    x2 : leftOffset, 
                    y2 : y2Top - this.verticalArrowHeightOffset
                },
                {
                    x1 : leftOffset - this.verticalArrowWidthOffset, 
                    y1 : y2Top - this.verticalArrowHeightOffset,
                    direction : 'down'
                }
            ]);
         }
         else {
            // From after to
            //        -
            //         |
            //     ----
            //    |-> 
            //     
            var leftOffset = x1 + this.xOffset + this.horizontalArrowWidthOffset;
            
            this.drawLines(container, dependencyId, [
                {
                    x1 : x1, 
                    y1 : y1, 
                    x2 : leftOffset,
                    y2 : y1
                },
                {
                   x1 : leftOffset, 
                   y1 : y1, 
                   x2 : leftOffset, 
                   y2 : y2offset
                },
                {
                    x1 : leftOffset, 
                    y1 : y2offset, 
                    x2 : x2 - this.xOffset - this.horizontalArrowWidthOffset, 
                    y2 : y2offset
                },
                {
                    x1 : x2 - this.xOffset - this.horizontalArrowWidthOffset, 
                    y1 : y2offset, 
                    x2 : x2 - this.xOffset - this.horizontalArrowWidthOffset, 
                    y2 : y2
                },
                {
                    x1 : x2 - this.xOffset - this.horizontalArrowWidthOffset, 
                    y1 : y2, 
                    x2 : x2 - this.horizontalArrowWidthOffset, 
                    y2 : y2
                },
                {
                    x1 : x2 - this.horizontalArrowWidthOffset, 
                    y1 : y2 - this.horizontalArrowHeightOffset,
                    direction : 'right'
                }
            ]);
        }
    },
    
    drawStartToEnd : function(dependencyId, fromEl, toEl) {
        
        var fromTop = fromEl.getTop(),
            toTop = toEl.getTop(),
            container = fromTop < toTop ? fromEl : toEl,
            containerX = container.getX(),
            containerY = container.getY(),
            x1 = fromEl.getLeft() - containerX - 1, 
            y1 = fromTop - containerY + this.yOffset, 
            x2 = toEl.getRight() - containerX, 
            y2Top = toTop - containerY,
            y2 = y2Top + this.yOffset,
            y2offset = fromTop < toTop ? (y2 - this.yOffset - this.midRowOffset) : (y2 + this.yOffset + this.midRowOffset);
         
         if (x2 > (x1 + this.xOffset - this.horizontalArrowWidthOffset) ||
             Math.abs(x2 - x1) < (2*(this.xOffset + this.horizontalArrowWidthOffset)))
         { 
            var leftOffset = x1 - this.xOffset - this.horizontalArrowWidthOffset;
            // To after from
            // --|
            // |-----------
            //             |
            //          <--|
            this.drawLines(container, dependencyId, [
                {
                    x1 : x1, 
                    y1 : y1, 
                    x2 : leftOffset,
                    y2 : y1
                },
                {
                   x1 : leftOffset, 
                   y1 : y1, 
                   x2 : leftOffset, 
                   y2 : y2offset
                },
                {
                    x1 : leftOffset, 
                    y1 : y2offset, 
                    x2 : x2 + this.xOffset + this.horizontalArrowWidthOffset, 
                    y2 : y2offset
                },
                {
                    x1 : x2 + this.xOffset + this.horizontalArrowWidthOffset, 
                    y1 : y2offset, 
                    x2 : x2 + this.xOffset + this.horizontalArrowWidthOffset, 
                    y2 : y2
                },
                {
                    x1 : x2 + this.xOffset + this.horizontalArrowWidthOffset, 
                    y1 : y2, 
                    x2 : x2 + this.horizontalArrowWidthOffset, 
                    y2 : y2
                },
                {
                    x1 : x2, 
                    y1 : y2 - this.horizontalArrowHeightOffset,
                    direction : 'left'
                }
            ]);
         }
         else {
            // From after to
            //    -----|
            // <--|
            //     
            var leftOffset = x1 - this.xOffset - this.horizontalArrowWidthOffset;
            
            this.drawLines(container, dependencyId, [
                {
                    x1 : x1, 
                    y1 : y1, 
                    x2 : leftOffset,
                    y2 : y1
                },
                {
                   x1 : leftOffset, 
                   y1 : y1, 
                   x2 : leftOffset, 
                   y2 : y2
                },
                {
                    x1 : leftOffset, 
                    y1 : y2, 
                    x2 : x2 + this.horizontalArrowWidthOffset, 
                    y2 : y2
                },
                {
                    x1 : x2, 
                    y1 : y2 - this.horizontalArrowHeightOffset,
                    direction : 'left'
                }
            ]);
        }
    },
    
    drawEndToEnd : function(dependencyId, fromEl, toEl) {
        
        var fromTop = fromEl.getTop(),
            toTop = toEl.getTop(),
            container = fromTop < toTop ? fromEl : toEl,
            containerX = container.getX(),
            containerY = container.getY(),
            x1 = fromEl.getRight() - containerX, 
            y1 = fromTop - containerY + this.yOffset, 
            x2 = toEl.getRight() - containerX, 
            y2Top = toTop - containerY,
            y2 = y2Top + this.yOffset,
            rightPointOffset = x2 + this.xOffset + this.horizontalArrowWidthOffset;
        
        if (x1 > (x2 + this.xOffset)) {
            rightPointOffset += x1 - x2;
        }
         
        this.drawLines(container, dependencyId, [
            {
                x1 : x1, 
                y1 : y1, 
                x2 : rightPointOffset,
                y2 : y1
            },
            {
               x1 : rightPointOffset, 
               y1 : y1, 
               x2 : rightPointOffset, 
               y2 : y2
            },
            {
                x1 : rightPointOffset, 
                y1 : y2, 
                x2 : x2, 
                y2 : y2
            },
            {
                x1 : x2, 
                y1 : y2 - this.horizontalArrowHeightOffset,
                direction : 'left'
            }
        ]);
    },
   
    drawStartToStart : function(dependencyId, fromEl, toEl) {
        
        var fromTop = fromEl.getTop(),
            toTop = toEl.getTop(),
            container = fromTop < toTop ? fromEl : toEl,
            containerX = container.getX(),
            containerY = container.getY(),
            x1 = fromEl.getLeft() - containerX - 2, 
            y1 = fromTop - containerY + this.yOffset, 
            x2 = toEl.getLeft() - containerX, 
            y2Top = toTop - containerY,
            y2 = y2Top + this.yOffset,
            leftPointOffset = this.xOffset + this.horizontalArrowWidthOffset;
        
        if (x1 > (x2 + this.xOffset)) {
            leftPointOffset += (x1 - x2);
        }
        
        this.drawLines(container, dependencyId, [
            {
                x1 : x1, 
                y1 : y1, 
                x2 : x1 - leftPointOffset,
                y2 : y1
            },
            {
               x1 : x1 - leftPointOffset, 
               y1 : y1, 
               x2 : x1 - leftPointOffset, 
               y2 : y2
            },
            {
                x1 : x1 - leftPointOffset, 
                y1 : y2, 
                x2 : x2 - this.horizontalArrowWidthOffset, 
                y2 : y2
            },
            {
                x1 : x2 - this.horizontalArrowWidthOffset, 
                y1 : y2 - this.horizontalArrowHeightOffset,
                direction : 'right'
            }
        ]);
    },
      
     
    drawLines : function(ct, depId, coordinatesArray) {
        var x1, y1, x2, y2, els = [];
        
        // Use the immediate parent as container
        ct = ct.up('', 1);
        
        for (var i = 0, l = coordinatesArray.length; i < l; i++) {
            if (i === l - 1) {
                // Last item is the arrow
                var arrow = Ext.DomHelper.createDom({
		            tag : 'div',
		            cls : String.format('sch-dependency sch-dep-{0} sch-dependency-arrow sch-dependency-arrow-{1}', depId, coordinatesArray[i].direction),
		            style : {
		                left : coordinatesArray[i].x1 + "px",
		                top : coordinatesArray[i].y1 + "px"
		            }
		         });
		         
		         els.push(arrow);
            } else {
                // Create arrow el
                x1 = coordinatesArray[i].x1;
                x2 = coordinatesArray[i].x2;
                y1 = coordinatesArray[i].y1;
                y2 = coordinatesArray[i].y2;
                
                var left = Math.min(x1, x2),
                    top  = Math.min(y1, y2),
                    width  = Math.abs(x2 - x1) + this.lineWidth,
                    height  = Math.abs(y2 - y1) + this.lineWidth,
                    el = Ext.DomHelper.createDom({
		                tag : 'div',
		                cls : String.format('sch-dependency sch-dep-{0} sch-dependency-line', depId), 
		                style : {
		                    left : left + "px",
		                    top : top + "px",
		                    width : width + "px",
		                    height : height + "px"
		                }
		             });
                 
		         // retrieve DIV
		         els.push(el);
		    }
		}
		ct.appendChild(els);
    }
});


 
Ext.ns('Sch');


Sch.Dependency = {
    StartToStart : 0,
    StartToEnd : 1,
    EndToStart : 2,
    EndToEnd : 3
};



Sch.DependencyManager = Ext.extend(Ext.util.Observable, {
    
    
    cascadeDelay : 10,
    
    
    highlightDependency : function(id) {
        this.getElementsForDependency(this.store.getById(id)).addClass('sch-dependency-selected');
    },
    
    
    unhighlightDependency : function(id) {
        this.getElementsForDependency(this.store.getById(id)).removeClass('sch-dependency-selected');
    },
    
    
    getElementsForDependency : function(rec) {
        var id = rec instanceof Ext.data.Record ? rec.id : rec;
        return this.containerEl.select('.sch-dep-' + id);
    },
    
    
    // private
    constructor : function (g, cfg) {
        cfg = cfg || {};
        Ext.apply(this, cfg);
        
        this.grid = g;
        g.getView().on('rowupdated', this.onRowUpdated, this);
        
        this.eventStore = g.store;
        this.eventStore.on({
            add : this.renderDependencies,
            update : this.onEventUpdated,
            remove : this.onEventDeleted,
            beforewrite : this.onEventStoreBeforeWrite,
            write : this.onEventStoreWrite,
            scope : this
        });
        
        this.store.on({
            datachanged : this.renderDependencies,
            add : this.onDependencyAdd,
            update : this.onDependencyUpdate,
            remove : this.onDependencyDelete,
            beforewrite : this.onBeforeWrite,
            write : this.onWrite, 
            scope : this
        });
        
        this.painter = new Sch.DependencyPainter(g, cfg);
        
        if (this.enableDependencyDragDrop !== false) {
            this.dnd = new Sch.DependencyDragDrop(g);
            this.dnd.on('afterdnd', this.onArrowDrop, this);
        }
    },
    
    // private
    depRe : new RegExp('sch-dep-([^\\s]+)'),
    
    // private
    getRecordForDependencyEl : function(t) {
        var m = t.className.match(this.depRe),
            rec = null;
        
        if(m && m[1]) {
            var recordId = m[1];
            
            rec = this.store.getById(recordId);
        }
        
        return rec;
    },
    
    
    renderDependencies : function() {
        this.containerEl.select('.sch-dependency').remove();
        
        if (this.eventStore.getCount() === 0) return;
        
        var dep, 
            l = this.store.getCount();
        
        for (var i = 0; i < l; i++) {
            dep = this.store.getAt(i);
            this.painter.drawSingleDependency(dep);
  	    }
    },
    
    renderEventDependencies : function(id) {
        var dep,
            l = this.store.getCount();
        
        for (var i = 0; i < l; i++) {
            dep = this.store.getAt(i);
            var toEventId = dep.get('To'),
                fromEventId = dep.get('From');
            
            if (id == toEventId || id == fromEventId) {
                this.painter.drawSingleDependency(dep);
            }
  	    }
    },
    
    onEventStoreBeforeWrite : function(s, action, records) {
        // Normalize
        if (!Ext.isArray(records)) {
            records = [records];
        }
        
        if (action === Ext.data.Api.actions.create) {
            Ext.each(records, function(r) {
                // HACK, save the phantom id to be able to replace the task phantom task id's in the dependency store
                r._phantomId = r.id;
            });
        }
    },
    
    onEventStoreWrite : function(s, action, result, t, records) {
        if (!Ext.isArray(records)) {
            records = [records];
        }
        var from, to;
        
        if (action === Ext.data.Api.actions.create) {
            Ext.each(records, function(r) {
                this.store.queryBy(function(dep) {
                    from = dep.get('From');
                    to = dep.get('To');
                    
                    if (from === r._phantomId) {
                        dep.set('From', r.id);
                    } else if (to === r._phantomId){
                        dep.set('To', r.id);
                    }
                });
            }, this);
        }
    },
    
    onDependencyUpdate : function(store, depRecord) {
       if (depRecord._phantomId) {
            // Delete lines drawn for any phantom record.
            this.getElementsForDependency(depRecord._phantomId).remove();
            delete depRecord._phantomId;
        }
        
       // Draw new dependencies for the event
       this.painter.drawSingleDependency(depRecord);
    },
    
    onDependencyAdd : function(store, depRecords) {
        // Draw new dependencies for the event
        this.painter.drawSingleDependency(depRecords[0]);
    },
    
    onDependencyDelete : function(store, depRecord) {
       this.getElementsForDependency(depRecord).fadeOut({ remove : true });
    },
    
    dimEventDependencies : function(eventId) {
        this.containerEl.select(this.depRe + eventId).setOpacity(0.2);
    },
    
    onBeforeWrite : function(s, action, records) {
        // Normalize
        if (!Ext.isArray(records)) {
            records = [records];
        }
        // Delete lines drawn for the phantom dependency record. After the create operation is finished the lines will be redrawn.
        if (action === Ext.data.Api.actions.create) {
            Ext.each(records, function(r) {
                // HACK, save the phantom id to be able to remove the dom elements associated with the phantom record
                r._phantomId = r.id;
            }, this);
        }
    },
    
    onWrite : function(s, action, result, t, records) {
        // Normalize
        if (!Ext.isArray(records)) {
            records = [records];
        }
    },
    
    onRowUpdated : function(v, index, record) {
        this.updateDependencies(record);
    },
    
    onEventUpdated : function(store, record, operation, hashPrevious) {
        this.updateDependencies(record);
        
        var id = record.id;
        
        if (this.grid.cascadeChanges && hashPrevious) {
            var dirty = false;
            (function(rec, changes, modified) {
                this.eventStore.suspendEvents();
                this.store.queryBy(function(dep) {
                    if (dep.get('From') == id) {
                        var dependentRec = store.getById(dep.get('To'));
                        
                        if (!dependentRec) return;
                        
                        var type = dep.get('Type'),
                            startDate = rec.get('StartDate'),
                            endDate = rec.get('EndDate'),
                            startMin = Date.getDurationInMinutes(hashPrevious.StartDate || startDate, startDate),
                            endMin = Date.getDurationInMinutes(hashPrevious.EndDate || endDate, endDate);
                        
                        if (type === Sch.Dependency.StartToStart || 
                            type === Sch.Dependency.StartToEnd)  {
                            this.performCascade(dependentRec, startMin, {});
                        } else {
                            this.performCascade(dependentRec, endMin, {});
                        }
                        dirty = true;
                    }
                }, this);
                this.eventStore.resumeEvents();
                if (dirty) {
                    this.grid.getView().refresh();
                }
            }).defer(this.cascadeDelay, this, [record, hashPrevious, {}]);
        }
    },
    
    updateDependencies : function (record) {
        var eventId = record.id;
        
        // First remove old dependency dom elements
        this.store.queryBy(function(r) {
            if (r.get('From') == eventId || r.get('To') == eventId) {
                this.containerEl.select('.sch-dep-' + r.id).remove();
            }
        }, this);
        
        // Draw new dependencies for the event
        this.renderEventDependencies(eventId);
    },
    
    onEventDeleted : function(store, record) {
        var eventId = record.id,
            toRemove = [];
        
        this.store.queryBy(function(r) {
            if (r.data.To == eventId || r.data.From == eventId) {
                toRemove.push(r);
            }
        });
        
        this.store.suspendEvents();
        this.store.remove(toRemove);
        this.store.resumeEvents();
        
        // Redraw all dependencies
        this.renderDependencies();
    },
    
    onArrowDrop : function(plugin, fromId, toId, type) {
        if (fromId === toId) return;
        
        var depRec = new this.store.recordType({
            From : fromId,
            To : toId,
            Type : type
        });
        
        this.store.add(depRec);
    },
    
    getDependenciesForTask : function(record) {
        var taskId = record.id;
        
        return this.store.queryBy(function(r) {
            return r.get('From') == taskId || r.get('To') == taskId;
        });
    },
    
    deleteDependency : function(id) {
        this.store.remove(this.store.getById(id));
    },
    
    performCascade : function(record, minutes, modified) {
        var oldStart = record.get('StartDate'),
            oldEnd = record.get('EndDate'),
            id = record.id;
        
        if (!(id in modified)) {
            modified[id] = null;
            record.set('StartDate', oldStart.add(Date.MINUTE, minutes));
            record.set('EndDate', oldEnd.add(Date.MINUTE, minutes));
            this.grid.recalculateParents(record.store, record);
        }
        
        this.store.queryBy(function(dep) {
            if (dep.get('From') == id) {
                this.performCascade(record.store.getById(dep.get('To')), minutes, modified);
            }
        }, this);
    }
});



Ext.ns('Sch');


Sch.DependencyDragDrop = function(g, config) {
    this.addEvents(
         
        'beforednd', 
        
        
        'dndstart',
        
        
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
                                html: String.format('From: <strong>{0}</strong> {1}<br/>', sourceEventRecord.get('Name'), isStart ? 'Start' : 'End')
                            },
                            {
                                tag: 'span', 
                                cls: 'sch-dd-dependency-to', 
                                html: 'To:'
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
                    targetId = targetRecord.id;
                
                if (!me.isValidDrop.call(me, data.fromId, targetId)) {
                    return this.dropNotAllowed;
                }
                    
                var isTargetStart = target.className.match('sch-gantt-terminal-start'),
                    newText = String.format('To: <strong>{0}</strong> {1}', targetRecord.get('Name'), 
                                                                            isTargetStart ? 'Start' : 'End');
                
                
                dd.proxy.el.child('.sch-dd-dependency-to').update(newText);
                return this.dropAllowed;
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


Ext.ns('Sch.gantt.plugins');
 

Sch.gantt.plugins.Resize = function(config) {
    Ext.apply(this, config);
    Sch.gantt.plugins.Resize.superclass.constructor.call(this);
};
 
Ext.extend(Sch.gantt.plugins.Resize, Sch.plugins.Resize, {
    
    
    showDuration : true,
    
    init:function(grid) {
        if (this.showDuration) {
            this.tipTemplate = new Ext.Template(
                '<div class="sch-timetipwrap {cls}">',
                    '<div>Start: {startText}</div>',
                    '<div>Duration: {duration} d</div>',
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


Ext.ns('Ext.ux.maximgb.tg');

Ext.ux.maximgb.tg.AbstractTreeStore = Ext.extend(Ext.data.Store,
{
    
    leaf_field_name : 'IsLeaf',
    
    
    page_offset : 0,
    
    
    active_node : null,
    
    defaultExpanded : false,
    
    
    constructor : function(config)
    {
        Ext.ux.maximgb.tg.AbstractTreeStore.superclass.constructor.call(this, config);
        
        if (!this.paramNames.active_node) {
            this.paramNames.active_node = 'anode';
        }
        
        this.addEvents(
            
            'beforeexpandnode',
            
            'expandnode',
            
            'expandnodefailed',
            
            'beforecollapsenode',
            
            'collapsenode',
            
            'beforeactivenodechange',
            
            'activenodechange'
        );
    },  

    // Store methods.
    // -----------------------------------------------------------------------------------------------  
    
    remove : function(record)
    {
        // ----- Modification start
        if (record === this.active_node) {
            this.setActiveNode(null);
        }
        this.removeNodeDescendants(record);
        // ----- End of modification        
        Ext.ux.maximgb.tg.AbstractTreeStore.superclass.remove.call(this, record);
    },
    
    
    removeNodeDescendants : function(rc)
    {
        var i, len, children = this.getNodeChildren(rc);
        for (i = 0, len = children.length; i < len; i++) {
            this.remove(children[i]);
        }
    },
    
    
    load : function(options)
    {
        if (options) {
            if (options.params) {
                if (options.params[this.paramNames.active_node] === undefined) {
                    options.params[this.paramNames.active_node] = this.active_node ? this.active_node.id : null;
                }
            }
            else {
                options.params = {};
                options.params[this.paramNames.active_node] = this.active_node ? this.active_node.id : null;
            }
        }
        else {
            options = {params: {}};
            options.params[this.paramNames.active_node] = this.active_node ? this.active_node.id : null;
        }

        if (options.params[this.paramNames.active_node] !== null) {
            options.add = true;
        }

        return Ext.ux.maximgb.tg.AbstractTreeStore.superclass.load.call(this, options); 
    },
    
    
    loadRecords : function(o, options, success)
    {
        if (!o || success === false) {
            if (success !== false) {
                this.fireEvent("load", this, [], options);
            }
            if (options.callback) {
                options.callback.call(options.scope || this, [], options, false);
            }
            return;
        }
    
        var r = o.records, t = o.totalRecords || r.length,  
            page_offset = this.getPageOffsetFromOptions(options),
            loaded_node_id = this.getLoadedNodeIdFromOptions(options), 
            loaded_node, i, len, record, idx, updated, self = this;
    
        if (!options || options.add !== true) {
            if (this.pruneModifiedRecords) {
                this.modified = [];
            }
            for (var i = 0, len = r.length; i < len; i++) {
                r[i].join(this);
            }
            if (this.snapshot) {
                this.data = this.snapshot;
                delete this.snapshot;
            }
            this.data.clear();
            this.data.addAll(r);
            this.page_offset = page_offset;
            this.totalLength = t;
            this.applySort();
            this.fireEvent("datachanged", this);
        } 
        else {
            if (loaded_node_id) {
                loaded_node = this.getById(loaded_node_id);
            }
            if (loaded_node) {
                this.setNodeChildrenOffset(loaded_node, page_offset);
                this.setNodeChildrenTotalCount(loaded_node, Math.max(t, r.length));
                this.removeNodeDescendants(loaded_node);
            }
            this.suspendEvents();
            updated = {};
            for (i = 0, len = r.length; i < len; i++) {
                record = r[i];
                idx = this.indexOfId(record.id);
                if (idx == -1) {
                    updated[record.id] = false;
                }
                else {
                    updated[record.id] = true;
                    this.setNodeExpanded(record, this.isExpandedNode(this.getAt(idx)));
                }
                this.add(record);
            }
            this.applySort();
            this.resumeEvents();
    
            r.sort(function(r1, r2) {
                var idx1 = self.data.indexOf(r1),
                    idx2 = self.data.indexOf(r2),
                    r;
         
                if (idx1 > idx2) {
                   r = 1;
                }
                else {
                   r = -1;
                }
                return r;
            });
    
            for (i = 0, len = r.length; i < len; i++) {
                record = r[i];
                if (updated[record.id] == true) {
                    this.fireEvent('update',  this, record, Ext.data.Record.COMMIT);
                }
                else {
                    this.fireEvent("add", this, [record], this.data.indexOf(record));
                }
            }
        }
        this.fireEvent("load", this, r, options);
        if (options.callback) {
            options.callback.call(options.scope || this, r, options, true);
        }
    },

   
    sort : function(fieldName, dir)
    {
        if (this.remoteSort) {
            this.setActiveNode(null);
            if (this.lastOptions) {
                this.lastOptions.add = false;
                if (this.lastOptions.params) {
                    this.lastOptions.params[this.paramNames.active_node] = null;
                }
            }
        }

        return Ext.ux.maximgb.tg.AbstractTreeStore.superclass.sort.call(this, fieldName, dir);         
    },    

    
    applySort : function()
    {
        if(this.sortInfo && !this.remoteSort){
            var s = this.sortInfo, f = s.field;
            this.sortData(f, s.direction);
        }
        // ----- Modification start
        else {
            this.applyTreeSort();
        }
        // ----- End of modification
    },
    
    
    sortData : function(f, direction) 
    {
        direction = direction || 'ASC';
        var st = this.fields.get(f).sortType;
        var fn = function(r1, r2){
            var v1 = st(r1.data[f]), v2 = st(r2.data[f]);
            return v1 > v2 ? 1 : (v1 < v2 ? -1 : 0);
        };
        this.data.sort(direction, fn);
        if(this.snapshot && this.snapshot != this.data){
            this.snapshot.sort(direction, fn);
        }
        // ----- Modification start
        this.applyTreeSort();
        // ----- End of modification
    },
    
    // Tree support methods.
    // -----------------------------------------------------------------------------------------------

    
    applyTreeSort : function()
    {
        var i, len, temp,
                rec, records = [],
                roots = this.getRootNodes();
                
        // Sorting data
        for (i = 0, len = roots.length; i < len; i++) {
            rec = roots[i];
            records.push(rec);
            this.collectNodeChildrenTreeSorted(records, rec); 
        }
        
        if (records.length > 0) {
            this.data.clear();
            this.data.addAll(records);
        }
        
        // Sorting the snapshot if one present.
        if (this.snapshot && this.snapshot !== this.data) {
            temp = this.data;
            this.data = this.snapshot;
            this.snapshot = null; 
            this.applyTreeSort();
            this.snapshot = this.data;
            this.data = temp;
        }
    },
    
    
    collectNodeChildrenTreeSorted : function(records, rec)
    {
        var i, len,
            child, 
            children = this.getNodeChildren(rec);
                
        for (i = 0, len = children.length; i < len; i++) {
            child = children[i];
            records.push(child);
            this.collectNodeChildrenTreeSorted(records, child); 
        }
    },
    
    
    getActiveNode : function()
    {
        return this.active_node;
    },
    
    
    setActiveNode : function(rc)
    {
        if (this.active_node !== rc) {
            if (rc) {
                if (this.data.indexOf(rc) != -1) {
                    if (this.fireEvent('beforeactivenodechange', this, this.active_node, rc) !== false) {
                        this.active_node = rc;
                        this.fireEvent('activenodechange', this, this.active_node, rc);
                    }
                }
                else {
                    throw "Given record is not from the store.";
                }
            }
            else {
                if (this.fireEvent('beforeactivenodechange', this, this.active_node, rc) !== false) {
                    this.active_node = rc;
                    this.fireEvent('activenodechange', this, this.active_node, rc);
                }
            }
        }
    },
     
    
    isExpandedNode : function(rc)
    {
        if (this.defaultExpanded) {
            return rc.ux_maximgb_tg_expanded !== false;
        } else {
            return rc.ux_maximgb_tg_expanded === true;
        }
    },
    
    
    setNodeExpanded : function(rc, value)
    {
        rc.ux_maximgb_tg_expanded = value;
    },
    
    
    isVisibleNode : function(rc)
    {
        var i, len,
                ancestors = this.getNodeAncestors(rc),
                result = true;
        
        for (i = 0, len = ancestors.length; i < len; i++) {
            result = result && this.isExpandedNode(ancestors[i]);
            if (!result) {
                break;
            }
        }
        
        return result;
    },
    
    
    isLeafNode : function(rc)
    {
        return rc.get(this.leaf_field_name) == true;
    },
    
    
    isLoadedNode : function(rc)
    {
        var result;
        
        if (rc.ux_maximgb_tg_loaded !== undefined) {
            result = rc.ux_maximgb_tg_loaded;
        }
        else if (this.isLeafNode(rc) || this.hasChildNodes(rc)) {
            result = true;
        }
        else {
            result = false;
        }
        
        return result;
    },
    
    
    setNodeLoaded : function(rc, value)
    {
        rc.ux_maximgb_tg_loaded = value;
    },
    
    
    getNodeChildrenOffset : function(rc)
    {
        return rc.ux_maximgb_tg_offset || 0;
    },
    
    
    setNodeChildrenOffset : function(rc, value)
    {
        rc.ux_maximgb_tg_offset = value;
    },
    
    
    getNodeChildrenTotalCount : function(rc)
    {
        return rc.ux_maximgb_tg_total || 0;
    },
    
    
    setNodeChildrenTotalCount : function(rc, value)
    {
        rc.ux_maximgb_tg_total = value;
    },
    
    
    collapseNode : function(rc)
    {
        if (
            this.isExpandedNode(rc) &&
            this.fireEvent('beforecollapsenode', this, rc) !== false 
        ) {
            this.setNodeExpanded(rc, false);
            this.fireEvent('collapsenode', this, rc);
        }
    },
    
    
    expandNode : function(rc)
    {
        var params;
        
        if (!this.isExpandedNode(rc) &&
             this.fireEvent('beforeexpandnode', this, rc) !== false
        ) {
            // If node is already loaded then expanding now.
            if (this.isLoadedNode(rc)) {
                this.setNodeExpanded(rc, true);
                this.fireEvent('expandnode', this, rc, false);
            }
            // If node isn't loaded yet then expanding after load.
            else {
                this.fireEvent('expandingnode', this, rc);
                params = {};
                params[this.paramNames.active_node] = rc.id;
                this.load({
                    add : true,
                    params : params,
                    callback : this.expandNodeCallback,
                    scope : this
                });
            }
        }
    },
    
    
    expandNodeCallback : function(r, options, success)
    {
        var rc = this.getById(options.params[this.paramNames.active_node]);
        
        if (success && rc) {
            this.setNodeLoaded(rc, true);
            this.setNodeExpanded(rc, true);
            this.fireEvent('expandnode', this, rc, true);
        }
        else {
            this.fireEvent('expandnodefailed', this, options.params[this.paramNames.active_node], rc);
        }
    },
    
    
    expandAll : function()
    {
        var r, i, len, records = this.data.getRange();
        this.suspendEvents();
        for (i = 0, len = records.length; i < len; i++) {
            r = records[i];
            if (!this.isExpandedNode(r)) {
                this.expandNode(r);
            }
        }
        this.resumeEvents();
        this.fireEvent('datachanged', this);
    },
    
    
    collapseAll : function()
    {
        var r, i, len, records = this.data.getRange();
        
        this.suspendEvents();
        for (i = 0, len = records.length; i < len; i++) {
            r = records[i];
            if (this.isExpandedNode(r)) {
                this.collapseNode(r);
            }
        }
        this.resumeEvents();
        this.fireEvent('datachanged', this);
    },
    
    
    getLoadedNodeIdFromOptions : function(options)
    {
        var result = null;
        if (options && options.params && options.params[this.paramNames.active_node]) {
            result = options.params[this.paramNames.active_node];
        }
        return result;
    },
    
    
    getPageOffsetFromOptions : function(options)
    {
        var result = 0;
        if (options && options.params && options.params[this.paramNames.start]) {
            result = parseInt(options.params[this.paramNames.start], 10);
            if (isNaN(result)) {
                result = 0;
            }
        }
        return result;
    },
    
    // Public
    hasNextSiblingNode : function(rc)
    {
        return this.getNodeNextSibling(rc) !== null;
    },
    
    // Public
    hasPrevSiblingNode : function(rc)
    {
        return this.getNodePrevSibling(rc) !== null;
    },
    
    // Public
    hasChildNodes : function(rc)
    {
        return this.getNodeChildrenCount(rc) > 0;
    },
    
    // Public
    getNodeAncestors : function(rc)
    {
        var ancestors = [],
            parent;
        
        parent = this.getNodeParent(rc);
        while (parent) {
            ancestors.push(parent);
            parent = this.getNodeParent(parent);    
        }
        
        return ancestors;
    },
    
    // Public
    getNodeChildrenCount : function(rc)
    {
        return this.getNodeChildren(rc).length;
    },
    
    // Public
    getNodeNextSibling : function(rc)
    {
        var siblings,
            parent,
            index,
            result = null;
                
        parent = this.getNodeParent(rc);
        if (parent) {
            siblings = this.getNodeChildren(parent);
        }
        else {
            siblings = this.getRootNodes();
        }
        
        index = siblings.indexOf(rc);
        
        if (index < siblings.length - 1) {
            result = siblings[index + 1];
        }
        
        return result;
    },
    
    // Public
    getNodePrevSibling : function(rc)
    {
        var siblings,
            parent,
            index,
            result = null;
                
        parent = this.getNodeParent(rc);
        if (parent) {
            siblings = this.getNodeChildren(parent);
        }
        else {
            siblings = this.getRootNodes();
        }
        
        index = siblings.indexOf(rc);
        if (index > 0) {
            result = siblings[index - 1];
        }
        
        return result;
    },
    
    // Abstract tree support methods.
    // -----------------------------------------------------------------------------------------------
    
    // Public - Abstract
    getRootNodes : function()
    {
        throw 'Abstract method call';
    },
    
    // Public - Abstract
    getNodeDepth : function(rc)
    {
        throw 'Abstract method call';
    },
    
    // Public - Abstract
    getNodeParent : function(rc)
    {
        throw 'Abstract method call';
    },
    
    // Public - Abstract
    getNodeChildren : function(rc)
    {
        throw 'Abstract method call';
    },
    
    // Public - Abstract
    addToNode : function(parent, child)
    {
        throw 'Abstract method call';
    },
    
    // Public - Abstract
    removeFromNode : function(parent, child)
    {
        throw 'Abstract method call';
    },
    
    // Paging support methods.
    // -----------------------------------------------------------------------------------------------
    
    getPageOffset : function()
    {
        return this.page_offset;
    },
    
    
    getActiveNodePageOffset : function()
    {
        var result;
        
        if (this.active_node) {
            result = this.getNodeChildrenOffset(this.active_node);
        }
        else {
            result = this.getPageOffset();
        }
        
        return result;
    },
    
    
    getActiveNodeCount : function()
    {
        var result;
        
        if (this.active_node) {
            result = this.getNodeChildrenCount(this.active_node);
        }
        else {
            result = this.getRootNodes().length;
        }
        
        return result;
    },
    
    
    getActiveNodeTotalCount : function()
    {
        var result;
        
        if (this.active_node) {
            result = this.getNodeChildrenTotalCount(this.active_node);
        }
        else {
            result = this.getTotalCount();
        }
        
        return result;  
    }
});


Ext.ns('Ext.ux.maximgb.tg');

Ext.ux.maximgb.tg.AdjacencyListStore = Ext.extend(Ext.ux.maximgb.tg.AbstractTreeStore,
{
    
    parent_id_field_name : 'ParentId',
    
    getRootNodes : function()
    {
        var i, 
            len, 
            result = [], 
            records = this.data.getRange();
        
        for (i = 0, len = records.length; i < len; i++) {
            if (1*records[i].get(this.parent_id_field_name) == null) { 
                result.push(records[i]);
            }
        }
        
        return result;
    },
    
    getNodeDepth : function(rc)
    {
        return this.getNodeAncestors(rc).length;
    },
    
    getNodeParent : function(rc)
    {
        return this.getById(rc.get(this.parent_id_field_name));
    },
    
    getNodeChildren : function(rc)
    {
        var i, 
            len, 
            result = [], 
            records = this.data.getRange();
        
        for (i = 0, len = records.length; i < len; i++) {
            if (records[i].get(this.parent_id_field_name) == rc.id) {
                result.push(records[i]);
            }
        }
        
        return result;
    },
    
    addToNode : function(parent, child)
    {
        child.set(this.parent_id_field_name, parent.id);
        this.addSorted(child);
    },
    
    removeFromNode : function(parent, child)
    {
        this.remove(child);
    }
});

Ext.reg('Ext.ux.maximgb.tg.AdjacencyListStore', Ext.ux.maximgb.tg.AdjacencyListStore);



Ext.ux.maximgb.tg.NestedSetStore = Ext.extend(Ext.ux.maximgb.tg.AbstractTreeStore,
{
    
    left_field_name : '_lft',
    
    
    right_field_name : '_rgt',
    
    
    level_field_name : '_level',
    
    
    root_node_level : 1,
    
    getRootNodes : function()
    {
        var i, 
            len, 
            result = [], 
            records = this.data.getRange();
        
        for (i = 0, len = records.length; i < len; i++) {
            if (records[i].get(this.level_field_name) == this.root_node_level) {
                result.push(records[i]);
            }
        }
        
        return result;
    },
    
    getNodeDepth : function(rc)
    {
        return rc.get(this.level_field_name) - this.root_node_level;
    },
    
    getNodeParent : function(rc)
    {
        var result = null,
            rec, records = this.data.getRange(),
            i, len,
            lft, r_lft,
            rgt, r_rgt,
            level, r_level;
                
        lft = rc.get(this.left_field_name);
        rgt = rc.get(this.right_field_name);
        level = rc.get(this.level_field_name);
        
        for (i = 0, len = records.length; i < len; i++) {
            rec = records[i];
            r_lft = rec.get(this.left_field_name);
            r_rgt = rec.get(this.right_field_name);
            r_level = rec.get(this.level_field_name);
            
            if (
                r_level == level - 1 &&
                r_lft < lft &&
                r_rgt > rgt
            ) {
                result = rec;
                break;
            }
        }
        
        return result;
    },
    
    getNodeChildren : function(rc)
    {
        var lft, r_lft,
            rgt, r_rgt,
            level, r_level,
            records, rec,
            result = [];
                
        records = this.data.getRange();
        
        lft = rc.get(this.left_field_name);
        rgt = rc.get(this.right_field_name);
        level = rc.get(this.level_field_name);
        
        for (i = 0, len = records.length; i < len; i++) {
            rec = records[i];
            r_lft = rec.get(this.left_field_name);
            r_rgt = rec.get(this.right_field_name);
            r_level = rec.get(this.level_field_name);
            
            if (
                r_level == level + 1 &&
                r_lft > lft &&
                r_rgt < rgt
            ) {
                result.push(rec);
            }
        }
        
        return result;
    }
});


Ext.ns('Sch');


Sch.TreeGanttView = Ext.extend(Sch.LockingSchedulerView, {
    
    // Without this line an extra redraw is done which doesn't fire any refresh event            
	afterRender : Ext.emptyFn,
	
	constructor : function() {
	    this.addEvents(
	        
	        'togglerow'
	    );
        Sch.TreeGanttView.superclass.constructor.apply(this, arguments);
	},
	
	doRender : function(cs, rs, ds, startRow, colCount, stripe){
		var ts = this.templates, ct = ts.cell, rt = ts.row, last = colCount-1,
			tstyle = 'width:'+this.getTotalWidth()+';',
			lstyle = 'width:'+this.getLockedWidth()+';',
			buf = [], lbuf = [], cb, lcb, c, p = {}, rp = {}, r, events, g = this.grid, processed_cnt = 0;
		for(var j = 0, len = rs.length; j < len; j++){
			r = rs[j]; cb = []; lcb = [];
			var rowIndex = (j+startRow);
			
			for(var i = 0; i < colCount; i++){
				c = cs[i];
				p.id = c.id;
				p.css = (i === 0 ? 'x-grid3-cell-first ' : (i == last ? 'x-grid3-cell-last ' : '')) +
                    (this.cm.config[i].cellCls ? ' ' + this.cm.config[i].cellCls : '');
				p.attr = p.cellAttr = '';
				p.cellCtCls = '';
				// @Modification: Added g to param list
                p.value = c.renderer(r.data[c.name], p, r, rowIndex, i, ds, g);
				p.style = c.style;
				if(Ext.isEmpty(p.value)){
					p.value = '&#160;';
				}
				if(this.markDirty && r.dirty && Ext.isDefined(r.modified[c.name])){
					p.css += ' x-grid3-dirty-cell';
				}
				if(c.locked){
				    // ----- Maxim Modification start
                    if (c.id === this.grid.master_column_id) {
                        p.treeui = this.renderCellTreeUI(r, ds);
                        p.css += ds.isLeafNode(r) ? ' sch-gantt-leaf-cell' : ' sch-gantt-parent-cell';
                        ct = ts.mastercell;             
                    }
                    else {
                        ct = ts.cell;
                    }
                    // ----- Maxim End of modification
					lcb[lcb.length] = ct.apply(p);
				}else{
                    ct = ts.cell;
					cb[cb.length] = ct.apply(p);
				}
			}
			var alt = [];
			
			// ----- Maxim Modification start
            if (!ds.isVisibleNode(r)) {
                rp.display_style = 'display: none;';
            }
            else {
                if(stripe && ((processed_cnt+1) % 2 === 0)){
				    alt[0] = 'x-grid3-row-alt';
			    }
                processed_cnt++;
                rp.display_style = '';
            }
            rp.level = ds.getNodeDepth(r);
            // ----- Maxim End of modification
            
			if(r.dirty){
				alt[1] = ' x-grid3-dirty-row';
			}
			rp.cols = colCount;
			if(this.getRowClass){
				alt[2] = this.getRowClass(r, rowIndex, rp, ds);
			}
            
			rp.alt = alt.join(' ');
			rp.cells = cb.join('');
			rp.tstyle = tstyle;
			buf[buf.length] = rt.apply(rp);
			rp.cells = lcb.join('');
			
			rp.tstyle = lstyle;
			lbuf[lbuf.length] = rt.apply(rp);
		}
		return [buf.join(''), lbuf.join('')];
	},
	
	// Maxim Tree Grid
	expanded_icon_class : 'ux-maximgb-tg-elbow-minus',
    collapsed_icon_class : 'ux-maximgb-tg-elbow-plus',
    
    // private - overriden
    initTemplates : function()
    {
        var ts = this.templates || {};
        
        if (!ts.row) {
            ts.row = new Ext.Template(
                '<div class="x-grid3-row ux-maximgb-tg-level-{level} {alt}" style="{tstyle} {display_style}">',
                    '<table class="x-grid3-row-table" border="0" cellspacing="0" cellpadding="0" style="{tstyle}">',
                        '<tbody>',
                            '<tr>{cells}</tr>',
                        '</tbody>',
                    '</table>',
                '</div>'
            );
        }
        
        if (!ts.mastercell) {
            ts.mastercell = new Ext.Template(
                '<td class="x-grid3-col x-grid3-cell x-grid3-td-{id} {css}" style="{style}" tabIndex="0" {cellAttr}>',
                    '<div class="ux-maximgb-tg-mastercell-wrap">', // This is for editor to place itself right
                        '{treeui}',
                        '<div class="x-grid3-cell-inner x-grid3-col-{id}" unselectable="on" {attr}>{value}</div>',
                    '</div>',
                '</td>'
            );
        }
        
        if (!ts.treeui) {
            ts.treeui = new Ext.Template(
                '<div class="ux-maximgb-tg-uiwrap" style="width: {wrap_width}px">',
                    '<div style="left: {left}px" class="{cls}">&#160;</div>',
                '</div>'
            );
        }
        
        this.templates = ts;
        Sch.TreeGanttView.superclass.initTemplates.call(this);
    },
    
    levelWidth : 16,
    
    renderCellTreeUI : function(record, store)
    {
        var tpl = this.templates.treeui,
            tpl_data = {},
            rec, parent,
            depth = level = store.getNodeDepth(record);
        
        tpl_data.wrap_width = (depth + 1) * this.levelWidth; 
        if (store.isLeafNode(record)) {
            tpl_data.cls = 'ux-maximgb-tg-elbow sch-gantt-leaf';
        }
        else {
            tpl_data.cls = 'ux-maximgb-tg-elbow-active sch-gantt-parent ';
            if (store.isExpandedNode(record)) {
                tpl_data.cls += this.expanded_icon_class;
            }
            else {
                tpl_data.cls += this.collapsed_icon_class;
            }
        }
        tpl_data.left = 1 + depth * this.levelWidth;
            
        return tpl.apply(tpl_data);
    },
    
    processRows : function(startRow, skipStripe){
        if(!this.ds || this.ds.getCount() < 1){
            return;
        }
        var rows = this.getRows(),
            lrows = this.getLockedRows(),
            row, lrow, processed_cnt = 0;
        skipStripe = skipStripe || !this.grid.stripeRows;
        startRow = startRow || 0;
        for(var i = 0, len = rows.length; i < len; ++i){
            row = rows[i];
            lrow = lrows[i];
            row.rowIndex = i;
            lrow.rowIndex = i;
            
            if (row.style.display != 'none') {
                if(!skipStripe){
                    row.className = row.className.replace(this.rowClsRe, ' ');
                    lrow.className = lrow.className.replace(this.rowClsRe, ' ');
                    
                    if ((processed_cnt + 1) % 2 === 0){
                        row.className += ' x-grid3-row-alt';
                        lrow.className += ' x-grid3-row-alt';
                    }
                    
                    processed_cnt++;
                }
            }
            
            if(this.syncHeights){
                var el1 = Ext.get(row),
                    el2 = Ext.get(lrow),
                    h1 = el1.getHeight(),
                    h2 = el2.getHeight();
                
                if(h1 > h2){
                    el2.setHeight(h1);    
                }else if(h2 > h1){
                    el1.setHeight(h2);
                }
            }
        }
        if(startRow === 0){
            Ext.fly(rows[0]).addClass(this.firstRowCls);
            Ext.fly(lrows[0]).addClass(this.firstRowCls);
        }
        Ext.fly(rows[rows.length - 1]).addClass(this.lastRowCls);
        Ext.fly(lrows[lrows.length - 1]).addClass(this.lastRowCls);
    },
    
    ensureVisible : function(row, col, hscroll)
    {
        var ancestors, record = this.ds.getAt(row);
        
        if (!this.ds.isVisibleNode(record)) {
            ancestors = this.ds.getNodeAncestors(record);
            while (ancestors.length > 0) {
                record = ancestors.shift();
                if (!this.ds.isExpandedNode(record)) {
                    this.ds.expandNode(record);
                }
            }
        }
        
        return Sch.TreeGanttView.superclass.ensureVisible.call(this, row, col, hscroll);
    },
    
    // Private
    expandRow : function(record, skip_process)
    {
        var ds = this.ds,
            i, len, row, pmel, children, index, child_index;
        
        if (typeof record == 'number') {
            index = record;
            record = ds.getAt(index);
        }
        else {
            index = ds.indexOf(record);
        }
        
        if (ds.isLeafNode(record)) return;
        
        skip_process = skip_process || false;
        
        row = this.getLockedRow(index);
        pmel = Ext.fly(row).child('.ux-maximgb-tg-elbow-active');
        if (pmel) {
            pmel.removeClass(this.collapsed_icon_class);
            pmel.addClass(this.expanded_icon_class);
        }
        if (ds.isVisibleNode(record)) {
            children = ds.getNodeChildren(record);
            for (i = 0, len = children.length; i < len; i++) {
                child_index = ds.indexOf(children[i]);
                this.getRow(child_index).style.display = 'block';
                this.getLockedRow(child_index).style.display = 'block';
                
                if (ds.isExpandedNode(children[i])) {
                    this.expandRow(child_index, true);
                }
            }
        }
        if (!skip_process) {
            this.processRows(0);
        }
        
        this.fireEvent('togglerow', this, record, true);
    },
    
    collapseRow : function(record, skip_process)
    {
        var ds = this.ds,
            i, len, children, pmel, row, index, child_index;
        
                
        if (typeof record == 'number') {
            index = record;
            record = ds.getAt(index);
        }
        else {
            index = ds.indexOf(record);
        }
        
        if (ds.isLeafNode(record)) return;
        
        skip_process = skip_process || false;
        
        row = this.getLockedRow(index);
        pmel = Ext.fly(row).child('.ux-maximgb-tg-elbow-active');
        if (pmel) {
            pmel.removeClass(this.expanded_icon_class);
            pmel.addClass(this.collapsed_icon_class);
        }
        children = ds.getNodeChildren(record);
        for (i = 0, len = children.length; i < len; i++) {
            child_index = ds.indexOf(children[i]);
            row = this.getRow(child_index);
            if (row.style.display != 'none') {
                row.style.display = 'none'; 
                this.getLockedRow(child_index).style.display = 'none'; 
                this.collapseRow(child_index, true);
            }
        }
        if (!skip_process) {
            this.processRows(0);
        }
        this.fireEvent('togglerow', this, record, true);
    },
    
    // private
    initData : function(ds, cm)
    {
        if(this.cm){
			this.cm.un('columnlockchange', this.onColumnLock, this);
		}
		
		// Bypass schedulerview initData since we don't want the default behaviour with the eventStore anymore
		Sch.SchedulerView.superclass.initData.call(this, ds, cm);
		
		if(this.cm){
			this.cm.on('columnlockchange', this.onColumnLock, this);
		}
		
        if (this.ds) {
            this.ds.un('expandnode', this.onStoreExpandNode, this);
            this.ds.un('collapsenode', this.onStoreCollapseNode, this);
        }
        if (ds) {
            ds.on({
                expandnode : this.onStoreExpandNode,
                expandingnode : this.onStoreStartExpand,
                expandnodefailed : this.onStoreExpandNodeFailed,
                collapsenode : this.onStoreCollapseNode,
                scope : this
            });
        }
    },
    
    // Before remote request
    onStoreStartExpand : function(s, record) {
        var index = s.indexOf(record);
        if (index >= 0) {
            var lockedRow = this.getLockedRow(index);
            Ext.fly(lockedRow).child('.' + this.collapsed_icon_class).addClass('sch-loading');
        }
    },
    
    // After failed remote request
    onStoreExpandNodeFailed : function(s, n, record) {
        var index = s.indexOf(record);
        if (index >= 0) {
            var lockedRow = this.getLockedRow(index);
            Ext.fly(lockedRow).child('.' + this.collapsed_icon_class).removeClass('sch-loading');
        }
    },
    
    onLoad : function(store, records, options)
    {
        if (options && 
            options.params && 
            (
                options.params[store.paramNames.active_node] === null ||
                store.indexOfId(options.params[store.paramNames.active_node]) == -1
            )
        ) {
            Sch.TreeGanttView.superclass.onLoad.call(this, store, records, options);
        }
    },
    
    onAdd : function(ds, records, index)
    {
        Sch.TreeGanttView.superclass.onAdd.call(this, ds, records, index);
        if (this.mainWrap) {
           this.processRows(0);
        }
    },
    
    onRemove : function(ds, record, index, isUpdate)
    {
        Sch.TreeGanttView.superclass.onRemove.call(this, ds, record, index, isUpdate);
         
        // Check if the remove made the parent a leaf? 
        var parent = ds.getNodeParent(record);
        
        if (parent) {
            parent.set(ds.leaf_field_name, !ds.hasChildNodes(parent));
        }
        
        if(isUpdate !== true){
            if (this.mainWrap) {
                this.processRows(0);
            }
        }
    },
    
    onUpdate : function(ds, record)
    {
        Sch.TreeGanttView.superclass.onUpdate.call(this, ds, record);
        if (this.mainWrap) {
            this.processRows(0);
        }
    },
    
    refreshRow : function(record)
    {
        Sch.TreeGanttView.superclass.refreshRow.call(this, record);
        if (this.mainWrap) {
            this.processRows(0);
        }
    },
    
    onStoreExpandNode : function(store, record, isAsync)
    {
        // If this was a remote request, remove the load indicator
        if (isAsync) {
            var index = store.indexOf(record);
            if (index >= 0) {
                var lockedRow = this.getLockedRow(index);
                Ext.fly(lockedRow).child('.' + this.collapsed_icon_class).removeClass('sch-loading');
            }
        }
        this.expandRow(record);
    },
    
    onStoreCollapseNode : function(store, rc)
    {
        this.collapseRow(rc);
    },
    
    refresh : Ext.ux.grid.LockingGridView.prototype.refresh.createInterceptor(function() {
        // Fit columns if view occupied by unlocked columns is less than what is available
        if (this.cm.getColumnWidth(this.grid.nbrStaticColumns) < Math.floor(this.scroller.getWidth() - this.getScrollOffset())/ (this.cm.getColumnCount() - this.cm.getLockedCount())) {
            this.fitTimeColumns(true);
        }
    }),
    
    //Prevent underlying EditorGridPanel from messing up scroll position after edit
    focusCell : Ext.ux.grid.LockingGridView.prototype.focusCell.createInterceptor(function(row, col, hscroll) {
        // EditorGridPanel performs a focusCell() after editing which messes up the scroll position
        return col >= this.cm.getLockedCount();
    })
});




 
Ext.ns('Sch');


Sch.TreeGanttPanel = Ext.extend(Sch.EditorSchedulerPanel, {
    
    
    overClass : 'sch-event-hover',
    
    
    resizeHandles : 'both',
    
    
    highlightWeekends : true,
    
    
    enableTaskDragDrop : true,
    
    
    enableDependencyDragDrop : true,
     
    
    enableLabelEdit : true,
    
    
    enableLockedFieldsEdit : true,
    
    
    recalculateParentsAfterEdit: true,
    
    
    cascadeChanges: false,
     
    
    showTreeLines : false,
     
    
    highlightAffectedTasks: true,

    
    getDependencyManager : function() {
        return this.dependencyManager;
    },
    
    
    disableWeekendHighlighting : function(disabled) {
        this.weekendZonesPlugin.setDisabled(disabled);
    },
    
    
    updateTimeColumnHeaderWidths : function(width) {
        var cm = this.getColumnModel();
        for (var i = this.nbrStaticColumns, l = cm.getColumnCount(); i < l; i++) {
	        cm.setColumnWidth(i, width, true);
	    }
	    
	    this.getView().updateHeaders();
    },
    
    
    updateTimeColumnWidths : function(width) {
        this.getView().updateTimeColumnWidths(width);
    },
    
    
    getEventRecordFromElement : function(el) {
        var element = Ext.get(el);
        if (!element.is(this.eventWrapSelector)) {
            element = element.up(this.eventWrapSelector);
        }
        return this.getEventRecordFromDomId(element.child(this.eventSelector).id);
    },
    
    
    fitTimeColumns : function() {
        this.getView().fitTimeColumns();
    },
    
    //Returns the duration in days between two dates, which is different depending if you include weekends or not.
    getDuration : function(start, end) {
        // TODO check for weekends boolean
        return Math.round(Date.getDurationInDays(start, end)*10)/10;
    },
    
    clicksToEdit : 1,
    
    columnLines : false,
    
    // private
    eventWrapSelector : '.sch-event-wrap',
    
    // private
    constructor : function(config) {
        this.addEvents(
            
            'labeledit_beforecomplete', 
            
            
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
                    '<div id="{id}" class="sch-event sch-gantt-item sch-gantt-task-bar {cls}" style="width:{width}px">',
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
                '</div>'
            ).compile();
        }
        
        if (!config.parentEventTemplate) {
            config.parentEventTemplate = new Ext.Template(
                '<div class="sch-event-wrap sch-gantt-parent-task" style="left:{leftOffset}px;width:{width}px">',
                    // Left label 
                    '<div class="sch-gantt-labelct-left"><label class="sch-gantt-label sch-gantt-label-left">{leftLabel}</label></div>',
                    
                    // Task bar
                    '<div id="{id}" class="sch-event sch-gantt-item sch-gantt-parenttask-bar {cls}" style="width:{width}px">',
                        '<div class="sch-gantt-parenttask-leftarrow"></div>'+
                        '<div class="sch-gantt-progress-bar" style="width:{percentDone}%">&#160;</div>',
                        '<div class="sch-gantt-parenttask-rightarrow"></div>'+
                    '</div>',
                    
                    // Right label 
                    '<div class="sch-gantt-labelct-right" style="left:{width}px"><label class="sch-gantt-label sch-gantt-label-right">{rightLabel}</label></div>',
                '</div>'
            ).compile();
        }
    
        if (!config.milestoneTemplate) {
            config.milestoneTemplate = new Ext.Template(
                '<div class="sch-event-wrap sch-gantt-milestone" style="left:{leftOffset}px">',
                    // Left label 
                    '<div class="sch-gantt-labelct-left"><label class="sch-gantt-label sch-gantt-label-left">{leftLabel}</label></div>',
                    
                    // Milestone indicator
                    '<div id="{id}" class="sch-event sch-gantt-item sch-gantt-milestone-diamond {cls}">',
                        // Left terminal
                        config.enableDependencyDragDrop !== false ? '<div class="sch-gantt-terminal sch-gantt-terminal-start"></div>' : '',
                        
                        // Right terminal
                        config.enableDependencyDragDrop !== false ? '<div class="sch-gantt-terminal sch-gantt-terminal-end"></div>' : '',
                    '</div>',
                    
                    // Right label 
                    '<div class="sch-gantt-labelct-right"><label class="sch-gantt-label sch-gantt-label-right">{rightLabel}</label></div>',
                '</div>'
            ).compile();
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
            var lineStore = new Ext.data.JsonStore({
                    fields : ['Date', 'Cls', 'Text'],
                    data : [{Date : new Date(), Cls : 'sch-todayLine', Text : 'Current time'}]
                });
                
            this.todayLinePlugin = new Sch.plugins.Lines({ store : lineStore });
            
            var runner = new Ext.util.TaskRunner();
            runner.start({
                run: function() {
                    lineStore.getAt(0).set('Date', new Date());
                },
                interval: 60000 // Update line store every minute
            });
            
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
    
    // private, override base class version to provide the gantt specific view
    internalRenderer : function(v, m, event, row, col, ds, grid) {
        var cellResult = '',
            viewStart = grid.getStart(),
            viewEnd = grid.getEnd(),
            colWidth = grid.getColumnModel().getColumnWidth(col),
            c = this;
            
        // Call timeCellRenderer to be able to set css/style properties on individual grid cells
        grid.timeCellRenderer.call(this, event, m, row, col, ds, c.start, c.end, grid);
            
        var start = event.get('StartDate'),
            end = event.get('EndDate'),
            startsInsideView = start.betweenLesser(c.start, c.end);
        
        // Determine if the event should be rendered or not
        if (startsInsideView || (col == grid.nbrStaticColumns && start < c.start && end > c.start)) {
            
            var availableTimeInColumn = Date.getDurationInMinutes(c.start, c.end),
                leftOffset = Math.floor((Date.getDurationInMinutes(c.start, startsInsideView ? start : c.start) / availableTimeInColumn) * colWidth),
                // Get data from user "renderer" 
                eventData = grid.eventRenderer.call(this, event, row, col, ds) || {};
                
            if (end - start > 0) {
                // Regular task
                var itemWidth = Math.floor(grid.getXFromDate(Date.min(end, viewEnd)) - grid.getXFromDate(startsInsideView ? start : viewStart)),
                    endsOutsideView = end > viewEnd,
                    isLeaf = ds.isLeafNode(event);
                
                if (!isLeaf) {
                    itemWidth += 12; // Add the down arrow width
                    leftOffset -= 6; // Remove half of the down arrow width
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
            } else {
                // Milestone
                Ext.apply(eventData, {
                    // Core properties
                    id : grid.eventPrefix + event.id,
                    cls : (eventData.cls || '') + (event.dirty ? ' sch-dirty' : ''),
                    leftOffset : leftOffset - 8, // Remove half of the diamond width
                    
                    // Gantt specific
                    leftLabel : event.get(grid.leftLabelField) || '',
                    rightLabel : event.get(grid.rightLabelField) || ''
                });
                
                cellResult += grid.milestoneTemplate.apply(eventData);
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
        Sch.TreeGanttPanel.superclass.initComponent.call(this);
    },
    
    initEvents : function() {
        this.on('afterrender', this.onGanttRender, this);
        this.on('mouseenter', this.onTaskOver, this);
        this.on('mouseleave', this.onTaskOut, this);
        
        if (this.recalculateParentsAfterEdit) {
            this.store.on({
                'update' : this.onStoreUpdate, 
                'add' : this.onStoreAddRemove, 
                'remove' : this.onStoreAddRemove, 
                scope : this
            });
        }
        this.on('mouseleave', this.onTaskOut, this);
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
        
        if (this.showTreeLines) {
            this.el.addClass('sch-ganttpanel-with-treelines');
        }
        
        this.dependencyManager = new Sch.DependencyManager(this, {
            containerEl : v.scroller,
            checkVisible : true,
            enableDependencyDragDrop : this.enableDependencyDragDrop,
            store : this.dependencyStore
        });
        
        v.on('refresh', this.dependencyManager.renderDependencies, this.dependencyManager);
        v.on('togglerow', this.dependencyManager.renderDependencies, this.dependencyManager);
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
    
    highlightDependency : function(depId) {
        this.dependencyManager.highlightDependency(depId);
    },
    
    unhighlightDependency : function(depId) {
        this.dependencyManager.unhighlightDependency(depId);
    },
    
    getTaskById : function(id) {
        return this.store.getById(id);
    },
    
    doHighlightAffectedTasks : function(taskId) {
        if (!this.cascadeChanges) return;
        
        var el = this.getElementFromEventId(taskId);
        
        // El might not exist in DOM
        if (el) {
            el.addClass('sch-event-selected');
        }
        
        this.dependencyStore.queryBy(function(dep) {
            if (dep.get('From') == taskId) {
                this.highlightDependency(dep.id);
                this.doHighlightAffectedTasks(dep.get('To'));
            }
        }, this);
    },
    
    clearSelectedTasksAndDependencies : function() {
        this.getView().el.select('.sch-event-selected').removeClass('sch-event-selected');
        this.getView().el.select('.sch-dependency-selected').removeClass('sch-dependency-selected');
    },
    
    
    
    master_column_id : 0,

    
    onClick : function(e)
    {
        var target = e.getTarget('.ux-maximgb-tg-elbow-active', 1) || 
                     e.getTarget('.sch-gantt-parenttask-bar');
        
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
        } else {
            Sch.TreeGanttPanel.superclass.onClick.call(this, e);
        }
    },

    
    onMouseDown : function(e)
    {
        if (!e.getTarget('.ux-maximgb-tg-elbow-active', 1)) {
            Sch.TreeGanttPanel.superclass.onMouseDown.call(this, e);
        }
    },
    
    onStoreAddRemove : function(store, rec) {
        this.store.suspendEvents();
        this.recalculateParents(store, rec);
        this.store.resumeEvents();
        this.getView().refresh();
    },
    
    onStoreUpdate : function(store, rec, operation, prev) {
        // No need to recalculate for commits
        if (operation !== Ext.data.Record.COMMIT && (!prev || prev.StartDate || prev.EndDate)) {
            this.onStoreAddRemove(store, rec);
        }
    },
    
    recalculateParents : function(store, rec) {
        rec = Ext.isArray(rec) ? rec[0] : rec;
        var earliest = new Date(9999,0,0), 
            latest = new Date(0),
            parent = this.store.getNodeParent(rec);
        
        if (parent) {
            var children = this.store.getNodeChildren(parent);
            if (children.length > 0) {
                Ext.each(children, function(r) {
                    earliest = Date.min(earliest, r.get('StartDate'));
                    latest = Date.max(latest, r.get('EndDate'));
                });
                
                parent.set('StartDate', earliest);
                parent.set('EndDate', latest);
            }
            this.recalculateParents(store, parent);
        }
    }
}); 

Ext.reg('treegantt', Sch.TreeGanttPanel);


Ext.ns('Sch.gantt.plugins');


Sch.gantt.plugins.DragDrop = function(config) {
    Ext.apply(this, config);
    Sch.gantt.plugins.DragDrop.superclass.constructor.call(this);
};

Ext.extend(Sch.gantt.plugins.DragDrop, Ext.util.Observable, {
    
    useTooltip : true,
    
    
    validatorFn : function(record, date, duration, e) {
        return true;
    },
    
    
    validatorFnScope : null,
    
    // private
    eventSelector : '.sch-event',

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
            selector = this.eventSelector,
            sm = g.getSelectionModel();
            
        if (this.useTooltip) {
            this.tip = new Sch.gantt.plugins.Tooltip({}, g);
        }
        
        // The drag zone behaviour
        g.dragZone = new Sch.gantt.DragZone(v.scroller, {
            ddGroup : this.ddGroup,
            containerScroll : true,
            
            onDragOver: function(e, id){
                
                var x = this.proxy.el.getX() + (this.dragData.duration === 0 ? 8 : 0), // Adjust x position for milestones
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
                var sourceNode = e.getTarget(selector, me.getItemDepth);
                
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
            shadow: !config || config.shadow !== false
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
        this.setXConstraint(0, // left
                this.el.getWidth() - b.width, //right
		        this.xTickSize
        );
        this.setYConstraint(0, 0, this.yTickSize);
    }
});




Ext.ns('Sch.gantt.plugins');


Sch.gantt.plugins.LabelEditor = function(config) {
    Ext.apply(this, config);
    Sch.gantt.plugins.LabelEditor.superclass.constructor.call(this, config);
};
 
Ext.extend(Sch.gantt.plugins.LabelEditor, Ext.util.Observable, {
    
    
    editLeft : function(record) {
        var wrap = this.grid.getElementFromEventRecord(record).up('.sch-event-wrap');
        this.editor.startEdit(wrap.child("." + this.leftLabelCls));
        this.record = record;
        this.editingLeft = true;
    },
    
    
    editRight : function(record) {    
        var wrap = this.grid.getElementFromEventRecord(record).up('.sch-event-wrap');
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
                    return grid.fireEvent('labeledit_beforecomplete', grid, value, original);
                },
                'complete' : {
                    fn : function(editor, value, original) {
                        this.record.set(this.editingLeft ? grid.leftLabelField : grid.rightLabelField, value);
                        grid.fireEvent('labeledit_complete', grid, value, original);
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



Ext.ns('Sch.gantt.plugins');


Sch.gantt.plugins.TaskContextMenu = function(config) {
    Ext.apply(this, config);
    Sch.gantt.plugins.TaskContextMenu.superclass.constructor.call(this, config);
};
 
Ext.extend(Sch.gantt.plugins.TaskContextMenu, Ext.util.Observable, {
    
    texts : {
        newTaskText : 'New task', 
        newMilestoneText : 'New milestone', 
        deleteTask : 'Delete task',
        editLeftLabel : 'Edit left label',
        editRightLabel : 'Edit right label',
        add : 'Add...',
        deleteDependency : 'Delete dependency...',
        addTaskAbove : 'Task above',
        addTaskBelow : 'Task below',
        addMilestone : 'Milestone',
        addSubtask :'Sub-task',
        addSuccessor : 'Successor',
        addPredecessor : 'Predecessor'
    },
    
    items : [
        {
            id : 'deleteTask'
        },
        {
            id : 'editLeftLabel'
        },
        {
            id : 'editRightLabel'
        },
        {
            id: 'add',
            menu : {
                plain : true,
                items : [
                    {
                        id : 'addTaskAbove',
                        text : Sch.gantt.plugins.TaskContextMenu.prototype.addTaskAbove
                    },
                    {
                        id : 'addTaskBelow',
                        text : Sch.gantt.plugins.TaskContextMenu.prototype.addTaskBelow
                    },
                    {
                        id : 'addMilestone',
                        text : Sch.gantt.plugins.TaskContextMenu.prototype.milestone
                    },
                    {
                        id : 'addSubtask',
                        text : Sch.gantt.plugins.TaskContextMenu.prototype.subtask
                    },
                    {
                        id : 'addSuccessor',
                        text : Sch.gantt.plugins.TaskContextMenu.prototype.successor
                    },
                    {
                        id : 'addPredecessor',
                        text : Sch.gantt.plugins.TaskContextMenu.prototype.predecessor
                    }
                ]
            }
        },
        {
            id : 'deleteDependency',
            menu : { plain : true }
        }
    ],
    
    populateDependencyMenu : function(menu) {
        var g = this.grid, 
            taskStore = g.store,
            dependencies = g.getDependenciesForTask(this.rec);
        
        menu.removeAll();

        if (dependencies.length === 0) {
            return false;
        }
        
        dependencies.each(function(dep) {
            var fromId = dep.get('From'),
                task = g.getTaskById(fromId == this.rec.id ? dep.get('To') : fromId),
                text = Ext.util.Format.ellipsis(task.get('Name'), 30);
            
            menu.addMenuItem({
                recIndex : dep.store.indexOf(dep),
                text : text,
                scope : this,
                handler : function(menuItem) {
                    dep.store.removeAt(menuItem.recIndex);
                }
            });
        }, this);
    },
    
    mouseOver : function(menu, e, item) {    
        this.grid.highlightDependency(item.id);
    },
    
    mouseOut : function(menu, e, item) {
        this.grid.unhighlightDependency(item.id);
    },
        
    init:function(grid) {
        this.grid = grid;
        
        // Special treatment of the sub-menu options
        Ext.each(this.items, function(o) {
            o.text = this.texts[o.id];
            if (o.id === 'add') {   
                Ext.each(o.menu.items, function(subo) {
                    subo.text = this.texts[subo.id];
                }, this);
                
                o.menu.listeners = {
                    itemclick : {
                        fn : this.onItemClick, 
                        scope : this
                    }
                };
            }
            else if (o.id === 'deleteDependency') {
                o.menu.listeners = {
                    beforeshow : {
                        fn : this.populateDependencyMenu,
                        scope : this
                    }
                    
                    // Todo, highlight dependencies on mouseover of the menu item
//                    ,
//                    
//                    mouseover : {
//                        fn : this.mouseOver,
//                        scope : this
//                    },
//                    
//                    mouseout : {
//                        fn : this.mouseOut,
//                        scope : this
//                    }
                };
            }
        }, this);
        
        grid.on('eventcontextmenu', this.onEventContextMenu, this);
    },
    
    onEventContextMenu : function(g, rec, e) {
        e.stopEvent();
        
        if (!this.menu) {
            this.menu = new Ext.menu.Menu({
                plain : true,
                items : this.items
            });
            
            this.menu.on('itemclick', this.onItemClick, this);
        }
            
        this.rec = rec;
        this.menu.showAt(e.getXY());
    },
    
    onItemClick : function(item, e) {
        this.actions[item.id] && this.actions[item.id].call(this);
    },
    
    copyTask : function(originalRecord) {
        var s = originalRecord.store,
            newTask = new s.recordType({
                PercentDone : 0,
                Name : this.texts.newTaskText,
                StartDate : originalRecord.get('StartDate'),
                EndDate : originalRecord.get('EndDate'),
                ParentId : originalRecord.get('ParentId'),
                IsLeaf : true
            });
        return newTask;
    },
    
    actions : {
        deleteTask: function() {
            this.grid.store.remove(this.rec);
        },
        
        editLeftLabel : function() {
            this.grid.editLeftLabel(this.rec);
        },
            
        editRightLabel : function() {
            this.grid.editRightLabel(this.rec);
        },
            
        addTaskAbove : function() {
            var s = this.rec.store,
                newTask = this.copyTask(this.rec);
                
            s.insert(s.indexOf(this.rec), newTask);
        },
            
        addTaskBelow : function() {
            var s = this.rec.store,
                newTask = this.copyTask(this.rec), 
                insertIndex;
            
            if (s.isLeafNode(this.rec)) {
                insertIndex = s.indexOf(this.rec) + 1;
            } else {
                var sibling = s.getNodeNextSibling(this.rec);
                
                insertIndex = sibling ? (s.indexOf(sibling) - 1) : s.getCount();
            }
            s.insert(insertIndex, newTask);
        },
            
        addSubtask : function() {
            var s = this.rec.store,
                newTask = this.copyTask(this.rec);
            
            this.rec.set(this.rec.store.leaf_field_name, false);
            s.addToNode(this.rec, newTask);
            s.expandNode(this.rec);
        },
            
        addSuccessor : function() {
            var s = this.rec.store,
                depStore = this.grid.dependencyStore,
                index = this.rec.store.indexOf(this.rec),
                newTask = this.copyTask(this.rec);
            
            newTask.set('StartDate', this.rec.get('EndDate'));
            newTask.set('EndDate', this.rec.get('EndDate').add(Date.DAY, 1));
            
            s.insert(index + 1, newTask);
            depStore.add(new depStore.recordType({
                    From : this.rec.id,
                    To : newTask.id,
                    Type : Sch.Dependency.EndToStart
                })
            );
        },
            
        addPredecessor : function() {
           var s = this.rec.store,
                depStore = this.grid.dependencyStore,
                index = this.rec.store.indexOf(this.rec),
                newTask = this.copyTask(this.rec),
                newEnd = this.rec.get('StartDate');
            
            newTask.set('EndDate', newEnd);
            newTask.set('StartDate', newEnd.add(Date.DAY, -1));
            
            s.insert(index, newTask);
            depStore.add(new depStore.recordType({
                    From : newTask.id,
                    To : this.rec.id,
                    Type : Sch.Dependency.EndToStart
                })
            );
        },
            
        addMilestone : function() {
            var s = this.rec.store,
                newMilestone = this.copyTask(this.rec);
                index = this.rec.store.indexOf(this.rec);
            newMilestone.set('StartDate', newMilestone.get('EndDate'));    
            s.insert(index + 1, newMilestone);
       }
    }
}); 


Ext.ns('Sch.gantt.plugins');
 

Sch.gantt.plugins.Tooltip = function(config, grid) {
    Ext.apply(this, config);
    this.grid = grid;
    Sch.gantt.plugins.Tooltip.superclass.constructor.call(this);
};
 
Ext.extend(Sch.gantt.plugins.Tooltip, Ext.ToolTip, {
    
    showClock : false,
    
    // Don't show end date when it's exactly the same as the start date (milestones)
    hideSameEndDate : true,
    
    startText : 'Starts: ',
    
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
        
        if (end - start > 0 || !this.hideSameEndDate) {
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

