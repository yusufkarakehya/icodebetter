var callAttributes={};

var _request={"_iwb_extra":"1"
,"_renderer":"react16"
,"_gid1":"41"
,"_ServerURL_":"localhost"
,".w":"wpi_1521494360504"
,"_dont_throw":"1"
,"_tid":"1109"}
var _page_tab_id='tpi_1521555896617';
var grd_w5_ws1 = {gridId:691, select:'row',keyField:'ws_id',
 extraOutMap:{"tplObjId":3502
,"tplId":1101},
 liveSync:true,
 defaultWidth:400,
 defaultHeight:300,
 gridReport:true,
 saveUserInfo:true,
 loading:true,
 displayInfo:true,striped:true,hover:true,bordered:false, name:'WS Clients',
 id:'ng_1521666662989',
 listeners:{},
 _url:'../app/ajaxQueryData?_renderer=react16&.t='+_page_tab_id+'&.w='+_webPageId+'&_qid=104&_gid=691&firstLimit=10',remote:{sort: true},
 pageSize:10,
 searchForm:{
 formId: 248,
 a:2,
 name:'Search Criteria',id:'null',
 defaultWidth:400,
 defaultHeight:300,
 getExtDef:function(){
var mf={params:{"_renderer":"react16"
,"_ServerURL_":"localhost"
,"_dont_throw":"1"
,"_tid":"1101"},_url:'search_form'}
var _xdsc=_(Input,{type:'text',name:'xdsc',id:'xdsc', label:'Description',_children:[]});
var _xws_tip=_(XSelect,{options:[{id:'1',dsc:'SOAP'},{id:'2',dsc:'REST'}], clearable:true,name:'xws_tip',id:'xws_tip', label:'Type',_children:[]});
var _xactive_flag=_(XSelect,{options:[{id:'1',dsc:'Yes'},{id:'0',dsc:'No'}], clearable:true,name:'xactive_flag',id:'xactive_flag', label:'Active ?',defaultValue:'1',_children:[]});

var __anaBaslik__='search_criteria'
var __action__=2
mf=_(XForm, mf,_(Row, null, _(Col,{xs:'12',md:'7'}, _(FormGroup, {row:true}, _(Label, {md:'3',htmlFor:"xdsc"},_xdsc.props.label), _(Col,{xs:'12',md:'9'},_xdsc))), _(Col,{xs:'12',md:'5'}, _(FormGroup, {row:true}, _(Label, {md:'3',htmlFor:"xws_tip"},_xws_tip.props.label), _(Col,{xs:'12',md:'9'},_xws_tip)), _(FormGroup, {row:true}, _(Label, {md:'3',htmlFor:"xactive_flag"},_xactive_flag.props.label), _(Col,{xs:'12',md:'9'},_xactive_flag)))));

return mf}},
 crudFormId:249,
 crudTableId:1375,
 crudFlags:{insert:true,edit:true,remove:true},
 bulkUpdateFlag:true,
 extraButtons:[{view:'button', width:35, type:'icon', icon:'cube', tooltip:'Define WS', click:function(a,b,c){

if(a && a._grid && a._grid.sm && a._grid.sm.getSelected()){
    promisRequest({requestWaitMsg:true, url:'ajaxDefineWs?_wsid='+ a._grid.sm.getSelected().id, successDs: a._grid.ds})
} else
alert('olmaz')

}}],
 menuButtons:[{text:'Business 4 Entity', ref:'../bus4entity',handler:function(a,b,c){
mainPanel.loadTab({attributes:{modalWindow:!0,href:'showPage?_tid=1214&xws_id='+a._grid.sm.getSelected().id}});
}}],
 saveUserInfo:true
}
grd_w5_ws1._active_flag=_(Input,{type:'checkbox', className:'switch-input',name:'active_flag',id:'active_flag', label:'Active ?',_children:[]})
grd_w5_ws1.editable=true
grd_w5_ws1.columns=[{text:'ID', dataField: 'ws_id', sort:true, align:'right', width: '93%'},
{text:'Type', dataField: 'ws_tip', sort:true, width: '66%', formatter:function(cell,row){return row.ws_tip_qw_;}},
{text:'Description', dataField: 'dsc', sort:true, width: '266%'},
{text:'Active ?', dataField: 'active_flag', sort:true, align:'center', width: '93%', formatter:disabledCheckBoxHtml},
{text:'Global?', dataField: 'global_flag', sort:true, align:'center', width: '93%', formatter:disabledCheckBoxHtml},
{text:'ws_url', dataField: 'ws_url', sort:true, width: '133%'},
{text:'Security?', dataField: 'wss_tip', sort:true, align:'center', width: '80%', formatter:function(cell,row){return row.wss_tip_qw_;}},
{text:'timeout', dataField: 'timeout', sort:true, width: '133%'},
{text:'Last Edited By', dataField: 'version_user_id', sort:true, width: '133%', formatter:gridUserRenderer('version_user_id')},
{text:'Version Date/Time', dataField: 'version_dttm', sort:true, width: '133%'},
{text:'', dataField: 'pkpkpk_vcsf', align:'center', width: '53%', formatter:vcsHtml}]
grd_w5_ws1.tabOrder=1

grd_w5_ws1.pk={tws_id:'ws_id'}
var _grid1=grd_w5_ws1

return _(XPage,{grid:_grid1})