// import "core-js/shim";
/** Html Editor */
import XHTMLEditor from "./XHTMLEditor";
import draftToHtml from "draftjs-to-html";
import htmlToDraft from "html-to-draftjs";
import styleInject from "style-inject";
import * as draftJs from "draft-js";
import * as draftJsWzz from "react-draft-wysiwyg";
/** redux state managment */
import * as Redux from "redux";
import * as ReactRedux from "react-redux";
import * as ReduxThunk from "redux-thunk";
import * as reduxLogger from "redux-logger";
/** DevExpress */
import "@devexpress/dx-react-grid-bootstrap4/dist/dx-react-grid-bootstrap4.css";
import "open-iconic/font/css/open-iconic-bootstrap.css";
import "./css/DXReactGridBootstrap4.css";
import * as DXCore from "@devexpress/dx-core";
import * as DXGridCore from "@devexpress/dx-grid-core";
import * as DXReactCore from "@devexpress/dx-react-core";
import * as DXReactGrid from "@devexpress/dx-react-grid";
import * as DXReactGridBootstrap4 from "@devexpress/dx-react-grid-bootstrap4";
import "animate.css/animate.css";
/** React */
import React from "react";
import * as ReactDOM from "react-dom";
/** Reactstrap bootstrap jquery */
import $ from "jquery";
const jQuery = $;
const jquery = $;
import bootstrap from "bootstrap";
import * as Reactstrap from "reactstrap";
import Popper from "popper.js";
import * as ReactPopper from "react-popper";
import * as ReactTransitionGroup from "react-transition-group";
// /** AutosizeInput */
import AutosizeInput from "react-input-autosize";
import classNames from "classnames";
var classnames = classNames;
/** Datetime */
import "react-datetime/css/react-datetime.css";
import Datetime from "react-datetime";
import moment from "moment";
/** NumberFormat */
import NumberFormat from "react-number-format";
// /** ReactRouterDOM */
import * as ReactRouterDOM from "react-router-dom";
import PropTypes from "prop-types";
/** Select */
import "react-select/dist/react-select.css";
import Select from "react-select";
/** toastr */
import "toastr";
import toastr from "toastr";
/** font-awesome */
import "@fortawesome/fontawesome-free/css/all.css";
import "simple-line-icons/css/simple-line-icons.css";
/** Loaders */
import * as loaders from "./Loaders";
import "./css/timeline.css";
import {
  Aside
} from "./components/Aside";
import * as ReactDiffViewer from 'react-diff-viewer'
/** Exports to window object */
export {
  React,
  ReactDOM,
  XHTMLEditor,
  ReactDiffViewer,
  draftJs,
  htmlToDraft,
  draftToHtml,
  draftJsWzz,
  styleInject,
  Redux,
  ReactRedux,
  reduxLogger,
  ReduxThunk,
  DXCore,
  DXGridCore,
  DXReactCore,
  DXReactGrid,
  DXReactGridBootstrap4,
  Reactstrap,
  Popper,
  ReactPopper,
  ReactTransitionGroup,
  ReactRouterDOM,
  AutosizeInput,
  classNames,
  classnames,
  Datetime,
  moment,
  NumberFormat,
  PropTypes,
  Select,
  bootstrap,
  $,
  jQuery,
  jquery,
  toastr,
  loaders,
  Aside
};
