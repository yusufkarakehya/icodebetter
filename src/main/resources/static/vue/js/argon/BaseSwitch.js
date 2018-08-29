var BaseSwitch = Vue.component("base-switch", {
  template: `<label class="custom-toggle">
        <input type="checkbox"
               v-model="model"
               v-bind="$attrs"
               v-on="$listeners">
        <span class="custom-toggle-slider rounded-circle"></span>
    </label>`,
  inheritAttrs: false,
  props: {
    value: {
      type: Boolean,
      default: false,
      description: "Switch value"
    }
  },
  computed: {
    model: {
      get() {
        return this.value;
      },
      set(value) {
        this.$emit("input", value);
      }
    }
  }
});
