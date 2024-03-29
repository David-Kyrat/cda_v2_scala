package cda.model.sealedconcept

sealed trait ExaSession

/**
 * January/Feburary Exam Session
 */
case object Jan extends ExaSession {
    override def toString = "Janvier"
}

/**
 * June/Jully Exam Session
 */
case object Jul extends ExaSession {
    override def toString = "Juillet"
}

/**
 * Makeup Exam Session. i.e. August/September exam session.
 */
case object Aug extends ExaSession {
    override def toString = "Août"
}

object ExaSession extends SealedConceptObject[ExaSession] {

    override def jsonKey = "" // TODO:
    override def ALL = Vector(Jan, Jul, Aug)
}
