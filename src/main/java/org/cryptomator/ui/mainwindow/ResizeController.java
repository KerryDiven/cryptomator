package org.cryptomator.ui.mainwindow;

import org.cryptomator.common.settings.Settings;
import org.cryptomator.ui.common.FxController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;

@MainWindow
public class ResizeController implements FxController {

	private static final Logger LOG = LoggerFactory.getLogger(ResizeController.class);

	private final Stage window;

	public Region tlResizer;
	public Region trResizer;
	public Region blResizer;
	public Region brResizer;
	public Region tResizer;
	public Region rResizer;
	public Region bResizer;
	public Region lResizer;
	public Region lDefaultRegion;
	public Region tDefaultRegion;
	public Region rDefaultRegion;
	public Region bDefaultRegion;

	private double origX, origY, origW, origH;

	private final Settings settings;

	private final BooleanBinding showResizingArrows;

	@Inject
	ResizeController(@MainWindow Stage window, Settings settings) {
		this.window = window;
		this.settings = settings;
		this.showResizingArrows = window.fullScreenProperty().not();
	}

	@FXML
	public void initialize() {
		LOG.trace("init ResizeController");

		if (neverTouched()) {
			settings.displayConfiguration.set(getMonitorSizes());
			return;
		} else {
			if (didDisplayConfigurationChange()) {
				//If the position is illegal, then the window appears on the main screen in the middle of the window.
				Rectangle2D primaryScreenBounds = Screen.getPrimary().getBounds();
				window.setX((primaryScreenBounds.getWidth() - window.getMinWidth()) / 2);
				window.setY((primaryScreenBounds.getHeight() - window.getMinHeight()) / 2);
				window.setWidth(window.getMinWidth());
				window.setHeight(window.getMinHeight());
			} else {
				window.setHeight(settings.windowHeight.get() > window.getMinHeight() ? settings.windowHeight.get() : window.getMinHeight());
				window.setWidth(settings.windowWidth.get() > window.getMinWidth() ? settings.windowWidth.get() : window.getMinWidth());
				window.setX(settings.windowXPosition.get());
				window.setY(settings.windowYPosition.get());
			}
		}
		savePositionalSettings();
	}

	private boolean neverTouched() {
		return (settings.windowHeight.get() == 0) && (settings.windowWidth.get() == 0) && (settings.windowXPosition.get() == 0) && (settings.windowYPosition.get() == 0);
	}

	private boolean didDisplayConfigurationChange() {
		String currentDisplayConfiguration = getMonitorSizes();
		String settingsDisplayConfiguration = settings.displayConfiguration.get();
		boolean configurationHasChanged = !settingsDisplayConfiguration.equals(currentDisplayConfiguration);
		if (configurationHasChanged) settings.displayConfiguration.set(currentDisplayConfiguration);
		return configurationHasChanged;
	}

	private String getMonitorSizes() {
		ObservableList<Screen> screens = Screen.getScreens();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < screens.size(); i++) {
			Rectangle2D screenBounds = screens.get(i).getBounds();
			if (!sb.isEmpty()) sb.append(" ");
			sb.append("displayId: " + i + ", " + screenBounds.getWidth() + "x" + screenBounds.getHeight() + ";");
		}
		return sb.toString();
	}

	private void startResize(MouseEvent evt) {
		origX = window.getX();
		origY = window.getY();
		origW = window.getWidth();
		origH = window.getHeight();
	}

	@FXML
	private void resizeTopLeft(MouseEvent evt) {
		resizeTop(evt);
		resizeLeft(evt);
	}

	@FXML
	private void resizeTopRight(MouseEvent evt) {
		resizeTop(evt);
		resizeRight(evt);
	}

	@FXML
	private void resizeBottomLeft(MouseEvent evt) {
		resizeBottom(evt);
		resizeLeft(evt);
	}

	@FXML
	private void resizeBottomRight(MouseEvent evt) {
		resizeBottom(evt);
		resizeRight(evt);
	}

	@FXML
	private void resizeTop(MouseEvent evt) {
		startResize(evt);
		double newY = evt.getScreenY();
		double dy = newY - origY;
		double newH = origH - dy;
		if (newH < window.getMaxHeight() && newH > window.getMinHeight()) {
			window.setY(newY);
			window.setHeight(newH);
		}
	}

	@FXML
	private void resizeLeft(MouseEvent evt) {
		startResize(evt);
		double newX = evt.getScreenX();
		double dx = newX - origX;
		double newW = origW - dx;
		if (newW < window.getMaxWidth() && newW > window.getMinWidth()) {
			window.setX(newX);
			window.setWidth(newW);
		}
	}

	@FXML
	private void resizeBottom(MouseEvent evt) {
		double newH = evt.getSceneY();
		if (newH < window.getMaxHeight() && newH > window.getMinHeight()) {
			window.setHeight(newH);
		}
	}

	@FXML
	private void resizeRight(MouseEvent evt) {
		double newW = evt.getSceneX();
		if (newW < window.getMaxWidth() && newW > window.getMinWidth()) {
			window.setWidth(newW);
		}
	}

	@FXML
	public void savePositionalSettings() {
		settings.windowWidth.setValue(window.getWidth());
		settings.windowHeight.setValue(window.getHeight());
		settings.windowXPosition.setValue(window.getX());
		settings.windowYPosition.setValue(window.getY());
	}

	public BooleanBinding showResizingArrowsProperty() {
		return showResizingArrows;
	}

	public boolean isShowResizingArrows() {
		return showResizingArrows.get();
	}

}