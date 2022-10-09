package cz.loono.backend.schedule

import cz.loono.backend.api.dto.ExaminationStatusDto
import cz.loono.backend.api.service.PushNotificationService
import cz.loono.backend.db.repository.ExaminationRecordRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Component
class ComingAndPassedExamNotificationTask(
    private val examinationRecordRepository: ExaminationRecordRepository,
    private val notificationService: PushNotificationService
) : DailySchedulerTask {

    override fun run() {
        val now = LocalDateTime.now()
        val plannedExams = examinationRecordRepository.findAllByStatus(ExaminationStatusDto.NEW)
        val plannedPeriodicExams = plannedExams.filter { it.periodicExam != false }
        plannedPeriodicExams.forEach { record ->
            record.plannedDate?.let {
                val hours = ChronoUnit.HOURS.between(record.plannedDate, now)
                if (hours in -47..-24) {
                    notificationService.sendComingVisitNotification(
                        setOf(record.account),
                        record.type,
                        record.plannedDate.plusHours(2).format(DateTimeFormatter.ofPattern("HH:mm")),
                        record.uuid
                    )
                }
                if (hours in 0..24) {
                    notificationService.sendCompletionNotification(
                        setOf(record.account),
                        record.plannedDate.plusHours(2).format(DateTimeFormatter.ofPattern("HH:mm")),
                        record.type,
                        record.uuid
                    )
                }
            }
        }
    }
}
