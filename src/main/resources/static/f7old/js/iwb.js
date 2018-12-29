// Expose Internal DOM library
var $$ = Dom7;

var _scd=false;
var tplOUsers;
var globalSearchForm=false;
var globalSearchFormJson = false;




var iwb={serverUrl:'./',tpls:{}, debug:true, chatMsgCount:0, newMsgCnt:{}, position:false, ajaxTrack:false};

iwb.mobile=3; //web
iwb.webPageId="iwb-"+new Date().getTime()+"-"+ Math.round(1000*Math.random());
iwb.deviceId=iwb.webPageId;
iwb.serverLagDiff=0;

iwb.logo=function(s, ml, mt){
	s= s||29; ml=ml||0;mt=mt||0;
	var s='<svg version="1.1" id="loading_logo" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" width="'+s+'px" height="'+s+'px" viewBox="0 0 510 380" enable-background="new 0 0 510 380" xml:space="preserve"><path id="svg_1" d="m377.15,126.621c-1.468,0 -2.897,0.14 -4.345,0.219c-0.379,-0.013 -0.733,-0.112 -1.117,-0.112c-1.152,0 -2.245,0.218 -3.365,0.339c-0.055,0.004 -0.109,0.004 -0.164,0.008l0,0.008c-15.791,1.777 -28.112,15.02 -28.112,31.286c0,16.327 12.412,29.607 28.288,31.304c0,0.052 0.001,0.109 0.001,0.16c0.823,0.048 1.629,0.061 2.439,0.086c0.311,0.009 0.601,0.092 0.913,0.092c0.151,0 0.29,-0.042 0.44,-0.044c1.641,0.033 3.293,0.044 5.021,0.044c28.27,0 51.188,22.25 51.188,50.521s-22.918,51.189 -51.188,51.189c-26.522,0 -48.33,-20.171 -50.928,-46.008l0,-184.123l-0.092,0c-0.493,-16.961 -14.319,-30.582 -31.4,-30.582c-17.08,0 -30.906,13.621 -31.4,30.582l-0.091,0l0,202.543c0,17.387 20.158,90.311 113.911,90.311c62.91,0 113.91,-51 113.91,-113.912c0.002,-62.911 -50.998,-113.911 -113.909,-113.911" fill="#0FA0E6"/><path id="svg_2" d="m263.238,264.134l-0.083,0c-1.375,15.323 -14.102,27.376 -29.782,27.376s-28.407,-12.053 -29.781,-27.376l-0.083,0l0,-113.289l-0.078,0c-0.419,-17.028 -14.286,-30.722 -31.415,-30.722s-30.996,13.694 -31.414,30.722l-0.078,0l0,113.289l-0.083,0c-1.375,15.323 -14.102,27.376 -29.782,27.376c-15.68,0 -28.407,-12.053 -29.782,-27.376l-0.083,0l0,-113.289l-0.077,0c-0.419,-17.028 -14.286,-30.722 -31.415,-30.722c-17.129,0 -30.996,13.694 -31.414,30.722l-0.078,0l0,113.289c1.429,50.104 42.398,90.309 92.849,90.309c23.565,0 44.993,-8.848 61.357,-23.302c16.365,14.454 37.792,23.302 61.357,23.302c24.957,0 47.59,-9.842 64.268,-25.851c-26.942,-22.761 -34.403,-53.882 -34.403,-64.458" fill="#808285"/><circle id="svg_4" fill="#FDBA31" r="33" cx="48.56" cy="70"/></svg>';
	if(ml || mt){
		s = '<span style="margin-top:'+mt+'px; margin-left:'+ml+'px;">' + s + '</span>';
	}
	return s;
}
iwb.apply = function(o, c, defaults){
    if(defaults){
        iwb.apply(o, defaults);
    }
    if(o && c && typeof c == 'object'){
        for(var p in c){
            o[p] = c[p];
        }
    }
    return o;
};

iwb.objProp=function(o){
	var t="";
	for(var q in o)t+=o[q] instanceof Function ? q + " = function{}\n" : q + " = " + o[q] + "\n";
	return t;
}


iwb.requests={}
//iwb.maxRequestToProgressbar = 3;
iwb.maxRequestToPreloader = 10;
iwb.maxRequestAge = 1000*60*4; //4 minutes

iwb.requestTimer = new Date().getTime();

if(!console.error)console.error=console.log;

iwb.addRequest = function(xhr){
	var cnt=iwb.getActiveRequestCount(true) + 1;
	if(iwb.debug){console.log('iwb.addRequest: ' + cnt);}
	if(cnt>iwb.maxRequestToPreloader && !iwb.preloader){
		iwb.preloader = true;
	    iwb.showIndicator('too-much-requests');
	}
	iwb.requestTimer++;
	iwb.requests[iwb.requestTimer] = {xhr:xhr, time:new Date().getTime()};
	
	return iwb.requestTimer;
};

iwb.removeRequest = function(k, l){
	if(!k){
		if(iwb.debug){
			console.error('iwb.removeRequest: Wrong Request Key ' + k + ' / ' + l);
		}
		return false;
	}
	var t = iwb.requests[k];
	if(!t){
		if(iwb.debug){
			console.error('iwb.removeRequest: Wrong Request Id ' + k + ' / ' + l);
		}
		return false;
	}
	if(iwb.getActiveRequestCount()<=iwb.maxRequestToPreloader && iwb.preloader){
		iwb.preloader = false;
	    iwb.hideIndicator('too-much-requests');
	}
	iwb.requests[k] = undefined;
	delete iwb.requests[k];
	return t;
};

iwb.getActiveRequestCount=function(b){
	if(b){
		var c=0, mor = new Date().getTime() - iwb.maxRequestAge, toBeRemoved=[];
		for(var k in iwb.requests){
			var r=iwb.requests[k];
			if(r.time>mor && (r.xhr && r.xhr.readyState && r.xhr.readyState<4))c++;
			else toBeRemoved.push(k);
		}
		for(var qi=0;qi<toBeRemoved.length;qi++)try{
			var k = toBeRemoved[qi];
			var r=iwb.requests[k];
			var url=r.xhr.requestUrl;
			if(r.xhr && r.xhr.readyState && r.xhr.readyState<4)r.xhr.abort();
			iwb.requests[k] = undefined;
			delete iwb.requests[k];
			if(iwb.debug)console.log('Removed getActiveRequestCount(true): ' + k + ' for ' + url);
		} catch(e){
			if(iwb.debug){console.log('Exception: iwb.getActiveRequestCount: ' + e);}
		}
		if(c+toBeRemoved.length>iwb.maxRequestToPreloader && c<=iwb.maxRequestToPreloader && iwb.preloader){
			iwb.preloader = false;
		    iwb.hideIndicator('too-much-requests');
		}
		return c;
	} else {
		var c=0;
		for(var k in iwb.requests)c++;
		return c;
	}
}

iwb.cleanOldRequests=function(){
	var mor = new Date().getTime() - iwb.maxRequestAge, cnt=0, toBeRemoved=[];
	for(var k in iwb.requests){
		var r=iwb.requests[k];
		if(r.time<mor){
			r.id=k;
			toBeRemoved.push(r);
		}
	}
	for(var qi=0;qi<toBeRemoved.length;qi++)try{
		var r=iwb.requests[k];
		if(r.xhr)r.xhr.abort();
		var rq=iwb.removeRequest(r.id, 'iwb.cleanOldRequests');
		if(rq)cnt++;
	} catch(e){
		if(iwb.debug){console.log('Exception: iwb.cleanOldRequests: ' + e);}
	}
	if(iwb.debug){console.log('Cleaned iwb.cleanOldRequests: ' + cnt);}
//	if(iwb.maxRequestAge)setTimeout(iwb.cleanOldRequests, iwb.maxRequestAge);
}

//if(iwb.maxRequestAge)setTimeout(iwb.cleanOldRequests, iwb.maxRequestAge);

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


Template7.registerHelper('ago', iwb.fmtDateAgo);
Template7.registerHelper('ago2', iwb.fmtDateAgo2);
Template7.registerHelper('dst', iwb.fmtDistance);


iwb.indicators={}
iwb.showIndicator=function(id){
	if(id)iwb.indicators[id]=new Date().getTime();
	iwb.app.showIndicator();
}

iwb.hideIndicator=function(id){
	if(id){
		iwb.indicators[id]=undefined;
		delete iwb.indicators[id];
		for(var k in iwb.indicators)return;
	}
	iwb.app.hideIndicator();	
}


iwb.progresses={}
iwb.showProgressbar=function(id, type){
	if(id)iwb.progresses[id]=type || 0;//new Date().getTime();
    var container = $$('body');
    if (container.children('.progressbar, .progressbar-infinite').length){
    	if(!type)return;
    	for(var k in iwb.progresses)if(iwb.progresses[k])return;
    	iwb.app.hideProgressbar();
    	iwb.app.showProgressbar(container, type);
    	return;
    }
    if(type)iwb.app.showProgressbar(container, type);
    else iwb.app.showProgressbar(container);
}

iwb.hideProgressbar=function(id){
	if(id){
		iwb.progresses[id]=undefined;
		delete iwb.progresses[id];
		for(var k in iwb.progresses)return;
	}
	iwb.app.hideProgressbar();	
}

iwb.cleanProgresAndIndicators=function(){
	iwb.app.hideProgressbar();iwb.app.hideIndicator();
	iwb.progresses={};iwb.indicators={};
}

iwb.timeoutActions=['inf','warn','lock','timeout', 'final'];//timeout final:'confirm for retry','error message and nothing','login'


iwb.timeoutStrategy={ //inf(progressbar), warn(progressbar-multi), lock(indicator), retr(saniye gostererek), err( vazgecti baglanamadi: alert), login(login ekrani): d4s=debug for ajaxstart
		'ajaxPing':{warn:1, err:15, ind:!0, d4s:!0}
		,'showMList':{warn:5, err:20, ind:!0}
		,'showMForm':{warn:5, err:20, ind:!0}
		,'ajaxQueryData':{inf:2,warn:3, err:5}
		,'ajaxPostForm':{warn:5, err:20, ind:!0}
		,'ajaxAuthenticateUser':{warn:10, err:20, ind:!0, d4s:!0}
		,'ajaxNotification':{err:190, d4s:!0}
		,'upload.form':{inf:1, err:5, ind:!0}
		,'ajaxLogoutUser':{ind:!0, d4s:!0}
		,'ajaxSelectUserRole':{ind:!0, d4s:!0}
		,'ajaxChangeActiveProject':{ind:!0, d4s:!0}
	}; //59sn
iwb.getTimeoutStrategy=function(url){
	var s=iwb.timeoutStrategy;
	for(var k in s)if(url.indexOf(k)>=0)return s[k];
	return false;
}
iwb.timeoutCallback=function(obj){
	var si=0, ta=iwb.timeoutActions;
	if(obj.currentStep)for(var qi=0;qi<ta.length;qi++)if(obj.currentStep==ta[qi]){
		si=qi+1;
		break;		
	}	
}

iwb.timeoutWarnCallback=function(obj){
	return function(){
		iwb.showProgressbar('warn-'+obj.xhr.timeoutId, 'multi');
		obj.xhr.timeoutId = -obj.xhr.timeoutId;
		if(obj.err && !obj.ind)obj.xhr.timeoutId2 = setTimeout(iwb.timeoutErrCallback(obj), obj.err*1000);
//		else obj.xhr.timeoutId=false;
	};
}

iwb.timeoutErrCallback=function(obj){
	return function(){
//		iwb.hideProgressbar('warn-'+obj.xhr.timeoutId);obj.xhr.timeoutId=false;
		iwb.showIndicator('err-'+obj.xhr.timeoutId2)
		obj.xhr.timeoutId2 = -obj.xhr.timeoutId2;
	};
}
/*
iwb.indicatorList=['ajaxAuthenticateUser','upload.form','ajaxChangeActiveProject','ajaxSelectUserRole','ajaxLogoutUser','showMList'];
iwb.hasAny=function(str, list){
	if(!list || !str)return false;
	for(var qi=0;qi<list.length;qi++)if(str.indexOf(list[qi])>=0)return true;
	return false;
}*/
if(iwb.ajaxTrack){
	$$(document).on('ajaxStart', function (e) {
		if(iwb.debug)console.log('ajaxStart: ' + e.detail.xhr.requestUrl);
	//	console.log(e.detail);
		var url=e.detail.xhr.requestUrl;
		var ts = iwb.getTimeoutStrategy(url), nts=false;
		if(ts && ts.warn){
			nts = iwb.apply({xhr: e.detail.xhr}, ts);
			e.detail.xhr.timeoutId = setTimeout(iwb.timeoutWarnCallback(nts), nts.warn*1000);
	//		nts.id = e.detail.xhr.timeoutId;
		}
	
		if(!ts || !ts.d4s){
			e.detail.xhr.requestId = iwb.addRequest(e.detail.xhr);
			if(iwb.iosDebug)iwb.app.addNotification({closeOnClick:true,title:'<b style="color:red;">Debug 4 ajaxStart </b>'+e.detail.xhr.requestId,message: url});
		}
		
	/*	if(url.indexOf('showMList')>=0 || url.indexOf('showMForm')>=0){ //adRequest'e koy
			
		} */
	//	if (!iwb.hasAny(url, iwb.indicatorList))return;
		if(ts && ts.ind)iwb.showIndicator(e.detail.xhr.requestId);
	});
	$$(document).on('ajaxComplete', function (e) {
		var xhr = e.detail.xhr;
		if(iwb.debug)console.log('ajaxComplete: ' + xhr.requestUrl);
	//	console.log(xhr);
		var id =xhr.requestId; 
		if(id)iwb.removeRequest(id, 'on.ajaxComplete: ' + id);
		var url=xhr.requestUrl;
		
		var ts = iwb.getTimeoutStrategy(url);
		if(ts){
			if(ts.warn && xhr.timeoutId){
				if(xhr.timeoutId>0){
					clearTimeout(xhr.timeoutId);
				} else {
					iwb.hideProgressbar('warn'+xhr.timeoutId);
				}
			} 
			if(ts.err && xhr.timeoutId2){
				if(xhr.timeoutId2>0){
					clearTimeout(xhr.timeoutId2);
				} else {
					iwb.hideIndicator('err'+xhr.timeoutId2);
				}
			} 
			if(ts.ind)iwb.hideIndicator(id);
		}
		
	//	if (!iwb.hasAny(url, iwb.indicatorList))return;
	});
	$$(document).on('ajaxError', function (e) {
		var xhr = e.detail.xhr;
		if(iwb.debug){console.log('ajaxError('+xhr.status+'): ' + e.detail.xhr.requestUrl);console.log(xhr);}
	//	console.log(e.detail.xhr);
		var id = e.detail.xhr.requestId; 
		if(id)iwb.removeRequest(id, 'on.ajaxError: ' + id);
		var url=xhr.requestUrl;
		
		var ts = iwb.getTimeoutStrategy(url);
		if(ts){
			if(ts.warn && xhr.timeoutId){
				if(xhr.timeoutId>0){
					clearTimeout(xhr.timeoutId);
				} else {
					iwb.hideProgressbar('warn'+xhr.timeoutId);
				}
			}
			if(ts.err && xhr.timeoutId2){
				if(xhr.timeoutId2>0){
					clearTimeout(xhr.timeoutId2);
				} else {
					iwb.hideIndicator('err'+xhr.timeoutId2);
				}
			} 
		}
		
		if(!xhr.status){ // connection error 1: checkSession/ajaxPing(start/resume), 2. asynch/ajaxNotification, 3. shotMList&showMForm, 4. ajaxLogoutUser, 5. Others (ajaxQuery & postForm/Chat, etc..)
			if(url.indexOf('ajaxPing')>=0){
	        	if(iwb.debug){console.log('ajaxPing-failure');}
				if(iwb.checkSessionId){
	                iwb.hideProgressbar('session');
					clearTimeout(iwb.checkSessionId);
					iwb.checkSessionId = false;
				}
				iwb.app.addNotification({closeOnClick:true,title:'<b style="color:red;">Bağlantı Hatası</b>',message: 'Lütfen internet bağlantınızı kontrol edip tekrar deneyin. ' + (iwb.debug ? iwb.serverUrl : ''), media:info4media(2), hold:5000});
				iwb.app.confirm('Tekrar Denemek ister misiniz?', function(){//convirmed
					iwb.checkSession();
				}, function (){//canceled
					iwb.longPoll.end();
					iwb.app.loginScreen();
				});
			} else if(url.indexOf('ajaxNotification')>=0){
				if(iwb.debug){console.log('longPoll-failure');}
				var j = iwb.longPoll; j.id = false; j.xhr = false;
				j.error = true;
				j.errorCount++; 
				if(j.retryMenuFlag && j.errorCount >= j.lockScreenErrorCount){
					if(j.errorCount == j.lockScreenErrorCount)iwb.hideProgressbar('longpoll');
	
					if(j.started && !j.id){
						j.retryTimerCount = Math.round(j.errorTimex/1000);
			            iwb.app.showPreloader('<span style="display: block;">'+ iwb.logo(40) + '</span><a href=# id="idx-timer-link" class="item" style="font-size:16px; color:black; font-weight:normal; margin-top:-10px;" > &nbsp; Bağlantı kesildi... <span id="idx-timer" style="color:red;">'+ j.retryTimerCount +'</span></a>');
			            var tl= $$('#idx-timer-link');
			            if(tl && tl.length){
			            	tl.off('click');
			            	tl.on('click', function(){
			            		j.retryTimerCount = 0;
			            	});
			            }
			            setTimeout(j.retryTimer, 1000);
					}
				} else {
					if(j.started && !j.id){
						j.id = setTimeout(j.doPoll, j.retryMenuFlag ? 1000 : j.errorTimex);
					    iwb.showProgressbar('longpoll', 'multi');
					}
				}
			} else if(url.indexOf('showMList')>=0 || url.indexOf('showMForm')>=0){
				if(iwb.debug){console.log('showMList/showMForm-failure');}
				iwb.hideIndicator(id);
				iwb.app.addNotification({closeOnClick:true,title:'<b style="color:red;">Bağlantı Hatası</b>',message: 'Lütfen internet bağlantınızı kontrol edip tekrar deneyin. ' + (iwb.debug ? iwb.serverUrl : ''), media:info4media(2), hold:5000});
				iwb.app.confirm('Try again?', function(){//convirmed
		            mainView.router.load({url:url+'&.r='+Math.random(), reload:url.indexOf('showM')==0 && url.indexOf('_reload=1')>-1,force: true, ignoreCache:true/*,animatePages:false*/}); //load another page with auth form
				});
			} else if(url.indexOf('ajaxLogoutUser')>=0){
	//			alert('error4ajaxLogoutUser')
				if(iwb.debug){console.log('ajaxLogoutUser-failure');}
				_scd=false;
				iwb.longPoll.end();
				iwb.app.loginScreen();
			} else if (ts && ts.ind){ //ajaxAuthenticate,Project
				if(iwb.debug){console.log('others-failure');}
				iwb.hideIndicator(id);
				iwb.app.alert('Connection error. ' + (iwb.debug ? iwb.serverUrl : ''), '<b style="color:red;">ERROR</b>');
				return;
			} else { //TODO. requestPoll'dan cikar
				iwb.app.addNotification({closeOnClick:true,title:'<b style="color:red;">Connection error</b>',message: 'Connection error. ' + (iwb.debug ? iwb.serverUrl : ''), media:info4media(2), hold:5000});
			}
			return;
		}
		
		if(ts && ts.ind)iwb.hideIndicator(id);
	});
}
iwb.request=function(cfg){
	if(iwb.debug)console.log('iwb.request: ' + (cfg ? cfg.url:''));
	if(!_scd){
		iwb.checkSession(cfg);
	} else if(cfg){
		if(cfg.url){
			return $$.ajax({
	            url: iwb.serverUrl + cfg.url,
	            method: 'GET', data: cfg.data || {},
	            dataType: cfg.dataType || 'json',
	            success: function (j) {
	//            	if(cfg.requestId)iwb.removeRequest(cfg.requestId, 'iwb.request: success');
	            	if(iwb.debug){console.log('ajax-success: '+cfg.url);}
	            	if(cfg.dataType && cfg.dataType!='json'){
	            		if(cfg.success)cfg.success(j, cfg);
	            	} else if(j.success){
	            		if(cfg.success)try{
	            			cfg.success(j, cfg);
	            		} catch(e){
	            			if(iwb.debug)iwb.app.confirm('iwb.request.success Expception. Throw?', function(){throw e});
	            		}
	            	} else if(cfg.error){
	            		try{
	                		cfg.error(j, cfg);
	            		} catch(e){
	            			if(iwb.debug)iwb.app.confirm('iwb.request.error Expception. Throw?', function(){throw e});
	            		}
	            	} else switch(j.errorType){
	    			case	'session':
	    				iwb.reLogin(cfg);
	    				return;
	    			case	'validation':
	    				var s='';
	    				if(j.errors)for(var qi=0;qi<j.errors.length && qi<3;qi++){
	    					s+=j.errors[qi].dsc+': ' + j.errors[qi].msg +'<br/>';
	    				} else s=j.error;
	    				iwb.app.alert(iwb.strTrim(s, 200), j.errorType || 'iWB');
	    				return;
	    			default:
	    				if(cfg.error)cfg.error(j, cfg);
	    				else iwb.showResponseError(j);
	    				return;
	    			}
	            },
	            error:function (j,err) {
	//            	if(cfg.requestId)iwb.removeRequest(cfg.requestId, 'iwb.request: error');
	//            	iwb.app.addNotification({closeOnClick:true,title:'Error',message: err || 'Connection Error to Server2', media:info4media(2)});
	            	if(iwb.debug && err){
	               		iwb.app.alert(err + ': ' + cfg.url);
	            	}
	            	if(cfg.failure)cfg.failure(j, cfg, err);
	//            	else iwb.app.addNotification({closeOnClick:true,title:'Error',message: err || 'Connection Error to Server2', media:info4media(2)});
	            }
	        });
		} else if(cfg.callback)cfg.callback();
//		if(iwb.mobile==1 || cfg.url.indexOf('ajaxNotification')<0)cfg.requestId = iwb.addRequest(xhr2);
	}
	return false;
}

iwb.combo2combo = function(prt, cmb, fnc, action){
	$$(prt).on('change',function(){
		if(iwb.debug){console.log(prt+':event:change');};
		var params = fnc($$(prt).val());
		if(params)iwb.loadCombo(cmb, params);
		else {
			$$(cmb).find('option').remove();//temizle once
			if(true || params===false)$$(cmb.replace('idx-','id-')).hide();
		}
	});
	$$(prt).trigger('change');
}

iwb.loadCombo = function(cmb, params){
	var ctrl = $$(cmb);
	var selected=ctrl && ctrl.length ? ctrl[0].attributes['iwb-value'].value:'';
	ctrl.find('option').remove();//temizle once
	iwb.request({url:'ajaxQueryData', data:params, success:function(j){
		var data=j.data,res=[];
		for(var qi=0;qi<data.length;qi++){
			iwb.app.smartSelectAddOption(cmb, '<option value="'+data[qi].id+'"'+(data[qi].id==selected ? ' selected':'')+'>'+data[qi].dsc+'</option>');
			if(data[qi].id==selected)res.push(data[qi].dsc);
		}
		$$(cmb.replace('idx-','id-')).find('.item-after').text(res.join(', '));
		$$(cmb.replace('idx-','id-')).show();
	}});
}



iwb.autoCompleteJson={
    openIn: 'page', //open in page
//    opener: $$('#autocomplete-standalone-ajax'), //link that opens autocomplete
//    multiple: true, //allow multiple values
//  limit: 50,
    valueProperty: 'id', //object's "value" property name
    textProperty: 'dsc', //object's "text" property name
    preloader: true, //enable preloader
    source: function (autocomplete, query, render) {
        var results = [];
        if (query.length === 0) {
            render(results);
            return;
        }
        // Show Preloader
        autocomplete.showPreloader();
        // Do Ajax request to Autocomplete data
        iwb.request({
            url: 'ajaxQueryData?'+this.params,
//            method: 'GET', dataType: 'json',
            //send "query" to server. Useful in case you generate response dynamically
            data: {
                xdsc: query
            },
            success: function (j) {
                // Hide Preoloader
                autocomplete.hidePreloader();
                // Render items by passing array with result items
                render(j.data);
            }, failure:function (j) {
                // Hide Preoloader
                autocomplete.hidePreloader();
            }
        });
    },
    onChange: function (autocomplete, value) {
        var itemText = [],
            inputValue = [];
        for (var i = 0; i < value.length; i++) {
            itemText.push(value[i][this.textProperty]);
            inputValue.push(value[i][this.valueProperty]);
        }
        // Add item text value to item-after
        this.opener.find('.item-after').text(itemText.join(', '));
        // Add item value to input value
        this.opener.find('input').val(inputValue.join(','));
    }
}

iwb.autoCompleteJson4Autocomplete=function(prtInitVal, cmb, fnc){
	if(prtInitVal){
		var params = fnc(prtInitVal);
		if(params)iwb.loadCombo(cmb, params);
	}
	
	return {
	    openIn: 'page', //open in page
	//    opener: $$('#autocomplete-standalone-ajax'), //link that opens autocomplete
	//    multiple: true, //allow multiple values
	//  limit: 50,
	    valueProperty: 'id', //object's "value" property name
	    textProperty: 'dsc', //object's "text" property name
	    preloader: true, //enable preloader
	    source: function (autocomplete, query, render) {
	        var results = [];
	        if (query.length === 0) {
	            render(results);
	            return;
	        }
	        // Show Preloader
	        autocomplete.showPreloader();
	        // Do Ajax request to Autocomplete data
	        iwb.request({
	            url: 'ajaxQueryData?'+this.params,
	//            method: 'GET', dataType: 'json',
	            //send "query" to server. Useful in case you generate response dynamically
	            data: {
	                xdsc: query
	            },
	            success: function (j) {
	                // Hide Preoloader
	                autocomplete.hidePreloader();
	                // Render items by passing array with result items
	                render(j.data);
	            }, failure:function (j) {
	                // Hide Preoloader
	                autocomplete.hidePreloader();
	            }
	        });
	    },
	    onChange: function (autocomplete, value) {
	        var itemText = [],
	            inputValue = [];
	        for (var i = 0; i < value.length; i++) {
	            itemText.push(value[i][this.textProperty]);
	            inputValue.push(value[i][this.valueProperty]);
	        }
	        // Add item text value to item-after
	        this.opener.find('.item-after').text(itemText.join(', '));
	        // Add item value to input value
	        var val = inputValue.join(',');
	        this.opener.find('input').val(val);
	        
	        
	        var params = fnc(val);
			if(params)iwb.loadCombo(cmb, params);
			else {
				$$(cmb).find('option').remove();//temizle once
				if(true || params===false)$$(cmb).hide();
			}
	    }
	}
}

function user4chat(j){
	return '<a href=# style="display:inline;'+(iwb.mobile==2 ? ' color:darkorange;':'')+'" iwb-key="'+j.userId+'" iwb-dsc="'+j.userDsc+'" class="item-link iwb-user-chat">'+j.userDsc + '</a>';
}

function user4media(j){
	return '<img src="'+iwb.serverUrl+'sf/pic'+j.userId+'.png" style="border-radius:50%;">';
}

function users4chat(l){
	if(!l || !l.length)return '';
	var s='';
	for(var qi=0;qi<l.length;qi++)s+=', ' + user4chat(l[qi]);
	return s.substr(2);	
}

function getPk(pk){
	for(var k in pk)if(k!='customizationId')return pk[k];
	return -1;
}

function open4chat(k){
	var pk=this.attributes['iwb-key'].value;
	if(1*pk==1*_scd.userId)return;
	iwb.lastChatUserDsc = this.attributes['iwb-dsc'].value;
	mainView.router.load({force: true, url:iwb.serverUrl+'showMList?_lid=14&ou='+pk+'&.r='+Math.random(),ignoreCache:true/*,animatePages:false*/});
	iwb.app.closePanel('right');
}

function open4chatAnother(j){
	if(!j || !j.userId || !j.userDsc){
		alert('yanlis adam')
		return;
	}
	var pk=1*j.userId;
	if(pk==1*_scd.userId)return;
	
	if(mainView.activePage && mainView.activePage.name.indexOf('messages-')==0){
		var oldOtherUserId = 1*mainView.activePage.name.substr('messages-'.length);
		if(pk==oldOtherUserId)return;//zaten pencere acik
		//mainView.router.back(); //TODO. yapilmali
	}
	
	iwb.lastChatUserDsc = j.userDsc;
	mainView.router.load({force: true, url:iwb.serverUrl+'showMList?_lid=14&ou='+pk+'&.r='+Math.random(),ignoreCache:true/*,animatePages:false*/});
//	iwb.app.closePanel('right');
}

iwb.showResponseError=function(j){
//	iwb.app.alert(iwb.strTrim(j.error, 200), j.errorType || 'iWB');
	try{
		if(top && top.ajaxErrorHandler)top.ajaxErrorHandler(j);
		else iwb.app.alert(j.errorMsg || iwb.strTrim(j.error.length.toString(), 200), j.errorType || 'iWB');
	}catch(e){
		iwb.app.alert('System Error', j.errorType || 'iWB');		
	}
}

$$('#idButtonLogin').on('click', function () {
    var username = $$('.login-screen input[name="username"]').val();
    var psw = $$('.login-screen input[name="password"]');
    var password = psw.val();
    psw.val('');
    
    $$.ajax({url:iwb.serverUrl + 'ajaxAuthenticateUser?d=1&c=1&_mobile='+(iwb.mobile||3)+'&_mobile_device_id='+iwb.deviceId+'&customizationId=0&locale=tr',
    	data: {userName:username, passWord:password}, dataType: 'json', 
    	success: function(j){
    	if(j.success){
    		iwb.rememberMe = false;
    		if(window.localStorage)try{
    			var ls=window.localStorage;
    			ls.setItem('userName',username);
    			iwb.rememberMe = $$('.login-screen input[name="remember-me"]')[0].checked;
    			if(iwb.rememberMe){
        			ls.setItem('passWord',password);
    			} else ls.setItem('passWord','');
    		} catch(e) {}

    		if(j.roleCount){
    			_scd={userCustomizationId:j.defaultUserCustomizationId};
    			prepareRoles(false);
    			return;
    		}
    		if(!_scd || _scd.userRoleId!=j.session.userRoleId){//TODO burda butun ekranlar gemizlenecek
    			iwb.home = false;
                iwb.tpls={};
    		}
    		_scd=j.session;
    		iwb.newMsgCnt = j.newMsgCnt;
    		iwb.updateNewMsgBadge(false);
    		iwb.updateBackendMobileStatus(1);
            iwb.app.closeModal('.login-screen');
            iwb.prepareMainMenu();
    		if(iwb.longPoll.strategy==1)iwb.longPoll.start();
    		if(!iwb.home){
    			iwb.home = true;
    			mainView.router.loadPage('showMList?_lid='+(_scd.homeListId || 15)+'&_reload=1&.r='+Math.random());
    		}
    	} else {
    		iwb.showResponseError(j);
    		
    	}
    }});
});

function selectProject(pq){
	return function(){
		iwb.request({url:'ajaxChangeActiveProject?.r='+Math.random(), data:{_uuid:pq},success: function (j) {
			_scd.projectId=pq;
		}});
	}
}

function selectUserRole(ur, closeLoginDialog){
	return function(){
		iwb.request({url:'ajaxSelectUserRole?d=1&c=1&_mobile='+(iwb.mobile||3)+'&_mobile_device_id='+iwb.deviceId + '&.r='+Math.random(), data:{userRoleId:ur, userCustomizationId:_scd.userCustomizationId},success: function (j) {
			if(!j.success){
				iwb.showResponseError(j);
				return;
			}
			if(iwb.rememberMe){
				var ls = window.localStorage;
				ls.setItem('userRoleId', ur);
				ls.setItem('userCustomizationId', _scd.userCustomizationId);
			}
			_scd = j.session; iwb.home = false;
            if(closeLoginDialog){
                iwb.app.closeModal('.login-screen');
            } else {
            	iwb.app.closePanel();
            }
			iwb.tpls={};
            iwb.prepareMainMenu();
            if(iwb.longPoll.strategy==1)iwb.longPoll.start();
    		if(!iwb.home){
    			iwb.home = true;
    			mainView.router.loadPage('showMList?_lid='+(_scd.homeListId || 15)+'&_reload=1&.r='+Math.random());
    		}
            
		}});
	}
}

function userSettings(){
	mainView.router.load({force: true, url:iwb.serverUrl+'showMForm?a=1&_fid=292&tuser_id='+_scd.userId+'&.r='+Math.random(),ignoreCache:true/*,animatePages:false*/}); //load another page with auth form
	iwb.app.closePanel();
}

function roleSettings(){
	alert('TODO')
}

function prepareRoles(otherButtons){
	iwb.request({url:'ajaxQueryData?_qid=1&xmobile_flag=1&.r='+Math.random(), success: function (j) {
		var rl=[];
		rl.push({text: 'Roller',label: true});
		for(var qi=0;qi<j.data.length;qi++)rl.push({text:' ·&nbsp; '+j.data[qi].role_id_qw_, bold:j.data[qi].user_role_id==_scd.userRoleId, onClick:selectUserRole(j.data[qi].user_role_id,otherButtons===false)});
		if(otherButtons===false){
			iwb.app.actions(rl);
		} else {
//			if(j.data.length==1)rl=[];
			var stg=[{text:'<img src="'+iwb.serverUrl+'sf/pic'+_scd.userId+'.png" class="ppic-mini"> &nbsp;'+ _scd.completeName, color:'purple', onClick:userSettings}, {text:'<i class="icon f7-icons" style="color:rgba(0,0,0,0.5);font-size: 21px;">gear</i>&nbsp; Settings', onClick:roleSettings}];
			iwb.app.actions(otherButtons ? [stg,rl,otherButtons] : [stg, rl]);
			if(otherButtons || rl.length>0)$$($$('.actions-modal-group')[1]).css('background-color','#fafaff');
		}
	}});
}

$$('#idx-settings').on('click', function(){
	if(false && _scd.roleId==0){//developer ise
		iwb.request({url:'ajaxQueryData?_qid=2765&.r='+Math.random(), success: function (j) {
			var pr=[];
			pr.push({text: 'Projects',label: true});
			for(var qi=0;qi<j.data.length;qi++)pr.push({text:' ·&nbsp; '+j.data[qi].dsc, bold:j.data[qi].id==_scd.projectId, onClick:selectProject(j.data[qi].id)});
			prepareRoles(pr);
		}});
	} else prepareRoles();
});



iwb.reLogin=function(afterCfg){
	if(window.localStorage){
		var ls = window.localStorage;
		var reUserName = ls.getItem("userName");
		var rePassWord = ls.getItem("passWord"); 
		if(reUserName && rePassWord){
			var reUserRoleId=ls.getItem("userRoleId");
			var reUserCustomizationId = ls.getItem('userCustomizationId');
		    $$.ajax({url:iwb.serverUrl + 'ajaxAuthenticateUser?d=1&c=1&_mobile='+(iwb.mobile||3)+'&_mobile_device_id='+iwb.deviceId+'&customizationId=0&locale=tr',
		    	data:{userName:reUserName, passWord:rePassWord, userRoleId: reUserRoleId||0},dataType: 'json', 
		    	success:function(j){
				if(j.success){
		    		if(!j.session){ //TODO. eger session gelmediyse, sikinti
		    			_scd = false;
			    		iwb.longPoll.end();
	        			iwb.app.loginScreen();
		    			return;
		    		}
		    		if(!_scd || _scd.userRoleId!=j.session.userRoleId){//TODO burda butun ekranlar gemizlenecek
		    			iwb.home = false;
		                iwb.tpls={};
		    		}
		    		
		    		
		    		iwb.home = _scd && 1*_scd.userRoleId==1*j.session.userRoleId;
		    		_scd=j.session;
		    		iwb.newMsgCnt = j.newMsgCnt;
		    		iwb.updateNewMsgBadge(false);
		    		iwb.updateBackendMobileStatus(1);
		            iwb.tpls={};
		            iwb.prepareMainMenu();
		            if(iwb.longPoll.strategy==1)iwb.longPoll.start();
            		if(afterCfg)iwb.request(afterCfg);//tekrar cagir
            		else if(!iwb.home){
		    			iwb.home = true;
		    			mainView.router.loadPage('showMList?_lid='+(_scd.homeListId || 15)+'&_reload=1&.r='+Math.random());
		    		}
		    	} else {
	    			_scd = false;
		    		$$('.login-screen input[name="username"]').val(reUserName);
		    		iwb.longPoll.end();
        			iwb.app.loginScreen();
		    	}
		    }});
			return;
		} else {
			if(reUserName)$$('.login-screen input[name="username"]').val(reUserName);
		}
	}
	iwb.longPoll.end();
	iwb.app.loginScreen();	
}

iwb.strTrim = function(s,len){
	if(!s)return '';
	if(s.length<=len)return s;
	return s.substr(0,len)+'...';
} 

iwb.checkSessionTimeout = 10000;
iwb.checkSession=function(afterCfg){
    if(!iwb.checkSessionId){
       	iwb.showProgressbar('session');
        
        if(iwb.checkSessionTimeout)iwb.checkSessionId = setTimeout(function(){
            if(iwb.checkSessionId){
				iwb.hideProgressbar('session');
				iwb.longPoll.end();
				iwb.app.loginScreen();
				iwb.checkSessionId = false;
            }
        }, iwb.checkSessionTimeout); 
    }
    $$.ajax({
        url: iwb.serverUrl + 'ajaxPing?d=1&c=1&.r=' + Math.random(),
        method: 'GET',
        dataType: 'json',
        success: function (j) {
			if(iwb.checkSessionId){
                iwb.hideProgressbar('session');
            	clearTimeout(iwb.checkSessionId);
				iwb.checkSessionId = false;
			}
        	if(iwb.debug){console.log('ajax-success: ajaxPing');console.log(j);}
        	if(j.success){
        		if(j.session){
        			iwb.home = _scd && 1*_scd.userRoleId==1*j.session.userRoleId;
            		_scd=j.session;
            		if(_scd.mobileDeviceId)iwb.webPageId = _scd.mobileDeviceId; //hack
    				iwb.longPoll.init('../async/ajaxNotifications?&.w=' + iwb.webPageId, iwbLP);
            		iwb.newMsgCnt = j.newMsgCnt;
            		iwb.updateNewMsgBadge(true);
            		iwb.updateBackendMobileStatus(1);
            		if(afterCfg)iwb.request(afterCfg);//tekrar cagir
            		if(iwb.longPoll.strategy==1)iwb.longPoll.start();
        		} else {//login screen
        			iwb.reLogin(afterCfg);
        		}
        	}
        }
    });
}


$$(document).on('page:back', function (e) {
	if(iwb.debug){console.log('page:back');console.log(e);console.log(e.srcElement.className);}
	
	if(e.srcElement.className.indexOf('smart-select-page')>=0 || e.srcElement.id=='idx-list-14')return;//smart select veya chat'ten donuyosa yapma
	if(iwb.lastLiveSyncPk){
		iwb.request({url:'ajaxLiveSync?.a=2&.t=' + iwb.webPageId + '-f-' + iwb.liveSyncRecord.fId +  '&.w=' + iwb.webPageId + '&.pk=' +  iwb.lastLiveSyncPk});
		iwb.lastLiveSyncPk = false;
//		iwb.longPoll.end();
	}
	if(iwb.longPoll.strategy==2)iwb.longPoll.end(); //TODO aslinda sadece chat ve edit form icin
});



iwb.longPoll={
	url:false, id:false, fnc:false, timex: 0, lockScreenErrorCount: 5, errorCount:0, retryMenuFlag:false, strategy:0, //0:yok, 1: her zaman, 2: sadece chat ve livesync form
	isStarted: function(){
		var j = iwb.longPoll;
//		console.log('longPoll.isStarted: ' + j.xhr.status);console.log(j.xhr);
		if(!j.started || !j.xhr || !j.id)return false;
	},
	init:function(url, fnc, timex, errorTimex){
		var j = iwb.longPoll;
		j.fnc=fnc; j.url=url; j.timex= timex || 0; j.errorTimex = errorTimex || 10000;
		if(iwb.debug)console.log(iwb.longPoll)
//			j.doPoll();
	},
	end:function(force){
		var j = iwb.longPoll;
		if(j.errorCount && (!j.retryMenuFlag || j.errorCount < j.lockScreenErrorCount)){
	        iwb.hideProgressbar('longpoll');
	        j.errorCount = 0;
		}
		if(!force && !j.started)return;
		if(iwb.debug)console.log('end-longPoll: ' + j.id);
		if(j.id){
			clearTimeout(j.id);
			j.id = false;
		}
		if(j.xhr){
			j.xhr.abort();
			j.xhr = false;
		}
		j.started = false;
	},
	start:function(force){
       // return;
		var j = iwb.longPoll;
		if((!force && j.started) || j.strategy==0)return;
		j.end(!!force);
		j.started=true;
		if(!j.id && !j.xhr)j.id = setTimeout(j.doPoll, 0);
	},
	doPoll:function(){
//		iwb.app.addNotification({closeOnClick:true,title:'Bilgi', message: 'longPoll ;)', media:info4media(0)}); // TODO
		var j = iwb.longPoll;j.id = false;
		if(j.started && j.url && !j.xhr){
			if(j.errorCount && (!j.retryMenuFlag || j.errorCount < j.lockScreenErrorCount)){
		        iwb.hideProgressbar('longpoll');
			}
			j.xhr = iwb.request({url:j.url+'&.r='+Math.random(), success:function(jms){
				var j = iwb.longPoll; j.id = false; j.xhr = false;
				if(!jms.success){
					j.id = setTimeout(j.doPoll, j.errorTimex);
					return;
				}
				if(j.errorCount){
			        j.errorCount=0;
			        j.error = false;
				}
				if(j.started) {
					j.fnc(jms)
					j.id = setTimeout(j.doPoll, j.timex);
				}
			}});
		} else if(!j.url){
			iwb.app.alert('Long Poll not initialized');
		}
	},
	retryTimer:function(){
		var j = iwb.longPoll;
		j.retryTimerCount--;
		if(j.retryTimerCount>0){
			$$('#idx-timer').html(j.retryTimerCount);
			if(j.started && !j.id)setTimeout(j.retryTimer, 1000);
			else iwb.app.hidePreloader();
		} else {
            iwb.app.hidePreloader();
			if(j.started && !j.id)j.id = setTimeout(j.doPoll, 0);
		}
	}
}

function iwbLP(jj){
	if(!jj)return;
	if(jj.userChatMsg){ //{"msg":"naber","userChatMsg":"true","userDsc":"Veli Öztürk","success":"true","userId":10}
		if(mainView.activePage && mainView.activePage.name.indexOf('messages-')==0){
			var otherUserId = 1*mainView.activePage.name.substr('messages-'.length);
			if(otherUserId==jj.userId){
				iwb.request({url:'ajaxQueryData?_qid=1618&xother_user_id='+jj.userId, data:{xmax_chat_id:iwb.maxChatId || 0, limit:20}, success: function (j) {
					if(j.data.length)iwb.maxChatId=j.data[j.data.length-1].chat_id;
					else return;
					var lastName2=iwb.chat && iwb.chat.lastName ? iwb.chat.lastName:'-';
					var lastDay2=iwb.chat && iwb.chat.lastDay ? iwb.chat.lastDay:'-';
						
					for(var qi=0;qi<j.data.length;qi++){
						if(lastDay2!=j.data[qi].day)lastDay2=j.data[qi].day;
						else j.data[qi].day=null;
						if(lastName2!=j.data[qi].name)lastName2=j.data[qi].name;
						else j.data[qi].name=null;
						
						if(1*j.data[qi].sender_user_id!=_scd.userId){
			                j.data[qi].isRec=1;
							j.data[qi].avatar=iwb.serverUrl+'sf/pic'+j.data[qi].sender_user_id+'.png';
						}
						
						if(qi<j.data.length-1)j.data[qi].label=null;
					}
					iwb.chat.newMsg=true;
					iwb.chatMessages.addMessages(j.data, 'append', true);
				}});
				return;
			}
		}
		iwb.app.addNotification({closeOnClick:true,title:user4chat(jj), message: iwb.mobile!=2 ? jj.msg : (user4chat(jj) + ' ' + jj.msg), hold: 5000, media:user4media(jj), json:jj, onClick:function(m){
			open4chatAnother(this.json);
		}});
		iwb.addNewChatMsg(jj.userId);
		iwb.updateNewMsgBadge(!0)
	} else 	if(jj.formBuilder){ //{"msg":"naber","userChatMsg":"true","userDsc":"Veli Öztürk","success":"true","userId":10}
		if(mainView.activePage && mainView.activePage.name.indexOf('form-builder-page')==0){
			var formData = iwb.app.formToData('#idx-form-builder');
			if(jj.formBuilder.htmlPage)$$('#idx-form-builder-data').html(Template7.compile(jj.formBuilder.htmlPage)(formData));
			if(jj.formBuilder.init)jj.formBuilder.init(jj);
		}
	} else if (jj.liveSyncAction) switch(jj.liveSyncAction){
	case	 17: //chat typing
		break;
	case	15:  //record changed in a list
		if(jj.tabId.indexOf('-l-')>=0){
			var listId = 1*jj.tabId.split('-l-')[1];
			var rec = $$('#idx-'+listId+' .iwb-link-'+listId+'[iwb-key="'+jj.key+'"]');
			var parent=rec.parent();jj.actionTime = new Date().getTime();
			rec.data('iwb-action',jj);
			switch(1*jj.crudAction){
			case	1:
				parent.css('background','rgba(131, 66, 66, .2)');
				break;
			case	3:
				parent.css('background','rgba(255,0,0,.5)');
				break;
			}
			parent.animate({'opacity': .6},{duration: 600,easing: 'swing'});
//			parent.css('opacity','.5');
			iwb.app.addNotification({closeOnClick:true,title:user4chat(jj), message: iwb.m4a(jj)+'Listedeki bir kaydı ' + ['','düzenlemiştir','','silmiştir'][1*jj.crudAction], media:user4media(jj), hold:3000}); // TODO
		}
		break;

	case	10:  //record opened for update// ' az önce incelemeye baslamistir.'
		setTimeout(function(jj){
			return function(){iwb.app.addNotification({closeOnClick:true,title:user4chat(jj), message: iwb.m4a(jj)+'Bu kaydı az önce incelemeye baslamistir.', media:user4media(jj), hold:3000});
		}}(jj), 700);
		iwb.liveSyncRecord.add([jj]);

		break;
		
	case	1:  //updated
		iwb.app.addNotification({closeOnClick:true,title:user4chat(jj), message: iwb.m4a(jj)+'Bu kaydı az önce guncellemiştir!', media:user4media(jj)}); // TODO
		iwb.liveSyncRecord.remove(jj,1);

		break;
	case 3: //record deleted
		iwb.app.addNotification({closeOnClick:true,title:user4chat(jj), message: iwb.m4a(jj)+'Bu kaydı az önce silmiştir!', media:user4media(jj)}); // TODO
		iwb.liveSyncRecord.remove(jj,3);
		break;
	case	2://page closed :'Bu Kayit ' + lb._text;
//		if (tb) Ext.infoMsg.msg(getLocMsg('js_notification'), getUsers4Chat([json]) + ' bu kaydi incelemekten vazgecmistir', 2);
		iwb.app.addNotification({closeOnClick:true,title:user4chat(jj), message: iwb.m4a(jj)+'Bu kaydi incelemekten vazgecmistir ;)', media:user4media(jj), hold:3000}); // TODO
		iwb.liveSyncRecord.remove(jj);
		break;
	case	4://formCell focused
		iwb.liveSyncRecord.highlight(jj);
	}
	
}
iwb.iwbLP=iwbLP;

iwb.m4a=function(j){
	return iwb.mobile==2 ? user4chat(j) + ' ' : '';
}

function liveSyncHtml(l){
	var s=users4chat(l);
	if(!s)return '';
	return 'Bu kayıt şu anda ' + s + ' tarafından incelenmektedir.';
}
iwb.liveSyncHtml=liveSyncHtml;

iwb.animate=function(o, t, h, d, c){ //object, type, height/zoom, duration, onComplete
	switch(t){
	case 'jump':
	o.animate({'margin-top': -h},{duration: d,easing: 'swing'}).animate({'margin-top': 0},{duration: 2*d/3,easing: 'linear'})
	.animate({'margin-top': -h/3},{duration: d/3,easing: 'swing'}).animate({'margin-top': 0},{duration: d/7,easing: 'linear', complete:function(){
		if(c)c();
	}});
	break;
	case	'zoom':
	o.animate({'zoom': h},{duration: d,easing: 'swing'}).animate({'zoom': 1},{duration: d/4,easing: 'linear', complete:function(){
		if(c)c();
	}});
	break;
	}
}

iwb.liveSyncRecord={
		fId:false, //tbId: false, tbPk: false, 
		add:function(l){
			if(!iwb.liveSyncRecord.fId || !l || !l.length)return;
			var nb=$$('.views .pages div[data-page="iwb-form-'+iwb.liveSyncRecord.fId+'"] .navbar-inner .right');

			var s='';
			for(var qi=0;qi<l.length;qi++){
				var nb2=nb.find('.iwb-user-chat[iwb-key="'+l[qi].userId+'"]');
				if(nb2 && nb2.length){
					l[qi].userId = false;
				} else
					s+='<a href=# style="opacity:1;padding-top: 5px; margin-left:0px; padding-right: 10px; zoom: .01;" class="item-link iwb-user-chat" iwb-dsc="'+l[qi].userDsc+'" iwb-key="'+l[qi].userId+'" ><img class="ppic-mini2" src="' + iwb.serverUrl + 'sf/pic' + l[qi].userId + '.png">';
			}
			if(!s)return;
			nb.append(s);
			attachUserChat();
			for(var qi=0;qi<l.length;qi++)if(l[qi].userId){
				var nb2=nb.find('.iwb-user-chat[iwb-key="'+l[qi].userId+'"]');
				nb2.animate({'zoom': 1},{duration: 30,easing: 'swing'}).animate({'margin-top': -10},{duration: 75,easing: 'swing'}).animate({'margin-top': 0},{duration: 50,easing: 'linear'})
				.animate({'margin-top': -3},{duration: 20,easing: 'swing'}).animate({'margin-top': 0},{duration: 10,easing: 'linear'});
			}
		},
		remove:function(j,a){ //action
			if(!iwb.liveSyncRecord.fId || !j)return;
			var nb=$$('.views .pages div[data-page="iwb-form-'+iwb.liveSyncRecord.fId+'"] .navbar-inner .right a[iwb-key="'+j.userId+'"]');
			/*nb.animate({'opacity': .01},{duration: 300,easing: 'swing'},{'zoom': .1}).animate({duration: 30,easing: 'linear', complete: function (aq,bq,cq) {
				console.log('this');console.log(this);console.log(aq);console.log(bq);console.log(cq);
                this.remove();
            }}); */
			if(nb && nb.length)nb.remove();
			if(a)switch(a){
			case	1://updated
				var nb=$$('.views .pages div[data-page="iwb-form-'+iwb.liveSyncRecord.fId+'"] .navbar').css('background','rgb(131, 66, 66)');
				break;
			case	3://deleted
				var nb=$$('.views .pages div[data-page="iwb-form-'+iwb.liveSyncRecord.fId+'"] .navbar').css('background','red');
				
			}
		},
		highlight:function(j){
			if(!iwb.liveSyncRecord.fId || !j)return;
			var nb=$$('.views .pages div[data-page="iwb-form-'+iwb.liveSyncRecord.fId+'"] .navbar-inner .right a[iwb-key="'+j.userId+'"]');
			if(!nb.length){
				iwb.liveSyncRecord.add([j]);
			} else 
				iwb.animate(nb, 'jump', 10, 75);
		}
		

}



$$('#idx-logout-1').on('click', function(){
	iwb.app.confirm('Are you sure you want to exit?', 
		function(){
			iwb.request({url:'ajaxLogoutUser?d=1&.r='+Math.random(),
				success:function(j){
					if(iwb.debug)alert('Hata. Buraya girmemeliydi')
					_scd = false;
					iwb.longPoll.end();
					if(window.localStorage){
						var ls = window.localStorage;
						var rePassWord = ls.getItem("passWord");
						if(rePassWord)ls.setItem("passWord","");
					}
					iwb.app.loginScreen();
				}, error:function(){
					_scd = false;
					iwb.longPoll.end();
					if(window.localStorage){
						var ls = window.localStorage;
						var rePassWord = ls.getItem("passWord");
						if(rePassWord)ls.setItem("passWord","");
					}
					iwb.app.loginScreen();
					
				}});
	});
});



$$('#idx-login-settings').on('click', function(){
    iwb.app.prompt('Yeni sunucu adresi', function (data) {
    	 $$.ajax({
    	        url: data + 'ajaxPing?.r=' + Math.random(),
    	        method: 'GET',
    	        dataType: 'json',
    	        success: function (j) {
    	        	iwb.serverUrl = data;
    	    		if(window.localStorage){
    	    			window.localStorage.setItem('serverUrl', data);
    	    		}
    	            genTplOUsers();
    	        },
    	        error: function(){
    	        	iwb.cleanProgresAndIndicators();
    	    		iwb.app.addNotification({closeOnClick:true,title:'Bağlantı Hatası', message: 'Verilen Bağlantıya Ulaşılamıyor', media:info4media(2)}); // TODO
    	        	$$('#idx-login-settings').trigger('click');
    	        }
    	 });
    });
    $$('.modal-text-input').val(iwb.serverUrl);
});

$$('#idx-login-help').click( function() {
          cordova.plugins.barcodeScanner.scan(
          function (result) {
              alert("We got a barcode\n" +
                    "Result: " + result.text + "\n" +
                    "Format: " + result.format + "\n" +
                    "Cancelled: " + result.cancelled);            
          }, 
          function (error) {
              alert("Scanning failed: " + error);
          });
        }
);


/*
if(window.localStorage){
	var ls=window.localStorage;
	if(ls.getItem('serverUrl')){
		iwb.serverUrl=ls.getItem('serverUrl');
	}
}*/

iwb.getNewMsgCnt=function(){
	var cnt=0;
	for(var k in iwb.newMsgCnt)if(iwb.newMsgCnt[k])cnt++;
	return cnt;
}
iwb.addNewChatMsg=function(u){
	if(!iwb.newMsgCnt[u])iwb.newMsgCnt[u]=1;
	else iwb.newMsgCnt[u]++;
	var cb=$$('#idx-cuser-'+u+' .badge');
	if(cb && cb.length){
		cb.html(iwb.newMsgCnt[u]>9 ? '9+':iwb.newMsgCnt[u]);
		cb.show();
		cb.animate({'zoom': 1.5},{duration: 100,easing: 'swing'}).animate({'zoom': 1},{duration: 25,easing: 'swing'});
	}
}

iwb.updateNewMsgBadge=function(a){
	var cd=$$('#idx-chat-badge');
	var n = iwb.getNewMsgCnt(); 
	if(iwb.debug)console.log('iwb.updateNewMsgBadge: ' + cd.length + ' / ' + n);
	if(cd && cd.length){
		if(n){
			cd.html(n);
			cd.show();
			if(a && iwb.chatMsgCount<n){
				cd.animate({'zoom': 1.5},{duration: 100,easing: 'swing'}).animate({'zoom': 1},{duration: 25,easing: 'swing'});
			}
		} else {
			cd.hide();
		}
	}
	iwb.chatMsgCount=n;
}

iwb.updateBackendMobileStatus=function(s){
	iwb.request({url:'ajaxLiveSync?.a=102&.s='+s+'&.r'+Math.random(), success:function(){}});
}

iwb.chatAction = function(aq){
	if(iwb.debug){console.log('iwb.chatAction');console.log(aq);console.log(iwb.chat);}
	if(!iwb.chat)return;
	iwb.newMsgCnt[iwb.chat.otherUserId]=0;
	if(iwb.chat.newMsg){
		iwb.chat.newMsg=false;
		if(aq){
			$$('#idx-list-14 .new-message').removeClass('new-message');
		} else {
			setTimeout(function(){$$('#idx-list-14 .new-message').removeClass('new-message');},2000);
		}
		var otherUserId = 1*mainView.activePage.name.substr('messages-'.length);
		iwb.request({
			url: 'ajaxNotifyChatMsgRead?u='+otherUserId, 
			data: { m: 1 },
			success: function() {
				iwb.newMsgCnt[otherUserId]=0;
				iwb.updateNewMsgBadge();
			}
		});
	}
}

/*
function attachMenu(il, json){
	if(!il || !il.length || !json)return;
	var json3=json;

	il.off('click');
	il.on('click', function () {
		var iwbAction = this.attributes['href'];
		if(iwbAction && iwbAction.value){
			alert('iwbAction ' + iwbAction);
			return;
			parent.animate({'zoom': 1.5},{duration: 100,easing: 'swing'}).animate({'zoom': 1},{duration: 25,easing: 'swing'});
		}
		var pk=this.attributes['iwb-key'].value;
		var clickedLink = this;
		if(iwb.debug){console.log('click');console.log(json3);console.log(mainView.activePage);}
		var lnk ='', href=false;
		if(json3.crudFlags){
			if(json3.crudFlags.edit){
				href='showMForm?a=1&_fid='+json3.crudFormId+'&'+json3.pkName+'='+pk+'&.r='+Math.random()+'&.w=' + iwb.webPageId;
				lnk+='<li><a href="'+href+'" class="item-link item-content close-popover"><div class="item-inner" style="background-image:none;"><div class="item-title"><i class="f7-icons" style="font-size: 18px;color: #027eff;">compose</i> &nbsp; Edit</div></div></a></li>';
			}
			if(json3.crudFlags.remove){
				lnk+='<li><a href="#" id="idx-confirm-delete-'+json3.crudFormId+'" class="item-link item-content close-popover"><div class="item-inner" style="background-image:none;color:red;"><div class="item-title"><i class="f7-icons" style="font-size: 18px;">delete_round</i> &nbsp; Delete</div></div></a></li>';
				href=true;
			}
		}
		
		if(json3.recordButtons)for(var qi=0;qi<json3.recordButtons.length;qi++){
			var bt=json3.recordButtons[qi];
			lnk+='<li><a href="'+(bt.href ? bt.href+pk+'&.r='+Math.random() : '#')+'" class="item-link item-content close-popover"><div class="item-inner"><div class="item-title">';
			if(bt.icon)lnk+='<i class="f7-icons" style="font-size: 17px;color: #027eff;">'+bt.icon+'</i> &nbsp '; 
			lnk+=bt.text+'</div>';
			if(bt.badge)lnk+='<div class="item-after">'+bt.badge+'</div>';
			lnk+='</div></a></li>';
			href=true;
		}
		if(lnk){
			if(href===true){
				iwb.app.popover('<div class="popover"><div class="popover-inner"><div class="list-block"><ul>'+lnk+'</ul></div></div></div>', clickedLink);
				if(json3.crudFlags && json3.crudFlags.remove){
					var pp = $$('#idx-confirm-delete-'+json3.crudFormId);
					pp.off('click');
					pp.on('click', function(){
						iwb.app.confirm('Silmek istediğinizden emin misiniz?', function(){
							alert('TODO');return;
							iwb.request({url:'ajaxPostForm?a=3&_fid='+json3.crudFormId+'&'+json3.pkName+'='+pk+'&.r='+Math.random()+'&.w=' + iwb.webPageId, success:function(){
								alert('silindi');
							}});
						});
					});
				}
			} else  mainView.router.loadPage(href);
		}
	});
}
*/

function attachMenu(il, json){
	if(!il || !il.length || !json)return;
	var json3=json;

	il.off('click');
	il.on('click', function () {
		var iwbAction = $$(this).data('iwb-action');
		var pk=this.attributes['iwb-key'].value;
		if(iwbAction)switch(1*iwbAction.crudAction){
		case	1://edit
			iwb.app.addNotification({closeOnClick:true,title:user4chat(iwbAction), message: iwb.m4a(iwbAction)+'This record was ' + ['','updated','','deleted'][1*iwbAction.crudAction] + '. Pull to refresh.', media:user4media(iwbAction), hold:3000}); // TODO
//			iwb.app.alert('Lütfen önce listeyi tazeleyiniz', '<b style="color:red;">Listenin İçeriği Değişti</b>');
			return;
		case	3://remove
			$$(this).parent().remove();
			return;
		}
		var clickedLink = this;
		if(iwb.debug){console.log('click');console.log(json3);console.log(mainView.activePage);}
		var lnk =[], href=false;
		if(json3.crudFlags){
			if(json3.crudFlags.edit){
				href='showMForm?a=1&_fid='+json3.crudFormId+'&'+json3.pkName+'='+pk+'&.r='+Math.random()+'&.w=' + iwb.webPageId;
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
				iwb.app.popover('<div class="popover"><div class="popover-inner"><div class="list-block"><ul>'+lnk.join('')+'</ul></div></div></div>', clickedLink);
				if(json3.crudFlags && json3.crudFlags.remove){
					var pp = $$('#idx-confirm-delete-'+json3.crudFormId);
					pp.off('click');
					pp.on('click', function(){
						iwb.app.confirm('Areyou sure you want to delete?', function(){
							alert('TODO');return;
							iwb.request({url:'ajaxPostForm?a=3&_fid='+json3.crudFormId+'&'+json3.pkName+'='+pk+'&.r='+Math.random()+'&.w=' + iwb.webPageId, success:function(){
								alert('silindi');
							}});
						});
					});
				}
				var pp2 = $$('.iwb-ab-'+json3.listId);
				pp2.off('click');
				pp2.on('click', function(aq,bq,cq){
					var idx=1*this.attributes['iwb-key'].value;
					json3.recordButtons[idx].click(pk);
				});
			} else  {
				if(href)mainView.router.loadPage(href);
				else json3.recordButtons[0].click(pk);
			}
		}
	});
}

window.onbeforeunload = function() {
	iwb.request({ url: 'ajaxLiveSync?.a=101&.r='+Math.random() , data:{".w":iwb.webPageId}, success: function() {} });
};


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