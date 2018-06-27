/*
 * Ext Gantt 1.1
 * Copyright(c) 2009-2010 Mats Bryntse Consulting
 * mats@ext-scheduler.com
 * http://www.ext-scheduler.com/license.html
 *
 */
 
Ext.ns('Sch');


Sch.Dependency = {
    StartToStart : 0,
    StartToEnd : 1,
    EndToStart : 2,
    EndToEnd : 3
};


/**
 * @class Sch.DependencyManager
 * @extends Ext.util.Observable
 * <p>Internal class handling the dependency related functionality.</p>
 */
Sch.DependencyManager = Ext.extend(Ext.util.Observable, {
    
    /**
     * @cfg {Int} cascadeDelay If you usually have deeply nested dependencies, it might be a good idea to add a small delay
     *            to allow the modified record to be refreshed in the UI right away and then handle the cascading
     */
    cascadeDelay : 10,
    
    /**
     * Highlight the elements representing a particular dependency
     * @param {Mixed} record Either the id of a record or a record in the dependency store
     */
    highlightDependency : function(record) {
        if (!(record instanceof Ext.data.Record)) {
            record = this.store.getById(record);
        }
        this.getElementsForDependency(record).addClass('sch-dependency-selected');
    },
    
    /**
     * Remove highlight of the elements representing a particular dependency
     * @param {Mixed} record Either the id of a record or a record in the dependency store
     */
    unhighlightDependency : function(record) {
        if (!(record instanceof Ext.data.Record)) {
            record = this.store.getById(record);
        }
        this.getElementsForDependency(record).removeClass('sch-dependency-selected');
    },
    
    /**
     * Retrieve the elements representing a particular dependency
     * @param {Record} rec the record in the dependency store
     * @return {CompositeElementLite/CompositeElement}
     */
    getElementsForDependency : function(rec) {
        var id = rec instanceof Ext.data.Record ? rec.id : rec;
        return this.containerEl.select('.sch-dep-' + id);
    },
    
    // private
    constructor : function (g, cfg) {
        cfg = cfg || {};
        Ext.apply(this, cfg);
        
        this.grid = g;
        g.getView().on({
            rowupdated: this.onRowUpdated, 
	        rowsinserted : this.renderDependencies,
            rowremoved : this.onRowDeleted,
            scope : this
        });
        
        this.eventStore = g.store;
        this.eventStore.on({
            update: this.onEventUpdated, 
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
        var dep = depRecords[0];
        
        // Draw new dependencies for the event
        this.painter.drawSingleDependency(dep);
        
        // If cascade changes is activated, adjust the connected task start/end date
        if (this.grid.cascadeChanges) {
            this.constrainTask(this.eventStore.getById(dep.get('To')));
        }
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
    
    onRowUpdated : function(v, index, record) {
        if (!this.grid.cascadeChanges) {
            this.updateDependencies(record);
        }
    },
    
    onEventUpdated : function(store, record, operation, hashPrevious) {
        if (this.grid.cascadeChanges && hashPrevious) {
            (function(taskId) {
                this.eventStore.suspendEvents();
                this.store.each(function(dep) {
                    if (dep.get('From') == taskId) {
                        var dependentRec = this.eventStore.getById(dep.get('To'));
                        
                        if (!dependentRec) return;
                        
                        this.performCascade(dependentRec);
                    }
                }, this);
                
                this.eventStore.resumeEvents();
                this.grid.getView().refresh();
            }).defer(this.cascadeDelay, this, [record.id]);
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
    
    onRowDeleted : function(store, index, record) {
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
    
    performCascade : function(record) {
        var id = record.id;
        
        if (record.store.isLeafNode(record)) {
            this.constrainTask(record);
            this.grid.recalculateParents(record);
        }
        
        this.store.queryBy(function(dep) {
            if (dep.get('From') == id) {
                this.performCascade(record.store.getById(dep.get('To')));
            }
        }, this);
    },
    
    constrainTask : function(task) {
        var es = this.eventStore,
            constrainContext = this.getConstrainContext(task);
        
        if (constrainContext) {
            task.beginEdit();
            task.set('StartDate', constrainContext.startDate);
            task.set('EndDate', constrainContext.endDate);
            task.endEdit();
        }
    },
    
    /**
     * Returns the critical path(s) that can affect the end date of the project
     * @return {Array} paths An array of arrays (containing task chains)
     */
    getCriticalPaths : function() {
        // Grab task id's that don't have any "incoming" dependencies
        var nbrTasks = this.eventStore.getCount();
        
        if (nbrTasks <= 0) return [];
        
        var lastTaskEndDate = new Date(0);
        
        this.eventStore.each(function(t){
            lastTaskEndDate = Date.max(t.get('EndDate'), lastTaskEndDate);
        });
        
        var finalTasks = this.eventStore.queryBy(function(t) { return lastTaskEndDate - t.get('EndDate') === 0; }),
            cPaths = [];
        
        finalTasks.each(function(t) {
            cPaths.push(this.getCriticalPathsInternal(t));
        }, this);
        
        return cPaths;
    },
    
    // 
    /**
     * @private
     * Internal method, called recursively to query for the longest duration of the chain structure
     * @return {Array} chain The chain of linked tasks
     */
    getCriticalPathsInternal : function(task) {
        var cPath = [task],
            ctx = this.getConstrainContext(task);
        
        while(ctx) {
            cPath.push(ctx.constrainingTask);
            ctx = this.getConstrainContext(ctx.constrainingTask);
        }
        
        return cPath;
    },
    
    getConstrainContext : function(task) {
        var incomingTaskDependencies = this.store.queryBy(function(r) { return r.get('To') === task.id; } );
        
        if (incomingTaskDependencies.getCount() === 0) {
            return null;
        }
        
        var es = this.eventStore,
            taskDuration = task.get('EndDate') - task.get('StartDate'),
            earliestStartDate = new Date(0),
            earliestEndDate = new Date(0),
            constrainingTask;
            
        incomingTaskDependencies.each(function(d) {
            var t = es.getById(d.get('From'));
            
            switch(d.get('Type')) {
                case Sch.Dependency.StartToEnd:
                    if (earliestEndDate < t.get('StartDate')) {
                        earliestEndDate = t.get('StartDate');
                        earliestStartDate = earliestEndDate.add(Date.MILLI, -taskDuration);
                        constrainingTask = t;
                    }
                break;
                
                case Sch.Dependency.StartToStart:
                    if (earliestStartDate < t.get('StartDate')) {
                        earliestStartDate = t.get('StartDate');
                        earliestEndDate = earliestStartDate.add(Date.MILLI, taskDuration);
                        constrainingTask = t;
                    }
                break;
                
                case Sch.Dependency.EndToStart:
                    if (earliestStartDate < t.get('EndDate')) {
                        earliestStartDate = t.get('EndDate');
                        earliestEndDate = earliestStartDate.add(Date.MILLI, taskDuration);
                        constrainingTask = t;
                    }
                break;
                
                case Sch.Dependency.EndToEnd:
                    if (earliestEndDate < t.get('EndDate')) {
                        earliestEndDate = t.get('EndDate');
                        earliestStartDate = earliestEndDate.add(Date.MILLI, -taskDuration);
                        constrainingTask = t;
                    }
                break;
                
                default:
                    throw 'Invalid case statement';
                break;
            }
        });
        
        return {
            startDate : earliestStartDate,
            endDate : earliestEndDate,
            constrainingDate : earliestStartDate,
            constrainingTask : constrainingTask
        };
    }
});
