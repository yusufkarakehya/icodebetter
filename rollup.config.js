import babel from "rollup-plugin-babel";
import external from "rollup-plugin-peer-deps-external";
import resolve from "rollup-plugin-node-resolve";
import commonjs from "rollup-plugin-commonjs";
import { uglify } from "rollup-plugin-uglify";
import replace from "rollup-plugin-replace";
// PostCSS plugins
import postcss from "rollup-plugin-postcss";
import simplevars from "postcss-simple-vars";
import nested from "postcss-nested";
import cssnext from "postcss-cssnext";
import cssnano from "cssnano";
import cpy from "rollup-plugin-cpy";
import autoprefixer from 'autoprefixer';

process.env.BABEL_ENV = 'production';
process.env.NODE_ENV = 'production';
// import svg from 'rollup-plugin-svg';
var copyFonts = (params)=>{
  return {
    name:'rollup-plugin-copy-fonts',
    generateBundle:cpy(params).onwrite
  }
}
export default {
  input: ["src/react/lib/index.js"],
  output: {
    format: "umd", //'amd', 'cjs', 'system', 'esm', 'iife' or 'umd'
    name: "window",
    file: "src/main/resources/public/roll-react/dist/bundle.js",
    extend: true
  },
  watch: {
    chokidar: {
      paths: "src/react/**"
    }
  },
  plugins: [
    replace({
      "process.env.NODE_ENV": JSON.stringify("production"),
      "require('react-onclickoutside').default":
        "require('react-onclickoutside')",
      webfonts: "fonts"
    }),
    external(),
    copyFonts({
      // copy over all fonts files
      files: [
        "node_modules/**/*.ttf",
        "node_modules/**/*.woff",
        "node_modules/**/*.woff2"
      ],
      dest: "src/main/resources/public/roll-react/fonts",
      options: {
        parents: false
      }
    }),
    postcss({
      plugins: [
        autoprefixer(),
        simplevars(),
        nested(),
        cssnext({
          warnForDuplicates: false
        }),
        cssnano()
      ],
      extensions: [".css"],
      extract: true
    }),
    uglify(),
    babel({
      exclude: "node_modules/**",
      presets: [
        [
          "react-app",
          {
            absoluteRuntime: false
          }
        ]
      ],
      runtimeHelpers: true
    }),
    resolve(),
    commonjs({
      namedExports: {
        jquery: ["$", "jquery", "jQuery"],
        react: [
          "Children",
          "Component",
          "Fragment",
          "PureComponent",
          "StrictMode",
          "Suspense",
          "cloneElement",
          "createContext",
          "createElement",
          "createFactory",
          "createRef",
          "forwardRef",
          "isValidElement",
          "lazy",
          "memo",
          "unstable_ConcurrentMode",
          "unstable_Profiler",
          "useCallback",
          "useContext",
          "useDebugValue",
          "useEffect",
          "useImperativeHandle",
          "useLayoutEffect",
          "useMemo",
          "useReducer",
          "useRef",
          "useState",
          "version",
          "__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED"
        ],
        "react-dom": [
          "createPortal",
          "findDOMNode",
          "flushSync",
          "hydrate",
          "render",
          "unmountComponentAtNode",
          "unstable_batchedUpdates",
          "unstable_createPortal",
          "unstable_createRoot",
          "unstable_flushControlled",
          "unstable_interactiveUpdates",
          "unstable_renderSubtreeIntoContainer",
          "__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED"
        ],
        "prop-types": [
          "any",
          "array",
          "arrayOf",
          "bool",
          "checkPropTypes",
          "element",
          "exact",
          "func",
          "instanceOf",
          "node",
          "number",
          "object",
          "objectOf",
          "oneOf",
          "oneOfType",
          "shape",
          "string",
          "symbol"
        ],
        "react-draft-wysiwyg": ["Editor"],
        "draftjs-utils": [
          "extractInlineStyle",
          "changeDepth",
          "getSelectedBlocksType",
          "getCustomStyleMap",
          "handleNewLine",
          "blockRenderMap",
          "getSelectedBlock",
          "getSelectionInlineStyle",
          "toggleCustomInlineStyle",
          "getSelectionCustomInlineStyle",
          "setBlockData",
          "getBlockBeforeSelectedBlock",
          "isListBlock",
          "getSelectedBlocksMetadata",
          "getEntityRange",
          "getSelectionText",
          "getSelectionEntity"
        ],
        "react-is": ["isValidElementType", "isContextConsumer"],
        "redux-logger": ["createLogger"],
        "draft-js": [
          "convertToRaw",
          "convertFromRaw",
          "CompositeDecorator",
          "EditorState",
          "RichUtils",
          "Editor",
          "Modifier",
          "AtomicBlockUtils",
          "ContentState"
        ]
      }
    })
  ]
};
