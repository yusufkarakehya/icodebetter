/*
 * Ext Gantt 1.1
 * Copyright(c) 2009-2010 Mats Bryntse Consulting
 * mats@ext-scheduler.com
 * http://www.ext-scheduler.com/license.html
 *
 */

/**
 * English translations for the Gantt component
 *
 * NOTE: To change locale for month/day names you have to use the Ext JS language pack.
 */

if (Sch.gantt.plugins.Resize) {
    Sch.gantt.plugins.Resize.prototype.startText = 'Start:';
    Sch.gantt.plugins.Resize.prototype.durationText = 'Duration:';
    Sch.gantt.plugins.Resize.prototype.dayText = 'd';
}

if (Sch.DependencyDragDrop) {
    Sch.DependencyDragDrop.prototype.fromText = 'From: <strong>{0}</strong> {1}<br/>';
    Sch.DependencyDragDrop.prototype.toText = 'To: <strong>{0}</strong> {1}';
    Sch.DependencyDragDrop.prototype.startText = 'Start';
    Sch.DependencyDragDrop.prototype.endText = 'End';
}

if (Sch.gantt.plugins.Tooltip) {
    Sch.gantt.plugins.Tooltip.prototype.startText = 'Starts: ';
    Sch.gantt.plugins.Tooltip.prototype.endText = 'Ends: ';
}
    
if (Sch.gantt.plugins.TaskContextMenu) {
    Sch.gantt.plugins.TaskContextMenu.prototype.texts = {
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
    };
}

if (Sch.gantt.plugins.CurrentTimeLine) {
    Sch.gantt.plugins.CurrentTimeLine.prototype.tooltipText = 'Current time';
}