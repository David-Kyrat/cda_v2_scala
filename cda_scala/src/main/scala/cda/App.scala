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
        try
            verbose = args.contains("verbose")
            val guiMain = new jfxuserform.Main()
            // launch gui and blocks until gui is closed
            val guiArgs = if Files.exists(abbrevFilePath) then Array(absAbbrevFilePath) else Array[String]()
            guiMain.start(guiArgs)
            val userResponse = guiMain.serializedOutput.get
            // FIXME: Even when setting PandocCommand.exec(silent=false)
            //    stderr is not printed to console. i.e. nothing tells us whether pandoc failed, and if it did => no way of knowing why
            // val userResponse = "#BM"
            modelMain.main(Array(userResponse))
        catch case e: Throwable => e.printStackTrace()
        finally onExit()

    /* def main(args: Array[String]): Unit =
        println("Hello World!")
        Application.launch(classOf[App], args: _*) */

    /**
     * See `ch.view.jfxuserform.Main.silenceBurningWaveLogsAfterStageClose()` for more details.
     */
    private def onExit(): Unit = jfxuserform.Main.silenceBurningWaveLogsAfterStageClose()
