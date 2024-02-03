package cda;

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.stage.Stage

class App extends Application:
    override def start(primaryStage: Stage): Unit =
        val subroot = new VBox(100)
        subroot.setPrefSize(500, 200)
        val root = new BorderPane(subroot)

        val scene = new Scene(root, 800, 800)
        primaryStage.setTitle("Card Game with MaterialFX")
        primaryStage.setScene(scene)
        primaryStage.centerOnScreen()
        primaryStage.show()

/**
 * Hello world!
 */
object App:

    def main(args: Array[String]): Unit =
        println("Hello World!")
        Application.launch(classOf[App], args: _*)

    /* def main(args: Array[String]): Unit =
        VBox subroot = new VBox(100, board, playerHand);
    BorderPane root = new BorderPane(subroot);

    Scene scene = new Scene(root, W, H);
    scene
        .getStylesheets()
        .add(getClass().getResource("/style.css").toExternalForm());

    primaryStage.setTitle("Card Game with MaterialFX");
    primaryStage.setScene(scene);
    primaryStage.centerOnScreen();
    primaryStage.show();

    println("Hello World!") */
