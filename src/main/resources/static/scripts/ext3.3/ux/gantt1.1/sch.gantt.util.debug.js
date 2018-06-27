/*
 * Ext Gantt 1.1
 * Copyright(c) 2009-2010 Mats Bryntse Consulting
 * mats@ext-scheduler.com
 * http://www.ext-scheduler.com/license.html
 *
 */

/*
 * To analyze possible errors in your setup, include this on your HTML page and use firebug (or any other console application) to execute line below:
 * >
 * > ganttDiagnostics();
 * > ...
 */ 
function ganttDiagnostics() {
    var log;
    if (console && console.log) {
        log = console.log;
    } else {
        if (!window.ganttDebugWin) {
            window.ganttDebugWin = new Ext.Window({
                height:400,
                width: 500,
                bodyStyle:'padding:10px',
                closeAction : 'hide',
                autoScroll:true
            });
        }
        window.ganttDebugWin.show();
        window.ganttDebugWin.update('');
        log = function(text){ window.ganttDebugWin.update((window.ganttDebugWin.body.dom.innerHTML || '') + text + '<br/>');};
    }

    var els = Ext.select('.sch-ganttpanel');
    
    if (els.getCount() === 0) {
        log('No gantt component found');
        return;
    }
    
    var gantt = Ext.getCmp(els.elements[0].id),
        ts = gantt.store,
        ds = gantt.dependencyStore;
    log('Gantt initial config:');
    log(gantt.initialConfig);
    log(gantt.el.select('*').getCount() + ' DOM elements in the gant component');
    log('Gantt view start: ' + gantt.getStart() + ', end: ' + gantt.getEnd());
    
    if (!ts) { log('No task store configured'); return; }
    if (!ds) {log('No dependency store configured'); }
    
    log(ts.getCount() + ' records in the resource store'); 
    log(ds.getCount() + ' records in the dependency store'); 
    log(Ext.select(gantt.eventSelector).getCount() + ' events present in the DOM'); 
    
    if (ts.getCount() > 0) {
        if (!ts.getAt(0).get('StartDate') || !(ts.getAt(0).get('StartDate') instanceof Date)) {
            log ('The store reader is misconfigured - The StartDate field is not setup correctly, please investigate');
            return;
        }
        
        if (!ts.getAt(0).get('EndDate') || !(ts.getAt(0).get('EndDate') instanceof Date)) {
            log('The store reader is misconfigured - The EndDate field is not setup correctly, please investigate');
            return;
        }
        
        if (!ts.fields.get('Id')) {
            log('The store reader is misconfigured - The Id field is not present');
            return;
        }
        
        log('Records in the task store:');
        ts.each(function(r, i) {
            log((i + 1) + '. Start:' + r.get('StartDate') + ', End:' + r.get('EndDate') + ', Id:' + r.get('Id'));
        });
    } else {
        log('Event store has no data.');
    }
    
    if (ds && ds.getCount() > 0) {
        log('Records in the dependency store:');
        ds.each(function(r) {
            log('From:' + r.get('From') + ', To:' + r.get('To') + ', Type:' + r.get('Type'));
            return;
        });
    }
    
    log('Everything seems to be setup ok!');
}