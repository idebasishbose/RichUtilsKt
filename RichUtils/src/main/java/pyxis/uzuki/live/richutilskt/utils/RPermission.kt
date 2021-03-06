@file:JvmName("RPermission")

package pyxis.uzuki.live.richutilskt.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat

class RPermission private constructor(private var context: Context) {

    private fun getActivity(context: Context): Activity? {
        var c = context

        while (c is ContextWrapper) {
            if (c is Activity) {
                return c
            }
            c = c.baseContext
        }
        return null
    }

    /**
     * check and request Permission which given.
     *
     * @param[array] array of Permission to check
     * @param[callback] callback object
     * @return check result
     */
    @JvmOverloads
    fun checkPermission(array: Array<String>, callback: (Int, List<String>) -> Unit = { _, _ -> }): Boolean
            = checkPermission(List(array.size) { array[it] }, callback)

    /**
     * check and request Permission which given.
     *
     * @param[list] list of Permission to check
     * @param[callback] callback object
     * @return check result
     */
    @SuppressLint("NewApi")
    fun checkPermission(list: List<String>, callback: (Int, List<String>) -> Unit): Boolean {
        if (Build.VERSION.SDK_INT < 23) {
            callback(PERMISSION_GRANTED, list)
            return true
        }

        val notGranted: ArrayList<String> = ArrayList()
        list.forEach {
            val result = ContextCompat.checkSelfPermission(context, it)
            if (result != PackageManager.PERMISSION_GRANTED)
                notGranted.add(it)
        }

        return if (notGranted.isNotEmpty()) {
            requestPermission(notGranted, callback)
            false
        } else {
            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermission(list: List<String>, callback: (Int, ArrayList<String>) -> Unit) {
        val fm = getActivity(context)?.fragmentManager
        val fragment = RequestFragment(fm as FragmentManager, callback)

        fm.beginTransaction().add(fragment, "FRAGMENT_TAG").commitAllowingStateLoss()
        fm.executePendingTransactions()

        fragment.requestPermissions(list.toTypedArray(), 72)
    }

    @SuppressLint("ValidFragment")
    inner class RequestFragment() : Fragment() {
        var fm: FragmentManager? = null
        var callback: ((Int, ArrayList<String>) -> Unit)? = null

        constructor(fm: FragmentManager, callback: (Int, ArrayList<String>) -> Unit) : this() {
            this.fm = fm
            this.callback = callback
        }

        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            val permissionList: ArrayList<String> = getVerifiedPermissions(permissions)
            val returnCode = if (verifyPermissions(grantResults)) PERMISSION_GRANTED else PERMISSION_FAILED
            callback?.invoke(returnCode, permissionList)
            fm?.beginTransaction()?.remove(this)?.commitAllowingStateLoss()
        }
    }

    private fun verifyPermissions(grantResults: IntArray): Boolean =
            if (grantResults.isEmpty()) false else grantResults.none { it != PackageManager.PERMISSION_GRANTED }

    private fun getVerifiedPermissions(permissions: Array<String>): ArrayList<String> {
        val permissionList: ArrayList<String> = ArrayList()
        permissions.forEach {
            if (ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED)
                permissionList.add(it)
        }

        return permissionList
    }

    companion object {
        private var instance: RPermission? = null

        @JvmStatic
        fun getInstance(c: Context): RPermission {

            if (instance == null) {
                instance = RPermission(c)
            }

            return instance as RPermission
        }

        @JvmField
        val PERMISSION_GRANTED = 1
        @JvmField
        val PERMISSION_FAILED = 2
    }
}