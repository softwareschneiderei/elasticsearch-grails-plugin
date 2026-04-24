package test

import java.time.*

class Dates {

    String name
    Date date = new Date()
    LocalDate localDate = LocalDate.now()
    LocalDateTime localDateTime = LocalDateTime.now()
    ZonedDateTime zonedDateTime = ZonedDateTime.now()
    OffsetDateTime offsetDateTime = OffsetDateTime.now()
    OffsetTime offsetTime = OffsetTime.now()

    static constraints = {
        name nullable: true
    }

    static searchable = {

    }
}
