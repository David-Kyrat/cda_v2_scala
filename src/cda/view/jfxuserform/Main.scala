package cda.view.jfxuserform

import cda.model.Utils.{pathOf, abbrevFilename}
import cda.App.abbrevFilePath
import javafx.application.Application
import javafx.scene.image.Image
import javafx.stage.Stage
import cda.view.jfxuserform.Main.{nullPrintStream, silenceBurningWaveLogsAfterStageClose}

import java.io.OutputStream.nullOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.StandardOpenOption.*
import java.nio.file.{Files, OpenOption, Path, StandardOpenOption}
import java.util
import java.util.List
import scala.io.Source
import scala.jdk.CollectionConverters.*
import java.nio.file.attribute.FileAttribute
import javafx.application.Platform
import cda.model.Utils

import java.util.concurrent.atomic.AtomicReference

class Main extends Application {
    private var jvmVersion: Int = 0
    private val ICONS_RES: util.List[String] = util.List.of("_x256")
    private val pattern: String = "jfxuserform/img/app-info-logo"

    private val stdout = System.out

    // only load img when called
    private def getIcons: util.List[Image] =
        ICONS_RES.stream.map((s: String) => new Image(getClass.getClassLoader.getResourceAsStream(pattern + s + ".png"), 256, 256, true, true)).toList

    private def addIconsToStage(stage: Stage): Unit = stage.getIcons.addAll(getIcons)

    /** This variable needs to be an AtomicReference because it is accessed from several threads:
     *  - The JavaFX Application Thread
     *  - And the thread that calls `Main.main()`
     *    Otherwise waiting in the main thread for the JavaFX Application to finish
     *    (by checking in a loop if `stage != null`) holds (i.e. never ends)
     */
    private var stageRef = new AtomicReference[ChoosingStage](null)

    private def stage = stageRef.get()

    /**
     * @return Serialized output to give to scala `Model.Main.main()` or `None` if `stage` or `stage.getSerializedOutput` is null
     */
    def serializedOutput: Option[String] = stage match {
        case null => None
        case _ => Option(stage.getSerializedOutput)
    }

    /**
     * @return Serialized output to give to scala `Model.Main.main()`
     */
    // def getSerializedOutput(): String = stage.getSerializedOutput()

    /**
     * Downloads the abbreviations file if it doesn't exist.
     *
     * @param url github "raw" url to the abbreviations file
     *            i.e. of the form `https://raw.githubusercontent.com/Name/Repo/branch/path/to/file`
     * @return Absolute path to the abbreviations file (as string)
     */
    private def dlAbbrevFileIfNotExist(url: String): String = {
        println("Downloading abbreviations file from " + url)
        val source = Source.fromURL(url)
        val path = Utils.pathOf("abbrevs.tsv")
        Files.writeString(path, source.mkString, UTF_8, CREATE, WRITE, TRUNCATE_EXISTING)
        source.close()
        path.toAbsolutePath.toString
    }

    /**
     * Starts the JavaFx Application
     *
     * @param primaryStage Can (should) be null
     *                     i.e. parameter isn't taken into account but is required to override
     *                     `Application.start()`
     */
    override def start(primaryStage: Stage): Unit = {
        if (primaryStage != null) primaryStage.close()
        // unique argument is the path to the abbreviations file
        val url = "https://raw.githubusercontent.com/David-Kyrat/Course-Description-Automation/master/files/res/abbrev.tsv"
        val abbrevFilePath = getParameters.getRaw.asScala.lastOption.getOrElse(dlAbbrevFileIfNotExist(url))
        stageRef.set(new ChoosingStage("Course Description Automation", abbrevFilePath))
        addIconsToStage(stage)
        System.setOut(stdout) // enable back console output
        stage.startAndShow()
    }

    /**
     * temp method to start the JavaFx App "manually" from App.scala => TODO: Find a better way to do this
     * needed because we need to access `this.serializedOutput` after exit of javafx app
     */

    /**
     * This method is used to start the JavaFX application manually.
     *  - It is a workaround to the standard JavaFX application lifecycle.
     *  - It is necessary because we need to access `this.serializedOutput` after the exit of the JavaFX application.
     *
     * @param args Arguments given on command line
     * @note **This method blocks until the JavaFX application has finished.**
     *       (i.e. until user filled the necessary data and clicked on the "Generate" button)
     */
    def start(args: Array[String]): Unit = {
        Platform.startup(() => {
            this.init()
            val url = "https://raw.githubusercontent.com/David-Kyrat/Course-Description-Automation/master/files/res/abbrev.tsv"
            val abbrevFilePath = args.lastOption.getOrElse(dlAbbrevFileIfNotExist(url))
            stageRef.set(new ChoosingStage("Course Description Automation", abbrevFilePath))
            addIconsToStage(stage)
            System.setOut(stdout) // enable back console output
            stage.startAndShow()
        })
        // Waits for the JavaFX application to finish
        while (serializedOutput.isEmpty) {}
    }

    override def init(): Unit = {
        super.init()
        System.setOut(nullPrintStream) // prevent Burningwave from flooding stdout with logs when JFX Apps starts
        jvmVersion = org.burningwave.core.assembler.StaticComponentContainer.JVMInfo.getVersion
    }

    override def stop(): Unit = silenceBurningWaveLogsAfterStageClose()

    /**
     * Exits JavaFx Application thread, and call the overidden `stop()` method from `javafx.Application`
     * (needs to be done manually when using an instance of this was used launch it instead of `Application.launch`)
     */
}

object Main {
    val nullPrintStream = PrintStream(nullOutputStream())

    /**
     * Silences stdout and stderr after JFXApplication has finished to
     * prevent org.Burningwave.* from flooding stdout & stderr with logs when JVM exits
     */
    def silenceBurningWaveLogsAfterStageClose(): Unit = {
        System.setOut(nullPrintStream)
        System.setErr(nullPrintStream)
    }

    def main(args: Array[String]): Unit = {
        Application.launch(classOf[Main], args: _*)
        silenceBurningWaveLogsAfterStageClose()
        // return serializedOutput;
    }

}
