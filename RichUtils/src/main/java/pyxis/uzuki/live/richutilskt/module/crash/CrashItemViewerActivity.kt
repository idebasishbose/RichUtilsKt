package pyxis.uzuki.live.richutilskt.module.crash

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import pyxis.uzuki.live.richutilskt.R
import pyxis.uzuki.live.richutilskt.utils.RPreference

/**
 * RichUtilsKt
 * Class: CrashItemViewerActivity
 * Created by winds on 2017-07-17.
 */

abstract class CrashItemViewerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crashitem_viewer)

        val logLocation = RPreference.getInstance(this).getString("logLocation")
        if (TextUtils.isEmpty(logLocation))
           displayEnterLocationDialog()
    }

    private fun displayEnterLocationDialog() {

    }
}