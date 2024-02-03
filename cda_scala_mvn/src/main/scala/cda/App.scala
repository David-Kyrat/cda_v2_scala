package cda;

import cda.model.Utils
import cda.model.Utils.abbrevFilename
import cda.model.Utils.pathOf
import cda.model.io.Serializer
import cda.model.pandoc.Colorify
import cda.model.pandoc.PandocCommand
import cda.model.pandoc.PandocCommand.toVec
import cda.model.pandoc.Timer.time_and_result
import cda.model.pandoc.Timer.time_average
import cda.model.Main as modelMain
import cda.view.jfxuserform
import com.jfoenix.controls.JFXTextField
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.stage.Stage

import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.URL
import java.nio.channels.Channels
import java.nio.channels.FileChannel
import java.nio.channels.ReadableByteChannel
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption.*
import scala.io.Source
import scala.util.{Try, Success, Failure}

/* class App extends Application:
    override def start(primaryStage: Stage): Unit =
        val subroot = new VBox(100)
        subroot.setPrefSize(500, 200)
        val root = new BorderPane(subroot)
        val tf = JFXTextField()
        tf.setPromptText("Enter your name")
        subroot.getChildren().add(tf)

        val scene = new Scene(root, 800, 800)
        primaryStage.setTitle("Card Game with MaterialFX")
        primaryStage.setScene(scene)
        primaryStage.centerOnScreen()
        primaryStage.show() */

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
            val guiArgs =
                if Files.exists(abbrevFilePath) then Array(absAbbrevFilePath)
                else Array[String]()
            guiMain.start(guiArgs)
            val userResponse = guiMain.serializedOutput.get
            // val userResponse = "#BM"
            modelMain.main(Array(userResponse))
        catch
            case e: Throwable => e.printStackTrace()
            // finally onExit()

    /* def main(args: Array[String]): Unit =
        println("Hello World!")
        Application.launch(classOf[App], args: _*) */

    /**
     * See `ch.view.jfxuserform.Main.silenceBurningWaveLogsAfterStageClose()` for
     * more details.
     */
    // private def onExit(): Unit = jfxuserform.Main.silenceBurningWaveLogsAfterStageClose()
