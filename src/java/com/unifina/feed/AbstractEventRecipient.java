package com.unifina.feed;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import com.unifina.datasource.IStartListener;
import com.unifina.signalpath.AbstractSignalPathModule;
import com.unifina.signalpath.Propagator;
import com.unifina.utils.Globals;
import com.streamr.client.protocol.message_layer.ITimestamped;

/**
 * An AbstractEventRecipient receives FeedEvents and updates the outputs
 * of registered AbstractSignalPathModules accordingly. It then handles the
 * propagation of events from those outputs through the SignalPaths in which
 * those modules reside.
 * @author Henri
 *
 * @param <ModuleClass> The type of objects than can be registered
 */
public abstract class AbstractEventRecipient<ModuleClass, MessageClass extends ITimestamped> implements Consumer<MessageClass>, IStartListener {

	private final List<ModuleClass> modules = new ArrayList<>();
	private final Propagator propagator = new Propagator();
	private final Class<?> moduleClass;

	public AbstractEventRecipient(Globals globals) {
		if (globals != null && globals.getDataSource() != null) {
			globals.getDataSource().addStartListener(this);
		}

		// Traverse class hierarchy up until we find a class that reveals the ModuleClass generic
		Class clazz = this.getClass();
		while (!(clazz.getGenericSuperclass() instanceof ParameterizedType)) {
			clazz = clazz.getSuperclass();
		}
		ParameterizedType pt = (ParameterizedType) clazz.getGenericSuperclass();
		moduleClass = (Class<?>) pt.getActualTypeArguments()[0];
	}

	@Override
	public void onStart() {
		propagator.initialize();
	}

	public void register(ModuleClass module) {
		if (!moduleClass.isAssignableFrom(module.getClass())) {
			throw new IllegalArgumentException("Can not register module of type: " + module.getClass() +
				", required type: " + moduleClass + ". Module: " + module);
		} else if (!modules.contains(module)) {
			modules.add(module);
			if (module instanceof AbstractSignalPathModule) {
				AbstractSignalPathModule m = (AbstractSignalPathModule) module;
				propagator.addModule(m);
			}
		}
	}

	@Override
	public void accept(MessageClass message) {
		sendOutputFromModules(message);
		propagator.propagate();
	}

	protected List<ModuleClass> getModules() {
		return Collections.unmodifiableList(modules);
	}

	/**
	 * This method should process the event, then if necessary, loop through each
	 * registered module and send appropriate values from the outputs of connected
	 * modules. Propagation will take place immediately after calling this method.
	 * @param event
	 */
	protected abstract void sendOutputFromModules(MessageClass event);

}
