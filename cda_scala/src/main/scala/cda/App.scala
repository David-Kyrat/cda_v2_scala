package cda;

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.stage.Stage

import cda.model.Main as modelMain
import cda.model.Utils.{abbrevFilename, pathOf}
import cda.model.pandoc.PandocCommand.toVec
import cda.model.pandoc.Timer.{time_and_result, time_average}
import cda.model.pandoc.{Colorify, PandocCommand}
import cda.view.jfxuserform
import javafx.application.{Application, Platform}
import java.nio.file.Files

import java.nio.file.Path

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
    var verbose = false
    // to get abbrevFile with resourceAsStream we need to access /res/abbrev.tsv. hence replacing files/res/abbrev.tsv by /res/abbrev.tsv
    val abbrevFilePath: Path = pathOf(abbrevFilename)
    val absAbbrevFilePath: String = abbrevFilePath.normalize().toString

    def main(args: Array[String]): Unit =
        println("Hello World!")
        Application.launch(classOf[App], args: _*)

    /**
     * See `ch.view.jfxuserform.Main.silenceBurningWaveLogsAfterStageClose()` for more details.
     */
    private def onExit(): Unit = jfxuserform.Main.silenceBurningWaveLogsAfterStageClose()

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
