package minesweeperGUI;

import java.io.File;
import java.util.Optional;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author Isaac Baldwin
 */
public class GUI extends Application implements EventHandler<ActionEvent>{
    
    @Override
    public void start(Stage primaryStage) {
        this.window = primaryStage;
        window.getIcons().add(new Image("resources\\mine.png"));
        window.setTitle("Minesweeper");
        mainMenu();
    }

    private Minefield game;
    private GridPane gameGrid;
    private Stage window;
    private int clicks;
    private Label score;
    private Double volume = 0.5;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    /**
     * graphical setup for the main menu
     */
    private void mainMenu(){
        VBox menu = new VBox();
        menu.setAlignment(Pos.CENTER);
        menu.setSpacing(20);
        Label minesweeper = new Label("Minesweeper");
        minesweeper.setPadding(new Insets(10));
        Button play = new Button("Play default");
        play.setOnAction(e-> gameDefault());
        Button custom = new Button("Custom game");
        custom.setOnAction(e-> setUp());
        Button quit = new Button("Quit");
        quit.setOnAction(e-> window.close());
        minesweeper.setFont(new Font(40));
        play.setFont(new Font(20));
        custom.setFont(new Font(20));
        quit.setFont(new Font(20));
        
        menu.getChildren().addAll(minesweeper,play,custom,quit);
        Scene options = new Scene(menu, 700, 700);
        window.setScene(options);
        window.show(); 
    }
    
    /**
     * Starts a new game with default settings
     */
    private void gameDefault(){
        game = new Minefield(30,25);
        game.populate(90);
        minesweeper();
    }
    
    /**
     * Main game display with controls and the minesweeper grid
     */
    private void minesweeper(){
        window.setMaximized(false);
        gameGrid = new GridPane();
        GridPane controls = new GridPane();
        Button restart = new Button("Restart");
        Button help = new Button("Help");
        Button quit = new Button("Quit");
        restart.setFont(new Font(15));
        help.setFont(new Font(15));
        quit.setFont(new Font(15));
        controls.add(restart,0,0);
        controls.add(help,1,0);
        controls.add(quit,2,0);
        controls.setVgap(5);
        controls.setHgap(100);
        restart.setOnAction(e -> gameRestart());
        help.setOnAction(e -> dialog("Help","Controls","Left-click to step on tile\nRight-click to mark a tile\nMarks all the mines to win!\nThe lower the score the better!","","mine.png",false));
        quit.setOnAction(e -> window.close());
        controls.setAlignment(Pos.CENTER);
        
        HBox volBox = new HBox();
        Slider volSlider = new Slider(0,1,1);
        Label volLabel = new Label("Volume:");
        volLabel.setFont(new Font(16));
        volSlider.setValue(volume);
        volSlider.valueProperty().addListener((observable, oldVal, newVal) ->
        {
            volume = newVal.doubleValue();
        } );
        
        volBox.setAlignment(Pos.CENTER);
        volBox.setPadding(new Insets(10));
        volBox.getChildren().addAll(volLabel,volSlider);
        
        gameGrid.setVgap(2);
        gameGrid.setHgap(2);
        gameGrid.setAlignment(Pos.CENTER);
        
        for(int x = 0; x < game.getRows();x++){
            for(int y = 0; y < game.getColumns(); y++){
                newImage("hidden.png",x,y);
            }
        }

        VBox mainBox = new VBox();
        mainBox.setAlignment(Pos.CENTER);
        mainBox.getChildren().addAll(controls,volBox,gameGrid);
        GridPane scorePane = new GridPane();
        Label lbScore = new Label("Score: ");
        score = new Label("0");
        lbScore.setFont(new Font(25));
        score.setFont(new Font(25));
        scorePane.add(score,1,0);
        scorePane.add(lbScore,0,0);
        mainBox.getChildren().add(scorePane);
        scorePane.setAlignment(Pos.CENTER);
        
        window.setScene(new Scene(mainBox, 1000,1000));
        window.setMaximized(true);
        window.show();
    }
    
    /**
     * Replaces the image at given position with new image
     * @param file image file name
     * @param x row position of image to change
     * @param y column position of image to change
     */
    private void newImage(String file, int x, int y){
        ImageView img = new ImageView("resources\\"+file);
        img.setFitHeight(30);
        img.setFitWidth(30);
        img.setPickOnBounds(true);
        img.setOnMouseClicked(e -> tileClicked(e,GridPane.getColumnIndex(img),GridPane.getRowIndex(img)));
        GridPane.setRowIndex(img, x);
        GridPane.setColumnIndex(img, y);
        gameGrid.setConstraints(img, x, y);
        gameGrid.getChildren().add(img);
    }
    
    /**
     * Handles when the user interacts with a tile
     * @param e what button was pressed
     * @param x row position
     * @param y column position
     */
    private void tileClicked(MouseEvent e, int x, int y){
        if(!game.getMineTile(x,y).isRevealed()){
            playSound("select.mp3");
            clicks++;
            score.setText(Integer.toString(clicks));
            if (e.getButton() == MouseButton.SECONDARY) {
                game.getMineTile(x,y).toggleMarked();
                if(game.getMineTile(x,y).isMarked()){
                    newImage("flag.jpg",x,y);
                }
                else{
                    newImage("hidden.png",x,y);
                }
            }
            if(e.getButton() == MouseButton.PRIMARY){
                Boolean gameContinue = game.step(x,y);
                if(game.getMineTile(x, y).isMarked()&&game.getMineTile(x, y).isRevealed()){
                    game.mark(x,y);
                }
                if(!gameContinue){
                    fail();
                }
                revealTiles();
                
            }
            Boolean gameWon = game.areAllMinesFound();
            if(gameWon){
                    win();
            }
        }
    }
    
    /**
     * Checks to see which tiles need to be revealed and changes their images accordingly 
     */
    private void revealTiles(){
        for(int x = 0; x < game.getRows();x++){
            for(int y = 0; y < game.getColumns(); y++){
                MineTile tile = game.getMineTile(x,y);
                if(tile.isRevealed() && tile.getMinedNeighbours() == 0){
                    newImage("safe.png",x,y);
                }
                else if(tile.isRevealed()){
                    newImage(tile.getMinedNeighbours()+".png",x,y);
                }
            }
        }
    }

    /**
     * Custom game menu setup
     */
    private void setUp(){
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(10));
        grid.setAlignment(Pos.CENTER);
        
        Text menuText = new Text("Setup");
        menuText.setFont(new Font(40));
        grid.add(menuText,0,0);
        
        Label lbWidth = new Label("Grid width:");
        Label lbLength = new Label("Grid height:");
        Label lbMines = new Label("Mine number:");
        lbWidth.setFont(new Font(15));
        lbLength.setFont(new Font(15));
        lbMines.setFont(new Font(15));
        grid.add(lbWidth, 0, 2);
        grid.add(lbLength, 0, 3);
        grid.add(lbMines, 0, 4);
        
        TextField txtWidth = new TextField();
        TextField txtHeight = new TextField();
        TextField txtMines = new TextField();
        txtWidth.setMaxWidth(150);
        txtHeight.setMaxWidth(150);
        txtMines.setMaxWidth(150);
        grid.add(txtWidth, 1, 2);
        grid.add(txtHeight, 1, 3);
        grid.add(txtMines, 1, 4);
        Label message = new Label();
        grid.add(message, 1, 6);
        Button save = new Button("Start");
        grid.add(save, 0, 6);
        grid.add(new Text(""),1,5);
        save.setOnAction(e -> checkSetUp(txtWidth, txtHeight, txtMines, message));

        Scene options = new Scene(grid, 700, 700);
        window.setScene(options);
        window.show();
    }

    /**
     * Restarts the game with same settings as before
     */
    private void gameRestart(){
        int mines = game.getMines();
        game = new Minefield(game.getRows(),game.getColumns());
        game.populate(mines);
        window.setMaximized(true);
        clicks = 0;
        minesweeper();
    }
    
    /**
     * Checks to see whether the custom game settings are valid
     * @param txtW width text input
     * @param txtH height text input
     * @param txtM mine text input
     * @param message label where error message is outputted
     */
    private void checkSetUp(TextField txtW, TextField txtH, TextField txtM,Label message){
        String error = "Needs to be a number!";
        try{
            txtW.setStyle("-fx-text-fill: black; -fx-font-size: 12px;");
            txtH.setStyle("-fx-text-fill: black; -fx-font-size: 12px;");
            txtM.setStyle("-fx-text-fill: black; -fx-font-size: 12px;");
            int w = Integer.parseInt(txtW.getText());
            int h = Integer.parseInt(txtH.getText());
            int m = Integer.parseInt(txtM.getText());
            if (m >= h*w){
                error = "Needs less than "+(h*w)+" mines!";
                txtM.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                throw new NumberFormatException();
                
            }
            else if(m<=0){
                error = "Must be at least 1 mine!";
                txtM.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                throw new NumberFormatException();
            }
            else if(h>25){
                error = "Maximum height is 25!";
                txtH.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                throw new NumberFormatException();
            }
            else if(w>50){
                error = "Maximum width is 50!";
                txtW.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
                throw new NumberFormatException();
            }
            game = new Minefield(w,h);
            game.populate(m);
            minesweeper();
        }
        catch(NumberFormatException e){
            playSound("error.mp3");
            message.setText(error);
        }
    }

    /**
     * Handles graphics/sounds when a mine is stepped on
     */
    private void fail(){
        for(int x = 0; x < game.getRows();x++){
            for(int y = 0; y < game.getColumns(); y++){
                if(game.getMineTile(x,y).isMined()){
                    newImage("explosion.jpg",x,y);
                }
            }
        }
        playSound("explosion.mp3");
        dialog("BOOOOOOM!","You Lose!","You stepped on a mine!","mine.png","explosion.png", true);
    }
    
    /**
     * Opens a dialog box with given settings
     * @param title title of dialog
     * @param header header of dialog
     * @param content content of dialog
     * @param graphic image used for dialog graphic
     * @param icon image used for dialog icon
     * @param gameEnd whether the dialog is used for the end of the game
     */
    private void dialog(String title,String header,String content,String graphic,String icon,Boolean gameEnd){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.setGraphic(new ImageView("resources\\"+graphic));
        Stage stage = (Stage)alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("resources\\"+icon));

        ButtonType menu = new ButtonType("Main menu");
        ButtonType again = new ButtonType("Play again");
        ButtonType cont = new ButtonType("Continue"); 

        if (gameEnd){
            alert.getButtonTypes().setAll(menu,again);
        } 
        else{
            alert.getButtonTypes().setAll(cont);
        }
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == menu){
            start(window);
        }
        else if (result.get() == again){
            gameRestart();
        }
        else if(result.get() == cont){
            alert.close();
        }
    }
   
    /**
     * Handles graphic/sounds for when all mines are correctly flagged
     */
    private void win(){
        playSound("win.mp3");
        dialog("Congradulations!","You Win!","You correctly marked every mine!","celebration.png","win.png", true);
    }
    
    /**
     * Plays the sound at the given volume set by volume slider
     * @param file file name of sound to play
     */
    private void playSound(String file){
        Media sound = new Media(new File("resources\\"+file).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setVolume(volume);
        mediaPlayer.play();
    }
    
    @Override
    public void handle(ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
