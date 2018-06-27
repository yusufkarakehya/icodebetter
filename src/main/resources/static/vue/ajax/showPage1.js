var grd_1={
		render(h){
			return h('div',{class:"container-fluid", style:""},[h('div',{class:"row"},[h('div',{class:"col-12"},[h('card',{},[
					h('el-table',{style:"width: 100%", props:{stripe:!0, data:this.tableData, on:{rowDblclick:this.dblClick}}},
							grd_1.columns.map(function(cc){return h('el-table-column',{props:cc})}))
					])])])]); 
		},
	    methods: {
	      handleClick() {
	        console.log('click');
	      },
	      dblClick(row){
	    	  console.log(row);
	    	  alert(row)
	    	  
	      }
	    },
	    data() {
	      return {
	        tableData: []
	      }
	    }
	    ,mounted(){
	    	if(true)this.tableData=[{
		          date: '2016-05-03',
		          name: 'Tom',
		          state: 'California',
		          city: 'Los Angeles',
		          address: 'No. 189, Grove St, Los Angeles',
		          zip: 'CA 90036',
		          tag: 'Home'
		        }, {
		          date: '2016-05-02',
		          name: 'Tom',
		          state: 'California',
		          city: 'Los Angeles',
		          address: 'No. 189, Grove St, Los Angeles',
		          zip: 'CA 90036',
		          tag: 'Office'
		        }, {
		          date: '2016-05-04',
		          name: 'Tom',
		          state: 'California',
		          city: 'Los Angeles',
		          address: 'No. 189, Grove St, Los Angeles',
		          zip: 'CA 90036',
		          tag: 'Home'
		        }, {
		          date: '2016-05-01',
		          name: 'Tom',
		          state: 'California',
		          city: 'Los Angeles',
		          address: 'No. 189, Grove St, Los Angeles',
		          zip: 'CA 90036',
		          tag: 'Office'
		        }];
	    	
	    }
	  };
grd_1.columns=[{fixed:!0,prop:"date", label:"Date", sortable:!0,minWidth:150},
               {prop:"name",sortable:!0, label:"Name", width:120},
               {prop:"state", label:"State", sortable:!0, width:120},
               {prop:"city", label:"City", sortable:!0, width:120},
               {prop:"address", sortable:!0, label:"Address", width:300},
               {prop:"zip", label:"Zip", sortable:!0, width:120}];
return h(grd_1);
