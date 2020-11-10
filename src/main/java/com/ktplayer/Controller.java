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


public class Controller {

    private void print(String str) { System.out.println(str);}
    private String currentTheme;
    
    @FXML
    private AnchorPane window;

    @FXML
    private AnchorPane playlistNode;

    @FXML
    private Pane showPlaylist;
    @FXML
    private Pane exit;
    @FXML
    private Pane minimize;
    @FXML
    private Pane imagePane;

    @FXML
    private TableView<Song> songTable;
    @FXML
    private TableColumn<Song, String> idColumn;
    @FXML
    private TableColumn<Song, String> artistNameColumn;
    @FXML
    private TableColumn<Song, String> songNameColumn;
    @FXML
    private TableColumn<Song, String> durationColumn;
    @FXML
    private TableColumn<Song, String> rateColumn;
    @FXML
    private TableColumn<Song, String> formatColumn;

    @FXML
    private Label artistName;
    @FXML
    private Label albumName;
    @FXML
    private Label songName;
    @FXML
    private Label totalDuration;
    @FXML
    private Label currentDuration;
    @FXML
    private Label volumeValue;
    @FXML
    private Label songsCounter;

    @FXML
    private JFXSlider songSlider;
    @FXML
    private Slider volumeSlider;

    @FXML
    private ImageView folderChooser;

    @FXML
    private ImageView playButton;
    @FXML
    private ImageView pauseButton;
    @FXML
    private ImageView nextSongButton;
    @FXML
    private ImageView previousSongButton;
    @FXML
    private ImageView muteIcon;
    @FXML
    private ImageView volumeIcon;
    @FXML
    private ToggleButton autoPlayIcon;

    @FXML
    private ResourceBundle resources ;

    @FXML
    private Stage stage;

    // -------------------------------------------------------------------------
    // AGGIUNTI DA NOI
    // --------------------------------------------------------------------------

    private Boolean currentlyMuted = false;
    private Double volumeBeforeMute = 0.00;
    private Utilities util;
    
    @FXML
    private SplitPane splitpane;
    @FXML
    private Menu settings_menu;
    @FXML
    private Menu audio_menu;
    @FXML
    private Pane volumePane;
    @FXML
    private MenuItem openfile_menu; 
    @FXML
    private MenuItem openfolder_menu; 
    @FXML
    private MenuItem close_menu; 
    @FXML
    private MenuItem exit_menu; 
    @FXML
    private MenuItem removefiles_menu;
    @FXML
    private Pane maximize;
    
    @FXML
    private MenuItem playpause_menu;
    @FXML
    private MenuItem next_menu;
    @FXML
    private MenuItem previous_menu;

    /* 
    @FXML
    private MenuItem fullscreen_menu;
    */ 
    
    @FXML
    private MenuItem minimize_menu;
    @FXML
    private MenuItem language_menu;
    @FXML
    private MenuItem about_menu;
    @FXML
    private MenuItem preview_menu;
    @FXML
    private MenuItem shortcuts_menu;
    @FXML
    private MenuItem theme_menu;
    @FXML
    private Menu menuVolume;
    @FXML
    private MenuItem decrVol;
    @FXML
    private MenuItem incrVol;
    @FXML
    private MenuItem muteVol;
    @FXML
    private Menu menuInterface;
    @FXML
    private MenuItem simpleInterface;
    @FXML
    private MenuItem advancedInterface;
    @FXML
    private MenuItem hidebar_menu;
    @FXML
    private ImageView exit_icon;
    @FXML
    private ImageView minimize_icon;
    
    // --------------------------------------------------------------------------
    
    
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

    public Controller() {
        players = new ArrayList<>();
        songSlider = new JFXSlider();
        isAutoplay = false;
        volume = 0.5; // between 0 and 1
        stage = Main.getStage();
        stage.getIcons().add(new Image(ClassLoader.getSystemResource("images/logo.png").toExternalForm()));
        util = new Utilities();
        currentTheme = util.getCurrentTheme();
    }

    private void closeProgram(){
        //aggiunta alert di conferma prima di chiudere l'applicazione
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setContentText(resources.getString("sureToClose"));
        alert.setHeaderText(null);
        alert.initStyle(StageStyle.UNDECORATED); //toglie completamente la barra del titolo
        alert.setTitle(resources.getString("confirmExit"));
        ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("file:src/main/resources/images/logo.png"));
        
        util.applyThemeToDialog(alert);
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            System.exit(0);
        } else { }
    }

    @FXML
    private void initialize() throws Exception {

        //--------------------------------------------------------------------------------------
        // AGGIUNTI DA NOI
        
        insertSubMenus_menuBar();	//enrich the menubar with submenus
        setIcons();					//set icons depending on the theme
        insertToolTips();			//attach tooltip to the main buttons
        volumeIconChanger(); 		//update volume icon if volume == 0
        addShortcutsMenubar();	
        attachMenuActions();		//add setOnAction to menuItems
        
        
        // Shortcuts handler
        // Add any shortcut you want here
        /*window.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode().getName() == "Space"){
                    try {
                        playPauseSong();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        */
       
        //--------------------------------------------------------------------------------------
        
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

        volumeSlider.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                volumeIconChanger();
            }
        });

        autoPlayIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(isAutoplay) {
                    autoPlayIcon.setSelected(false);
                    isAutoplay = false;
                }
                else if(!isAutoplay) {
                    autoPlayIcon.setSelected(true);
                    isAutoplay = true;
                }
            }
        });

        showPlaylist.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(playlistNode.isVisible() == true) {
                    hideTransation(playlistNode);
                }
                else {
                    showTransation(playlistNode);
                }
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
                closeProgram(); //creato "handler" da riutilizzare
            }
        });

        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        artistNameColumn.setCellValueFactory(cellData -> cellData.getValue().artistNameProperty());
        songNameColumn.setCellValueFactory(cellData -> cellData.getValue().songNameProperty());
        durationColumn.setCellValueFactory(cellData -> cellData.getValue().durationProperty());
        rateColumn.setCellValueFactory(cellData -> cellData.getValue().rateProperty());
        formatColumn.setCellValueFactory(cellData -> cellData.getValue().formatProperty());

        showSongInfo(null);

        //this runs everytime an item in tableview is single-clicked/selected
        songTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("SongTable: selection detected - " + newValue.getSongName());
            currentSelection = newValue;
        });

        folderChooser.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("folderChooser: choose a music folder");
                chooseFolder();
            }
        });
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
                                    	
//                     if(e.getClickCount() == 1) {
//                    	     try {
//		                    	 print("SongTable: prendo in carico...");
//                    	         takeCare();
//		                     }
//		                     catch (Exception ex) {}
//                    	 }
                	
                    //con doppio click, parte la canzone!
                    if (e.getClickCount() == 2) {
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
            }
            catch(Exception e) {}
        }
    }

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
        albumName.setText(albumLabel);
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
                        Song song = new Song(String.valueOf(i), tag.getArtist(), title, kbToMb(file.length()), secToMin(mp3.getLengthInSeconds()),tag.getAlbum(), file.getAbsolutePath());
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
        songsCounter.setText("");
        songsCounter.setText("Songs: " + players.size());
        return  songData;
    }

    public void playPauseSong(Song song) throws Exception{
        if(song != null) {
        	
            File file = new File(song.getUrl());
            String path = file.getAbsolutePath();
            path.replace("\\", "/");

            // If there's another media set, we remove its mediaview and mediaplayer before setting the new ones
            if((mediaView != null) && (mediaPlayer != null)) {
            	
            	print("playPauseSong(Song): deleting old mediaview and mediaplayer...");
            	
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
            volumeValue.setText(String.valueOf((int)volumeSlider.getValue()));
            volumeSlider.setValue(volume*100);
            mediaView.getMediaPlayer().setVolume(volume);
            mediaView.getMediaPlayer().seek(Duration.ZERO);
            updateSliderPosition(Duration.ZERO);
            double duration = mediaView.getMediaPlayer().getTotalDuration().toSeconds();
            totalDuration.setText(secToMin((long) duration));

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

            songSlider.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    Bounds b1 = songSlider.getLayoutBounds();
                    double mouseX = event.getX();
                    double percent = (((b1.getMinX() + mouseX) * 100) / (b1.getMaxX() - b1.getMinX()));
                    songSlider.setValue((percent) / 100);
                    seekAndUpdate(new Duration(mediaView.getMediaPlayer().getTotalDuration().toMillis() * percent / 100));
                    songSlider.setValueFactory(slider ->
                            Bindings.createStringBinding(
                                    () -> (secToMin((long) mediaView.getMediaPlayer().getCurrentTime().toSeconds())),
                                    songSlider.valueProperty()
                            )
                    );
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
                        updateSliderPosition(Duration.ZERO);
                        songSlider.setValue(0);
                        double duration = mediaView.getMediaPlayer().getTotalDuration().toSeconds();
                        totalDuration.setText(secToMin((long) duration));
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
        albumName.setText(tag.getAlbum());
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
                                currentDuration.setText(secToMin((long) player.getCurrentTime().toSeconds()));
                                updateSliderPosition(player.getCurrentTime());
                                volumeHandler();
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

    private void updateSliderPosition(Duration currentTime) {
        final MediaPlayer player = mediaView.getMediaPlayer();
        final Duration totalDuration = player.getTotalDuration();
        if((totalDuration == null) || (currentTime == null)) {
            songSlider.setValue(0);
        }
        else {
            songSlider.setValue((currentTime.toMillis() / totalDuration.toMillis()) * 100);
        }
    }

    private void volumeHandler() {
        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mediaView.getMediaPlayer().setVolume(volumeSlider.getValue() /100);
                volumeValue.setText(String.valueOf((int)volumeSlider.getValue()));
                volume = mediaView.getMediaPlayer().getVolume();
                volumeIconChanger();
            }
        });
    }

    private void volumeIconChanger() {
        if(volumeSlider.getValue() == 0) {
            muteIcon.setVisible(true);
            volumeIcon.setVisible(false);
        }
        else {
            muteIcon.setVisible(false);
            volumeIcon.setVisible(true);
        }
    }

    private void showTransation(AnchorPane anchorPane) {
        fadeIn.setNode(anchorPane);
        fadeIn.setDuration(Duration.millis(1000));
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        anchorPane.setVisible(true);
        fadeIn.play();
    }

    private void hideTransation(AnchorPane anchorPane) {
        fadeOut.setNode(anchorPane);
        fadeOut.setDuration(Duration.millis(1000));
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        anchorPane.setVisible(false);
        fadeOut.play();
    }

    private void setImage() throws Exception {
        String path = "";
        path = path.replace("\\", "/");
        path = path.replace(" ", "%20");
        //path = "file:/" + path;
        path = ClassLoader.getSystemResource("images/Question.PNG").toExternalForm();
        System.out.println(path);

        /*imagePane.setStyle("-fx-background-image: url(\"" + path + "\"); " +
                "-fx-background-position: center center; " +
                "-fx-background-repeat: stretch;");*/

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

    // ***************************************************************************************************************
    // NOSTRE MODIFICHE
    // ***************************************************************************************************************
    
    //Quando premo play dal menubar --> implementare con le nuove modifiche
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

    @FXML
    //se spingi ctrl+w ti esce questo
    private void closeFolder(){
        System.out.println("I'm closing the music folder");
        players.clear();
        songTable.getItems().clear();
        mediaView = null;
    }

    private void changeVolume(Integer amount){
        volumeSlider.setValue(volumeSlider.getValue() + amount);
        volumeValue.setText(String.valueOf((int)volumeSlider.getValue()));
        if(mediaView != null && mediaView.getMediaPlayer() != null) mediaView.getMediaPlayer().setVolume(volumeSlider.getValue() / 100);
        volumeIconChanger(); 
    }

    // ----------------------------------------------------------------------------------------------------------------------
    // THEME SELECTION
    // ----------------------------------------------------------------------------------------------------------------------
    
    @FXML
    private void themeSelection (ActionEvent event){
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
         ChoiceDialog<String> choiceDialog = new ChoiceDialog<String>();
         choiceDialog.setHeaderText(resources.getString("selecttheme"));
         choiceDialog.setTitle(resources.getString("theme"));
         ((Stage)choiceDialog.getDialogPane().getScene().getWindow()).getIcons().add(
        		 new Image("file:src/main/resources/images/logo.png"));
         Image img = new Image("file:src/main/resources/images/theme.png");
         choiceDialog.setGraphic(new ImageView(img));

         //Retrieving the observable list
         ObservableList<String> list = choiceDialog.getItems();
         //Adding items to the language list
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
    
    // ----------------------------------------------------------------------------------------------------------------------
    // LANGUAGE SELECTION
    // ----------------------------------------------------------------------------------------------------------------------
    
    @FXML
    private void languageSelection(ActionEvent event){
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
            
            alert.initStyle(StageStyle.UNDECORATED); //toglie completamente la barra del titolo
            util.applyThemeToDialog(alert);
            alert.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchElementException e){
            System.out.println("User has changed his/her mind");
        }
    }	
    
	// ----------------------------------------------------------------------------------------------------------------------
	// HANDLERS FOR MENU ITEMS
	// ----------------------------------------------------------------------------------------------------------------------
	
    private void attachMenuActions(){
    	
    	incrVol.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				changeVolume(10);
			}
		});
   
		decrVol.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				changeVolume(-10);
			}
		});
   
		muteVol.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				muteUnmuteVolume();
			}
		});
		
		exit_menu.setOnAction( new EventHandler<ActionEvent>() {
    	    public void handle(ActionEvent t) {
    	    	closeProgram();
    	    }
    	});          
        
        //Play/Pausa
		playpause_menu.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent t) {
				playPauseSong();
			}
		});

		minimize_menu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
	        public void handle(ActionEvent e) {
	            stage.setIconified(true);
	        }
		});
		
		//Click sul MenuItem "About"
		about_menu.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
    	
		    	String toShow = "WG Player has been developed for the Work Project on Multimedia Data Management M,"
		    			+ " a course of Computer Science Engineering at the University of Bologna held by Prof. Ilaria Bartolini.\n"
		    			+ "\nThe students Riccardo Rinaldi and Laura Gruppioni started using a very simple Music Player software created by Alexey Ktualhu" 
		    			+ " with the aim of improving the User Interface by applying some of the most famous Design Principles written by Ben Shneiderman and his research partners.\n\n"
		    			+ " _________________________________________________ \n\n"
		    			+ "Here you can find the original software:\nhttps://github.com/ktualhu/ktPlayer-Music-Player\n"
		    			+ "\nIf you want to join our team, here you can take a look at our highly evolved project:\nhttps://github.com/riccardorinaldi7/MusicPlayerMDM";
            	
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle(resources.getString("about"));
				alert.setHeaderText(null);
				alert.setResizable(false);
				alert.getDialogPane().setMinWidth(850);
				alert.getDialogPane().setMinHeight(400);
				alert.setContentText(toShow);
				((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("file:src/main/resources/images/logo.png"));
				alert.setGraphic(new ImageView(new Image("file:src/main/resources/images/almamater.png")));
					
				alert.initStyle(StageStyle.UNDECORATED); //toglie completamente la barra del titolo
				util.applyThemeToDialog(alert); 
				
				alert.showAndWait();
			}
		});
    }
    
    private void muteUnmuteVolume(){
    	if (currentlyMuted == false ) {
    		currentlyMuted = true;
    		volumeBeforeMute = volumeSlider.getValue();
    		volumeSlider.setValue(0);
    		volumeValue.setText(String.valueOf((int)volumeSlider.getValue()));
    		if(mediaView != null && mediaView.getMediaPlayer() != null) mediaView.getMediaPlayer().setVolume(volumeSlider.getValue() / 100);
    	}
    	else {
    		//restore the volume of the slider before the mute action
    		currentlyMuted = false;
    		volumeSlider.setValue(volumeBeforeMute);
    		volumeBeforeMute = 0.00;
    		volumeValue.setText(String.valueOf((int)volumeSlider.getValue()));
    		if(mediaView != null && mediaView.getMediaPlayer() != null) mediaView.getMediaPlayer().setVolume(volumeSlider.getValue() / 100);
    	}
    	volumeIconChanger(); 
    }
    
    // ----------------------------------------------------------------------------------------------------------------------
    // METHODS FOR GRAPHICS AND ICONS
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
        Tooltip.install(volumePane, 		new Tooltip(resources.getString("tt_volumepane")));
        Tooltip.install(showPlaylist, 		new Tooltip(resources.getString("tt_playlist")));
        Tooltip.install(folderChooser, 		new Tooltip(resources.getString("tt_folder")));
        
        //in alto a dx
        Tooltip.install(minimize, 		new Tooltip(resources.getString("tt_minimize")));
        Tooltip.install(maximize, 		new Tooltip(resources.getString("tt_maximize")));
        Tooltip.install(exit, 		new Tooltip(resources.getString("tt_exit")));
    }

    //----------------------
    // SUB MENU
    //----------------------

    private void insertSubMenus_menuBar() {
        menuInterface = new Menu();
        menuInterface.setText(resources.getString("interface"));
        settings_menu.getItems().add(menuInterface);
        simpleInterface = new MenuItem();
        simpleInterface.setText(resources.getString("simple"));
        advancedInterface = new MenuItem();
        advancedInterface.setText(resources.getString("advanced"));
        menuInterface.getItems().addAll(simpleInterface, advancedInterface);

        menuVolume = new Menu();
        menuVolume.setText(resources.getString("volume"));
        audio_menu.getItems().add(menuVolume);
        decrVol = new MenuItem();
        decrVol.setText(resources.getString("decreasevolume"));
        incrVol = new MenuItem();
        incrVol.setText(resources.getString("increasevolume"));
        muteVol = new MenuItem();
        muteVol.setText(resources.getString("novolume"));
        menuVolume.getItems().addAll(decrVol, incrVol, muteVol);
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
    	
    	//Icons for the main window
    	
    	exit_icon.setImage(new Image(new File("src/main/resources/images/cancelw.png").toURI().toString()));
        minimize_icon.setImage(new Image(new File("src/main/resources/images/minimizew.png").toURI().toString()));
        folderChooser.setImage(new Image(new File("src/main/resources/images/music-folderw.png").toURI().toString()));
        
        muteIcon.setImage(new Image(new File("src/main/resources/images/speakermutew.png").toURI().toString()));
        volumeIcon.setImage(new Image(new File("src/main/resources/images/speakerw.png").toURI().toString()));
        previousSongButton.setImage(new Image(new File("src/main/resources/images/back-arrowsw.png").toURI().toString()));
        nextSongButton.setImage(new Image(new File("src/main/resources/images/forward-arrowsw.png").toURI().toString()));
        pauseButton.setImage(new Image(new File("src/main/resources/images/pausew.png").toURI().toString()));
        playButton.setImage(new Image(new File("src/main/resources/images/playw.png").toURI().toString()));
        
        //Icons for the menu bar
        
        //File menu
    	openfile_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/openw.png"));
    	openfolder_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/openw.png"));
    	exit_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/closew.png"));
    	close_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/closefolderw.png"));
    	removefiles_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/removew.png"));
    	
    	//Playback menu
    	playpause_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/playpausew.png"));
    	next_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/forwardw.png"));
    	previous_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/rewindw.png"));
    	menuVolume.setGraphic(new ImageView("file:src/main/resources/images/menubar/volumew.png"));
    	
    	decrVol.setGraphic(new ImageView("file:src/main/resources/images/menubar/minusw.png"));
    	incrVol.setGraphic(new ImageView("file:src/main/resources/images/menubar/plusw.png"));
    	muteVol.setGraphic(new ImageView("file:src/main/resources/images/menubar/speakermutew.png"));
    	
    	//View menu
    	//fullscreen_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/fullscreen.png"));
    	minimize_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/minimizew.png"));
    	theme_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/themew.png"));
    	hidebar_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/menubarw.png"));
    	
    	//Settings menu
    	menuInterface.setGraphic(new ImageView("file:src/main/resources/images/menubar/interfacew.png"));
    	language_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/languagesw.png"));
    	
    	//Help menu
    	about_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/logow.png"));
    	preview_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/tutorialw.png"));
    	shortcuts_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/tipsw.png"));
		
	}

	private void setIconsForLightTheme() {
    	
		//Icons for the main window
		
		exit_icon.setImage(new Image(new File("src/main/resources/images/cancel.png").toURI().toString()));
		minimize_icon.setImage(new Image(new File("src/main/resources/images/minimize.png").toURI().toString()));
		folderChooser.setImage(new Image(new File("src/main/resources/images/music-folder.png").toURI().toString()));
		
		muteIcon.setImage(new Image(new File("src/main/resources/images/speakermute.png").toURI().toString()));
        volumeIcon.setImage(new Image(new File("src/main/resources/images/speaker.png").toURI().toString()));
        previousSongButton.setImage(new Image(new File("src/main/resources/images/back-arrows.png").toURI().toString()));
        nextSongButton.setImage(new Image(new File("src/main/resources/images/forward-arrows.png").toURI().toString()));
        pauseButton.setImage(new Image(new File("src/main/resources/images/pause.png").toURI().toString()));
        playButton.setImage(new Image(new File("src/main/resources/images/play.png").toURI().toString()));
		
        //Icons for the menu bar
        
        //File menu
		openfile_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/open.png"));
    	openfolder_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/open.png"));
    	exit_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/close.png"));
    	close_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/closefolder.png"));
    	removefiles_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/remove.png"));
    	
    	//Playback menu
    	playpause_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/playpause.png"));
    	next_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/forward.png"));
    	previous_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/rewind.png"));
    	menuVolume.setGraphic(new ImageView("file:src/main/resources/images/menubar/volume.png"));
    	
    	decrVol.setGraphic(new ImageView("file:src/main/resources/images/menubar/minus.png"));
    	incrVol.setGraphic(new ImageView("file:src/main/resources/images/menubar/plus.png"));
    	muteVol.setGraphic(new ImageView("file:src/main/resources/images/menubar/speakermute.png"));
    	
    	//View menu
    	//fullscreen_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/fullscreen.png"));
    	minimize_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/minimize.png"));
    	theme_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/theme.png"));
    	hidebar_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/menubar.png"));
    	
    	//Settings menu
    	language_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/languages.png"));
    	menuInterface.setGraphic(new ImageView("file:src/main/resources/images/menubar/interface.png"));
    	
    	//Help menu
    	about_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/logo.png"));
    	preview_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/tutorial.png"));
    	shortcuts_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/tips.png"));
		
	}
    
    // ----------------------------------------------------------------------------------------------------------------------
    // METHOD FOR SHORTCUTS
    // ----------------------------------------------------------------------------------------------------------------------
    
    private void addShortcutsMenubar() {
    	previous_menu.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
    	next_menu.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));
    	playpause_menu.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN));
    	
    	language_menu.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN));
    	openfolder_menu.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
    	exit_menu.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));			//quit
    	close_menu.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN));
    	removefiles_menu.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));	//cut selected
    	
    	//decrVol.setAccelerator(new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN));		//- not in numpad
    	//incrVol.setAccelerator(new KeyCodeCombination(KeyCode.PLUS, KeyCombination.CONTROL_DOWN));		//+ not in numpad
    	decrVol.setAccelerator(new KeyCodeCombination(KeyCode.SUBTRACT, KeyCombination.CONTROL_DOWN)); 		//- in numpad
    	incrVol.setAccelerator(new KeyCodeCombination(KeyCode.ADD, KeyCombination.CONTROL_DOWN));			//+ in numpad
    	muteVol.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD0, KeyCombination.CONTROL_DOWN));
    	
    	//fullscreen_menu.setAccelerator(new KeyCodeCombination(KeyCode.F11, KeyCombination.ALT_DOWN));
    	minimize_menu.setAccelerator(new KeyCodeCombination(KeyCode.M, KeyCombination.CONTROL_DOWN));
    	preview_menu.setAccelerator(new KeyCodeCombination(KeyCode.F1, KeyCombination.CONTROL_DOWN));	//help
    }
	
    

    
    
}