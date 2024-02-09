package cda.dependencycheck

import scala.sys.process._
import language.postfixOps
import Colorify as c

/**
 * Dependency checker for CDA Project
 * @param deps list of dependencies to check for. Default is Vector("pandoc", "wkhtmltopdf")
 */
class DepChecker(deps: Vector[String] = Vector("pandoc", "wkhtmltopdf")):
    // empty ProcessLogger
    val slt = ProcessLogger(line => (), line => ())

    private def errMsg(name: String) = name + " isn't installed or is not in $PATH (can't be found)"
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
        if "which which" ! slt != 0 then
            println(f" ${c.bold(c.underline("which"))} must be installed to check if a program is installed.")
            return false

        val err = deps map (checkDep) forall identity
        println("===================================================\n")
        err

    /** Check for dependencies and exit if any is missing. (calls `this.checkDeps` and exit with exit code 1 when false was returned) */
    def checkDepsOrExit(): Unit =
        if !checkDeps then System.exit(1)
        println("All dependencies are installed and usable continuing program...\n")

//
// else println(f"\t${c.dep(name)} found at " + c.underline((f"which $name" !! slt).strip) + " !\n") no need display where it is instalelled
