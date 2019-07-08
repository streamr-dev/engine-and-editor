package com.unifina.feed.map;

import com.streamr.client.protocol.message_layer.StreamMessage;
import com.streamr.client.utils.StreamPartition;
import com.unifina.domain.data.Stream;
import com.unifina.exceptions.StreamFieldChangedException;
import com.unifina.feed.StreamEventRecipient;
import com.unifina.signalpath.AbstractSignalPathModule;
import com.unifina.signalpath.Output;
import com.unifina.signalpath.TimeSeriesOutput;
import com.unifina.utils.Globals;

import java.io.IOException;
import java.util.*;

/**
 * This class receives FeedEvents with StreamMessage content. It sends out
 * the values in the StreamMessage if the receiving module has an output
 * with a corresponding name. Other values are ignored.
 *
 * Note that the type of value is unchecked and must match with the output type.
 */
public class MapMessageEventRecipient extends StreamEventRecipient {

	private Map<String, List<Output>> outputsByName = null;

	public MapMessageEventRecipient(Globals globals, Stream stream, Collection<StreamPartition> partitions) {
		super(globals, stream, partitions);
	}

	@Override
	protected void sendOutputFromModules(StreamMessage streamMessage) {
		if (outputsByName == null) {
			initCacheMap();
		}

		Map msg;
		try {
			msg = streamMessage.getContent();
		} catch (IOException e) {
			msg = new HashMap();
		}

		for (Map.Entry<String, List<Output>> entry : outputsByName.entrySet()) {
			String fieldName = entry.getKey();
			List<Output> outputs = entry.getValue();

			if (msg.containsKey(fieldName)) {
				Object fieldValue = msg.get(fieldName);

				// Null values are just not sent
				if (fieldValue == null) {
					continue;
				}

				for (Output o : outputs) {
					// Convert all numbers to doubles
					if (o instanceof TimeSeriesOutput) {
						try {
							o.send(((Number) fieldValue).doubleValue());
						} catch (ClassCastException e) {
							final String s = String.format("Stream field configuration has changed: cannot convert value '%s' to number", fieldValue);
							throw new StreamFieldChangedException(s);
						}
					} else {
						o.send(fieldValue);
					}
				}
			}
		}
	}

	private void initCacheMap() {
		outputsByName = new LinkedHashMap<>();

		for (AbstractSignalPathModule m : getModules()) {
			for (Output o : m.getOutputs()) {
				if (!outputsByName.containsKey(o.getName())) {
					outputsByName.put(o.getName(), new ArrayList<Output>());
				}

				outputsByName.get(o.getName()).add(o);
			}
		}
	}
}
