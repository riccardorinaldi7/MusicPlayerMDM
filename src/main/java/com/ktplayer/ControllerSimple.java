package com.ktplayer;

import com.jfoenix.controls.JFXSlider;
import com.mpatric.mp3agic.*;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCodeCombination;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

public class ControllerSimple {
    
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
    private Boolean currentlyMuted = false;
    private Double volumeBeforeMute = 0.00;
    private Utilities util;
    
    //---------------------------------------------------------------------------------------------------------
    
    private Main main;

    private List<MediaPlayer> players;
    private MediaPlayer mediaPlayer;
    private MediaView mediaView;

    private boolean isAutoplay;
    private double volume;
    private String path;

    private double xOffset = 0;
    private double yOffset = 0;

    private FadeTransition fadeIn = new FadeTransition();
    private FadeTransition fadeOut = new FadeTransition();
    private Song currentSelection;
    private Song currentPlaying;

    //---------------------------------------------------------------------------------------------------------
    // Builder
    //---------------------------------------------------------------------------------------------------------
   
    public ControllerSimple() {
        players = new ArrayList<>();
        
        isAutoplay = false;
        volume = 0.5; // between 0 and 1
        stage = Main.getStage();
        stage.getIcons().add(new Image(ClassLoader.getSystemResource("images/logo.png").toExternalForm()));
        util = new Utilities();
        currentTheme = util.getCurrentTheme();
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
        showSongInfo(null);

        //this runs everytime an item in tableview is single-clicked/selected
        songTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("SongTable: selection detected - " + newValue.getSongName());
            currentSelection = newValue;
        });
        
        addButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	chooseFolder();
            }
        });
        
        exitProgramButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	closeProgram();
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

    @FXML
    private void chooseFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        File selectedDirectory = chooser.showDialog(stage);
        if(selectedDirectory == null) {
            System.out.println("No directory selected!");
        }
        else {

            try {
                if(!(players.isEmpty())) {
                    players.clear();
                    System.out.println("new array list");
                }
                songTable.setItems(loadSongs(selectedDirectory));

                songTable.setOnMouseClicked((MouseEvent e) -> {
                	
                    //con doppio click, parte la canzone!
                    if (e.getClickCount() == 2) {
                        try {
                            takeCare();
                            if(currentPlaying != null) showSongInfo(currentPlaying);
                            System.out.println("SongTable: play - " + currentPlaying.getSongName());
                            //playPauseSong(); it is better to play inside takeCare() -> playPauseSong(song)
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                });
            }
            catch(Exception e) {}
        }
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
        ChoiceDialog<String> choiceDialog = new ChoiceDialog<String>();
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
        list.remove(language); //rimuovo lingua corrente affinchÃ© non sia selezionabile
        
        choiceDialog.initStyle(StageStyle.UNDECORATED); //toglie completamente la barra del titolo
        util.applyThemeToDialog(choiceDialog);
        
        // Show the dialog box and wait for a selection
        Optional<String> selectedLanguage = choiceDialog.showAndWait();

        try {
            language = selectedLanguage.get();
            appProps.setProperty("language", language);

            appProps.store(new FileWriter(appConfigPath), null);

            //alert per dire che bisogna riavviare il programma
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle(resources.getString("attention"));
            alert.setHeaderText(null);
            alert.setContentText(resources.getString("restart"));
            ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(
            		new Image("file:src/main/resources/images/logo.png"));
            alert.setGraphic(new ImageView(new Image("file:src/main/resources/images/refresh.png")));
            alert.initStyle(StageStyle.UNDECORATED); //toglie completamente la barra del titolo
            util.applyThemeToDialog(alert);
            alert.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchElementException e){
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
	
	     
	     String theme = appProps.getProperty("theme");
	
	     ChoiceDialog<String> choiceDialog = new ChoiceDialog<String>();
	     choiceDialog.setHeaderText(resources.getString("selecttheme"));
	     choiceDialog.setTitle(resources.getString("theme"));
	     ((Stage)choiceDialog.getDialogPane().getScene().getWindow()).getIcons().add(
	    		 new Image("file:src/main/resources/images/logo.png"));
	     Image img = new Image("file:src/main/resources/images/theme.png");
	     choiceDialog.setGraphic(new ImageView(img));
	
	     //Retrieving the observable list
	     ObservableList<String> list = choiceDialog.getItems();
	     list.add(resources.getString("dark"));
	     list.add(resources.getString("light"));
	     list.remove(theme); //rimuovo tema corrente affinche' non sia selezionabile
	     
	     choiceDialog.initStyle(StageStyle.UNDECORATED); //toglie completamente la barra del titolo
	     
	     util.applyThemeToDialog(choiceDialog);
	     
	     
	     // Show the dialog box and wait for a selection
	     Optional<String> selectedTheme = choiceDialog.showAndWait();
	
	     try {
	    	 theme = selectedTheme.get();
	         appProps.setProperty("theme", theme);
	         appProps.store(new FileWriter(appConfigPath), null);
	
	         //alert per dire che bisogna riavviare il programma
	         Alert alert = new Alert(AlertType.WARNING);
	         alert.setTitle(resources.getString("attention"));
	         alert.setHeaderText(null);
	         alert.setContentText(resources.getString("restart"));
	         ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(
	        		 new Image("file:src/main/resources/images/logo.png"));
	         alert.setGraphic(new ImageView(new Image("file:src/main/resources/images/refresh.png")));
	         alert.initStyle(StageStyle.UNDECORATED); //toglie completamente la barra del titolo
	         util.applyThemeToDialog(alert);
	         
	         alert.showAndWait();
	     } 
	     catch (IOException e) {
	         e.printStackTrace();
	     } 
	     catch (NoSuchElementException e){
	         System.out.println("User has changed his/her mind");
	     }
    }
    
    @FXML
    private void interfaceClicked() {
    	System.out.println("Switch interface...");
    }
    
    @FXML
    private void helpClicked() {
    	System.out.println("See the tutorial...");
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

    // Loads all files in a directory and creates Song and MediaPlayer for each file
    public ObservableList<Song> loadSongs(File dir)   throws Exception{
        ObservableList<Song> songData = FXCollections.observableArrayList();
        File[] files = dir.listFiles();
        String name;
        int i = 0;
        for(File file : files) {
            if(file.isFile()) {
                name = file.getName();
                if(name.endsWith("mp3") || name.endsWith("wav")) {
                    try {
                        i++; //song id from 1 to n
                        Mp3File mp3 = new Mp3File(file.getPath());
                        ID3v2 tag = mp3.getId3v2Tag();
                        String title = tag.getTitle() == null ? name : tag.getTitle(); //use filename if no song title exists
                        Song song = new Song(String.valueOf(i), tag.getArtist(), title, kbToMb(file.length()), secToMin(mp3.getLengthInSeconds()),tag.getAlbum(), file.getAbsolutePath(), tag.getAlbumImage());
                        players.add(createMediaPlayer(file.getAbsolutePath()));
                        songData.add(song);
                    }
                    catch(IOException e) {e.printStackTrace();}
                }
            }
        }
        setImage();
        i = 0;
        System.out.println(players.size());
        
        return  songData;
    }

    public void playPauseSong(Song song) throws Exception{
        if(song != null) {
        	
            File file = new File(song.getUrl());
            String path = file.getAbsolutePath();
            path.replace("\\", "/");

            // If there's another media set, we remove its mediaview and mediaplayer before setting the new ones
            if((mediaView != null) && (mediaPlayer != null)) {
            	
            	 System.out.println("playPauseSong(Song): deleting old mediaview and mediaplayer...");
            	
                volume = mediaView.getMediaPlayer().getVolume(); //?? why ??
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
           
            mediaView.getMediaPlayer().setVolume(volume);
            mediaView.getMediaPlayer().seek(Duration.ZERO);
            
            double duration = mediaView.getMediaPlayer().getTotalDuration().toSeconds();
            

            // create a Thread that runs throughout the song and update values of slider, timing and volume
            updateValues();

            mediaView.mediaPlayerProperty().addListener(new ChangeListener<MediaPlayer>() {
                @Override
                public void changed(ObservableValue<? extends MediaPlayer> observable, MediaPlayer oldValue, MediaPlayer newValue) {
                    try {
                        setCurrentlyPlayer(newValue);
                        updateValues();
                    }
                    catch(IOException e) {}
                    catch(UnsupportedTagException e) {}
                    catch(InvalidDataException e) {}
                }
            });

            /*playButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    mediaView.getMediaPlayer().play();
                    viewPauseIcon();
                    updateValues();
                    for (int i = ((players.indexOf(mediaView.getMediaPlayer())) % players.size()); i < players.size(); i++) {
                        final MediaPlayer player = players.get(i);
                        mediaPlayer = player;
                        final MediaPlayer nextPlayer = players.get((i + 1) % players.size());
                        mediaPlayer.setOnEndOfMedia(new Runnable() {
                            @Override
                            public void run() {
                                mediaView.getMediaPlayer().stop();
                                mediaView.getMediaPlayer().seek(Duration.ZERO);
                                if(isAutoplay) {
                                    mediaView.getMediaPlayer().seek(Duration.ZERO);
                                    repeatSongs();
                                    return;
                                }
                                mediaPlayer = nextPlayer;
                                mediaView.setMediaPlayer(mediaPlayer);
                                mediaView.getMediaPlayer().seek(Duration.ZERO);
                                updateSliderPosition(Duration.ZERO);
                                songSlider.setValue(0);
                                updateValues();
                                mediaPlayer.setVolume(volume);
                                mediaPlayer.play();
                                viewPauseIcon();
                            }
                        });
                        setPlayButtonHandler();
                    }
                }
            });*/

            pauseButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    mediaView.getMediaPlayer().pause();
                    viewPlayIcon();
                    setPlayButtonHandler();
                    updateValues();
                }
            });

            nextSongButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    seekAndUpdate(players.get(players.indexOf(mediaView.getMediaPlayer())).getTotalDuration());
                }
            });

            previousSongButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    seekAndUpdate(Duration.ZERO);
                }
            });

            

            // TODO: reset all players everytime we takeCare of a Song?? Too expensive...should be fixed
            for (int i = ((players.indexOf(mediaView.getMediaPlayer())) % players.size()); i < players.size(); i++) {
                final MediaPlayer player = players.get(i);
                mediaPlayer = player;
                final MediaPlayer nextPlayer = players.get((i + 1) % players.size());
                mediaPlayer.setOnEndOfMedia(new Runnable() {
                    @Override
                    public void run() {
                        mediaView.getMediaPlayer().stop();
                        mediaView.getMediaPlayer().seek(Duration.ZERO);
                        if(isAutoplay) {
                            mediaView.getMediaPlayer().seek(Duration.ZERO);
                            repeatSongs();
                            return;
                        }
                        mediaPlayer = nextPlayer;
                        mediaView.setMediaPlayer(mediaPlayer);
                        mediaView.getMediaPlayer().seek(Duration.ZERO);
                        
                       
                        double duration = mediaView.getMediaPlayer().getTotalDuration().toSeconds();
                        
                        updateValues();
                        mediaPlayer.setVolume(volume);
                        mediaPlayer.play();
                        viewPauseIcon();
                    }
                });
            }

            playPauseSong();

        }
        else {
        	 System.out.println("Come posso essere qui? PARADOSSOOOOO");
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

    public void setMain(Main main) {
        this.main = main;
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

    private void setImage() throws Exception {
        String path = "";
        path = path.replace("\\", "/");
        path = path.replace(" ", "%20");
        //path = "file:/" + path;
        path = ClassLoader.getSystemResource("images/Question.PNG").toExternalForm();
        System.out.println(path);
    }

    private void repeatSongs(){
        mediaView.getMediaPlayer().setOnRepeat(new Runnable() {
            @Override
            public void run() {
                mediaView.getMediaPlayer().seek(Duration.ZERO);
            }
        });
        if(isAutoplay) {
            mediaView.getMediaPlayer().play();
            viewPauseIcon();
        }
        else return;
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
        Tooltip.install(minimize, 		new Tooltip(resources.getString("tt_minimize")));
        Tooltip.install(exit, 		new Tooltip(resources.getString("tt_exit")));
    }

    /*//---------------------------------------------------------------------------------------------------------
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
     */
    
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
        
        muteIcon.setImage(new Image(new File("src/main/resources/images/speakermutew.png").toURI().toString()));
        volumeIcon.setImage(new Image(new File("src/main/resources/images/simple/speakerw.png").toURI().toString()));
        previousSongButton.setImage(new Image(new File("src/main/resources/images/simple/back-arrowsw.png").toURI().toString()));
        nextSongButton.setImage(new Image(new File("src/main/resources/images/simple/forward-arrowsw.png").toURI().toString()));
        pauseButton.setImage(new Image(new File("src/main/resources/images/simple/pausew.png").toURI().toString()));
        playButton.setImage(new Image(new File("src/main/resources/images/simple/playw.png").toURI().toString()));
        
        plusVol.setImage(new Image(new File("src/main/resources/images/simple/minusw.png").toURI().toString()));
        minusVol.setImage(new Image(new File("src/main/resources/images/simple/plusw.png").toURI().toString()));
        
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
		
		muteIcon.setImage(new Image(new File("src/main/resources/images/speakermute.png").toURI().toString()));
        volumeIcon.setImage(new Image(new File("src/main/resources/images/simple/speaker.png").toURI().toString()));
        previousSongButton.setImage(new Image(new File("src/main/resources/images/simple/back-arrows.png").toURI().toString()));
        nextSongButton.setImage(new Image(new File("src/main/resources/images/simple/forward-arrows.png").toURI().toString()));
        pauseButton.setImage(new Image(new File("src/main/resources/images/simple/pause.png").toURI().toString()));
        playButton.setImage(new Image(new File("src/main/resources/images/simple/play.png").toURI().toString()));
		
        plusVol.setImage(new Image(new File("src/main/resources/images/simple/minus.png").toURI().toString()));
        minusVol.setImage(new Image(new File("src/main/resources/images/simple/plus.png").toURI().toString()));
        
        languageButton.setGraphic(new ImageView("file:src/main/resources/images/menubar/languages.png"));
        helpButton.setGraphic(new ImageView("file:src/main/resources/images/menubar/tutorial.png"));
        interfaceButton.setGraphic(new ImageView("file:src/main/resources/images/menubar/interface.png"));
        themeButton.setGraphic(new ImageView("file:src/main/resources/images/menubar/theme.png"));
	}
    
    
    

    
    
}