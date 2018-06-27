mf = h('div', {
			class: 'row'
		}, [h('div', {
			class: 'col-12 col-xl-8 col-lg-8 col-md-8 col-sm-8'
		}, [false, _ara && h('div', _ara.hidden ? {
			style: {
				display: 'none'
			}
		} : {
			class: 'form-group' + (errors.ara ? ' validation-error' : '')
		}, [h('label', {
			htmlFor: "ara"
		}, _ara.label), viewMode ? iwb.getFieldRawValue(_ara, this.options.ara) : h(_ara.$ || 'el-input', {
			class: _ara.class || '',
			props: _ara,
			on: {
				change: function(v) {
					this.values.ara = v;
				}
			}
		}), errors.ara && h('small', errors.ara)]), h('div', {
			style: {
				padding: '0.45rem .85rem'
			},
			class: 'alert alert-danger'
		}, h('i', {
			class: 'icon-info'
		}), ' ', 'Aradıkların Buraya Gelecek'), h('div', {
			class: 'row'
		}, [h('div', {
			class: 'col-12 col-md-4'
		})]
		
		
		), _kanuni_ad && h('div', _kanuni_ad.hidden ? {
			style: {
				display: 'none'
			}
		} : {
			class: 'form-group' + (errors.kanuni_ad ? ' validation-error' : '')
		}, [h('label', {
			htmlFor: "kanuni_ad"
		}, _kanuni_ad.label), viewMode ? iwb.getFieldRawValue(_kanuni_ad, this.options.kanuni_ad) : h(_kanuni_ad.$ || 'el-input', {
			class: _kanuni_ad.class || '',
			props: _kanuni_ad,
			on: {
				change: function(v) {
					this.values.kanuni_ad = v;
				}
			}
		}), errors.kanuni_ad && h('small', errors.kanuni_ad)])]), h('div', {
			class: 'col-12 col-xl-4 col-lg-4 col-md-4 col-sm-4'
		}, [false, _il && h('div', _il.hidden ? {
			style: {
				display: 'none'
			}
		} : {
			class: 'form-group' + (errors.il ? ' validation-error' : '')
		}, [h('label', {
			htmlFor: "il"
		}, _il.label), viewMode ? iwb.getFieldRawValue(_il, this.options.il) : h(_il.$ || 'el-input', {
			class: _il.class || '',
			props: _il,
			on: {
				change: function(v) {
					this.values.il = v;
				}
			}
		}, _il.options && _il.options.map(function(o) {
			return h('el-option', {
				props: {
					key: o.id,
					value: o.id,
					label: o.dsc
				}
			})
		})), errors.il && h('small', errors.il)]), _adres && h('div', _adres.hidden ? {
			style: {
				display: 'none'
			}
		} : {
			class: 'form-group' + (errors.adres ? ' validation-error' : '')
		}, [h('label', {
			htmlFor: "adres"
		}, _adres.label), viewMode ? iwb.getFieldRawValue(_adres, this.options.adres) : h(_adres.$ || 'el-input', {
			class: _adres.class || '',
			props: _adres,
			on: {
				change: function(v) {
					this.values.adres = v;
				}
			}
		}), errors.adres && h('small', errors.adres)])])]);