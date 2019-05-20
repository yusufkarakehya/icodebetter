import "react-draft-wysiwyg/dist/react-draft-wysiwyg.css";
import "./style.css";
import React, { Component } from "react";
import { EditorState, convertToRaw, ContentState } from "draft-js";
import { Editor } from "react-draft-wysiwyg";
import draftToHtml from "draftjs-to-html";
import htmlToDraft from "html-to-draftjs";
export default class XHTMLEditor extends Component {
  constructor(props) {
    super(props);
    const html = this.props.value || "";
    const contentBlock = htmlToDraft(html);
    if (contentBlock) {
      const contentState = ContentState.createFromBlockArray(
        contentBlock.contentBlocks
      );
      const editorState = EditorState.createWithContent(contentState);
      this.state = {
        editorState
      };
    }
  }
  onEditorStateChange = editorState => {
    this.setState(
      {
        editorState
      },
      () => {
        let val = draftToHtml(convertToRaw(editorState.getCurrentContent()));
        if (typeof val === "string") {
          this.props.onHtmlChange && this.props.onHtmlChange(val);
        }
      }
    );
  };
  render() {
    const { editorState } = this.state;
    return (
      <Editor
        editorState={editorState}
        wrapperClassName="demo-wrapper"
        editorClassName="demo-editor"
        onEditorStateChange={this.onEditorStateChange}
        {...this.props}
      />
    );
  }
}
