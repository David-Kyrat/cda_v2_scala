package cda.model.pandoc


import scala.collection.mutable.ArrayBuffer
import scala.sys.process.*
import cda.model.pandoc.PandocTest as pt
import cda.model.pandoc.Timer.*
import cda.model.pandoc.PandocTest.test_cda_convert_1time
import scala.language.postfixOps

/**
 * @param pandoc_path - Absolute path to pandoc executable.
 * @param cmds        , `ArrayBuffer[Vector[String]]` - Buffer of cmd to run.
 *                    Private to keep class immuatble.
 *                    Mutable buffer of commands each commands will be preceded by <pandoc_path>
 *                    i.e. PandocCommand(ArrayBuffer(
 *                    Vector("-t", "html", "input.md", "-o", "output.pdf"),
 *                    Vector("-t", "tex", "input.md", "-o", "output.pdf"),
 *                    ))
 *
 *                    Once called `execute()` method will perform
 *                    ```bash
 *                    path_to_pandoc -t html input.md -o output.pdf &
 *                    path_to_pandoc -t tex input.md -o output.pdf &
 *                    # background jobs to allow them to run concurrenrly
 *                    ```
 *
 *                    NB: apply method takes an Int and return the cmd at that index
 */
case class PandocCommand(val pandoc_path: String, private val cmds: ArrayBuffer[Vector[String]]) extends (Int => Option[Vector[String]]):
    // WARN: can't use 1 fg process to launch the `N` bg process => so we're launching them sequentially (for loop on `cmds`)
    //      but they're still run as bg proc.
    private val before_each_cmd = f"$pandoc_path " // for mkString of each vector
    private val after_each_cmd = " &" // bg process
    // for mkstring of ArrayBuffer.
    private val between_all_cmd = " "

    /**
     * Add a command to the buffer of command.
     *
     * <pandoc_path> will be prepend to `cmd` at execution, only give a list of arguments
     *
     * @param cmd Vector[String] list of argument to pandoc command
     */
    def add(cmd: Vector[String]) = cmds.addOne(cmd)

    /**
     * @return command at index `idx`-th if it exist, otherwise return Option.None
     */
    override def apply(idx: Int): Option[Vector[String]] = cmds.lift(idx)

    /**
     * Create **1** [[scala.sys.process.ProcessBuilder]] that will execute all the commands
     * in 1 process (usually as a background jobs)
     *
     * @return corresponding proccessBuilder
     */
    def toProcessBuilder() =
        println()

    /**
     * Create **N** [[scala.sys.process.ProcessBuilder]] 1 for each command that can
     * be comnbined or run independently later one
     *
     * @return list of proccessBuilders
     */
    def toProcessBuilders() =
        println()

    // TODO: update doc with the one from launch_1by1_background
    /*
     * Execute each command in `cmds` as background shell processes.
     *
     * i.e. if `cmds` == ArrayBuffer(
     * Vector("-t", "html", "input.md", "-o", "output.pdf"),
     * Vector("-t", "tex", "input.md", "-o", "output.pdf"),
     * ))
     *
     * this method calls 1 processbuilder to run 1 command constitued of :
     * ```bash
     * <path_to_pandoc> -t html input.md -o output.pdf &
     * <path_to_pandoc> -t tex input.md -o output.pdf &
     * ```
     */
    def exec(silent: Boolean = true) =
        // if (ch.Main.VERBOSE) println(f"\n cmds:\n$this")
        if silent then
            val processLogger = ProcessLogger(_ => (), _ => ())
            cmds.map(pandoc_path +: _ run processLogger).toVector
        else
            // cmds.map(vec => (pandoc_path +: vec).run()).toVector
            cmds.map(vec => (pandoc_path +: vec) run) toVector

    def ! = exec()

    def !! = exec(false)

    // PERF: Slow

    /**
     * Execute processes 1 by 1 using the implicit conversion from vector of param/argument to Process.
     * (From the doc: "Here we use a `Seq` to make the parameter whitespace-safe").
     * i.e. `cmds.map(vector => (pandoc_path +: vector).!!)`
     */
    def exec_1by1() = cmds.map(pandoc_path +: _ !!)

    // PERF: Doesn't do what was intended. i.e. 1 process spawning N bg pandoc processes. But works fast & well nontheless.
    // Plus that way we keep a handle on each process. I.e. we create a `sys.process.Process` for each and we can collect their exit code (only) if necessary. => Bothersome => need to wait / check when they're all done.

    /**
     * Launches processes 1 by 1 using the implicit conversion from vector of param/argument to Process.
     * (From the doc: "Here we use a `Seq` to make the parameter whitespace-safe").
     * i.e. `cmds.map(vector => (pandoc_path +: vector).run())`
     *
     * i.e. Doesn't really run them 1by1. They are ***launched*** 1 by 1, `cmds.length` process are created. But then they're ran concurrently.
     *
     * See scaladoc:
     * > "`run()`: the most general method,
     * > it returns a `scala.sys.process.Process` immediately,
     * > and the external command executes concurrently."
     *
     * @return Vector of handle to each launched Processes
     */
    def launch_1by1_background(): Vector[Process] =
        // NB: ignores output remarkably well
        val processLogger = ProcessLogger(line => (), line => ())
        cmds.map(pandoc_path +: _ run processLogger).toVector

    /**
     * Does the same thing as [[launch_1by1_background]] but in parallel.
     *
     * returned array is mutable (although of fixed size).
     * Would immutability really serve a purpose here ?
     * It's not like mutating the returned array would change
     * anything at how the background process would run. Plus
     * ParArray seems pretty optimized
     *
     * @return Parallel Array of handle to each launched Processes
     */
    /*    def launch_par_background() =
            val processLogger = ProcessLogger(line => (), line => ())
            cmds.toVector.par.map(pandoc_path +: _ run processLogger)
     */
    override def toString(): String = cmds
        .map(_.mkString(before_each_cmd, " ", after_each_cmd))
        .mkString("", f"$between_all_cmd \\\n", "\n")

//
// ============= OBJECT ================
//

object PandocCommand:
    /**
     * Just runs `which pandoc`
     *
     * @return output of `which pandoc`
     */
    def extractPandocPath = "which pandoc".!!.strip

    def apply(pandoc_path: String, cmds: Vector[String]*) = new PandocCommand(pandoc_path, cmds.to(ArrayBuffer))

    def apply(cmds: Vector[String]*) = new PandocCommand(extractPandocPath, cmds.to(ArrayBuffer))
    def apply(cmds: ArrayBuffer[Vector[String]]) = new PandocCommand(extractPandocPath, cmds)

    /* def apply(cmds: String*): PandocCommand =
        val tmp = cmds.map(_.strip.split(" ").toVector).to(ArrayBuffer)
        new PandocCommand(extractPandocPath, tmp) */
    // def toVecs(cmds: String*) =

    def toVec(cmd: String) = cmd.strip.split(" ").map(_.strip).filterNot(_.isBlank).toVector

    def toArrayBuf(cmds: String*) = cmds.map(toVec).to(ArrayBuffer)

    /**
     * Creates a new thread by process to wait on each one concurrently and get their exit code
     *
     * @param processes list of processes to extract exit code from
     */
    def waitAllInNewThread(processes: IndexedSeq[Process]) =
        // val ts = ArrayBuffer[Thread]() // thread to wait for completion (wait for completion of all but they still run concurrently, at least thats the intentded behavior).

        processes.foreach(proc =>
            val x = new Thread(() =>
                // create new thread to wait on current process and get its exit code
                val exitCode = proc.exitValue()
                // println(f"\n\nprocess $proc\t Exit value: $exitCode")
            ) // .start()
            // ts += x
            x.start()
        )

    /**
     * Not actual testing suite, just basic running use case to see the output
     */
    def runTests() =
        import cda.model.pandoc.PandocTest as Pt
        println("\n")
        pt.test_time_1by1_background()
        /* pt.test_extract_all_exit_codes_in_separate_threads()
        time_average({ pt.test_extract_all_exit_codes_in_separate_threads() }, 25)
        pt.test_with_par_array() */
        // pt.test_time_1by1_background()
        /* time_average(
            {
                pt.test_time_full_1by1_bg
            },
            5
        ) */
        // test_cda_convert_1time()

        // pt.test_cda_convert_2time()
        // pt.test_time_1by1_background()
