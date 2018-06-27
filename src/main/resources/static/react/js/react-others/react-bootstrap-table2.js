(function webpackUniversalModuleDefinition(root, factory) {
	if(typeof exports === 'object' && typeof module === 'object')
		module.exports = factory(require("react"));
	else if(typeof define === 'function' && define.amd)
		define(["react"], factory);
	else if(typeof exports === 'object')
		exports["ReactBootstrapTable"] = factory(require("react"));
	else
		root["ReactBootstrapTable"] = factory(root["React"]);
})(this, function(__WEBPACK_EXTERNAL_MODULE_0__) {
return /******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, {
/******/ 				configurable: false,
/******/ 				enumerable: true,
/******/ 				get: getter
/******/ 			});
/******/ 		}
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = 21);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ (function(module, exports) {

module.exports = __WEBPACK_EXTERNAL_MODULE_0__;

/***/ }),
/* 1 */
/***/ (function(module, exports, __webpack_require__) {

/**
 * Copyright 2013-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

if (false) {
  var REACT_ELEMENT_TYPE = (typeof Symbol === 'function' &&
    Symbol.for &&
    Symbol.for('react.element')) ||
    0xeac7;

  var isValidElement = function(object) {
    return typeof object === 'object' &&
      object !== null &&
      object.$$typeof === REACT_ELEMENT_TYPE;
  };

  // By explicitly using `prop-types` you are opting into new development behavior.
  // http://fb.me/prop-types-in-prod
  var throwOnDirectAccess = true;
  module.exports = require('./factoryWithTypeCheckers')(isValidElement, throwOnDirectAccess);
} else {
  // By explicitly using `prop-types` you are opting into new production behavior.
  // http://fb.me/prop-types-in-prod
  module.exports = __webpack_require__(4)();
}


/***/ }),
/* 2 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = {
  SORT_ASC: 'asc',
  SORT_DESC: 'desc',
  ROW_SELECT_SINGLE: 'radio',
  ROW_SELECT_MULTIPLE: 'checkbox',
  ROW_SELECT_DISABLED: 'ROW_SELECT_DISABLED',
  CHECKBOX_STATUS_CHECKED: 'checked',
  CHECKBOX_STATUS_INDETERMINATE: 'indeterminate',
  CHECKBOX_STATUS_UNCHECKED: 'unchecked'
};

/***/ }),
/* 3 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

/* eslint no-empty: 0 */
/* eslint no-param-reassign: 0 */
/* eslint prefer-rest-params: 0 */

function splitNested(str) {
  return [str].join('.').replace(/\[/g, '.').replace(/\]/g, '').split('.');
}

function get(target, field) {
  var pathArray = splitNested(field);
  var result = void 0;
  try {
    result = pathArray.reduce(function (curr, path) {
      return curr[path];
    }, target);
  } catch (e) {}
  return result;
}

function set(target, field, value) {
  var safe = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : false;

  var pathArray = splitNested(field);
  var level = 0;
  pathArray.reduce(function (a, b) {
    level += 1;
    if (typeof a[b] === 'undefined') {
      if (!safe) throw new Error(a + '.' + b + ' is undefined');
      a[b] = {};
      return a[b];
    }

    if (level === pathArray.length) {
      a[b] = value;
      return value;
    }
    return a[b];
  }, target);
}

function isFunction(obj) {
  return obj && typeof obj === 'function';
}

/**
 * Checks if `value` is the Object. the `Object` except `Function` and `Array.`
 *
 * @param {*} obj - The value gonna check
 */
function isObject(obj) {
  var type = typeof obj === 'undefined' ? 'undefined' : _typeof(obj);
  return obj !== null && type === 'object' && obj.constructor === Object;
}

function isEmptyObject(obj) {
  if (!isObject(obj)) return false;

  var hasOwnProperty = Object.prototype.hasOwnProperty;
  var keys = Object.keys(obj);

  for (var i = 0; i < keys.length; i += 1) {
    if (hasOwnProperty.call(obj, keys[i])) return false;
  }

  return true;
}

function isDefined(value) {
  return typeof value !== 'undefined' && value !== null;
}

function sleep(fn, ms) {
  return setTimeout(function () {
    return fn();
  }, ms);
}

function debounce(func, wait, immediate) {
  var _this = this,
      _arguments = arguments;

  var timeout = void 0;

  return function () {
    var later = function later() {
      timeout = null;

      if (!immediate) {
        func.apply(_this, _arguments);
      }
    };

    var callNow = immediate && !timeout;

    clearTimeout(timeout);
    timeout = setTimeout(later, wait || 0);

    if (callNow) {
      func.appy(_this, _arguments);
    }
  };
}

exports.default = {
  get: get,
  set: set,
  isFunction: isFunction,
  isObject: isObject,
  isEmptyObject: isEmptyObject,
  isDefined: isDefined,
  sleep: sleep,
  debounce: debounce
};

/***/ }),
/* 4 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/**
 * Copyright 2013-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */



var emptyFunction = __webpack_require__(5);
var invariant = __webpack_require__(6);
var ReactPropTypesSecret = __webpack_require__(7);

module.exports = function() {
  function shim(props, propName, componentName, location, propFullName, secret) {
    if (secret === ReactPropTypesSecret) {
      // It is still safe when called from React.
      return;
    }
    invariant(
      false,
      'Calling PropTypes validators directly is not supported by the `prop-types` package. ' +
      'Use PropTypes.checkPropTypes() to call them. ' +
      'Read more at http://fb.me/use-check-prop-types'
    );
  };
  shim.isRequired = shim;
  function getShim() {
    return shim;
  };
  // Important!
  // Keep this list in sync with production version in `./factoryWithTypeCheckers.js`.
  var ReactPropTypes = {
    array: shim,
    bool: shim,
    func: shim,
    number: shim,
    object: shim,
    string: shim,
    symbol: shim,

    any: shim,
    arrayOf: getShim,
    element: shim,
    instanceOf: getShim,
    node: shim,
    objectOf: getShim,
    oneOf: getShim,
    oneOfType: getShim,
    shape: getShim
  };

  ReactPropTypes.checkPropTypes = emptyFunction;
  ReactPropTypes.PropTypes = ReactPropTypes;

  return ReactPropTypes;
};


/***/ }),
/* 5 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


/**
 * Copyright (c) 2013-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * 
 */

function makeEmptyFunction(arg) {
  return function () {
    return arg;
  };
}

/**
 * This function accepts and discards inputs; it has no side effects. This is
 * primarily useful idiomatically for overridable function endpoints which
 * always need to be callable, since JS lacks a null-call idiom ala Cocoa.
 */
var emptyFunction = function emptyFunction() {};

emptyFunction.thatReturns = makeEmptyFunction;
emptyFunction.thatReturnsFalse = makeEmptyFunction(false);
emptyFunction.thatReturnsTrue = makeEmptyFunction(true);
emptyFunction.thatReturnsNull = makeEmptyFunction(null);
emptyFunction.thatReturnsThis = function () {
  return this;
};
emptyFunction.thatReturnsArgument = function (arg) {
  return arg;
};

module.exports = emptyFunction;

/***/ }),
/* 6 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/**
 * Copyright (c) 2013-present, Facebook, Inc.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 */



/**
 * Use invariant() to assert state which your program assumes to be true.
 *
 * Provide sprintf-style format (only %s is supported) and arguments
 * to provide information about what broke and what you were
 * expecting.
 *
 * The invariant message will be stripped in production, but the invariant
 * will remain to ensure logic does not differ in production.
 */

var validateFormat = function validateFormat(format) {};

if (false) {
  validateFormat = function validateFormat(format) {
    if (format === undefined) {
      throw new Error('invariant requires an error message argument');
    }
  };
}

function invariant(condition, format, a, b, c, d, e, f) {
  validateFormat(format);

  if (!condition) {
    var error;
    if (format === undefined) {
      error = new Error('Minified exception occurred; use the non-minified dev environment ' + 'for the full error message and additional helpful warnings.');
    } else {
      var args = [a, b, c, d, e, f];
      var argIndex = 0;
      error = new Error(format.replace(/%s/g, function () {
        return args[argIndex++];
      }));
      error.name = 'Invariant Violation';
    }

    error.framesToPop = 1; // we don't care about invariant's own frame
    throw error;
  }
}

module.exports = invariant;

/***/ }),
/* 7 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
/**
 * Copyright 2013-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */



var ReactPropTypesSecret = 'SECRET_DO_NOT_PASS_THIS_OR_YOU_WILL_BE_FIRED';

module.exports = ReactPropTypesSecret;


/***/ }),
/* 8 */,
/* 9 */,
/* 10 */,
/* 11 */,
/* 12 */
/***/ (function(module, exports, __webpack_require__) {

var __WEBPACK_AMD_DEFINE_ARRAY__, __WEBPACK_AMD_DEFINE_RESULT__;/*!
  Copyright (c) 2016 Jed Watson.
  Licensed under the MIT License (MIT), see
  http://jedwatson.github.io/classnames
*/
/* global define */

(function () {
	'use strict';

	var hasOwn = {}.hasOwnProperty;

	function classNames () {
		var classes = [];

		for (var i = 0; i < arguments.length; i++) {
			var arg = arguments[i];
			if (!arg) continue;

			var argType = typeof arg;

			if (argType === 'string' || argType === 'number') {
				classes.push(arg);
			} else if (Array.isArray(arg)) {
				classes.push(classNames.apply(null, arg));
			} else if (argType === 'object') {
				for (var key in arg) {
					if (hasOwn.call(arg, key) && arg[key]) {
						classes.push(key);
					}
				}
			}
		}

		return classes.join(' ');
	}

	if (typeof module !== 'undefined' && module.exports) {
		module.exports = classNames;
	} else if (true) {
		// register as 'classnames', consistent with npm package name
		!(__WEBPACK_AMD_DEFINE_ARRAY__ = [], __WEBPACK_AMD_DEFINE_RESULT__ = function () {
			return classNames;
		}.apply(exports, __WEBPACK_AMD_DEFINE_ARRAY__),
				__WEBPACK_AMD_DEFINE_RESULT__ !== undefined && (module.exports = __WEBPACK_AMD_DEFINE_RESULT__));
	} else {
		window.classNames = classNames;
	}
}());


/***/ }),
/* 13 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
var matchRow = exports.matchRow = function matchRow(keyField, id) {
  return function (row) {
    return row[keyField] === id;
  };
};

var getRowByRowId = exports.getRowByRowId = function getRowByRowId(_ref) {
  var data = _ref.data,
      keyField = _ref.keyField;
  return function (id) {
    return data.find(matchRow(keyField, id));
  };
};

/***/ }),
/* 14 */,
/* 15 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.getSelectedRows = exports.unSelectableKeys = exports.selectableKeys = exports.isAnySelectedRow = exports.isSelectedAll = undefined;

var _utils = __webpack_require__(3);

var _utils2 = _interopRequireDefault(_utils);

var _rows = __webpack_require__(13);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

var isSelectedAll = exports.isSelectedAll = function isSelectedAll(_ref) {
  var data = _ref.data,
      selected = _ref.selected;
  return data.length === selected.length;
};

var isAnySelectedRow = exports.isAnySelectedRow = function isAnySelectedRow(_ref2) {
  var selected = _ref2.selected;
  return function () {
    var skips = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : [];

    if (skips.length === 0) {
      return selected.length > 0;
    }
    return selected.filter(function (x) {
      return !skips.includes(x);
    }).length;
  };
};

var selectableKeys = exports.selectableKeys = function selectableKeys(_ref3) {
  var data = _ref3.data,
      keyField = _ref3.keyField;
  return function () {
    var skips = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : [];

    if (skips.length === 0) {
      return data.map(function (row) {
        return _utils2.default.get(row, keyField);
      });
    }
    return data.filter(function (row) {
      return !skips.includes(_utils2.default.get(row, keyField));
    }).map(function (row) {
      return _utils2.default.get(row, keyField);
    });
  };
};

var unSelectableKeys = exports.unSelectableKeys = function unSelectableKeys(_ref4) {
  var selected = _ref4.selected;
  return function () {
    var skips = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : [];

    if (skips.length === 0) {
      return [];
    }
    return selected.filter(function (x) {
      return skips.includes(x);
    });
  };
};

var getSelectedRows = exports.getSelectedRows = function getSelectedRows(store) {
  var getRow = (0, _rows.getRowByRowId)(store);
  return store.selected.map(function (k) {
    return getRow(k);
  });
};

/***/ }),
/* 16 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _utils = __webpack_require__(3);

var _utils2 = _interopRequireDefault(_utils);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

exports.default = function (ExtendBase) {
  return function (_ExtendBase) {
    _inherits(RemoteResolver, _ExtendBase);

    function RemoteResolver() {
      _classCallCheck(this, RemoteResolver);

      return _possibleConstructorReturn(this, (RemoteResolver.__proto__ || Object.getPrototypeOf(RemoteResolver)).apply(this, arguments));
    }

    _createClass(RemoteResolver, [{
      key: 'getNewestState',
      value: function getNewestState() {
        var state = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};

        var store = this.store || this.props.store;
        return _extends({
          page: store.page,
          sizePerPage: store.sizePerPage,
          filters: store.filters,
          sortField: store.sortField,
          sortOrder: store.sortOrder,
          data: store.getAllData()
        }, state);
      }
    }, {
      key: 'isRemotePagination',
      value: function isRemotePagination() {
        var remote = this.props.remote;

        return remote === true || _utils2.default.isObject(remote) && remote.pagination;
      }
    }, {
      key: 'isRemoteFiltering',
      value: function isRemoteFiltering() {
        var remote = this.props.remote;

        return remote === true || _utils2.default.isObject(remote) && remote.filter;
      }
    }, {
      key: 'isRemoteSort',
      value: function isRemoteSort() {
        var remote = this.props.remote;

        return remote === true || _utils2.default.isObject(remote) && remote.sort;
      }
    }, {
      key: 'isRemoteCellEdit',
      value: function isRemoteCellEdit() {
        var remote = this.props.remote;

        return remote === true || _utils2.default.isObject(remote) && remote.cellEdit;
      }
    }, {
      key: 'handleRemotePageChange',
      value: function handleRemotePageChange() {
        this.props.onTableChange('pagination', this.getNewestState());
      }
    }, {
      key: 'handleRemoteFilterChange',
      value: function handleRemoteFilterChange() {
        var newState = {};
        if (this.isRemotePagination()) {
          var options = this.props.pagination.options || {};
          newState.page = _utils2.default.isDefined(options.pageStartIndex) ? options.pageStartIndex : 1;
        }
        this.props.onTableChange('filter', this.getNewestState(newState));
      }
    }, {
      key: 'handleSortChange',
      value: function handleSortChange() {
        this.props.onTableChange('sort', this.getNewestState());
      }
    }, {
      key: 'handleCellChange',
      value: function handleCellChange(rowId, dataField, newValue) {
        var cellEdit = { rowId: rowId, dataField: dataField, newValue: newValue };
        this.props.onTableChange('cellEdit', this.getNewestState({ cellEdit: cellEdit }));
      }
    }]);

    return RemoteResolver;
  }(ExtendBase);
};

/***/ }),
/* 17 */,
/* 18 */,
/* 19 */,
/* 20 */,
/* 21 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _bootstrapTable = __webpack_require__(22);

var _bootstrapTable2 = _interopRequireDefault(_bootstrapTable);

var _container = __webpack_require__(37);

var _container2 = _interopRequireDefault(_container);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

exports.default = (0, _container2.default)(_bootstrapTable2.default);

/***/ }),
/* 22 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _react = __webpack_require__(0);

var _react2 = _interopRequireDefault(_react);

var _propTypes = __webpack_require__(1);

var _propTypes2 = _interopRequireDefault(_propTypes);

var _classnames = __webpack_require__(12);

var _classnames2 = _interopRequireDefault(_classnames);

var _header = __webpack_require__(23);

var _header2 = _interopRequireDefault(_header);

var _caption = __webpack_require__(28);

var _caption2 = _interopRequireDefault(_caption);

var _body = __webpack_require__(29);

var _body2 = _interopRequireDefault(_body);

var _propsResolver = __webpack_require__(35);

var _propsResolver2 = _interopRequireDefault(_propsResolver);

var _const = __webpack_require__(2);

var _const2 = _interopRequireDefault(_const);

var _selection = __webpack_require__(15);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; } /* eslint arrow-body-style: 0 */

var BootstrapTable = function (_PropsBaseResolver) {
  _inherits(BootstrapTable, _PropsBaseResolver);

  function BootstrapTable(props) {
    _classCallCheck(this, BootstrapTable);

    var _this = _possibleConstructorReturn(this, (BootstrapTable.__proto__ || Object.getPrototypeOf(BootstrapTable)).call(this, props));

    _this.validateProps();

    _this.state = {
      data: props.data
    };
    return _this;
  }

  _createClass(BootstrapTable, [{
    key: 'componentWillReceiveProps',
    value: function componentWillReceiveProps(nextProps) {
      this.setState({
        data: nextProps.data
      });
    }
  }, {
    key: 'render',
    value: function render() {
      var _props = this.props,
          loading = _props.loading,
          overlay = _props.overlay;

      var table = this.renderTable();
      if (loading && overlay) {
        var LoadingOverlay = overlay(table, loading);
        return _react2.default.createElement(LoadingOverlay, null);
      }
      return table;
    }
  }, {
    key: 'renderTable',
    value: function renderTable() {
      var _props2 = this.props,
          store = _props2.store,
          columns = _props2.columns,
          keyField = _props2.keyField,
          striped = _props2.striped,
          hover = _props2.hover,
          bordered = _props2.bordered,
          condensed = _props2.condensed,
          noDataIndication = _props2.noDataIndication,
          caption = _props2.caption,
          rowStyle = _props2.rowStyle,
          rowClasses = _props2.rowClasses,
          rowEvents = _props2.rowEvents;


      var tableClass = (0, _classnames2.default)('table', {
        'table-striped': striped,
        'table-hover': hover,
        'table-bordered': bordered,
        'table-condensed': condensed
      });

      var cellSelectionInfo = this.resolveSelectRowProps({
        onRowSelect: this.props.onRowSelect
      });

      var headerCellSelectionInfo = this.resolveSelectRowPropsForHeader({
        onAllRowsSelect: this.props.onAllRowsSelect,
        selected: store.selected,
        allRowsSelected: (0, _selection.isSelectedAll)(store)
      });

      var tableCaption = caption && _react2.default.createElement(
        _caption2.default,
        null,
        caption
      );

      return _react2.default.createElement(
        'div',
        { className: 'react-bootstrap-table' },
        _react2.default.createElement(
          'table',
          { className: tableClass },
          tableCaption,
          _react2.default.createElement(_header2.default, {
            columns: columns,
            sortField: store.sortField,
            sortOrder: store.sortOrder,
            onSort: this.props.onSort,
            onFilter: this.props.onFilter,
            selectRow: headerCellSelectionInfo
          }),
          _react2.default.createElement(_body2.default, {
            data: this.state.data,
            keyField: keyField,
            columns: columns,
            isEmpty: this.isEmpty(),
            visibleColumnSize: this.visibleColumnSize(),
            noDataIndication: noDataIndication,
            cellEdit: this.props.cellEdit || {},
            selectRow: cellSelectionInfo,
            selectedRowKeys: store.selected,
            rowStyle: rowStyle,
            rowClasses: rowClasses,
            rowEvents: rowEvents
          })
        )
      );
    }
  }]);

  return BootstrapTable;
}((0, _propsResolver2.default)(_react.Component));

BootstrapTable.propTypes = {
  keyField: _propTypes2.default.string.isRequired,
  data: _propTypes2.default.array.isRequired,
  columns: _propTypes2.default.array.isRequired,
  remote: _propTypes2.default.oneOfType([_propTypes2.default.bool, _propTypes2.default.shape({
    pagination: _propTypes2.default.bool
  })]),
  store: _propTypes2.default.object,
  noDataIndication: _propTypes2.default.oneOfType([_propTypes2.default.string, _propTypes2.default.func]),
  striped: _propTypes2.default.bool,
  bordered: _propTypes2.default.bool,
  hover: _propTypes2.default.bool,
  condensed: _propTypes2.default.bool,
  caption: _propTypes2.default.oneOfType([_propTypes2.default.node, _propTypes2.default.string]),
  pagination: _propTypes2.default.object,
  filter: _propTypes2.default.object,
  cellEdit: _propTypes2.default.object,
  selectRow: _propTypes2.default.shape({
    mode: _propTypes2.default.oneOf([_const2.default.ROW_SELECT_SINGLE, _const2.default.ROW_SELECT_MULTIPLE]).isRequired,
    clickToSelect: _propTypes2.default.bool,
    clickToEdit: _propTypes2.default.bool,
    onSelect: _propTypes2.default.func,
    onSelectAll: _propTypes2.default.func,
    style: _propTypes2.default.oneOfType([_propTypes2.default.object, _propTypes2.default.func]),
    classes: _propTypes2.default.oneOfType([_propTypes2.default.string, _propTypes2.default.func]),
    nonSelectable: _propTypes2.default.array,
    bgColor: _propTypes2.default.oneOfType([_propTypes2.default.string, _propTypes2.default.func]),
    hideSelectColumn: _propTypes2.default.bool
  }),
  onRowSelect: _propTypes2.default.func,
  onAllRowsSelect: _propTypes2.default.func,
  rowStyle: _propTypes2.default.oneOfType([_propTypes2.default.object, _propTypes2.default.func]),
  rowEvents: _propTypes2.default.object,
  rowClasses: _propTypes2.default.oneOfType([_propTypes2.default.string, _propTypes2.default.func]),
  defaultSorted: _propTypes2.default.arrayOf(_propTypes2.default.shape({
    dataField: _propTypes2.default.string.isRequired,
    order: _propTypes2.default.oneOf([_const2.default.SORT_DESC, _const2.default.SORT_ASC]).isRequired
  })),
  overlay: _propTypes2.default.func,
  onTableChange: _propTypes2.default.func,
  onSort: _propTypes2.default.func,
  onFilter: _propTypes2.default.func
};

BootstrapTable.defaultProps = {
  remote: false,
  striped: false,
  bordered: true,
  hover: false,
  condensed: false,
  noDataIndication: null
};

exports.default = BootstrapTable;

/***/ }),
/* 23 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _react = __webpack_require__(0);

var _react2 = _interopRequireDefault(_react);

var _propTypes = __webpack_require__(1);

var _propTypes2 = _interopRequireDefault(_propTypes);

var _const = __webpack_require__(2);

var _const2 = _interopRequireDefault(_const);

var _headerCell = __webpack_require__(24);

var _headerCell2 = _interopRequireDefault(_headerCell);

var _selectionHeaderCell = __webpack_require__(27);

var _selectionHeaderCell2 = _interopRequireDefault(_selectionHeaderCell);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

var Header = function Header(props) {
  var ROW_SELECT_DISABLED = _const2.default.ROW_SELECT_DISABLED;
  var columns = props.columns,
      onSort = props.onSort,
      onFilter = props.onFilter,
      sortField = props.sortField,
      sortOrder = props.sortOrder,
      selectRow = props.selectRow;


  return _react2.default.createElement(
    'thead',
    null,
    _react2.default.createElement(
      'tr',
      null,
      selectRow.mode !== ROW_SELECT_DISABLED && !selectRow.hideSelectColumn ? _react2.default.createElement(_selectionHeaderCell2.default, selectRow) : null,
      columns.map(function (column, i) {
        var currSort = column.dataField === sortField;
        var isLastSorting = column.dataField === sortField;

        return _react2.default.createElement(_headerCell2.default, {
          index: i,
          key: column.dataField,
          column: column,
          onSort: onSort,
          sorting: currSort,
          onFilter: onFilter,
          sortOrder: sortOrder,
          isLastSorting: isLastSorting
        });
      })
    )
  );
}; /* eslint react/require-default-props: 0 */


Header.propTypes = {
  columns: _propTypes2.default.array.isRequired,
  onSort: _propTypes2.default.func,
  onFilter: _propTypes2.default.func,
  sortField: _propTypes2.default.string,
  sortOrder: _propTypes2.default.string,
  selectRow: _propTypes2.default.object
};

exports.default = Header;

/***/ }),
/* 24 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; /* eslint react/require-default-props: 0 */


var _react = __webpack_require__(0);

var _react2 = _interopRequireDefault(_react);

var _classnames = __webpack_require__(12);

var _classnames2 = _interopRequireDefault(_classnames);

var _propTypes = __webpack_require__(1);

var _propTypes2 = _interopRequireDefault(_propTypes);

var _const = __webpack_require__(2);

var _const2 = _interopRequireDefault(_const);

var _symbol = __webpack_require__(25);

var _symbol2 = _interopRequireDefault(_symbol);

var _caret = __webpack_require__(26);

var _caret2 = _interopRequireDefault(_caret);

var _utils = __webpack_require__(3);

var _utils2 = _interopRequireDefault(_utils);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

var HeaderCell = function HeaderCell(props) {
  var column = props.column,
      index = props.index,
      onSort = props.onSort,
      sorting = props.sorting,
      sortOrder = props.sortOrder,
      isLastSorting = props.isLastSorting,
      onFilter = props.onFilter;
  var text = column.text,
      sort = column.sort,
      filter = column.filter,
      hidden = column.hidden,
      headerTitle = column.headerTitle,
      headerAlign = column.headerAlign,
      headerFormatter = column.headerFormatter,
      headerEvents = column.headerEvents,
      headerClasses = column.headerClasses,
      headerStyle = column.headerStyle,
      headerAttrs = column.headerAttrs,
      headerSortingClasses = column.headerSortingClasses,
      headerSortingStyle = column.headerSortingStyle;


  var cellAttrs = _extends({}, _utils2.default.isFunction(headerAttrs) ? headerAttrs(column, index) : headerAttrs, headerEvents);

  var sortSymbol = void 0;
  var filterElm = void 0;
  var cellStyle = {};
  var cellClasses = _utils2.default.isFunction(headerClasses) ? headerClasses(column, index) : headerClasses;

  if (headerStyle) {
    cellStyle = _utils2.default.isFunction(headerStyle) ? headerStyle(column, index) : headerStyle;
  }

  if (headerTitle) {
    cellAttrs.title = _utils2.default.isFunction(headerTitle) ? headerTitle(column, index) : text;
  }

  if (headerAlign) {
    cellStyle.textAlign = _utils2.default.isFunction(headerAlign) ? headerAlign(column, index) : headerAlign;
  }

  if (hidden) {
    cellStyle.display = 'none';
  }

  if (sort) {
    var customClick = cellAttrs.onClick;
    cellAttrs.onClick = function (e) {
      onSort(column);
      if (_utils2.default.isFunction(customClick)) customClick(e);
    };
    cellAttrs.className = (0, _classnames2.default)(cellAttrs.className, 'sortable');

    if (sorting) {
      sortSymbol = _react2.default.createElement(_caret2.default, { order: sortOrder });

      // append customized classes or style if table was sorting based on the current column.
      cellClasses = (0, _classnames2.default)(cellClasses, _utils2.default.isFunction(headerSortingClasses) ? headerSortingClasses(column, sortOrder, isLastSorting, index) : headerSortingClasses);

      cellStyle = _extends({}, cellStyle, _utils2.default.isFunction(headerSortingStyle) ? headerSortingStyle(column, sortOrder, isLastSorting, index) : headerSortingStyle);
    } else {
      sortSymbol = _react2.default.createElement(_symbol2.default, null);
    }
  }

  if (cellClasses) cellAttrs.className = (0, _classnames2.default)(cellAttrs.className, cellClasses);
  if (!_utils2.default.isEmptyObject(cellStyle)) cellAttrs.style = cellStyle;
  if (filter) {
    filterElm = _react2.default.createElement(filter.Filter, _extends({}, filter.props, { onFilter: onFilter, column: column }));
  }

  var children = headerFormatter ? headerFormatter(column, index, { sortElement: sortSymbol, filterElement: filterElm }) : text;

  if (headerFormatter) {
    return _react2.default.createElement('th', cellAttrs, children);
  }

  return _react2.default.createElement('th', cellAttrs, children, sortSymbol, filterElm);
};

HeaderCell.propTypes = {
  column: _propTypes2.default.shape({
    dataField: _propTypes2.default.string.isRequired,
    text: _propTypes2.default.string.isRequired,
    hidden: _propTypes2.default.bool,
    headerFormatter: _propTypes2.default.func,
    formatter: _propTypes2.default.func,
    formatExtraData: _propTypes2.default.any,
    headerClasses: _propTypes2.default.oneOfType([_propTypes2.default.string, _propTypes2.default.func]),
    classes: _propTypes2.default.oneOfType([_propTypes2.default.string, _propTypes2.default.func]),
    headerStyle: _propTypes2.default.oneOfType([_propTypes2.default.object, _propTypes2.default.func]),
    style: _propTypes2.default.oneOfType([_propTypes2.default.object, _propTypes2.default.func]),
    headerTitle: _propTypes2.default.oneOfType([_propTypes2.default.bool, _propTypes2.default.func]),
    title: _propTypes2.default.oneOfType([_propTypes2.default.bool, _propTypes2.default.func]),
    headerEvents: _propTypes2.default.object,
    events: _propTypes2.default.object,
    headerAlign: _propTypes2.default.oneOfType([_propTypes2.default.string, _propTypes2.default.func]),
    align: _propTypes2.default.oneOfType([_propTypes2.default.string, _propTypes2.default.func]),
    headerAttrs: _propTypes2.default.oneOfType([_propTypes2.default.object, _propTypes2.default.func]),
    attrs: _propTypes2.default.oneOfType([_propTypes2.default.object, _propTypes2.default.func]),
    sort: _propTypes2.default.bool,
    sortFunc: _propTypes2.default.func,
    onSort: _propTypes2.default.func,
    editable: _propTypes2.default.oneOfType([_propTypes2.default.bool, _propTypes2.default.func]),
    editCellStyle: _propTypes2.default.oneOfType([_propTypes2.default.object, _propTypes2.default.func]),
    editCellClasses: _propTypes2.default.oneOfType([_propTypes2.default.string, _propTypes2.default.func]),
    validator: _propTypes2.default.func,
    filter: _propTypes2.default.object,
    filterValue: _propTypes2.default.func
  }).isRequired,
  index: _propTypes2.default.number.isRequired,
  onSort: _propTypes2.default.func,
  sorting: _propTypes2.default.bool,
  sortOrder: _propTypes2.default.oneOf([_const2.default.SORT_ASC, _const2.default.SORT_DESC]),
  isLastSorting: _propTypes2.default.bool,
  onFilter: _propTypes2.default.func
};

exports.default = HeaderCell;

/***/ }),
/* 25 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _react = __webpack_require__(0);

var _react2 = _interopRequireDefault(_react);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

var SortSymbol = function SortSymbol() {
  return _react2.default.createElement(
    "span",
    { className: "order" },
    _react2.default.createElement(
      "span",
      { className: "dropdown" },
      _react2.default.createElement("span", { className: "caret" })
    ),
    _react2.default.createElement(
      "span",
      { className: "dropup" },
      _react2.default.createElement("span", { className: "caret" })
    )
  );
};

exports.default = SortSymbol;

/***/ }),
/* 26 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _react = __webpack_require__(0);

var _react2 = _interopRequireDefault(_react);

var _classnames = __webpack_require__(12);

var _classnames2 = _interopRequireDefault(_classnames);

var _propTypes = __webpack_require__(1);

var _propTypes2 = _interopRequireDefault(_propTypes);

var _const = __webpack_require__(2);

var _const2 = _interopRequireDefault(_const);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

var SortCaret = function SortCaret(_ref) {
  var order = _ref.order;

  var orderClass = (0, _classnames2.default)('react-bootstrap-table-sort-order', {
    dropup: order === _const2.default.SORT_ASC
  });
  return _react2.default.createElement(
    'span',
    { className: orderClass },
    _react2.default.createElement('span', { className: 'caret' })
  );
};

SortCaret.propTypes = {
  order: _propTypes2.default.oneOf([_const2.default.SORT_ASC, _const2.default.SORT_DESC]).isRequired
};
exports.default = SortCaret;

/***/ }),
/* 27 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.CheckBox = undefined;

var _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _react = __webpack_require__(0);

var _react2 = _interopRequireDefault(_react);

var _propTypes = __webpack_require__(1);

var _propTypes2 = _interopRequireDefault(_propTypes);

var _const = __webpack_require__(2);

var _const2 = _interopRequireDefault(_const);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; } /* eslint react/require-default-props: 0 */


var CheckBox = exports.CheckBox = function CheckBox(_ref) {
  var checked = _ref.checked,
      indeterminate = _ref.indeterminate;
  return _react2.default.createElement('input', {
    type: 'checkbox',
    checked: checked,
    ref: function ref(input) {
      if (input) input.indeterminate = indeterminate; // eslint-disable-line no-param-reassign
    }
  });
};

CheckBox.propTypes = {
  checked: _propTypes2.default.bool.isRequired,
  indeterminate: _propTypes2.default.bool.isRequired
};

var SelectionHeaderCell = function (_Component) {
  _inherits(SelectionHeaderCell, _Component);

  function SelectionHeaderCell() {
    _classCallCheck(this, SelectionHeaderCell);

    var _this = _possibleConstructorReturn(this, (SelectionHeaderCell.__proto__ || Object.getPrototypeOf(SelectionHeaderCell)).call(this));

    _this.handleCheckBoxClick = _this.handleCheckBoxClick.bind(_this);
    return _this;
  }

  /**
   * avoid updating if button is
   * 1. radio
   * 2. status was not changed.
   */


  _createClass(SelectionHeaderCell, [{
    key: 'shouldComponentUpdate',
    value: function shouldComponentUpdate(nextProps) {
      var ROW_SELECT_SINGLE = _const2.default.ROW_SELECT_SINGLE;
      var _props = this.props,
          mode = _props.mode,
          checkedStatus = _props.checkedStatus;


      if (mode === ROW_SELECT_SINGLE) return false;

      return nextProps.checkedStatus !== checkedStatus;
    }
  }, {
    key: 'handleCheckBoxClick',
    value: function handleCheckBoxClick() {
      var onAllRowsSelect = this.props.onAllRowsSelect;


      onAllRowsSelect();
    }
  }, {
    key: 'render',
    value: function render() {
      var CHECKBOX_STATUS_CHECKED = _const2.default.CHECKBOX_STATUS_CHECKED,
          CHECKBOX_STATUS_INDETERMINATE = _const2.default.CHECKBOX_STATUS_INDETERMINATE,
          ROW_SELECT_SINGLE = _const2.default.ROW_SELECT_SINGLE;
      var _props2 = this.props,
          mode = _props2.mode,
          checkedStatus = _props2.checkedStatus;


      var checked = checkedStatus === CHECKBOX_STATUS_CHECKED;

      var indeterminate = checkedStatus === CHECKBOX_STATUS_INDETERMINATE;

      return mode === ROW_SELECT_SINGLE ? _react2.default.createElement('th', { 'data-row-selection': true }) : _react2.default.createElement(
        'th',
        { 'data-row-selection': true, onClick: this.handleCheckBoxClick },
        _react2.default.createElement(CheckBox, _extends({}, this.props, {
          checked: checked,
          indeterminate: indeterminate
        }))
      );
    }
  }]);

  return SelectionHeaderCell;
}(_react.Component);

SelectionHeaderCell.propTypes = {
  mode: _propTypes2.default.string.isRequired,
  checkedStatus: _propTypes2.default.string,
  onAllRowsSelect: _propTypes2.default.func
};
exports.default = SelectionHeaderCell;

/***/ }),
/* 28 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _react = __webpack_require__(0);

var _react2 = _interopRequireDefault(_react);

var _propTypes = __webpack_require__(1);

var _propTypes2 = _interopRequireDefault(_propTypes);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

/* eslint react/require-default-props: 0 */
var Caption = function Caption(props) {
  if (!props.children) return null;
  return _react2.default.createElement(
    'caption',
    null,
    props.children
  );
};

Caption.propTypes = {
  children: _propTypes2.default.oneOfType([_propTypes2.default.node, _propTypes2.default.string])
};

exports.default = Caption;

/***/ }),
/* 29 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; /* eslint react/prop-types: 0 */
/* eslint react/require-default-props: 0 */

var _react = __webpack_require__(0);

var _react2 = _interopRequireDefault(_react);

var _propTypes = __webpack_require__(1);

var _propTypes2 = _interopRequireDefault(_propTypes);

var _classnames = __webpack_require__(12);

var _classnames2 = _interopRequireDefault(_classnames);

var _utils = __webpack_require__(3);

var _utils2 = _interopRequireDefault(_utils);

var _row = __webpack_require__(30);

var _row2 = _interopRequireDefault(_row);

var _rowSection = __webpack_require__(34);

var _rowSection2 = _interopRequireDefault(_rowSection);

var _const = __webpack_require__(2);

var _const2 = _interopRequireDefault(_const);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

var Body = function Body(props) {
  var columns = props.columns,
      data = props.data,
      keyField = props.keyField,
      isEmpty = props.isEmpty,
      noDataIndication = props.noDataIndication,
      visibleColumnSize = props.visibleColumnSize,
      cellEdit = props.cellEdit,
      selectRow = props.selectRow,
      selectedRowKeys = props.selectedRowKeys,
      rowStyle = props.rowStyle,
      rowClasses = props.rowClasses,
      rowEvents = props.rowEvents;
  var bgColor = selectRow.bgColor,
      nonSelectable = selectRow.nonSelectable;


  var content = void 0;

  if (isEmpty) {
    var indication = _utils2.default.isFunction(noDataIndication) ? noDataIndication() : noDataIndication;
    content = _react2.default.createElement(_rowSection2.default, { content: indication, colSpan: visibleColumnSize });
  } else {
    var nonEditableRows = cellEdit.nonEditableRows || [];
    content = data.map(function (row, index) {
      var key = _utils2.default.get(row, keyField);
      var editable = !(nonEditableRows.length > 0 && nonEditableRows.indexOf(key) > -1);

      var selected = selectRow.mode !== _const2.default.ROW_SELECT_DISABLED ? selectedRowKeys.includes(key) : null;

      var attrs = rowEvents || {};
      var style = _utils2.default.isFunction(rowStyle) ? rowStyle(row, index) : rowStyle;
      var classes = _utils2.default.isFunction(rowClasses) ? rowClasses(row, index) : rowClasses;
      if (selected) {
        var selectedStyle = _utils2.default.isFunction(selectRow.style) ? selectRow.style(row, index) : selectRow.style;

        var selectedClasses = _utils2.default.isFunction(selectRow.classes) ? selectRow.classes(row, index) : selectRow.classes;

        style = _extends({}, style, selectedStyle);
        classes = (0, _classnames2.default)(classes, selectedClasses);

        if (bgColor) {
          style = style || {};
          style.backgroundColor = _utils2.default.isFunction(bgColor) ? bgColor(row, index) : bgColor;
        }
      }

      var selectable = !nonSelectable || !nonSelectable.includes(key);

      return _react2.default.createElement(_row2.default, {
        key: key,
        row: row,
        keyField: keyField,
        rowIndex: index,
        columns: columns,
        cellEdit: cellEdit,
        editable: editable,
        selectable: selectable,
        selected: selected,
        selectRow: selectRow,
        style: style,
        className: classes,
        attrs: attrs
      });
    });
  }

  return _react2.default.createElement(
    'tbody',
    null,
    content
  );
};

Body.propTypes = {
  keyField: _propTypes2.default.string.isRequired,
  data: _propTypes2.default.array.isRequired,
  columns: _propTypes2.default.array.isRequired,
  selectRow: _propTypes2.default.object,
  selectedRowKeys: _propTypes2.default.array
};

exports.default = Body;

/***/ }),
/* 30 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _react = __webpack_require__(0);

var _react2 = _interopRequireDefault(_react);

var _propTypes = __webpack_require__(1);

var _propTypes2 = _interopRequireDefault(_propTypes);

var _utils = __webpack_require__(3);

var _utils2 = _interopRequireDefault(_utils);

var _cell = __webpack_require__(31);

var _cell2 = _interopRequireDefault(_cell);

var _selectionCell = __webpack_require__(32);

var _selectionCell2 = _interopRequireDefault(_selectionCell);

var _rowEventDelegater = __webpack_require__(33);

var _rowEventDelegater2 = _interopRequireDefault(_rowEventDelegater);

var _const = __webpack_require__(2);

var _const2 = _interopRequireDefault(_const);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _objectWithoutProperties(obj, keys) { var target = {}; for (var i in obj) { if (keys.indexOf(i) >= 0) continue; if (!Object.prototype.hasOwnProperty.call(obj, i)) continue; target[i] = obj[i]; } return target; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; } /* eslint react/prop-types: 0 */
/* eslint react/no-array-index-key: 0 */


var Row = function (_eventDelegater) {
  _inherits(Row, _eventDelegater);

  function Row() {
    _classCallCheck(this, Row);

    return _possibleConstructorReturn(this, (Row.__proto__ || Object.getPrototypeOf(Row)).apply(this, arguments));
  }

  _createClass(Row, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          row = _props.row,
          columns = _props.columns,
          keyField = _props.keyField,
          rowIndex = _props.rowIndex,
          className = _props.className,
          style = _props.style,
          attrs = _props.attrs,
          cellEdit = _props.cellEdit,
          selected = _props.selected,
          selectRow = _props.selectRow,
          selectable = _props.selectable,
          editableRow = _props.editable;

      var mode = cellEdit.mode,
          onStart = cellEdit.onStart,
          EditingCell = cellEdit.EditingCell,
          editingRowIdx = cellEdit.ridx,
          editingColIdx = cellEdit.cidx,
          CLICK_TO_CELL_EDIT = cellEdit.CLICK_TO_CELL_EDIT,
          DBCLICK_TO_CELL_EDIT = cellEdit.DBCLICK_TO_CELL_EDIT,
          rest = _objectWithoutProperties(cellEdit, ['mode', 'onStart', 'EditingCell', 'ridx', 'cidx', 'CLICK_TO_CELL_EDIT', 'DBCLICK_TO_CELL_EDIT']);

      var key = _utils2.default.get(row, keyField);
      var hideSelectColumn = selectRow.hideSelectColumn;

      var trAttrs = this.delegate(attrs);

      return _react2.default.createElement(
        'tr',
        _extends({ style: style, className: className }, trAttrs),
        selectRow.mode !== _const2.default.ROW_SELECT_DISABLED && !hideSelectColumn ? _react2.default.createElement(_selectionCell2.default, _extends({}, selectRow, {
          rowKey: key,
          rowIndex: rowIndex,
          selected: selected,
          disabled: !selectable
        })) : null,
        columns.map(function (column, index) {
          var dataField = column.dataField;

          var content = _utils2.default.get(row, dataField);
          var editable = _utils2.default.isDefined(column.editable) ? column.editable : true;
          if (dataField === keyField || !editableRow) editable = false;
          if (_utils2.default.isFunction(column.editable)) {
            editable = column.editable(content, row, rowIndex, index);
          }
          if (rowIndex === editingRowIdx && index === editingColIdx) {
            var editCellstyle = column.editCellStyle || {};
            var editCellclasses = column.editCellClasses;
            if (_utils2.default.isFunction(column.editCellStyle)) {
              editCellstyle = column.editCellStyle(content, row, rowIndex, index);
            }
            if (_utils2.default.isFunction(column.editCellClasses)) {
              editCellclasses = column.editCellClasses(content, row, rowIndex, index);
            }
            return _react2.default.createElement(EditingCell, _extends({
              key: content + '-' + index,
              row: row,
              column: column,
              className: editCellclasses,
              style: editCellstyle
            }, rest));
          }
          return _react2.default.createElement(_cell2.default, {
            key: content + '-' + index,
            row: row,
            rowIndex: rowIndex,
            columnIndex: index,
            column: column,
            onStart: onStart,
            editable: editable,
            clickToEdit: mode === CLICK_TO_CELL_EDIT,
            dbclickToEdit: mode === DBCLICK_TO_CELL_EDIT
          });
        })
      );
    }
  }]);

  return Row;
}((0, _rowEventDelegater2.default)(_react.Component));

Row.propTypes = {
  row: _propTypes2.default.object.isRequired,
  rowIndex: _propTypes2.default.number.isRequired,
  columns: _propTypes2.default.array.isRequired,
  style: _propTypes2.default.object,
  className: _propTypes2.default.string,
  attrs: _propTypes2.default.object
};

Row.defaultProps = {
  editable: true,
  style: {},
  className: null,
  attrs: {}
};

exports.default = Row;

/***/ }),
/* 31 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _react = __webpack_require__(0);

var _react2 = _interopRequireDefault(_react);

var _propTypes = __webpack_require__(1);

var _propTypes2 = _interopRequireDefault(_propTypes);

var _utils = __webpack_require__(3);

var _utils2 = _interopRequireDefault(_utils);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; } /* eslint react/prop-types: 0 */


var Cell = function (_Component) {
  _inherits(Cell, _Component);

  function Cell(props) {
    _classCallCheck(this, Cell);

    var _this = _possibleConstructorReturn(this, (Cell.__proto__ || Object.getPrototypeOf(Cell)).call(this, props));

    _this.handleEditingCell = _this.handleEditingCell.bind(_this);
    return _this;
  }

  _createClass(Cell, [{
    key: 'handleEditingCell',
    value: function handleEditingCell(e) {
      var _props = this.props,
          column = _props.column,
          onStart = _props.onStart,
          rowIndex = _props.rowIndex,
          columnIndex = _props.columnIndex,
          clickToEdit = _props.clickToEdit,
          dbclickToEdit = _props.dbclickToEdit;
      var events = column.events;

      if (events) {
        if (clickToEdit) {
          var customClick = events.onClick;
          if (_utils2.default.isFunction(customClick)) customClick(e);
        } else if (dbclickToEdit) {
          var customDbClick = events.onDoubleClick;
          if (_utils2.default.isFunction(customDbClick)) customDbClick(e);
        }
      }
      if (onStart) {
        onStart(rowIndex, columnIndex);
      }
    }
  }, {
    key: 'render',
    value: function render() {
      var _props2 = this.props,
          row = _props2.row,
          rowIndex = _props2.rowIndex,
          column = _props2.column,
          columnIndex = _props2.columnIndex,
          editable = _props2.editable,
          clickToEdit = _props2.clickToEdit,
          dbclickToEdit = _props2.dbclickToEdit;
      var dataField = column.dataField,
          hidden = column.hidden,
          formatter = column.formatter,
          formatExtraData = column.formatExtraData,
          style = column.style,
          classes = column.classes,
          title = column.title,
          events = column.events,
          align = column.align,
          attrs = column.attrs;

      var cellTitle = void 0;
      var cellStyle = {};
      var content = _utils2.default.get(row, dataField);

      var cellAttrs = _extends({}, _utils2.default.isFunction(attrs) ? attrs(content, row, rowIndex, columnIndex) : attrs, events);

      var cellClasses = _utils2.default.isFunction(classes) ? classes(content, row, rowIndex, columnIndex) : classes;

      if (style) {
        cellStyle = _utils2.default.isFunction(style) ? style(content, row, rowIndex, columnIndex) : style;
      }

      if (title) {
        cellTitle = _utils2.default.isFunction(title) ? title(content, row, rowIndex, columnIndex) : content;
        cellAttrs.title = cellTitle;
      }

      if (formatter) {
        content = column.formatter(content, row, rowIndex, formatExtraData);
      }

      if (align) {
        cellStyle.textAlign = _utils2.default.isFunction(align) ? align(content, row, rowIndex, columnIndex) : align;
      }

      if (hidden) {
        cellStyle.display = 'none';
      }

      if (cellClasses) cellAttrs.className = cellClasses;

      if (!_utils2.default.isEmptyObject(cellStyle)) cellAttrs.style = cellStyle;
      if (clickToEdit && editable) {
        cellAttrs.onClick = this.handleEditingCell;
      } else if (dbclickToEdit && editable) {
        cellAttrs.onDoubleClick = this.handleEditingCell;
      }
      return _react2.default.createElement(
        'td',
        cellAttrs,
        content
      );
    }
  }]);

  return Cell;
}(_react.Component);

Cell.propTypes = {
  row: _propTypes2.default.object.isRequired,
  rowIndex: _propTypes2.default.number.isRequired,
  column: _propTypes2.default.object.isRequired,
  columnIndex: _propTypes2.default.number.isRequired
};

exports.default = Cell;

/***/ }),
/* 32 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _react = __webpack_require__(0);

var _react2 = _interopRequireDefault(_react);

var _propTypes = __webpack_require__(1);

var _propTypes2 = _interopRequireDefault(_propTypes);

var _const = __webpack_require__(2);

var _const2 = _interopRequireDefault(_const);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; } /* eslint
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 react/require-default-props: 0
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 jsx-a11y/no-noninteractive-element-interactions: 0
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               */


var SelectionCell = function (_Component) {
  _inherits(SelectionCell, _Component);

  function SelectionCell() {
    _classCallCheck(this, SelectionCell);

    var _this = _possibleConstructorReturn(this, (SelectionCell.__proto__ || Object.getPrototypeOf(SelectionCell)).call(this));

    _this.handleClick = _this.handleClick.bind(_this);
    return _this;
  }

  _createClass(SelectionCell, [{
    key: 'shouldComponentUpdate',
    value: function shouldComponentUpdate(nextProps) {
      var selected = this.props.selected;


      return nextProps.selected !== selected;
    }
  }, {
    key: 'handleClick',
    value: function handleClick() {
      var _props = this.props,
          inputType = _props.mode,
          rowKey = _props.rowKey,
          selected = _props.selected,
          onRowSelect = _props.onRowSelect,
          disabled = _props.disabled,
          rowIndex = _props.rowIndex,
          clickToSelect = _props.clickToSelect;


      if (disabled) return;
      if (clickToSelect) return;

      var checked = inputType === _const2.default.ROW_SELECT_SINGLE ? true : !selected;

      onRowSelect(rowKey, checked, rowIndex);
    }
  }, {
    key: 'render',
    value: function render() {
      var _props2 = this.props,
          inputType = _props2.mode,
          selected = _props2.selected,
          disabled = _props2.disabled;


      return _react2.default.createElement(
        'td',
        { onClick: this.handleClick },
        _react2.default.createElement('input', {
          type: inputType,
          checked: selected,
          disabled: disabled
        })
      );
    }
  }]);

  return SelectionCell;
}(_react.Component);

SelectionCell.propTypes = {
  mode: _propTypes2.default.string.isRequired,
  rowKey: _propTypes2.default.any,
  selected: _propTypes2.default.bool,
  onRowSelect: _propTypes2.default.func,
  disabled: _propTypes2.default.bool,
  rowIndex: _propTypes2.default.number,
  clickToSelect: _propTypes2.default.bool
};
exports.default = SelectionCell;

/***/ }),
/* 33 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _utils = __webpack_require__(3);

var _utils2 = _interopRequireDefault(_utils);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

var events = ['onClick', 'onMouseEnter', 'onMouseLeave'];

exports.default = function (ExtendBase) {
  return function (_ExtendBase) {
    _inherits(RowEventDelegater, _ExtendBase);

    function RowEventDelegater(props) {
      _classCallCheck(this, RowEventDelegater);

      var _this = _possibleConstructorReturn(this, (RowEventDelegater.__proto__ || Object.getPrototypeOf(RowEventDelegater)).call(this, props));

      _this.clickNum = 0;
      _this.createDefaultEventHandler = _this.createDefaultEventHandler.bind(_this);
      _this.createClickEventHandler = _this.createClickEventHandler.bind(_this);
      return _this;
    }

    _createClass(RowEventDelegater, [{
      key: 'createDefaultEventHandler',
      value: function createDefaultEventHandler(cb) {
        var _this2 = this;

        return function (e) {
          var _props = _this2.props,
              row = _props.row,
              rowIndex = _props.rowIndex;

          cb(e, row, rowIndex);
        };
      }
    }, {
      key: 'createClickEventHandler',
      value: function createClickEventHandler(cb) {
        var _this3 = this;

        return function (e) {
          var _props2 = _this3.props,
              row = _props2.row,
              selected = _props2.selected,
              keyField = _props2.keyField,
              selectable = _props2.selectable,
              rowIndex = _props2.rowIndex,
              _props2$selectRow = _props2.selectRow,
              onRowSelect = _props2$selectRow.onRowSelect,
              clickToEdit = _props2$selectRow.clickToEdit,
              _props2$cellEdit = _props2.cellEdit,
              mode = _props2$cellEdit.mode,
              DBCLICK_TO_CELL_EDIT = _props2$cellEdit.DBCLICK_TO_CELL_EDIT,
              DELAY_FOR_DBCLICK = _props2$cellEdit.DELAY_FOR_DBCLICK;


          var clickFn = function clickFn() {
            if (cb) {
              cb(e, row, rowIndex);
            }
            if (selectable) {
              var key = _utils2.default.get(row, keyField);
              onRowSelect(key, !selected, rowIndex);
            }
          };

          if (mode === DBCLICK_TO_CELL_EDIT && clickToEdit) {
            _this3.clickNum += 1;
            _utils2.default.debounce(function () {
              if (_this3.clickNum === 1) {
                clickFn();
              }
              _this3.clickNum = 0;
            }, DELAY_FOR_DBCLICK)();
          } else {
            clickFn();
          }
        };
      }
    }, {
      key: 'delegate',
      value: function delegate() {
        var _this4 = this;

        var attrs = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};

        var newAttrs = {};
        if (this.props.selectRow && this.props.selectRow.clickToSelect) {
          newAttrs.onClick = this.createClickEventHandler(attrs.onClick);
        }
        Object.keys(attrs).forEach(function (attr) {
          if (!newAttrs[attr]) {
            if (events.includes(attr)) {
              newAttrs[attr] = _this4.createDefaultEventHandler(attrs[attr]);
            } else {
              newAttrs[attr] = attrs[attr];
            }
          }
        });
        return newAttrs;
      }
    }]);

    return RowEventDelegater;
  }(ExtendBase);
};

/***/ }),
/* 34 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _react = __webpack_require__(0);

var _react2 = _interopRequireDefault(_react);

var _propTypes = __webpack_require__(1);

var _propTypes2 = _interopRequireDefault(_propTypes);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

var RowSection = function RowSection(_ref) {
  var content = _ref.content,
      colSpan = _ref.colSpan;
  return _react2.default.createElement(
    'tr',
    null,
    _react2.default.createElement(
      'td',
      {
        'data-toggle': 'collapse',
        colSpan: colSpan,
        className: 'react-bs-table-no-data'
      },
      content
    )
  );
};

RowSection.propTypes = {
  content: _propTypes2.default.any,
  colSpan: _propTypes2.default.number
};

RowSection.defaultProps = {
  content: null,
  colSpan: 1
};

exports.default = RowSection;

/***/ }),
/* 35 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _columnResolver = __webpack_require__(36);

var _columnResolver2 = _interopRequireDefault(_columnResolver);

var _const = __webpack_require__(2);

var _const2 = _interopRequireDefault(_const);

var _utils = __webpack_require__(3);

var _utils2 = _interopRequireDefault(_utils);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _objectWithoutProperties(obj, keys) { var target = {}; for (var i in obj) { if (keys.indexOf(i) >= 0) continue; if (!Object.prototype.hasOwnProperty.call(obj, i)) continue; target[i] = obj[i]; } return target; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

exports.default = function (ExtendBase) {
  return function (_ColumnResolver) {
    _inherits(TableResolver, _ColumnResolver);

    function TableResolver() {
      _classCallCheck(this, TableResolver);

      return _possibleConstructorReturn(this, (TableResolver.__proto__ || Object.getPrototypeOf(TableResolver)).apply(this, arguments));
    }

    _createClass(TableResolver, [{
      key: 'validateProps',
      value: function validateProps() {
        var _props = this.props,
            columns = _props.columns,
            keyField = _props.keyField;

        if (!keyField) {
          throw new Error('Please specify a field as key via keyField');
        }
        if (this.visibleColumnSize(columns) <= 0) {
          throw new Error('No any visible columns detect');
        }
      }
    }, {
      key: 'isEmpty',
      value: function isEmpty() {
        return this.props.data.length === 0;
      }

      /**
       * props resolver for cell selection
       * @param {Object} options - addtional options like callback which are about to merge into props
       *
       * @returns {Object} result - props for cell selections
       * @returns {String} result.mode - input type of row selection or disabled.
       */

    }, {
      key: 'resolveSelectRowProps',
      value: function resolveSelectRowProps(options) {
        var selectRow = this.props.selectRow;
        var ROW_SELECT_DISABLED = _const2.default.ROW_SELECT_DISABLED;


        if (_utils2.default.isDefined(selectRow)) {
          return _extends({}, selectRow, options);
        }

        return {
          mode: ROW_SELECT_DISABLED
        };
      }

      /**
       * props resolver for header cell selection
       * @param {Object} options - addtional options like callback which are about to merge into props
       *
       * @returns {Object} result - props for cell selections
       * @returns {String} result.mode - input type of row selection or disabled.
       * @returns {String} result.checkedStatus - checkbox status depending on selected rows counts
       */

    }, {
      key: 'resolveSelectRowPropsForHeader',
      value: function resolveSelectRowPropsForHeader() {
        var options = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};
        var selectRow = this.props.selectRow;

        var allRowsSelected = options.allRowsSelected,
            _options$selected = options.selected,
            selected = _options$selected === undefined ? [] : _options$selected,
            rest = _objectWithoutProperties(options, ['allRowsSelected', 'selected']);

        var ROW_SELECT_DISABLED = _const2.default.ROW_SELECT_DISABLED,
            CHECKBOX_STATUS_CHECKED = _const2.default.CHECKBOX_STATUS_CHECKED,
            CHECKBOX_STATUS_INDETERMINATE = _const2.default.CHECKBOX_STATUS_INDETERMINATE,
            CHECKBOX_STATUS_UNCHECKED = _const2.default.CHECKBOX_STATUS_UNCHECKED;


        if (_utils2.default.isDefined(selectRow)) {
          var checkedStatus = void 0;

          // checkbox status depending on selected rows counts
          if (allRowsSelected) checkedStatus = CHECKBOX_STATUS_CHECKED;else if (selected.length === 0) checkedStatus = CHECKBOX_STATUS_UNCHECKED;else checkedStatus = CHECKBOX_STATUS_INDETERMINATE;

          return _extends({}, selectRow, rest, {
            checkedStatus: checkedStatus
          });
        }

        return {
          mode: ROW_SELECT_DISABLED
        };
      }
    }]);

    return TableResolver;
  }((0, _columnResolver2.default)(ExtendBase));
};

/***/ }),
/* 36 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; }

exports.default = function (ExtendBase) {
  return function (_ExtendBase) {
    _inherits(ColumnResolver, _ExtendBase);

    function ColumnResolver() {
      _classCallCheck(this, ColumnResolver);

      return _possibleConstructorReturn(this, (ColumnResolver.__proto__ || Object.getPrototypeOf(ColumnResolver)).apply(this, arguments));
    }

    _createClass(ColumnResolver, [{
      key: "visibleColumnSize",
      value: function visibleColumnSize() {
        return this.props.columns.filter(function (c) {
          return !c.hidden;
        }).length;
      }
    }]);

    return ColumnResolver;
  }(ExtendBase);
};

/***/ }),
/* 37 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _react = __webpack_require__(0);

var _react2 = _interopRequireDefault(_react);

var _store = __webpack_require__(38);

var _store2 = _interopRequireDefault(_store);

var _wrapper = __webpack_require__(40);

var _wrapper2 = _interopRequireDefault(_wrapper);

var _wrapper3 = __webpack_require__(41);

var _wrapper4 = _interopRequireDefault(_wrapper3);

var _remoteResolver2 = __webpack_require__(16);

var _remoteResolver3 = _interopRequireDefault(_remoteResolver2);

var _utils = __webpack_require__(3);

var _utils2 = _interopRequireDefault(_utils);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; } /* eslint no-return-assign: 0 */
/* eslint react/prop-types: 0 */


var withDataStore = function withDataStore(Base) {
  return function (_remoteResolver) {
    _inherits(BootstrapTableContainer, _remoteResolver);

    function BootstrapTableContainer(props) {
      _classCallCheck(this, BootstrapTableContainer);

      var _this = _possibleConstructorReturn(this, (BootstrapTableContainer.__proto__ || Object.getPrototypeOf(BootstrapTableContainer)).call(this, props));

      _this.store = new _store2.default(props.keyField);
      _this.store.data = props.data;
      _this.wrapComponents();
      return _this;
    }

    _createClass(BootstrapTableContainer, [{
      key: 'componentWillReceiveProps',
      value: function componentWillReceiveProps(nextProps) {
        this.store.setAllData(nextProps.data);
      }
    }, {
      key: 'wrapComponents',
      value: function wrapComponents() {
        this.BaseComponent = Base;
        var _props = this.props,
            pagination = _props.pagination,
            columns = _props.columns,
            filter = _props.filter,
            selectRow = _props.selectRow,
            cellEdit = _props.cellEdit;

        if (pagination) {
          var wrapperFactory = pagination.wrapperFactory;

          this.BaseComponent = wrapperFactory(this.BaseComponent, {
            remoteResolver: _remoteResolver3.default
          });
        }

        if (columns.filter(function (col) {
          return col.sort;
        }).length > 0) {
          this.BaseComponent = (0, _wrapper2.default)(this.BaseComponent);
        }

        if (filter) {
          var _wrapperFactory = filter.wrapperFactory;

          this.BaseComponent = _wrapperFactory(this.BaseComponent, {
            _: _utils2.default,
            remoteResolver: _remoteResolver3.default
          });
        }

        if (cellEdit) {
          var _wrapperFactory2 = cellEdit.wrapperFactory;

          this.BaseComponent = _wrapperFactory2(this.BaseComponent, {
            _: _utils2.default,
            remoteResolver: _remoteResolver3.default
          });
        }

        if (selectRow) {
          this.BaseComponent = (0, _wrapper4.default)(this.BaseComponent);
        }
      }
    }, {
      key: 'render',
      value: function render() {
        var baseProps = _extends({}, this.props, {
          store: this.store
        });

        return _react2.default.createElement(this.BaseComponent, baseProps);
      }
    }]);

    return BootstrapTableContainer;
  }((0, _remoteResolver3.default)(_react.Component));
};

exports.default = withDataStore;

/***/ }),
/* 38 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }(); /* eslint no-underscore-dangle: 0 */


var _utils = __webpack_require__(3);

var _utils2 = _interopRequireDefault(_utils);

var _sort = __webpack_require__(39);

var _rows = __webpack_require__(13);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

var Store = function () {
  function Store(keyField) {
    _classCallCheck(this, Store);

    this._data = [];
    this._filteredData = [];
    this._keyField = keyField;
    this._sortOrder = undefined;
    this._sortField = undefined;
    this._selected = [];
    this._filters = {};
    this._page = undefined;
    this._sizePerPage = undefined;
  }

  _createClass(Store, [{
    key: 'edit',
    value: function edit(rowId, dataField, newValue) {
      var row = (0, _rows.getRowByRowId)(this)(rowId);
      if (row) _utils2.default.set(row, dataField, newValue);
    }
  }, {
    key: 'setSort',
    value: function setSort(_ref, order) {
      var dataField = _ref.dataField;

      this.sortOrder = (0, _sort.nextOrder)(this)(dataField, order);
      this.sortField = dataField;
    }
  }, {
    key: 'sortBy',
    value: function sortBy(_ref2) {
      var sortFunc = _ref2.sortFunc;

      this.data = (0, _sort.sort)(this)(sortFunc);
    }
  }, {
    key: 'getAllData',
    value: function getAllData() {
      return this._data;
    }
  }, {
    key: 'setAllData',
    value: function setAllData(data) {
      this._data = data;
    }
  }, {
    key: 'data',
    get: function get() {
      if (Object.keys(this._filters).length > 0) {
        return this._filteredData;
      }
      return this._data;
    },
    set: function set(data) {
      if (Object.keys(this._filters).length > 0) {
        this._filteredData = data;
      } else {
        this._data = data ? JSON.parse(JSON.stringify(data)) : [];
      }
    }
  }, {
    key: 'filteredData',
    get: function get() {
      return this._filteredData;
    },
    set: function set(filteredData) {
      this._filteredData = filteredData;
    }
  }, {
    key: 'keyField',
    get: function get() {
      return this._keyField;
    },
    set: function set(keyField) {
      this._keyField = keyField;
    }
  }, {
    key: 'sortOrder',
    get: function get() {
      return this._sortOrder;
    },
    set: function set(sortOrder) {
      this._sortOrder = sortOrder;
    }
  }, {
    key: 'page',
    get: function get() {
      return this._page;
    },
    set: function set(page) {
      this._page = page;
    }
  }, {
    key: 'sizePerPage',
    get: function get() {
      return this._sizePerPage;
    },
    set: function set(sizePerPage) {
      this._sizePerPage = sizePerPage;
    }
  }, {
    key: 'sortField',
    get: function get() {
      return this._sortField;
    },
    set: function set(sortField) {
      this._sortField = sortField;
    }
  }, {
    key: 'selected',
    get: function get() {
      return this._selected;
    },
    set: function set(selected) {
      this._selected = selected;
    }
  }, {
    key: 'filters',
    get: function get() {
      return this._filters;
    },
    set: function set(filters) {
      this._filters = filters;
    }
  }]);

  return Store;
}();

exports.default = Store;

/***/ }),
/* 39 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.nextOrder = exports.sort = undefined;

var _utils = __webpack_require__(3);

var _utils2 = _interopRequireDefault(_utils);

var _const = __webpack_require__(2);

var _const2 = _interopRequireDefault(_const);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _toConsumableArray(arr) { if (Array.isArray(arr)) { for (var i = 0, arr2 = Array(arr.length); i < arr.length; i++) { arr2[i] = arr[i]; } return arr2; } else { return Array.from(arr); } } /* eslint no-nested-ternary: 0 */
/* eslint no-lonely-if: 0 */
/* eslint no-underscore-dangle: 0 */


function comparator(a, b) {
  var result = void 0;
  if (typeof b === 'string') {
    result = b.localeCompare(a);
  } else {
    result = a > b ? -1 : a < b ? 1 : 0;
  }
  return result;
}

var sort = exports.sort = function sort(_ref) {
  var data = _ref.data,
      sortOrder = _ref.sortOrder,
      sortField = _ref.sortField;
  return function (sortFunc) {
    var _data = [].concat(_toConsumableArray(data));
    _data.sort(function (a, b) {
      var result = void 0;
      var valueA = _utils2.default.get(a, sortField);
      var valueB = _utils2.default.get(b, sortField);
      valueA = _utils2.default.isDefined(valueA) ? valueA : '';
      valueB = _utils2.default.isDefined(valueB) ? valueB : '';

      if (sortFunc) {
        result = sortFunc(valueA, valueB, sortOrder, sortField);
      } else {
        if (sortOrder === _const2.default.SORT_DESC) {
          result = comparator(valueA, valueB);
        } else {
          result = comparator(valueB, valueA);
        }
      }
      return result;
    });
    return _data;
  };
};

var nextOrder = exports.nextOrder = function nextOrder(store) {
  return function (field, order) {
    if (order) return order;

    if (field !== store.sortField) {
      return _const2.default.SORT_DESC;
    }
    return store.sortOrder === _const2.default.SORT_DESC ? _const2.default.SORT_ASC : _const2.default.SORT_DESC;
  };
};

/***/ }),
/* 40 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _react = __webpack_require__(0);

var _react2 = _interopRequireDefault(_react);

var _propTypes = __webpack_require__(1);

var _propTypes2 = _interopRequireDefault(_propTypes);

var _remoteResolver2 = __webpack_require__(16);

var _remoteResolver3 = _interopRequireDefault(_remoteResolver2);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; } /* eslint react/prop-types: 0 */


exports.default = function (Base) {
  var _class, _temp;

  return _temp = _class = function (_remoteResolver) {
    _inherits(SortWrapper, _remoteResolver);

    function SortWrapper(props) {
      _classCallCheck(this, SortWrapper);

      var _this = _possibleConstructorReturn(this, (SortWrapper.__proto__ || Object.getPrototypeOf(SortWrapper)).call(this, props));

      _this.handleSort = _this.handleSort.bind(_this);
      return _this;
    }

    _createClass(SortWrapper, [{
      key: 'componentWillMount',
      value: function componentWillMount() {
        var _props = this.props,
            columns = _props.columns,
            defaultSorted = _props.defaultSorted,
            store = _props.store;
        // defaultSorted is an array, it's ready to use as multi / single sort
        // when we start to support multi sort, please update following code to use array.forEach

        if (defaultSorted && defaultSorted.length > 0) {
          var dataField = defaultSorted[0].dataField;
          var order = defaultSorted[0].order;
          var column = columns.filter(function (col) {
            return col.dataField === dataField;
          });
          if (column.length > 0) {
            store.setSort(column[0], order);

            if (column[0].onSort) {
              column[0].onSort(store.sortField, store.sortOrder);
            }

            if (this.isRemoteSort() || this.isRemotePagination()) {
              this.handleSortChange();
            } else {
              store.sortBy(column[0]);
            }
          }
        }
      }
    }, {
      key: 'componentWillReceiveProps',
      value: function componentWillReceiveProps(nextProps) {
        var sortedColumn = nextProps.columns.find(function (column) {
          return column.dataField === nextProps.store.sortField;
        });
        if (sortedColumn && sortedColumn.sort) {
          nextProps.store.sortBy(sortedColumn);
        }
      }
    }, {
      key: 'handleSort',
      value: function handleSort(column) {
        var store = this.props.store;

        store.setSort(column);

        if (column.onSort) {
          column.onSort(store.sortField, store.sortOrder);
        }

        if (this.isRemoteSort() || this.isRemotePagination()) {
          this.handleSortChange();
        } else {
          store.sortBy(column);
          this.forceUpdate();
        }
      }
    }, {
      key: 'render',
      value: function render() {
        return _react2.default.createElement(Base, _extends({}, this.props, {
          onSort: this.handleSort,
          data: this.props.store.data
        }));
      }
    }]);

    return SortWrapper;
  }((0, _remoteResolver3.default)(_react.Component)), _class.propTypes = {
    store: _propTypes2.default.object.isRequired
  }, _temp;
};

/***/ }),
/* 41 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


Object.defineProperty(exports, "__esModule", {
  value: true
});

var _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; };

var _createClass = function () { function defineProperties(target, props) { for (var i = 0; i < props.length; i++) { var descriptor = props[i]; descriptor.enumerable = descriptor.enumerable || false; descriptor.configurable = true; if ("value" in descriptor) descriptor.writable = true; Object.defineProperty(target, descriptor.key, descriptor); } } return function (Constructor, protoProps, staticProps) { if (protoProps) defineProperties(Constructor.prototype, protoProps); if (staticProps) defineProperties(Constructor, staticProps); return Constructor; }; }();

var _react = __webpack_require__(0);

var _react2 = _interopRequireDefault(_react);

var _propTypes = __webpack_require__(1);

var _propTypes2 = _interopRequireDefault(_propTypes);

var _const = __webpack_require__(2);

var _const2 = _interopRequireDefault(_const);

var _selection = __webpack_require__(15);

var _rows = __webpack_require__(13);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _toConsumableArray(arr) { if (Array.isArray(arr)) { for (var i = 0, arr2 = Array(arr.length); i < arr.length; i++) { arr2[i] = arr[i]; } return arr2; } else { return Array.from(arr); } }

function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

function _possibleConstructorReturn(self, call) { if (!self) { throw new ReferenceError("this hasn't been initialised - super() hasn't been called"); } return call && (typeof call === "object" || typeof call === "function") ? call : self; }

function _inherits(subClass, superClass) { if (typeof superClass !== "function" && superClass !== null) { throw new TypeError("Super expression must either be null or a function, not " + typeof superClass); } subClass.prototype = Object.create(superClass && superClass.prototype, { constructor: { value: subClass, enumerable: false, writable: true, configurable: true } }); if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass; } /* eslint no-param-reassign: 0 */


exports.default = function (Base) {
  var _class, _temp;

  return _temp = _class = function (_Component) {
    _inherits(RowSelectionWrapper, _Component);

    function RowSelectionWrapper(props) {
      _classCallCheck(this, RowSelectionWrapper);

      var _this = _possibleConstructorReturn(this, (RowSelectionWrapper.__proto__ || Object.getPrototypeOf(RowSelectionWrapper)).call(this, props));

      _this.handleRowSelect = _this.handleRowSelect.bind(_this);
      _this.handleAllRowsSelect = _this.handleAllRowsSelect.bind(_this);
      props.store.selected = _this.props.selectRow.selected || [];
      _this.state = {
        selectedRowKeys: props.store.selected
      };
      return _this;
    }

    _createClass(RowSelectionWrapper, [{
      key: 'componentWillReceiveProps',
      value: function componentWillReceiveProps(nextProps) {
        var _this2 = this;

        if (nextProps.selectRow) {
          this.store.selected = nextProps.selectRow.selected || [];
          this.setState(function () {
            return {
              selectedRowKeys: _this2.store.selected
            };
          });
        }
      }

      /**
       * row selection handler
       * @param {String} rowKey - row key of what was selected.
       * @param {Boolean} checked - next checked status of input button.
       */

    }, {
      key: 'handleRowSelect',
      value: function handleRowSelect(rowKey, checked, rowIndex) {
        var _props = this.props,
            _props$selectRow = _props.selectRow,
            mode = _props$selectRow.mode,
            onSelect = _props$selectRow.onSelect,
            store = _props.store;
        var ROW_SELECT_SINGLE = _const2.default.ROW_SELECT_SINGLE;


        var currSelected = [].concat(_toConsumableArray(store.selected));

        if (mode === ROW_SELECT_SINGLE) {
          // when select mode is radio
          currSelected = [rowKey];
        } else if (checked) {
          // when select mode is checkbox
          currSelected.push(rowKey);
        } else {
          currSelected = currSelected.filter(function (value) {
            return value !== rowKey;
          });
        }

        store.selected = currSelected;

        if (onSelect) {
          var row = (0, _rows.getRowByRowId)(store)(rowKey);
          onSelect(row, checked, rowIndex);
        }

        this.setState(function () {
          return {
            selectedRowKeys: currSelected
          };
        });
      }

      /**
       * handle all rows selection on header cell by store.selected or given specific result.
       * @param {Boolean} option - customized result for all rows selection
       */

    }, {
      key: 'handleAllRowsSelect',
      value: function handleAllRowsSelect(option) {
        var _props2 = this.props,
            store = _props2.store,
            _props2$selectRow = _props2.selectRow,
            onSelectAll = _props2$selectRow.onSelectAll,
            nonSelectable = _props2$selectRow.nonSelectable;

        var selected = (0, _selection.isAnySelectedRow)(store)(nonSelectable);

        // set next status of all row selected by store.selected or customizing by user.
        var result = option || !selected;

        var currSelected = result ? (0, _selection.selectableKeys)(store)(nonSelectable) : (0, _selection.unSelectableKeys)(store)(nonSelectable);

        store.selected = currSelected;

        if (onSelectAll) {
          onSelectAll(result, (0, _selection.getSelectedRows)(store));
        }

        this.setState(function () {
          return {
            selectedRowKeys: currSelected
          };
        });
      }
    }, {
      key: 'render',
      value: function render() {
        return _react2.default.createElement(Base, _extends({}, this.props, {
          onRowSelect: this.handleRowSelect,
          onAllRowsSelect: this.handleAllRowsSelect
        }));
      }
    }]);

    return RowSelectionWrapper;
  }(_react.Component), _class.propTypes = {
    store: _propTypes2.default.object.isRequired,
    selectRow: _propTypes2.default.object.isRequired
  }, _temp;
};

/***/ })
/******/ ]);
});
//# sourceMappingURL=data:application/json;charset=utf-8;base64,eyJ2ZXJzaW9uIjozLCJzb3VyY2VzIjpbIndlYnBhY2s6Ly8vd2VicGFjay91bml2ZXJzYWxNb2R1bGVEZWZpbml0aW9uIiwid2VicGFjazovLy93ZWJwYWNrL2Jvb3RzdHJhcCBjNDE0NGQxNTZlMjlhMDExZmU5MyIsIndlYnBhY2s6Ly8vZXh0ZXJuYWwge1wicm9vdFwiOlwiUmVhY3RcIixcImNvbW1vbmpzMlwiOlwicmVhY3RcIixcImNvbW1vbmpzXCI6XCJyZWFjdFwiLFwiYW1kXCI6XCJyZWFjdFwifSIsIndlYnBhY2s6Ly8vLi9ub2RlX21vZHVsZXMvcHJvcC10eXBlcy9pbmRleC5qcyIsIndlYnBhY2s6Ly8vLi9wYWNrYWdlcy9yZWFjdC1ib290c3RyYXAtdGFibGUyL3NyYy9jb25zdC5qcyIsIndlYnBhY2s6Ly8vLi9wYWNrYWdlcy9yZWFjdC1ib290c3RyYXAtdGFibGUyL3NyYy91dGlscy5qcyIsIndlYnBhY2s6Ly8vLi9ub2RlX21vZHVsZXMvcHJvcC10eXBlcy9mYWN0b3J5V2l0aFRocm93aW5nU2hpbXMuanMiLCJ3ZWJwYWNrOi8vLy4vbm9kZV9tb2R1bGVzL2ZianMvbGliL2VtcHR5RnVuY3Rpb24uanMiLCJ3ZWJwYWNrOi8vLy4vbm9kZV9tb2R1bGVzL2ZianMvbGliL2ludmFyaWFudC5qcyIsIndlYnBhY2s6Ly8vLi9ub2RlX21vZHVsZXMvcHJvcC10eXBlcy9saWIvUmVhY3RQcm9wVHlwZXNTZWNyZXQuanMiLCJ3ZWJwYWNrOi8vLy4vcGFja2FnZXMvcmVhY3QtYm9vdHN0cmFwLXRhYmxlMi9ub2RlX21vZHVsZXMvY2xhc3NuYW1lcy9pbmRleC5qcyIsIndlYnBhY2s6Ly8vLi9wYWNrYWdlcy9yZWFjdC1ib290c3RyYXAtdGFibGUyL3NyYy9zdG9yZS9yb3dzLmpzIiwid2VicGFjazovLy8uL3BhY2thZ2VzL3JlYWN0LWJvb3RzdHJhcC10YWJsZTIvc3JjL3N0b3JlL3NlbGVjdGlvbi5qcyIsIndlYnBhY2s6Ly8vLi9wYWNrYWdlcy9yZWFjdC1ib290c3RyYXAtdGFibGUyL3NyYy9wcm9wcy1yZXNvbHZlci9yZW1vdGUtcmVzb2x2ZXIuanMiLCJ3ZWJwYWNrOi8vLy4vcGFja2FnZXMvcmVhY3QtYm9vdHN0cmFwLXRhYmxlMi9pbmRleC5qcyIsIndlYnBhY2s6Ly8vLi9wYWNrYWdlcy9yZWFjdC1ib290c3RyYXAtdGFibGUyL3NyYy9ib290c3RyYXAtdGFibGUuanMiLCJ3ZWJwYWNrOi8vLy4vcGFja2FnZXMvcmVhY3QtYm9vdHN0cmFwLXRhYmxlMi9zcmMvaGVhZGVyLmpzIiwid2VicGFjazovLy8uL3BhY2thZ2VzL3JlYWN0LWJvb3RzdHJhcC10YWJsZTIvc3JjL2hlYWRlci1jZWxsLmpzIiwid2VicGFjazovLy8uL3BhY2thZ2VzL3JlYWN0LWJvb3RzdHJhcC10YWJsZTIvc3JjL3NvcnQvc3ltYm9sLmpzIiwid2VicGFjazovLy8uL3BhY2thZ2VzL3JlYWN0LWJvb3RzdHJhcC10YWJsZTIvc3JjL3NvcnQvY2FyZXQuanMiLCJ3ZWJwYWNrOi8vLy4vcGFja2FnZXMvcmVhY3QtYm9vdHN0cmFwLXRhYmxlMi9zcmMvcm93LXNlbGVjdGlvbi9zZWxlY3Rpb24taGVhZGVyLWNlbGwuanMiLCJ3ZWJwYWNrOi8vLy4vcGFja2FnZXMvcmVhY3QtYm9vdHN0cmFwLXRhYmxlMi9zcmMvY2FwdGlvbi5qcyIsIndlYnBhY2s6Ly8vLi9wYWNrYWdlcy9yZWFjdC1ib290c3RyYXAtdGFibGUyL3NyYy9ib2R5LmpzIiwid2VicGFjazovLy8uL3BhY2thZ2VzL3JlYWN0LWJvb3RzdHJhcC10YWJsZTIvc3JjL3Jvdy5qcyIsIndlYnBhY2s6Ly8vLi9wYWNrYWdlcy9yZWFjdC1ib290c3RyYXAtdGFibGUyL3NyYy9jZWxsLmpzIiwid2VicGFjazovLy8uL3BhY2thZ2VzL3JlYWN0LWJvb3RzdHJhcC10YWJsZTIvc3JjL3Jvdy1zZWxlY3Rpb24vc2VsZWN0aW9uLWNlbGwuanMiLCJ3ZWJwYWNrOi8vLy4vcGFja2FnZXMvcmVhY3QtYm9vdHN0cmFwLXRhYmxlMi9zcmMvcm93LWV2ZW50LWRlbGVnYXRlci5qcyIsIndlYnBhY2s6Ly8vLi9wYWNrYWdlcy9yZWFjdC1ib290c3RyYXAtdGFibGUyL3NyYy9yb3ctc2VjdGlvbi5qcyIsIndlYnBhY2s6Ly8vLi9wYWNrYWdlcy9yZWFjdC1ib290c3RyYXAtdGFibGUyL3NyYy9wcm9wcy1yZXNvbHZlci9pbmRleC5qcyIsIndlYnBhY2s6Ly8vLi9wYWNrYWdlcy9yZWFjdC1ib290c3RyYXAtdGFibGUyL3NyYy9wcm9wcy1yZXNvbHZlci9jb2x1bW4tcmVzb2x2ZXIuanMiLCJ3ZWJwYWNrOi8vLy4vcGFja2FnZXMvcmVhY3QtYm9vdHN0cmFwLXRhYmxlMi9zcmMvY29udGFpbmVyLmpzIiwid2VicGFjazovLy8uL3BhY2thZ2VzL3JlYWN0LWJvb3RzdHJhcC10YWJsZTIvc3JjL3N0b3JlL2luZGV4LmpzIiwid2VicGFjazovLy8uL3BhY2thZ2VzL3JlYWN0LWJvb3RzdHJhcC10YWJsZTIvc3JjL3N0b3JlL3NvcnQuanMiLCJ3ZWJwYWNrOi8vLy4vcGFja2FnZXMvcmVhY3QtYm9vdHN0cmFwLXRhYmxlMi9zcmMvc29ydC93cmFwcGVyLmpzIiwid2VicGFjazovLy8uL3BhY2thZ2VzL3JlYWN0LWJvb3RzdHJhcC10YWJsZTIvc3JjL3Jvdy1zZWxlY3Rpb24vd3JhcHBlci5qcyJdLCJuYW1lcyI6WyJTT1JUX0FTQyIsIlNPUlRfREVTQyIsIlJPV19TRUxFQ1RfU0lOR0xFIiwiUk9XX1NFTEVDVF9NVUxUSVBMRSIsIlJPV19TRUxFQ1RfRElTQUJMRUQiLCJDSEVDS0JPWF9TVEFUVVNfQ0hFQ0tFRCIsIkNIRUNLQk9YX1NUQVRVU19JTkRFVEVSTUlOQVRFIiwiQ0hFQ0tCT1hfU1RBVFVTX1VOQ0hFQ0tFRCIsInNwbGl0TmVzdGVkIiwic3RyIiwiam9pbiIsInJlcGxhY2UiLCJzcGxpdCIsImdldCIsInRhcmdldCIsImZpZWxkIiwicGF0aEFycmF5IiwicmVzdWx0IiwicmVkdWNlIiwiY3VyciIsInBhdGgiLCJlIiwic2V0IiwidmFsdWUiLCJzYWZlIiwibGV2ZWwiLCJhIiwiYiIsIkVycm9yIiwibGVuZ3RoIiwiaXNGdW5jdGlvbiIsIm9iaiIsImlzT2JqZWN0IiwidHlwZSIsImNvbnN0cnVjdG9yIiwiT2JqZWN0IiwiaXNFbXB0eU9iamVjdCIsImhhc093blByb3BlcnR5IiwicHJvdG90eXBlIiwia2V5cyIsImkiLCJjYWxsIiwiaXNEZWZpbmVkIiwic2xlZXAiLCJmbiIsIm1zIiwic2V0VGltZW91dCIsImRlYm91bmNlIiwiZnVuYyIsIndhaXQiLCJpbW1lZGlhdGUiLCJ0aW1lb3V0IiwibGF0ZXIiLCJhcHBseSIsImNhbGxOb3ciLCJjbGVhclRpbWVvdXQiLCJhcHB5IiwibWF0Y2hSb3ciLCJrZXlGaWVsZCIsImlkIiwicm93IiwiZ2V0Um93QnlSb3dJZCIsImRhdGEiLCJmaW5kIiwiaXNTZWxlY3RlZEFsbCIsInNlbGVjdGVkIiwiaXNBbnlTZWxlY3RlZFJvdyIsInNraXBzIiwiZmlsdGVyIiwiaW5jbHVkZXMiLCJ4Iiwic2VsZWN0YWJsZUtleXMiLCJtYXAiLCJ1blNlbGVjdGFibGVLZXlzIiwiZ2V0U2VsZWN0ZWRSb3dzIiwic3RvcmUiLCJnZXRSb3ciLCJrIiwic3RhdGUiLCJwcm9wcyIsInBhZ2UiLCJzaXplUGVyUGFnZSIsImZpbHRlcnMiLCJzb3J0RmllbGQiLCJzb3J0T3JkZXIiLCJnZXRBbGxEYXRhIiwicmVtb3RlIiwicGFnaW5hdGlvbiIsInNvcnQiLCJjZWxsRWRpdCIsIm9uVGFibGVDaGFuZ2UiLCJnZXROZXdlc3RTdGF0ZSIsIm5ld1N0YXRlIiwiaXNSZW1vdGVQYWdpbmF0aW9uIiwib3B0aW9ucyIsInBhZ2VTdGFydEluZGV4Iiwicm93SWQiLCJkYXRhRmllbGQiLCJuZXdWYWx1ZSIsIkV4dGVuZEJhc2UiLCJCb290c3RyYXBUYWJsZSIsInZhbGlkYXRlUHJvcHMiLCJuZXh0UHJvcHMiLCJzZXRTdGF0ZSIsImxvYWRpbmciLCJvdmVybGF5IiwidGFibGUiLCJyZW5kZXJUYWJsZSIsIkxvYWRpbmdPdmVybGF5IiwiY29sdW1ucyIsInN0cmlwZWQiLCJob3ZlciIsImJvcmRlcmVkIiwiY29uZGVuc2VkIiwibm9EYXRhSW5kaWNhdGlvbiIsImNhcHRpb24iLCJyb3dTdHlsZSIsInJvd0NsYXNzZXMiLCJyb3dFdmVudHMiLCJ0YWJsZUNsYXNzIiwiY2VsbFNlbGVjdGlvbkluZm8iLCJyZXNvbHZlU2VsZWN0Um93UHJvcHMiLCJvblJvd1NlbGVjdCIsImhlYWRlckNlbGxTZWxlY3Rpb25JbmZvIiwicmVzb2x2ZVNlbGVjdFJvd1Byb3BzRm9ySGVhZGVyIiwib25BbGxSb3dzU2VsZWN0IiwiYWxsUm93c1NlbGVjdGVkIiwidGFibGVDYXB0aW9uIiwib25Tb3J0Iiwib25GaWx0ZXIiLCJpc0VtcHR5IiwidmlzaWJsZUNvbHVtblNpemUiLCJwcm9wVHlwZXMiLCJzdHJpbmciLCJpc1JlcXVpcmVkIiwiYXJyYXkiLCJvbmVPZlR5cGUiLCJib29sIiwic2hhcGUiLCJvYmplY3QiLCJub2RlIiwic2VsZWN0Um93IiwibW9kZSIsIm9uZU9mIiwiY2xpY2tUb1NlbGVjdCIsImNsaWNrVG9FZGl0Iiwib25TZWxlY3QiLCJvblNlbGVjdEFsbCIsInN0eWxlIiwiY2xhc3NlcyIsIm5vblNlbGVjdGFibGUiLCJiZ0NvbG9yIiwiaGlkZVNlbGVjdENvbHVtbiIsImRlZmF1bHRTb3J0ZWQiLCJhcnJheU9mIiwib3JkZXIiLCJkZWZhdWx0UHJvcHMiLCJIZWFkZXIiLCJjb2x1bW4iLCJjdXJyU29ydCIsImlzTGFzdFNvcnRpbmciLCJIZWFkZXJDZWxsIiwiaW5kZXgiLCJzb3J0aW5nIiwidGV4dCIsImhpZGRlbiIsImhlYWRlclRpdGxlIiwiaGVhZGVyQWxpZ24iLCJoZWFkZXJGb3JtYXR0ZXIiLCJoZWFkZXJFdmVudHMiLCJoZWFkZXJDbGFzc2VzIiwiaGVhZGVyU3R5bGUiLCJoZWFkZXJBdHRycyIsImhlYWRlclNvcnRpbmdDbGFzc2VzIiwiaGVhZGVyU29ydGluZ1N0eWxlIiwiY2VsbEF0dHJzIiwic29ydFN5bWJvbCIsImZpbHRlckVsbSIsImNlbGxTdHlsZSIsImNlbGxDbGFzc2VzIiwidGl0bGUiLCJ0ZXh0QWxpZ24iLCJkaXNwbGF5IiwiY3VzdG9tQ2xpY2siLCJvbkNsaWNrIiwiY2xhc3NOYW1lIiwiY2hpbGRyZW4iLCJzb3J0RWxlbWVudCIsImZpbHRlckVsZW1lbnQiLCJjcmVhdGVFbGVtZW50IiwiZm9ybWF0dGVyIiwiZm9ybWF0RXh0cmFEYXRhIiwiYW55IiwiZXZlbnRzIiwiYWxpZ24iLCJhdHRycyIsInNvcnRGdW5jIiwiZWRpdGFibGUiLCJlZGl0Q2VsbFN0eWxlIiwiZWRpdENlbGxDbGFzc2VzIiwidmFsaWRhdG9yIiwiZmlsdGVyVmFsdWUiLCJudW1iZXIiLCJTb3J0U3ltYm9sIiwiU29ydENhcmV0Iiwib3JkZXJDbGFzcyIsImRyb3B1cCIsIkNoZWNrQm94IiwiY2hlY2tlZCIsImluZGV0ZXJtaW5hdGUiLCJpbnB1dCIsIlNlbGVjdGlvbkhlYWRlckNlbGwiLCJoYW5kbGVDaGVja0JveENsaWNrIiwiYmluZCIsImNoZWNrZWRTdGF0dXMiLCJDYXB0aW9uIiwiQm9keSIsInNlbGVjdGVkUm93S2V5cyIsImNvbnRlbnQiLCJpbmRpY2F0aW9uIiwibm9uRWRpdGFibGVSb3dzIiwia2V5IiwiaW5kZXhPZiIsInNlbGVjdGVkU3R5bGUiLCJzZWxlY3RlZENsYXNzZXMiLCJiYWNrZ3JvdW5kQ29sb3IiLCJzZWxlY3RhYmxlIiwiUm93Iiwicm93SW5kZXgiLCJlZGl0YWJsZVJvdyIsIm9uU3RhcnQiLCJFZGl0aW5nQ2VsbCIsImVkaXRpbmdSb3dJZHgiLCJyaWR4IiwiZWRpdGluZ0NvbElkeCIsImNpZHgiLCJDTElDS19UT19DRUxMX0VESVQiLCJEQkNMSUNLX1RPX0NFTExfRURJVCIsInJlc3QiLCJ0ckF0dHJzIiwiZGVsZWdhdGUiLCJlZGl0Q2VsbHN0eWxlIiwiZWRpdENlbGxjbGFzc2VzIiwiQ2VsbCIsImhhbmRsZUVkaXRpbmdDZWxsIiwiY29sdW1uSW5kZXgiLCJkYmNsaWNrVG9FZGl0IiwiY3VzdG9tRGJDbGljayIsIm9uRG91YmxlQ2xpY2siLCJjZWxsVGl0bGUiLCJTZWxlY3Rpb25DZWxsIiwiaGFuZGxlQ2xpY2siLCJpbnB1dFR5cGUiLCJyb3dLZXkiLCJkaXNhYmxlZCIsImNsaWNrTnVtIiwiY3JlYXRlRGVmYXVsdEV2ZW50SGFuZGxlciIsImNyZWF0ZUNsaWNrRXZlbnRIYW5kbGVyIiwiY2IiLCJERUxBWV9GT1JfREJDTElDSyIsImNsaWNrRm4iLCJuZXdBdHRycyIsImZvckVhY2giLCJhdHRyIiwiUm93U2VjdGlvbiIsImNvbFNwYW4iLCJjIiwid2l0aERhdGFTdG9yZSIsIndyYXBDb21wb25lbnRzIiwic2V0QWxsRGF0YSIsIkJhc2VDb21wb25lbnQiLCJCYXNlIiwid3JhcHBlckZhY3RvcnkiLCJyZW1vdGVSZXNvbHZlciIsImNvbCIsIl8iLCJiYXNlUHJvcHMiLCJTdG9yZSIsIl9kYXRhIiwiX2ZpbHRlcmVkRGF0YSIsIl9rZXlGaWVsZCIsIl9zb3J0T3JkZXIiLCJ1bmRlZmluZWQiLCJfc29ydEZpZWxkIiwiX3NlbGVjdGVkIiwiX2ZpbHRlcnMiLCJfcGFnZSIsIl9zaXplUGVyUGFnZSIsIkpTT04iLCJwYXJzZSIsInN0cmluZ2lmeSIsImZpbHRlcmVkRGF0YSIsImNvbXBhcmF0b3IiLCJsb2NhbGVDb21wYXJlIiwidmFsdWVBIiwidmFsdWVCIiwibmV4dE9yZGVyIiwiaGFuZGxlU29ydCIsInNldFNvcnQiLCJpc1JlbW90ZVNvcnQiLCJoYW5kbGVTb3J0Q2hhbmdlIiwic29ydEJ5Iiwic29ydGVkQ29sdW1uIiwiZm9yY2VVcGRhdGUiLCJoYW5kbGVSb3dTZWxlY3QiLCJoYW5kbGVBbGxSb3dzU2VsZWN0IiwiY3VyclNlbGVjdGVkIiwicHVzaCIsIm9wdGlvbiJdLCJtYXBwaW5ncyI6IkFBQUE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0EsQ0FBQztBQUNELE87QUNWQTtBQUNBOztBQUVBO0FBQ0E7O0FBRUE7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7QUFDQTtBQUNBO0FBQ0E7O0FBRUE7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LEtBQWpCLEVBQXdCMkUsYUFBeEIsQ0FBakI7O0FBRUE7QUFDQSxZQUFNckksU0FBU21TLFVBQVUsQ0FBQ25QLFFBQTFCOztBQUVBLFlBQU1pUCxlQUFlalMsU0FDbkIsK0JBQWUwRCxLQUFmLEVBQXNCMkUsYUFBdEIsQ0FEbUIsR0FFbkIsaUNBQWlCM0UsS0FBakIsRUFBd0IyRSxhQUF4QixDQUZGOztBQUtBM0UsY0FBTVYsUUFBTixHQUFpQmlQLFlBQWpCOztBQUVBLFlBQUkvSixXQUFKLEVBQWlCO0FBQ2ZBLHNCQUFZbEksTUFBWixFQUFvQixnQ0FBZ0IwRCxLQUFoQixDQUFwQjtBQUNEOztBQUVELGFBQUs0QixRQUFMLENBQWM7QUFBQSxpQkFBTztBQUNuQmtILDZCQUFpQnlGO0FBREUsV0FBUDtBQUFBLFNBQWQ7QUFHRDtBQXJGVTtBQUFBO0FBQUEsK0JBdUZGO0FBQ1AsZUFDRSw4QkFBQyxJQUFELGVBQ08sS0FBS25PLEtBRFo7QUFFRSx1QkFBYyxLQUFLaU8sZUFGckI7QUFHRSwyQkFBa0IsS0FBS0M7QUFIekIsV0FERjtBQU9EO0FBL0ZVOztBQUFBO0FBQUEsOEJBRUo3SyxTQUZJLEdBRVE7QUFDakJ6RCxXQUFPLG9CQUFVZ0UsTUFBVixDQUFpQkwsVUFEUDtBQUVqQk8sZUFBVyxvQkFBVUYsTUFBVixDQUFpQkw7QUFGWCxHQUZSO0FBQUEsQyIsImZpbGUiOiJyZWFjdC1ib290c3RyYXAtdGFibGUyL2Rpc3QvcmVhY3QtYm9vdHN0cmFwLXRhYmxlMi5qcyIsInNvdXJjZXNDb250ZW50IjpbIihmdW5jdGlvbiB3ZWJwYWNrVW5pdmVyc2FsTW9kdWxlRGVmaW5pdGlvbihyb290LCBmYWN0b3J5KSB7XG5cdGlmKHR5cGVvZiBleHBvcnRzID09PSAnb2JqZWN0JyAmJiB0eXBlb2YgbW9kdWxlID09PSAnb2JqZWN0Jylcblx0XHRtb2R1bGUuZXhwb3J0cyA9IGZhY3RvcnkocmVxdWlyZShcInJlYWN0XCIpKTtcblx0ZWxzZSBpZih0eXBlb2YgZGVmaW5lID09PSAnZnVuY3Rpb24nICYmIGRlZmluZS5hbWQpXG5cdFx0ZGVmaW5lKFtcInJlYWN0XCJdLCBmYWN0b3J5KTtcblx0ZWxzZSBpZih0eXBlb2YgZXhwb3J0cyA9PT0gJ29iamVjdCcpXG5cdFx0ZXhwb3J0c1tcIlJlYWN0Qm9vdHN0cmFwVGFibGVcIl0gPSBmYWN0b3J5KHJlcXVpcmUoXCJyZWFjdFwiKSk7XG5cdGVsc2Vcblx0XHRyb290W1wiUmVhY3RCb290c3RyYXBUYWJsZVwiXSA9IGZhY3Rvcnkocm9vdFtcIlJlYWN0XCJdKTtcbn0pKHRoaXMsIGZ1bmN0aW9uKF9fV0VCUEFDS19FWFRFUk5BTF9NT0RVTEVfMF9fKSB7XG5yZXR1cm4gXG5cblxuLy8gV0VCUEFDSyBGT09URVIgLy9cbi8vIHdlYnBhY2svdW5pdmVyc2FsTW9kdWxlRGVmaW5pdGlvbiIsIiBcdC8vIFRoZSBtb2R1bGUgY2FjaGVcbiBcdHZhciBpbnN0YWxsZWRNb2R1bGVzID0ge307XG5cbiBcdC8vIFRoZSByZXF1aXJlIGZ1bmN0aW9uXG4gXHRmdW5jdGlvbiBfX3dlYnBhY2tfcmVxdWlyZV9fKG1vZHVsZUlkKSB7XG5cbiBcdFx0Ly8gQ2hlY2sgaWYgbW9kdWxlIGlzIGluIGNhY2hlXG4gXHRcdGlmKGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdKSB7XG4gXHRcdFx0cmV0dXJuIGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdLmV4cG9ydHM7XG4gXHRcdH1cbiBcdFx0Ly8gQ3JlYXRlIGEgbmV3IG1vZHVsZSAoYW5kIHB1dCBpdCBpbnRvIHRoZSBjYWNoZSlcbiBcdFx0dmFyIG1vZHVsZSA9IGluc3RhbGxlZE1vZHVsZXNbbW9kdWxlSWRdID0ge1xuIFx0XHRcdGk6IG1vZHVsZUlkLFxuIFx0XHRcdGw6IGZhbHNlLFxuIFx0XHRcdGV4cG9ydHM6IHt9XG4gXHRcdH07XG5cbiBcdFx0Ly8gRXhlY3V0ZSB0aGUgbW9kdWxlIGZ1bmN0aW9uXG4gXHRcdG1vZHVsZXNbbW9kdWxlSWRdLmNhbGwobW9kdWxlLmV4cG9ydHMsIG1vZHVsZSwgbW9kdWxlLmV4cG9ydHMsIF9fd2VicGFja19yZXF1aXJlX18pO1xuXG4gXHRcdC8vIEZsYWcgdGhlIG1vZHVsZSBhcyBsb2FkZWRcbiBcdFx0bW9kdWxlLmwgPSB0cnVlO1xuXG4gXHRcdC8vIFJldHVybiB0aGUgZXhwb3J0cyBvZiB0aGUgbW9kdWxlXG4gXHRcdHJldHVybiBtb2R1bGUuZXhwb3J0cztcbiBcdH1cblxuXG4gXHQvLyBleHBvc2UgdGhlIG1vZHVsZXMgb2JqZWN0IChfX3dlYnBhY2tfbW9kdWxlc19fKVxuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5tID0gbW9kdWxlcztcblxuIFx0Ly8gZXhwb3NlIHRoZSBtb2R1bGUgY2FjaGVcbiBcdF9fd2VicGFja19yZXF1aXJlX18uYyA9IGluc3RhbGxlZE1vZHVsZXM7XG5cbiBcdC8vIGRlZmluZSBnZXR0ZXIgZnVuY3Rpb24gZm9yIGhhcm1vbnkgZXhwb3J0c1xuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5kID0gZnVuY3Rpb24oZXhwb3J0cywgbmFtZSwgZ2V0dGVyKSB7XG4gXHRcdGlmKCFfX3dlYnBhY2tfcmVxdWlyZV9fLm8oZXhwb3J0cywgbmFtZSkpIHtcbiBcdFx0XHRPYmplY3QuZGVmaW5lUHJvcGVydHkoZXhwb3J0cywgbmFtZSwge1xuIFx0XHRcdFx0Y29uZmlndXJhYmxlOiBmYWxzZSxcbiBcdFx0XHRcdGVudW1lcmFibGU6IHRydWUsXG4gXHRcdFx0XHRnZXQ6IGdldHRlclxuIFx0XHRcdH0pO1xuIFx0XHR9XG4gXHR9O1xuXG4gXHQvLyBnZXREZWZhdWx0RXhwb3J0IGZ1bmN0aW9uIGZvciBjb21wYXRpYmlsaXR5IHdpdGggbm9uLWhhcm1vbnkgbW9kdWxlc1xuIFx0X193ZWJwYWNrX3JlcXVpcmVfXy5uID0gZnVuY3Rpb24obW9kdWxlKSB7XG4gXHRcdHZhciBnZXR0ZXIgPSBtb2R1bGUgJiYgbW9kdWxlLl9fZXNNb2R1bGUgP1xuIFx0XHRcdGZ1bmN0aW9uIGdldERlZmF1bHQoKSB7IHJldHVybiBtb2R1bGVbJ2RlZmF1bHQnXTsgfSA6XG4gXHRcdFx0ZnVuY3Rpb24gZ2V0TW9kdWxlRXhwb3J0cygpIHsgcmV0dXJuIG1vZHVsZTsgfTtcbiBcdFx0X193ZWJwYWNrX3JlcXVpcmVfXy5kKGdldHRlciwgJ2EnLCBnZXR0ZXIpO1xuIFx0XHRyZXR1cm4gZ2V0dGVyO1xuIFx0fTtcblxuIFx0Ly8gT2JqZWN0LnByb3RvdHlwZS5oYXNPd25Qcm9wZXJ0eS5jYWxsXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLm8gPSBmdW5jdGlvbihvYmplY3QsIHByb3BlcnR5KSB7IHJldHVybiBPYmplY3QucHJvdG90eXBlLmhhc093blByb3BlcnR5LmNhbGwob2JqZWN0LCBwcm9wZXJ0eSk7IH07XG5cbiBcdC8vIF9fd2VicGFja19wdWJsaWNfcGF0aF9fXG4gXHRfX3dlYnBhY2tfcmVxdWlyZV9fLnAgPSBcIlwiO1xuXG4gXHQvLyBMb2FkIGVudHJ5IG1vZHVsZSBhbmQgcmV0dXJuIGV4cG9ydHNcbiBcdHJldHVybiBfX3dlYnBhY2tfcmVxdWlyZV9fKF9fd2VicGFja19yZXF1aXJlX18ucyA9IDIxKTtcblxuXG5cbi8vIFdFQlBBQ0sgRk9PVEVSIC8vXG4vLyB3ZWJwYWNrL2Jvb3RzdHJhcCBjNDE0NGQxNTZlMjlhMDExZmU5MyIsIm1vZHVsZS5leHBvcnRzID0gX19XRUJQQUNLX0VYVEVSTkFMX01PRFVMRV8wX187XG5cblxuLy8vLy8vLy8vLy8vLy8vLy8vXG4vLyBXRUJQQUNLIEZPT1RFUlxuLy8gZXh0ZXJuYWwge1wicm9vdFwiOlwiUmVhY3RcIixcImNvbW1vbmpzMlwiOlwicmVhY3RcIixcImNvbW1vbmpzXCI6XCJyZWFjdFwiLFwiYW1kXCI6XCJyZWFjdFwifVxuLy8gbW9kdWxlIGlkID0gMFxuLy8gbW9kdWxlIGNodW5rcyA9IDAgMSAyIDMgNCA1IDYgNyA4IDkiLCIvKipcbiAqIENvcHlyaWdodCAyMDEzLXByZXNlbnQsIEZhY2Vib29rLCBJbmMuXG4gKiBBbGwgcmlnaHRzIHJlc2VydmVkLlxuICpcbiAqIFRoaXMgc291cmNlIGNvZGUgaXMgbGljZW5zZWQgdW5kZXIgdGhlIEJTRC1zdHlsZSBsaWNlbnNlIGZvdW5kIGluIHRoZVxuICogTElDRU5TRSBmaWxlIGluIHRoZSByb290IGRpcmVjdG9yeSBvZiB0aGlzIHNvdXJjZSB0cmVlLiBBbiBhZGRpdGlvbmFsIGdyYW50XG4gKiBvZiBwYXRlbnQgcmlnaHRzIGNhbiBiZSBmb3VuZCBpbiB0aGUgUEFURU5UUyBmaWxlIGluIHRoZSBzYW1lIGRpcmVjdG9yeS5cbiAqL1xuXG5pZiAocHJvY2Vzcy5lbnYuTk9ERV9FTlYgIT09ICdwcm9kdWN0aW9uJykge1xuICB2YXIgUkVBQ1RfRUxFTUVOVF9UWVBFID0gKHR5cGVvZiBTeW1ib2wgPT09ICdmdW5jdGlvbicgJiZcbiAgICBTeW1ib2wuZm9yICYmXG4gICAgU3ltYm9sLmZvcigncmVhY3QuZWxlbWVudCcpKSB8fFxuICAgIDB4ZWFjNztcblxuICB2YXIgaXNWYWxpZEVsZW1lbnQgPSBmdW5jdGlvbihvYmplY3QpIHtcbiAgICByZXR1cm4gdHlwZW9mIG9iamVjdCA9PT0gJ29iamVjdCcgJiZcbiAgICAgIG9iamVjdCAhPT0gbnVsbCAmJlxuICAgICAgb2JqZWN0LiQkdHlwZW9mID09PSBSRUFDVF9FTEVNRU5UX1RZUEU7XG4gIH07XG5cbiAgLy8gQnkgZXhwbGljaXRseSB1c2luZyBgcHJvcC10eXBlc2AgeW91IGFyZSBvcHRpbmcgaW50byBuZXcgZGV2ZWxvcG1lbnQgYmVoYXZpb3IuXG4gIC8vIGh0dHA6Ly9mYi5tZS9wcm9wLXR5cGVzLWluLXByb2RcbiAgdmFyIHRocm93T25EaXJlY3RBY2Nlc3MgPSB0cnVlO1xuICBtb2R1bGUuZXhwb3J0cyA9IHJlcXVpcmUoJy4vZmFjdG9yeVdpdGhUeXBlQ2hlY2tlcnMnKShpc1ZhbGlkRWxlbWVudCwgdGhyb3dPbkRpcmVjdEFjY2Vzcyk7XG59IGVsc2Uge1xuICAvLyBCeSBleHBsaWNpdGx5IHVzaW5nIGBwcm9wLXR5cGVzYCB5b3UgYXJlIG9wdGluZyBpbnRvIG5ldyBwcm9kdWN0aW9uIGJlaGF2aW9yLlxuICAvLyBodHRwOi8vZmIubWUvcHJvcC10eXBlcy1pbi1wcm9kXG4gIG1vZHVsZS5leHBvcnRzID0gcmVxdWlyZSgnLi9mYWN0b3J5V2l0aFRocm93aW5nU2hpbXMnKSgpO1xufVxuXG5cblxuLy8vLy8vLy8vLy8vLy8vLy8vXG4vLyBXRUJQQUNLIEZPT1RFUlxuLy8gLi9ub2RlX21vZHVsZXMvcHJvcC10eXBlcy9pbmRleC5qc1xuLy8gbW9kdWxlIGlkID0gMVxuLy8gbW9kdWxlIGNodW5rcyA9IDAgMSA0IDUgNiA3IDggOSIsImV4cG9ydCBkZWZhdWx0IHtcbiAgU09SVF9BU0M6ICdhc2MnLFxuICBTT1JUX0RFU0M6ICdkZXNjJyxcbiAgUk9XX1NFTEVDVF9TSU5HTEU6ICdyYWRpbycsXG4gIFJPV19TRUxFQ1RfTVVMVElQTEU6ICdjaGVja2JveCcsXG4gIFJPV19TRUxFQ1RfRElTQUJMRUQ6ICdST1dfU0VMRUNUX0RJU0FCTEVEJyxcbiAgQ0hFQ0tCT1hfU1RBVFVTX0NIRUNLRUQ6ICdjaGVja2VkJyxcbiAgQ0hFQ0tCT1hfU1RBVFVTX0lOREVURVJNSU5BVEU6ICdpbmRldGVybWluYXRlJyxcbiAgQ0hFQ0tCT1hfU1RBVFVTX1VOQ0hFQ0tFRDogJ3VuY2hlY2tlZCdcbn07XG5cblxuXG4vLyBXRUJQQUNLIEZPT1RFUiAvL1xuLy8gLi9wYWNrYWdlcy9yZWFjdC1ib290c3RyYXAtdGFibGUyL3NyYy9jb25zdC5qcyIsIi8qIGVzbGludCBuby1lbXB0eTogMCAqL1xuLyogZXNsaW50IG5vLXBhcmFtLXJlYXNzaWduOiAwICovXG4vKiBlc2xpbnQgcHJlZmVyLXJlc3QtcGFyYW1zOiAwICovXG5cbmZ1bmN0aW9uIHNwbGl0TmVzdGVkKHN0cikge1xuICByZXR1cm4gW3N0cl1cbiAgICAuam9pbignLicpXG4gICAgLnJlcGxhY2UoL1xcWy9nLCAnLicpXG4gICAgLnJlcGxhY2UoL1xcXS9nLCAnJylcbiAgICAuc3BsaXQoJy4nKTtcbn1cblxuZnVuY3Rpb24gZ2V0KHRhcmdldCwgZmllbGQpIHtcbiAgY29uc3QgcGF0aEFycmF5ID0gc3BsaXROZXN0ZWQoZmllbGQpO1xuICBsZXQgcmVzdWx0O1xuICB0cnkge1xuICAgIHJlc3VsdCA9IHBhdGhBcnJheS5yZWR1Y2UoKGN1cnIsIHBhdGgpID0+IGN1cnJbcGF0aF0sIHRhcmdldCk7XG4gIH0gY2F0Y2ggKGUpIHt9XG4gIHJldHVybiByZXN1bHQ7XG59XG5cbmZ1bmN0aW9uIHNldCh0YXJnZXQsIGZpZWxkLCB2YWx1ZSwgc2FmZSA9IGZhbHNlKSB7XG4gIGNvbnN0IHBhdGhBcnJheSA9IHNwbGl0TmVzdGVkKGZpZWxkKTtcbiAgbGV0IGxldmVsID0gMDtcbiAgcGF0aEFycmF5LnJlZHVjZSgoYSwgYikgPT4ge1xuICAgIGxldmVsICs9IDE7XG4gICAgaWYgKHR5cGVvZiBhW2JdID09PSAndW5kZWZpbmVkJykge1xuICAgICAgaWYgKCFzYWZlKSB0aHJvdyBuZXcgRXJyb3IoYCR7YX0uJHtifSBpcyB1bmRlZmluZWRgKTtcbiAgICAgIGFbYl0gPSB7fTtcbiAgICAgIHJldHVybiBhW2JdO1xuICAgIH1cblxuICAgIGlmIChsZXZlbCA9PT0gcGF0aEFycmF5Lmxlbmd0aCkge1xuICAgICAgYVtiXSA9IHZhbHVlO1xuICAgICAgcmV0dXJuIHZhbHVlO1xuICAgIH1cbiAgICByZXR1cm4gYVtiXTtcbiAgfSwgdGFyZ2V0KTtcbn1cblxuZnVuY3Rpb24gaXNGdW5jdGlvbihvYmopIHtcbiAgcmV0dXJuIG9iaiAmJiAodHlwZW9mIG9iaiA9PT0gJ2Z1bmN0aW9uJyk7XG59XG5cbi8qKlxuICogQ2hlY2tzIGlmIGB2YWx1ZWAgaXMgdGhlIE9iamVjdC4gdGhlIGBPYmplY3RgIGV4Y2VwdCBgRnVuY3Rpb25gIGFuZCBgQXJyYXkuYFxuICpcbiAqIEBwYXJhbSB7Kn0gb2JqIC0gVGhlIHZhbHVlIGdvbm5hIGNoZWNrXG4gKi9cbmZ1bmN0aW9uIGlzT2JqZWN0KG9iaikge1xuICBjb25zdCB0eXBlID0gdHlwZW9mIG9iajtcbiAgcmV0dXJuIG9iaiAhPT0gbnVsbCAmJiB0eXBlID09PSAnb2JqZWN0JyAmJiBvYmouY29uc3RydWN0b3IgPT09IE9iamVjdDtcbn1cblxuZnVuY3Rpb24gaXNFbXB0eU9iamVjdChvYmopIHtcbiAgaWYgKCFpc09iamVjdChvYmopKSByZXR1cm4gZmFsc2U7XG5cbiAgY29uc3QgaGFzT3duUHJvcGVydHkgPSBPYmplY3QucHJvdG90eXBlLmhhc093blByb3BlcnR5O1xuICBjb25zdCBrZXlzID0gT2JqZWN0LmtleXMob2JqKTtcblxuICBmb3IgKGxldCBpID0gMDsgaSA8IGtleXMubGVuZ3RoOyBpICs9IDEpIHtcbiAgICBpZiAoaGFzT3duUHJvcGVydHkuY2FsbChvYmosIGtleXNbaV0pKSByZXR1cm4gZmFsc2U7XG4gIH1cblxuICByZXR1cm4gdHJ1ZTtcbn1cblxuZnVuY3Rpb24gaXNEZWZpbmVkKHZhbHVlKSB7XG4gIHJldHVybiB0eXBlb2YgdmFsdWUgIT09ICd1bmRlZmluZWQnICYmIHZhbHVlICE9PSBudWxsO1xufVxuXG5mdW5jdGlvbiBzbGVlcChmbiwgbXMpIHtcbiAgcmV0dXJuIHNldFRpbWVvdXQoKCkgPT4gZm4oKSwgbXMpO1xufVxuXG5mdW5jdGlvbiBkZWJvdW5jZShmdW5jLCB3YWl0LCBpbW1lZGlhdGUpIHtcbiAgbGV0IHRpbWVvdXQ7XG5cbiAgcmV0dXJuICgpID0+IHtcbiAgICBjb25zdCBsYXRlciA9ICgpID0+IHtcbiAgICAgIHRpbWVvdXQgPSBudWxsO1xuXG4gICAgICBpZiAoIWltbWVkaWF0ZSkge1xuICAgICAgICBmdW5jLmFwcGx5KHRoaXMsIGFyZ3VtZW50cyk7XG4gICAgICB9XG4gICAgfTtcblxuICAgIGNvbnN0IGNhbGxOb3cgPSBpbW1lZGlhdGUgJiYgIXRpbWVvdXQ7XG5cbiAgICBjbGVhclRpbWVvdXQodGltZW91dCk7XG4gICAgdGltZW91dCA9IHNldFRpbWVvdXQobGF0ZXIsIHdhaXQgfHwgMCk7XG5cbiAgICBpZiAoY2FsbE5vdykge1xuICAgICAgZnVuYy5hcHB5KHRoaXMsIGFyZ3VtZW50cyk7XG4gICAgfVxuICB9O1xufVxuXG5leHBvcnQgZGVmYXVsdCB7XG4gIGdldCxcbiAgc2V0LFxuICBpc0Z1bmN0aW9uLFxuICBpc09iamVjdCxcbiAgaXNFbXB0eU9iamVjdCxcbiAgaXNEZWZpbmVkLFxuICBzbGVlcCxcbiAgZGVib3VuY2Vcbn07XG5cblxuXG4vLyBXRUJQQUNLIEZPT1RFUiAvL1xuLy8gLi9wYWNrYWdlcy9yZWFjdC1ib290c3RyYXAtdGFibGUyL3NyYy91dGlscy5qcyIsIi8qKlxuICogQ29weXJpZ2h0IDIwMTMtcHJlc2VudCwgRmFjZWJvb2ssIEluYy5cbiAqIEFsbCByaWdodHMgcmVzZXJ2ZWQuXG4gKlxuICogVGhpcyBzb3VyY2UgY29kZSBpcyBsaWNlbnNlZCB1bmRlciB0aGUgQlNELXN0eWxlIGxpY2Vuc2UgZm91bmQgaW4gdGhlXG4gKiBMSUNFTlNFIGZpbGUgaW4gdGhlIHJvb3QgZGlyZWN0b3J5IG9mIHRoaXMgc291cmNlIHRyZWUuIEFuIGFkZGl0aW9uYWwgZ3JhbnRcbiAqIG9mIHBhdGVudCByaWdodHMgY2FuIGJlIGZvdW5kIGluIHRoZSBQQVRFTlRTIGZpbGUgaW4gdGhlIHNhbWUgZGlyZWN0b3J5LlxuICovXG5cbid1c2Ugc3RyaWN0JztcblxudmFyIGVtcHR5RnVuY3Rpb24gPSByZXF1aXJlKCdmYmpzL2xpYi9lbXB0eUZ1bmN0aW9uJyk7XG52YXIgaW52YXJpYW50ID0gcmVxdWlyZSgnZmJqcy9saWIvaW52YXJpYW50Jyk7XG52YXIgUmVhY3RQcm9wVHlwZXNTZWNyZXQgPSByZXF1aXJlKCcuL2xpYi9SZWFjdFByb3BUeXBlc1NlY3JldCcpO1xuXG5tb2R1bGUuZXhwb3J0cyA9IGZ1bmN0aW9uKCkge1xuICBmdW5jdGlvbiBzaGltKHByb3BzLCBwcm9wTmFtZSwgY29tcG9uZW50TmFtZSwgbG9jYXRpb24sIHByb3BGdWxsTmFtZSwgc2VjcmV0KSB7XG4gICAgaWYgKHNlY3JldCA9PT0gUmVhY3RQcm9wVHlwZXNTZWNyZXQpIHtcbiAgICAgIC8vIEl0IGlzIHN0aWxsIHNhZmUgd2hlbiBjYWxsZWQgZnJvbSBSZWFjdC5cbiAgICAgIHJldHVybjtcbiAgICB9XG4gICAgaW52YXJpYW50KFxuICAgICAgZmFsc2UsXG4gICAgICAnQ2FsbGluZyBQcm9wVHlwZXMgdmFsaWRhdG9ycyBkaXJlY3RseSBpcyBub3Qgc3VwcG9ydGVkIGJ5IHRoZSBgcHJvcC10eXBlc2AgcGFja2FnZS4gJyArXG4gICAgICAnVXNlIFByb3BUeXBlcy5jaGVja1Byb3BUeXBlcygpIHRvIGNhbGwgdGhlbS4gJyArXG4gICAgICAnUmVhZCBtb3JlIGF0IGh0dHA6Ly9mYi5tZS91c2UtY2hlY2stcHJvcC10eXBlcydcbiAgICApO1xuICB9O1xuICBzaGltLmlzUmVxdWlyZWQgPSBzaGltO1xuICBmdW5jdGlvbiBnZXRTaGltKCkge1xuICAgIHJldHVybiBzaGltO1xuICB9O1xuICAvLyBJbXBvcnRhbnQhXG4gIC8vIEtlZXAgdGhpcyBsaXN0IGluIHN5bmMgd2l0aCBwcm9kdWN0aW9uIHZlcnNpb24gaW4gYC4vZmFjdG9yeVdpdGhUeXBlQ2hlY2tlcnMuanNgLlxuICB2YXIgUmVhY3RQcm9wVHlwZXMgPSB7XG4gICAgYXJyYXk6IHNoaW0sXG4gICAgYm9vbDogc2hpbSxcbiAgICBmdW5jOiBzaGltLFxuICAgIG51bWJlcjogc2hpbSxcbiAgICBvYmplY3Q6IHNoaW0sXG4gICAgc3RyaW5nOiBzaGltLFxuICAgIHN5bWJvbDogc2hpbSxcblxuICAgIGFueTogc2hpbSxcbiAgICBhcnJheU9mOiBnZXRTaGltLFxuICAgIGVsZW1lbnQ6IHNoaW0sXG4gICAgaW5zdGFuY2VPZjogZ2V0U2hpbSxcbiAgICBub2RlOiBzaGltLFxuICAgIG9iamVjdE9mOiBnZXRTaGltLFxuICAgIG9uZU9mOiBnZXRTaGltLFxuICAgIG9uZU9mVHlwZTogZ2V0U2hpbSxcbiAgICBzaGFwZTogZ2V0U2hpbVxuICB9O1xuXG4gIFJlYWN0UHJvcFR5cGVzLmNoZWNrUHJvcFR5cGVzID0gZW1wdHlGdW5jdGlvbjtcbiAgUmVhY3RQcm9wVHlwZXMuUHJvcFR5cGVzID0gUmVhY3RQcm9wVHlwZXM7XG5cbiAgcmV0dXJuIFJlYWN0UHJvcFR5cGVzO1xufTtcblxuXG5cbi8vLy8vLy8vLy8vLy8vLy8vL1xuLy8gV0VCUEFDSyBGT09URVJcbi8vIC4vbm9kZV9tb2R1bGVzL3Byb3AtdHlwZXMvZmFjdG9yeVdpdGhUaHJvd2luZ1NoaW1zLmpzXG4vLyBtb2R1bGUgaWQgPSA0XG4vLyBtb2R1bGUgY2h1bmtzID0gMCAxIDQgNSA2IDcgOCA5IiwiXCJ1c2Ugc3RyaWN0XCI7XG5cbi8qKlxuICogQ29weXJpZ2h0IChjKSAyMDEzLXByZXNlbnQsIEZhY2Vib29rLCBJbmMuXG4gKlxuICogVGhpcyBzb3VyY2UgY29kZSBpcyBsaWNlbnNlZCB1bmRlciB0aGUgTUlUIGxpY2Vuc2UgZm91bmQgaW4gdGhlXG4gKiBMSUNFTlNFIGZpbGUgaW4gdGhlIHJvb3QgZGlyZWN0b3J5IG9mIHRoaXMgc291cmNlIHRyZWUuXG4gKlxuICogXG4gKi9cblxuZnVuY3Rpb24gbWFrZUVtcHR5RnVuY3Rpb24oYXJnKSB7XG4gIHJldHVybiBmdW5jdGlvbiAoKSB7XG4gICAgcmV0dXJuIGFyZztcbiAgfTtcbn1cblxuLyoqXG4gKiBUaGlzIGZ1bmN0aW9uIGFjY2VwdHMgYW5kIGRpc2NhcmRzIGlucHV0czsgaXQgaGFzIG5vIHNpZGUgZWZmZWN0cy4gVGhpcyBpc1xuICogcHJpbWFyaWx5IHVzZWZ1bCBpZGlvbWF0aWNhbGx5IGZvciBvdmVycmlkYWJsZSBmdW5jdGlvbiBlbmRwb2ludHMgd2hpY2hcbiAqIGFsd2F5cyBuZWVkIHRvIGJlIGNhbGxhYmxlLCBzaW5jZSBKUyBsYWNrcyBhIG51bGwtY2FsbCBpZGlvbSBhbGEgQ29jb2EuXG4gKi9cbnZhciBlbXB0eUZ1bmN0aW9uID0gZnVuY3Rpb24gZW1wdHlGdW5jdGlvbigpIHt9O1xuXG5lbXB0eUZ1bmN0aW9uLnRoYXRSZXR1cm5zID0gbWFrZUVtcHR5RnVuY3Rpb247XG5lbXB0eUZ1bmN0aW9uLnRoYXRSZXR1cm5zRmFsc2UgPSBtYWtlRW1wdHlGdW5jdGlvbihmYWxzZSk7XG5lbXB0eUZ1bmN0aW9uLnRoYXRSZXR1cm5zVHJ1ZSA9IG1ha2VFbXB0eUZ1bmN0aW9uKHRydWUpO1xuZW1wdHlGdW5jdGlvbi50aGF0UmV0dXJuc051bGwgPSBtYWtlRW1wdHlGdW5jdGlvbihudWxsKTtcbmVtcHR5RnVuY3Rpb24udGhhdFJldHVybnNUaGlzID0gZnVuY3Rpb24gKCkge1xuICByZXR1cm4gdGhpcztcbn07XG5lbXB0eUZ1bmN0aW9uLnRoYXRSZXR1cm5zQXJndW1lbnQgPSBmdW5jdGlvbiAoYXJnKSB7XG4gIHJldHVybiBhcmc7XG59O1xuXG5tb2R1bGUuZXhwb3J0cyA9IGVtcHR5RnVuY3Rpb247XG5cblxuLy8vLy8vLy8vLy8vLy8vLy8vXG4vLyBXRUJQQUNLIEZPT1RFUlxuLy8gLi9ub2RlX21vZHVsZXMvZmJqcy9saWIvZW1wdHlGdW5jdGlvbi5qc1xuLy8gbW9kdWxlIGlkID0gNVxuLy8gbW9kdWxlIGNodW5rcyA9IDAgMSA0IDUgNiA3IDggOSIsIi8qKlxuICogQ29weXJpZ2h0IChjKSAyMDEzLXByZXNlbnQsIEZhY2Vib29rLCBJbmMuXG4gKlxuICogVGhpcyBzb3VyY2UgY29kZSBpcyBsaWNlbnNlZCB1bmRlciB0aGUgTUlUIGxpY2Vuc2UgZm91bmQgaW4gdGhlXG4gKiBMSUNFTlNFIGZpbGUgaW4gdGhlIHJvb3QgZGlyZWN0b3J5IG9mIHRoaXMgc291cmNlIHRyZWUuXG4gKlxuICovXG5cbid1c2Ugc3RyaWN0JztcblxuLyoqXG4gKiBVc2UgaW52YXJpYW50KCkgdG8gYXNzZXJ0IHN0YXRlIHdoaWNoIHlvdXIgcHJvZ3JhbSBhc3N1bWVzIHRvIGJlIHRydWUuXG4gKlxuICogUHJvdmlkZSBzcHJpbnRmLXN0eWxlIGZvcm1hdCAob25seSAlcyBpcyBzdXBwb3J0ZWQpIGFuZCBhcmd1bWVudHNcbiAqIHRvIHByb3ZpZGUgaW5mb3JtYXRpb24gYWJvdXQgd2hhdCBicm9rZSBhbmQgd2hhdCB5b3Ugd2VyZVxuICogZXhwZWN0aW5nLlxuICpcbiAqIFRoZSBpbnZhcmlhbnQgbWVzc2FnZSB3aWxsIGJlIHN0cmlwcGVkIGluIHByb2R1Y3Rpb24sIGJ1dCB0aGUgaW52YXJpYW50XG4gKiB3aWxsIHJlbWFpbiB0byBlbnN1cmUgbG9naWMgZG9lcyBub3QgZGlmZmVyIGluIHByb2R1Y3Rpb24uXG4gKi9cblxudmFyIHZhbGlkYXRlRm9ybWF0ID0gZnVuY3Rpb24gdmFsaWRhdGVGb3JtYXQoZm9ybWF0KSB7fTtcblxuaWYgKHByb2Nlc3MuZW52Lk5PREVfRU5WICE9PSAncHJvZHVjdGlvbicpIHtcbiAgdmFsaWRhdGVGb3JtYXQgPSBmdW5jdGlvbiB2YWxpZGF0ZUZvcm1hdChmb3JtYXQpIHtcbiAgICBpZiAoZm9ybWF0ID09PSB1bmRlZmluZWQpIHtcbiAgICAgIHRocm93IG5ldyBFcnJvcignaW52YXJpYW50IHJlcXVpcmVzIGFuIGVycm9yIG1lc3NhZ2UgYXJndW1lbnQnKTtcbiAgICB9XG4gIH07XG59XG5cbmZ1bmN0aW9uIGludmFyaWFudChjb25kaXRpb24sIGZvcm1hdCwgYSwgYiwgYywgZCwgZSwgZikge1xuICB2YWxpZGF0ZUZvcm1hdChmb3JtYXQpO1xuXG4gIGlmICghY29uZGl0aW9uKSB7XG4gICAgdmFyIGVycm9yO1xuICAgIGlmIChmb3JtYXQgPT09IHVuZGVmaW5lZCkge1xuICAgICAgZXJyb3IgPSBuZXcgRXJyb3IoJ01pbmlmaWVkIGV4Y2VwdGlvbiBvY2N1cnJlZDsgdXNlIHRoZSBub24tbWluaWZpZWQgZGV2IGVudmlyb25tZW50ICcgKyAnZm9yIHRoZSBmdWxsIGVycm9yIG1lc3NhZ2UgYW5kIGFkZGl0aW9uYWwgaGVscGZ1bCB3YXJuaW5ncy4nKTtcbiAgICB9IGVsc2Uge1xuICAgICAgdmFyIGFyZ3MgPSBbYSwgYiwgYywgZCwgZSwgZl07XG4gICAgICB2YXIgYXJnSW5kZXggPSAwO1xuICAgICAgZXJyb3IgPSBuZXcgRXJyb3IoZm9ybWF0LnJlcGxhY2UoLyVzL2csIGZ1bmN0aW9uICgpIHtcbiAgICAgICAgcmV0dXJuIGFyZ3NbYXJnSW5kZXgrK107XG4gICAgICB9KSk7XG4gICAgICBlcnJvci5uYW1lID0gJ0ludmFyaWFudCBWaW9sYXRpb24nO1xuICAgIH1cblxuICAgIGVycm9yLmZyYW1lc1RvUG9wID0gMTsgLy8gd2UgZG9uJ3QgY2FyZSBhYm91dCBpbnZhcmlhbnQncyBvd24gZnJhbWVcbiAgICB0aHJvdyBlcnJvcjtcbiAgfVxufVxuXG5tb2R1bGUuZXhwb3J0cyA9IGludmFyaWFudDtcblxuXG4vLy8vLy8vLy8vLy8vLy8vLy9cbi8vIFdFQlBBQ0sgRk9PVEVSXG4vLyAuL25vZGVfbW9kdWxlcy9mYmpzL2xpYi9pbnZhcmlhbnQuanNcbi8vIG1vZHVsZSBpZCA9IDZcbi8vIG1vZHVsZSBjaHVua3MgPSAwIDEgNCA1IDYgNyA4IDkiLCIvKipcbiAqIENvcHlyaWdodCAyMDEzLXByZXNlbnQsIEZhY2Vib29rLCBJbmMuXG4gKiBBbGwgcmlnaHRzIHJlc2VydmVkLlxuICpcbiAqIFRoaXMgc291cmNlIGNvZGUgaXMgbGljZW5zZWQgdW5kZXIgdGhlIEJTRC1zdHlsZSBsaWNlbnNlIGZvdW5kIGluIHRoZVxuICogTElDRU5TRSBmaWxlIGluIHRoZSByb290IGRpcmVjdG9yeSBvZiB0aGlzIHNvdXJjZSB0cmVlLiBBbiBhZGRpdGlvbmFsIGdyYW50XG4gKiBvZiBwYXRlbnQgcmlnaHRzIGNhbiBiZSBmb3VuZCBpbiB0aGUgUEFURU5UUyBmaWxlIGluIHRoZSBzYW1lIGRpcmVjdG9yeS5cbiAqL1xuXG4ndXNlIHN0cmljdCc7XG5cbnZhciBSZWFjdFByb3BUeXBlc1NlY3JldCA9ICdTRUNSRVRfRE9fTk9UX1BBU1NfVEhJU19PUl9ZT1VfV0lMTF9CRV9GSVJFRCc7XG5cbm1vZHVsZS5leHBvcnRzID0gUmVhY3RQcm9wVHlwZXNTZWNyZXQ7XG5cblxuXG4vLy8vLy8vLy8vLy8vLy8vLy9cbi8vIFdFQlBBQ0sgRk9PVEVSXG4vLyAuL25vZGVfbW9kdWxlcy9wcm9wLXR5cGVzL2xpYi9SZWFjdFByb3BUeXBlc1NlY3JldC5qc1xuLy8gbW9kdWxlIGlkID0gN1xuLy8gbW9kdWxlIGNodW5rcyA9IDAgMSA0IDUgNiA3IDggOSIsIi8qIVxuICBDb3B5cmlnaHQgKGMpIDIwMTYgSmVkIFdhdHNvbi5cbiAgTGljZW5zZWQgdW5kZXIgdGhlIE1JVCBMaWNlbnNlIChNSVQpLCBzZWVcbiAgaHR0cDovL2plZHdhdHNvbi5naXRodWIuaW8vY2xhc3NuYW1lc1xuKi9cbi8qIGdsb2JhbCBkZWZpbmUgKi9cblxuKGZ1bmN0aW9uICgpIHtcblx0J3VzZSBzdHJpY3QnO1xuXG5cdHZhciBoYXNPd24gPSB7fS5oYXNPd25Qcm9wZXJ0eTtcblxuXHRmdW5jdGlvbiBjbGFzc05hbWVzICgpIHtcblx0XHR2YXIgY2xhc3NlcyA9IFtdO1xuXG5cdFx0Zm9yICh2YXIgaSA9IDA7IGkgPCBhcmd1bWVudHMubGVuZ3RoOyBpKyspIHtcblx0XHRcdHZhciBhcmcgPSBhcmd1bWVudHNbaV07XG5cdFx0XHRpZiAoIWFyZykgY29udGludWU7XG5cblx0XHRcdHZhciBhcmdUeXBlID0gdHlwZW9mIGFyZztcblxuXHRcdFx0aWYgKGFyZ1R5cGUgPT09ICdzdHJpbmcnIHx8IGFyZ1R5cGUgPT09ICdudW1iZXInKSB7XG5cdFx0XHRcdGNsYXNzZXMucHVzaChhcmcpO1xuXHRcdFx0fSBlbHNlIGlmIChBcnJheS5pc0FycmF5KGFyZykpIHtcblx0XHRcdFx0Y2xhc3Nlcy5wdXNoKGNsYXNzTmFtZXMuYXBwbHkobnVsbCwgYXJnKSk7XG5cdFx0XHR9IGVsc2UgaWYgKGFyZ1R5cGUgPT09ICdvYmplY3QnKSB7XG5cdFx0XHRcdGZvciAodmFyIGtleSBpbiBhcmcpIHtcblx0XHRcdFx0XHRpZiAoaGFzT3duLmNhbGwoYXJnLCBrZXkpICYmIGFyZ1trZXldKSB7XG5cdFx0XHRcdFx0XHRjbGFzc2VzLnB1c2goa2V5KTtcblx0XHRcdFx0XHR9XG5cdFx0XHRcdH1cblx0XHRcdH1cblx0XHR9XG5cblx0XHRyZXR1cm4gY2xhc3Nlcy5qb2luKCcgJyk7XG5cdH1cblxuXHRpZiAodHlwZW9mIG1vZHVsZSAhPT0gJ3VuZGVmaW5lZCcgJiYgbW9kdWxlLmV4cG9ydHMpIHtcblx0XHRtb2R1bGUuZXhwb3J0cyA9IGNsYXNzTmFtZXM7XG5cdH0gZWxzZSBpZiAodHlwZW9mIGRlZmluZSA9PT0gJ2Z1bmN0aW9uJyAmJiB0eXBlb2YgZGVmaW5lLmFtZCA9PT0gJ29iamVjdCcgJiYgZGVmaW5lLmFtZCkge1xuXHRcdC8vIHJlZ2lzdGVyIGFzICdjbGFzc25hbWVzJywgY29uc2lzdGVudCB3aXRoIG5wbSBwYWNrYWdlIG5hbWVcblx0XHRkZWZpbmUoJ2NsYXNzbmFtZXMnLCBbXSwgZnVuY3Rpb24gKCkge1xuXHRcdFx0cmV0dXJuIGNsYXNzTmFtZXM7XG5cdFx0fSk7XG5cdH0gZWxzZSB7XG5cdFx0d2luZG93LmNsYXNzTmFtZXMgPSBjbGFzc05hbWVzO1xuXHR9XG59KCkpO1xuXG5cblxuLy8vLy8vLy8vLy8vLy8vLy8vXG4vLyBXRUJQQUNLIEZPT1RFUlxuLy8gLi9wYWNrYWdlcy9yZWFjdC1ib290c3RyYXAtdGFibGUyL25vZGVfbW9kdWxlcy9jbGFzc25hbWVzL2luZGV4LmpzXG4vLyBtb2R1bGUgaWQgPSAxMlxuLy8gbW9kdWxlIGNodW5rcyA9IDAgMSIsIlxuZXhwb3J0IGNvbnN0IG1hdGNoUm93ID0gKGtleUZpZWxkLCBpZCkgPT4gcm93ID0+IHJvd1trZXlGaWVsZF0gPT09IGlkO1xuXG5leHBvcnQgY29uc3QgZ2V0Um93QnlSb3dJZCA9ICh7IGRhdGEsIGtleUZpZWxkIH0pID0+IGlkID0+IGRhdGEuZmluZChtYXRjaFJvdyhrZXlGaWVsZCwgaWQpKTtcblxuXG5cbi8vIFdFQlBBQ0sgRk9PVEVSIC8vXG4vLyAuL3BhY2thZ2VzL3JlYWN0LWJvb3RzdHJhcC10YWJsZTIvc3JjL3N0b3JlL3Jvd3MuanMiLCJpbXBvcnQgXyBmcm9tICcuLi91dGlscyc7XG5pbXBvcnQgeyBnZXRSb3dCeVJvd0lkIH0gZnJvbSAnLi9yb3dzJztcblxuZXhwb3J0IGNvbnN0IGlzU2VsZWN0ZWRBbGwgPSAoeyBkYXRhLCBzZWxlY3RlZCB9KSA9PiBkYXRhLmxlbmd0aCA9PT0gc2VsZWN0ZWQubGVuZ3RoO1xuXG5leHBvcnQgY29uc3QgaXNBbnlTZWxlY3RlZFJvdyA9ICh7IHNlbGVjdGVkIH0pID0+IChza2lwcyA9IFtdKSA9PiB7XG4gIGlmIChza2lwcy5sZW5ndGggPT09IDApIHtcbiAgICByZXR1cm4gc2VsZWN0ZWQubGVuZ3RoID4gMDtcbiAgfVxuICByZXR1cm4gc2VsZWN0ZWQuZmlsdGVyKHggPT4gIXNraXBzLmluY2x1ZGVzKHgpKS5sZW5ndGg7XG59O1xuXG5leHBvcnQgY29uc3Qgc2VsZWN0YWJsZUtleXMgPSAoeyBkYXRhLCBrZXlGaWVsZCB9KSA9PiAoc2tpcHMgPSBbXSkgPT4ge1xuICBpZiAoc2tpcHMubGVuZ3RoID09PSAwKSB7XG4gICAgcmV0dXJuIGRhdGEubWFwKHJvdyA9PiBfLmdldChyb3csIGtleUZpZWxkKSk7XG4gIH1cbiAgcmV0dXJuIGRhdGFcbiAgICAuZmlsdGVyKHJvdyA9PiAhc2tpcHMuaW5jbHVkZXMoXy5nZXQocm93LCBrZXlGaWVsZCkpKVxuICAgIC5tYXAocm93ID0+IF8uZ2V0KHJvdywga2V5RmllbGQpKTtcbn07XG5cbmV4cG9ydCBjb25zdCB1blNlbGVjdGFibGVLZXlzID0gKHsgc2VsZWN0ZWQgfSkgPT4gKHNraXBzID0gW10pID0+IHtcbiAgaWYgKHNraXBzLmxlbmd0aCA9PT0gMCkge1xuICAgIHJldHVybiBbXTtcbiAgfVxuICByZXR1cm4gc2VsZWN0ZWQuZmlsdGVyKHggPT4gc2tpcHMuaW5jbHVkZXMoeCkpO1xufTtcblxuZXhwb3J0IGNvbnN0IGdldFNlbGVjdGVkUm93cyA9IChzdG9yZSkgPT4ge1xuICBjb25zdCBnZXRSb3cgPSBnZXRSb3dCeVJvd0lkKHN0b3JlKTtcbiAgcmV0dXJuIHN0b3JlLnNlbGVjdGVkLm1hcChrID0+IGdldFJvdyhrKSk7XG59O1xuXG5cblxuXG4vLyBXRUJQQUNLIEZPT1RFUiAvL1xuLy8gLi9wYWNrYWdlcy9yZWFjdC1ib290c3RyYXAtdGFibGUyL3NyYy9zdG9yZS9zZWxlY3Rpb24uanMiLCJpbXBvcnQgXyBmcm9tICcuLi91dGlscyc7XG5cbmV4cG9ydCBkZWZhdWx0IEV4dGVuZEJhc2UgPT5cbiAgY2xhc3MgUmVtb3RlUmVzb2x2ZXIgZXh0ZW5kcyBFeHRlbmRCYXNlIHtcbiAgICBnZXROZXdlc3RTdGF0ZShzdGF0ZSA9IHt9KSB7XG4gICAgICBjb25zdCBzdG9yZSA9IHRoaXMuc3RvcmUgfHwgdGhpcy5wcm9wcy5zdG9yZTtcbiAgICAgIHJldHVybiB7XG4gICAgICAgIHBhZ2U6IHN0b3JlLnBhZ2UsXG4gICAgICAgIHNpemVQZXJQYWdlOiBzdG9yZS5zaXplUGVyUGFnZSxcbiAgICAgICAgZmlsdGVyczogc3RvcmUuZmlsdGVycyxcbiAgICAgICAgc29ydEZpZWxkOiBzdG9yZS5zb3J0RmllbGQsXG4gICAgICAgIHNvcnRPcmRlcjogc3RvcmUuc29ydE9yZGVyLFxuICAgICAgICBkYXRhOiBzdG9yZS5nZXRBbGxEYXRhKCksXG4gICAgICAgIC4uLnN0YXRlXG4gICAgICB9O1xuICAgIH1cblxuICAgIGlzUmVtb3RlUGFnaW5hdGlvbigpIHtcbiAgICAgIGNvbnN0IHsgcmVtb3RlIH0gPSB0aGlzLnByb3BzO1xuICAgICAgcmV0dXJuIHJlbW90ZSA9PT0gdHJ1ZSB8fCAoXy5pc09iamVjdChyZW1vdGUpICYmIHJlbW90ZS5wYWdpbmF0aW9uKTtcbiAgICB9XG5cbiAgICBpc1JlbW90ZUZpbHRlcmluZygpIHtcbiAgICAgIGNvbnN0IHsgcmVtb3RlIH0gPSB0aGlzLnByb3BzO1xuICAgICAgcmV0dXJuIHJlbW90ZSA9PT0gdHJ1ZSB8fCAoXy5pc09iamVjdChyZW1vdGUpICYmIHJlbW90ZS5maWx0ZXIpO1xuICAgIH1cblxuICAgIGlzUmVtb3RlU29ydCgpIHtcbiAgICAgIGNvbnN0IHsgcmVtb3RlIH0gPSB0aGlzLnByb3BzO1xuICAgICAgcmV0dXJuIHJlbW90ZSA9PT0gdHJ1ZSB8fCAoXy5pc09iamVjdChyZW1vdGUpICYmIHJlbW90ZS5zb3J0KTtcbiAgICB9XG5cbiAgICBpc1JlbW90ZUNlbGxFZGl0KCkge1xuICAgICAgY29uc3QgeyByZW1vdGUgfSA9IHRoaXMucHJvcHM7XG4gICAgICByZXR1cm4gcmVtb3RlID09PSB0cnVlIHx8IChfLmlzT2JqZWN0KHJlbW90ZSkgJiYgcmVtb3RlLmNlbGxFZGl0KTtcbiAgICB9XG5cbiAgICBoYW5kbGVSZW1vdGVQYWdlQ2hhbmdlKCkge1xuICAgICAgdGhpcy5wcm9wcy5vblRhYmxlQ2hhbmdlKCdwYWdpbmF0aW9uJywgdGhpcy5nZXROZXdlc3RTdGF0ZSgpKTtcbiAgICB9XG5cbiAgICBoYW5kbGVSZW1vdGVGaWx0ZXJDaGFuZ2UoKSB7XG4gICAgICBjb25zdCBuZXdTdGF0ZSA9IHt9O1xuICAgICAgaWYgKHRoaXMuaXNSZW1vdGVQYWdpbmF0aW9uKCkpIHtcbiAgICAgICAgY29uc3Qgb3B0aW9ucyA9IHRoaXMucHJvcHMucGFnaW5hdGlvbi5vcHRpb25zIHx8IHt9O1xuICAgICAgICBuZXdTdGF0ZS5wYWdlID0gXy5pc0RlZmluZWQob3B0aW9ucy5wYWdlU3RhcnRJbmRleCkgPyBvcHRpb25zLnBhZ2VTdGFydEluZGV4IDogMTtcbiAgICAgIH1cbiAgICAgIHRoaXMucHJvcHMub25UYWJsZUNoYW5nZSgnZmlsdGVyJywgdGhpcy5nZXROZXdlc3RTdGF0ZShuZXdTdGF0ZSkpO1xuICAgIH1cblxuICAgIGhhbmRsZVNvcnRDaGFuZ2UoKSB7XG4gICAgICB0aGlzLnByb3BzLm9uVGFibGVDaGFuZ2UoJ3NvcnQnLCB0aGlzLmdldE5ld2VzdFN0YXRlKCkpO1xuICAgIH1cblxuICAgIGhhbmRsZUNlbGxDaGFuZ2Uocm93SWQsIGRhdGFGaWVsZCwgbmV3VmFsdWUpIHtcbiAgICAgIGNvbnN0IGNlbGxFZGl0ID0geyByb3dJZCwgZGF0YUZpZWxkLCBuZXdWYWx1ZSB9O1xuICAgICAgdGhpcy5wcm9wcy5vblRhYmxlQ2hhbmdlKCdjZWxsRWRpdCcsIHRoaXMuZ2V0TmV3ZXN0U3RhdGUoeyBjZWxsRWRpdCB9KSk7XG4gICAgfVxuICB9O1xuXG5cblxuLy8gV0VCUEFDSyBGT09URVIgLy9cbi8vIC4vcGFja2FnZXMvcmVhY3QtYm9vdHN0cmFwLXRhYmxlMi9zcmMvcHJvcHMtcmVzb2x2ZXIvcmVtb3RlLXJlc29sdmVyLmpzIiwiaW1wb3J0IEJvb3RzdHJhcFRhYmxlIGZyb20gJy4vc3JjL2Jvb3RzdHJhcC10YWJsZSc7XG5pbXBvcnQgd2l0aERhdGFTdG9yZSBmcm9tICcuL3NyYy9jb250YWluZXInO1xuXG5leHBvcnQgZGVmYXVsdCB3aXRoRGF0YVN0b3JlKEJvb3RzdHJhcFRhYmxlKTtcblxuXG5cblxuLy8gV0VCUEFDSyBGT09URVIgLy9cbi8vIC4vcGFja2FnZXMvcmVhY3QtYm9vdHN0cmFwLXRhYmxlMi9pbmRleC5qcyIsIi8qIGVzbGludCBhcnJvdy1ib2R5LXN0eWxlOiAwICovXG5cbmltcG9ydCBSZWFjdCwgeyBDb21wb25lbnQgfSBmcm9tICdyZWFjdCc7XG5pbXBvcnQgUHJvcFR5cGVzIGZyb20gJ3Byb3AtdHlwZXMnO1xuaW1wb3J0IGNzIGZyb20gJ2NsYXNzbmFtZXMnO1xuXG5pbXBvcnQgSGVhZGVyIGZyb20gJy4vaGVhZGVyJztcbmltcG9ydCBDYXB0aW9uIGZyb20gJy4vY2FwdGlvbic7XG5pbXBvcnQgQm9keSBmcm9tICcuL2JvZHknO1xuaW1wb3J0IFByb3BzQmFzZVJlc29sdmVyIGZyb20gJy4vcHJvcHMtcmVzb2x2ZXInO1xuaW1wb3J0IENvbnN0IGZyb20gJy4vY29uc3QnO1xuaW1wb3J0IHsgaXNTZWxlY3RlZEFsbCB9IGZyb20gJy4vc3RvcmUvc2VsZWN0aW9uJztcblxuY2xhc3MgQm9vdHN0cmFwVGFibGUgZXh0ZW5kcyBQcm9wc0Jhc2VSZXNvbHZlcihDb21wb25lbnQpIHtcbiAgY29uc3RydWN0b3IocHJvcHMpIHtcbiAgICBzdXBlcihwcm9wcyk7XG4gICAgdGhpcy52YWxpZGF0ZVByb3BzKCk7XG5cbiAgICB0aGlzLnN0YXRlID0ge1xuICAgICAgZGF0YTogcHJvcHMuZGF0YVxuICAgIH07XG4gIH1cblxuICBjb21wb25lbnRXaWxsUmVjZWl2ZVByb3BzKG5leHRQcm9wcykge1xuICAgIHRoaXMuc2V0U3RhdGUoe1xuICAgICAgZGF0YTogbmV4dFByb3BzLmRhdGFcbiAgICB9KTtcbiAgfVxuXG4gIHJlbmRlcigpIHtcbiAgICBjb25zdCB7IGxvYWRpbmcsIG92ZXJsYXkgfSA9IHRoaXMucHJvcHM7XG4gICAgY29uc3QgdGFibGUgPSB0aGlzLnJlbmRlclRhYmxlKCk7XG4gICAgaWYgKGxvYWRpbmcgJiYgb3ZlcmxheSkge1xuICAgICAgY29uc3QgTG9hZGluZ092ZXJsYXkgPSBvdmVybGF5KHRhYmxlLCBsb2FkaW5nKTtcbiAgICAgIHJldHVybiA8TG9hZGluZ092ZXJsYXkgLz47XG4gICAgfVxuICAgIHJldHVybiB0YWJsZTtcbiAgfVxuXG4gIHJlbmRlclRhYmxlKCkge1xuICAgIGNvbnN0IHtcbiAgICAgIHN0b3JlLFxuICAgICAgY29sdW1ucyxcbiAgICAgIGtleUZpZWxkLFxuICAgICAgc3RyaXBlZCxcbiAgICAgIGhvdmVyLFxuICAgICAgYm9yZGVyZWQsXG4gICAgICBjb25kZW5zZWQsXG4gICAgICBub0RhdGFJbmRpY2F0aW9uLFxuICAgICAgY2FwdGlvbixcbiAgICAgIHJvd1N0eWxlLFxuICAgICAgcm93Q2xhc3NlcyxcbiAgICAgIHJvd0V2ZW50c1xuICAgIH0gPSB0aGlzLnByb3BzO1xuXG4gICAgY29uc3QgdGFibGVDbGFzcyA9IGNzKCd0YWJsZScsIHtcbiAgICAgICd0YWJsZS1zdHJpcGVkJzogc3RyaXBlZCxcbiAgICAgICd0YWJsZS1ob3Zlcic6IGhvdmVyLFxuICAgICAgJ3RhYmxlLWJvcmRlcmVkJzogYm9yZGVyZWQsXG4gICAgICAndGFibGUtY29uZGVuc2VkJzogY29uZGVuc2VkXG4gICAgfSk7XG5cbiAgICBjb25zdCBjZWxsU2VsZWN0aW9uSW5mbyA9IHRoaXMucmVzb2x2ZVNlbGVjdFJvd1Byb3BzKHtcbiAgICAgIG9uUm93U2VsZWN0OiB0aGlzLnByb3BzLm9uUm93U2VsZWN0XG4gICAgfSk7XG5cbiAgICBjb25zdCBoZWFkZXJDZWxsU2VsZWN0aW9uSW5mbyA9IHRoaXMucmVzb2x2ZVNlbGVjdFJvd1Byb3BzRm9ySGVhZGVyKHtcbiAgICAgIG9uQWxsUm93c1NlbGVjdDogdGhpcy5wcm9wcy5vbkFsbFJvd3NTZWxlY3QsXG4gICAgICBzZWxlY3RlZDogc3RvcmUuc2VsZWN0ZWQsXG4gICAgICBhbGxSb3dzU2VsZWN0ZWQ6IGlzU2VsZWN0ZWRBbGwoc3RvcmUpXG4gICAgfSk7XG5cbiAgICBjb25zdCB0YWJsZUNhcHRpb24gPSAoY2FwdGlvbiAmJiA8Q2FwdGlvbj57IGNhcHRpb24gfTwvQ2FwdGlvbj4pO1xuXG4gICAgcmV0dXJuIChcbiAgICAgIDxkaXYgY2xhc3NOYW1lPVwicmVhY3QtYm9vdHN0cmFwLXRhYmxlXCI+XG4gICAgICAgIDx0YWJsZSBjbGFzc05hbWU9eyB0YWJsZUNsYXNzIH0+XG4gICAgICAgICAgeyB0YWJsZUNhcHRpb24gfVxuICAgICAgICAgIDxIZWFkZXJcbiAgICAgICAgICAgIGNvbHVtbnM9eyBjb2x1bW5zIH1cbiAgICAgICAgICAgIHNvcnRGaWVsZD17IHN0b3JlLnNvcnRGaWVsZCB9XG4gICAgICAgICAgICBzb3J0T3JkZXI9eyBzdG9yZS5zb3J0T3JkZXIgfVxuICAgICAgICAgICAgb25Tb3J0PXsgdGhpcy5wcm9wcy5vblNvcnQgfVxuICAgICAgICAgICAgb25GaWx0ZXI9eyB0aGlzLnByb3BzLm9uRmlsdGVyIH1cbiAgICAgICAgICAgIHNlbGVjdFJvdz17IGhlYWRlckNlbGxTZWxlY3Rpb25JbmZvIH1cbiAgICAgICAgICAvPlxuICAgICAgICAgIDxCb2R5XG4gICAgICAgICAgICBkYXRhPXsgdGhpcy5zdGF0ZS5kYXRhIH1cbiAgICAgICAgICAgIGtleUZpZWxkPXsga2V5RmllbGQgfVxuICAgICAgICAgICAgY29sdW1ucz17IGNvbHVtbnMgfVxuICAgICAgICAgICAgaXNFbXB0eT17IHRoaXMuaXNFbXB0eSgpIH1cbiAgICAgICAgICAgIHZpc2libGVDb2x1bW5TaXplPXsgdGhpcy52aXNpYmxlQ29sdW1uU2l6ZSgpIH1cbiAgICAgICAgICAgIG5vRGF0YUluZGljYXRpb249eyBub0RhdGFJbmRpY2F0aW9uIH1cbiAgICAgICAgICAgIGNlbGxFZGl0PXsgdGhpcy5wcm9wcy5jZWxsRWRpdCB8fCB7fSB9XG4gICAgICAgICAgICBzZWxlY3RSb3c9eyBjZWxsU2VsZWN0aW9uSW5mbyB9XG4gICAgICAgICAgICBzZWxlY3RlZFJvd0tleXM9eyBzdG9yZS5zZWxlY3RlZCB9XG4gICAgICAgICAgICByb3dTdHlsZT17IHJvd1N0eWxlIH1cbiAgICAgICAgICAgIHJvd0NsYXNzZXM9eyByb3dDbGFzc2VzIH1cbiAgICAgICAgICAgIHJvd0V2ZW50cz17IHJvd0V2ZW50cyB9XG4gICAgICAgICAgLz5cbiAgICAgICAgPC90YWJsZT5cbiAgICAgIDwvZGl2PlxuICAgICk7XG4gIH1cbn1cblxuQm9vdHN0cmFwVGFibGUucHJvcFR5cGVzID0ge1xuICBrZXlGaWVsZDogUHJvcFR5cGVzLnN0cmluZy5pc1JlcXVpcmVkLFxuICBkYXRhOiBQcm9wVHlwZXMuYXJyYXkuaXNSZXF1aXJlZCxcbiAgY29sdW1uczogUHJvcFR5cGVzLmFycmF5LmlzUmVxdWlyZWQsXG4gIHJlbW90ZTogUHJvcFR5cGVzLm9uZU9mVHlwZShbUHJvcFR5cGVzLmJvb2wsIFByb3BUeXBlcy5zaGFwZSh7XG4gICAgcGFnaW5hdGlvbjogUHJvcFR5cGVzLmJvb2xcbiAgfSldKSxcbiAgc3RvcmU6IFByb3BUeXBlcy5vYmplY3QsXG4gIG5vRGF0YUluZGljYXRpb246IFByb3BUeXBlcy5vbmVPZlR5cGUoW1Byb3BUeXBlcy5zdHJpbmcsIFByb3BUeXBlcy5mdW5jXSksXG4gIHN0cmlwZWQ6IFByb3BUeXBlcy5ib29sLFxuICBib3JkZXJlZDogUHJvcFR5cGVzLmJvb2wsXG4gIGhvdmVyOiBQcm9wVHlwZXMuYm9vbCxcbiAgY29uZGVuc2VkOiBQcm9wVHlwZXMuYm9vbCxcbiAgY2FwdGlvbjogUHJvcFR5cGVzLm9uZU9mVHlwZShbXG4gICAgUHJvcFR5cGVzLm5vZGUsXG4gICAgUHJvcFR5cGVzLnN0cmluZ1xuICBdKSxcbiAgcGFnaW5hdGlvbjogUHJvcFR5cGVzLm9iamVjdCxcbiAgZmlsdGVyOiBQcm9wVHlwZXMub2JqZWN0LFxuICBjZWxsRWRpdDogUHJvcFR5cGVzLm9iamVjdCxcbiAgc2VsZWN0Um93OiBQcm9wVHlwZXMuc2hhcGUoe1xuICAgIG1vZGU6IFByb3BUeXBlcy5vbmVPZihbQ29uc3QuUk9XX1NFTEVDVF9TSU5HTEUsIENvbnN0LlJPV19TRUxFQ1RfTVVMVElQTEVdKS5pc1JlcXVpcmVkLFxuICAgIGNsaWNrVG9TZWxlY3Q6IFByb3BUeXBlcy5ib29sLFxuICAgIGNsaWNrVG9FZGl0OiBQcm9wVHlwZXMuYm9vbCxcbiAgICBvblNlbGVjdDogUHJvcFR5cGVzLmZ1bmMsXG4gICAgb25TZWxlY3RBbGw6IFByb3BUeXBlcy5mdW5jLFxuICAgIHN0eWxlOiBQcm9wVHlwZXMub25lT2ZUeXBlKFtQcm9wVHlwZXMub2JqZWN0LCBQcm9wVHlwZXMuZnVuY10pLFxuICAgIGNsYXNzZXM6IFByb3BUeXBlcy5vbmVPZlR5cGUoW1Byb3BUeXBlcy5zdHJpbmcsIFByb3BUeXBlcy5mdW5jXSksXG4gICAgbm9uU2VsZWN0YWJsZTogUHJvcFR5cGVzLmFycmF5LFxuICAgIGJnQ29sb3I6IFByb3BUeXBlcy5vbmVPZlR5cGUoW1Byb3BUeXBlcy5zdHJpbmcsIFByb3BUeXBlcy5mdW5jXSksXG4gICAgaGlkZVNlbGVjdENvbHVtbjogUHJvcFR5cGVzLmJvb2xcbiAgfSksXG4gIG9uUm93U2VsZWN0OiBQcm9wVHlwZXMuZnVuYyxcbiAgb25BbGxSb3dzU2VsZWN0OiBQcm9wVHlwZXMuZnVuYyxcbiAgcm93U3R5bGU6IFByb3BUeXBlcy5vbmVPZlR5cGUoW1Byb3BUeXBlcy5vYmplY3QsIFByb3BUeXBlcy5mdW5jXSksXG4gIHJvd0V2ZW50czogUHJvcFR5cGVzLm9iamVjdCxcbiAgcm93Q2xhc3NlczogUHJvcFR5cGVzLm9uZU9mVHlwZShbUHJvcFR5cGVzLnN0cmluZywgUHJvcFR5cGVzLmZ1bmNdKSxcbiAgZGVmYXVsdFNvcnRlZDogUHJvcFR5cGVzLmFycmF5T2YoUHJvcFR5cGVzLnNoYXBlKHtcbiAgICBkYXRhRmllbGQ6IFByb3BUeXBlcy5zdHJpbmcuaXNSZXF1aXJlZCxcbiAgICBvcmRlcjogUHJvcFR5cGVzLm9uZU9mKFtDb25zdC5TT1JUX0RFU0MsIENvbnN0LlNPUlRfQVNDXSkuaXNSZXF1aXJlZFxuICB9KSksXG4gIG92ZXJsYXk6IFByb3BUeXBlcy5mdW5jLFxuICBvblRhYmxlQ2hhbmdlOiBQcm9wVHlwZXMuZnVuYyxcbiAgb25Tb3J0OiBQcm9wVHlwZXMuZnVuYyxcbiAgb25GaWx0ZXI6IFByb3BUeXBlcy5mdW5jXG59O1xuXG5Cb290c3RyYXBUYWJsZS5kZWZhdWx0UHJvcHMgPSB7XG4gIHJlbW90ZTogZmFsc2UsXG4gIHN0cmlwZWQ6IGZhbHNlLFxuICBib3JkZXJlZDogdHJ1ZSxcbiAgaG92ZXI6IGZhbHNlLFxuICBjb25kZW5zZWQ6IGZhbHNlLFxuICBub0RhdGFJbmRpY2F0aW9uOiBudWxsXG59O1xuXG5leHBvcnQgZGVmYXVsdCBCb290c3RyYXBUYWJsZTtcblxuXG5cbi8vIFdFQlBBQ0sgRk9PVEVSIC8vXG4vLyAuL3BhY2thZ2VzL3JlYWN0LWJvb3RzdHJhcC10YWJsZTIvc3JjL2Jvb3RzdHJhcC10YWJsZS5qcyIsIi8qIGVzbGludCByZWFjdC9yZXF1aXJlLWRlZmF1bHQtcHJvcHM6IDAgKi9cbmltcG9ydCBSZWFjdCBmcm9tICdyZWFjdCc7XG5pbXBvcnQgUHJvcFR5cGVzIGZyb20gJ3Byb3AtdHlwZXMnO1xuaW1wb3J0IENvbnN0IGZyb20gJy4vY29uc3QnO1xuXG5pbXBvcnQgSGVhZGVyQ2VsbCBmcm9tICcuL2hlYWRlci1jZWxsJztcbmltcG9ydCBTZWxlY3Rpb25IZWFkZXJDZWxsIGZyb20gJy4vcm93LXNlbGVjdGlvbi9zZWxlY3Rpb24taGVhZGVyLWNlbGwnO1xuXG5jb25zdCBIZWFkZXIgPSAocHJvcHMpID0+IHtcbiAgY29uc3QgeyBST1dfU0VMRUNUX0RJU0FCTEVEIH0gPSBDb25zdDtcblxuICBjb25zdCB7XG4gICAgY29sdW1ucyxcbiAgICBvblNvcnQsXG4gICAgb25GaWx0ZXIsXG4gICAgc29ydEZpZWxkLFxuICAgIHNvcnRPcmRlcixcbiAgICBzZWxlY3RSb3dcbiAgfSA9IHByb3BzO1xuXG4gIHJldHVybiAoXG4gICAgPHRoZWFkPlxuICAgICAgPHRyPlxuICAgICAgICB7XG4gICAgICAgICAgKHNlbGVjdFJvdy5tb2RlICE9PSBST1dfU0VMRUNUX0RJU0FCTEVEICYmICFzZWxlY3RSb3cuaGlkZVNlbGVjdENvbHVtbilcbiAgICAgICAgICAgID8gPFNlbGVjdGlvbkhlYWRlckNlbGwgeyAuLi5zZWxlY3RSb3cgfSAvPiA6IG51bGxcbiAgICAgICAgfVxuICAgICAgICB7XG4gICAgICAgICAgY29sdW1ucy5tYXAoKGNvbHVtbiwgaSkgPT4ge1xuICAgICAgICAgICAgY29uc3QgY3VyclNvcnQgPSBjb2x1bW4uZGF0YUZpZWxkID09PSBzb3J0RmllbGQ7XG4gICAgICAgICAgICBjb25zdCBpc0xhc3RTb3J0aW5nID0gY29sdW1uLmRhdGFGaWVsZCA9PT0gc29ydEZpZWxkO1xuXG4gICAgICAgICAgICByZXR1cm4gKFxuICAgICAgICAgICAgICA8SGVhZGVyQ2VsbFxuICAgICAgICAgICAgICAgIGluZGV4PXsgaSB9XG4gICAgICAgICAgICAgICAga2V5PXsgY29sdW1uLmRhdGFGaWVsZCB9XG4gICAgICAgICAgICAgICAgY29sdW1uPXsgY29sdW1uIH1cbiAgICAgICAgICAgICAgICBvblNvcnQ9eyBvblNvcnQgfVxuICAgICAgICAgICAgICAgIHNvcnRpbmc9eyBjdXJyU29ydCB9XG4gICAgICAgICAgICAgICAgb25GaWx0ZXI9eyBvbkZpbHRlciB9XG4gICAgICAgICAgICAgICAgc29ydE9yZGVyPXsgc29ydE9yZGVyIH1cbiAgICAgICAgICAgICAgICBpc0xhc3RTb3J0aW5nPXsgaXNMYXN0U29ydGluZyB9XG4gICAgICAgICAgICAgIC8+KTtcbiAgICAgICAgICB9KVxuICAgICAgICB9XG4gICAgICA8L3RyPlxuICAgIDwvdGhlYWQ+XG4gICk7XG59O1xuXG5IZWFkZXIucHJvcFR5cGVzID0ge1xuICBjb2x1bW5zOiBQcm9wVHlwZXMuYXJyYXkuaXNSZXF1aXJlZCxcbiAgb25Tb3J0OiBQcm9wVHlwZXMuZnVuYyxcbiAgb25GaWx0ZXI6IFByb3BUeXBlcy5mdW5jLFxuICBzb3J0RmllbGQ6IFByb3BUeXBlcy5zdHJpbmcsXG4gIHNvcnRPcmRlcjogUHJvcFR5cGVzLnN0cmluZyxcbiAgc2VsZWN0Um93OiBQcm9wVHlwZXMub2JqZWN0XG59O1xuXG5leHBvcnQgZGVmYXVsdCBIZWFkZXI7XG5cblxuXG4vLyBXRUJQQUNLIEZPT1RFUiAvL1xuLy8gLi9wYWNrYWdlcy9yZWFjdC1ib290c3RyYXAtdGFibGUyL3NyYy9oZWFkZXIuanMiLCIvKiBlc2xpbnQgcmVhY3QvcmVxdWlyZS1kZWZhdWx0LXByb3BzOiAwICovXG5pbXBvcnQgUmVhY3QgZnJvbSAncmVhY3QnO1xuaW1wb3J0IGNzIGZyb20gJ2NsYXNzbmFtZXMnO1xuaW1wb3J0IFByb3BUeXBlcyBmcm9tICdwcm9wLXR5cGVzJztcblxuaW1wb3J0IENvbnN0IGZyb20gJy4vY29uc3QnO1xuaW1wb3J0IFNvcnRTeW1ib2wgZnJvbSAnLi9zb3J0L3N5bWJvbCc7XG5pbXBvcnQgU29ydENhcmV0IGZyb20gJy4vc29ydC9jYXJldCc7XG5pbXBvcnQgXyBmcm9tICcuL3V0aWxzJztcblxuXG5jb25zdCBIZWFkZXJDZWxsID0gKHByb3BzKSA9PiB7XG4gIGNvbnN0IHtcbiAgICBjb2x1bW4sXG4gICAgaW5kZXgsXG4gICAgb25Tb3J0LFxuICAgIHNvcnRpbmcsXG4gICAgc29ydE9yZGVyLFxuICAgIGlzTGFzdFNvcnRpbmcsXG4gICAgb25GaWx0ZXJcbiAgfSA9IHByb3BzO1xuXG4gIGNvbnN0IHtcbiAgICB0ZXh0LFxuICAgIHNvcnQsXG4gICAgZmlsdGVyLFxuICAgIGhpZGRlbixcbiAgICBoZWFkZXJUaXRsZSxcbiAgICBoZWFkZXJBbGlnbixcbiAgICBoZWFkZXJGb3JtYXR0ZXIsXG4gICAgaGVhZGVyRXZlbnRzLFxuICAgIGhlYWRlckNsYXNzZXMsXG4gICAgaGVhZGVyU3R5bGUsXG4gICAgaGVhZGVyQXR0cnMsXG4gICAgaGVhZGVyU29ydGluZ0NsYXNzZXMsXG4gICAgaGVhZGVyU29ydGluZ1N0eWxlXG4gIH0gPSBjb2x1bW47XG5cbiAgY29uc3QgY2VsbEF0dHJzID0ge1xuICAgIC4uLl8uaXNGdW5jdGlvbihoZWFkZXJBdHRycykgPyBoZWFkZXJBdHRycyhjb2x1bW4sIGluZGV4KSA6IGhlYWRlckF0dHJzLFxuICAgIC4uLmhlYWRlckV2ZW50c1xuICB9O1xuXG4gIGxldCBzb3J0U3ltYm9sO1xuICBsZXQgZmlsdGVyRWxtO1xuICBsZXQgY2VsbFN0eWxlID0ge307XG4gIGxldCBjZWxsQ2xhc3NlcyA9IF8uaXNGdW5jdGlvbihoZWFkZXJDbGFzc2VzKSA/IGhlYWRlckNsYXNzZXMoY29sdW1uLCBpbmRleCkgOiBoZWFkZXJDbGFzc2VzO1xuXG4gIGlmIChoZWFkZXJTdHlsZSkge1xuICAgIGNlbGxTdHlsZSA9IF8uaXNGdW5jdGlvbihoZWFkZXJTdHlsZSkgPyBoZWFkZXJTdHlsZShjb2x1bW4sIGluZGV4KSA6IGhlYWRlclN0eWxlO1xuICB9XG5cbiAgaWYgKGhlYWRlclRpdGxlKSB7XG4gICAgY2VsbEF0dHJzLnRpdGxlID0gXy5pc0Z1bmN0aW9uKGhlYWRlclRpdGxlKSA/IGhlYWRlclRpdGxlKGNvbHVtbiwgaW5kZXgpIDogdGV4dDtcbiAgfVxuXG4gIGlmIChoZWFkZXJBbGlnbikge1xuICAgIGNlbGxTdHlsZS50ZXh0QWxpZ24gPSBfLmlzRnVuY3Rpb24oaGVhZGVyQWxpZ24pID8gaGVhZGVyQWxpZ24oY29sdW1uLCBpbmRleCkgOiBoZWFkZXJBbGlnbjtcbiAgfVxuXG4gIGlmIChoaWRkZW4pIHtcbiAgICBjZWxsU3R5bGUuZGlzcGxheSA9ICdub25lJztcbiAgfVxuXG4gIGlmIChzb3J0KSB7XG4gICAgY29uc3QgY3VzdG9tQ2xpY2sgPSBjZWxsQXR0cnMub25DbGljaztcbiAgICBjZWxsQXR0cnMub25DbGljayA9IChlKSA9PiB7XG4gICAgICBvblNvcnQoY29sdW1uKTtcbiAgICAgIGlmIChfLmlzRnVuY3Rpb24oY3VzdG9tQ2xpY2spKSBjdXN0b21DbGljayhlKTtcbiAgICB9O1xuICAgIGNlbGxBdHRycy5jbGFzc05hbWUgPSBjcyhjZWxsQXR0cnMuY2xhc3NOYW1lLCAnc29ydGFibGUnKTtcblxuICAgIGlmIChzb3J0aW5nKSB7XG4gICAgICBzb3J0U3ltYm9sID0gPFNvcnRDYXJldCBvcmRlcj17IHNvcnRPcmRlciB9IC8+O1xuXG4gICAgICAvLyBhcHBlbmQgY3VzdG9taXplZCBjbGFzc2VzIG9yIHN0eWxlIGlmIHRhYmxlIHdhcyBzb3J0aW5nIGJhc2VkIG9uIHRoZSBjdXJyZW50IGNvbHVtbi5cbiAgICAgIGNlbGxDbGFzc2VzID0gY3MoXG4gICAgICAgIGNlbGxDbGFzc2VzLFxuICAgICAgICBfLmlzRnVuY3Rpb24oaGVhZGVyU29ydGluZ0NsYXNzZXMpXG4gICAgICAgICAgPyBoZWFkZXJTb3J0aW5nQ2xhc3Nlcyhjb2x1bW4sIHNvcnRPcmRlciwgaXNMYXN0U29ydGluZywgaW5kZXgpXG4gICAgICAgICAgOiBoZWFkZXJTb3J0aW5nQ2xhc3Nlc1xuICAgICAgKTtcblxuICAgICAgY2VsbFN0eWxlID0ge1xuICAgICAgICAuLi5jZWxsU3R5bGUsXG4gICAgICAgIC4uLl8uaXNGdW5jdGlvbihoZWFkZXJTb3J0aW5nU3R5bGUpXG4gICAgICAgICAgPyBoZWFkZXJTb3J0aW5nU3R5bGUoY29sdW1uLCBzb3J0T3JkZXIsIGlzTGFzdFNvcnRpbmcsIGluZGV4KVxuICAgICAgICAgIDogaGVhZGVyU29ydGluZ1N0eWxlXG4gICAgICB9O1xuICAgIH0gZWxzZSB7XG4gICAgICBzb3J0U3ltYm9sID0gPFNvcnRTeW1ib2wgLz47XG4gICAgfVxuICB9XG5cbiAgaWYgKGNlbGxDbGFzc2VzKSBjZWxsQXR0cnMuY2xhc3NOYW1lID0gY3MoY2VsbEF0dHJzLmNsYXNzTmFtZSwgY2VsbENsYXNzZXMpO1xuICBpZiAoIV8uaXNFbXB0eU9iamVjdChjZWxsU3R5bGUpKSBjZWxsQXR0cnMuc3R5bGUgPSBjZWxsU3R5bGU7XG4gIGlmIChmaWx0ZXIpIHtcbiAgICBmaWx0ZXJFbG0gPSA8ZmlsdGVyLkZpbHRlciB7IC4uLmZpbHRlci5wcm9wcyB9IG9uRmlsdGVyPXsgb25GaWx0ZXIgfSBjb2x1bW49eyBjb2x1bW4gfSAvPjtcbiAgfVxuXG4gIGNvbnN0IGNoaWxkcmVuID0gaGVhZGVyRm9ybWF0dGVyID9cbiAgICBoZWFkZXJGb3JtYXR0ZXIoY29sdW1uLCBpbmRleCwgeyBzb3J0RWxlbWVudDogc29ydFN5bWJvbCwgZmlsdGVyRWxlbWVudDogZmlsdGVyRWxtIH0pIDpcbiAgICB0ZXh0O1xuXG4gIGlmIChoZWFkZXJGb3JtYXR0ZXIpIHtcbiAgICByZXR1cm4gUmVhY3QuY3JlYXRlRWxlbWVudCgndGgnLCBjZWxsQXR0cnMsIGNoaWxkcmVuKTtcbiAgfVxuXG4gIHJldHVybiBSZWFjdC5jcmVhdGVFbGVtZW50KCd0aCcsIGNlbGxBdHRycywgY2hpbGRyZW4sIHNvcnRTeW1ib2wsIGZpbHRlckVsbSk7XG59O1xuXG5IZWFkZXJDZWxsLnByb3BUeXBlcyA9IHtcbiAgY29sdW1uOiBQcm9wVHlwZXMuc2hhcGUoe1xuICAgIGRhdGFGaWVsZDogUHJvcFR5cGVzLnN0cmluZy5pc1JlcXVpcmVkLFxuICAgIHRleHQ6IFByb3BUeXBlcy5zdHJpbmcuaXNSZXF1aXJlZCxcbiAgICBoaWRkZW46IFByb3BUeXBlcy5ib29sLFxuICAgIGhlYWRlckZvcm1hdHRlcjogUHJvcFR5cGVzLmZ1bmMsXG4gICAgZm9ybWF0dGVyOiBQcm9wVHlwZXMuZnVuYyxcbiAgICBmb3JtYXRFeHRyYURhdGE6IFByb3BUeXBlcy5hbnksXG4gICAgaGVhZGVyQ2xhc3NlczogUHJvcFR5cGVzLm9uZU9mVHlwZShbUHJvcFR5cGVzLnN0cmluZywgUHJvcFR5cGVzLmZ1bmNdKSxcbiAgICBjbGFzc2VzOiBQcm9wVHlwZXMub25lT2ZUeXBlKFtQcm9wVHlwZXMuc3RyaW5nLCBQcm9wVHlwZXMuZnVuY10pLFxuICAgIGhlYWRlclN0eWxlOiBQcm9wVHlwZXMub25lT2ZUeXBlKFtQcm9wVHlwZXMub2JqZWN0LCBQcm9wVHlwZXMuZnVuY10pLFxuICAgIHN0eWxlOiBQcm9wVHlwZXMub25lT2ZUeXBlKFtQcm9wVHlwZXMub2JqZWN0LCBQcm9wVHlwZXMuZnVuY10pLFxuICAgIGhlYWRlclRpdGxlOiBQcm9wVHlwZXMub25lT2ZUeXBlKFtQcm9wVHlwZXMuYm9vbCwgUHJvcFR5cGVzLmZ1bmNdKSxcbiAgICB0aXRsZTogUHJvcFR5cGVzLm9uZU9mVHlwZShbUHJvcFR5cGVzLmJvb2wsIFByb3BUeXBlcy5mdW5jXSksXG4gICAgaGVhZGVyRXZlbnRzOiBQcm9wVHlwZXMub2JqZWN0LFxuICAgIGV2ZW50czogUHJvcFR5cGVzLm9iamVjdCxcbiAgICBoZWFkZXJBbGlnbjogUHJvcFR5cGVzLm9uZU9mVHlwZShbUHJvcFR5cGVzLnN0cmluZywgUHJvcFR5cGVzLmZ1bmNdKSxcbiAgICBhbGlnbjogUHJvcFR5cGVzLm9uZU9mVHlwZShbUHJvcFR5cGVzLnN0cmluZywgUHJvcFR5cGVzLmZ1bmNdKSxcbiAgICBoZWFkZXJBdHRyczogUHJvcFR5cGVzLm9uZU9mVHlwZShbUHJvcFR5cGVzLm9iamVjdCwgUHJvcFR5cGVzLmZ1bmNdKSxcbiAgICBhdHRyczogUHJvcFR5cGVzLm9uZU9mVHlwZShbUHJvcFR5cGVzLm9iamVjdCwgUHJvcFR5cGVzLmZ1bmNdKSxcbiAgICBzb3J0OiBQcm9wVHlwZXMuYm9vbCxcbiAgICBzb3J0RnVuYzogUHJvcFR5cGVzLmZ1bmMsXG4gICAgb25Tb3J0OiBQcm9wVHlwZXMuZnVuYyxcbiAgICBlZGl0YWJsZTogUHJvcFR5cGVzLm9uZU9mVHlwZShbUHJvcFR5cGVzLmJvb2wsIFByb3BUeXBlcy5mdW5jXSksXG4gICAgZWRpdENlbGxTdHlsZTogUHJvcFR5cGVzLm9uZU9mVHlwZShbUHJvcFR5cGVzLm9iamVjdCwgUHJvcFR5cGVzLmZ1bmNdKSxcbiAgICBlZGl0Q2VsbENsYXNzZXM6IFByb3BUeXBlcy5vbmVPZlR5cGUoW1Byb3BUeXBlcy5zdHJpbmcsIFByb3BUeXBlcy5mdW5jXSksXG4gICAgdmFsaWRhdG9yOiBQcm9wVHlwZXMuZnVuYyxcbiAgICBmaWx0ZXI6IFByb3BUeXBlcy5vYmplY3QsXG4gICAgZmlsdGVyVmFsdWU6IFByb3BUeXBlcy5mdW5jXG4gIH0pLmlzUmVxdWlyZWQsXG4gIGluZGV4OiBQcm9wVHlwZXMubnVtYmVyLmlzUmVxdWlyZWQsXG4gIG9uU29ydDogUHJvcFR5cGVzLmZ1bmMsXG4gIHNvcnRpbmc6IFByb3BUeXBlcy5ib29sLFxuICBzb3J0T3JkZXI6IFByb3BUeXBlcy5vbmVPZihbQ29uc3QuU09SVF9BU0MsIENvbnN0LlNPUlRfREVTQ10pLFxuICBpc0xhc3RTb3J0aW5nOiBQcm9wVHlwZXMuYm9vbCxcbiAgb25GaWx0ZXI6IFByb3BUeXBlcy5mdW5jXG59O1xuXG5leHBvcnQgZGVmYXVsdCBIZWFkZXJDZWxsO1xuXG5cblxuLy8gV0VCUEFDSyBGT09URVIgLy9cbi8vIC4vcGFja2FnZXMvcmVhY3QtYm9vdHN0cmFwLXRhYmxlMi9zcmMvaGVhZGVyLWNlbGwuanMiLCJpbXBvcnQgUmVhY3QgZnJvbSAncmVhY3QnO1xuXG5jb25zdCBTb3J0U3ltYm9sID0gKCkgPT4gKFxuICA8c3BhbiBjbGFzc05hbWU9XCJvcmRlclwiPlxuICAgIDxzcGFuIGNsYXNzTmFtZT1cImRyb3Bkb3duXCI+XG4gICAgICA8c3BhbiBjbGFzc05hbWU9XCJjYXJldFwiIC8+XG4gICAgPC9zcGFuPlxuICAgIDxzcGFuIGNsYXNzTmFtZT1cImRyb3B1cFwiPlxuICAgICAgPHNwYW4gY2xhc3NOYW1lPVwiY2FyZXRcIiAvPlxuICAgIDwvc3Bhbj5cbiAgPC9zcGFuPik7XG5cbmV4cG9ydCBkZWZhdWx0IFNvcnRTeW1ib2w7XG5cblxuXG4vLyBXRUJQQUNLIEZPT1RFUiAvL1xuLy8gLi9wYWNrYWdlcy9yZWFjdC1ib290c3RyYXAtdGFibGUyL3NyYy9zb3J0L3N5bWJvbC5qcyIsImltcG9ydCBSZWFjdCBmcm9tICdyZWFjdCc7XG5pbXBvcnQgY3MgZnJvbSAnY2xhc3NuYW1lcyc7XG5pbXBvcnQgUHJvcFR5cGVzIGZyb20gJ3Byb3AtdHlwZXMnO1xuXG5pbXBvcnQgQ29uc3QgZnJvbSAnLi4vY29uc3QnO1xuXG5jb25zdCBTb3J0Q2FyZXQgPSAoeyBvcmRlciB9KSA9PiB7XG4gIGNvbnN0IG9yZGVyQ2xhc3MgPSBjcygncmVhY3QtYm9vdHN0cmFwLXRhYmxlLXNvcnQtb3JkZXInLCB7XG4gICAgZHJvcHVwOiBvcmRlciA9PT0gQ29uc3QuU09SVF9BU0NcbiAgfSk7XG4gIHJldHVybiAoXG4gICAgPHNwYW4gY2xhc3NOYW1lPXsgb3JkZXJDbGFzcyB9PlxuICAgICAgPHNwYW4gY2xhc3NOYW1lPVwiY2FyZXRcIiAvPlxuICAgIDwvc3Bhbj5cbiAgKTtcbn07XG5cblNvcnRDYXJldC5wcm9wVHlwZXMgPSB7XG4gIG9yZGVyOiBQcm9wVHlwZXMub25lT2YoW0NvbnN0LlNPUlRfQVNDLCBDb25zdC5TT1JUX0RFU0NdKS5pc1JlcXVpcmVkXG59O1xuZXhwb3J0IGRlZmF1bHQgU29ydENhcmV0O1xuXG5cblxuLy8gV0VCUEFDSyBGT09URVIgLy9cbi8vIC4vcGFja2FnZXMvcmVhY3QtYm9vdHN0cmFwLXRhYmxlMi9zcmMvc29ydC9jYXJldC5qcyIsIi8qIGVzbGludCByZWFjdC9yZXF1aXJlLWRlZmF1bHQtcHJvcHM6IDAgKi9cbmltcG9ydCBSZWFjdCwgeyBDb21wb25lbnQgfSBmcm9tICdyZWFjdCc7XG5pbXBvcnQgUHJvcFR5cGVzIGZyb20gJ3Byb3AtdHlwZXMnO1xuaW1wb3J0IENvbnN0IGZyb20gJy4uL2NvbnN0JztcblxuZXhwb3J0IGNvbnN0IENoZWNrQm94ID0gKHsgY2hlY2tlZCwgaW5kZXRlcm1pbmF0ZSB9KSA9PiAoXG4gIDxpbnB1dFxuICAgIHR5cGU9XCJjaGVja2JveFwiXG4gICAgY2hlY2tlZD17IGNoZWNrZWQgfVxuICAgIHJlZj17IChpbnB1dCkgPT4ge1xuICAgICAgaWYgKGlucHV0KSBpbnB1dC5pbmRldGVybWluYXRlID0gaW5kZXRlcm1pbmF0ZTsgLy8gZXNsaW50LWRpc2FibGUtbGluZSBuby1wYXJhbS1yZWFzc2lnblxuICAgIH0gfVxuICAvPlxuKTtcblxuQ2hlY2tCb3gucHJvcFR5cGVzID0ge1xuICBjaGVja2VkOiBQcm9wVHlwZXMuYm9vbC5pc1JlcXVpcmVkLFxuICBpbmRldGVybWluYXRlOiBQcm9wVHlwZXMuYm9vbC5pc1JlcXVpcmVkXG59O1xuXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBTZWxlY3Rpb25IZWFkZXJDZWxsIGV4dGVuZHMgQ29tcG9uZW50IHtcbiAgc3RhdGljIHByb3BUeXBlcyA9IHtcbiAgICBtb2RlOiBQcm9wVHlwZXMuc3RyaW5nLmlzUmVxdWlyZWQsXG4gICAgY2hlY2tlZFN0YXR1czogUHJvcFR5cGVzLnN0cmluZyxcbiAgICBvbkFsbFJvd3NTZWxlY3Q6IFByb3BUeXBlcy5mdW5jXG4gIH1cblxuICBjb25zdHJ1Y3RvcigpIHtcbiAgICBzdXBlcigpO1xuICAgIHRoaXMuaGFuZGxlQ2hlY2tCb3hDbGljayA9IHRoaXMuaGFuZGxlQ2hlY2tCb3hDbGljay5iaW5kKHRoaXMpO1xuICB9XG5cbiAgLyoqXG4gICAqIGF2b2lkIHVwZGF0aW5nIGlmIGJ1dHRvbiBpc1xuICAgKiAxLiByYWRpb1xuICAgKiAyLiBzdGF0dXMgd2FzIG5vdCBjaGFuZ2VkLlxuICAgKi9cbiAgc2hvdWxkQ29tcG9uZW50VXBkYXRlKG5leHRQcm9wcykge1xuICAgIGNvbnN0IHsgUk9XX1NFTEVDVF9TSU5HTEUgfSA9IENvbnN0O1xuICAgIGNvbnN0IHsgbW9kZSwgY2hlY2tlZFN0YXR1cyB9ID0gdGhpcy5wcm9wcztcblxuICAgIGlmIChtb2RlID09PSBST1dfU0VMRUNUX1NJTkdMRSkgcmV0dXJuIGZhbHNlO1xuXG4gICAgcmV0dXJuIG5leHRQcm9wcy5jaGVja2VkU3RhdHVzICE9PSBjaGVja2VkU3RhdHVzO1xuICB9XG5cbiAgaGFuZGxlQ2hlY2tCb3hDbGljaygpIHtcbiAgICBjb25zdCB7IG9uQWxsUm93c1NlbGVjdCB9ID0gdGhpcy5wcm9wcztcblxuICAgIG9uQWxsUm93c1NlbGVjdCgpO1xuICB9XG5cbiAgcmVuZGVyKCkge1xuICAgIGNvbnN0IHtcbiAgICAgIENIRUNLQk9YX1NUQVRVU19DSEVDS0VELCBDSEVDS0JPWF9TVEFUVVNfSU5ERVRFUk1JTkFURSwgUk9XX1NFTEVDVF9TSU5HTEVcbiAgICB9ID0gQ29uc3Q7XG5cbiAgICBjb25zdCB7IG1vZGUsIGNoZWNrZWRTdGF0dXMgfSA9IHRoaXMucHJvcHM7XG5cbiAgICBjb25zdCBjaGVja2VkID0gY2hlY2tlZFN0YXR1cyA9PT0gQ0hFQ0tCT1hfU1RBVFVTX0NIRUNLRUQ7XG5cbiAgICBjb25zdCBpbmRldGVybWluYXRlID0gY2hlY2tlZFN0YXR1cyA9PT0gQ0hFQ0tCT1hfU1RBVFVTX0lOREVURVJNSU5BVEU7XG5cbiAgICByZXR1cm4gbW9kZSA9PT0gUk9XX1NFTEVDVF9TSU5HTEVcbiAgICAgID8gPHRoIGRhdGEtcm93LXNlbGVjdGlvbiAvPlxuICAgICAgOiAoXG4gICAgICAgIDx0aCBkYXRhLXJvdy1zZWxlY3Rpb24gb25DbGljaz17IHRoaXMuaGFuZGxlQ2hlY2tCb3hDbGljayB9PlxuICAgICAgICAgIDxDaGVja0JveFxuICAgICAgICAgICAgeyAuLi50aGlzLnByb3BzIH1cbiAgICAgICAgICAgIGNoZWNrZWQ9eyBjaGVja2VkIH1cbiAgICAgICAgICAgIGluZGV0ZXJtaW5hdGU9eyBpbmRldGVybWluYXRlIH1cbiAgICAgICAgICAvPlxuICAgICAgICA8L3RoPlxuICAgICAgKTtcbiAgfVxufVxuXG5cblxuLy8gV0VCUEFDSyBGT09URVIgLy9cbi8vIC4vcGFja2FnZXMvcmVhY3QtYm9vdHN0cmFwLXRhYmxlMi9zcmMvcm93LXNlbGVjdGlvbi9zZWxlY3Rpb24taGVhZGVyLWNlbGwuanMiLCIvKiBlc2xpbnQgcmVhY3QvcmVxdWlyZS1kZWZhdWx0LXByb3BzOiAwICovXG5pbXBvcnQgUmVhY3QgZnJvbSAncmVhY3QnO1xuaW1wb3J0IFByb3BUeXBlcyBmcm9tICdwcm9wLXR5cGVzJztcblxuY29uc3QgQ2FwdGlvbiA9IChwcm9wcykgPT4ge1xuICBpZiAoIXByb3BzLmNoaWxkcmVuKSByZXR1cm4gbnVsbDtcbiAgcmV0dXJuIChcbiAgICA8Y2FwdGlvbj57IHByb3BzLmNoaWxkcmVuIH08L2NhcHRpb24+XG4gICk7XG59O1xuXG5DYXB0aW9uLnByb3BUeXBlcyA9IHtcbiAgY2hpbGRyZW46IFByb3BUeXBlcy5vbmVPZlR5cGUoW1xuICAgIFByb3BUeXBlcy5ub2RlLFxuICAgIFByb3BUeXBlcy5zdHJpbmdcbiAgXSlcbn07XG5cbmV4cG9ydCBkZWZhdWx0IENhcHRpb247XG5cblxuXG4vLyBXRUJQQUNLIEZPT1RFUiAvL1xuLy8gLi9wYWNrYWdlcy9yZWFjdC1ib290c3RyYXAtdGFibGUyL3NyYy9jYXB0aW9uLmpzIiwiLyogZXNsaW50IHJlYWN0L3Byb3AtdHlwZXM6IDAgKi9cbi8qIGVzbGludCByZWFjdC9yZXF1aXJlLWRlZmF1bHQtcHJvcHM6IDAgKi9cblxuaW1wb3J0IFJlYWN0IGZyb20gJ3JlYWN0JztcbmltcG9ydCBQcm9wVHlwZXMgZnJvbSAncHJvcC10eXBlcyc7XG5pbXBvcnQgY3MgZnJvbSAnY2xhc3NuYW1lcyc7XG5cbmltcG9ydCBfIGZyb20gJy4vdXRpbHMnO1xuaW1wb3J0IFJvdyBmcm9tICcuL3Jvdyc7XG5pbXBvcnQgUm93U2VjdGlvbiBmcm9tICcuL3Jvdy1zZWN0aW9uJztcbmltcG9ydCBDb25zdCBmcm9tICcuL2NvbnN0JztcblxuY29uc3QgQm9keSA9IChwcm9wcykgPT4ge1xuICBjb25zdCB7XG4gICAgY29sdW1ucyxcbiAgICBkYXRhLFxuICAgIGtleUZpZWxkLFxuICAgIGlzRW1wdHksXG4gICAgbm9EYXRhSW5kaWNhdGlvbixcbiAgICB2aXNpYmxlQ29sdW1uU2l6ZSxcbiAgICBjZWxsRWRpdCxcbiAgICBzZWxlY3RSb3csXG4gICAgc2VsZWN0ZWRSb3dLZXlzLFxuICAgIHJvd1N0eWxlLFxuICAgIHJvd0NsYXNzZXMsXG4gICAgcm93RXZlbnRzXG4gIH0gPSBwcm9wcztcblxuICBjb25zdCB7XG4gICAgYmdDb2xvcixcbiAgICBub25TZWxlY3RhYmxlXG4gIH0gPSBzZWxlY3RSb3c7XG5cbiAgbGV0IGNvbnRlbnQ7XG5cbiAgaWYgKGlzRW1wdHkpIHtcbiAgICBjb25zdCBpbmRpY2F0aW9uID0gXy5pc0Z1bmN0aW9uKG5vRGF0YUluZGljYXRpb24pID8gbm9EYXRhSW5kaWNhdGlvbigpIDogbm9EYXRhSW5kaWNhdGlvbjtcbiAgICBjb250ZW50ID0gPFJvd1NlY3Rpb24gY29udGVudD17IGluZGljYXRpb24gfSBjb2xTcGFuPXsgdmlzaWJsZUNvbHVtblNpemUgfSAvPjtcbiAgfSBlbHNlIHtcbiAgICBjb25zdCBub25FZGl0YWJsZVJvd3MgPSBjZWxsRWRpdC5ub25FZGl0YWJsZVJvd3MgfHwgW107XG4gICAgY29udGVudCA9IGRhdGEubWFwKChyb3csIGluZGV4KSA9PiB7XG4gICAgICBjb25zdCBrZXkgPSBfLmdldChyb3csIGtleUZpZWxkKTtcbiAgICAgIGNvbnN0IGVkaXRhYmxlID0gIShub25FZGl0YWJsZVJvd3MubGVuZ3RoID4gMCAmJiBub25FZGl0YWJsZVJvd3MuaW5kZXhPZihrZXkpID4gLTEpO1xuXG4gICAgICBjb25zdCBzZWxlY3RlZCA9IHNlbGVjdFJvdy5tb2RlICE9PSBDb25zdC5ST1dfU0VMRUNUX0RJU0FCTEVEXG4gICAgICAgID8gc2VsZWN0ZWRSb3dLZXlzLmluY2x1ZGVzKGtleSlcbiAgICAgICAgOiBudWxsO1xuXG4gICAgICBjb25zdCBhdHRycyA9IHJvd0V2ZW50cyB8fCB7fTtcbiAgICAgIGxldCBzdHlsZSA9IF8uaXNGdW5jdGlvbihyb3dTdHlsZSkgPyByb3dTdHlsZShyb3csIGluZGV4KSA6IHJvd1N0eWxlO1xuICAgICAgbGV0IGNsYXNzZXMgPSAoXy5pc0Z1bmN0aW9uKHJvd0NsYXNzZXMpID8gcm93Q2xhc3Nlcyhyb3csIGluZGV4KSA6IHJvd0NsYXNzZXMpO1xuICAgICAgaWYgKHNlbGVjdGVkKSB7XG4gICAgICAgIGNvbnN0IHNlbGVjdGVkU3R5bGUgPSBfLmlzRnVuY3Rpb24oc2VsZWN0Um93LnN0eWxlKVxuICAgICAgICAgID8gc2VsZWN0Um93LnN0eWxlKHJvdywgaW5kZXgpXG4gICAgICAgICAgOiBzZWxlY3RSb3cuc3R5bGU7XG5cbiAgICAgICAgY29uc3Qgc2VsZWN0ZWRDbGFzc2VzID0gXy5pc0Z1bmN0aW9uKHNlbGVjdFJvdy5jbGFzc2VzKVxuICAgICAgICAgID8gc2VsZWN0Um93LmNsYXNzZXMocm93LCBpbmRleClcbiAgICAgICAgICA6IHNlbGVjdFJvdy5jbGFzc2VzO1xuXG4gICAgICAgIHN0eWxlID0ge1xuICAgICAgICAgIC4uLnN0eWxlLFxuICAgICAgICAgIC4uLnNlbGVjdGVkU3R5bGVcbiAgICAgICAgfTtcbiAgICAgICAgY2xhc3NlcyA9IGNzKGNsYXNzZXMsIHNlbGVjdGVkQ2xhc3Nlcyk7XG5cbiAgICAgICAgaWYgKGJnQ29sb3IpIHtcbiAgICAgICAgICBzdHlsZSA9IHN0eWxlIHx8IHt9O1xuICAgICAgICAgIHN0eWxlLmJhY2tncm91bmRDb2xvciA9IF8uaXNGdW5jdGlvbihiZ0NvbG9yKSA/IGJnQ29sb3Iocm93LCBpbmRleCkgOiBiZ0NvbG9yO1xuICAgICAgICB9XG4gICAgICB9XG5cbiAgICAgIGNvbnN0IHNlbGVjdGFibGUgPSAhbm9uU2VsZWN0YWJsZSB8fCAhbm9uU2VsZWN0YWJsZS5pbmNsdWRlcyhrZXkpO1xuXG4gICAgICByZXR1cm4gKFxuICAgICAgICA8Um93XG4gICAgICAgICAga2V5PXsga2V5IH1cbiAgICAgICAgICByb3c9eyByb3cgfVxuICAgICAgICAgIGtleUZpZWxkPXsga2V5RmllbGQgfVxuICAgICAgICAgIHJvd0luZGV4PXsgaW5kZXggfVxuICAgICAgICAgIGNvbHVtbnM9eyBjb2x1bW5zIH1cbiAgICAgICAgICBjZWxsRWRpdD17IGNlbGxFZGl0IH1cbiAgICAgICAgICBlZGl0YWJsZT17IGVkaXRhYmxlIH1cbiAgICAgICAgICBzZWxlY3RhYmxlPXsgc2VsZWN0YWJsZSB9XG4gICAgICAgICAgc2VsZWN0ZWQ9eyBzZWxlY3RlZCB9XG4gICAgICAgICAgc2VsZWN0Um93PXsgc2VsZWN0Um93IH1cbiAgICAgICAgICBzdHlsZT17IHN0eWxlIH1cbiAgICAgICAgICBjbGFzc05hbWU9eyBjbGFzc2VzIH1cbiAgICAgICAgICBhdHRycz17IGF0dHJzIH1cbiAgICAgICAgLz5cbiAgICAgICk7XG4gICAgfSk7XG4gIH1cblxuICByZXR1cm4gKFxuICAgIDx0Ym9keT57IGNvbnRlbnQgfTwvdGJvZHk+XG4gICk7XG59O1xuXG5Cb2R5LnByb3BUeXBlcyA9IHtcbiAga2V5RmllbGQ6IFByb3BUeXBlcy5zdHJpbmcuaXNSZXF1aXJlZCxcbiAgZGF0YTogUHJvcFR5cGVzLmFycmF5LmlzUmVxdWlyZWQsXG4gIGNvbHVtbnM6IFByb3BUeXBlcy5hcnJheS5pc1JlcXVpcmVkLFxuICBzZWxlY3RSb3c6IFByb3BUeXBlcy5vYmplY3QsXG4gIHNlbGVjdGVkUm93S2V5czogUHJvcFR5cGVzLmFycmF5XG59O1xuXG5leHBvcnQgZGVmYXVsdCBCb2R5O1xuXG5cblxuLy8gV0VCUEFDSyBGT09URVIgLy9cbi8vIC4vcGFja2FnZXMvcmVhY3QtYm9vdHN0cmFwLXRhYmxlMi9zcmMvYm9keS5qcyIsIi8qIGVzbGludCByZWFjdC9wcm9wLXR5cGVzOiAwICovXG4vKiBlc2xpbnQgcmVhY3Qvbm8tYXJyYXktaW5kZXgta2V5OiAwICovXG5pbXBvcnQgUmVhY3QsIHsgQ29tcG9uZW50IH0gZnJvbSAncmVhY3QnO1xuaW1wb3J0IFByb3BUeXBlcyBmcm9tICdwcm9wLXR5cGVzJztcblxuaW1wb3J0IF8gZnJvbSAnLi91dGlscyc7XG5pbXBvcnQgQ2VsbCBmcm9tICcuL2NlbGwnO1xuaW1wb3J0IFNlbGVjdGlvbkNlbGwgZnJvbSAnLi9yb3ctc2VsZWN0aW9uL3NlbGVjdGlvbi1jZWxsJztcbmltcG9ydCBldmVudERlbGVnYXRlciBmcm9tICcuL3Jvdy1ldmVudC1kZWxlZ2F0ZXInO1xuaW1wb3J0IENvbnN0IGZyb20gJy4vY29uc3QnO1xuXG5jbGFzcyBSb3cgZXh0ZW5kcyBldmVudERlbGVnYXRlcihDb21wb25lbnQpIHtcbiAgcmVuZGVyKCkge1xuICAgIGNvbnN0IHtcbiAgICAgIHJvdyxcbiAgICAgIGNvbHVtbnMsXG4gICAgICBrZXlGaWVsZCxcbiAgICAgIHJvd0luZGV4LFxuICAgICAgY2xhc3NOYW1lLFxuICAgICAgc3R5bGUsXG4gICAgICBhdHRycyxcbiAgICAgIGNlbGxFZGl0LFxuICAgICAgc2VsZWN0ZWQsXG4gICAgICBzZWxlY3RSb3csXG4gICAgICBzZWxlY3RhYmxlLFxuICAgICAgZWRpdGFibGU6IGVkaXRhYmxlUm93XG4gICAgfSA9IHRoaXMucHJvcHM7XG5cbiAgICBjb25zdCB7XG4gICAgICBtb2RlLFxuICAgICAgb25TdGFydCxcbiAgICAgIEVkaXRpbmdDZWxsLFxuICAgICAgcmlkeDogZWRpdGluZ1Jvd0lkeCxcbiAgICAgIGNpZHg6IGVkaXRpbmdDb2xJZHgsXG4gICAgICBDTElDS19UT19DRUxMX0VESVQsXG4gICAgICBEQkNMSUNLX1RPX0NFTExfRURJVCxcbiAgICAgIC4uLnJlc3RcbiAgICB9ID0gY2VsbEVkaXQ7XG5cbiAgICBjb25zdCBrZXkgPSBfLmdldChyb3csIGtleUZpZWxkKTtcbiAgICBjb25zdCB7IGhpZGVTZWxlY3RDb2x1bW4gfSA9IHNlbGVjdFJvdztcbiAgICBjb25zdCB0ckF0dHJzID0gdGhpcy5kZWxlZ2F0ZShhdHRycyk7XG5cbiAgICByZXR1cm4gKFxuICAgICAgPHRyIHN0eWxlPXsgc3R5bGUgfSBjbGFzc05hbWU9eyBjbGFzc05hbWUgfSB7IC4uLnRyQXR0cnMgfT5cbiAgICAgICAge1xuICAgICAgICAgIChzZWxlY3RSb3cubW9kZSAhPT0gQ29uc3QuUk9XX1NFTEVDVF9ESVNBQkxFRCAmJiAhaGlkZVNlbGVjdENvbHVtbilcbiAgICAgICAgICAgID8gKFxuICAgICAgICAgICAgICA8U2VsZWN0aW9uQ2VsbFxuICAgICAgICAgICAgICAgIHsgLi4uc2VsZWN0Um93IH1cbiAgICAgICAgICAgICAgICByb3dLZXk9eyBrZXkgfVxuICAgICAgICAgICAgICAgIHJvd0luZGV4PXsgcm93SW5kZXggfVxuICAgICAgICAgICAgICAgIHNlbGVjdGVkPXsgc2VsZWN0ZWQgfVxuICAgICAgICAgICAgICAgIGRpc2FibGVkPXsgIXNlbGVjdGFibGUgfVxuICAgICAgICAgICAgICAvPlxuICAgICAgICAgICAgKVxuICAgICAgICAgICAgOiBudWxsXG4gICAgICAgIH1cbiAgICAgICAge1xuICAgICAgICAgIGNvbHVtbnMubWFwKChjb2x1bW4sIGluZGV4KSA9PiB7XG4gICAgICAgICAgICBjb25zdCB7IGRhdGFGaWVsZCB9ID0gY29sdW1uO1xuICAgICAgICAgICAgY29uc3QgY29udGVudCA9IF8uZ2V0KHJvdywgZGF0YUZpZWxkKTtcbiAgICAgICAgICAgIGxldCBlZGl0YWJsZSA9IF8uaXNEZWZpbmVkKGNvbHVtbi5lZGl0YWJsZSkgPyBjb2x1bW4uZWRpdGFibGUgOiB0cnVlO1xuICAgICAgICAgICAgaWYgKGRhdGFGaWVsZCA9PT0ga2V5RmllbGQgfHwgIWVkaXRhYmxlUm93KSBlZGl0YWJsZSA9IGZhbHNlO1xuICAgICAgICAgICAgaWYgKF8uaXNGdW5jdGlvbihjb2x1bW4uZWRpdGFibGUpKSB7XG4gICAgICAgICAgICAgIGVkaXRhYmxlID0gY29sdW1uLmVkaXRhYmxlKGNvbnRlbnQsIHJvdywgcm93SW5kZXgsIGluZGV4KTtcbiAgICAgICAgICAgIH1cbiAgICAgICAgICAgIGlmIChyb3dJbmRleCA9PT0gZWRpdGluZ1Jvd0lkeCAmJiBpbmRleCA9PT0gZWRpdGluZ0NvbElkeCkge1xuICAgICAgICAgICAgICBsZXQgZWRpdENlbGxzdHlsZSA9IGNvbHVtbi5lZGl0Q2VsbFN0eWxlIHx8IHt9O1xuICAgICAgICAgICAgICBsZXQgZWRpdENlbGxjbGFzc2VzID0gY29sdW1uLmVkaXRDZWxsQ2xhc3NlcztcbiAgICAgICAgICAgICAgaWYgKF8uaXNGdW5jdGlvbihjb2x1bW4uZWRpdENlbGxTdHlsZSkpIHtcbiAgICAgICAgICAgICAgICBlZGl0Q2VsbHN0eWxlID0gY29sdW1uLmVkaXRDZWxsU3R5bGUoY29udGVudCwgcm93LCByb3dJbmRleCwgaW5kZXgpO1xuICAgICAgICAgICAgICB9XG4gICAgICAgICAgICAgIGlmIChfLmlzRnVuY3Rpb24oY29sdW1uLmVkaXRDZWxsQ2xhc3NlcykpIHtcbiAgICAgICAgICAgICAgICBlZGl0Q2VsbGNsYXNzZXMgPSBjb2x1bW4uZWRpdENlbGxDbGFzc2VzKGNvbnRlbnQsIHJvdywgcm93SW5kZXgsIGluZGV4KTtcbiAgICAgICAgICAgICAgfVxuICAgICAgICAgICAgICByZXR1cm4gKFxuICAgICAgICAgICAgICAgIDxFZGl0aW5nQ2VsbFxuICAgICAgICAgICAgICAgICAga2V5PXsgYCR7Y29udGVudH0tJHtpbmRleH1gIH1cbiAgICAgICAgICAgICAgICAgIHJvdz17IHJvdyB9XG4gICAgICAgICAgICAgICAgICBjb2x1bW49eyBjb2x1bW4gfVxuICAgICAgICAgICAgICAgICAgY2xhc3NOYW1lPXsgZWRpdENlbGxjbGFzc2VzIH1cbiAgICAgICAgICAgICAgICAgIHN0eWxlPXsgZWRpdENlbGxzdHlsZSB9XG4gICAgICAgICAgICAgICAgICB7IC4uLnJlc3QgfVxuICAgICAgICAgICAgICAgIC8+XG4gICAgICAgICAgICAgICk7XG4gICAgICAgICAgICB9XG4gICAgICAgICAgICByZXR1cm4gKFxuICAgICAgICAgICAgICA8Q2VsbFxuICAgICAgICAgICAgICAgIGtleT17IGAke2NvbnRlbnR9LSR7aW5kZXh9YCB9XG4gICAgICAgICAgICAgICAgcm93PXsgcm93IH1cbiAgICAgICAgICAgICAgICByb3dJbmRleD17IHJvd0luZGV4IH1cbiAgICAgICAgICAgICAgICBjb2x1bW5JbmRleD17IGluZGV4IH1cbiAgICAgICAgICAgICAgICBjb2x1bW49eyBjb2x1bW4gfVxuICAgICAgICAgICAgICAgIG9uU3RhcnQ9eyBvblN0YXJ0IH1cbiAgICAgICAgICAgICAgICBlZGl0YWJsZT17IGVkaXRhYmxlIH1cbiAgICAgICAgICAgICAgICBjbGlja1RvRWRpdD17IG1vZGUgPT09IENMSUNLX1RPX0NFTExfRURJVCB9XG4gICAgICAgICAgICAgICAgZGJjbGlja1RvRWRpdD17IG1vZGUgPT09IERCQ0xJQ0tfVE9fQ0VMTF9FRElUIH1cbiAgICAgICAgICAgICAgLz5cbiAgICAgICAgICAgICk7XG4gICAgICAgICAgfSlcbiAgICAgICAgfVxuICAgICAgPC90cj5cbiAgICApO1xuICB9XG59XG5cblJvdy5wcm9wVHlwZXMgPSB7XG4gIHJvdzogUHJvcFR5cGVzLm9iamVjdC5pc1JlcXVpcmVkLFxuICByb3dJbmRleDogUHJvcFR5cGVzLm51bWJlci5pc1JlcXVpcmVkLFxuICBjb2x1bW5zOiBQcm9wVHlwZXMuYXJyYXkuaXNSZXF1aXJlZCxcbiAgc3R5bGU6IFByb3BUeXBlcy5vYmplY3QsXG4gIGNsYXNzTmFtZTogUHJvcFR5cGVzLnN0cmluZyxcbiAgYXR0cnM6IFByb3BUeXBlcy5vYmplY3Rcbn07XG5cblJvdy5kZWZhdWx0UHJvcHMgPSB7XG4gIGVkaXRhYmxlOiB0cnVlLFxuICBzdHlsZToge30sXG4gIGNsYXNzTmFtZTogbnVsbCxcbiAgYXR0cnM6IHt9XG59O1xuXG5leHBvcnQgZGVmYXVsdCBSb3c7XG5cblxuXG4vLyBXRUJQQUNLIEZPT1RFUiAvL1xuLy8gLi9wYWNrYWdlcy9yZWFjdC1ib290c3RyYXAtdGFibGUyL3NyYy9yb3cuanMiLCIvKiBlc2xpbnQgcmVhY3QvcHJvcC10eXBlczogMCAqL1xuaW1wb3J0IFJlYWN0LCB7IENvbXBvbmVudCB9IGZyb20gJ3JlYWN0JztcbmltcG9ydCBQcm9wVHlwZXMgZnJvbSAncHJvcC10eXBlcyc7XG5cbmltcG9ydCBfIGZyb20gJy4vdXRpbHMnO1xuXG5jbGFzcyBDZWxsIGV4dGVuZHMgQ29tcG9uZW50IHtcbiAgY29uc3RydWN0b3IocHJvcHMpIHtcbiAgICBzdXBlcihwcm9wcyk7XG4gICAgdGhpcy5oYW5kbGVFZGl0aW5nQ2VsbCA9IHRoaXMuaGFuZGxlRWRpdGluZ0NlbGwuYmluZCh0aGlzKTtcbiAgfVxuXG4gIGhhbmRsZUVkaXRpbmdDZWxsKGUpIHtcbiAgICBjb25zdCB7IGNvbHVtbiwgb25TdGFydCwgcm93SW5kZXgsIGNvbHVtbkluZGV4LCBjbGlja1RvRWRpdCwgZGJjbGlja1RvRWRpdCB9ID0gdGhpcy5wcm9wcztcbiAgICBjb25zdCB7IGV2ZW50cyB9ID0gY29sdW1uO1xuICAgIGlmIChldmVudHMpIHtcbiAgICAgIGlmIChjbGlja1RvRWRpdCkge1xuICAgICAgICBjb25zdCBjdXN0b21DbGljayA9IGV2ZW50cy5vbkNsaWNrO1xuICAgICAgICBpZiAoXy5pc0Z1bmN0aW9uKGN1c3RvbUNsaWNrKSkgY3VzdG9tQ2xpY2soZSk7XG4gICAgICB9IGVsc2UgaWYgKGRiY2xpY2tUb0VkaXQpIHtcbiAgICAgICAgY29uc3QgY3VzdG9tRGJDbGljayA9IGV2ZW50cy5vbkRvdWJsZUNsaWNrO1xuICAgICAgICBpZiAoXy5pc0Z1bmN0aW9uKGN1c3RvbURiQ2xpY2spKSBjdXN0b21EYkNsaWNrKGUpO1xuICAgICAgfVxuICAgIH1cbiAgICBpZiAob25TdGFydCkge1xuICAgICAgb25TdGFydChyb3dJbmRleCwgY29sdW1uSW5kZXgpO1xuICAgIH1cbiAgfVxuXG4gIHJlbmRlcigpIHtcbiAgICBjb25zdCB7XG4gICAgICByb3csXG4gICAgICByb3dJbmRleCxcbiAgICAgIGNvbHVtbixcbiAgICAgIGNvbHVtbkluZGV4LFxuICAgICAgZWRpdGFibGUsXG4gICAgICBjbGlja1RvRWRpdCxcbiAgICAgIGRiY2xpY2tUb0VkaXRcbiAgICB9ID0gdGhpcy5wcm9wcztcbiAgICBjb25zdCB7XG4gICAgICBkYXRhRmllbGQsXG4gICAgICBoaWRkZW4sXG4gICAgICBmb3JtYXR0ZXIsXG4gICAgICBmb3JtYXRFeHRyYURhdGEsXG4gICAgICBzdHlsZSxcbiAgICAgIGNsYXNzZXMsXG4gICAgICB0aXRsZSxcbiAgICAgIGV2ZW50cyxcbiAgICAgIGFsaWduLFxuICAgICAgYXR0cnNcbiAgICB9ID0gY29sdW1uO1xuICAgIGxldCBjZWxsVGl0bGU7XG4gICAgbGV0IGNlbGxTdHlsZSA9IHt9O1xuICAgIGxldCBjb250ZW50ID0gXy5nZXQocm93LCBkYXRhRmllbGQpO1xuXG4gICAgY29uc3QgY2VsbEF0dHJzID0ge1xuICAgICAgLi4uXy5pc0Z1bmN0aW9uKGF0dHJzKSA/IGF0dHJzKGNvbnRlbnQsIHJvdywgcm93SW5kZXgsIGNvbHVtbkluZGV4KSA6IGF0dHJzLFxuICAgICAgLi4uZXZlbnRzXG4gICAgfTtcblxuICAgIGNvbnN0IGNlbGxDbGFzc2VzID0gXy5pc0Z1bmN0aW9uKGNsYXNzZXMpXG4gICAgICA/IGNsYXNzZXMoY29udGVudCwgcm93LCByb3dJbmRleCwgY29sdW1uSW5kZXgpXG4gICAgICA6IGNsYXNzZXM7XG5cbiAgICBpZiAoc3R5bGUpIHtcbiAgICAgIGNlbGxTdHlsZSA9IF8uaXNGdW5jdGlvbihzdHlsZSkgPyBzdHlsZShjb250ZW50LCByb3csIHJvd0luZGV4LCBjb2x1bW5JbmRleCkgOiBzdHlsZTtcbiAgICB9XG5cbiAgICBpZiAodGl0bGUpIHtcbiAgICAgIGNlbGxUaXRsZSA9IF8uaXNGdW5jdGlvbih0aXRsZSkgPyB0aXRsZShjb250ZW50LCByb3csIHJvd0luZGV4LCBjb2x1bW5JbmRleCkgOiBjb250ZW50O1xuICAgICAgY2VsbEF0dHJzLnRpdGxlID0gY2VsbFRpdGxlO1xuICAgIH1cblxuICAgIGlmIChmb3JtYXR0ZXIpIHtcbiAgICAgIGNvbnRlbnQgPSBjb2x1bW4uZm9ybWF0dGVyKGNvbnRlbnQsIHJvdywgcm93SW5kZXgsIGZvcm1hdEV4dHJhRGF0YSk7XG4gICAgfVxuXG4gICAgaWYgKGFsaWduKSB7XG4gICAgICBjZWxsU3R5bGUudGV4dEFsaWduID1cbiAgICAgICAgXy5pc0Z1bmN0aW9uKGFsaWduKSA/IGFsaWduKGNvbnRlbnQsIHJvdywgcm93SW5kZXgsIGNvbHVtbkluZGV4KSA6IGFsaWduO1xuICAgIH1cblxuICAgIGlmIChoaWRkZW4pIHtcbiAgICAgIGNlbGxTdHlsZS5kaXNwbGF5ID0gJ25vbmUnO1xuICAgIH1cblxuICAgIGlmIChjZWxsQ2xhc3NlcykgY2VsbEF0dHJzLmNsYXNzTmFtZSA9IGNlbGxDbGFzc2VzO1xuXG4gICAgaWYgKCFfLmlzRW1wdHlPYmplY3QoY2VsbFN0eWxlKSkgY2VsbEF0dHJzLnN0eWxlID0gY2VsbFN0eWxlO1xuICAgIGlmIChjbGlja1RvRWRpdCAmJiBlZGl0YWJsZSkge1xuICAgICAgY2VsbEF0dHJzLm9uQ2xpY2sgPSB0aGlzLmhhbmRsZUVkaXRpbmdDZWxsO1xuICAgIH0gZWxzZSBpZiAoZGJjbGlja1RvRWRpdCAmJiBlZGl0YWJsZSkge1xuICAgICAgY2VsbEF0dHJzLm9uRG91YmxlQ2xpY2sgPSB0aGlzLmhhbmRsZUVkaXRpbmdDZWxsO1xuICAgIH1cbiAgICByZXR1cm4gKFxuICAgICAgPHRkIHsgLi4uY2VsbEF0dHJzIH0+eyBjb250ZW50IH08L3RkPlxuICAgICk7XG4gIH1cbn1cblxuQ2VsbC5wcm9wVHlwZXMgPSB7XG4gIHJvdzogUHJvcFR5cGVzLm9iamVjdC5pc1JlcXVpcmVkLFxuICByb3dJbmRleDogUHJvcFR5cGVzLm51bWJlci5pc1JlcXVpcmVkLFxuICBjb2x1bW46IFByb3BUeXBlcy5vYmplY3QuaXNSZXF1aXJlZCxcbiAgY29sdW1uSW5kZXg6IFByb3BUeXBlcy5udW1iZXIuaXNSZXF1aXJlZFxufTtcblxuZXhwb3J0IGRlZmF1bHQgQ2VsbDtcblxuXG5cbi8vIFdFQlBBQ0sgRk9PVEVSIC8vXG4vLyAuL3BhY2thZ2VzL3JlYWN0LWJvb3RzdHJhcC10YWJsZTIvc3JjL2NlbGwuanMiLCIvKiBlc2xpbnRcbiAgcmVhY3QvcmVxdWlyZS1kZWZhdWx0LXByb3BzOiAwXG4gIGpzeC1hMTF5L25vLW5vbmludGVyYWN0aXZlLWVsZW1lbnQtaW50ZXJhY3Rpb25zOiAwXG4qL1xuaW1wb3J0IFJlYWN0LCB7IENvbXBvbmVudCB9IGZyb20gJ3JlYWN0JztcbmltcG9ydCBQcm9wVHlwZXMgZnJvbSAncHJvcC10eXBlcyc7XG5pbXBvcnQgQ29uc3QgZnJvbSAnLi4vY29uc3QnO1xuXG5leHBvcnQgZGVmYXVsdCBjbGFzcyBTZWxlY3Rpb25DZWxsIGV4dGVuZHMgQ29tcG9uZW50IHtcbiAgc3RhdGljIHByb3BUeXBlcyA9IHtcbiAgICBtb2RlOiBQcm9wVHlwZXMuc3RyaW5nLmlzUmVxdWlyZWQsXG4gICAgcm93S2V5OiBQcm9wVHlwZXMuYW55LFxuICAgIHNlbGVjdGVkOiBQcm9wVHlwZXMuYm9vbCxcbiAgICBvblJvd1NlbGVjdDogUHJvcFR5cGVzLmZ1bmMsXG4gICAgZGlzYWJsZWQ6IFByb3BUeXBlcy5ib29sLFxuICAgIHJvd0luZGV4OiBQcm9wVHlwZXMubnVtYmVyLFxuICAgIGNsaWNrVG9TZWxlY3Q6IFByb3BUeXBlcy5ib29sXG4gIH1cblxuICBjb25zdHJ1Y3RvcigpIHtcbiAgICBzdXBlcigpO1xuICAgIHRoaXMuaGFuZGxlQ2xpY2sgPSB0aGlzLmhhbmRsZUNsaWNrLmJpbmQodGhpcyk7XG4gIH1cblxuICBzaG91bGRDb21wb25lbnRVcGRhdGUobmV4dFByb3BzKSB7XG4gICAgY29uc3QgeyBzZWxlY3RlZCB9ID0gdGhpcy5wcm9wcztcblxuICAgIHJldHVybiBuZXh0UHJvcHMuc2VsZWN0ZWQgIT09IHNlbGVjdGVkO1xuICB9XG5cbiAgaGFuZGxlQ2xpY2soKSB7XG4gICAgY29uc3Qge1xuICAgICAgbW9kZTogaW5wdXRUeXBlLFxuICAgICAgcm93S2V5LFxuICAgICAgc2VsZWN0ZWQsXG4gICAgICBvblJvd1NlbGVjdCxcbiAgICAgIGRpc2FibGVkLFxuICAgICAgcm93SW5kZXgsXG4gICAgICBjbGlja1RvU2VsZWN0XG4gICAgfSA9IHRoaXMucHJvcHM7XG5cbiAgICBpZiAoZGlzYWJsZWQpIHJldHVybjtcbiAgICBpZiAoY2xpY2tUb1NlbGVjdCkgcmV0dXJuO1xuXG4gICAgY29uc3QgY2hlY2tlZCA9IGlucHV0VHlwZSA9PT0gQ29uc3QuUk9XX1NFTEVDVF9TSU5HTEVcbiAgICAgID8gdHJ1ZVxuICAgICAgOiAhc2VsZWN0ZWQ7XG5cbiAgICBvblJvd1NlbGVjdChyb3dLZXksIGNoZWNrZWQsIHJvd0luZGV4KTtcbiAgfVxuXG4gIHJlbmRlcigpIHtcbiAgICBjb25zdCB7XG4gICAgICBtb2RlOiBpbnB1dFR5cGUsXG4gICAgICBzZWxlY3RlZCxcbiAgICAgIGRpc2FibGVkXG4gICAgfSA9IHRoaXMucHJvcHM7XG5cbiAgICByZXR1cm4gKFxuICAgICAgPHRkIG9uQ2xpY2s9eyB0aGlzLmhhbmRsZUNsaWNrIH0+XG4gICAgICAgIDxpbnB1dFxuICAgICAgICAgIHR5cGU9eyBpbnB1dFR5cGUgfVxuICAgICAgICAgIGNoZWNrZWQ9eyBzZWxlY3RlZCB9XG4gICAgICAgICAgZGlzYWJsZWQ9eyBkaXNhYmxlZCB9XG4gICAgICAgIC8+XG4gICAgICA8L3RkPlxuICAgICk7XG4gIH1cbn1cblxuXG5cbi8vIFdFQlBBQ0sgRk9PVEVSIC8vXG4vLyAuL3BhY2thZ2VzL3JlYWN0LWJvb3RzdHJhcC10YWJsZTIvc3JjL3Jvdy1zZWxlY3Rpb24vc2VsZWN0aW9uLWNlbGwuanMiLCJpbXBvcnQgXyBmcm9tICcuL3V0aWxzJztcblxuY29uc3QgZXZlbnRzID0gW1xuICAnb25DbGljaycsXG4gICdvbk1vdXNlRW50ZXInLFxuICAnb25Nb3VzZUxlYXZlJ1xuXTtcblxuZXhwb3J0IGRlZmF1bHQgRXh0ZW5kQmFzZSA9PlxuICBjbGFzcyBSb3dFdmVudERlbGVnYXRlciBleHRlbmRzIEV4dGVuZEJhc2Uge1xuICAgIGNvbnN0cnVjdG9yKHByb3BzKSB7XG4gICAgICBzdXBlcihwcm9wcyk7XG4gICAgICB0aGlzLmNsaWNrTnVtID0gMDtcbiAgICAgIHRoaXMuY3JlYXRlRGVmYXVsdEV2ZW50SGFuZGxlciA9IHRoaXMuY3JlYXRlRGVmYXVsdEV2ZW50SGFuZGxlci5iaW5kKHRoaXMpO1xuICAgICAgdGhpcy5jcmVhdGVDbGlja0V2ZW50SGFuZGxlciA9IHRoaXMuY3JlYXRlQ2xpY2tFdmVudEhhbmRsZXIuYmluZCh0aGlzKTtcbiAgICB9XG5cbiAgICBjcmVhdGVEZWZhdWx0RXZlbnRIYW5kbGVyKGNiKSB7XG4gICAgICByZXR1cm4gKGUpID0+IHtcbiAgICAgICAgY29uc3QgeyByb3csIHJvd0luZGV4IH0gPSB0aGlzLnByb3BzO1xuICAgICAgICBjYihlLCByb3csIHJvd0luZGV4KTtcbiAgICAgIH07XG4gICAgfVxuXG4gICAgY3JlYXRlQ2xpY2tFdmVudEhhbmRsZXIoY2IpIHtcbiAgICAgIHJldHVybiAoZSkgPT4ge1xuICAgICAgICBjb25zdCB7XG4gICAgICAgICAgcm93LFxuICAgICAgICAgIHNlbGVjdGVkLFxuICAgICAgICAgIGtleUZpZWxkLFxuICAgICAgICAgIHNlbGVjdGFibGUsXG4gICAgICAgICAgcm93SW5kZXgsXG4gICAgICAgICAgc2VsZWN0Um93OiB7XG4gICAgICAgICAgICBvblJvd1NlbGVjdCxcbiAgICAgICAgICAgIGNsaWNrVG9FZGl0XG4gICAgICAgICAgfSxcbiAgICAgICAgICBjZWxsRWRpdDoge1xuICAgICAgICAgICAgbW9kZSxcbiAgICAgICAgICAgIERCQ0xJQ0tfVE9fQ0VMTF9FRElULFxuICAgICAgICAgICAgREVMQVlfRk9SX0RCQ0xJQ0tcbiAgICAgICAgICB9XG4gICAgICAgIH0gPSB0aGlzLnByb3BzO1xuXG4gICAgICAgIGNvbnN0IGNsaWNrRm4gPSAoKSA9PiB7XG4gICAgICAgICAgaWYgKGNiKSB7XG4gICAgICAgICAgICBjYihlLCByb3csIHJvd0luZGV4KTtcbiAgICAgICAgICB9XG4gICAgICAgICAgaWYgKHNlbGVjdGFibGUpIHtcbiAgICAgICAgICAgIGNvbnN0IGtleSA9IF8uZ2V0KHJvdywga2V5RmllbGQpO1xuICAgICAgICAgICAgb25Sb3dTZWxlY3Qoa2V5LCAhc2VsZWN0ZWQsIHJvd0luZGV4KTtcbiAgICAgICAgICB9XG4gICAgICAgIH07XG5cbiAgICAgICAgaWYgKG1vZGUgPT09IERCQ0xJQ0tfVE9fQ0VMTF9FRElUICYmIGNsaWNrVG9FZGl0KSB7XG4gICAgICAgICAgdGhpcy5jbGlja051bSArPSAxO1xuICAgICAgICAgIF8uZGVib3VuY2UoKCkgPT4ge1xuICAgICAgICAgICAgaWYgKHRoaXMuY2xpY2tOdW0gPT09IDEpIHtcbiAgICAgICAgICAgICAgY2xpY2tGbigpO1xuICAgICAgICAgICAgfVxuICAgICAgICAgICAgdGhpcy5jbGlja051bSA9IDA7XG4gICAgICAgICAgfSwgREVMQVlfRk9SX0RCQ0xJQ0spKCk7XG4gICAgICAgIH0gZWxzZSB7XG4gICAgICAgICAgY2xpY2tGbigpO1xuICAgICAgICB9XG4gICAgICB9O1xuICAgIH1cblxuICAgIGRlbGVnYXRlKGF0dHJzID0ge30pIHtcbiAgICAgIGNvbnN0IG5ld0F0dHJzID0ge307XG4gICAgICBpZiAodGhpcy5wcm9wcy5zZWxlY3RSb3cgJiYgdGhpcy5wcm9wcy5zZWxlY3RSb3cuY2xpY2tUb1NlbGVjdCkge1xuICAgICAgICBuZXdBdHRycy5vbkNsaWNrID0gdGhpcy5jcmVhdGVDbGlja0V2ZW50SGFuZGxlcihhdHRycy5vbkNsaWNrKTtcbiAgICAgIH1cbiAgICAgIE9iamVjdC5rZXlzKGF0dHJzKS5mb3JFYWNoKChhdHRyKSA9PiB7XG4gICAgICAgIGlmICghbmV3QXR0cnNbYXR0cl0pIHtcbiAgICAgICAgICBpZiAoZXZlbnRzLmluY2x1ZGVzKGF0dHIpKSB7XG4gICAgICAgICAgICBuZXdBdHRyc1thdHRyXSA9IHRoaXMuY3JlYXRlRGVmYXVsdEV2ZW50SGFuZGxlcihhdHRyc1thdHRyXSk7XG4gICAgICAgICAgfSBlbHNlIHtcbiAgICAgICAgICAgIG5ld0F0dHJzW2F0dHJdID0gYXR0cnNbYXR0cl07XG4gICAgICAgICAgfVxuICAgICAgICB9XG4gICAgICB9KTtcbiAgICAgIHJldHVybiBuZXdBdHRycztcbiAgICB9XG4gIH07XG5cblxuXG4vLyBXRUJQQUNLIEZPT1RFUiAvL1xuLy8gLi9wYWNrYWdlcy9yZWFjdC1ib290c3RyYXAtdGFibGUyL3NyYy9yb3ctZXZlbnQtZGVsZWdhdGVyLmpzIiwiaW1wb3J0IFJlYWN0IGZyb20gJ3JlYWN0JztcbmltcG9ydCBQcm9wVHlwZXMgZnJvbSAncHJvcC10eXBlcyc7XG5cbmNvbnN0IFJvd1NlY3Rpb24gPSAoeyBjb250ZW50LCBjb2xTcGFuIH0pID0+IChcbiAgPHRyPlxuICAgIDx0ZFxuICAgICAgZGF0YS10b2dnbGU9XCJjb2xsYXBzZVwiXG4gICAgICBjb2xTcGFuPXsgY29sU3BhbiB9XG4gICAgICBjbGFzc05hbWU9XCJyZWFjdC1icy10YWJsZS1uby1kYXRhXCJcbiAgICA+XG4gICAgICB7IGNvbnRlbnQgfVxuICAgIDwvdGQ+XG4gIDwvdHI+XG4pO1xuXG5Sb3dTZWN0aW9uLnByb3BUeXBlcyA9IHtcbiAgY29udGVudDogUHJvcFR5cGVzLmFueSxcbiAgY29sU3BhbjogUHJvcFR5cGVzLm51bWJlclxufTtcblxuUm93U2VjdGlvbi5kZWZhdWx0UHJvcHMgPSB7XG4gIGNvbnRlbnQ6IG51bGwsXG4gIGNvbFNwYW46IDFcbn07XG5cbmV4cG9ydCBkZWZhdWx0IFJvd1NlY3Rpb247XG5cblxuXG4vLyBXRUJQQUNLIEZPT1RFUiAvL1xuLy8gLi9wYWNrYWdlcy9yZWFjdC1ib290c3RyYXAtdGFibGUyL3NyYy9yb3ctc2VjdGlvbi5qcyIsImltcG9ydCBDb2x1bW5SZXNvbHZlciBmcm9tICcuL2NvbHVtbi1yZXNvbHZlcic7XG5pbXBvcnQgQ29uc3QgZnJvbSAnLi4vY29uc3QnO1xuaW1wb3J0IF8gZnJvbSAnLi4vdXRpbHMnO1xuXG5leHBvcnQgZGVmYXVsdCBFeHRlbmRCYXNlID0+XG4gIGNsYXNzIFRhYmxlUmVzb2x2ZXIgZXh0ZW5kcyBDb2x1bW5SZXNvbHZlcihFeHRlbmRCYXNlKSB7XG4gICAgdmFsaWRhdGVQcm9wcygpIHtcbiAgICAgIGNvbnN0IHsgY29sdW1ucywga2V5RmllbGQgfSA9IHRoaXMucHJvcHM7XG4gICAgICBpZiAoIWtleUZpZWxkKSB7XG4gICAgICAgIHRocm93IG5ldyBFcnJvcignUGxlYXNlIHNwZWNpZnkgYSBmaWVsZCBhcyBrZXkgdmlhIGtleUZpZWxkJyk7XG4gICAgICB9XG4gICAgICBpZiAodGhpcy52aXNpYmxlQ29sdW1uU2l6ZShjb2x1bW5zKSA8PSAwKSB7XG4gICAgICAgIHRocm93IG5ldyBFcnJvcignTm8gYW55IHZpc2libGUgY29sdW1ucyBkZXRlY3QnKTtcbiAgICAgIH1cbiAgICB9XG5cbiAgICBpc0VtcHR5KCkge1xuICAgICAgcmV0dXJuIHRoaXMucHJvcHMuZGF0YS5sZW5ndGggPT09IDA7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogcHJvcHMgcmVzb2x2ZXIgZm9yIGNlbGwgc2VsZWN0aW9uXG4gICAgICogQHBhcmFtIHtPYmplY3R9IG9wdGlvbnMgLSBhZGR0aW9uYWwgb3B0aW9ucyBsaWtlIGNhbGxiYWNrIHdoaWNoIGFyZSBhYm91dCB0byBtZXJnZSBpbnRvIHByb3BzXG4gICAgICpcbiAgICAgKiBAcmV0dXJucyB7T2JqZWN0fSByZXN1bHQgLSBwcm9wcyBmb3IgY2VsbCBzZWxlY3Rpb25zXG4gICAgICogQHJldHVybnMge1N0cmluZ30gcmVzdWx0Lm1vZGUgLSBpbnB1dCB0eXBlIG9mIHJvdyBzZWxlY3Rpb24gb3IgZGlzYWJsZWQuXG4gICAgICovXG4gICAgcmVzb2x2ZVNlbGVjdFJvd1Byb3BzKG9wdGlvbnMpIHtcbiAgICAgIGNvbnN0IHsgc2VsZWN0Um93IH0gPSB0aGlzLnByb3BzO1xuICAgICAgY29uc3QgeyBST1dfU0VMRUNUX0RJU0FCTEVEIH0gPSBDb25zdDtcblxuICAgICAgaWYgKF8uaXNEZWZpbmVkKHNlbGVjdFJvdykpIHtcbiAgICAgICAgcmV0dXJuIHtcbiAgICAgICAgICAuLi5zZWxlY3RSb3csXG4gICAgICAgICAgLi4ub3B0aW9uc1xuICAgICAgICB9O1xuICAgICAgfVxuXG4gICAgICByZXR1cm4ge1xuICAgICAgICBtb2RlOiBST1dfU0VMRUNUX0RJU0FCTEVEXG4gICAgICB9O1xuICAgIH1cblxuICAgIC8qKlxuICAgICAqIHByb3BzIHJlc29sdmVyIGZvciBoZWFkZXIgY2VsbCBzZWxlY3Rpb25cbiAgICAgKiBAcGFyYW0ge09iamVjdH0gb3B0aW9ucyAtIGFkZHRpb25hbCBvcHRpb25zIGxpa2UgY2FsbGJhY2sgd2hpY2ggYXJlIGFib3V0IHRvIG1lcmdlIGludG8gcHJvcHNcbiAgICAgKlxuICAgICAqIEByZXR1cm5zIHtPYmplY3R9IHJlc3VsdCAtIHByb3BzIGZvciBjZWxsIHNlbGVjdGlvbnNcbiAgICAgKiBAcmV0dXJucyB7U3RyaW5nfSByZXN1bHQubW9kZSAtIGlucHV0IHR5cGUgb2Ygcm93IHNlbGVjdGlvbiBvciBkaXNhYmxlZC5cbiAgICAgKiBAcmV0dXJucyB7U3RyaW5nfSByZXN1bHQuY2hlY2tlZFN0YXR1cyAtIGNoZWNrYm94IHN0YXR1cyBkZXBlbmRpbmcgb24gc2VsZWN0ZWQgcm93cyBjb3VudHNcbiAgICAgKi9cbiAgICByZXNvbHZlU2VsZWN0Um93UHJvcHNGb3JIZWFkZXIob3B0aW9ucyA9IHt9KSB7XG4gICAgICBjb25zdCB7IHNlbGVjdFJvdyB9ID0gdGhpcy5wcm9wcztcbiAgICAgIGNvbnN0IHsgYWxsUm93c1NlbGVjdGVkLCBzZWxlY3RlZCA9IFtdLCAuLi5yZXN0IH0gPSBvcHRpb25zO1xuICAgICAgY29uc3Qge1xuICAgICAgICBST1dfU0VMRUNUX0RJU0FCTEVELCBDSEVDS0JPWF9TVEFUVVNfQ0hFQ0tFRCxcbiAgICAgICAgQ0hFQ0tCT1hfU1RBVFVTX0lOREVURVJNSU5BVEUsIENIRUNLQk9YX1NUQVRVU19VTkNIRUNLRURcbiAgICAgIH0gPSBDb25zdDtcblxuICAgICAgaWYgKF8uaXNEZWZpbmVkKHNlbGVjdFJvdykpIHtcbiAgICAgICAgbGV0IGNoZWNrZWRTdGF0dXM7XG5cbiAgICAgICAgLy8gY2hlY2tib3ggc3RhdHVzIGRlcGVuZGluZyBvbiBzZWxlY3RlZCByb3dzIGNvdW50c1xuICAgICAgICBpZiAoYWxsUm93c1NlbGVjdGVkKSBjaGVja2VkU3RhdHVzID0gQ0hFQ0tCT1hfU1RBVFVTX0NIRUNLRUQ7XG4gICAgICAgIGVsc2UgaWYgKHNlbGVjdGVkLmxlbmd0aCA9PT0gMCkgY2hlY2tlZFN0YXR1cyA9IENIRUNLQk9YX1NUQVRVU19VTkNIRUNLRUQ7XG4gICAgICAgIGVsc2UgY2hlY2tlZFN0YXR1cyA9IENIRUNLQk9YX1NUQVRVU19JTkRFVEVSTUlOQVRFO1xuXG4gICAgICAgIHJldHVybiB7XG4gICAgICAgICAgLi4uc2VsZWN0Um93LFxuICAgICAgICAgIC4uLnJlc3QsXG4gICAgICAgICAgY2hlY2tlZFN0YXR1c1xuICAgICAgICB9O1xuICAgICAgfVxuXG4gICAgICByZXR1cm4ge1xuICAgICAgICBtb2RlOiBST1dfU0VMRUNUX0RJU0FCTEVEXG4gICAgICB9O1xuICAgIH1cbiAgfTtcblxuXG5cbi8vIFdFQlBBQ0sgRk9PVEVSIC8vXG4vLyAuL3BhY2thZ2VzL3JlYWN0LWJvb3RzdHJhcC10YWJsZTIvc3JjL3Byb3BzLXJlc29sdmVyL2luZGV4LmpzIiwiZXhwb3J0IGRlZmF1bHQgRXh0ZW5kQmFzZSA9PlxuICBjbGFzcyBDb2x1bW5SZXNvbHZlciBleHRlbmRzIEV4dGVuZEJhc2Uge1xuICAgIHZpc2libGVDb2x1bW5TaXplKCkge1xuICAgICAgcmV0dXJuIHRoaXMucHJvcHMuY29sdW1ucy5maWx0ZXIoYyA9PiAhYy5oaWRkZW4pLmxlbmd0aDtcbiAgICB9XG4gIH07XG5cblxuXG4vLyBXRUJQQUNLIEZPT1RFUiAvL1xuLy8gLi9wYWNrYWdlcy9yZWFjdC1ib290c3RyYXAtdGFibGUyL3NyYy9wcm9wcy1yZXNvbHZlci9jb2x1bW4tcmVzb2x2ZXIuanMiLCIvKiBlc2xpbnQgbm8tcmV0dXJuLWFzc2lnbjogMCAqL1xuLyogZXNsaW50IHJlYWN0L3Byb3AtdHlwZXM6IDAgKi9cbmltcG9ydCBSZWFjdCwgeyBDb21wb25lbnQgfSBmcm9tICdyZWFjdCc7XG5pbXBvcnQgU3RvcmUgZnJvbSAnLi9zdG9yZSc7XG5pbXBvcnQgd2l0aFNvcnQgZnJvbSAnLi9zb3J0L3dyYXBwZXInO1xuaW1wb3J0IHdpdGhTZWxlY3Rpb24gZnJvbSAnLi9yb3ctc2VsZWN0aW9uL3dyYXBwZXInO1xuXG5pbXBvcnQgcmVtb3RlUmVzb2x2ZXIgZnJvbSAnLi9wcm9wcy1yZXNvbHZlci9yZW1vdGUtcmVzb2x2ZXInO1xuaW1wb3J0IF8gZnJvbSAnLi91dGlscyc7XG5cbmNvbnN0IHdpdGhEYXRhU3RvcmUgPSBCYXNlID0+XG4gIGNsYXNzIEJvb3RzdHJhcFRhYmxlQ29udGFpbmVyIGV4dGVuZHMgcmVtb3RlUmVzb2x2ZXIoQ29tcG9uZW50KSB7XG4gICAgY29uc3RydWN0b3IocHJvcHMpIHtcbiAgICAgIHN1cGVyKHByb3BzKTtcbiAgICAgIHRoaXMuc3RvcmUgPSBuZXcgU3RvcmUocHJvcHMua2V5RmllbGQpO1xuICAgICAgdGhpcy5zdG9yZS5kYXRhID0gcHJvcHMuZGF0YTtcbiAgICAgIHRoaXMud3JhcENvbXBvbmVudHMoKTtcbiAgICB9XG5cbiAgICBjb21wb25lbnRXaWxsUmVjZWl2ZVByb3BzKG5leHRQcm9wcykge1xuICAgICAgdGhpcy5zdG9yZS5zZXRBbGxEYXRhKG5leHRQcm9wcy5kYXRhKTtcbiAgICB9XG5cbiAgICB3cmFwQ29tcG9uZW50cygpIHtcbiAgICAgIHRoaXMuQmFzZUNvbXBvbmVudCA9IEJhc2U7XG4gICAgICBjb25zdCB7IHBhZ2luYXRpb24sIGNvbHVtbnMsIGZpbHRlciwgc2VsZWN0Um93LCBjZWxsRWRpdCB9ID0gdGhpcy5wcm9wcztcbiAgICAgIGlmIChwYWdpbmF0aW9uKSB7XG4gICAgICAgIGNvbnN0IHsgd3JhcHBlckZhY3RvcnkgfSA9IHBhZ2luYXRpb247XG4gICAgICAgIHRoaXMuQmFzZUNvbXBvbmVudCA9IHdyYXBwZXJGYWN0b3J5KHRoaXMuQmFzZUNvbXBvbmVudCwge1xuICAgICAgICAgIHJlbW90ZVJlc29sdmVyXG4gICAgICAgIH0pO1xuICAgICAgfVxuXG4gICAgICBpZiAoY29sdW1ucy5maWx0ZXIoY29sID0+IGNvbC5zb3J0KS5sZW5ndGggPiAwKSB7XG4gICAgICAgIHRoaXMuQmFzZUNvbXBvbmVudCA9IHdpdGhTb3J0KHRoaXMuQmFzZUNvbXBvbmVudCk7XG4gICAgICB9XG5cbiAgICAgIGlmIChmaWx0ZXIpIHtcbiAgICAgICAgY29uc3QgeyB3cmFwcGVyRmFjdG9yeSB9ID0gZmlsdGVyO1xuICAgICAgICB0aGlzLkJhc2VDb21wb25lbnQgPSB3cmFwcGVyRmFjdG9yeSh0aGlzLkJhc2VDb21wb25lbnQsIHtcbiAgICAgICAgICBfLFxuICAgICAgICAgIHJlbW90ZVJlc29sdmVyXG4gICAgICAgIH0pO1xuICAgICAgfVxuXG4gICAgICBpZiAoY2VsbEVkaXQpIHtcbiAgICAgICAgY29uc3QgeyB3cmFwcGVyRmFjdG9yeSB9ID0gY2VsbEVkaXQ7XG4gICAgICAgIHRoaXMuQmFzZUNvbXBvbmVudCA9IHdyYXBwZXJGYWN0b3J5KHRoaXMuQmFzZUNvbXBvbmVudCwge1xuICAgICAgICAgIF8sXG4gICAgICAgICAgcmVtb3RlUmVzb2x2ZXJcbiAgICAgICAgfSk7XG4gICAgICB9XG5cbiAgICAgIGlmIChzZWxlY3RSb3cpIHtcbiAgICAgICAgdGhpcy5CYXNlQ29tcG9uZW50ID0gd2l0aFNlbGVjdGlvbih0aGlzLkJhc2VDb21wb25lbnQpO1xuICAgICAgfVxuICAgIH1cblxuICAgIHJlbmRlcigpIHtcbiAgICAgIGNvbnN0IGJhc2VQcm9wcyA9IHtcbiAgICAgICAgLi4udGhpcy5wcm9wcyxcbiAgICAgICAgc3RvcmU6IHRoaXMuc3RvcmVcbiAgICAgIH07XG5cbiAgICAgIHJldHVybiAoXG4gICAgICAgIDx0aGlzLkJhc2VDb21wb25lbnQgeyAuLi5iYXNlUHJvcHMgfSAvPlxuICAgICAgKTtcbiAgICB9XG4gIH07XG5cbmV4cG9ydCBkZWZhdWx0IHdpdGhEYXRhU3RvcmU7XG5cblxuXG4vLyBXRUJQQUNLIEZPT1RFUiAvL1xuLy8gLi9wYWNrYWdlcy9yZWFjdC1ib290c3RyYXAtdGFibGUyL3NyYy9jb250YWluZXIuanMiLCIvKiBlc2xpbnQgbm8tdW5kZXJzY29yZS1kYW5nbGU6IDAgKi9cbmltcG9ydCBfIGZyb20gJy4uL3V0aWxzJztcbmltcG9ydCB7IHNvcnQsIG5leHRPcmRlciB9IGZyb20gJy4vc29ydCc7XG5pbXBvcnQgeyBnZXRSb3dCeVJvd0lkIH0gZnJvbSAnLi9yb3dzJztcblxuZXhwb3J0IGRlZmF1bHQgY2xhc3MgU3RvcmUge1xuICBjb25zdHJ1Y3RvcihrZXlGaWVsZCkge1xuICAgIHRoaXMuX2RhdGEgPSBbXTtcbiAgICB0aGlzLl9maWx0ZXJlZERhdGEgPSBbXTtcbiAgICB0aGlzLl9rZXlGaWVsZCA9IGtleUZpZWxkO1xuICAgIHRoaXMuX3NvcnRPcmRlciA9IHVuZGVmaW5lZDtcbiAgICB0aGlzLl9zb3J0RmllbGQgPSB1bmRlZmluZWQ7XG4gICAgdGhpcy5fc2VsZWN0ZWQgPSBbXTtcbiAgICB0aGlzLl9maWx0ZXJzID0ge307XG4gICAgdGhpcy5fcGFnZSA9IHVuZGVmaW5lZDtcbiAgICB0aGlzLl9zaXplUGVyUGFnZSA9IHVuZGVmaW5lZDtcbiAgfVxuXG4gIGVkaXQocm93SWQsIGRhdGFGaWVsZCwgbmV3VmFsdWUpIHtcbiAgICBjb25zdCByb3cgPSBnZXRSb3dCeVJvd0lkKHRoaXMpKHJvd0lkKTtcbiAgICBpZiAocm93KSBfLnNldChyb3csIGRhdGFGaWVsZCwgbmV3VmFsdWUpO1xuICB9XG5cbiAgc2V0U29ydCh7IGRhdGFGaWVsZCB9LCBvcmRlcikge1xuICAgIHRoaXMuc29ydE9yZGVyID0gbmV4dE9yZGVyKHRoaXMpKGRhdGFGaWVsZCwgb3JkZXIpO1xuICAgIHRoaXMuc29ydEZpZWxkID0gZGF0YUZpZWxkO1xuICB9XG5cbiAgc29ydEJ5KHsgc29ydEZ1bmMgfSkge1xuICAgIHRoaXMuZGF0YSA9IHNvcnQodGhpcykoc29ydEZ1bmMpO1xuICB9XG5cbiAgZ2V0QWxsRGF0YSgpIHtcbiAgICByZXR1cm4gdGhpcy5fZGF0YTtcbiAgfVxuXG4gIHNldEFsbERhdGEoZGF0YSkge1xuICAgIHRoaXMuX2RhdGEgPSBkYXRhO1xuICB9XG5cbiAgZ2V0IGRhdGEoKSB7XG4gICAgaWYgKE9iamVjdC5rZXlzKHRoaXMuX2ZpbHRlcnMpLmxlbmd0aCA+IDApIHtcbiAgICAgIHJldHVybiB0aGlzLl9maWx0ZXJlZERhdGE7XG4gICAgfVxuICAgIHJldHVybiB0aGlzLl9kYXRhO1xuICB9XG4gIHNldCBkYXRhKGRhdGEpIHtcbiAgICBpZiAoT2JqZWN0LmtleXModGhpcy5fZmlsdGVycykubGVuZ3RoID4gMCkge1xuICAgICAgdGhpcy5fZmlsdGVyZWREYXRhID0gZGF0YTtcbiAgICB9IGVsc2Uge1xuICAgICAgdGhpcy5fZGF0YSA9IChkYXRhID8gSlNPTi5wYXJzZShKU09OLnN0cmluZ2lmeShkYXRhKSkgOiBbXSk7XG4gICAgfVxuICB9XG5cbiAgZ2V0IGZpbHRlcmVkRGF0YSgpIHsgcmV0dXJuIHRoaXMuX2ZpbHRlcmVkRGF0YTsgfVxuICBzZXQgZmlsdGVyZWREYXRhKGZpbHRlcmVkRGF0YSkgeyB0aGlzLl9maWx0ZXJlZERhdGEgPSBmaWx0ZXJlZERhdGE7IH1cblxuICBnZXQga2V5RmllbGQoKSB7IHJldHVybiB0aGlzLl9rZXlGaWVsZDsgfVxuICBzZXQga2V5RmllbGQoa2V5RmllbGQpIHsgdGhpcy5fa2V5RmllbGQgPSBrZXlGaWVsZDsgfVxuXG4gIGdldCBzb3J0T3JkZXIoKSB7IHJldHVybiB0aGlzLl9zb3J0T3JkZXI7IH1cbiAgc2V0IHNvcnRPcmRlcihzb3J0T3JkZXIpIHsgdGhpcy5fc29ydE9yZGVyID0gc29ydE9yZGVyOyB9XG5cbiAgZ2V0IHBhZ2UoKSB7IHJldHVybiB0aGlzLl9wYWdlOyB9XG4gIHNldCBwYWdlKHBhZ2UpIHsgdGhpcy5fcGFnZSA9IHBhZ2U7IH1cblxuICBnZXQgc2l6ZVBlclBhZ2UoKSB7IHJldHVybiB0aGlzLl9zaXplUGVyUGFnZTsgfVxuICBzZXQgc2l6ZVBlclBhZ2Uoc2l6ZVBlclBhZ2UpIHsgdGhpcy5fc2l6ZVBlclBhZ2UgPSBzaXplUGVyUGFnZTsgfVxuXG4gIGdldCBzb3J0RmllbGQoKSB7IHJldHVybiB0aGlzLl9zb3J0RmllbGQ7IH1cbiAgc2V0IHNvcnRGaWVsZChzb3J0RmllbGQpIHsgdGhpcy5fc29ydEZpZWxkID0gc29ydEZpZWxkOyB9XG5cbiAgZ2V0IHNlbGVjdGVkKCkgeyByZXR1cm4gdGhpcy5fc2VsZWN0ZWQ7IH1cbiAgc2V0IHNlbGVjdGVkKHNlbGVjdGVkKSB7IHRoaXMuX3NlbGVjdGVkID0gc2VsZWN0ZWQ7IH1cblxuICBnZXQgZmlsdGVycygpIHsgcmV0dXJuIHRoaXMuX2ZpbHRlcnM7IH1cbiAgc2V0IGZpbHRlcnMoZmlsdGVycykgeyB0aGlzLl9maWx0ZXJzID0gZmlsdGVyczsgfVxufVxuXG5cblxuLy8gV0VCUEFDSyBGT09URVIgLy9cbi8vIC4vcGFja2FnZXMvcmVhY3QtYm9vdHN0cmFwLXRhYmxlMi9zcmMvc3RvcmUvaW5kZXguanMiLCIvKiBlc2xpbnQgbm8tbmVzdGVkLXRlcm5hcnk6IDAgKi9cbi8qIGVzbGludCBuby1sb25lbHktaWY6IDAgKi9cbi8qIGVzbGludCBuby11bmRlcnNjb3JlLWRhbmdsZTogMCAqL1xuaW1wb3J0IF8gZnJvbSAnLi4vdXRpbHMnO1xuaW1wb3J0IENvbnN0IGZyb20gJy4uL2NvbnN0JztcblxuZnVuY3Rpb24gY29tcGFyYXRvcihhLCBiKSB7XG4gIGxldCByZXN1bHQ7XG4gIGlmICh0eXBlb2YgYiA9PT0gJ3N0cmluZycpIHtcbiAgICByZXN1bHQgPSBiLmxvY2FsZUNvbXBhcmUoYSk7XG4gIH0gZWxzZSB7XG4gICAgcmVzdWx0ID0gYSA+IGIgPyAtMSA6ICgoYSA8IGIpID8gMSA6IDApO1xuICB9XG4gIHJldHVybiByZXN1bHQ7XG59XG5cbmV4cG9ydCBjb25zdCBzb3J0ID0gKHsgZGF0YSwgc29ydE9yZGVyLCBzb3J0RmllbGQgfSkgPT4gKHNvcnRGdW5jKSA9PiB7XG4gIGNvbnN0IF9kYXRhID0gWy4uLmRhdGFdO1xuICBfZGF0YS5zb3J0KChhLCBiKSA9PiB7XG4gICAgbGV0IHJlc3VsdDtcbiAgICBsZXQgdmFsdWVBID0gXy5nZXQoYSwgc29ydEZpZWxkKTtcbiAgICBsZXQgdmFsdWVCID0gXy5nZXQoYiwgc29ydEZpZWxkKTtcbiAgICB2YWx1ZUEgPSBfLmlzRGVmaW5lZCh2YWx1ZUEpID8gdmFsdWVBIDogJyc7XG4gICAgdmFsdWVCID0gXy5pc0RlZmluZWQodmFsdWVCKSA/IHZhbHVlQiA6ICcnO1xuXG4gICAgaWYgKHNvcnRGdW5jKSB7XG4gICAgICByZXN1bHQgPSBzb3J0RnVuYyh2YWx1ZUEsIHZhbHVlQiwgc29ydE9yZGVyLCBzb3J0RmllbGQpO1xuICAgIH0gZWxzZSB7XG4gICAgICBpZiAoc29ydE9yZGVyID09PSBDb25zdC5TT1JUX0RFU0MpIHtcbiAgICAgICAgcmVzdWx0ID0gY29tcGFyYXRvcih2YWx1ZUEsIHZhbHVlQik7XG4gICAgICB9IGVsc2Uge1xuICAgICAgICByZXN1bHQgPSBjb21wYXJhdG9yKHZhbHVlQiwgdmFsdWVBKTtcbiAgICAgIH1cbiAgICB9XG4gICAgcmV0dXJuIHJlc3VsdDtcbiAgfSk7XG4gIHJldHVybiBfZGF0YTtcbn07XG5cbmV4cG9ydCBjb25zdCBuZXh0T3JkZXIgPSBzdG9yZSA9PiAoZmllbGQsIG9yZGVyKSA9PiB7XG4gIGlmIChvcmRlcikgcmV0dXJuIG9yZGVyO1xuXG4gIGlmIChmaWVsZCAhPT0gc3RvcmUuc29ydEZpZWxkKSB7XG4gICAgcmV0dXJuIENvbnN0LlNPUlRfREVTQztcbiAgfVxuICByZXR1cm4gc3RvcmUuc29ydE9yZGVyID09PSBDb25zdC5TT1JUX0RFU0MgPyBDb25zdC5TT1JUX0FTQyA6IENvbnN0LlNPUlRfREVTQztcbn07XG5cblxuXG4vLyBXRUJQQUNLIEZPT1RFUiAvL1xuLy8gLi9wYWNrYWdlcy9yZWFjdC1ib290c3RyYXAtdGFibGUyL3NyYy9zdG9yZS9zb3J0LmpzIiwiLyogZXNsaW50IHJlYWN0L3Byb3AtdHlwZXM6IDAgKi9cbmltcG9ydCBSZWFjdCwgeyBDb21wb25lbnQgfSBmcm9tICdyZWFjdCc7XG5pbXBvcnQgUHJvcFR5cGVzIGZyb20gJ3Byb3AtdHlwZXMnO1xuaW1wb3J0IHJlbW90ZVJlc29sdmVyIGZyb20gJy4uL3Byb3BzLXJlc29sdmVyL3JlbW90ZS1yZXNvbHZlcic7XG5cbmV4cG9ydCBkZWZhdWx0IEJhc2UgPT5cbiAgY2xhc3MgU29ydFdyYXBwZXIgZXh0ZW5kcyByZW1vdGVSZXNvbHZlcihDb21wb25lbnQpIHtcbiAgICBzdGF0aWMgcHJvcFR5cGVzID0ge1xuICAgICAgc3RvcmU6IFByb3BUeXBlcy5vYmplY3QuaXNSZXF1aXJlZFxuICAgIH1cblxuICAgIGNvbnN0cnVjdG9yKHByb3BzKSB7XG4gICAgICBzdXBlcihwcm9wcyk7XG4gICAgICB0aGlzLmhhbmRsZVNvcnQgPSB0aGlzLmhhbmRsZVNvcnQuYmluZCh0aGlzKTtcbiAgICB9XG5cbiAgICBjb21wb25lbnRXaWxsTW91bnQoKSB7XG4gICAgICBjb25zdCB7IGNvbHVtbnMsIGRlZmF1bHRTb3J0ZWQsIHN0b3JlIH0gPSB0aGlzLnByb3BzO1xuICAgICAgLy8gZGVmYXVsdFNvcnRlZCBpcyBhbiBhcnJheSwgaXQncyByZWFkeSB0byB1c2UgYXMgbXVsdGkgLyBzaW5nbGUgc29ydFxuICAgICAgLy8gd2hlbiB3ZSBzdGFydCB0byBzdXBwb3J0IG11bHRpIHNvcnQsIHBsZWFzZSB1cGRhdGUgZm9sbG93aW5nIGNvZGUgdG8gdXNlIGFycmF5LmZvckVhY2hcbiAgICAgIGlmIChkZWZhdWx0U29ydGVkICYmIGRlZmF1bHRTb3J0ZWQubGVuZ3RoID4gMCkge1xuICAgICAgICBjb25zdCBkYXRhRmllbGQgPSBkZWZhdWx0U29ydGVkWzBdLmRhdGFGaWVsZDtcbiAgICAgICAgY29uc3Qgb3JkZXIgPSBkZWZhdWx0U29ydGVkWzBdLm9yZGVyO1xuICAgICAgICBjb25zdCBjb2x1bW4gPSBjb2x1bW5zLmZpbHRlcihjb2wgPT4gY29sLmRhdGFGaWVsZCA9PT0gZGF0YUZpZWxkKTtcbiAgICAgICAgaWYgKGNvbHVtbi5sZW5ndGggPiAwKSB7XG4gICAgICAgICAgc3RvcmUuc2V0U29ydChjb2x1bW5bMF0sIG9yZGVyKTtcblxuICAgICAgICAgIGlmIChjb2x1bW5bMF0ub25Tb3J0KSB7XG4gICAgICAgICAgICBjb2x1bW5bMF0ub25Tb3J0KHN0b3JlLnNvcnRGaWVsZCwgc3RvcmUuc29ydE9yZGVyKTtcbiAgICAgICAgICB9XG5cbiAgICAgICAgICBpZiAodGhpcy5pc1JlbW90ZVNvcnQoKSB8fCB0aGlzLmlzUmVtb3RlUGFnaW5hdGlvbigpKSB7XG4gICAgICAgICAgICB0aGlzLmhhbmRsZVNvcnRDaGFuZ2UoKTtcbiAgICAgICAgICB9IGVsc2Uge1xuICAgICAgICAgICAgc3RvcmUuc29ydEJ5KGNvbHVtblswXSk7XG4gICAgICAgICAgfVxuICAgICAgICB9XG4gICAgICB9XG4gICAgfVxuXG4gICAgY29tcG9uZW50V2lsbFJlY2VpdmVQcm9wcyhuZXh0UHJvcHMpIHtcbiAgICAgIGNvbnN0IHNvcnRlZENvbHVtbiA9IG5leHRQcm9wcy5jb2x1bW5zLmZpbmQoXG4gICAgICAgIGNvbHVtbiA9PiBjb2x1bW4uZGF0YUZpZWxkID09PSBuZXh0UHJvcHMuc3RvcmUuc29ydEZpZWxkKTtcbiAgICAgIGlmIChzb3J0ZWRDb2x1bW4gJiYgc29ydGVkQ29sdW1uLnNvcnQpIHtcbiAgICAgICAgbmV4dFByb3BzLnN0b3JlLnNvcnRCeShzb3J0ZWRDb2x1bW4pO1xuICAgICAgfVxuICAgIH1cblxuICAgIGhhbmRsZVNvcnQoY29sdW1uKSB7XG4gICAgICBjb25zdCB7IHN0b3JlIH0gPSB0aGlzLnByb3BzO1xuICAgICAgc3RvcmUuc2V0U29ydChjb2x1bW4pO1xuXG4gICAgICBpZiAoY29sdW1uLm9uU29ydCkge1xuICAgICAgICBjb2x1bW4ub25Tb3J0KHN0b3JlLnNvcnRGaWVsZCwgc3RvcmUuc29ydE9yZGVyKTtcbiAgICAgIH1cblxuICAgICAgaWYgKHRoaXMuaXNSZW1vdGVTb3J0KCkgfHwgdGhpcy5pc1JlbW90ZVBhZ2luYXRpb24oKSkge1xuICAgICAgICB0aGlzLmhhbmRsZVNvcnRDaGFuZ2UoKTtcbiAgICAgIH0gZWxzZSB7XG4gICAgICAgIHN0b3JlLnNvcnRCeShjb2x1bW4pO1xuICAgICAgICB0aGlzLmZvcmNlVXBkYXRlKCk7XG4gICAgICB9XG4gICAgfVxuXG4gICAgcmVuZGVyKCkge1xuICAgICAgcmV0dXJuIChcbiAgICAgICAgPEJhc2VcbiAgICAgICAgICB7IC4uLnRoaXMucHJvcHMgfVxuICAgICAgICAgIG9uU29ydD17IHRoaXMuaGFuZGxlU29ydCB9XG4gICAgICAgICAgZGF0YT17IHRoaXMucHJvcHMuc3RvcmUuZGF0YSB9XG4gICAgICAgIC8+XG4gICAgICApO1xuICAgIH1cbiAgfTtcblxuXG5cbi8vIFdFQlBBQ0sgRk9PVEVSIC8vXG4vLyAuL3BhY2thZ2VzL3JlYWN0LWJvb3RzdHJhcC10YWJsZTIvc3JjL3NvcnQvd3JhcHBlci5qcyIsIi8qIGVzbGludCBuby1wYXJhbS1yZWFzc2lnbjogMCAqL1xuaW1wb3J0IFJlYWN0LCB7IENvbXBvbmVudCB9IGZyb20gJ3JlYWN0JztcbmltcG9ydCBQcm9wVHlwZXMgZnJvbSAncHJvcC10eXBlcyc7XG5cbmltcG9ydCBDb25zdCBmcm9tICcuLi9jb25zdCc7XG5pbXBvcnQge1xuICBpc0FueVNlbGVjdGVkUm93LFxuICBzZWxlY3RhYmxlS2V5cyxcbiAgdW5TZWxlY3RhYmxlS2V5cyxcbiAgZ2V0U2VsZWN0ZWRSb3dzXG59IGZyb20gJy4uL3N0b3JlL3NlbGVjdGlvbic7XG5pbXBvcnQgeyBnZXRSb3dCeVJvd0lkIH0gZnJvbSAnLi4vc3RvcmUvcm93cyc7XG5cbmV4cG9ydCBkZWZhdWx0IEJhc2UgPT5cbiAgY2xhc3MgUm93U2VsZWN0aW9uV3JhcHBlciBleHRlbmRzIENvbXBvbmVudCB7XG4gICAgc3RhdGljIHByb3BUeXBlcyA9IHtcbiAgICAgIHN0b3JlOiBQcm9wVHlwZXMub2JqZWN0LmlzUmVxdWlyZWQsXG4gICAgICBzZWxlY3RSb3c6IFByb3BUeXBlcy5vYmplY3QuaXNSZXF1aXJlZFxuICAgIH1cblxuICAgIGNvbnN0cnVjdG9yKHByb3BzKSB7XG4gICAgICBzdXBlcihwcm9wcyk7XG4gICAgICB0aGlzLmhhbmRsZVJvd1NlbGVjdCA9IHRoaXMuaGFuZGxlUm93U2VsZWN0LmJpbmQodGhpcyk7XG4gICAgICB0aGlzLmhhbmRsZUFsbFJvd3NTZWxlY3QgPSB0aGlzLmhhbmRsZUFsbFJvd3NTZWxlY3QuYmluZCh0aGlzKTtcbiAgICAgIHByb3BzLnN0b3JlLnNlbGVjdGVkID0gdGhpcy5wcm9wcy5zZWxlY3RSb3cuc2VsZWN0ZWQgfHwgW107XG4gICAgICB0aGlzLnN0YXRlID0ge1xuICAgICAgICBzZWxlY3RlZFJvd0tleXM6IHByb3BzLnN0b3JlLnNlbGVjdGVkXG4gICAgICB9O1xuICAgIH1cblxuICAgIGNvbXBvbmVudFdpbGxSZWNlaXZlUHJvcHMobmV4dFByb3BzKSB7XG4gICAgICBpZiAobmV4dFByb3BzLnNlbGVjdFJvdykge1xuICAgICAgICB0aGlzLnN0b3JlLnNlbGVjdGVkID0gbmV4dFByb3BzLnNlbGVjdFJvdy5zZWxlY3RlZCB8fCBbXTtcbiAgICAgICAgdGhpcy5zZXRTdGF0ZSgoKSA9PiAoe1xuICAgICAgICAgIHNlbGVjdGVkUm93S2V5czogdGhpcy5zdG9yZS5zZWxlY3RlZFxuICAgICAgICB9KSk7XG4gICAgICB9XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogcm93IHNlbGVjdGlvbiBoYW5kbGVyXG4gICAgICogQHBhcmFtIHtTdHJpbmd9IHJvd0tleSAtIHJvdyBrZXkgb2Ygd2hhdCB3YXMgc2VsZWN0ZWQuXG4gICAgICogQHBhcmFtIHtCb29sZWFufSBjaGVja2VkIC0gbmV4dCBjaGVja2VkIHN0YXR1cyBvZiBpbnB1dCBidXR0b24uXG4gICAgICovXG4gICAgaGFuZGxlUm93U2VsZWN0KHJvd0tleSwgY2hlY2tlZCwgcm93SW5kZXgpIHtcbiAgICAgIGNvbnN0IHsgc2VsZWN0Um93OiB7IG1vZGUsIG9uU2VsZWN0IH0sIHN0b3JlIH0gPSB0aGlzLnByb3BzO1xuICAgICAgY29uc3QgeyBST1dfU0VMRUNUX1NJTkdMRSB9ID0gQ29uc3Q7XG5cbiAgICAgIGxldCBjdXJyU2VsZWN0ZWQgPSBbLi4uc3RvcmUuc2VsZWN0ZWRdO1xuXG4gICAgICBpZiAobW9kZSA9PT0gUk9XX1NFTEVDVF9TSU5HTEUpIHsgLy8gd2hlbiBzZWxlY3QgbW9kZSBpcyByYWRpb1xuICAgICAgICBjdXJyU2VsZWN0ZWQgPSBbcm93S2V5XTtcbiAgICAgIH0gZWxzZSBpZiAoY2hlY2tlZCkgeyAvLyB3aGVuIHNlbGVjdCBtb2RlIGlzIGNoZWNrYm94XG4gICAgICAgIGN1cnJTZWxlY3RlZC5wdXNoKHJvd0tleSk7XG4gICAgICB9IGVsc2Uge1xuICAgICAgICBjdXJyU2VsZWN0ZWQgPSBjdXJyU2VsZWN0ZWQuZmlsdGVyKHZhbHVlID0+IHZhbHVlICE9PSByb3dLZXkpO1xuICAgICAgfVxuXG4gICAgICBzdG9yZS5zZWxlY3RlZCA9IGN1cnJTZWxlY3RlZDtcblxuICAgICAgaWYgKG9uU2VsZWN0KSB7XG4gICAgICAgIGNvbnN0IHJvdyA9IGdldFJvd0J5Um93SWQoc3RvcmUpKHJvd0tleSk7XG4gICAgICAgIG9uU2VsZWN0KHJvdywgY2hlY2tlZCwgcm93SW5kZXgpO1xuICAgICAgfVxuXG4gICAgICB0aGlzLnNldFN0YXRlKCgpID0+ICh7XG4gICAgICAgIHNlbGVjdGVkUm93S2V5czogY3VyclNlbGVjdGVkXG4gICAgICB9KSk7XG4gICAgfVxuXG4gICAgLyoqXG4gICAgICogaGFuZGxlIGFsbCByb3dzIHNlbGVjdGlvbiBvbiBoZWFkZXIgY2VsbCBieSBzdG9yZS5zZWxlY3RlZCBvciBnaXZlbiBzcGVjaWZpYyByZXN1bHQuXG4gICAgICogQHBhcmFtIHtCb29sZWFufSBvcHRpb24gLSBjdXN0b21pemVkIHJlc3VsdCBmb3IgYWxsIHJvd3Mgc2VsZWN0aW9uXG4gICAgICovXG4gICAgaGFuZGxlQWxsUm93c1NlbGVjdChvcHRpb24pIHtcbiAgICAgIGNvbnN0IHsgc3RvcmUsIHNlbGVjdFJvdzoge1xuICAgICAgICBvblNlbGVjdEFsbCxcbiAgICAgICAgbm9uU2VsZWN0YWJsZVxuICAgICAgfSB9ID0gdGhpcy5wcm9wcztcbiAgICAgIGNvbnN0IHNlbGVjdGVkID0gaXNBbnlTZWxlY3RlZFJvdyhzdG9yZSkobm9uU2VsZWN0YWJsZSk7XG5cbiAgICAgIC8vIHNldCBuZXh0IHN0YXR1cyBvZiBhbGwgcm93IHNlbGVjdGVkIGJ5IHN0b3JlLnNlbGVjdGVkIG9yIGN1c3RvbWl6aW5nIGJ5IHVzZXIuXG4gICAgICBjb25zdCByZXN1bHQgPSBvcHRpb24gfHwgIXNlbGVjdGVkO1xuXG4gICAgICBjb25zdCBjdXJyU2VsZWN0ZWQgPSByZXN1bHQgP1xuICAgICAgICBzZWxlY3RhYmxlS2V5cyhzdG9yZSkobm9uU2VsZWN0YWJsZSkgOlxuICAgICAgICB1blNlbGVjdGFibGVLZXlzKHN0b3JlKShub25TZWxlY3RhYmxlKTtcblxuXG4gICAgICBzdG9yZS5zZWxlY3RlZCA9IGN1cnJTZWxlY3RlZDtcblxuICAgICAgaWYgKG9uU2VsZWN0QWxsKSB7XG4gICAgICAgIG9uU2VsZWN0QWxsKHJlc3VsdCwgZ2V0U2VsZWN0ZWRSb3dzKHN0b3JlKSk7XG4gICAgICB9XG5cbiAgICAgIHRoaXMuc2V0U3RhdGUoKCkgPT4gKHtcbiAgICAgICAgc2VsZWN0ZWRSb3dLZXlzOiBjdXJyU2VsZWN0ZWRcbiAgICAgIH0pKTtcbiAgICB9XG5cbiAgICByZW5kZXIoKSB7XG4gICAgICByZXR1cm4gKFxuICAgICAgICA8QmFzZVxuICAgICAgICAgIHsgLi4udGhpcy5wcm9wcyB9XG4gICAgICAgICAgb25Sb3dTZWxlY3Q9eyB0aGlzLmhhbmRsZVJvd1NlbGVjdCB9XG4gICAgICAgICAgb25BbGxSb3dzU2VsZWN0PXsgdGhpcy5oYW5kbGVBbGxSb3dzU2VsZWN0IH1cbiAgICAgICAgLz5cbiAgICAgICk7XG4gICAgfVxuICB9O1xuXG5cblxuLy8gV0VCUEFDSyBGT09URVIgLy9cbi8vIC4vcGFja2FnZXMvcmVhY3QtYm9vdHN0cmFwLXRhYmxlMi9zcmMvcm93LXNlbGVjdGlvbi93cmFwcGVyLmpzIl0sInNvdXJjZVJvb3QiOiIifQ==
//# sourceMappingURL=react-bootstrap-table2.js.map