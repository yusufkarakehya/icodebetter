var _f1 = _(Input, {
    type: 'textarea',required:true,className:'xrequired',
    id: 'name', name:'my-text-area',
    placeholder: 'Enter your name'
}); 

var options = [
               { id: '1', dsc: 'One' },
               { id: '2', dsc: 'Two' },{ id: '3', dsc: 'Three' },{ id: '4', dsc: 'Four' }
           ];
var _f2 = _(XSelect, {
    name: 'ccnumber',clearable:false,required:true,className:'xrequired',
    options:options,_children:[],
    id: 'ccnumber', value:2,
    placeholder: 'Combo (required)'
}); 
var _f2b = _(XSelect, {//remote icin options null olmali
    name: 'ccnumberr', _children:[],_parents:[{c:_f2,onSelect:function(a1,b1,c1, self){return self.reload('ajaxQueryData',function(ax,bx,cx){return {_qid:123,xmodule_id:ax}}(a1,b1,c1),self);}}],
//    options:options,
    id: 'ccnumberr', value:338,
    placeholder: 'Combo Remote'
}); 

/*
var _f2b = _(XSelectRemote, {
    name: 'ccnumberr',// ref:'alibaba',
    id: 'ccnumberr', xreload:function(){alert('veli')},
    placeholder: 'Remote Combo'
}); 
*/

var _f3 = _(XAdvancedSelect, {
    name: 'firma', value:'', _parents:[{c:_f2,onSelect:function(a1,b1,c1, self){console.log('hayde bre:'+a1)}}],
    id: 'firma', url:'ajaxQueryData?_qid=123',searchPromptText:'Birşeyler Yazın',
    placeholder: 'Advanced Select Firma Seç...'
}); 

var _f4 = _(XAdvancedSelect, {
    name: 'contact', value:'', 
    id: 'contact', url:'ajaxQueryData?_qid=123',
    placeholder: 'Advanced Select Contact Seç...'
}); 

var mf=_(Card, null, _(CardBlock, {
    className: 'card-body'
}, _(Form,{onSubmit:function(e){
	e.preventDefault();
	var p = iwb.getFormValues(e.target);
	console.log("submitFormValues",p);
	callAttributes.closeTab();
	return false;
}},_(Row, null, _(Col, {
    xs: '12', md:'7'
}, _(FormGroup, {}, _(Label, {
    htmlFor: 'name'
}, 'Name'), _f1), _(FormGroup, {}, _(Label, {
    htmlFor: 'ccnumber'
}, 'Credit Card Number'), _f2), _(FormGroup, {}, _(Label, {
    htmlFor: 'ccnumberr'
}, 'Credit Card Number Remote'), _f2b)), _(Col, {
    xs: '12', md:'5'
}, _(FormGroup, {},_(Label, {
    htmlFor: 'firma'
}, 'Firma'), _f3), _(FormGroup, {disabled:true},_(Label, {
    htmlFor: 'contact'
}, 'Contact'), _f4)))
, _('hr'),_('div',{className:'form-actions'},_(Button,{type:'submit',color:'primary'},'Save Changes'),' ',_(Button,{color:'secondary',onClick:function(){callAttributes.closeTab();}},'Cancel'))
)));

return mf;
/*
return _(Form,{},
<div className="form-actions">
<Button type="submit" color="primary">Save changes</Button>
<Button color="secondary">Cancel</Button>
</div>
*/