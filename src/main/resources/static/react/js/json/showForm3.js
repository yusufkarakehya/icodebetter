var callAttributes={};

var _page_tab_id='fi_1521498087097';
var getForm={
 formId: 3770,
 a:2,
 name:'iWB TS Portlet Object',id:'fi_1521498087097',
 defaultWidth:600,
 defaultHeight:400,
 renderTip:1,
 crudTableId:2723,
 contFlag:false,
 tmpId:-87097,
 smsMailTemplateCrudFlag:true,
 getExtDef:function(){
var mf={_formId:3770,id:'fi_1521498087097',baseParams:{"a":"2"
,"_tmpId":"-87097"
,"_iwb_extra":"1"
,"_renderer":"react16"
,".t":"fi_1521498087097"
,"_ServerURL_":"localhost"
,".w":"wpi_1521494360504"
,"_fid":"3770"},_url:'ajaxPostForm'}
var _portlet_id='';
var _tab_order=_(Input,{type:'text',name:'tab_order',required:true,className:'xrequired', label:'Order No.',_children:[]});
var _object_tip=_(XSelect,{options:[{id:'2709',dsc:'TS Measurement'},{id:'8',dsc:'Query'}], clearable:false,name:'object_tip',required:true,className:'xrequired', label:'Type',_children:[]});
var _object_id=_(XSelect,{options:[],name:'object_id',required:true,className:'xrequired', label:'Project No.',_children:[]});
var _extra_code=_(Input,{type:'textarea',name:'extra_code', label:'Extra Code',_children:[]});

var __anaBaslik__='w5_ts_portlet_object'
var __action__=2
mf=_(XForm, mf,_(Row, null, _(Col,{xs:'12',md:'8'}, _(FormGroup, null, _(Label, {htmlFor:"tab_order"},_tab_order.props.label), _tab_order), _(FormGroup, null, _(Label, {htmlFor:"object_tip"},_object_tip.props.label), _object_tip), _(FormGroup, null, _(Label, {htmlFor:"object_id"},_object_id.props.label), _object_id), _(FormGroup, null, _(Label, {htmlFor:"extra_code"},_extra_code.props.label), _extra_code))));

return mf}}

var _f1 = _(Input, {
    type: 'text',
    id: 'name',
    placeholder: 'Enter your name'
}); 

var options = [
               { id: '1', dsc: 'One' },
               { id: '2', dsc: 'Two' },{ id: '3', dsc: 'Three' },{ id: '4', dsc: 'Four' }
           ];
var _f2 = _(XSelect, {
    name: 'ccnumber', value:'1', 
    options:options,
    id: 'ccnumber', multi:true,closeOnSelect:false,
    placeholder: 'Seviye Seç'
}); 

var _f3 = _(XAdvancedSelect, {
    name: 'firma', value:'',  multi:true,
    id: 'firma', url:'ajaxQueryData?_qid=123',
    placeholder: 'Firma Seç...'
}); 

return _('div', {
    className: 'animated fadeIn'
}, _(Row, null, _(Col, {
    xs: '12'
}, _(Card, null, _(CardHeader, null, _('strong', null, 'Credit Card'), _('small', null, ' Form')), _(CardBlock, {
    className: 'card-body'
}, _(Row, null, _(Col, {
    xs: '12'
}, _(FormGroup, null, _(Label, {
    htmlFor: 'name'
}, 'Name'), _f1))), _(Row, null, _(Col, {
    xs: '12'
}, _(FormGroup, null, _(Label, {
    htmlFor: 'ccnumber'
}, 'Credit Card Number'), _f2, _(Label, {
    htmlFor: 'firma'
}, 'Firma'), _f3))))))));