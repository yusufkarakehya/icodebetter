var routes = [
	  {
		    path: '/',
		    url: './showPage?_tid=4296',
		  },
		  {
		    path: '/showMList',
		    async: function (routeTo, routeFrom, resolve, reject) {
			      // Router instance
			      var router = this;

			      // App instance
			      var app = router.app;
			      iwb.request({url:'showMList',data:Object.assign(routeTo.query,routeTo.params), success:function(d){
					var j = eval('('+d+')');
					resolve({component:j})
			    	  
			      }})
			      return;
		    }
			  
		  },
		  
		  {
		    path: '/showMForm',
		    async: function (routeTo, routeFrom, resolve, reject) {
		      // Router instance
		      var router = this;

		      // App instance
		      var app = router.app;
		      console.log(routeTo.query);
alert('hoho')
resolve({})
		      return;
		      // Show Preloader
		      iwb.app.preloader.show();

		      // User ID from request
		      var userId = routeTo.params.userId;

		      // Simulate Ajax Request
		      setTimeout(function () {
		        // We got user data from request
		        var user = {
		          firstName: 'Vladimir',
		          lastName: 'Kharlampidi',
		          about: 'Hello, i am creator of Framework7! Hope you like it!',
		          links: [
		            {
		              title: 'Framework7 Website',
		              url: 'http://framework7.io',
		            },
		            {
		              title: 'Framework7 Forum',
		              url: 'http://forum.framework7.io',
		            },
		          ]
		        };
		        // Hide Preloader
		        iwb.app.preloader.hide();

		        // Resolve route to load page
		        resolve(
		          {
		            componentUrl: './pages/request-and-load.html',
		          },
		          {
		            context: {
		              user: user,
		            }
		          }
		        );
		      }, 1000);
		    },
		  },
		  // Default route (404 page). MUST BE THE LAST
		  {
		    path: '(.*)',
		    url: '/f7/pages/404.htm',
		  },
		];

var _scd={};
var iwb={};

iwb.serverUrl='';
iwb.debug=true;

iwb.request=function(cfg){
	if(iwb.debug)console.log('iwb.request: ' + (cfg ? cfg.url:''));
	if(!_scd){
		iwb.checkSession(cfg);
	} else if(cfg){
		if(cfg.url){
			return iwb.app.request({
	            url: iwb.serverUrl + cfg.url,
	            method: 'GET', data: cfg.data || {},
//	            dataType: cfg.dataType || 'json',
	            success: function (j) {
	            	if(iwb.debug){console.log('ajax-success: '+cfg.url);}
            		if(cfg.success)try{
            			cfg.success(j, cfg);
            		} catch(e){
            			if(iwb.debug && confirm('iwb.request.success Expception. Throw?'))throw e;
	            	} else if(cfg.error)try{
                		cfg.error(j, cfg);
            		} catch(e){
            			if(iwb.debug && confirm('iwb.request.success Expception. Throw?'))throw e;
            		} else switch(j.errorType){
	    			case	'session':
	    				iwb.reLogin(cfg);
	    				return;
	    			case	'validation':
	    				var s='';
	    				if(j.errors)for(var qi=0;qi<j.errors.length && qi<3;qi++){
	    					s+=j.errors[qi].dsc+': ' + j.errors[qi].msg +'<br/>';
	    				} else s=j.error;
	    				iwb.app.dialog.alert(iwb.strTrim(s, 200), j.errorType || 'iWB');
	    				return;
	    			default:
	    				if(cfg.error)cfg.error(j, cfg);
	    				else iwb.showResponseError(j);
	    				return;
	    			}
	            },
	            error:function (j,err) {
	            	if(iwb.debug && err){
	               		iwb.app.dialog.alert(err + ': ' + cfg.url);
	            	}
	            	if(cfg.failure)cfg.failure(j, cfg, err);
	            }
	        });
		} else if(cfg.callback)cfg.callback();
	}
	return false;
}


function recMenu(r, lvl){
	if(!r || !r.length)return '';
	var s='';
	if(!lvl)lvl=0;
	for(var qi=0;qi<r.length;qi++)if(r[qi].children){// submenu
														// style="font-size:
														// 13px;color:green;"
		s+='<li class="accordion-item iwb-menu iwb-menu-folder"><a href="#" class="item-link item-content" ><div class="item-inner">';
//		if(r[qi].icon)s+='<div class="item-media"><i class="f7-icons">'+r[qi].icon+'</i></div>';
		s+='<div class="item-title">'+r[qi].text+'</div></div></a><div class="accordion-item-content iwb-menu-url">'+
		'<div class="list accordion-list"><ul>'+recMenu(r[qi].children, lvl+1)+'</ul></div></div>';
	} else {
		var href=r[qi].href;
		s+='<li class="accordion-item iwb-menu"><a href="'+href+'"'+ (href=='#' ? (' id="'+r[qi].id+'"'):'') +' class="item-content item-link close-panel'+(!lvl ? '  iwb-menu-murl':'')+'"><div class="item-inner">';
		s+='<div class="item-title">';
		if(lvl && r[qi].icon)s+='<i class="f7-icons iwb-mmenu-icon">'+(r[qi].icon||'star')+'</i>&nbsp; ';
//		else if(lvl)for(var ji=0;ji<lvl;ji++)s+=' &nbsp;';
		s+=r[qi].text+'</div></div></a></li>';
	}
	return s;	
}

iwb.prepareMainMenu=function(){
	iwb.request({url:'ajaxQueryData?_qid='+ (_scd.mobileMenuQueryId || 1487)+'&.r='+Math.random(),dataType:'text',data:{_json:1, xuser_tip:typeof xuserTip!='undefined' && xuserTip ? xuserTip:_scd.userTip}, success:function(d){
// if(iwb.debug){console.log('prepareMainMenu');console.log(d);}
		var j = eval('('+d+')');
		$$('#idx-main-menu').html(recMenu(j.data));
	}}); 
}


// Dom7
var $$ = Dom7;

// Framework7 App main instance
iwb.app = new Framework7({
  root: '#app', // App root element
  id: 'io.framework7.iwb', // App bundle ID
  name: 'Framework7', // App name
  theme: 'md',// 'auto', // Automatic theme detection
  // App root data
  data: function () {
    return {
      user: {
        firstName: 'John',
        lastName: 'Doe',
      },
    };
  },
  // App root methods
  methods: {
    helloWorld: function () {
      app.dialog.alert('Hello World!');
    },
  },
  // App routes
  routes: routes,
  on: {
    init: function () {
      var f7 = this;
      if (f7.device.cordova) {
        // Init cordova APIs (see cordova-app.js)
        cordovaApp.init(f7);
      }
    },
  }
});

// Init/Create main view
var mainView = iwb.app.views.create('.view-main', {
  url: '/'
});

// Login Screen Demo
$$('#my-login-screen .login-button').on('click', function () {
  var username = $$('#my-login-screen [name="username"]').val();
  var password = $$('#my-login-screen [name="password"]').val();

  // Close login screen
  iwb.app.loginScreen.close('#my-login-screen');

  // Alert username and password
  iwb.app.dialog.alert('Username: ' + username + '<br>Password: ' + password);
});


iwb.prepareMainMenu();

iwb.showRecordMenu=function(json3, targetEl){
	console.log('dd',json3.me,$$(json3.me.target));
	var lnk =[], href=false, pk=1;
	if(json3.crudFlags){
		if(json3.crudFlags.edit){
			href='showMForm?a=1&_fid='+json3.crudFormId+'&'+json3.pkName+'='+pk+'&.r='+Math.random();
			lnk.push('<li><a href="'+href+'" class="item-link item-content close-popover"><div class="item-inner" style="background-image:none;"><div class="item-title"><i class="f7-icons" style="font-size: 18px;color: #027eff;">compose</i> &nbsp; Update</div></div></a></li>');
		}
		if(json3.crudFlags.remove){
			lnk.push('<li><a href="#" id="idx-confirm-delete-'+json3.crudFormId+'" class="item-link item-content close-popover"><div class="item-inner" style="background-image:none;color:red;"><div class="item-title"><i class="f7-icons" style="font-size: 18px;">delete_round</i> &nbsp; Delete</div></div></a></li>');
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
		var s='<li><a href="'+xhref+'" class="item-link item-content close-popover'+e1+'"'+e2+'><div class="item-inner"><div class="item-title">';
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
			if(href)iwb.app.router.loadPage(href);
//			else json3.recordButtons[0].click(pk);
		}
	}
}