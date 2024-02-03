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

    /**
     * Download file from remote url as raw bytes (useful to download images,
     * pdf...) i.e. do not use any method from scala.io are anything also that
     * automatically insert line endings and such
     *
     * @param url
     *   url of the file to download
     * @param path
     */
    def dlBinFile(url: String, path: String) =
        println()
    def gettemplatesContent() =
        /* val x = "/res/templates/template.html"
        val reader = new BufferedReader(new InputStreamReader(getClass.getResourceAsStream(x), UTF_8))
        // println(reader.lines().toList())
        Files.createDirectories(Path.of(cda.model.io.Serializer.templatePath))
        val tmp2 = Path.of("files/res/templates/template.html")
        val tmp = Files.newBufferedWriter(tmp2)
        val pw = new PrintWriter(tmp)
        reader.lines().forEach(pw.println(_))
        pw.close()
        reader.close() */
        // Serializer.extractTemplatesIfNotExists()
        val url =
            "https://github.com/David-Kyrat/Course-Description-Automation/raw/master/files/res/templates/unige.png"
        val filename = Serializer.templatePath + "/unige.png"
        try {
            val readableByteChannel = Channels.newChannel(URL(url).openStream());
            val fileOutputStream = new FileOutputStream(filename);
            val fileChannel = fileOutputStream.getChannel();
            fileChannel.transferFrom(readableByteChannel, 0, Long.MaxValue);
        } catch {
            case e: java.io.IOException => "error occured"
        }
        // val source = Source.fromURL(url)
        // val tmp = source.reader()
        // Files.write(Path.of(Serializer.templatePath + "/unige.png"),
        //     source., CREATE, WRITE, TRUNCATE_EXISTING)
        // source.close()

    def main(args: Array[String]): Unit =
        try
            verbose = args.contains("verbose")
            val guiMain = new jfxuserform.Main()
            // launch gui and blocks until gui is closed
            val guiArgs =
                if Files.exists(abbrevFilePath) then Array(absAbbrevFilePath)
                else Array[String]()
            // guiMain.start(guiArgs)
            // val userResponse = guiMain.serializedOutput.get
            // val userResponse = "#BM"
            // modelMain.main(Array(userResponse))
            gettemplatesContent()

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
