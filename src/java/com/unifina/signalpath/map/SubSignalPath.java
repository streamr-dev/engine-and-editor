package com.unifina.signalpath.map;

import com.unifina.signalpath.Input;
import com.unifina.signalpath.Output;
import com.unifina.signalpath.Propagator;
import com.unifina.signalpath.SignalPath;

import java.io.Serializable;
import java.util.List;

public class SubSignalPath implements Serializable {
	private final SignalPath signalPath;
	private final Propagator propagator;

	public SubSignalPath(SignalPath signalPath, Propagator propagator) {
		this.signalPath = signalPath;
		this.propagator = propagator;
	}

	public void feedInput(String inputName, Object value) {
		signalPath.getInput(inputName).receive(value);
	}

	public void propagate() {
		propagator.propagate();
	}

	void connectTo(String outputName, Input input) {
		signalPath.getOutput(outputName).connect(input);
	}

	public void proxyToOutputs(List<Output> proxiedOutputs) {
		for (Output proxyOutput : proxiedOutputs) {
			Output output = signalPath.getOutput(proxyOutput.getName());
			output.addProxiedOutput(proxyOutput);
		}
	}
}
