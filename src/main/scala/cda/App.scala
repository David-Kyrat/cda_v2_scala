package cda;

import cda.model.Utils
import cda.model.Utils.abbrevFilename
import cda.model.Utils.pathOf
import cda.model.io.Serializer
import cda.model.Main as modelMain
import cda.view.jfxuserform
import com.jfoenix.controls.JFXTextField
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.stage.Stage

import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.PrintWriter
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
import cda.view.jfxuserform.Main.globalStage
import javafx.scene.text.TextFlow
import cda.view.helpers.Nodes
import collection.mutable.*
import scala.jdk.CollectionConverters.*
import java.util.Arrays
import javafx.scene.text.Text

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
            // System.setOut(ps);
            val (txts, err) = DepChecker(dependencies).checkDeps
            if err then return false
            // else Files.createFile(depcheckedPath)
            // collect output from depChecker
            // val depCheckerStrOutput = depCheckerBinOutput.toString(UTF_8)
            // System.setOut(stdout)
            for (txt <- txts) {
                println(txt.getText())
            }
            val tf = Nodes.newTextFlow(txts.asJava)
            ModalTextWindow(tf, 800, 800).startAndBlock()
            // ModalTextWindow(depCheckerStrOutput, 800, 800).startAndBlock()
        else println("Dependencies already checked")
        return true

    def main(args: Array[String]): Unit =
        try
            verbose = args.contains("verbose")
            val guiMain = new jfxuserform.Main()
            guiMain.initializeJavaFXToolkit()

            // System.exit(0)
            if !checkDep() then System.exit(1) // if requirements are not met, exit

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
