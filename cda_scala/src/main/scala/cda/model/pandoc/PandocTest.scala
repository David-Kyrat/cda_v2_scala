package cda.model.pandoc

import cda.model.pandoc.PandocCommand._
import scala.annotation.allowConversions
import scala.collection.mutable.ArrayBuffer
import scala.sys.process._

object Timer:
    /**
     * Run block and print time
     *
     * @param block block to run
     * @return returned value
     */
    def time_and_result[R](block: => R): R = {
        val t0 = System.nanoTime()
        val result = block // call-by-name
        val t1 = System.nanoTime()
        val delta = (t1 - t0) / math.pow(10, 6)
        println("Average elapsed time: " + delta + "ms")
        result
    }

    def time[R](block: => R, print: Boolean = true): Double = {
        val t0 = System.nanoTime()
        val result = block // call-by-name
        val t1 = System.nanoTime()
        val delta = (t1 - t0) / math.pow(10, 6)
        // if (print) println("Elapsed time: " + delta + "ms")
        delta
    }

    /**
     * Run `num` times block and return time
     *
     * @param block block to run
     * @param num number of time to run block
     * @return average time (sec) for the `num` run
     */
    def time_average[R](block: => R, num: Int): Double = {
        var sum: Double = 0.0
        for i <- 0 until num do sum += time(block)
        val avg = (sum / num)

        println(f"\nElapsed time for $num runs: $avg ms.")

        return avg
    }

object PandocTest:
    import Timer._

    // WARNING: Waiting for wompletion to get their exit code (even in seperate threads)
    // completely defeats the purpose of launching the processes in background
    // => do not actually do that in production.
    /**
     * Output :
     * ```bash
     *
     * process scala.sys.process.ProcessImpl$SimpleProcess@70d2a3e      Exit value: 0
     *
     * process scala.sys.process.ProcessImpl$SimpleProcess@23031eab     Exit value: 0
     * Printing pages (2/2)
     * Done
     *
     * process scala.sys.process.ProcessImpl$SimpleProcess@57a7fcdb     Exit value: 0
     * ```
     */
    def test_extract_all_exit_codes_in_separate_threads() =
        val pandoc_path = "/usr/bin/pandoc"
        val pc = PandocCommand(pandoc_path, Vector("res/in1.md", "-o", "res/out1.pdf"), Vector("-t", "html", "in2.md", "-o", "out2.pdf"), Vector("-f", "latex", "in3.md", "-o", "out3.pdf"))
        println(pc)
        println("------")
        waitAllInNewThread(pc.launch_1by1_background())

    // PERF: Fast => don't use para collection
    def test_time_1by1_background() =
        val pc = PandocCommand(
            Vector("res/in1.md", "-o", "res/out1.pdf"),
            Vector("-t", "html", "res/in2.md", "-o", "res/out2.pdf"),
            Vector("-f", "latex", "res/in3.md", "-o", "res/out3.pdf"),
            Vector("-t", "html", "res/in4.md", "-o", "res/out4.pdf"),
            Vector("-t", "html", "res/in5.md", "-o", "res/out5.pdf"),
            Vector("res/in1.md", "-o", "res/out6.pdf"),
            Vector("-t", "html", "res/in2.md", "-o", "res/out7.pdf"),
            Vector("-f", "latex", "res/in3.md", "-o", "res/out8.pdf"),
            Vector("-t", "html", "res/in4.md", "-o", "res/out9.pdf"),
            Vector("-t", "html", "res/in5.md", "-o", "res/out10.pdf"),
            Vector("res/in1.md", "-o", "res/out11.pdf"),
            Vector("-t", "html", "res/in2.md", "-o", "res/out12.pdf"),
            Vector("-f", "latex", "res/in3.md", "-o", "res/out13.pdf"),
            Vector("-t", "html", "res/in4.md", "-o", "res/out14.pdf"),

            Vector("-t", "html", "res/in5.md", "-o", "res/out15.pdf")
        )
        // time_average({ waitAllInNewThread(pc.launch_1by1_background()) }, 5)
        time_average({ waitAllInNewThread(pc.exec()) }, 50)

    // PERF: really bad => Although doubling input incrase time by only 10% but base value is still way too high

    def test_time_full_1by1_bg = PandocCommand(
        Vector("res/in1.md", "-o", "res/out1.pdf"),
        Vector("-t", "html", "res/in2.md", "-o", "res/out2.pdf"),
        Vector("-f", "latex", "res/in3.md", "-o", "res/out3.pdf"),
        Vector("-t", "html", "res/in4.md", "-o", "res/out4.pdf"),
        Vector("-t", "html", "res/in5.md", "-o", "res/out5.pdf"),
        Vector("res/in1.md", "-o", "res/out6.pdf"),
        Vector("-t", "html", "res/in2.md", "-o", "res/out7.pdf"),
        Vector("-f", "latex", "res/in3.md", "-o", "res/out8.pdf"),
        Vector("-t", "html", "res/in4.md", "-o", "res/out9.pdf"),
        Vector("-t", "html", "res/in5.md", "-o", "res/out10.pdf"),
        Vector("res/in1.md", "-o", "res/out11.pdf"),
        Vector("-t", "html", "res/in2.md", "-o", "res/out12.pdf"),
        Vector("-f", "latex", "res/in3.md", "-o", "res/out13.pdf"),
        Vector("-t", "html", "res/in4.md", "-o", "res/out14.pdf"),
        Vector("-t", "html", "res/in5.md", "-o", "res/out15.pdf")
    ).exec()

    /**
     * Convert 1 md from Course-Description-Automation (CDA) to pdf
     * with the complex ndoc PandocCommand
     * `pandoc res/input.md -t html5 --template=template.html  --pdf-engine wkhtmltopdf  -V margin-top=2 -V margin-left=3 -V margin-right=0 -V margin-bottom=0 --css course-desc.css -o res/output.pdf`
     *
     * @return
     */
    def test_cda_convert_1time() =
        val cmd =
            "desc-2022-11X001.md -t html5 --template=template.html  --pdf-engine wkhtmltopdf  -V margin-top=2 -V margin-left=3 -V margin-right=0 -V margin-bottom=0 --css course-desc.css -o res/output.pdf"
        PandocCommand(toVec(cmd)).!

    def test_cda_convert_2time() =
        val cmd =
            "desc-2022-11X001.md -t html5 --template=template.html  --pdf-engine wkhtmltopdf  -V margin-top=2 -V margin-left=3 -V margin-right=0 -V margin-bottom=0 --css course-desc.css -o 11X001.pdf"

        val cmd2 =
            "desc-2022-12X001.md -t html5 --template=template.html  --pdf-engine wkhtmltopdf  -V margin-top=2 -V margin-left=3 -V margin-right=0 -V margin-bottom=0 --css course-desc.css -o 12X001.pdf"
        PandocCommand(toVec(cmd), toVec(cmd2)).!
