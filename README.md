# Fancy-Login-Form

Rebuilt in scala for better integration with Course-Description-Automation project.

**NB**: This project is simply the GUI for the [Course-Description-Automation](https://github.com/David-Kyrat/Course-Description-Automation) project.
It was before "hidden" in a jar in the resource directory that was "manually" launched with shell command.  
However:
 - this is obviously not best practice, as it is hard to maintain (because not modifiable).
 - when building  [Course-Description-Automation](https://github.com/David-Kyrat/Course-Description-Automation) from source, the gui needs to be packaged into a jar appart from the main project and moved to the resource directory. (Even though building from source is not required to run project, it is still nice to have.)

The configuration files *fli_scala.iml, .idea/* ... are left in this repository on purpose, to simplify further tinkering
of build configuration with IntelliJ.
The end project doesn't require Intellij, or any specific configuration file to run (aside of the Makefile of course).

---


