package cda.model.pandoc

import scala.sys.process._
import language.postfixOps

/**
 * Dependency checker for CDA Project
 */
object CDADepChecker:
    import Colorify as c
    // empty ProcessLogger
    val slt = ProcessLogger(line => (), line => ())
    private val deps = Vector("pandoc", "wkhtmltopdf", "notinstalled", "ls")

    private def errMsg(name: String) = name + " isn't installed or is not in $PATH"
    private val ok = "\u2705"
    private val notok = "\u274C"

    private def checkDep(name: String): Boolean =
        println(f"     Checking for ${c.dep(name)}...")
        val succ = f"which $name" ! slt == 0
        if !succ then println(f" $notok  ${c.red(errMsg(name))} \n     Please ensure it is installed.\n")
        else println(f" $ok  ${c.dep(name)} found !\n")
        succ

    /** @return whether any of the depency is missing. True if they're all present. */
    def checkDeps: Boolean =
        println("============== Checking for installed Dependecy ==============\n")
        if "whichh --help" ! slt != 0 then
            println(f"Cannot locate if a program is installed if you do not have ${c.bold(c.underline("wich"))} installed.")
            return false

        val err = deps map (checkDep) forall identity
        println("==============================================================\n")
        err

    def runDepTest(): Unit =
        if !checkDeps then System.exit(1)
        println("All dependencies are installed and usable continuing program...\n")

//
// else println(f"\t${c.dep(name)} found at " + c.underline((f"which $name" !! slt).strip) + " !\n") no need display where it is instalelled
