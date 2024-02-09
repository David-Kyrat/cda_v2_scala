package cda.dependencycheck

import scala.sys.process._
import language.postfixOps
import Colorify as c
import javafx.scene.paint.Color
import javafx.scene.text.Text
import cda.view.helpers.Nodes
import scala.collection.mutable.ArrayBuffer
import javafx.scene.text.FontWeight

/**
 * Dependency checker for CDA Project
 * @param deps list of dependencies to check for. Default is Vector("pandoc", "wkhtmltopdf")
 */
class DepChecker(deps: Vector[String] = Vector("pandoc", "wkhtmltopdf")):
    // empty ProcessLogger
    val slt = ProcessLogger(line => (), line => ())
    val defFontSize = 24
    val defCol = Color.BLACK
    def txt(text: String, fontSize: Int = defFontSize, color: Color = defCol, fontWeight: Int = 400) = Nodes.newTxt(text, color, fontSize, fontWeight)

    def red(text: String, fontSize: Int = defFontSize, color: Color = Color.RED) = Nodes.newTxt(text, color, fontSize)
    def blue(text: String, fontSize: Int = defFontSize, color: Color = Color.BLUE) = Nodes.newTxt(text, color, fontSize)
    def green(text: String, fontSize: Int = defFontSize, color: Color = Color.GREEN) = Nodes.newTxt(text, color, fontSize)

    def bold(text: String, color: Color = defCol) = txt(text, color = color, 800)
    def underline(text: String, color: Color = defCol, fontWeight: Int = 400) = Nodes.withAction(txt(text, color = color, fontWeight), _.setUnderline(true))
    def dep(text: String, fontSize: Int = defFontSize) = bold(text, Color.BLUE)

    private def errMsg(name: String) = name + " isn't installed or is not in $PATH (can't be found)"
    private val ok = "\u2705"
    private val notok = "\u274C"

    private def checkDep(name: String): (Array[Text], Boolean) =
        // println(f"     Checking for ${c.dep(name)}...")
        val succ = f"which $name" ! slt == 0
        val texts = new ArrayBuffer[Text]()
        // if !succ then println(f" $notok  ${c.red(errMsg(name))} \n     Please ensure it is installed.\n")
        // else println(f" $ok  ${c.dep(name)} found !\n")
        if !succ then
            texts += txt(f"\t$notok ") += red(errMsg(name)) += txt(" \n     Please ensure it is installed.\n\n")
            // defTxt(f" $notok  ${c.red(errMsg(name))} \n     Please ensure it is installed.\n")
            println(f" $notok  ${c.red(errMsg(name))} \n     Please ensure it is installed.\n")
        else
            // texts += txt(f"\t$ok ") += Nodes.newTxt(name, Color.BLUE, defFontSize, FontWeight.EXTRA_BOLD) += txt(" found !\n\n")
            texts += txt(f"\t$ok ") += dep(name) += txt(" found !\n\n")
            // println(f" $ok  ${c.dep(name)} found !\n")
        (texts.toArray, succ)

    /** @return whether any of the depency is missing. True if they're all present. */
    def checkDeps: (List[Text], Boolean) =
        val textsAll = new ArrayBuffer[Text]()
        textsAll += txt("       ========== Checking for installed Dependecy ==========    \n\n")

        // println("============== Checking for installed Dependecy ==============\n")
        if "which which" ! slt != 0 then
            // println(f" ${c.bold(c.underline("which"))} must be installed to check if a program is installed.")
            return (List[Text](underline("\twhich", fontWeight = 800), txt(" must be installed to check if a program is installed")), false)

        val (txts, succs) = deps map checkDep unzip
        val succ = succs forall identity
        textsAll ++= txts flatMap identity
        // println("===================================================\n")
        textsAll += txt("      ================================================\n")
        (textsAll.toList, succ)

    /** Check for dependencies and exit if any is missing. (calls `this.checkDeps` and exit with exit code 1 when false was returned) */
    // def checkDepsOrExit(): Unit =
    //     if !checkDeps then System.exit(1)
    //     println("All dependencies are installed and usable continuing program...\n")

//
// else println(f"\t${c.dep(name)} found at " + c.underline((f"which $name" !! slt).strip) + " !\n") no need display where it is instalelled

