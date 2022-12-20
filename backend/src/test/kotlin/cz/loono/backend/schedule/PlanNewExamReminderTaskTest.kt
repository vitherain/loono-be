package cz.loono.backend.schedule

import cz.loono.backend.api.dto.BadgeTypeDto
import cz.loono.backend.api.dto.ExaminationCategoryTypeDto
import cz.loono.backend.api.dto.ExaminationPreventionStatusDto
import cz.loono.backend.api.dto.ExaminationStatusDto
import cz.loono.backend.api.dto.ExaminationTypeDto
import cz.loono.backend.api.dto.PreventionStatusDto
import cz.loono.backend.api.service.AccountService
import cz.loono.backend.api.service.PreventionService
import cz.loono.backend.api.service.PushNotificationService
import cz.loono.backend.createAccount
import cz.loono.backend.db.model.Account
import cz.loono.backend.db.repository.CronControlRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import java.time.OffsetDateTime

class PlanNewExamReminderTaskTest {

    private val accountService: AccountService = mock()
    private val preventionService: PreventionService = mock()
    private val notificationService: PushNotificationService = mock()
    private val cronControlRepository: CronControlRepository = mock()

    @Test
    fun `two months ahead notification`() {
        val planNewExamReminderTask = PlanNewExamReminderTask(accountService, preventionService, notificationService, cronControlRepository)
        val user = createAccount()

        `when`(accountService.paginateOverAccounts(any())).then { invocation ->
            @Suppress("UNCHECKED_CAST")
            (invocation.arguments[0] as (List<Account>) -> Unit).invoke(listOf(user))
        }
        `when`(preventionService.getPreventionStatus(any())).thenReturn(
            PreventionStatusDto(
                listOf(
                    ExaminationPreventionStatusDto(
                        uuid = "1",
                        examinationType = ExaminationTypeDto.GENERAL_PRACTITIONER,
                        intervalYears = 2,
                        firstExam = false,
                        priority = 1,
                        state = ExaminationStatusDto.CONFIRMED,
                        count = 1,
                        points = 200,
                        badge = BadgeTypeDto.HEADBAND,
                        plannedDate = null,
                        lastConfirmedDate = OffsetDateTime.now().minusMonths(22),
                        examinationCategoryType = ExaminationCategoryTypeDto.MANDATORY
                    )
                ),
                emptyList()
            )
        )

        planNewExamReminderTask.run()

        verify(notificationService, times(1)).sendNewExam2MonthsAheadNotificationToOrder(
            setOf(user),
            ExaminationTypeDto.GENERAL_PRACTITIONER,
            2,
            ExaminationCategoryTypeDto.MANDATORY,
            "1"
        )
    }

    @Test
    fun `a month ahead notification`() {
        val planNewExamReminderTask = PlanNewExamReminderTask(accountService, preventionService, notificationService, cronControlRepository)
        val user = createAccount()

        `when`(accountService.paginateOverAccounts(any())).then { invocation ->
            @Suppress("UNCHECKED_CAST")
            (invocation.arguments[0] as (List<Account>) -> Unit).invoke(listOf(user))
        }
        `when`(preventionService.getPreventionStatus(any())).thenReturn(
            PreventionStatusDto(
                listOf(
                    ExaminationPreventionStatusDto(
                        uuid = "1",
                        examinationType = ExaminationTypeDto.DENTIST,
                        intervalYears = 2,
                        firstExam = false,
                        priority = 1,
                        state = ExaminationStatusDto.CONFIRMED,
                        count = 1,
                        points = 200,
                        badge = BadgeTypeDto.HEADBAND,
                        plannedDate = null,
                        lastConfirmedDate = OffsetDateTime.now().minusMonths(23),
                        examinationCategoryType = ExaminationCategoryTypeDto.MANDATORY,
                        periodicExam = true,
                        customInterval = null,
                        note = null
                    )
                ),
                emptyList()
            )
        )

        planNewExamReminderTask.run()

        verify(notificationService, times(1)).sendNewExamMonthAheadNotificationToOrder(
            setOf(user),
            ExaminationTypeDto.DENTIST,
            2,
            ExaminationCategoryTypeDto.MANDATORY,
            "1"
        )
    }

    @Test
    fun `no last confirmed`() {
        val planNewExamReminderTask = PlanNewExamReminderTask(accountService, preventionService, notificationService, cronControlRepository)
        val user = createAccount()

        `when`(accountService.paginateOverAccounts(any())).then { invocation ->
            @Suppress("UNCHECKED_CAST")
            (invocation.arguments[0] as (List<Account>) -> Unit).invoke(listOf(user))
        }
        `when`(preventionService.getPreventionStatus(any())).thenReturn(
            PreventionStatusDto(
                listOf(
                    ExaminationPreventionStatusDto(
                        uuid = "1",
                        examinationType = ExaminationTypeDto.GENERAL_PRACTITIONER,
                        intervalYears = 2,
                        firstExam = false,
                        priority = 1,
                        state = ExaminationStatusDto.CONFIRMED,
                        count = 1,
                        points = 200,
                        badge = BadgeTypeDto.HEADBAND,
                        plannedDate = null,
                        lastConfirmedDate = null
                    )
                ),
                emptyList()
            )
        )

        planNewExamReminderTask.run()

        verify(notificationService, times(0)).sendNewExam2MonthsAheadNotificationToOrder(
            setOf(user),
            ExaminationTypeDto.GENERAL_PRACTITIONER,
            2,
            ExaminationCategoryTypeDto.MANDATORY,
            null
        )
    }

    @Test
    fun `without notification`() {
        val planNewExamReminderTask = PlanNewExamReminderTask(accountService, preventionService, notificationService, cronControlRepository)
        val user = createAccount()

        `when`(accountService.paginateOverAccounts(any())).then { invocation ->
            @Suppress("UNCHECKED_CAST")
            (invocation.arguments[0] as (List<Account>) -> Unit).invoke(listOf(user))
        }
        `when`(preventionService.getPreventionStatus(any())).thenReturn(
            PreventionStatusDto(
                listOf(
                    ExaminationPreventionStatusDto(
                        uuid = "1",
                        examinationType = ExaminationTypeDto.GENERAL_PRACTITIONER,
                        intervalYears = 2,
                        firstExam = false,
                        priority = 1,
                        state = ExaminationStatusDto.CONFIRMED,
                        count = 1,
                        points = 200,
                        badge = BadgeTypeDto.HEADBAND,
                        plannedDate = null,
                        lastConfirmedDate = OffsetDateTime.now().minusYears(1)
                    )
                ),
                emptyList()
            )
        )

        planNewExamReminderTask.run()

        verify(notificationService, times(0)).sendNewExam2MonthsAheadNotificationToOrder(
            setOf(user),
            ExaminationTypeDto.GENERAL_PRACTITIONER,
            2,
            ExaminationCategoryTypeDto.MANDATORY,
            null
        )
    }
}
