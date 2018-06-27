/**
 * Bundle of @devexpress/dx-react-grid
 * Generated: 2018-03-20
 * Version: 1.1.1
 * License: https://js.devexpress.com/Licensing
 */

var DXReactGrid=function(){
//import { Fragment, PureComponent, createElement } from 'react';
//import { any, array, arrayOf, bool, func, node, number, object, shape, string } from 'prop-types';
var any=PropTypes.any, array=PropTypes.array, arrayOf=PropTypes.arrayOf, bool=PropTypes.bool, func=PropTypes.func
	, node=PropTypes.node, number=PropTypes.number, object=PropTypes.object, shape=PropTypes.shape, string=PropTypes.string;
//import { Action, DragDropProvider, DragSource, DropTarget, Getter, Plugin, PluginHost, Template, TemplateConnector, TemplatePlaceholder } from '@devexpress/dx-react-core';
var Action=DXReactCore.Action, DragDropProvider=DXReactCore.DragDropProvider, DragSource=DXReactCore.DragSource, DropTarget=DXReactCore.DropTarget
	, Getter=DXReactCore.Getter, Plugin=DXReactCore.Plugin, PluginHost=DXReactCore.PluginHost, Template=DXReactCore.Template
	, TemplateConnector=DXReactCore.TemplateConnector, TemplatePlaceholder=DXReactCore.TemplatePlaceholder;
//import { TABLE_DATA_TYPE, TABLE_REORDERING_TYPE, addRow, addedRowsByIds, allSelected, calculateKeepOther, cancelAddedRows, cancelChanges, cancelColumnGroupingDraft, cancelDeletedRows, cancelTableColumnWidthDraft, cellValueGetter, changeAddedRow, changeColumnFilter, changeColumnGrouping, changeColumnOrder, changeColumnSorting, changeRow, changeSearchValue, changeTableColumnWidth, changedRowsByIds, columnChooserItems, createRowChangeGetter, customGroupedRows, customGroupingRowIdGetter, deleteRows, draftColumnGrouping, draftOrder, draftTableColumnWidth, evalAnimations, expandedGroupRows, filterActiveAnimations, filteredRows, getAnimations, getColumnExtension, getColumnExtensionValueGetter, getColumnFilterConfig, getColumnSortingDirection, getGroupCellTargetIndex, getMessagesFormatter, getPersistentSortedColumns, getRowChange, getTableRowColumnsWithColSpan, getTableTargetColumnIndex, groupRowChecker, groupRowLevelKeyGetter, groupedRows, groupingPanelItems, isAddedTableRow, isDataTableCell, isDataTableRow, isDetailRowExpanded, isDetailTableRow, isDetailToggleTableCell, isEditCommandsTableCell, isEditTableCell, isEditTableRow, isFilterTableCell, isFilterTableRow, isGroupIndentTableCell, isGroupTableCell, isGroupTableRow, isHeaderStubTableCell, isHeadingEditCommandsTableCell, isHeadingTableCell, isHeadingTableRow, isNoDataTableRow, isSelectAllTableCell, isSelectTableCell, orderedColumns, pageCount, paginatedRows, pushFilterExpression, pushSearchFilterExpression, rowCount, rowIdGetter, rowsWithAvailableToSelect, rowsWithPageHeaders, setCurrentPage, setPageSize, someSelected, sortedRows, startEditRows, stopEditRows, tableColumnsWithDataRows, tableColumnsWithDetail, tableColumnsWithEditing, tableColumnsWithGrouping, tableColumnsWithSelection, tableColumnsWithWidths, tableDataColumnsExist, tableHeaderRowsWithFilter, tableHeaderRowsWithReordering, tableRowsWithDataRows, tableRowsWithEditing, tableRowsWithExpandedDetail, tableRowsWithGrouping, tableRowsWithHeading, toggleColumn, toggleDetailRowExpanded, toggleExpandedGroups, toggleSelection, unwrapSelectedRows, visibleTableColumns } from '@devexpress/dx-grid-core';
var TABLE_DATA_TYPE=DXGridCore.TABLE_DATA_TYPE, TABLE_REORDERING_TYPE=DXGridCore.TABLE_REORDERING_TYPE, addRow=DXGridCore.addRow
	, addedRowsByIds=DXGridCore.addedRowsByIds, allSelected=DXGridCore.allSelected, calculateKeepOther=DXGridCore.calculateKeepOther
	, cancelAddedRows=DXGridCore.cancelAddedRows, cancelChanges=DXGridCore.cancelChanges, cancelColumnGroupingDraft=DXGridCore.cancelColumnGroupingDraft
	, cancelDeletedRows=DXGridCore.cancelDeletedRows, cancelTableColumnWidthDraft=DXGridCore.cancelTableColumnWidthDraft, cellValueGetter=DXGridCore.cellValueGetter
	, changeAddedRow=DXGridCore.changeAddedRow, changeColumnFilter=DXGridCore.changeColumnFilter, changeColumnGrouping=DXGridCore.changeColumnGrouping
	, changeColumnOrder=DXGridCore.changeColumnOrder, changeColumnSorting=DXGridCore.changeColumnSorting, changeRow=DXGridCore.changeRow
	, changeSearchValue=DXGridCore.changeSearchValue, changeTableColumnWidth=DXGridCore.changeTableColumnWidth, changedRowsByIds=DXGridCore.changedRowsByIds
	, columnChooserItems=DXGridCore.columnChooserItems, createRowChangeGetter=DXGridCore.createRowChangeGetter, customGroupedRows=DXGridCore.customGroupedRows
	, customGroupingRowIdGetter=DXGridCore.customGroupingRowIdGetter, deleteRows=DXGridCore.deleteRows, draftColumnGrouping=DXGridCore.draftColumnGrouping
	, draftOrder=DXGridCore.draftOrder, draftTableColumnWidth=DXGridCore.draftTableColumnWidth, evalAnimations=DXGridCore.evalAnimations
	, expandedGroupRows=DXGridCore.expandedGroupRows, filterActiveAnimations=DXGridCore.filterActiveAnimations, filteredRows=DXGridCore.filteredRows
	, getAnimations=DXGridCore.getAnimations, getColumnExtension=DXGridCore.getColumnExtension, getColumnExtensionValueGetter=DXGridCore.getColumnExtensionValueGetter
	, getColumnFilterConfig=DXGridCore.getColumnFilterConfig, getColumnSortingDirection=DXGridCore.getColumnSortingDirection, getGroupCellTargetIndex=DXGridCore.getGroupCellTargetIndex
	, getMessagesFormatter=DXGridCore.getMessagesFormatter, getPersistentSortedColumns=DXGridCore.getPersistentSortedColumns, getRowChange=DXGridCore.getRowChange
	, getTableRowColumnsWithColSpan=DXGridCore.getTableRowColumnsWithColSpan, getTableTargetColumnIndex=DXGridCore.getTableTargetColumnIndex, groupRowChecker=DXGridCore.groupRowChecker
	, groupRowLevelKeyGetter=DXGridCore.groupRowLevelKeyGetter, groupedRows=DXGridCore.groupedRows, groupingPanelItems=DXGridCore.groupingPanelItems
	, isAddedTableRow=DXGridCore.isAddedTableRow, isDataTableCell=DXGridCore.isDataTableCell, isDataTableRow=DXGridCore.isDataTableRow
	, isDetailRowExpanded=DXGridCore.isDetailRowExpanded, isDetailTableRow=DXGridCore.isDetailTableRow, isDetailToggleTableCell=DXGridCore.isDetailToggleTableCell
	, isEditCommandsTableCell=DXGridCore.isEditCommandsTableCell, isEditTableCell=DXGridCore.isEditTableCell, isEditTableRow=DXGridCore.isEditTableRow
	, isFilterTableCell=DXGridCore.isFilterTableCell, isFilterTableRow=DXGridCore.isFilterTableRow, isGroupIndentTableCell=DXGridCore.isGroupIndentTableCell
	, isGroupTableCell=DXGridCore.isGroupTableCell, isGroupTableRow=DXGridCore.isGroupTableRow, isHeaderStubTableCell=DXGridCore.isHeaderStubTableCell
	, isHeadingEditCommandsTableCell=DXGridCore.isHeadingEditCommandsTableCell, isHeadingTableCell=DXGridCore.isHeadingTableCell, isHeadingTableRow=DXGridCore.isHeadingTableRow
	, isNoDataTableRow=DXGridCore.isNoDataTableRow, isSelectAllTableCell=DXGridCore.isSelectAllTableCell, isSelectTableCell=DXGridCore.isSelectTableCell
	, orderedColumns=DXGridCore.orderedColumns, pageCount=DXGridCore.pageCount, paginatedRows=DXGridCore.paginatedRows
	, pushFilterExpression=DXGridCore.pushFilterExpression, pushSearchFilterExpression=DXGridCore.pushSearchFilterExpression, rowCount=DXGridCore.rowCount
	, rowIdGetter=DXGridCore.rowIdGetter, rowsWithAvailableToSelect=DXGridCore.rowsWithAvailableToSelect, rowsWithPageHeaders=DXGridCore.rowsWithPageHeaders
	, setCurrentPage=DXGridCore.setCurrentPage, setPageSize=DXGridCore.setPageSize, someSelected=DXGridCore.someSelected, sortedRows=DXGridCore.sortedRows
	, startEditRows=DXGridCore.startEditRows, stopEditRows=DXGridCore.stopEditRows, tableColumnsWithDataRows=DXGridCore.tableColumnsWithDataRows
	, tableColumnsWithDetail=DXGridCore.tableColumnsWithDetail, tableColumnsWithEditing=DXGridCore.tableColumnsWithEditing, tableColumnsWithGrouping=DXGridCore.tableColumnsWithGrouping
	, tableColumnsWithSelection=DXGridCore.tableColumnsWithSelection, tableColumnsWithWidths=DXGridCore.tableColumnsWithWidths, tableDataColumnsExist=DXGridCore.tableDataColumnsExist
	, tableHeaderRowsWithFilter=DXGridCore.tableHeaderRowsWithFilter, tableHeaderRowsWithReordering=DXGridCore.tableHeaderRowsWithReordering
	, tableRowsWithDataRows=DXGridCore.tableRowsWithDataRows, tableRowsWithEditing=DXGridCore.tableRowsWithEditing, tableRowsWithExpandedDetail=DXGridCore.tableRowsWithExpandedDetail
	, tableRowsWithGrouping=DXGridCore.tableRowsWithGrouping, tableRowsWithHeading=DXGridCore.tableRowsWithHeading, toggleColumn=DXGridCore.toggleColumn
	, toggleDetailRowExpanded=DXGridCore.toggleDetailRowExpanded, toggleExpandedGroups=DXGridCore.toggleExpandedGroups, toggleSelection=DXGridCore.toggleSelection
	, unwrapSelectedRows=DXGridCore.unwrapSelectedRows, visibleTableColumns =DXGridCore.visibleTableColumns;
//import { findDOMNode } from 'react-dom';

var asyncGenerator = function () {
  function AwaitValue(value) {
    this.value = value;
  }

  function AsyncGenerator(gen) {
    var front, back;

    function send(key, arg) {
      return new Promise(function (resolve, reject) {
        var request = {
          key: key,
          arg: arg,
          resolve: resolve,
          reject: reject,
          next: null
        };

        if (back) {
          back = back.next = request;
        } else {
          front = back = request;
          resume(key, arg);
        }
      });
    }

    function resume(key, arg) {
      try {
        var result = gen[key](arg);
        var value = result.value;

        if (value instanceof AwaitValue) {
          Promise.resolve(value.value).then(function (arg) {
            resume("next", arg);
          }, function (arg) {
            resume("throw", arg);
          });
        } else {
          settle(result.done ? "return" : "normal", result.value);
        }
      } catch (err) {
        settle("throw", err);
      }
    }

    function settle(type, value) {
      switch (type) {
        case "return":
          front.resolve({
            value: value,
            done: true
          });
          break;

        case "throw":
          front.reject(value);
          break;

        default:
          front.resolve({
            value: value,
            done: false
          });
          break;
      }

      front = front.next;

      if (front) {
        resume(front.key, front.arg);
      } else {
        back = null;
      }
    }

    this._invoke = send;

    if (typeof gen.return !== "function") {
      this.return = undefined;
    }
  }

  if (typeof Symbol === "function" && Symbol.asyncIterator) {
    AsyncGenerator.prototype[Symbol.asyncIterator] = function () {
      return this;
    };
  }

  AsyncGenerator.prototype.next = function (arg) {
    return this._invoke("next", arg);
  };

  AsyncGenerator.prototype.throw = function (arg) {
    return this._invoke("throw", arg);
  };

  AsyncGenerator.prototype.return = function (arg) {
    return this._invoke("return", arg);
  };

  return {
    wrap: function (fn) {
      return function () {
        return new AsyncGenerator(fn.apply(this, arguments));
      };
    },
    await: function (value) {
      return new AwaitValue(value);
    }
  };
}();





var classCallCheck = function (instance, Constructor) {
  if (!(instance instanceof Constructor)) {
    throw new TypeError("Cannot call a class as a function");
  }
};

var createClass = function () {
  function defineProperties(target, props) {
    for (var i = 0; i < props.length; i++) {
      var descriptor = props[i];
      descriptor.enumerable = descriptor.enumerable || false;
      descriptor.configurable = true;
      if ("value" in descriptor) descriptor.writable = true;
      Object.defineProperty(target, descriptor.key, descriptor);
    }
  }

  return function (Constructor, protoProps, staticProps) {
    if (protoProps) defineProperties(Constructor.prototype, protoProps);
    if (staticProps) defineProperties(Constructor, staticProps);
    return Constructor;
  };
}();





var defineProperty = function (obj, key, value) {
  if (key in obj) {
    Object.defineProperty(obj, key, {
      value: value,
      enumerable: true,
      configurable: true,
      writable: true
    });
  } else {
    obj[key] = value;
  }

  return obj;
};

var _extends = Object.assign || function (target) {
  for (var i = 1; i < arguments.length; i++) {
    var source = arguments[i];

    for (var key in source) {
      if (Object.prototype.hasOwnProperty.call(source, key)) {
        target[key] = source[key];
      }
    }
  }

  return target;
};



var inherits = function (subClass, superClass) {
  if (typeof superClass !== "function" && superClass !== null) {
    throw new TypeError("Super expression must either be null or a function, not " + typeof superClass);
  }

  subClass.prototype = Object.create(superClass && superClass.prototype, {
    constructor: {
      value: subClass,
      enumerable: false,
      writable: true,
      configurable: true
    }
  });
  if (superClass) Object.setPrototypeOf ? Object.setPrototypeOf(subClass, superClass) : subClass.__proto__ = superClass;
};









var objectWithoutProperties = function (obj, keys) {
  var target = {};

  for (var i in obj) {
    if (keys.indexOf(i) >= 0) continue;
    if (!Object.prototype.hasOwnProperty.call(obj, i)) continue;
    target[i] = obj[i];
  }

  return target;
};

var possibleConstructorReturn = function (self, call) {
  if (!self) {
    throw new ReferenceError("this hasn't been initialised - super() hasn't been called");
  }

  return call && (typeof call === "object" || typeof call === "function") ? call : self;
};





var slicedToArray = function () {
  function sliceIterator(arr, i) {
    var _arr = [];
    var _n = true;
    var _d = false;
    var _e = undefined;

    try {
      for (var _i = arr[Symbol.iterator](), _s; !(_n = (_s = _i.next()).done); _n = true) {
        _arr.push(_s.value);

        if (i && _arr.length === i) break;
      }
    } catch (err) {
      _d = true;
      _e = err;
    } finally {
      try {
        if (!_n && _i["return"]) _i["return"]();
      } finally {
        if (_d) throw _e;
      }
    }

    return _arr;
  }

  return function (arr, i) {
    if (Array.isArray(arr)) {
      return arr;
    } else if (Symbol.iterator in Object(arr)) {
      return sliceIterator(arr, i);
    } else {
      throw new TypeError("Invalid attempt to destructure non-iterable instance");
    }
  };
}();













var toConsumableArray = function (arr) {
  if (Array.isArray(arr)) {
    for (var i = 0, arr2 = Array(arr.length); i < arr.length; i++) arr2[i] = arr[i];

    return arr2;
  } else {
    return Array.from(arr);
  }
};

var GridCore = function (_React$PureComponent) {
  inherits(GridCore, _React$PureComponent);

  function GridCore() {
    classCallCheck(this, GridCore);
    return possibleConstructorReturn(this, (GridCore.__proto__ || Object.getPrototypeOf(GridCore)).apply(this, arguments));
  }

  createClass(GridCore, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          rows = _props.rows,
          columns = _props.columns,
          getRowId = _props.getRowId,
          getCellValue = _props.getCellValue,
          Root = _props.rootComponent;


      return _(
        Plugin,
        null,
        _(Getter, { name: 'rows', value: rows }),
        _(Getter, { name: 'getRowId', value: rowIdGetter(getRowId, rows) }),
        _(Getter, { name: 'columns', value: columns }),
        _(Getter, { name: 'getCellValue', value: cellValueGetter(getCellValue, columns) }),
        _(Template, { name: 'header' }),
        _(Template, { name: 'body' }),
        _(Template, { name: 'footer' }),
        _(
          Template,
          { name: 'root' },
          _(
            Root,
            null,
            _(TemplatePlaceholder, { name: 'header' }),
            _(TemplatePlaceholder, { name: 'body' }),
            _(TemplatePlaceholder, { name: 'footer' })
          )
        )
      );
    }
  }]);
  return GridCore;
}(React.PureComponent);

GridCore.propTypes = {
  rows: array.isRequired,
  getRowId: func,
  getCellValue: func,
  columns: array.isRequired,
  rootComponent: func.isRequired
};

GridCore.defaultProps = {
  getRowId: undefined,
  getCellValue: undefined
};

var Grid = function Grid(_ref) {
  var rows = _ref.rows,
      columns = _ref.columns,
      getRowId = _ref.getRowId,
      getCellValue = _ref.getCellValue,
      rootComponent = _ref.rootComponent,
      children = _ref.children;
  return _(
    PluginHost,
    null,
    _(GridCore, {
      rows: rows,
      columns: columns,
      getRowId: getRowId,
      getCellValue: getCellValue,
      rootComponent: rootComponent
    }),
    children
  );
};

Grid.propTypes = {
  rows: array.isRequired,
  getRowId: func,
  getCellValue: func,
  columns: array.isRequired,
  rootComponent: func.isRequired,
  children: node
};

Grid.defaultProps = {
  getRowId: undefined,
  getCellValue: undefined,
  children: undefined
};

var pluginDependencies = [{ name: 'TableColumnVisibility' }, { name: 'Toolbar' }];
var ColumnChooser = function (_React$PureComponent) {
  inherits(ColumnChooser, _React$PureComponent);

  function ColumnChooser(props) {
    classCallCheck(this, ColumnChooser);

    var _this = possibleConstructorReturn(this, (ColumnChooser.__proto__ || Object.getPrototypeOf(ColumnChooser)).call(this, props));

    _this.state = {
      visible: false
    };

    _this.handleToggle = _this.handleToggle.bind(_this);
    _this.handleHide = _this.handleHide.bind(_this);
    _this.buttonRef = _this.buttonRef.bind(_this);
    return _this;
  }

  createClass(ColumnChooser, [{
    key: 'buttonRef',
    value: function buttonRef(button) {
      this.button = button;
    }
  }, {
    key: 'handleToggle',
    value: function handleToggle() {
      this.setState({ visible: !this.state.visible });
    }
  }, {
    key: 'handleHide',
    value: function handleHide() {
      this.setState({ visible: false });
    }
  }, {
    key: 'render',
    value: function render() {
      var _this2 = this;

      var _props = this.props,
          Overlay = _props.overlayComponent,
          Container = _props.containerComponent,
          Item = _props.itemComponent,
          ToggleButton = _props.toggleButtonComponent,
          messages = _props.messages;

      var getMessage = getMessagesFormatter(messages);
      var visible = this.state.visible;


      return _(
        Plugin,
        {
          name: 'ColumnChooser',
          dependencies: pluginDependencies
        },
        _(
          Template,
          { name: 'toolbarContent' },
          _(TemplatePlaceholder, null),
          _(
            TemplateConnector,
            null,
            function (_ref, _ref2) {
              var columns = _ref.columns,
                  hiddenColumnNames = _ref.hiddenColumnNames,
                  isColumnTogglingEnabled = _ref.isColumnTogglingEnabled;
              var toggleColumnVisibility = _ref2.toggleColumnVisibility;
              return _(
                React.Fragment,
                null,
                _(ToggleButton, {
                  buttonRef: _this2.buttonRef,
                  onToggle: _this2.handleToggle,
                  getMessage: getMessage,
                  active: visible
                }),
                _(
                  Overlay,
                  {
                    visible: visible,
                    target: _this2.button,
                    onHide: _this2.handleHide,
                    toggle: _this2.handleToggle
                  },
                  _(
                    Container,
                    null,
                    columnChooserItems(columns, hiddenColumnNames).map(function (item) {
                      var columnName = item.column.name;

                      var togglingEnabled = isColumnTogglingEnabled(columnName);
                      return _(Item, {
                        key: columnName,
                        item: item,
                        disabled: !togglingEnabled,
                        onToggle: function onToggle() {
                          return toggleColumnVisibility(columnName);
                        }
                      });
                    })
                  )
                )
              );
            }
          )
        )
      );
    }
  }]);
  return ColumnChooser;
}(React.PureComponent);

ColumnChooser.propTypes = {
  overlayComponent: func.isRequired,
  containerComponent: func.isRequired,
  itemComponent: func.isRequired,
  toggleButtonComponent: func.isRequired,
  messages: object
};

ColumnChooser.defaultProps = {
  messages: {}
};

var STATE_CONSISTENCY_TIMEOUT = 1000 / 60;

var createStateHelper = function createStateHelper(component) {
  var controlledStateProperties = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : {};

  var stateConsistencyTimers = {};
  var checkStateConsistency = function checkStateConsistency(propertyName) {
    if (component.props[propertyName] !== undefined && component.props[propertyName] !== component.state[propertyName]) {
      component.setState(defineProperty({}, propertyName, component.props[propertyName]));
    }
  };
  var notifyStateChange = function notifyStateChange(nextState, state) {
    Object.keys(controlledStateProperties).forEach(function (propertyName) {
      var changeEvent = controlledStateProperties[propertyName]();
      if (changeEvent && nextState[propertyName] !== state[propertyName]) {
        changeEvent(nextState[propertyName]);
        clearTimeout(stateConsistencyTimers[propertyName]);
        stateConsistencyTimers[propertyName] = setTimeout(checkStateConsistency.bind(null, propertyName), STATE_CONSISTENCY_TIMEOUT);
      }
    });
  };

  var lastStateUpdater = null;
  var initialState = null;
  var applyReducer = function applyReducer(reduce, payload, callback) {
    var stateUpdater = function stateUpdater(prevState) {
      if (initialState === null) {
        initialState = prevState;
      }
      var stateChange = reduce(_extends({}, prevState), payload);
      var state = _extends({}, prevState, stateChange);

      if (typeof callback === 'function') {
        callback(state, prevState);
      }
      if (stateUpdater === lastStateUpdater) {
        notifyStateChange(state, initialState);
        initialState = null;
      }

      return stateChange;
    };
    lastStateUpdater = stateUpdater;
    component.setState(stateUpdater);
  };
  var applyFieldReducer = function applyFieldReducer(field, reduce, payload) {
    applyReducer(function (state) {
      return defineProperty({}, field, reduce(state[field], payload));
    });
  };

  return {
    applyReducer: applyReducer,
    applyFieldReducer: applyFieldReducer
  };
};

var columnExtensionValueGetter = function columnExtensionValueGetter(columnExtensions, defaultValue) {
  return getColumnExtensionValueGetter(columnExtensions, 'filteringEnabled', defaultValue);
};

var FilteringState = function (_React$PureComponent) {
  inherits(FilteringState, _React$PureComponent);

  function FilteringState(props) {
    classCallCheck(this, FilteringState);

    var _this = possibleConstructorReturn(this, (FilteringState.__proto__ || Object.getPrototypeOf(FilteringState)).call(this, props));

    _this.state = {
      filters: props.filters || props.defaultFilters
    };
    var stateHelper = createStateHelper(_this, {
      filters: function filters() {
        return _this.props.onFiltersChange;
      }
    });

    _this.changeColumnFilter = stateHelper.applyFieldReducer.bind(stateHelper, 'filters', changeColumnFilter);
    return _this;
  }

  createClass(FilteringState, [{
    key: 'componentWillReceiveProps',
    value: function componentWillReceiveProps(nextProps) {
      var filters = nextProps.filters;

      this.setState(_extends({}, filters !== undefined ? { filters: filters } : null));
    }
  }, {
    key: 'render',
    value: function render() {
      var filters = this.state.filters;
      var _props = this.props,
          columnExtensions = _props.columnExtensions,
          columnFilteringEnabled = _props.columnFilteringEnabled;


      return _(
        Plugin,
        {
          name: 'FilteringState'
        },
        _(Getter, { name: 'filters', value: filters }),
        _(Getter, { name: 'filterExpression', computed: pushFilterExpression(filters) }),
        _(Getter, {
          name: 'isColumnFilteringEnabled',
          value: columnExtensionValueGetter(columnExtensions, columnFilteringEnabled)
        }),
        _(Action, { name: 'changeColumnFilter', action: this.changeColumnFilter })
      );
    }
  }]);
  return FilteringState;
}(React.PureComponent);

FilteringState.propTypes = {
  filters: array,
  defaultFilters: array,
  onFiltersChange: func,
  columnExtensions: array,
  columnFilteringEnabled: bool
};

FilteringState.defaultProps = {
  filters: undefined,
  defaultFilters: [],
  onFiltersChange: undefined,
  columnExtensions: undefined,
  columnFilteringEnabled: true
};

var pluginDependencies$1 = [{ name: 'FilteringState', optional: true }, { name: 'SearchState', optional: true }];

var IntegratedFiltering = function (_React$PureComponent) {
  inherits(IntegratedFiltering, _React$PureComponent);

  function IntegratedFiltering() {
    classCallCheck(this, IntegratedFiltering);
    return possibleConstructorReturn(this, (IntegratedFiltering.__proto__ || Object.getPrototypeOf(IntegratedFiltering)).apply(this, arguments));
  }

  createClass(IntegratedFiltering, [{
    key: 'render',
    value: function render() {
      var columnExtensions = this.props.columnExtensions;

      var getColumnPredicate = function getColumnPredicate(columnName) {
        return getColumnExtension(columnExtensions, columnName).predicate;
      };

      var rowsComputed = function rowsComputed(_ref) {
        var rows = _ref.rows,
            filterExpression = _ref.filterExpression,
            getCellValue = _ref.getCellValue,
            isGroupRow = _ref.isGroupRow,
            getRowLevelKey = _ref.getRowLevelKey;
        return filteredRows(rows, filterExpression, getCellValue, getColumnPredicate, isGroupRow, getRowLevelKey);
      };

      return _(
        Plugin,
        {
          name: 'IntegratedFiltering',
          dependencies: pluginDependencies$1
        },
        _(Getter, { name: 'rows', computed: rowsComputed })
      );
    }
  }]);
  return IntegratedFiltering;
}(React.PureComponent);

IntegratedFiltering.propTypes = {
  columnExtensions: array
};

IntegratedFiltering.defaultProps = {
  columnExtensions: undefined
};

var columnExtensionValueGetter$1 = function columnExtensionValueGetter(columnExtensions, defaultValue) {
  return getColumnExtensionValueGetter(columnExtensions, 'editingEnabled', defaultValue);
};

var EditingState = function (_React$PureComponent) {
  inherits(EditingState, _React$PureComponent);

  function EditingState(props) {
    classCallCheck(this, EditingState);

    var _this = possibleConstructorReturn(this, (EditingState.__proto__ || Object.getPrototypeOf(EditingState)).call(this, props));

    _this.state = {
      editingRowIds: props.editingRowIds || props.defaultEditingRowIds,
      addedRows: props.addedRows || props.defaultAddedRows,
      rowChanges: props.rowChanges || props.defaultRowChanges,
      deletedRowIds: props.deletedRowIds || props.defaultDeletedRowIds
    };

    var stateHelper = createStateHelper(_this, {
      editingRowIds: function editingRowIds() {
        return _this.props.onEditingRowIdsChange;
      },
      addedRows: function addedRows() {
        return _this.props.onAddedRowsChange;
      },
      rowChanges: function rowChanges() {
        return _this.props.onRowChangesChange;
      },
      deletedRowIds: function deletedRowIds() {
        return _this.props.onDeletedRowIdsChange;
      }
    });

    _this.startEditRows = stateHelper.applyFieldReducer.bind(stateHelper, 'editingRowIds', startEditRows);
    _this.stopEditRows = stateHelper.applyFieldReducer.bind(stateHelper, 'editingRowIds', stopEditRows);

    _this.changeRow = stateHelper.applyFieldReducer.bind(stateHelper, 'rowChanges', changeRow);
    _this.cancelChangedRows = stateHelper.applyFieldReducer.bind(stateHelper, 'rowChanges', cancelChanges);
    _this.commitChangedRows = function (_ref) {
      var rowIds = _ref.rowIds;

      _this.props.onCommitChanges({
        changed: changedRowsByIds(_this.state.rowChanges, rowIds)
      });
      _this.cancelChangedRows({ rowIds: rowIds });
    };

    _this.addRow = stateHelper.applyFieldReducer.bind(stateHelper, 'addedRows', addRow);
    _this.changeAddedRow = stateHelper.applyFieldReducer.bind(stateHelper, 'addedRows', changeAddedRow);
    _this.cancelAddedRows = stateHelper.applyFieldReducer.bind(stateHelper, 'addedRows', cancelAddedRows);
    _this.commitAddedRows = function (_ref2) {
      var rowIds = _ref2.rowIds;

      _this.props.onCommitChanges({
        added: addedRowsByIds(_this.state.addedRows, rowIds)
      });
      _this.cancelAddedRows({ rowIds: rowIds });
    };

    _this.deleteRows = stateHelper.applyFieldReducer.bind(stateHelper, 'deletedRowIds', deleteRows);
    _this.cancelDeletedRows = stateHelper.applyFieldReducer.bind(stateHelper, 'deletedRowIds', cancelDeletedRows);
    _this.commitDeletedRows = function (_ref3) {
      var rowIds = _ref3.rowIds;

      _this.props.onCommitChanges({ deleted: rowIds });
      _this.cancelDeletedRows({ rowIds: rowIds });
    };
    return _this;
  }

  createClass(EditingState, [{
    key: 'componentWillReceiveProps',
    value: function componentWillReceiveProps(nextProps) {
      var editingRowIds = nextProps.editingRowIds,
          rowChanges = nextProps.rowChanges,
          addedRows = nextProps.addedRows,
          deletedRowIds = nextProps.deletedRowIds;

      this.setState(_extends({}, editingRowIds !== undefined ? { editingRowIds: editingRowIds } : null, rowChanges !== undefined ? { rowChanges: rowChanges } : null, addedRows !== undefined ? { addedRows: addedRows } : null, deletedRowIds !== undefined ? { deletedRowIds: deletedRowIds } : null));
    }
  }, {
    key: 'render',
    value: function render() {
      var _props = this.props,
          createRowChange = _props.createRowChange,
          columnExtensions = _props.columnExtensions,
          columnEditingEnabled = _props.columnEditingEnabled;
      var _state = this.state,
          editingRowIds = _state.editingRowIds,
          rowChanges = _state.rowChanges,
          addedRows = _state.addedRows,
          deletedRowIds = _state.deletedRowIds;


      return _(
        Plugin,
        {
          name: 'EditingState'
        },
        _(Getter, {
          name: 'createRowChange',
          value: createRowChangeGetter(createRowChange, columnExtensions)
        }),
        _(Getter, { name: 'editingRowIds', value: editingRowIds }),
        _(Action, { name: 'startEditRows', action: this.startEditRows }),
        _(Action, { name: 'stopEditRows', action: this.stopEditRows }),
        _(Getter, { name: 'rowChanges', value: rowChanges }),
        _(Action, { name: 'changeRow', action: this.changeRow }),
        _(Action, { name: 'cancelChangedRows', action: this.cancelChangedRows }),
        _(Action, { name: 'commitChangedRows', action: this.commitChangedRows }),
        _(Getter, { name: 'addedRows', value: addedRows }),
        _(Action, { name: 'addRow', action: this.addRow }),
        _(Action, { name: 'changeAddedRow', action: this.changeAddedRow }),
        _(Action, { name: 'cancelAddedRows', action: this.cancelAddedRows }),
        _(Action, { name: 'commitAddedRows', action: this.commitAddedRows }),
        _(Getter, { name: 'deletedRowIds', value: deletedRowIds }),
        _(Action, { name: 'deleteRows', action: this.deleteRows }),
        _(Action, { name: 'cancelDeletedRows', action: this.cancelDeletedRows }),
        _(Action, { name: 'commitDeletedRows', action: this.commitDeletedRows }),
        _(Getter, {
          name: 'isColumnEditingEnabled',
          value: columnExtensionValueGetter$1(columnExtensions, columnEditingEnabled)
        })
      );
    }
  }]);
  return EditingState;
}(React.PureComponent);

EditingState.propTypes = {
  createRowChange: func,
  columnEditingEnabled: bool,
  columnExtensions: array,

  editingRowIds: array,
  defaultEditingRowIds: array,
  onEditingRowIdsChange: func,

  addedRows: array,
  defaultAddedRows: array,
  onAddedRowsChange: func,

  rowChanges: object,
  defaultRowChanges: object,
  onRowChangesChange: func,

  deletedRowIds: array,
  defaultDeletedRowIds: array,
  onDeletedRowIdsChange: func,

  onCommitChanges: func.isRequired
};

EditingState.defaultProps = {
  createRowChange: undefined,
  columnEditingEnabled: true,
  columnExtensions: undefined,

  editingRowIds: undefined,
  defaultEditingRowIds: [],
  onEditingRowIdsChange: undefined,

  rowChanges: undefined,
  defaultRowChanges: {},
  onRowChangesChange: undefined,

  addedRows: undefined,
  defaultAddedRows: [],
  onAddedRowsChange: undefined,

  deletedRowIds: undefined,
  defaultDeletedRowIds: [],
  onDeletedRowIdsChange: undefined
};

var PagingState = function (_React$PureComponent) {
  inherits(PagingState, _React$PureComponent);

  function PagingState(props) {
    classCallCheck(this, PagingState);

    var _this = possibleConstructorReturn(this, (PagingState.__proto__ || Object.getPrototypeOf(PagingState)).call(this, props));

    _this.state = {
      currentPage: props.currentPage || props.defaultCurrentPage,
      pageSize: props.pageSize !== undefined ? props.pageSize : props.defaultPageSize
    };

    var stateHelper = createStateHelper(_this, {
      currentPage: function currentPage() {
        return _this.props.onCurrentPageChange;
      },
      pageSize: function pageSize() {
        return _this.props.onPageSizeChange;
      }
    });

    _this.setCurrentPage = stateHelper.applyFieldReducer.bind(stateHelper, 'currentPage', setCurrentPage);
    _this.setPageSize = stateHelper.applyFieldReducer.bind(stateHelper, 'pageSize', setPageSize);
    return _this;
  }

  createClass(PagingState, [{
    key: 'componentWillReceiveProps',
    value: function componentWillReceiveProps(nextProps) {
      var currentPage = nextProps.currentPage,
          pageSize = nextProps.pageSize;

      this.setState(_extends({}, currentPage !== undefined ? { currentPage: currentPage } : null, pageSize !== undefined ? { pageSize: pageSize } : null));
    }
  }, {
    key: 'render',
    value: function render() {
      var _state = this.state,
          pageSize = _state.pageSize,
          currentPage = _state.currentPage;


      return _(
        Plugin,
        {
          name: 'PagingState'
        },
        _(Getter, { name: 'currentPage', value: currentPage }),
        _(Getter, { name: 'pageSize', value: pageSize }),
        _(Action, { name: 'setCurrentPage', action: this.setCurrentPage }),
        _(Action, { name: 'setPageSize', action: this.setPageSize })
      );
    }
  }]);
  return PagingState;
}(React.PureComponent);

PagingState.propTypes = {
  pageSize: number,
  defaultPageSize: number,
  onPageSizeChange: func,
  currentPage: number,
  defaultCurrentPage: number,
  onCurrentPageChange: func
};

PagingState.defaultProps = {
  pageSize: undefined,
  defaultPageSize: 10,
  onPageSizeChange: undefined,
  currentPage: undefined,
  defaultCurrentPage: 0,
  onCurrentPageChange: undefined
};

var pluginDependencies$2 = [{ name: 'PagingState' }];

var clamp = function clamp(value, max) {
  return Math.max(Math.min(value, max), 0);
};

var rowsWithHeadersComputed = function rowsWithHeadersComputed(_ref) {
  var rows = _ref.rows,
      pageSize = _ref.pageSize,
      getRowLevelKey = _ref.getRowLevelKey;
  return rowsWithPageHeaders(rows, pageSize, getRowLevelKey);
};
var totalCountComputed = function totalCountComputed(_ref2) {
  var rows = _ref2.rows;
  return rowCount(rows);
};
var paginatedRowsComputed = function paginatedRowsComputed(_ref3) {
  var rows = _ref3.rows,
      pageSize = _ref3.pageSize,
      currentPage = _ref3.currentPage;
  return paginatedRows(rows, pageSize, currentPage);
};
var currentPageComputed = function currentPageComputed(_ref4, _ref5) {
  var currentPage = _ref4.currentPage,
      totalCount = _ref4.totalCount,
      pageSize = _ref4.pageSize;
  var setCurrentPage$$1 = _ref5.setCurrentPage;

  var totalPages = pageCount(totalCount, pageSize);
  var adjustedCurrentPage = clamp(currentPage, totalPages - 1);
  if (currentPage !== adjustedCurrentPage) {
    setTimeout(function () {
      return setCurrentPage$$1(adjustedCurrentPage);
    });
  }
  return adjustedCurrentPage;
};

// eslint-disable-next-line react/prefer-stateless-function
var IntegratedPaging = function (_React$PureComponent) {
  inherits(IntegratedPaging, _React$PureComponent);

  function IntegratedPaging() {
    classCallCheck(this, IntegratedPaging);
    return possibleConstructorReturn(this, (IntegratedPaging.__proto__ || Object.getPrototypeOf(IntegratedPaging)).apply(this, arguments));
  }

  createClass(IntegratedPaging, [{
    key: 'render',
    value: function render() {
      return _(
        Plugin,
        {
          name: 'IntegratedPaging',
          dependencies: pluginDependencies$2
        },
        _(Getter, { name: 'rows', computed: rowsWithHeadersComputed }),
        _(Getter, { name: 'totalCount', computed: totalCountComputed }),
        _(Getter, { name: 'currentPage', computed: currentPageComputed }),
        _(Getter, { name: 'rows', computed: paginatedRowsComputed })
      );
    }
  }]);
  return IntegratedPaging;
}(React.PureComponent);

var pluginDependencies$3 = [{ name: 'PagingState' }];

var CustomPaging = function (_React$PureComponent) {
  inherits(CustomPaging, _React$PureComponent);

  function CustomPaging() {
    classCallCheck(this, CustomPaging);
    return possibleConstructorReturn(this, (CustomPaging.__proto__ || Object.getPrototypeOf(CustomPaging)).apply(this, arguments));
  }

  createClass(CustomPaging, [{
    key: 'render',
    value: function render() {
      var totalCount = this.props.totalCount;


      return _(
        Plugin,
        {
          name: 'CustomPaging',
          dependencies: pluginDependencies$3
        },
        _(Getter, { name: 'totalCount', value: totalCount })
      );
    }
  }]);
  return CustomPaging;
}(React.PureComponent);

CustomPaging.propTypes = {
  totalCount: number
};

CustomPaging.defaultProps = {
  totalCount: 0
};

var dependencies = [{ name: 'SortingState', optional: true }];

var adjustSortIndex = function adjustSortIndex(sortIndex, grouping, sorting) {
  return Math.max(grouping.slice(0, sortIndex).reduce(function (acc, columnGrouping) {
    var columnSortingIndex = sorting.findIndex(function (columnSorting) {
      return columnSorting.columnName === columnGrouping.columnName;
    });
    return columnSortingIndex === -1 ? acc - 1 : acc;
  }, sortIndex), 0);
};

var columnExtensionValueGetter$2 = function columnExtensionValueGetter(columnExtensions, defaultValue) {
  return getColumnExtensionValueGetter(columnExtensions, 'groupingEnabled', defaultValue);
};

var GroupingState = function (_React$PureComponent) {
  inherits(GroupingState, _React$PureComponent);

  function GroupingState(props) {
    classCallCheck(this, GroupingState);

    var _this = possibleConstructorReturn(this, (GroupingState.__proto__ || Object.getPrototypeOf(GroupingState)).call(this, props));

    _this.state = {
      grouping: props.grouping || props.defaultGrouping,
      draftGrouping: null,
      expandedGroups: props.expandedGroups || props.defaultExpandedGroups
    };

    _this.stateHelper = createStateHelper(_this, {
      grouping: function grouping() {
        return _this.props.onGroupingChange;
      },
      expandedGroups: function expandedGroups() {
        return _this.props.onExpandedGroupsChange;
      }
    });

    _this.changeColumnGrouping = _this.changeColumnGrouping.bind(_this);
    _this.toggleGroupExpanded = _this.stateHelper.applyReducer.bind(_this.stateHelper, toggleExpandedGroups);
    _this.draftColumnGrouping = _this.stateHelper.applyReducer.bind(_this.stateHelper, draftColumnGrouping);
    _this.cancelColumnGroupingDraft = _this.stateHelper.applyReducer.bind(_this.stateHelper, cancelColumnGroupingDraft);
    _this.changeColumnSorting = _this.changeColumnSorting.bind(_this);
    return _this;
  }

  createClass(GroupingState, [{
    key: 'componentWillReceiveProps',
    value: function componentWillReceiveProps(nextProps) {
      var grouping = nextProps.grouping,
          expandedGroups = nextProps.expandedGroups;

      this.setState(_extends({}, grouping !== undefined ? { grouping: grouping } : null, expandedGroups !== undefined ? { expandedGroups: expandedGroups } : null));
    }
  }, {
    key: 'changeColumnSorting',
    value: function changeColumnSorting$$1(_ref, _ref2, _ref3) {
      var sorting = _ref2.sorting;
      var _changeColumnSorting = _ref3.changeColumnSorting;
      var columnName = _ref.columnName,
          keepOther = _ref.keepOther,
          restParams = objectWithoutProperties(_ref, ['columnName', 'keepOther']);
      var grouping = this.state.grouping;

      var groupingIndex = grouping.findIndex(function (columnGrouping) {
        return columnGrouping.columnName === columnName;
      });
      if (groupingIndex === -1) {
        _changeColumnSorting(_extends({
          columnName: columnName,
          keepOther: keepOther || grouping.map(function (columnGrouping) {
            return columnGrouping.columnName;
          })
        }, restParams));
        return false;
      }

      var sortIndex = adjustSortIndex(groupingIndex, grouping, sorting);
      _changeColumnSorting(_extends({
        columnName: columnName,
        keepOther: true,
        sortIndex: sortIndex
      }, restParams));
      return false;
    }
  }, {
    key: 'changeColumnGrouping',
    value: function changeColumnGrouping$$1(_ref4, getters, actions) {
      var columnName = _ref4.columnName,
          groupIndex = _ref4.groupIndex;

      this.stateHelper.applyReducer(changeColumnGrouping, { columnName: columnName, groupIndex: groupIndex }, function (nextState, state) {
        var grouping = nextState.grouping;
        var prevGrouping = state.grouping;
        var sorting = getters.sorting;
        var changeColumnSorting$$1 = actions.changeColumnSorting;


        if (!sorting) return;

        var columnSortingIndex = sorting.findIndex(function (columnSorting) {
          return columnSorting.columnName === columnName;
        });
        var prevGroupingIndex = prevGrouping.findIndex(function (columnGrouping) {
          return columnGrouping.columnName === columnName;
        });
        var groupingIndex = grouping.findIndex(function (columnGrouping) {
          return columnGrouping.columnName === columnName;
        });

        if (columnSortingIndex === -1 || prevGroupingIndex === prevGrouping.length - 1 && groupingIndex === -1) return;

        var sortIndex = adjustSortIndex(groupingIndex === -1 ? grouping.length : groupingIndex, grouping, sorting);

        if (columnSortingIndex === sortIndex) return;

        changeColumnSorting$$1(_extends({
          keepOther: true,
          sortIndex: sortIndex
        }, sorting[columnSortingIndex]));
      });
    }
  }, {
    key: 'render',
    value: function render() {
      var _state = this.state,
          grouping = _state.grouping,
          draftGrouping = _state.draftGrouping,
          expandedGroups = _state.expandedGroups;
      var _props = this.props,
          columnExtensions = _props.columnExtensions,
          columnGroupingEnabled = _props.columnGroupingEnabled;


      return _(
        Plugin,
        {
          name: 'GroupingState',
          dependencies: dependencies
        },
        _(Getter, { name: 'grouping', value: grouping }),
        _(Getter, { name: 'draftGrouping', value: draftGrouping || grouping }),
        _(Getter, {
          name: 'isColumnGroupingEnabled',
          value: columnExtensionValueGetter$2(columnExtensions, columnGroupingEnabled)
        }),
        _(Action, { name: 'changeColumnGrouping', action: this.changeColumnGrouping }),
        _(Action, { name: 'draftColumnGrouping', action: this.draftColumnGrouping }),
        _(Action, { name: 'cancelColumnGroupingDraft', action: this.cancelColumnGroupingDraft }),
        _(Getter, { name: 'expandedGroups', value: expandedGroups }),
        _(Action, { name: 'toggleGroupExpanded', action: this.toggleGroupExpanded }),
        _(Action, { name: 'changeColumnSorting', action: this.changeColumnSorting })
      );
    }
  }]);
  return GroupingState;
}(React.PureComponent);

GroupingState.propTypes = {
  grouping: array,
  defaultGrouping: array,
  onGroupingChange: func,
  expandedGroups: array,
  defaultExpandedGroups: array,
  onExpandedGroupsChange: func,
  columnExtensions: array,
  columnGroupingEnabled: bool
};

GroupingState.defaultProps = {
  grouping: undefined,
  defaultGrouping: [],
  onGroupingChange: undefined,
  expandedGroups: undefined,
  defaultExpandedGroups: [],
  onExpandedGroupsChange: undefined,
  columnExtensions: undefined,
  columnGroupingEnabled: true
};

var pluginDependencies$4 = [{ name: 'GroupingState' }];

var expandedGroupedRowsComputed = function expandedGroupedRowsComputed(_ref) {
  var rows = _ref.rows,
      grouping = _ref.grouping,
      expandedGroups = _ref.expandedGroups;
  return expandedGroupRows(rows, grouping, expandedGroups);
};

var IntegratedGrouping = function (_React$PureComponent) {
  inherits(IntegratedGrouping, _React$PureComponent);

  function IntegratedGrouping() {
    classCallCheck(this, IntegratedGrouping);
    return possibleConstructorReturn(this, (IntegratedGrouping.__proto__ || Object.getPrototypeOf(IntegratedGrouping)).apply(this, arguments));
  }

  createClass(IntegratedGrouping, [{
    key: 'render',
    value: function render() {
      var columnExtensions = this.props.columnExtensions;

      var getColumnCriteria = function getColumnCriteria(columnName) {
        return getColumnExtension(columnExtensions, columnName).criteria;
      };

      var groupedRowsComputed = function groupedRowsComputed(_ref2) {
        var rows = _ref2.rows,
            grouping = _ref2.grouping,
            getCellValue = _ref2.getCellValue;
        return groupedRows(rows, grouping, getCellValue, getColumnCriteria);
      };

      return _(
        Plugin,
        {
          name: 'IntegratedGrouping',
          dependencies: pluginDependencies$4
        },
        _(Getter, { name: 'isGroupRow', value: groupRowChecker }),
        _(Getter, { name: 'getRowLevelKey', value: groupRowLevelKeyGetter }),
        _(Getter, { name: 'rows', computed: groupedRowsComputed }),
        _(Getter, { name: 'rows', computed: expandedGroupedRowsComputed })
      );
    }
  }]);
  return IntegratedGrouping;
}(React.PureComponent);

IntegratedGrouping.propTypes = {
  columnExtensions: array
};

IntegratedGrouping.defaultProps = {
  columnExtensions: undefined
};

var pluginDependencies$5 = [{ name: 'GroupingState' }];

var expandedGroupedRowsComputed$1 = function expandedGroupedRowsComputed(_ref) {
  var rows = _ref.rows,
      grouping = _ref.grouping,
      expandedGroups = _ref.expandedGroups;
  return expandedGroupRows(rows, grouping, expandedGroups);
};
var getRowIdComputed = function getRowIdComputed(_ref2) {
  var getRowId = _ref2.getRowId,
      rows = _ref2.rows;
  return customGroupingRowIdGetter(getRowId, rows);
};

var CustomGrouping = function (_React$PureComponent) {
  inherits(CustomGrouping, _React$PureComponent);

  function CustomGrouping() {
    classCallCheck(this, CustomGrouping);
    return possibleConstructorReturn(this, (CustomGrouping.__proto__ || Object.getPrototypeOf(CustomGrouping)).apply(this, arguments));
  }

  createClass(CustomGrouping, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          getChildGroups = _props.getChildGroups,
          appliedGrouping = _props.grouping,
          appliedExpandedGroups = _props.expandedGroups;

      var groupedRowsComputed = function groupedRowsComputed(_ref3) {
        var rows = _ref3.rows,
            grouping = _ref3.grouping;
        return customGroupedRows(rows, grouping, getChildGroups);
      };

      return _(
        Plugin,
        {
          name: 'CustomGrouping',
          dependencies: pluginDependencies$5
        },
        appliedGrouping && _(Getter, { name: 'grouping', value: appliedGrouping }),
        appliedExpandedGroups && _(Getter, { name: 'expandedGroups', value: appliedExpandedGroups }),
        _(Getter, { name: 'isGroupRow', value: groupRowChecker }),
        _(Getter, { name: 'getRowLevelKey', value: groupRowLevelKeyGetter }),
        _(Getter, { name: 'rows', computed: groupedRowsComputed }),
        _(Getter, { name: 'getRowId', computed: getRowIdComputed }),
        _(Getter, { name: 'rows', computed: expandedGroupedRowsComputed$1 })
      );
    }
  }]);
  return CustomGrouping;
}(React.PureComponent);

CustomGrouping.propTypes = {
  getChildGroups: func.isRequired,
  grouping: array,
  expandedGroups: array
};

CustomGrouping.defaultProps = {
  grouping: undefined,
  expandedGroups: undefined
};

var SelectionState = function (_React$PureComponent) {
  inherits(SelectionState, _React$PureComponent);

  function SelectionState(props) {
    classCallCheck(this, SelectionState);

    var _this = possibleConstructorReturn(this, (SelectionState.__proto__ || Object.getPrototypeOf(SelectionState)).call(this, props));

    _this.state = {
      selection: props.selection || props.defaultSelection
    };

    var stateHelper = createStateHelper(_this, {
      selection: function selection() {
        return _this.props.onSelectionChange;
      }
    });

    _this.toggleSelection = stateHelper.applyFieldReducer.bind(stateHelper, 'selection', toggleSelection);
    return _this;
  }

  createClass(SelectionState, [{
    key: 'componentWillReceiveProps',
    value: function componentWillReceiveProps(nextProps) {
      var selection = nextProps.selection;

      this.setState(_extends({}, selection !== undefined ? { selection: selection } : null));
    }
  }, {
    key: 'render',
    value: function render() {
      var selection = this.state.selection;


      return _(
        Plugin,
        {
          name: 'SelectionState'
        },
        _(Getter, { name: 'selection', value: selection }),
        _(Action, { name: 'toggleSelection', action: this.toggleSelection })
      );
    }
  }]);
  return SelectionState;
}(React.PureComponent);

SelectionState.propTypes = {
  selection: array,
  defaultSelection: array,
  onSelectionChange: func
};

SelectionState.defaultProps = {
  selection: undefined,
  defaultSelection: [],
  onSelectionChange: undefined
};

var rowsWithAvailableToSelectComputed = function rowsWithAvailableToSelectComputed(_ref) {
  var rows = _ref.rows,
      getRowId = _ref.getRowId,
      isGroupRow = _ref.isGroupRow;
  return rowsWithAvailableToSelect(rows, getRowId, isGroupRow);
};
var allSelectedComputed = function allSelectedComputed(_ref2) {
  var rows = _ref2.rows,
      selection = _ref2.selection;
  return allSelected(rows, selection);
};
var someSelectedComputed = function someSelectedComputed(_ref3) {
  var rows = _ref3.rows,
      selection = _ref3.selection;
  return someSelected(rows, selection);
};
var selectAllAvailableComputed = function selectAllAvailableComputed(_ref4) {
  var availableToSelect = _ref4.rows.availableToSelect;
  return !!availableToSelect.length;
};
var toggleSelectAll = function toggleSelectAll(state, _ref5, _ref6) {
  var availableToSelect = _ref5.rows.availableToSelect;
  var toggleSelection$$1 = _ref6.toggleSelection;

  toggleSelection$$1({ rowIds: availableToSelect, state: state });
};
var unwrapRowsComputed = function unwrapRowsComputed(_ref7) {
  var rows = _ref7.rows;
  return unwrapSelectedRows(rows);
};

var pluginDependencies$6 = [{ name: 'SelectionState' }];

// eslint-disable-next-line react/prefer-stateless-function
var IntegratedSelection = function (_React$PureComponent) {
  inherits(IntegratedSelection, _React$PureComponent);

  function IntegratedSelection() {
    classCallCheck(this, IntegratedSelection);
    return possibleConstructorReturn(this, (IntegratedSelection.__proto__ || Object.getPrototypeOf(IntegratedSelection)).apply(this, arguments));
  }

  createClass(IntegratedSelection, [{
    key: 'render',
    value: function render() {
      return _(
        Plugin,
        {
          name: 'IntegratedSelection',
          dependencies: pluginDependencies$6
        },
        _(Getter, { name: 'rows', computed: rowsWithAvailableToSelectComputed }),
        _(Getter, { name: 'allSelected', computed: allSelectedComputed }),
        _(Getter, { name: 'someSelected', computed: someSelectedComputed }),
        _(Getter, { name: 'selectAllAvailable', computed: selectAllAvailableComputed }),
        _(Action, { name: 'toggleSelectAll', action: toggleSelectAll }),
        _(Getter, { name: 'rows', computed: unwrapRowsComputed })
      );
    }
  }]);
  return IntegratedSelection;
}(React.PureComponent);

var columnExtensionValueGetter$3 = function columnExtensionValueGetter(columnExtensions, defaultValue) {
  return getColumnExtensionValueGetter(columnExtensions, 'sortingEnabled', defaultValue);
};

var SortingState = function (_React$PureComponent) {
  inherits(SortingState, _React$PureComponent);

  function SortingState(props) {
    classCallCheck(this, SortingState);

    var _this = possibleConstructorReturn(this, (SortingState.__proto__ || Object.getPrototypeOf(SortingState)).call(this, props));

    _this.state = {
      sorting: props.sorting || props.defaultSorting
    };

    var persistentSortedColumns = getPersistentSortedColumns(_this.state.sorting, props.columnExtensions);

    var stateHelper = createStateHelper(_this, {
      sorting: function sorting() {
        return _this.props.onSortingChange;
      }
    });

    _this.changeColumnSorting = stateHelper.applyReducer.bind(stateHelper, function (prevState, payload) {
      var keepOther = calculateKeepOther(prevState.sorting, payload.keepOther, persistentSortedColumns);
      return changeColumnSorting(prevState, _extends({}, payload, { keepOther: keepOther }));
    });
    return _this;
  }

  createClass(SortingState, [{
    key: 'componentWillReceiveProps',
    value: function componentWillReceiveProps(nextProps) {
      var sorting = nextProps.sorting;

      this.setState(_extends({}, sorting !== undefined ? { sorting: sorting } : null));
    }
  }, {
    key: 'render',
    value: function render() {
      var sorting = this.state.sorting;
      var _props = this.props,
          columnExtensions = _props.columnExtensions,
          columnSortingEnabled = _props.columnSortingEnabled;


      return _(
        Plugin,
        {
          name: 'SortingState'
        },
        _(Getter, { name: 'sorting', value: sorting }),
        _(Getter, {
          name: 'isColumnSortingEnabled',
          value: columnExtensionValueGetter$3(columnExtensions, columnSortingEnabled)
        }),
        _(Action, { name: 'changeColumnSorting', action: this.changeColumnSorting })
      );
    }
  }]);
  return SortingState;
}(React.PureComponent);

SortingState.propTypes = {
  sorting: array,
  defaultSorting: array,
  onSortingChange: func,
  columnExtensions: array,
  columnSortingEnabled: bool
};

SortingState.defaultProps = {
  sorting: undefined,
  defaultSorting: [],
  onSortingChange: undefined,
  columnExtensions: undefined,
  columnSortingEnabled: true
};

var pluginDependencies$7 = [{ name: 'SortingState' }];

var IntegratedSorting = function (_React$PureComponent) {
  inherits(IntegratedSorting, _React$PureComponent);

  function IntegratedSorting() {
    classCallCheck(this, IntegratedSorting);
    return possibleConstructorReturn(this, (IntegratedSorting.__proto__ || Object.getPrototypeOf(IntegratedSorting)).apply(this, arguments));
  }

  createClass(IntegratedSorting, [{
    key: 'render',
    value: function render() {
      var columnExtensions = this.props.columnExtensions;

      var getColumnCompare = function getColumnCompare(columnName) {
        return getColumnExtension(columnExtensions, columnName).compare;
      };

      var rowsComputed = function rowsComputed(_ref) {
        var rows = _ref.rows,
            sorting = _ref.sorting,
            getCellValue = _ref.getCellValue,
            isGroupRow = _ref.isGroupRow,
            getRowLevelKey = _ref.getRowLevelKey;
        return sortedRows(rows, sorting, getCellValue, getColumnCompare, isGroupRow, getRowLevelKey);
      };

      return _(
        Plugin,
        {
          name: 'IntegratedSorting',
          dependencies: pluginDependencies$7
        },
        _(Getter, { name: 'rows', computed: rowsComputed })
      );
    }
  }]);
  return IntegratedSorting;
}(React.PureComponent);

IntegratedSorting.propTypes = {
  columnExtensions: array
};

IntegratedSorting.defaultProps = {
  columnExtensions: undefined
};

var getTargetColumns = function getTargetColumns(payload, columns) {
  return payload.filter(function (item) {
    return item.type === 'column';
  }).map(function (item) {
    return columns.find(function (column) {
      return column.name === item.columnName;
    });
  });
};

var DragDropProvider$1 = function (_React$PureComponent) {
  inherits(DragDropProvider$$1, _React$PureComponent);

  function DragDropProvider$$1(props) {
    classCallCheck(this, DragDropProvider$$1);

    var _this = possibleConstructorReturn(this, (DragDropProvider$$1.__proto__ || Object.getPrototypeOf(DragDropProvider$$1)).call(this, props));

    _this.state = {
      payload: null,
      clientOffset: null
    };

    _this.change = function (_ref) {
      var payload = _ref.payload,
          clientOffset = _ref.clientOffset;
      return _this.setState({ payload: payload, clientOffset: clientOffset });
    };
    return _this;
  }

  createClass(DragDropProvider$$1, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          Container = _props.containerComponent,
          Column = _props.columnComponent;
      var _state = this.state,
          payload = _state.payload,
          clientOffset = _state.clientOffset;


      return _(
        Plugin,
        {
          name: 'DragDropProvider'
        },
        _(Getter, { name: 'draggingEnabled', value: true }),
        _(
          Template,
          { name: 'root' },
          _(
            DragDropProvider,
            {
              onChange: this.change
            },
            _(TemplatePlaceholder, null)
          ),
          payload && _(
            TemplateConnector,
            null,
            function (_ref2) {
              var columns = _ref2.columns;
              return _(
                Container,
                {
                  clientOffset: clientOffset
                },
                getTargetColumns(payload, columns).map(function (column) {
                  return _(Column, {
                    key: column.name,
                    column: column
                  });
                })
              );
            }
          )
        )
      );
    }
  }]);
  return DragDropProvider$$1;
}(React.PureComponent);

DragDropProvider$1.propTypes = {
  containerComponent: func.isRequired,
  columnComponent: func.isRequired
};

var pluginDependencies$8 = [{ name: 'Table' }];

var tableHeaderRowsComputed = function tableHeaderRowsComputed(_ref) {
  var tableHeaderRows = _ref.tableHeaderRows;
  return tableHeaderRowsWithReordering(tableHeaderRows);
};

var TableColumnReordering = function (_React$PureComponent) {
  inherits(TableColumnReordering, _React$PureComponent);

  function TableColumnReordering(props) {
    classCallCheck(this, TableColumnReordering);

    var _this = possibleConstructorReturn(this, (TableColumnReordering.__proto__ || Object.getPrototypeOf(TableColumnReordering)).call(this, props));

    _this.state = {
      order: props.defaultOrder,
      sourceColumnIndex: -1,
      targetColumnIndex: -1
    };

    _this.onOver = _this.handleOver.bind(_this);
    _this.onLeave = _this.handleLeave.bind(_this);
    _this.onDrop = _this.handleDrop.bind(_this);
    return _this;
  }

  createClass(TableColumnReordering, [{
    key: 'getState',
    value: function getState() {
      var _props$order = this.props.order,
          order = _props$order === undefined ? this.state.order : _props$order;

      return _extends({}, this.state, {
        order: order
      });
    }
  }, {
    key: 'getDraftOrder',
    value: function getDraftOrder() {
      var _getState = this.getState(),
          order = _getState.order,
          sourceColumnIndex = _getState.sourceColumnIndex,
          targetColumnIndex = _getState.targetColumnIndex;

      return draftOrder(order, sourceColumnIndex, targetColumnIndex);
    }
  }, {
    key: 'getAvailableColumns',
    value: function getAvailableColumns() {
      var _this2 = this;

      return this.getDraftOrder().filter(function (columnName) {
        return !!_this2.cellDimensionGetters[columnName];
      });
    }
  }, {
    key: 'cacheCellDimensions',
    value: function cacheCellDimensions() {
      var _this3 = this;

      this.cellDimensions = this.cellDimensions && this.cellDimensions.length ? this.cellDimensions : this.getAvailableColumns().map(function (columnName) {
        return _this3.cellDimensionGetters[columnName]();
      });
    }
  }, {
    key: 'resetCellDimensions',
    value: function resetCellDimensions() {
      this.cellDimensions = [];
    }
  }, {
    key: 'ensureCellDimensionGetters',
    value: function ensureCellDimensionGetters(tableColumns) {
      var _this4 = this;

      Object.keys(this.cellDimensionGetters).forEach(function (columnName) {
        var columnIndex = tableColumns.findIndex(function (_ref2) {
          var type = _ref2.type,
              column = _ref2.column;
          return type === TABLE_DATA_TYPE && column.name === columnName;
        });
        if (columnIndex === -1) {
          delete _this4.cellDimensionGetters[columnName];
        }
      });
    }
  }, {
    key: 'storeCellDimensionsGetter',
    value: function storeCellDimensionsGetter(tableColumn, getter, tableColumns) {
      if (tableColumn.type === TABLE_DATA_TYPE) {
        this.cellDimensionGetters[tableColumn.column.name] = getter;
      }
      this.ensureCellDimensionGetters(tableColumns);
    }
  }, {
    key: 'handleOver',
    value: function handleOver(_ref3) {
      var payload = _ref3.payload,
          x = _ref3.clientOffset.x;

      var sourceColumnName = payload[0].columnName;
      var availableColumns = this.getAvailableColumns();
      var relativeSourceColumnIndex = availableColumns.indexOf(sourceColumnName);

      if (relativeSourceColumnIndex === -1) return;

      this.cacheCellDimensions();
      var cellDimensions = this.cellDimensions;


      var overlappedColumns = cellDimensions.filter(function (_ref4) {
        var left = _ref4.left,
            right = _ref4.right;
        return left <= x && x <= right;
      });

      if (overlappedColumns.length > 1) return;

      var relativeTargetIndex = getTableTargetColumnIndex(cellDimensions, relativeSourceColumnIndex, x);

      if (relativeTargetIndex === -1) return;

      var _getState2 = this.getState(),
          prevSourceColumnIndex = _getState2.sourceColumnIndex,
          prevTargetColumnIndex = _getState2.targetColumnIndex;

      var draftOrder$$1 = this.getDraftOrder();
      var targetColumnIndex = draftOrder$$1.indexOf(availableColumns[relativeTargetIndex]);

      if (targetColumnIndex === prevTargetColumnIndex) return;

      var sourceColumnIndex = prevSourceColumnIndex === -1 ? draftOrder$$1.indexOf(sourceColumnName) : prevSourceColumnIndex;

      this.setState({
        sourceColumnIndex: sourceColumnIndex,
        targetColumnIndex: targetColumnIndex
      });
    }
  }, {
    key: 'handleLeave',
    value: function handleLeave() {
      this.setState({
        sourceColumnIndex: -1,
        targetColumnIndex: -1
      });

      this.resetCellDimensions();
    }
  }, {
    key: 'handleDrop',
    value: function handleDrop() {
      var _getState3 = this.getState(),
          sourceColumnIndex = _getState3.sourceColumnIndex,
          targetColumnIndex = _getState3.targetColumnIndex,
          order = _getState3.order;

      var onOrderChange = this.props.onOrderChange;

      var nextOrder = changeColumnOrder(order, {
        sourceColumnName: order[sourceColumnIndex],
        targetColumnName: order[targetColumnIndex]
      });

      this.setState({
        order: nextOrder,
        sourceColumnIndex: -1,
        targetColumnIndex: -1
      });

      if (onOrderChange) {
        onOrderChange(nextOrder);
      }

      this.resetCellDimensions();
    }
  }, {
    key: 'render',
    value: function render() {
      var _this5 = this;

      var _props = this.props,
          Container = _props.tableContainerComponent,
          Row = _props.rowComponent,
          Cell = _props.cellComponent;


      var columnsComputed = function columnsComputed(_ref5) {
        var tableColumns = _ref5.tableColumns;
        return orderedColumns(tableColumns, _this5.getDraftOrder());
      };

      this.cellDimensionGetters = {};

      return _(
        Plugin,
        {
          name: 'TableColumnReordering',
          dependencies: pluginDependencies$8
        },
        _(Getter, { name: 'tableColumns', computed: columnsComputed }),
        _(Getter, { name: 'tableHeaderRows', computed: tableHeaderRowsComputed }),
        _(
          Template,
          { name: 'table' },
          function (params) {
            return _(
              Container,
              _extends({}, params, {
                onOver: _this5.onOver,
                onLeave: _this5.onLeave,
                onDrop: _this5.onDrop
              }),
              _(TemplatePlaceholder, null)
            );
          }
        ),
        _(
          Template,
          {
            name: 'tableRow',
            predicate: function predicate(_ref6) {
              var tableRow = _ref6.tableRow;
              return tableRow.type === TABLE_REORDERING_TYPE;
            }
          },
          function (params) {
            return _(Row, params);
          }
        ),
        _(
          Template,
          {
            name: 'tableCell',
            predicate: function predicate(_ref7) {
              var tableRow = _ref7.tableRow;
              return tableRow.type === TABLE_REORDERING_TYPE;
            }
          },
          function (params) {
            return _(
              TemplateConnector,
              null,
              function (_ref8) {
                var tableColumns = _ref8.tableColumns;
                return _(Cell, _extends({}, params, {
                  getCellDimensions: function getCellDimensions(getter) {
                    return _this5.storeCellDimensionsGetter(params.tableColumn, getter, tableColumns);
                  }
                }));
              }
            );
          }
        )
      );
    }
  }]);
  return TableColumnReordering;
}(React.PureComponent);

TableColumnReordering.propTypes = {
  order: arrayOf(string),
  defaultOrder: arrayOf(string),
  onOrderChange: func,
  tableContainerComponent: func.isRequired,
  rowComponent: func.isRequired,
  cellComponent: func.isRequired
};

TableColumnReordering.defaultProps = {
  order: undefined,
  defaultOrder: [],
  onOrderChange: undefined
};

var RowPlaceholder = function RowPlaceholder(props) {
  return _(TemplatePlaceholder, { name: 'tableRow', params: props });
};
var CellPlaceholder = function CellPlaceholder(props) {
  return _(TemplatePlaceholder, { name: 'tableCell', params: props });
};

var tableHeaderRows = [];
var tableBodyRowsComputed = function tableBodyRowsComputed(_ref) {
  var rows = _ref.rows,
      getRowId = _ref.getRowId;
  return tableRowsWithDataRows(rows, getRowId);
};

var pluginDependencies$9 = [{ name: 'DataTypeProvider', optional: true }];

var Table = function (_React$PureComponent) {
  inherits(Table, _React$PureComponent);

  function Table() {
    classCallCheck(this, Table);
    return possibleConstructorReturn(this, (Table.__proto__ || Object.getPrototypeOf(Table)).apply(this, arguments));
  }

  createClass(Table, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          Layout = _props.layoutComponent,
          Cell = _props.cellComponent,
          Row = _props.rowComponent,
          NoDataRow = _props.noDataRowComponent,
          NoDataCell = _props.noDataCellComponent,
          StubCell = _props.stubCellComponent,
          StubHeaderCell = _props.stubHeaderCellComponent,
          columnExtensions = _props.columnExtensions,
          messages = _props.messages,
          containerComponent = _props.containerComponent,
          tableComponent = _props.tableComponent,
          headComponent = _props.headComponent,
          bodyComponent = _props.bodyComponent,
          fixedHeaderComponent = _props.fixedHeaderComponent;


      var getMessage = getMessagesFormatter(messages);
      var tableColumnsComputed = function tableColumnsComputed(_ref2) {
        var columns = _ref2.columns;
        return tableColumnsWithDataRows(columns, columnExtensions);
      };

      return _(
        Plugin,
        {
          name: 'Table',
          dependencies: pluginDependencies$9
        },
        _(Getter, { name: 'tableHeaderRows', value: tableHeaderRows }),
        _(Getter, { name: 'tableBodyRows', computed: tableBodyRowsComputed }),
        _(Getter, { name: 'tableColumns', computed: tableColumnsComputed }),
        _(
          Template,
          { name: 'body' },
          _(TemplatePlaceholder, { name: 'table' })
        ),
        _(
          Template,
          { name: 'table' },
          _(
            TemplateConnector,
            null,
            function (_ref3) {
              var headerRows = _ref3.tableHeaderRows,
                  bodyRows = _ref3.tableBodyRows,
                  columns = _ref3.tableColumns;
              return _(Layout, {
                headTableComponent: fixedHeaderComponent,
                tableComponent: tableComponent,
                headComponent: headComponent,
                bodyComponent: bodyComponent,
                containerComponent: containerComponent,
                headerRows: headerRows,
                bodyRows: bodyRows,
                columns: columns,
                rowComponent: RowPlaceholder,
                cellComponent: CellPlaceholder
              });
            }
          )
        ),
        _(
          Template,
          { name: 'tableCell' },
          function (params) {
            return _(
              TemplateConnector,
              null,
              function (_ref4) {
                var headerRows = _ref4.tableHeaderRows;
                return isHeaderStubTableCell(params.tableRow, headerRows) ? _(StubHeaderCell, params) : _(StubCell, params);
              }
            );
          }
        ),
        _(
          Template,
          {
            name: 'tableCell',
            predicate: function predicate(_ref5) {
              var tableRow = _ref5.tableRow,
                  tableColumn = _ref5.tableColumn;
              return isDataTableCell(tableRow, tableColumn);
            }
          },
          function (params) {
            return _(
              TemplateConnector,
              null,
              function (_ref6) {
                var getCellValue = _ref6.getCellValue;

                var columnName = params.tableColumn.column.name;
                var value = getCellValue(params.tableRow.row, columnName);
                return _(
                  TemplatePlaceholder,
                  {
                    name: 'valueFormatter',
                    params: {
                      row: params.tableRow.row,
                      column: params.tableColumn.column,
                      value: value
                    }
                  },
                  function (content) {
                    return _(
                      Cell,
                      _extends({}, params, {
                        row: params.tableRow.row,
                        column: params.tableColumn.column,
                        value: value
                      }),
                      content
                    );
                  }
                );
              }
            );
          }
        ),
        _(
          Template,
          {
            name: 'tableCell',
            predicate: function predicate(_ref7) {
              var tableRow = _ref7.tableRow;
              return isNoDataTableRow(tableRow);
            }
          },
          function (params) {
            return _(NoDataCell, _extends({ getMessage: getMessage }, params));
          }
        ),
        _(
          Template,
          {
            name: 'tableRow',
            predicate: function predicate(_ref8) {
              var tableRow = _ref8.tableRow;
              return isDataTableRow(tableRow);
            }
          },
          function (params) {
            return _(Row, _extends({}, params, {
              row: params.tableRow.row
            }));
          }
        ),
        _(
          Template,
          {
            name: 'tableRow',
            predicate: function predicate(_ref9) {
              var tableRow = _ref9.tableRow;
              return isNoDataTableRow(tableRow);
            }
          },
          function (params) {
            return _(NoDataRow, params);
          }
        )
      );
    }
  }]);
  return Table;
}(React.PureComponent);

Table.propTypes = {
  layoutComponent: func.isRequired,
  tableComponent: func.isRequired,
  headComponent: func.isRequired,
  bodyComponent: func.isRequired,
  containerComponent: func.isRequired,
  cellComponent: func.isRequired,
  rowComponent: func.isRequired,
  noDataCellComponent: func.isRequired,
  noDataRowComponent: func.isRequired,
  stubCellComponent: func.isRequired,
  stubHeaderCellComponent: func.isRequired,
  columnExtensions: array,
  messages: object,
  fixedHeaderComponent: func
};

Table.defaultProps = {
  fixedHeaderComponent: undefined,
  columnExtensions: undefined,
  messages: {}
};

var TableSelection = function (_React$PureComponent) {
  inherits(TableSelection, _React$PureComponent);

  function TableSelection() {
    classCallCheck(this, TableSelection);
    return possibleConstructorReturn(this, (TableSelection.__proto__ || Object.getPrototypeOf(TableSelection)).apply(this, arguments));
  }

  createClass(TableSelection, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          highlightRow = _props.highlightRow,
          selectByRowClick = _props.selectByRowClick,
          showSelectionColumn = _props.showSelectionColumn,
          showSelectAll = _props.showSelectAll,
          HeaderCell = _props.headerCellComponent,
          Cell = _props.cellComponent,
          Row = _props.rowComponent,
          selectionColumnWidth = _props.selectionColumnWidth;


      var tableColumnsComputed = function tableColumnsComputed(_ref) {
        var tableColumns = _ref.tableColumns;
        return tableColumnsWithSelection(tableColumns, selectionColumnWidth);
      };

      return _(
        Plugin,
        {
          name: 'TableSelection',
          dependencies: [{ name: 'Table' }, { name: 'SelectionState' }, { name: 'IntegratedSelection', optional: !showSelectAll }]
        },
        showSelectionColumn && _(Getter, { name: 'tableColumns', computed: tableColumnsComputed }),
        showSelectionColumn && showSelectAll && _(
          Template,
          {
            name: 'tableCell',
            predicate: function predicate(_ref2) {
              var tableRow = _ref2.tableRow,
                  tableColumn = _ref2.tableColumn;
              return isSelectAllTableCell(tableRow, tableColumn);
            }
          },
          function (params) {
            return _(
              TemplateConnector,
              null,
              function (_ref3, _ref4) {
                var selectAllAvailable = _ref3.selectAllAvailable,
                    allSelected$$1 = _ref3.allSelected,
                    someSelected$$1 = _ref3.someSelected;
                var toggleSelectAll = _ref4.toggleSelectAll;
                return _(HeaderCell, _extends({}, params, {
                  disabled: !selectAllAvailable,
                  allSelected: allSelected$$1,
                  someSelected: someSelected$$1,
                  onToggle: function onToggle(select) {
                    return toggleSelectAll(select);
                  }
                }));
              }
            );
          }
        ),
        showSelectionColumn && _(
          Template,
          {
            name: 'tableCell',
            predicate: function predicate(_ref5) {
              var tableRow = _ref5.tableRow,
                  tableColumn = _ref5.tableColumn;
              return isSelectTableCell(tableRow, tableColumn);
            }
          },
          function (params) {
            return _(
              TemplateConnector,
              null,
              function (_ref6, _ref7) {
                var selection = _ref6.selection;
                var toggleSelection$$1 = _ref7.toggleSelection;
                return _(Cell, _extends({}, params, {
                  row: params.tableRow.row,
                  selected: selection.indexOf(params.tableRow.rowId) !== -1,
                  onToggle: function onToggle() {
                    return toggleSelection$$1({ rowIds: [params.tableRow.rowId] });
                  }
                }));
              }
            );
          }
        ),
        (highlightRow || selectByRowClick) && _(
          Template,
          {
            name: 'tableRow',
            predicate: function predicate(_ref8) {
              var tableRow = _ref8.tableRow;
              return isDataTableRow(tableRow);
            }
          },
          function (params) {
            return _(
              TemplateConnector,
              null,
              function (_ref9, _ref10) {
                var selection = _ref9.selection;
                var toggleSelection$$1 = _ref10.toggleSelection;
                return _(Row, _extends({}, params, {
                  selectByRowClick: true,
                  selected: highlightRow && selection.indexOf(params.tableRow.rowId) !== -1,
                  onToggle: function onToggle() {
                    return toggleSelection$$1({ rowIds: [params.tableRow.rowId] });
                  }
                }));
              }
            );
          }
        )
      );
    }
  }]);
  return TableSelection;
}(React.PureComponent);

TableSelection.propTypes = {
  headerCellComponent: func.isRequired,
  cellComponent: func.isRequired,
  rowComponent: func.isRequired,
  highlightRow: bool,
  selectByRowClick: bool,
  showSelectAll: bool,
  showSelectionColumn: bool,
  selectionColumnWidth: number.isRequired
};

TableSelection.defaultProps = {
  highlightRow: false,
  selectByRowClick: false,
  showSelectAll: false,
  showSelectionColumn: true
};

var RowDetailState = function (_React$PureComponent) {
  inherits(RowDetailState, _React$PureComponent);

  function RowDetailState(props) {
    classCallCheck(this, RowDetailState);

    var _this = possibleConstructorReturn(this, (RowDetailState.__proto__ || Object.getPrototypeOf(RowDetailState)).call(this, props));

    _this.state = {
      expandedRowIds: props.expandedRowIds || props.defaultExpandedRowIds
    };

    var stateHelper = createStateHelper(_this, {
      expandedRowIds: function expandedRowIds() {
        return _this.props.onExpandedRowIdsChange;
      }
    });

    _this.toggleDetailRowExpanded = stateHelper.applyFieldReducer.bind(stateHelper, 'expandedRowIds', toggleDetailRowExpanded);
    return _this;
  }

  createClass(RowDetailState, [{
    key: 'componentWillReceiveProps',
    value: function componentWillReceiveProps(nextProps) {
      var expandedRowIds = nextProps.expandedRowIds;

      this.setState(_extends({}, expandedRowIds !== undefined ? { expandedRowIds: expandedRowIds } : null));
    }
  }, {
    key: 'render',
    value: function render() {
      var expandedRowIds = this.state.expandedRowIds;


      return _(
        Plugin,
        {
          name: 'RowDetailState'
        },
        _(Getter, { name: 'expandedDetailRowIds', value: expandedRowIds }),
        _(Action, { name: 'toggleDetailRowExpanded', action: this.toggleDetailRowExpanded })
      );
    }
  }]);
  return RowDetailState;
}(React.PureComponent);

RowDetailState.propTypes = {
  expandedRowIds: array,
  defaultExpandedRowIds: array,
  onExpandedRowIdsChange: func
};

RowDetailState.defaultProps = {
  expandedRowIds: undefined,
  defaultExpandedRowIds: [],
  onExpandedRowIdsChange: undefined
};

var pluginDependencies$10 = [{ name: 'Table' }];

var TableRowDetail = function (_React$PureComponent) {
  inherits(TableRowDetail, _React$PureComponent);

  function TableRowDetail() {
    classCallCheck(this, TableRowDetail);
    return possibleConstructorReturn(this, (TableRowDetail.__proto__ || Object.getPrototypeOf(TableRowDetail)).apply(this, arguments));
  }

  createClass(TableRowDetail, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          rowHeight = _props.rowHeight,
          Content = _props.contentComponent,
          ToggleCell = _props.toggleCellComponent,
          Cell = _props.cellComponent,
          Row = _props.rowComponent,
          toggleColumnWidth = _props.toggleColumnWidth;


      var tableColumnsComputed = function tableColumnsComputed(_ref) {
        var tableColumns = _ref.tableColumns;
        return tableColumnsWithDetail(tableColumns, toggleColumnWidth);
      };
      var tableBodyRowsComputed = function tableBodyRowsComputed(_ref2) {
        var tableBodyRows = _ref2.tableBodyRows,
            expandedDetailRowIds = _ref2.expandedDetailRowIds;
        return tableRowsWithExpandedDetail(tableBodyRows, expandedDetailRowIds, rowHeight);
      };

      return _(
        Plugin,
        {
          name: 'TableRowDetail',
          dependencies: pluginDependencies$10
        },
        _(Getter, { name: 'tableColumns', computed: tableColumnsComputed }),
        _(Getter, { name: 'tableBodyRows', computed: tableBodyRowsComputed }),
        _(
          Template,
          {
            name: 'tableCell',
            predicate: function predicate(_ref3) {
              var tableRow = _ref3.tableRow,
                  tableColumn = _ref3.tableColumn;
              return isDetailToggleTableCell(tableRow, tableColumn);
            }
          },
          function (params) {
            return _(
              TemplateConnector,
              null,
              function (_ref4, _ref5) {
                var expandedDetailRowIds = _ref4.expandedDetailRowIds;
                var toggleDetailRowExpanded$$1 = _ref5.toggleDetailRowExpanded;
                return _(ToggleCell, _extends({}, params, {
                  row: params.tableRow.row,
                  expanded: isDetailRowExpanded(expandedDetailRowIds, params.tableRow.rowId),
                  onToggle: function onToggle() {
                    return toggleDetailRowExpanded$$1({ rowId: params.tableRow.rowId });
                  }
                }));
              }
            );
          }
        ),
        _(
          Template,
          {
            name: 'tableCell',
            predicate: function predicate(_ref6) {
              var tableRow = _ref6.tableRow;
              return isDetailTableRow(tableRow);
            }
          },
          function (params) {
            return _(
              Cell,
              _extends({}, params, {
                row: params.tableRow.row
              }),
              _(Content, { row: params.tableRow.row })
            );
          }
        ),
        _(
          Template,
          {
            name: 'tableRow',
            predicate: function predicate(_ref7) {
              var tableRow = _ref7.tableRow;
              return isDetailTableRow(tableRow);
            }
          },
          function (params) {
            return _(Row, _extends({}, params, {
              row: params.tableRow.row
            }));
          }
        )
      );
    }
  }]);
  return TableRowDetail;
}(React.PureComponent);

TableRowDetail.propTypes = {
  contentComponent: func,
  toggleCellComponent: func.isRequired,
  cellComponent: func.isRequired,
  rowComponent: func.isRequired,
  toggleColumnWidth: number.isRequired,
  rowHeight: number
};

TableRowDetail.defaultProps = {
  contentComponent: function contentComponent() {
    return null;
  },
  rowHeight: undefined
};

var pluginDependencies$11 = [{ name: 'GroupingState' }, { name: 'Table' }, { name: 'DataTypeProvider', optional: true }];

var tableBodyRowsComputed$1 = function tableBodyRowsComputed(_ref) {
  var tableBodyRows = _ref.tableBodyRows,
      isGroupRow = _ref.isGroupRow;
  return tableRowsWithGrouping(tableBodyRows, isGroupRow);
};

var showColumnWhenGroupedGetter = function showColumnWhenGroupedGetter(showColumnsWhenGrouped) {
  var columnExtensions = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : [];

  var map = columnExtensions.reduce(function (acc, columnExtension) {
    acc[columnExtension.columnName] = columnExtension.showWhenGrouped;
    return acc;
  }, {});

  return function (columnName) {
    return map[columnName] || showColumnsWhenGrouped;
  };
};

var TableGroupRow = function (_React$PureComponent) {
  inherits(TableGroupRow, _React$PureComponent);

  function TableGroupRow() {
    classCallCheck(this, TableGroupRow);
    return possibleConstructorReturn(this, (TableGroupRow.__proto__ || Object.getPrototypeOf(TableGroupRow)).apply(this, arguments));
  }

  createClass(TableGroupRow, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          GroupCell = _props.cellComponent,
          GroupRow = _props.rowComponent,
          GroupIndentCell = _props.indentCellComponent,
          indentColumnWidth = _props.indentColumnWidth,
          showColumnsWhenGrouped = _props.showColumnsWhenGrouped,
          columnExtensions = _props.columnExtensions;


      var tableColumnsComputed = function tableColumnsComputed(_ref2) {
        var columns = _ref2.columns,
            tableColumns = _ref2.tableColumns,
            grouping = _ref2.grouping,
            draftGrouping = _ref2.draftGrouping;
        return tableColumnsWithGrouping(columns, tableColumns, grouping, draftGrouping, indentColumnWidth, showColumnWhenGroupedGetter(showColumnsWhenGrouped, columnExtensions));
      };

      return _(
        Plugin,
        {
          name: 'TableGroupRow',
          dependencies: pluginDependencies$11
        },
        _(Getter, { name: 'tableColumns', computed: tableColumnsComputed }),
        _(Getter, { name: 'tableBodyRows', computed: tableBodyRowsComputed$1 }),
        _(
          Template,
          {
            name: 'tableCell',
            predicate: function predicate(_ref3) {
              var tableRow = _ref3.tableRow,
                  tableColumn = _ref3.tableColumn;
              return isGroupTableCell(tableRow, tableColumn);
            }
          },
          function (params) {
            return _(
              TemplateConnector,
              null,
              function (_ref4, _ref5) {
                var expandedGroups = _ref4.expandedGroups;
                var toggleGroupExpanded = _ref5.toggleGroupExpanded;
                return _(
                  TemplatePlaceholder,
                  {
                    name: 'valueFormatter',
                    params: {
                      column: params.tableColumn.column,
                      value: params.tableRow.row.value
                    }
                  },
                  function (content) {
                    return _(
                      GroupCell,
                      _extends({}, params, {
                        row: params.tableRow.row,
                        column: params.tableColumn.column,
                        expanded: expandedGroups.indexOf(params.tableRow.row.compoundKey) !== -1,
                        onToggle: function onToggle() {
                          return toggleGroupExpanded({ groupKey: params.tableRow.row.compoundKey });
                        }
                      }),
                      content
                    );
                  }
                );
              }
            );
          }
        ),
        GroupIndentCell && _(
          Template,
          {
            name: 'tableCell',
            predicate: function predicate(_ref6) {
              var tableRow = _ref6.tableRow,
                  tableColumn = _ref6.tableColumn;
              return isGroupIndentTableCell(tableRow, tableColumn);
            }
          },
          function (params) {
            return _(GroupIndentCell, _extends({}, params, {
              row: params.tableRow.row,
              column: params.tableColumn.column
            }));
          }
        ),
        _(
          Template,
          {
            name: 'tableRow',
            predicate: function predicate(_ref7) {
              var tableRow = _ref7.tableRow;
              return isGroupTableRow(tableRow);
            }
          },
          function (params) {
            return _(GroupRow, _extends({}, params, {
              row: params.tableRow.row
            }));
          }
        )
      );
    }
  }]);
  return TableGroupRow;
}(React.PureComponent);

TableGroupRow.propTypes = {
  cellComponent: func.isRequired,
  rowComponent: func.isRequired,
  indentCellComponent: func,
  indentColumnWidth: number.isRequired,
  showColumnsWhenGrouped: bool,
  columnExtensions: array
};

TableGroupRow.defaultProps = {
  indentCellComponent: null,
  showColumnsWhenGrouped: false,
  columnExtensions: undefined
};

var tableHeaderRowsComputed$1 = function tableHeaderRowsComputed(_ref) {
  var tableHeaderRows = _ref.tableHeaderRows;
  return tableRowsWithHeading(tableHeaderRows);
};

var TableHeaderRow = function (_React$PureComponent) {
  inherits(TableHeaderRow, _React$PureComponent);

  function TableHeaderRow() {
    classCallCheck(this, TableHeaderRow);
    return possibleConstructorReturn(this, (TableHeaderRow.__proto__ || Object.getPrototypeOf(TableHeaderRow)).apply(this, arguments));
  }

  createClass(TableHeaderRow, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          showSortingControls = _props.showSortingControls,
          showGroupingControls = _props.showGroupingControls,
          HeaderCell = _props.cellComponent,
          HeaderRow = _props.rowComponent,
          messages = _props.messages;

      var getMessage = getMessagesFormatter(messages);

      return _(
        Plugin,
        {
          name: 'TableHeaderRow',
          dependencies: [{ name: 'Table' }, { name: 'SortingState', optional: !showSortingControls }, { name: 'GroupingState', optional: !showGroupingControls }, { name: 'DragDropProvider', optional: true }, { name: 'TableColumnResizing', optional: true }]
        },
        _(Getter, { name: 'tableHeaderRows', computed: tableHeaderRowsComputed$1 }),
        _(
          Template,
          {
            name: 'tableCell',
            predicate: function predicate(_ref2) {
              var tableRow = _ref2.tableRow,
                  tableColumn = _ref2.tableColumn;
              return isHeadingTableCell(tableRow, tableColumn);
            }
          },
          function (params) {
            return _(
              TemplateConnector,
              null,
              function (_ref3, _ref4) {
                var sorting = _ref3.sorting,
                    isColumnSortingEnabled = _ref3.isColumnSortingEnabled,
                    isColumnGroupingEnabled = _ref3.isColumnGroupingEnabled,
                    tableColumns = _ref3.tableColumns,
                    draggingEnabled = _ref3.draggingEnabled,
                    tableColumnResizingEnabled = _ref3.tableColumnResizingEnabled;
                var changeColumnSorting$$1 = _ref4.changeColumnSorting,
                    changeColumnGrouping$$1 = _ref4.changeColumnGrouping,
                    changeTableColumnWidth$$1 = _ref4.changeTableColumnWidth,
                    draftTableColumnWidth$$1 = _ref4.draftTableColumnWidth,
                    cancelTableColumnWidthDraft$$1 = _ref4.cancelTableColumnWidthDraft;
                var columnName = params.tableColumn.column.name;

                var atLeastOneDataColumn = tableColumns.filter(function (_ref5) {
                  var type = _ref5.type;
                  return type === TABLE_DATA_TYPE;
                }).length > 1;
                var sortingEnabled = isColumnSortingEnabled && isColumnSortingEnabled(columnName);
                var groupingEnabled = isColumnGroupingEnabled && isColumnGroupingEnabled(columnName) && atLeastOneDataColumn;

                return _(HeaderCell, _extends({}, params, {
                  column: params.tableColumn.column,
                  getMessage: getMessage,
                  sortingEnabled: sortingEnabled,
                  groupingEnabled: groupingEnabled,
                  showSortingControls: showSortingControls,
                  showGroupingControls: showGroupingControls,
                  draggingEnabled: draggingEnabled && atLeastOneDataColumn,
                  resizingEnabled: tableColumnResizingEnabled,
                  sortingDirection: showSortingControls ? getColumnSortingDirection(sorting, columnName) : undefined,
                  onSort: function onSort(_ref6) {
                    var direction = _ref6.direction,
                        keepOther = _ref6.keepOther;
                    return changeColumnSorting$$1({ columnName: columnName, direction: direction, keepOther: keepOther });
                  },
                  onGroup: function onGroup() {
                    return changeColumnGrouping$$1({ columnName: columnName });
                  },
                  onWidthChange: function onWidthChange(_ref7) {
                    var shift = _ref7.shift;
                    return changeTableColumnWidth$$1({ columnName: columnName, shift: shift });
                  },
                  onWidthDraft: function onWidthDraft(_ref8) {
                    var shift = _ref8.shift;
                    return draftTableColumnWidth$$1({ columnName: columnName, shift: shift });
                  },
                  onWidthDraftCancel: function onWidthDraftCancel() {
                    return cancelTableColumnWidthDraft$$1();
                  }
                }));
              }
            );
          }
        ),
        _(
          Template,
          {
            name: 'tableRow',
            predicate: function predicate(_ref9) {
              var tableRow = _ref9.tableRow;
              return isHeadingTableRow(tableRow);
            }
          },
          function (params) {
            return _(HeaderRow, params);
          }
        )
      );
    }
  }]);
  return TableHeaderRow;
}(React.PureComponent);

TableHeaderRow.propTypes = {
  showSortingControls: bool,
  showGroupingControls: bool,
  cellComponent: func.isRequired,
  rowComponent: func.isRequired,
  messages: object
};

TableHeaderRow.defaultProps = {
  showSortingControls: false,
  showGroupingControls: false,
  messages: null
};

var pluginDependencies$12 = [{ name: 'FilteringState' }, { name: 'Table' }, { name: 'DataTypeProvider', optional: true }];

var TableFilterRow = function (_React$PureComponent) {
  inherits(TableFilterRow, _React$PureComponent);

  function TableFilterRow() {
    classCallCheck(this, TableFilterRow);
    return possibleConstructorReturn(this, (TableFilterRow.__proto__ || Object.getPrototypeOf(TableFilterRow)).apply(this, arguments));
  }

  createClass(TableFilterRow, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          rowHeight = _props.rowHeight,
          FilterCell = _props.cellComponent,
          FilterRow = _props.rowComponent,
          messages = _props.messages;


      var getMessage = getMessagesFormatter(messages);

      var tableHeaderRowsComputed = function tableHeaderRowsComputed(_ref) {
        var tableHeaderRows = _ref.tableHeaderRows;
        return tableHeaderRowsWithFilter(tableHeaderRows, rowHeight);
      };

      return _(
        Plugin,
        {
          name: 'TableFilterRow',
          dependencies: pluginDependencies$12
        },
        _(Getter, { name: 'tableHeaderRows', computed: tableHeaderRowsComputed }),
        _(
          Template,
          {
            name: 'tableCell',
            predicate: function predicate(_ref2) {
              var tableRow = _ref2.tableRow,
                  tableColumn = _ref2.tableColumn;
              return isFilterTableCell(tableRow, tableColumn);
            }
          },
          function (params) {
            return _(
              TemplateConnector,
              null,
              function (_ref3, _ref4) {
                var filters = _ref3.filters,
                    isColumnFilteringEnabled = _ref3.isColumnFilteringEnabled;
                var changeColumnFilter$$1 = _ref4.changeColumnFilter;
                var columnName = params.tableColumn.column.name;

                var filter = getColumnFilterConfig(filters, columnName);
                var onFilter = function onFilter(config) {
                  return changeColumnFilter$$1({ columnName: columnName, config: config });
                };
                return _(
                  TemplatePlaceholder,
                  {
                    name: 'valueEditor',
                    params: {
                      column: params.tableColumn.column,
                      value: filter ? filter.value : undefined,
                      onValueChange: function onValueChange(newValue) {
                        return onFilter(newValue !== undefined ? { value: newValue } : null);
                      }
                    }
                  },
                  function (content) {
                    return _(
                      FilterCell,
                      _extends({}, params, {
                        getMessage: getMessage,
                        column: params.tableColumn.column,
                        filter: filter,
                        filteringEnabled: isColumnFilteringEnabled(columnName),
                        onFilter: onFilter
                      }),
                      content
                    );
                  }
                );
              }
            );
          }
        ),
        _(
          Template,
          {
            name: 'tableRow',
            predicate: function predicate(_ref5) {
              var tableRow = _ref5.tableRow;
              return isFilterTableRow(tableRow);
            }
          },
          function (params) {
            return _(FilterRow, params);
          }
        )
      );
    }
  }]);
  return TableFilterRow;
}(React.PureComponent);

TableFilterRow.propTypes = {
  rowHeight: any,
  messages: object,
  cellComponent: func.isRequired,
  rowComponent: func.isRequired
};

TableFilterRow.defaultProps = {
  rowHeight: undefined,
  messages: {}
};

var pluginDependencies$13 = [{ name: 'EditingState' }, { name: 'Table' }, { name: 'DataTypeProvider', optional: true }];

var TableEditRow = function (_React$PureComponent) {
  inherits(TableEditRow, _React$PureComponent);

  function TableEditRow() {
    classCallCheck(this, TableEditRow);
    return possibleConstructorReturn(this, (TableEditRow.__proto__ || Object.getPrototypeOf(TableEditRow)).apply(this, arguments));
  }

  createClass(TableEditRow, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          EditCell = _props.cellComponent,
          EditRow = _props.rowComponent,
          rowHeight = _props.rowHeight;


      var tableBodyRowsComputed = function tableBodyRowsComputed(_ref) {
        var tableBodyRows = _ref.tableBodyRows,
            editingRowIds = _ref.editingRowIds,
            addedRows = _ref.addedRows;
        return tableRowsWithEditing(tableBodyRows, editingRowIds, addedRows, rowHeight);
      };

      return _(
        Plugin,
        {
          name: 'TableEditRow',
          dependencies: pluginDependencies$13
        },
        _(Getter, { name: 'tableBodyRows', computed: tableBodyRowsComputed }),
        _(
          Template,
          {
            name: 'tableCell',
            predicate: function predicate(_ref2) {
              var tableRow = _ref2.tableRow,
                  tableColumn = _ref2.tableColumn;
              return isEditTableCell(tableRow, tableColumn);
            }
          },
          function (params) {
            return _(
              TemplateConnector,
              null,
              function (_ref3, _ref4) {
                var getCellValue = _ref3.getCellValue,
                    createRowChange = _ref3.createRowChange,
                    rowChanges = _ref3.rowChanges,
                    isColumnEditingEnabled = _ref3.isColumnEditingEnabled;
                var changeAddedRow$$1 = _ref4.changeAddedRow,
                    changeRow$$1 = _ref4.changeRow;
                var _params$tableRow = params.tableRow,
                    rowId = _params$tableRow.rowId,
                    row = _params$tableRow.row;
                var column = params.tableColumn.column;
                var columnName = column.name;


                var isNew = isAddedTableRow(params.tableRow);
                var changedRow = isNew ? row : _extends({}, row, getRowChange(rowChanges, rowId));

                var value = getCellValue(changedRow, columnName);
                var onValueChange = function onValueChange(newValue) {
                  var changeArgs = {
                    rowId: rowId,
                    change: createRowChange(changedRow, newValue, columnName)
                  };
                  if (isNew) {
                    changeAddedRow$$1(changeArgs);
                  } else {
                    changeRow$$1(changeArgs);
                  }
                };
                return _(
                  TemplatePlaceholder,
                  {
                    name: 'valueEditor',
                    params: {
                      column: column,
                      row: row,
                      value: value,
                      onValueChange: onValueChange
                    }
                  },
                  function (content) {
                    return _(
                      EditCell,
                      _extends({}, params, {
                        row: row,
                        column: column,
                        value: value,
                        editingEnabled: isColumnEditingEnabled(columnName),
                        onValueChange: onValueChange
                      }),
                      content
                    );
                  }
                );
              }
            );
          }
        ),
        _(
          Template,
          {
            name: 'tableRow',
            predicate: function predicate(_ref5) {
              var tableRow = _ref5.tableRow;
              return isEditTableRow(tableRow) || isAddedTableRow(tableRow);
            }
          },
          function (params) {
            return _(EditRow, _extends({}, params, {
              row: params.tableRow.row
            }));
          }
        )
      );
    }
  }]);
  return TableEditRow;
}(React.PureComponent);

TableEditRow.propTypes = {
  rowHeight: any,
  cellComponent: func.isRequired,
  rowComponent: func.isRequired
};

TableEditRow.defaultProps = {
  rowHeight: undefined
};

var pluginDependencies$14 = [{ name: 'EditingState' }, { name: 'Table' }];

var TableEditColumn = function (_React$PureComponent) {
  inherits(TableEditColumn, _React$PureComponent);

  function TableEditColumn() {
    classCallCheck(this, TableEditColumn);
    return possibleConstructorReturn(this, (TableEditColumn.__proto__ || Object.getPrototypeOf(TableEditColumn)).apply(this, arguments));
  }

  createClass(TableEditColumn, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          Cell = _props.cellComponent,
          HeaderCell = _props.headerCellComponent,
          Command = _props.commandComponent,
          showAddCommand = _props.showAddCommand,
          showEditCommand = _props.showEditCommand,
          showDeleteCommand = _props.showDeleteCommand,
          width = _props.width,
          messages = _props.messages;

      var getMessage = getMessagesFormatter(messages);
      var tableColumnsComputed = function tableColumnsComputed(_ref) {
        var tableColumns = _ref.tableColumns;
        return tableColumnsWithEditing(tableColumns, width);
      };

      return _(
        Plugin,
        {
          name: 'TableEditColumn',
          dependencies: pluginDependencies$14
        },
        _(Getter, { name: 'tableColumns', computed: tableColumnsComputed }),
        _(
          Template,
          {
            name: 'tableCell',
            predicate: function predicate(_ref2) {
              var tableRow = _ref2.tableRow,
                  tableColumn = _ref2.tableColumn;
              return isHeadingEditCommandsTableCell(tableRow, tableColumn);
            }
          },
          function (params) {
            return _(
              TemplateConnector,
              null,
              function (getters, actions) {
                return _(
                  HeaderCell,
                  params,
                  showAddCommand && _(Command, {
                    id: 'add',
                    text: getMessage('addCommand'),
                    onExecute: function onExecute() {
                      return actions.addRow();
                    }
                  })
                );
              }
            );
          }
        ),
        _(
          Template,
          {
            name: 'tableCell',
            predicate: function predicate(_ref3) {
              var tableRow = _ref3.tableRow,
                  tableColumn = _ref3.tableColumn;
              return isEditCommandsTableCell(tableRow, tableColumn);
            }
          },
          function (params) {
            return _(
              TemplateConnector,
              null,
              function (getters, actions) {
                var isEdit = isEditTableRow(params.tableRow);
                var isNew = isAddedTableRow(params.tableRow);
                var isEditing = isEdit || isNew;
                var rowIds = [params.tableRow.rowId];
                return _(
                  Cell,
                  _extends({}, params, {
                    row: params.tableRow.row
                  }),
                  showEditCommand && !isEditing && _(Command, {
                    id: 'edit',
                    text: getMessage('editCommand'),
                    onExecute: function onExecute() {
                      return actions.startEditRows({ rowIds: rowIds });
                    }
                  }),
                  showDeleteCommand && !isEditing && _(Command, {
                    id: 'delete',
                    text: getMessage('deleteCommand'),
                    onExecute: function onExecute() {
                      actions.deleteRows({ rowIds: rowIds });
                      actions.commitDeletedRows({ rowIds: rowIds });
                    }
                  }),
                  isEditing && _(Command, {
                    id: 'commit',
                    text: getMessage('commitCommand'),
                    onExecute: function onExecute() {
                      if (isNew) {
                        actions.commitAddedRows({ rowIds: rowIds });
                      } else {
                        actions.stopEditRows({ rowIds: rowIds });
                        actions.commitChangedRows({ rowIds: rowIds });
                      }
                    }
                  }),
                  isEditing && _(Command, {
                    id: 'cancel',
                    text: getMessage('cancelCommand'),
                    onExecute: function onExecute() {
                      if (isNew) {
                        actions.cancelAddedRows({ rowIds: rowIds });
                      } else {
                        actions.stopEditRows({ rowIds: rowIds });
                        actions.cancelChangedRows({ rowIds: rowIds });
                      }
                    }
                  })
                );
              }
            );
          }
        )
      );
    }
  }]);
  return TableEditColumn;
}(React.PureComponent);
TableEditColumn.propTypes = {
  cellComponent: func.isRequired,
  headerCellComponent: func.isRequired,
  commandComponent: func.isRequired,
  showAddCommand: bool,
  showEditCommand: bool,
  showDeleteCommand: bool,
  width: number,
  messages: object
};
TableEditColumn.defaultProps = {
  showAddCommand: false,
  showEditCommand: false,
  showDeleteCommand: false,
  width: 140,
  messages: {}
};

var pluginDependencies$15 = [{ name: 'Table' }];

var TableColumnResizing = function (_React$PureComponent) {
  inherits(TableColumnResizing, _React$PureComponent);

  function TableColumnResizing(props) {
    classCallCheck(this, TableColumnResizing);

    var _this = possibleConstructorReturn(this, (TableColumnResizing.__proto__ || Object.getPrototypeOf(TableColumnResizing)).call(this, props));

    _this.state = {
      columnWidths: props.columnWidths || props.defaultColumnWidths,
      draftColumnWidths: []
    };

    var stateHelper = createStateHelper(_this, {
      columnWidths: function columnWidths() {
        return _this.props.onColumnWidthsChange;
      }
    });

    _this.changeTableColumnWidth = stateHelper.applyReducer.bind(stateHelper, changeTableColumnWidth);
    _this.draftTableColumnWidth = stateHelper.applyReducer.bind(stateHelper, draftTableColumnWidth);
    _this.cancelTableColumnWidthDraft = stateHelper.applyReducer.bind(stateHelper, cancelTableColumnWidthDraft);
    return _this;
  }

  createClass(TableColumnResizing, [{
    key: 'componentWillReceiveProps',
    value: function componentWillReceiveProps(nextProps) {
      var columnWidths = nextProps.columnWidths;

      this.setState(_extends({}, columnWidths !== undefined ? { columnWidths: columnWidths } : null));
    }
  }, {
    key: 'render',
    value: function render() {
      var _state = this.state,
          columnWidths = _state.columnWidths,
          draftColumnWidths = _state.draftColumnWidths;


      var tableColumnsComputed = function tableColumnsComputed(_ref) {
        var tableColumns = _ref.tableColumns;
        return tableColumnsWithWidths(tableColumns, columnWidths, draftColumnWidths);
      };

      return _(
        Plugin,
        {
          name: 'TableColumnResizing',
          dependencies: pluginDependencies$15
        },
        _(Getter, { name: 'tableColumnResizingEnabled', value: true }),
        _(Getter, { name: 'tableColumns', computed: tableColumnsComputed }),
        _(Action, { name: 'changeTableColumnWidth', action: this.changeTableColumnWidth }),
        _(Action, { name: 'draftTableColumnWidth', action: this.draftTableColumnWidth }),
        _(Action, { name: 'cancelTableColumnWidthDraft', action: this.cancelTableColumnWidthDraft })
      );
    }
  }]);
  return TableColumnResizing;
}(React.PureComponent);

TableColumnResizing.propTypes = {
  defaultColumnWidths: array,
  columnWidths: array,
  onColumnWidthsChange: func
};

TableColumnResizing.defaultProps = {
  defaultColumnWidths: [],
  columnWidths: undefined,
  onColumnWidthsChange: undefined
};

var pluginDependencies$16 = [{ name: 'PagingState' }];

var PagingPanel = function (_React$PureComponent) {
  inherits(PagingPanel, _React$PureComponent);

  function PagingPanel() {
    classCallCheck(this, PagingPanel);
    return possibleConstructorReturn(this, (PagingPanel.__proto__ || Object.getPrototypeOf(PagingPanel)).apply(this, arguments));
  }

  createClass(PagingPanel, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          Pager = _props.containerComponent,
          pageSizes = _props.pageSizes,
          messages = _props.messages;

      var getMessage = getMessagesFormatter(messages);

      return _(
        Plugin,
        {
          name: 'PagingPanel',
          dependencies: pluginDependencies$16
        },
        _(
          Template,
          { name: 'footer' },
          _(TemplatePlaceholder, null),
          _(
            TemplateConnector,
            null,
            function (_ref, _ref2) {
              var currentPage = _ref.currentPage,
                  pageSize = _ref.pageSize,
                  totalCount = _ref.totalCount;
              var setCurrentPage$$1 = _ref2.setCurrentPage,
                  setPageSize$$1 = _ref2.setPageSize;
              return _(Pager, {
                currentPage: currentPage,
                pageSize: pageSize,
                totalCount: totalCount,
                totalPages: pageCount(totalCount, pageSize),
                pageSizes: pageSizes,
                getMessage: getMessage,
                onCurrentPageChange: setCurrentPage$$1,
                onPageSizeChange: setPageSize$$1
              });
            }
          )
        )
      );
    }
  }]);
  return PagingPanel;
}(React.PureComponent);

PagingPanel.propTypes = {
  pageSizes: arrayOf(number),
  containerComponent: func.isRequired,
  messages: object
};

PagingPanel.defaultProps = {
  pageSizes: [],
  messages: {}
};

var GroupingPanel = function (_React$PureComponent) {
  inherits(GroupingPanel, _React$PureComponent);

  function GroupingPanel() {
    classCallCheck(this, GroupingPanel);
    return possibleConstructorReturn(this, (GroupingPanel.__proto__ || Object.getPrototypeOf(GroupingPanel)).apply(this, arguments));
  }

  createClass(GroupingPanel, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          Layout = _props.layoutComponent,
          Container = _props.containerComponent,
          Item = _props.itemComponent,
          EmptyMessage = _props.emptyMessageComponent,
          showSortingControls = _props.showSortingControls,
          showGroupingControls = _props.showGroupingControls,
          messages = _props.messages;


      var getMessage = getMessagesFormatter(messages);

      var EmptyMessagePlaceholder = function EmptyMessagePlaceholder() {
        return _(EmptyMessage, {
          getMessage: getMessage
        });
      };

      var ItemPlaceholder = function ItemPlaceholder(_ref) {
        var item = _ref.item;
        var columnName = item.column.name;


        return _(
          TemplateConnector,
          null,
          function (_ref2, _ref3) {
            var sorting = _ref2.sorting,
                isColumnSortingEnabled = _ref2.isColumnSortingEnabled,
                isColumnGroupingEnabled = _ref2.isColumnGroupingEnabled;
            var changeColumnGrouping$$1 = _ref3.changeColumnGrouping,
                changeColumnSorting$$1 = _ref3.changeColumnSorting;

            var sortingEnabled = isColumnSortingEnabled && isColumnSortingEnabled(columnName);
            var groupingEnabled = isColumnGroupingEnabled && isColumnGroupingEnabled(columnName);

            return _(Item, {
              item: item,
              sortingEnabled: sortingEnabled,
              groupingEnabled: groupingEnabled,
              showSortingControls: showSortingControls,
              sortingDirection: showSortingControls ? getColumnSortingDirection(sorting, columnName) : undefined,
              showGroupingControls: showGroupingControls,
              onGroup: function onGroup() {
                return changeColumnGrouping$$1({ columnName: columnName });
              },
              onSort: function onSort(_ref4) {
                var direction = _ref4.direction,
                    keepOther = _ref4.keepOther;
                return changeColumnSorting$$1({ columnName: columnName, direction: direction, keepOther: keepOther });
              }
            });
          }
        );
      };

      return _(
        Plugin,
        {
          name: 'GroupingPanel',
          dependencies: [{ name: 'GroupingState' }, { name: 'Toolbar' }, { name: 'SortingState', optional: !showSortingControls }]
        },
        _(
          Template,
          { name: 'toolbarContent' },
          _(
            TemplateConnector,
            null,
            function (_ref5, _ref6) {
              var columns = _ref5.columns,
                  grouping = _ref5.grouping,
                  draftGrouping = _ref5.draftGrouping,
                  draggingEnabled = _ref5.draggingEnabled,
                  isColumnGroupingEnabled = _ref5.isColumnGroupingEnabled;
              var changeColumnGrouping$$1 = _ref6.changeColumnGrouping,
                  draftColumnGrouping$$1 = _ref6.draftColumnGrouping,
                  cancelColumnGroupingDraft$$1 = _ref6.cancelColumnGroupingDraft;
              return _(Layout, {
                items: groupingPanelItems(columns, grouping, draftGrouping),
                isColumnGroupingEnabled: isColumnGroupingEnabled,
                draggingEnabled: draggingEnabled,
                onGroup: changeColumnGrouping$$1,
                onGroupDraft: draftColumnGrouping$$1,
                onGroupDraftCancel: cancelColumnGroupingDraft$$1,
                itemComponent: ItemPlaceholder,
                emptyMessageComponent: EmptyMessagePlaceholder,
                containerComponent: Container
              });
            }
          ),
          _(TemplatePlaceholder, null)
        )
      );
    }
  }]);
  return GroupingPanel;
}(React.PureComponent);

GroupingPanel.propTypes = {
  showSortingControls: bool,
  showGroupingControls: bool,
  layoutComponent: func.isRequired,
  containerComponent: func.isRequired,
  itemComponent: func.isRequired,
  emptyMessageComponent: func.isRequired,
  messages: object
};

GroupingPanel.defaultProps = {
  showSortingControls: false,
  showGroupingControls: false,
  messages: {}
};

// eslint-disable-next-line react/prefer-stateless-function
var DataTypeProvider = function (_React$PureComponent) {
  inherits(DataTypeProvider, _React$PureComponent);

  function DataTypeProvider() {
    classCallCheck(this, DataTypeProvider);
    return possibleConstructorReturn(this, (DataTypeProvider.__proto__ || Object.getPrototypeOf(DataTypeProvider)).apply(this, arguments));
  }

  createClass(DataTypeProvider, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          columnNames = _props.for,
          Formatter = _props.formatterComponent,
          Editor = _props.editorComponent;

      return _(
        Plugin,
        { name: 'DataTypeProvider' },
        Formatter ? _(
          Template,
          {
            name: 'valueFormatter',
            predicate: function predicate(_ref) {
              var column = _ref.column;
              return columnNames.includes(column.name);
            }
          },
          function (params) {
            return _(Formatter, params);
          }
        ) : null,
        Editor ? _(
          Template,
          {
            name: 'valueEditor',
            predicate: function predicate(_ref2) {
              var column = _ref2.column;
              return columnNames.includes(column.name);
            }
          },
          function (params) {
            return _(Editor, params);
          }
        ) : null
      );
    }
  }]);
  return DataTypeProvider;
}(React.PureComponent);

DataTypeProvider.propTypes = {
  for: arrayOf(string).isRequired,
  formatterComponent: func,
  editorComponent: func
};

DataTypeProvider.defaultProps = {
  formatterComponent: undefined,
  editorComponent: undefined
};

var pluginDependencies$17 = [{ name: 'Table' }];

var visibleTableColumnsComputed = function visibleTableColumnsComputed(_ref) {
  var tableColumns = _ref.tableColumns,
      hiddenColumnNames = _ref.hiddenColumnNames;
  return visibleTableColumns(tableColumns, hiddenColumnNames);
};

var columnExtensionValueGetter$4 = function columnExtensionValueGetter(columnExtensions, defaultValue) {
  return getColumnExtensionValueGetter(columnExtensions, 'togglingEnabled', defaultValue);
};

var TableColumnVisibility = function (_React$PureComponent) {
  inherits(TableColumnVisibility, _React$PureComponent);

  function TableColumnVisibility(props) {
    classCallCheck(this, TableColumnVisibility);

    var _this = possibleConstructorReturn(this, (TableColumnVisibility.__proto__ || Object.getPrototypeOf(TableColumnVisibility)).call(this, props));

    _this.state = {
      hiddenColumnNames: props.hiddenColumnNames || props.defaultHiddenColumnNames
    };
    var stateHelper = createStateHelper(_this, {
      hiddenColumnNames: function hiddenColumnNames() {
        return _this.props.onHiddenColumnNamesChange;
      }
    });

    _this.toggleColumnVisibility = stateHelper.applyFieldReducer.bind(stateHelper, 'hiddenColumnNames', toggleColumn);
    return _this;
  }

  createClass(TableColumnVisibility, [{
    key: 'componentWillReceiveProps',
    value: function componentWillReceiveProps(nextProps) {
      var hiddenColumnNames = nextProps.hiddenColumnNames;

      this.setState(_extends({}, hiddenColumnNames !== undefined ? { hiddenColumnNames: hiddenColumnNames } : null));
    }
  }, {
    key: 'render',
    value: function render() {
      var _props = this.props,
          EmptyMessage = _props.emptyMessageComponent,
          messages = _props.messages;

      var getMessage = getMessagesFormatter(messages);
      var hiddenColumnNames = this.state.hiddenColumnNames;
      var _props2 = this.props,
          columnExtensions = _props2.columnExtensions,
          columnTogglingEnabled = _props2.columnTogglingEnabled;


      return _(
        Plugin,
        {
          name: 'TableColumnVisibility',
          dependencies: pluginDependencies$17
        },
        _(Getter, { name: 'hiddenColumnNames', value: hiddenColumnNames }),
        _(Getter, { name: 'tableColumns', computed: visibleTableColumnsComputed }),
        _(Getter, {
          name: 'isColumnTogglingEnabled',
          value: columnExtensionValueGetter$4(columnExtensions, columnTogglingEnabled)
        }),
        _(Action, {
          name: 'toggleColumnVisibility',
          action: this.toggleColumnVisibility
        }),
        _(
          Template,
          { name: 'table' },
          function (params) {
            return _(
              TemplateConnector,
              null,
              function (_ref2) {
                var tableColumns = _ref2.tableColumns;
                return tableDataColumnsExist(tableColumns) ? _(TemplatePlaceholder, null) : _(EmptyMessage, _extends({
                  getMessage: getMessage
                }, params));
              }
            );
          }
        )
      );
    }
  }]);
  return TableColumnVisibility;
}(React.PureComponent);

TableColumnVisibility.propTypes = {
  hiddenColumnNames: arrayOf(string),
  defaultHiddenColumnNames: arrayOf(string),
  emptyMessageComponent: func.isRequired,
  onHiddenColumnNamesChange: func,
  messages: object,
  columnExtensions: array,
  columnTogglingEnabled: bool
};

TableColumnVisibility.defaultProps = {
  hiddenColumnNames: undefined,
  defaultHiddenColumnNames: [],
  onHiddenColumnNamesChange: undefined,
  messages: {},
  columnExtensions: undefined,
  columnTogglingEnabled: true
};

/* globals requestAnimationFrame */

var TABLE_FLEX_TYPE = 'flex';

var areColumnsChanged = function areColumnsChanged(prevColumns, nextColumns) {
  if (prevColumns.length !== nextColumns.length) return true;
  var prevKeys = prevColumns.map(function (column) {
    return column.key;
  });
  return nextColumns.find(function (column) {
    return prevKeys.indexOf(column.key) === -1;
  }) !== undefined;
};

var TableLayout = function (_React$PureComponent) {
  inherits(TableLayout, _React$PureComponent);

  function TableLayout(props) {
    classCallCheck(this, TableLayout);

    var _this = possibleConstructorReturn(this, (TableLayout.__proto__ || Object.getPrototypeOf(TableLayout)).call(this, props));

    _this.state = {
      animationState: new Map()
    };

    _this.animations = new Map();
    _this.tableNode = null;

    _this.setRef = function (ref) {
      if (ref) _this.tableNode = ref;
    };
    return _this;
  }

  createClass(TableLayout, [{
    key: 'componentWillReceiveProps',
    value: function componentWillReceiveProps(nextProps) {
      var nextColumns = nextProps.columns;
      var columns = this.props.columns;


      if (areColumnsChanged(columns, nextColumns)) return;

      // eslint-disable-next-line react/no-find-dom-node
      var tableWidth = findDOMNode(this).scrollWidth;
      this.animations = getAnimations(columns, nextColumns, tableWidth, this.animations);
      this.processAnimationFrame();
    }
  }, {
    key: 'getColumns',
    value: function getColumns() {
      var columns = this.props.columns;
      var animationState = this.state.animationState;


      var result = columns;

      var isFixedWidth = columns.filter(function (column) {
        return column.width === undefined;
      }).length === 0;
      if (isFixedWidth) {
        result = result.slice();
        result.push({ key: TABLE_FLEX_TYPE, type: TABLE_FLEX_TYPE });
      }

      if (animationState.size) {
        result = result.map(function (column) {
          return animationState.has(column.key) ? _extends({}, column, { animationState: animationState.get(column.key) }) : column;
        });
      }

      return result;
    }
  }, {
    key: 'processAnimationFrame',
    value: function processAnimationFrame() {
      this.animations = filterActiveAnimations(this.animations);

      if (!this.animations.size) {
        if (this.state.animationState.size) {
          this.setState({ animationState: new Map() });
        }
        return;
      }

      var animationState = evalAnimations(this.animations);
      this.setState({ animationState: animationState });

      requestAnimationFrame(this.processAnimationFrame.bind(this));
    }
  }, {
    key: 'render',
    value: function render() {
      var _props = this.props,
          Layout = _props.layoutComponent,
          minColumnWidth = _props.minColumnWidth,
          restProps = objectWithoutProperties(_props, ['layoutComponent', 'minColumnWidth']);

      var columns = this.getColumns();
      var minWidth = columns.map(function (column) {
        return column.width || (column.type === TABLE_FLEX_TYPE ? 0 : minColumnWidth);
      }).reduce(function (acc, width) {
        return acc + width;
      }, 0);

      return _(Layout, _extends({}, restProps, {
        columns: columns,
        minWidth: minWidth
      }));
    }
  }]);
  return TableLayout;
}(React.PureComponent);

TableLayout.propTypes = {
  columns: array.isRequired,
  minColumnWidth: number.isRequired,
  layoutComponent: func.isRequired
};

var ColumnGroup = function (_React$PureComponent) {
  inherits(ColumnGroup, _React$PureComponent);

  function ColumnGroup() {
    classCallCheck(this, ColumnGroup);
    return possibleConstructorReturn(this, (ColumnGroup.__proto__ || Object.getPrototypeOf(ColumnGroup)).apply(this, arguments));
  }

  createClass(ColumnGroup, [{
    key: 'render',
    value: function render() {
      var columns = this.props.columns;


      return _(
        'colgroup',
        null,
        columns.map(function (column) {
          return _('col', {
            key: column.key,
            style: column.width !== undefined ? { width: column.width + 'px' } : null
          });
        })
      );
    }
  }]);
  return ColumnGroup;
}(React.PureComponent);

ColumnGroup.propTypes = {
  columns: array.isRequired
};

var getColumnStyle = function getColumnStyle(_ref) {
  var column = _ref.column;
  return column.animationState;
};

var getRowStyle = function getRowStyle(_ref2) {
  var row = _ref2.row;
  return row.height !== undefined ? { height: row.height + 'px' } : undefined;
};

var RowLayout = function (_React$PureComponent) {
  inherits(RowLayout, _React$PureComponent);

  function RowLayout() {
    classCallCheck(this, RowLayout);
    return possibleConstructorReturn(this, (RowLayout.__proto__ || Object.getPrototypeOf(RowLayout)).apply(this, arguments));
  }

  createClass(RowLayout, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          row = _props.row,
          columns = _props.columns,
          Row = _props.rowComponent,
          Cell = _props.cellComponent;


      return _(
        Row,
        {
          tableRow: row,
          style: getRowStyle({ row: row })
        },
        getTableRowColumnsWithColSpan(columns, row.colSpanStart).map(function (column) {
          return _(Cell, {
            key: column.key,
            tableRow: row,
            tableColumn: column,
            style: getColumnStyle({ column: column }),
            colSpan: column.colSpan
          });
        })
      );
    }
  }]);
  return RowLayout;
}(React.PureComponent);

RowLayout.propTypes = {
  row: object.isRequired,
  columns: array.isRequired,
  rowComponent: func.isRequired,
  cellComponent: func.isRequired
};

var OVERSCAN = 3;

var STUB_TYPE = 'stub';
var VISIBLE_TYPE = 'visible';
var OVERSCAN_TYPE = 'overscan';

var STARTING_KEY = 'starting';
var ENDING_KEY = 'ending';

var getVisibleRows = function getVisibleRows(rows, viewportTop, viewportHeight, getRowHeight) {
  var result = [];

  var viewportBottom = viewportTop + viewportHeight;
  var topPosition = 0;

  var _loop = function _loop(_i) {
    var row = rows[_i];
    var lastIndex = result.length - 1;
    var last = result[lastIndex];

    var height = getRowHeight(row);
    var bottomPosition = topPosition + height;
    if (topPosition >= viewportTop && topPosition < viewportBottom || bottomPosition > viewportTop && bottomPosition <= viewportBottom || topPosition < viewportTop && bottomPosition > viewportBottom) {
      if (last && last.type === STUB_TYPE) {
        rows.slice(Math.max(0, _i - OVERSCAN), _i).forEach(function (overscanRow) {
          var overscanRowSize = getRowHeight(overscanRow);
          last.height -= overscanRowSize;
          result.push({ type: OVERSCAN_TYPE, height: overscanRowSize, row: overscanRow });
        });
        if (last.height === 0) {
          result.splice(lastIndex, 1);
        }
      }
      result.push({ type: VISIBLE_TYPE, height: height, row: row });
    } else if (last && last.type === STUB_TYPE) {
      last.height += height;
    } else if (_i > 0) {
      rows.slice(_i, _i + OVERSCAN).forEach(function (overscanRow) {
        var overscanRowSize = getRowHeight(overscanRow);
        result.push({ type: OVERSCAN_TYPE, height: overscanRowSize, row: overscanRow });
      });
      _i += OVERSCAN - 1;
      if (_i < rows.length) {
        result.push({ type: STUB_TYPE, key: ENDING_KEY, height: 0 });
      }
    } else {
      result.push({ type: STUB_TYPE, key: STARTING_KEY, height: height });
    }
    topPosition = bottomPosition;
    i = _i;
  };

  for (var i = 0; i < rows.length; i += 1) {
    _loop(i);
  }

  return result;
};

var getRowPosition = function getRowPosition(visibleRows, index) {
  return visibleRows.slice(0, index).reduce(function (acc, row) {
    return acc + row.height;
  }, 0);
};

var firstVisibleRowOffset = function firstVisibleRowOffset(prevVisibleRows, visibleRows) {
  var firstVisibleRowIndex = visibleRows.findIndex(function (row) {
    return row.type === VISIBLE_TYPE;
  });
  if (firstVisibleRowIndex === -1) return 0;

  var firstVisibleRow = visibleRows[firstVisibleRowIndex].row;
  var prevIndex = prevVisibleRows.findIndex(function (row) {
    return row.row === firstVisibleRow;
  });
  if (prevIndex === -1) return 0;

  var position = getRowPosition(visibleRows, firstVisibleRowIndex);
  var prevPosition = getRowPosition(prevVisibleRows, prevIndex);
  return position - prevPosition;
};

var VirtualTableLayout = function (_React$PureComponent) {
  inherits(VirtualTableLayout, _React$PureComponent);

  function VirtualTableLayout(props) {
    classCallCheck(this, VirtualTableLayout);

    var _this = possibleConstructorReturn(this, (VirtualTableLayout.__proto__ || Object.getPrototypeOf(VirtualTableLayout)).call(this, props));

    _this.state = {
      rowHeights: new Map(),
      viewportTop: 0
    };

    _this.rowRefs = new Map();
    _this.updateViewport = _this.updateViewport.bind(_this);
    _this.registerRowRef = _this.registerRowRef.bind(_this);
    _this.getRowHeight = _this.getRowHeight.bind(_this);
    return _this;
  }

  createClass(VirtualTableLayout, [{
    key: 'componentDidMount',
    value: function componentDidMount() {
      this.storeRowHeights();
    }
  }, {
    key: 'componentWillReceiveProps',
    value: function componentWillReceiveProps(nextProps) {
      if (this.props.headerRows !== nextProps.headerRows || this.props.rows !== nextProps.rows) {
        var prevRowHeight = this.state.rowHeights;

        this.setState({
          rowHeights: [].concat(toConsumableArray(nextProps.headerRows), toConsumableArray(nextProps.rows)).reduce(function (acc, row) {
            var rowHeight = prevRowHeight.get(row.key);
            if (rowHeight !== undefined) {
              acc.set(row.key, rowHeight);
            }
            return acc;
          }, new Map())
        });
      }
    }
  }, {
    key: 'componentDidUpdate',
    value: function componentDidUpdate() {
      this.storeRowHeights();
    }
  }, {
    key: 'getRowHeight',
    value: function getRowHeight(row) {
      var storedHeight = this.state.rowHeights.get(row.key);
      if (storedHeight !== undefined) return storedHeight;
      if (row.height) return row.height;
      return this.props.estimatedRowHeight;
    }
  }, {
    key: 'getVisibleRows',
    value: function getVisibleRows$$1() {
      var _this2 = this;

      var _ref = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : this.props,
          rows = _ref.rows,
          headerRows = _ref.headerRows,
          height = _ref.height;

      var top = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : this.state.viewportTop;

      var headHeight = headerRows.reduce(function (acc, row) {
        return acc + _this2.getRowHeight(row);
      }, 0);
      return getVisibleRows(rows, top, height - headHeight, this.getRowHeight);
    }
  }, {
    key: 'storeRowHeights',
    value: function storeRowHeights() {
      var _this3 = this;

      var rowsWithChangedHeights = Array.from(this.rowRefs.entries())
      // eslint-disable-next-line react/no-find-dom-node
      .map(function (_ref2) {
        var _ref3 = slicedToArray(_ref2, 2),
            row = _ref3[0],
            ref = _ref3[1];

        return [row, findDOMNode(ref).getBoundingClientRect().height];
      }).filter(function (_ref4) {
        var _ref5 = slicedToArray(_ref4, 2),
            row = _ref5[0],
            height = _ref5[1];

        return height !== _this3.getRowHeight(row);
      });

      if (rowsWithChangedHeights.length) {
        var prevVisibleBodyRows = this.getVisibleRows();

        var rowHeights = this.state.rowHeights;

        rowsWithChangedHeights.forEach(function (_ref6) {
          var _ref7 = slicedToArray(_ref6, 2),
              row = _ref7[0],
              height = _ref7[1];

          return rowHeights.set(row.key, height);
        });

        this.setState({
          rowHeights: rowHeights
        });

        var visibleBodyRows = this.getVisibleRows();
        var scrollOffset = firstVisibleRowOffset(prevVisibleBodyRows, visibleBodyRows);
        if (scrollOffset !== 0) {
          // eslint-disable-next-line react/no-find-dom-node
          findDOMNode(this).scrollTop += scrollOffset;
        }
      }
    }
  }, {
    key: 'registerRowRef',
    value: function registerRowRef(row, ref) {
      if (ref === null) {
        this.rowRefs.delete(row);
      } else {
        this.rowRefs.set(row, ref);
      }
    }
  }, {
    key: 'updateViewport',
    value: function updateViewport(e) {
      var node$$1 = e.target;

      if (node$$1 !== e.currentTarget) {
        return;
      }

      // NOTE: prevent iOS to flicker in bounces
      if (node$$1.scrollTop < 0 || node$$1.scrollLeft < 0 || node$$1.scrollLeft + node$$1.clientWidth > node$$1.scrollWidth || node$$1.scrollTop + node$$1.clientHeight > node$$1.scrollHeight) {
        return;
      }

      if (this.viewportTop !== node$$1.scrollTop) {
        this.setState({
          viewportTop: node$$1.scrollTop
        });
      }
    }
  }, {
    key: 'render',
    value: function render() {
      var _this4 = this;

      var _props = this.props,
          headerRows = _props.headerRows,
          columns = _props.columns,
          minWidth = _props.minWidth,
          height = _props.height,
          Container = _props.containerComponent,
          HeadTable = _props.headTableComponent,
          Table = _props.tableComponent,
          Head = _props.headComponent,
          Body = _props.bodyComponent,
          rowComponent = _props.rowComponent,
          cellComponent = _props.cellComponent;

      var visibleBodyRows = this.getVisibleRows();

      return _(
        Container,
        {
          style: { height: height + 'px' },
          onScroll: this.updateViewport
        },
        !!headerRows.length && _(
          HeadTable,
          {
            style: { minWidth: minWidth + 'px' }
          },
          _(ColumnGroup, { columns: columns }),
          _(
            Head,
            null,
            headerRows.map(function (row) {
              return _(RowLayout, {
                key: row.key,
                ref: function ref(_ref8) {
                  return _this4.registerRowRef(row, _ref8);
                },
                row: row,
                columns: columns,
                rowComponent: rowComponent,
                cellComponent: cellComponent
              });
            })
          )
        ),
        _(
          Table,
          {
            style: { minWidth: minWidth + 'px' }
          },
          _(ColumnGroup, { columns: columns }),
          _(
            Body,
            null,
            visibleBodyRows.map(function (visibleRow) {
              if (visibleRow.type === 'stub') {
                return _('tr', { key: visibleRow.key, style: { height: visibleRow.height + 'px' } });
              }
              var row = visibleRow.row;

              return _(RowLayout, {
                key: row.key,
                ref: function ref(_ref9) {
                  return _this4.registerRowRef(row, _ref9);
                },
                row: row,
                columns: columns,
                rowComponent: rowComponent,
                cellComponent: cellComponent
              });
            })
          )
        )
      );
    }
  }]);
  return VirtualTableLayout;
}(React.PureComponent);

VirtualTableLayout.propTypes = {
  minWidth: number.isRequired,
  height: number.isRequired,
  headerRows: array,
  rows: array.isRequired,
  columns: array.isRequired,
  cellComponent: func.isRequired,
  rowComponent: func.isRequired,
  bodyComponent: func.isRequired,
  headComponent: func,
  tableComponent: func.isRequired,
  headTableComponent: func,
  containerComponent: func.isRequired,
  estimatedRowHeight: number.isRequired
};

VirtualTableLayout.defaultProps = {
  headerRows: [],
  headComponent: function headComponent() {
    return null;
  },
  headTableComponent: function headTableComponent() {
    return null;
  }
};

var RowsBlockLayout = function (_React$PureComponent) {
  inherits(RowsBlockLayout, _React$PureComponent);

  function RowsBlockLayout() {
    classCallCheck(this, RowsBlockLayout);
    return possibleConstructorReturn(this, (RowsBlockLayout.__proto__ || Object.getPrototypeOf(RowsBlockLayout)).apply(this, arguments));
  }

  createClass(RowsBlockLayout, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          rows = _props.rows,
          columns = _props.columns,
          Block = _props.blockComponent,
          rowComponent = _props.rowComponent,
          cellComponent = _props.cellComponent;


      return _(
        Block,
        null,
        rows.map(function (row) {
          return _(RowLayout, {
            key: row.key,
            row: row,
            columns: columns,
            rowComponent: rowComponent,
            cellComponent: cellComponent
          });
        })
      );
    }
  }]);
  return RowsBlockLayout;
}(React.PureComponent);

RowsBlockLayout.propTypes = {
  rows: array.isRequired,
  columns: array.isRequired,
  blockComponent: func.isRequired,
  rowComponent: func.isRequired,
  cellComponent: func.isRequired
};

var StaticTableLayout = function (_React$PureComponent) {
  inherits(StaticTableLayout, _React$PureComponent);

  function StaticTableLayout() {
    classCallCheck(this, StaticTableLayout);
    return possibleConstructorReturn(this, (StaticTableLayout.__proto__ || Object.getPrototypeOf(StaticTableLayout)).apply(this, arguments));
  }

  createClass(StaticTableLayout, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          headerRows = _props.headerRows,
          rows = _props.rows,
          columns = _props.columns,
          minWidth = _props.minWidth,
          Container = _props.containerComponent,
          Table = _props.tableComponent,
          headComponent = _props.headComponent,
          bodyComponent = _props.bodyComponent,
          rowComponent = _props.rowComponent,
          cellComponent = _props.cellComponent;


      return _(
        Container,
        null,
        _(
          Table,
          {
            style: { minWidth: minWidth + 'px' }
          },
          _(ColumnGroup, { columns: columns }),
          !!headerRows.length && _(RowsBlockLayout, {
            rows: headerRows,
            columns: columns,
            blockComponent: headComponent,
            rowComponent: rowComponent,
            cellComponent: cellComponent
          }),
          _(RowsBlockLayout, {
            rows: rows,
            columns: columns,
            blockComponent: bodyComponent,
            rowComponent: rowComponent,
            cellComponent: cellComponent
          })
        )
      );
    }
  }]);
  return StaticTableLayout;
}(React.PureComponent);

StaticTableLayout.propTypes = {
  headerRows: array,
  rows: array.isRequired,
  columns: array.isRequired,
  minWidth: number.isRequired,
  containerComponent: func.isRequired,
  tableComponent: func.isRequired,
  headComponent: func,
  bodyComponent: func.isRequired,
  rowComponent: func.isRequired,
  cellComponent: func.isRequired
};

StaticTableLayout.defaultProps = {
  headerRows: [],
  headComponent: function headComponent() {
    return null;
  }
};

var ItemLayout = function (_React$PureComponent) {
  inherits(ItemLayout, _React$PureComponent);

  function ItemLayout(props) {
    classCallCheck(this, ItemLayout);

    var _this = possibleConstructorReturn(this, (ItemLayout.__proto__ || Object.getPrototypeOf(ItemLayout)).call(this, props));

    _this.state = {
      dragging: false
    };
    return _this;
  }

  createClass(ItemLayout, [{
    key: 'render',
    value: function render() {
      var _this2 = this;

      var _props = this.props,
          item = _props.item,
          Item = _props.itemComponent,
          draggingEnabled = _props.draggingEnabled,
          onDragStart = _props.onDragStart,
          onDragEnd = _props.onDragEnd;
      var dragging = this.state.dragging;


      var itemElement = _(Item, { item: _extends({}, item, { draft: dragging || item.draft }) });

      return draggingEnabled ? _(
        DragSource,
        {
          payload: [{ type: 'column', columnName: item.column.name }],
          onStart: function onStart() {
            _this2.setState({ dragging: true });
            onDragStart();
          },
          onEnd: function onEnd() {
            _this2.setState({ dragging: false });
            onDragEnd();
          }
        },
        itemElement
      ) : itemElement;
    }
  }]);
  return ItemLayout;
}(React.PureComponent);

ItemLayout.propTypes = {
  item: shape({
    column: object,
    draft: bool
  }).isRequired,
  itemComponent: func.isRequired,
  draggingEnabled: bool,
  onDragStart: func,
  onDragEnd: func
};

ItemLayout.defaultProps = {
  draggingEnabled: false,
  onDragStart: function onDragStart() {},
  onDragEnd: function onDragEnd() {}
};

var GroupPanelLayout = function (_React$PureComponent) {
  inherits(GroupPanelLayout, _React$PureComponent);

  function GroupPanelLayout(props) {
    classCallCheck(this, GroupPanelLayout);

    var _this = possibleConstructorReturn(this, (GroupPanelLayout.__proto__ || Object.getPrototypeOf(GroupPanelLayout)).call(this, props));

    _this.state = {
      sourceColumnName: null,
      targetItemIndex: -1
    };
    _this.handleDragEvent = function (eventHandler, _ref) {
      var payload = _ref.payload,
          restArgs = objectWithoutProperties(_ref, ['payload']);
      var isColumnGroupingEnabled = _this.props.isColumnGroupingEnabled;
      var columnName = payload[0].columnName;


      if (isColumnGroupingEnabled(columnName)) {
        eventHandler(_extends({ payload: payload }, restArgs));
      }
    };
    _this.onEnter = function (_ref2) {
      var payload = _ref2.payload;

      _this.setState({
        sourceColumnName: payload[0].columnName
      });
    };
    _this.onOver = function (_ref3) {
      var clientOffset = _ref3.clientOffset;
      var _this$props = _this.props,
          onGroupDraft = _this$props.onGroupDraft,
          items = _this$props.items;
      var _this$state = _this.state,
          sourceColumnName = _this$state.sourceColumnName,
          prevTargetItemIndex = _this$state.targetItemIndex;
      // eslint-disable-next-line react/no-find-dom-node

      var itemGeometries = _this.itemRefs.map(function (ref) {
        return findDOMNode(ref).getBoundingClientRect();
      });
      var sourceItemIndex = items.findIndex(function (_ref4) {
        var column = _ref4.column;
        return column.name === sourceColumnName;
      });
      var targetItemIndex = getGroupCellTargetIndex(itemGeometries, sourceItemIndex, clientOffset);

      if (prevTargetItemIndex === targetItemIndex) return;

      onGroupDraft({
        columnName: sourceColumnName,
        groupIndex: targetItemIndex
      });
      _this.setState({ targetItemIndex: targetItemIndex });
    };
    _this.onLeave = function () {
      var onGroupDraft = _this.props.onGroupDraft;
      var sourceColumnName = _this.state.sourceColumnName;

      if (!_this.draggingColumnName) {
        _this.resetState();
        return;
      }
      onGroupDraft({
        columnName: sourceColumnName,
        groupIndex: -1
      });
      _this.setState({
        targetItemIndex: -1
      });
    };
    _this.onDrop = function () {
      var onGroup = _this.props.onGroup;
      var _this$state2 = _this.state,
          sourceColumnName = _this$state2.sourceColumnName,
          targetItemIndex = _this$state2.targetItemIndex;

      _this.resetState();
      onGroup({
        columnName: sourceColumnName,
        groupIndex: targetItemIndex
      });
    };
    _this.onDragStart = function (columnName) {
      _this.draggingColumnName = columnName;
    };
    _this.onDragEnd = function () {
      _this.draggingColumnName = null;
      var _this$state3 = _this.state,
          sourceColumnName = _this$state3.sourceColumnName,
          targetItemIndex = _this$state3.targetItemIndex;
      var onGroup = _this.props.onGroup;

      if (sourceColumnName && targetItemIndex === -1) {
        onGroup({
          columnName: sourceColumnName
        });
      }
      _this.resetState();
    };
    return _this;
  }

  createClass(GroupPanelLayout, [{
    key: 'resetState',
    value: function resetState() {
      var onGroupDraftCancel = this.props.onGroupDraftCancel;

      onGroupDraftCancel();
      this.setState({
        sourceColumnName: null,
        targetItemIndex: -1
      });
    }
  }, {
    key: 'render',
    value: function render() {
      var _this2 = this;

      var _props = this.props,
          items = _props.items,
          EmptyMessage = _props.emptyMessageComponent,
          Container = _props.containerComponent,
          Item = _props.itemComponent,
          draggingEnabled = _props.draggingEnabled,
          isColumnGroupingEnabled = _props.isColumnGroupingEnabled;


      this.itemRefs = [];

      var groupPanel = items.length ? _(
        Container,
        null,
        items.map(function (item) {
          var columnName = item.column.name;

          return _(ItemLayout, {
            key: columnName,
            ref: function ref(element) {
              return element && _this2.itemRefs.push(element);
            },
            item: item,
            itemComponent: Item,
            draggingEnabled: draggingEnabled && isColumnGroupingEnabled(columnName),
            onDragStart: function onDragStart() {
              return _this2.onDragStart(columnName);
            },
            onDragEnd: _this2.onDragEnd
          });
        })
      ) : _(EmptyMessage, null);

      return draggingEnabled ? _(
        DropTarget,
        {
          onEnter: function onEnter(args) {
            return _this2.handleDragEvent(_this2.onEnter, args);
          },
          onOver: function onOver(args) {
            return _this2.handleDragEvent(_this2.onOver, args);
          },
          onLeave: function onLeave(args) {
            return _this2.handleDragEvent(_this2.onLeave, args);
          },
          onDrop: function onDrop(args) {
            return _this2.handleDragEvent(_this2.onDrop, args);
          }
        },
        groupPanel
      ) : groupPanel;
    }
  }]);
  return GroupPanelLayout;
}(React.PureComponent);

GroupPanelLayout.propTypes = {
  items: arrayOf(shape({
    column: object,
    draft: bool
  })).isRequired,
  onGroup: func,
  itemComponent: func.isRequired,
  containerComponent: func.isRequired,
  emptyMessageComponent: func.isRequired,
  draggingEnabled: bool,
  isColumnGroupingEnabled: func,
  onGroupDraft: func,
  onGroupDraftCancel: func
};

GroupPanelLayout.defaultProps = {
  onGroup: function onGroup() {},
  draggingEnabled: false,
  isColumnGroupingEnabled: function isColumnGroupingEnabled() {},
  onGroupDraft: function onGroupDraft() {},
  onGroupDraftCancel: function onGroupDraftCancel() {}
};

var Toolbar = function (_React$PureComponent) {
  inherits(Toolbar, _React$PureComponent);

  function Toolbar() {
    classCallCheck(this, Toolbar);
    return possibleConstructorReturn(this, (Toolbar.__proto__ || Object.getPrototypeOf(Toolbar)).apply(this, arguments));
  }

  createClass(Toolbar, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          Root = _props.rootComponent,
          FlexibleSpaceComponent = _props.flexibleSpaceComponent;

      return _(
        Plugin,
        {
          name: 'Toolbar'
        },
        _(
          Template,
          { name: 'header' },
          _(
            Root,
            null,
            _(TemplatePlaceholder, { name: 'toolbarContent' })
          ),
          _(TemplatePlaceholder, null)
        ),
        _(
          Template,
          { name: 'toolbarContent' },
          _(FlexibleSpaceComponent, null)
        )
      );
    }
  }]);
  return Toolbar;
}(React.PureComponent);

Toolbar.propTypes = {
  rootComponent: func.isRequired,
  flexibleSpaceComponent: func.isRequired
};

var SearchState = function (_React$PureComponent) {
  inherits(SearchState, _React$PureComponent);

  function SearchState(props) {
    classCallCheck(this, SearchState);

    var _this = possibleConstructorReturn(this, (SearchState.__proto__ || Object.getPrototypeOf(SearchState)).call(this, props));

    _this.state = {
      value: props.value || props.defaultValue
    };
    var stateHelper = createStateHelper(_this, {
      value: function value() {
        return _this.props.onValueChange;
      }
    });

    _this.changeValue = stateHelper.applyFieldReducer.bind(stateHelper, 'value', changeSearchValue);
    return _this;
  }

  createClass(SearchState, [{
    key: 'componentWillReceiveProps',
    value: function componentWillReceiveProps(nextProps) {
      var value = nextProps.value;

      this.setState(_extends({}, value !== undefined ? { value: value } : null));
    }
  }, {
    key: 'render',
    value: function render() {
      var value = this.state.value;


      return _(
        Plugin,
        {
          name: 'SearchState'
        },
        _(Getter, { name: 'filterExpression', computed: pushSearchFilterExpression(value) }),
        _(Getter, { name: 'searchValue', value: value }),
        _(Action, { name: 'changeSearchValue', action: this.changeValue })
      );
    }
  }]);
  return SearchState;
}(React.PureComponent);

SearchState.propTypes = {
  value: string,
  defaultValue: string,
  onValueChange: func
};

SearchState.defaultProps = {
  value: undefined,
  defaultValue: '',
  onValueChange: undefined
};

var pluginDependencies$18 = [{ name: 'Toolbar' }, { name: 'SearchState' }];

var SearchPanel = function (_React$PureComponent) {
  inherits(SearchPanel, _React$PureComponent);

  function SearchPanel() {
    classCallCheck(this, SearchPanel);
    return possibleConstructorReturn(this, (SearchPanel.__proto__ || Object.getPrototypeOf(SearchPanel)).apply(this, arguments));
  }

  createClass(SearchPanel, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          Input = _props.inputComponent,
          messages = _props.messages;

      var getMessage = getMessagesFormatter(messages);

      return _(
        Plugin,
        {
          name: 'SearchPanel',
          dependencies: pluginDependencies$18
        },
        _(
          Template,
          { name: 'toolbarContent' },
          _(TemplatePlaceholder, null),
          _(
            TemplateConnector,
            null,
            function (_ref, _ref2) {
              var searchValue = _ref.searchValue;
              var changeSearchValue$$1 = _ref2.changeSearchValue;
              return _(Input, {
                value: searchValue,
                onValueChange: changeSearchValue$$1,
                getMessage: getMessage
              });
            }
          )
        )
      );
    }
  }]);
  return SearchPanel;
}(React.PureComponent);

SearchPanel.propTypes = {
  inputComponent: func.isRequired,
  messages: object
};

SearchPanel.defaultProps = {
  messages: {}
};

return { Grid:Grid, ColumnChooser:ColumnChooser, FilteringState:FilteringState, IntegratedFiltering:IntegratedFiltering
	, EditingState:EditingState, PagingState:PagingState, IntegratedPaging:IntegratedPaging, CustomPaging:CustomPaging
	, GroupingState:GroupingState, IntegratedGrouping:IntegratedGrouping, CustomGrouping:CustomGrouping
	, SelectionState:SelectionState, IntegratedSelection:IntegratedSelection, SortingState:SortingState
	, IntegratedSorting:IntegratedSorting, DragDropProvider:DragDropProvider$1, TableColumnReordering:TableColumnReordering
	, Table:Table, TableSelection:TableSelection, RowDetailState:RowDetailState, TableRowDetail:TableRowDetail, TableGroupRow:TableGroupRow
	, TableHeaderRow:TableHeaderRow, TableFilterRow:TableFilterRow, TableEditRow:TableEditRow, TableEditColumn:TableEditColumn
	, TableColumnResizing:TableColumnResizing, PagingPanel:PagingPanel, GroupingPanel:GroupingPanel, DataTypeProvider:DataTypeProvider
	, TableColumnVisibility:TableColumnVisibility, TableLayout:TableLayout, VirtualTableLayout:VirtualTableLayout, StaticTableLayout:StaticTableLayout
	, GroupPanelLayout:GroupPanelLayout, Toolbar:Toolbar, SearchState:SearchState, SearchPanel:SearchPanel };
}();
//# sourceMappingURL=dx-react-grid.es.js.map
