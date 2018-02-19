package com.unifina.service

import com.unifina.api.*
import com.unifina.domain.data.Stream
import com.unifina.domain.marketplace.Product
import com.unifina.domain.security.Permission
import com.unifina.domain.security.SecUser
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class ProductService {
	ApiService apiService
	PermissionService permissionService

	List<Product> list(ProductListParams listParams, SecUser currentUser) {
		apiService.list(Product, listParams, currentUser)
	}

	Product findById(String id, SecUser currentUser, Permission.Operation op)
			throws NotFoundException, NotPermittedException {
		apiService.authorizedGetById(Product, id, currentUser, op)
	}

	Product create(CreateProductCommand command, SecUser currentUser)
			throws ValidationException, NotPermittedException {
		if (!command.validate()) {
			throw new ValidationException(command.errors)
		}

		command.streams.each {
			permissionService.verifyShare(currentUser, it)
		}

		Product product = new Product(command.properties)
		product.save(failOnError: true)
		permissionService.systemGrantAll(currentUser, product)
		return product
	}

	Product update(String id, UpdateProductCommand command, SecUser currentUser) {
		if (!command.validate()) {
			throw new ValidationException(command.errors)
		}

		command.streams.each {
			permissionService.verifyShare(currentUser, it)
		}

		Product product = findById(id, currentUser, Permission.Operation.WRITE)
		product.setProperties(command.properties)
		return product.save(failOnError: true)
	}

	void transitionToDeploying(Product product, String tx) {
		if (product.state == Product.State.NOT_DEPLOYED) {
			product.tx = tx
			product.state = Product.State.DEPLOYING
			product.save(failOnError: true)
		} else {
			throw new InvalidStateTransitionException(product.state, Product.State.DEPLOYING)
		}
	}

	Product deployed(Product product, ProductDeployedCommand command, SecUser currentUser) {
		if (!command.validate()) {
			throw new ValidationException(command.errors)
		}
		if (product.state == Product.State.UNDEPLOYING) {
			throw new InvalidStateTransitionException(product.state, Product.State.DEPLOYED)
		}
		verifyDevops(currentUser)

		product.setProperties(command.properties)
		product.state = Product.State.DEPLOYED
		product.save(failOnError: true)
	}

	void transitionToUndeploying(Product product) {
		if (product.state == Product.State.DEPLOYED) {
			product.state = Product.State.UNDEPLOYING
			product.save(failOnError: true)
		} else {
			throw new InvalidStateTransitionException(product.state, Product.State.UNDEPLOYING)
		}
	}

	void undeployed(Product product, SecUser currentUser) throws NotPermittedException {
		if (!(product.state in [Product.State.DEPLOYED, Product.State.UNDEPLOYING])) {
			throw new InvalidStateTransitionException(product.state, Product.State.NOT_DEPLOYED)
		}
		verifyDevops(currentUser)

		permissionService.systemRevokeAnonymousAccess(product)
		product.state = Product.State.NOT_DEPLOYED
		product.save(failOnError: true)
	}

	private static void verifyDevops(SecUser currentUser) {
		if (!currentUser.isDevOps()) {
			throw new NotPermittedException("DevOps role required")
		}
	}
}
