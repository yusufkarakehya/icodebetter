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
			      iwb.app.preloader.show();
			      iwb.request({url:'showMList',preloader:!0,data:Object.assign(routeTo.query,routeTo.params), success:function(d){
					//var j = eval('('+d+')');
			    	  console.log('hh',d)
					resolve({component:d})
			      }});
		    }
			  
		  },
		  
		  {
		    path: '/showMForm',
		    async: function (routeTo, routeFrom, resolve, reject) {
			      // Router instance
			      var router = this;

			      // App instance
			      var app = router.app;
			      iwb.request({url:'showMForm',preloader:!0,data:Object.assign(routeTo.query,routeTo.params), success:function(d){
					//var j = eval('('+d+')');
			    	  
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



var _scd={};
var iwb={};

iwb.serverUrl='';
iwb.debug=true;

iwb.submit=function(idForm, baseParams, callback){
	iwb.request({url:'ajaxPostForm',preloader:!0,data:Object.assign(baseParams, iwb.app.form.convertToData(idForm)),method:'POST',success:function(j){
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
//	if(iwb.debug)console.log('iwb.request: ' + (cfg ? cfg.url:''));
	if(!_scd){
		iwb.checkSession(cfg);
	} else if(cfg){
		if(cfg.url){
			if(cfg.preloader)iwb.app.preloader.show();

			return iwb.app.request({
	            url: iwb.serverUrl + cfg.url,
	            method: cfg.method||'GET', data: cfg.data || {},
//	            dataType: cfg.dataType || 'json',
	            success: function (d) {
	            	if(cfg.preloader)iwb.app.preloader.hide();
	            	var j = {};
	            	try{
	            		j = eval('('+d+')');
	            	}catch(ee){
	            		if(iwb.debug && confirm('iwb.request.eval Expception. Throw?'))throw e;
	            		return;
	            	}

            		if(j.success)try{
            			if(cfg.success)cfg.success(j, cfg);
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
		s+='<li class="accordion-item iwb-menu"><a href="/'+href+'"'+ (href=='#' ? (' id="'+r[qi].id+'"'):'') +' class="item-content item-link panel-close'+(!lvl ? '  iwb-menu-murl':'')+'"><div class="item-inner">';
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
//		var j = eval('('+d+')');
		$$('#idx-main-menu').html(recMenu(d.data));
	}}); 
}


// Dom7
var $$ = Dom7;

// Framework7 App main instance
iwb.app = new Framework7({
  root: '#app', // App root element
  id: 'io.framework7.iwb', // App bundle ID
  name: 'Framework7', // App name
  theme: 'auto',// 'auto', // Automatic theme detection
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
	var tg=$$(json3._event.target);
	var pk = tg.attr('iwb-key');
	if(!pk){
		tg = tg.parents('a');
		if(tg.length)pk = tg.attr('iwb-key');
	}
	if(!pk)return;
	var lnk =[], href=false;
	if(json3.crudFlags){
		if(json3.crudFlags.edit){
			href='/showMForm?a=1&_fid='+json3.crudFormId+'&'+json3.pkName+'='+pk+'&.r='+Math.random();
			lnk.push('<li><a href="'+href+'" class="item-link item-content popover-close"><div class="item-inner" style="background-image:none;"><div class="item-title"><i class="f7-icons" style="font-size: 18px;color: #027eff;">compose</i> &nbsp; Update</div></div></a></li>');
		}
		if(json3.crudFlags.remove){
			lnk.push('<li><a href="#" id="idx-confirm-delete-'+json3.crudFormId+'" class="item-link item-content popover-close"><div class="item-inner" style="background-image:none;color:red;"><div class="item-title"><i class="f7-icons" style="font-size: 18px;">delete_round</i> &nbsp; Delete</div></div></a></li>');
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
			if(href)iwb.app.router.loadPage(href);
//			else json3.recordButtons[0].click(pk);
		}
	}
}
Template7.registerHelper('iwb', function (key){
	return iwb[key];
});

Template7.registerHelper('scd', function (key){
	return _scd[key];
});

var daysOfTheWeek=['Pazar', 'Pazartesi','Salı','Çarşamba','Perşembe','Cuma','Cumartesi'];
iwb.fmtDateAgo=function(dt){
	if(!dt)return '';
	var tnow = new Date().getTime();
	var dt2=dt.toDate("dd/mm/yyyy hh:ii:ss");
	var t = dt2.getTime();
	if(t+30*1000>tnow)return 'Az Önce';//5 sn
	if(t+2*60*1000>tnow)return 'Bir Dakika Önce';//1 dka
	if(t+60*60*1000>tnow)return Math.round((tnow-t)/(60*1000)) + ' Dakika Önce';
	if(t+24*60*60*1000>tnow)return Math.round((tnow-t)/(60*60*1000)) + ' Saat Önce';
	if(t+2*24*60*60*1000>tnow)return 'Dün';
	if(t+7*24*60*60*1000>tnow)return daysOfTheWeek[dt2.getDay()];//5dka
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

function fmtDecimalNew(value,digit){
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
	return fmtDecimalNew(d,1)+ ' km';
}
iwb.combo2combo = function(prt, cmb, fnc, action){
	$$(prt).on('change',function(){
		if(iwb.debug){console.log(prt+':event:change');};
		var params = fnc($$(prt).val());
		if(params){
			$$(cmb.replace('idx-','id-')).show();
			iwb.loadCombo(cmb, params);
		} else {
			$$(cmb).find('option').remove();//temizle once
			if(true || params===false)$$(cmb.replace('idx-','id-')).hide();
		}
	});
	$$(prt).trigger('change');
}

iwb.loadCombo = function(cmb, params){
	var ctrl = $$(cmb);

	var selected=ctrl && ctrl.length ? ctrl.data('value'):'';

	ctrl.find('option').remove();//temizle once
	iwb.app.smartSelect.destroy(cmb);
	iwb.request({url:'ajaxQueryData', data:params, success:function(j){
		var data=j.data,res=[], s='';
		
		for(var qi=0;qi<data.length;qi++){
			s+='<option value="'+data[qi].id+'"'+(data[qi].id==selected ? ' selected':'')+'>'+data[qi].dsc+'</option>';
			if(data[qi].id==selected)res.push(data[qi].dsc);
		}
		ctrl.append(s);
		//console.log('as',selected, s);
		iwb.app.smartSelect.create(cmb);
		//$$(cmb.replace('idx-','id-')).find('.item-after').text(res.join(', '));
		//$$(cmb.replace('idx-','id-')).show();
	}});
}


Template7.registerHelper('ago', iwb.fmtDateAgo);
Template7.registerHelper('ago2', iwb.fmtDateAgo2);
Template7.registerHelper('dst', iwb.fmtDistance);

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