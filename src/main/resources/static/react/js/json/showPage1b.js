
const products = [];

function addProducts(quantity) {
  const startId = products.length;
  for (var i = 0; i < quantity; i++) {
    const id = startId + i;
    products.push({
      id: id,
      name: 'Item name ' + id,
      price: 2100 + i
    });
  }
}

addProducts(70);

const columns = [{dataField: 'id',text: 'Product ID'}, 
                 {dataField: 'name',text: 'Product Name'},
                 {dataField: 'price',text: 'Product Price'}
                 ];


var t=_(BootstrapTable,{keyField:'id',columns:columns,data:products});
return _('div',{className:'animated fadeIn'},t);