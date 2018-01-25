package com.unifina.signalpath.blockchain;

import com.unifina.api.NotPermittedException;
import com.unifina.domain.security.IntegrationKey;
import com.unifina.domain.security.SecUser;
import com.unifina.service.EthereumIntegrationKeyService;
import com.unifina.signalpath.AbstractSignalPathModule;
import com.unifina.signalpath.Parameter;
import com.unifina.signalpath.PossibleValue;
import grails.util.Holders;
import org.codehaus.groovy.runtime.InvokerHelper;

import java.util.*;

class EthereumAccountParameter extends Parameter<IntegrationKey> {
	EthereumAccountParameter(AbstractSignalPathModule owner, String name) {
		super(owner, name, null, "String");
		setCanConnect(false);
	}

	@Override
	public IntegrationKey parseValue(String id) {
		return (IntegrationKey) InvokerHelper.invokeStaticMethod(IntegrationKey.class, "findByIdAndService",
			new Object[]{id, "ETHEREUM"});
	}

	String getAddress() {
		if (hasValue()) {
			if (getOwner().getGlobals().isRunContext()) {
				checkPermission();
			}
			return (String) ((Map) getValue().toMap().get("json")).get("address");
		}
		return null;
	}

	String getPrivateKey() {
		EthereumIntegrationKeyService keyService = Holders.getApplicationContext().getBean(EthereumIntegrationKeyService.class);
		if (hasValue()) {
			checkPermission();
			return keyService.decryptPrivateKey(getValue());
		}
		return null;
	}

	private void checkPermission() {
		SecUser loggedInUser = SecUser.loadViaJava(getOwner().getGlobals().getUserId());
		SecUser integrationKeyUser = getValue().getUser();
		if (!integrationKeyUser.equals(loggedInUser)) {
			throw new NotPermittedException("Access denied to Ethereum private key (id " + getValue().getId()+"). Only the owner can use it.");
		}
	}

	@Override
	public String formatValue(IntegrationKey value) {
		return value == null ? null : value.getId();
	}

	@Override
	protected List<PossibleValue> getPossibleValues() {
		EthereumIntegrationKeyService service = Holders.getApplicationContext().getBean(EthereumIntegrationKeyService.class);
		SecUser user = SecUser.loadViaJava(getOwner().getGlobals().getUserId());
		Set<IntegrationKey> integrationKeys = new LinkedHashSet<>(service.getAllKeysForUser(user));
		if (hasValue()) {
			integrationKeys.add(getValue());
		}


		List<PossibleValue> possibleValues = new ArrayList<>();
		possibleValues.add(new PossibleValue("(none)", null));
		for (IntegrationKey key : integrationKeys) {
			possibleValues.add(new PossibleValue(key.getName(), key.getId()));
		}
		return possibleValues;
	}
}
