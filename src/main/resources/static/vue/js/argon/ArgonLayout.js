function hasElement (className) {
    return document.getElementsByClassName(className).length > 0;
}

function initScrollbar (className) {
    if (hasElement(className)) {
      new PerfectScrollbar(`.${className}`);
    } else {
      // try to init it later in case this component is loaded async
      setTimeout(() => {
        initScrollbar(className);
      }, 100);
    }
}

var ArgonLayout =  {
	template:`<div id="app">
	<top-navbar></top-navbar>
	<notifications></notifications>
	  <span class="navbar-brand" style="    margin-left: 30px;
    text-transform: none;
    padding: 20px 20px 0px 0px;
    font-size: 2rem;
    font-weight: normal;" href="#">
	    {{$route.name}}
	  </span>
	<main style="padding-top: 15px;">
	<fade-transition origin="center" mode="out-in" :duration="250">
	    <router-view/>
	</fade-transition>
	<modal></modal>
	</main>
</div>`
	,methods: {

    },
    mounted () {
		iwb.$notify = this.$notify;
    }
};