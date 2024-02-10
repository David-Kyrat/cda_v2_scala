package cda;

import cda.model.Utils
import cda.model.Utils.abbrevFilename
import cda.model.Utils.pathOf
import cda.model.Main as modelMain
import cda.view.jfxuserform
import javafx.application.Application
import javafx.application.Platform

import java.nio.file.Files
import java.nio.file.Path
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import cda.dependencycheck.DepChecker
import cda.view.jfxuserform.Main.globalStage
import javafx.scene.text.TextFlow
import cda.view.helpers.Nodes
import scala.jdk.CollectionConverters.*
import javafx.scene.text.Text

object App:
    var verbose = false
    val abbrevFilePath: Path = pathOf(abbrevFilename)
    val absAbbrevFilePath: String = abbrevFilePath.normalize().toString
    val depcheckedPath: Path = pathOf(".depchecked")
    val dependencies = Vector("pandoc", "wkhtmltopdf")

    private def checkDep(): Boolean =
        if !Files.exists(depcheckedPath) then
            var out = false
            val (txts, succ) = DepChecker(dependencies).checkDeps
            if succ then out = true
            ModalTextWindow(Nodes.newTextFlow(txts.asJava), 800, 500, title = "Checking if necessary program are installed...").startAndBlock()
            return out
        else
            println("Dependencies already checked")
            return true

    def main(args: Array[String]): Unit =
        try
            verbose = args.contains("verbose")
            val guiMain = new jfxuserform.Main()
            guiMain.initializeJavaFXToolkit()

            // System.exit(0)
            if !checkDep() then
                Platform.exit()
                System.exit(1) // if requirements are not met, exit

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
