package se.mattiashellman.a2x

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import java.lang.ref.WeakReference

const val CURRENT_NUM = "currentnum"

class MainActivity : AppCompatActivity() {
    private var numberView: TextView? = null
    private var doAsync : AsyncRunner? = null
    private var primeGenerator : PrimeGenerator? = null

    /**
     * Initialize members from instance state / local storage / default
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val sharedPref = getPreferences(MODE_PRIVATE)
            val default = resources.getInteger(R.integer.defaultNum).toLong()
            primeGenerator = PrimeGenerator(sharedPref.getLong(CURRENT_NUM, default))
        }

        numberView = findViewById(R.id.theNumber) as TextView;

        doAsync = AsyncRunner(
            WeakReference<TextView>(findViewById(R.id.theNumber) as TextView),
            resources.getInteger(R.integer.defaultSpeed).toLong())
        doAsync?.execute(primeGenerator)
    }

    /**
     * Stop calculations
     */
    override fun onPause() {
        super.onPause()

        doAsync?.cancel(true)
    }

    /**
     * Resume calculations
     */
    override fun onResume() {
        super.onResume()

        if (doAsync?.isCancelled() == true) {
            doAsync = AsyncRunner(
                WeakReference<TextView>(numberView),
                resources.getInteger(R.integer.defaultSpeed).toLong())
            doAsync?.execute(primeGenerator)
        }
    }

    /**
     * Save to local storage
     */
    override fun onStop() {
        super.onStop()

        val sharedPref = getPreferences(MODE_PRIVATE)

        with (sharedPref.edit()) {
            putLong(CURRENT_NUM, primeGenerator!!.number)
            commit()
        }
    }

    /**
     * Save instance state on system-initiated process death
     */
    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putLong(CURRENT_NUM, primeGenerator!!.number)

        super.onSaveInstanceState(outState)
    }

    /**
     * Restore available instance state
     */
    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        savedInstanceState?.run {
            primeGenerator = PrimeGenerator(getLong(CURRENT_NUM))
        }
    }

    /**
     * Class for performing background calculation of a PrimeGenerator while enabling updates to UI
     */
    private class AsyncRunner(val numberView : WeakReference<TextView>, val speed : Long) : AsyncTask<PrimeGenerator, Long, Unit>() {

        override fun doInBackground(vararg input: PrimeGenerator) {
            // aktivera cancel i onPause()
            while (!isCancelled()) {
                val primeNum : Long = input[0].generateNext()

                publishProgress(primeNum)

                try {
                    Thread.sleep(speed)
                } catch (ex: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }
        }

        override fun onProgressUpdate(vararg progress: Long?) {
            numberView.get()?.text = progress[0].toString()
        }
    }

}
