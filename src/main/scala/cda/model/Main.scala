package cda.model

import cda.model.Utils.pathOf
import cda.model.io.Serializer
import cda.model.net.Resp
import cda.model.net.exception.*
import cda.model.{Course, StudyPlan, Utils}
import cda.App.abbrevFilePath

import java.nio.file.{Files, Path}
import java.util.Locale
import scala.collection.parallel.CollectionConverters.*
import scala.collection.parallel.mutable.ParArray
import scala.collection.parallel.{ParIterable, ParSet}
import scala.util.{Failure, Success, Try}

object Main {
    def VERBOSE = cda.App.verbose

    // NB: lazy so value only get computed when needed

    /**
     * Contains association (for each studyPlan) of the form : `Abbreviation ->
     * (FullName, id)`
     */
    lazy val abbrevMap: Map[String, (Int, String)] = getAbbrevMap
    lazy val usageMsg =
        "Usage: course_description_automation.jar \"[<course_codes>]#[<study_plan_codes>]\" ('#' is not optional)"

    def writeCoursDescToRes(id: String) =
        Utils.write(pathOf(f"$id-desc.json"), Resp.prettify(Course.get(id)))

    def writeSpDescToRes(id: String) =
        Utils.write(pathOf(f"sp-$id.json"), Resp.prettify(StudyPlan.get(id)))

    /**
     * Check if user entered study plan abbreviation defined in `abbrev.tsv`, or
     * if he directly entered raw ids defined by the unige database. Entering
     * abbreviation is more user friendly but the abbreviation has to be defined
     * / to exist in `abbrev.tsv` beforehand. Entering a raw id "discharges" the
     * responsibility of entering a correct study plan id to the user but if for
     * some reason a study plan is missing on `abbrev.tsv` it can still be
     * queried this way.
     *
     * @param cleanInput
     *   cleaned string consisting of uniquely and id or abbreviation with no
     *   additional spaces etc...
     * @return
     *   Int - Study Plan Id necessary in the HTTP request to unige database api.
     *   (i.e. the one in `https://pgc.unige.ch/main/api/study-plan-nodes/[id]`
     */
    private def checkIdOrAbbrev(cleanInput: String): Int =
        Try(cleanInput.toInt) match {
            case Success(intId: Int) => intId
            case Failure(_)          => abbrevMap(cleanInput)._1
        }

    /**
     * Parses the java GUI input which looks something like
     * `[code_1],...,[code_n]#[sp_1],...,[sp_m]` where `code_i` is the i-th
     * course code and `sp_j` the j-th study plan abbreviation. see
     * `/res/abbrev.tsv` for a list of abbreviations and what they correspond to.
     *
     * @param args
     *   entrypoint input (stdin) of this program. i.e. what was passed on
     *   command line (should normally be a single string)
     * \@ return Pair of Parallel Sets `(Courses, StudyPlans)`
     */
    private def parseGuiInput(args: Array[String]): (ParSet[String], ParSet[Int]) = {
        if (args.length <= 0) {
            val argsStr = args.mkString("\t")
            throw new IllegalArgumentException(f"Expect at least 1 argument. Argument:\n\"$argsStr\" wrong input.\n $usageMsg")
        }
        var _gui_input: String = args(0).trim();
        // if gui_input has a "#" but nothing after i.e. "12X001#" program will fail
        // since apparently the split array is only of length 1.
        // So checking that special case here.
        val gui_input =
            if (_gui_input.endsWith("#")) _gui_input + " "
            else if (_gui_input.startsWith("#")) " " + _gui_input
            else _gui_input
        val tmp = gui_input.split("#")
        if (tmp.length < 2) {
            val argsStr = args.toList.mkString("\t")
            throw new IllegalArgumentException(f"Argument:\n\"$argsStr\" wrong input.\n $usageMsg")
        }
        val formatter = (s: String) => s.strip.toUpperCase
        val courses: ParSet[String] =
            if (!tmp(0).isBlank) tmp(0).split(",").par.map(formatter).to(ParSet)
            else ParSet.empty[String]
        val studyPlans: ParSet[Int] =
            if (!tmp(1).isBlank)
                tmp(1).split(",").par.map(formatter andThen checkIdOrAbbrev).to(ParSet)
            else ParSet.empty[Int]
        (courses, studyPlans)
    }

    /**
     * Reads file at `res/abbrev.tsv` i.e. list of associations ("study plan",
     * ("abbreviation", "study-plan id")) and parses it into a map.
     *
     * @return
     *   (`studyPlan_abbreviation -> (studyPlan_id, studyPlan_fullName)`) mapping
     */
    private def getAbbrevMap: Map[String, (Int, String)] = {
        Utils
            .readLines(abbrevFilePath)
            .map(_.split("\t")) // split each line of tsv file (tab separated value)
            .map(s => (s(1), (s(2).toInt, s(0)))) // WARN: each line is of the form "FullName   Abbreviation   Id"
            .toSet
            .toMap
    }

    /** 'real' Main with parsed user input */
    private def main(courseCodes: ParSet[String], sps: ParSet[Int]): Unit = {
        // FIXME: Even when setting PandocCommand.exec(silent=false)
        //    stderr is not printed to console. i.e. nothing tells us whether pandoc failed, and if it did => no way of knowing why
        if (courseCodes.nonEmpty || sps.nonEmpty)
            Serializer.extractTemplatesIfNotExists()
        if (courseCodes.nonEmpty) {
            new Thread(() => {
                val mdFiles = courseCodes.map(Course(_).saveToMarkdown())
                Serializer.mdToPdf(mdFiles)
            }).start()
        }
        if (sps.nonEmpty) {
            /* NOTE:
             * - construct each plan study from each study plan id
             * - save each course inside each one to markdown ignoring the ones in `courseCodes`
             * - `saveToMarkdown()` returns the name of the created file so we have a
             *   ParSet of ParVector of filename that we flatten to pass to `mdToPdf()`
             */
            new Thread(() =>
                // err in studyPlan
                val mdFiles = sps.flatMap(StudyPlan(_).saveToMarkdown(courseCodes))
                Serializer.mdToPdf(mdFiles)
            ).start()
        }
        println(f"Files are being generated at \"${Utils.pathOf("pdf").toAbsolutePath}\". Please wait...")
    }

    def main(args: Array[String]): Unit = {
        // if (VERBOSE) println("called " + args.mkString(" "))
        println(f"Started Generation of courses & study plans: \t" + args.mkString(" "))
        try
            Utils.createResDirsIfNotExists()
            val courseSpPair = parseGuiInput(args)
            main(courseSpPair._1, courseSpPair._2)
        catch
            case re: ResourceNotFoundException => {
                Utils.log(re)
                System.err.println(re.getMessage)
            }
            case err: Throwable => {
                Utils.log(err, s"${err.getClass}\t  ${err.getMessage}")
                err.printStackTrace()
                System.err.println(
                    f"An unexpected error (${err.getMessage()}) happened during the pdf generation.\n" +
                        """Please try again. See files/res/cda-err.log for more details. (If the error persists,
                  please contact the developer by sending him everything present in the "files/res/log" folder)
                  """
                )
            }
        // finally
        // TODO: MAKE THIS PRINT GET EXECUTED WHEN JVM EXITS (I.e. when all threads are done)
        // println("Done.")

    }
}

// testAbbrevMap()
// testStudyPlanFactory()
// testSaveStudyPlanToMarkdown()
// TestCourse.testCourseOptional()
// writeCoursDescToRes("14M258")
// testMultipleCourseToMarkdown()
// testMultipleStudyPlanToMarkdown()
