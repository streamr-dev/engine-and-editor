package com.unifina.domain

import com.unifina.service.NotFoundException
import com.unifina.service.NotPermittedException
import com.unifina.service.PermissionService
import com.unifina.service.StreamService
import grails.compiler.GrailsCompileStatic
import grails.util.Holders
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler

@ToString
@GrailsCompileStatic
@EqualsAndHashCode
class Resource {
	Class<?> clazz
	Object id

	Resource(Class<?> clazz, Object id) {
		if (!clazz) {
			throw new IllegalArgumentException("Missing resource class")
		}
		if (!DomainClassArtefactHandler.isDomainClass(clazz)) {
			throw new IllegalArgumentException("Not a valid Grails domain class: " + clazz.simpleName)
		}
		this.clazz = clazz
		if (!id) {
			throw new IllegalArgumentException("Missing resource id")
		}
		this.id = id
	}

	String type() {
		if (Product.isAssignableFrom(clazz)) {
			return "Product"
		} else if (Stream.isAssignableFrom(clazz)) {
			return "Stream"
		}
		return "Unknown"
	}

	Object load(User apiUser, boolean requireShareResourcePermission) {
		Object resource
		if (Product.isAssignableFrom(clazz)) {
			resource = Product.get(idToString())
		} else if (Stream.isAssignableFrom(clazz)) {
			StreamService streamService = Holders.getApplicationContext().getBean(StreamService)
			resource = streamService.getStream(idToString())
		} else {
			throw new IllegalArgumentException("Unexpected resource class: " + clazz)
		}
		if (resource == null) {
			throw new NotFoundException(clazz.simpleName, idToString())
		}
		Permission.Operation shareOp = Permission.Operation.shareOperation(resource)
		PermissionService permissionService = Holders.getApplicationContext().getBean(PermissionService)
		if (requireShareResourcePermission && !permissionService.check(apiUser, resource, shareOp)) {
			throw new NotPermittedException(apiUser?.username, clazz.simpleName, idToString(), shareOp.id)
		}
		return resource
	}

	String idToString() {
		return id?.toString()
	}
}
