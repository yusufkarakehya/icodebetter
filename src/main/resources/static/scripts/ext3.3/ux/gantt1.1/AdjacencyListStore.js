/*

Copyright (c) 2009, Maxim G. Bazhenov

All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

 Redistributions of source code must retain the above copyright notice, this 
 list of conditions and the following disclaimer.

 Redistributions in binary form must reproduce the above copyright notice, this 
 list of conditions and the following disclaimer in the documentation and/or 
 other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, 
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

Ext.ns('Ext.ux.maximgb.tg');

/**
 * @class Ext.ux.maximgb.tg.AdjacencyListStore
 * @extends Ext.ux.maximgb.tg.AbstractTreeStore
 * Tree store for adjacency list tree representation. For each record, you have to specify the id of its parent (see {@link #parent_id_field_name}) and you also have to indicate if the record is a leaf or not, see {@link Ext.ux.maximgb.tg.AbstractTreeStore#leaf_field_name leaf_field_name} property. More information about the adjacency list theory 
 * <a href="http://dev.mysql.com/tech-resources/articles/hierarchical-data.html">here</a>.
 * @constructor
 * @param {Object} config The object containing the configuration of this model.
 */
Ext.ux.maximgb.tg.AdjacencyListStore = Ext.extend(Ext.ux.maximgb.tg.AbstractTreeStore,
{
    /**
     * @cfg {String} parent_id_field_name Record parent id field name. Defaults to 'ParentId'
     */
    parent_id_field_name : 'ParentId',
    
    getRootNodes : function()
    {
        var pField = this.parent_id_field_name;
        return this.queryBy(function(r) { 
            return r.data[pField] === null || r.data[pField] === ""; 
        }).items;
    },
    
    getNodeDepth : function(rc)
    {
        return this.getNodeAncestors(rc).length;
    },
    
    getNodeParent : function(rc)
    {
        return this.getById(rc.get(this.parent_id_field_name));
    },
    
    // Modified, inlined version of "query" method for speed
    getNodeChildren : function(rc)
    {   
        var rs = [], 
            records = this.data.items,
            id = rc.id,
            pField = this.parent_id_field_name,
            r;
            
        for(var i = 0, len = records.length; i < len; i++){
            if(records[i].data[pField] === id){
                rs.push(records[i]);
            }
        }
        return rs;
    },
    
    addToNode : function(parent, child)
    {
        child.set(this.parent_id_field_name, parent.id);
        this.addSorted(child);
    },
    
    removeFromNode : function(parent, child)
    {
        this.remove(child);
    },
    
    hasChildNodes : function(rc)
    {
        return this.findExact(this.parent_id_field_name, rc.id) > 0;
    }
});

Ext.reg('Ext.ux.maximgb.tg.AdjacencyListStore', Ext.ux.maximgb.tg.AdjacencyListStore);