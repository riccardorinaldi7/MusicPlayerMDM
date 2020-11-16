package com.ktplayer;

import com.mpatric.mp3agic.*;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.*;

public class ControllerSimple {
    private void print(String str) { System.out.println(str);}
    
    @FXML
    private AnchorPane window;
    @FXML
    private ResourceBundle resources ;
    @FXML
    private Stage stage;
    
    //---------------------------------------------------------------------------------------------------------
    // 2 buttons on the title bar: minimize windows, quit the program
    //---------------------------------------------------------------------------------------------------------
    
    @FXML
    private Pane exit;
    @FXML
    private Pane minimize;
    
    @FXML
    private ImageView exit_icon;
    @FXML
    private ImageView minimize_icon;
    
    //---------------------------------------------------------------------------------------------------------
    // 4 buttons on top: Language, Theme, Interface, Help
    //---------------------------------------------------------------------------------------------------------
    
    @FXML
    private Button languageButton;
    @FXML
    private Button themeButton;
    @FXML
    private Button helpButton;
    @FXML
    private Button interfaceButton;
    
    //---------------------------------------------------------------------------------------------------------
    // 3 buttons under label "Music Player": Add song, Remove song, Quit the program
    //---------------------------------------------------------------------------------------------------------
    
    @FXML
    private ImageView exitProgramButton;
    @FXML
    private ImageView removeButton;
    @FXML
    private ImageView addButton;

    //---------------------------------------------------------------------------------------------------------
    // Pane with songTable
    //---------------------------------------------------------------------------------------------------------
    
    @FXML
    private TableView<Song> songTable;
    @FXML
    private TableColumn<Song, String> idColumn;
    @FXML
    private TableColumn<Song, String> artistNameColumn;
    @FXML
    private TableColumn<Song, String> songNameColumn;

    //---------------------------------------------------------------------------------------------------------
    // Pane with song name and artist playing
    //---------------------------------------------------------------------------------------------------------
    
    @FXML
    private Label artistName;
    @FXML
    private Label songName;

    //---------------------------------------------------------------------------------------------------------
    // Right panel for volume
    //---------------------------------------------------------------------------------------------------------
    @FXML
    private Label volumeValue;
    @FXML
    private ImageView minusVol;
    @FXML
    private ImageView plusVol;
    @FXML
    private ImageView muteIcon;
    @FXML
    private ImageView volumeIcon;
    
    //---------------------------------------------------------------------------------------------------------
    // Left panel with 3 buttons: previous song, play/pause, next song
    //---------------------------------------------------------------------------------------------------------
    
    @FXML
    private ImageView playButton;
    @FXML
    private ImageView pauseButton;
    @FXML
    private ImageView nextSongButton;
	@FXML
    private ImageView previousSongButton;
    
	//---------------------------------------------------------------------------------------------------------
    
    private String currentTheme;
    private String currentLanguage;
    private Boolean currentlyMuted = false;
    private Double volumeBeforeMute = 0.00;
    private Utilities util;
    
    //---------------------------------------------------------------------------------------------------------
    
    private Main main;

    private List<MediaPlayer> players;
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;

    private boolean isAutoplay;
    private int volume;
    private String path;

    private double xOffset = 0;
    private double yOffset = 0;

    private FadeTransition fadeIn = new FadeTransition();
    private FadeTransition fadeOut = new FadeTransition();
    private Song currentActive;
    private Song currentPlaying;

    //---------------------------------------------------------------------------------------------------------
    // Builder
    //---------------------------------------------------------------------------------------------------------
   
    public ControllerSimple() {
        players = new ArrayList<>();
        
        isAutoplay = false;
        volume = 50; // between 0 and 1
        stage = Main.getStage();
        stage.getIcons().add(new Image(ClassLoader.getSystemResource("images/logo.png").toExternalForm()));
        util = new Utilities();
        currentTheme = util.getCurrentTheme();
        currentLanguage = util.getCurrentLanguage();
    }

    //---------------------------------------------------------------------------------------------------------
    // HANDLERS
    //---------------------------------------------------------------------------------------------------------
    
    @FXML
    private void initialize() throws Exception {

        setIcons();					//set icons depending on the theme
        insertToolTips();			//attach tooltip to the main buttons
        
        window.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = stage.getX() - event.getScreenX();
                yOffset = stage.getY() - event.getScreenY();
            }
        });

        window.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() + xOffset);
                stage.setY(event.getScreenY() + yOffset);
            }
        });

        minimize.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setIconified(true);
            }
        });

        exit.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                closeProgram(); //creato handler da riutilizzare
            }
        });

        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        artistNameColumn.setCellValueFactory(cellData -> cellData.getValue().artistNameProperty());
        songNameColumn.setCellValueFactory(cellData -> cellData.getValue().songNameProperty());
        showSongInfo((Song) null);

        //this runs everytime an item in tableview is single-clicked/selected
        songTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("SongTable: selection detected - " + newValue.getSongName());
            currentActive = newValue;
        });
        
        addButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	chooseFile();
            }
        });

        removeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                removeSong();
            }
        });
        
        exitProgramButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	closeProgram();
            }
        });

        minusVol.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(volume > 0) changeVolume(-10);
            }
        });

        plusVol.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(volume < 100) changeVolume(10);
            }
        });
    }

    private void removeSong() {
        if(currentActive == null) return;

        //remove both from tableview and from players
        int idRemovedSong = Integer.parseInt(currentActive.getId()) - 1;
        String nameRemovedSong = currentActive.getSongName();

        /************ WARNING ***********
         * when you remove a selected item from a tableview,
         * the item after the removed one will be automatically selected
         */
        songTable.getItems().remove(currentActive);
        updateSongIds(songTable.getItems(), idRemovedSong);
        songTable.refresh();
        print("removeFile: removed " + nameRemovedSong + " from table...");
        players.remove(idRemovedSong);
        //print("removeFile: removed player's song");
        //print("Current player list: ");
        //for(int i = 0; i < players.size(); i++) print(players.get(i).getMedia().getSource());

        //reset the onEndOfMedia of previous Song
        MediaPlayer prevPlayer = players.get(idRemovedSong - 1);
        MediaPlayer nextPlayer = players.get(idRemovedSong % players.size());
        //print("I'm setting " + nextPlayer.getMedia().getSource() + " as next song for " + prevPlayer.getMedia().getSource());
        setEndOfMedia(prevPlayer, nextPlayer);
    }

    private void updateSongIds(ObservableList<Song> items, int start) {
        for(int i=start; i<items.size(); i++)
            items.get(i).setId(Integer.toString(i+1));

    }

    //choose multiple files
    private void chooseFile() {
        FileChooser chooser = new FileChooser();
        List<File> selectedFilesList = chooser.showOpenMultipleDialog(stage);
        if(selectedFilesList == null) {
            System.out.println("No files selected!");
            return;
        }
        File[] selectedFiles = selectedFilesList.toArray(new File[selectedFilesList.size()]);
        importSongs(selectedFiles);
    }

    private void importSongs(File[] selectedFiles) {
        try {
                /*if(!(players.isEmpty())) {
                    players.clear();
                    System.out.println("new array list");
                }*/
            ObservableList<Song> currentList = songTable.getItems();
            ObservableList<Song> newList = createSongsAndMediaPlayers(selectedFiles);
            //set endofmedia of new mediaPlayers
            if(mediaView != null && !currentList.isEmpty()){
                for(int i=currentList.size()-1; i<newList.size()+currentList.size(); i++){
                    print("reset of media " + (i+1) + ": next song #" + ((i+1)%players.size()+1));
                    setEndOfMedia(players.get(i), players.get((i+1) % players.size()));
                }
            }
            songTable.setItems(appendList(currentList, newList));
            songTable.setOnMouseClicked((MouseEvent e) -> {
                     /*if(e.getClickCount() == 1) {
                    	     try {
		                    	 print("SongTable: prendo in carico...");
                    	         takeCare();
		                     }
		                     catch (Exception ex) {}
                    	 }*/
                if (e.getClickCount() == 2) {    //double-clik to play a song
                    try {
                        takeCare();
                        if(currentPlaying != null) showSongInfo(currentPlaying);
                        print("SongTable: play - " + currentPlaying.getSongName());
                        //playPauseSong(); it is better to play inside takeCare() -> playPauseSong(song)
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            });
        } catch(Exception e) {}
    }

    private ObservableList<Song> appendList(ObservableList<Song> currentList, ObservableList<Song> newList) {
        for(int i=0; i<newList.size(); i++) currentList.add(newList.get(i));
        return currentList;
    }

    // Loads all files in a directory and creates Song and MediaPlayer for each file
    public ObservableList<Song> createSongsAndMediaPlayers(File[] files)   throws Exception{
        ObservableList<Song> songData = FXCollections.observableArrayList();
        String name;
        int i = players.size();
        for(File file : files) {
            if(file.isFile()) {
                name = file.getName();
                if(name.endsWith("mp3") || name.endsWith("wav")) {
                    try {
                        i++; //song id from 1 to n
                        Mp3File mp3 = new Mp3File(file.getPath());
                        ID3v2 tag = mp3.getId3v2Tag();
                        String title = tag.getTitle() == null ? name.split("[.]")[0] : tag.getTitle(); //use filename if no song title exists
                        Song song = new Song(String.valueOf(i), tag.getArtist(), title, kbToMb(file.length()), secToMin(mp3.getLengthInSeconds()),tag.getAlbum(), file.getAbsolutePath(), tag.getAlbumImage());
                        players.add(createMediaPlayer(file.getAbsolutePath()));
                        songData.add(song);
                    }
                    catch(IOException e) {e.printStackTrace();}
                }
            }
        }
        i = 0;
        System.out.println(players.size());
        return  songData;
    }

    private void setEndOfMedia(MediaPlayer prevPlayer, MediaPlayer nextPlayer) {
        prevPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                //print("New setOnEndOfMedia detected!");
                mediaView.getMediaPlayer().stop();
                mediaView.getMediaPlayer().seek(Duration.ZERO);
                mediaPlayer = nextPlayer;
                mediaView.setMediaPlayer(mediaPlayer);
                mediaView.getMediaPlayer().seek(Duration.ZERO);
                updateValues();
                mediaPlayer.setVolume(((double)volume)/100);
                mediaPlayer.play();
                viewPauseIcon();
            }
        });
    }
    
    private void closeProgram() {
        //confirmation alert before quit
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setContentText(resources.getString("sureToClose"));
        alert.setHeaderText(null);
        alert.initStyle(StageStyle.UNDECORATED); //toglie completamente la barra del titolo
        alert.setTitle(resources.getString("confirmExit"));
        ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("file:src/main/resources/images/logo.png"));
        
        alert.setGraphic(new ImageView(new Image("file:src/main/resources/images/exit.png")));
        
        util.applyThemeToDialog(alert);
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            System.exit(0);
        } else { }
    }

    public String kbToMb(long length) {
        Long l = length;
        double d = l.doubleValue();
        DecimalFormat df = new DecimalFormat("#.00");
        String form = df.format((d/1024)/1024);
        return form + "Mb";
    }

    public String secToMin(long sec) {
        Long s = sec;
        String time = null;
        if((s%60) < 10) {
            time = s/60 + ":0" + s%60;
        }
        else {
            time = s/60 + ":" + s%60;
        }
        return time;
    }

    @FXML
    private void languageClicked() {
    	System.out.println("Change the language...");
    	
    	String rootPath = "src\\main\\resources\\";
        String appConfigPath = rootPath + "application.properties";

        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(appConfigPath));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Trying to get the language setting
        String language = appProps.getProperty("language");

        //Creating a choice box asking for the language
        ChoiceDialog<String> choiceDialog = new ChoiceDialog<String>(util.returnLanguageToShow(language));
        choiceDialog.setHeaderText(resources.getString("selectlanguage"));
        choiceDialog.setTitle(resources.getString("language"));
        ((Stage)choiceDialog.getDialogPane().getScene().getWindow()).getIcons().add(
        		new Image("file:src/main/resources/images/logo.png"));
        Image img = new Image("file:src/main/resources/images/languages.png");
        choiceDialog.setGraphic(new ImageView(img));

        //Retrieving the observable list
        ObservableList<String> list = choiceDialog.getItems();
        //Adding items to the language list
        list.add("English");
        list.add("Italiano");
        list.add("Français");
        list.add("Español");
        //list.remove(language); //rimuovo lingua corrente affinche' non sia selezionabile
        
        choiceDialog.initStyle(StageStyle.UNDECORATED); //toglie completamente la barra del titolo
        util.applyThemeToDialog(choiceDialog);
        
        // Show the dialog box and wait for a selection
        Optional<String> selectedLanguage = choiceDialog.showAndWait();
        
        if (selectedLanguage.isPresent()) {		// Ok was pressed
		    
			language = selectedLanguage.get();
			
			//alert per dire che bisogna riavviare il programma
	        Alert alert = new Alert(AlertType.CONFIRMATION);
	        alert.setTitle(resources.getString("attention"));
	        alert.setHeaderText(null);
	        alert.setContentText(resources.getString("restart"));
	        ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(
	        		new Image("file:src/main/resources/images/logo.png"));
	        alert.setGraphic(new ImageView(new Image("file:src/main/resources/images/languages.png")));
	        alert.initStyle(StageStyle.UNDECORATED); //toglie completamente la barra del titolo
	        util.applyThemeToDialog(alert);

	        ButtonType okButton = new ButtonType("Ok", ButtonData.OK_DONE);
	        ButtonType buttonTypeCancel = new ButtonType(resources.getString("quitButton"), ButtonData.CANCEL_CLOSE);

	        alert.getButtonTypes().setAll(okButton, buttonTypeCancel);

	        Optional<ButtonType> result = alert.showAndWait();

	        if (result.get() == okButton) {	
	        	// ... user chose "Ok" --> don't close the program now
	        	try {
		        	appProps.setProperty("language", util.returnLanguageToWrite(language));
		            appProps.store(new FileWriter(appConfigPath), null);
	        	} 
		        catch (IOException e) {
		                e.printStackTrace();
		        }   	
	        } 
	        
	        else {	
	        	// ... user chose CANCEL or closed the dialog --> the program will close, after having set the properties
	        	try {
	        		appProps.setProperty("language", util.returnLanguageToWrite(language));
		            appProps.store(new FileWriter(appConfigPath), null);
	        	} 
		        catch (IOException e) {
		                e.printStackTrace();
		        }   
	        	closeProgram();
	        }
		} 
        else {
			System.out.println("User has changed his/her mind");
		}
    }
    
    @FXML
    private void themeClicked() {
    	System.out.println("Change the theme...");
    	
       String rootPath = "src\\main\\resources\\";
        String appConfigPath = rootPath + "application.properties";

        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(appConfigPath));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Trying to get the language setting
        String theme = appProps.getProperty("theme");

        //Creating a choice box asking for the language
        ChoiceDialog<String> choiceDialog = new ChoiceDialog<String>(resources.getString(theme.toLowerCase()));
        choiceDialog.setHeaderText(resources.getString("selecttheme"));
        choiceDialog.setTitle(resources.getString("theme"));
        ((Stage)choiceDialog.getDialogPane().getScene().getWindow()).getIcons().add(
       		 new Image("file:src/main/resources/images/logo.png"));
        Image img = new Image("file:src/main/resources/images/theme.png");
        choiceDialog.setGraphic(new ImageView(img));
      
        choiceDialog.initStyle(StageStyle.UNDECORATED); //toglie completamente la barra del titolo
        
        util.applyThemeToDialog(choiceDialog);

      //Retrieving the observable list
        ObservableList<String> list = choiceDialog.getItems();
        //Adding items to the language list
        list.add(resources.getString("dark"));
        list.add(resources.getString("light"));
        //list.remove(theme); //rimuovo tema corrente affinche' non sia selezionabile
        
        // Show the dialog box and wait for a selection
        Optional<String> selectedTheme = choiceDialog.showAndWait();

        if (selectedTheme.isPresent()) {		// Ok was pressed
		    
			theme = selectedTheme.get();
			
			//alert per dire che bisogna riavviare il programma
	        Alert alert = new Alert(AlertType.CONFIRMATION);
	        alert.setTitle(resources.getString("attention"));
	        alert.setHeaderText(null);
	        alert.setContentText(resources.getString("restart"));
	        ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(
	        		new Image("file:src/main/resources/images/logo.png"));
	        alert.setGraphic(new ImageView(new Image("file:src/main/resources/images/theme.png")));
	        alert.initStyle(StageStyle.UNDECORATED); //toglie completamente la barra del titolo
	        util.applyThemeToDialog(alert);

	        ButtonType okButton = new ButtonType("Ok", ButtonData.OK_DONE);
	        ButtonType buttonTypeCancel = new ButtonType(resources.getString("quitButton"), ButtonData.CANCEL_CLOSE);

	        alert.getButtonTypes().setAll(okButton, buttonTypeCancel);

	        Optional<ButtonType> result = alert.showAndWait();

	        if (result.get() == okButton) {	
	        	// ... user chose "Ok" --> don't close the program now
	        	try {
		        	appProps.setProperty("theme", util.returnThemeToWrite(theme));
		            appProps.store(new FileWriter(appConfigPath), null);
	        	} 
		        catch (IOException e) {
		                e.printStackTrace();
		        }   	
	        } 
	        
	        else {	
	        	// ... user chose CANCEL or closed the dialog --> the program will close, after having set the properties
	        	try {
	        		appProps.setProperty("theme", util.returnThemeToWrite(theme));
		            appProps.store(new FileWriter(appConfigPath), null);
	        	} 
		        catch (IOException e) {
		                e.printStackTrace();
		        }   
	        	closeProgram();
	        }
		} 
        else {
			System.out.println("User has changed his/her mind");
		}
    }
    
    @FXML
    private void setAdvancedInterface() {
    	System.out.println("Switch interface...");
        String rootPath = "src\\main\\resources\\";
        String appConfigPath = rootPath + "application.properties";

        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream(appConfigPath));
        }
        catch (IOException e) {
        	e.printStackTrace();
        } 
        catch (NoSuchElementException e){
        	System.out.println("User has changed his/her mind");
        }

        //alert per dire che bisogna riavviare il programma
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(resources.getString("attention"));
        alert.setHeaderText(null);
        alert.setContentText(resources.getString("restart"));
        ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(
                new Image("file:src/main/resources/images/logo.png"));

        alert.initStyle(StageStyle.UNDECORATED); //toglie completamente la barra del titolo
        util.applyThemeToDialog(alert);
        alert.setGraphic(new ImageView(new Image("file:src/main/resources/images/interfaces.png")));
        
        ButtonType okButton = new ButtonType("Ok", ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType(resources.getString("quitButton"), ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(okButton, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == okButton) {	
        	// ... user chose "Ok" --> don't close the program now
        	try {
        		appProps.setProperty("interface", "Advanced");
                appProps.store(new FileWriter(appConfigPath), null);
        	} 
            catch (IOException e) {
                    e.printStackTrace();
            }   	
        } 
        
        else {	
        	// ... user chose CANCEL or closed the dialog --> the program will close, after having set the properties
        	try {
        		appProps.setProperty("interface", "Advanced");
                appProps.store(new FileWriter(appConfigPath), null);
        	} 
            catch (IOException e) {
                    e.printStackTrace();
            }   
        	closeProgram();
        }  
    }
    
    @FXML
    private void helpClicked() {
    	System.out.println("See the tutorial...");
    	Desktop desktop = Desktop.getDesktop();
		try {
			desktop.browse(new URI("https://www.youtube.com/watch?v=ANbDIIsi5Pg&t=3s"));
		} 
		catch (IOException e1) {
			e1.printStackTrace();
		} 
		catch (URISyntaxException e1) {					
			e1.printStackTrace();
		}
    }
    
    //---------------------------------------------------------------------------------------------------------
    // OTHER
    //---------------------------------------------------------------------------------------------------------
    
    public void showSongInfo(Song song) {
        String artistLabel = "-";
        String songLabel = "-";
        String albumLabel = "-";
        if(song != null) {
            if(song.getArtistName() != null) artistLabel = song.getArtistName();
            if(song.getSongName() != null) songLabel = song.getSongName();
            if(song.getFormat() != null) albumLabel = song.getFormat();
        }
        artistName.setText(artistLabel);
        songName.setText(songLabel);
   
    }

    public void playPauseSong(Song song) throws Exception{
        if(song != null) {

            File file = new File(song.getUrl());
            String path = file.getAbsolutePath();
            path.replace("\\", "/");

            // If there's another media set, we remove its mediaview and mediaplayer before setting the new ones
            if((mediaView != null) && (mediaPlayer != null)) {

                print("playPauseSong(Song): deleting old mediaview and mediaplayer...");

                //volume = ((int)mediaView.getMediaPlayer().getVolume()) * 100; //?? why ??
                //questo è quello che fa stoppare la canzone quando ne premo un'altra nella lista
                mediaView.getMediaPlayer().stop();
                mediaView = null;
                mediaPlayer = null;
            }

            Media media = new Media(new File(path).toURI().toString());

            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.stop(); // why?? ha paura che parta? ahah
            mediaPlayer.setAutoPlay(false);

            mediaView = new MediaView(mediaPlayer);
            //viewPlayIcon();
            mediaView = new MediaView(players.get(Integer.parseInt(song.getId()) - 1));

            // Put some value before playing
            mediaView.getMediaPlayer().setVolume(((double)volume)/100);
            mediaView.getMediaPlayer().seek(Duration.ZERO);

            // create a Thread that runs throughout the song and update values of slider, timing and volume
            updateValues();

            mediaView.mediaPlayerProperty().addListener(new ChangeListener<MediaPlayer>() {
                @Override
                public void changed(ObservableValue<? extends MediaPlayer> observable, MediaPlayer oldValue, MediaPlayer newValue) {
                    try {
                        //print("mediaView: mediaPlayer change detected...");
                        showSongInfo(newValue);
                        updateValues();
                    }
                    catch(IOException e) {}
                    catch(UnsupportedTagException e) {}
                    catch(InvalidDataException e) {} catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            pauseButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(mediaView.getMediaPlayer() != null) {
                        mediaView.getMediaPlayer().pause();
                        viewPlayIcon();
                        setPlayButtonHandler();
                        updateValues();
                    }
                }
            });

            nextSongButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(mediaView.getMediaPlayer() != null && players != null) seekAndUpdate(players.get(players.indexOf(mediaView.getMediaPlayer())).getTotalDuration());
                }
            });

            previousSongButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    playPreviousSong();
                }
            });

            // TODO: reset all players everytime we takeCare of a Song?? Too expensive...should be fixed
            for (int i = ((players.indexOf(mediaView.getMediaPlayer())) % players.size()); i < players.size(); i++) {
                final MediaPlayer player = players.get(i);
                mediaPlayer = player;
                final MediaPlayer nextPlayer = players.get((i + 1) % players.size());
                setEndOfMedia(mediaPlayer, nextPlayer);
            }

            playPauseSong();

        }
        else {
            print("Come posso essere qui? PARADOSSOOOOO");
            if(pauseButton.isVisible()) {
                if ((mediaPlayer != null) && (mediaView != null)) {
                    mediaPlayer = mediaView.getMediaPlayer();
                    mediaPlayer.stop();
                    mediaView = null;
                    mediaPlayer = null;
                }
                viewPlayIcon();
            }
            System.out.println("Song does not exist!");
        }
    }

    private void playPreviousSong() {
        if(mediaView.getMediaPlayer() == null) return;
        if(mediaView.getMediaPlayer().getCurrentTime().toSeconds() > 5) seekAndUpdate(Duration.ZERO);
        else{
            mediaView.getMediaPlayer().stop();
            MediaPlayer prevPlayer = players.get((players.indexOf(mediaView.getMediaPlayer()) - 1) % players.size());
            print("PrevSong: " + prevPlayer.getMedia().getSource());
            mediaView.setMediaPlayer(prevPlayer);
            mediaView.getMediaPlayer().seek(Duration.ZERO);
            updateValues();
            mediaView.getMediaPlayer().setVolume(((double)volume)/100);
            mediaView.getMediaPlayer().play();
            viewPauseIcon();
        }
    }

    public void showSongInfo(MediaPlayer player) throws InvalidDataException, IOException, UnsupportedTagException {
        String artist = "-";
        String title = "-";
        String album = "-";
        if(player != null) {
            String source = player.getMedia().getSource();
            source = source.replace("/", "\\");
            source = source.replaceAll("%20", " ");
            source = source.replaceAll("%5B", "[");
            source = source.replaceAll("%5D", "]");
            source = source.substring(6, source.length());
            //print("Playing a new song...");
            Mp3File mp3 = new Mp3File(source);
            ID3v2 tag = mp3.getId3v2Tag();
            String splittedSource[] = source.split("\\\\");
            String name = splittedSource[splittedSource.length - 1];
            artist = tag.getArtist() == null ? "-" : tag.getArtist();
            title = tag.getTitle() == null ? name : tag.getTitle();
            album = tag.getAlbum() == null ? "-" : tag.getAlbum();
        }
        artistName.setText(artist);
        songName.setText(title);
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public MediaPlayer createMediaPlayer(String url) {
        url.replace("\\", "/");
        final Media media = new Media(new File(url).toURI().toString());
        final MediaPlayer player = new MediaPlayer(media);
        System.out.println("created MediaPlayer for: " + url);
        return player;
    }

    public Media createMedia(String url) {
        url.replace("\\", "/");
        final Media media = new Media(new File(url).toURI().toString());
        return media;
    }

    public void viewPauseIcon() {
        playButton.setVisible(false);
        playButton.setDisable(true);
        pauseButton.setVisible(true);
        pauseButton.setDisable(false);
    }

    public void viewPlayIcon() {
        pauseButton.setVisible(false);
        pauseButton.setDisable(true);
        playButton.setVisible(true);
        playButton.setDisable(false);
    }

    public void setCurrentlyPlayer(MediaPlayer player) throws InvalidDataException, IOException, UnsupportedTagException {
        String source = player.getMedia().getSource();
        source = source.replace("/","\\");
        source = source.replaceAll("%20", " ");
        source = source.replaceAll("%5B", "[");
        source = source.replaceAll("%5D", "]");
        source = source.substring(6,source.length());
        System.out.println(source + " +++");
        Mp3File mp3 = new Mp3File(source);
        ID3v2 tag = mp3.getId3v2Tag();
        artistName.setText(tag.getArtist());
        songName.setText(tag.getTitle());
    }

    public void takeCare() throws Exception {
        if(songTable.getSelectionModel().getSelectedItem() != null) {
            currentPlaying = songTable.getSelectionModel().getSelectedItem();
            playPauseSong(currentPlaying);
        }
        else {
            System.out.println("null");
        }
    }

    private void seekAndUpdate(Duration duration) {
        final MediaPlayer player = players.get(players.indexOf(mediaView.getMediaPlayer()));

        player.seek(duration);
    }

    private void updateValues() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            final MediaPlayer player = mediaView.getMediaPlayer();
                            if((player.getStatus() != Status.PAUSED) && (player.getStatus() != Status.STOPPED) && (player.getStatus() != Status.READY)) {
                                
                               
                                
                            }
                        }
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        break;
                    }
                }
                while(!players.isEmpty());
            }
        });
        thread.start();
    }

    private void setPlayButtonHandler() {
        //mediaView.getMediaPlayer().setAutoPlay(true);
        playButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (mediaView.getMediaPlayer().getStatus() != MediaPlayer.Status.PLAYING) {
                    mediaView.getMediaPlayer().play();
                    viewPauseIcon();
                }
            }
        });
    }

    @FXML
    private void playPauseSong() {
    	
        MediaPlayer mp = mediaView.getMediaPlayer();
        if(mp.getStatus() == Status.PLAYING){
            mediaView.getMediaPlayer().pause();
            pauseButton.setVisible(false);
            pauseButton.setDisable(true);
            playButton.setVisible(true);
            playButton.setDisable(false);
        } 
        else {
            mediaView.getMediaPlayer().play();
            playButton.setVisible(false);
            playButton.setDisable(true);
            pauseButton.setVisible(true);
            pauseButton.setDisable(false);
        }
    }

    private void changeVolume(int amount){
        volume += amount;
        volumeValue.setText(Integer.toString(volume));
        if(mediaView != null && mediaView.getMediaPlayer() != null) mediaView.getMediaPlayer().setVolume(((double)volume)/100);
        volumeIconChanger();
    }

    private void volumeIconChanger() {
        if(volume == 0) {
            muteIcon.setVisible(true);
            volumeIcon.setVisible(false);
        }
        else {
            muteIcon.setVisible(false);
            volumeIcon.setVisible(true);
        }
    }

    // ----------------------------------------------------------------------------------------------------------------------
    //                                                 GRAPHICS AND ICONS
    // ----------------------------------------------------------------------------------------------------------------------
    
    //----------------------
    // TOOL TIPS
    //----------------------
    
    private void insertToolTips() {
        //SIGNATURE: Tooltip.install(imageView, tooltip);
    	Tooltip.install(playButton, 		new Tooltip(resources.getString("tt_playbutton")));
        Tooltip.install(pauseButton, 		new Tooltip(resources.getString("tt_pausebutton")));
        Tooltip.install(nextSongButton, 	new Tooltip(resources.getString("tt_nextsong")));
        Tooltip.install(previousSongButton, new Tooltip(resources.getString("tt_previoussong")));
        
        //in alto a dx
        Tooltip.install(minimize, 			new Tooltip(resources.getString("tt_minimize")));
        Tooltip.install(exit, 				new Tooltip(resources.getString("tt_exit")));
        
        //4 bottoni in alto
        Tooltip.install(languageButton, 		new Tooltip(resources.getString("tt_language")));
        Tooltip.install(themeButton, 		new Tooltip(resources.getString("tt_theme")));
        Tooltip.install(helpButton, 		new Tooltip(resources.getString("tt_help")));
        Tooltip.install(interfaceButton, 		new Tooltip(resources.getString("tt_interface")));
        
        //3 bottoni tondi
        Tooltip.install(exitProgramButton, 		new Tooltip(resources.getString("tt_exit")));
        Tooltip.install(removeButton, 		new Tooltip(resources.getString("tt_removesong")));
        Tooltip.install(addButton, 		new Tooltip(resources.getString("tt_addsongs")));
        
        //volume
        Tooltip.install(minusVol, 		new Tooltip(resources.getString("tt_decrvol")));
        Tooltip.install(plusVol, 		new Tooltip(resources.getString("tt_incrvol")));
        Tooltip.install(muteIcon, 		new Tooltip(resources.getString("tt_muted")));
        Tooltip.install(volumeIcon, 		new Tooltip(resources.getString("tt_volumepane")));       
        
    }

    //----------------------
    // ICONS FOR THEMES
    //----------------------

    private void setIcons() {
    	if (util.getCurrentTheme().equals("Light")) {
    		setIconsForLightTheme();
    	}
    	else if (util.getCurrentTheme().equals("Dark")) {
    		setIconsForDarkTheme();	
    	}
    }
    
    private void setIconsForDarkTheme() {
    	
    	exit_icon.setImage(new Image(new File("src/main/resources/images/cancelw.png").toURI().toString()));
        minimize_icon.setImage(new Image(new File("src/main/resources/images/minimizew.png").toURI().toString()));
   
        exitProgramButton.setImage(new Image(new File("src/main/resources/images/simple/offw.png").toURI().toString()));
        removeButton.setImage(new Image(new File("src/main/resources/images/simple/removeSongw.png").toURI().toString()));
		addButton.setImage(new Image(new File("src/main/resources/images/simple/addSongw.png").toURI().toString()));
        
        muteIcon.setImage(new Image(new File("src/main/resources/images/simple/speakermutew.png").toURI().toString()));
        volumeIcon.setImage(new Image(new File("src/main/resources/images/simple/speakerw.png").toURI().toString()));
        previousSongButton.setImage(new Image(new File("src/main/resources/images/simple/back-arrowsw.png").toURI().toString()));
        nextSongButton.setImage(new Image(new File("src/main/resources/images/simple/forward-arrowsw.png").toURI().toString()));
        pauseButton.setImage(new Image(new File("src/main/resources/images/simple/pausew.png").toURI().toString()));
        playButton.setImage(new Image(new File("src/main/resources/images/simple/playw.png").toURI().toString()));
        
        plusVol.setImage(new Image(new File("src/main/resources/images/simple/plusw.png").toURI().toString()));
        minusVol.setImage(new Image(new File("src/main/resources/images/simple/minusw.png").toURI().toString()));
        
        languageButton.setGraphic(new ImageView("file:src/main/resources/images/menubar/languagesw.png"));
        helpButton.setGraphic(new ImageView("file:src/main/resources/images/menubar/tutorialw.png"));
        interfaceButton.setGraphic(new ImageView("file:src/main/resources/images/menubar/interfacew.png"));
        themeButton.setGraphic(new ImageView("file:src/main/resources/images/menubar/themew.png"));
	}

	private void setIconsForLightTheme() {
    	
		exit_icon.setImage(new Image(new File("src/main/resources/images/cancel.png").toURI().toString()));
		minimize_icon.setImage(new Image(new File("src/main/resources/images/minimize.png").toURI().toString()));

		exitProgramButton.setImage(new Image(new File("src/main/resources/images/simple/off.png").toURI().toString()));
		removeButton.setImage(new Image(new File("src/main/resources/images/simple/removeSong.png").toURI().toString()));
		addButton.setImage(new Image(new File("src/main/resources/images/simple/addSong.png").toURI().toString()));
		
		muteIcon.setImage(new Image(new File("src/main/resources/images/simple/speakermute.png").toURI().toString()));
        volumeIcon.setImage(new Image(new File("src/main/resources/images/simple/speaker.png").toURI().toString()));
        previousSongButton.setImage(new Image(new File("src/main/resources/images/simple/back-arrows.png").toURI().toString()));
        nextSongButton.setImage(new Image(new File("src/main/resources/images/simple/forward-arrows.png").toURI().toString()));
        pauseButton.setImage(new Image(new File("src/main/resources/images/simple/pause.png").toURI().toString()));
        playButton.setImage(new Image(new File("src/main/resources/images/simple/play.png").toURI().toString()));
		
        plusVol.setImage(new Image(new File("src/main/resources/images/simple/plus.png").toURI().toString()));
        minusVol.setImage(new Image(new File("src/main/resources/images/simple/minus.png").toURI().toString()));
        
        languageButton.setGraphic(new ImageView("file:src/main/resources/images/menubar/languages.png"));
        helpButton.setGraphic(new ImageView("file:src/main/resources/images/menubar/tutorial.png"));
        interfaceButton.setGraphic(new ImageView("file:src/main/resources/images/menubar/interface.png"));
        themeButton.setGraphic(new ImageView("file:src/main/resources/images/menubar/theme.png"));
	}
    
    
    

    
    
}