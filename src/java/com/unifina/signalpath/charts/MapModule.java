package com.unifina.signalpath.charts;

import com.unifina.datasource.ITimeListener;
import com.unifina.signalpath.*;
import com.unifina.utils.StreamrColor;

import java.util.*;

abstract class MapModule extends ModuleWithUI implements ITimeListener {
	private static final String DEFAULT_MARKER_ICON = "fa fa-4x fa-long-arrow-up";

	private final Input<Object> id = new Input<>(this, "id", "Object");
	private final Input<Object> label = new Input<>(this, "label", "Object");
	private final TimeSeriesInput latitude = new TimeSeriesInput(this, "latitude");
	private final TimeSeriesInput longitude = new TimeSeriesInput(this, "longitude");
	private final TimeSeriesInput heading = new TimeSeriesInput(this, "heading");		// degrees clockwise ("right-handed down")
	private final ColorParameter color = new ColorParameter(this, "traceColor", new StreamrColor(233, 87, 15));

	private double centerLat;
	private double centerLng;
	private int minZoom;
	private int maxZoom;
	private int zoom;
	private boolean autoZoom;
	private boolean drawTrace = false;
	private int traceRadius = 2;
	private boolean customMarkerLabel = false;

	private boolean directionalMarkers = false;
	private String markerIcon = DEFAULT_MARKER_ICON;

	private int expiringTimeInSecs = 0;
	private Set<MapPoint> expiringMapPoints = new LinkedHashSet<>();
	private long currentTime;

	MapModule(double centerLat, double centerLng, int minZoom, int maxZoom, int zoom, boolean autoZoom) {
		this.centerLat = centerLat;
		this.centerLng = centerLng;
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
		this.zoom = zoom;
		this.autoZoom = autoZoom;
	}

	@Override
	public void init() {
		addInput(id);
		addInput(latitude);
		addInput(longitude);
		this.canClearState = false;
		this.resendAll = false;
		this.resendLast = 0;
		latitude.setDrivingInput(true);
		latitude.canHaveInitialValue = false;
		latitude.canBeFeedback = false;
		longitude.setDrivingInput(true);
		longitude.canHaveInitialValue = false;
		longitude.canBeFeedback = false;
		id.setDrivingInput(true);
		id.canBeFeedback = false;
		id.requiresConnection = false;
		label.setDrivingInput(false);
		label.canBeFeedback = false;
		heading.requiresConnection = false;
		heading.canBeFeedback = false;
	}

	@Override
	public void initialize() {
		super.initialize();
		if (!id.isConnected()) {
			id.receive("id");
		}
	}

	@Override
	public void sendOutput() {
		MapPoint mapPoint = new MapPoint(
			id.getValue(),
			latitude.getValue(),
			longitude.getValue(),
			color.getValue()
		);
		if (expiringTimeInSecs > 0) {
			mapPoint.setExpirationTime(currentTime + (expiringTimeInSecs * 1000));
			expiringMapPoints.remove(mapPoint);
			expiringMapPoints.add(mapPoint);
		}
		if (customMarkerLabel) {
			mapPoint.put("label", label.getValue());
		}
		if (directionalMarkers) {
			mapPoint.put("dir", heading.getValue());
		}

		pushToUiChannel(mapPoint);
	}

	@Override
	public void clearState() {}

	@Override
	public java.util.Map<String, Object> getConfiguration() {
		java.util.Map<String, Object> config = super.getConfiguration();

		ModuleOptions options = ModuleOptions.get(config);
		options.addIfMissing(ModuleOption.createDouble("centerLat", centerLat));
		options.addIfMissing(ModuleOption.createDouble("centerLng", centerLng));
		options.addIfMissing(ModuleOption.createInt("minZoom", minZoom));
		options.addIfMissing(ModuleOption.createInt("maxZoom", maxZoom));
		options.addIfMissing(ModuleOption.createInt("zoom", zoom));
		options.addIfMissing(ModuleOption.createBoolean("autoZoom", autoZoom));
		options.addIfMissing(ModuleOption.createBoolean("drawTrace", drawTrace));
		options.addIfMissing(ModuleOption.createInt("traceRadius", traceRadius));
		options.addIfMissing(ModuleOption.createBoolean("markerLabel", customMarkerLabel));
		options.addIfMissing(ModuleOption.createBoolean("directionalMarkers", directionalMarkers));
		options.addIfMissing(ModuleOption.createInt("expiringTimeInSecs", expiringTimeInSecs));
		options.addIfMissing(ModuleOption.createString("markerIcon", markerIcon)
			.addPossibleValue("Default", DEFAULT_MARKER_ICON)
			.addPossibleValue("Long arrow", "fa fa-4x fa-long-arrow-up")
			.addPossibleValue("Short arrow", "fa fa-2x fa-arrow-up")
			.addPossibleValue("Circled arrow", "fa fa-2x fa-arrow-circle-o-up")
			.addPossibleValue("Wedge", "fa fa-3x fa-chevron-up")
			.addPossibleValue("Double wedge", "fa fa-4x fa-angle-double-up")
			.addPossibleValue("Circled wedge", "fa fa-2x fa-chevron-circle-up")
			.addPossibleValue("Triangle", "fa fa-4x fa-caret-up")
			.addPossibleValue("Triangle box", "fa fa-2x fa-caret-square-o-up")
// 			TODO: Implement rotation logic for these markers (default is 45 deg too much)
//			.addPossibleValue("Airplane", "fa fa-4x fa-plane")
//			.addPossibleValue("Rocket", "fa fa-4x fa-rocket")
		);

		return config;
	}

	@Override
	protected void onConfiguration(java.util.Map<String, Object> config) {
		super.onConfiguration(config);
		ModuleOptions options = ModuleOptions.get(config);

		if (options.containsKey("centerLat")) {
			centerLat = options.getOption("centerLat").getDouble();
		}

		if (options.containsKey("centerLng")) {
			centerLng = options.getOption("centerLng").getDouble();
		}

		if (options.containsKey("minZoom")) {
			minZoom = options.getOption("minZoom").getInt();
		}

		if (options.containsKey("maxZoom")) {
			maxZoom = options.getOption("maxZoom").getInt();
		}

		if (options.containsKey("zoom")) {
			zoom = options.getOption("zoom").getInt();
		}

		if (options.containsKey("autoZoom")) {
			autoZoom = options.getOption("autoZoom").getBoolean();
		}

		if (options.containsKey("drawTrace")) {
			drawTrace = options.getOption("drawTrace").getBoolean();
		}

		if (options.containsKey("traceRadius")) {
			traceRadius = options.getOption("traceRadius").getInt();
		}

		if (options.containsKey("markerLabel")) {
			customMarkerLabel = options.getOption("markerLabel").getBoolean();
		}

		if (options.containsKey("directionalMarkers")) {
			directionalMarkers = options.getOption("directionalMarkers").getBoolean();
		}

		if (options.containsKey("expiringTimeInSecs")) {
			expiringTimeInSecs = options.getOption("expiringTimeInSecs").getInt();
		}

		if (options.containsKey("markerIcon")) {
			markerIcon = options.getOption("markerIcon").getString();
		}

		if (drawTrace) {
			addInput(color);
		}

		if (customMarkerLabel) {
			addInput(label);
		}

		if (directionalMarkers) {
			addInput(heading);
		}
	}

	@Override
	public void setTime(Date time) {
		if (expiringTimeInSecs > 0) {
			currentTime = time.getTime();

			List<String> expiredMapPointIds = new ArrayList<>();
			Iterator<MapPoint> iterator = expiringMapPoints.iterator();
			MapPoint mapPoint;

			while (iterator.hasNext() && (mapPoint = iterator.next()).getExpirationTime() <= currentTime) {
				iterator.remove();
				expiredMapPointIds.add((String) mapPoint.get("id"));
			}
			if (!expiredMapPointIds.isEmpty()) {
				pushToUiChannel(new MarkerDeletionList(expiredMapPointIds));
			}
		}
	}

	private static class MapPoint extends LinkedHashMap<String, Object> {
		private Long expirationTime;

		private MapPoint(Object id, Double latitude, Double longitude, StreamrColor color) {
			put("t", "p");	// type: MapPoint
			put("id", id.toString());
			put("lat", latitude);
			put("lng", longitude);
			put("color", color.toString());
		}

		public String getId() {
			return (String) get("id");
		}

		Long getExpirationTime() {
			return expirationTime;
		}

		void setExpirationTime(long expirationTime) {
			this.expirationTime = expirationTime;
		}

		@Override
		public boolean equals(Object o) {
			return o != null && o instanceof MapPoint && getId().equals(((MapPoint) o).getId());
		}

		@Override
		public int hashCode() {
			return get("id").hashCode();
		}
	}

	private static class MarkerDeletionList extends LinkedHashMap<String, Object> {
		private MarkerDeletionList(List<String> listOfIds) {
			put("t", "d");
			put("list", listOfIds);
		}
	}
}
