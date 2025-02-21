package zlc.season.rxdownload4.recorder

import android.arch.persistence.room.TypeConverter
import zlc.season.rxdownload4.manager.*

class StatusConverter {
    companion object {
        const val NORMAL = 0
        const val STARTED = 1
        const val DOWNLOADING = 2
        const val PAUSED = 3
        const val COMPLETED = 4
        const val FAILED = 5
        const val DELETED = 6
    }

    @TypeConverter
    fun intToStatus(status: Int): Status {
        return when (status) {
            NORMAL -> Normal()
            STARTED -> Started()
            DOWNLOADING -> Downloading()
            PAUSED -> Paused()
            COMPLETED -> Completed()
            FAILED -> Failed()
            DELETED -> Deleted()
            else -> throw IllegalStateException("UNKNOWN STATE")
        }
    }

    @TypeConverter
    fun statusToInt(status: Status): Int {
        return when (status) {
            is Normal -> NORMAL
            is Started -> STARTED
            is Downloading -> DOWNLOADING
            is Paused -> PAUSED
            is Completed -> COMPLETED
            is Failed -> FAILED
            is Deleted -> DELETED
        }
    }
}