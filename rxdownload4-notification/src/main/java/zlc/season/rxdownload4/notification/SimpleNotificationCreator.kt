package zlc.season.rxdownload4.notification

import android.app.Notification
import android.support.v4.app.NotificationCompat.Builder
import zlc.season.claritypotion.ClarityPotion.Companion.clarityPotion
import zlc.season.rxdownload4.manager.*
import zlc.season.rxdownload4.notification.NotificationActionService.Companion.cancelAction
import zlc.season.rxdownload4.notification.NotificationActionService.Companion.startAction
import zlc.season.rxdownload4.notification.NotificationActionService.Companion.stopAction
import zlc.season.rxdownload4.task.Task
import zlc.season.rxdownload4.utils.log

class SimpleNotificationCreator : NotificationCreator {
    private val channelId = "RxDownload"
    private val channelName = "RxDownload"
    private val channelDesc = "RxDownload"

    private lateinit var task: Task
    private val builderHelper by lazy { BuilderHelper(channelId, task) }

    override fun init(task: Task) {
        this.task = task

        if (!isEnableNotification()) {
            "Notification not enable".log()
        }

        createNotificationChannel(channelId, channelName, channelDesc)
    }

    override fun create(task: Task, status: Status): Notification? {
        return builderHelper.get(status).build()
    }

    class BuilderHelper(private val channelId: String, private val task: Task) {
        private val builderMap = mutableMapOf<Status, Builder>()

        private val startedContent by lazy { clarityPotion.getString(R.string.notification_started_text) }
        private val pausedContent by lazy { clarityPotion.getString(R.string.notification_paused_text) }
        private val failedContent by lazy { clarityPotion.getString(R.string.notification_failed_text) }
        private val completedContent by lazy { clarityPotion.getString(R.string.notification_completed_text) }

        private val startedActions by lazy { listOf(stopAction(task), cancelAction(task)) }
        private val downloadingActions by lazy { listOf(stopAction(task), cancelAction(task)) }
        private val pausedActions by lazy { listOf(startAction(task), cancelAction(task)) }
        private val failedActions by lazy { listOf(startAction(task), cancelAction(task)) }

        fun get(status: Status): Builder {
            var builder = builderMap[status]

            if (builder == null) {
                val (content, actions, icon) = when (status) {
                    is Normal -> Triple("", emptyList(), 0)
                    is Started -> Triple(startedContent, startedActions, R.drawable.ic_download)
                    is Downloading -> Triple("", downloadingActions, R.drawable.ic_download)
                    is Paused -> Triple(pausedContent, pausedActions, R.drawable.ic_pause)
                    is Failed -> Triple(failedContent, failedActions, R.drawable.ic_pause)
                    is Completed -> Triple(completedContent, emptyList(), R.drawable.ic_completed)
                    is Deleted -> Triple("", emptyList(), 0)
                }

                val newBuilder = createNotificationBuilder(
                        channelId = channelId,
                        title = task.taskName,
                        content = content,
                        icon = icon,
                        actions = actions
                )
                builderMap[status] = newBuilder
                builder = newBuilder
            }

            if (status is Downloading) {
                builder.setProgress(
                        status.progress.totalSize.toInt(),
                        status.progress.downloadSize.toInt(),
                        status.progress.isChunked
                )
            }
            return builder
        }
    }
}