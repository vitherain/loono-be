package cz.loono.backend.db.model

import cz.loono.backend.api.dto.ExaminationActionTypeDto
import cz.loono.backend.api.dto.ExaminationCategoryTypeDto
import cz.loono.backend.api.dto.ExaminationStatusDto
import cz.loono.backend.api.dto.ExaminationTypeDto
import org.hibernate.Hibernate
import org.hibernate.envers.Audited
import java.time.LocalDateTime
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "examination_record")
@Audited
data class ExaminationRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "TEXT")
    val type: ExaminationTypeDto = ExaminationTypeDto.GENERAL_PRACTITIONER,

    @Column
    val plannedDate: LocalDateTime? = null,

    @ManyToOne(optional = false)
    val account: Account,

    @Column(nullable = false)
    val firstExam: Boolean = true,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "TEXT")
    var status: ExaminationStatusDto = ExaminationStatusDto.NEW,

    @Column(nullable = true)
    val note: String? = null,

    @Column(nullable = true)
    val customInterval: Int? = null,

    @Column(nullable = false)
    val periodicExam: Boolean = true,

    @Column(unique = true, nullable = false, columnDefinition = "TEXT")
    val uuid: String? = UUID.randomUUID().toString(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "TEXT")
    var examinationActionType: ExaminationActionTypeDto = ExaminationActionTypeDto.EXAMINATION,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "TEXT")
    var examinationCategoryType: ExaminationCategoryTypeDto = ExaminationCategoryTypeDto.MANDATORY,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as ExaminationRecord

        @Suppress("SENSELESS_COMPARISON")
        return id != null && id == other.id
    }

    override fun hashCode(): Int = 0

    @Override
    override fun toString(): String =
        this::class.simpleName + "(id = $id , type = $type , date = $plannedDate , accountId = ${account.uid}, firstExam = $firstExam, status = $status)" // ktlint-disable max-line-ength
}
