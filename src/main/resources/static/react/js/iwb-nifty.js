//array color name
const dBGColors2 = [, , "#de9338", "#222", , , , ,];
const dgColors = [
  "warning",
  "secondary",
  "danger",
  "primary",
  "success",
  "info"
];
// const dgColors2 =
// ["primary","info","secondary","gray-700","gray-500","gray-400","gray-700"];
var dgColors2 = [
  "orange",
  "primary",
  "teal",
  "pink",
  "gray-500",
  "gray-400",
  "gray-700"
];
const detailSpinnerColors2 = [
  "#187da0",
  "#2eadd3",
  "darkorange",
  "#187da0",
  "#4d6672",
  "#626a70",
  "#66767d"
];
const dgColors3 = [
  "gray-700",
  "danger",
  "gray-500",
  "gray-400",
  "gray-700",
  "info",
  "secondary",
  "secondary",
  "secondary",
  "warning"
];
// ReactRouterDOM for routing
const Link = ReactRouterDOM.Link;
const Route = ReactRouterDOM.Route;
const Switch = ReactRouterDOM.Switch;
const NavLink = ReactRouterDOM.NavLink;
const Redirect = ReactRouterDOM.Redirect;
const HashRouter = ReactRouterDOM.HashRouter;
// Reactstrap components
const Row = Reactstrap.Row;
const Col = Reactstrap.Col;
const Nav = Reactstrap.Nav;
const Card = Reactstrap.Card;
const Form = Reactstrap.Form;
const Alert = Reactstrap.Alert;
const Media = Reactstrap.Media;
const Input = Reactstrap.Input;
const Label = Reactstrap.Label;
const Table = Reactstrap.Table;
const Badge = Reactstrap.Badge;
const Modal = Reactstrap.Modal;
const Button = Reactstrap.Button;
const NavItem = Reactstrap.NavItem;
 
const Popover = Reactstrap.Popover;
const TabPane = Reactstrap.TabPane;
const Tooltip = Reactstrap.Tooltip;
const NavLinkS = Reactstrap.NavLink;
const FormText = Reactstrap.FormText;
const Dropdown = Reactstrap.Dropdown;
const CardBody = Reactstrap.CardBody;
const CardBlock = Reactstrap.CardBody;
const CardTitle = Reactstrap.CardTitle;
const ListGroup = Reactstrap.ListGroup;
const Container = Reactstrap.Container;
const ModalBody = Reactstrap.ModalBody;
const FormGroup = Reactstrap.FormGroup;
const CardHeader = Reactstrap.CardHeader;
const CardFooter = Reactstrap.CardFooter;
const Breadcrumb = Reactstrap.Breadcrumb;
const InputGroup = Reactstrap.InputGroup;
const Pagination = Reactstrap.Pagination;
const TabContent = Reactstrap.TabContent;
const PopoverBody = Reactstrap.PopoverBody;
const ModalHeader = Reactstrap.ModalHeader;
const ModalFooter = Reactstrap.ModalFooter;
const ButtonGroup = Reactstrap.ButtonGroup;
const NavbarBrand = Reactstrap.NavbarBrand;
const DropdownMenu = Reactstrap.DropdownMenu;
const DropdownItem = Reactstrap.DropdownItem;
const ListGroupItem = Reactstrap.ListGroupItem;
const NavbarToggler = Reactstrap.NavbarToggler;
const PopoverHeader = Reactstrap.PopoverHeader;
const DropdownToggle = Reactstrap.DropdownToggle;
const PaginationLink = Reactstrap.PaginationLink;
const PaginationItem = Reactstrap.PaginationItem;
const ButtonDropdown = Reactstrap.ButtonDropdown;
const BreadcrumbItem = Reactstrap.BreadcrumbItem;
const InputGroupAddon = Reactstrap.InputGroupAddon;
const InputGroupButton = Reactstrap.InputGroupButton;
const ListGroupItemText = Reactstrap.ListGroupItemText;
const UncontrolledTooltip = Reactstrap.UncontrolledTooltip;
const ListGroupItemHeading = Reactstrap.ListGroupItemHeading;
// FW Community Components
const Select = window.Select;
const Popper = window.Popper;
const findDOMNode = ReactDOM.findDOMNode;
// React
var _ = React.createElement;
// DXReactCore imports
const  { DXReactCore, DXReactGrid, DXReactGridBootstrap4 } = DevExpress;
const Getter = DXReactCore.Getter;
const Plugin = DXReactCore.Plugin;
const Template = DXReactCore.Template;
const TemplateConnector = DXReactCore.TemplateConnector;
const TemplatePlaceholder = DXReactCore.TemplatePlaceholder;

var _dxrg = DXReactGrid;
var _dxgrb = DXReactGridBootstrap4;
/**
 * @description iwb object is MIXIN like object most of the configuration is
 *              here and most used functions
 */
var iwb = {
  toastr: toastr,
  components :{},
  grids: {},
  forms: {},
  tabs:{},
  closeTab:null,
  debug: false,
  debugRender: false,
  debugConstructor: false,
  detailPageSize: 10,
  log: console.log.bind(window.console),
  mem:(( isArrayEqual = (array1, array2) => array1.length === array2.length &&
  array1.every((value, index) => value === array2[index]) &&
  JSON.stringify(array1) === JSON.stringify(array2)
  ) => {
    let fnList = {}, resultList = {}, argList = {};
    return (resultFn, ...newArgs) => {
      let key = resultFn.toString().replace(/(\r\n\t|\n|\r\t|\s)/gm, "")+newArgs.toString().replace(/(,|\s)/gm, '');
      if ( key && fnList[key] && resultList[key] && isArrayEqual(argList[key], newArgs) ) { return resultList[key]; }
      argList[key] = newArgs;
      resultList[key] = resultFn.apply(this, newArgs);
      fnList[key] = resultFn;
      return resultList[key];
    };
  })(),
  /**
	 * A function to insert css classes into Dom
	 * 
	 * @param {string}
	 *            css - example '.aclass{display:none}'
	 * @param {string}
	 *            id - template id of the page not mandatory
	 */
  addCssString : (css = '', id = Math.random()) => {
    let style = document.createElement('style');
    style.type = 'text/css';
    style.id = "iwb-tpl-" + id;
    (style.styleSheet) ? style.styleSheet.cssText = css: style.appendChild(document.createTextNode(css))
    document['head'].appendChild(style);
  },
  /**
	 * a function used for react.lazy
	 * 
	 * @param {string}
	 *            url - example '/comp/2/js'
	 */
  import: async (url) => {
    var loc = document.location.href;
    var xloc = loc.split('main.htm');
    xloc[xloc.length-1]=url;
    loc = xloc.join('');
    if (Object.keys(iwb.components).indexOf(url) > 0) {
      return iwb.components[url];
    }
    var imported = await import(loc);
    iwb.components = { ...iwb.components,
      [url]: imported
    };
    return imported;
  },
  /**
	 * @param {string}
	 *            url - example '/comp/2/js'
	 * @param {string}
	 *            id - example '2' -id of the component
	 */
  addCss: async (url, id = Math.floor(Math.random() * 1000 + 1)) => {
    let response = await fetch(url);
    let cssText = await response.text();
    if(document.getElementById(id)===null){
      let element = document.createElement('style');
      element.innerHTML = cssText;
      element.id='style'+id
      window.document.head.appendChild(element);
    }
    return cssText;
  },
  /**
	 * @param {string}
	 *            id - example '2' -id of the component
	 */
  removePageCss: (id)=>{
    let elem = document.getElementById('style'+id);
    if(elem !== null){
      elem.parentNode.removeChild(elem);
    }
    return true
  },
  loadable : (loaderFunction) => 
    class AsyncComponent extends React.Component {
    	constructor(props){
            super(props);
            this.state = { ResultComponent: null, error: false, errorText:''};
          }
        componentWillMount() {
          loaderFunction
            .then(result => this.setState({ ResultComponent: result.default || result})) // "es6"
																							// default
																							// export
            .catch((errorText) => this.setState({ error: true, errorText}))
        }
        render() {
          const { error, ResultComponent } = this.state;
          return ResultComponent ? _(ResultComponent,{ ...this.props }) : (error ? _('span',{className:'alert alert-danger'}) : _(XLoading,null) )
        }
    },
  /**
	 * @description used for giving data for grid button
	 */
  commandComponentProps: {
    add: { icon: "plus", hint: "Create new row" },
    edit: { icon: "pencil", hint: "Edit row", color: "text-warning" },
    delete: { icon: "trash", hint: "Delete row", color: "text-danger" },
    cancel: { icon: "x", hint: "Cancel changes", color: "text-danger" },
    import: { icon: "target", hint: "Import" }
  },
  copyToClipboard:(text)=>{
    const el = document.createElement('textarea');
    el.value = (typeof text === 'object')?window.JSON.stringify(text):text;
    el.style.position = 'absolute'; 
    el.style.left = '-9999px'; 
    el.setAttribute('readonly', '');
    document.body.appendChild(el);
    el.select();
    document.execCommand('copy');
    document.body.removeChild(el);
    toastr.success( "Use CTR + V to paste the content!", "Copied Successfully", { timeOut: 3000 } );
  },
  // logo:'<svg width="32" height="22" xmlns="http://www.w3.org/2000/svg"
	// x="0px" y="0px" viewBox="0 0 300 202.576" enable-background="new 0 0 300
	// 202.576" class="white-logo standard-logo middle-content"><g
	// id="svg_14"><path id="svg_15" d="m46.536,31.08c0,10.178 -8.251,18.429
	// -18.429,18.429c-10.179,0 -18.429,-8.251 -18.429,-18.429c0,-10.179
	// 8.25,-18.43 18.429,-18.43c10.177,0 18.429,8.251 18.429,18.43"
	// fill="darkorange"></path><path id="svg_16" d="m220.043,62.603c-0.859,0
	// -1.696,0.082 -2.542,0.128c-0.222,-0.007 -0.429,-0.065
	// -0.654,-0.065c-0.674,0 -1.314,0.128 -1.969,0.198c-0.032,0.003
	// -0.064,0.003 -0.096,0.005l0,0.005c-9.241,1.04 -16.451,8.79
	// -16.451,18.309c0,9.555 7.263,17.326 16.554,18.319c0,0.03 0,0.063
	// 0,0.094c0.482,0.027 0.953,0.035 1.428,0.05c0.182,0.006 0.351,0.055
	// 0.534,0.055c0.088,0 0.17,-0.025 0.258,-0.026c0.96,0.02 1.927,0.026
	// 2.938,0.026c16.543,0 29.956,13.021 29.956,29.564c0,16.545 -13.412,29.956
	// -29.956,29.956c-15.521,0 -28.283,-11.804
	// -29.803,-26.924l0,-107.75l-0.054,0c-0.289,-9.926 -8.379,-17.896
	// -18.375,-17.896c-9.995,0 -18.086,7.971
	// -18.375,17.896l-0.053,0l0,118.529c0,10.175 11.796,52.85
	// 66.661,52.85c36.815,0 66.661,-29.846 66.661,-66.662c-0.001,-36.816
	// -29.847,-66.661 -66.662,-66.661" fill="#20a8d8"></path><path id="svg_17"
	// d="m153.381,143.076l-0.049,0c-0.805,8.967 -8.252,16.021
	// -17.428,16.021s-16.624,-7.054
	// -17.428,-16.021l-0.048,0l0,-66.298l-0.045,0c-0.245,-9.965 -8.36,-17.979
	// -18.384,-17.979s-18.139,8.014
	// -18.384,17.979l-0.045,0l0,66.298l-0.05,0c-0.805,8.967 -8.252,16.021
	// -17.428,16.021c-9.176,0 -16.624,-7.054
	// -17.429,-16.021l-0.048,0l0,-66.298l-0.045,0c-0.246,-9.965 -8.361,-17.978
	// -18.384,-17.978c-10.024,0 -18.139,8.014
	// -18.384,17.979l-0.046,0l0,66.298c0.836,29.321 24.811,52.849
	// 54.335,52.849c13.79,0 26.33,-5.178 35.906,-13.636c9.577,8.458
	// 22.116,13.636 35.906,13.636c14.604,0 27.85,-5.759
	// 37.61,-15.128c-15.765,-13.32 -20.132,-31.532 -20.132,-37.722"
	// fill="#bbb"></path></g></svg>',
  logo:
    '<img src="/images/rabbit-head.png" border=0 style="vertical-align: top;width: 28px;margin-top: -2px;">',

  detailSearch: () => false,
  fmtShortDate: x => {
    x ? moment(x).format("DD/MM/YYYY") : "";
  },
  fmtDateTime: x => {
    x ? moment(x).format("DD/MM/YYYY HH:mm") : "";
  },
  openForm: url => {
    if (url) iwb.openTab("1-" + Math.random(), url);
    return false;
  },
  sidebarToggle: e => {
    e.preventDefault();
    document.body.classList.toggle("sidebar-hidden");
  },
  sidebarMinimize: e => {
    e.preventDefault();
    document.body.classList.toggle("sidebar-minimized");
    document.body.classList.toggle("brand-minimized");
  },
  mobileSidebarToggle: e => {
    e.preventDefault();
    document.body.classList.toggle("sidebar-mobile-show");
  },
  asideToggle: e => {
    e.preventDefault();
    if (iwb.asideToggleX) iwb.asideToggleX(e);
    else document.body.classList.toggle("aside-menu-hidden");
  },
  /**
	 * used to remove Global search value
	 */
  killGlobalSearch: () => {
    iwb.onGlobalSearch2 = false;
    var component = document.getElementById("id-global-search");
    if (!component) return;
    component.value = "";
    component.classList.remove("global-search-active");
  },
  /**
	 * Converts JSON to URI
	 */
  JSON2URI: json => {
    if (!json) return "";
    var resultString = "";
    for (key in json)
      resultString +=
        encodeURIComponent(key) +
        "=" +
        (json[key] === null || json[key] === false
          ? ""
          : encodeURIComponent(json[key])) +
        "&";
    return resultString;
  },
  onGlobalSearch: v => {
    var component = document.getElementById("id-global-search");
    var cc = component.classList.contains("global-search-active");
    if ((c.value && !cc) || (!c.value && cc))
      c.classList.toggle("global-search-active");
    if (iwb.onGlobalSearch2) iwb.onGlobalSearch2(v);
  },
  getFieldRawValue: (field, extraOptions) => {
    if (!field || !field.value) return iwb.emptyField;
    if (field.$ === MapInput) return _(field.$,{value:field.value, disabled:true});
    var options = extraOptions || field.options;
    if (!options || !options.length) {
      var value = (field.decimalScale)?Number(field.value).toFixed(field.decimalScale):field.value;
      if (typeof value == "undefined" || value == "") return iwb.emptyField;
      return _("b", { className: "form-control" }, value);
    }
    var optionsMap = {};
    options.map(o => {
      optionsMap[o.id] = o.dsc;
    });
    if (field.multi) {
      var value = [],
        vs = field.value;
      if (!Array.isArray(vs)) vs = vs.split(",");
      vs.map(v => {
        value.push(optionsMap[v]);
      });
      if (!value.length) return iwb.emptyField;
      return _("b", { className: "form-control" }, value.join(", "));
    }
    var value = field.value;
    if (value.id) value = value.id;
    value = optionsMap[value];
    if (value == undefined || value == "") return iwb.emptyField;
    return _("b", { className: "form-control" }, value);
  },
  approvalColorMap:{1:'primary',2:'warning',3:'danger',5:'success',901:'secondary'},
  approvalLogs: arid =>{
	return (event) =>{
		event.preventDefault();
		iwb.ajax.query(1667, {xapproval_record_id:arid}, (j)=>{
		if(j.data && j.data.length)iwb.showModal({
	        title: "Approval Logs",
	        footer: false,
	        color: "primary",
	        size: "lg",
	        body: _(
	          ListGroup,
	          { style: { fontSize: "1.0rem" }, children:j.data.map( item =>
	          _(
	  	            ListGroupItem,
	  	            {
	  	            },
	  	            _("span", { className: "float-right badge badge-pill badge-"+iwb.approvalColorMap[item.approval_action_tip] }, item.approval_action_tip_qw_),
	  	            " ",
	  	            _("b",null,item.user_id_qw_)," ",
	  	            item.step_dsc, " - ", _("i",{style:{color:'#aaa'}},item.log_dttm)
	  	          )
	          )}
	        )
	      });
		else alert('no data');
	}
		);
	}  
  },
  request: cfg => {
    if (!window.fetch) {
      toastr.error("window.fetch not supported",'ERROR! ');
      return false;
    }
    if (!cfg || !cfg.url) {
      toastr.error("Config missing",'ERROR!');
      return false;
    }
    fetch(cfg.url, {
      body: JSON.stringify(cfg.params || {}), // must match 'Content-Type'
												// header
      cache: "no-cache", // *default, no-cache, reload, force-cache,
							// only-if-cached
      credentials: "same-origin", // include, same-origin, *omit
      headers: {
        "content-type": "application/json"
      },
      method: "POST", // *GET, POST, PUT, DELETE, etc.
      mode: "cors", // no-cors, cors, *same-origin
      redirect: "follow", // *manual, follow, error
      referrer: "no-referrer" // *client, no-referrer
    })
      .then(
        response =>
          response.status === 200 || response.status === 0
            ? response.json()
            : Promise.reject(new Error(response.text() || response.statusText))
      )
      .then(
        result => {
          if (cfg.callback && cfg.callback(result, cfg) === false) return;
          if (result.success) {
            if (cfg.successCallback) cfg.successCallback(result, cfg);
          } else {
            if (cfg.errorCallback && cfg.errorCallback(result, cfg) === false)
              return;
            iwb.requestErrorHandler(result);
          }
        },
        error => {
          if (
            cfg.errorCallback &&
            cfg.errorCallback({ error: error }, cfg) === false
          )
            return;
          toastr.error(error || "Unknown ERROR", "Request Error");
        }
      );
  },
  requestErrorHandler: obj => {
	  console.log("requestErrorHandler", obj)
    if (obj.errorType) {
      switch (obj.errorType) {
        case "session":
          return iwb.showLoginDialog();
        case "validation":
          toastr.error(obj.errors.join("<br/>"), "Validation Error");
          break;
        default:
          toastr.error(
        		  obj.error || obj.errorMsg || "Unknown ERROR",
            obj.errorType + " Error"
          );
      }
    } else {
      toastr.error(obj.errorMsg || "Unknown ERROR", "Request Error");
    }
  },
  getFormValues: formObj => {
    if (!formObj || !formObj.elements) return {};
    var elements = formObj.elements,
      values = {};
    for (var index = 0; index < elements.length; index++) {
      if (elements[index].name)
        switch (elements[index].type) {
          case "checkbox":
            values[elements[index].name] = elements[index].checked;
            break;
          case "hidden":
            values[elements[index].name] =
              values[elements[index].name] === undefined
                ? elements[index].value
                : values[elements[index].name] + "," + elements[index].value;
            break;
          default:
            values[elements[index].name] = elements[index].value;
        }
    }
    return values;
  },
  /**
	 * @description sadece master-insert durumunda cagir. farki _postMap ve hic
	 *              bir zaman _insertedItems,_deletedItems dikkate almamasi
	 * @param {*}
	 *            grid
	 * @param {*}
	 *            prefix
	 * @param {*}
	 *            values
	 */
  prepareParams4grid: (grid, prefix, values) => {
    var dirtyCount = 0;
    var params = {};
    var items = values.deleted;
    var pk = grid._pk || grid.pk;
    if (items)
      for (var bjk = 0; bjk < items.length; bjk++) {
        // deleted
        dirtyCount++;
        for (var key in pk) {
          var val = pk[key];
          if (typeof val == "function") {
            params[key + prefix + "." + dirtyCount] = val(items[bjk]);
          } else {
            params[key + prefix + "." + dirtyCount] =
              val.charAt(0) == "!" ? val.substring(1) : items[bjk][val];
          }
        }
        params["a" + prefix + "." + dirtyCount] = 3;
      }
    items = values.changed;
    if (items)
      for (var bjk = 0; bjk < items.length; bjk++) {
        // edited
        dirtyCount++;
        params["a" + prefix + "." + dirtyCount] = 1;
        var changes = items[bjk]._new;
        for (var key in changes)
          params[key + prefix + "." + dirtyCount] = changes[key];
        if (grid._postMap)
          for (var key in grid._postMap) {
            var val = grid._postMap[key];
            if (typeof val == "function") {
              params[key + prefix + "." + dirtyCount] = val(changes);
            } else {
              params[key + prefix + "." + dirtyCount] =
                val.charAt(0) == "!" ? val.substring(1) : changes[val];
            }
          }

        for (var key in pk) {
          var val = pk[key];
          if (typeof val == "function") {
            params[key + prefix + "." + dirtyCount] = val(items[bjk]);
          } else {
            params[key + prefix + "." + dirtyCount] =
              val.charAt(0) == "!" ? val.substring(1) : items[bjk][val];
          }
        }
      }
    items = values.inserted;
    if (items)
      for (var bjk = 0; bjk < items.length; bjk++) {
        dirtyCount++;
        params["a" + prefix + "." + dirtyCount] = 2;
        var changes = items[bjk]._new;
        for (var key in changes)
          params[key + prefix + "." + dirtyCount] = changes[key];

        if (grid._postMap)
          for (var key in grid._postMap) {
            var val = grid._postMap[key];
            if (typeof val == "function") {
              params[key + prefix + "." + dirtyCount] = val(changes);
            } else {
              params[key + prefix + "." + dirtyCount] =
                val.charAt(0) == "!" ? val.substring(1) : changes[val];
            }
          }
        if (grid._postInsertParams) {
          for (var key in grid._postInsertParams)
            params[key + prefix + "." + dirtyCount] =
              grid._postInsertParams[key];
        }
      }
    if (dirtyCount > 0) {
      params["_cnt" + prefix] = dirtyCount;
      params["_fid" + prefix] = grid.crudFormId;
      return params;
    } else return {};
  }
};
var ajaxErrorHandler = iwb.requestErrorHandler;
(iwb.emptyField = _(
  "i",
  { className: "raw-field-empty" },
  _("br"),
  " ",
  "(boş)"
)),
  (iwb.loadPage = function(cfg) {});
iwb.ui = {
  buildPanel: c => {
    if (!c.grid.pk) c.grid.pk = c.pk || c._pk;
    if (!c.grid.detailGrids) c.grid.detailGrids = c.detailGrids || false;
    return _(XPage, c);
  }
};
function disabledCheckBoxHtml(row, cell) {
  return row[cell] && 1 * row[cell]
    ? _("i", { className: "fa fa-check", style: { color: "green" } })
    : null; // _('i',{className:'fa fa-check', style:{color: 'white',background:
			// 'red', padding: 5, borderRadius: 25}});
}
function gridUserRenderer(row, cell) {
  // TODO
  return row[cell + "_qw_"];
}
function gridQwRendererWithLink(t) {
  // tableId
  return function(row, cell) {
    return row[cell + "_qw_"];
  };
}
function editGridComboRenderer(cell, combo) {
  if (!combo || !combo.options)
    return function(row) {
      return "?x?: " + row[cell];
    };
  var moptions = {};
  combo.options.map(function(o) {
    moptions[o.id] = o;
  });
  combo.moptions = moptions;
  return function(row) {
    var c = row[cell];
    if (!c) return "";
    var o = combo.moptions[c];
    return o ? o.dsc : "???: " + row[cell];
  };
}
function editGridLovComboRenderer(cell, combo) {
  if (!combo || !combo.options)
    return function(row) {
      return "?x?: " + row[cell];
    };
  var moptions = {};
  combo.options.map(option => {
    moptions[option.id] = option;
  });
  combo.moptions = moptions;
  return row => {
    var tempCell = row[cell];
    if (!tempCell) return "";
    tempCell = tempCell.split(",");
    return tempCell.map(opName => combo.moptions[opName]);
  };
}
function fileAttachmentHtml(row, cell) {
  // TODO
  return row[cell] && 1 * row[cell]
    ? _("i", { className: "icon-paper-clip" })
    : null;
}
function vcsHtml(row, cell) {
  return row[cell] && 1 * row[cell]
    ? _("i", { className: "icon-social-github" })
    : null;
}
function pictureHtml(row, cell) {
  return row[cell] && 1 * row[cell]
    ? _("i", { className: "icon-picture" })
    : null;
}
function mailBoxRenderer(row, cell) {
  return row[cell] && 1 * row[cell]
    ? _("i", { className: "icon-envelope" })
    : null;
}
function strShortDate(x) {
  return x ? x.substr(0, 10) : "";
}
function accessControlHtml() {
  return null;
}
function fmtDateTime(x) {
  return x ? moment(x).format("DD/MM/YYYY HH:mm") : "";
}
function fmtShortDate(x) {
  return x ? moment(x).format("DD/MM/YYYY") : "";
}
function strDateTime(x) {
  return x || "";
}
function strDateTimeAgo(x) {
  return x || "";
}
function getStrapSize(w) {
  if (w >= 700) return "lg";
  if (w >= 400) return "md";
  return "sm";
}
function getMasterGridSel(a, sel) {
  return sel;
}
function buildParams2(params, map) {
  var bp = "";
  for (var key in params) {
    var newKey = params[key];
    if (typeof newKey == "function") {
      bp += "&" + key + "=" + newKey(params);
    } else if (newKey.charAt(0) == "!")
      bp += "&" + key + "=" + newKey.substring(1);
    else bp += "&" + key + "=" + map[params[key]];
  }
  return bp;
}
/**
 * @description Grids common methods are located in this class
 */
class GridCommon extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {};
    this.lastQuery;
    /**
	 * @description Used to set State of Grid with pagination number
	 * @param {Number}
	 *            currentPage - current page number
	 */
    this.onCurrentPageChange = currentPage => this.setState({ currentPage });
    /**
	 * @description Used to Set State of grid with Column width
	 * @param {String}
	 *            columnWidths[].columnName - name of the column
	 * @param {Number}
	 *            columnWidths[].width - width of the column
	 */
    this.onColumnWidthsChange = columnWidths => this.setState({ columnWidths });
    /**
	 * @description Used to Set State of Grid with column order
	 * @param {Array}
	 *            order - ["ColName1","ColName2",...]
	 */
    this.onOrderChange = order => this.setState({ order });
    /**
	 * @description Used to set Pagination row Nummber
	 * @param {Number}
	 *            pageSize - sets size of the number for
	 */
    this.onPageSizeChange = pageSize => {
      var { currentPage, totalCount } = this.state;
      currentPage = Math.min(currentPage, Math.ceil(totalCount / pageSize) - 1);
      this.setState({ pageSize, currentPage });
    };
    /**
	 * @description get selected array from grid
	 */
    this.getSelected = () => this.state.rows.reduce((accumulator, row) => {
      this.state.selection.includes(row[props.keyField]) ? accumulator.push(row) : '';
      return accumulator;
    }, []);
    // //////////////////////////////////////////------2-----////////////////////////////////////////
    /**
	 * @description Used to Set Sorting state of Grid with column name
	 * @example Used only in XMainGrid and XGrid
	 * @param {String}
	 *            sorting
	 */
    this.onSortingChange = sorting => this.setState({ sorting });
    /**
	 * @description
	 * 
	 * You can access every row data Also Used to Map Doulble click action to
	 * the row
	 * @example Used Only in XMainGrid and XGrid
	 * @param {Object}
	 *            tableRowData -
	 * @param {Symbol}
	 *            tableRowData.childeren -React.Components
	 * @param {Object}
	 *            tableRowData.row - Row Data
	 * @param {Object}
	 *            tableRowData.TableRow - {Current RowData,key,type,rowId}
	 */
    this.rowComponent = tableRowData => {
      var { openTab, crudFlags, pk, crudFormId } = this.props;
      return _(
        _dxgrb.Table.Row,
        openTab && crudFlags && crudFlags.edit && pk && crudFormId
          ? {
              ...tableRowData,
              ...{
                onDoubleClick: event =>
                  this.onEditClick({
                    event,
                    rowData: tableRowData.row,
                    openEditable: false
                  }),
                style: { ...tableRowData.style, cursor: "pointer" }
              }
            }
          : tableRowData
      );
    };
    /**
	 * @description will open new page with
	 * @example Used in XMainGrid and XGrid
	 * @param {event}
	 *            event - Event from of the clicked buttuon
	 * @param {state/props}
	 *            grid -state of the grid
	 * @param {Array}
	 *            row - row data to pass into _postInsert
	 */
    this.onOnNewRecord = (event, grid, row) => {
      if (!grid) grid = this.props;
      if (grid.crudFlags && grid.crudFlags.insert && this.props.openTab) {
        var url = "showForm?a=2&_fid=" + grid.crudFormId;
        if (grid._postInsert) {
          url = grid._postInsert(row || {}, url, grid);
          if (!url) return;
        }
        var modal = !!event.ctrlKey;
        this.props.openTab(
          "2-" + grid.gridId,
          url + (modal ? "&_modal=1" : ""),
          {},
          { modal: modal }
        );
      }
    };
    /**
	 * @description prerpares url with query
	 * @example Used in XMainGrid and XGrid to make query inside loadData
	 * @returns {String}
	 */
    this.queryString = () => {
      const { sorting, pageSize, currentPage } = this.state;
      let queryString =
        this.props._url + "&limit="+ pageSize +"&start=" + pageSize * currentPage;
      const columnSorting = sorting[0];
      if (columnSorting) {
        const sortingDirectionString =
          columnSorting.direction === "desc" ? " desc" : "";
        queryString +=
          "&sort=" + columnSorting.columnName + sortingDirectionString;
      }
      return queryString;
    };
    /**
	 * @description Used to Edit and double click on the row
	 * @param {
	 *            Object } param0 - consist of two data evet and Rowdata
	 * @param {
	 *            Event } param0.event - Click event from the Edit button and
	 *            double click on the row
	 * @param {
	 *            rowData } param0.rowData - Data of the row where the Edit
	 *            button or double click clicked
	 */
    this.onEditClick = ({ event, rowData, openEditable }) => {
      var { props } = this;
      var pkz = buildParams2(props.pk, rowData);
      var url = "showForm?a=1&_fid=" + props.crudFormId + pkz;
      if (props._postUpdate) {
        var url = this.props._postUpdate(rowData, url, props);
        if (!url) return;
      }
      var modal = event.ctrlKey && !!event.ctrlKey;
      props.openTab(
        "1-" + pkz,
        url + (modal ? "&_modal=1" : ""),
        {},
        { modal: modal, openEditable }
      );
    };
    /**
	 * todo
	 * 
	 * @param {Object}
	 *            param0 - event from delete button
	 * @param {Event}
	 *            param0.event - event from delete button
	 * @param {Array}
	 *            param0.rowData - data for the deleted Row
	 */
    this.onDeleteClick = ({ event, rowData }) => {
      var { pk, crudFormId } = this.props;
      var pkz = buildParams2(pk, rowData);
      var url = "ajaxPostForm?a=3&_fid=" + crudFormId + pkz;
      yesNoDialog({
        text: "Are you Sure!",
        callback: success => {
          if (success) {
            iwb.request({ url, successCallback: () => this.loadData(true) });
          }
        }
      });
    };
    /**
	 * @description used to make request and fill the grid
	 * @param {boolean}
	 *            force - to fill with up to date data
	 */
    this.loadData = force => {
      if (this.props.rows) return;
      const queryString = this.queryString();
      if (!force && queryString === this.lastQuery) {
        return;
      }
      this.setState({ loading: true });
      iwb.request({
        url: queryString,
        self: this,
        params:
          this.props.searchForm &&
          iwb.getFormValues(document.getElementById(this.props.searchForm.id)),
        successCallback: (result, cfg) => {
          cfg.self.setState({
            rows: result.data,
            totalCount: result.total_count,
            loading: false
          });
        },
        errorCallback: (error, cfg) => {
          cfg.self.setState({
            rows: [],
            totalCount: 0,
            loading: false
          });
        }
      });
      this.lastQuery = queryString;
    };
    // ####################################EDit Grid Common
	// ############################################
    /**
	 * @param {Array}
	 *            editingRowIds - IDs of the Editing rows
	 */
    this.onEditingRowIdsChange = editingRowIds =>
      this.setState({ editingRowIds });
    /**
	 * @description A function that returns a row change object depending on row
	 *              editor values. This function is called each time the row
	 *              editor’s value changes.
	 * @param {object}
	 *            addedRows - (row: any, columnName: string, value: string |
	 *            number)
	 */
    this.onAddedRowsChange = addedRows => {
      var newRecord = Object.assign({}, this.props.newRecord || {});
      var pk = this.state.pkInsert;
      --pk;
      newRecord[this.props.keyField] = pk;
      this.setState({
        pkInsert: pk,
        addedRows: addedRows.map(
          row => (Object.keys(row).length ? row : newRecord)
        )
      });
    };
    /**
	 * @description Handles adding or removing a row changes to/from the
	 *              rowChanges array.
	 * @param {Array}
	 *            rowChanges -(rowChanges: { [key: string]: any }) => void
	 */
    this.onRowChangesChange = rowChanges => {
      this.setState({ rowChanges });
    };
    /**
	 * @description Handles selection changes.
	 * @param {Array}
	 *            selection - (selection: Array<number | string>) => void
	 */
    this.onSelectionChange = selection => {
      this.setState({ selection });
    };
    /**
	 * Used to delete from the frontend
	 * 
	 * @param {Array}
	 *            param0
	 * @param {Array}
	 *            param0.deleted
	 */
    this.onCommitChanges = ({ deleted }) => {
      let { rows, deletedRows } = this.state;
      if (deleted && deleted.length) {
        yesNoDialog({
          text: "Are you Sure!",
          callback: success => {
            if (success) {
              rows = rows.slice();
              deleted.forEach(rowId => {
                const index = rows.findIndex(
                  row => row[this.props.keyField] === rowId
                );
                if (index > -1) {
                  if (rowId > 0) {
                    deletedRows.push(Object.assign({}, rows[index]));
                  }
                  rows.splice(index, 1);
                }
              });
              this.setState({ rows, deletingRows: [], deletedRows });
            }
          }
        });
      }
    };
    /**
	 * @example push id to this.state.deletingRows then this.deleteRows();
	 */
    this.deleteRows = () => {
      const rows = this.state.rows.slice();
      this.state.deletingRows.forEach(rowId => {
        const index = rows.findIndex(row => row.id === rowId);
        if (index > -1) {
          rows.splice(index, 1);
        }
      });
      this.setState({ rows, deletingRows: [] });
    };
  }
}
/**
 * helper componet for MapInput
 */
class XMap extends React.PureComponent {
	  constructor(props) {
	    super(props);
	    /**
		 * there is no state since if we provide state it will start rerendering
		 * itself
		 */
	    this.map;
	    this.script;
	    this.marker;
	    this.geocoder;
	    this.inputNode;
	    this.infoWindow;
	    this.autoComplete;
	    this.elementsWithListeners = [];
	    this.defPosition = { lat: 41.0082, lng: 28.9784 };
	    !props.apiKey && alert("GoogleMaps:::::::this.props.apiKey not provided");
	    this.id = "GoogleMaps" + Math.floor(Math.random() * 1000 + 1);
	    /*
		 * runs on ofter scrip is loaded
		 */
	    this.onScriptLoad = () => {
	      this.map = this.createMap(this.props.mapOpt || {});
	      this.marker = this.createMarker(this.props.markerOpt || {});
	      this.geocoder = this.createGeocoder(this.props.geocoderOpt || {});
	      this.infoWindow = this.createInfoWindow({maxWidth:300,...this.props.infoWindowOpt});
	      this.autoComplete = this.createAutocomplete(
	        this.props.autocompleteOpt || undefined
	      );
	      this.props.onMapLoad && this.props.onMapLoad(this);
	      /** after all the map listeners is set */
	      this.elementsWithListeners.push(this.map);
	      this.elementsWithListeners.push(this.marker);
	      this.elementsWithListeners.push(this.script);
	      this.elementsWithListeners.push(this.geocoder);
	      this.elementsWithListeners.push(this.inputNode);
	      this.elementsWithListeners.push(this.infoWindow);
	      this.elementsWithListeners.push(this.autoComplete);
	    };
	    /**
		 * locate me on the map and used as if stetement for displaying button
		 * of geolocation
		 */
	    this.findMe = () => {
	      if (window.navigator.geolocation) {
	        window.navigator.geolocation.getCurrentPosition(this.findMeOuter);
	        return true;
	      } else {
	        return false;
	      }
	    };
	    /**
		 * A function to create Google Map object
		 * 
		 * @param {Object}
		 *            opt
		 */
	    this.createMap = opt => {
	      let opt1 = {
	        center: this.defPosition,
	        zoom: 8
	      };
	      return new window.google.maps.Map( document.getElementById(this.id), {...opt1, ...opt});
	    };
	    /**
		 * A function return GMarker
		 */
	    this.createMarker = opt => {
	      let opt1 = {
	        position: this.defPosition,
	        draggable: true,
	        map: this.map,
	        title: "default title"
	      };
	      return new window.google.maps.Marker({...opt1, ...opt});
	    };
	    /**
		 * a function used to init geolocation
		 * 
		 * @param {Object}
		 *            opt
		 */
	    this.createGeocoder = opt => {
	      let opt1 = {};
	      return new window.google.maps.Geocoder({...opt1, ...opt})
	    };
	    /**
		 * a function to create InfoWindow
		 * 
		 * @param {Object}
		 *            opt
		 */
	    this.createInfoWindow = opt => {
	      let opt1 = {
	        content: `<div id="infoWindow" />`,
	        position: this.defPosition
	      };
	      return new window.google.maps.InfoWindow({...opt1, ...opt});
	    };
	    /**
		 * A function return Autocomplete
		 * 
		 * @param {HTMLElement}
		 *            inputNode
		 */
	    this.createAutocomplete = (
	      inputNode = document.getElementById("pac-input")
	    ) => {
	      this.inputNode = inputNode;
	      return new window.google.maps.places.Autocomplete(inputNode);
	    };
	    /**
		 * function to remove listeners from the dom element
		 * 
		 * @param {HTMLElement}
		 *            element
		 */
	    this.removeAllEventListenersFromElement = element => {
	      /**
			 * to find out if it is a dom object
			 */
	      if (element && element.cloneNode) {
	        let clone = element.cloneNode();
	        // move all child elements from the original to the clone
	        while (element.firstChild) {
	          clone.appendChild(element.lastChild);
	        }
	        element.parentNode.replaceChild(clone, element);
	      }
	    };
	    /**
		 * A function to remove listeners from the array of obj
		 * 
		 * @param {Array}
		 *            elements
		 */
	    this.removeAllEventListenersFromElements = (elements = []) => {
	      /** cheks if it is array */
	      elements &&
	        typeof elements.length === "number" &&
	        elements.length > 0 &&
	        elements.map(this.removeAllEventListenersFromElement);
	    };
	  }
	  /**
		 * Used to load script to the body
		 */
	  componentDidMount() {
	    if (!window.google) {
	      this.script = document.createElement("script");
	      this.script.id = "script-" + this.id;
	      this.script.type = "text/javascript";
	      this.script.async = false;
	      this.script.src = `https://maps.googleapis.com/maps/api/js?key=${
	        this.props.apiKey
	      }&libraries=places`;
	      var xscript = document.getElementsByTagName("script")[0];
	      xscript.parentNode.insertBefore(this.script, xscript);
	      // Below is important.
	      // We cannot access google.maps until it's finished loading
	      this.script.addEventListener("load", e => {
	        this.onScriptLoad();
	      });
	    } else {
	      this.onScriptLoad();
	    }
	  }
	  /**
		 * Used to delete all listeners and delete script from the body but all
		 * the google func will work
		 */
	  componentWillUnmount() {
	    this.removeAllEventListenersFromElements(this.elementsWithListeners);
	  }
	  render() {
	    return React.createElement(
	      React.Fragment,
	      null,
	      React.createElement(
	        PopoverHeader,
	        null,
	        React.createElement(
	          FormGroup,
	          null,
	          React.createElement(Label, { for: "exampleEmail" }, "Address"),
	          React.createElement('div', {style:{cursor: 'pointer'},className:'float-right', onClick:()=>{this.props.onClick(false)}},
	        		  React.createElement('i',{className:'icon-close'})
	        		  ),
	          React.createElement(
	            InputGroup,
	            { type: "text", name: "name" },
	            React.createElement(
	              InputGroupAddon,
	              { hidden: !!this.findMe, addonType: "prepend" },
	              React.createElement(
	                Button,
	                {
	                  disabled: !!this.findMe,
	                  type: "submit",
	                  onClick: this.findMe
	                },
	                React.createElement("i", { className: "icon-location-pin" })
	              )
	            ),
	            React.createElement(Input, {
	              id: "pac-input",
	              type: "text",
	              placeholder: "Enter a location"
	            }),
	            React.createElement(
	              InputGroupAddon,
	              { addonType: "append" },
	              React.createElement(
	                Button,
	                { type: "submit", onClick: this.props.onClick, className:'btn btn-success'},
	                _('i', {className:'icon-pin'})
	              )
	            )
	          )
	        )
	      ),
	      React.createElement(
	        PopoverBody,
	        null,
	        React.createElement("div", {
	          style: {
	            width: this.props.width || 400,
	            height: this.props.height || 400
	          },
	          id: this.id
	        })
	      )
	    );
	  }
	}
class MapInput extends React.PureComponent {
	  constructor(props) {
      super(props);
      let st = (props.stringifyResult && props.value)? JSON.parse(props.value):props.value;
	    this.state = {
	      zoom: st.zoom || 8,
	      maptype: st.maptype || "roadmap",
	      formatted_address: st.formatted_address || "",
	      place_id: st.place_id ||  "",
	      place_lat: st.place_lat||  "",
	      place_lng: st.place_lng ||  "",
	      mapOpen: false,
	    };
	    this.popoverId = this.props.id
	      ? "popoverId" + this.props.id
	      : "popoverId" + Math.floor(Math.random() * 1000 + 1);
	    /**
		 * a function used to hide and open the map on the DOM
		 */
	    this.toggle = () => {
	      this.setState(prevState => ({
	        mapOpen: !prevState.mapOpen
	      }));
	    };
	    /**
		 * a function used to render info window content
		 */
	    this.getInfoWindowContent = () => {
	      return `
	            <div class="">
	                <div class="card-body">
	                    <h5 class="card-title text-center">
	                    		<i class="navbar-brand icon-globe"></i>
	                    ${this.state.formatted_address}</h5>
	                </div>
	            </div>
	            `;
	    };
	    /**
		 * it is a callback function which will work after imporing the google
		 * script
		 * 
		 * @param {object}
		 *            innerScope - state of the internal component
		 */
	    this.onMapLoad = innerScope => {
	      innerScope.geocoder.geocode(
	    	(this.state.place_id)?{'placeId':this.state.place_id}:{ latLng: innerScope.defPosition || undefined },
	        (result, status) => {
	          if (
	            status === window.google.maps.GeocoderStatus.OK &&
	            result.length > 0
	          ) {
	            let {
	              place_id,
	              formatted_address,
	              geometry: { location }
	            } = result[0];
	            this.setState({
	              place_id,
	              formatted_address,
	              place_lat: location.lat(),
	              place_lng: location.lng()
	            });
	            innerScope.map.setCenter(location);
	            innerScope.marker.setPosition(location);
	            innerScope.infoWindow.setPosition(location);
	            innerScope.inputNode.value = formatted_address;
	            innerScope.infoWindow.setContent(`${formatted_address}`);
	            innerScope.infoWindow.open(innerScope.map);
	          }
	        }
	      );
	      /** when the marker is clicked */
	      innerScope.marker.addListener("click", event => {
	        let location = innerScope.marker.getPosition();
	        innerScope.inputNode.value = this.state.formatted_address;
	        innerScope.infoWindow.setPosition(location);
	        innerScope.infoWindow.setContent(`${this.getInfoWindowContent()}`);
	        innerScope.infoWindow.open(innerScope.map);
	      });
	      /** after marker is left */
	      innerScope.marker.addListener("dragend", event => {
	        let dragedPoint = innerScope.marker.getPosition();
	        innerScope.map.panTo(dragedPoint);

	        innerScope.geocoder.geocode(
	          { latLng: dragedPoint },
	          (result, status) => {
	            if (
	              status === window.google.maps.GeocoderStatus.OK &&
	              result.length > 0
	            ) {
	              let {
	                place_id,
	                formatted_address,
	                geometry: { location }
	              } = result[0];
	              this.setState({
	                place_id,
	                formatted_address,
	                place_lat: location.lat(),
	                place_lng: location.lng()
	              });
	              innerScope.map.setCenter(location);
	              innerScope.marker.setPosition(location);
	              innerScope.inputNode.value = formatted_address;
	              innerScope.infoWindow.setPosition(location);
	              innerScope.infoWindow.setContent(
	                `${this.getInfoWindowContent()}`
	              );
	              innerScope.infoWindow.open(innerScope.map);
	            }
	          }
	        );
	      });
	      /** lisens for the place change */
	      innerScope.autoComplete.addListener("place_changed", () => {
	        let place = innerScope.autoComplete.getPlace();
	        // return if the auto compleate is not selected from the drop down
	        if (!place.geometry) return;
	        let {
	          place_id,
	          formatted_address,
	          geometry: { location }
	        } = place;
	        this.setState({
	          place_id,
	          formatted_address,
	          place_lat: location.lat(),
	          place_lng: location.lng()
	        });
	        // bring the selected place in view on the innerScope.map
	        // innerScope.map.fitBounds(place.geometry.viewport);
	        innerScope.map.setCenter(location);
	        innerScope.marker.setPosition(location);
	        innerScope.infoWindow.setPosition(location);
	        innerScope.infoWindow.setContent(`${this.getInfoWindowContent()}`);
	        innerScope.infoWindow.open(innerScope.map);
	      });

	      innerScope.findMeOuter = position => {
	        let pos = new window.google.maps.LatLng(
	          position.coords.latitude,
	          position.coords.longitude
	        );
	        innerScope.geocoder.geocode({ latLng: pos }, (result, status) => {
	          if (
	            status === window.google.maps.GeocoderStatus.OK &&
	            result.length > 0
	          ) {
	            let {
	              place_id,
	              formatted_address,
	              geometry: { location }
	            } = result[0];
	            this.setState({
	              place_id,
	              formatted_address,
	              place_lat: location.lat(),
	              place_lng: location.lng()
	            });
	            innerScope.map.setCenter(location);
	            innerScope.marker.setPosition(location);
	            innerScope.infoWindow.setPosition(location);
	            innerScope.inputNode.value = formatted_address;
	            innerScope.infoWindow.setContent(`${formatted_address}`);
	            innerScope.infoWindow.open(innerScope.map);
	          }
	        });
	      };
	    };
	    /**
		 * a function used to give id of the table Row in db
		 * 
		 * @param {event}
		 *            event
		 */
	    this.onClick = event => {
	    	this.toggle();
	    	if(!event)return;
	    	event.preventDefault();
	    	event.target = {...this.props , value: this.state, stringValue:JSON.stringify(this.state) }
	    	this.props.onChange && this.props.onChange(event);	      
	    };
	  }
	  render() {
	    return React.createElement(
	      React.Fragment,
	      null,
	      React.createElement(
	        InputGroup,
	        { type: "text", name: "name", id: this.popoverId},
	        React.createElement(Input, {
	          type: "text",
	          value: this.state.formatted_address,
	          readOnly: true,
	          disabled:!!this.props.disabled
	        }),
	        React.createElement(
	          InputGroupAddon,
	          { addonType: "append" },
	          React.createElement(
	            Button,
	            {
	              className: "mr-1 btn-success",
	              onClick: this.toggle,
	              color: "success",
	              disabled:!!this.props.disabled
	            },
	            React.createElement("i", { className: "icon-map" })
	          )
	        )
	      ),
	      React.createElement(
	        Popover,
	        {
	          className: "gMapPopover",
	          placement: "bottom-end",
	          isOpen: this.state.mapOpen,
	          target: this.popoverId,
	          toggle: this.toggle
	        },
	        React.createElement(XMap, {
	          apiKey: _app.map_api,
	          onMapLoad: this.onMapLoad,
	          onClick: this.onClick
	        })
	      )
	    );
	  }
	}
/**
 * A component to render Masonry layout
 * 
 * @param {Object}
 *            props.masonryRowStyle - style of the container
 * @param {Object}
 *            props.masonryStyle - style of the container
 * @param {Object}
 *            props.columnStyle - style of the column
 * @example ```jsx <XMasonry loadingComponent = {()=>{return '***********you can
 *          give loading component***********'}} breakPoints={[350, 500, 750]}
 *          loadNext={({columns,totalItems}) => { {columns,totalItems} - use
 *          this to construct url}} >{ this.state.photos.map((image, id) =>(
 *          <img key={id} src={image}/> ) ) } </XMasonry> ```
 */
class XMasonry extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      columns: 1,
      prevY: 0,
      loading: false
    };
    /**
	 * a funntion used to calculate columns when resized
	 */
    this.onResize = () => {
      const columns = this.getColumns(this.refs.Masonry.offsetWidth);
      if (columns !== this.state.columns){
        this.setState({ columns: columns });
      } 
    };
    /**
	 * a function used to calculate columns from this.props.breakPoints
	 * 
	 * @param {Number}
	 *            width - width of the masonry component
	 */
    this.getColumns = width => {
      return (
        this.props.breakPoints.reduceRight((p, c, i) => {
          return c < width ? p : i;
        }, this.props.breakPoints.length) + 1
      );
    };
    /**
	 * a function used to calculate children according to column size
	 */
    this.mapChildren = () => {
      let col = [];
      const numC = this.state.columns;
      for (let i = 0; i < numC; i++) {
        col.push([]);
      }
      return this.props.children.reduce((p, c, i) => {
        p[i % numC].push(c);
        return p;
      }, col);
    };
    /**
	 * a function used to call loadNext method to make lazyLoading from the rest
	 * 
	 * @param {*}
	 *            entities
	 * @param {*}
	 *            observer
	 */
    this.handleObserver = (entities, observer) => {
      const y = entities[0].boundingClientRect.y;
      if (this.state.prevY > y) {
        this.props.loadNext && this.setState({ loading: true });
        this.props.loadNext && !this.props.loadNext(
          {
            columns: this.mapChildren().length,
            totalItems: this.props.children.reduce((tot, ch) => tot + 1, 0)
          }
        ) && this.setState({ loading: false });
      }
      this.setState({ prevY: y });
    };
  }

  componentDidUpdate(prevProps, prevState, snapshot) {
    if (
      prevProps.children.length < this.props.children.length &&
      this.props.loadNext
    ) {
      this.setState({ loading: false });
    }
    if(prevProps.breakPoints.length !== this.props.breakPoints.length){
      this.onResize();
    }
    return true;
  }
  componentDidMount() {
    // initial resize
    this.onResize();
    // add listener for window object
    window.addEventListener("resize", this.onResize, true);
    if (this.props.loadNext) {
      // Create an observer
      this.observer = new IntersectionObserver(
        this.handleObserver.bind(this), // callback
        {
          root: null, // Page as root
          rootMargin: "0px",
          threshold: 0.01
        }
      );
      // Observ the `loadingRef`
      this.observer.observe(this.refs.loadingRef);
    }
  }
  componentWillUnmount(){
    window.removeEventListener("resize", this.onResize, true);
  }
  render() {
    const masonryStyle = this.props
    return React.createElement(
      Row,
      {
        className:`xMasonryRoot overflowY-auto scrollY`,
        ...this.props.root
      },
      React.createElement(
        "div",
        { 
          className:'d-flex flex-row justify-content-center align-content-stretch flex-fill m-auto w-100',
          style: masonryStyle,
          ref: "Masonry",
          ...this.props.rootInner
        },
        this.mapChildren().map((col, ci) => {
          return React.createElement(
            Col,
            { className: "pr-2 pl-2", style: this.props.columnStyle, key: ci },
            col.map((child, i) => {
              return React.createElement(
                Card,
                { key: i, className: "mt-2 mb-2" , ...this.props.item },
                child
              );
            })
          );
        })
      ),
      React.createElement(
        "div",
        {
          ref: "loadingRef",
          style: {
            height: "10%",
            width: "100%",
            margin: "0px",
            display: this.props.loadNext? "block" : "none"
          }
        },
        React.createElement(
          "span",
          { style: { display: this.state.loading ? "block" : "none" } },
          this.props.loadingComponent
            ? this.props.loadingComponent()
            : "Loading..."
        )
      )
    );
  }
}
/**
 * XAjaxQueryData - function is used to get data by giving guery id
 * 
 * @param {String}
 *            props.qui - query id that you want to get data from
 * @param {Function}
 *            props.middleMan
 * @param {Symbol}
 *            props.children
 * @example React.createElement(XAjaxQueryData,{},data=>{ return
 *          React.createElement(AnyComponent,{data}......) }
 */
class XAjaxQueryData extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = { data: [] };
    /** to get data from backend */
    this.fetch = () => {
      // todo: build url
      let self = this;
      iwb.request({
        url: "ajaxQueryData?" + "_qid=" + this.props.qid,
        successCallback: ({ data }) => {
          self.setState({
            data:
              this.props.middleMan && typeof this.props.middleMan === "function"
                ? this.props.middleMan(data)
                : data
          });
        }
      });
    };
  }
  componentDidMount() {
    this.fetch();
  }
  render() {
    return _(
      React.Fragment,
      {},
      this.props &&
      this.props.children &&
      typeof this.props.children === "function"
        ? this.props.children(this.state.data)
        : this.props.children
    );
  }
}
/**
 * A function to load script from the CDN or filesystem and apply css
 * 
 * @param {String}
 *            props.css - query id that you want to get data from
 * @param {Array/String}
 *            props.loadjs - used to define which script to download see exapmle
 *            below
 * @param {Array/String}
 *            props.loadcss - used to define which css script to download see
 *            exapmle below
 * @param {Symbol}
 *            props.loading - conponent to show loading indicator while feching
 *            scripts from CDN or static file
 * @param {Symbol}
 *            props.children
 * @example _(XLazyScriptLoader,{loading:React.createElement(CustomLoadingComponent,{options}),css:`.customClassName{color:red}`,
 *          loadjs:['CDN','CDN2']||'CDN' }, childNode )
 */
class XLazyScriptLoader extends React.PureComponent {
    constructor(props) {
        super(props);
        this.state = {
            loading: true
        }
        /**
		 * a self invoking function to load js and css into Dom from source
		 * {cdn,server,local.....}
		 */
        this.load = (() => {
            // Function which returns a function:
			// https://davidwalsh.name/javascript-functions
            var _load = (tag) => {
                return (src) => {
                    // This promise will be used by Promise.all to determine
					// success or failure
                    return new Promise( (resolve, reject) => {
                        let element = document.createElement(tag);
                        let parent = 'body';
                        let attr = 'src';
                        // Important success and error for the promise
                        element.onload = e => resolve(src);
                        element.onerror = e => reject(src);
                        // Need to set different attributes depending on tag
						// type
                        switch (tag) {
                            case 'script':
                                element.async = false;
                                break;
                            case 'link':
                                element.type = 'text/css';
                                element.rel = 'stylesheet';
                                attr = 'href';
                                parent = 'head';
                                break;
                            default:
                        }
                        // Inject into document to kick off loading
                        element[attr] = src;
                        window.document[parent].appendChild(element);
                    });
                };
            }
            return {
                css: _load('link'),
                js:  _load('script'),
                img: _load('img')
            }
        })();
    }
    componentDidMount() {
        let arrayProm = []
        let {loadcss,loadjs, css} = this.props;
        loadcss && arrayProm.push(...(loadcss.constructor === Array)?loadcss.map(item=>this.load.css(item)):[this.load.css(loadcss)]);
        loadjs && arrayProm.push(...(loadjs.constructor === Array)?loadjs.map(item=>this.load.js(item)):[this.load.js(loadjs)]);
        Promise.all(arrayProm).then(() => {
            this.setState({ loading: false})
        }).catch(() => {
            console.error('Oh no, epic failure!');
            alert('Oh no, epic failure!');
        });
        iwb.addCssString(css);
    }
    render() {
        return React.createElement(React.Fragment, {},(this.state.loading)?this.props.loading:this.props.children)
    }
}
 // Set default props
 XLazyScriptLoader.defaultProps = {
   loading: "LOADING....",
 };
 XLazyScriptLoader.propTypes = {
   loading: PropTypes.oneOfType([
     PropTypes.func,
     PropTypes.string,
   ])
 };
const XPreviewFile = ({
  file
}) => {
  let type = file ? file.type : null;
  let style = {
    fontSize: '12em'
  };
  switch (type) {
    case 'image/png':
      return _('img', {
        src: URL.createObjectURL(file),
        className: 'img-fluid rounded'
      })
    case 'text/plain':
      return _('i', {
        style,
        className: 'fas fa-file-alt m-auto'
      })
    case 'application/pdf':
      return _('i', {
        style,
        className: 'fas fa-file-pdf m-auto'
      })
    default:
      return _('div', {className:'m-auto text-center'},
      file ? _('i',{className:'far fa-file',style}) : _('i',{className:'fas fa-upload',style}),
        _('br',null),
        getLocMsg(file ? 'undefined_type' : 'choose_file_or_drag_it_here')
      )
  }
}
class XListFiles extends React.Component {
  constructor(){
    super()
    this.state = {
      files:[]
    }
    this.getFileList = this.getFileList.bind(this)
    this.deleteItem = this.deleteItem.bind(this)
    this.downladLink = this.downladLink.bind(this)
  }
  /** run query to get data based on pk and id */
  getFileList(){
    iwb.request({
      url:'ajaxQueryData?_qid=61&xtable_id='+this.props.cfg.crudTableId+'&xtable_pk='+ (this.props.cfg.tmpId ? this.props.cfg.tmpId : json2pk(this.props.cfg.pk))+'&.r='+Math.random(),
      successCallback: ({data}) => {
        this.setState({
          files:data
        })
      }
    })
  }
  deleteItem(fileItem) {
    return (event) => {
      event.preventDefault();
      event.stopPropagation();
      /** deleteRequest */
      iwb.request({
        url: 'ajaxPostForm?a=3&_fid=1383&tfile_attachment_id='+fileItem.file_attachment_id,
        successCallback: (res) => {
          this.setState({
            files: this.state.files.filter(file => file.file_attachment_id != fileItem.file_attachment_id)
          })
        }
      })
    }
  }
  /** test */
  downladLink(fileItem) {
    let url = 'dl/'+fileItem.original_file_name+'?_fai='+fileItem.file_attachment_id+'&.r='+Math.random();
    return (event) => {
      event.preventDefault();
      event.stopPropagation();
      const link = document.createElement('a');
      link.href = url ;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  }
  componentDidMount(){ this.getFileList() }
  render() {
    return _(
      ListGroup, {},
      this.state.files.map(fileItem => _(ListGroupItem, null,
        _('a', { onClick:this.downladLink(fileItem),href:'#' }, fileItem.original_file_name),
        _('i', {
          key: fileItem.file_attachment_id,
          onClick: this.deleteItem(fileItem),
          style:{ cursor: 'pointer' },
          className: 'icon-trash float-right text-danger'
        })
      ))
    )
  }
}
class XSingleUploadComponent extends React.Component {
  constructor() {
    super();
    this.state = {
      canUpload: false,
      dragOver: false,
      file: null
    };
    this.xListFilesRef = React.createRef();
    this.onDrop = this.onDrop.bind(this);
    this.dragenter = this.dragenter.bind(this);
    this.dragleave = this.dragleave.bind(this);
    this.dragover = this.dragover.bind(this);
    this.onDeleteFile = this.onDeleteFile.bind(this);
    this.onclick = this.onclick.bind(this);
    this.onchange = this.onchange.bind(this);
    this.uplaodFile = this.uplaodFile.bind(this);
  }
  /** function to click input ref click */
  onclick(event) {
    event.preventDefault();
    event.stopPropagation();
    this.inpuRef.click();
  }
  /** used to disable opening file on new tab */
  dragover(event) {
    event.preventDefault();
    event.stopPropagation();
  }
  /** used with css */
  dragleave(event) {
    event.preventDefault();
    event.stopPropagation();
    this.setState({
      dragOver: false
    });
  }
  /** when the file over drag area */
  dragenter(event) {
    event.preventDefault();
    event.stopPropagation();
    this.setState({
      dragOver: true
    });
  }
  /** when the file dproped over drop area */
  onDrop(event) {
    event.preventDefault();
    event.stopPropagation();
    this.setState({
      canUpload: true,
      dragOver: false,
      file: event.dataTransfer.files[0]
    },()=>{
      this.uplaodFile()
    })
  }
  /** when the file dproped over drop area */
  onchange(event) {
    event.preventDefault();
    event.stopPropagation();
    this.setState({
      canUpload: true,
      dragOver: false,
      file: event.target.files[0]
    },()=>{
      this.uplaodFile();
    })
  }
  /** remove file from form state */
  onDeleteFile(event) {
    event.preventDefault();
    event.stopPropagation();
    /** will reset to null currently uploaded file */
    this.setState({
      canUpload: false,
      file: null
    })
  }
  /** uploader function */
  uplaodFile() {
    // event.preventDefault();
    // event.stopPropagation();
    if (!this.state.file) {
      return;
    }
    let formData = new FormData()
    formData.append('table_pk', this.props.cfg.tmpId ? this.props.cfg.tmpId : json2pk(this.props.cfg.pk))
    formData.append('table_id', this.props.cfg.crudTableId)
    formData.append('file', this.state.file)
    formData.append('profilePictureFlag', this.props.profilePictureFlag || 0)
    fetch('upload.form', {
        method: 'POST',
        body: formData,
        cache: 'no-cache',
        credentials: 'same-origin',
        mode: 'cors',
        redirect: 'follow',
        referrer: 'no-referrer'
      })
      .then(response => response.status === 200 || response.status === 0 ? response.json() : Promise.reject(new Error(response.text() || response.statusText)))
      .then(
        result => {
          if (result.success) {
            toastr.success(getLocMsg('file_sucessfully_uploaded!'), getLocMsg('Success'), {
              timeOut: 3000
            });
            this.xListFilesRef.current.getFileList();
            this.setState({
              file: null,
              canUpload: false
            })

          } else {
            if (result.error) {
              toastr.error(result.error, result.errorType);
            }
            return;
          }
        },
        error => {
          toastr.error(error, getLocMsg('Error'));
        }
      )
  }
  render() {
    let defaultStyle = {
      height: '100%',
      width: '100%',
      position: 'absolute',
      top: '0',
      left: '0'
    }
    return _(React.Fragment, {},
      _(Button, {
          id: this.props.cfg.id,
          type: 'button',
          className: 'float-right btn-round-shadow mr-1',
          color: 'light'
        },
        _('i', {
          className: 'icon-paper-clip'
        })
      ),
      _(Reactstrap.UncontrolledPopover, {
          trigger: 'legacy',
          placement: 'auto',
          target: this.props.cfg.id
        },
        _(PopoverHeader, null,
          this.state.file ? getLocMsg(this.state.file.name) : getLocMsg('File Upload'),
          _('input', {
            className: 'd-none',
            type: 'file',
            onChange: this.onchange,
            ref: input => this.inpuRef = input
          }),
          this.props.extraButtons && this.props.extraButtons
        ),
        _(PopoverBody,
          null,
          _('div', {
              style: {
                height: '200px',
                width: '200px',
                position: 'relative',
                border: this.state.dragOver ? '3px dashed #20a8d8' : '3px dashed #a4b7c1'
              }
            },
            _('div', {
              style: {
                ...defaultStyle,
                zIndex: '10',
                background: 'gray',
                cursor: 'pointer',
                opacity: this.state.canUpload ? '0' : '0.5',
              },
              className: 'rounded',
              onDrop: this.onDrop,
              onDragEnter: this.dragenter,
              onDragLeave: this.dragleave,
              onDragOver: this.dragover,
              onClick: this.onclick
            }),
            _('div', {
                style: {
                  ...defaultStyle,
                  display: 'flex'
                }
              },
              _(XPreviewFile, {
                file: this.state.file
              }))
          ),
          _('div', {
            className: 'clearfix'
          }),
          _(XListFiles,{cfg: this.props.cfg, ref: this.xListFilesRef})
        )
      )
    )
  }
}
/**
 * @description used to render tab and show active tab on the full XPage
 * @param {Object}
 *            props.body - it renders bodyForm class wich came from the backend
 * @param {Object}
 *            props.cfg - config of the form [edit or intest, id of the form]
 * @param {Object}
 *            props.parentCt - [xpage]-a function to open and close tab from the
 *            form
 * @param {Object}
 *            props.callAttributes - extra props to XTabForm
 * @param {Object}
 *            props.callAttributes.openEditable - open form in edit mode
 */
class XTabForm extends React.PureComponent {
  constructor(props) {
    if (iwb.debug) console.log("XTabForm.constructor", props);
    super(props);
    this.state = {
      viewMode:
        this.props.callAttributes && this.props.callAttributes.openEditable
          ? false
          : this.props.cfg.a == 1
    };
    /**
	 * a function to make editable and non editable
	 */
    this.toggleViewMode = () =>
      this.setState({ viewMode: !this.state.viewMode });
    /**
	 * a function to send form data
	 * 
	 * @param {Event}
	 *            event
	 */
    this.onSubmit = event => {
      event && event.preventDefault && event.preventDefault();
      var selfie = this;
      if (this.form) {
        this.form.submit({
          callback: (json, cfg) => {
            var url = "showForm";
            if (json.outs) {
              url += "?a=1&_fid=" + json.formId;
              for (var key in json.outs)
                url += "&t" + key + "=" + json.outs[key];
            } else {
              url += cfg.url.substring("ajaxPostForm".length);
            }
            // console.log(selfie.props);
            selfie.props.callAttributes.callback &&
            selfie.props.callAttributes.callback(json, cfg);
            toastr.success(
              "Click! To see saved item <a href=# onClick=\"return iwb.openForm('" +
                url +
                "')\"></a>",
              "Saved Successfully",
              { timeOut: 3000 }
            );
            var { parentCt } = selfie.props;
            if (parentCt) {
              iwb.closeModal();
              iwb.closeTab();
              iwb.onGlobalSearch2 && iwb.onGlobalSearch2("");
            }
          }
        });
      } else alert("this.form not set");
      return false;
    };
    this.onContSubmit = event => {
        event && event.preventDefault && event.preventDefault();
        var selfie = this;
        if (this.form) {
          this.form.submit({
            callback: (json, cfg) => {
              var url = "showForm";
              if (json.outs) {
                url += "?a=1&_fid=" + json.formId;
                for (var key in json.outs)
                  url += "&t" + key + "=" + json.outs[key];
              } else {
                url += cfg.url.substring("ajaxPostForm".length);
              }
              console.log(selfie.props);
              selfie.props.callAttributes.callback &&
              selfie.props.callAttributes.callback(json, cfg);
              toastr.success(
                "Click! To see saved item <a href=# onClick=\"return iwb.openForm('" +
                  url +
                  "')\"></a>",
                "Saved Successfully",
                { timeOut: 3000 }
              );
            }
          });
        } else alert("this.form not set");
        return false;
      };
    /**
	 * a function to delete current editing record
	 * 
	 * @param {event}
	 *            event
	 */
    this.deleteRecord = event => {
      event && event.preventDefault && event.preventDefault();
      let { formId, pk } = this.props.cfg;
      let pkz = "";
      for (let key in pk) {
        pkz += "&" + key + "=" + pk[key];
      }
      let url = "ajaxPostForm?a=3&_fid=" + formId + pkz;
      yesNoDialog({
        text: "Are you Sure?",
        callback: success =>
          success &&
          iwb.request({
            url,
            successCallback: () => this.props.parentCt.closeTab(event, success)
          })
      });
    };
    this.approvalAction = action => {
    	return (event) => {
          event && event.preventDefault && event.preventDefault();
          let { formId, pk } = this.props.cfg;
          let pkz = "";
          for (let key in pk) {
            pkz += "&" + key + "=" + pk[key];
          }
          let url = "";
          switch(action){
          case	901:// start approval
        	  url = "ajaxApproveRecord?_aa=901&_arid=" + this.props.cfg.approval.approvalRecordId;
              yesNoDialog({
                text: "Are you Sure to Start Approval?",
                callback: success =>
                  success &&
                  iwb.request({
                    url,params:{_adsc:'start approval'},
                    successCallback: () => this.props.parentCt.closeTab(event, success)
                  })
              });
              break;
          default:
        	  var p = prompt("Please enter comment", ["","Approve","Return","Reject"][action]);
          	  if(p){
	              url = "ajaxApproveRecord?_aa="+action+"&_arid=" + this.props.cfg.approval.approvalRecordId;
                  iwb.request({
	                    url,params:{_adsc:p,_avno:this.props.cfg.approval.versionNo},
	                    successCallback: () => this.props.parentCt.closeTab(event, true)
	                    
                  });
          	  }
          break;
          }

        };
      }
  }
  
  render() {
    let {
      props: {
        body,
        parentCt: { closeTab },
        cfg: { deletable, name }
      },
      state: { viewMode },
      // methods
      onSubmit, onContSubmit, 
      deleteRecord, approvalAction,
      toggleViewMode
    } = this;

    let formBody = _(body, { parentCt: this, viewMode });
    if (!formBody) return null;
    return _(
      Form,
      { onSubmit: event => event.preventDefault() },
      _(
        CardBlock,
        { className: "card-body" },
        _(
          "h3",
          {
            className: "form-header"
          } /* _("i",{className:"icon-star form-icon"})," ", */,
          name,
          " ",
          !this.props.cfg.viewMode && viewMode &&
            _(
              Button,
              {
                color: "light",
                className: "btn-form-edit",
                onClick: toggleViewMode
              },
              _("i", { className: "icon-pencil" }),
              " ",
              getLocMsg('js_edit')
            ),
          " ",
          viewMode &&
            _(
              Button,
              { color: "light", className: "btn-form-edit", onClick: iwb.closeTab },
              getLocMsg('close')
            ),
          " ",
          viewMode &&
            deletable &&
            _(
              Button,
              {
                color: "danger",
                className: "btn-form-edit",
                onClick: deleteRecord
              },
              _("i", { className: "icon-trash" }),
              " ",
              getLocMsg('delete')
            ),
          false && _(
            Button,
            {
              className: "float-right btn-round-shadow hover-shake",
              color: "danger"
            },
            _("i", { className: "icon-options" })
          ),
          " ",
          this.props.cfg.commentFlag && _(
            Button,
            { className: "float-right btn-round-shadow mr-1", color: "light" },
            _("i", { className: "icon-bubbles" })
          ),
          " ",
          this.props.cfg.fileAttachFlag && _(XSingleUploadComponent, {
            cfg: this.props.cfg
          })
          , _('br'),
          this.props.cfg.approval && this.props.cfg.approval.stepDsc &&
          _(
            'span',
            {style:{fontSize:"1rem"}
            },
// " step ",
            _("b",null,this.props.cfg.approval.stepDsc)
            ,"    "
          ),
          this.props.cfg.approval && this.props.cfg.approval.wait4start &&
          _(
            Button,
            {
              color: "success",
              className: "btn-form-edit",
              onClick: approvalAction(901)
            },
            _("i", { className: "icon-support" }),
            " ",
            getLocMsg('start_approval')
          ),
          this.props.cfg.approval && this.props.cfg.approval.versionNo &&
          _(
            Button,
            {
              color: "success",
              className: "btn-form-edit",
              onClick: approvalAction(1) // approve
            },
            _("i", { className: "icon-shield" }),
            " ",
            getLocMsg('approve')
          ),
          " "
          ,this.props.cfg.approval && this.props.cfg.approval.returnFlag &&
          _(
            Button,
            {
              color: "warning",
              className: "btn-form-edit",
              onClick: approvalAction(2) // return
            },
            _("i", { className: "icon-shield" }),
            " ",
            getLocMsg('return')
          ),
          " "
          ,this.props.cfg.approval && this.props.cfg.approval.versionNo &&
          _(
            Button,
            {
              color: "secondary",
              className: "btn-form-edit",
              onClick: approvalAction(3) // reject
            },
            _("i", { className: "icon-shield" }),
            " ",
            getLocMsg('reject')
          ),
          " "
          ,this.props.cfg.approval && this.props.cfg.approval.approvalRecordId &&
          _(
            Button,
            {
              color: "light",
              className: "btn-form-edit",
              onClick: iwb.approvalLogs(this.props.cfg.approval.approvalRecordId) // reject
            },
            _("i", { className: "icon-eye" }),
            " ",
            getLocMsg('logs')
          )
        ),
        _("hr"),
        formBody
      ),
      !viewMode &&
        _(
          CardFooter,
          { style: { padding: "1.1rem 1.25rem" } },
          _(
            Button,
            {
              type: "submit",
              color: "submit",
              className: "btn-form mr-1",
              onClick: onSubmit
            },
            " ",
            "Save",
            " "
          ),
          " ",this.props.cfg.contFlag && _(
                  Button,
                  {
                    type: "submit",
                    color: "secondary",
                    className: "btn-form mr-1",
                    onClick: onContSubmit
                  },
                  " ",
                  "Save & Continue",
                  " "
                ),
                " ",
          _(
            Button,
            {
              color: "light",
              style: { border: ".5px solid #e6e6e6" },
              className: "btn-form",
              onClick: iwb.closeTab
            },
            "Cancel"
          )
        )
    );
  }
}
/**
 * @description Used for PopUp a Modal it is singletone and you can use
 * @example iwb.showModal(cfg); iwb.closeModal
 * @param {Object}
 *            props -props of the Xmodal
 */
class XModal extends React.Component {
  constructor(props) {
    super(props);
    this.state = { modal: false };
    /**
	 * @description Used to construct Modal (popup)
	 * @example iwb.showModal(cfg); iwb.closeModal
	 * @param {Object}
	 *            cfg - Moadal Configuration
	 * @param {String}
	 *            cfg.title - Moadal title
	 * @param {String}
	 *            cfg.color - Moadal Color 'primary'
	 * @param {String}
	 *            cfg.size - Moadal Size 'lg' 'md' 'sm'
	 * @param {Symbol}
	 *            cfg.body - Moadal body React.Component
	 * @param {Object}
	 *            cfg.style - Moadal style
	 * @param {Object}
	 *            cfg.footer - Moadal Configuration
	 * @param {Object}
	 *            cfg.modalBodyProps - Moadal Body Props to pass to the body of
	 *            the modal
	 */
    this.open = cfg => {
      this.setState({
        modal: true,
        body: cfg.body,
        style: cfg.style,
        footer: cfg.footer,
        size: cfg.size || "lg",
        title: cfg.title || "Form",
        color: cfg.color || "primary",
        modalBodyProps: cfg.modalBodyProps || {},
        props: cfg.props || {}
      });
      return false;
    };
    /**
	 * @description Used to close the Modal (actually hide)
	 */
    this.close = () => this.setState({ modal: false });
    this.toggle = () => this.setState({ modal: !this.state.modal });
    iwb.showModal = this.open;
    iwb.closeModal = this.close;
  }

  render() {
    const {
      modal,
      footer,
      size,
      style,
      color,
      title,
      modalBodyProps,
      body,
      props
    } = this.state;
    return (
      modal &&
      _(
        Modal,
        {
          keyboard: true,
          backdrop: footer !== false ? "static" : true,
          toggle: this.toggle,
          isOpen: modal,
          className: "modal-" + size + " primary",
          style,
          ...props
        },
        _(
          ModalHeader,
          {
            toggle: this.toggle,
            className: "bg-" + color
          },
          title
        ),
        _(ModalBody, modalBodyProps, body),

        // !footer && _(ModalFooter, null,
        // _(Button, {
        // className:'btn-form',
        // color: 'teal',
        // onClick: this.toggle
        // },"KAYDET"),
        // ' ',
        // _(Button, {
        // className:'btn-form',
        // color: "light",
        // style:{border: ".5px solid #e6e6e6"},
        // onClick: this.toggle
        // }, "VAZGEÇ")
        // ),

        footer && _(React.Fragment, null, footer)
      )
    );
  }
}
/**
 * @description this component used to login after session is timeout
 */
class XLoginDialog extends React.Component {
  constructor(props) {
    super(props);
    this.state = { modal: false, msg: false };
    /**
	 * @description Used to open Modal we made it GLOBAL
	 * @example iwb.showLoginDialog()
	 */
    this.open = () => this.setState({ modal: true });
    iwb.showLoginDialog = this.open;
    /**
	 * Used To Login User
	 */
    this.login = () => {
      var self = this;
      var passWord = document.getElementById("id-password").value;
      if (!passWord) {
        self.setState({ msg: "Önce Şifre Giriniz" });
        return false;
      }
      iwb.request({
        url:
          "ajaxAuthenticateUser?userRoleId=" +
          _scd.userRoleId +
          "&locale=" +
          _scd.locale +
          (_scd.projectId ? "&projectId=" + _scd.projectId : ""),
        params: {
          customizationId: _scd.customizationId,
          userName: _scd.userName,
          passWord: passWord,
          locale: _scd.locale
        },
        callback: response => {
          if (response.success) {
            if (!response.waitFor) {
              if (response.session) {
                _scd = response.session;
              }
              self.setState({ modal: false, msg: false });
            } else {
              self.setState({ msg: "TODO! " + response.waitFor });
            }
            return false;
          } else {
            self.setState({ msg: response.errorMsg });
          }
        },
        errorCallback: j => {
          this.setState({ msg: "Olmadi" });
        }
      });
    };
  }

  render() {
    return _(
      Modal,
      {
        centered: true,
        keyboard: false,
        backdrop: "static",
        toggle: this.toggle,
        isOpen: this.state.modal,
        className: "modal-sm primary"
      },
      _(
        ModalBody,
        null,
        _("h1", null, "Login"),
        _(
          "p",
          {
            className: "text-muted",
            style: { color: this.state.msg ? "red !important" : "" }
          },
          this.state.msg || "Session Timeout"
        ),
        _(
          InputGroup,
          { className: "mb-3" },
          _(
            "div",
            { className: "input-group-prepend" },
            _(
              "span",
              { className: "input-group-text" },
              _("i", { className: "icon-user" })
            )
          ),
          _(Input, {
            type: "text",
            readOnly: true,
            value: _scd.userName,
            placeholder: "Username"
          })
        ),
        _(
          InputGroup,
          { className: "mb-4" },
          _(
            "div",
            { className: "input-group-prepend" },
            _(
              "span",
              { className: "input-group-text" },
              _("i", { className: "icon-lock" })
            )
          ),
          _(Input, {
            type: "password",
            id: "id-password",
            placeholder: "Password"
          })
        )
      ),
      _(
        ModalFooter,
        null,
        _(Button, { color: "primary", onClick: this.login }, "Login"),
        " ",
        _(
          Button,
          {
            color: "secondary",
            onClick: () => (document.location = "login.htm?.r=" + Math.random())
          },
          "Exit"
        )
      )
    );
  }
}
/**
 * @description used to open dropDown make edit and delete from main and detail
 *              grid when the Grid is not in edit mode
 * @param {
 *            Object } props - gets data of right click and crud
 * @param {
 *            Array } props.menuButtons - return array of Objects conf { text,
 *            handler, cls }
 * @param {
 *            Boolean } props.crudFlags.edit -ACL Edit Option
 * @param {
 *            Boolean } props.crudFlags.insert -ACL insert Option
 * @param {
 *            Boolean } props.crudFlags.remove -ACL Delete Option
 * @param {
 *            Array } props.rowData - data of the clicked Row
 */
class XGridRowAction extends React.PureComponent {
  constructor(props) {
    super(props);
    if (iwb.debug) console.log("XGridRowAction", props);
    this.state = { isOpen: false };
    this.toggle = (event) => {
      event.preventDefault();
      event.stopPropagation() 
      this.setState({ isOpen: !this.state.isOpen });
    }
  }
  render() {
    const {
      state: { isOpen },
      props: {
        rowData,
        parentCt,
        menuButtons,
        onEditClick,
        onDeleteClick,
        crudFlags: { edit, remove },
      },
      toggle
    } = this;
    return _(
      Dropdown,
      { isOpen, toggle, className:this.props.className },
      _(DropdownToggle, {
        tag: "i",
        className: "icon-options-vertical column-action"
      }),
      isOpen &&
        _(
          DropdownMenu,
          { className: isOpen ? "show" : "", style:{fontSize:'small'}},
          edit &&
            _(
              DropdownItem,
              {
                key: "123",
                onClick: event => {
                  onEditClick({ event, rowData, openEditable: true });
                }
              },
              _("span", { className: "mr-2 icon-pencil"}),
              getLocMsg('edit')
            ),
          remove &&
            _(
              DropdownItem,
              {
                key: "1223",
                onClick: event => {
                  onDeleteClick({ event, rowData });
                }
              },
              _("span", {
                className: "mr-2 icon-minus text-danger"
              }),
              getLocMsg('delete')
            ),
          menuButtons &&
          menuButtons.map(({
            text = 'ButtonTextWillBeHere',
            handler = (event, rowData, parentCt) => {
              console.group();
              console.warn('No Render Method! event, rowData, parentCt ');
              console.table([{ 'event':event, 'rowData':rowData, 'parentCt':parentCt }])
            },
            cls = ''
          }) => {
            cls = cls.split('|');
            return _(
              DropdownItem, {
                key: text,
                onClick: event => handler.call(this.state, event, rowData, parentCt),
                className: cls[1]
              },
              _("span", { className: 'mr-2 ' + cls[0] }),
              text
            );
          })
        )
    );
  }
}
XGridRowAction.propTypes = {
  rowData:PropTypes.object,
  parentCt: PropTypes.obj,
  menuButtons:PropTypes.arrayOf(
    PropTypes.shape({
      text: PropTypes.string,
      handler: PropTypes.func,
      cls: PropTypes.string
    })
  ),
  onEditClick: PropTypes.func,
  onDeleteClick: PropTypes.func,
  crudFlags: PropTypes.shape({
    edit: PropTypes.bool,
    remove: PropTypes.bool
  }),
};
/**
 * @deprecated todo: not used yet
 */
class XGridAction extends React.PureComponent {
  constructor(props) {
    super(props);
    this.toggle = () => this.setState({ isOpen: !this.state.isOpen });
    this.state = { isOpen: false };
  }
  render() {
    const {
      state: { isOpen },
      props: { color },
      toggle
    } = this;
    return _(
      Dropdown,
      { isOpen, toggle },
      // ,_('i',{className:'icon-options-vertical column-action',
		// onClick:qqq.toggleGridAction})
      _(
        DropdownToggle,
        {
          tag: "div",
          className: "timeline-badge hover-shake " + color,
          onClick: () => alert("hehey")
        },
        _("i", { className: "icon-grid", style: { fontSize: 17 } })
      ),
      // {tag:'i',className: "icon-grid", color||'danger'}
      isOpen &&
        _(
          DropdownMenu,
          { className: isOpen ? "show" : "" },
          // ,_('div',{style:{padding: "7px 13px",background: "gray", color:
			// "darkorange", fontWeight: "500", fontSize:" 16px"}},'İşlemler')
          _(
            DropdownItem,
            { ur: "123", onClick: false },
            _("i", {
              className: "icon-plus",
              style: {
                marginRight: 5,
                marginLeft: -2,
                fontSize: 12,
                color: "#777"
              }
            }),
            "NEW RECORD"
          ),
          _("hr"),
          _(
            DropdownItem,
            { ur: "1223", onClick: false },
            _("i", {
              className: "icon-equalizer",
              style: {
                marginRight: 5,
                marginLeft: -2,
                fontSize: 12,
                color: "#777"
              }
            }),
            "REPORTS/BI"
          )
          // ,_(DropdownItem,{ur:'1223',onClick:false},_('i',{className:'icon-drop',style:{marginRight:5,
			// marginLeft:-2, fontSize:12,color:'#777'}}),'Diğer İşlemler')
        )
    );
  }
}
/**
 * @description it renders detail grid there is no search form
 * @param {Object}
 *            props - Input of the Grid Component
 * @param {Array}
 *            props.columns[] - Column conf List {name title width sort}
 * @param {Object}
 *            props.crudFlags - Grid Component {edit insert remove} options Used
 *            to render CRUD buttons and routes
 * @param {Number}
 *            props.crudFormId - crudFormId is used to make route to the form
 * @param {Number}
 *            props.crudTableId - crudTableId is id of sql table
 * @param {Number}
 *            props.defaultHeight -
 * @deprecated defaultHeight is a height of the Grid
 * @param {Number}
 *            props.defaultWidth -
 * @deprecated defaultWidth is width of the Grid
 * @param {Boolean}
 *            props.detailFlag - Am I detail grid?
 * @param {Boolean}
 *            props.editable - Used to Open Grid in EditingState
 *            Mode############
 * @param {Number}
 *            props.gridId - Id of the Detail grid
 * @param {String}
 *            props.keyField - Used to spesify primety Key name of the Grid
 * @param {String}
 *            props.name - Rendered name of the Grid Component
 * @param {Function}
 *            props.openTab - Used to open Form in new tab
 * @param {Function}
 *            props.pageSize - [0] by default
 * @param {Number}
 *            props.queryId - Query id of the grid
 * @param {Symbol}
 *            props._disableIntegratedGrouping - ['null'] Disable Grouping
 * @param {Symbol}
 *            props._disableIntegratedSorting - ['null'] Disable sorting
 * @param {Symbol}
 *            props._disableSearchPanel - ['null'] Disable search panel
 * @param {Symbol}
 *            props.multiselect - ['null'] Enambe multiselect option
 * @param {Symbol}
 *            props.showDetail - ['null'] detail grid used in nested detail grid
 */
class XGrid extends GridCommon {
  constructor(props) {
    super(props);
    if (iwb.debug) console.log("XGrid", props);
    var columns = [];
    var columnExtensions = [];
    const canIOpenActions =
      (props.crudFlags && (props.crudFlags.edit || props.crudFlags.remove)) ||
      props.menuButtons;
    if (canIOpenActions) {
      columns.push({
        name: "_qw_",
        title: ".",
        getCellValue: rowData => {
          var { onEditClick, onDeleteClick } = this;
          return _(XGridRowAction, {
            ...{ rowData },
            ...{ onEditClick, onDeleteClick },
            ...{ crudFlags: props.crudFlags },
            ...{ menuButtons: props.menuButtons },
            ...{ parentCt: this}
          });
        }
      });
      columnExtensions.push({
        columnName: "_qw_",
        width: 50,
        align: "right",
        sortingEnabled: false
      });
    }

    var colTemp = props.columns;
    colTemp &&
      colTemp.map(colLocal => {
        var title;
        switch (colLocal.name) {
          case "pkpkpk_faf":
            title = _("i", { className: "icon-paper-clip" });
            break;
          case "pkpkpk_ms":
            title = _("i", { className: "icon-envelope" });
            break;
          case "pkpkpk_cf":
            title = _("i", { className: "icon-bubble" });
            break;
          case "pkpkpk_apf":
            title = _("i", { className: "icon-picture" });
            break;
          case "pkpkpk_vcsf":
            title = _("i", { className: "icon-social-github" });
            break;
        }
        columns.push({
          name: colLocal.name,
          title: title || colLocal.title,
          getCellValue: colLocal.formatter || undefined
        });
        columnExtensions.push({
          width: +colLocal.width,
          columnName: colLocal.name,
          align: colLocal.align || "left",
          sortingEnabled: !!colLocal.sort
        });
      });

    this.state = {
      columns,
      sorting: [],
      totalCount: 0,
      currentPage: 0,
      loading: false,
      columnExtensions,
      gridActionOpen: false,
      rows: props.rows || [],
      order: columns.map(({ name }) => name),
      pageSize: props.pageSize || iwb.detailPageSize,
      columnWidths: columnExtensions.map(({ columnName, width }) => {
        return { columnName, width };
      }),
      pageSizes:
        props.pageSize > 1
          ? [parseInt(props.pageSize / 2), props.pageSize, 3 * props.pageSize]
          : [5, 10, 25, 100]
    };
    /**
	 * @overloading
	 * @description used to make request and fill the grid
	 * @param {boolean}
	 *            force - to fill with up to date data
	 */
    this.loadData = force => {
      if (this.props.rows) return;
      const queryString = this.queryString();
      if (!force && queryString === this.lastQuery) {
        return;
      }
      this.setState({ loading: true });
      iwb.request({
        url: queryString,
        self: this,
        params:
          this.props.searchForm &&
          iwb.getFormValues(document.getElementById(this.props.searchForm.id)),
        successCallback: (result, cfg) => {
          cfg.self.setState({
            rows: result.data,
            totalCount: result.total_count,
            loading: false
          });
        },
        errorCallback: (error, cfg) => {
          cfg.self.setState({
            rows: [],
            totalCount: 0,
            loading: false
          });
        }
      });
      this.lastQuery = queryString;
    };
  }
  componentDidMount() {
    if (!this.dontRefresh) this.loadData();
    this.dontRefresh = false;
  }
  componentDidUpdate() {
    this.loadData();
    this.dontRefresh = false;
  }
  componentWillUnmount() {
    iwb.grids[this.props.id] = Object.assign({}, this.state);
  }
  render() {
    const {
      state: {
        rows,
        order,
        columns,
        sorting,
        pageSize,
        pageSizes,
        totalCount,
        currentPage,
        columnWidths,
        columnExtensions
      },
      props: {
        keyField,
        showDetail,
        multiselect,
        _disableSearchPanel,
        _disableIntegratedSorting,
        _disableIntegratedGrouping
      },
      // methods
      rowComponent,
      onOrderChange,
      onSortingChange,
      onPageSizeChange,
      onCurrentPageChange,
      onColumnWidthsChange
    } = this;

    if (!rows || !rows.length) return null;
    return _(
      _dxgrb.Grid,
      { rows, columns, getRowId: row => row[keyField] },
      /** sorting */
      !_disableIntegratedSorting &&
        _(
          _dxrg.SortingState,
          !pageSize ? null : { sorting, onSortingChange, columnExtensions }
        ),
      /** state multiselect */
      multiselect && _(_dxrg.SelectionState, null),
      /** state search */
      !pageSize && _(_dxrg.SearchState, null),
      /** Client filtering */
      !_disableSearchPanel &&
        !pageSize &&
        rows.length > 1 &&
        _(_dxrg.IntegratedFiltering, null),
      /** state grouping */
      !_disableIntegratedGrouping &&
        !pageSize &&
        rows.length > 1 &&
        _(_dxrg.GroupingState, null),
      /** Enable UI grouping */

      !_disableIntegratedGrouping &&
        !pageSize &&
        rows.length > 1 &&
        _(_dxrg.IntegratedGrouping, null),
      /** state sorting */
      !_disableIntegratedSorting &&
        !pageSize &&
        rows.length > 1 &&
        _(_dxrg.IntegratedSorting, null),
      /** state detail grid */
      showDetail && _(_dxrg.RowDetailState, null),
      /** state paging */

      rows.length > iwb.detailPageSize || pageSize > 1
        ? _(
            _dxrg.PagingState,
            pageSize > 1
              ? { currentPage, pageSize, onCurrentPageChange, onPageSizeChange }
              : {}
          )
        : null,
      /** UI paging */

      pageSize > 1 &&
        rows.length > 1 &&
        totalCount > iwb.detailPageSize &&
        _(_dxrg.CustomPaging, { totalCount }),
      /** multiselect */

      multiselect && _(_dxrg.IntegratedSelection, null),
      /** Enable Drag and Drop */
      _(_dxgrb.DragDropProvider, null),
      /** UI table */
      _(_dxgrb.Table, { columnExtensions, rowComponent }),
      /** UI multiselect */
      multiselect && _(_dxgrb.TableSelection, { showSelectAll: multiselect }),
      /** UI ordering of the table */
      _(_dxgrb.TableColumnReordering, { order, onOrderChange }),
      /** UI column table resizer */
      _(_dxgrb.TableColumnResizing, { columnWidths, onColumnWidthsChange }),
      _(_dxgrb.TableHeaderRow, { showSortingControls: true }),
      /** UI detail Grid */
      showDetail
        ? _(_dxgrb.TableRowDetail, { contentComponent: showDetail })
        : null,
      /** Paging panel */
      rows.length > iwb.detailPageSize &&
        _(_dxgrb.PagingPanel, { pageSizes: pageSizes || iwb.detailPageSize }),
      /** UI row Grouping */
      !_disableIntegratedGrouping &&
        !pageSize &&
        rows.length > 1 &&
        _(_dxgrb.TableGroupRow, null),
      !_disableIntegratedGrouping ||
        !_disableIntegratedSorting ||
        !_disableSearchPanel ||
        (!pageSize && rows.length > 1 && _(_dxgrb.Toolbar, null)),
      !_disableSearchPanel &&
        !pageSize &&
        rows.length > 1 &&
        _(_dxgrb.SearchPanel, {
          messages: { searchPlaceholder: "Hızlı Arama..." },
          changeSearchValue: ax => {
            if (iwb.debug) console.log("onValueChange", ax);
          }
        }), // TODO
      !_disableIntegratedGrouping &&
        !pageSize &&
        rows.length > 1 &&
        _(_dxgrb.GroupingPanel, { showSortingControls: true })
    );
  }
}
/**
 * @description A functional component to glue button inside grid with its props
 * @param {Object}
 *            props - { id, onExecute }
 * @param {Number}
 *            props.id - index of the ComponentProps array
 * @param {Function}
 *            props.onExecute - a callback function to be executed when button
 *            is clicked
 */
class Command extends React.PureComponent {
  render() {
    var { id, onExecute } = this.props;
    var ComponentProps = iwb.commandComponentProps[id];
    return ComponentProps
      ? _(
          "button",
          {
            className: "btn btn-link",
            style: { padding: "11px" },
            onClick: onExecute,
            title: ComponentProps.hint
          },
          _(
            "span",
            { className: ComponentProps.color || "undefined" },
            ComponentProps.icon
              ? _("i", {
                  className: "oi oi-" + ComponentProps.icon,
                  style: { marginRight: ComponentProps.text ? 5 : 0 }
                })
              : null,
            ComponentProps.text
          )
        )
      : null;
  }
}
/**
 * @description can be used to overload grid functionality component for making
 *              GRIDROW Edit + Multiselect
 */
class SelectableStubCell extends React.PureComponent {
  render() {
    return _(
      Plugin,
      null,
      _(
        Template,
        {
          name: "tableCell",
          predicate: ({ tableRow, tableColumn }) => {
            if (
              tableRow.key !== "heading" &&
              tableColumn.key === "select" &&
              tableRow.type === "edit"
            ) {
              return true;
            }
          }
        },
        params =>
          _(TemplateConnector, null, ({ selection }, { toggleSelection }) => {
            return _(_dxgrb.TableSelection.Cell, {
              row: params.tableRow.row,
              selected: selection.indexOf(params.tableRow.rowId) !== -1,
              onToggle: () =>
                toggleSelection({ rowIds: [params.tableRow.rowId] })
            });
          })
      )
    );
  }
}
/**
 * @description used for sf grid in popup Modal
 * @param {Object}
 *            props - Input of the Grid Component
 * @param {Function}
 *            props.callback - used to send back selected data
 * @param {Array}
 *            props.columns[] - Column conf List {name title width sort}
 * @param {Boolean}
 *            props.crudFlags.edit - Grid Component {edit} options Used to
 *            render CRUD buttons and routes
 * @param {Number}
 *            props.defaultHeight -
 * @deprecated defaultHeight is a height of the Grid
 * @param {Number}
 *            props.defaultWidth -
 * @deprecated defaultWidth is width of the Grid
 * @param {Boolean}
 *            props.editable - Used to Open Grid in EditingState
 *            Mode############
 * @param {Number}
 *            props.gridId - Id of the grid grid
 * @param {String}
 *            props.gridReport -@deprecated usage
 * @param {String}
 *            props.keyField - Used to spesify primety Key name of the Grid
 * @param {String}
 *            props.name - Rendered name of the Grid Component
 * @param {Symbol}
 *            props.multiselect - ['null'] Enable multiselect option
 * @param {Function}
 *            props.pageSize - [10] by default
 * @param {Number}
 *            props.queryId - Query id of the grid
 * @param {Symbol}
 *            props.searchForm - Search form is generated from ServerSide and
 *            extens from XForm Component
 * @param {Object}
 *            props.selectRow - [{mode:"checkbox",clickToSelect: true}]Used to
 *            Edit and make Selectable
 * @param {Symbol}
 *            props._disableIntegratedGrouping - ['null'] Disable Grouping
 * @param {Symbol}
 *            props._disableIntegratedSorting - ['null'] Disable sorting
 * @param {Symbol}
 *            props._disableSearchPanel - ['null'] Disable search panel
 */
class XEditGridSF extends GridCommon {
  constructor(props) {
    if (iwb.debug) console.log("XEditGridSF.constructor", props);
    super(props);
    var oldGridState = iwb.grids[props.id];
    if (iwb.debug) console.log("oldGridState", oldGridState);
    if (oldGridState) {
      this.editors = {};
      this.dontRefresh = true;
      this.state = oldGridState;
      var colTemp = props.columns;
      colTemp &&
        colTemp.map(colLocal => {
          if (colLocal.editor) {
            this.editors[colLocal.name] = colLocal.editor;
          }
        });
    } else {
      var columns = [];
      this.editors = {};
      var columnExtensions = [];
      var colTemp = props.columns;
      colTemp &&
        colTemp.map(colLocal => {
          switch (colLocal.name) {
            case "pkpkpk_faf":
            case "pkpkpk_ms":
            case "pkpkpk_cf":
            case "pkpkpk_apf":
            case "pkpkpk_vcsf":
              break;
            default:
              columns.push({
                name: colLocal.name,
                title: colLocal.title,
                getCellValue: colLocal.formatter || undefined
              });
              var editor = colLocal.editor || false;
              if (editor) {
                editor.autoComplete = "off";
                if (!editor.style) editor.style = {};
                editor.style.width = "100%";
                switch (+editor._control) {
                  case 6:
                  case 8:
                  case 58:
                  case 7:
                  case 15:
                  case 59:
                  case 9:
                  case 10: // combos
                    break;
                  default:
                    editor.style.textAlign = colLocal.align || "left";
                }
                this.editors[colLocal.name] = editor;
              }
              columnExtensions.push({
                width: +colLocal.width,
                editingEnabled: !!editor,
                columnName: colLocal.name,
                align: colLocal.align || "left",
                sortingEnabled: !!colLocal.sort
              });
          }
        });
      this.state = {
        columns,
        rows: [],
        pkInsert: 0,
        sorting: [],
        selection: [],
        addedRows: [],
        totalCount: 0,
        deletedRows: [],
        rowChanges: {},
        loading: false,
        currentPage: 0,
        columnExtensions,
        deletingRows: [],
        editingRowIds: [],
        order: columns.map(({ name }) => name),
        pageSize: props.pageSize || iwb.detailPageSize,
        viewMode: !props.editable && (props.viewMode || true),
        columnWidths: columnExtensions.map(({ columnName, width }) => {
          return { columnName, width };
        }),
        pageSizes:
          props.pageSize > 1
            ? [parseInt(props.pageSize / 2), props.pageSize, 3 * props.pageSize]
            : [5, 10, 25, 100]
      };
    }
    /**
	 * used to get values of the grid
	 */
    this.getValues = () => {
      let {
        rows,
        addedRows,
        deletedRows,
        editingRowIds,
        selection
      } = this.state;
      let addedRowsTemp = addedRows;
      rows = rows.slice();
      selection.forEach(rowId => {
        if (rowId > 0) {
          const index = rows.findIndex(
            row => row[this.props.keyField] === rowId
          );
          if (index > -1) {
            addedRowsTemp = [{ ...rows[index] }];
          }
        }
      });
      var searchFormData =
        this.props.searchForm &&
        iwb.getFormValues(document.getElementById("s-" + this.props.id));
      // xsample_id to sample_id converter could be written as helper function
      searchFormData &&
        Object.keys(searchFormData).forEach((key, index) => {
          if (key.charAt(0) === "x") {
            searchFormData[key.slice(1)] = searchFormData[key];
            delete searchFormData[key];
          }
        });
      return {
        searchFormData,
        inserted: addedRowsTemp,
        deleted: deletedRows,
        _state: this.state,
        _this: this,
      };
    };
    if (props.parentCt && props.parentCt.egrids)
      props.parentCt.egrids[props.gridId] = this;
    if (this.props.searchForm) {
      this.searchForm = _(
        Nav,
        { style: {} },
        _("div", { className: "hr-text" }, _("h6", null, "Arama Kriterleri")),
        _(
          "div",
          { style: { zoom: ".9"}, className:"searchFormFields"  },
          _(this.props.searchForm, { parentCt: this }),
          _(
            "div",
            { className: "form-group", style: { paddingTop: 10 } },
            _(
              Button,
              {
                color: "danger",
                style: { width: "100%", borderRadius: 2 },
                onClick: () => this.loadData(true)
              },
              "ARA"
            )
          )
        )
      );
    }
    /**
	 * @param {Boolean}
	 *            force
	 */
    this.loadData = force => {
      const queryString = this.props._url;
      const t_props = this.props;
      this.setState({ loading: true });
      iwb.request({
        url: queryString,
        self: this,
        params:
          this.props.searchForm &&
          iwb.getFormValues(document.getElementById("s-" + this.props.id)),
        successCallback: (result, cfg) => {
          var state = {
            loading: false,
            rows: result.data,
            totalCount: result.total_count
          };
          state.editingRowIds = state.rows.map(row => row[t_props.keyField]);
          cfg.self.setState(state);
        },
        errorCallback: (error, cfg) => {
          cfg.self.setState({
            rows: [],
            totalCount: 0,
            loading: false
          });
        }
      });
      this.lastQuery = queryString;
    };
    this.EditCell = xprops => {
      var editor = this.editors[xprops.column.name];
      if (!editor) return _(_dxgrb.TableEditRow.Cell, xprops);

      editor = Object.assign({}, editor);
      if (!xprops.row._new) xprops.row._new = {}; // Object.assign({},xprops.row);
      if (!xprops.row._new.hasOwnProperty(xprops.column.name))
        xprops.row._new[xprops.column.name] = xprops.row[xprops.column.name];
      var keyFieldValue = (xprops.row._new && xprops.row._new[this.props.keyField])?xprops.row._new[this.props.keyField]:xprops.row[this.props.keyField]; 
      delete editor.defaultValue;
      switch (1 * editor._control) {
        case 3:
        case 4: // number
          editor.value = xprops.value || 0; // xprops.row._new[xprops.column.name];
          editor.onValueChange = ({ value }) => {
            xprops.row._new[xprops.column.name] = value;
            xprops.onValueChange(value);
            this.props.onValueChange && this.props.onValueChange({inthis:this,keyFieldValue:keyFieldValue, inputName:xprops.column.name,inputValue:value })
          };
          break;
        case 6:
        case 8:
        case 58:
        case 7:
        case 15:
        case 59:
        case 9:
        case 10: // combos
          editor.value = xprops.row._new[xprops.column.name] || 0 || ""; // TODO.
																			// ilk
																			// edit
																			// ettigini
																			// aliyor
          editor.onChange = ({ id }) => {
            xprops.row._new[xprops.column.name] = id;
            xprops.onValueChange(id);
            this.props.onValueChange && this.props.onValueChange({
            	inthis:this,
            	keyFieldValue,
            	inputName:xprops.column.name,
            	inputValue:id
            })
          };
          break;
        case 5:// checkbox
          editor.checked = +xprops.row._new[xprops.column.name];
          editor.onChange = ({ target: { checked } }) => {
            xprops.row._new[xprops.column.name] = checked;
            xprops.onValueChange(checked);
            this.props.onValueChange && this.props.onValueChange({
              inthis:this,
              keyFieldValue,
              inputName:xprops.column.name,
              inputValue:checked
            })
          };
        break;
        default:
          editor.value = xprops.value || ""; // xprops.row._new[xprops.column.name];
          editor.onChange = ({ target: { value } }) => {
            xprops.row._new[xprops.column.name] = value;
            xprops.onValueChange(value);
            this.props.onValueChange && this.props.onValueChange({
            	inthis:this,
            	keyFieldValue,
            	inputName:xprops.column.name,
            	inputValue:value
            })
          };
          break;
      }
      var cmp = Input;
      if (editor.$) {
        cmp = editor.$;
        delete editor.$;
      }
      return _(
        "td",
        { style: { verticalAlign: "middle", padding: 1 } },
        _(cmp, editor)
      );
    };
  }
  componentDidMount() {
    if (!this.dontRefresh) this.loadData();
  }
  componentDidUpdate() {
    if (this.props.editable && this.props.viewMode != this.state.viewMode) {
      this.setState({ viewMode: this.props.viewMode });
    }
  }
  componentWillUnmount() {
    iwb.grids[this.props.id] = Object.assign({}, this.state);
  }
  render() {
    if (iwb.debug) console.log("XEditGrid:render");
    const {
      state: {
        rows,
        order,
        loading,
        sorting,
        columns,
        viewMode,
        pageSize,
        addedRows,
        selection,
        pageSizes,
        rowChanges,
        totalCount,
        currentPage,
        columnWidths,
        editingRowIds,
        columnExtensions
      },
      props: {
        keyField,
        selectRow,
        crudFlags,
        multiselect,
        _importClicked,
        _disableSearchPanel,
        _disableIntegratedSorting,
        _disableIntegratedGrouping
      },
      // methods
      onOrderChange,
      onCommitChanges,
      onPageSizeChange,
      onAddedRowsChange,
      onSelectionChange,
      onRowChangesChange,
      onCurrentPageChange,
      onColumnWidthsChange,
      onEditingRowIdsChange
    } = this;
    var g = _(
      _dxgrb.Grid,
      {
        rows,
        columns,
        getRowId: row => row[keyField]
      },
      !_disableIntegratedSorting ? _(_dxrg.SortingState, null) : null,

      multiselect &&
        _(_dxrg.SelectionState, {
          selection,
          onSelectionChange
        }),
      _(_dxrg.SearchState, null),
      /** Client filtering //was used for panel search(@dependency) */
      !_disableSearchPanel && _(_dxrg.IntegratedFiltering, null),
      /** state grouping */
      !_disableIntegratedGrouping && _(_dxrg.GroupingState, null),
      /** Enable UI grouping */

      !_disableIntegratedGrouping && _(_dxrg.IntegratedGrouping, null),
      /** state sorting */
      !_disableIntegratedSorting && _(_dxrg.IntegratedSorting, null),
      /** state paging */

      rows.length > iwb.detailPageSize &&
        _(
          _dxrg.PagingState,
          pageSize > 1
            ? { pageSize, currentPage, onCurrentPageChange, onPageSizeChange }
            : {}
        ),

      multiselect && _(_dxrg.IntegratedSelection, null),
      !viewMode &&
        _(_dxrg.EditingState, {
          addedRows,
          rowChanges,
          editingRowIds,
          onCommitChanges,
          columnExtensions,
          onAddedRowsChange,
          onRowChangesChange,
          onEditingRowIdsChange
        }),

      _(_dxgrb.DragDropProvider, null),
      _(_dxgrb.Table, { columnExtensions }),
      multiselect &&
        _(_dxgrb.TableSelection, {
          showSelectAll: true
        }),
      /** UI ordering of the table */
      _(_dxgrb.TableColumnReordering, { order, onOrderChange }),
      /** UI tablle resizing */
      _(_dxgrb.TableColumnResizing, { columnWidths, onColumnWidthsChange }),
      _(_dxgrb.TableHeaderRow, {
        showSortingControls: true
      }),
      selectRow.mode === "checkbox" && _(SelectableStubCell, null), // select
																	// box

      !viewMode &&
        _(_dxgrb.TableEditRow, {
          cellComponent: this.EditCell
        }),

      !multiselect &&
        !viewMode &&
        _(_dxgrb.TableEditColumn, {
          showAddCommand: crudFlags && crudFlags.insert,
          showEditCommand: crudFlags && crudFlags.edit,
          commandComponent: Command,
          showDeleteCommand: crudFlags && crudFlags.remove
        }),

      rows.length > iwb.detailPageSize
        ? _(_dxgrb.PagingPanel, { pageSizes: pageSizes || iwb.detailPageSize })
        : null,
      !_disableIntegratedGrouping ? _(_dxgrb.TableGroupRow, null) : null,
      !_disableIntegratedGrouping ||
      !_disableIntegratedSorting ||
      !_disableSearchPanel
        ? _(_dxgrb.Toolbar, null)
        : null,
      !_disableSearchPanel
        ? _(_dxgrb.SearchPanel, {
            messages: { searchPlaceholder: "Hızlı Arama..." }
          })
        : null,
      !_disableIntegratedGrouping
        ? _(_dxgrb.GroupingPanel, { showSortingControls: true })
        : null
    );

    var footer = _(
      ModalFooter,
      null,
      _(
        Button,
        {
          className: "btn-form",
          color: "teal",
          onClick: () => {
            this.onCommitChanges(this.state);
            if (this.props.callback(this.getValues()) === true)
              iwb.closeModal();
          }
        },
        "Save"
      ),
      " ",
      _(
        Button,
        {
          className: "btn-form",
          color: "light",
          style: { border: ".5px solid #e6e6e6" },
          onClick: iwb.closeModal
        },
        "Cancel"
      )
    );

    return _("div", { className: "tab-grid mb-4" }, [
      !!this.searchForm &&
        _(
          "nav",
          { id: "sf-" + this.props.id, key: "sf-" + this.props.id },
          this.searchForm
        ),
      _("main", { className: "inbox", key: "inbox" }, g, footer)
    ]);
  }
}
/**
 * @description {name, children, predicate, position} used to extend template of
 *              the grid!
 * @param {
 *            object } param0
 * @param {
 *            string } param0.name - to find tample name
 * @param {
 *            Symbol } param0.children - React.Component
 * @param {
 *            Function } param0.predicate - is a function to deside where to
 *            render
 * @param {
 *            String } param0.position - ['before','after','',null] used to
 *            render before, after or override
 * @example overloading template example located in XEditGrid render
 */

const extendGrid = ({ name, children, predicate, position }) => {
  return _(
    Plugin,
    null,
    _(
      Template,
      {
        name,
        predicate: rest => predicate(rest)
      },
      params => {
        return _(
          React.Fragment,
          null,
          position === "before" && children,
          position && _(TemplatePlaceholder, null),
          position === "after" && children,
          position !== true && children
        );
      }
    )
  );
};
/**
 * @description {text,callback} used for making popup dialog
 * @param {object}
 *            conf.text - body of the mesasge
 * @param {object}
 *            conf.title - title of the modal
 * @param {function}
 *            conf.callback - callback function
 * @return {boolean} - retur true or false to the call back
 * @example yesNoDialog({ text:"Are you Sure!", callback:(success)=>{ logic here
 *          }});
 */
yesNoDialog = ({
  text = "Are You Sure?",
  title = "Are You Sure?",
  callback = alert('obj.callback is not a function'),
  ...confg
}) => {
  iwb.showModal({
    body: text,
    size: "sm",
    title: title,
    color: "danger",
    footer: _(
      ModalFooter,
      null,
      _(
        Button,
        {
          className: "btn-form",
          color: "teal",
          onClick: () => {
            callback(true);
            iwb.closeModal();
          }
        },
        getLocMsg('js_tamam')
      ),
      " ",
      _(
        Button,
        {
          className: "btn-form",
          color: "light",
          style: { border: ".5px solid #e6e6e6" },
          onClick: () => {
            callback(false);
            iwb.closeModal();
          }
        },
        getLocMsg('js_cancel')
      )
    ),
    ...confg
  });
};
/**
 * @description component for edit Detail Grid mostly used for form + grid mode
 */
class XEditGrid extends GridCommon {
  constructor(props) {
    if (iwb.debug) console.log("XEditGrid.constructor", props);
    super(props);
    // state
    var oldGridState = iwb.grids[props.id];
    if (iwb.debug) console.log("oldGridState", oldGridState);
    if (oldGridState) {
      this.dontRefresh = true;
      this.state = oldGridState;
      var c = props.columns;
      this.editors = {};
      colTemp &&
        colTemp.map(colLocal => {
          if (colLocal.editor) {
            this.editors[colLocal.name] = editor;
          }
        });
    } else {
      var columns = [],
        columnExtensions = [];
      var colTemp = props.columns;
      this.editors = {};
      colTemp &&
        colTemp.map(colLocal => {
          switch (colLocal.name) {
            case "pkpkpk_faf":
            case "pkpkpk_ms":
            case "pkpkpk_cf":
            case "pkpkpk_apf":
            case "pkpkpk_vcsf":
              break;
            default:
              columns.push({
                name: colLocal.name,
                title: colLocal.title,
                getCellValue: colLocal.formatter || undefined
              });
              var editor = colLocal.editor || false;
              if (editor) {
                editor.autoComplete = "off";
                if (!editor.style) editor.style = {};
                editor.style.width = "100%";
                editor.style.position = "inherit";
                switch (+editor._control) {
                  case 6:
                  case 8:
                  case 58:
                  case 7:
                  case 15:
                  case 59:
                  case 9:
                  case 10: // combos
                    break;
                  default:
                    editor.style.textAlign = colLocal.align || "left";
                }
                this.editors[colLocal.name] = editor;
              }
              columnExtensions.push({
                width: +colLocal.width,
                editingEnabled: !!editor,
                columnName: colLocal.name,
                align: colLocal.align || "left",
                sortingEnabled: !!colLocal.sort
              });
          }
        });

      this.state = {
        columns,
        rows: [],
        pkInsert: 0,
        sorting: [],
        totalCount: 0,
        addedRows: [],
        rowChanges: {},
        currentPage: 0,
        deletedRows: [],
        loading: false,
        columnExtensions,
        deletingRows: [],
        editingRowIds: [],
        order: columns.map(({ name }) => name),
        pageSize: props.pageSize || iwb.detailPageSize,
        viewMode: !props.editable && (props.viewMode || true),
        columnWidths: columnExtensions.map(({ columnName, width }) => {
          return { columnName, width };
        }),
        pageSizes:
          props.pageSize > 1
            ? [parseInt(props.pageSize / 2), props.pageSize, 3 * props.pageSize]
            : [5, 10, 25, 100]
      };
    }
    // methods
    /**
	 * used to get values of the grid
	 */

    this.getValues = () => {
      let { rows, addedRows, deletedRows, editingRowIds } = this.state;
      rows = rows.slice();
      var changedRows = [];
      editingRowIds.forEach(rowId => {
        if (rowId > 0) {
          const index = rows.findIndex(
            row => row[this.props.keyField] === rowId
          );
          if (index > -1) changedRows.push(Object.assign({}, rows[index]));
        }
      });
      return {
        inserted: addedRows,
        deleted: deletedRows,
        changed: changedRows
      };
    };
    /**
	 * bind with parent Element
	 */
    if (props.parentCt && props.parentCt.egrids)
      props.parentCt.egrids[props.gridId] = this;
    /**
	 * used to make data request to fill the frid with related data
	 * 
	 * @param {boolean}
	 *            force
	 */
    this.loadData = force => {
      const queryString = this.props._url;
      const t_props = this.props;
      this.setState({ loading: true });
      iwb.request({
        url: queryString,
        self: this,
        params:
          this.props.searchForm &&
          iwb.getFormValues(document.getElementById("s-" + this.props.id)),
        successCallback: (result, cfg) => {
          var state = {
            loading: false,
            rows: result.data,
            totalCount: result.total_count
          };
          if (t_props.multiselect) {
            state.editingRowIds = state.rows.map(row => row[t_props.keyField]);
          }
          cfg.self.setState(state);
          t_props.afterLoadData && t_props.afterLoadData(cfg.self);
        },
        errorCallback: (error, cfg) => {
          cfg.self.setState({
            rows: [],
            totalCount: 0,
            loading: false
          });
        }
      });
      this.lastQuery = queryString;
    };
    /**
	 * used for import data from the popup with ne flag
	 */
    this.BulkyImport = ({ searchFormData, inserted, deleted, _state }) => {
      const { rows, addedRows } = this.state;
      let tempRow = [];
      let max;
      // find max tab_order from grid
      if (
        (rows["0"] && rows["0"].tab_order) ||
        (addedRows["0"] && addedRows["0"].tab_order)
      ) {
        max =
          Math.max(
            ...rows.map(d => +d.tab_order),
            ...addedRows.map(d => +d.tab_order)
          ) + 10;
      } else {
        max = (rows.length + addedRows.length) * 10;
      }
      if (max === "-Infinity" || +max === 0) {
        max = 10;
      }
      // merge new imported data
      let pkInsert = this.state.pkInsert;
      inserted.forEach(data => {
        var merged = { ...searchFormData, ...data };
        merged = { ...merged, ...merged._new };
        merged.tab_order = max;
        merged.max = max;
        --pkInsert;
        merged[this.props.keyField] = pkInsert;
        tempRow.push(merged);
        max += 10;
      });
      // Adds data to the grit from the popup
      this.setState({ addedRows: [...addedRows, ...tempRow], pkInsert });
    };
    /**
	 * to get all data from grid editing + noneEdited at current time
	 */

    this.getAllData = () => {
      let tempRowData = [];
      this.state.rows.forEach(data => {
        tempRowData.push({ ...data, ...data._new });
      });
      return tempRowData;
    };
    /**
	 * used for Cell Editing
	 * 
	 * @param {Object}
	 *            xprops
	 */
    this.EditCell = xprops => {
      var editor = this.editors[xprops.column.name];
      if (
        this.props.isCellEditable &&
        this.props.isCellEditable(xprops.row, xprops.column.name) === false
      )
        return _(_dxgrb.TableEditRow.Cell, {
          ...xprops,
          ...{ editingEnabled: false }
        });
      if (!editor) return _(_dxgrb.TableEditRow.Cell, xprops);
      editor = Object.assign({}, editor);
      if (!xprops.row._new) xprops.row._new = {}; // Object.assign({},xprops.row);
      if (!xprops.row._new.hasOwnProperty(xprops.column.name))
        xprops.row._new[xprops.column.name] = xprops.row[xprops.column.name];
      
      var keyFieldValue = (xprops.row._new && xprops.row._new[this.props.keyField])?xprops.row._new[this.props.keyField]:xprops.row[this.props.keyField]; 
      
      switch (1 * editor._control) {
        case 3:
        case 4: // number
          editor.value = (xprops.row && xprops.row._new && xprops.row._new[xprops.column.name])?xprops.row._new[xprops.column.name]:xprops.value;
          editor.onValueChange = ({ value }) => {
            xprops.row._new[xprops.column.name] = value;
            xprops.onValueChange(value);
            this.props.onValueChange && this.props.onValueChange({inthis:this,keyFieldValue:keyFieldValue, inputName:xprops.column.name,inputValue:value })
          };
          break;
        case 6:
        case 8:
        case 58:
        case 7:
        case 15:
        case 59:
        case 9:
        case 10: // combos
          editor.value = xprops.row._new[xprops.column.name]; // TODO. ilk
																// edit ettigini
																// aliyor
          editor.onChange = ({ id }) => {
            xprops.row._new[xprops.column.name] = id;
            xprops.onValueChange(id);
            this.props.onValueChange && this.props.onValueChange({
            	inthis:this,
            	keyFieldValue,
            	inputName:xprops.column.name,
            	inputValue:id
            })
          };
          break;
        case 5:
          editor.checked = +xprops.row._new[xprops.column.name];
          editor.onChange = ({ target: { checked } }) => {
            xprops.row._new[xprops.column.name] = checked;
            xprops.onValueChange(checked);
            this.props.onValueChange && this.props.onValueChange({
            	inthis:this,
            	keyFieldValue,
            	inputName:xprops.column.name,
            	inputValue:checked
            })
          };
          break;
        default:
          editor.value = (xprops.row && xprops.row._new && xprops.row._new[xprops.column.name])?xprops.row._new[xprops.column.name]:xprops.value;
          editor.onChange = ({ target: { value } }) => {
            xprops.row._new[xprops.column.name] = value;
            xprops.onValueChange(value);
            this.props.onValueChange && this.props.onValueChange({
            	inthis:this,
            	keyFieldValue,
            	inputName:xprops.column.name,
            	inputValue:value
            })
          };
          break;
      }
      delete editor.defaultValue;
      delete editor.defaultChecked;
      var cmp = Input;
      if (editor.$) {
        cmp = editor.$;
        delete editor.$;
      }
      return _(
        "td",
        { style: { verticalAlign: "middle", padding: 1 } },
        _(cmp, editor)
      );
    };
  }
  componentDidMount() {
    if (!this.dontRefresh) this.loadData();
  }
  componentDidUpdate() {
    if (this.props.editable && this.props.viewMode != this.state.viewMode) {
      this.setState({ viewMode: this.props.viewMode });
    }
  }
  componentWillUnmount() {
    iwb.grids[this.props.id] = Object.assign({}, this.state);
  }
  render() {
    const {
      state: {
        rows,
        order,
        columns,
        loading,
        sorting,
        pageSize,
        viewMode,
        pageSizes,
        addedRows,
        rowChanges,
        totalCount,
        currentPage,
        columnWidths,
        editingRowIds,
        columnExtensions
      },
      props: {
        keyField,
        crudFlags,
        multiselect,
        _importClicked,
        _disableSearchPanel,
        _disableIntegratedSorting,
        _disableIntegratedGrouping
      },
      onOrderChange,
      onCommitChanges,
      onPageSizeChange,
      onAddedRowsChange,
      onRowChangesChange,
      onCurrentPageChange,
      onColumnWidthsChange,
      onEditingRowIdsChange
    } = this;
    return _(
      _dxgrb.Grid,
      {
        rows,
        columns,
        getRowId: row => row[keyField]
      },
      !_disableIntegratedSorting ? _(_dxrg.SortingState, null) : null,
      multiselect && _(_dxrg.SelectionState, null),
      _(_dxrg.SearchState, null),
      !_disableSearchPanel ? _(_dxrg.IntegratedFiltering, null) : null, // was
																		// used
																		// for
																		// panel
																		// search(@dependency)
      !_disableIntegratedGrouping ? _(_dxrg.GroupingState, null) : null,
      !_disableIntegratedGrouping ? _(_dxrg.IntegratedGrouping, null) : null,
      !_disableIntegratedSorting ? _(_dxrg.IntegratedSorting, null) : null,
      rows.length > iwb.detailPageSize
        ? _(
            _dxrg.PagingState,
            pageSize > 1
              ? {
                  pageSize,
                  currentPage,
                  onPageSizeChange,
                  onCurrentPageChange
                }
              : {}
          )
        : null,
      multiselect && _(_dxrg.IntegratedSelection, null),
      !viewMode &&
        _(_dxrg.EditingState, {
          addedRows,
          rowChanges,
          editingRowIds,
          onCommitChanges,
          columnExtensions,
          onAddedRowsChange,
          onRowChangesChange,
          onEditingRowIdsChange
        }),
      _(_dxgrb.DragDropProvider, null),
      _(_dxgrb.Table, { columnExtensions }),
      multiselect &&
        _(_dxgrb.TableSelection, {
          showSelectAll: true
        }),
      /** UI ordering of the table */
      _(_dxgrb.TableColumnReordering, { order, onOrderChange }),
      /** UI tablle resizing */
      _(_dxgrb.TableColumnResizing, { columnWidths, onColumnWidthsChange }),
      _(_dxgrb.TableHeaderRow, {
        showSortingControls: !_disableIntegratedSorting
      }),
      !viewMode &&
        _(_dxgrb.TableEditRow, {
          cellComponent: this.EditCell
        }),
      !multiselect &&
        !viewMode &&
        _(_dxgrb.TableEditColumn, {
          showAddCommand: crudFlags && crudFlags.insert,
          showEditCommand: crudFlags && crudFlags.edit,
          showDeleteCommand: crudFlags && crudFlags.remove,
          commandComponent: Command
        }),

      _importClicked &&
        _(
          extendGrid,
          {
            name: "tableCell",
            predicate: rest => {
              if (
                rest.tableRow.key === "heading" &&
                rest.tableColumn.key === "editCommand" &&
                rest.tableRow.type === "heading"
              ) {
                return true;
              }
            }
          },
          _(TemplateConnector, {}, (getters, actions) => {
            return _(
              _dxgrb.TableEditColumn.HeaderCell,
              {},
              crudFlags &&
                crudFlags.insert &&
                _(Command, {
                  id: "add",
                  onExecute: () => actions.addRow()
                }),
              _importClicked &&
                _(Command, {
                  id: "import",
                  onExecute: () => _importClicked()
                })
            );
          })
        ),

      rows.length > iwb.detailPageSize
        ? _(_dxgrb.PagingPanel, { pageSizes: pageSizes || iwb.detailPageSize })
        : null,
      !_disableIntegratedGrouping ? _(_dxgrb.TableGroupRow, null) : null,
      !_disableIntegratedGrouping ||
      !_disableIntegratedSorting ||
      !_disableSearchPanel
        ? _(_dxgrb.Toolbar, null)
        : null,
      !_disableSearchPanel
        ? _(_dxgrb.SearchPanel, {
            messages: { searchPlaceholder: "Hızlı Arama..." }
          })
        : null,
      !_disableIntegratedGrouping
        ? _(_dxgrb.GroupingPanel, { showSortingControls: true })
        : null
    );
  }
}
/**
 * @description used for rendering master grid with search form in it
 * @param {Object}
 *            props - props of the grid
 * @param {Array}
 *            props.columns - props of the grid
 * @param {string}
 *            props.columns[].title - Ui title of the grid
 * @param {string}
 *            props.columns[].name - column name of the sql tale
 * @param {Boolean}
 *            props.columns[].sort - is it sortable column?
 * @param {Number}
 *            props.columns[].width - width of the column
 * @param {Function}
 *            props.columns[].formatter - a function to make own UI from the
 *            backend params (row,cell)
 * @param {Object}
 *            props.crudFlags - An object to make UI ACL {insert: true, edit:
 *            true, remove: true}
 * @param {Number}
 *            props.crudFormId - An Id of the Form
 * @param {Number}
 *            props.crudTableId - SQL table id
 * @param {Number}
 *            props.defaultHeight -
 * @deprecated defaultHeight is a height of the Grid
 * @param {Number}
 *            props.defaultWidth -
 * @deprecated defaultWidth is width of the Grid
 * @param {Array}
 *            props.detailGrids[] - ['false']=> no grid, Array of detail grids
 *            conf
 * @param {Object}
 *            props.detailGrids[].grid - detail grids props
 * @param {Object}
 *            props.detailGrids[].params - master detail connection Master
 *            primaty key name {xoffer_id: "offer_id"}
 * @param {Object}
 *            props.detailGrids[].pk - Master detail connection Detail primaty
 *            key name {toffer_detail_id: "offer_detail_id"}
 * @param {Number}
 *            props.gridId - Id of the grid
 * @param {string}
 *            props.gridReport - show or not show reporter tools
 * @param {string}
 *            props.keyField - PK of the table
 * @param {string}
 *            props.name - UI Name of the grid table
 * @param {
 *            Array } props.menuButtons - return array of Objects conf { text,
 *            handler, cls, ref }
 * @param {Number}
 *            props.pageSize - Number of rows in grid to show in one page
 * @param {Number}
 *            props.queryId - Query id of the Grid
 * @param {Symbol}
 *            props.searchForm - Search form is generated from ServerSide and
 *            extens from XForm Component
 * @param {String}
 *            props._url -
 *            ["ajaxQueryData?_renderer=react16&.t=tpi_1531758063549&.w=wpi_1531758063547&_qid=4220&_gid=3376&firstLimit=10"]
 * @param {function}
 *            props._timelineBadgeBtn - will work when the timelineBadge is
 *            clicked
 * @param {Number}
 *            props.forceRelaod - to find out weathet it is delated or not used
 *            to compare props with prevProps
 * @param {Boolean}
 *            props._hideTimelineBadgeBtn - to hide _hideTimelineBadgeBtn
 * @param {Array}
 *            props.extraButtons - Array of buttons in grid
 * 
 */
class XMainGrid extends GridCommon {
  constructor(props) {
    super(props);
    var oldGridState = iwb.grids[props.id];
    if (iwb.debug) console.log("XMainGrid", props.extraButtons);
    if (oldGridState) {
      this.state = oldGridState;
      this.dontRefresh = true; // true-yuklemez, false-yukleme yapar
    } else {
      var columns = [],
        columnExtensions = [];
      const canIOpenActions =
        (props.crudFlags && (props.crudFlags.edit || props.crudFlags.remove)) ||
        props.menuButtons;
      if (canIOpenActions) {
        columns.push({
          name: "_qw_",
          title: ".",
          getCellValue: rowData => {
            var { onEditClick, onDeleteClick } = this;
            return _(XGridRowAction, {
              ...{ rowData },
              ...{ menuButtons: props.menuButtons },
              ...{ crudFlags: props.crudFlags },
              ...{ onEditClick, onDeleteClick },
              ...{ parentCt: this}
            });
          }
        });
        columnExtensions.push({
          columnName: "_qw_",
          width: 60,
          align: "right",
          sortingEnabled: false
        });
      }
      var colTemp = props.columns;
      colTemp &&
        colTemp.map(colLocal => {
          var title;
          switch (colLocal.name) {
            case "pkpkpk_faf":
              title = _("i", { className: "icon-paper-clip" });
              break;
            case "pkpkpk_ms":
              title = _("i", { className: "icon-envelope" });
              break;
            case "pkpkpk_cf":
              title = _("i", { className: "icon-bubble" });
              break;
            case "pkpkpk_apf":
              title = _("i", { className: "icon-picture" });
              break;
            case "pkpkpk_vcsf":
              title = _("i", { className: "icon-social-github" });
              break;
          }
          columns.push({
            name: colLocal.name,
            title: title || colLocal.title,
            getCellValue: colLocal.formatter || undefined
          });
          columnExtensions.push({
            columnName: colLocal.name,
            align: colLocal.align || "left",
            width: +colLocal.width,
            sortingEnabled: !!colLocal.sort
          });
        });
      var state = {
        columns,
        order: columns.map(({ name }) => name),
        columnExtensions,
        columnWidths: columnExtensions.map(({ columnName, width }) => {
          return { columnName, width };
        }),
        rows: [],
        sorting: [],
        totalCount: 0,
        pageSize: props.pageSize || iwb.detailPageSize,
        pageSizes:
          props.pageSize > 1
            ? [parseInt(props.pageSize / 2), props.pageSize, 3 * props.pageSize]
            : [5, 10, 25, 100],
        currentPage: 0,
        hideSF: true,
        loading: false
      };
      props.detailGrids &&
        props.detailGrids.length > 1 &&
        props.detailGrids.map(({ grid }, key) => {
          if (key < 2) state["dg-" + grid.gridId] = key < 2;
        });

      this.state = state;
    }
    /**
	 * used to give click event to the detail timeLineBadge button
	 * (event,masterDridProps,detailGridProps,row)
	 */
    this._timelineBadgeBtn = this.props._timelineBadgeBtn;
    /**
	 * @description A function to open and close detail grid
	 * @param {event}
	 *            event - click event
	 * @param {Object}
	 *            event.target - target object from clicked place
	 */
    this.toggleDetailGrid = ({ target }) => {
      var detailGridList = {};
      detailGridList[target.name] = target.checked;
      this.setState(detailGridList);
    };
    let { searchForm, detailGrids } = this.props;
    if (searchForm || (detailGrids && detailGrids.length > 1)) {
      var self = this;
      this.searchForm = _(
        Nav,
        { style: {} },
        searchForm &&
          _(
            "span",
            null,
            _(
              "div",
              { className: "hr-text" },
              _("h6", null, "Seacrh Criteria")
            ),
            _(
              "div",
              { style: { zoom: ".9"}, className:"searchFormFields"  },
              _(searchForm, { parentCt: this }),
              _(
                "div",
                { className: "form-group", style: { paddingTop: 10 } },
                _(
                  Button,
                  {
                    color: "danger",
                    style: { width: "100%", borderRadius: 2 },
                    onClick: () => {
                      this.loadData(true);
                    }
                  },
                  "SEARCH"
                )
              )
            ),
         /*
			 * _("div", { style: { height: 10 } }), _("div", { className:
			 * "hr-text" }, _("h6", null, "Şablonlar")), _( Link, { style: {
			 * padding: 2 }, to: "" }, _("i", { className: "icon-star" }), " ", "
			 * Yıllık Faturalar" ), _( Link, { style: { padding: 2, color:
			 * "#a0a0a0" }, to: "" }, _("i", { className: "icon-plus" }), " ", "
			 * Yeni Şablon Ekle" ),
			 */
            _("div", { style: { height: 20 } })
          ),
        detailGrids &&
          detailGrids.length > 1 &&
          _(
            "div",
            { className: "hr-text", key: "hr-text" },
            _("h6", null, "DETAILS")
          ),
        detailGrids &&
          detailGrids.length > 1 &&
          detailGrids.map((detailGrid, key) => {
            return _(
              "div",
              {
                key,
                style: {
                  padding: "3px 0px 2px 3px",
                  color: "#6d7284",
                  fontSize: ".9rem"
                }
              },
              detailGrid.grid.name,
              _(
                "label",
                {
                  className:
                    "float-right switch switch-xs switch-3d switch-" +
                    dgColors[key % dgColors.length] +
                    " form-control-label"
                },
                _("input", {
                  name: "dg-" + detailGrid.grid.gridId,
                  type: "checkbox",
                  className: "switch-input form-check-input",
                  onChange: self.toggleDetailGrid,
                  defaultChecked: self.state["dg-" + detailGrid.grid.gridId]
                }),
                _("span", { className: "switch-label" }),
                _("span", { className: "switch-handle" })
              )
            );
          })
      );
    }
    /**
	 * @description A function to search globally
	 * @param {Event}
	 *            event - event from the global search
	 */
    this.onGlobalSearch = event =>
      this.loadData(true, {
        xsearch: event && event.target ? event.target.value : event
      });
    iwb.onGlobalSearch2 = this.onGlobalSearch;
    /**
	 * @description Is a function to toggle search form from the XMainGrid
	 *              component and animata iconMagnifier
	 */
    this.toggleSearch = () => {
      var searchFormDOM = document.getElementById("sf-" + this.props.id);
      if (searchFormDOM) {
        var iconMagnifier = document.getElementById("eq-" + this.props.id);
        if (searchFormDOM.classList.contains("sf-hidden")) {
          iconMagnifier.classList.add("rotate-90deg");
        } else {
          iconMagnifier.classList.remove("rotate-90deg");
        }
        searchFormDOM.classList.toggle("sf-hidden");
      }
    };
    /**
	 * @description A function to open EXPORT menu in XModal
	 */
    this.openBI = () => {
      let { props } = this;
      let { columnExtensions, order } = this.state;
      let cmap = {};
      let url = "grd/" + props.name + ".";
      let params = "?_gid=" + props.gridId + "&_columns=";
      columnExtensions.map(({ columnName, width }) => {
        cmap[columnName] = width;
      });
      order.map(
        columnName =>
          (params += columnName + "," + (cmap[columnName] || 100) + ";")
      );
      iwb.showModal({
        title: "REPORTS / BI",
        footer: false,
        color: "danger",
        size: "sm",
        body: _(
          ListGroup,
          { style: { fontSize: "1.0rem" } },
          _("b", null, "Exports"),
          _(
            ListGroupItem,
            {
              tag: "a",
              href: url + "xls" + params,
              target: "_blank",
              action: true
            },
            _("i", { className: "float-right text-success fa fa-file-excel" }),
            " ",
            "Export to Excel"
          ),
          _(
            ListGroupItem,
            {
              tag: "a",
              href: url + "pdf" + params,
              target: "_blank",
              action: true
            },
            _("i", { className: "float-right text-danger fa fa-file-pdf" }),
            " ",
            "Export to PDF"
          ),
          _(
            ListGroupItem,
            {
              tag: "a",
              href: url + "csv" + params,
              target: "_blank",
              action: true
            },
            _("i", { className: "float-right text-secondary fa fa-file-alt" }),
            " ",
            "Export to CSV File"
          ),
          _(
            ListGroupItem,
            {
              tag: "a",
              href: url + "txt" + params,
              target: "_blank",
              action: true
            },
            _("i", { className: "float-right text-secondary fa fa-file-word" }),
            " ",
            "Export to Text File"
          ),
          _("hr"),
          _("b", null, "BI"),
          _(
            ListGroupItem,
            {
              tag: "a",
              href:
                "showPage?_tid=" +
                (props.crudTableId
                  ? "1200&xtable_id=" + props.crudTableId
                  : "2395&xquery_id=" + props.queryId),
              target: "_blank",
              action: true /* , className:'list-group-item-danger2' */
            },
            _("i", { className: "float-right text-primary fa fa-th" }),
            " ",
            "Pivot Table"
          ),
          _(
            ListGroupItem,
            {
              tag: "a",
              href:
                "showPage?_tid=" +
                (props.crudTableId
                  ? "784&xtable_id=" + props.crudTableId
                  : "2413&xquery_id=" + props.queryId),
              target: "_blank",
              action: true
            },
            _("i", { className: "float-right text-primary fa fa-table" }),
            " ",
            "Data List"
          )
        )
      });
    };
    /**
	 * @description A function to render Details under Muster's row
	 * @param {Array}
	 *            tempDetailGrids[] - array of detail grids conf
	 * @param {Object}
	 *            tempDetailGrids[].grid - detail grids props
	 * @param {Object}
	 *            tempDetailGrids[].params - master detail connection Master
	 *            primaty key name {xoffer_id: "offer_id"}
	 * @param {Object}
	 *            tempDetailGrids[].pk - Master detail connection Detail primaty
	 *            key name {toffer_detail_id: "offer_detail_id"}
	 */
    this.showDetail2 = tempDetailGrids => {
      var selfie = this;
      return row => {
        if (row) {
          var rowSDetailGrids = [];
          for (var DGindex = 0; DGindex < tempDetailGrids.length; DGindex++) {
            if (
              tempDetailGrids.length >= 1
            ) {
              var show = (selfie.state.hasOwnProperty('dg-' + tempDetailGrids[DGindex].grid.gridId)) ? selfie.state['dg-' + tempDetailGrids[DGindex].grid.gridId] : true;
              var detailXGrid = {
                ...{
                  pk: tempDetailGrids[DGindex].pk || {}
                },
                ...tempDetailGrids[DGindex].grid
              };
              if (detailXGrid._url)
                detailXGrid._url += buildParams2(
                  tempDetailGrids[DGindex].params,
                  row.row
                );
              else detailXGrid.rows = row.row[detailXGrid.detailRowsFieldName];
              detailXGrid.detailFlag = true;
              show && rowSDetailGrids.push(
                _(
                  "li", {
                    key: DGindex,
                    className: "timeline-inverted"
                  },
                  // _(XGridAction,{color:dgColors[DGindex%dgColors.length]}),
                  !detailXGrid._hideTimelineBadgeBtn &&
                  _(
                    "div", {
                      className: "timeline-badge hover-shake " +
                        dgColors[DGindex % dgColors.length],
                      dgindex: DGindex,
                      onClick: event => {
                        var DGindexDOM = +event.target.getAttribute("dgindex");
                        if (iwb.debug)
                          console.log(
                            "dasss",
                            DGindexDOM,
                            tempDetailGrids[DGindexDOM].grid
                          );
                        if (!!selfie._timelineBadgeBtn) {
                          selfie._timelineBadgeBtn(
                            event,
                            selfie.props,
                            tempDetailGrids[DGindexDOM].grid,
                            row.row,
                            selfie
                          );
                        } else {
                          selfie.onOnNewRecord(
                            event,
                            tempDetailGrids[DGindexDOM].grid,
                            row.row
                          );
                        }
                      },
                      style: {
                        cursor: "pointer"
                      }
                    },
                    _("i", {
                      className: "icon-grid",
                      style: {
                        fontSize: 17
                      },
                      dgindex: DGindex
                    })
                  ),
                  _(
                    "div", {
                      className: "timeline-panel",
                      ...(!!detailXGrid._hideTimelineBadgeBtn ? {
                        style: {
                          left: "30px"
                        }
                      } : {})
                    },
                    _(
                      "div", {
                        className: "timeline-heading mb-1"
                      },
                      _(
                        "span", {
                          className: "timeline-title pr-3 h5"
                        },
                        detailXGrid.name,
                      ),
                      detailXGrid.extraButtons && detailXGrid.extraButtons.map((props, index) => {
                        if (props.type === "button") {
                          var {click,text,icon} = props;
                          var cls = icon.split('|');
                          return _(
                            Button, {
                              key: 'key' + index,
                              size: 'sm',
                              outline: true,
                              className: 'btn-round-shadow hover-to-show-link ml-1 ' + cls[1],
                              color: dgColors[index % dgColors.length],
                              onClick: event => click( event, detailXGrid, row.row )
                            },
                            cls[0] && _('i', { className: 'icon-' + cls[0] }),
                            text && _('span', { className: 'hover-to-show'}, text)
                          )
                        }
                      })
                      /**
						 * other inputs will be added when there will be need
						 */
                      // _('span',{className: "float-right",
						// style:{marginTop:'-23px', marginRight:'15px'}},
                      // _('i',{ className: "icon-arrow-up",
						// style:{marginRight: '12px'}}),' ',_('i',{ className:
						// "icon-close"}),' ')
                    ),
                    _(XGrid, {
                      responsive: true,
                      openTab: selfie.props.openTab,
                      showDetail: tempDetailGrids[DGindex].detailGrids ?
                        selfie.showDetail2(
                          tempDetailGrids[DGindex].detailGrids
                        ) : false,
                      ...detailXGrid
                    })
                  )
                )
              ); // push end
            } // if end
          } // for end
          return (
            rowSDetailGrids.length > 0 &&
            _("ul", {
              className: "timeline"
            }, rowSDetailGrids)
          );
        } else {
          return null;
        }
      };
    };
    /**
	 * @overloading
	 * @param {Boolean}
	 *            force - Get up to data data
	 * @param {object}
	 *            params -[{xsearch:'searchValue'}] Params from Global Search
	 */
    this.loadData = (force, params = {}) => {
      const queryString = this.queryString();
      if (!force && queryString === this.lastQuery) {
        return;
      }
      var tempParams = {
        ...{ params },
        ...(this.form ? this.form.getValues() : {})
      };
      iwb.request({
        url: queryString,
        self: this,
        params:tempParams,
        successCallback: (result, cfg) => {
          cfg.self.setState({
            rows: result.data,
            totalCount: result.total_count
          });
        },
        errorCallback: (error, cfg) => {
          cfg.self.setState({
            rows: [],
            totalCount: 0
          });
        }
      });
      this.lastQuery = queryString;
    };
  }
  componentDidMount() {
    if (!this.dontRefresh) this.loadData();
    this.dontRefresh = false;
  }
  componentDidUpdate(prevProps, prevState, snapshot) {
    if (this.props.forceRelaod !== prevProps.forceRelaod) {
      this.loadData(true);
    } else {
      this.loadData();
      this.dontRefresh = false;
    }
  }
  componentWillUnmount() {
    var state = Object.assign({}, this.state);
    var sf = document.getElementById("sf-" + this.props.id);
    if (sf) {
      state.hideSF = sf.classList.contains("sf-hidden");
    }
    iwb.grids[this.props.id] = state;
  }
  render() {
    const {
      state: {
        rows,
        order,
        columns,
        sorting,
        loading,
        pageSize,
        selection,
        pageSizes,
        totalCount,
        currentPage,
        columnWidths,
        columnExtensions
      },
      props: {
        keyField,
        crudFlags,
        detailGrids,
        multiselect,
        extraButtons,
        _disableSearchPanel,
        _disableIntegratedSorting,
        _disableIntegratedGrouping
      },
      loadData,
      searchForm,
      rowComponent,
      toggleSearch,
      onOrderChange,
      onOnNewRecord,
      onSortingChange,
      onPageSizeChange,
      onSelectionChange,
      onCurrentPageChange,
      onColumnWidthsChange
    } = this;

    let showDetail = detailGrids && detailGrids.length > 0;
    let grid = _(
      _dxgrb.Grid,
      { className:'maingrid', rows: rows, columns, getRowId: row => row[keyField] },
      /** sorting state */
      !_disableIntegratedSorting &&
        _(
          _dxrg.SortingState,
          !pageSize ? null : { sorting, onSortingChange, columnExtensions }
        ),
        multiselect &&
        _(_dxrg.SelectionState, {
            selection,
            onSelectionChange
        }),
      /** pagesize > 0 will import search state */
      !pageSize ? _(_dxrg.SearchState, null) : null,
      /** Client filtering */
      !_disableSearchPanel &&
        !pageSize &&
        rows.length > 1 &&
        _(_dxrg.IntegratedFiltering, null),
      /** state of the grouping */
      !_disableIntegratedGrouping &&
        !pageSize &&
        rows.length > 1 &&
        _(_dxrg.GroupingState, null),
      /** ability to group like a tree */

      !_disableIntegratedGrouping &&
        !pageSize &&
        rows.length > 1 &&
        _(_dxrg.IntegratedGrouping, null),
      /** sorting wii be enabled when pageSize>0 and row has more than one data */
      !_disableIntegratedSorting &&
        !pageSize &&
        rows.length > 1 &&
        _(_dxrg.IntegratedSorting, null),
      /** row detail state */
      showDetail ? _(_dxrg.RowDetailState, null) : null,
      /** state paging */
      rows.length > iwb.detailPageSize || pageSize > 1
        ? _(
            _dxrg.PagingState,
            pageSize > 1
              ? { pageSize, currentPage, onPageSizeChange, onCurrentPageChange }
              : {}
          )
        : null,
        multiselect && _(_dxrg.IntegratedSelection, null),
      /** For remote paging */
      pageSize > 1 &&
        rows.length > 1 &&
        _(_dxrg.CustomPaging, { totalCount: totalCount }),
      /** enable group drag drop */
      _(_dxgrb.DragDropProvider, null),
      /** ui table */
      _(_dxgrb.Table, { columnExtensions, rowComponent }),
      /** multiselect */
      multiselect &&
        _(_dxgrb.TableSelection, {
          showSelectAll: true
        }),
      /** UI ordering of the table */
      _(_dxgrb.TableColumnReordering, { order, onOrderChange }),
      /** UI tablle resizing */
      _(_dxgrb.TableColumnResizing, { columnWidths, onColumnWidthsChange }),
      /** UI to show table row container */

      _(_dxgrb.TableHeaderRow, { showSortingControls: true }),
      /** UI of the detail table */
      showDetail
        ? _(_dxgrb.TableRowDetail, {
            contentComponent: this.showDetail2(detailGrids)
          })
        : null,
      /** UI show pagining */
      rows.length > iwb.detailPageSize || pageSize > 1
        ? _(_dxgrb.PagingPanel, { pageSizes: pageSizes || iwb.detailPageSize })
        : null,
      /** UI table Grouping */
      !_disableIntegratedGrouping && !pageSize && rows.length > 1
        ? _(_dxgrb.TableGroupRow, null)
        : null,
      /** top of grit do render some buttons */
      !pageSize && rows.length > 1 && _(_dxgrb.Toolbar, null),
      /** ui search input */
      !pageSize &&
        rows.length > 1 &&
        !_disableSearchPanel &&
        _(_dxgrb.SearchPanel, {
          messages: { searchPlaceholder: "Hızlı Arama..." },
          changeSearchValue: ax => {
            if (iwb.debug) console.log("onValueChange", ax);
          }
        }),
      /** UI grouping panel */
      !_disableIntegratedGrouping &&
        !pageSize &&
        rows.length > 1 &&
        _(_dxgrb.GroupingPanel, { showSortingControls: true })
    );

    return _(
      "div",
      { className: "tab-grid mb-4" },
      searchForm &&
        _(
          "nav",
          {
            id: "sf-" + this.props.id,
            className: this.state.hideSF ? "sf-hidden" : ""
          },
          searchForm
        ),
      _(
        "main",
        { className: "inbox" },
        _(
          CardHeader,
          {},
          searchForm &&
            _(
              Button,
              {
                className: "btn-round-shadow",
                color: "secondary",
                style: { marginLeft: "5px" },
                onClick: toggleSearch
              },
              _("i", { id: "eq-" + this.props.id, className: "icon-magnifier" })
            ),

          !searchForm &&
            _(
              Button,
              {
                className: "btn-round-shadow",
                disabled: loading,
                color: "secondary",
                style: { marginLeft: "5px" },
                onClick: event => loadData(true)
              },
              _("i", { className: "icon-refresh" })
            ),

          crudFlags &&
            crudFlags.insert &&
            _(
              Button,
              {
                className: "btn-round-shadow",
                style: { marginLeft: "5px" },
                color: "primary",
                onClick: event => onOnNewRecord(event, this.props)
              },
              _("i", { className: "icon-plus" }),
              " NEW RECORD"
            ),
            _('div',{className:"fgrow"},null),

          extraButtons &&
            extraButtons.map((prop, index) => {
              if (prop.type === "button") {
                let { icon } = prop;
                var cls = icon.split('|');
                return _(
                  Button,
                  {
                    id: "toolpin" + index,
                    key: "key" + index,
                    className: classNames("btn-round-shadow mx-1", cls[1]),
                    color: "success",
                    onClick: prop.click && prop.click.bind(this)
                  },
                  cls[0] && _("i", { className: cls[0] }),
                  prop.text && prop.text
                );
              }
              prop.autoComplete = "off";
              prop.key = "Ikey" + index;
              return _(prop.$ || Input, { ...prop, $: undefined });
            }),
          // _(Button,{className:'float-right btn-round-shadow
			// hover-shake',color:'danger',
			// onClick:this.toggleSearch},_('i',{style:{transition: "transform
			// .2s"},id:'eq-'+this.props.id,className:'icon-equalizer'+(this.state.hideSF?'':'
			// rotate-90deg')}))
          this.props.gridReport &&
            _(
              Button,
              {
                className: "float-right btn-round-shadow hover-shake",
                color: "danger",
                onClick: this.openBI
              },
              _("i", { className: "icon-equalizer" })
            ) // , this.props.globalSearch && _(Input,{type:"text",
				// className:"float-right form-control w-25",
				// onChange:this.onGlobalSearch, placeholder:"Hızlı Arama...",
				// defaultValue:"", style:{marginTop: '-0.355rem',
				// marginRight:'.4rem'}}) )
        ),
        grid
      )
    );
  }
}
/**
 * @description this component made for render complex ui
 * @example form+grid, grid, form, form+form
 */
class XPage extends React.PureComponent {
  constructor(props) {
    if (iwb.debugConstructor && iwb.debug) console.log("XPage.constructor", props);
    super(props);
    document.getElementById("id-breed").innerHTML = this.props.grid.name;
    iwb.killGlobalSearch();
    this.state = { activeTab: "x" };
    this.tabs = (iwb.tabs[this.props.grid.id])?[...iwb.tabs[this.props.grid.id]]:[{ name: "x", icon:"icon-list", title: "Liste", value: props.grid }];
    /**
	 * @description a Function to toggle between tabs
	 * @param {Event}
	 *            event - click event from tab
	 */
    this.toggle = event => {
      var activeTab = event.target ? event.target.getAttribute("name") : event;
      if (this.state.activeTab !== activeTab) {
        var {
          tabs
        } = this;
        tabs &&
          tabs.forEach(tempTab => {
            if (tempTab.name === activeTab) {
              this.setState({
                activeTab
              });
              return true;
            }
          });
      }
      return false;
    };
    this.isActionInTabList = action => {
      var stopToFetch = false;
      this.tabs &&
      this.tabs.forEach(tempTab => {
          if (tempTab.name === action) {
            this.toggle(action);
            stopToFetch = true;
          }
        });
      return stopToFetch;
    };
    /**
	 * @description A function responsible for opening tab getting component
	 *              from the server and evaluating it on the page
	 * @param {String}
	 *            action - ['1-&toffer_id=4'] EditForm satrts 1-* , InsertForm
	 *            satrts 2-*
	 * @param {String}
	 *            url - ['showForm?a=1&_fid=3988&twork_position_id=1']
	 * @param {Object}
	 *            params - a varible wich holds request body params
	 * @param {Object}
	 *            callAttributes - [{modal:false}] a variable used to pass
	 *            params to a component which comes from the server
	 */
    this.openTab = (action, url, params, callAttributes) => {
      if (this.state.activeTab !== action) {
        if (this.isActionInTabList(action)) return;
        fetch(url, {
            body: JSON.stringify(params || {}), // must match 'Content-Type'
												// header
            cache: "no-cache", // *default, no-cache, reload, force-cache,
								// only-if-cached
            credentials: "same-origin", // include, same-origin, *omit
            headers: {
              "content-type": "application/json"
            },
            method: "POST", // *GET, POST, PUT, DELETE, etc.
            mode: "cors", // no-cors, cors, *same-origin
            redirect: "follow", // *manual, follow, error
            referrer: "no-referrer" // *client, no-referrer
          })
          .then(
            response =>
            response.status === 200 || response.status === 0 ?
            response.text() :
            Promise.reject(
              new Error(response.text() || response.statusText)
            )
          )
          .then(
            result => {
              if (result) {
                var f;
                eval("f=(callAttributes, parentCt)=>{\n" + result + "\n}");
                var serverComponent = f(callAttributes || {}, this);
                if (serverComponent) {
                  if (callAttributes && callAttributes.modal) {
                    // console.log(callAttributes);
                    iwb.showModal({
                      body: serverComponent,
                      size: "lg",
                      title: serverComponent.props && serverComponent.props.cfg ?
                        serverComponent.props.cfg.name :
                        "",
                      color:"primary",
                      ...callAttributes.modalProps
                    });
                  } else {
                    var plus = action.substr(0, 1) == "2";
                    if (this.isActionInTabList(action)) return;
                    this.tabs.push({
                      name: action,
                      icon: plus ? "icon-plus" : "icon-doc",
                      title: ' '+plus ? getLocMsg('js_new') : getLocMsg('js_edit'),
                      value: serverComponent
                    });
                    this.setState({
                      activeTab: action
                    });
                  }
                }
              } else {
                toastr.error("Sonuc Gelmedi", " Error");
              }
            },
            error => {
              toastr.error(error, "Connection Error");
            }
          );
      }
    };
    iwb.openTab = this.openTab;
    /**
	 * @description A function responsible for closing tab and delating
	 *              CurrentTab from the state of Xpage Component this function
	 *              will be passed to whenever new tab is opened
	 */
    this.closeTab = (event, forceRelaod = false) => {
      if (this.state.activeTab == "x") return;
      this.tabs = this.tabs && this.tabs.filter(tempTab => tempTab.name !== this.state.activeTab);
      if (forceRelaod) {
        this.tabs["0"].value.forceRelaod = Math.floor(Math.random() * 1000);
      }
      this.toggle("x");
    };
    iwb.closeTab = this.closeTab;
    /**
	 * @description A function is used to open new FormTab
	 * @param {string}
	 *            url
	 */
    this.openForm = (url, callAttributes = {}) => {
      if (url) this.openTab("1-" + Math.random(), url, {}, callAttributes);
      return false;
    };
    iwb.openForm = this.openForm;
  }
  componentWillUnmount() {
    iwb.killGlobalSearch();
    iwb.tabs[this.props.grid.id] = [...this.tabs];
  }
  render() {
    if (iwb.debugRender) if (iwb.debug) console.log("XPage.render");
    return _(
      "div",
      {},
      _(
        Row,
        null,
        _(
          Col,
          { className: "mb-4" },
          _(
            Nav,
            { tabs: true, hidden: this.tabs.length == 1 },
            this.tabs.map(({ name, icon, title }, index) => {
              return _(
                NavItem,
                { key: "NavItem" + index },
                _(
                  NavLinkS,
                  {
                    className: classNames({
                      active: this.state.activeTab === name
                    }),
                    name,
                    onClick: event => this.toggle(event)
                  },
                  _("i", {
                    className: icon,
                    name,
                    title,
                    onClick: event => this.toggle(event)
                  }),
                  title && name != "x" && this.state.activeTab === name && title
                )
              );
            })
          ),
          _(
            TabContent,
            { activeTab: this.state.activeTab },
            this.tabs.map(({ name, value }, index) => {
              return _(
                TabPane,
                { key: "TabPane" + index, tabId: name },
                value.gridId
                  ? _(XMainGrid, {
                      openTab: this.openTab,
                      closeTab: this.closeTab,
                      ...value
                    })
                  : value
              );
            })
          )
        )
      )
    );
  }
}
/**
 * @description this component is mostly used for render menu page You can set
 *              ti as a home page
 * @param {String}
 *            props.color - ["primary"] Color class of the card
 * @param {String}
 *            props.color2 - ["#2eadd3"] Color of the icon
 * @param {String}
 *            props.color3 - Fadein color of the card
 * @param {Object}
 *            props.node - MINI MENU data
 * @param {String}
 *            props.node.icon - ["icon-heart"] icon class of the menu
 * @param {String}
 *            props.node.name - ['Teklif/Talep Listesi'] name of the menu
 * @param {String}
 *            props.node.url - ["/mnu_2477/showPage2352"] - URL of the router
 * @param {Boolean}
 *            props.node.visited - Visited?
 */
class XCardMenu extends React.PureComponent {
  render() {
    return _(
      Col,
      { xs: "12", sm: "6", md: "6", lg: "6", xl: "4" },
      _(
        Link,
        { to: this.props.node.url },
        _(
          Card,
          {
            className: "card-menu text-white bg-" + this.props.color,
            style: this.props.fadeOut
              ? { opacity: 0, transform: "scale(.9)" }
              : this.props.fadeOut === false
                ? { transform: "scale(1.1)" }
                : {}
          },
          _("i", {
            className: "big-icon " + (this.props.node.icon || "icon-settings"),
            style: this.props.color3 ? { color: this.props.color3 } : {}
          }),
          _(
            CardBlock,
            { className: "pb-0" },
            this.props.fadeOut === false
              ? _(
                  "div",
                  {
                    className: "float-right",
                    style: {
                      height: "56px",
                      width: "56px",
                      background: "white",
                      padding: "0px",
                      borderRadius: "55px"
                    }
                  },
                  iwb.loaders.puff(56, 56, this.props.color2)
                )
              : _("i", {
                  className:
                    "float-right " + (this.props.node.icon || "icon-settings"),
                  style: {
                    fontSize: "30px",
                    background: "white",
                    padding: "13px",
                    borderRadius: "55px",
                    color: this.props.color2
                  }
                }),
            _("h1", { className: "mb-0" }, this.props.node.name),
            _("p", null, this.props.node.name + " ile ilgili işlemler")
          )
        )
      )
    );
  }
}
/**
 * @description it is used to list opened pages on the main page
 * @param {String}
 *            props.color - ['gray-700'] - color class of the Card
 * @param {Boolean}
 *            props.fadeOut - Card Animation
 * @param {Object}
 *            props.node - MINI MENU data
 * @param {String}
 *            props.node.icon - ["icon-heart"] icon class of the menu
 * @param {String}
 *            props.node.name - ['Teklif/Talep Listesi'] name of the menu
 * @param {String}
 *            props.node.url - ["/mnu_2477/showPage2352"] - URL of the router
 */
class XCardMiniMenu extends React.PureComponent {
  render() {
    let {
      color,
      fadeOut,
      node: { icon, name, url }
    } = this.props;
    return _(
      Col,
      { xs: "4", sm: "3", md: "2", lg: "2", xl: "1" },
      _(
        Link,
        { to: url },
        _(
          Card,
          {
            className: "card-mini-menu text-white bg-" + color,
            style: fadeOut
              ? { opacity: 0, transform: "scale(.9)" }
              : fadeOut === false
                ? { transform: "scale(1.1)" }
                : {}
          },
          _(
            CardBlock,
            { className: "pb-1", style: { textAlign: "center", padding: "0" } },
            _("i", {
              className: icon || "icon-settings",
              style: { fontSize: "28px", padding: "12px", color: "white" }
            })
          )
        )
      ),
      _("h6", { style: { textAlign: "center" } }, name)
    );
  }
}
/**
 * @description used to render left menu it gets data from index.htm file
 *              (catche)
 * @param {String}
 *            props.path - [/iwb-home"] path of the current route
 * @param {String}
 *            props.node.icon - ["icon-heart"] icon class of the menu
 * @param {String}
 *            props.node.name - ['Teklif/Talep Listesi'] name of the menu
 * @param {String}
 *            props.node.url - ["/mnu_2477/showPage2352"] - URL of the router
 */
class XMainNav extends React.PureComponent {
  constructor(props) {
    if (iwb.debugConstructor)
      if (iwb.debug) console.log("XMainNav.constructor", props);
    super(props);
    this.onGlobalSearch = inputValue =>
      this.setState({
        xsearch:
          inputValue && inputValue.target ? inputValue.target.value : inputValue
      });
    iwb.onGlobalSearch2 = this.onGlobalSearch;
    this.state = { xsearch: "" };
  }
  componentWillUnmount() {
    if (iwb.debug) console.log("XMainNav.componentWillUnmount");
    iwb.killGlobalSearch();
  }
  componentDidUpdate() {
    if (iwb.debug) console.log("XMainNav.componentDidUpdate");
  }
  render() {
    if (iwb.debug) console.log("this.state.xsearch", this.state.xsearch);
    if (this.state.xsearch) {
      var nodes = iwb.nav.findNodes(this.state.xsearch.toLowerCase(), {
        name: "Home",
        children: iwb.nav.items
      });
      if (iwb.debug) console.log("nodes", nodes);
      if (!nodes || !nodes.length) return "Bulunamadı :(";
      return _(
        "div",
        { className: "animated fadeIn" },
        _("div", { style: { height: "1.45rem" } }),
        "Arama Sonuçları",
        _("hr", { style: { marginTop: "0.4rem" } }),
        _(
          Row,
          { style: { maxWidth: "1300px" } },
          nodes.map((node, visitedIndex) => {
            return _(XCardMiniMenu, {
              color: dgColors3[visitedIndex % dgColors3.length],
              node
            });
          })
        )
      );
    }
    var path = this.props.path,
      node = this.props.node;
    var visitedList = false,
      saggestedList = false;
    if (path == "/" || path == "/iwb-home") {
      (visitedList = []), (saggestedList = []);
      var visitedIndex = 0,
        saggestedIndex = 0;
      for (var k in iwb.nav.visitedItems) {
        var o = iwb.nav.visitedItems[k];
        visitedList.push(
          _(XCardMiniMenu, {
            key: "xcardmini" + Math.random(),
            color: dgColors3[visitedIndex % dgColors3.length],
            node: o
          })
        );
        visitedIndex++;
        if (o.visitCnt > 2) {
          saggestedList.push(
            _(XCardMiniMenu, {
              key: "xcardminivisit" + Math.random(),
              color: dgColors2[saggestedIndex % dgColors2.length],
              node: o
            })
          );
          saggestedIndex++;
        }
      }
      if (visitedIndex == 0) visitedList = false;
      else {
        visitedList = [
          _("div", { key: "a1", style: { height: "1.5rem" } }),
          "Açık Ekranlar",
          _("hr", { key: "a2", style: { marginTop: "0.4rem" } }),
          _(Row, { key: "a3", style: { maxWidth: "1300px" } }, visitedList)
        ];
        if (saggestedIndex > 0) {
          if (saggestedList.length > 4) {
            saggestedList.splice(4, 1000);
          }
          visitedList.push(
            _("div", { style: { height: "1.5rem" } }),
            "iWB Öneriler",
            _("hr", { style: { marginTop: "0.4rem" } }),
            _(Row, { style: { maxWidth: "1300px" } }, saggestedList)
          );
        }
      }
    }

    return _(
      "div",
      { className: "animated fadeIn" },
      _("div", { style: { height: "1.45rem" } }),
      _(
        Row,
        { style: { maxWidth: "1300px" } },
        node.children.map((tempNode, index) =>
          _(XCardMenu, {
            key: index,
            node: tempNode,
            color: dgColors2[index % dgColors2.length],
            color3: dBGColors2[index % dBGColors2.length],
            color2: detailSpinnerColors2[index % detailSpinnerColors2.length]
          })
        )
      ),
      visitedList
    );
  }
}
/**
 * @description it renders main part of the application it contains XPage
 *              component of XCardMenu role : component like a container
 * @param {Object}
 *            props.history - history object from react router
 * @param {Object}
 *            props.location - current location object from react router
 * @param {Object}
 *            props.match - same with location object from react router
 */
class XMainPanel extends React.PureComponent {
  constructor(props) {
    if (iwb.debugConstructor) console.log("XMainPanel.constructor", props);
    super(props);
    this.state = { templateID: -1 };
    /**
	 * @description A function to load page from the server
	 */
    this.loadPage = () => {
      var templateID = this.templateID;
      if (!iwb["t-" + templateID]) {
        fetch("showPage?_tid=" + templateID, {
          cache: "no-cache", // *default, no-cache, reload, force-cache,
								// only-if-cached
          credentials: "same-origin", // include, same-origin, *omit
          headers: {
            "content-type": "application/json"
          },
          method: "POST", // *GET, POST, PUT, DELETE, etc.
          mode: "cors", // no-cors, cors, *same-origin
          redirect: "follow", // *manual, follow, error
          referrer: "no-referrer" // *client, no-referrer
        })
          .then(
            response =>
              response.status === 200 || response.status === 0
                ? response.text()
                : Promise.reject(new Error(response.statusText))
          )
          .then(
            result => {
              if (result) {
                var f;
                eval("f=(callAttributes, parentCt)=>{\n" + result + "\n}");
                var serverComponent = f(false, this);
                if (serverComponent) {
                  serverComponent = _(
                    "div",
                    { className: "animated fadeIn" },
                    serverComponent
                  );
                  iwb["t-" + templateID] = serverComponent;
                  this.setState({ templateID });
                  iwb.nav.visitItem(this.props.match.path);
                }
              } else {
                toastr.error("Sonuc Gelmedi", " Error");
              }
            },
            error => {
              toastr.error(error, "Connection Error");
            }
          );
      } else if (templateID != this.state.templateID)
        this.setState({ templateID });
    };
  }
  componentDidMount() {
    if (iwb.debug)
      console.log("XMainPanel.componentDidMount", this.props.match.path);
    if (!this.loading) this.loadPage();
  }
  componentDidUpdate() {
    if (iwb.debug)
      console.log("XMainPanel.componentDidUpdate", this.props.match.path);
    if (!this.loading) this.loadPage();
  }
  componentDidCatch() {
    if (iwb.debug) console.log("XMainPanel.componentDidCatch", this);
  }
  componentWillUnmount() {
    if (iwb.debug)
      console.log("XMainPanel.componentWillUnmount", this.props.match.path);
  }
  render() {
    var { path } = this.props.match;
    var children = { name: "Home", children: iwb.nav.items };
    var node =
      path == "/" || path == "/iwb-home"
        ? children
        : iwb.nav.findNode(path, children);
    if (iwb.debug) console.log("XMainPanel:render:", path, node);
    if (node) {
      var showPageIndex = path.indexOf("showPage");
      if (showPageIndex > -1) {
        var templateID = 1 * path.substr(showPageIndex + "showPage".length);
        this.templateID = templateID;
        if (this.templateID != this.state.templateID) {
          if (this.loading) {
            var ll = this.loading;
            this.loading = false;
            return _(
              "div",
              { className: "animated fadeIn" },
              _("div", { style: { height: "1.45rem" } }),
              _(
                Row,
                { style: { maxWidth: "1300px" } },
                ll.children.map((menuitem, index) => {
                  return _(XCardMenu, {
                    key: index,
                    node: menuitem,
                    fadeOut: menuitem.url != node.url,
                    color: dgColors2[index % dgColors2.length],
                    color3: dBGColors2[index % dBGColors2.length],
                    color2:
                      detailSpinnerColors2[index % detailSpinnerColors2.length]
                  });
                })
              )
            );
          }
          return _(XLoading, null);
        }
        var visitedItems = iwb.nav.visitedItems[path];
        if (visitedItems) visitedItems.visitCnt++;
        return iwb["t-" + templateID];
      } else {
        var pageName = document.getElementById("id-breed");
        if (pageName) {
          pageName.innerHTML = node.name || "Home";
        }
        this.loading = node;
        return _(XMainNav, { path, node });
      }
    } else {
      this.loading = false;
      return "ERROR! Wrong Page";
    }
  }
}
/**
 * @description Loading Component
 */
class XLoading extends React.Component {
  render() {
    return _(
      "span",
      { style: { position: "fixed", left: "48%", top: "45%" } },
      iwb.loading
    );
  }
}
/**
 * @description All the Forms will extend from this component so all the props
 *              will come from the server side
 */
class XForm extends React.Component {
  constructor(props) {
    super(props);
    // methods
    /**
	 * sets the state with value of input
	 * 
	 * @param {event}
	 *            param0
	 */
    this.onChange = ({ target }) => {
      var { values } = this.state;
      if (target) {
        values[target.name] =
          target.type == "checkbox" ? target.checked : target.value;
        this.setState({ values });
      }
    };
    /**
	 * sets state for combo change else sets oprions of it after the request
	 * 
	 * @param {String}
	 *            inputName
	 */
    this.onComboChange = inputName => {
      var self = this;
      return selectedOption => {
        var { values } = self.state;
        var slectedOption_Id = selectedOption && selectedOption.id;
        values[inputName] = slectedOption_Id;
        var triggers = self.triggerz4ComboRemotes;
        // used for remote @depricated
        if (triggers[inputName]) {
          triggers[inputName].map(trigger => {
            var nv = trigger.f(slectedOption_Id, null, values);
            var { options } = self.state;
            if (nv) {
              iwb.request({
                url:
                  "ajaxQueryData?" + iwb.JSON2URI(nv) + ".r=" + Math.random(),
                successCallback: ({ data }) => {
                  options[trigger.n] = data;
                  self.setState({ options });
                }
              });
            } else {
              options[trigger.n] = [];
              self.setState({ options });
            }
          });
        }
        self.setState({ values });
      };
    };
    /**
	 * sets state when low combo is entered
	 * 
	 * @param {String}
	 *            inputName
	 */
    this.onLovComboChange = inputName => {
      var self = this;
      return selectedOptions => {
        var { values } = self.state;
        var slectedOption_Ids = [];
        if (selectedOptions) {
          selectedOptions.map(selectedOption => {
            slectedOption_Ids.push(selectedOption.id);
          });
        }
        values[inputName] = slectedOption_Ids.join(",");
        self.setState({ values });
      };
    };
    /**
	 * sets state when number entered
	 * 
	 * @param {String}
	 *            dsc
	 */
    this.onNumberChange = inputName => {
      var self = this;
      return inputEvent => {
        var { values } = self.state;
        var inputValue = inputEvent && inputEvent.value;
        values[inputName] = inputValue;
        self.setState({ values });
      };
    };
    /**
	 * sends post to the server
	 * 
	 * @param {Object}
	 *            cfg
	 */
    this.submit = cfg => {
      var values = { ...this.state.values };
      if (this.componentWillPost) {
        /**
		 * componentWillPostResult = true || fase || {field_name : 'custom
		 * value'}
		 */
        var componentWillPostResult = this.componentWillPost(values, cfg || {});
        if (!componentWillPostResult) return false;
        values = { ...values, ...componentWillPostResult };
      }
      iwb.request({
        url:
          this.url +
          "?" +
          iwb.JSON2URI(this.params) +
          "_renderer=react16&.r=" +
          Math.random(),
        params: values,
        self: this,
        errorCallback: json => {
          var errors = {};
          if (json.errorType)
            switch (json.errorType) {
              case "validation":
                toastr.error("Validation Errors");
                if (json.errors && json.errors.length) {
                  json.errors.map(oneError => {
                    errors[oneError.id] = oneError.msg;
                  });
                }
                if (json.error) {
                  iwb.showModal({
                    title: "ERROR",
                    footer: false,
                    color: "danger",
                    size: "sm",
                    body: json.error
                  });
                }
                break;
              case "framework":
                if (json.error) {
                  iwb.showModal({
                    title: json.error,
                    footer: false,
                    color: "danger",
                    size: "lg",
                    body: _(Media, {
                        body: true
                      },
                      json.objectType && _(Media, {
                        heading: true
                      }, json.objectType),

                      _(ListGroup, {},
                        json.icodebetter && json.icodebetter.map((item, index) => {
                          return _(ListGroupItem, {},
                            _(ListGroupItemHeading, {},
                              item.errorType,
                              item && _(Button, {
                                  className: 'float-right btn btn-xs',
                                  color:'info',
                                  onClick: (e) => {
                                    e.preventDefault();
                                    iwb.copyToClipboard(item);
                                  }
                                },
                                _('i', {
                                  className: 'icon-docs'
                                }, '')
                              ),
                              item && _(Button, {
                                  className: 'float-right btn btn-xs',
                                  color:'primary',
                                  onClick: (e) => {
                                    e.preventDefault();
                                    iwb.log(item);
                                    toastr.success( "Use CTR + SHIFT + I to see the log content!", "Console Log", { timeOut: 3000 } );
                                  }
                                },
                                _('i', {
                                  className: 'icon-target'
                                }, '')
                              )
                            ),
                            _(ListGroupItemText, {},
                              item && _('pre', {}, window.JSON.stringify(item, null, 2))
                            )
                          )
                        })
                      )
                    )
                  });
                }
                break;
              default:
                alert(json.errorType);
            }
          else alert(json);
          this.setState({ errors });
          return false;
        },
        successCallback: (json, xcfg) => {
          if (cfg.callback) cfg.callback(json, xcfg);
        }
      });
    };
    /**
	 * used to make form active tab and visible on the page
	 * 
	 * @param {object}
	 *            tab
	 */
    this.toggleTab = tab => {
      if (this.state.activeTab !== tab) {
        this.setState({ activeTab: tab });
      }
    };
    /**
	 * returns form data from state
	 */
    this.getValues = () => {
      return { ...this.state.values };
    };
    /**
	 * used for date inputs
	 * 
	 * @param {String}
	 *            inputName
	 * @param {Boolean}
	 *            isItDTTM
	 */
    this.onDateChange = (inputName, isItDTTM) => {
      var self = this;
      return selectedDate => {
        var values = self.state.values;
        var dateValue = selectedDate && selectedDate._d;
        values[inputName] = isItDTTM
          ? fmtDateTime(dateValue)
          : fmtShortDate(dateValue);
        self.setState({ values });
      };
    };
  }
  componentDidMount() {
    var self = this;
    var triggers = this.triggerz4ComboRemotes;
    var { values } = this.state;
    for (var trigger in triggers) {
      if (values[trigger]) {
        triggers[trigger].map(zzz => {
          var nv = zzz.f(values[trigger], null, values);
          if (nv)
            iwb.request({
              url: "ajaxQueryData?" + iwb.JSON2URI(nv) + ".r=" + Math.random(),
              successCallback: ({ data }) => {
                var { options } = self.state;
                options[zzz.n] = data;
                self.setState({ options });
              }
            });
        });
      }
    }
  }
  componentWillUnmount() {
    iwb.forms[this._id] = { ...this.state };
  }
}

class XGraph extends React.Component {
  constructor(props) {
    super(props);
  }
  componentDidMount() {
    var dg = this.props.graph;
    var gid = "idG" + dg.graphId;
    iwb.graphAmchart(dg, gid);
  }
  render() {
    return _("div", {
      style: { width: "100%", height: this.props.props.height || "20vw" },
      id: "idG" + this.props.graph.graphId
    });
  }
}

iwb.createPortlet = function(o) {
  var name = o.graph || o.grid || o.card || o.query;
  if (!name) return _("div", null, "not portlet");
  if (o.query) {
    var q = o.query.data;
    if (!q || !q.length) return _("div", null, "not data");
    q = q[0];
    return _(
      Card,
      {
        className: "card-portlet text-white bg-" + (o.props.color || "primary")
      },
      _("i", { className: "big-icon " + (q.icon || "icon-settings") }),
      _(
        CardBlock,
        { className: "pb-0" },
        _(
          "div",
          {
            className: "float-right",
            style: {
              fontSize: "30px",
              background: "white",
              padding: "0 13px",
              borderRadius: "55px",
              color: "darkorange"
            }
          },
          q.xvalue
        ),
        _("h1", { className: "mb-0" }, q.dsc),
        _("div", { style: { height: "25px" } })
      )
    );
  }
  name = name.name;
  var cmp = null;
  if (o.graph) {
    return _(
      Card,
      {
        className:
          "card-portlet " + (o.props.color ? "bg-" + o.props.color : "")
      },
      _(
        "h3",
        {
          className: "form-header",
          style: {
            fontSize: "1.5rem",
            padding: "10px 12px 0px",
            marginBottom: ".5rem"
          }
        },
        name,
        _("i", { className: "portlet-refresh float-right icon-refresh" })
      ),
      _(XGraph, o)
    );
  } else if (o.grid) {
    o.grid.crudFlags = false;
    return _(
      Card,
      {
        className:
          "card-portlet " + (o.props.color ? "bg-" + o.props.color : "")
      },
      _(
        "h3",
        {
          className: "form-header",
          style: {
            fontSize: "1.5rem",
            padding: "10px 12px 0px",
            marginBottom: ".5rem"
          }
        },
        name,
        _("i", { className: "portlet-refresh float-right icon-refresh" })
      ),
      _(XGrid, o.grid)
    );
  } else if (o.card) cmp = "Card";
  else if (o.query) cmp = "KPI Card";
  return _(
    Card,
    {
      className: "card-portlet text-white bg-" + o.props.color || "primary"
    },
    _(
      CardBlock,
      { className: "card-body" },
      _(
        "h3",
        {
          className: "form-header",
          style: { padding: "10px 12px 0px", marginBottom: ".5rem" }
        },
        name
      ),
      _("hr"),
      cmp
    )
  );
};

iwb.ui.buildDashboard = function(o) {
  if (!o || !o.rows || !o.rows.length)
    return _("div", null, "No portlets defined");
  return o.rows.map((rowItem, rowIndex) => {
    return _(Row, {
      key: rowIndex,
      children: rowItem.map((colItem, colIndex) =>
        _(Col, colItem.props, iwb.createPortlet(colItem))
      )
    });
  });
};

iwb.ajax={}
iwb.ajax.query=function(qid,params,callback){
	iwb.request({url:'ajaxQueryData?_qid='+qid,params:params||{},successCallback:callback||false})
}
iwb.ajax.postForm=function(fid,action,params,callback){
	iwb.request({url:'ajaxPostForm?_fid='+fid+'&a='+action,params:params||{},successCallback:callback||false})
}
iwb.ajax.execFunc=function(did,params,callback){
	iwb.request({url:'ajaxExecDbFunc?_did='+did,params:params||{},successCallback:callback||false})
}