
// Page Headers
var DefaultHeader = Vue.component('default-header',{
	template:'<div class="panel-header panel-header-sm"></div>'
});


var AuthLayout = Vue.component('auth-layout',{
	template:'<div></div>'
});

var SimplePage = Vue.component('simple-page',{
	template:'<div>Ali Baba</div>'
});

// Pages
const User = ()=>  SimplePage;
const Pricing = ()=>  SimplePage;
const TimeLine = ()=>  SimplePage;
const Login = ()=>  SimplePage;
const Register = ()=>  SimplePage;
const Lock = ()=>  SimplePage;

// Components pages
const Buttons = ()=>  SimplePage;
const GridSystem = ()=>  SimplePage;
const Panels = ()=>  SimplePage;
const SweetAlert = () => SimplePage;
const Icons = ()=> SimplePage;
const Typography = ()=> SimplePage;

// Forms pages
const RegularForms = () => SimplePage;
const ExtendedForms = () => SimplePage;
const ValidationForms = () => SimplePage;
const Wizard = () => SimplePage;

// TableList pages
const RegularTables = () => SimplePage;
const ExtendedTables = () => SimplePage;
const PaginatedTables = () => SimplePage;
// Maps pages
const GoogleMaps = () => SimplePage;
const FullScreenMap = () => SimplePage;
const VectorMaps = () => SimplePage;

// Calendar
const Calendar = () => SimplePage;
// Charts
const Charts = () => SimplePage;

let componentsMenu = {
  path: '/components',
  component: DashboardLayout,
  redirect: '/components/buttons',
  name: 'Components',
  children: [
    {
      path: 'buttons',
      name: 'Buttons22',
      components: {default: Buttons, header: DefaultHeader}
    },
    {
      path: 'grid-system',
      name: 'Grid System',
      components: {default: GridSystem, header: DefaultHeader}
    },
    {
      path: 'panels',
      name: 'Panels',
      components: {default: Panels, header: DefaultHeader}
    },
    {
      path: 'sweet-alert',
      name: 'Sweet Alert',
      components: {default: SweetAlert, header: DefaultHeader}
    },
    {
      path: 'icons',
      name: 'Icons',
      components: {default: Icons, header: DefaultHeader}
    },
    {
      path: 'typography',
      name: 'Typography',
      components: {default: Typography, header: DefaultHeader}
    }

  ]
}
let formsMenu = {
  path: '/forms',
  component: DashboardLayout,
  redirect: '/forms/regular',
  name: 'Forms',
  children: [
    {
      path: 'regular',
      name: 'Regular Forms',
      components: {default: RegularForms, header: DefaultHeader}
    },
    {
      path: 'extended',
      name: 'Extended Forms',
      components: {default: ExtendedForms, header: DefaultHeader}
    },
    {
      path: 'validation',
      name: 'Validation Forms',
      components: {default: ValidationForms, header: DefaultHeader}
    },
    {
      path: 'wizard',
      name: 'Wizard',
      components: {default: Wizard, header: DefaultHeader}
    }
  ]
}

let tablesMenu = {
  path: '/table-list',
  component: DashboardLayout,
  redirect: '/table-list/regular',
  name: 'Tables',
  children: [
    {
      path: 'regular',
      name: 'Regular Tables',
      components: {default: RegularTables, header: DefaultHeader}
    },
    {
      path: 'extended',
      name: 'Extended Tables',
      components: {default: ExtendedTables, header: DefaultHeader}
    },
    {
      path: 'paginated',
      name: 'Paginated Tables',
      components: {default: PaginatedTables, header: DefaultHeader}
    }]
}

let mapsMenu = {
  path: '/maps',
  component: DashboardLayout,
  name: 'Maps',
  redirect: '/maps/google',
  children: [
    {
      path: 'google',
      name: 'Google Maps',
      components: {default: GoogleMaps, header: DefaultHeader}
    },
    {
      path: 'full-screen',
      name: 'Full Screen Map',
      meta: {
        hideContent: true,
        hideFooter: true
      },
      components: {default: FullScreenMap}
    },
    {
      path: 'vector-map',
      name: 'Vector Map',
      components: {default: VectorMaps, header: DefaultHeader}
    }
  ]
}

let pagesMenu = {
  path: '/pages',
  component: DashboardLayout,
  name: 'Pages',
  redirect: '/pages/user',
  children: [
    {
      path: 'user',
      name: 'User Page',
      components: {default: User, header: DefaultHeader}
    },
    {
      path: 'timeline',
      name: 'Timeline Page',
      components: {default: TimeLine, header: DefaultHeader}
    }
  ]
}

let authPages = {
  path: '/',
  component: AuthLayout,
  name: 'Authentication',
  children: [
    {
      path: '/login',
      name: 'Login',
      component: Login
    },
    {
      path: '/register',
      name: 'Register',
      component: Register
    },
    {
      path: '/pricing',
      name: 'Pricing',
      component: Pricing
    },
    {
      path: '/lock',
      name: 'Lock',
      component: Lock
    }
  ]
}

const xroutes = [
  {
    path: '/',
    redirect: '/dashboard',
    name: 'Home'
  },
  componentsMenu,
  formsMenu,
  tablesMenu,
  mapsMenu,
  pagesMenu,
  {
    path: '/',
    component: DashboardLayout,
    redirect: '/dashboard',
    name: 'Dashboard',
    children: [
      {
        path: 'calendar',
        name: 'Calendar',
        components: {default: Calendar, header: DefaultHeader}
      },
      {
        path: 'charts',
        name: 'Charts',
        components: {default: Charts, header: DefaultHeader}
      }
    ]
  },
  {path: '*', component: NotFoundPage, header: DefaultHeader}
];

