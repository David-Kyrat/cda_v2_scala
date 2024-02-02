/* package ch.model.sealedconcept

sealed trait CourseType

case object Mandatory extends CourseType {
    override def toString = "Obligatoire"
}

case object Optional extends CourseType {
    override def toString = "Optionnel"
}

object CourseType extends SealedConceptObject[CourseType] {
    override def jsonKey = "type" // TODO: check if its actually that
    override def ALL = Vector(Mandatory, Optional)
} */
