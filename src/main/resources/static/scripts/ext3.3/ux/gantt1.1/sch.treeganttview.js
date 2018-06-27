/*
 * Ext Gantt 1.1
 * Copyright(c) 2009-2010 Mats Bryntse Consulting
 * mats@ext-scheduler.com
 * http://www.ext-scheduler.com/license.html
 *
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
 * @class Sch.TreeGanttView
 * @extends Sch.LockingSchedulerView
 * This class provides column locking support and tree functionality. It is partially based on <a href="http://www.extjs.com/forum/showthread.php?76331-TreeGrid-%28Ext.ux.maximgb.tg%29-a-tree-grid-component-based-on-Ext-s-native-grid.">MaximGB's TreeGrid</a>. 
 * @constructor
 * @param {Object} config The object containing the configuration of this model.
 */
Sch.TreeGanttView = Ext.extend(Sch.LockingSchedulerView, {
    
    // Without this line an extra redraw is done which doesn't fire any refresh event            
	afterRender : Ext.emptyFn,
	
	constructor : function() {
	    this.addEvents(
	        /**
             * @event togglerow
             * Fires after a node has been toggled
             * @param {TreeGanttView} view The view object
             * @param {Ext.data.Record} record The record corresponding to the clicked record
             * @param {Boolean} expand True if node was expanded
             */
	        'togglerow'
	    );
        Sch.TreeGanttView.superclass.constructor.apply(this, arguments);
	},
	
	doRender : function(cs, rs, ds, startRow, colCount, stripe){
		var ts = this.templates, ct = ts.cell, rt = ts.row, last = colCount-1,
			tstyle = 'width:'+this.getTotalWidth()+';',
			lstyle = 'width:'+this.getLockedWidth()+';',
			buf = [], lbuf = [], cb, lcb, c, p = {}, rp = {}, r, events, processed_cnt = 0;
		for(var j = 0, len = rs.length; j < len; j++){
			r = rs[j]; cb = []; lcb = [];
			var rowIndex = (j+startRow);
			
			for(var i = 0; i < colCount; i++){
				c = cs[i];
				p.id = c.id;
				p.css = (i === 0 ? 'x-grid3-cell-first ' : (i == last ? 'x-grid3-cell-last ' : '')) +
                    (this.cm.config[i].cellCls ? ' ' + this.cm.config[i].cellCls : '');
				p.attr = p.cellAttr = '';
				
                p.value = c.renderer.call(c.scope || c, r.data[c.name], p, r, rowIndex, i, ds);
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
                '<div class="x-grid3-row {alt}" style="{tstyle} {display_style}">',
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
                    '<div class="{cls}">&#160;</div>',
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
            depth = store.getNodeDepth(record);
        
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
    
    //Prevent underlying EditorGridPanel from messing up scroll position after edit
    focusCell : Ext.ux.grid.LockingGridView.prototype.focusCell.createInterceptor(function(row, col, hscroll) {
        // EditorGridPanel performs a focusCell() after editing which messes up the scroll position
        return col >= this.cm.getLockedCount();
    })
});

// Adaptations for LockingGridView changes in Ext 3.3
if (Ext.getMajorVersion() >= 3 && Ext.getMinorVersion() >= 3) {
    Ext.override(Sch.TreeGanttView, {
	    refreshRow : function(record) {
            var store     = this.ds,
                colCount  = this.cm.getColumnCount(),
                columns   = this.getColumnData(),
                last      = colCount - 1,
                cls       = ['x-grid3-row'],
                rowParams = {
                    tstyle: String.format("width: {0};", this.getTotalWidth())
                },
                lockedRowParams = {
                    tstyle: String.format("width: {0};", this.getLockedWidth())
                },
                colBuffer = [],
                lockedColBuffer = [],
                cellTpl   = this.templates.cell,
                mastercellTpl   = this.templates.mastercell,
                rowIndex, row, lockedRow, column, meta, css, i;
            
            if (Ext.isNumber(record)) {
                rowIndex = record;
                record   = store.getAt(rowIndex);
            } else {
                rowIndex = store.indexOf(record);
            }
            
            if (!record || rowIndex < 0) {
                return;
            }
            
            for (i = 0; i < colCount; i++) {
                column = columns[i];
                
                if (i == 0) {
                    css = 'x-grid3-cell-first';
                } else {
                    css = (i == last) ? 'x-grid3-cell-last ' : '';
                }
                
                meta = {
                    id      : column.id,
                    style   : column.style,
                    css     : css,
                    attr    : "",
                    cellAttr: ""
                };
                
                meta.value = column.renderer.call(column.scope || column, record.data[column.name], meta, record, rowIndex, i, store);
                
                if (Ext.isEmpty(meta.value)) {
                    meta.value = '&#160;';
                }
                
                if (this.markDirty && record.dirty && typeof record.modified[column.name] != 'undefined') {
                    meta.css += ' x-grid3-dirty-cell';
                }
                
                if(column.locked){
                    if (column.id === this.grid.master_column_id) {
                        meta.treeui = this.renderCellTreeUI(record, store);
                        meta.css += store.isLeafNode(record) ? ' sch-gantt-leaf-cell' : ' sch-gantt-parent-cell';
                        lockedColBuffer[i] = mastercellTpl.apply(meta);
                    } else {
                        lockedColBuffer[i] = cellTpl.apply(meta);
                    }
                }else{
                    colBuffer[i] = cellTpl.apply(meta);
                }
            }
            
            row = this.getRow(rowIndex);
            row.className = '';
            lockedRow = this.getLockedRow(rowIndex);
            lockedRow.className = '';
            
            if (this.grid.stripeRows && ((rowIndex + 1) % 2 === 0)) {
                cls.push('x-grid3-row-alt');
            }
            
            if (this.getRowClass) {
                rowParams.cols = colCount;
                cls.push(this.getRowClass(record, rowIndex, rowParams, store));
            }
            
            // Unlocked rows
            this.fly(row).addClass(cls).setStyle(rowParams.tstyle);
            rowParams.cells = colBuffer.join("");
            row.innerHTML = this.templates.rowInner.apply(rowParams);
            
            // Locked rows
            this.fly(lockedRow).addClass(cls).setStyle(lockedRowParams.tstyle);
            lockedRowParams.cells = lockedColBuffer.join("");
            lockedRow.innerHTML = this.templates.rowInner.apply(lockedRowParams);
            lockedRow.rowIndex = rowIndex;
            
            this.fireEvent('rowupdated', this, rowIndex, record);
        }
    });
}

