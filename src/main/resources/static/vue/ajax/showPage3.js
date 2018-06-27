mf = h('div', {
	class: 'row'
}, [h('div', {
	class: 'col-12 col-xl-2 col-lg-3 col-md-4 col-sm-6'
}, [false, _ad && h('div', _ad.hidden ? {
	style: {
		display: 'none'
	}
} : {
	class: 'form-group' + (errors.ad ? ' validation-error' : '')
}, [h('label', {
	htmlFor: "ad"
}, _ad.label), viewMode ? iwb.getFieldRawValue(_ad, this.options.ad) : h(_ad.$ || 'el-input', _ad), errors.ad && h('small', null, errors.ad)]), _soyad && h('div', _soyad.hidden ? {
	style: {
		display: 'none'
	}
} : {
	class: 'form-group' + (errors.soyad ? ' validation-error' : '')
}, [h('label', {
	htmlFor: "soyad"
}, _soyad.label), viewMode ? iwb.getFieldRawValue(_soyad, this.options.soyad) : h(_soyad.$ || 'el-input', _soyad), errors.soyad && h('small', null, errors.soyad)]), _il && h('div', _il.hidden ? {
	style: {
		display: 'none'
	}
} : {
	class: 'form-group' + (errors.il ? ' validation-error' : '')
}, [h('label', {
	htmlFor: "il"
}, _il.label), viewMode ? iwb.getFieldRawValue(_il, this.options.il) : h(_il.$ || 'el-input', _il), errors.il && h('small', null, errors.il)]), _cinsiyet && h('div', _cinsiyet.hidden ? {
	style: {
		display: 'none'
	}
} : {
	class: 'form-group' + (errors.cinsiyet ? ' validation-error' : '')
}, [h('label', {
	htmlFor: "cinsiyet"
}, _cinsiyet.label), viewMode ? iwb.getFieldRawValue(_cinsiyet, this.options.cinsiyet) : h(_cinsiyet.$ || 'el-input', _cinsiyet), errors.cinsiyet && h('small', null, errors.cinsiyet)])])

, h('div', {
	class: 'col-12 col-xl-4 col-lg-4 col-md-6 col-sm-12'
}, _aklama && h('div', _aklama.hidden ? {
	style: {
		display: 'none'
	}
} : {
	class: 'form-group' + (errors.aklama ? ' validation-error' : '')
}, [h('label', {
	htmlFor: "aklama"
}, _aklama.label), viewMode ? iwb.getFieldRawValue(_aklama, this.options.aklama) : h(_aklama.$ || 'el-input', _aklama), errors.aklama && h('small', null, errors.aklama)])

, _tutar && h('div', _tutar.hidden ? {
	style: {
		display: 'none'
	}
} : {
	class: 'form-group' + (errors.tutar ? ' validation-error' : '')
}, [h('label', {
	htmlFor: "tutar"
}, _tutar.label), viewMode ? iwb.getFieldRawValue(_tutar, this.options.tutar) : h(_tutar.$ || 'el-input', _tutar), errors.tutar && h('small', null, errors.tutar)
		])

)]

);