var callAttributes={};

var _page_tab_id='fi_1521512261429';
var getForm={
 formId: 24,
 a:2,
 name:'Form',id:'fi_1521512261429',
 defaultWidth:990,
 defaultHeight:500,
 renderTip:2,
 crudTableId:40,
 contFlag:false,
 tmpId:-261425,
 smsMailTemplateCrudFlag:true,
 fileAttachFlag:true, fileAttachCount:0,
conversionCnt:1,
conversionForms:[{xid:62,text:"form2conversion (<i>preview</i>)",checked:false}],
manualConversionForms:[{xid:62,_fid:1169,preview:true,text:"form2conversion"}],
 getExtDef:function(){
var mf={_formId:24,id:'fi_1521512261429',baseParams:{"a":"2"
,"_tmpId":"-261425"
,"_iwb_extra":"1"
,"_renderer":"react16"
,"tform_id":"24"
,".t":"fi_1521512261429"
,"_ServerURL_":"localhost"
,".w":"wpi_1521494360504"
,"_fid":"24"},_url:'ajaxPostForm'}
var _object_tip=_(XSelect,{options:[{id:'1',dsc:'Grid (Search Form)'},{id:'2',dsc:'Table (CRUD Form)'},{id:'3',dsc:'Report (Param Form)'},{id:'4',dsc:'Stored Proc (Param Form)'},{id:'5',dsc:'Query (Form by Query)'},{id:'6',dsc:'Conversion'},{id:'7',dsc:'List (Search Form)'},{id:'8',dsc:'DataView (searchForm)'},{id:'9',dsc:'gantt'},{id:'10',dsc:'Mobile List (Search Form)'},{id:'11',dsc:'WS Method (Param Form)'},{id:'12',dsc:'Graph Dashboard'},{id:'99',dsc:'Free'}], clearable:false,name:'object_tip',id:'object_tip',required:true,className:'xrequired', label:'Object Type',_children:[]});
var _xobject_id=_(XSelect,{clearable:false,_parents:[{c:_object_tip,onSelect:function(a1,b1,c1,self){
var p=function(ax,bx,cx){var p={}
switch(1*ax){
case 1:p._qid=18;  break;
case 2:p._qid=50;break;
case 5:p._qid=23;p.xquery_tip=11;break;
case 6:p._qid=1222;break;
case 8:p._qid=1722;break;
case 10:p._qid=1633;break;
case 11:p._qid=2810;p.xpublish_flag=1;break;//return false;//todo
case 99:return false;
default:p._qid=69;break;
}
return p;}(a1,b1,c1);if(p)self.reload('../app/ajaxQueryData',p,self);}}],name:'xobject_id',id:'xobject_id',required:true,className:'xrequired', label:'Table',_children:[]});
var _dsc=_(Input,{type:'text',name:'dsc',id:'dsc',required:true,className:'xrequired', label:'Form Name',_children:[]});
var _locale_msg_key=_(Input,{type:'text',name:'locale_msg_key',id:'locale_msg_key',required:true,className:'xrequired', label:'Locale Message Key',_children:[]});
var _code=_(Input,{type:'textarea',name:'code',id:'code', label:'JS Constructor',_children:[]});
var _show_extended_fields_flag=_(Input,{type:'checkbox', className:'switch-input',name:'show_extended_fields_flag',id:'show_extended_fields_flag', label:'ShowExtFields?',_children:[]});
var _render_tip=_(XSelect,{options:[{id:'0',dsc:'Straight'},{id:'1',dsc:'Fieldset'},{id:'2',dsc:'TabPanel'},{id:'3',dsc:'TabPanel - BorderLayout'},{id:'5',dsc:'Mobile'}], clearable:false,name:'render_tip',id:'render_tip',required:true,className:'xrequired', label:'Render Tip',defaultValue:'1',_children:[]});
var _render_template_id=_(XSelect,{options:[{dsc:'basic Edit Form',id:'1089'},{dsc:'CSV Report Form',id:'686'},{dsc:'edit Form',id:'26'},{dsc:'edit Form 4 Data Import',id:'589'},{dsc:'edit Form By Query',id:'554'},{dsc:'edit form deneme',id:'588'},{dsc:'edit Form Multi',id:'198'},{dsc:'edit Form w/ detailEditibleGrids',id:'546'},{dsc:'edit+ jasper Form',id:'579'},{dsc:'editForm 4 CodeList',id:'557'},{dsc:'empty Form',id:'777'},{dsc:'File Import 2',id:'979'},{dsc:'mail Form',id:'401'},{dsc:'proc + jasper Form',id:'258'},{dsc:'proc Form',id:'101'},{dsc:'report Form',id:'115'},{dsc:'report Form (Switch Jasper - Normal)',id:'561'},{dsc:'showLogEditForm',id:'668'},{dsc:'Single Upload',id:'673'},{dsc:'Text Report Form',id:'687'},{dsc:'view Mail Form',id:'573'},{dsc:'wizard edit Form',id:'947'},{dsc:'ws Preview Form',id:'459'}], clearable:true,name:'render_template_id',id:'render_template_id', label:'Render Tmp',defaultValue:'26',_children:[]});
var _customizable_flag=_(Input,{type:'checkbox', className:'switch-input',name:'customizable_flag',id:'customizable_flag', label:'Customizable?',_children:[]});
var _cont_entry_flag=_(Input,{type:'checkbox', className:'switch-input',name:'cont_entry_flag',id:'cont_entry_flag', label:'Permanent Record ?',_children:[]});
var _default_width=_(NumberFormat,{style:{textAlign:'right'},thousandSeparator:',',decimalSeparator:'.',className:'form-control xrequired',required:true,decimalScale:0,name:'default_width',id:'default_width', label:'Default Width',_children:[]});
var _default_height=_(NumberFormat,{style:{textAlign:'right'},thousandSeparator:',',decimalSeparator:'.',className:'form-control xrequired',required:true,decimalScale:0,name:'default_height',id:'default_height', label:'Default Height',defaultValue:'0',_children:[]});
var _label_width=_(NumberFormat,{style:{textAlign:'right'},thousandSeparator:',',decimalSeparator:'.',className:'form-control xrequired',required:true,decimalScale:0,name:'label_width',id:'label_width', label:'Label Width',defaultValue:'100',_children:[]});
var _label_align_tip=_(XSelect,{options:[{id:'0',dsc:'Center'},{id:'1',dsc:'Left'},{id:'2',dsc:'Right'}], clearable:false,name:'label_align_tip',id:'label_align_tip',required:true,className:'xrequired', label:'Label Alignment',defaultValue:'2',_children:[]});
var _tab_order=_(NumberFormat,{style:{textAlign:'right'},thousandSeparator:',',decimalSeparator:'.',className:'form-control xrequired',required:true,decimalScale:0,name:'tab_order',id:'tab_order', label:'Tab Order',defaultValue:'1',_children:[]});

var __anaBaslik__='Form'
var __action__=2
try{

}catch(e){if(confirm('ERROR form.JS!!! Throw?'))throw e;}
mf=_(XForm, mf,_(Row, null, _(Col,{xs:'12',md:'7'}, _(FormGroup, {row:true}, _(Label, {md:'3',htmlFor:"object_tip"},_object_tip.props.label), _(Col,{xs:'12',md:'9'},_object_tip)), _(FormGroup, {row:true}, _(Label, {md:'3',htmlFor:"xobject_id"},_xobject_id.props.label), _(Col,{xs:'12',md:'9'},_xobject_id)), _(FormGroup, {row:true}, _(Label, {md:'3',htmlFor:"dsc"},_dsc.props.label), _(Col,{xs:'12',md:'9'},_dsc)), _(FormGroup, {row:true}, _(Label, {md:'3',htmlFor:"locale_msg_key"},_locale_msg_key.props.label), _(Col,{xs:'12',md:'9'},_locale_msg_key)), _(FormGroup, {row:true}, _(Label, {md:'3',htmlFor:"code"},_code.props.label), _(Col,{xs:'12',md:'9'},_code)), _(FormGroup, {row:true}, _(Label, {md:'3',htmlFor:"show_extended_fields_flag"},_show_extended_fields_flag.props.label), _(Label,{ className: 'switch switch-3d switch-primary' }, _show_extended_fields_flag,_('span', { className: 'switch-label' }),_('span', { className: 'switch-handle' }))), _(FormGroup, {row:true}, _(Label, {md:'3',htmlFor:"render_tip"},_render_tip.props.label), _(Col,{xs:'12',md:'9'},_render_tip)), _(FormGroup, {row:true}, _(Label, {md:'3',htmlFor:"render_template_id"},_render_template_id.props.label), _(Col,{xs:'12',md:'9'},_render_template_id)), _(FormGroup, {row:true}, _(Label, {md:'3',htmlFor:"customizable_flag"},_customizable_flag.props.label), _(Label,{ className: 'switch switch-3d switch-primary' }, _customizable_flag,_('span', { className: 'switch-label' }),_('span', { className: 'switch-handle' })))), _(Col,{xs:'12',md:'5'}, _(FormGroup, {row:true}, _(Label, {md:'3',htmlFor:"cont_entry_flag"},_cont_entry_flag.props.label), _(Label,{ className: 'switch switch-3d switch-primary' }, _cont_entry_flag,_('span', { className: 'switch-label' }),_('span', { className: 'switch-handle' }))), _(FormGroup, {row:true}, _(Label, {md:'3',htmlFor:"default_width"},_default_width.props.label), _(Col,{xs:'12',md:'9'},_default_width)), _(FormGroup, {row:true}, _(Label, {md:'3',htmlFor:"default_height"},_default_height.props.label), _(Col,{xs:'12',md:'9'},_default_height)), _(FormGroup, {row:true}, _(Label, {md:'3',htmlFor:"label_width"},_label_width.props.label), _(Col,{xs:'12',md:'9'},_label_width)), _(FormGroup, {row:true}, _(Label, {md:'3',htmlFor:"label_align_tip"},_label_align_tip.props.label), _(Col,{xs:'12',md:'9'},_label_align_tip)), _(FormGroup, {row:true}, _(Label, {md:'3',htmlFor:"tab_order"},_tab_order.props.label), _(Col,{xs:'12',md:'9'},_tab_order)))));

return mf}}






var mf = getForm.getExtDef();


return _('div', {
    className: 'animated fadeIn'
}, _(Row, null, _(Col, {
    xs: '12'
}, _(Card, null, _(
		  Card,
		  { className: "text-white bg-primary" },
		  _(
		    CardBlock,
		    { className: "card-body" },
		    _("div", { className: "h4 m-0" }, getForm.name),
		    _("div", null, "mainText")
		  )
		),  _(CardBlock, {
    className: 'card-body'
}
,mf)))));