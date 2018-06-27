<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Promis BMP</title>
<noscript>Javascript must be enabled!</noscript>
<link rel="shortcut icon" href="../images/custom/bmp.ico">    
<link rel="stylesheet" type="text/css" href="../styles/ext3.3/ext-all.css" />
<link rel="stylesheet" type="text/css" href="../styles/custom/promis.css" />
<style>
#outerLogin {
	position: absolute;
	top: 20%;
	left: -160px;
	width: 100%;
	height: 1px;
	overflow: visible;
}

#innerLogin {
  width: 1px;
  height: 1px;
  position: absolute;
  left: 50%;
}

.button-enter {
	background-image: url(../images/custom/icons/enter.png) !important;
}

.button-help {
	background-image: url(../images/custom/icons/help.png) !important;
}

a{
    color: #053799;
    text-decoration: none;
}
#footer{bottom:0px;height:35px;left:0px;width:100%;position:fixed;z-index:3;}
#footer #feedback{font-size:10px;}
#footer #mem_ft{margin-top:0;}

    
#mem_ft {border-top:1px solid #CCCCCC;font-size:10px;margin-top:1em;padding-top:0.5em;font-family:arial,helvetica,clean,sans-serif;text-align:center}
#mem_ft p {margin:0;padding:0;}
</style>
 
<!--   
<script type="text/javascript" src="../scripts/ext3.3/adapter/ext/ext-base-debug-w-comments.js"></script>        
<script type="text/javascript" src="../scripts/ext3.3/ext-all.js?v=${version}"></script>
-->
<script type="text/javascript" src="../scripts/ext3.3/adapter/ext/ext-base.js"></script>        
<script type="text/javascript" src="../scripts/ext3.3/ext-all.js"></script>
<script type="text/javascript" src="../scripts/ext3.3/lang/ext-lang-tr.js?v=${version}"></script>


<script>
${promis}

function loginUserRole(userRoleId,userCustomizationId){
	Ext.Ajax.request({
		url:"ajaxSelectUserRole?userRoleId="+userRoleId+"&userCustomizationId="+userCustomizationId,
		success:function(){
			Ext.Msg.wait('Sisteme giriş yapılıyor','Lütfen bekleyiniz...');
			//document.location='main.htm?.r='+new Date().getTime();
			document.location='main.htm';
		}
	});
}

function selectUserRole(roleCount,defaultUserCustomizationId){
	var gg=new Ext.grid.GridPanel(Ext.apply(grd_select_role1,{region: 'center',stripeRows: true,autoScroll:true,listeners:{rowdblclick:function(){loginUserRole(gg.getSelectionModel().getSelected().id, cb.getValue());}}}));
	var cb = new Ext.form.ComboBox({width: 200, x:170, y:15, valueField:'id',displayField:'dsc', store: new Ext.data.JsonStore({url:'ajaxQueryData?_=_', baseParams: {_qid:'824'}, root:'data', autoLoad:true, totalProperty:'browseInfo.totalCount',id:'id',fields:[{name:'dsc'},
				{name:'id',type:'int'}],listeners:{loadexception:promisLoadException, load: function(){cb.setValue(defaultUserCustomizationId);}}}), editable: false, typeAhead: false, mode:'local', triggerAction: 'all'});

	var pp = new Ext.Panel({region: 'north', layout:'absolute', height: 55, items: [new Ext.form.Label({text: 'Görünüm:', style:'font-size: 12px', x:100, y:17}), cb]});
	var wndy=new Ext.Window({
		modal:true,
		title:'Bağlanmak İstediğiniz Rolu Seçiniz',
		width: 500,
		height:300,
		border: false,
		layout: 'border',
		items:[pp,gg],
		buttons:[{text:'Seç',handler:function(){
		var sel = gg.getSelectionModel().getSelected()
		if(!sel){
		    alert('Önce birşeyler Seçmelisiniz')
		    return
		}
		loginUserRole(sel.id,cb.getValue());
		}}]
	});
	gg.store.reload();
	wndy.show();
}

function changePassword(roleCount, defaultUserCustomizationId){
	var f=null;
	var w=new Ext.Window({
		width:320,
		height:170,
		title:'Şifrenizi Değiştirmelisiniz',
		modal:true,
		closable:false,
		layout:'fit',
		items:[f=new Ext.form.FormPanel({
			layout:'form',
			labelAlign:'right',
			labelWidth:100,
			items:[pfrm_change_password.getExtDef().items[0].items]
		})],
		buttons:[{
			text:'Değiştir',
			handler:function(){
				var r=f.getForm().getValues();
				if (r.oldPassword && r.newPassword && r.reNewPassword && (r.reNewPassword==r.newPassword) && (r.oldPassword!=r.newPassword)){
					Ext.Ajax.request({
						url:'ajaxChangePassword',
						params:r,
						success:function(o){
							var res=Ext.decode(o.responseText);
							if (res.success){
								alert("İşlem başarı ile tamamlanmıştır.");
								w.destroy();
								if(roleCount<0){
									loginUserRole(-roleCount, defaultUserCustomizationId);
								} else if(roleCount>1) {
									selectUserRole(roleCount,defaultUserCustomizationId)
								}
							}else
								Ext.MessageBox.alert("HATA",res.error||"Bilinmeyen hata");
						},
						failure:function(){
							Ext.MessageBox.alert("HATA"," - Eski şifre veya yeni şifre boş olamaz.<br/> - Eski şifre ile yeni şifre için yazılan değerler aynı olamaz.<br/> - Yeni şifre için yazılan değerler birbirinden farklı olamaz.<br/> - Yeni şifre en az 4 karakter olmalıdır.");
						}
					});
				}else
				    Ext.MessageBox.alert("HATA"," - Eski şifre veya yeni şifre boş olamaz.<br/> - Eski şifre ile yeni şifre için yazılan değerler aynı olamaz.<br/> - Yeni şifre için yazılan değerler birbirinden farklı olamaz.<br/> - Yeni şifre en az 4 karakter olmalıdır.");
			}
		}]
	});
	f.doLayout();
	w.show();
}

function objProp(o){
    var t="";
    for(var q in o)t+=o[q] instanceof Function ? q + " = function{}\n" : q + " = " + o[q] + "\n";
    return t;
}


function ajaxAuthenticateUser(){
	Ext.getCmp('loginForm').getForm().submit({
		url: 'ajaxAuthenticateUser',
		method:'POST', 
		clientValidation: true,
		waitMsg:'Kontrol Ediliyor...',
		success: function(o,resp){
			if(resp.result.success){
				if(resp.result.smsFlag){
				} else if(resp.result.expireFlag){
					changePassword(resp.result.roleCount, resp.result.defaultUserCustomizationId);
				} else if(resp.result.roleCount){
					selectUserRole(resp.result.roleCount, resp.result.defaultUserCustomizationId);
				} else {
					Ext.Msg.wait('Sisteme giriş yapılıyor','Lütfen bekleyiniz...');
					document.location='main.htm?.r='+new Date().getTime();
				}
			}else {
				Ext.MessageBox.alert("HATA",resp.errorMsg||'Yanlış Kullanıcı Adı/Şifre');
			}
		},
		failure: function(o,resp){
			var resp=eval('('+resp.response.responseText+')');
			if(resp.errorMsg){
				Ext.MessageBox.alert("HATA",'Yanlış Kullanıcı Adı/Şifre');
			}else{
				Ext.MessageBox.alert("HATA",resp.error||"Verileri kontrol edin.");
			}
		}
	});
	return false;    
}

function promisLoadException(a,b,c){
	if(c && c.responseText){
		var myJson = Ext.decode(c.responseText)
		if(myJson.error=='no_session'){
		} else if(myJson.validationError){
		    alert(objProp(myJson.validationError))
		} else {
		    alert(myJson.msg || ('Bilinmeyen Hata:' + c.responseText))
		}
	}else
		alert('Sunucuyla bağlantı yok')
}

function gridQwRenderer(field){return function(a,b,c){return c.data[field+'_qw_'];}}

Ext.onReady(function(){
	Ext.BLANK_IMAGE_URL="../images/ext3.3/default/s.gif";
	Ext.QuickTips.init();
	Ext.form.Field.prototype.msgTarget = 'side';

	var ged=pfrm_login.getExtDef();
	var gedItems=ged.items[0].items;
ged.items[0].bodyStyle=null;
	for(var qi=0;qi<gedItems.length;qi++)gedItems[qi].setWidth(200);
	gedItems.push({xtype:'checkbox',boxLabel:'Beni Hatırla'});
	var fs = new Ext.form.FormPanel({
		id: 'loginForm',
		name: 'loginForm',
		labelWidth: 75, // label settings here cascade unless overridden
		labelAlign: 'right',
		frame:true,border:false,
//		title: 'Multi Column, Nested Layouts and Anchoring',
		bodyStyle:'padding:1px',
		width: 320,
		buttonAlign:'center',
		buttons: [
		{text: 'Giriş', iconCls: 'button-enter', handler: ajaxAuthenticateUser}
		//,{text: 'Yardım', iconCls: 'button-help', handler: function(){Ext.MessageBox.alert("YARDIM","Yardım gelecek");}}
		],
//		defaults:{align:left},
		items :ged.items
	});
	fs.render('innerLogin');

	var nav = new Ext.KeyNav(Ext.getCmp('loginForm').getForm().getEl(),{'enter':ajaxAuthenticateUser,'scope': Ext.getCmp('loginForm')});
})
</script>
</head>
<body style="background: url(../images/custom/background/waterfall_desktop_background.jpg) repeat scroll right bottom #3566A8;">

<div id="outerLogin"><div align="center" id="innerLogin"></div></div>
<div id="footer"><div id="mem_ft">
    <p>Copyright © 2010 ProMIS BMP Ltd. All rights reserved.</p>    <p>
        <a href='info/copyright.html' target='_blank'>Copyright Policy</a> | 
        <a href='info/terms.html' target='_blank'>Terms of Service</a> | 
        <a href="http://security.yahoo.com" target="_blank">Guide to Online Security</a> |
        <a href="http://privacy.yahoo.com" target="_blank">Privacy Policy</a>    </p>
</div></div>
</body></html>