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
import java.nio.file.Paths
import java.nio.file.StandardOpenOption.*
import scala.io.Source
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import cda.dependencycheck.DepChecker
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import javafx.stage.StageStyle
import cda.view.jfxuserform.Main.globalStage

object App:
    var verbose = false
    val abbrevFilePath: Path = pathOf(abbrevFilename)
    val absAbbrevFilePath: String = abbrevFilePath.normalize().toString
    val depcheckedPath: Path = pathOf(".depchecked")
    val dependencies = Vector("pandoc", "wkhtmltopdf")

    private def checkDep(): Boolean =
        if !Files.exists(depcheckedPath) then 
            val stdout = System.out
            val depCheckerBinOutput = new ByteArrayOutputStream();
            val ps = new PrintStream(depCheckerBinOutput, true, "UTF-8")
            // redirect stdout to depCheckerOutput
            System.setOut(ps);
            if !DepChecker(dependencies).checkDeps then return false 
            else Files.createFile(depcheckedPath)
            // collect output from depChecker
            val depCheckerStrOutput = depCheckerBinOutput.toString(UTF_8)
            System.setOut(stdout)
            // ModalTextWindow(depCheckerStrOutput).startJavaFxRuntime()
        else println("Dependencies already checked")
        return true

    def main(args: Array[String]): Unit =
        try
            verbose = args.contains("verbose")
            val guiMain = new jfxuserform.Main()
            guiMain.initializeJavaFXToolkit()
            val modalTextWindow = ModalTextWindow("Some \n new \n Text.").start()
            modalTextWindow.waitDone()

            // ModalTextWindow().start(new Stage(StageStyle.DECORATED))
            // Application.launch(classOf[ModalTextWindow], args: _*)
            // ModalTextWindow.launchApp("Test")
            // JavaFXInitializer.main(Array())

            // System.exit(0)
            // if !checkDep() then System.exit(1) // if requirements are not met, exit
            println("stuff")
            // System.exit(0)

            // guiMain.start(Array[String]())
            // launch gui and blocks until gui is closed
            val guiArgs =
                if Files.exists(abbrevFilePath) then Array(absAbbrevFilePath)
                else Array[String]()
            guiMain.start(guiArgs)
            val userResponse = guiMain.serializedOutput.get
            // val userResponse = "#BM"
            modelMain.main(Array(userResponse))
        catch
            case e: Throwable =>
                Utils.log(s"Error: ${e.getMessage()}\n${e.getStackTrace.mkString("\n\t")}")
                e.printStackTrace()
