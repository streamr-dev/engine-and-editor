package com.unifina.domain.marketplace

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class TermsOfUse {
	Boolean redistribution = true
	Boolean commercialUse = true
	Boolean reselling = true
	Boolean storage = true
	String termsUrl
	String termsName

	public TermsOfUse() {}
	public TermsOfUse(Product product) {}

	static constraints = {
		redistribution(nullable: false)
		commercialUse(nullable: false)
		reselling(nullable: false)
		storage(nullable: false)
		termsUrl(nullable: true, url: true, maxSize: 2048)
		termsName(nullable: true, maxSize: 100)
	}
	Map toMap() {
		return [
			redistribution: redistribution,
			commercialUse: commercialUse,
			reselling: reselling,
			storage: storage,
			termsUrl: termsUrl,
			termsName: termsName,
		]
	}
}
