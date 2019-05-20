import roll from "./rollup.config";
roll.input = "src/react/lib/core.js";
roll.output = {
  format: "umd", //'amd', 'cjs', 'system', 'esm', 'iife' or 'umd'
  name: "window",
  file: "src/main/resources/public/roll-react/dist/core-bundle.js",
  extend: true
};
export default roll;

// import css from 'rollup-plugin-css-only'

// export default {
//   entry: 'entry.js',
//   dest: 'bundle.js',
//   plugins: [
//     css({ output: 'bundle.css' })
//   ]
// }
