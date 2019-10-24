package io.agaghd.machinegunkiss

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.util.SparseArray
import android.view.MotionEvent
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var soundPool: SoundPool
    lateinit var soundThread: HandlerThread
    lateinit var soundHandler: Handler
    lateinit var vibrator: Vibrator
    val soundMap = SparseArray<Int>()
    var playLoop = false
    val gunDelay = 100L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        setListeners()
    }

    fun init() {
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        soundPool = SoundPool.Builder().setMaxStreams(5)
            .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).build()).build()
        soundThread = HandlerThread("soundThread")
        soundThread.start()
        soundHandler = Handler(soundThread.looper, Handler.Callback {
            soundPool.play(soundMap.get(it.what), 1.0f, 1.0f, 0, 0, 1.0f)
            vibrator.vibrate(gunDelay * 8 / 10)
            if (playLoop) {
                soundHandler.sendEmptyMessageDelayed(it.what, gunDelay)
            }
            true
        })
        soundMap.put(R.raw.ak47_1, soundPool.load(this@MainActivity, R.raw.ak47_1, 1))
    }

    private fun setListeners() {
        test_btn.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    playLoop = true
                    soundHandler.sendEmptyMessage(R.raw.ak47_1)
                }
                MotionEvent.ACTION_UP -> {
                    soundHandler.removeMessages(R.raw.ak47_1)
                    playLoop = false
                }

            }
            true
        }
    }

}
