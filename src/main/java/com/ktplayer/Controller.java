package com.ktplayer;

import com.jfoenix.controls.JFXSlider;
import com.mpatric.mp3agic.*;
import javafx.fxml.FXMLLoader;
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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.control.Alert.AlertType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;


public class Controller {

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
    private Pane maximize;
    
    @FXML
    private MenuItem playpause_menu;
    @FXML
    private MenuItem next_menu;
    @FXML
    private MenuItem previous_menu;
    @FXML
    private MenuItem fullscreen_menu;
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
        
    private Menu menuVolume;
    private MenuItem decrVol;
    private MenuItem incrVol;
    private MenuItem muteVol;
    
    private Menu menuInterface;
    private MenuItem simpleInterface;
    private MenuItem advancedInterface;
    
    
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

    public Controller() {
        players = new ArrayList<>();
        songSlider = new JFXSlider();
        isAutoplay = false;
        volume = 0.5; // between 0 and 1
        stage = Main.getStage();
        stage.getIcons().add(new Image(ClassLoader.getSystemResource("images/logo.png").toExternalForm()));
    }

    @FXML
    private void initialize() throws Exception {

        if(stage.getScene() == null) System.out.println("scene null");

        //--------------------------------------------------------------------------------------
        // AGGIUNTI DA NOI
        
        insertSubMenus_menuBar();	//enrich the menubar with submenus
        iconsMenubar();				//enrich the manubar with icons 
        insertToolTips();			//attach tooltip to the main buttons
        volumeIconChanger(); 		//update volume icon if volume == 0

        // Shortcuts handler
        // Add any shortcut you want here
        window.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
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
                
            	//aggiunta alert di conferma prima di chiudere l'applicazione
            	Alert alert = new Alert(AlertType.CONFIRMATION);
            	alert.setContentText(resources.getString("sureToClose"));
            	alert.setHeaderText(null);
            	alert.setTitle(resources.getString("confirmExit"));
            	((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("file:src/main/resources/images/logo.png"));
            	
            	Optional<ButtonType> result = alert.showAndWait();
            	if (result.get() == ButtonType.OK){
            		System.exit(0);
            	} else { }
            }
        });

        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        artistNameColumn.setCellValueFactory(cellData -> cellData.getValue().artistNameProperty());
        songNameColumn.setCellValueFactory(cellData -> cellData.getValue().songNameProperty());
        durationColumn.setCellValueFactory(cellData -> cellData.getValue().durationProperty());
        rateColumn.setCellValueFactory(cellData -> cellData.getValue().rateProperty());
        formatColumn.setCellValueFactory(cellData -> cellData.getValue().formatProperty());

        showSongInfo(null);

        songTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showSongInfo(newValue));

        folderChooser.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("folderChooser: icon clicked");
                chooseFolder();
            }
        });
        
        // ----------------------------------------------------------------------------------------------------------
        // HANDLER AGGIUNTI DA NOI
        // ----------------------------------------------------------------------------------------------------------
        
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
			    
			    alert.showAndWait();
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
                songTable.setItems(songsUrls(selectedDirectory));

                songTable.setOnMouseClicked((MouseEvent e) -> {
                                    	
                     if(e.getClickCount() == 1) {
                    	     try {
		                    	 takeCare();
		                     }		                        
		                     catch (Exception ex) {}
                    	 }
                     
                	
                    //con doppio click, parte la canzone!
                    if (e.getClickCount() == 2) {
                    	playPauseSong();
                    }
                });
            }
            catch(Exception e) {}
        }
    }

    public void showSongInfo(Song song) {
        if(song != null) {
            artistName.setText(song.getArtistName());
            songName.setText(song.getSongName());
            albumName.setText(song.getFormat());
        }
        else {
            artistName.setText("-");
            songName.setText("-");
            albumName.setText("-");
        }

    }

    public ObservableList<Song> songsUrls(File dir)   throws Exception{
        ObservableList<Song> songData = FXCollections.observableArrayList();
        File[] files = dir.listFiles();
        String name;
        int i = 0;
        for(File file : files) {
            if(file.isFile()) {
                name = file.getName();
                /*if(name.endsWith("jpg")) {
                    path = file.getAbsolutePath();
                }*/
                if(name.endsWith("mp3") || name.endsWith("wav")) {
                    try {
                        i++;
                        Mp3File mp3 = new Mp3File(file.getPath());
                        ID3v2 tag = mp3.getId3v2Tag();
                        Song song = new Song(String.valueOf(i), tag.getArtist(), tag.getTitle(), kbToMb(file.length()), secToMin(mp3.getLengthInSeconds()),tag.getAlbum(), file.getAbsolutePath());
                        players.add(createPlayer(file.getAbsolutePath()));
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
            
            if((mediaView != null) && (mediaPlayer != null)) {
            	
            	System.out.println(mediaPlayer.getStatus().toString()); //Status = READY
            	
                volume = mediaView.getMediaPlayer().getVolume();
                //questo è quello che fa stoppare la canzone quando ne premo un'altra nella lista
                mediaView.getMediaPlayer().stop();
                mediaView = null;
                mediaPlayer = null;
            }

            Media media = new Media(new File(path).toURI().toString());

            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.stop();
            mediaPlayer.setAutoPlay(false);

            mediaView = new MediaView(mediaPlayer);
            pauseIcon();
            mediaView = new MediaView(players.get(Integer.parseInt(song.getId()) - 1));

            volumeValue.setText(String.valueOf((int)volumeSlider.getValue()));
            volumeSlider.setValue(volume*100);
            mediaView.getMediaPlayer().setVolume(volume);
            mediaView.getMediaPlayer().seek(Duration.ZERO);
            updateSliderPosition(Duration.ZERO);

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

            playButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    mediaView.getMediaPlayer().play();
                    playIcon();
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
                                playIcon();
                            }
                        });
                        pauseSong();
                    }
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

        }
        else {
            if(pauseButton.isVisible()) {
                if ((mediaPlayer != null) && (mediaView != null)) {
                    mediaPlayer = mediaView.getMediaPlayer();
                    mediaPlayer.stop();
                    mediaView = null;
                    mediaPlayer = null;
                }
                pauseIcon();
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

    public MediaPlayer createPlayer(String url) {
        url.replace("\\", "/");
        final Media media = new Media(new File(url).toURI().toString());
        final MediaPlayer player = new MediaPlayer(media);
        System.out.println("+++++ " + url);
        return player;
    }

    public Media createMedia(String url) {
        url.replace("\\", "/");
        final Media media = new Media(new File(url).toURI().toString());
        return media;
    }

    public void playIcon() {
        playButton.setVisible(false);
        playButton.setDisable(true);
        pauseButton.setVisible(true);
        pauseButton.setDisable(false);
    }

    public void pauseIcon() {
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
            Song song = songTable.getSelectionModel().getSelectedItem();
            playPauseSong(song);
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
                                double tduration = player.getTotalDuration().toSeconds();
                                totalDuration.setText(secToMin((long) tduration));
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
            playIcon();
        }
        else return;
    }

    private void pauseSong() {
        mediaView.getMediaPlayer().setAutoPlay(true);
        pauseButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (mediaView.getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING) {
                    mediaView.getMediaPlayer().pause();
                    pauseButton.setVisible(false);
                    pauseButton.setDisable(true);
                    playButton.setVisible(true);
                    playButton.setDisable(false);
                }
            }
        });
    }

    //----------------------------------------------------------------------------------------------------------------
    // NOSTRE MODIFICHE
    //----------------------------------------------------------------------------------------------------------------

    // play from menu bar
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
    private void closeFolder(){
        System.out.println("I'm closing the music folder");
        players.clear();
        songTable.getItems().clear();
        mediaView = null;
    }

    //ATTENZIONE --> ECCEZIONE SE FACCIAMO ANNULLA!!!!!! DA GESTIRE!!!!!!
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
        ((Stage)choiceDialog.getDialogPane().getScene().getWindow()).getIcons().add(new Image("file:src/main/resources/images/logo.png"));
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
            ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image("file:src/main/resources/images/logo.png"));
            alert.showAndWait();
            

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchElementException e){
            System.out.println("User has changed his/her mind");
        }
    }

    private void iconsMenubar() {
    	openfile_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/open.png"));
    	openfolder_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/open.png"));
    	close_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/close.png"));
    	
    	playpause_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/playpause.png"));
    	next_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/forward.png"));
    	previous_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/rewind.png"));
    	menuVolume.setGraphic(new ImageView("file:src/main/resources/images/menubar/volume.png"));
    	
    	decrVol.setGraphic(new ImageView("file:src/main/resources/images/menubar/minus.png"));
    	incrVol.setGraphic(new ImageView("file:src/main/resources/images/menubar/plus.png"));
    	muteVol.setGraphic(new ImageView("file:src/main/resources/images/menubar/speakermute.png"));
    	
    	fullscreen_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/fullscreen.png"));
    	minimize_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/minimize.png"));
    	language_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/languages.png"));
    	
    	menuInterface.setGraphic(new ImageView("file:src/main/resources/images/menubar/interface.png"));
    	
    	about_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/logo.png"));
    	preview_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/tutorial.png"));
    	shortcuts_menu.setGraphic(new ImageView("file:src/main/resources/images/menubar/tips.png"));
   }
    
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

    //----------------------------------------------------------------------------------------------------
    // SUB MENU
    //----------------------------------------------------------------------------------------------------

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

}

