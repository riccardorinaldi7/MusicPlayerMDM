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
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.*;
import javafx.util.Duration;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.control.ButtonBar.ButtonData;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.*;


public class Controller {

    private void print(String str) { System.out.println(str);}
    private String currentTheme;
    
    @FXML
    private AnchorPane window;

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
//    @FXML
//    private TableColumn<Song, String> durationColumn;
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
    private MenuItem menuInterface;
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
    private Song currentActive;
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
        
        alert.setGraphic(new ImageView(new Image("file:src/main/resources/images/exit32.png")));
        util.applyThemeToDialog(alert);
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            System.exit(0);
        } else { }
    }

    @FXML
    private void initialize() throws Exception {


        //-------- AGGIUNTI DA NOI ------------
        
        insertSubMenus_menuBar();	//enrich the menubar with submenus
        setIcons();					//set icons depending on the theme
        insertToolTips();			//attach tooltip to the main buttons
        volumeIconChanger(); 		//update volume icon if volume == 0
        addShortcutsMenubar();	
        attachMenuActions();		//add setOnAction to menuItems

        //restartMessage();

        //----------------------------------------
        
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

        /*showPlaylist.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(playlistNode.isVisible() == true) {
                    hideTransation(playlistNode);
                }
                else {
                    showTransation(playlistNode);
                }
            }
        });*/

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
        //durationColumn.setCellValueFactory(cellData -> cellData.getValue().durationProperty());
        rateColumn.setCellValueFactory(cellData -> cellData.getValue().rateProperty());
        formatColumn.setCellValueFactory(cellData -> cellData.getValue().formatProperty());

        showSongInfo((Song) null);

        //this runs everytime an item in tableview is single-clicked/selected
        songTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                System.out.println("SongTable: selection detected - " + newValue.getSongName());
                currentActive = newValue;
            }
        });

        folderChooser.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("folderChooser: choose a music folder");
                chooseFolder();
            }
        });
    }

    //TODO
    private void restartMessage() {
        DialogPane dialogPane = new DialogPane();
        Dialog<?> dialog = new Dialog<>();
        HBox hb = new HBox();
        Label label = new Label("Restart to apply changes");
        Button offButton = new Button("Shutdown");
        offButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                System.exit(0);
            }
        });

        hb.getChildren().addAll(offButton);
        hb.setSpacing(3);

        VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.getChildren().addAll(label, hb);

        dialogPane.setContent(vbox);
        dialog.setTitle("Restart Message");
        dialog.setDialogPane(dialogPane);
        ((Stage)dialog.getDialogPane().getScene().getWindow()).setAlwaysOnTop(true);
        dialog.show();
    }

    @FXML
    private void chooseFolder() {
        DirectoryChooser chooser = new DirectoryChooser();
        File selectedDirectory = chooser.showDialog(stage);
        if(selectedDirectory == null) {
            System.out.println("No directory selected!");
            return;
        }

        importSongs(selectedDirectory.listFiles());
    }

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
                            drawAlbumImage(currentPlaying);
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

    // Shows information about the played song above the playback slider
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
                        //print("mediaView: mediaPlayer change detected...");
                        showSongInfo(newValue);
                        updateValues();
                        if(newValue !=  null) drawAlbumImage(songTable.getItems().get(players.indexOf(newValue)));
                        else removeAlbumImage();
                    }
                    catch(IOException e) {}
                    catch(UnsupportedTagException e) {}
                    catch(InvalidDataException e) {} catch (Exception e) {
                        e.printStackTrace();
                    }
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

            songSlider.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if(mediaView.getMediaPlayer() != null) {
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
            
            // ********************************************************************************************************
        	songTable.getSelectionModel().clearSelection(); //TOLTA SELEZIONE modifica del 19/11/2020
        	// ********************************************************************************************************
        	
            
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
        print("created MediaPlayer for: " + url);
        return player;
    }

    /*public Media createMedia(String url) {
        url.replace("\\", "/");
        final Media media = new Media(new File(url).toURI().toString());
        return media;
    }*/

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
        albumName.setText(album);
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
                            if(player != null && (player.getStatus() != Status.PAUSED) && (player.getStatus() != Status.STOPPED) && (player.getStatus() != Status.READY)) {
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

    /*private void showTransation(AnchorPane anchorPane) {
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
    }*/

    private void drawAlbumImage(Song song) throws Exception {
        ObservableList<Node> imgList = imagePane.getChildren();
        if(!imgList.isEmpty()) imgList.removeAll(imgList);

        BufferedImage img = song.getImage();
        if(img == null){
            print("drawAlbum(): no image in this song");
            return;
        }
        double W = imagePane.getPrefWidth();
        double H = imagePane.getPrefHeight();
        final Canvas canvas = new Canvas(W, H);
        GraphicsContext ctx = canvas.getGraphicsContext2D();
        Image image = SwingFXUtils.toFXImage(img, null);
        ctx.drawImage(image, 5, 10, W, H);

        imagePane.getChildren().add(new Group(canvas));
    }

    private void removeAlbumImage() {
        ObservableList<Node> imgList = imagePane.getChildren();
        if(!imgList.isEmpty()) imgList.removeAll(imgList);
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
                if (mediaView.getMediaPlayer() != null && mediaView.getMediaPlayer().getStatus() != MediaPlayer.Status.PLAYING) {
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
        if(mp == null) return;

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

        //aggiunta alert di conferma prima di chiudere i file
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setContentText(resources.getString("closePlaylistMessage"));
        alert.setHeaderText(null);
        alert.initStyle(StageStyle.UNDECORATED); //toglie completamente la barra del titolo
        alert.setTitle(resources.getString("closePlaylistTitle"));
        ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("file:src/main/resources/images/logo.png"));

        util.applyThemeToDialog(alert);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() != ButtonType.OK) return;

        System.out.println("I'm closing the music folder");
        if(mediaView != null && mediaView.getMediaPlayer() != null){
            mediaView.getMediaPlayer().stop();
            //songSlider.setValue(0);
            mediaView.setMediaPlayer(null);
            mediaPlayer = null;
            currentDuration.setText("00:00");
            totalDuration.setText("00:00");
        }
        players.clear();
        songTable.getItems().clear();
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
    
	// ----------------------------------------------------------------------------------------------------------------------
	// HANDLERS FOR MENU ITEMS
	// ----------------------------------------------------------------------------------------------------------------------
	
    private void attachMenuActions(){

        openfile_menu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                chooseFile();
            }
        });

        // Remove a SINGLE file!!
        removefiles_menu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
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
        });

        close_menu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                closeFolder();
            }
        });
    	
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

		previous_menu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                playPreviousSong();
            }
        });

		next_menu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(mediaView.getMediaPlayer() != null && players != null) seekAndUpdate(players.get(players.indexOf(mediaView.getMediaPlayer())).getTotalDuration());
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
		
		preview_menu.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
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
		});

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
            updateSliderPosition(Duration.ZERO);
            songSlider.setValue(0);
            double duration = mediaView.getMediaPlayer().getTotalDuration().toSeconds();
            totalDuration.setText(secToMin((long) duration));
            updateValues();
            mediaView.getMediaPlayer().setVolume(volume);
            mediaView.getMediaPlayer().play();
            viewPauseIcon();
        }
    }

    private void setEndOfMedia(MediaPlayer prevPlayer, MediaPlayer nextPlayer) {
        prevPlayer.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                //print("New setOnEndOfMedia detected!");
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

    private void updateSongIds(ObservableList<Song> items, int start) {
        for(int i=start; i<items.size(); i++)
            items.get(i).setId(Integer.toString(i+1));

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
    
    @FXML
    public void simplifyInteface(ActionEvent actionEvent) {
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
        		appProps.setProperty("interface", "Simple");
                appProps.store(new FileWriter(appConfigPath), null);
        	} 
            catch (IOException e) {
                    e.printStackTrace();
            }   	
        } 
        
        else {	
        	// ... user chose CANCEL or closed the dialog --> the program will close, after having set the properties
        	try {
        		appProps.setProperty("interface", "Simple");
                appProps.store(new FileWriter(appConfigPath), null);
        	} 
            catch (IOException e) {
                    e.printStackTrace();
            }   
        	closeProgram();
        }  
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
        Tooltip.install(volumeSlider, 		new Tooltip(resources.getString("tt_volumepane")));
        Tooltip.install(showPlaylist, 		new Tooltip(resources.getString("tt_playlist")));
        Tooltip.install(folderChooser, 		new Tooltip(resources.getString("tt_folder")));

        //in alto a dx
        Tooltip.install(minimize, 		new Tooltip(resources.getString("tt_minimize")));
        //Tooltip.install(maximize, 		new Tooltip(resources.getString("tt_maximize")));
        Tooltip.install(exit, 		new Tooltip(resources.getString("tt_exit")));
    }

    //----------------------
    // SUB MENU
    //----------------------

    private void insertSubMenus_menuBar() {
        /*menuInterface = new Menu();
        menuInterface.setText(resources.getString("interface"));
        settings_menu.getItems().add(menuInterface);
        simpleInterface = new MenuItem();
        simpleInterface.setText(resources.getString("simple"));
        advancedInterface = new MenuItem();
        advancedInterface.setText(resources.getString("advanced"));
        menuInterface.getItems().addAll(simpleInterface, advancedInterface);*/

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
    	
    	//nuovi setImage
    	//cartella "resources" è considerata root
    	exit_icon.setImage(new Image(ClassLoader.getSystemResource("images/cancelw.png").toExternalForm()));
    	minimize_icon.setImage(new Image(ClassLoader.getSystemResource("images/minimizew.png").toExternalForm()));
        folderChooser.setImage(new Image(ClassLoader.getSystemResource("images/music-folderw.png").toExternalForm()));
        
        muteIcon.setImage(new Image(ClassLoader.getSystemResource("images/speakermutew.png").toExternalForm()));
        volumeIcon.setImage(new Image(ClassLoader.getSystemResource("images/speakerw.png").toExternalForm()));
        previousSongButton.setImage(new Image(ClassLoader.getSystemResource("images/back-arrowsw.png").toExternalForm()));
        nextSongButton.setImage(new Image(ClassLoader.getSystemResource("images/forward-arrowsw.png").toExternalForm()));
        pauseButton.setImage(new Image(ClassLoader.getSystemResource("images/pausew.png").toExternalForm()));
        playButton.setImage(new Image(ClassLoader.getSystemResource("images/playw.png").toExternalForm()));
        
        //Icons for the menu bar
        
        //File menu
    	openfile_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/openw.png").toExternalForm()));
    	openfolder_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/openw.png").toExternalForm()));
    	exit_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/closew.png").toExternalForm()));
    	close_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/closefolderw.png").toExternalForm()));
    	removefiles_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/removew.png").toExternalForm()));
    	
    	//Playback menu
    	playpause_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/playpausew.png").toExternalForm()));
    	next_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/forwardw.png").toExternalForm()));
    	previous_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/rewindw.png").toExternalForm()));
    	menuVolume.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/volumew.png").toExternalForm()));
    	
    	decrVol.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/minusw.png").toExternalForm()));
    	incrVol.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/plusw.png").toExternalForm()));
    	muteVol.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/speakermutew.png").toExternalForm()));
    	
    	//View menu
    	//fullscreen_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/fullscreen.png"));
    	minimize_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/minimizew.png").toExternalForm()));
    	theme_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/themew.png").toExternalForm()));
    	//hidebar_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/menubarw.png"));
    	
    	//Settings menu
    	menuInterface.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/interfacew.png").toExternalForm()));
    	language_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/languagesw.png").toExternalForm()));
    	
    	//Help menu
    	about_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/logow.png").toExternalForm()));
    	preview_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/tutorialw.png").toExternalForm()));
    	shortcuts_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/tipsw.png").toExternalForm()));
    	
    	//vecchi setImage
    	/*
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
    	//hidebar_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/menubarw.png"));
    	
    	//Settings menu
    	menuInterface.setGraphic(new ImageView("file:src/main/resources/images/menubar/interfacew.png"));
    	language_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/languagesw.png"));
    	
    	//Help menu
    	about_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/logow.png"));
    	preview_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/tutorialw.png"));
    	shortcuts_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/tipsw.png"));
		*/
	}

	private void setIconsForLightTheme() {
    	
		//nuovi setImage
    	//cartella "resources" è considerata root
    	exit_icon.setImage(new Image(ClassLoader.getSystemResource("images/cancel.png").toExternalForm()));
    	minimize_icon.setImage(new Image(ClassLoader.getSystemResource("images/minimize.png").toExternalForm()));
        folderChooser.setImage(new Image(ClassLoader.getSystemResource("images/music-folder.png").toExternalForm()));
        
        muteIcon.setImage(new Image(ClassLoader.getSystemResource("images/speakermute.png").toExternalForm()));
        volumeIcon.setImage(new Image(ClassLoader.getSystemResource("images/speaker.png").toExternalForm()));
        previousSongButton.setImage(new Image(ClassLoader.getSystemResource("images/back-arrows.png").toExternalForm()));
        nextSongButton.setImage(new Image(ClassLoader.getSystemResource("images/forward-arrows.png").toExternalForm()));
        pauseButton.setImage(new Image(ClassLoader.getSystemResource("images/pause.png").toExternalForm()));
        playButton.setImage(new Image(ClassLoader.getSystemResource("images/play.png").toExternalForm()));
        
        //Icons for the menu bar
        
        //File menu
    	openfile_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/open.png").toExternalForm()));
    	openfolder_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/open.png").toExternalForm()));
    	exit_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/close.png").toExternalForm()));
    	close_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/closefolder.png").toExternalForm()));
    	removefiles_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/remove.png").toExternalForm()));
    	
    	//Playback menu
    	playpause_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/playpause.png").toExternalForm()));
    	next_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/forward.png").toExternalForm()));
    	previous_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/rewind.png").toExternalForm()));
    	menuVolume.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/volume.png").toExternalForm()));
    	
    	decrVol.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/minus.png").toExternalForm()));
    	incrVol.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/plus.png").toExternalForm()));
    	muteVol.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/speakermute.png").toExternalForm()));
    	
    	//View menu
    	//fullscreen_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/fullscreen.png"));
    	minimize_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/minimize.png").toExternalForm()));
    	theme_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/theme.png").toExternalForm()));
    	//hidebar_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/menubar.png"));
    	
    	//Settings menu
    	menuInterface.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/interface.png").toExternalForm()));
    	language_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/languages.png").toExternalForm()));
    	
    	//Help menu
    	about_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/logo.png").toExternalForm()));
    	preview_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/tutorial.png").toExternalForm()));
    	shortcuts_menu.setGraphic(new ImageView(ClassLoader.getSystemResource("images/menubar/tips.png").toExternalForm()));
		
    	//vecchie icone
    	/*
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
    	//hidebar_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/menubar.png"));
    	
    	//Settings menu
    	language_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/languages.png"));
    	menuInterface.setGraphic(new ImageView("file:src/main/resources/images/menubar/interface.png"));
    	
    	//Help menu
    	about_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/logo.png"));
    	preview_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/tutorial.png"));
    	shortcuts_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/tips.png"));
		*/
	}
    
    // ----------------------------------------------------------------------------------------------------------------------
    // METHOD FOR SHORTCUTS
    // ----------------------------------------------------------------------------------------------------------------------
    
    private void addShortcutsMenubar() {
    	//SHORTCUT_DOWN vs CONTROL_DOWN --> Control is only for Windows, Shortcut is platform independent
    	
    	previous_menu.setAccelerator(new KeyCodeCombination(KeyCode.R));
    	next_menu.setAccelerator(new KeyCodeCombination(KeyCode.F));
    	//playpause_menu.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN));
    	playpause_menu.setAccelerator(new KeyCodeCombination(KeyCode.SPACE));
    	
    	language_menu.setAccelerator(new KeyCodeCombination(KeyCode.L));
    	openfile_menu.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
    	openfolder_menu.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHIFT_DOWN, KeyCombination.SHORTCUT_DOWN));
    	exit_menu.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN));			//quit
    	close_menu.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN));
    	removefiles_menu.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN));	//cut selected
    	
    	//decrVol.setAccelerator(new KeyCodeCombination(KeyCode.MINUS));	//- not in numpad
    	//incrVol.setAccelerator(new KeyCodeCombination(KeyCode.PLUS));		//+ not in numpad
    	decrVol.setAccelerator(new KeyCodeCombination(KeyCode.SUBTRACT)); 	//- in numpad
    	incrVol.setAccelerator(new KeyCodeCombination(KeyCode.ADD));		//+ in numpad
    	muteVol.setAccelerator(new KeyCodeCombination(KeyCode.NUMPAD0));
    	theme_menu.setAccelerator(new KeyCodeCombination(KeyCode.T));
    	
    	//fullscreen_menu.setAccelerator(new KeyCodeCombination(KeyCode.F11, KeyCombination.ALT_DOWN));
    	minimize_menu.setAccelerator(new KeyCodeCombination(KeyCode.M, KeyCombination.SHORTCUT_DOWN));
    	menuInterface.setAccelerator(new KeyCodeCombination(KeyCode.I));
    	preview_menu.setAccelerator(new KeyCodeCombination(KeyCode.F1, KeyCombination.SHORTCUT_DOWN));	//help
    }

    public void shortcutsDialog(ActionEvent actionEvent) {
    	
    	//System.out.println(System.getProperty("os.name"));
    	
        DialogPane dialogPane = new DialogPane();
        dialogPane.setHeaderText(resources.getString("sc_shortcuts"));
        
        util.applyThemeToDialogPane(dialogPane);
        TableView<Shortcut> table = new TableView<Shortcut>();
      
        final ObservableList<Shortcut> data = FXCollections.observableArrayList(
                new Shortcut("CTRL + O", resources.getString("sc_openfiles")),
                new Shortcut("CTRL + SHIFT + O", resources.getString("tt_folder")),
                new Shortcut("CTRL + X", resources.getString("tt_removesong")),
                new Shortcut("CTRL + W", resources.getString("sc_closeplaylist")),
                new Shortcut("CTRL + Q", resources.getString("sc_exit")),
                new Shortcut("SPACE", resources.getString("sc_playpause")),
                new Shortcut("F", resources.getString("tt_nextsong")),
                new Shortcut("R", resources.getString("tt_previoussong")),
                new Shortcut("-", resources.getString("tt_decrvol")),
                new Shortcut("+", resources.getString("tt_incrvol")),
                new Shortcut("0", resources.getString("sc_zerovol")),
                new Shortcut("CTRL + M", resources.getString("tt_minimize")),
                new Shortcut("T", resources.getString("sc_theme")),
                new Shortcut("L", resources.getString("sc_language")),
                new Shortcut("I", resources.getString("sc_interface")),
                new Shortcut("CTRL + F1", resources.getString("sc_help"))
        );
      
        table.setEditable(false);
        TableColumn shortcutColumn = new TableColumn(resources.getString("sc_colShortcut"));
        shortcutColumn.setMinWidth(200);
        shortcutColumn.setCellValueFactory(new PropertyValueFactory<Shortcut, String>("shortcut"));
        TableColumn descriptionColumn = new TableColumn(resources.getString("sc_colDescr"));
        descriptionColumn.setMinWidth(400);
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<Shortcut, String>("description"));

        table.setItems(data);
        table.getColumns().addAll(shortcutColumn, descriptionColumn);
        dialogPane.setContent(new Group(table));

        Dialog<DialogPane> dialog = new Dialog();
        dialog.setTitle(resources.getString("shortcuts"));
        dialog.setDialogPane(dialogPane);
        ((Stage)dialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image("file:src/main/resources/images/logo.png"));

        dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                dialog.close();
            }
        });
        dialog.show();
    }
    

    
}