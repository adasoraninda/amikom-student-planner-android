package com.codetron.studentplanner.data.model

class Student private constructor(
    val email: String,
    val name: String? = null,
    var photo: String? = null,
    val education: String? = null,
    val grade: Int? = null
) {

    companion object {
        fun fromMapStudent(student: Map<String, Any>?): Student {
            return Student(
                student?.getValue("email").toString(),
                student?.getOrElse("name") { "Tidak ada" }.toString(),
                student?.getOrElse("photo") { "" }.toString(),
                student?.getOrElse("education") { "Tidak ada" }.toString(),
                student?.getOrElse("grade") { 0 }.toString().toInt(),
            )
        }

        fun toMapStudent(student: Student): Map<String, Any?> {
            val mapStudent = HashMap<String, Any?>()
            mapStudent["email"] = student.email
            mapStudent["name"] = student.name
            mapStudent["photo"] = student.photo
            mapStudent["education"] = student.education
            mapStudent["grade"] = student.grade
            return mapStudent
        }

    }

    class Builder(
        private val email: String
    ) {
        private var name: String? = null
        private var photo: String? = null
        private var education: String? = null
        private var grade: Int? = null

        fun withName(name: String): Builder {
            this.name = name
            return this
        }

        fun withPhoto(photo: String): Builder {
            this.photo = photo
            return this
        }

        fun withEducation(education: String): Builder {
            this.education = education
            return this
        }

        fun withGrade(grade: Int?): Builder {
            this.grade = grade
            return this
        }

        fun build(): Student {
            return Student(
                email, name, photo, education, grade
            )
        }
    }

}