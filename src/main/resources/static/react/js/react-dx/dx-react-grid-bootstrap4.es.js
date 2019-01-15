/**
 * Bundle of @devexpress/dx-react-grid-bootstrap4
 * Generated: 2018-12-25
 * Version: 1.10.0
 * License: https://js.devexpress.com/Licensing
 */

(function (global, factory) {
  typeof exports === 'object' && typeof module !== 'undefined' ? factory(exports, require('react'), require('prop-types'), require('@devexpress/dx-react-grid'), require('classnames'), require('@devexpress/dx-react-core'), require('react-dom'), require('react-popper'), require('@devexpress/dx-grid-core')) :
  typeof define === 'function' && define.amd ? define(['exports', 'react', 'prop-types', '@devexpress/dx-react-grid', 'classnames', '@devexpress/dx-react-core', 'react-dom', 'react-popper', '@devexpress/dx-grid-core'], factory) :
  (global = global || self, factory((global.DevExpress = global.DevExpress || {}, global.DevExpress.DXReactGridBootstrap4 = {}), global.React, global.PropTypes, global.DevExpress.DXReactGrid, global.classNames, global.DevExpress.DXReactCore, global.ReactDOM, global.reactPopper, global.DevExpress.DXGridCore));
}(this, function (exports, React, PropTypes, dxReactGrid, classNames, dxReactCore, ReactDOM, reactPopper, dxGridCore) { 'use strict';

  if (typeof process === "undefined") { var process = { env: {} }; }

  var PropTypes__default = 'default' in PropTypes ? PropTypes['default'] : PropTypes;
  classNames = classNames && classNames.hasOwnProperty('default') ? classNames['default'] : classNames;

  function _classCallCheck(instance, Constructor) {
    if (!(instance instanceof Constructor)) {
      throw new TypeError("Cannot call a class as a function");
    }
  }

  function _defineProperties(target, props) {
    for (var i = 0; i < props.length; i++) {
      var descriptor = props[i];
      descriptor.enumerable = descriptor.enumerable || false;
      descriptor.configurable = true;
      if ("value" in descriptor) descriptor.writable = true;
      Object.defineProperty(target, descriptor.key, descriptor);
    }
  }

  function _createClass(Constructor, protoProps, staticProps) {
    if (protoProps) _defineProperties(Constructor.prototype, protoProps);
    if (staticProps) _defineProperties(Constructor, staticProps);
    return Constructor;
  }

  function _defineProperty(obj, key, value) {
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
  }

  function _extends() {
    _extends = Object.assign || function (target) {
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

    return _extends.apply(this, arguments);
  }

  function _objectSpread(target) {
    for (var i = 1; i < arguments.length; i++) {
      var source = arguments[i] != null ? arguments[i] : {};
      var ownKeys = Object.keys(source);

      if (typeof Object.getOwnPropertySymbols === 'function') {
        ownKeys = ownKeys.concat(Object.getOwnPropertySymbols(source).filter(function (sym) {
          return Object.getOwnPropertyDescriptor(source, sym).enumerable;
        }));
      }

      ownKeys.forEach(function (key) {
        _defineProperty(target, key, source[key]);
      });
    }

    return target;
  }

  function _inherits(subClass, superClass) {
    if (typeof superClass !== "function" && superClass !== null) {
      throw new TypeError("Super expression must either be null or a function");
    }

    subClass.prototype = Object.create(superClass && superClass.prototype, {
      constructor: {
        value: subClass,
        writable: true,
        configurable: true
      }
    });
    if (superClass) _setPrototypeOf(subClass, superClass);
  }

  function _getPrototypeOf(o) {
    _getPrototypeOf = Object.setPrototypeOf ? Object.getPrototypeOf : function _getPrototypeOf(o) {
      return o.__proto__ || Object.getPrototypeOf(o);
    };
    return _getPrototypeOf(o);
  }

  function _setPrototypeOf(o, p) {
    _setPrototypeOf = Object.setPrototypeOf || function _setPrototypeOf(o, p) {
      o.__proto__ = p;
      return o;
    };

    return _setPrototypeOf(o, p);
  }

  function _objectWithoutPropertiesLoose(source, excluded) {
    if (source == null) return {};
    var target = {};
    var sourceKeys = Object.keys(source);
    var key, i;

    for (i = 0; i < sourceKeys.length; i++) {
      key = sourceKeys[i];
      if (excluded.indexOf(key) >= 0) continue;
      target[key] = source[key];
    }

    return target;
  }

  function _objectWithoutProperties(source, excluded) {
    if (source == null) return {};

    var target = _objectWithoutPropertiesLoose(source, excluded);

    var key, i;

    if (Object.getOwnPropertySymbols) {
      var sourceSymbolKeys = Object.getOwnPropertySymbols(source);

      for (i = 0; i < sourceSymbolKeys.length; i++) {
        key = sourceSymbolKeys[i];
        if (excluded.indexOf(key) >= 0) continue;
        if (!Object.prototype.propertyIsEnumerable.call(source, key)) continue;
        target[key] = source[key];
      }
    }

    return target;
  }

  function _assertThisInitialized(self) {
    if (self === void 0) {
      throw new ReferenceError("this hasn't been initialised - super() hasn't been called");
    }

    return self;
  }

  function _possibleConstructorReturn(self, call) {
    if (call && (typeof call === "object" || typeof call === "function")) {
      return call;
    }

    return _assertThisInitialized(self);
  }

  var BodyColorContext = React.createContext();

  var getBodyColor = function getBodyColor() {
    var body = document.getElementsByTagName('body')[0];

    var _window$getComputedSt = window.getComputedStyle(body),
        backgroundColor = _window$getComputedSt.backgroundColor;

    return backgroundColor;
  };

  var Root =
  /*#__PURE__*/
  function (_React$PureComponent) {
    _inherits(Root, _React$PureComponent);

    function Root(props) {
      var _this;

      _classCallCheck(this, Root);

      _this = _possibleConstructorReturn(this, _getPrototypeOf(Root).call(this, props));
      _this.state = {
        backgroundColor: undefined
      };
      return _this;
    }

    _createClass(Root, [{
      key: "componentDidMount",
      value: function componentDidMount() {
        this.setState({
          backgroundColor: getBodyColor()
        });
      }
    }, {
      key: "render",
      value: function render() {
        var _this$props = this.props,
            children = _this$props.children,
            className = _this$props.className,
            restProps = _objectWithoutProperties(_this$props, ["children", "className"]);

        var backgroundColor = this.state.backgroundColor;
        return React.createElement("div", _extends({
          className: classNames('d-flex flex-column position-relative', className)
        }, restProps), React.createElement(BodyColorContext.Provider, {
          value: backgroundColor
        }, children));
      }
    }]);

    return Root;
  }(React.PureComponent);
  process.env.NODE_ENV !== "production" ? Root.propTypes = {
    className: PropTypes.string,
    children: PropTypes.oneOfType([PropTypes.node, PropTypes.arrayOf(PropTypes.node)])
  } : void 0;
  Root.defaultProps = {
    className: undefined,
    children: undefined
  };

  var Grid = function Grid(_ref) {
    var children = _ref.children,
        props = _objectWithoutProperties(_ref, ["children"]);

    return React.createElement(dxReactGrid.Grid, _extends({
      rootComponent: Root
    }, props), children);
  };
  Grid.Root = Root;
  process.env.NODE_ENV !== "production" ? Grid.propTypes = {
    children: PropTypes.oneOfType([PropTypes.arrayOf(PropTypes.node), PropTypes.node]).isRequired
  } : void 0;

  var Popover =
  /*#__PURE__*/
  function (_React$PureComponent) {
    _inherits(Popover, _React$PureComponent);

    function Popover(props) {
      var _this;

      _classCallCheck(this, Popover);

      _this = _possibleConstructorReturn(this, _getPrototypeOf(Popover).call(this, props));
      _this.contentRef = React.createRef();
      _this.handleClick = _this.handleClick.bind(_assertThisInitialized(_assertThisInitialized(_this)));
      return _this;
    }

    _createClass(Popover, [{
      key: "componentDidMount",
      value: function componentDidMount() {
        this.toggleSubscribtions();
      }
    }, {
      key: "componentDidUpdate",
      value: function componentDidUpdate() {
        this.toggleSubscribtions();
      }
    }, {
      key: "componentWillUnmount",
      value: function componentWillUnmount() {
        this.detachDocumentEvents();
      }
    }, {
      key: "handleClick",
      value: function handleClick(e) {
        var eventTarget = e.target;
        var contentNode = this.contentRef.current;
        var _this$props = this.props,
            toggle = _this$props.toggle,
            target = _this$props.target;

        if (contentNode && !contentNode.contains(eventTarget) && !target.contains(eventTarget)) {
          toggle();
        }
      }
    }, {
      key: "toggleSubscribtions",
      value: function toggleSubscribtions() {
        var isOpen = this.props.isOpen;

        if (isOpen) {
          this.attachDocumentEvents();
        } else {
          this.detachDocumentEvents();
        }
      }
    }, {
      key: "attachDocumentEvents",
      value: function attachDocumentEvents() {
        this.toggleDocumentEvents('addEventListener');
      }
    }, {
      key: "detachDocumentEvents",
      value: function detachDocumentEvents() {
        this.toggleDocumentEvents('removeEventListener');
      }
    }, {
      key: "toggleDocumentEvents",
      value: function toggleDocumentEvents(method) {
        var _this2 = this;

        ['click', 'touchstart'].forEach(function (eventType) {
          document[method](eventType, _this2.handleClick, true);
        });
      }
    }, {
      key: "renderPopper",
      value: function renderPopper() {
        var _this3 = this;

        var _this$props2 = this.props,
            children = _this$props2.children,
            target = _this$props2.target,
            restProps = _objectWithoutProperties(_this$props2, ["children", "target"]);

        return React.createElement(reactPopper.Popper, _extends({
          referenceElement: target
        }, restProps), function (_ref) {
          var ref = _ref.ref,
              style = _ref.style,
              arrowProps = _ref.arrowProps,
              placement = _ref.placement;
          return React.createElement("div", {
            className: "popover show bs-popover-".concat(placement),
            ref: ref,
            style: style
          }, React.createElement("div", {
            className: "popover-inner",
            ref: _this3.contentRef
          }, children), React.createElement("div", {
            className: "arrow",
            ref: arrowProps.ref,
            style: arrowProps.style
          }));
        });
      }
    }, {
      key: "render",
      value: function render() {
        var _this$props3 = this.props,
            isOpen = _this$props3.isOpen,
            container = _this$props3.container;
        if (!isOpen) return null;
        return container === 'body' ? ReactDOM.createPortal(this.renderPopper(), document.body) : this.renderPopper();
      }
    }]);

    return Popover;
  }(React.PureComponent);
  var targetElementType = PropTypes.oneOfType([PropTypes.string, PropTypes.func, PropTypes.node, PropTypes.instanceOf(typeof Element !== 'undefined' ? Element : Object)]);
  process.env.NODE_ENV !== "production" ? Popover.propTypes = {
    container: targetElementType,
    placement: PropTypes.string,
    isOpen: PropTypes.bool,
    children: PropTypes.node.isRequired,
    target: targetElementType,
    toggle: PropTypes.func
  } : void 0;
  Popover.defaultProps = {
    target: null,
    container: 'body',
    isOpen: false,
    placement: 'auto',
    toggle: function toggle() {}
  };

  var Overlay = function Overlay(_ref) {
    var visible = _ref.visible,
        children = _ref.children,
        target = _ref.target,
        onHide = _ref.onHide,
        restProps = _objectWithoutProperties(_ref, ["visible", "children", "target", "onHide"]);

    var handleToggle = function handleToggle() {
      if (visible) onHide();
    };

    return target ? React.createElement(Popover, _extends({
      placement: "bottom",
      isOpen: visible,
      target: target,
      container: target ? target.parentElement : undefined,
      toggle: handleToggle
    }, restProps), children) : null;
  };
  process.env.NODE_ENV !== "production" ? Overlay.propTypes = {
    children: PropTypes.node.isRequired,
    onHide: PropTypes.func.isRequired,
    visible: PropTypes.bool,
    target: PropTypes.oneOfType([PropTypes.object, PropTypes.func])
  } : void 0;
  Overlay.defaultProps = {
    visible: false,
    target: null
  };

  var Container = function Container(_ref) {
    var children = _ref.children,
        className = _ref.className,
        restProps = _objectWithoutProperties(_ref, ["children", "className"]);

    return React.createElement("div", _extends({
      className: classNames('py-2', className)
    }, restProps), children);
  };
  process.env.NODE_ENV !== "production" ? Container.propTypes = {
    children: PropTypes.node.isRequired,
    className: PropTypes.string
  } : void 0;
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
        restProps = _objectWithoutProperties(_ref, ["item", "onToggle", "className", "disabled"]);

    return React.createElement("button", _extends({
      className: classNames({
        'dropdown-item dx-g-bs4-column-chooser-item': true,
        'dx-g-bs4-cursor-pointer': !disabled
      }, className),
      type: "button",
      onClick: onToggle,
      onMouseDown: handleMouseDown,
      onBlur: handleBlur,
      disabled: disabled
    }, restProps), React.createElement("input", {
      type: "checkbox",
      className: classNames({
        'dx-g-bs4-cursor-pointer': !disabled,
        'dx-g-bs4-column-chooser-checkbox': true
      }),
      tabIndex: -1,
      checked: !hidden,
      disabled: disabled,
      onChange: onToggle,
      onClick: function onClick(e) {
        return e.stopPropagation();
      }
    }), column.title || column.name);
  };
  process.env.NODE_ENV !== "production" ? Item.propTypes = {
    item: PropTypes.shape({
      column: PropTypes.shape({
        name: PropTypes.string
      }),
      hidden: PropTypes.bool
    }).isRequired,
    onToggle: PropTypes.func,
    className: PropTypes.string,
    disabled: PropTypes.bool
  } : void 0;
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
        restProps = _objectWithoutProperties(_ref, ["onToggle", "className", "getMessage", "buttonRef", "active"]);

    var buttonClasses = classNames({
      btn: true,
      'btn-outline-secondary': true,
      'border-0': true,
      active: active
    }, className);
    return React.createElement("button", _extends({
      type: "button",
      className: buttonClasses,
      onClick: onToggle,
      ref: buttonRef
    }, restProps), React.createElement("span", {
      className: "oi oi-eye"
    }));
  };
  process.env.NODE_ENV !== "production" ? ToggleButton.propTypes = {
    onToggle: PropTypes.func.isRequired,
    getMessage: PropTypes.func.isRequired,
    buttonRef: PropTypes.func.isRequired,
    className: PropTypes.string,
    active: PropTypes.bool
  } : void 0;
  ToggleButton.defaultProps = {
    className: undefined,
    active: false
  };

  var ColumnChooser = dxReactCore.withComponents({
    Container: Container,
    Item: Item,
    Overlay: Overlay,
    ToggleButton: ToggleButton
  })(dxReactGrid.ColumnChooser);

  var Container$1 = function Container(_ref) {
    var clientOffset = _ref.clientOffset,
        style = _ref.style,
        className = _ref.className,
        children = _ref.children,
        restProps = _objectWithoutProperties(_ref, ["clientOffset", "style", "className", "children"]);

    return React.createElement("ul", _extends({
      className: classNames('list-group d-inline-block position-fixed dx-g-bs4-drag-drop', className),
      style: _objectSpread({
        transform: "translate(calc(".concat(clientOffset.x, "px - 50%), calc(").concat(clientOffset.y, "px - 50%))"),
        zIndex: 1000,
        left: 0,
        top: 0
      }, style)
    }, restProps), children);
  };
  process.env.NODE_ENV !== "production" ? Container$1.propTypes = {
    clientOffset: PropTypes.shape({
      x: PropTypes.number.isRequired,
      y: PropTypes.number.isRequired
    }).isRequired,
    style: PropTypes.object,
    className: PropTypes.string,
    children: PropTypes.oneOfType([PropTypes.node, PropTypes.arrayOf(PropTypes.node)])
  } : void 0;
  Container$1.defaultProps = {
    style: {},
    className: undefined,
    children: undefined
  };
  var Column = function Column(_ref2) {
    var column = _ref2.column,
        className = _ref2.className,
        restProps = _objectWithoutProperties(_ref2, ["column", "className"]);

    return React.createElement("li", _extends({
      className: classNames('list-group-item', className)
    }, restProps), column.title);
  };
  process.env.NODE_ENV !== "production" ? Column.propTypes = {
    column: PropTypes.object.isRequired,
    className: PropTypes.string
  } : void 0;
  Column.defaultProps = {
    className: undefined
  };

  var DragDropProvider = dxReactCore.withComponents({
    Container: Container$1,
    Column: Column
  })(dxReactGrid.DragDropProvider);

  var PaginationLink = function PaginationLink(_ref) {
    var previous = _ref.previous,
        next = _ref.next,
        children = _ref.children,
        restProps = _objectWithoutProperties(_ref, ["previous", "next", "children"]);

    var ariaLabel = '';
    var content = children;

    if (next || previous) {
      var angleQuote;

      if (next) {
        angleQuote = "\xBB";
        ariaLabel = 'Next';
      }

      if (previous) {
        angleQuote = "\xAB";
        ariaLabel = 'Previous';
      }

      content = [React.createElement("span", {
        "aria-hidden": "true",
        key: "caret"
      }, children || angleQuote), React.createElement("span", {
        className: "sr-only",
        key: "sr"
      }, ariaLabel)];
    }

    return React.createElement("a", _extends({
      className: "page-link",
      "aria-label": ariaLabel
    }, restProps), content);
  };
  process.env.NODE_ENV !== "production" ? PaginationLink.propTypes = {
    previous: PropTypes__default.bool,
    next: PropTypes__default.bool,
    children: PropTypes__default.node
  } : void 0;
  PaginationLink.defaultProps = {
    previous: false,
    next: false,
    children: undefined
  };

  var PaginationItem = function PaginationItem(_ref) {
    var active = _ref.active,
        disabled = _ref.disabled,
        restProps = _objectWithoutProperties(_ref, ["active", "disabled"]);

    return React.createElement("li", _extends({
      className: classNames('page-item', {
        active: active,
        disabled: disabled
      })
    }, restProps));
  };
  process.env.NODE_ENV !== "production" ? PaginationItem.propTypes = {
    active: PropTypes__default.bool,
    disabled: PropTypes__default.bool
  } : void 0;
  PaginationItem.defaultProps = {
    active: false,
    disabled: false
  };

  var Pagination = function Pagination(_ref) {
    var className = _ref.className,
        listClassName = _ref.listClassName,
        restProps = _objectWithoutProperties(_ref, ["className", "listClassName"]);

    return React.createElement("nav", {
      className: className
    }, React.createElement("ul", _extends({
      className: classNames('pagination', listClassName)
    }, restProps)));
  };
  process.env.NODE_ENV !== "production" ? Pagination.propTypes = {
    className: PropTypes__default.string,
    listClassName: PropTypes__default.string
  } : void 0;
  Pagination.defaultProps = {
    className: undefined,
    listClassName: undefined
  };

  var PageSizeSelector = function PageSizeSelector(_ref) {
    var pageSize = _ref.pageSize,
        onPageSizeChange = _ref.onPageSizeChange,
        pageSizes = _ref.pageSizes,
        getMessage = _ref.getMessage;
    var showAll = getMessage('showAll');
    return React.createElement("div", {
      className: "d-inline-block"
    }, React.createElement("select", {
      className: "form-control d-sm-none",
      value: pageSize,
      onChange: function onChange(e) {
        return onPageSizeChange(parseInt(e.target.value, 10));
      }
    }, pageSizes.map(function (val) {
      return React.createElement("option", {
        key: val,
        value: val
      }, val || showAll);
    })), React.createElement(Pagination, {
      className: "d-none d-sm-flex",
      listClassName: "m-0"
    }, pageSizes.map(function (item) {
      return React.createElement(PaginationItem, {
        key: item,
        active: item === pageSize && true
      }, React.createElement(PaginationLink, {
        href: "#",
        onClick: function onClick(e) {
          e.preventDefault();
          onPageSizeChange(item);
        }
      }, item || showAll));
    })));
  };
  process.env.NODE_ENV !== "production" ? PageSizeSelector.propTypes = {
    pageSize: PropTypes.number.isRequired,
    onPageSizeChange: PropTypes.func.isRequired,
    pageSizes: PropTypes.arrayOf(PropTypes.number).isRequired,
    getMessage: PropTypes.func.isRequired
  } : void 0;

  var renderPageButtons = function renderPageButtons(currentPage, totalPageCount, currentPageChange) {
    var pageButtons = [];
    var maxButtonCount = 3;
    var startPage = 1;
    var endPage = totalPageCount || 1;

    if (maxButtonCount < totalPageCount) {
      startPage = dxGridCore.calculateStartPage(currentPage + 1, maxButtonCount, totalPageCount);
      endPage = startPage + maxButtonCount - 1;
    }

    if (startPage > 1) {
      pageButtons.push(React.createElement(PaginationItem, {
        key: 1
      }, React.createElement(PaginationLink, {
        href: "#",
        onClick: function onClick(e) {
          return currentPageChange(e, 0);
        }
      }, 1)));

      if (startPage > 2) {
        pageButtons.push(React.createElement(PaginationItem, {
          key: "ellipsisStart",
          disabled: true
        }, React.createElement(PaginationLink, null, '...')));
      }
    }

    var _loop = function _loop(page) {
      pageButtons.push(React.createElement(PaginationItem, {
        key: page,
        active: page === currentPage + 1,
        disabled: startPage === endPage
      }, React.createElement(PaginationLink, {
        href: "#",
        onClick: function onClick(e) {
          return currentPageChange(e, page - 1);
        }
      }, page)));
    };

    for (var page = startPage; page <= endPage; page += 1) {
      _loop(page);
    }

    if (endPage < totalPageCount) {
      if (endPage < totalPageCount - 1) {
        pageButtons.push(React.createElement(PaginationItem, {
          key: "ellipsisEnd",
          disabled: true
        }, React.createElement(PaginationLink, null, '...')));
      }

      pageButtons.push(React.createElement(PaginationItem, {
        key: totalPageCount
      }, React.createElement(PaginationLink, {
        href: "#",
        onClick: function onClick(e) {
          return currentPageChange(e, totalPageCount - 1);
        }
      }, totalPageCount)));
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
    var from = dxGridCore.firstRowOnPage(currentPage, pageSize, totalCount);
    var to = dxGridCore.lastRowOnPage(currentPage, pageSize, totalCount);

    var currentPageChange = function currentPageChange(e, nextPage) {
      e.preventDefault();
      onCurrentPageChange(nextPage);
    };

    return React.createElement(React.Fragment, null, React.createElement(Pagination, {
      className: "float-right d-none d-sm-flex",
      listClassName: "m-0"
    }, React.createElement(PaginationItem, {
      disabled: currentPage === 0
    }, React.createElement(PaginationLink, {
      previous: true,
      href: "#",
      onClick: function onClick(e) {
        return currentPageChange(e, currentPage - 1);
      }
    })), renderPageButtons(currentPage, totalPages, currentPageChange), React.createElement(PaginationItem, {
      disabled: currentPage === totalPages - 1 || totalCount === 0
    }, React.createElement(PaginationLink, {
      next: true,
      href: "#",
      onClick: function onClick(e) {
        return currentPageChange(e, currentPage + 1);
      }
    }))), React.createElement(Pagination, {
      className: "float-right d-sm-none",
      listClassName: "m-0"
    }, React.createElement(PaginationItem, {
      disabled: currentPage === 0
    }, React.createElement(PaginationLink, {
      previous: true,
      href: "#",
      onClick: function onClick(e) {
        return currentPageChange(e, currentPage - 1);
      }
    })), "\xA0", React.createElement(PaginationItem, {
      disabled: currentPage === totalPages - 1 || totalCount === 0
    }, React.createElement(PaginationLink, {
      next: true,
      href: "#",
      onClick: function onClick(e) {
        return currentPageChange(e, currentPage + 1);
      }
    }))), React.createElement("span", {
      className: "float-right d-sm-none mr-4"
    }, React.createElement("span", {
      className: "d-inline-block align-middle"
    }, getMessage('info', {
      from: from,
      to: to,
      count: totalCount
    }))));
  };
  process.env.NODE_ENV !== "production" ? Pagination$1.propTypes = {
    totalPages: PropTypes.number.isRequired,
    currentPage: PropTypes.number.isRequired,
    onCurrentPageChange: PropTypes.func.isRequired,
    totalCount: PropTypes.number.isRequired,
    pageSize: PropTypes.number.isRequired,
    getMessage: PropTypes.func.isRequired
  } : void 0;

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
        restProps = _objectWithoutProperties(_ref, ["currentPage", "onCurrentPageChange", "totalPages", "pageSize", "onPageSizeChange", "pageSizes", "totalCount", "getMessage", "className"]);

    return React.createElement("div", _extends({
      className: classNames('clearfix card-footer dx-g-bs4-paging-panel', className)
    }, restProps), !!pageSizes.length && React.createElement(PageSizeSelector, {
      pageSize: pageSize,
      onPageSizeChange: onPageSizeChange,
      pageSizes: pageSizes,
      getMessage: getMessage
    }), React.createElement(Pagination$1, {
      totalPages: totalPages,
      totalCount: totalCount,
      currentPage: currentPage,
      onCurrentPageChange: function onCurrentPageChange(page) {
        return _onCurrentPageChange(page);
      },
      pageSize: pageSize,
      getMessage: getMessage
    }));
  };
  process.env.NODE_ENV !== "production" ? Pager.propTypes = {
    currentPage: PropTypes.number.isRequired,
    onCurrentPageChange: PropTypes.func.isRequired,
    totalPages: PropTypes.number.isRequired,
    pageSize: PropTypes.number.isRequired,
    onPageSizeChange: PropTypes.func.isRequired,
    pageSizes: PropTypes.arrayOf(PropTypes.number).isRequired,
    totalCount: PropTypes.number.isRequired,
    getMessage: PropTypes.func.isRequired,
    className: PropTypes.string
  } : void 0;
  Pager.defaultProps = {
    className: undefined
  };

  var PagingPanel = dxReactCore.withComponents({
    Container: Pager
  })(dxReactGrid.PagingPanel);

  var GroupPanelContainer = function GroupPanelContainer(_ref) {
    var children = _ref.children,
        className = _ref.className,
        restProps = _objectWithoutProperties(_ref, ["children", "className"]);

    return React.createElement("div", _extends({
      className: classNames('w-100 mt-1', className)
    }, restProps), children);
  };
  process.env.NODE_ENV !== "production" ? GroupPanelContainer.propTypes = {
    children: PropTypes.oneOfType([PropTypes.arrayOf(PropTypes.node), PropTypes.node]),
    className: PropTypes.string
  } : void 0;
  GroupPanelContainer.defaultProps = {
    children: null,
    className: undefined
  };

  var SortingIndicator = function SortingIndicator(_ref) {
    var direction = _ref.direction,
        className = _ref.className;
    return React.createElement("span", {
      className: classNames({
        'oi dx-g-bs4-sorting-indicator mx-2': true,
        'oi-arrow-thick-bottom': direction === 'desc',
        'oi-arrow-thick-top': direction !== 'desc',
        invisible: !direction
      }, className)
    });
  };
  process.env.NODE_ENV !== "production" ? SortingIndicator.propTypes = {
    direction: PropTypes.oneOf(['asc', 'desc']),
    className: PropTypes.string
  } : void 0;
  SortingIndicator.defaultProps = {
    direction: null,
    className: undefined
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
        restProps = _objectWithoutProperties(_ref, ["item", "onGroup", "showGroupingControls", "showSortingControls", "sortingDirection", "onSort", "className", "groupingEnabled", "sortingEnabled"]);

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

    return React.createElement("div", _extends({
      className: classNames({
        'btn-group mb-1 mr-1': true,
        'dx-g-bs4-inactive': draft
      }, className)
    }, restProps), React.createElement("span", _extends({
      className: classNames({
        'btn btn-outline-secondary': true,
        disabled: !sortingEnabled && (showSortingControls || !groupingEnabled)
      }),
      onClick: handleSortingChange,
      onKeyDown: handleSortingChange
    }, sortingEnabled ? {
      tabIndex: 0
    } : null), column.title || column.name, showSortingControls && sortingDirection && React.createElement("span", null, "\xA0", React.createElement(SortingIndicator, {
      direction: sortingDirection
    }))), showGroupingControls && React.createElement("span", {
      className: classNames({
        'btn btn-outline-secondary': true,
        disabled: !groupingEnabled
      }),
      onClick: handleUngroup
    }, "\xA0", React.createElement("span", {
      className: "oi oi-x dx-g-bs4-group-panel-item-icon"
    })));
  };
  process.env.NODE_ENV !== "production" ? GroupPanelItem.propTypes = {
    item: PropTypes.shape({
      column: PropTypes.shape({
        title: PropTypes.string
      }).isRequired,
      draft: PropTypes.bool
    }).isRequired,
    showSortingControls: PropTypes.bool,
    sortingDirection: PropTypes.oneOf(['asc', 'desc', null]),
    className: PropTypes.string,
    onSort: PropTypes.func,
    onGroup: PropTypes.func,
    showGroupingControls: PropTypes.bool,
    groupingEnabled: PropTypes.bool,
    sortingEnabled: PropTypes.bool
  } : void 0;
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
        restProps = _objectWithoutProperties(_ref, ["getMessage", "className"]);

    return React.createElement("div", _extends({
      className: classNames('dx-g-bs4-group-panel-empty-message', className)
    }, restProps), getMessage('groupByColumn'));
  };
  process.env.NODE_ENV !== "production" ? GroupPanelEmptyMessage.propTypes = {
    getMessage: PropTypes.func.isRequired,
    className: PropTypes.string
  } : void 0;
  GroupPanelEmptyMessage.defaultProps = {
    className: undefined
  };

  var GroupingPanel = dxReactCore.withComponents({
    Container: GroupPanelContainer,
    Item: GroupPanelItem,
    EmptyMessage: GroupPanelEmptyMessage
  })(dxReactGrid.GroupingPanel);

  var ENTER_KEY_CODE$1 = 13;
  var SPACE_KEY_CODE$1 = 32;

  var handleMouseDown$1 = function handleMouseDown(e) {
    e.target.style.outline = 'none';
  };

  var handleBlur$1 = function handleBlur(e) {
    e.target.style.outline = '';
  };

  var ExpandButton = function ExpandButton(_ref) {
    var visible = _ref.visible,
        expanded = _ref.expanded,
        onToggle = _ref.onToggle,
        className = _ref.className,
        restProps = _objectWithoutProperties(_ref, ["visible", "expanded", "onToggle", "className"]);

    var fireToggle = function fireToggle() {
      if (!visible) return;
      onToggle(!expanded);
    };

    var handleClick = function handleClick(e) {
      e.stopPropagation();
      fireToggle();
    };

    var handleKeyDown = function handleKeyDown(e) {
      if (e.keyCode === ENTER_KEY_CODE$1 || e.keyCode === SPACE_KEY_CODE$1) {
        e.preventDefault();
        fireToggle();
      }
    };

    return React.createElement("i", _extends({
      className: classNames({
        'oi p-2 text-center dx-g-bs4-toggle-button': true,
        'oi-chevron-bottom': expanded,
        'oi-chevron-right': !expanded,
        'dx-g-bs4-toggle-button-hidden': !visible
      }, className),
      tabIndex: visible ? 0 : undefined // eslint-disable-line jsx-a11y/no-noninteractive-tabindex
      ,
      onKeyDown: handleKeyDown,
      onMouseDown: handleMouseDown$1,
      onBlur: handleBlur$1,
      onClick: handleClick
    }, restProps));
  };
  process.env.NODE_ENV !== "production" ? ExpandButton.propTypes = {
    visible: PropTypes.bool,
    expanded: PropTypes.bool,
    onToggle: PropTypes.func,
    className: PropTypes.string
  } : void 0;
  ExpandButton.defaultProps = {
    visible: true,
    expanded: false,
    onToggle: function onToggle() {},
    className: undefined
  };

  var TableDetailToggleCell = function TableDetailToggleCell(_ref) {
    var expanded = _ref.expanded,
        onToggle = _ref.onToggle,
        tableColumn = _ref.tableColumn,
        tableRow = _ref.tableRow,
        row = _ref.row,
        className = _ref.className,
        restProps = _objectWithoutProperties(_ref, ["expanded", "onToggle", "tableColumn", "tableRow", "row", "className"]);

    return React.createElement("td", _extends({
      className: classNames('text-center align-middle', className)
    }, restProps), React.createElement(ExpandButton, {
      expanded: expanded,
      onToggle: onToggle
    }));
  };
  process.env.NODE_ENV !== "production" ? TableDetailToggleCell.propTypes = {
    className: PropTypes.string,
    expanded: PropTypes.bool,
    onToggle: PropTypes.func,
    tableColumn: PropTypes.object,
    tableRow: PropTypes.object,
    row: PropTypes.any
  } : void 0;
  TableDetailToggleCell.defaultProps = {
    className: undefined,
    expanded: false,
    onToggle: function onToggle() {},
    tableColumn: undefined,
    tableRow: undefined,
    row: undefined
  };

  var TableDetailCell = function TableDetailCell(_ref) {
    var colSpan = _ref.colSpan,
        children = _ref.children,
        className = _ref.className,
        tableColumn = _ref.tableColumn,
        tableRow = _ref.tableRow,
        row = _ref.row,
        restProps = _objectWithoutProperties(_ref, ["colSpan", "children", "className", "tableColumn", "tableRow", "row"]);

    return React.createElement("td", _extends({
      colSpan: colSpan,
      className: classNames('table-active', className)
    }, restProps), children);
  };
  process.env.NODE_ENV !== "production" ? TableDetailCell.propTypes = {
    style: PropTypes.object,
    colSpan: PropTypes.number,
    children: PropTypes.oneOfType([PropTypes.arrayOf(PropTypes.node), PropTypes.node]),
    className: PropTypes.string,
    tableColumn: PropTypes.object,
    tableRow: PropTypes.object,
    row: PropTypes.any
  } : void 0;
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
        restProps = _objectWithoutProperties(_ref, ["children", "row", "tableRow"]);

    return React.createElement("tr", restProps, children);
  };
  process.env.NODE_ENV !== "production" ? TableRow.propTypes = {
    children: PropTypes.node,
    row: PropTypes.any,
    tableRow: PropTypes.object
  } : void 0;
  TableRow.defaultProps = {
    children: null,
    row: undefined,
    tableRow: undefined
  };

  var TableRowDetailWithWidth = function TableRowDetailWithWidth(props) {
    return React.createElement(dxReactGrid.TableRowDetail, _extends({
      toggleColumnWidth: 40
    }, props));
  };

  TableRowDetailWithWidth.components = dxReactGrid.TableRowDetail.components;
  var TableRowDetail = dxReactCore.withComponents({
    Row: TableRow,
    Cell: TableDetailCell,
    ToggleCell: TableDetailToggleCell
  })(TableRowDetailWithWidth);
  TableRowDetail.COLUMN_TYPE = dxReactGrid.TableRowDetail.COLUMN_TYPE;
  TableRowDetail.ROW_TYPE = dxReactGrid.TableRowDetail.ROW_TYPE;

  var Cell = function Cell(_ref) {
    var className = _ref.className,
        colSpan = _ref.colSpan,
        row = _ref.row,
        column = _ref.column,
        expanded = _ref.expanded,
        onToggle = _ref.onToggle,
        children = _ref.children,
        tableRow = _ref.tableRow,
        tableColumn = _ref.tableColumn,
        Icon = _ref.iconComponent,
        Content = _ref.contentComponent,
        restProps = _objectWithoutProperties(_ref, ["className", "colSpan", "row", "column", "expanded", "onToggle", "children", "tableRow", "tableColumn", "iconComponent", "contentComponent"]);

    var handleClick = function handleClick() {
      return onToggle();
    };

    return React.createElement("td", _extends({
      colSpan: colSpan,
      className: classNames('dx-g-bs4-cursor-pointer', className),
      onClick: handleClick
    }, restProps), React.createElement(Icon, {
      expanded: expanded,
      onToggle: onToggle,
      className: "mr-2"
    }), React.createElement(Content, {
      column: column,
      row: row
    }, children));
  };
  process.env.NODE_ENV !== "production" ? Cell.propTypes = {
    contentComponent: PropTypes.func.isRequired,
    iconComponent: PropTypes.func.isRequired,
    className: PropTypes.string,
    colSpan: PropTypes.number,
    row: PropTypes.any,
    column: PropTypes.object,
    expanded: PropTypes.bool,
    onToggle: PropTypes.func,
    children: PropTypes.oneOfType([PropTypes.node, PropTypes.arrayOf(PropTypes.node)]),
    tableRow: PropTypes.object,
    tableColumn: PropTypes.object
  } : void 0;
  Cell.defaultProps = {
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

  var Content = function Content(_ref) {
    var column = _ref.column,
        row = _ref.row,
        children = _ref.children,
        restProps = _objectWithoutProperties(_ref, ["column", "row", "children"]);

    return React.createElement("span", restProps, React.createElement("strong", null, column.title || column.name, ":", ' '), children || row.value);
  };
  process.env.NODE_ENV !== "production" ? Content.propTypes = {
    row: PropTypes.any,
    column: PropTypes.object,
    children: PropTypes.node
  } : void 0;
  Content.defaultProps = {
    row: {},
    column: {},
    children: undefined
  };

  var TableGroupRowWithIndent = function TableGroupRowWithIndent(props) {
    return React.createElement(dxReactGrid.TableGroupRow, _extends({
      indentColumnWidth: 33
    }, props));
  };

  TableGroupRowWithIndent.components = dxReactGrid.TableGroupRow.components;
  var TableGroupRow = dxReactCore.withComponents({
    Row: TableRow,
    Cell: Cell,
    Content: Content,
    Icon: ExpandButton
  })(TableGroupRowWithIndent);
  TableGroupRow.COLUMN_TYPE = dxReactGrid.TableGroupRow.COLUMN_TYPE;
  TableGroupRow.ROW_TYPE = dxReactGrid.TableGroupRow.ROW_TYPE;

  var SelectionControl = function SelectionControl(_ref) {
    var disabled = _ref.disabled,
        checked = _ref.checked,
        indeterminate = _ref.indeterminate,
        _onChange = _ref.onChange,
        className = _ref.className,
        restProps = _objectWithoutProperties(_ref, ["disabled", "checked", "indeterminate", "onChange", "className"]);

    return React.createElement("input", _extends({
      className: classNames({
        'd-inline-block': true,
        'dx-g-bs4-cursor-pointer': !disabled
      }, className),
      type: "checkbox",
      disabled: disabled,
      checked: checked,
      ref: function ref(_ref2) {
        if (_ref2) {
          _ref2.indeterminate = indeterminate; // eslint-disable-line no-param-reassign
        }
      },
      onChange: function onChange() {
        if (disabled) return;

        _onChange();
      },
      onClick: function onClick(e) {
        return e.stopPropagation();
      }
    }, restProps));
  };
  process.env.NODE_ENV !== "production" ? SelectionControl.propTypes = {
    disabled: PropTypes.bool,
    checked: PropTypes.bool,
    indeterminate: PropTypes.bool,
    onChange: PropTypes.func,
    className: PropTypes.string
  } : void 0;
  SelectionControl.defaultProps = {
    disabled: false,
    checked: false,
    indeterminate: false,
    onChange: function onChange() {},
    className: undefined
  };

  var TableSelectAllCell = function TableSelectAllCell(_ref) {
    var className = _ref.className,
        allSelected = _ref.allSelected,
        someSelected = _ref.someSelected,
        disabled = _ref.disabled,
        onToggle = _ref.onToggle,
        tableColumn = _ref.tableColumn,
        tableRow = _ref.tableRow,
        rowSpan = _ref.rowSpan,
        restProps = _objectWithoutProperties(_ref, ["className", "allSelected", "someSelected", "disabled", "onToggle", "tableColumn", "tableRow", "rowSpan"]);

    return React.createElement("th", _extends({
      className: classNames({
        'text-center': true,
        'align-middle': !rowSpan,
        'align-bottom': !!rowSpan
      }, className),
      rowSpan: rowSpan
    }, restProps), React.createElement(SelectionControl, {
      disabled: disabled,
      checked: allSelected,
      indeterminate: someSelected,
      onChange: onToggle
    }));
  };
  process.env.NODE_ENV !== "production" ? TableSelectAllCell.propTypes = {
    className: PropTypes.string,
    allSelected: PropTypes.bool,
    someSelected: PropTypes.bool,
    disabled: PropTypes.bool,
    onToggle: PropTypes.func,
    tableRow: PropTypes.object,
    tableColumn: PropTypes.object,
    rowSpan: PropTypes.number
  } : void 0;
  TableSelectAllCell.defaultProps = {
    className: undefined,
    allSelected: false,
    someSelected: false,
    disabled: false,
    onToggle: function onToggle() {},
    tableRow: undefined,
    tableColumn: undefined,
    rowSpan: undefined
  };

  var TableSelectCell = function TableSelectCell(_ref) {
    var className = _ref.className,
        selected = _ref.selected,
        onToggle = _ref.onToggle,
        row = _ref.row,
        tableRow = _ref.tableRow,
        tableColumn = _ref.tableColumn,
        restProps = _objectWithoutProperties(_ref, ["className", "selected", "onToggle", "row", "tableRow", "tableColumn"]);

    return React.createElement("td", _extends({
      className: classNames('text-center align-middle', className)
    }, restProps), React.createElement(SelectionControl, {
      checked: selected,
      onChange: onToggle
    }));
  };
  process.env.NODE_ENV !== "production" ? TableSelectCell.propTypes = {
    className: PropTypes.string,
    selected: PropTypes.bool,
    onToggle: PropTypes.func,
    row: PropTypes.any,
    tableRow: PropTypes.object,
    tableColumn: PropTypes.object
  } : void 0;
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
    return React.createElement("tr", {
      style: style,
      className: selected ? 'table-active' : '',
      onClick: function onClick(e) {
        if (!selectByRowClick) return;
        e.stopPropagation();
        onToggle();
      }
    }, children);
  };
  process.env.NODE_ENV !== "production" ? TableSelectRow.propTypes = {
    selected: PropTypes.bool,
    children: PropTypes.node,
    onToggle: PropTypes.func,
    selectByRowClick: PropTypes.bool,
    style: PropTypes.object
  } : void 0;
  TableSelectRow.defaultProps = {
    children: null,
    onToggle: function onToggle() {},
    selected: false,
    selectByRowClick: false,
    style: null
  };

  var TableSelectionWithWidth = function TableSelectionWithWidth(props) {
    return React.createElement(dxReactGrid.TableSelection, _extends({
      selectionColumnWidth: 40
    }, props));
  };

  TableSelectionWithWidth.components = dxReactGrid.TableSelection.components;
  var TableSelection = dxReactCore.withComponents({
    Row: TableSelectRow,
    Cell: TableSelectCell,
    HeaderCell: TableSelectAllCell
  })(TableSelectionWithWidth);
  TableSelection.COLUMN_TYPE = dxReactGrid.TableSelection.COLUMN_TYPE;

  var MINIMAL_COLUMN_WIDTH = 150;
  var TableLayout = function TableLayout(props) {
    return React.createElement(dxReactGrid.TableLayout, _extends({
      layoutComponent: dxReactGrid.StaticTableLayout,
      minColumnWidth: MINIMAL_COLUMN_WIDTH
    }, props));
  };

  var TableCell = function TableCell(_ref) {
    var column = _ref.column,
        value = _ref.value,
        children = _ref.children,
        tableRow = _ref.tableRow,
        tableColumn = _ref.tableColumn,
        row = _ref.row,
        className = _ref.className,
        restProps = _objectWithoutProperties(_ref, ["column", "value", "children", "tableRow", "tableColumn", "row", "className"]);

    return React.createElement("td", _extends({
      className: classNames({
        'dx-g-bs4-table-cell': true,
        'text-nowrap': !(tableColumn && tableColumn.wordWrapEnabled),
        'text-right': tableColumn && tableColumn.align === 'right',
        'text-center': tableColumn && tableColumn.align === 'center'
      }, className)
    }, restProps), children || value);
  };
  process.env.NODE_ENV !== "production" ? TableCell.propTypes = {
    value: PropTypes.any,
    column: PropTypes.object,
    row: PropTypes.any,
    children: PropTypes.oneOfType([PropTypes.node, PropTypes.arrayOf(PropTypes.node)]),
    tableRow: PropTypes.object,
    tableColumn: PropTypes.object,
    className: PropTypes.string
  } : void 0;
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
        restProps = _objectWithoutProperties(_ref, ["className", "tableRow", "tableColumn"]);

    return React.createElement("td", _extends({
      className: classNames('p-0', className)
    }, restProps));
  };
  process.env.NODE_ENV !== "production" ? TableStubCell.propTypes = {
    className: PropTypes.string,
    tableRow: PropTypes.object,
    tableColumn: PropTypes.object
  } : void 0;
  TableStubCell.defaultProps = {
    className: undefined,
    tableRow: undefined,
    tableColumn: undefined
  };

  var TableStubHeaderCell = function TableStubHeaderCell(_ref) {
    var className = _ref.className,
        tableRow = _ref.tableRow,
        tableColumn = _ref.tableColumn,
        restProps = _objectWithoutProperties(_ref, ["className", "tableRow", "tableColumn"]);

    return React.createElement("th", _extends({
      className: classNames('p-0', className)
    }, restProps));
  };
  process.env.NODE_ENV !== "production" ? TableStubHeaderCell.propTypes = {
    className: PropTypes.string,
    tableRow: PropTypes.object,
    tableColumn: PropTypes.object
  } : void 0;
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
        restProps = _objectWithoutProperties(_ref, ["className", "colSpan", "getMessage", "tableRow", "tableColumn"]);

    return React.createElement("td", _extends({
      className: classNames('p-0 py-5', className),
      colSpan: colSpan
    }, restProps), React.createElement("big", {
      className: "text-muted dx-g-bs4-fixed-block"
    }, getMessage('noData')));
  };
  process.env.NODE_ENV !== "production" ? TableNoDataCell.propTypes = {
    colSpan: PropTypes.number,
    getMessage: PropTypes.func.isRequired,
    tableRow: PropTypes.object,
    tableColumn: PropTypes.object,
    className: PropTypes.string
  } : void 0;
  TableNoDataCell.defaultProps = {
    className: undefined,
    colSpan: 1,
    tableRow: undefined,
    tableColumn: undefined
  };

  var Table =
  /*#__PURE__*/
  function (_React$Component) {
    _inherits(Table, _React$Component);

    function Table() {
      _classCallCheck(this, Table);

      return _possibleConstructorReturn(this, _getPrototypeOf(Table).apply(this, arguments));
    }

    _createClass(Table, [{
      key: "render",
      value: function render() {
        var _this$props = this.props,
            children = _this$props.children,
            use = _this$props.use,
            style = _this$props.style,
            className = _this$props.className,
            tableRef = _this$props.tableRef,
            restProps = _objectWithoutProperties(_this$props, ["children", "use", "style", "className", "tableRef"]);

        var backgroundColor = this.context;
        return React.createElement("table", _extends({
          ref: tableRef,
          className: classNames({
            'table dx-g-bs4-table': true,
            'dx-g-bs4-table-sticky': !!use,
            'dx-g-bs4-table-head': use === 'head',
            'dx-g-bs4-table-foot': use === 'foot'
          }, className)
        }, restProps, {
          style: _objectSpread({}, style, use ? {
            backgroundColor: backgroundColor
          } : null)
        }), children);
      }
    }]);

    return Table;
  }(React.Component);
  Table.contextType = BodyColorContext;
  process.env.NODE_ENV !== "production" ? Table.propTypes = {
    use: PropTypes.oneOf(['head', 'foot']),
    children: PropTypes.oneOfType([PropTypes.arrayOf(PropTypes.node), PropTypes.node]).isRequired,
    style: PropTypes.object,
    className: PropTypes.string,
    tableRef: dxReactCore.RefType.isRequired
  } : void 0;
  Table.defaultProps = {
    className: undefined,
    use: undefined,
    style: null
  };

  var TableContainer = function TableContainer(_ref) {
    var children = _ref.children,
        className = _ref.className,
        style = _ref.style,
        restProps = _objectWithoutProperties(_ref, ["children", "className", "style"]);

    return React.createElement("div", _extends({
      className: classNames('table-responsive dx-g-bs4-table-container', className),
      style: _objectSpread({
        msOverflowStyle: 'auto'
      }, style)
    }, restProps), children);
  };
  process.env.NODE_ENV !== "production" ? TableContainer.propTypes = {
    children: PropTypes.oneOfType([PropTypes.arrayOf(PropTypes.node), PropTypes.node]).isRequired,
    className: PropTypes.string
  } : void 0;
  TableContainer.defaultProps = {
    className: undefined
  };

  var TableStubRow = function TableStubRow(_ref) {
    var children = _ref.children,
        tableRow = _ref.tableRow,
        restProps = _objectWithoutProperties(_ref, ["children", "tableRow"]);

    return React.createElement("tr", restProps, children);
  };
  process.env.NODE_ENV !== "production" ? TableStubRow.propTypes = {
    children: PropTypes.node,
    tableRow: PropTypes.object
  } : void 0;
  TableStubRow.defaultProps = {
    children: null,
    tableRow: undefined
  };

  var TableHead = function TableHead(props) {
    return React.createElement("thead", props);
  };

  var TableBody = function TableBody(props) {
    return React.createElement("tbody", props);
  };

  var TableFooter = function TableFooter(props) {
    return React.createElement("tfoot", props);
  };

  var Table$1 = dxReactCore.withComponents({
    Table: Table,
    TableHead: TableHead,
    TableBody: TableBody,
    TableFooter: TableFooter,
    Container: TableContainer,
    Layout: TableLayout,
    Row: TableRow,
    Cell: TableCell,
    NoDataRow: TableRow,
    NoDataCell: TableNoDataCell,
    StubRow: TableStubRow,
    StubCell: TableStubCell,
    StubHeaderCell: TableStubHeaderCell
  })(dxReactGrid.Table);
  Table$1.COLUMN_TYPE = dxReactGrid.Table.COLUMN_TYPE;
  Table$1.ROW_TYPE = dxReactGrid.Table.ROW_TYPE;
  Table$1.NODATA_ROW_TYPE = dxReactGrid.Table.NODATA_ROW_TYPE;

  var MINIMAL_COLUMN_WIDTH$1 = 150;
  var VirtualTableLayout = function VirtualTableLayout(props) {
    return React.createElement(dxReactGrid.TableLayout, _extends({
      layoutComponent: dxReactGrid.VirtualTableLayout,
      minColumnWidth: MINIMAL_COLUMN_WIDTH$1
    }, props));
  };

  var FixedHeader = function FixedHeader(props) {
    return React.createElement(Table, _extends({
      use: "head"
    }, props));
  };

  var FixedFooter = function FixedFooter(props) {
    return React.createElement(Table, _extends({
      use: "foot"
    }, props));
  };

  var VirtualTable = dxReactGrid.makeVirtualTable(Table$1, {
    VirtualLayout: VirtualTableLayout,
    FixedHeader: FixedHeader,
    FixedFooter: FixedFooter,
    defaultEstimatedRowHeight: 49,
    defaultHeight: 530
  });
  VirtualTable.COLUMN_TYPE = Table$1.COLUMN_TYPE;
  VirtualTable.ROW_TYPE = Table$1.ROW_TYPE;
  VirtualTable.NODATA_ROW_TYPE = Table$1.NODATA_ROW_TYPE;

  var TableFilterCell = function TableFilterCell(_ref) {
    var filter = _ref.filter,
        onFilter = _ref.onFilter,
        children = _ref.children,
        column = _ref.column,
        tableRow = _ref.tableRow,
        tableColumn = _ref.tableColumn,
        getMessage = _ref.getMessage,
        filteringEnabled = _ref.filteringEnabled,
        restProps = _objectWithoutProperties(_ref, ["filter", "onFilter", "children", "column", "tableRow", "tableColumn", "getMessage", "filteringEnabled"]);

    return React.createElement("th", restProps, React.createElement("div", {
      className: "input-group"
    }, children));
  };
  process.env.NODE_ENV !== "production" ? TableFilterCell.propTypes = {
    filter: PropTypes.object,
    onFilter: PropTypes.func,
    children: PropTypes.oneOfType([PropTypes.node, PropTypes.arrayOf(PropTypes.node)]),
    column: PropTypes.object,
    tableRow: PropTypes.object,
    tableColumn: PropTypes.object,
    getMessage: PropTypes.func,
    filteringEnabled: PropTypes.bool
  } : void 0;
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

  var Editor = function Editor(_ref) {
    var value = _ref.value,
        disabled = _ref.disabled,
        getMessage = _ref.getMessage,
        _onChange = _ref.onChange,
        className = _ref.className,
        restProps = _objectWithoutProperties(_ref, ["value", "disabled", "getMessage", "onChange", "className"]);

    return React.createElement("input", _extends({
      type: "text",
      className: classNames('form-control', className),
      value: value,
      onChange: function onChange(event) {
        return _onChange(event.target.value);
      },
      readOnly: disabled,
      placeholder: getMessage('filterPlaceholder')
    }, restProps));
  };
  process.env.NODE_ENV !== "production" ? Editor.propTypes = {
    value: PropTypes.any,
    disabled: PropTypes.bool,
    onChange: PropTypes.func,
    getMessage: PropTypes.func.isRequired,
    className: PropTypes.string
  } : void 0;
  Editor.defaultProps = {
    value: '',
    disabled: false,
    onChange: function onChange() {},
    className: undefined
  };

  var FilterSelector =
  /*#__PURE__*/
  function (_React$PureComponent) {
    _inherits(FilterSelector, _React$PureComponent);

    function FilterSelector(props) {
      var _this;

      _classCallCheck(this, FilterSelector);

      _this = _possibleConstructorReturn(this, _getPrototypeOf(FilterSelector).call(this, props));
      _this.state = {
        opened: false
      };

      _this.handleButtonClick = function () {
        _this.setState(function (prevState) {
          return {
            opened: !prevState.opened
          };
        });
      };

      _this.handleOverlayToggle = function () {
        var opened = _this.state.opened;
        if (opened) _this.setState({
          opened: false
        });
      };

      _this.handleMenuItemClick = function (nextValue) {
        var onChange = _this.props.onChange;

        _this.setState({
          opened: false
        });

        onChange(nextValue);
      };

      return _this;
    }

    _createClass(FilterSelector, [{
      key: "render",
      value: function render() {
        var _this2 = this;

        var _this$props = this.props,
            value = _this$props.value,
            availableValues = _this$props.availableValues,
            disabled = _this$props.disabled,
            getMessage = _this$props.getMessage,
            Icon = _this$props.iconComponent,
            ToggleButton = _this$props.toggleButtonComponent,
            className = _this$props.className,
            restProps = _objectWithoutProperties(_this$props, ["value", "availableValues", "disabled", "getMessage", "iconComponent", "toggleButtonComponent", "className"]);

        var opened = this.state.opened;
        return availableValues.length ? React.createElement("div", _extends({
          className: classNames('input-group-prepend', className)
        }, restProps), React.createElement(ToggleButton, {
          disabled: disabled || availableValues.length === 1,
          onToggle: this.handleButtonClick,
          buttonRef: function buttonRef(ref) {
            _this2.targetElement = ref;
          }
        }, React.createElement(Icon, {
          type: value
        })), this.targetElement ? React.createElement(Popover, {
          placement: "bottom",
          isOpen: opened,
          target: this.targetElement,
          container: undefined,
          toggle: this.handleOverlayToggle
        }, React.createElement("div", {
          className: "py-2"
        }, availableValues.map(function (valueItem) {
          return React.createElement("button", {
            type: "button",
            key: valueItem,
            className: classNames({
              'dropdown-item d-flex align-items-center': true,
              'dx-g-bs4-cursor-pointer dx-g-bs4-filter-selector-item': true,
              active: valueItem === value
            }),
            onClick: function onClick() {
              return _this2.handleMenuItemClick(valueItem);
            }
          }, React.createElement(Icon, {
            type: valueItem
          }), React.createElement("span", {
            className: "dx-g-bs4-filter-selector-item-text"
          }, getMessage(valueItem)));
        }))) : null) : null;
      }
    }]);

    return FilterSelector;
  }(React.PureComponent);
  process.env.NODE_ENV !== "production" ? FilterSelector.propTypes = {
    value: PropTypes.string,
    availableValues: PropTypes.arrayOf(PropTypes.string),
    onChange: PropTypes.func,
    disabled: PropTypes.bool,
    iconComponent: PropTypes.func.isRequired,
    toggleButtonComponent: PropTypes.func.isRequired,
    getMessage: PropTypes.func.isRequired,
    className: PropTypes.string
  } : void 0;
  FilterSelector.defaultProps = {
    value: undefined,
    availableValues: [],
    onChange: function onChange() {},
    disabled: false,
    className: undefined
  };

  var ToggleButton$1 = function ToggleButton(_ref) {
    var buttonRef = _ref.buttonRef,
        onToggle = _ref.onToggle,
        disabled = _ref.disabled,
        children = _ref.children,
        className = _ref.className,
        restProps = _objectWithoutProperties(_ref, ["buttonRef", "onToggle", "disabled", "children", "className"]);

    return React.createElement("button", _extends({
      type: "button",
      className: classNames('btn btn-outline-secondary', className),
      disabled: disabled,
      onClick: onToggle,
      ref: buttonRef
    }, restProps), children);
  };
  process.env.NODE_ENV !== "production" ? ToggleButton$1.propTypes = {
    buttonRef: PropTypes.func.isRequired,
    onToggle: PropTypes.func.isRequired,
    children: PropTypes.node,
    disabled: PropTypes.bool
  } : void 0;
  ToggleButton$1.defaultProps = {
    children: undefined,
    disabled: false
  };

  var AVAILABLE_PATHS = {
    contains: 'M6.094 19.563l-2.031 0.281c-0.646 0.094-1.13 0.266-1.453 0.516-0.302 0.24-0.453 0.646-0.453 1.219 0 0.438 0.138 0.799 0.414 1.086s0.664 0.419 1.164 0.398c0.708 0 1.281-0.24 1.719-0.719 0.427-0.49 0.641-1.125 0.641-1.906v-0.875zM8.234 24.641h-2.172v-1.641c-0.677 1.24-1.661 1.859-2.953 1.859-0.927 0-1.682-0.276-2.266-0.828-0.552-0.552-0.828-1.292-0.828-2.219 0-1.927 1.068-3.052 3.203-3.375l2.875-0.438c0-1.469-0.656-2.203-1.969-2.203-1.177 0-2.224 0.427-3.141 1.281v-2.078c1.010-0.656 2.198-0.984 3.563-0.984 2.458 0 3.687 1.302 3.687 3.906v6.719zM14.734 16.797c0.521-0.583 1.167-0.875 1.938-0.875 0.74 0 1.323 0.281 1.75 0.844 0.448 0.583 0.672 1.38 0.672 2.391 0 1.188-0.24 2.13-0.719 2.828-0.49 0.677-1.13 1.016-1.922 1.016-0.719 0-1.302-0.271-1.75-0.813-0.427-0.51-0.641-1.141-0.641-1.891v-1.266c-0.021-0.906 0.203-1.651 0.672-2.234zM16.969 24.859c1.375 0 2.443-0.521 3.203-1.562 0.781-1.042 1.172-2.427 1.172-4.156 0-1.542-0.354-2.771-1.063-3.688-0.688-0.958-1.651-1.438-2.891-1.438-1.427 0-2.531 0.693-3.313 2.078v-6.781h-2.156v15.328h2.172v-1.5c0.677 1.146 1.635 1.719 2.875 1.719zM22.266 6.125c0.135 0 0.245 0.063 0.328 0.188 0.104 0.073 0.156 0.182 0.156 0.328v22.953c0 0.125-0.052 0.24-0.156 0.344-0.083 0.115-0.193 0.172-0.328 0.172h-12.281c-0.146 0-0.266-0.057-0.359-0.172-0.115-0.115-0.172-0.229-0.172-0.344v-22.953c0-0.135 0.057-0.245 0.172-0.328 0.094-0.125 0.214-0.188 0.359-0.188h12.281zM31.531 24.141c-0.76 0.479-1.693 0.719-2.797 0.719-1.427 0-2.589-0.479-3.484-1.438-0.865-0.958-1.286-2.198-1.266-3.719 0-1.688 0.448-3.052 1.344-4.094 0.917-1.042 2.208-1.573 3.875-1.594 0.854 0 1.63 0.177 2.328 0.531v2.156c-0.677-0.531-1.391-0.792-2.141-0.781-0.938 0-1.714 0.339-2.328 1.016-0.594 0.677-0.891 1.552-0.891 2.625 0 1.042 0.297 1.88 0.891 2.516 0.521 0.615 1.25 0.922 2.188 0.922 0.813 0 1.573-0.297 2.281-0.891v2.031z',
    notContains: 'M5.828 20.469v0.328c0 0.385-0.057 0.667-0.172 0.844-0.052 0.083-0.117 0.177-0.195 0.281s-0.174 0.224-0.289 0.359c-0.458 0.521-1.031 0.771-1.719 0.75-0.521 0-0.927-0.141-1.219-0.422-0.292-0.292-0.438-0.661-0.438-1.109 0-0.156 0.010-0.273 0.031-0.352s0.052-0.141 0.094-0.188 0.094-0.086 0.156-0.117 0.141-0.078 0.234-0.141c0.031-0.031 0.078-0.070 0.141-0.117s0.146-0.086 0.25-0.117h3.125zM14.016 18.328c0.010-0.406 0.070-0.729 0.18-0.969s0.289-0.49 0.539-0.75c0.479-0.604 1.13-0.906 1.953-0.906 0.75 0 1.344 0.292 1.781 0.875 0.198 0.25 0.349 0.495 0.453 0.734s0.172 0.578 0.203 1.016h-5.109zM19.078 20.469c-0.063 0.427-0.146 0.708-0.25 0.844-0.052 0.073-0.109 0.159-0.172 0.258l-0.219 0.352c-0.469 0.688-1.135 1.031-2 1.031-0.708 0-1.297-0.271-1.766-0.813l-0.305-0.359c-0.089-0.104-0.159-0.198-0.211-0.281-0.104-0.167-0.156-0.448-0.156-0.844v-0.188h5.078zM33.344 18.328l-6.875 0c0.031-0.198 0.070-0.372 0.117-0.523s0.107-0.284 0.18-0.398 0.154-0.224 0.242-0.328l0.305-0.344c0.604-0.688 1.391-1.031 2.359-1.031 0.771 0 1.51 0.266 2.219 0.797v-2.234c-0.75-0.333-1.552-0.5-2.406-0.5-1.667 0-2.974 0.531-3.922 1.594-0.396 0.427-0.708 0.859-0.938 1.297s-0.385 0.995-0.469 1.672h-2.719c-0.021-0.719-0.117-1.31-0.289-1.773s-0.424-0.914-0.758-1.352c-0.729-0.938-1.719-1.417-2.969-1.438-1.479 0-2.615 0.708-3.406 2.125v-6.953h-2.266v9.391h-3.75v-0.594c0-2.646-1.25-3.969-3.75-3.969-1.365 0-2.583 0.328-3.656 0.984v2.125c0.99-0.865 2.063-1.297 3.219-1.297 1.344 0 2.016 0.75 2.016 2.25l-2.953 0.125c-0.25 0.021-0.487 0.070-0.711 0.148l-0.633 0.227h-3.328v2.141h1.828l-0.281 0.594c-0.073 0.135-0.109 0.37-0.109 0.703 0 0.938 0.276 1.682 0.828 2.234 0.542 0.573 1.313 0.859 2.313 0.859 1.281 0 2.297-0.635 3.047-1.906v1.656h2.172v-4.141h3.75v4.141h2.297v-1.516c0.677 1.188 1.661 1.776 2.953 1.766 1.385 0 2.464-0.531 3.234-1.594 0.302-0.385 0.557-0.792 0.766-1.219 0.198-0.385 0.339-0.911 0.422-1.578h2.703c0.021 0.708 0.141 1.25 0.359 1.625 0.115 0.198 0.253 0.401 0.414 0.609s0.346 0.427 0.555 0.656c0.906 1 2.099 1.5 3.578 1.5 1.104 0 2.057-0.245 2.859-0.734v-2.109c-0.75 0.604-1.526 0.917-2.328 0.938-0.979 0-1.74-0.318-2.281-0.953l-0.328-0.328c-0.094-0.094-0.177-0.195-0.25-0.305s-0.13-0.234-0.172-0.375-0.073-0.315-0.094-0.523h6.906v-2.141zM33.297 5.688c0.146 0 0.266 0.047 0.359 0.141 0.104 0.104 0.156 0.229 0.156 0.375v23.484c0 0.135-0.052 0.255-0.156 0.359-0.094 0.115-0.214 0.172-0.359 0.172h-35.078c-0.135 0-0.26-0.057-0.375-0.172-0.094-0.115-0.135-0.234-0.125-0.359v-23.484c0-0.104 0.042-0.229 0.125-0.375 0.104-0.094 0.229-0.141 0.375-0.141h35.078z',
    startsWith: 'M6.109 20.688c0 0.813-0.219 1.474-0.656 1.984-0.448 0.531-1.010 0.786-1.688 0.766-0.51 0-0.896-0.141-1.156-0.422-0.302-0.292-0.443-0.667-0.422-1.125 0-0.615 0.151-1.042 0.453-1.281 0.177-0.135 0.378-0.245 0.602-0.328s0.497-0.146 0.82-0.188l2.047-0.313v0.906zM8.203 18.063c0-2.688-1.219-4.031-3.656-4.031-1.333 0-2.51 0.339-3.531 1.016v2.141c0.917-0.885 1.948-1.328 3.094-1.328 1.333 0 2 0.766 2 2.297l-2.891 0.453c-2.115 0.333-3.161 1.516-3.141 3.547 0 0.958 0.266 1.724 0.797 2.297 0.542 0.573 1.292 0.859 2.25 0.859 1.292 0 2.26-0.641 2.906-1.922v1.688h2.172v-7.016zM14.703 16.906c0.479-0.604 1.109-0.906 1.891-0.906 0.76 0 1.344 0.297 1.75 0.891 0.438 0.615 0.656 1.443 0.656 2.484 0 1.219-0.229 2.198-0.688 2.938-0.469 0.719-1.109 1.078-1.922 1.078-0.719 0-1.286-0.281-1.703-0.844-0.448-0.542-0.672-1.208-0.672-2v-1.313c-0.010-0.938 0.219-1.714 0.688-2.328zM16.906 25.313c1.365 0 2.422-0.542 3.172-1.625 0.771-1.115 1.156-2.563 1.156-4.344 0-1.604-0.339-2.885-1.016-3.844-0.698-0.979-1.661-1.469-2.891-1.469-1.438 0-2.531 0.719-3.281 2.156v-7.078h-2.188v15.969h2.172v-1.563c0.667 1.198 1.625 1.797 2.875 1.797zM31.375 24.563c-0.75 0.5-1.672 0.75-2.766 0.75-1.427 0-2.583-0.505-3.469-1.516-0.885-0.969-1.318-2.26-1.297-3.875 0-1.74 0.464-3.161 1.391-4.266 0.927-1.063 2.198-1.604 3.813-1.625 0.844 0 1.62 0.172 2.328 0.516v2.25c-0.688-0.563-1.406-0.828-2.156-0.797-0.927 0-1.688 0.349-2.281 1.047-0.583 0.698-0.875 1.609-0.875 2.734 0 1.094 0.281 1.969 0.844 2.625 0.542 0.656 1.286 0.984 2.234 0.984 0.781 0 1.526-0.323 2.234-0.969v2.141zM22.172 5.844c0.115 0 0.224 0.052 0.328 0.156 0.094 0.125 0.141 0.25 0.141 0.375v23.844c0 0.156-0.047 0.286-0.141 0.391-0.115 0.094-0.224 0.141-0.328 0.141h-23.469c-0.125 0-0.24-0.047-0.344-0.141-0.094-0.104-0.141-0.234-0.141-0.391v-23.844c0-0.125 0.047-0.25 0.141-0.375 0.104-0.104 0.219-0.156 0.344-0.156h23.469z',
    endsWith: 'M6.234 19.344l-2.047 0.313c-0.625 0.083-1.104 0.26-1.438 0.531-0.302 0.24-0.453 0.651-0.453 1.234 0 0.469 0.141 0.852 0.422 1.148s0.672 0.435 1.172 0.414c0.677 0 1.234-0.25 1.672-0.75 0.448-0.51 0.672-1.167 0.672-1.969v-0.922zM8.359 24.578h-2.141v-1.656c-0.667 1.26-1.656 1.891-2.969 1.891-0.938 0-1.698-0.276-2.281-0.828-0.542-0.573-0.813-1.328-0.813-2.266 0-2.021 1.063-3.188 3.188-3.5l2.891-0.484c0-1.51-0.661-2.266-1.984-2.266-1.167 0-2.214 0.443-3.141 1.328v-2.125c1.042-0.677 2.224-1.016 3.547-1.016 2.469 0 3.703 1.333 3.703 4v6.922zM14.906 16.516c0.49-0.615 1.13-0.922 1.922-0.922 0.76 0 1.339 0.297 1.734 0.891 0.438 0.615 0.656 1.438 0.656 2.469 0 1.208-0.229 2.182-0.688 2.922-0.469 0.698-1.115 1.047-1.938 1.047-0.708 0-1.276-0.276-1.703-0.828-0.458-0.552-0.688-1.214-0.688-1.984v-1.281c-0.010-0.948 0.224-1.719 0.703-2.313zM17.125 24.813c1.354 0 2.417-0.531 3.188-1.594 0.781-1.073 1.172-2.505 1.172-4.297 0-1.604-0.349-2.87-1.047-3.797-0.698-0.979-1.661-1.469-2.891-1.469-1.438 0-2.542 0.714-3.313 2.141v-7h-2.203v15.781h2.188v-1.531c0.677 1.177 1.646 1.766 2.906 1.766zM31.688 21.969c-0.698 0.635-1.453 0.953-2.266 0.953-0.958 0-1.703-0.323-2.234-0.969-0.563-0.667-0.849-1.536-0.859-2.609 0-1.115 0.297-2.016 0.891-2.703 0.594-0.698 1.359-1.047 2.297-1.047 0.76 0 1.484 0.266 2.172 0.797v-2.219c-0.708-0.344-1.49-0.516-2.344-0.516-1.625 0-2.906 0.536-3.844 1.609-0.938 1.083-1.406 2.495-1.406 4.234 0 1.594 0.438 2.875 1.313 3.844 0.885 0.979 2.052 1.469 3.5 1.469 1.083 0 2.010-0.245 2.781-0.734v-2.109zM33.188 5.563c0.104 0 0.219 0.047 0.344 0.141 0.094 0.146 0.141 0.276 0.141 0.391v23.578c0 0.146-0.047 0.281-0.141 0.406-0.125 0.094-0.24 0.141-0.344 0.141h-23.625c-0.125 0-0.24-0.047-0.344-0.141-0.094-0.135-0.135-0.271-0.125-0.406v-23.578c0-0.115 0.042-0.245 0.125-0.391 0.094-0.094 0.208-0.141 0.344-0.141h23.625z',
    equal: 'M29.438 11.797v2.75h-26.922v-2.75h26.922zM29.438 17.406v2.75h-26.922v-2.75h26.922z',
    notEqual: 'M16.906 11.797l3.016-6.547 2.094 1-2.547 5.547h9.969v2.75h-11.234l-1.328 2.859h12.563v2.75h-13.828l-2.875 6.281-2.094-0.984 2.438-5.297h-10.563v-2.75h11.828l1.297-2.859h-13.125v-2.75h14.391z',
    greaterThan: 'M24.125 16.047l-14.906 8.625-1.375-2.375 10.781-6.25-10.781-6.234 1.375-2.375z',
    greaterThanOrEqual: 'M23.031 14.328l-14.906 8.625-1.375-2.375 10.797-6.25-10.797-6.234 1.375-2.375zM23.828 15.641l1.375 2.391-14.938 8.609-1.375-2.375z',
    lessThan: 'M22.75 7.438l1.375 2.375-10.781 6.234 10.781 6.25-1.375 2.375-14.906-8.609z',
    lessThanOrEqual: 'M23.828 5.719l1.375 2.375-10.813 6.234 10.813 6.25-1.375 2.375-14.922-8.609zM23.047 24.266l-1.375 2.375-14.922-8.609 1.375-2.391z'
  };
  var Icon = function Icon(_ref) {
    var type = _ref.type,
        className = _ref.className,
        restProps = _objectWithoutProperties(_ref, ["type", "className"]);

    var path = AVAILABLE_PATHS[type];
    return path ? React.createElement("svg", _extends({
      className: classNames('d-block dx-g-bs4-filter-selector-icon', className),
      viewBox: "0 0 32 32"
    }, restProps), React.createElement("path", {
      d: path
    })) : React.createElement("span", _extends({
      className: classNames('d-block', 'oi', 'oi-magnifying-glass', 'dx-g-bs4-filter-selector-icon', className)
    }, restProps));
  };
  process.env.NODE_ENV !== "production" ? Icon.propTypes = {
    type: PropTypes.string,
    className: PropTypes.string
  } : void 0;
  Icon.defaultProps = {
    type: undefined,
    className: undefined
  };

  var TableFilterRow = dxReactCore.withComponents({
    Row: TableRow,
    Cell: TableFilterCell,
    Editor: Editor,
    FilterSelector: FilterSelector,
    Icon: Icon,
    ToggleButton: ToggleButton$1
  })(dxReactGrid.TableFilterRow);
  TableFilterRow.ROW_TYPE = dxReactGrid.TableFilterRow.ROW_TYPE;

  var ResizingControl =
  /*#__PURE__*/
  function (_React$PureComponent) {
    _inherits(ResizingControl, _React$PureComponent);

    function ResizingControl(props) {
      var _this;

      _classCallCheck(this, ResizingControl);

      _this = _possibleConstructorReturn(this, _getPrototypeOf(ResizingControl).call(this, props));
      _this.state = {
        resizing: false
      };

      _this.onResizeStart = function (_ref) {
        var x = _ref.x;
        _this.resizeStartingX = x;

        _this.setState({
          resizing: true
        });
      };

      _this.onResizeUpdate = function (_ref2) {
        var x = _ref2.x;
        var onWidthDraft = _this.props.onWidthDraft;
        onWidthDraft({
          shift: x - _this.resizeStartingX
        });
      };

      _this.onResizeEnd = function (_ref3) {
        var x = _ref3.x;
        var _this$props = _this.props,
            onWidthChange = _this$props.onWidthChange,
            onWidthDraftCancel = _this$props.onWidthDraftCancel;
        onWidthDraftCancel();
        onWidthChange({
          shift: x - _this.resizeStartingX
        });

        _this.setState({
          resizing: false
        });
      };

      return _this;
    }

    _createClass(ResizingControl, [{
      key: "render",
      value: function render() {
        var resizing = this.state.resizing;
        return React.createElement(dxReactCore.Draggable, {
          onStart: this.onResizeStart,
          onUpdate: this.onResizeUpdate,
          onEnd: this.onResizeEnd
        }, React.createElement("div", {
          className: classNames({
            'dx-g-bs4-resizing-control-wrapper': true,
            'dx-g-bs4-resizing-control-wrapper-active': resizing
          })
        }, React.createElement("div", {
          className: classNames({
            'dx-g-bs4-resize-control-line dx-g-bs4-resize-control-line-first bg-primary': true,
            'dx-g-bs4-resize-control-line-active': resizing
          })
        }), React.createElement("div", {
          className: classNames({
            'dx-g-bs4-resize-control-line dx-g-bs4-resize-control-line-second bg-primary': true,
            'dx-g-bs4-resize-control-line-active': resizing
          })
        })));
      }
    }]);

    return ResizingControl;
  }(React.PureComponent);
  process.env.NODE_ENV !== "production" ? ResizingControl.propTypes = {
    onWidthChange: PropTypes.func.isRequired,
    onWidthDraft: PropTypes.func.isRequired,
    onWidthDraftCancel: PropTypes.func.isRequired
  } : void 0;

  var TableHeaderCell =
  /*#__PURE__*/
  function (_React$PureComponent) {
    _inherits(TableHeaderCell, _React$PureComponent);

    function TableHeaderCell(props) {
      var _this;

      _classCallCheck(this, TableHeaderCell);

      _this = _possibleConstructorReturn(this, _getPrototypeOf(TableHeaderCell).call(this, props));
      _this.state = {
        dragging: false
      };
      _this.cellRef = React.createRef();

      _this.onDragStart = function () {
        _this.setState({
          dragging: true
        });
      };

      _this.onDragEnd = function () {
        if (_this.cellRef.current) {
          _this.setState({
            dragging: false
          });
        }
      };

      return _this;
    }

    _createClass(TableHeaderCell, [{
      key: "render",
      value: function render() {
        var _this$props = this.props,
            className = _this$props.className,
            column = _this$props.column,
            tableColumn = _this$props.tableColumn,
            showGroupingControls = _this$props.showGroupingControls,
            onGroup = _this$props.onGroup,
            groupingEnabled = _this$props.groupingEnabled,
            draggingEnabled = _this$props.draggingEnabled,
            onWidthDraftCancel = _this$props.onWidthDraftCancel,
            resizingEnabled = _this$props.resizingEnabled,
            onWidthChange = _this$props.onWidthChange,
            onWidthDraft = _this$props.onWidthDraft,
            tableRow = _this$props.tableRow,
            getMessage = _this$props.getMessage,
            children = _this$props.children,
            showSortingControls = _this$props.showSortingControls,
            sortingDirection = _this$props.sortingDirection,
            sortingEnabled = _this$props.sortingEnabled,
            onSort = _this$props.onSort,
            before = _this$props.before,
            restProps = _objectWithoutProperties(_this$props, ["className", "column", "tableColumn", "showGroupingControls", "onGroup", "groupingEnabled", "draggingEnabled", "onWidthDraftCancel", "resizingEnabled", "onWidthChange", "onWidthDraft", "tableRow", "getMessage", "children", "showSortingControls", "sortingDirection", "sortingEnabled", "onSort", "before"]);

        var dragging = this.state.dragging;
        var cellLayout = React.createElement("th", _extends({
          className: classNames({
            'position-relative dx-g-bs4-header-cell': true,
            'dx-g-bs4-user-select-none': draggingEnabled,
            'dx-g-bs4-cursor-pointer': draggingEnabled,
            'dx-g-bs4-inactive': dragging || tableColumn && tableColumn.draft,
            'text-nowrap': !(tableColumn && tableColumn.wordWrapEnabled)
          }, className),
          scope: "col"
        }, restProps), React.createElement("div", {
          className: "d-flex flex-direction-row align-items-center"
        }, children), resizingEnabled && React.createElement(ResizingControl, {
          onWidthChange: onWidthChange,
          onWidthDraft: onWidthDraft,
          onWidthDraftCancel: onWidthDraftCancel
        }));
        return draggingEnabled ? React.createElement(dxReactCore.DragSource, {
          ref: this.cellRef,
          payload: [{
            type: 'column',
            columnName: column.name
          }],
          onStart: this.onDragStart,
          onEnd: this.onDragEnd
        }, cellLayout) : cellLayout;
      }
    }]);

    return TableHeaderCell;
  }(React.PureComponent);
  process.env.NODE_ENV !== "production" ? TableHeaderCell.propTypes = {
    before: PropTypes.node,
    tableColumn: PropTypes.object,
    tableRow: PropTypes.object,
    column: PropTypes.object,
    className: PropTypes.string,
    showSortingControls: PropTypes.bool,
    sortingEnabled: PropTypes.bool,
    sortingDirection: PropTypes.oneOf(['asc', 'desc', null]),
    onSort: PropTypes.func,
    showGroupingControls: PropTypes.bool,
    onGroup: PropTypes.func,
    groupingEnabled: PropTypes.bool,
    draggingEnabled: PropTypes.bool,
    resizingEnabled: PropTypes.bool,
    onWidthChange: PropTypes.func,
    onWidthDraft: PropTypes.func,
    onWidthDraftCancel: PropTypes.func,
    getMessage: PropTypes.func,
    children: PropTypes.oneOfType([PropTypes.arrayOf(PropTypes.node), PropTypes.node])
  } : void 0;
  TableHeaderCell.defaultProps = {
    before: undefined,
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
    getMessage: undefined,
    children: undefined
  };

  var handleMouseDown$2 = function handleMouseDown(e) {
    e.currentTarget.style.outline = 'none';
  };

  var handleBlur$2 = function handleBlur(e) {
    e.currentTarget.style.outline = '';
  };

  var ENTER_KEY_CODE$2 = 13;
  var SPACE_KEY_CODE$2 = 32;

  var _onClick = function onClick(e, onSort) {
    var isActionKeyDown = e.keyCode === ENTER_KEY_CODE$2 || e.keyCode === SPACE_KEY_CODE$2;
    var isMouseClick = e.keyCode === undefined;
    if (!(isActionKeyDown || isMouseClick)) return;
    var cancelSortingRelatedKey = e.metaKey || e.ctrlKey;
    var direction = (isMouseClick || isActionKeyDown) && cancelSortingRelatedKey ? null : undefined;
    var keepOther = e.shiftKey || cancelSortingRelatedKey;
    e.preventDefault();
    onSort({
      direction: direction,
      keepOther: keepOther
    });
  };

  var SortLabel = function SortLabel(_ref) {
    var align = _ref.align,
        direction = _ref.direction,
        disabled = _ref.disabled,
        children = _ref.children,
        onSort = _ref.onSort,
        getMessage = _ref.getMessage,
        className = _ref.className,
        column = _ref.column,
        restProps = _objectWithoutProperties(_ref, ["align", "direction", "disabled", "children", "onSort", "getMessage", "className", "column"]);

    return React.createElement("span", _extends({
      className: classNames({
        'd-inline-flex flex-direction-row align-items-center mw-100 dx-g-bs4-user-select-none': true,
        'dx-g-bs4-cursor-pointer': !disabled,
        'flex-row-reverse': align === 'right',
        'text-primary': direction
      }, className),
      tabIndex: disabled ? -1 : 0,
      onMouseDown: handleMouseDown$2,
      onBlur: handleBlur$2
    }, !disabled ? {
      onKeyDown: function onKeyDown(e) {
        return _onClick(e, onSort);
      },
      onClick: function onClick(e) {
        return _onClick(e, onSort);
      }
    } : null, restProps), children, React.createElement(SortingIndicator, {
      direction: direction,
      className: direction ? '' : 'dx-g-bs4-sort-indicator-invisible'
    }));
  };
  process.env.NODE_ENV !== "production" ? SortLabel.propTypes = {
    column: PropTypes.object,
    align: PropTypes.string,
    direction: PropTypes.oneOf(['asc', 'desc']),
    children: PropTypes.node,
    onSort: PropTypes.func.isRequired,
    disabled: PropTypes.bool,
    className: PropTypes.string,
    getMessage: PropTypes.func
  } : void 0;
  SortLabel.defaultProps = {
    column: undefined,
    direction: null,
    disabled: false,
    children: undefined,
    className: undefined,
    align: 'left',
    getMessage: function getMessage() {}
  };

  var GroupButton = function GroupButton(_ref) {
    var disabled = _ref.disabled,
        onGroup = _ref.onGroup,
        className = _ref.className,
        restProps = _objectWithoutProperties(_ref, ["disabled", "onGroup", "className"]);

    return React.createElement("div", _extends({
      className: classNames({
        'dx-g-bs4-grouping-control': true
      }, className),
      onClick: function onClick(e) {
        if (disabled) return;
        e.stopPropagation();
        onGroup();
      }
    }, restProps), React.createElement("span", {
      className: classNames({
        'oi oi-list dx-g-bs4-grouping-control-icon': true,
        'dx-g-bs4-cursor-pointer': !disabled,
        'dx-g-bs4-inactive': disabled
      })
    }));
  };
  process.env.NODE_ENV !== "production" ? GroupButton.propTypes = {
    onGroup: PropTypes.func.isRequired,
    disabled: PropTypes.bool,
    className: PropTypes.string
  } : void 0;
  GroupButton.defaultProps = {
    disabled: false,
    className: undefined
  };

  var Title = function Title(_ref) {
    var children = _ref.children,
        className = _ref.className,
        restProps = _objectWithoutProperties(_ref, ["children", "className"]);

    return React.createElement("span", _extends({
      className: classNames('dx-rg-bs4-table-header-title', className)
    }, restProps), children);
  };
  process.env.NODE_ENV !== "production" ? Title.propTypes = {
    children: PropTypes.oneOfType([PropTypes.arrayOf(PropTypes.node), PropTypes.node]),
    className: PropTypes.string
  } : void 0;
  Title.defaultProps = {
    className: null,
    children: undefined
  };

  var Content$1 = function Content(_ref) {
    var column = _ref.column,
        children = _ref.children,
        align = _ref.align,
        className = _ref.className,
        restProps = _objectWithoutProperties(_ref, ["column", "children", "align", "className"]);

    return React.createElement("div", _extends({
      className: classNames({
        'w-100 d-flex flex-row align-items-end': true,
        'justify-content-center': align === 'center',
        'justify-content-end': align === 'right'
      }, className)
    }, restProps), children);
  };
  process.env.NODE_ENV !== "production" ? Content$1.propTypes = {
    column: PropTypes.object,
    children: PropTypes.oneOfType([PropTypes.arrayOf(PropTypes.node), PropTypes.node]),
    align: PropTypes.string,
    className: PropTypes.string
  } : void 0;
  Content$1.defaultProps = {
    column: undefined,
    align: 'left',
    className: null,
    children: undefined
  };

  var TableHeaderRow = dxReactCore.withComponents({
    Cell: TableHeaderCell,
    Row: TableRow,
    Content: Content$1,
    SortLabel: SortLabel,
    Title: Title,
    GroupButton: GroupButton
  })(dxReactGrid.TableHeaderRow);
  TableHeaderRow.ROW_TYPE = dxReactGrid.TableHeaderRow.ROW_TYPE;

  var Cell$1 = function Cell(_ref) {
    var column = _ref.column,
        children = _ref.children,
        beforeBorder = _ref.beforeBorder,
        tableRow = _ref.tableRow,
        tableColumn = _ref.tableColumn,
        row = _ref.row,
        className = _ref.className,
        restProps = _objectWithoutProperties(_ref, ["column", "children", "beforeBorder", "tableRow", "tableColumn", "row", "className"]);

    return React.createElement("th", _extends({
      className: classNames({
        'dx-g-bs4-banded-cell dx-g-bs4-table-cell text-nowrap border-right': true,
        'border-left': beforeBorder
      }, className)
    }, restProps), children);
  };
  process.env.NODE_ENV !== "production" ? Cell$1.propTypes = {
    column: PropTypes.object,
    row: PropTypes.any,
    children: PropTypes.oneOfType([PropTypes.node, PropTypes.arrayOf(PropTypes.node)]),
    tableRow: PropTypes.object,
    tableColumn: PropTypes.object,
    className: PropTypes.string,
    beforeBorder: PropTypes.bool
  } : void 0;
  Cell$1.defaultProps = {
    column: undefined,
    row: undefined,
    children: undefined,
    tableRow: undefined,
    tableColumn: undefined,
    className: undefined,
    beforeBorder: false
  };

  var BandedHeaderCell = function BandedHeaderCell(_ref) {
    var HeaderCellComponent = _ref.component,
        className = _ref.className,
        beforeBorder = _ref.beforeBorder,
        restProps = _objectWithoutProperties(_ref, ["component", "className", "beforeBorder"]);

    return React.createElement(HeaderCellComponent, _extends({
      className: classNames({
        'dx-g-bs4-banded-header-cell border-right': true,
        'border-left': beforeBorder
      }, className)
    }, restProps));
  };
  process.env.NODE_ENV !== "production" ? BandedHeaderCell.propTypes = {
    component: PropTypes.func.isRequired,
    className: PropTypes.string,
    beforeBorder: PropTypes.bool
  } : void 0;
  BandedHeaderCell.defaultProps = {
    className: undefined,
    beforeBorder: false
  };

  var InvisibleCell = function InvisibleCell() {
    return React.createElement("th", {
      className: "d-none"
    });
  };

  var TableBandHeader = dxReactCore.withComponents({
    Cell: Cell$1,
    Row: TableRow,
    BandedHeaderCell: BandedHeaderCell,
    InvisibleCell: InvisibleCell
  })(dxReactGrid.TableBandHeader);
  TableBandHeader.ROW_TYPE = dxReactGrid.TableBandHeader.ROW_TYPE;

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
        restProps = _objectWithoutProperties(_ref, ["column", "value", "onValueChange", "className", "children", "row", "tableRow", "tableColumn", "editingEnabled"]);

    return React.createElement("td", _extends({
      className: classNames('align-middle dx-g-bs4-table-edit-cell', className)
    }, restProps), children || React.createElement("input", {
      type: "text",
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
    }));
  };
  process.env.NODE_ENV !== "production" ? EditCell.propTypes = {
    column: PropTypes.object,
    row: PropTypes.any,
    tableColumn: PropTypes.object,
    tableRow: PropTypes.object,
    value: PropTypes.any,
    onValueChange: PropTypes.func.isRequired,
    className: PropTypes.string,
    editingEnabled: PropTypes.bool,
    children: PropTypes.node
  } : void 0;
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

  var TableEditRow = dxReactCore.withComponents({
    Row: TableRow,
    Cell: EditCell
  })(dxReactGrid.TableEditRow);
  TableEditRow.ADDED_ROW_TYPE = dxReactGrid.TableEditRow.ADDED_ROW_TYPE;
  TableEditRow.EDIT_ROW_TYPE = dxReactGrid.TableEditRow.EDIT_ROW_TYPE;

  var CommandButton = function CommandButton(_ref) {
    var onExecute = _ref.onExecute,
        text = _ref.text,
        className = _ref.className,
        restProps = _objectWithoutProperties(_ref, ["onExecute", "text", "className"]);

    return React.createElement("button", _extends({
      type: "button",
      className: classNames('btn btn-link dx-g-bs4-table-edit-command-cell', className),
      onClick: function onClick(e) {
        e.stopPropagation();
        onExecute();
      }
    }, restProps), text);
  };
  process.env.NODE_ENV !== "production" ? CommandButton.propTypes = {
    text: PropTypes.string.isRequired,
    onExecute: PropTypes.func.isRequired,
    className: PropTypes.string
  } : void 0;
  CommandButton.defaultProps = {
    className: undefined
  };
  var EditCommandHeadingCell = function EditCommandHeadingCell(_ref2) {
    var children = _ref2.children,
        className = _ref2.className,
        tableColumn = _ref2.tableColumn,
        tableRow = _ref2.tableRow,
        restProps = _objectWithoutProperties(_ref2, ["children", "className", "tableColumn", "tableRow"]);

    return React.createElement("th", _extends({
      className: classNames('text-center p-0 text-nowrap', className)
    }, restProps), children);
  };
  process.env.NODE_ENV !== "production" ? EditCommandHeadingCell.propTypes = {
    children: PropTypes.oneOfType([PropTypes.arrayOf(PropTypes.node), PropTypes.node]),
    tableColumn: PropTypes.object,
    tableRow: PropTypes.object,
    className: PropTypes.string
  } : void 0;
  EditCommandHeadingCell.defaultProps = {
    children: undefined,
    tableColumn: undefined,
    tableRow: undefined,
    className: undefined
  };
  var EditCommandCell = function EditCommandCell(_ref3) {
    var tableColumn = _ref3.tableColumn,
        tableRow = _ref3.tableRow,
        row = _ref3.row,
        children = _ref3.children,
        className = _ref3.className,
        restProps = _objectWithoutProperties(_ref3, ["tableColumn", "tableRow", "row", "children", "className"]);

    return React.createElement("td", _extends({
      className: classNames('text-center p-0 text-nowrap', className)
    }, restProps), children);
  };
  process.env.NODE_ENV !== "production" ? EditCommandCell.propTypes = {
    children: PropTypes.oneOfType([PropTypes.arrayOf(PropTypes.node), PropTypes.node]),
    tableColumn: PropTypes.object,
    tableRow: PropTypes.object,
    row: PropTypes.any,
    className: PropTypes.string
  } : void 0;
  EditCommandCell.defaultProps = {
    children: undefined,
    tableColumn: undefined,
    tableRow: undefined,
    row: undefined,
    className: undefined
  };

  var TableEditColumn = dxReactCore.withComponents({
    Cell: EditCommandCell,
    HeaderCell: EditCommandHeadingCell,
    Command: CommandButton
  })(dxReactGrid.TableEditColumn);
  TableEditColumn.COLUMN_TYPE = dxReactGrid.TableEditColumn.COLUMN_TYPE;

  var EmptyMessage = function EmptyMessage(_ref) {
    var getMessage = _ref.getMessage,
        className = _ref.className,
        restProps = _objectWithoutProperties(_ref, ["getMessage", "className"]);

    return React.createElement("div", _extends({
      className: classNames('py-5 text-center', className)
    }, restProps), React.createElement("big", {
      className: "text-muted"
    }, getMessage('noColumns')));
  };
  process.env.NODE_ENV !== "production" ? EmptyMessage.propTypes = {
    getMessage: PropTypes.func.isRequired,
    className: PropTypes.string
  } : void 0;
  EmptyMessage.defaultProps = {
    className: undefined
  };

  var TableColumnVisibility = dxReactCore.withComponents({
    EmptyMessage: EmptyMessage
  })(dxReactGrid.TableColumnVisibility);

  var TableInvisibleRow = function TableInvisibleRow(_ref) {
    var className = _ref.className,
        restParams = _objectWithoutProperties(_ref, ["className"]);

    return React.createElement(TableRow, _extends({
      className: classNames('dx-g-bs4-table-invisible-row', className)
    }, restParams));
  };
  process.env.NODE_ENV !== "production" ? TableInvisibleRow.propTypes = {
    className: PropTypes.string
  } : void 0;
  TableInvisibleRow.defaultProps = {
    className: undefined
  };

  var TableReorderingCell = function TableReorderingCell(_ref) {
    var style = _ref.style,
        getCellDimensions = _ref.getCellDimensions;

    var refHandler = function refHandler(node) {
      return node && getCellDimensions(function () {
        var _node$getBoundingClie = node.getBoundingClientRect(),
            left = _node$getBoundingClie.left,
            right = _node$getBoundingClie.right;

        return {
          left: left,
          right: right
        };
      });
    };

    return React.createElement("td", {
      ref: refHandler,
      className: "p-0 border-0",
      style: style
    });
  };
  process.env.NODE_ENV !== "production" ? TableReorderingCell.propTypes = {
    getCellDimensions: PropTypes.func.isRequired,
    style: PropTypes.object
  } : void 0;
  TableReorderingCell.defaultProps = {
    style: null
  };

  var TableColumnReordering = dxReactCore.withComponents({
    Row: TableInvisibleRow,
    Cell: TableReorderingCell
  })(dxReactGrid.TableColumnReordering);

  var TableColumnResizing =
  /*#__PURE__*/
  function (_React$PureComponent) {
    _inherits(TableColumnResizing, _React$PureComponent);

    function TableColumnResizing() {
      _classCallCheck(this, TableColumnResizing);

      return _possibleConstructorReturn(this, _getPrototypeOf(TableColumnResizing).apply(this, arguments));
    }

    _createClass(TableColumnResizing, [{
      key: "render",
      value: function render() {
        var _this$props = this.props,
            minColumnWidth = _this$props.minColumnWidth,
            restProps = _objectWithoutProperties(_this$props, ["minColumnWidth"]);

        return React.createElement(dxReactGrid.TableColumnResizing, _extends({}, restProps, {
          minColumnWidth: minColumnWidth
        }));
      }
    }]);

    return TableColumnResizing;
  }(React.PureComponent);
  process.env.NODE_ENV !== "production" ? TableColumnResizing.propTypes = {
    minColumnWidth: PropTypes.number
  } : void 0;
  TableColumnResizing.defaultProps = {
    minColumnWidth: 55
  };

  var Toolbar = function Toolbar(_ref) {
    var children = _ref.children,
        className = _ref.className,
        style = _ref.style,
        restProps = _objectWithoutProperties(_ref, ["children", "className", "style"]);

    return React.createElement("div", _extends({
      className: classNames('card-header py-2 d-flex position-relative dx-g-bs4-toolbar', className),
      style: style
    }, restProps), children);
  };
  process.env.NODE_ENV !== "production" ? Toolbar.propTypes = {
    children: PropTypes.oneOfType([PropTypes.arrayOf(PropTypes.node), PropTypes.node]).isRequired,
    className: PropTypes.string,
    style: PropTypes.object
  } : void 0;
  Toolbar.defaultProps = {
    className: undefined,
    style: null
  };

  var FlexibleSpace = function FlexibleSpace() {
    return React.createElement("div", {
      className: "d-flex ml-auto"
    });
  };

  var Toolbar$1 = dxReactCore.withComponents({
    Root: Toolbar,
    FlexibleSpace: FlexibleSpace
  })(dxReactGrid.Toolbar);

  var TableTreeExpandButton = function TableTreeExpandButton(_ref) {
    var className = _ref.className,
        visible = _ref.visible,
        expanded = _ref.expanded,
        onToggle = _ref.onToggle,
        restProps = _objectWithoutProperties(_ref, ["className", "visible", "expanded", "onToggle"]);

    return React.createElement(ExpandButton, _extends({
      visible: visible,
      expanded: expanded,
      onToggle: onToggle,
      className: classNames('mr-3', className)
    }, restProps));
  };
  process.env.NODE_ENV !== "production" ? TableTreeExpandButton.propTypes = {
    className: PropTypes.string,
    visible: PropTypes.bool,
    expanded: PropTypes.bool,
    onToggle: PropTypes.func
  } : void 0;
  TableTreeExpandButton.defaultProps = {
    className: undefined,
    visible: false,
    expanded: false,
    onToggle: function onToggle() {}
  };

  var TableTreeCheckbox = function TableTreeCheckbox(_ref) {
    var className = _ref.className,
        checked = _ref.checked,
        indeterminate = _ref.indeterminate,
        disabled = _ref.disabled,
        onChange = _ref.onChange,
        restProps = _objectWithoutProperties(_ref, ["className", "checked", "indeterminate", "disabled", "onChange"]);

    return React.createElement(SelectionControl, _extends({
      disabled: disabled,
      checked: checked,
      indeterminate: indeterminate,
      onChange: onChange,
      className: classNames('mr-4', className)
    }, restProps));
  };
  process.env.NODE_ENV !== "production" ? TableTreeCheckbox.propTypes = {
    className: PropTypes.string,
    checked: PropTypes.bool,
    indeterminate: PropTypes.bool,
    disabled: PropTypes.bool,
    onChange: PropTypes.func
  } : void 0;
  TableTreeCheckbox.defaultProps = {
    className: undefined,
    checked: false,
    indeterminate: false,
    disabled: false,
    onChange: function onChange() {}
  };

  var TableTreeIndent = function TableTreeIndent(_ref) {
    var level = _ref.level;
    return Array.from({
      length: level
    }).map(function (value, currentLevel) {
      return React.createElement("span", {
        // eslint-disable-next-line react/no-array-index-key
        key: currentLevel,
        className: "d-inline-block mr-4"
      });
    });
  };
  process.env.NODE_ENV !== "production" ? TableTreeIndent.propTypes = {
    level: PropTypes.number
  } : void 0;
  TableTreeIndent.defaultProps = {
    level: 0
  };

  var TableTreeCell = function TableTreeCell(_ref) {
    var column = _ref.column,
        children = _ref.children,
        tableRow = _ref.tableRow,
        tableColumn = _ref.tableColumn,
        row = _ref.row,
        restProps = _objectWithoutProperties(_ref, ["column", "children", "tableRow", "tableColumn", "row"]);

    return React.createElement("td", restProps, React.createElement("div", {
      className: classNames({
        'd-flex flex-direction-row align-items-center': true,
        'text-nowrap': !(tableColumn && tableColumn.wordWrapEnabled),
        'text-right': tableColumn && tableColumn.align === 'right',
        'text-center': tableColumn && tableColumn.align === 'center'
      })
    }, children));
  };
  process.env.NODE_ENV !== "production" ? TableTreeCell.propTypes = {
    column: PropTypes.object,
    row: PropTypes.any,
    children: PropTypes.node,
    tableRow: PropTypes.object,
    tableColumn: PropTypes.object,
    style: PropTypes.object
  } : void 0;
  TableTreeCell.defaultProps = {
    column: undefined,
    row: undefined,
    children: undefined,
    tableRow: undefined,
    tableColumn: undefined,
    style: null
  };

  var TableTreeContent = function TableTreeContent(_ref) {
    var children = _ref.children,
        className = _ref.className,
        restProps = _objectWithoutProperties(_ref, ["children", "className"]);

    return React.createElement("div", _extends({
      className: classNames('w-100 dx-g-bs4-table-tree-content', className)
    }, restProps), children);
  };
  process.env.NODE_ENV !== "production" ? TableTreeContent.propTypes = {
    className: PropTypes.string,
    children: PropTypes.node
  } : void 0;
  TableTreeContent.defaultProps = {
    className: undefined,
    children: undefined
  };

  var TableTreeColumn = dxReactCore.withComponents({
    Cell: TableTreeCell,
    Content: TableTreeContent,
    Indent: TableTreeIndent,
    ExpandButton: TableTreeExpandButton,
    Checkbox: TableTreeCheckbox
  })(dxReactGrid.TableTreeColumn);

  var SearchPanelInput = function SearchPanelInput(_ref) {
    var onValueChange = _ref.onValueChange,
        value = _ref.value,
        getMessage = _ref.getMessage,
        className = _ref.className,
        restProps = _objectWithoutProperties(_ref, ["onValueChange", "value", "getMessage", "className"]);

    return React.createElement("input", _extends({
      type: "text",
      className: classNames('form-control w-25', className),
      onChange: function onChange(e) {
        return onValueChange(e.target.value);
      },
      value: value,
      placeholder: getMessage('searchPlaceholder')
    }, restProps));
  };
  process.env.NODE_ENV !== "production" ? SearchPanelInput.propTypes = {
    value: PropTypes.any,
    onValueChange: PropTypes.func.isRequired,
    getMessage: PropTypes.func.isRequired,
    className: PropTypes.string
  } : void 0;
  SearchPanelInput.defaultProps = {
    value: null,
    className: undefined
  };

  var SearchPanel = dxReactCore.withComponents({
    Input: SearchPanelInput
  })(dxReactGrid.SearchPanel);

  var FixedCell =
  /*#__PURE__*/
  function (_React$PureComponent) {
    _inherits(FixedCell, _React$PureComponent);

    function FixedCell() {
      _classCallCheck(this, FixedCell);

      return _possibleConstructorReturn(this, _getPrototypeOf(FixedCell).apply(this, arguments));
    }

    _createClass(FixedCell, [{
      key: "render",
      value: function render() {
        var _this$props = this.props,
            CellPlaceholder = _this$props.component,
            side = _this$props.side,
            showLeftDivider = _this$props.showLeftDivider,
            showRightDivider = _this$props.showRightDivider,
            className = _this$props.className,
            style = _this$props.style,
            position = _this$props.position,
            restProps = _objectWithoutProperties(_this$props, ["component", "side", "showLeftDivider", "showRightDivider", "className", "style", "position"]);

        var backgroundColor = this.context;
        return React.createElement(CellPlaceholder, _extends({
          className: classNames({
            'position-sticky': true,
            'dx-g-bs4-fixed-cell': true,
            'border-left': showLeftDivider,
            'border-right': showRightDivider
          }, className),
          style: _objectSpread({}, style, _defineProperty({
            backgroundColor: backgroundColor
          }, side, position))
        }, restProps));
      }
    }]);

    return FixedCell;
  }(React.PureComponent);
  FixedCell.contextType = BodyColorContext;
  process.env.NODE_ENV !== "production" ? FixedCell.propTypes = {
    className: PropTypes.string,
    style: PropTypes.object,
    component: PropTypes.func.isRequired,
    side: PropTypes.string.isRequired,
    position: PropTypes.number,
    showLeftDivider: PropTypes.bool,
    showRightDivider: PropTypes.bool
  } : void 0;
  FixedCell.defaultProps = {
    className: undefined,
    style: null,
    showLeftDivider: false,
    showRightDivider: false,
    position: undefined
  };

  var TableBorderlessStubCell = function TableBorderlessStubCell(_ref) {
    var className = _ref.className,
        restProps = _objectWithoutProperties(_ref, ["className"]);

    return React.createElement(TableStubCell, _extends({
      className: classNames('border-0', className)
    }, restProps));
  };

  process.env.NODE_ENV !== "production" ? TableBorderlessStubCell.propTypes = {
    className: PropTypes.string
  } : void 0;
  TableBorderlessStubCell.defaultProps = {
    className: undefined
  };
  var TableListenerCell = function TableListenerCell(_ref2) {
    var listen = _ref2.listen,
        onSizeChange = _ref2.onSizeChange,
        restProps = _objectWithoutProperties(_ref2, ["listen", "onSizeChange"]);

    return listen ? React.createElement(dxReactCore.Sizer, _extends({
      containerComponent: TableBorderlessStubCell,
      onSizeChange: onSizeChange
    }, restProps)) : React.createElement(TableBorderlessStubCell, restProps);
  };
  process.env.NODE_ENV !== "production" ? TableListenerCell.propTypes = {
    listen: PropTypes.bool.isRequired,
    onSizeChange: PropTypes.func.isRequired
  } : void 0;

  var TableFixedColumns = dxReactCore.withComponents({
    Cell: FixedCell,
    ListenerRow: TableInvisibleRow,
    ListenerCell: TableListenerCell
  })(dxReactGrid.TableFixedColumns);

  var TableSummaryItem = function TableSummaryItem(_ref) {
    var children = _ref.children,
        type = _ref.type,
        value = _ref.value,
        getMessage = _ref.getMessage,
        className = _ref.className,
        restProps = _objectWithoutProperties(_ref, ["children", "type", "value", "getMessage", "className"]);

    return React.createElement("div", _extends({
      className: classNames('dx-g-bs4-table-summary-item', className)
    }, restProps), React.createElement(React.Fragment, null, getMessage(type), ":\xA0\xA0", children));
  };
  process.env.NODE_ENV !== "production" ? TableSummaryItem.propTypes = {
    value: PropTypes.number,
    type: PropTypes.string.isRequired,
    getMessage: PropTypes.func.isRequired,
    className: PropTypes.string,
    children: PropTypes.node
  } : void 0;
  TableSummaryItem.defaultProps = {
    value: null,
    className: undefined,
    children: undefined
  };

  var TableSummaryRow = dxReactCore.withComponents({
    TotalRow: TableRow,
    GroupRow: TableRow,
    TreeRow: TableRow,
    TotalCell: TableCell,
    GroupCell: TableCell,
    TreeCell: TableCell,
    TableTreeCell: TableTreeCell,
    TableTreeContent: TableTreeContent,
    TableTreeIndent: TableTreeIndent,
    Item: TableSummaryItem
  })(dxReactGrid.TableSummaryRow);
  TableSummaryRow.TREE_ROW_TYPE = dxReactGrid.TableSummaryRow.TREE_ROW_TYPE;
  TableSummaryRow.GROUP_ROW_TYPE = dxReactGrid.TableSummaryRow.GROUP_ROW_TYPE;
  TableSummaryRow.TOTAL_ROW_TYPE = dxReactGrid.TableSummaryRow.TOTAL_ROW_TYPE;

  exports.Grid = Grid;
  exports.ColumnChooser = ColumnChooser;
  exports.DragDropProvider = DragDropProvider;
  exports.PagingPanel = PagingPanel;
  exports.GroupingPanel = GroupingPanel;
  exports.TableRowDetail = TableRowDetail;
  exports.TableGroupRow = TableGroupRow;
  exports.TableSelection = TableSelection;
  exports.Table = Table$1;
  exports.VirtualTable = VirtualTable;
  exports.TableFilterRow = TableFilterRow;
  exports.TableHeaderRow = TableHeaderRow;
  exports.TableBandHeader = TableBandHeader;
  exports.TableEditRow = TableEditRow;
  exports.TableEditColumn = TableEditColumn;
  exports.TableColumnVisibility = TableColumnVisibility;
  exports.TableColumnReordering = TableColumnReordering;
  exports.TableColumnResizing = TableColumnResizing;
  exports.Toolbar = Toolbar$1;
  exports.TableTreeColumn = TableTreeColumn;
  exports.SearchPanel = SearchPanel;
  exports.TableFixedColumns = TableFixedColumns;
  exports.TableSummaryRow = TableSummaryRow;

  Object.defineProperty(exports, '__esModule', { value: true });

}));
//# sourceMappingURL=dx-react-grid-bootstrap4.umd.js.map
