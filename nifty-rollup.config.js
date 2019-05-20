import roll from "./rollup.config";
roll.input = "src/react/lib/nifty.js";
roll.output = {
  format: "umd", //'amd', 'cjs', 'system', 'esm', 'iife' or 'umd'
  name: "window",
  file: "src/main/resources/public/roll-react/dist/nifty-bundle.js",
  extend: true
};
export default roll;
