package com.sandbox.calvin_li.bats

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters


class BatCheckWorker(val context: Context, workerParams: WorkerParameters):
    Worker(context, workerParams)  {

    override fun doWork(): Result {
        BatNotification.displayNotification(context)
        return Result.success()
    }
}