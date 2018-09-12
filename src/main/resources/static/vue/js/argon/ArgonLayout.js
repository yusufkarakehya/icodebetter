function hasElement(className) {
  return document.getElementsByClassName(className).length > 0;
}

function initScrollbar(className) {
  if (hasElement(className)) {
    new PerfectScrollbar(`.${className}`);
  } else {
    // try to init it later in case this component is loaded async
    setTimeout(() => {
      initScrollbar(className);
    }, 100);
  }
}

var ArgonLayout = {
  template: `<div>
	<top-navbar></top-navbar>
	<notifications></notifications>

	<main style="padding-top: 35px;">
	<fade-transition origin="center" mode="out-in" :duration="250">
		<router-view :key="$route.fullPath"></router-view>
	</fade-transition>
	<modal></modal>
	</main>
</div>`,
  methods: {},
  mounted() {
    iwb.$notify = this.$notify;
  }
};
