package cs1302.gallery;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.text.*;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.geometry.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCode;
import java.util.Random;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import java.net.URLEncoder;
import java.net.URL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

/** Makes the gallery app. */
public class GalleryApp extends Application {

    Button pause = new Button("Pause");
    VBox vPane = new VBox();
    HBox hPane = new HBox();
    HBox imagePane = new HBox();
    ToolBar toolbar = new ToolBar();
    TextField searchbar = new TextField();
    GridPane rows = new GridPane();
    Timeline tl = new Timeline();
    boolean play = false;
    boolean start = true;
    String search = "";
    String[] imageArt;
    ImageView[] image;
    int size = 0;
    URL url = null;
    InputStreamReader read = null;
    KeyFrame kf = null;
    ProgressBar progress = new ProgressBar();
    Timeline timeline = new Timeline();


    /**
     * Method that holds everything used to make the app.
     * @param stage stage where everything takes place
     */
    public void start(Stage stage) {
        Scene scene = new Scene(vPane, 500, 485);
        stage.setTitle("Gallery!");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
        vPane.getChildren().add(hPane);

        menubar();

        if (start) {
            search();
            parser("alternative");
            EventHandler<ActionEvent> handler = swap -> change();
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(2), handler);
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.getKeyFrames().add(keyFrame);
            timeline.play();
            pause();
            start = false;
        } // if

        progress();

    } // start

    /**
     * Menubar with a "File" menu. The only menu item is "Exit", which exits the app.
     */
    public void menubar() {
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(event -> {
            Platform.exit();
            System.exit(0);
        }); // exit

        Menu file = new Menu("File");
        file.getItems().add(exit);

        MenuBar menu = new MenuBar();
        menu.getMenus().add(file);
        vPane.getChildren().add(menu);
    } // menubar

    /**
     * Play/Pause button allow the user to pause and resume the random image replacement described
     * for the main content of the application. Button text changes to reflect application mode.
     */
    public void pause() {
        pause.setOnAction(event -> {
            if (pause.getText().equals("Play")) {
                pause.setText("Pause");
                play = false;
                timeline.play();
                // change();
            } else if (pause.getText().equals("Pause")) {
                pause.setText("Play");
                play = true;
                timeline.pause();
            } // else
        }); // pause button
    } // pause

    /**
     * User enters a search query for the iTunes Search API.
     */
    public void search() {
        Button updateImages = new Button("Update Images");
        pause();

        updateImages.setOnAction(event -> {
            search = searchbar.getText();
            parser(search);
        });

        toolbar.getItems().addAll(pause, new Separator(), new Text("Search Query..."),
            searchbar, updateImages);

        vPane.getChildren().add(toolbar);
    } // search

    /**
     * JSON accesses the iTunes Search API and parses the information from the API with the search
     * the user puts in.
     * @param search the keyword the user is searching
     */
    public void parser(String search) {
        String userInput = "";
        try {
            userInput = URLEncoder.encode(search, "UTF-8");
        } catch (java.io.UnsupportedEncodingException uee) {
            System.out.println("Character Encoding is not supported");
        } // try

        try {
            url = new URL("https://itunes.apple.com/search?term=" + userInput);
        } catch (java.net.MalformedURLException murle) {
            System.out.println("URL is malformed");
        } // try

        try {
            read = new InputStreamReader(url.openStream());
        } catch (IOException ioe) {
            System.out.println("IO Exception has occured.");
        } // try

        JsonObject root = JsonParser.parseReader(read).getAsJsonObject();
        JsonArray images = root.getAsJsonArray("results");

        int result = images.size();

        if (result < 20) {
            this.size = 0;
        } else {
            this.size = result;
        } // if

        imageArt = new String[result];
        setProgress(0);
        for (int i = 0; i < result; i++) {
            JsonObject album = images.get(i).getAsJsonObject();
            JsonElement art = album.get("artworkUrl100");
            String albumURL = art.getAsString();
            System.out.println(albumURL);
            imageArt[i] = albumURL;
            setProgress(1.0 * i / result);
        } // for
        setProgress(1);
        display();
    } // parser

    /**
     * Grid displaying the images of the searched term.
     */
    public void display() {
        if (this.size >= 20) {
            image = new ImageView[20];
            for (int i = 0; i < image.length; i++) {
                image[i] = new ImageView(imageArt[i]);
            } // for

            for (int i = 0; i < 5; i++) {
                rows.setConstraints(image[i], i, 0);
            } // for

            for (int i = 5; i < 10; i++) {
                rows.setConstraints(image[i], i - 5, 1);
            } // for

            for (int i = 10; i < 15; i++) {
                rows.setConstraints(image[i], i - 10, 2);
            } // for

            for (int i = 15; i < 20; i++) {
                rows.setConstraints(image[i], i - 15, 3);
            } // for

            for (int i = 0; i < image.length; i++) {
                rows.getChildren().addAll(image[i]);
            } // for

        } else {

            Stage closeImages = new Stage();
            Text close = new Text("Invalid search, try again.");
            HBox closePane = new HBox();

            closePane.getChildren().addAll(close);
            closePane.setPadding(new Insets(10));
            Scene scene = new Scene(closePane);

            closeImages.setTitle("Error");
            closeImages.setScene(scene);
            closeImages.sizeToScene();
            closeImages.show();
        } // if
    } // display

    /**
     * Images not shown on the grid are stored in memory and will replace the previous images in the
     * grid.
     */
    public void change() {
        ImageView[] change;
        Random random = new Random();

        if (this.size > 20) {
            change = new ImageView[this.size];
            for (int i = 21; i < this.size; i++) {
                change[i] = new ImageView(imageArt[i]);
            } // for
            int swapImages = random.nextInt(5) + 2;
            for (int i = 1; i <= swapImages; i++) {
                int randomRow = random.nextInt(4);
                int randomColumn = random.nextInt(3);
                int nextImage = random.nextInt(this.size - 1) + 21;
                rows.setConstraints(change[nextImage], randomColumn, randomRow);
                rows.getChildren().addAll(change[nextImage]);
            } // for
        } // if
    } // change

    /**
     * Indicates the progress of querying the iTunes Search API, loading the images into memory, and
     * updating the main content area of the application.
     */
    public void progress() {
        BorderPane bp = new BorderPane();

        imagePane.getChildren().addAll(progress, new Text(" Images provided courtesy of iTunes"));
        bp.setBottom(imagePane);
        vPane.getChildren().addAll(rows, bp);
    } // progress

    /**
     * Allows the progress bar to increment when the images swap.
     * @param progressNum the number to increment the progress
     */
    private void setProgress(final double progressNum) {
        Platform.runLater(() -> progress.setProgress(progressNum));
    } // setProgress

} // GalleryApp
