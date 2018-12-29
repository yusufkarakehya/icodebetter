iwb.mobile=2;
iwb.longPoll.strategy=0;



iwb.onPause=function(aq){//asagi atilinca
	   setTimeout(function() {
//			iwb.longPoll.end();
			iwb.updateBackendMobileStatus(2);
		}, 0);
}

iwb.onResume=function(aq){//uyaninca
   setTimeout(function() {
//		iwb.request({url:iwb.serverUrl+'ajaxMobileStatus?s=1&.r'+Math.random(), success:function(d){if(d.success)}});
		iwb.checkSession();        
	}, 0);
}

iwb.onMenuKeyDown=function(aq,bq,cq){//burda: bulamadim nerde?
/*	alert('onMenuKeyDown');
	alert(iwb.objProp(aq));	
	alert(iwb.objProp(bq));	
	alert(iwb.objProp(cq));	*/
}

iwb.onBack=function(aq){//android
	mainView.router.back();
}

iwb.onSearchKeyDown = function(aq) {
	//alert('onSearchKeyDown');
}

iwb.getGeoPosition=function(){
	var options = {enableHighAccuracy: true,maximumAge: 3600000};
	var onSuccess = function(position) {
		iwb.position=position;
	    /*alert('Latitude: '          + position.coords.latitude          + '\n' +
	          'Longitude: '         + position.coords.longitude         + '\n' +
	          'Altitude: '          + position.coords.altitude          + '\n' +
	          'Accuracy: '          + position.coords.accuracy          + '\n' +
	          'Altitude Accuracy: ' + position.coords.altitudeAccuracy  + '\n' +
	          'Heading: '           + position.coords.heading           + '\n' +
	          'Speed: '             + position.coords.speed             + '\n' +
	          'Timestamp: '         + position.timestamp                + '\n'); */
	};
	
	// onError Callback receives a PositionError object
	//
	function onError(error) {
	    if(iwb.debug)iwb.app.alert('code: '    + error.code    + '\n' +
	          'message: ' + error.message + '\n');
	}
	
	navigator.geolocation.getCurrentPosition(onSuccess, onError, options);
}

function onDeviceReady(){
	iwb.senderId='553372575530';
	iwb.apiKey='AIzaSyBTET2hfQa_6AGQy5ErILBz9IFBAF3tx3E';
//	iwb.gcmId=iwb.webPageId;//'dtGoRPDEjQk:APA91bE_7I-pnCHsCGdMA4nCXd83B7yaoS5Ald2XLDBhVEjL4r7ycc0NpQkyD35AKr6amtXyzLQcykPdGiLhXh_9QcDVqPchtSN2LXzxLZqG9Mkt3NgNW60-3gTf39xh8Bf9rJH8_OG5';
//	iwb.deviceId=iwb.gcmId;
	
	try{
		
		document.addEventListener("pause", iwb.onPause, false);
		document.addEventListener("resume", iwb.onResume, false);
		document.addEventListener("menubutton", iwb.onMenuKeyDown, false);
		document.addEventListener("backbutton", iwb.onBack, false);
		document.addEventListener("searchbutton", iwb.onSearchKeyDown, false);

		iwb.camera = !!navigator.camera;
		if(navigator.geolocation)setTimeout(iwb.getGeoPosition, 3000);
		var push = PushNotification.init({ "android": {"senderID": iwb.senderId} /*, "ios": {"sound": true, "vibration": true, "badge": true}*/ });
		 	push.on('registration', function(data) {
//				iwb.gcmId=data.registrationId;
				iwb.deviceId=data.registrationId;
				iwb.webPageId=data.registrationId;
//				iwb.longPoll.init('../async/ajaxNotifications?&.w=' + iwb.webPageId, iwbLP);
//				if(iwb.debug){console.log('iwb-push-registration');console.log(data.registrationId);}
				iwb.checkSession({callback:iwb.prepareMainMenu});
		});
	
		 push.on('notification', function(data) {
			 if(data){
				 if(!data.success && data.title && data.message)iwb.app.addNotification(data);
				 else iwb.iwbLP(data);
			 }
//			 iwb.app.addNotification({title:'PUSH! ' + data.title,message: data.message});
		 });
	
		 if(iwb.debug)push.on('error', function(e) {
			 if(iwb.debug)iwb.app.addNotification({closeOnClick:true,title:'<b style="color:red;">Push Error</b>',message: 'onError: ' + e, media:info4media(2)});
		 });
	} catch(e){
		if(iwb.debug)iwb.app.addNotification({closeOnClick:true,title:'<b style="color:red;">Push Error</b>',message: 'onDeviceReady: ' + e, media:info4media(2)});
		iwb.checkSession({callback:iwb.prepareMainMenu});
	}
}
document.addEventListener("deviceready",onDeviceReady,false);

// Init App
iwb.app = new Framework7({
    modalTitle: iwb.logo(40), cache:false,animatePages:false,sortable:false,//swipeout:false,
    material: true, materialPageLoadDelay:100,
    preprocess: function (content, url, next, aq) {
    	if(iwb.debug)console.log('preprocess: ' + url);
    	//console.log(content);console.log(url);console.log(next);console.log(aq);
    	if(url && url.indexOf('showM')>-1)try{
    		var j = eval('('+content+')');
    		if(j){
//    			if(j.baseParams && j.baseParams._t)iwb.removeRequest(j.baseParams._t, 'preprocess');
    			if(j.success){
    				iwb.last={url:url, json:j};
    				var r=null;
    				if(j.init)r=j.init({url:url, next:next, json:j});
    				if(r===false)return;
    				return r || j.htmlPage;
    			} else switch(j.errorType){
    			case	'session':
//    				iwb.longPoll.end();
    				iwb.app.loginScreen();
    				return;
    			default:
//    				iwb.app.alert(j.error, j.errorType);
    				iwb.showResponseError(j);
    				return;
    			}
    		}  
		}catch(e){if(iwb.debug && confirm('ERROR form.JS!!! Throw? ' + e.message))throw e;}
    	return content;
    },
    preroute: function (view, options) {
    	if(iwb.debug)console.log('preroute: ' + options.url);
    	if(iwb.serverUrl && options.url && (options.url.indexOf('showM')==0/* || options.url.indexOf('ajax')==0*/)){
    		var url=options.url;
//    		var t = iwb.addRequest(false);
            view.router.load({url:iwb.serverUrl+url+'&.r='+Math.random(), reload:url.indexOf('showM')==0 && url.indexOf('_reload=1')>-1,force: true, ignoreCache:true,animatePages:true}); //load another page with auth form
            return false; //required to prevent default router action
    	}
    }
});



//Add main view
var mainView = iwb.app.addView('.view-main', {
});
//Add another view, which is in right panel
var rightView = iwb.app.addView('.view-right', {
});

//Show/hide preloader for remote ajax loaded pages
//Probably should be removed on a production/local app

var loading = false;


function recMenu(r, lvl){
	if(!r || !r.length)return '';
	var s='';
	if(!lvl)lvl=0;
	for(var qi=0;qi<r.length;qi++)if(r[qi].children){//submenu style="font-size: 13px;color:green;"
		s+='<li class="accordion-item iwb-menu-folder"><a href="#" class="item-link item-content" ><div class="item-inner">';
		if(r[qi].icon)s+='<div class="item-media"><i class="f7-icons">'+r[qi].icon+'</i></div>';
		s+='<div class="item-title">'+r[qi].text+'</div></div></a><div class="accordion-item-content iwb-menu-url">'+
		'<div class="list-block" style="font-size:15px;"><ul>'+recMenu(r[qi].children, lvl+1)+'</ul></div></div>';
	} else {
		var href=r[qi].href;
		s+='<li><a href="'+href+'&_reload=1"'+ (href=='#' ? (' id="'+r[qi].id+'"'):'') +' class="item-link close-panel'+(!lvl ? '  iwb-menu-murl':'')+'"><div class="item-content"><div class="item-inner"'+(!lvl ? ' style="background-image:none;"':'')+'>';
		s+='<div class="item-title">';
		if(r[qi].icon)s+='<i class="f7-icons iwb-mmenu-icon">'+r[qi].icon+'</i>&nbsp; ';
		else if(lvl)for(var ji=0;ji<lvl;ji++)s+=' ·&nbsp; ';
		s+=r[qi].text+'</div></div></div></a></li>';
	}
	return s;	
}

iwb.prepareMainMenu=function(){
	iwb.request({url:'ajaxQueryData?_qid='+ (_scd.mobileMenuQueryId || 1487)+'&.r='+Math.random(),dataType:'text',data:{_json:1, xuser_tip:typeof xuserTip!='undefined' && xuserTip ? xuserTip:_scd.userTip}, success:function(d){
//		if(iwb.debug){console.log('prepareMainMenu');console.log(d);}
		var j = eval('('+d+')');
		if(_scd && _scd.roleDsc)$$('#idx-main-menu-label').html( _scd.roleDsc);
		var lmid = 'idx-logout-' + new Date().getTime();
//		j.data.push({icon:'logout',text:'Logout',href:'#',id:lmid});
		$$('#idx-main-menu').html(recMenu(j.data));
		if(!iwb.home){
			iwb.home = true;
			mainView.router.loadPage('showMList?_lid='+(_scd.homeListId || 15)+'&_reload=1&.r='+Math.random());
		}
	}}); 
}

iwb.photoBrowser=function(tid,tpk){
	iwb.request({url:'ajaxQueryData?_qid=1357', data:{xtable_id:tid, xtable_pk:tpk}, success:function(j){
	   for(var qi=0;qi<j.data.length;qi++)j.data[qi].url=iwb.serverUrl+j.data[qi].url;
	   iwb.app.photoBrowser({
		    photos: j.data,theme: 'dark'
			    ,lazyLoading: true
			}).open();; 		   
	}});
} 

function attachListPhoto(lp, json){
	if(!lp || !lp.length || !json)return;
	var json7=json;
	lp.off('click');
	lp.on('click', function () {
		var pk=this.attributes['iwb-key'].value;
		iwb.photoBrowser(json7.crudTableId, pk);
	});
}



iwb.cameraClearCache=function() {
	if(navigator.camera)navigator.camera.cleanup();
}
iwb.photoFail=function(message) {
	iwb.hideProgressbar('photo');
	iwb.app.alert('Foto Yükleme Başarısız: ' + message);
}

iwb.takePhoto=function(tid,tpk, jsonX, fromAlbum, profilePictureFlag){
	if(navigator.camera){
		var options = iwb.apply({ quality: 50, destinationType: Camera.DestinationType.FILE_URI}, jsonX.cameraOptions || {});
		if(fromAlbum)options.sourceType=Camera.PictureSourceType.SAVEDPHOTOALBUM;
		navigator.camera.getPicture(function(fileURI) {
			 	var win = function (r) {
			 		iwb.hideProgressbar('photo');
				 	iwb.cameraClearCache();
				 	if(jsonX.photoSuccess && jsonX.photoSuccess(r)===false)return;
				 	if(jsonX){
				 		jsonX.pictureCount++;
						var bd=$$('#idx-photo-badge-'+jsonX.formId);
						if(bd.length){
							if(jsonX.pictureCount>1)bd.html(jsonX.pictureCount);
							else bd.show();
							iwb.animate(bd, 'jump', 7, 75);
//							bd.animate({'zoom': 2.5},{duration: 200,easing: 'swing'}).animate({'zoom': 1},{duration: 50,easing: 'swing'});
						}
				 	}
				 	iwb.app.addNotification({closeOnClick:true,message: 'Foto Yüklendi', hold:5000, title:'Bilgi', media:info4media(0)});
			    }
			 
			    var fail = function (error) {
			    	iwb.hideProgressbar('photo');
				 	iwb.cameraClearCache();
				 	iwb.app.alert('Hmmm... Olmaması Gereken Birşey oldu!');
			    }
			 
			    var params = new FileUploadOptions();
			    params.fileKey = "file";
			    params.fileName = fileURI.substr(fileURI.lastIndexOf('/') + 1);
			    params.mimeType = "image/jpeg";
			    params.params = {table_id:tid, table_pk:tpk, profilePictureFlag:profilePictureFlag||0, file_type_id:-997}; // if we need to send parameters to the server request
			    var ft = new FileTransfer();
		        iwb.showProgressbar('photo');
			    ft.upload(fileURI, encodeURI(iwb.serverUrl + 'upload.form'), win, fail, params);
		}, iwb.photoFail, options);
	} else iwb.app.alert('Kamera Tanımlı Değil');
}

function info4media(j){
	if(j)switch(j){
	case	1:
		return '<span class="icon f7-icons" style="color: rgba(255, 152, 0, 1);font-size: 18px;">bell</span>';
	case	2:
		return '<span class="icon f7-icons" style="color: red;font-size: 18px;">bell_fill</span>';
	case	3:
		return iwb.logo(30);
	}
	return '<span class="icon f7-icons" style="color: #007aff;font-size: 18px;">info</span>';
}



//<div class="item-media"><img src="http://lorempixel.com/160/160/people/1" width="80"/></div>
//{{#if user_role}}{{else}} style="height:43px;"{{/if}} <img src="'+iwb.serverUrl+'sf/pic{{user_id}}.png" class="ppic-mini"> 
//style="padding-left: 6px;border-bottom: rgba(0,0,0,0.1) 1px dotted;" 

function genTplOUsers(){
    var t='{{#each data}}'+
    '<li id="idx-cuser-{{user_id}}"{{#if instance_count>0}} style="background-color: rgba(255,255,255,.05);"{{/if}}><a href=# style="padding-left: 4px;" class="iwb-user-chat item-link item-content" iwb-key="{{user_id}}" iwb-dsc="{{adi_soyadi}}" >'+
    '<div class="item-media"><img src="'+iwb.serverUrl+'sf/pic{{user_id}}.png" class="ppic-mini3">{{#if chat_status_tip>0}}<svg height="12" width="12" style="margin-top:27px;margin-left:-10px"><circle fill="#09c740" stroke-width=".5" stroke="white" r="4" cy="6" cx="6"></circle></svg>{{/if}}</div>'+
    '<div class="item-inner" style="padding-right: 8px;margin-bottom: 0px;border-bottom: 1px solid rgba(150,150,150,.1);margin-left: 10px;{{#if user_role}}padding-top: 5px;{{/if}}">'+
    '<div class="item-title-row" style="background-image:{{#if mobile>0}}url(images/mobile.png) !important;background-size: 14px 20px;{{else}}none{{/if}};font-size: {{#if user_role}}15{{else}}16{{/if}}px;">{{adi_soyadi}}'+
    '&nbsp; <span class="badge bg-red" style="{{#if instance_count<1}}display:none;{{/if}}margin-top: 0px; border-radius: 12px; height: 19px; ">{{instance_count}}</span></div>'+
    '{{#if user_role}}<div class="item-text" style="font-size: 14px;color:#b6a487;height: auto;">{{user_role}}...</div>{{/if}}'+
    '</div></a></li>'+
    '{{/each}}';
    tplOUsers = Template7.compile(t);
}

//genTplOUsers();


$$('#idx-sfrm-pull').on('refresh', function (e) {
	if(globalSearchForm){
		$$('#idx-right-panel').html(globalSearchForm);
		iwb.app.pullToRefreshDone();
	} else {
		if(globalSearchFormJson && globalSearchFormJson.searchForm)globalSearchFormJson.searchForm.done=false;
		iwb.request({url:'ajaxQueryData?_qid=142&.r='+Math.random(), success: function (j2) {
			$$('#idx-right-panel').html(tplOUsers(j2));
			iwb.app.pullToRefreshDone();
			attachUserChat();
		}});
	}
});

$$('#idx-right-reload').on('click', function(e){
	if(globalSearchForm){
		var jsonA=globalSearchFormJson;
		var formData = iwb.app.formToData('#idx-right-form');
		jsonA.baseParams = iwb.apply(jsonA.baseParams, formData);
		formData = iwb.apply({},jsonA.baseParams);
		formData.limit=jsonA.pageSize;formData.start=0;
//		jsonA.formData= iwb.apply(iwb.apply({},jsonA.baseParams), formData);
		jsonA.pageStart=0;
		iwb.request({url:jsonA.dataUrl, data:formData, success: function (j) {
			iwb.app.closePanel('right');
			$$('#idx-'+jsonA.listId).html(iwb.tpls[jsonA.listId](j));
			loading = false;
			attachMenu($$('.iwb-link-'+jsonA.listId), jsonA);
		}});
	} else {
		iwb.app.alert('TODO: prompt for search in messages')
	}
});



function attachUserChat(){
	var iou = $$('.iwb-user-chat');
	iou.off('click', open4chat);
	iou.on('click', open4chat);
}


$$(document).on('pageInit', function (e) {
	var page = e.detail.page;
	if(iwb.debug){console.log('pageInit');console.log(page);}
	
	if(iwb.lastLiveSyncPk && (!page.name || page.name.indexOf('smart-select')<0) && (!page.name || page.name.indexOf('messages-')!=0) && page.id!='idx-list-14'){//TODO.hata var, gereksiz yere donuyor
//		iwb.request({url:'ajaxLiveSync?.a=2&.t=' + iwb.webPageId +  '-f-'+  iwb.liveSyncRecord.fId + '&.w=' + iwb.webPageId + '&.pk=' +  iwb.lastLiveSyncPk});
		iwb.lastLiveSyncPk = false;
	}

	if(iwb.last && page.url==iwb.last.url && !page.json){
		page.json=iwb.last.json;
	}
	iwb.last = false;
	
	if(page.json){
		var json = page.json;
		if(!json.baseParams)json.baseParams={};
		if(json.listId){
			iwb.updateNewMsgBadge();
			var ou=$$('#idx-o-users-'+json.listId);
			if(ou && ou.length){
				var json9 = page.json;
				ou.off('click');
				ou.on('click', function(e){
					if(json9 && json9.searchForm)json9.searchForm.done=false;
					iwb.request({url:'ajaxQueryData?_qid=142&.r='+Math.random(), success: function (j) {
//						$$('#idx-sfrm-navbar .navbar-inner').css({'background': 'linear-gradient(rgba(254, 241, 222,1), rgba(255, 255, 255, 0.901961))', 'color': 'black'});
						var pp=$$('.panel.panel-right');
						if(pp.hasClass('panel-cover')){pp.removeClass('panel-cover');pp.addClass('panel-reveal');}
						$$('#idx-sfrm-label').html('Sohbet');
						$$('#idx-right-reload-icon').html('search');
						$$('#idx-right-panel').html(tplOUsers(j));
						iwb.newMsgCnt={};
						for(var zi=0;zi<j.data.length;zi++)if(j.data[zi].instance_count>0)iwb.newMsgCnt[j.data[zi].user_id]=j.data[zi].instance_count;
						iwb.updateNewMsgBadge();
						globalSearchForm = false;
						attachUserChat();
						iwb.app.openPanel('right');
					}});
		
				});
			}
		}

		
		if(json.searchForm){
			var fp=$$('#idx-filter-'+json.listId);
			if(fp && fp.length){
				var json5=json;
				fp.off('click');
				fp.on('click', function(e){
					if(!json5.searchForm.done){
						$$('#idx-right-panel').html(json5.searchForm.htmlPage);
						if(json5.searchForm.init){
							json5.searchForm.init({json:json5, url:page.url});
						}
						json5.searchForm.done=true;
					}
//					$$('#idx-sfrm-navbar').css({'background-color':'darkgray'});
//					$$('#idx-sfrm-navbar .navbar-inner').css({'background': 'linear-gradient(rgba(240, 249, 255,1), rgba(255,255,255,.9))', 'color': 'black'});
					var pp=$$('.panel.panel-right');console.log('ahuu');console.log(pp);
					if(pp.hasClass('panel-reveal')){pp.removeClass('panel-reveal');pp.addClass('panel-cover');}
					$$('#idx-sfrm-label').html('Arama Kriterleri');
					$$('#idx-right-reload-icon').html('refresh');
					globalSearchForm = json5.searchForm.htmlPage;
					globalSearchFormJson = json5;
					iwb.app.openPanel('right');
				});
			}
		}
		if(json.crudFormId){
			var fi=$$('#idx-insert-'+json.crudFormId);
			if(fi && fi.length && (!json.crudFlags || !json.crudFlags.insert)){
				fi.remove();
			}
		} 
		if(json.listId){
			attachUserChat();
			var json8 = json;
			if(json8.postInit){//chat
				if(json8.postInit(json8)===false)return;
			}
			var pr=$$('#idx-page-content-'+json8.listId);
			if(pr && pr.length){
				pr.off('refresh');
				pr.on('refresh', function (e) {
					var data=iwb.apply({}, json8.baseParams);
					var tpl=iwb.tpls[json8.listId];
					if(!tpl){
						iwb.app.addNotification({closeOnClick:true,title:'Error',message: 'Template not found for: ' + page.url});
				        return;
					}
					if(json8.pageSize)data.limit=json8.pageSize;
					json8.pageStart=0;
					iwb.request({url:json8.dataUrl+'&.r='+Math.random(), data:data, success: function (j) {
						$$('#idx-'+json8.listId).html(tpl(j));
						iwb.app.pullToRefreshDone();
						loading = false;
						attachMenu($$('.iwb-link-'+json8.listId), json8);
						attachUserChat();
						if(json8.listTip==3)attachListPhoto($$('.iwb-list-photo-'+json8.listId), json8);
						//				        console.log('Page Refreshed: ' + json8.dataUrl);
					}});
					
				});

				pr.off('infinite');loading = false;
				pr.on('infinite', function () {
					// Exit, if loading in progress
					if (loading) return;
					loading = true;
					
					var tpl=iwb.tpls[json8.listId];
					if(!tpl){
						iwb.app.addNotification({closeOnClick:true,title:'Error',message: 'Template not found for3: ' + page.url});
				        return;
					}
					if(!json8.pageStart)json8.pageStart=0;
					json8.pageStart += json8.pageSize;
					var data=iwb.apply({},json8.baseParams);
					data.limit=json8.pageSize;data.start=json8.pageStart;
					iwb.request({url:json8.dataUrl+'&.r='+Math.random(), data:data, success: function (j) {
						var b = j.browseInfo;
						if(1*b.startRow==data.start){
							$$('#idx-'+json8.listId).append(tpl(j));
							loading = false;
							attachMenu($$('.iwb-link-'+json8.listId), json8);
							attachUserChat();
							if(json8.listTip==3)attachListPhoto($$('.iwb-list-photo-'+json8.listId), json8);

						}
					}});
				});
			}
			var sc=$$('#idx-sort-'+json8.listId);
			if(sc && pr.length){
				sc.off('click');
				sc.on('click', function (e) {
					var bs=[{text:'Sıralama', label: true}], od=json8.orderNames;
					for(var qi=0;qi<od.length;qi++){
						var mm= od[qi];
						var bq= json8.baseParams.sort && json8.baseParams.sort==mm.id;
						bs.push({text: (bq ? ('<b>'+mm.dsc+' ('+(json8.baseParams.dir=='ASC' ? 'Artan':'Azalan')+')</b>'):mm.dsc), onClick: function (d,e) {
							if(iwb.debug){console.log('sort-action-click');console.log(d);console.log(e);}
							if(!e.srcElement || !e.srcElement.innerText){
								alert('olmadi');return;
							}
							var t=e.srcElement.innerText, qq=false;
							for(var ji=0;ji<od.length;ji++)if(t.indexOf(od[ji].dsc)==0){
								qq=od[ji];
								break;
							}
							if(!qq){
								alert('olmadi2');return;
							}
							if(json8.baseParams.sort && json8.baseParams.sort==qq.id){
								json8.baseParams.dir= json8.baseParams.dir=='ASC' ? 'DESC':'ASC';
							} else {
								json8.baseParams.sort=qq.id;
								json8.baseParams.dir='ASC';
							}
							var data=iwb.apply({}, json8.baseParams);
							var tpl=iwb.tpls[json8.listId];
							if(!tpl){
								iwb.app.addNotification({closeOnClick:true,title:'Error',message: 'Template not found for: ' + page.url});
						        return;
							}
							if(json8.pageSize)data.limit=json8.pageSize;
							json8.pageStart=0;
							iwb.request({url:json8.dataUrl+'&.r='+Math.random(), data:data, success: function (j) {
								$$('#idx-'+json8.listId).html(tpl(j));
								iwb.app.pullToRefreshDone();
								loading = false;
								attachMenu($$('.iwb-link-'+json8.listId), json8);
								attachUserChat();
								if(json8.listTip==3)attachListPhoto($$('.iwb-list-photo-'+json8.listId), json8);
//						        console.log('Page Refreshed: ' + json8.dataUrl);
							}});
		                   }
						});
					}
					iwb.app.actions(bs);
				});
			}
				

			attachMenu($$('.iwb-link-'+json8.listId), json8);
	        if(json8.listTip==3)attachListPhoto($$('.iwb-list-photo-'+json8.listId), json8);

			
		} else if(json.formId){
			var json4=json;
			if(json4.postInit){
				if(json4.postInit(json4)===false)return;
			}
			var fr=$$('#iwb-submit-'+json4.formId);
			if(fr && fr.length){
				fr.off('click');
				fr.on('click', function (e) {
					var formData = iwb.app.formToData('#idx-form-'+json4.formId);
					iwb.request({url:'ajaxPostForm?.r='+Math.random(), data:iwb.apply(formData, json4.baseParams), success: function (j) {
						loading=false;
						if(j.msgs){
							for(var qi=0;qi<5 && qi<j.msgs.length;qi++)iwb.app.addNotification({closeOnClick:true, title:'Bilgi', media:info4media(0), message: j.msgs[qi], hold: qi==0? null: 2000});
							if(j.msgs.length>5)iwb.app.addNotification({closeOnClick:true,title:'Bilgi', media:info4media(0), message: '... ve diğer ' + (j.msgs.length-5) + ' adet', hold: 2000});
						} else 
							iwb.app.addNotification({closeOnClick:true,title:'Bilgi', media:info4media(0), message: 'Kaydedildi', hold:2000});
						if(json4 && json4.submitSuccess && json4.submitSuccess(json4, j)===false)return;
						mainView.router.back();
					}, error:function(j){
						if(j.errorType='validation'){
							for(var qi=0;qi<5 && qi<j.errors.length;qi++)iwb.app.addNotification({closeOnClick:true,title:'Hata',media:info4media(2), message: '<b style="color:red;">'+j.errors[qi].dsc + '</b> ' + j.errors[qi].msg, hold: qi==0? null: 2000});
							if(j.errors.length>5)iwb.app.addNotification({closeOnClick:true,title:'Hata',media:info4media(2),message: '... ve diğer ' + (j.errors.length-5) + ' adet', hold: 2000});

							/*"errorType":"validation",
							"errors":[{"id":"tarih","msg":"Boş değer olamaz","dsc":"Tarih"}]*/
						}
					}});
				});
			}

			var fp=$$('#idx-photo-'+json4.formId);
			if(fp && fp.length){
				fp.off('click');
				fp.on('click', function (e) {
					var buttons=[];
					//if(navigator.camera)
					if(json4.pictureCount)buttons.push([{ text: 'Foto Galeri (' + (json4.pictureCount)+')' ,
		                   onClick: function () {
		                	   iwb.photoBrowser(json4.crudTableId, json4.tmpId || getPk(json4.pk));
		                   }}]);
					if(iwb.camera)buttons.push([{text: 'Kamera ile çek', onClick: function () {
					                	   iwb.takePhoto(json4.crudTableId, json4.tmpId || getPk(json4.pk), json4);
					                   }
					               },{text: 'Galeriden yükle', onClick: function () {
					                	   iwb.takePhoto(json4.crudTableId, json4.tmpId || getPk(json4.pk), json4, true);
					                   }
					               }
					           ]);
					if(buttons.length)iwb.app.actions(buttons);
				});
			}
			if(json4.liveSync){
				iwb.lastLiveSyncPk = json4.crudTableId + '-'+ getPk(json4.pk);
				iwb.liveSyncRecord.fId=json4.formId;
//				if(iwb.longPoll.strategy==2)iwb.longPoll.start();
//				iwb.request({url:'ajaxLiveSync?.a=10&.t=' + iwb.webPageId + '-f-'+  iwb.liveSyncRecord.fId + '&.w=' + iwb.webPageId + '&.pk=' +  iwb.lastLiveSyncPk});
			}
			if(json4.liveSyncBy){
				iwb.app.addNotification({closeOnClick:true,title:user4chat(json4.liveSyncBy[0]), media:user4media(json4.liveSyncBy[0]),message:liveSyncHtml(json4.liveSyncBy), hold:8000});
				iwb.liveSyncRecord.add(json4.liveSyncBy);
			}

		}
		
	}
	

});


$$('#idx-main-menu-logo').html(iwb.logo(29));

genTplOUsers();

//iwb.longPoll.init('../async/ajaxNotifications?&.w=' + iwb.webPageId, iwbLP);
iwb.checkSession({callback:iwb.prepareMainMenu});
