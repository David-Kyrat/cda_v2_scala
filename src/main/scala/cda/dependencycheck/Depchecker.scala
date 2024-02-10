package cda.dependencycheck

import scala.sys.process._
import language.postfixOps
import Colorify as c
import javafx.scene.paint.Color
import javafx.scene.text.Text
import cda.view.helpers.Nodes
import scala.collection.mutable.ArrayBuffer
import javafx.scene.text.FontWeight
import javafx.scene.text.Font
import javafx.scene.text.TextFlow
import javafx.scene.Node
import javafx.scene.layout.VBox
import javafx.scene.layout.HBox
import javafx.scene.image.ImageView
import javafx.scene.image.Image

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

    def red(text: String, fontSize: Int = defFontSize, color: Color = Color.RED) = txt(text, fontSize, color)
    def blue(text: String, fontSize: Int = defFontSize, color: Color = Color.BLUE) = txt(text, fontSize, color)
    def green(text: String, fontSize: Int = defFontSize, color: Color = Color.GREEN) = txt(text, fontSize, color)

    def bold(text: String, color: Color = defCol) = txt(text, color = color, 1000)
    def underline(text: String, color: Color = defCol, fontWeight: Int = 400) = Nodes.withAction(txt(text, color = color, fontWeight), _.setUnderline(true))
    def dep(text: String, fontSize: Int = defFontSize) = bold(text, Color.BLUE)

    private def errMsg(name: String) = name + " isn't installed or is not in $PATH (can't be found)"
    private val ok = "\u2705"
    private val notok = "\u274C"

    private def checkDep[T >: Node](name: String): (ArrayBuffer[T], Boolean) =
        val texts = new ArrayBuffer[T]()
        texts += txt("     \tChecking for ") += dep(name) += txt("...\n")
        val succ = f"which $name" ! slt == 0

        if !succ then texts += txt(f"\t\t$notok ") += red(errMsg(name)) += txt(" \n     Please ensure it is installed.\n\n")
        // else texts += txt(f"\t\t$ok ") += dep(name) += txt(" found !\n\n")
        else
            val okb = HBox(
                10,
                txt("\t   "),
                new ImageView(
                    new Image("https://upload.wikimedia.org/wikipedia/commons/thumb/b/b1/Antu_mail-mark-notjunk.svg/1920px-Antu_mail-mark-notjunk.svg.png", 40, 40, true, true)
                ),
                dep(name),
                txt(" found !\n\n")
            )
            texts += okb
        (texts, succ)

    /** @return whether any of the depency is missing. True if they're all present. */
    def checkDeps[T >: Node]: (List[T], Boolean) =
        val textsAll = new ArrayBuffer[T]()
        textsAll += txt("       ========== Checking for installed Dependecy ==========    \n\n")

        if "which which" ! slt != 0 then
            return (
                List[T](
                    HBox(
                        20,
                        txt("  "),
                        new ImageView(new Image("https://upload.wikimedia.org/wikipedia/commons/thumb/f/ff/Warning_Emoji.svg/768px-Warning_Emoji.svg.png", 50, 50, true, true))
                    ),
                    txt("   "),
                    underline("\"which\"", fontWeight = 800),
                    txt("  command must be installed to check\n\t if a program is installed...\n\t Cannot check if necessary programs are installed,\n\t generation might fail.")
                ),
                true // ignore dep checking
            )

        val (txts, succs) = deps map checkDep unzip
        val succ = succs forall identity
        textsAll ++= txts flatMap identity
        textsAll += txt("      ================================================\n")
        (textsAll.toList, succ)
