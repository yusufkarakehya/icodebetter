var routes = [
//	  {path: '/',content: ' iCodeBetter ',},
	  {path: '/home',async: function (routeTo, routeFrom, resolve, reject) {
		  	resolve({component:{
		  	    template: `<div class="page" data-name="home">
		  	      <!-- Top Navbar -->
		  	      <div class="navbar">
		  	        <div class="navbar-inner">
		  	          <div class="left">
		  	            <a href="#" class="link icon-only panel-open" data-panel="left">
		  	              <i class="icon f7-icons if-not-md">menu</i>
		  	              <i class="icon material-icons if-md">menu</i>
		  	            </a>
		  	          </div>
		  	          <div class="title">iCodeBetter</div>
		  	        </div>
		  	      </div>
		  	      <!-- Toolbar-->
		  	     
		  	      <!-- Scrollable page content-->
		  	      <div id="idx-page-content-home" class="page-content ptr-content">
		  	    	<div class="ptr-preloader"><div class="preloader"></div><div class="ptr-arrow"></div></div>
				  	  {{#each data}}
		                  <div key="{{dashId}}" class="card">
		                  	<div class="card-header">
		                  		{{name}} {{#if listId}}<a href="/showMList?_lid={{listId}}"><i class="icon f7-icons">arrow_right</i></a>{{/if}}
		                  	</div>
		                  	<div class="card-content">
		    					<div id="id-chart-{{dashId}}"></div>
		    				</div>
		                  </div>
				  	  {{/each}}
		  	      </div>
		  	    </div>`
		  	    ,methods:{reload:function(){
		  	    	  var self=this;
		  	          var data=this.data;
	  	              if(data.length){
  	                	  data.map((z)=>iwb.graph(z,'id-chart-'+z.dashId));
  	                	  if(self.ptr)self.ptr.done()
	  	              }
		  	    }},data:function(){
		  	        return {data:[]};
		  	        },on:{
		  	        	pageInit:function(){
			  	    	  var self=this;
			  	          iwb.request({url:'ajaxQueryData?_qid=6648',data:{xmobile_flag:1,sort:'mobile_tab_order'}, success:function(j){
			  	              if(j.data.length){
			  	                  self.$setState(j);
					  	    	  setTimeout(function(){
					  	    		  self.reload();
					  	    	  },200);

			  	              }
			  	          }});
			  	    	  setTimeout(function(){
			  	    		  self.ptr=iwb.app.ptr.get('#idx-page-content-home.ptr-content');
			  	    		  self.ptr.on('refresh',self.reload);
			  	    	  },100);
		  	        	}
		  	      }
		  	    
		  	  }
		  	});
		  			  
	  	}
	  },
	  {path: '/workspace',async: function (routeTo, routeFrom, resolve, reject) {
		  	resolve({component:{template:`<div class="page">
 {{#if backButton}}<div class="navbar">
 <div class="navbar-inner">
    <div class="left">
      <a href="#" class="link back">
        <i class="icon icon-back"></i>
        <span class="if-not-md">Back</span>
      </a>
    </div>
  </div>
</div>{{/if}}
<div class="page-content">
  <div class="block">
    <p>Welcome to iCodebetter Mobile Platform.</p>
  </div>
  <div class="icb-expandable-cards">
    <div class="card card-expandable" id="idx-card-new-project">
      <div class="card-content">
        <div class="bg-color-red" style="height: 200px">
          <div class="card-header text-color-white display-block" style="font-weight:500;">
            Add Project
            <br>
            <small style="opacity: 0.8;font-size:1rem;font-weight:400;">Manually or by scanning square code</small>
          </div>
          <a href="#" class="link card-close card-opened-fade-in color-white" style="position: absolute; right: 15px; top: 15px">
            <i class="icon f7-icons">close_round_fill</i>
          </a>
  		  <div class="block">
  			<p/> <br/> 
	        <p class="row">
	        <a href=# @click="clickSquareCode" class="col button button-fill color-black button-large ">Scan Square Code</a></p>
		  </div>
        </div>
        <div class="card-content-padding">
			<form class="list" id="idx-form-new-project">
			    <ul>
			        <li>
			            <div class="item-content item-input item-input-outline">
			                <div class="item-inner">
			                    <div class="item-title item-floating-label">Name</div>
			                    <div class="item-input-wrap">
			                        <input type="text" name="name" value="Default" placeholder="">
			                    </div>
			                </div>
			            </div>
			        </li>
			        <li>
			            <div class="item-content item-input item-input-outline">
			                <div class="item-inner">
			                    <div class="item-title item-floating-label">URL</div>
			                    <div class="item-input-wrap">
			                        <input type="text" name="url" value="http://" placeholder="">
			                    </div>
			                </div>
			            </div>
			        </li>
			        <li>
			            <div class="item-content item-input item-input-outline">
			                <div class="item-inner">
			                    <div class="item-title item-floating-label">Username</div>
			                    <div class="item-input-wrap">
			                        <input type="text" name="user_name" value="" placeholder="">
			                    </div>
			                </div>
			            </div>
			        </li>
			        <li>
			            <div class="item-content item-input item-input-outline">
			                <div class="item-inner">
			                    <div class="item-title item-floating-label">Password</div>
			                    <div class="item-input-wrap">
			                        <input type="password" name="pass_word" value="" placeholder="">
			                    </div>
			                </div>
			            </div>
			        </li>
			    </ul>
			    <div class="block">
			    <p/> <p/> <br/>
			        <p class="row">
			        <a href=# @click="clickTest(-1)" class="col button button-big button-fill button-raised color-green">Test</a>
			        <a href=# @click="clickNew" class="col button button-big button-fill button-raised color-blue">Save</a></p>
			    </div>
			</form>        
		</div>
      </div>
    </div>

    

{{#each projects}}
	<div class="card card-expandable" key="{{id}}" id="idx-card-project-{{id}}">
      <div class="card-content">
        <div class="bg-color-blue" style="height: 200px">
          <div class="card-header text-color-white display-block" style="font-weight:500;">
            {{name}}
            <br>
            <small style="opacity: 0.7;font-size:1rem;font-weight:400;">{{url}}</small><br/>
          </div>
          <a href="#" class="link card-close card-opened-fade-in color-white" style="position: absolute; right: 15px; top: 15px">
            <i class="icon f7-icons">close_round_fill</i>
          </a>
  		  <div class="block">
  			<p/> <br/> 
	        <p class="row">
	        <a href=# @click="clickConnect({{id}})" class="col button button-fill color-black button-large ">Connect</a></p>
		  </div>
        </div>
        <div class="card-content-padding">
			<form class="list" id="idx-form-project-{{id}}" data-pk="{{id}}">
			    <ul>
			        <li>
			            <div class="item-content item-input item-input-outline">
			                <div class="item-inner">
			                    <div class="item-title item-floating-label">Name</div>
			                    <div class="item-input-wrap">
			                        <input type="text" name="name" value="{{name}}" placeholder="">
			                    </div>
			                </div>
			            </div>
			        </li>
			        <li>
			            <div class="item-content item-input item-input-outline">
			                <div class="item-inner">
			                    <div class="item-title item-floating-label">URL</div>
			                    <div class="item-input-wrap">
			                        <input type="text" name="url" value="{{url}}" placeholder="">
			                    </div>
			                </div>
			            </div>
			        </li>
			        <li>
			            <div class="item-content item-input item-input-outline">
			                <div class="item-inner">
			                    <div class="item-title item-floating-label">Username</div>
			                    <div class="item-input-wrap">
			                        <input type="text" name="user_name" autocomplete="off" value="{{user_name}}" placeholder="">
			                    </div>
			                </div>
			            </div>
			        </li>
			        <li>
			            <div class="item-content item-input item-input-outline">
			                <div class="item-inner">
			                    <div class="item-title item-floating-label">Password</div>
			                    <div class="item-input-wrap">
			                        <input type="password" name="pass_word" autocomplete="off" value="{{pass_word}}" placeholder="">
			                    </div>
			                </div>
			            </div>
			        </li>
			    </ul>
			    <div class="block">
		  			<p/> <p/> <br/> 
			        <p class="row">
			        <a href=# @click="clickTest({{id}})" class="col button button-big button-fill button-raised color-green">Test</a>
			        <a href=# @click="clickSave({{id}})" class="col button button-big button-fill button-raised color-blue">Save</a></p>
		  			<p/> 
			        <p class="row"><a href=# @click="clickDelete({{id}})" class="col button button-big button-fill button-raised color-red">Delete</a></p>

			    </div>
			</form>        
		</div>
      </div>
    </div>
{{/each}}
    
  </div>
</div>
</div>
`, style:`
.card-expandable {
  height: 100px;
  box-shadow: 0 1px 10px #777
}
.icb-expandable-cards .card-header img{
	width:80px;position:absolute; border-radius:5px; right:40px; top:20px; opacity:.9;
}
.icb-expandable-cards .card-opened .card-header{

}
.icb-expandable-cards .card-opened .card-header img{
	display:none;
	width: 80%;
	overflow: hidden;
	position: relative;
	left: 10%;
}
`,
			data:function(){
				return {
					backButton: Object.assign(routeTo.query,routeTo.params).back||false,
					projects:[]
				}
			},

	        on:{pageInit:function(){
	        	var ls = window.localStorage;
				  if(ls){
					  var projects = ls.getItem('iwb-projects');
					  if(!projects)projects = "[]";
					  var p = JSON.parse(projects);
					  this.$setState({projects:p})
				  }
	        }},
	        
	        methods:{clickSquareCode:function(){
	        	alert('TODO');	        	  
	        },clickConnect:function(id){
	        	var self=this;
				  	var f = iwb.app.form.convertToData('#idx-form-project-'+id);
				  	alert(f.url)
		        	Framework7.request({
		                url: f.url + 'ajaxPing?d=1&c=1&.r=' + Math.random(),
		                method: 'POST', data: {},
		        // dataType: 'json',
		                success: function (d) {
		                	_scd=null;
		        			iwb.serverUrl=f.url;
		        			var ls = window.localStorage;
		        			ls.setItem('userName', f.user_name||'');
		        			ls.setItem('passWord', f.pass_name||'');			        			
		        			iwb.app.card.close('#idx-card-project-'+id);
		        			iwb.reLogin();
		        			
		                }, error: function (d) {
		        			iwb.app.toast.show({
		        				  text: 'Error Connecting. Check URL or Device Connection<br/>' + d ,closeTimeout: 3000
		        			});
		                }
		            });	       
	        },clickDelete:function(id){
				var self = this;
	        	var ls = window.localStorage;
				  var p = this.projects;
				  for(var qi=0;qi<p.length;qi++)if(id==1*p[qi].id){
					  iwb.app.dialog.confirm("Are you sure?", "Delete", function(){
						  p.splice(qi,1);
						  console.log('aha',qi,p);
						  ls.setItem('iwb-projects', JSON.stringify(p));
						  iwb.app.card.close('#idx-card-project-'+id);
						  setTimeout(function(){self.$setState({projects:p});},200);
					  });
					  break;
				  }
	        },clickTest:function(id){
	        	var f = iwb.app.form.convertToData(id==-1 ? '#idx-form-new-project':('#idx-form-project-'+id));
	        	if(!f.url){
					  iwb.app.dialog.alert('Error: Url is mandatory');
					  return;
				}

	        	Framework7.request({
	                url: f.url + 'ajaxPing?d=1&c=1&.r=' + Math.random(),
	                method: 'POST', data: {},
	        // dataType: 'json',
	                success: function (d) {
	        			iwb.app.toast.show({
	        				  text: 'Connection Done',closeTimeout: 2000
	        			});           	
	                }, error: function (d) {
	        			iwb.app.toast.show({
	        				  text: 'Error Connecting. Check URL or Device Connection<br/>' + d ,closeTimeout: 3000
	        			});
	                }
	            });
	        	
	        },clickSave:function(id){
	        	var ls = window.localStorage;
				  if(ls){
					  var f = iwb.app.form.convertToData('#idx-form-project-'+id);
					  if(!f.name || !f.url){
						  iwb.app.dialog.alert('Error: Name & Url is mandatory');
						  return;
					  }
					  var p = this.projects;
					  for(var qi=0;qi<p.length;qi++)if(id==1*p[qi].id){
						  id=1*p[qi].id;
						  f.id=id;
						  p[qi] = f;
						  ls.setItem('iwb-projects', JSON.stringify(p));
						  this.$setState({projects:p});
						  iwb.app.card.close('#idx-card-project-'+id);
					  }
					  
				  }
	        },clickNew:function(){
				  var ls = window.localStorage;
				  if(ls){
					  var f = iwb.app.form.convertToData('#idx-form-new-project');
					  if(!f.name || !f.url){
						  iwb.app.dialog.alert('Error: Name & Url is mandatory');
						  return;
					  }
					  var p = this.projects;
					  var id = 0;
					  for(var qi=0;qi<p.length;qi++)if(id<1*p[qi].id)id=1*p[qi].id;
					  f.id=id+1;
					  p.push(f);
					  ls.setItem('iwb-projects', JSON.stringify(p));
					  this.$setState({projects:p});
					  iwb.app.card.close('#idx-card-new-project');
					  
				  } else
					  iwb.app.dialog.alert('Error: No local Storage');
			}
	        }}});
		  },
	  },
	  {path: '/about',async: function (routeTo, routeFrom, resolve, reject) {
		  	resolve({component:{template:`<div class="page">
 <div class="navbar">
 <div class="navbar-inner">
    <div class="left">
      <a href="#" class="link back">
        <i class="icon icon-back"></i>
        <span class="if-not-md">Back</span>
      </a>
    </div>
  </div>
</div>
<div class="page-content">
  <div class="block">
    <p>About iCodebetter</p>
  </div>
</div>
</div>
`,
			data:function(){
				return {}
			},
	        on:{pageInit:function(){
	          
	        }},methods:{}
	        }});
		  },
	  },
	  {path:'/login',async: function (routeTo, routeFrom, resolve, reject) {
		  reject();
		  resolve({component:{template:`
			<div class="page" data-name="icb-login">
			  <div class="toolbar tabbar tabbar-labels toolbar-bottom">
			    <div class="toolbar-inner">
				  <a class="tab-link panel-close" href="/about">
			      	<i class="icon f7-icons if-not-md">info_round</i>
			        <i class="icon material-icons md-only">info</i>
			        <span class="tabbar-label">About</span>
			      </a>
			      
			      <a class="tab-link panel-close" href="/workspace">
			      	<i class="icon f7-icons if-not-md">list</i>
			        <i class="icon material-icons md-only">list</i>
			        <span class="tabbar-label">Workspace</span>
			      </a>
			    </div>
			  </div>  
			  <div class="page-content login-screen-content" id="icb-login-screen">
			  	<p>&nbsp;</p><p>&nbsp;</p><p>&nbsp;</p>
	            <div class="login-screen-title">Login</div>
	            <div class="list">
	              <ul>
	                <li class="item-content item-input">
	                  <div class="item-inner">
	                    <div class="item-title item-label">Username</div>
	                    <div class="item-input-wrap">
	                      <input type="text" name="username" placeholder="Your username">
	                    </div>
	                  </div>
	                </li>
	                <li class="item-content item-input">
	                  <div class="item-inner">
	                    <div class="item-title item-label">Password</div>
	                    <div class="item-input-wrap">
	                      <input type="password" name="password" placeholder="Your password">
	                    </div>
	                  </div>
	                </li>
	              </ul>
	            </div>
	            <div class="list">
	              <ul>
	                <li>
	                  <a href="#" @click="clickLogin" class="item-link list-button login-button">Sign In</a>
	                </li>
	              </ul>
	              <div class="block-footer">iCodeBetter (c) 2019</div>
	            </div>
	          </div></div>`, 
          on:{pageInit:function(){
			  _scd = null;
			  var ls = window.localStorage;
			  if(ls){
				  var reUserName = ls.getItem("userName");
				  if(reUserName)$$('#icb-login-screen [name="username"]').val(reUserName);
			  }
          
        }},methods:{clickLogin:function(){
        	  var username = $$('#icb-login-screen [name="username"]').val();
        	  var password = $$('#icb-login-screen [name="password"]').val();
        	  if(username && password){
        		  iwb.app.preloader.show();
        		  Framework7.request({url:iwb.serverUrl + 'ajaxAuthenticateUser?d=1&c=1&_mobile=0&customizationId=0&locale=tr',
    		    	data:{userName: username, passWord: password},dataType: 'json', 
    		    	success:function(j){
    		    		iwb.app.preloader.hide();
    				if(j.success){
    		    		if(!j.session){ // TODO. eger session gelmediyse,
										// sikinti
    		    			iwb.app.toast.show({text:j.errorMsg || j.error || 'Error ', closeTimeout: 3000});
    		    			return;
    		    		}
    					  var ls = window.localStorage;
    					  if(ls){
    						  ls.setItem("userName", username);
    						  ls.setItem("passWord", password);
    					  }
		    			// iwb.app.views.main.router.clearPreviousHistory();
		    			iwb.home = false;
			    		_scd=j.session;
			    		setTimeout(function(){
// iwb.goHome();
				    		iwb.prepareMainMenu();
			    		}, 100);
    		    	} else {
		    			iwb.app.toast.show({text:j.errorMsg || j.error || 'Error ', closeTimeout: 3000});
    		    	}
    		    },error:function(){
    		    	iwb.app.preloader.hide();
    		    }});
        	  }
        }}}});
	  }
  },
  {path: '/confirmDeleteRecord',async: function (routeTo, routeFrom, resolve, reject) {
	  reject();
	  iwb.app.dialog.confirm("Are you sure?", "Delete", function(){
		  iwb.submit(false,Object.assign(routeTo.query,routeTo.params), function(d){
			  iwb.currentLoader(0);
		      });
		  });
    }},
  
	  {
	    path: '/showMList',
	    async: function (routeTo, routeFrom, resolve, reject) {
		      iwb.request({url:'showMList',preloader:!0,data:Object.assign(routeTo.query,routeTo.params), success:function(d){
		    	  resolve({component:d})
		      }});
	    }
	  },
	  {
	    path: '/showMForm',
	    async: function (routeTo, routeFrom, resolve, reject) {
		      iwb.request({url:'showMForm',preloader:!0,data:Object.assign(routeTo.query,routeTo.params), success:function(d){
		    	  d.parentLoader=iwb.currentLoader;
		    	  resolve({component:d});
		      }});
	    },
	  },
	  {
	    path: '/showMPage',
	    async: function (routeTo, routeFrom, resolve, reject) {
		      iwb.request({url:'showMPage',preloader:!0,data:Object.assign(routeTo.query,routeTo.params), success:function(d){
				resolve({component:d});
		      }});
	    },
	  },
	  // Default route (404 page). MUST BE THE LAST
	  {
	    path: '(.*)',
	    url: '/f7/pages/404.htm'
	  },
	];



var _scd=null;
var iwb={};

iwb.serverUrl='/app/';
iwb.debug=true;

iwb.submit=function(idForm, baseParams, callback){
	iwb.request({url:'ajaxPostForm',preloader:!0,data:Object.assign(baseParams, idForm ? iwb.app.form.convertToData(idForm):{}),method:'POST',success:function(j){
		if(j.msgs)for(var qi=0;qi<j.msgs.length && qi<5;qi++){
			iwb.app.toast.show({
				  text: j.msgs[qi],closeButton: true
				});
		} else iwb.app.toast.create({
			  text: 'done',
			  closeTimeout: 2000,
			});
		if(callback)callback(j);
	}});
}

iwb.request=function(cfg){
	if(!_scd){
		iwb.checkSession(cfg);
	} else if(cfg){
		if(cfg.url){
			if(cfg.preloader)iwb.app.preloader.show();

			return iwb.app.request({
	            url: iwb.serverUrl + cfg.url,
	            method: cfg.method||'GET', data: cfg.data || {},
// dataType: cfg.dataType || 'json',
	            success: function (d) {
	            	if(cfg.preloader)iwb.app.preloader.hide();
	            	var j = {};
	            	try{
	            		j = eval('('+d+')');
	            	}catch(e){
	            		if(iwb.debug && confirm('iwb.request.Response Eval Exception. Throw?'))throw e;
	            		return;
	            	}

            		if(j.success)try{
            			if(cfg.success)cfg.success(j, cfg);
            		} catch(e){
            			if(iwb.debug && confirm('iwb.request.Success Callback Exception. Throw?'))throw e;
	            	} else if(cfg.error)try{
                		cfg.error(j, cfg);
            		} catch(e){
            			if(iwb.debug && confirm('iwb.request.Error Callback Exception. Throw?'))throw e;
            		} else switch(j.errorType){
	    			case	'session':
	    				if(cfg.error)cfg.error();
	    				iwb.reLogin(cfg);
	    				return;
	    			case	'confirm':
	    				iwb.app.dialog.confirm(j.error,function(){var ncfg=Object.assign({},cfg);ncfg.data=Object.assign(ncfg.data,{confirmId:j.objectId});iwb.request(ncfg);}, cfg.error);
	    				return;
	    			case	'validation':
	    				if(cfg.error)cfg.error();
	    				var s='';// <i class="icon material-icons color-red"
									// style="font-size:16px">error</i>
									// <b>Validation Errors</b><br/>';
	    				if(j.errors)for(var qi=0;qi<j.errors.length && qi<3;qi++){
	    					s+='<i class="icon material-icons color-red" style="font-size:18px">error</i> &nbsp; <span style="color:orange;font-weight:bosld">'+ j.errors[qi].dsc+'</span> &nbsp; ' + j.errors[qi].msg +'<br/>';
	    				} else s=j.error;
	    				iwb.app.toast.create({position:'top',closeTimeout:6000,text:s}).open();
	    				
	    				return;
	    			default:
	    				console.log('uknown error',j)
	    				if(cfg.error)cfg.error(j, cfg);
	    				else iwb.app.toast.create({position:'top',closeButton:!0,text:j.error||'Unknown Error'}).open();
	    				return;
	    			}
	            },
	            error:function (j,err) {
	            	if(cfg.preloader)iwb.app.preloader.hide();
	            	if(iwb.debug && err){
	               		iwb.app.dialog.alert(err + ': ' + cfg.url);
	            	}
	            	if(cfg.failure)cfg.failure(j, cfg, err); 
	            	else if(cfg.error)cfg.error(j, cfg, err);
	            }
	        });
		}
	}
	return false;
}

iwb.orderList=function(j){
	var bs=[{text:'Sorting...', label: true}], od=j.$options.props.orderNames;
	for(var qi=0;qi<od.length;qi++){
		var mm= od[qi];
		var bq= j.sort && j.sort==mm.id;
		
		bs.push({text: (bq ? ('<b>'+mm.dsc+' ('+(j.dir=='ASC' ? 'Ascending':'Descending')+')</b>'):mm.dsc), onClick: function (d,e) {
			if(iwb.debug){console.log('sort-action-click');console.log(d);console.log(e);}
			if(!e.srcElement || !e.srcElement.innerText){
				iwb.app.dialog.alert('error1');return;
			}
			var t=e.srcElement.innerText, qq=false;
			for(var ji=0;ji<od.length;ji++)if(t.indexOf(od[ji].dsc)==0){
				qq=od[ji];
				break;
			}
			if(!qq){
				iwb.app.dialog.alert('error2');return;
			}
			var newSort=j.sort, newDir='ASC';
			if(j.sort && j.sort==qq.id){
				newDir= j.dir=='ASC' ? 'DESC':'ASC';
			} else {
				newSort=qq.id;
			}
			j.$setState({sort:newSort, dir:newDir, data:[], browseInfo:{startRow:0}});
			setTimeout(function(){j.load(0)}, 100)
           }
		});
	}
	iwb.app.actions.create({ buttons: bs}).open();
	
}

function recMenu(r, lvl){
	if(!r || !r.length)return '';
	var s='';
	if(!lvl)lvl=0;
	for(var qi=0;qi<r.length;qi++)if(r[qi].children){// submenu
														// style="font-size:
														// 13px;color:green;"
		s+='<li class="accordion-item iwb-menu iwb-menu-folder"><a href="#" class="item-link item-content" ><div class="item-inner">';
// if(r[qi].icon)s+='<div class="item-media"><i class="f7-icons">'+r[qi].icon+'</i></div>';
		s+='<div class="item-title">'+r[qi].text+'</div></div></a><div class="accordion-item-content iwb-menu-url">'+
		'<div class="list accordion-list"><ul>'+recMenu(r[qi].children, lvl+1)+'</ul></div></div>';
	} else {
		var href=r[qi].href;
		s+='<li class="accordion-item iwb-menu"><a href="/'+href+'"'+ (href=='#' ? (' id="'+r[qi].id+'"'):'') +' class="item-content item-link panel-close'+(!lvl ? '  iwb-menu-murl':'')+'"><div class="item-inner">';
		s+='<div class="item-title">';
		if(lvl && r[qi].icon)s+='<i class="f7-icons iwb-mmenu-icon">'+(r[qi].icon||'star')+'</i>&nbsp; ';
// else if(lvl)for(var ji=0;ji<lvl;ji++)s+=' &nbsp;';
		s+=r[qi].text+'</div></div></a></li>';
	}
	return s;	
}

iwb.getPk=function(pk){
	for(var k in pk)if(k!='customizationId' && k!='projectId' && k!='tenantId')return pk[k];
	return -1;
}

iwb.formPhotoMenu=function(j){
	var buttons=[];
	// if(navigator.camera)
	if(j.fileAttachCount)buttons.push([{ text: 'Photo Gallery (' + (j.fileAttachCount)+')' ,
	   onClick: function () {
		   iwb.photoBrowser(j.crudTableId, j.tmpId || iwb.getPk(j.pk));
	   }}]);
	buttons.push([{text: 'Camera', onClick: function () {
        	   iwb.takePhoto(j.crudTableId, j.tmpId || iwb.getPk(j.pk), j);
           }
       },{text: 'Gallery', onClick: function () {
        	   iwb.takePhoto(j.crudTableId, j.tmpId || iwb.getPk(j.pk), j, true);
           }
       }
   ]);
	iwb.app.actions.create({ buttons: buttons}).open();
}

iwb.cameraClearCache=function() {
	if(navigator.camera)navigator.camera.cleanup();
}
iwb.photoFail=function(message) {
	iwb.app.preloader.hide();
	iwb.app.dialog.alert('Photo Upload Error: ' + message);
}

iwb.takePhoto=function(tid,tpk, jsonX, fromAlbum, profilePictureFlag){
	if(navigator.camera){
		var options = Object.assign({ quality: 50, destinationType: Camera.DestinationType.FILE_URI}, jsonX.cameraOptions || {});
		if(fromAlbum)options.sourceType=Camera.PictureSourceType.SAVEDPHOTOALBUM;
		navigator.camera.getPicture(function(fileURI) {
			 	var win = function (r) {
			 		iwb.app.preloader.hide();
				 	iwb.cameraClearCache();
				 	if(jsonX.photoSuccess && jsonX.photoSuccess(r)===false)return;
				 	iwb.app.toast.show({buttonClose:!0,position:'top',text: 'Photo Uploaded'});
				 	var pc=(jsonX.fileAttachCount||0)+1;
					var bd=$$('#idx-photo-badge-'+jsonX.formId);
					if(bd.length){
						if(pc>1)bd.html(pc);
						else bd.show();
					}
			    }
			 
			    var fail = function (error) {
			    	iwb.app.preloader.hide();
				 	iwb.cameraClearCache();
				 	iwb.app.dialog.alert('Unknown error!' + error);
			    }
			 
			    var params = new FileUploadOptions();
			    params.fileKey = "file";
			    params.fileName = fileURI.substr(fileURI.lastIndexOf('/') + 1);
			    params.mimeType = "image/jpeg";
			    params.params = {table_id:tid, table_pk:tpk, profilePictureFlag:profilePictureFlag||0, file_type_id:-997}; 
			    // if we need to send
																															// parameters
																															// to
																															// the
																															// server
																															// request
			    var ft = new FileTransfer();
			    iwb.app.preloader.show();
			    ft.upload(fileURI, encodeURI(iwb.serverUrl + 'upload.form'), win, fail, params);
		}, iwb.photoFail, options);
	} else iwb.app.dialog.alert('Camera Not Defined');
}

iwb.photoBrowser=function(tid, pk){
	iwb.request({url:'ajaxQueryData?_qid=806',data:{_tableId:tid,_tablePk:pk}, success:function(j){
		if(j.data && j.data.length){
			var photos=[];
			for(var qi=0;qi<j.data.length;qi++){
				var s = j.data[qi].dsc.toLowerCase();
				if(s.endsWith('.png') || s.endsWith('.jpg') || s.endsWith('.jpeg'))photos.push('sf/'+j.data[qi].dsc+'?_fai='+j.data[qi].id+'&.r='+ Math.random());
			}
			if(photos.length){
				var p = iwb.app.photoBrowser.create({photos:photos, theme:'dark'});
				p.open();
			} else iwb.app.dialog.alert('no photo found')
		}
	}});
}

iwb.autoCompleteJson=function(postUrl){
	return function(query, render){
		if (query.length === 0) {
	      render([]);
	      return;
	    }
		iwb.request({url:'ajaxQueryData?_qid='+postUrl, success:function(j){
			render(j.data);
		}});
	}
}
// Dom7
var $$ = Dom7;



iwb.currentLoader=null;



iwb.showRecordMenu=function(json3, targetEl){
	var tg=$$(json3._event.target);
	iwb.currentLoader=json3._this.load;
	var pk = tg.data('pk');
	if(!pk){
		tg = tg.parents('a');
		if(tg.length)pk = tg.data('pk');
	}
	if(!pk)return;
	var lnk =[], href=false;
	if(json3.crudFlags){
		if(json3.crudFlags.edit){
			href='/showMForm?a=1&_fid='+json3.crudFormId+'&'+json3.pkName+'='+pk+'&.r='+Math.random();
			lnk.push('<li><a href="'+href+'" class="item-link item-content popover-close"><div class="item-inner" style="background-image:none;"><div class="item-title"><i class="f7-icons" style="font-size: 18px;color: #027eff;">compose</i> &nbsp; Update</div></div></a></li>');
		}
		if(json3.crudFlags.remove){
			href='/confirmDeleteRecord?a=3&_fid='+json3.crudFormId+'&'+json3.pkName+'='+pk+'&.r='+Math.random();
			lnk.push('<li><a href="'+href+'" class="item-link item-content popover-close"><div class="item-inner" style="background-image:none;color:red;"><div class="item-title"><i class="f7-icons" style="font-size: 18px;">delete_round</i> &nbsp; Delete</div></div></a></li>');
		}
	}
	
	if(json3.recordButtons)for(var qi=0;qi<json3.recordButtons.length;qi++){
		var bt=json3.recordButtons[qi];
		var xhref=(bt.href ? bt.href+pk+'&.r='+Math.random() : '#'), e1='', e2='';
		if(bt.href)href=xhref;
		else {
			e1=' iwb-ab-'+json3.listId;
			e2=' iwb-key="'+qi+'"';
		}
		var s='<li><a href="'+xhref+'" class="item-link item-content popover-close'+e1+'"'+e2+'><div class="item-inner"><div class="item-title">';
		if(bt.icon)s+='<i class="f7-icons" style="font-size: 17px;color: #027eff;">'+bt.icon+'</i> &nbsp '; 
		s+=bt.text+'</div>';
		if(bt.badge)s+='<div class="item-after">'+bt.badge+'</div>';
		s+='</div></a></li>';
		lnk.push(s);
	}
	if(lnk.length){
		if(lnk.length>1){
			var p=iwb.app.popover.create({content:'<div class="popover"><div class="popover-inner"><div class="list"><ul>'+lnk.join('')+'</ul></div></div></div>', targetEl:targetEl});
			p.open();
		} else  {
			if(href)iwb.app.router.navigate(href);
		}
	}
}

iwb.graph = function(dg, gid) {
	  var newStat = 1 * dg.funcTip ? dg.funcFields : "";
	  var params = {};
	  if (newStat) params._ffids = newStat;
	  if (1 * dg.graphTip >= 5) params._sfid = dg.stackedFieldId;
      var series=[], labels=[], lookUps=[], chart =null;

	  iwb.request({
	    url:
	      (dg.query? "ajaxQueryData4Stat?_gid=":"ajaxQueryData4StatTree?_gid=") +
	      dg.gridId +
	      "&_stat=" +
	      dg.funcTip +
	      "&_qfid=" +
	      dg.groupBy +
	      "&_dtt=" +
	      dg.dtTip,
	    data: Object.assign(params, dg.queryParams),
	    success: function(j) {
			var d= j.data;
			if(!d || !d.length)return;
	        
			switch (1 * dg.graphTip) {
	        case 6: //stacked area
	        case 5: //stacked column
	        	var l= j.lookUp;
	        	for(var k in l)lookUps.push(k);
	            if(!lookUps.length)return;
	            d.map((z)=>{
	                var data=[];
	                lookUps.map((y)=>data.push(1*(z['xres_'+y]||0)));
	                series.push({name:z.dsc, data:data});
	            });
	            lookUps.map((y)=>labels.push(l[y]||'-'));

	            options = {
	                chart: {
	                	id:'apex-'+gid,
	                    height: 40*d.length+20,
	                    type: 'bar',
	                    stacked: true,
	                    toolbar: {show: false}
	                },
	                plotOptions: {
	                    bar: {horizontal: true},
	                    
	                },
	                series: series,
//	                title: {text: dg.name},
	                xaxis: {
	                    categories: labels,
	                },
	                yaxis: {
	                    title: {
	                        text: undefined
	                    },                
	                }
	            }
	        	break;
	        case 3://pie
	            d.map((z)=>{
	                series.push(1*z.xres);
	                labels.push(z.dsc||'-');
	            });
	            var options = {
//	                title: {text: dg.name},
	                chart: {id:'apex-'+gid, type: 'donut', toolbar: {show: false}},
	                series: series, labels: labels, legend: dg.legend ? {position:'bottom'} : false
	                ,dataLabels: dg.legend ? {}:{formatter: function (val, opts) {return labels[opts.seriesIndex] + ' - ' + fmtDecimal(val);}}
	            }

	        	break;
	        case 1://column
	        case 2://area
	        	d.map((z)=>{
	                series.push(1*z.xres);
	                labels.push(z.dsc);
	            });
	            options = {
//	                title: {text: dg.name},
	                chart: {
	                	id:'apex-'+gid,
	                    height:40*d.length+20,
	                    type: 1 * dg.graphTip==1?'bar':'area',
	                    toolbar: {show: false}
	                },
	                plotOptions: {
	                    bar: {
	                        horizontal: 1 * dg.graphTip==1 //d.length>5
	                    }
	                },
	                dataLabels:  1 * dg.graphTip==1 ? {
	                    enabled: true,
	                    textAnchor: 'start',
	                    style: {
	                        colors: ['#fff']
	                    },
	                    formatter: function(val, opt) {
	                        return opt.w.globals.labels[opt.dataPointIndex] + ":  " + val
	                    },
	                    offsetX: 0,
	                    dropShadow: {
	                      enabled: true
	                    }
	                }:{},
	                series: [{
	                    data: series
	                }],
	                xaxis: {
	                    categories: labels,
	                },
	                yaxis: {labels: {show: false}},
	            }
	        	break;
			}

			if(options){
	            chart = new ApexCharts(
	                document.getElementById(gid),
	                options
	            );
	            chart.render();
			}
			
	    }
	  });
}


var daysOfTheWeek=['Sunday', 'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday'];
iwb.fmtDateAgo=function(dt){
	if(!dt)return '';
	var tnow = new Date().getTime();
	var dt2=dt.toDate("dd/mm/yyyy hh:ii:ss");
	var t = dt2.getTime();
	if(t+30*1000>tnow)return 'Az Önce';// 5 sn
	if(t+2*60*1000>tnow)return 'Bir Dakika Önce';// 1 dka
	if(t+60*60*1000>tnow)return Math.round((tnow-t)/(60*1000)) + ' Dakika Önce';
	if(t+24*60*60*1000>tnow)return Math.round((tnow-t)/(60*60*1000)) + ' Saat Önce';
	if(t+2*24*60*60*1000>tnow)return 'Dün';
	if(t+7*24*60*60*1000>tnow)return daysOfTheWeek[dt2.getDay()];// 5dka
	return dt.substr(0,10);
}

iwb.fmtDateAgo2=function(dt){
	if(!dt)return '';
	var tnow = new Date().getTime();
	var dt2=dt.toDate("dd/mm/yyyy hh:ii:ss");
	var t = dt2.getTime();
	if(t+24*60*60*1000>tnow)return dt.substr(11,5);
	return dt.substr(0,10);
}

function fmtDecimal(value,digit){
	if(!value)return '0';
	if(!digit)digit=2;	
	var result = Math.round(value*Math.pow(10,digit))/Math.pow(10,digit)+'';
	var s=1*result<0?1:0;
	var x=result.split('.');
	var x1=x[0],x2=x[1];
	for(var i=x1.length-3;i>s;i-=3)x1=x1.substr(0,i)+('.')+x1.substr(i);
	if(x2 && x2>0) return x1+(',')+x2;
	return x1;	
}

iwb.fmtDistance=function(d){
	if(!d || d<0)return '';
	if(d<1000)return d + ' m';
	d = d/1000;
	return fmtDecimal(d,1)+ ' km';
}
iwb.combo2combo = function(prt, cmb, fnc, action){
	$$(prt).on('change',function(){
		if(iwb.debug){console.log(prt+':event:change');};
		var params = fnc($$(prt).val());
		if(params){
			$$(cmb.replace('idx-','id-')).show();
			iwb.loadCombo(cmb, params);
		} else {
			$$(cmb).find('option').remove();// temizle once
			if(true || params===false)$$(cmb.replace('idx-','id-')).hide();
		}
	});
	$$(prt).trigger('change');
}

iwb.loadCombo = function(cmb, params){
	var ctrl = $$(cmb);

	var selected=ctrl && ctrl.length ? ctrl.data('value'):'';

	ctrl.find('option').remove();// temizle once
	iwb.app.smartSelect.destroy(cmb);
	iwb.request({url:'ajaxQueryData', data:params, success:function(j){
		var data=j.data,res=[], s='';
		
		for(var qi=0;qi<data.length;qi++){
			s+='<option value="'+data[qi].id+'"'+(data[qi].id==selected ? ' selected':'')+'>'+data[qi].dsc+'</option>';
			if(data[qi].id==selected)res.push(data[qi].dsc);
		}
		ctrl.append(s);
		// console.log('as',selected, s);
		iwb.app.smartSelect.create(cmb);
		// $$(cmb.replace('idx-','id-')).find('.item-after').text(res.join(',
		// '));
		// $$(cmb.replace('idx-','id-')).show();
	}});
}


Template7.registerHelper('ago', iwb.fmtDateAgo);
Template7.registerHelper('ago2', iwb.fmtDateAgo2);
Template7.registerHelper('dst', iwb.fmtDistance);
Template7.registerHelper('iwb', function (key){
	return iwb[key];
});

Template7.registerHelper('scd', function (key){
	return _scd[key];
});

String.prototype.toDate = function(format){
  var normalized      = this.replace(/[^a-zA-Z0-9]/g, '-');
  var normalizedFormat= format.toLowerCase().replace(/[^a-zA-Z0-9]/g, '-');
  var formatItems     = normalizedFormat.split('-');
  var dateItems       = normalized.split('-');

  var monthIndex  = formatItems.indexOf("mm");
  var dayIndex    = formatItems.indexOf("dd");
  var yearIndex   = formatItems.indexOf("yyyy");
  var hourIndex     = formatItems.indexOf("hh");
  var minutesIndex  = formatItems.indexOf("ii");
  var secondsIndex  = formatItems.indexOf("ss");

  var today = new Date();

  var year  = yearIndex>-1  ? dateItems[yearIndex]    : today.getFullYear();
  var month = monthIndex>-1 ? dateItems[monthIndex]-1 : today.getMonth()-1;
  var day   = dayIndex>-1   ? dateItems[dayIndex]     : today.getDate();

  var hour    = hourIndex>-1      ? dateItems[hourIndex]    : today.getHours();
  var minute  = minutesIndex>-1   ? dateItems[minutesIndex] : today.getMinutes();
  var second  = secondsIndex>-1   ? dateItems[secondsIndex] : today.getSeconds();

  return new Date(year,month,day,hour,minute,second);
};

iwb.prepareMainMenu=function(){
	iwb.request({url:'ajaxQueryData?_qid=1487&.r='+Math.random(),dataType:'text',data:{_json:1,xuser_tip:typeof xuserTip!='undefined' && xuserTip ? xuserTip:0}, success:function(d){
		if(!d.data || !d.data.length){//no menu
			iwb.openLoginScreen();
			iwb.app.toast.create({position:'top',closeTimeout: 3000, text:'No menu defined for this app'}).open();
			return;
		}
		$$('#idx-main-menu').html(recMenu(d.data));
		iwb.goHome();
	}}); 
}
iwb.home=false;
iwb.goHome=function(){
	iwb.app.views.main.router.navigate('/home');
	iwb.home=!0;
}

iwb.openLoginScreen=function(){
	iwb.app.views.main.router.navigate('/login');
}

iwb.reLogin=function(afterCfg){
	var ls = window.localStorage;
	if(ls){
		var reUserName = ls.getItem("userName");
		var rePassWord = ls.getItem("passWord"); 
		if(reUserName && rePassWord){
			iwb.app.preloader.show();
			Framework7.request({url:iwb.serverUrl + 'ajaxAuthenticateUser?d=1&c=1&_mobile=0&_mobile_device_id='+iwb.deviceId+'&customizationId=0&locale=tr',
		    	data:{userName:reUserName, passWord:rePassWord},dataType: 'json', 
		    	success:function(j){
		    		iwb.app.preloader.hide();
					if(j.success){
			    		if(!j.session){ // TODO. eger session gelmediyse,
										// sikinti
			    			iwb.openLoginScreen();
			    			return;
			    		}
			    		if(!_scd || _scd.userId!=j.session.userId){// TODO
																	// burda
																	// butun
																	// ekranlar
																	// gemizlenecek
			    			iwb.app.views.main.router.clearPreviousHistory();
			    			iwb.home = false;
				    		_scd=j.session;
	            			iwb.goHome();
			    		} else {
				    		_scd=j.session;
		            		if(afterCfg)iwb.request(afterCfg);// tekrar cagir
		            		else iwb.goHome();
			    		}
			    	} else {
		    			_scd = null;
		    			iwb.openLoginScreen();
	        			return;
			    	}
		    }, error:function(){
      		  iwb.app.preloader.hide();
      		  iwb.app.toast.show({
				  text: 'Error Connecting ' + d ,closeButton: true, closeButtonText: 'Try Again'
					  , on:{
						  close:function(){
							  iwb.reLogin(afterCfg);
						  }
					  }
				});      		  
		    }});
			return;
		}
	}
	iwb.openLoginScreen();
}


iwb.checkSession=function(afterCfg){
    Framework7.request({
        url: iwb.serverUrl + 'ajaxPing?d=1&c=1&.r=' + Math.random(),
        method: 'POST', data: {},
// dataType: 'json',
        success: function (d) {
        	_scd = null;
        	var j = eval('('+d+')');
        	if(j.success && j.session){
        		_scd=j.session;
        		if(afterCfg)iwb.request(afterCfg);
        	} else 
        		iwb.reLogin(afterCfg);
        }, error: function (d) {
			iwb.app.toast.show({
				  text: 'Error Connecting ' + d ,closeButton: true, closeButtonText: 'Try Again'
				  , on:{
					  close:function(){
						  iwb.checkSession(afterCfg);
					  }
				  }
			});
        }
    });
}
// Framework7 App main instance
iwb.app = new Framework7({
  root: '#app', // App root element
  id: 'io.framework7.iwb', // App bundle ID
  name: 'iCodeBetter F7', // App name
  theme: 'auto',// 'auto', // Automatic theme detection
  // App root data
  data: function () {
    return {
    };
  },
  dialog: {
	    // set default title for all dialog shortcuts
	    title: 'iCodeBetter',
  },
  // App routes
  routes: routes,
  methods:{
	test:function(){
		alert('test')
	}  
  },
  on: {
    init: function () {
        var f7 = this;

      if (f7.device.cordova) {
        // Init cordova APIs (see cordova-app.js)
        cordovaApp.init(f7);
      }
	  	$$('#idx-reload').on('click', function(){
			var fd=iwb.app.form.convertToData('#idx-reload-form');
			iwb.currentLoader(0,null,fd);
		});
	  	
    }
  }
});
// Init/Create main view
var mainView = iwb.app.views.create('.view-main');

iwb.checkWorkspace=function(){
	
}
iwb.prepareMainMenu();
