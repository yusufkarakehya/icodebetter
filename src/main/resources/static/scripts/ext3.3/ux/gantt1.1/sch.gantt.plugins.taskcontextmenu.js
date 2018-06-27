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
 * @class Sch.gantt.plugins.TaskContextMenu
 * @extends Ext.util.Observable
 * Plugin for showing a context menu when right clicking a task
 * @constructor
 * @param {Object} config The object containing the configuration 
 */
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
            dependencies = g.getDependenciesForTask(this.rec),
            depStore = g.dependencyStore;
        
        menu.removeAll();

        if (dependencies.length === 0) {
            return false;
        }
        
        dependencies.each(function(dep) {
            var fromId = dep.get('From'),
                task = g.getTaskById(fromId == this.rec.id ? dep.get('To') : fromId),
                text = Ext.util.Format.ellipsis(task.get('Name'), 30);
            
            menu.addMenuItem({
                depId : dep.id,
                text : text,
                scope : this,
                handler : function(menuItem) {
                    depStore.removeAt(depStore.indexOfId(menuItem.depId));
                }
            });
        }, this);
    },
    
    mouseOver : function(menu, e, item) {
        if (item) {
            this.grid.highlightDependency(item.depId);
        }
    },
    
    mouseOut : function(menu, e, item) {
        if (item) {
            this.grid.unhighlightDependency(item.depId);
        }
    },
        
    cleanUp : function() {
        if (this.menu) {
            this.menu.destroy();
        }
    },
    
    init:function(grid) {
        this.grid = grid;
        
        this.grid.on('destroy', this.cleanUp, this);
        
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
                    },
                    
                    // highlight dependencies on mouseover of the menu item
                    mouseover : {
                        fn : this.mouseOver,
                        scope : this
                    },
                    
                    // unhighlight dependencies on mouseout of the menu item
                    mouseout : {
                        fn : this.mouseOut,
                        scope : this
                    }
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
                
                insertIndex = sibling ? s.indexOf(sibling) : s.getCount();
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