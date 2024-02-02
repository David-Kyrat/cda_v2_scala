package cda

import cda.model.Main as modelMain
import cda.model.Utils.{abbrevFilename, pathOf}
import cda.model.pandoc.PandocCommand.toVec
import cda.model.pandoc.Timer.{time_and_result, time_average}
import cda.model.pandoc.{Colorify, PandocCommand}
import cda.view.jfxuserform
import javafx.application.{Application, Platform}
import java.nio.file.Files

import java.nio.file.Path

// j.Main.main(args) launches gui
object App {
    var verbose = false
    // to get abbrevFile with resourceAsStream we need to access /res/abbrev.tsv. hence replacing files/res/abbrev.tsv by /res/abbrev.tsv
    val abbrevFilePath: Path = pathOf(abbrevFilename)
    val absAbbrevFilePath: String = abbrevFilePath.normalize().toString

    def main(args: Array[String]): Unit = {
        try
            verbose = args.contains("verbose")
            val y = App.getClass.getResourceAsStream("/res/abbrev.tsv")
            val y1 = App.getClass.getResourceAsStream("/res/templates/course-desc.css") 
            val y2 = App.getClass.getResourceAsStream("/res/templates/template.html") 
            val y3 = App.getClass.getResourceAsStream("/res/templates/unige.png")
            println(s"App.getClass.getResourceAsStream('/res/abbrev.tsv') is null: ${y == null}")
            println(s"App.getClass.getResourceAsStream('/res/templates/course-desc.css') is null: ${y1 == null}")
            println(s"App.getClass.getResourceAsStream('/res/templates/template.html  ') is null: ${y2 == null}")
            println(s"App.getClass.getResourceAsStream('/res/templates/unige.png ') is null: ${y3 == null}")
            println("----")
            val guiMain = new jfxuserform.Main()
            // launch gui and blocks until gui is closed
            val guiArgs = Files.exists(abbrevFilePath) match
                case true  => Array(absAbbrevFilePath)
                case false => Array[String]()
            guiMain.start(guiArgs)
            val userResponse = guiMain.serializedOutput.get
            // FIXME: Even when setting PandocCommand.exec(silent=false)
            //    stderr is not printed to console. i.e. nothing tells us whether pandoc failed, and if it did => no way of knowing why
            // val userResponse = "11M020,11M010,12m061,11X001#"
            modelMain.main(Array(userResponse))
        catch case e: Throwable => e.printStackTrace()
        finally {
            onExit()
        }
    }

    /**
     * See `ch.view.jfxuserform.Main.silenceBurningWaveLogsAfterStageClose()` for more details.
     */
    private def onExit(): Unit = jfxuserform.Main.silenceBurningWaveLogsAfterStageClose()
}
