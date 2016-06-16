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

package org.opentravel.diffutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.opentravel.schemacompiler.diff.ModelComparator;
import org.opentravel.schemacompiler.model.NamedEntity;
import org.opentravel.schemacompiler.model.TLLibrary;
import org.opentravel.schemacompiler.model.TLModel;
import org.opentravel.schemacompiler.model.TLOperation;
import org.opentravel.schemacompiler.model.TLService;
import org.opentravel.schemacompiler.repository.Project;
import org.opentravel.schemacompiler.repository.ProjectItem;
import org.opentravel.schemacompiler.repository.ProjectManager;
import org.opentravel.schemacompiler.repository.RepositoryItem;
import org.opentravel.schemacompiler.saver.LibrarySaveException;
import org.opentravel.schemacompiler.util.SchemaCompilerException;

/**
 * JavaFX controller class for the OTM-Diff application.
 */
public class DiffUtilityController {
	
	public static final String FXML_FILE = "/ota2-diff-util.fxml";
	
	private Stage primaryStage;
	
	@FXML private TextField oldProjectFilename;
	@FXML private TextField newProjectFilename;
	@FXML private TextField oldLibraryFilename;
	@FXML private TextField newLibraryFilename;
	@FXML private ChoiceBox<ChoiceItem> oldEntityChoice;
	@FXML private ChoiceBox<ChoiceItem> newEntityChoice;
	@FXML private Button oldProjectFileButton;
	@FXML private Button newProjectFileButton;
	@FXML private Button oldLibraryFileButton;
	@FXML private Button newLibraryFileButton;
	@FXML private Button oldLibraryRepoButton;
	@FXML private Button newLibraryRepoButton;
	@FXML private Button runProjectButton;
	@FXML private Button runLibraryButton;
	@FXML private WebView reportViewer;
	@FXML private Button saveReportButton;
	@FXML private Label statusBarLabel;
	
	private File oldProjectFile;
	private File newProjectFile;
	private File oldLibraryFile;
	private File newLibraryFile;
	private RepositoryItem oldLibraryRepo;
	private RepositoryItem newLibraryRepo;
	
	private ProjectManager oldProjectManager = new ProjectManager( new TLModel(), false, null );
	private ProjectManager newProjectManager = new ProjectManager( new TLModel(), false, null );
	
	/**
	 * Called when the user clicks the button select the file for the old version
	 * of an OTM project.
	 * 
	 * @param event  the action event that triggered this method call
	 */
	@FXML public void selectOldProject(ActionEvent event) {
		UserSettings userSettings = UserSettings.load();
		FileChooser chooser = newFileChooser( "Select Old Project Version",
				userSettings.getOldProjectFolder(), "otp", "OTM Project Files" );
		File selectedFile = chooser.showOpenDialog( primaryStage );
		
		if (selectedFile != null) {
			oldProjectFile = selectedFile;
			oldProjectFilename.setText( selectedFile.getName() );
			updateControlStates();
			userSettings.setOldProjectFolder( selectedFile.getParentFile() );
			userSettings.save();
		}
	}
	
	/**
	 * Called when the user clicks the button select the file for the new version
	 * of an OTM project.
	 * 
	 * @param event  the action event that triggered this method call
	 */
	@FXML public void selectNewProject(ActionEvent event) {
		UserSettings userSettings = UserSettings.load();
		FileChooser chooser = newFileChooser( "Select New Project Version",
				userSettings.getNewProjectFolder(), "otp", "OTM Project Files" );
		File selectedFile = chooser.showOpenDialog( primaryStage );
		
		if (selectedFile != null) {
			newProjectFile = selectedFile;
			newProjectFilename.setText( selectedFile.getName() );
			updateControlStates();
			userSettings.setNewProjectFolder( selectedFile.getParentFile() );
			userSettings.save();
		}
	}
	
	/**
	 * Called when the user clicks the button select the old version library from a
	 * file on the local file system.
	 * 
	 * @param event  the action event that triggered this method call
	 */
	@FXML public void selectOldLibraryFromFile(ActionEvent event) {
		UserSettings userSettings = UserSettings.load();
		FileChooser chooser = newFileChooser( "Select Old Library Version",
				userSettings.getOldLibraryFolder(), "otm", "OTM Library Files" );
		File selectedFile = chooser.showOpenDialog( primaryStage );
		
		if (selectedFile != null) {
			Runnable r = new BackgroundTask( "Loading Library: " + selectedFile.getName() ) {
				public void execute() throws Throwable {
					try {
						TLLibrary library;
						
						oldLibraryRepo = null;
						oldLibraryFile = selectedFile;
						setFilenameText( selectedFile.getName(), oldLibraryFilename );
						library = loadLibrary( selectedFile, oldProjectManager );
						updateEntityList( oldEntityChoice, library );
						
					} finally {
						closeAllProjects( oldProjectManager );
						userSettings.setOldLibraryFolder( selectedFile.getParentFile() );
						userSettings.save();
					}
				}
			};
			
			new Thread( r ).start();
		}
		
	}
	
	/**
	 * Called when the user clicks the button select the old version library from a
	 * remote repository.
	 * 
	 * @param event  the action event that triggered this method call
	 */
	@FXML public void selectOldLibraryFromRepo(ActionEvent event) {
		SelectLibraryDialogController controller = initSelectLibraryFromRepoDialog();
		
		if (controller != null) {
			controller.showAndWait();
			
			if (controller.isOkSelected()) {
				final RepositoryItem selectedItem = controller.getSelectedRepositoryItem();
				Runnable r = new BackgroundTask( "Loading Library: " + selectedItem.getFilename() ) {
					public void execute() throws Throwable {
						try {
							TLLibrary library;
							
							oldLibraryRepo = selectedItem;
							oldLibraryFile = null;
							setFilenameText( selectedItem.getFilename(), oldLibraryFilename );
							library = loadLibrary( selectedItem, oldProjectManager );
							updateEntityList( oldEntityChoice, library );
							
						} finally {
							closeAllProjects( oldProjectManager );
						}
					}
				};
				
				new Thread( r ).start();
			}
		}
	}
	
	/**
	 * Called when the user clicks the button select the new version library from a
	 * file on the local file system.
	 * 
	 * @param event  the action event that triggered this method call
	 */
	@FXML public void selectNewLibraryFromFile(ActionEvent event) {
		UserSettings userSettings = UserSettings.load();
		FileChooser chooser = newFileChooser( "Select New Library Version",
				userSettings.getNewLibraryFolder(), "otm", "OTM Library Files" );
		File selectedFile = chooser.showOpenDialog( primaryStage );
		
		if (selectedFile != null) {
			Runnable r = new BackgroundTask( "Loading Library: " + selectedFile.getName() ) {
				public void execute() throws Throwable {
					try {
						TLLibrary library;
						
						newLibraryRepo = null;
						newLibraryFile = selectedFile;
						setFilenameText( selectedFile.getName(), newLibraryFilename );
						library = loadLibrary( selectedFile, newProjectManager );
						updateEntityList( newEntityChoice, library );
						
					} finally {
						closeAllProjects( newProjectManager );
						userSettings.setNewLibraryFolder( selectedFile.getParentFile() );
						userSettings.save();
					}
				}
			};
			
			new Thread( r ).start();
		}
	}
	
	/**
	 * Called when the user clicks the button select the new version library from a
	 * remote repository.
	 * 
	 * @param event  the action event that triggered this method call
	 */
	@FXML public void selectNewLibraryFromRepo(ActionEvent event) {
		SelectLibraryDialogController controller = initSelectLibraryFromRepoDialog();
		
		if (controller != null) {
			controller.showAndWait();
			
			if (controller.isOkSelected()) {
				RepositoryItem selectedItem = controller.getSelectedRepositoryItem();
				Runnable r = new BackgroundTask( "Loading Library: " + selectedItem.getFilename() ) {
					public void execute() throws Throwable {
						try {
							TLLibrary library;
							
							newLibraryRepo = selectedItem;
							newLibraryFile = null;
							setFilenameText( selectedItem.getFilename(), newLibraryFilename );
							library = loadLibrary( selectedItem, newProjectManager );
							updateEntityList( newEntityChoice, library );
							
						} catch (Throwable t) {
							t.printStackTrace( System.out );
							
						} finally {
							setStatusMessage( null, false );
							updateControlStates();
							closeAllProjects( newProjectManager );
						}
					}
				};
				
				new Thread( r ).start();
			}
		}
	}
	
	/**
	 * Called when the user clicks the button to save the document currently displayed
	 * in the report viewer.
	 * 
	 * @param event  the action event that triggered this method call
	 */
	@FXML public void saveReport(ActionEvent event) {
		UserSettings userSettings = UserSettings.load();
		FileChooser chooser = newFileChooser( "Save Report",
				userSettings.getReportFolder(), "html", "HTML Files" );
		File targetFile = chooser.showSaveDialog( primaryStage );
		
		if (targetFile != null) {
			Runnable r = new BackgroundTask( "Saving Report" ) {
				protected void execute() throws Throwable {
					try {
						URL reportUrl = new URL( reportViewer.getEngine().getLocation() );
						File reportFile = new File( reportUrl.toURI() );
						
						try (InputStream in = new FileInputStream( reportFile )) {
							try (OutputStream out = new FileOutputStream( targetFile )) {
								byte[] buffer = new byte[1024];
								int count;
								
								while ((count = in.read( buffer, 0, buffer.length )) >= 0) {
									out.write( buffer, 0, count );
								}
							}
						}
						
					} finally {
						userSettings.setReportFolder( targetFile.getParentFile() );
						userSettings.save();
					}
				}
			};
			
			new Thread( r ).start();
		}
	}
	
	/**
	 * Initializes the dialog stage and controller used to select an OTM library
	 * from a remote repository.
	 * 
	 * @return SelectLibraryDialogController
	 */
	private SelectLibraryDialogController initSelectLibraryFromRepoDialog() {
		SelectLibraryDialogController controller = null;
		try {
			FXMLLoader loader = new FXMLLoader( DiffUtilityController.class.getResource(
					SelectLibraryDialogController.FXML_FILE ) );
			AnchorPane page = (AnchorPane) loader.load();
			Stage dialogStage = new Stage();
			Scene scene = new Scene( page );
			
			dialogStage.setTitle( "Select OTM Library" );
			dialogStage.initModality( Modality.WINDOW_MODAL );
			dialogStage.initOwner( primaryStage );
			dialogStage.setScene( scene );
			
			controller = loader.getController();
			controller.setDialogStage( dialogStage );
			controller.initializeTreeView();
			
		} catch (IOException e) {
			e.printStackTrace( System.out );
		}
		return controller;
	}
	
	/**
	 * Returns a new file chooser that is configured for the selection of a sepecific
	 * type of file.
	 * 
	 * @param title  the title of the new file chooser
	 * @param initialDirectory  the initial directory location for the chooser
	 * @param fileExtension  the file extension of the chooser's filter
	 * @param extensionDescription  description of the specified file extension
	 * @return FileChooser
	 */
	private FileChooser newFileChooser(String title, File initialDirectory,
			String fileExtension, String extensionDescription) {
		FileChooser chooser = new FileChooser();
		
		chooser.setTitle( title );
		chooser.setInitialDirectory( initialDirectory );
		chooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter(extensionDescription, "*." + fileExtension ),
				new FileChooser.ExtensionFilter("All Files", "*.*") );
		return chooser;
	}
	
	/**
	 * Updates the enabled/disables states of the visual controls based on the current
	 * state of user selections.
	 */
	private void updateControlStates() {
		Platform.runLater( new Runnable() {
			public void run() {
				String reportLocation = reportViewer.getEngine().getLocation();
				boolean reportDisplayed = (reportLocation != null) && reportLocation.startsWith( "file:" );
				boolean runProjectEnabled = (oldProjectFile != null) && oldProjectFile.exists()
						&& (newProjectFile != null) && newProjectFile.exists();
				boolean oldLibrarySelected = (((oldLibraryFile != null) && oldLibraryFile.exists()) || (oldLibraryRepo != null));
				boolean newLibrarySelected = (((newLibraryFile != null) && newLibraryFile.exists()) || (newLibraryRepo != null));
				boolean nullOldEntitySelected = (oldEntityChoice.getValue() != null) && (oldEntityChoice.getValue().value == null);
				boolean nullNewEntitySelected = (newEntityChoice.getValue() != null) && (newEntityChoice.getValue().value == null);
				boolean runLibraryEnabled = oldLibrarySelected && newLibrarySelected && (nullOldEntitySelected == nullNewEntitySelected);
				
				runProjectButton.disableProperty().set( !runProjectEnabled );
				runLibraryButton.disableProperty().set( !runLibraryEnabled );
				saveReportButton.disableProperty().set( !reportDisplayed );
			}
		});
	}
	
	/**
	 * Updates the value of the specified filename text field.
	 * 
	 * @param filenameValue  the value to assign to the filename text field
	 * @param textField  the text field to which the value will be assigned
	 */
	private void setFilenameText(String filenameValue, TextField textField) {
		Platform.runLater( new Runnable() {
			public void run() {
				textField.setText( filenameValue );
			}
		});
	}
	
	/**
	 * Displays the contents of the specified file in the HTML report viewer.
	 * 
	 * @param reportFile  the HTML report file to display
	 */
	private void showReport(final File reportFile) {
		Platform.runLater( new Runnable() {
			public void run() {
				if (reportFile != null) {
					reportViewer.getEngine().load( reportFile.toURI().toString() );
					
				} else {
					reportViewer.getEngine().loadContent( "" );
				}
			}
		});
	}
	
	/**
	 * Displays a message to the user in the status bar and optionally disables the
	 * interactive controls on the display.
	 * 
	 * @param message  the status bar message to display
	 * @param disableControls  flag indicating whether interactive controls should be disabled
	 */
	private void setStatusMessage(String message, boolean disableControls) {
		Platform.runLater( new Runnable() {
			public void run() {
				statusBarLabel.setText( message );
				oldEntityChoice.disableProperty().set( disableControls );
				newEntityChoice.disableProperty().set( disableControls );
				oldProjectFileButton.disableProperty().set( disableControls );
				newProjectFileButton.disableProperty().set( disableControls );
				oldLibraryFileButton.disableProperty().set( disableControls );
				newLibraryFileButton.disableProperty().set( disableControls );
				oldLibraryRepoButton.disableProperty().set( disableControls );
				newLibraryRepoButton.disableProperty().set( disableControls );
				runProjectButton.disableProperty().set( disableControls );
				runLibraryButton.disableProperty().set( disableControls );
			}
		});
	}
	
	/**
	 * Updates the given choice box with the list of entity names from the given library.
	 * 
	 * @param entityChoice  the choice box to update
	 * @param library  the library from which to obtain the list of entity names
	 */
	private void updateEntityList(ChoiceBox<ChoiceItem> entityChoice, TLLibrary library) {
		ObservableList<ChoiceItem> itemList = entityChoice.itemsProperty().get();
		ChoiceItem defaultItem = new ChoiceItem( "< All Entities >", null );
		final List<ChoiceItem> newItems = new ArrayList<>();
		
		newItems.add( defaultItem );
		
		for (NamedEntity entity : library.getNamedMembers()) {
			if (entity instanceof TLService) {
				for (TLOperation operation : ((TLService) entity).getOperations()) {
					newItems.add( new ChoiceItem("Operation: " + operation.getName() ) );
				}
				
			} else {
				newItems.add( new ChoiceItem( entity.getLocalName() ) );
			}
		}
		
		Platform.runLater( new Runnable() {
			public void run() {
				itemList.clear();
				itemList.addAll( newItems );
				entityChoice.setValue( defaultItem );
			}
		});
	}
	
	/**
	 * Loads and returns an OTM library from the specified file.
	 * 
	 * @param libraryFile  the library file to load
	 * @param projectManager  the project manager to use when processing the load
	 * @return TLLibrary
	 * @throws SchemaCompilerException  thrown if an error occurs during the library loading process
	 */
	private TLLibrary loadLibrary(File libraryFile, ProjectManager projectManager)
			throws SchemaCompilerException {
		Project tempProject;
		ProjectItem item;
		
		projectManager.closeAll();
		tempProject = newTempProject( projectManager );
		item = projectManager.addUnmanagedProjectItem( libraryFile, tempProject );
		return (TLLibrary) item.getContent();
	}
	
	/**
	 * Loads and returns an OTM library from a remote repository.
	 * 
	 * @param libraryItem  the library repository item to load
	 * @param projectManager  the project manager to use when processing the load
	 * @return TLLibrary
	 * @throws SchemaCompilerException  thrown if an error occurs during the library loading process
	 */
	private TLLibrary loadLibrary(RepositoryItem libraryItem, ProjectManager projectManager)
			throws SchemaCompilerException {
		Project tempProject;
		ProjectItem item;
		
		projectManager.closeAll();
		tempProject = newTempProject( projectManager );
		item = projectManager.addManagedProjectItem( libraryItem, tempProject );
		return (TLLibrary) item.getContent();
	}
	
	/**
	 * Creates a temporary project under which a library may be loaded.
	 * 
	 * @param projectManager  the project manager in which the new project should be created
	 * @return Project
	 * @throws LibrarySaveException  thrown if the new project cannot be created
	 */
	private Project newTempProject(ProjectManager projectManager) throws LibrarySaveException {
		try {
			return projectManager.newProject( File.createTempFile( "tempProject", ".otp" ),
					"http://diff-util.com/project/temp", "Temp Project", null );
			
		} catch (IOException e) {
			throw new LibrarySaveException("Unable to create temporary project file.", e);
		}
	}
	
	/**
	 * Closes all projects contained within the given project manager.
	 * 
	 * @param projectManager  the project manager for which to close all projects
	 */
	private void closeAllProjects(ProjectManager projectManager) {
		List<File> tempFiles = new ArrayList<>();
		
		for (Project p : projectManager.getAllProjects()) {
			if (p.getProjectId().equals( "http://diff-util.com/project/temp" )) {
				tempFiles.add( p.getProjectFile() );
			}
		}
		projectManager.closeAll();
		
		for (File tempFile : tempFiles) {
			tempFile.delete();
		}
	}
	
	/**
	 * Called when the user clicks the 'Run Comparison' button to compare two
	 * OTM projects.
	 * 
	 * @param event  the action event that triggered this method call
	 */
	@FXML public void runProjectComparison(ActionEvent event) {
		Runnable r = new BackgroundTask( "Running comparison..." ) {
			public void execute() throws Throwable {
				try {
					showReport( null );
					Project oldProject = oldProjectManager.loadProject( oldProjectFile );
					Project newProject = newProjectManager.loadProject( newProjectFile );
					File reportFile = File.createTempFile( "otmDiff", "html" );
					
					try (OutputStream out = new FileOutputStream( reportFile )) {
						ModelComparator.compareProjects( oldProject, newProject, out );
					}
					showReport( reportFile );
					reportFile.deleteOnExit();
					
				} finally {
					closeAllProjects( oldProjectManager );
					closeAllProjects( newProjectManager );
				}
			}
		};
		
		new Thread( r ).start();
	}
	
	/**
	 * Called when the user clicks the 'Run Comparison' button to compare two
	 * OTM libraries or entities.
	 * 
	 * @param event  the action event that triggered this method call
	 */
	@FXML public void runLibraryComparison(ActionEvent event) {
		Runnable r = new BackgroundTask( "Running comparison..." ) {
			public void execute() throws Throwable {
				try {
					File reportFile = File.createTempFile( "otmDiff", ".html" );
					TLLibrary oldLibrary, newLibrary;
					
					showReport( null );
					
					if (oldLibraryFile != null) {
						oldLibrary = loadLibrary( oldLibraryFile, oldProjectManager );
					} else if (oldLibraryRepo != null) {
						oldLibrary = loadLibrary( oldLibraryRepo, oldProjectManager );
					} else {
						throw new IllegalStateException("Old library version not accessible.");
					}
					
					if (newLibraryFile != null) {
						newLibrary = loadLibrary( newLibraryFile, newProjectManager );
					} else if (newLibraryRepo != null) {
						newLibrary = loadLibrary( newLibraryRepo, newProjectManager );
					} else {
						throw new IllegalStateException("New library version not accessible.");
					}
					
					if (oldEntityChoice.getValue().value == null) { // compare libraries
						
						try (OutputStream out = new FileOutputStream( reportFile )) {
							ModelComparator.compareLibraries( oldLibrary, newLibrary, out );
						}
						
					} else { // compare entities
						NamedEntity oldEntity = oldLibrary.getNamedMember( oldEntityChoice.getValue().value );
						NamedEntity newEntity = newLibrary.getNamedMember( newEntityChoice.getValue().value );
						
						if ((oldEntity == null) || (newEntity == null)) {
							throw new IllegalStateException("Selected entities are not accessible.");
						}
						
						try (OutputStream out = new FileOutputStream( reportFile )) {
							ModelComparator.compareEntities( oldEntity, newEntity, out );
						}
					}
					showReport( reportFile );
					reportFile.deleteOnExit();
					
				} finally {
					closeAllProjects( oldProjectManager );
					closeAllProjects( newProjectManager );
				}
			}
		};
		
		new Thread( r ).start();
	}
	
	/**
	 * Assigns the primary stage for the window associated with this controller.
	 *
	 * @param primaryStage  the primary stage for this controller
	 */
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
		oldEntityChoice.valueProperty().addListener( new ChangeListener<ChoiceItem>() {
			public void changed(ObservableValue<? extends ChoiceItem> observable, ChoiceItem oldValue, ChoiceItem newValue) {
				updateControlStates();
			}
		} );
		newEntityChoice.valueProperty().addListener( new ChangeListener<ChoiceItem>() {
			public void changed(ObservableValue<? extends ChoiceItem> observable, ChoiceItem oldValue, ChoiceItem newValue) {
				updateControlStates();
			}
		} );
		updateControlStates();
	}
	
	/**
	 * Abstract class that executes a background task in a non-UI thread.
	 */
	private abstract class BackgroundTask implements Runnable {
		
		private String statusMessage;
		
		/**
		 * Constructor that specifies the status message to display during task execution.
		 * 
		 * @param statusMessage  the status message for the task
		 */
		public BackgroundTask(String statusMessage) {
			this.statusMessage = statusMessage;
		}
		
		/**
		 * Executes the sub-class specific task functions.
		 * 
		 * @throws Throwable  thrown if an error occurs during task execution
		 */
		protected abstract void execute() throws Throwable;

		/**
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				setStatusMessage( statusMessage, true );
				execute();
				
			} catch (Throwable t) {
				String errorMessage = (t.getMessage() != null) ? t.getMessage() : "See log output for details.";
				
				try {
					setStatusMessage( "ERROR: " + errorMessage, false );
					updateControlStates();
					t.printStackTrace( System.out );
					Thread.sleep( 1000 );
					
				} catch (InterruptedException e) {}
				
			} finally {
				setStatusMessage( null, false );
				updateControlStates();
			}
		}
		
	}
	
	/**
	 * Encapsulates a single selectable item that may be included in a choice box.
	 */
	private static class ChoiceItem {
		
		public String label;
		public String value;
		
		/**
		 * Constructor that creates an item with the same value for its label and value.
		 * 
		 * @param value  the choice value to assign
		 */
		public ChoiceItem(String value) {
			this.label = this.value = value;
		}
		
		/**
		 * Constructor that creates an item with different values for its label and value.
		 * 
		 * @param label  the display label for the item
		 * @param value  the choice value to assign
		 */
		public ChoiceItem(String label, String value) {
			this.label = label;
			this.value = value;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return label;
		}
		
	}
	
}
