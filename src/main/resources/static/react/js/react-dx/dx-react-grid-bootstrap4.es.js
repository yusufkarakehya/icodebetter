/**
 * Bundle of @devexpress/dx-react-grid-bootstrap4
 * Generated: 2018-03-20
 * Version: 1.1.1
 * License: https://js.devexpress.com/Licensing
 */

var DXReactGridBootstrap4=function(){
//import { React.Component, React.Fragment, React.PureComponent, createElement } from 'react';
//	import { any, array, arrayOf, bool, func, node, number, object, oneOf, oneOfType, shape, string } from 'prop-types';
var any=PropTypes.any, array=PropTypes.array, arrayOf=PropTypes.arrayOf, bool=PropTypes.bool, func=PropTypes.func, node=PropTypes.node
	, number=PropTypes.number, object=PropTypes.object, oneOf=PropTypes.oneOf, oneOfType=PropTypes.oneOfType, shape=PropTypes.shape, string=PropTypes.string;// } from 'prop-types';
//import { ColumnChooser, DragDropProvider, Grid, GroupPanelLayout, GroupingPanel, PagingPanel, SearchPanel, StaticTableLayout, Table, TableColumnReordering, TableColumnResizing, TableColumnVisibility, TableEditColumn, TableEditRow, TableFilterRow, TableGroupRow, TableHeaderRow, TableLayout, TableRowDetail, TableSelection, Toolbar, VirtualTableLayout } from '@devexpress/dx-react-grid';
var ColumnChooser=DXReactGrid.ColumnChooser, DragDropProvider=DXReactGrid.DragDropProvider, Grid=DXReactGrid.Grid, GroupPanelLayout=DXReactGrid.GroupPanelLayout
	, GroupingPanel=DXReactGrid.GroupingPanel, PagingPanel=DXReactGrid.PagingPanel, SearchPanel=DXReactGrid.SearchPanel, StaticTableLayout=DXReactGrid.StaticTableLayout
	, Table=DXReactGrid.Table, TableColumnReordering=DXReactGrid.TableColumnReordering, TableColumnResizing=DXReactGrid.TableColumnResizing
	, TableColumnVisibility=DXReactGrid.TableColumnVisibility, TableEditColumn=DXReactGrid.TableEditColumn, TableEditRow=DXReactGrid.TableEditRow, TableFilterRow=DXReactGrid.TableFilterRow
	, TableGroupRow=DXReactGrid.TableGroupRow, TableHeaderRow=DXReactGrid.TableHeaderRow, TableLayout=DXReactGrid.TableLayout, TableRowDetail=DXReactGrid.TableRowDetail
	, TableSelection=DXReactGrid.TableSelection, Toolbar=DXReactGrid.Toolbar, VirtualTableLayout=DXReactGrid.VirtualTableLayout;// } from '@devexpress/dx-react-grid';
//import { Pagination, PaginationItem, PaginationLink, Popover } from 'reactstrap';
//import classNames from 'classnames';
//import { firstRowOnPage, lastRowOnPage } from '@devexpress/dx-grid-core';
var firstRowOnPage=DXGridCore.firstRowOnPage, lastRowOnPage=DXGridCore.lastRowOnPage;// } from '@devexpress/dx-grid-core';
//import { DragSource, Draggable, DropTarget, createRenderComponent } from '@devexpress/dx-react-core';
var DragSource=DXReactCore.DragSource, Draggable=DXReactCore.Draggable, DropTarget=DXReactCore.DropTarget, createRenderComponent=DXReactCore.createRenderComponent;// } from '@devexpress/dx-react-core';

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

var Root = function Root(_ref) {
  var children = _ref.children,
      restProps = objectWithoutProperties(_ref, ['children']);
  return _(
    'div',
    restProps,
    children
  );
};

Root.propTypes = {
  children: oneOfType([node, arrayOf(node)])
};

Root.defaultProps = {
  children: undefined
};

var Grid$1 = function Grid$$1(_ref) {
  var children = _ref.children,
      props = objectWithoutProperties(_ref, ['children']);
  return _(
    Grid,
    _extends({
      rootComponent: Root
    }, props),
    children
  );
};

Grid$1.Root = Root;

Grid$1.propTypes = {
  children: oneOfType([arrayOf(node), node]).isRequired
};

var Overlay = function Overlay(_ref) {
  var visible = _ref.visible,
      children = _ref.children,
      toggle = _ref.toggle,
      target = _ref.target,
      onHide = _ref.onHide,
      restProps = objectWithoutProperties(_ref, ['visible', 'children', 'toggle', 'target', 'onHide']);
  return target ? _(
    Popover,
    _extends({
      placement: 'bottom',
      isOpen: visible,
      target: target,
      toggle: toggle,
      container: target ? target.parentElement : undefined
    }, restProps),
    children
  ) : null;
};

Overlay.propTypes = {
  children: node.isRequired,
  toggle: func.isRequired,
  visible: bool,
  target: object,
  onHide: func
};

Overlay.defaultProps = {
  visible: false,
  target: undefined,
  onHide: undefined
};

var Container = function Container(_ref) {
  var children = _ref.children,
      className = _ref.className,
      restProps = objectWithoutProperties(_ref, ['children', 'className']);
  return _(
    'div',
    _extends({
      className: classNames('py-2', className)
    }, restProps),
    children
  );
};

Container.propTypes = {
  children: node.isRequired,
  className: string
};

Container.defaultProps = {
  className: undefined
};

var handleMouseDown = function handleMouseDown(e) {
  e.currentTarget.style.outline = 'none';
};
var handleBlur = function handleBlur(e) {
  e.currentTarget.style.outline = '';
};

var Item = function Item(_ref) {
  var _ref$item = _ref.item,
      column = _ref$item.column,
      hidden = _ref$item.hidden,
      onToggle = _ref.onToggle,
      className = _ref.className,
      disabled = _ref.disabled,
      restProps = objectWithoutProperties(_ref, ['item', 'onToggle', 'className', 'disabled']);
  return _(
    'button',
    _extends({
      className: classNames({
        'dropdown-item dx-rg-bs4-column-chooser-item': true,
        'dx-rg-bs4-cursor-pointer': !disabled
      }, className),
      type: 'button',
      onClick: onToggle,
      onMouseDown: handleMouseDown,
      onBlur: handleBlur,
      disabled: disabled
    }, restProps),
    _('input', {
      type: 'checkbox',
      className: classNames({
        'dx-rg-bs4-cursor-pointer': !disabled,
        'dx-rg-bs4-column-chooser-checkbox': true
      }),
      tabIndex: -1,
      checked: !hidden,
      disabled: disabled,
      onChange: onToggle,
      onClick: function onClick(e) {
        return e.stopPropagation();
      }
    }),
    column.title || column.name
  );
};

Item.propTypes = {
  item: shape({
    column: shape({
      name: string
    }),
    hidden: bool
  }).isRequired,
  onToggle: func,
  className: string,
  disabled: bool
};

Item.defaultProps = {
  onToggle: function onToggle() {},
  className: undefined,
  disabled: false
};

var ToggleButton = function ToggleButton(_ref) {
  var onToggle = _ref.onToggle,
      className = _ref.className,
      getMessage = _ref.getMessage,
      buttonRef = _ref.buttonRef,
      active = _ref.active,
      restProps = objectWithoutProperties(_ref, ['onToggle', 'className', 'getMessage', 'buttonRef', 'active']);

  var buttonClasses = classNames({
    btn: true,
    'btn-outline-secondary': true,
    'border-0': true,
    active: active
  }, className);
  return _(
    'button',
    _extends({
      className: buttonClasses,
      onClick: onToggle,
      ref: buttonRef
    }, restProps),
    _('span', { className: 'oi oi-eye' })
  );
};

ToggleButton.propTypes = {
  onToggle: func.isRequired,
  getMessage: func.isRequired,
  buttonRef: func.isRequired,
  className: string,
  active: bool
};

ToggleButton.defaultProps = {
  className: undefined,
  active: false
};

var ColumnChooser$1 = function ColumnChooser$$1(props) {
  return _(ColumnChooser, _extends({
    overlayComponent: Overlay,
    containerComponent: Container,
    itemComponent: Item,
    toggleButtonComponent: ToggleButton
  }, props));
};

ColumnChooser$1.Container = Container;
ColumnChooser$1.Item = Item;
ColumnChooser$1.Overlay = Overlay;
ColumnChooser$1.ToggleButton = ToggleButton;

var Container$1 = function Container(_ref) {
  var clientOffset = _ref.clientOffset,
      style = _ref.style,
      className = _ref.className,
      children = _ref.children,
      restProps = objectWithoutProperties(_ref, ['clientOffset', 'style', 'className', 'children']);
  return _(
    'ul',
    _extends({
      className: classNames('list-group d-inline-block position-fixed dx-rg-bs4-drag-drop', className),
      style: _extends({
        transform: 'translate(calc(' + clientOffset.x + 'px - 50%), calc(' + clientOffset.y + 'px - 50%))',
        zIndex: 1000
      }, style)
    }, restProps),
    children
  );
};

Container$1.propTypes = {
  clientOffset: shape({
    x: number.isRequired,
    y: number.isRequired
  }).isRequired,
  style: object,
  className: string,
  children: oneOfType([node, arrayOf(node)])
};

Container$1.defaultProps = {
  style: {},
  className: undefined,
  children: undefined
};

var Column = function Column(_ref2) {
  var column = _ref2.column,
      className = _ref2.className,
      restProps = objectWithoutProperties(_ref2, ['column', 'className']);
  return _(
    'li',
    _extends({
      className: classNames('list-group-item', className)
    }, restProps),
    column.title
  );
};

Column.propTypes = {
  column: object.isRequired,
  className: string
};

Column.defaultProps = {
  className: undefined
};

var DragDropProvider$1 = function (_React$PureComponent) {
  inherits(DragDropProvider$$1, _React$PureComponent);

  function DragDropProvider$$1() {
    classCallCheck(this, DragDropProvider$$1);
    return possibleConstructorReturn(this, (DragDropProvider$$1.__proto__ || Object.getPrototypeOf(DragDropProvider$$1)).apply(this, arguments));
  }

  createClass(DragDropProvider$$1, [{
    key: 'render',
    value: function render() {
      return _(DragDropProvider, _extends({
        containerComponent: Container$1,
        columnComponent: Column
      }, this.props));
    }
  }]);
  return DragDropProvider$$1;
}(React.PureComponent);

DragDropProvider$1.Container = Container$1;
DragDropProvider$1.Column = Column;

var PageSizeSelector = function PageSizeSelector(_ref) {
  var pageSize = _ref.pageSize,
      onPageSizeChange = _ref.onPageSizeChange,
      pageSizes = _ref.pageSizes,
      getMessage = _ref.getMessage;

  var showAll = getMessage('showAll');
  return _(
    'div',
    { className: 'd-inline-block' },
    _(
      'select',
      {
        className: 'form-control d-sm-none',
        value: pageSize,
        onChange: function onChange(e) {
          return onPageSizeChange(parseInt(e.target.value, 10));
        }
      },
      pageSizes.map(function (val) {
        return _(
          'option',
          { key: val, value: val },
          val || showAll
        );
      })
    ),
    _(
      Pagination,
      { className: 'd-none d-sm-flex m-0' },
      pageSizes.map(function (item) {
        return _(
          PaginationItem,
          { key: item, active: item === pageSize && true },
          _(
            PaginationLink,
            {
              href: '#',
              onClick: function onClick(e) {
                e.preventDefault();
                onPageSizeChange(item);
              }
            },
            item || showAll
          )
        );
      })
    )
  );
};

PageSizeSelector.propTypes = {
  pageSize: number.isRequired,
  onPageSizeChange: func.isRequired,
  pageSizes: arrayOf(number).isRequired,
  getMessage: func.isRequired
};

var calculateStartPage = function calculateStartPage(currentPage, maxButtonCount, totalPageCount) {
  return Math.max(Math.min(currentPage - Math.floor(maxButtonCount / 2, 10), totalPageCount - maxButtonCount + 1), 1);
};

var renderPageButtons = function renderPageButtons(currentPage, totalPageCount, currentPageChange) {
  var pageButtons = [];
  var maxButtonCount = 3;
  var startPage = 1;
  var endPage = totalPageCount || 1;

  if (maxButtonCount < totalPageCount) {
    startPage = calculateStartPage(currentPage + 1, maxButtonCount, totalPageCount);
    endPage = startPage + maxButtonCount - 1;
  }
  if (startPage > 1) {
    pageButtons.push(_(
      PaginationItem,
      { key: 1 },
      _(
        PaginationLink,
        {
          href: '#',
          onClick: function onClick(e) {
            return currentPageChange(e, 0);
          }
        },
        1
      )
    ));

    if (startPage > 2) {
      pageButtons.push(_(
        PaginationItem,
        { key: 'ellipsisStart', disabled: true },
        _(
          PaginationLink,
          null,
          '...'
        )
      ));
    }
  }

  var _loop = function _loop(page) {
    pageButtons.push(_(
      PaginationItem,
      {
        key: page,
        active: page === currentPage + 1,
        disabled: startPage === endPage
      },
      _(
        PaginationLink,
        {
          href: '#',
          onClick: function onClick(e) {
            return currentPageChange(e, page - 1);
          }
        },
        page
      )
    ));
  };

  for (var page = startPage; page <= endPage; page += 1) {
    _loop(page);
  }

  if (endPage < totalPageCount) {
    if (endPage < totalPageCount - 1) {
      pageButtons.push(_(
        PaginationItem,
        { key: 'ellipsisEnd', disabled: true },
        _(
          PaginationLink,
          null,
          '...'
        )
      ));
    }

    pageButtons.push(_(
      PaginationItem,
      { key: totalPageCount },
      _(
        PaginationLink,
        {
          href: '#',
          onClick: function onClick(e) {
            return currentPageChange(e, totalPageCount - 1);
          }
        },
        totalPageCount
      )
    ));
  }

  return pageButtons;
};

var Pagination$1 = function Pagination$$1(_ref) {
  var totalPages = _ref.totalPages,
      currentPage = _ref.currentPage,
      onCurrentPageChange = _ref.onCurrentPageChange,
      totalCount = _ref.totalCount,
      pageSize = _ref.pageSize,
      getMessage = _ref.getMessage;

  var from = firstRowOnPage(currentPage, pageSize, totalCount);
  var to = lastRowOnPage(currentPage, pageSize, totalCount);
  var currentPageChange = function currentPageChange(e, nextPage) {
    e.preventDefault();
    onCurrentPageChange(nextPage);
  };
  return _(
    React.Fragment,
    null,
    _(
      Pagination,
      { className: 'float-right d-none d-sm-flex m-0' },
      _(
        PaginationItem,
        { disabled: currentPage === 0 },
        _(PaginationLink, {
          previous: true,
          href: '#',
          onClick: function onClick(e) {
            return currentPageChange(e, currentPage - 1);
          }
        })
      ),
      renderPageButtons(currentPage, totalPages, currentPageChange),
      _(
        PaginationItem,
        { disabled: currentPage === totalPages - 1 || totalCount === 0 },
        _(PaginationLink, {
          next: true,
          href: '#',
          onClick: function onClick(e) {
            return currentPageChange(e, currentPage + 1);
          }
        })
      )
    ),
    _(
      Pagination,
      { className: 'float-right d-sm-none m-0' },
      _(
        PaginationItem,
        { disabled: currentPage === 0 },
        _(PaginationLink, {
          previous: true,
          href: '#',
          onClick: function onClick(e) {
            return currentPageChange(e, currentPage - 1);
          }
        })
      ),
      '\xA0',
      _(
        PaginationItem,
        { disabled: currentPage === totalPages - 1 || totalCount === 0 },
        _(PaginationLink, {
          next: true,
          href: '#',
          onClick: function onClick(e) {
            return currentPageChange(e, currentPage + 1);
          }
        })
      )
    ),
    _(
      'span',
      { className: 'float-right d-sm-none mr-4' },
      _(
        'span',
        { className: 'd-inline-block align-middle' },
        getMessage('info', { from: from, to: to, count: totalCount })
      )
    )
  );
};

Pagination$1.propTypes = {
  totalPages: number.isRequired,
  currentPage: number.isRequired,
  onCurrentPageChange: func.isRequired,
  totalCount: number.isRequired,
  pageSize: number.isRequired,
  getMessage: func.isRequired
};

var Pager = function Pager(_ref) {
  var currentPage = _ref.currentPage,
      _onCurrentPageChange = _ref.onCurrentPageChange,
      totalPages = _ref.totalPages,
      pageSize = _ref.pageSize,
      onPageSizeChange = _ref.onPageSizeChange,
      pageSizes = _ref.pageSizes,
      totalCount = _ref.totalCount,
      getMessage = _ref.getMessage,
      className = _ref.className,
      restProps = objectWithoutProperties(_ref, ['currentPage', 'onCurrentPageChange', 'totalPages', 'pageSize', 'onPageSizeChange', 'pageSizes', 'totalCount', 'getMessage', 'className']);
  return _(
    'div',
    _extends({
      className: classNames('clearfix', 'card-footer', className)
    }, restProps),
    !!pageSizes.length && _(PageSizeSelector, {
      pageSize: pageSize,
      onPageSizeChange: onPageSizeChange,
      pageSizes: pageSizes,
      getMessage: getMessage
    }),
    _(Pagination$1, {
      totalPages: totalPages,
      totalCount: totalCount,
      currentPage: currentPage,
      onCurrentPageChange: function onCurrentPageChange(page) {
        return _onCurrentPageChange(page);
      },
      pageSize: pageSize,
      getMessage: getMessage
    })
  );
};

Pager.propTypes = {
  currentPage: number.isRequired,
  onCurrentPageChange: func.isRequired,
  totalPages: number.isRequired,
  pageSize: number.isRequired,
  onPageSizeChange: func.isRequired,
  pageSizes: arrayOf(number).isRequired,
  totalCount: number.isRequired,
  getMessage: func.isRequired,
  className: string
};

Pager.defaultProps = {
  className: undefined
};

var defaultMessages = {
  showAll: 'All',
  info: function info(_ref) {
    var from = _ref.from,
        to = _ref.to,
        count = _ref.count;
    return '' + from + (from < to ? '-' + to : '') + ' of ' + count;
  }
};

var PagingPanel$1 = function (_React$PureComponent) {
  inherits(PagingPanel$$1, _React$PureComponent);

  function PagingPanel$$1() {
    classCallCheck(this, PagingPanel$$1);
    return possibleConstructorReturn(this, (PagingPanel$$1.__proto__ || Object.getPrototypeOf(PagingPanel$$1)).apply(this, arguments));
  }

  createClass(PagingPanel$$1, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          messages = _props.messages,
          restProps = objectWithoutProperties(_props, ['messages']);


      return _(PagingPanel, _extends({
        containerComponent: Pager,
        messages: _extends({}, defaultMessages, messages)
      }, restProps));
    }
  }]);
  return PagingPanel$$1;
}(React.PureComponent);

PagingPanel$1.Container = Pager;

PagingPanel$1.propTypes = {
  messages: shape({
    showAll: string,
    info: oneOfType([string, func])
  })
};

PagingPanel$1.defaultProps = {
  messages: {}
};

var GroupPanelContainer = function GroupPanelContainer(_ref) {
  var children = _ref.children,
      className = _ref.className,
      restProps = objectWithoutProperties(_ref, ['children', 'className']);
  return _(
    'div',
    _extends({
      className: classNames('w-100 mt-1', className)
    }, restProps),
    children
  );
};

GroupPanelContainer.propTypes = {
  children: oneOfType([arrayOf(node), node]),
  className: string
};

GroupPanelContainer.defaultProps = {
  children: null,
  className: undefined
};

var SortingIndicator = function SortingIndicator(_ref) {
  var direction = _ref.direction;
  return _('span', {
    className: classNames({
      'oi dx-rg-bs4-sorting-indicator': true,
      'oi-arrow-thick-bottom': direction === 'desc',
      'oi-arrow-thick-top': direction !== 'desc',
      invisible: !direction
    })
  });
};

SortingIndicator.propTypes = {
  direction: oneOf(['asc', 'desc'])
};

SortingIndicator.defaultProps = {
  direction: null
};

var ENTER_KEY_CODE = 13;
var SPACE_KEY_CODE = 32;

var isActionKey = function isActionKey(keyCode) {
  return keyCode === ENTER_KEY_CODE || keyCode === SPACE_KEY_CODE;
};

var GroupPanelItem = function GroupPanelItem(_ref) {
  var _ref$item = _ref.item,
      column = _ref$item.column,
      draft = _ref$item.draft,
      onGroup = _ref.onGroup,
      showGroupingControls = _ref.showGroupingControls,
      showSortingControls = _ref.showSortingControls,
      sortingDirection = _ref.sortingDirection,
      onSort = _ref.onSort,
      className = _ref.className,
      groupingEnabled = _ref.groupingEnabled,
      sortingEnabled = _ref.sortingEnabled,
      restProps = objectWithoutProperties(_ref, ['item', 'onGroup', 'showGroupingControls', 'showSortingControls', 'sortingDirection', 'onSort', 'className', 'groupingEnabled', 'sortingEnabled']);

  var handleSortingChange = function handleSortingChange(e) {
    var isActionKeyDown = isActionKey(e.keyCode);
    var isMouseClick = e.keyCode === undefined;

    if (!showSortingControls || !sortingEnabled || !(isActionKeyDown || isMouseClick)) return;

    var cancelSortingRelatedKey = e.metaKey || e.ctrlKey;
    var direction = (isMouseClick || isActionKeyDown) && cancelSortingRelatedKey ? null : undefined;

    e.preventDefault();
    onSort({
      direction: direction,
      keepOther: cancelSortingRelatedKey
    });
  };
  var handleUngroup = function handleUngroup(e) {
    if (!groupingEnabled) return;
    var isActionKeyDown = isActionKey(e.keyCode);
    var isMouseClick = e.keyCode === undefined;

    if (!isActionKeyDown && !isMouseClick) return;
    onGroup();
  };
  return _(
    'div',
    _extends({
      className: classNames({
        'btn-group mb-1 mr-1': true,
        'dx-rg-bs4-inactive': draft
      }, className)
    }, restProps),
    _(
      'span',
      _extends({
        className: classNames({
          'btn btn-outline-secondary': true,
          disabled: !sortingEnabled && showSortingControls
        }),
        onClick: handleSortingChange,
        onKeyDown: handleSortingChange
      }, sortingEnabled ? { tabIndex: 0 } : null),
      column.title || column.name,
      showSortingControls && sortingDirection && _(
        'span',
        null,
        '\xA0',
        _(SortingIndicator, {
          direction: sortingDirection
        })
      )
    ),
    showGroupingControls && _(
      'span',
      {
        className: classNames({
          'btn btn-outline-secondary': true,
          disabled: !groupingEnabled
        }),
        onClick: handleUngroup
      },
      '\xA0',
      _('span', {
        className: 'oi oi-x dx-rg-bs4-group-panel-item-icon'
      })
    )
  );
};

GroupPanelItem.propTypes = {
  item: shape({
    column: shape({
      title: string
    }).isRequired,
    draft: bool
  }).isRequired,
  showSortingControls: bool,
  sortingDirection: oneOf(['asc', 'desc', null]),
  className: string,
  onSort: func,
  onGroup: func,
  showGroupingControls: bool,
  groupingEnabled: bool,
  sortingEnabled: bool
};

GroupPanelItem.defaultProps = {
  showSortingControls: false,
  sortingDirection: undefined,
  className: undefined,
  onSort: undefined,
  onGroup: undefined,
  showGroupingControls: false,
  sortingEnabled: false,
  groupingEnabled: false
};

var GroupPanelEmptyMessage = function GroupPanelEmptyMessage(_ref) {
  var getMessage = _ref.getMessage,
      className = _ref.className,
      restProps = objectWithoutProperties(_ref, ['getMessage', 'className']);
  return _(
    'div',
    _extends({
      className: classNames('dx-rg-bs4-group-panel-empty-message', className)
    }, restProps),
    getMessage('groupByColumn')
  );
};

GroupPanelEmptyMessage.propTypes = {
  getMessage: func.isRequired,
  className: string
};

GroupPanelEmptyMessage.defaultProps = {
  className: undefined
};

var defaultMessages$1 = {
  groupByColumn: 'Drag a column header here to group by that column'
};

var GroupingPanel$1 = function (_React$PureComponent) {
  inherits(GroupingPanel$$1, _React$PureComponent);

  function GroupingPanel$$1() {
    classCallCheck(this, GroupingPanel$$1);
    return possibleConstructorReturn(this, (GroupingPanel$$1.__proto__ || Object.getPrototypeOf(GroupingPanel$$1)).apply(this, arguments));
  }

  createClass(GroupingPanel$$1, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          messages = _props.messages,
          restProps = objectWithoutProperties(_props, ['messages']);


      return _(GroupingPanel, _extends({
        layoutComponent: GroupPanelLayout,
        containerComponent: GroupPanelContainer,
        itemComponent: GroupPanelItem,
        emptyMessageComponent: GroupPanelEmptyMessage,
        messages: _extends({}, defaultMessages$1, messages)
      }, restProps));
    }
  }]);
  return GroupingPanel$$1;
}(React.PureComponent);

GroupingPanel$1.Container = GroupPanelContainer;
GroupingPanel$1.Item = GroupPanelItem;
GroupingPanel$1.EmptyMessage = GroupPanelEmptyMessage;

GroupingPanel$1.propTypes = {
  showSortingControls: bool,
  messages: shape({
    groupByColumn: string
  })
};

GroupingPanel$1.defaultProps = {
  showSortingControls: false,
  messages: {}
};

var ENTER_KEY_CODE$1 = 13;
var SPACE_KEY_CODE$1 = 32;

var handleMouseDown$1 = function handleMouseDown(e) {
  e.target.style.outline = 'none';
};
var handleBlur$1 = function handleBlur(e) {
  e.target.style.outline = '';
};

var TableDetailToggleCell = function TableDetailToggleCell(_ref) {
  var expanded = _ref.expanded,
      onToggle = _ref.onToggle,
      className = _ref.className,
      tableColumn = _ref.tableColumn,
      tableRow = _ref.tableRow,
      row = _ref.row,
      restProps = objectWithoutProperties(_ref, ['expanded', 'onToggle', 'className', 'tableColumn', 'tableRow', 'row']);

  var handleKeyDown = function handleKeyDown(e) {
    if (e.keyCode === ENTER_KEY_CODE$1 || e.keyCode === SPACE_KEY_CODE$1) {
      e.preventDefault();
      onToggle();
    }
  };
  return _(
    'td',
    _extends({
      className: classNames('align-middle dx-rg-bs4-cursor-pointer', className),
      onClick: function onClick(e) {
        e.stopPropagation();
        onToggle();
      }
    }, restProps),
    _('span', {
      className: classNames({
        'oi d-block dx-rg-bs4-table-detail-toggle-cell-icon': true,
        'oi-chevron-bottom': expanded,
        'oi-chevron-right': !expanded
      }),
      tabIndex: 0 // eslint-disable-line jsx-a11y/no-noninteractive-tabindex
      , onKeyDown: handleKeyDown,
      onMouseDown: handleMouseDown$1,
      onBlur: handleBlur$1
    })
  );
};

TableDetailToggleCell.propTypes = {
  expanded: bool,
  onToggle: func,
  tableColumn: object,
  tableRow: object,
  row: object,
  className: string
};

TableDetailToggleCell.defaultProps = {
  expanded: false,
  onToggle: function onToggle() {},
  tableColumn: undefined,
  tableRow: undefined,
  row: undefined,
  className: undefined
};

var TableDetailCell = function TableDetailCell(_ref) {
  var colSpan = _ref.colSpan,
      children = _ref.children,
      className = _ref.className,
      tableColumn = _ref.tableColumn,
      tableRow = _ref.tableRow,
      row = _ref.row,
      restProps = objectWithoutProperties(_ref, ['colSpan', 'children', 'className', 'tableColumn', 'tableRow', 'row']);
  return _(
    'td',
    _extends({
      colSpan: colSpan,
      className: classNames('table-active', className)
    }, restProps),
    children
  );
};

TableDetailCell.propTypes = {
  style: object,
  colSpan: number,
  children: oneOfType([arrayOf(node), node]),
  className: string,
  tableColumn: object,
  tableRow: object,
  row: object
};

TableDetailCell.defaultProps = {
  style: null,
  colSpan: 1,
  className: undefined,
  tableColumn: undefined,
  tableRow: undefined,
  row: undefined,
  children: undefined
};

var TableRow = function TableRow(_ref) {
  var children = _ref.children,
      row = _ref.row,
      tableRow = _ref.tableRow,
      tableColumn = _ref.tableColumn,
      restProps = objectWithoutProperties(_ref, ['children', 'row', 'tableRow', 'tableColumn']);
  return _(
    'tr',
    restProps,
    children
  );
};

TableRow.propTypes = {
  children: node,
  row: object,
  tableColumn: object,
  tableRow: object
};

TableRow.defaultProps = {
  children: null,
  row: undefined,
  tableColumn: undefined,
  tableRow: undefined
};

var TableRowDetail$1 = function (_React$PureComponent) {
  inherits(TableRowDetail$$1, _React$PureComponent);

  function TableRowDetail$$1() {
    classCallCheck(this, TableRowDetail$$1);
    return possibleConstructorReturn(this, (TableRowDetail$$1.__proto__ || Object.getPrototypeOf(TableRowDetail$$1)).apply(this, arguments));
  }

  createClass(TableRowDetail$$1, [{
    key: 'render',
    value: function render() {
      return _(TableRowDetail, _extends({
        toggleCellComponent: TableDetailToggleCell,
        cellComponent: TableDetailCell,
        rowComponent: TableRow,
        toggleColumnWidth: 25
      }, this.props));
    }
  }]);
  return TableRowDetail$$1;
}(React.PureComponent);

TableRowDetail$1.Cell = TableDetailCell;
TableRowDetail$1.ToggleCell = TableDetailToggleCell;
TableRowDetail$1.Row = TableRow;

var ENTER_KEY_CODE$2 = 13;
var SPACE_KEY_CODE$2 = 32;

var handleMouseDown$2 = function handleMouseDown(e) {
  e.target.style.outline = 'none';
};
var handleBlur$2 = function handleBlur(e) {
  e.target.style.outline = '';
};

var TableGroupCell = function TableGroupCell(_ref) {
  var className = _ref.className,
      colSpan = _ref.colSpan,
      row = _ref.row,
      column = _ref.column,
      expanded = _ref.expanded,
      onToggle = _ref.onToggle,
      children = _ref.children,
      tableRow = _ref.tableRow,
      tableColumn = _ref.tableColumn,
      restProps = objectWithoutProperties(_ref, ['className', 'colSpan', 'row', 'column', 'expanded', 'onToggle', 'children', 'tableRow', 'tableColumn']);

  var handleClick = function handleClick() {
    return onToggle();
  };
  var handleKeyDown = function handleKeyDown(e) {
    var keyCode = e.keyCode;

    if (keyCode === ENTER_KEY_CODE$2 || keyCode === SPACE_KEY_CODE$2) {
      e.preventDefault();
      onToggle();
    }
  };

  return _(
    'td',
    _extends({
      colSpan: colSpan,
      className: classNames('dx-rg-bs4-cursor-pointer', className),
      onClick: handleClick
    }, restProps),
    _('span', {
      className: classNames({
        'oi dx-rg-bs4-table-group-row-cell': true,
        'oi-chevron-bottom mr-2': expanded,
        'oi-chevron-right': !expanded
      }),
      tabIndex: 0 // eslint-disable-line jsx-a11y/no-noninteractive-tabindex
      , onMouseDown: handleMouseDown$2,
      onBlur: handleBlur$2,
      onKeyDown: handleKeyDown
    }),
    _(
      'strong',
      null,
      column.title || column.name,
      ': '
    ),
    children || row.value
  );
};

TableGroupCell.propTypes = {
  className: string,
  colSpan: number,
  row: object,
  column: object,
  expanded: bool,
  onToggle: func,
  children: oneOfType([node, arrayOf(node)]),
  tableRow: object,
  tableColumn: object
};

TableGroupCell.defaultProps = {
  className: undefined,
  colSpan: 1,
  row: {},
  column: {},
  expanded: false,
  onToggle: function onToggle() {},
  children: undefined,
  tableRow: undefined,
  tableColumn: undefined
};

var TableGroupRow$1 = function (_React$PureComponent) {
  inherits(TableGroupRow$$1, _React$PureComponent);

  function TableGroupRow$$1() {
    classCallCheck(this, TableGroupRow$$1);
    return possibleConstructorReturn(this, (TableGroupRow$$1.__proto__ || Object.getPrototypeOf(TableGroupRow$$1)).apply(this, arguments));
  }

  createClass(TableGroupRow$$1, [{
    key: 'render',
    value: function render() {
      return _(TableGroupRow, _extends({
        cellComponent: TableGroupCell,
        rowComponent: TableRow,
        indentColumnWidth: 20
      }, this.props));
    }
  }]);
  return TableGroupRow$$1;
}(React.PureComponent);

TableGroupRow$1.Row = TableRow;
TableGroupRow$1.Cell = TableGroupCell;

var TableSelectAllCell = function TableSelectAllCell(_ref) {
  var allSelected = _ref.allSelected,
      someSelected = _ref.someSelected,
      disabled = _ref.disabled,
      onToggle = _ref.onToggle,
      tableColumn = _ref.tableColumn,
      tableRow = _ref.tableRow,
      className = _ref.className,
      restProps = objectWithoutProperties(_ref, ['allSelected', 'someSelected', 'disabled', 'onToggle', 'tableColumn', 'tableRow', 'className']);

  var toggle = function toggle(e) {
    if (disabled) return;

    e.stopPropagation();
    onToggle();
  };

  return _(
    'th',
    _extends({
      className: classNames({
        'align-middle': true,
        'dx-rg-bs4-cursor-pointer': !disabled
      }, className),
      onClick: toggle
    }, restProps),
    _('input', {
      className: classNames({
        'd-block m-auto': true,
        'dx-rg-bs4-cursor-pointer': !disabled
      }),
      type: 'checkbox',
      disabled: disabled,
      checked: allSelected,
      ref: function ref(_ref2) {
        if (_ref2) {
          var checkbox = _ref2;
          checkbox.indeterminate = someSelected;
        }
      },
      onChange: toggle,
      onClick: function onClick(e) {
        return e.stopPropagation();
      }
    })
  );
};

TableSelectAllCell.propTypes = {
  className: string,
  allSelected: bool,
  someSelected: bool,
  disabled: bool,
  onToggle: func,
  tableRow: object,
  tableColumn: object
};

TableSelectAllCell.defaultProps = {
  className: undefined,
  allSelected: false,
  someSelected: false,
  disabled: false,
  onToggle: function onToggle() {},
  tableRow: undefined,
  tableColumn: undefined
};

var TableSelectCell = function TableSelectCell(_ref) {
  var className = _ref.className,
      selected = _ref.selected,
      onToggle = _ref.onToggle,
      row = _ref.row,
      tableRow = _ref.tableRow,
      tableColumn = _ref.tableColumn,
      restProps = objectWithoutProperties(_ref, ['className', 'selected', 'onToggle', 'row', 'tableRow', 'tableColumn']);
  return _(
    'td',
    _extends({
      className: classNames('dx-rg-bs4-cursor-pointer align-middle', className),
      onClick: function onClick(e) {
        e.stopPropagation();
        onToggle();
      }
    }, restProps),
    _('input', {
      className: 'd-block m-auto dx-rg-bs4-cursor-pointer',
      type: 'checkbox',
      checked: selected,
      onChange: onToggle,
      onClick: function onClick(e) {
        return e.stopPropagation();
      }
    })
  );
};

TableSelectCell.propTypes = {
  className: string,
  selected: bool,
  onToggle: func,
  row: object,
  tableRow: object,
  tableColumn: object
};

TableSelectCell.defaultProps = {
  className: undefined,
  selected: false,
  onToggle: function onToggle() {},
  row: undefined,
  tableRow: undefined,
  tableColumn: undefined
};

var TableSelectRow = function TableSelectRow(_ref) {
  var selected = _ref.selected,
      children = _ref.children,
      style = _ref.style,
      onToggle = _ref.onToggle,
      selectByRowClick = _ref.selectByRowClick;
  return _(
    'tr',
    {
      style: style,
      className: selected ? 'table-active' : '',
      onClick: function onClick(e) {
        if (!selectByRowClick) return;
        e.stopPropagation();
        onToggle();
      }
    },
    children
  );
};

TableSelectRow.propTypes = {
  selected: bool,
  children: node,
  onToggle: func,
  selectByRowClick: bool,
  style: object
};

TableSelectRow.defaultProps = {
  children: null,
  onToggle: function onToggle() {},
  selected: false,
  selectByRowClick: false,
  style: null
};

var TableSelection$1 = function (_React$PureComponent) {
  inherits(TableSelection$$1, _React$PureComponent);

  function TableSelection$$1() {
    classCallCheck(this, TableSelection$$1);
    return possibleConstructorReturn(this, (TableSelection$$1.__proto__ || Object.getPrototypeOf(TableSelection$$1)).apply(this, arguments));
  }

  createClass(TableSelection$$1, [{
    key: 'render',
    value: function render() {
      return _(TableSelection, _extends({
        rowComponent: TableSelectRow,
        cellComponent: TableSelectCell,
        headerCellComponent: TableSelectAllCell,
        selectionColumnWidth: 40
      }, this.props));
    }
  }]);
  return TableSelection$$1;
}(React.PureComponent);

TableSelection$1.Cell = TableSelectCell;
TableSelection$1.HeaderCell = TableSelectAllCell;

var MINIMAL_COLUMN_WIDTH = 120;

var TableLayout$1 = function TableLayout$$1(_ref) {
  var headerRows = _ref.headerRows,
      bodyRows = _ref.bodyRows,
      columns = _ref.columns,
      cellComponent = _ref.cellComponent,
      rowComponent = _ref.rowComponent,
      tableComponent = _ref.tableComponent,
      headComponent = _ref.headComponent,
      bodyComponent = _ref.bodyComponent,
      containerComponent = _ref.containerComponent;
  return _(TableLayout, {
    layoutComponent: StaticTableLayout,
    headerRows: headerRows,
    rows: bodyRows,
    columns: columns,
    minColumnWidth: MINIMAL_COLUMN_WIDTH,
    containerComponent: containerComponent,
    tableComponent: tableComponent,
    headComponent: headComponent,
    bodyComponent: bodyComponent,
    rowComponent: rowComponent,
    cellComponent: cellComponent
  });
};

TableLayout$1.propTypes = {
  headerRows: array.isRequired,
  bodyRows: array.isRequired,
  columns: array.isRequired,
  cellComponent: func.isRequired,
  rowComponent: func.isRequired,
  tableComponent: func.isRequired,
  headComponent: func.isRequired,
  bodyComponent: func.isRequired,
  containerComponent: func.isRequired
};

var TableCell = function TableCell(_ref) {
  var column = _ref.column,
      value = _ref.value,
      children = _ref.children,
      tableRow = _ref.tableRow,
      tableColumn = _ref.tableColumn,
      row = _ref.row,
      className = _ref.className,
      restProps = objectWithoutProperties(_ref, ['column', 'value', 'children', 'tableRow', 'tableColumn', 'row', 'className']);
  return _(
    'td',
    _extends({
      className: classNames({
        'text-nowrap dx-rg-bs4-table-cell': true,
        'text-right': tableColumn && tableColumn.align === 'right',
        'text-center': tableColumn && tableColumn.align === 'center'
      }, className)
    }, restProps),
    children || value
  );
};

TableCell.propTypes = {
  value: any,
  column: object,
  row: object,
  children: oneOfType([node, arrayOf(node)]),
  tableRow: object,
  tableColumn: object,
  className: string
};

TableCell.defaultProps = {
  value: undefined,
  column: undefined,
  row: undefined,
  children: undefined,
  tableRow: undefined,
  tableColumn: undefined,
  className: undefined
};

var TableStubCell = function TableStubCell(_ref) {
  var className = _ref.className,
      tableRow = _ref.tableRow,
      tableColumn = _ref.tableColumn,
      restProps = objectWithoutProperties(_ref, ['className', 'tableRow', 'tableColumn']);
  return _('td', _extends({
    className: classNames('p-0', className)
  }, restProps));
};

TableStubCell.propTypes = {
  className: string,
  tableRow: object,
  tableColumn: object
};

TableStubCell.defaultProps = {
  className: undefined,
  tableRow: undefined,
  tableColumn: undefined
};

var TableStubHeaderCell = function TableStubHeaderCell(_ref) {
  var className = _ref.className,
      tableRow = _ref.tableRow,
      tableColumn = _ref.tableColumn,
      restProps = objectWithoutProperties(_ref, ['className', 'tableRow', 'tableColumn']);
  return _('th', _extends({
    className: classNames('p-0', className)
  }, restProps));
};

TableStubHeaderCell.propTypes = {
  className: string,
  tableRow: object,
  tableColumn: object
};

TableStubHeaderCell.defaultProps = {
  className: undefined,
  tableRow: undefined,
  tableColumn: undefined
};

var TableNoDataCell = function TableNoDataCell(_ref) {
  var className = _ref.className,
      colSpan = _ref.colSpan,
      getMessage = _ref.getMessage,
      tableRow = _ref.tableRow,
      tableColumn = _ref.tableColumn,
      restProps = objectWithoutProperties(_ref, ['className', 'colSpan', 'getMessage', 'tableRow', 'tableColumn']);
  return _(
    'td',
    _extends({
      className: classNames('py-5 text-center', className),
      colSpan: colSpan
    }, restProps),
    _(
      'big',
      { className: 'text-muted' },
      getMessage('noData')
    )
  );
};

TableNoDataCell.propTypes = {
  colSpan: number,
  getMessage: func.isRequired,
  tableRow: object,
  tableColumn: object,
  className: string
};

TableNoDataCell.defaultProps = {
  className: undefined,
  colSpan: 1,
  tableRow: undefined,
  tableColumn: undefined
};

/* globals document:true window:true */

var globalStickyProp = void 0;
var testCSSProp = function testCSSProp(property, value, noPrefixes) {
  var prop = property + ':';
  var el = document.createElement('test');
  var mStyle = el.style;

  if (!noPrefixes) {
    mStyle.cssText = prop + ['-webkit-', '-moz-', '-ms-', '-o-', ''].join(value + ';' + prop) + value + ';';
  } else {
    mStyle.cssText = prop + value;
  }
  return mStyle[property];
};

var Table$2 = function (_React$Component) {
  inherits(Table$$1, _React$Component);

  function Table$$1() {
    classCallCheck(this, Table$$1);

    var _this = possibleConstructorReturn(this, (Table$$1.__proto__ || Object.getPrototypeOf(Table$$1)).call(this));

    _this.state = {
      stickyProp: globalStickyProp,
      backgroundColor: 'white'
    };
    return _this;
  }

  createClass(Table$$1, [{
    key: 'componentDidMount',
    value: function componentDidMount() {
      this.checkStyles();
    }
  }, {
    key: 'checkStyles',
    value: function checkStyles() {
      globalStickyProp = testCSSProp('position', 'sticky');

      var body = document.getElementsByTagName('body')[0];

      var _window$getComputedSt = window.getComputedStyle(body),
          backgroundColor = _window$getComputedSt.backgroundColor;

      if (this.state.backgroundColor !== backgroundColor || this.state.stickyProp !== globalStickyProp) {
        this.setState({ stickyProp: globalStickyProp, backgroundColor: backgroundColor });
      }
    }
  }, {
    key: 'render',
    value: function render() {
      var _this2 = this;

      var _props = this.props,
          children = _props.children,
          use = _props.use,
          style = _props.style,
          className = _props.className,
          restProps = objectWithoutProperties(_props, ['children', 'use', 'style', 'className']);
      var _state = this.state,
          stickyProp = _state.stickyProp,
          backgroundColor = _state.backgroundColor;

      return _(
        'table',
        _extends({
          ref: function ref(node$$1) {
            _this2.node = node$$1;
          },
          className: classNames({
            'table mb-0 dx-rg-bs4-overflow-hidden dx-rg-bs4-table': true,
            'dx-rg-bs4-table-head': use === 'head'
          }, className)
        }, restProps, {
          style: _extends({}, style, use === 'head' ? {
            position: stickyProp,
            backgroundColor: backgroundColor
          } : null)
        }),
        children
      );
    }
  }]);
  return Table$$1;
}(React.Component);

Table$2.propTypes = {
  use: oneOf(['head']),
  children: oneOfType([arrayOf(node), node]).isRequired,
  style: object,
  className: string
};

Table$2.defaultProps = {
  className: undefined,
  use: undefined,
  style: null
};

var TableContainer = function TableContainer(_ref) {
  var children = _ref.children,
      className = _ref.className,
      restProps = objectWithoutProperties(_ref, ['children', 'className']);
  return _(
    'div',
    _extends({
      className: classNames('table-responsive dx-rg-bs4-table-container', className)
    }, restProps),
    children
  );
};

TableContainer.propTypes = {
  children: oneOfType([arrayOf(node), node]).isRequired,
  className: string
};

TableContainer.defaultProps = {
  className: undefined
};

var TableHead = function TableHead(props) {
  return _('thead', props);
};
var TableBody = function TableBody(props) {
  return _('tbody', props);
};

var defaultMessages$2 = {
  noData: 'No data'
};

var Table$1 = function (_React$PureComponent) {
  inherits(Table$$1, _React$PureComponent);

  function Table$$1() {
    classCallCheck(this, Table$$1);
    return possibleConstructorReturn(this, (Table$$1.__proto__ || Object.getPrototypeOf(Table$$1)).apply(this, arguments));
  }

  createClass(Table$$1, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          messages = _props.messages,
          restProps = objectWithoutProperties(_props, ['messages']);


      return _(Table, _extends({
        tableComponent: Table$2,
        headComponent: TableHead,
        bodyComponent: TableBody,
        containerComponent: TableContainer,
        layoutComponent: TableLayout$1,
        rowComponent: TableRow,
        cellComponent: TableCell,
        noDataRowComponent: TableRow,
        noDataCellComponent: TableNoDataCell,
        stubCellComponent: TableStubCell,
        stubHeaderCellComponent: TableStubHeaderCell,
        messages: _extends({}, defaultMessages$2, messages)
      }, restProps));
    }
  }]);
  return Table$$1;
}(React.PureComponent);

Table$1.Cell = TableCell;
Table$1.Row = TableRow;
Table$1.NoDataCell = TableNoDataCell;
Table$1.NoDataRow = TableRow;
Table$1.StubCell = TableStubCell;
Table$1.StubHeaderCell = TableStubCell;
Table$1.Table = Table$2;
Table$1.TableHead = TableHead;
Table$1.TableBody = TableBody;
Table$1.Container = TableContainer;

Table$1.propTypes = {
  messages: shape({
    noData: string
  })
};

Table$1.defaultProps = {
  messages: {}
};

var MINIMAL_COLUMN_WIDTH$1 = 120;

var VirtualTableLayout$1 = function VirtualTableLayout$$1(_ref) {
  var headerRows = _ref.headerRows,
      bodyRows = _ref.bodyRows,
      columns = _ref.columns,
      cellComponent = _ref.cellComponent,
      rowComponent = _ref.rowComponent,
      height = _ref.height,
      estimatedRowHeight = _ref.estimatedRowHeight,
      containerComponent = _ref.containerComponent,
      tableComponent = _ref.tableComponent,
      headComponent = _ref.headComponent,
      bodyComponent = _ref.bodyComponent,
      headTableComponent = _ref.headTableComponent;
  return _(TableLayout, {
    layoutComponent: VirtualTableLayout,
    headerRows: headerRows,
    rows: bodyRows,
    columns: columns,
    cellComponent: cellComponent,
    rowComponent: rowComponent,
    tableComponent: tableComponent,
    headComponent: headComponent,
    bodyComponent: bodyComponent,
    headTableComponent: headTableComponent,
    containerComponent: containerComponent,
    estimatedRowHeight: estimatedRowHeight,
    minColumnWidth: MINIMAL_COLUMN_WIDTH$1,
    height: height
  });
};

VirtualTableLayout$1.propTypes = {
  headerRows: array.isRequired,
  bodyRows: array.isRequired,
  columns: array.isRequired,
  cellComponent: func.isRequired,
  rowComponent: func.isRequired,
  height: number.isRequired,
  estimatedRowHeight: number.isRequired,
  tableComponent: func.isRequired,
  headComponent: func.isRequired,
  bodyComponent: func.isRequired,
  headTableComponent: func.isRequired,
  containerComponent: func.isRequired
};

var FixedHeader = function FixedHeader(props) {
  return _(Table$2, _extends({ use: 'head' }, props));
};
var TableHead$1 = function TableHead(props) {
  return _('thead', props);
};
var TableBody$1 = function TableBody(props) {
  return _('tbody', props);
};

var defaultMessages$3 = {
  noData: 'No data'
};

var VirtualTable = function (_React$PureComponent) {
  inherits(VirtualTable, _React$PureComponent);

  function VirtualTable(props) {
    classCallCheck(this, VirtualTable);

    var _this = possibleConstructorReturn(this, (VirtualTable.__proto__ || Object.getPrototypeOf(VirtualTable)).call(this, props));

    var height = props.height,
        estimatedRowHeight = props.estimatedRowHeight;

    _this.layoutRenderComponent = createRenderComponent(VirtualTableLayout$1, { height: height, estimatedRowHeight: estimatedRowHeight });
    return _this;
  }

  createClass(VirtualTable, [{
    key: 'componentWillReceiveProps',
    value: function componentWillReceiveProps(_ref) {
      var height = _ref.height,
          estimatedRowHeight = _ref.estimatedRowHeight;

      this.layoutRenderComponent.update({ height: height, estimatedRowHeight: estimatedRowHeight });
    }
  }, {
    key: 'render',
    value: function render() {
      var _props = this.props,
          height = _props.height,
          estimatedRowHeight = _props.estimatedRowHeight,
          messages = _props.messages,
          restProps = objectWithoutProperties(_props, ['height', 'estimatedRowHeight', 'messages']);


      return _(Table, _extends({
        layoutComponent: this.layoutRenderComponent.component,
        tableComponent: Table$2,
        headComponent: TableHead$1,
        bodyComponent: TableBody$1,
        containerComponent: TableContainer,
        fixedHeaderComponent: FixedHeader,
        rowComponent: TableRow,
        cellComponent: TableCell,
        noDataRowComponent: TableRow,
        noDataCellComponent: TableNoDataCell,
        stubCellComponent: TableStubCell,
        stubHeaderCellComponent: TableStubHeaderCell,
        messages: _extends({}, defaultMessages$3, messages)
      }, restProps));
    }
  }]);
  return VirtualTable;
}(React.PureComponent);

VirtualTable.Cell = TableCell;
VirtualTable.Row = TableRow;
VirtualTable.NoDataCell = TableNoDataCell;
VirtualTable.NoDataRow = TableRow;
VirtualTable.StubCell = TableStubCell;
VirtualTable.StubHeaderCell = TableStubCell;
VirtualTable.Table = Table$2;
VirtualTable.TableHead = TableHead$1;
VirtualTable.TableBody = TableBody$1;
VirtualTable.FixedHeader = FixedHeader;
VirtualTable.Container = TableContainer;

VirtualTable.propTypes = {
  estimatedRowHeight: number,
  height: number,
  messages: shape({
    noData: string
  })
};

VirtualTable.defaultProps = {
  estimatedRowHeight: 37,
  height: 530,
  messages: {}
};

var TableFilterCell = function TableFilterCell(_ref) {
  var filter = _ref.filter,
      onFilter = _ref.onFilter,
      children = _ref.children,
      column = _ref.column,
      tableRow = _ref.tableRow,
      tableColumn = _ref.tableColumn,
      getMessage = _ref.getMessage,
      filteringEnabled = _ref.filteringEnabled,
      restProps = objectWithoutProperties(_ref, ['filter', 'onFilter', 'children', 'column', 'tableRow', 'tableColumn', 'getMessage', 'filteringEnabled']);
  return _(
    'th',
    restProps,
    children || _('input', {
      type: 'text',
      className: 'form-control',
      value: filter ? filter.value : '',
      onChange: function onChange(e) {
        return onFilter(e.target.value ? { value: e.target.value } : null);
      },
      readOnly: !filteringEnabled
    })
  );
};

TableFilterCell.propTypes = {
  filter: object,
  onFilter: func,
  children: oneOfType([node, arrayOf(node)]),
  column: object,
  tableRow: object,
  tableColumn: object,
  getMessage: func,
  filteringEnabled: bool
};

TableFilterCell.defaultProps = {
  filter: null,
  onFilter: function onFilter() {},
  children: undefined,
  column: undefined,
  tableRow: undefined,
  tableColumn: undefined,
  getMessage: undefined,
  filteringEnabled: true
};

var TableFilterRow$1 = function (_React$PureComponent) {
  inherits(TableFilterRow$$1, _React$PureComponent);

  function TableFilterRow$$1() {
    classCallCheck(this, TableFilterRow$$1);
    return possibleConstructorReturn(this, (TableFilterRow$$1.__proto__ || Object.getPrototypeOf(TableFilterRow$$1)).apply(this, arguments));
  }

  createClass(TableFilterRow$$1, [{
    key: 'render',
    value: function render() {
      return _(TableFilterRow, _extends({
        cellComponent: TableFilterCell,
        rowComponent: TableRow
      }, this.props));
    }
  }]);
  return TableFilterRow$$1;
}(React.PureComponent);

TableFilterRow$1.Cell = TableFilterCell;
TableFilterRow$1.Row = TableRow;

var ResizingControlLine = function ResizingControlLine(_ref) {
  var resizing = _ref.resizing,
      style = _ref.style;

  var resizingControlLineBody = resizing && _('div', {
    className: 'bg-primary position-absolute w-100 h-100 dx-rg-bs4-resizing-control-wrapper'
  });

  return _(
    'div',
    {
      className: 'position-absolute h-50 dx-rg-bs4-resizing-control-line',
      style: style
    },
    resizingControlLineBody
  );
};

ResizingControlLine.propTypes = {
  resizing: bool.isRequired,
  style: object.isRequired
};

var ResizingControl = function (_React$PureComponent) {
  inherits(ResizingControl, _React$PureComponent);

  function ResizingControl(props) {
    classCallCheck(this, ResizingControl);

    var _this = possibleConstructorReturn(this, (ResizingControl.__proto__ || Object.getPrototypeOf(ResizingControl)).call(this, props));

    _this.state = {
      resizing: false
    };

    _this.onResizeStart = function (_ref2) {
      var x = _ref2.x;

      _this.resizeStartingX = x;
      _this.setState({ resizing: true });
    };
    _this.onResizeUpdate = function (_ref3) {
      var x = _ref3.x;
      var onWidthDraft = _this.props.onWidthDraft;

      onWidthDraft({ shift: x - _this.resizeStartingX });
    };
    _this.onResizeEnd = function (_ref4) {
      var x = _ref4.x;
      var _this$props = _this.props,
          onWidthChange = _this$props.onWidthChange,
          onWidthDraftCancel = _this$props.onWidthDraftCancel;

      onWidthDraftCancel();
      onWidthChange({ shift: x - _this.resizeStartingX });
      _this.setState({ resizing: false });
    };
    return _this;
  }

  createClass(ResizingControl, [{
    key: 'render',
    value: function render() {
      var resizing = this.state.resizing;


      return _(
        Draggable,
        {
          onStart: this.onResizeStart,
          onUpdate: this.onResizeUpdate,
          onEnd: this.onResizeEnd
        },
        _(
          'div',
          {
            className: 'position-absolute h-100 dx-rg-bs4-resizing-control dx-rg-bs4-user-select-none'
          },
          _(ResizingControlLine, { resizing: resizing, style: { left: '5px' } }),
          _(ResizingControlLine, { resizing: resizing, style: { left: '7px' } })
        )
      );
    }
  }]);
  return ResizingControl;
}(React.PureComponent);

ResizingControl.propTypes = {
  onWidthChange: func.isRequired,
  onWidthDraft: func.isRequired,
  onWidthDraftCancel: func.isRequired
};

var GroupingControl = function GroupingControl(_ref) {
  var align = _ref.align,
      disabled = _ref.disabled,
      onGroup = _ref.onGroup;

  var invertedAlign = align === 'left';

  return _(
    'div',
    {
      className: classNames({
        'dx-rg-bs4-grouping-control': true,
        'float-right text-right': invertedAlign,
        'float-left text-left': !invertedAlign
      }),
      onClick: function onClick(e) {
        if (disabled) return;
        e.stopPropagation();
        onGroup();
      }
    },
    _('span', {
      className: classNames({
        'oi oi-list dx-rg-bs4-grouping-control-icon': true,
        'dx-rg-bs4-cursor-pointer': !disabled,
        'dx-rg-bs4-inactive': disabled
      })
    })
  );
};

GroupingControl.propTypes = {
  align: string.isRequired,
  onGroup: func.isRequired,
  disabled: bool
};

GroupingControl.defaultProps = {
  disabled: false
};

var handleMouseDown$3 = function handleMouseDown(e) {
  e.currentTarget.style.outline = 'none';
};
var handleBlur$3 = function handleBlur(e) {
  e.currentTarget.style.outline = '';
};

var getProps = function getProps(sortingDirection, disabled, onClick) {
  return {
    className: classNames({
      'dx-rg-bs4-sorting-control': true,
      'text-primary': sortingDirection
    }),
    tabIndex: disabled ? -1 : 0,
    onMouseDown: handleMouseDown$3,
    onBlur: handleBlur$3,
    onKeyDown: onClick
  };
};

var SortingControl = function SortingControl(_ref) {
  var align = _ref.align,
      sortingDirection = _ref.sortingDirection,
      columnTitle = _ref.columnTitle,
      disabled = _ref.disabled,
      onClick = _ref.onClick;

  var props = getProps(sortingDirection, disabled, onClick);
  return align === 'right' ? _(
    'span',
    props,
    _(SortingIndicator, {
      direction: sortingDirection
    }),
    '\xA0',
    columnTitle
  ) : _(
    'span',
    props,
    columnTitle,
    '\xA0',
    _(SortingIndicator, {
      direction: sortingDirection
    })
  );
};

SortingControl.propTypes = {
  align: string.isRequired,
  sortingDirection: oneOf(['asc', 'desc']),
  columnTitle: string.isRequired,
  onClick: func.isRequired,
  disabled: bool
};

SortingControl.defaultProps = {
  sortingDirection: null,
  disabled: false
};

var ENTER_KEY_CODE$3 = 13;
var SPACE_KEY_CODE$3 = 32;

var TableHeaderCell = function (_React$PureComponent) {
  inherits(TableHeaderCell, _React$PureComponent);

  function TableHeaderCell(props) {
    classCallCheck(this, TableHeaderCell);

    var _this = possibleConstructorReturn(this, (TableHeaderCell.__proto__ || Object.getPrototypeOf(TableHeaderCell)).call(this, props));

    _this.state = {
      dragging: false
    };
    _this.onClick = function (e) {
      var _this$props = _this.props,
          sortingEnabled = _this$props.sortingEnabled,
          showSortingControls = _this$props.showSortingControls,
          onSort = _this$props.onSort;

      var isActionKeyDown = e.keyCode === ENTER_KEY_CODE$3 || e.keyCode === SPACE_KEY_CODE$3;
      var isMouseClick = e.keyCode === undefined;

      if (!showSortingControls || !sortingEnabled || !(isActionKeyDown || isMouseClick)) return;

      var cancelSortingRelatedKey = e.metaKey || e.ctrlKey;
      var direction = (isMouseClick || isActionKeyDown) && cancelSortingRelatedKey ? null : undefined;

      e.preventDefault();
      onSort({
        direction: direction,
        keepOther: e.shiftKey || cancelSortingRelatedKey
      });
    };
    return _this;
  }

  createClass(TableHeaderCell, [{
    key: 'render',
    value: function render() {
      var _classNames,
          _this2 = this;

      var _props = this.props,
          className = _props.className,
          column = _props.column,
          tableColumn = _props.tableColumn,
          showSortingControls = _props.showSortingControls,
          sortingDirection = _props.sortingDirection,
          sortingEnabled = _props.sortingEnabled,
          showGroupingControls = _props.showGroupingControls,
          onGroup = _props.onGroup,
          groupingEnabled = _props.groupingEnabled,
          draggingEnabled = _props.draggingEnabled,
          onWidthDraftCancel = _props.onWidthDraftCancel,
          resizingEnabled = _props.resizingEnabled,
          onWidthChange = _props.onWidthChange,
          onWidthDraft = _props.onWidthDraft,
          tableRow = _props.tableRow,
          getMessage = _props.getMessage,
          onSort = _props.onSort,
          restProps = objectWithoutProperties(_props, ['className', 'column', 'tableColumn', 'showSortingControls', 'sortingDirection', 'sortingEnabled', 'showGroupingControls', 'onGroup', 'groupingEnabled', 'draggingEnabled', 'onWidthDraftCancel', 'resizingEnabled', 'onWidthChange', 'onWidthDraft', 'tableRow', 'getMessage', 'onSort']);
      var dragging = this.state.dragging;

      var align = tableColumn && tableColumn.align || 'left';
      var columnTitle = column && (column.title || column.name);
      var isCellInteractive = showSortingControls && sortingEnabled || draggingEnabled;

      var cellLayout = _(
        'th',
        _extends({
          className: classNames({
            'position-relative': true,
            'dx-rg-bs4-cursor-pointer dx-rg-bs4-user-select-none': isCellInteractive,
            'dx-rg-bs4-inactive': dragging || tableColumn && tableColumn.draft
          }, className),
          scope: 'col',
          onClick: this.onClick
        }, restProps),
        showGroupingControls && _(GroupingControl, {
          align: align,
          onGroup: onGroup,
          disabled: !groupingEnabled
        }),
        _(
          'div',
          {
            className: classNames((_classNames = {
              'text-nowrap dx-rg-bs4-table-header-cell-wrapper': true
            }, defineProperty(_classNames, 'text-' + align, align !== 'left'), defineProperty(_classNames, 'dx-rg-bs4-table-header-cell-' + align, showGroupingControls), _classNames))
          },
          showSortingControls ? _(SortingControl, {
            align: align,
            sortingDirection: sortingDirection,
            disabled: !sortingEnabled,
            columnTitle: columnTitle,
            onClick: this.onClick
          }) : columnTitle
        ),
        resizingEnabled && _(ResizingControl, {
          onWidthChange: onWidthChange,
          onWidthDraft: onWidthDraft,
          onWidthDraftCancel: onWidthDraftCancel
        })
      );

      return draggingEnabled ? _(
        DragSource,
        {
          ref: function ref(element) {
            _this2.cellRef = element;
          },
          payload: [{ type: 'column', columnName: column.name }],
          onStart: function onStart() {
            return _this2.setState({ dragging: true });
          },
          onEnd: function onEnd() {
            return _this2.cellRef && _this2.setState({ dragging: false });
          }
        },
        cellLayout
      ) : cellLayout;
    }
  }]);
  return TableHeaderCell;
}(React.PureComponent);

TableHeaderCell.propTypes = {
  tableColumn: object,
  tableRow: object,
  column: object,
  className: string,
  showSortingControls: bool,
  sortingEnabled: bool,
  sortingDirection: oneOf(['asc', 'desc', null]),
  onSort: func,
  showGroupingControls: bool,
  onGroup: func,
  groupingEnabled: bool,
  draggingEnabled: bool,
  resizingEnabled: bool,
  onWidthChange: func,
  onWidthDraft: func,
  onWidthDraftCancel: func,
  getMessage: func
};

TableHeaderCell.defaultProps = {
  column: undefined,
  tableColumn: undefined,
  tableRow: undefined,
  className: undefined,
  showSortingControls: false,
  sortingEnabled: false,
  sortingDirection: undefined,
  onSort: undefined,
  showGroupingControls: false,
  onGroup: undefined,
  groupingEnabled: false,
  draggingEnabled: false,
  resizingEnabled: false,
  onWidthChange: undefined,
  onWidthDraft: undefined,
  onWidthDraftCancel: undefined,
  getMessage: undefined
};

var TableHeaderRow$1 = function (_React$PureComponent) {
  inherits(TableHeaderRow$$1, _React$PureComponent);

  function TableHeaderRow$$1() {
    classCallCheck(this, TableHeaderRow$$1);
    return possibleConstructorReturn(this, (TableHeaderRow$$1.__proto__ || Object.getPrototypeOf(TableHeaderRow$$1)).apply(this, arguments));
  }

  createClass(TableHeaderRow$$1, [{
    key: 'render',
    value: function render() {
      return _(TableHeaderRow, _extends({
        cellComponent: TableHeaderCell,
        rowComponent: TableRow
      }, this.props));
    }
  }]);
  return TableHeaderRow$$1;
}(React.PureComponent);

TableHeaderRow$1.Cell = TableHeaderCell;
TableHeaderRow$1.Row = TableRow;

var EditCell = function EditCell(_ref) {
  var column = _ref.column,
      value = _ref.value,
      onValueChange = _ref.onValueChange,
      className = _ref.className,
      children = _ref.children,
      row = _ref.row,
      tableRow = _ref.tableRow,
      tableColumn = _ref.tableColumn,
      editingEnabled = _ref.editingEnabled,
      restProps = objectWithoutProperties(_ref, ['column', 'value', 'onValueChange', 'className', 'children', 'row', 'tableRow', 'tableColumn', 'editingEnabled']);
  return _(
    'td',
    _extends({
      className: classNames('align-middle dx-rg-bs4-table-edit-cell', className)
    }, restProps),
    children || _('input', {
      type: 'text',
      className: classNames({
        'form-control w-100': true,
        'text-right': tableColumn && tableColumn.align === 'right',
        'text-center': tableColumn && tableColumn.align === 'center'
      }),
      readOnly: !editingEnabled,
      value: value,
      onChange: function onChange(e) {
        return onValueChange(e.target.value);
      }
    })
  );
};
EditCell.propTypes = {
  column: object,
  row: object,
  tableColumn: object,
  tableRow: object,
  value: any,
  onValueChange: func.isRequired,
  className: string,
  editingEnabled: bool,
  children: node
};
EditCell.defaultProps = {
  column: undefined,
  row: undefined,
  tableColumn: undefined,
  tableRow: undefined,
  className: undefined,
  children: undefined,
  editingEnabled: true,
  value: ''
};

var TableEditRow$1 = function (_React$PureComponent) {
  inherits(TableEditRow$$1, _React$PureComponent);

  function TableEditRow$$1() {
    classCallCheck(this, TableEditRow$$1);
    return possibleConstructorReturn(this, (TableEditRow$$1.__proto__ || Object.getPrototypeOf(TableEditRow$$1)).apply(this, arguments));
  }

  createClass(TableEditRow$$1, [{
    key: 'render',
    value: function render() {
      return _(TableEditRow, _extends({
        cellComponent: EditCell,
        rowComponent: TableRow
      }, this.props));
    }
  }]);
  return TableEditRow$$1;
}(React.PureComponent);

TableEditRow$1.Cell = EditCell;
TableEditRow$1.Row = TableRow;

var CommandButton = function CommandButton(_ref) {
  var onExecute = _ref.onExecute,
      text = _ref.text,
      className = _ref.className,
      restProps = objectWithoutProperties(_ref, ['onExecute', 'text', 'className']);
  return _(
    'button',
    _extends({
      className: classNames('btn btn-link dx-rg-bs4-table-edit-command-cell', className),
      onClick: function onClick(e) {
        e.stopPropagation();
        onExecute();
      }
    }, restProps),
    text
  );
};

CommandButton.propTypes = {
  text: string.isRequired,
  onExecute: func.isRequired,
  className: string
};

CommandButton.defaultProps = {
  className: undefined
};

var EditCommandHeadingCell = function EditCommandHeadingCell(_ref2) {
  var children = _ref2.children,
      className = _ref2.className,
      tableColumn = _ref2.tableColumn,
      tableRow = _ref2.tableRow,
      restProps = objectWithoutProperties(_ref2, ['children', 'className', 'tableColumn', 'tableRow']);
  return _(
    'th',
    _extends({
      className: classNames('text-center p-0 text-nowrap', className)
    }, restProps),
    children
  );
};

EditCommandHeadingCell.propTypes = {
  children: oneOfType([arrayOf(node), node]),
  tableColumn: object,
  tableRow: object,
  className: string
};

EditCommandHeadingCell.defaultProps = {
  children: undefined,
  tableColumn: undefined,
  tableRow: undefined,
  className: undefined
};

var EditCommandCell = function EditCommandCell(_ref3) {
  var children = _ref3.children,
      className = _ref3.className,
      tableColumn = _ref3.tableColumn,
      tableRow = _ref3.tableRow,
      restProps = objectWithoutProperties(_ref3, ['children', 'className', 'tableColumn', 'tableRow']);
  return _(
    'td',
    _extends({
      className: classNames('text-center p-0 text-nowrap', className)
    }, restProps),
    children
  );
};

EditCommandCell.propTypes = {
  children: oneOfType([arrayOf(node), node]),
  tableColumn: object,
  tableRow: object,
  className: string
};

EditCommandCell.defaultProps = {
  children: undefined,
  tableColumn: undefined,
  tableRow: undefined,
  className: undefined
};

var defaultMessages$4 = {
  addCommand: 'New',
  editCommand: 'Edit',
  deleteCommand: 'Delete',
  commitCommand: 'Save',
  cancelCommand: 'Cancel'
};

var TableEditColumn$1 = function (_React$PureComponent) {
  inherits(TableEditColumn$$1, _React$PureComponent);

  function TableEditColumn$$1() {
    classCallCheck(this, TableEditColumn$$1);
    return possibleConstructorReturn(this, (TableEditColumn$$1.__proto__ || Object.getPrototypeOf(TableEditColumn$$1)).apply(this, arguments));
  }

  createClass(TableEditColumn$$1, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          messages = _props.messages,
          restProps = objectWithoutProperties(_props, ['messages']);


      return _(TableEditColumn, _extends({
        cellComponent: EditCommandCell,
        headerCellComponent: EditCommandHeadingCell,
        commandComponent: CommandButton,
        messages: _extends({}, defaultMessages$4, messages)
      }, restProps));
    }
  }]);
  return TableEditColumn$$1;
}(React.PureComponent);

TableEditColumn$1.Command = CommandButton;
TableEditColumn$1.Cell = EditCommandCell;
TableEditColumn$1.HeaderCell = EditCommandHeadingCell;

TableEditColumn$1.propTypes = {
  messages: shape({
    addCommand: string,
    editCommand: string,
    deleteCommand: string,
    commitCommand: string,
    cancelCommand: string
  })
};

TableEditColumn$1.defaultProps = {
  messages: {}
};

var EmptyMessage = function EmptyMessage(_ref) {
  var getMessage = _ref.getMessage,
      className = _ref.className,
      restProps = objectWithoutProperties(_ref, ['getMessage', 'className']);
  return _(
    'div',
    _extends({
      className: classNames('py-5 text-center', className)
    }, restProps),
    _(
      'big',
      { className: 'text-muted' },
      getMessage('noColumns')
    )
  );
};

EmptyMessage.propTypes = {
  getMessage: func.isRequired,
  className: string
};

EmptyMessage.defaultProps = {
  className: undefined
};

var defaultMessages$5 = {
  noColumns: 'Nothing to show'
};

var TableColumnVisibility$1 = function (_React$PureComponent) {
  inherits(TableColumnVisibility$$1, _React$PureComponent);

  function TableColumnVisibility$$1() {
    classCallCheck(this, TableColumnVisibility$$1);
    return possibleConstructorReturn(this, (TableColumnVisibility$$1.__proto__ || Object.getPrototypeOf(TableColumnVisibility$$1)).apply(this, arguments));
  }

  createClass(TableColumnVisibility$$1, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          messages = _props.messages,
          restProps = objectWithoutProperties(_props, ['messages']);


      return _(TableColumnVisibility, _extends({
        emptyMessageComponent: EmptyMessage,
        messages: _extends({}, defaultMessages$5, messages)
      }, restProps));
    }
  }]);
  return TableColumnVisibility$$1;
}(React.PureComponent);

TableColumnVisibility$1.EmptyMessage = EmptyMessage;

TableColumnVisibility$1.propTypes = {
  messages: shape({
    noColumns: string
  })
};

TableColumnVisibility$1.defaultProps = {
  messages: {}
};

var TableReorderingCell = function TableReorderingCell(_ref) {
  var style = _ref.style,
      getCellDimensions = _ref.getCellDimensions;

  var refHandler = function refHandler(node$$1) {
    return node$$1 && getCellDimensions(function () {
      var _node$getBoundingClie = node$$1.getBoundingClientRect(),
          left = _node$getBoundingClie.left,
          right = _node$getBoundingClie.right;

      return { left: left, right: right };
    });
  };
  return _('td', {
    ref: refHandler,
    className: 'p-0 border-0',
    style: style
  });
};

TableReorderingCell.propTypes = {
  getCellDimensions: func.isRequired,
  style: object
};

TableReorderingCell.defaultProps = {
  style: null
};

var TableContainer$1 = function TableContainer(_ref) {
  var onOver = _ref.onOver,
      onLeave = _ref.onLeave,
      onDrop = _ref.onDrop,
      children = _ref.children;
  return _(
    DropTarget,
    {
      onOver: onOver,
      onLeave: onLeave,
      onDrop: onDrop
    },
    children
  );
};

// eslint-disable-next-line react/prop-types
var ReorderingRow = function ReorderingRow(_ref2) {
  var style = _ref2.style,
      restParams = objectWithoutProperties(_ref2, ['style']);
  return _(TableRow, _extends({
    style: _extends({}, style, {
      visibility: 'hidden'
    })
  }, restParams));
};

var TableColumnReordering$1 = function TableColumnReordering$$1(props) {
  return _(TableColumnReordering, _extends({
    tableContainerComponent: TableContainer$1,
    rowComponent: ReorderingRow,
    cellComponent: TableReorderingCell
  }, props));
};

var TableColumnResizing$1 = function (_React$PureComponent) {
  inherits(TableColumnResizing$$1, _React$PureComponent);

  function TableColumnResizing$$1() {
    classCallCheck(this, TableColumnResizing$$1);
    return possibleConstructorReturn(this, (TableColumnResizing$$1.__proto__ || Object.getPrototypeOf(TableColumnResizing$$1)).apply(this, arguments));
  }

  createClass(TableColumnResizing$$1, [{
    key: 'render',
    value: function render() {
      return _(TableColumnResizing, this.props);
    }
  }]);
  return TableColumnResizing$$1;
}(React.PureComponent);

var Toolbar$2 = function Toolbar$$1(_ref) {
  var children = _ref.children,
      className = _ref.className,
      style = _ref.style,
      restProps = objectWithoutProperties(_ref, ['children', 'className', 'style']);
  return _(
    'div',
    _extends({
      className: classNames('card-header py-2 d-flex position-relative dx-rg-bs4-toolbar', className),
      style: style
    }, restProps),
    children
  );
};

Toolbar$2.propTypes = {
  children: oneOfType([arrayOf(node), node]).isRequired,
  className: string,
  style: object
};

Toolbar$2.defaultProps = {
  className: undefined,
  style: null
};

var FlexibleSpace = function FlexibleSpace() {
  return _("div", { className: "d-flex ml-auto" });
};

var Toolbar$1 = function (_React$PureComponent) {
  inherits(Toolbar$$1, _React$PureComponent);

  function Toolbar$$1() {
    classCallCheck(this, Toolbar$$1);
    return possibleConstructorReturn(this, (Toolbar$$1.__proto__ || Object.getPrototypeOf(Toolbar$$1)).apply(this, arguments));
  }

  createClass(Toolbar$$1, [{
    key: 'render',
    value: function render() {
      return _(Toolbar, _extends({
        rootComponent: Toolbar$2,
        flexibleSpaceComponent: FlexibleSpace
      }, this.props));
    }
  }]);
  return Toolbar$$1;
}(React.PureComponent);

Toolbar$1.Root = Toolbar$2;

var SearchPanelInput = function SearchPanelInput(_ref) {
  var onValueChange = _ref.onValueChange,
      value = _ref.value,
      getMessage = _ref.getMessage,
      className = _ref.className,
      restProps = objectWithoutProperties(_ref, ['onValueChange', 'value', 'getMessage', 'className']);
  return _('input', _extends({
    type: 'text',
    className: classNames('form-control w-25', className),
    onChange: function onChange(e) {
      return onValueChange(e.target.value);
    },
    value: value,
    placeholder: getMessage('searchPlaceholder')
  }, restProps));
};

SearchPanelInput.propTypes = {
  value: any,
  onValueChange: func.isRequired,
  getMessage: func.isRequired,
  className: string
};

SearchPanelInput.defaultProps = {
  value: null,
  className: undefined
};

var defaultMessages$6 = {
  searchPlaceholder: 'Search...'
};
var SearchPanel$1 = function (_React$PureComponent) {
  inherits(SearchPanel$$1, _React$PureComponent);

  function SearchPanel$$1() {
    classCallCheck(this, SearchPanel$$1);
    return possibleConstructorReturn(this, (SearchPanel$$1.__proto__ || Object.getPrototypeOf(SearchPanel$$1)).apply(this, arguments));
  }

  createClass(SearchPanel$$1, [{
    key: 'render',
    value: function render() {
      var _props = this.props,
          messages = _props.messages,
          restProps = objectWithoutProperties(_props, ['messages']);

      return _(SearchPanel, _extends({
        inputComponent: SearchPanelInput,
        messages: _extends({}, defaultMessages$6, messages)
      }, restProps));
    }
  }]);
  return SearchPanel$$1;
}(React.PureComponent);

SearchPanel$1.Input = SearchPanelInput;

SearchPanel$1.propTypes = {
  messages: shape({
    searchPlaceholder: string
  })
};

SearchPanel$1.defaultProps = {
  messages: {}
};

return { Grid:Grid$1, ColumnChooser:ColumnChooser$1, DragDropProvider:DragDropProvider$1, PagingPanel:PagingPanel$1
	, GroupingPanel :GroupingPanel$1, TableRowDetail :TableRowDetail$1, TableGroupRow :TableGroupRow$1
	, TableSelection :TableSelection$1, Table :Table$1, VirtualTable:VirtualTable, TableFilterRow :TableFilterRow$1
	, TableHeaderRow :TableHeaderRow$1, TableEditRow :TableEditRow$1, TableEditColumn :TableEditColumn$1
	, TableColumnVisibility :TableColumnVisibility$1, TableColumnReordering :TableColumnReordering$1
	, TableColumnResizing :TableColumnResizing$1, Toolbar :Toolbar$1, SearchPanel :SearchPanel$1 };
}();
//# sourceMappingURL=dx-react-grid-bootstrap4.es.js.map
