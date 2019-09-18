import "./style.css";
import React, { Component } from "react";
import DropdownTreeSelect from "react-dropdown-tree-select";
import 'react-dropdown-tree-select/dist/styles.css'
import classNames from 'classnames';
import PropTypes from 'prop-types';
const { iwb } = window;

class TreeSelectBase extends Component {
  static defaultProps = {
    onFocus: () => { },
    onBlur: () => { },
    onChange: () => { },
    texts: {},
    showDropdown: 'default'
  }
  /**
   * to get initial values only
   * @param {Object} nextProps 
   */
  shouldComponentUpdate(nextProps) {
    if (nextProps.value !== this.props.value) {
      return false;
    }
    return true;
  }
  /**
   * make cheked property to tru depending on selected values of tree object
   * @param {Object[]} allItems 
   * @param {Object[]} selectedItems 
   * @private
   */
  _markChekedItems(allItems = [], selectedItems = []) {
    return allItems.map(item => {
      if (selectedItems.findIndex(i => i && i.label !== undefined && i.label === item.label) > -1) {
        item.checked = true;
      }
      if (item.children && item.children.length) {
        item.children = this._markChekedItems(item.children, selectedItems)
      }
      return item;
    })
  }
  /**
   * cenverts to array
   * @param {(string\|object)} value 
   * @return {Object}
   */
  _convertToArray(value) {
    value = (typeof value == 'string' && value !== '') ? JSON.parse(value) : value;
    value = (Array.isArray(value)) ? value : [value];
    return value
  }
  /**
   * Prepare value for component
   * @param {Object[]} allItems 
   * @param {Object[]} selectedItems 
   */
  _prepareData(allItems, selectedItems) {
    allItems = this._convertToArray(allItems)
    selectedItems = this._convertToArray(selectedItems)
    return this._markChekedItems(allItems, selectedItems)
  }
  /**
   * tree flatten
   * @param {Object[]} arrayList 
   * @param {Array} colection 
   */
  _treeFlattener(arrayList, colection = []) {
    arrayList.forEach(item => {
      if (item.children && item.children.length) {
        this._treeFlattener(item.children, colection);
      }
      let { children, ...rest } = item
      colection.push(rest)
    })
    return colection
  }
  /**
   * function emits selected data to parent form
   * @param {Object} currentNode 
   * @param {Object[]} selectedNodes 
   */
  _onChange(currentNode, selectedNodes) {
    selectedNodes = this._withChildren(this._convertToArray(this.props.defaultList), selectedNodes)
    selectedNodes = this._treeFlattener(selectedNodes)
    this.props.onChange && this.props.onChange(selectedNodes);
  }
  /**
   * child element are added to selected items
   * @param {object[]} defaultList 
   * @param {Array} selectedNodes 
   */
  _withChildren(defaultList = [], selectedNodes = []) {
    defaultList.forEach(item => {
      let selectedIndex = selectedNodes.findIndex(i => i && i.label !== undefined && i.label === item.label);
      if (selectedIndex > -1) {
        selectedNodes[selectedIndex].children = (item && item.children !== undefined && item.children.length) ? item.children : [];
      } else {
        this._withChildren(item.children, selectedNodes)
      }
    })
    return selectedNodes;
  }
}
TreeSelectBase.propTypes = {
  value: PropTypes.oneOfType([PropTypes.array, PropTypes.object]),
  clearSearchOnChange: PropTypes.bool,
  keepTreeOnSearch: PropTypes.bool,
  keepChildrenOnSearch: PropTypes.bool,
  keepOpenOnSelect: PropTypes.bool,
  showDropdown: PropTypes.oneOf(['default', 'initial', 'always']),
  className: PropTypes.string,
  onChange: PropTypes.func,
  onAction: PropTypes.func,
  onNodeToggle: PropTypes.func,
  onFocus: PropTypes.func,
  onBlur: PropTypes.func,
  mode: PropTypes.oneOf(['multiSelect', 'simpleSelect', 'radioSelect', 'hierarchical']),
  showPartiallySelected: PropTypes.bool,
  disabled: PropTypes.bool,
  readOnly: PropTypes.bool,
  id: PropTypes.string,
  searchPredicate: PropTypes.func,
  url: PropTypes.string,
};

export class XDropdownMultiTreeSelect extends TreeSelectBase {
  render() {
    let { className, onChange, value, defaultList, ...props } = this.props;
    let checkedData = this._prepareData(defaultList, value);
    return (
      <DropdownTreeSelect
        data={checkedData}
        className={classNames('bootstrap-enbaled', 'multi-tree-select', className)}
        onChange={this._onChange.bind(this)}
        {...props}
      />
    );
  }
}
/**
 * remote select tree
 */
export class XDropdownMultiRemoteTreeSelect extends TreeSelectBase {
  constructor(props) {
    super(props);
    this.state = { data: [] };
  }
  /**
   * will request the data from backend and set the data to state
   * @param {Number} queryId 
   * @param {Object} params 
   */
  _requestData(queryId, params) {
    queryId && iwb && iwb.request({
      url: "ajaxQueryData?_qid=" + queryId,
      params: params || {},
      successCallback: ({ data }) => {
        this.setState({ data: this._prepareData(data, this.props.value) })
      }
    });
  }
  componentDidMount() {
    let { params, queryId } = this.props;
    this._requestData(params, queryId);
  }
  render() {
    let { className, onChange, value, defaultList, ...props } = this.props;
    return (
      <DropdownTreeSelect
        data={this.state.data}
        className={classNames('bootstrap-enbaled', 'multi-remote-tree-select', className)}
        onChange={this._onChange.bind(this)}
        {...props}
      />
    );
  }
}
/**
 * single select tree
 */
export class XDropdownTreeSelect extends TreeSelectBase {
  _onChange(currentNode, selectedNodes) {
    // selectedNodes = this._withChildren(this._convertToArray(this.props.defaultList), selectedNodes)
    selectedNodes = this._treeFlattener(selectedNodes)
    this.props.onChange && this.props.onChange(selectedNodes[0]);
  }
  render() {
    let { className, onChange, value, defaultList, ...props } = this.props;
    let checkedData = this._prepareData(defaultList, value);
    return (
      <DropdownTreeSelect
        data={checkedData}
        mode={'radioSelect'}
        className={classNames('bootstrap-enbaled', 'tree-select', className)}
        onChange={this._onChange.bind(this)}
        {...props}
      />
    )
  }
}