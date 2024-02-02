package cda.model.io

import scala.collection.immutable.ArraySeq
import scala.collection.parallel.ParIterable

import java.io.{BufferedWriter, FileWriter, PrintWriter}
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Path
import cda.model.{Course, Utils}

import cda.model.Main.VERBOSE
import cda.model.pandoc.PandocCommand
import scala.collection.mutable.ArrayBuffer

object Serializer {
    // Begin and end symbol of yaml header in markdown file
    val yamlHeaderSep = "---"
    val resDirpath = "files/res/"
    val mdDir = f"$resDirpath/md"
    val pdfDir = f"$resDirpath/pdf"
    val templatePath = f"$resDirpath/templates"
    private val cssFile = f"$templatePath/course-desc.css"
    private val htmlTemplate = f"$templatePath/template.html"

    /**
     * Pandoc opts except input and output
     * @param htmlTemplate Path to the html template that pandoc will use to convert the
     * data in the markdown file to the final pdf we want
     * @param cssFile Path to the css file styling the pandoc template
     */
    private val pandocOpts = Vector(
        f"--template=$htmlTemplate",
        "--pdf-engine",
        "wkhtmltopdf",
        "-V",
        "margin-top=0",
        "-V",
        "margin-left=3",
        "-V",
        "margin-right=0",
        "-V",
        "margin-bottom=0",
        "--css",
        cssFile,
        "-o"
    )

    /**
     * @param mdInput name of markdown file to convert
     * @return Arguments to pass to pandoc as a vector e.g. `in.md -t html5 --template... -o output.pdf`
     */
    private def pandocArgs(mdFileName: String) = {
        val pdfName = mdFileName.replace(".md", ".pdf")
        f"$mdDir/$mdFileName" +: pandocOpts :+ f"$pdfDir/$pdfName"
    }

    private def mdFileName(course: Course) =
        f"desc-${course.year}-${course.id}.md"

    // def mdToPdf_(courses: ParIterable[Course]): PandocCommand = mdToPdf(courses.map(mdFileName))

    /**
     * Takes in several markdown filename and converts each one to a pdf.
     * Use pandoc to fill an html template with the info in the corresponding markdown file
     * and converts it directly to pdf.
     * Launch each pandoc conversion as a background process until there's no more filename in `mdFileNames`
     * @param mdFileNames ParIterable[String] Any parallel collection containing the name of markdown file
     */
    def mdToPdf(mdFileNames: ParIterable[String]): Unit =
        PandocCommand(mdFileNames.map(pandocArgs).to(ArrayBuffer)).! : Unit

    /**
     * Escapes, in the given string, characters that actually that have a special meaning in Yaml.
     * (e.g. "-" is a reserved character to indicate the begining of a list/enumeration)
     *
     * @param ymlStmt
     * @return
     */
    def sanitizeForYaml(ymlStmt: String): String = {
        ""
    }

    /**
     * Format given parameters in a yaml fmt i.e. "key: value"
     * @param key String
     * @param value String
     */
    def yamlFmt[T](key: String, value: T): String = f"$key: \"$value\""

    // unused
    def yamlFmTOpt[T](key: String, value: Option[T]) =
        value match {
            case Some(t) => yamlFmt(key, value)
            case None    => ""
            // case Some(t) => f"$key: \"$t\""
        }

    /**
     * Writes optional fields of given course to given writer.
     * i.e. Do nothing if `Option` is None and writes the "regular"
     * serialization on extracted object to `wr` if it is `Some`
     *
     * @param wr `BufferedWriter` to write to
     * @param course course to extract optional fields from
     */
    def yamlWriteCourseOpt(br: BufferedWriter, course: Course) = {
        val keyOptValuePair: ArraySeq[(String, Object)] = ArraySeq(("prerequisite", course.prerequisites), ("various", course.various), ("comments", course.comments))
        keyOptValuePair.map(pair =>
            pair._2 match {
                case Some(value) => write(br, yamlFmtMultiLineStr(pair._1, value.toString))
                case None        => ()
            }
        )
    }

    /**
     * Format given parameters in a yaml fmt i.e. "key: value",
     * where value is a string over several lines (i.e. with '\n' characters in it)
     * @param key String
     * @param value String
     */
    def yamlFmtMultiLineStr(key: String, value: String) = {
        val sbld = new StringBuilder(f"$key:  |\n")
        val indent = " " * (f"$key:  ".length) // indentation to respect to have correct yaml syntax
        sbld ++= indent
        val lines = value.strip().replace("\n", f"\n$indent")
        sbld ++= lines
        sbld.toString
    }

    def yamlFmtCursus(course: Course) = {
        val map = course.studyPlan
        val sbld = new StringBuilder("cursus:\n")
        val credFmt: (Float) => String = c => if (c <= 0) "-" else c.toString // if credits = 0 write a "-" instead
        map.foreach(kv => sbld ++= f"  - {name: \"${kv._1}\", type: \"${kv._2._2}\", credits: \"${credFmt(kv._2._1)}\"}\n")
        sbld.toString
    }

    /** Buffered writing */
    private def write(br: BufferedWriter, content: String) = { br.write(content + "\n") }

    /** Buffered writing */
    private def writes(br: BufferedWriter, contents: String*) = for (s <- contents) write(br, s)
    private var print_flag = 0

    /**
     * Serialize a `Course` into a markdown file that can be used to fill
     * the html course-description template.
     *
     * The syntax of those specific markdown file is :
     *  - a yaml header
     *  - empty "body"
     *
     * @param course Course to serialize
     * @return name of Markdown file
     */
    def courseToMarkdown(course: Course): String = {
        // val name = f"desc-${course.year}-${course.id}.md"
        val name = mdFileName(course)
        val path = Utils.pathOf(f"md/$name")
        if (VERBOSE) {
            this.synchronized {
                if (print_flag <= 0) {
                    val tmp = Utils.pathOf("<some_resource>")
                    println(f"  ---- Saving ${course.id} to ${path.toAbsolutePath.normalize()}\n Because Utils.pathOf('<some_resource>') gives ${tmp.toAbsolutePath.normalize()}\n")
                    print_flag += 1
                }
            }
        }

        val br = new BufferedWriter(new FileWriter(path.toAbsolutePath.toString, UTF_8))
        def write(content: String) = br.write(content + "\n")
        def writes(contents: String*) = for (s <- contents) write(s)

        write(yamlHeaderSep)
        writes(
            yamlFmt("title", course.title),
//            yamlFmt("author", course.authors.mkString(", ") + f"  -  ${course.id}"),
//            yamlFmt("author", f"${course.id} | " + course.authors.mkString(", ")),
            yamlFmt("author", course.authors.mkString(", ")),
            yamlFmt("course_code", course.id),
            yamlFmt("weekly_hours", course.hoursNb.sum),
            yamlFmt("lectures_hours", course.hoursNb.lectures),
            yamlFmt("exercices_hours", course.hoursNb.exercices),
            yamlFmt("total_hours", course.hoursNb.semesterSum),
            yamlFmt("course_lang", course.language),
            yamlFmt("semester", course.semester),
            yamlFmt("eval_mode", course.evalMode),
            yamlFmt("exa_session", course.semester.session),
            yamlFmt("course_format", course.format.replace("-", "-")),
            yamlFmtCursus(course),
            yamlFmtMultiLineStr("objective", Utils.sanitize(course.objective)),
            yamlFmtMultiLineStr("description", Utils.sanitize(course.description))
        )
        val ch = course.hoursNb
        if (ch.seminaire > 0) {
            if (ch.practice > 0) write(yamlFmt("practice_hours", course.hoursNb.practice))
            val toWrite = yamlFmt("sem_hours", course.hoursNb.seminaire)
            write(toWrite)
        } else write(yamlFmt("practice_hours", course.hoursNb.practice))
        write(yamlHeaderSep)
        br.flush
        br.close

        name
    }
}
