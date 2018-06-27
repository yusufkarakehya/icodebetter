var Navbar =  Vue.component('navbar', {
	template:`<nav :class="classes" class="navbar navbar-expand-lg">
    <div class="container-fluid">
      <slot></slot>

      <collapse-transition>
        <div class="collapse navbar-collapse justify-content-end show"
             v-show="showNavbar"
             id="navigation">
          <ul class="navbar-nav">
            <slot name="navbar-menu"></slot>
          </ul>
        </div>
      </collapse-transition>
    </div>
  </nav>`
,props: {
      showNavbar: Boolean,
      transparent: {
        type: Boolean,
        default: true
      },
      position: {
        type: String,
        default: 'absolute'
      },
      type: {
        type: String,
        default: 'white',
        validator(value) {
          return ['white', 'default', 'primary', 'danger', 'success', 'warning', 'info'].includes(value);
        }
      }
    },

    computed: {
      classes() {
        let color = `bg-${this.type}`;
        let navPosition = `navbar-${this.position}`;
        return [
          {'navbar-transparent': !this.showNavbar && this.transparent},
          {[color]: this.showNavbar || !this.transparent},
          navPosition]
      }
    }
  });


var NavbarToggleButton =  Vue.component('navbar-toggle-button', {
	template:`<button type="button"
        class="navbar-toggler collapsed"
        data-toggle="collapse"
        data-target="#navbar"
        aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
  <span class="navbar-toggler-bar bar1"></span>
  <span class="navbar-toggler-bar bar2"></span>
  <span class="navbar-toggler-bar bar3"></span>
</button>`
});