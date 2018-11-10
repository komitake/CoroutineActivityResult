package jp.komitake.coroutine_activity_result

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private object ContinuationHolder {
    private var requestCodeCounter: Int = 1
    private var map = mutableMapOf<Int, Continuation<ActivityResult>>()

    fun next(continuation: Continuation<ActivityResult>): Int {
        return (++requestCodeCounter).also { code ->
            map[code] = continuation
        }
    }

    fun delete(requestCode: Int): Continuation<ActivityResult>? = map.remove(requestCode)
}

class GetActivityResultFragment: Fragment() {

    companion object {
        const val TAG: String = "GetActivityResultFragment"
    }

    fun startActivityForResult(intent: Intent, continuation: Continuation<ActivityResult>) {
        val code = ContinuationHolder.next(continuation)
        startActivityForResult(intent, code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val continuation = ContinuationHolder.delete(requestCode)
        if (continuation != null) {
            fragmentManager.beginTransaction().remove(this).commit()
            fragmentManager.executePendingTransactions()

            when (resultCode) {
                Activity.RESULT_OK -> continuation.resume(ActivityResult.Ok(data!!))
                Activity.RESULT_CANCELED -> continuation.resume(ActivityResult.Canceled)
            }
        }
    }
}

public sealed class ActivityResult {
    data class Ok(val data: Intent): ActivityResult()
    object Canceled: ActivityResult()
}

public suspend fun <A: Activity> A.activityResult(i: Intent): ActivityResult {
    return suspendCoroutine { c ->
        val f = GetActivityResultFragment()
        fragmentManager.beginTransaction().add(f, GetActivityResultFragment.TAG).commit()
        fragmentManager.executePendingTransactions()
        f.startActivityForResult(i, c)
    }
}
