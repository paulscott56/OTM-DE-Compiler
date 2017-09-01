/**
 * Copyright (C) 2014 OpenTravel Alliance (info@opentravel.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opentravel.messagevalidate;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;

import org.opentravel.schemacompiler.util.OTM16Upgrade;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * JavaFX application for the OTM-Diff Utility.
 */
public class OTMMessageValidatorApplication extends javafx.application.Application {
	
	/**
	 * Main method invoked from the command-line.
	 * 
	 * @param args the command-line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader( OTMMessageValidatorApplication.class.getResource(
					OTMMessageValidatorController.FXML_FILE ) );
			Parent root = loader.load();
			OTMMessageValidatorController controller = loader.getController();
			UserSettings userSettings = UserSettings.load();
			
			validateWindowLocation( userSettings );
			primaryStage.setTitle("Message Validation Utility");
			primaryStage.setScene( new Scene( root, userSettings.getWindowSize().getWidth(),
					userSettings.getWindowSize().getHeight() ) );
			primaryStage.setX( userSettings.getWindowPosition().getX() );
			primaryStage.setY( userSettings.getWindowPosition().getY() );
			
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				public void handle(WindowEvent we) {
					UserSettings settings = UserSettings.load();
					Scene scene = primaryStage.getScene();
					
					settings.setWindowPosition( new Point(
							primaryStage.xProperty().intValue(), primaryStage.yProperty().intValue() ) );
					settings.setWindowSize( new Dimension(
							scene.widthProperty().intValue(), scene.heightProperty().intValue() ) );
					settings.save();
				}
			});
			controller.setPrimaryStage( primaryStage );
			primaryStage.show();
			
		} catch (IOException e) {
			System.out.println(e.getMessage());
			throw new RuntimeException("Unable to initialize JavaFX application.", e);
		}
	}
	
	/**
	 * Compares the window dimensions contained within the given user settings against the
	 * current screen dimensions.  If the previous coordinates are outside of the current
	 * viewing area, they will be adjusted so that the main window is visible.
	 * 
	 * @param userSettings  the user settings that specify the preferred window size and location
	 */
	private void validateWindowLocation(UserSettings userSettings) {
		Point position = new Point( userSettings.getWindowPosition() );
		Dimension size = userSettings.getWindowSize();
		Rectangle windowBounds = new Rectangle( position.x, position.y, size.width, size.height);
		Rectangle screenBounds = new Rectangle( 0, 0, 0, 0 );
		
		// Calculate the full dimensions of the visible screen area
		for (Screen screen : Screen.getScreens()) {
			Rectangle2D bounds = screen.getBounds();
			
			screenBounds.x = Math.min( screenBounds.x, (int) bounds.getMinX() );
			screenBounds.y = Math.min( screenBounds.y, (int) bounds.getMinY() );
		}
		for (Screen screen : Screen.getScreens()) {
			Rectangle2D bounds = screen.getBounds();
			
			screenBounds.width  = Math.max( screenBounds.width - screenBounds.x, (int) bounds.getWidth() - screenBounds.x );
			screenBounds.height = Math.max( screenBounds.height - screenBounds.y, (int) bounds.getHeight() - screenBounds.y );
		}
		
		// If the window is outside of the visible area, move the origin to the default
		// coordinates (0,0)
		if (!screenBounds.contains(windowBounds)) {
			userSettings.setWindowPosition( new Point( 0, 0 ) );
			userSettings.setWindowSize( UserSettings.getDefaultSettings().getWindowSize() );
		}
	}
	
	/**
	 * Since this is a read-only application, enable the OTM 1.6 file format for
	 * all operations.
	 */
	static {
		OTM16Upgrade.otm16Enabled = true;
	}
	
}