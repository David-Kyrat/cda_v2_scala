package cda.dependencycheck

import scala.io.AnsiColor._

object Colorify:
    /** dep are bold green */
    def red(txt: String) = f"${RED}${txt}${RESET}"
    def blue(txt: String) = f"${BLUE}${txt}${RESET}"
    def green(txt: String) = f"${GREEN}${txt}${RESET}"
    def bold(txt: String) = f"${BOLD}${txt}${RESET}"
    def underline(txt: String) = f"${UNDERLINED}${txt}${RESET}"
    def dep(name: String) = bold(blue(name))
