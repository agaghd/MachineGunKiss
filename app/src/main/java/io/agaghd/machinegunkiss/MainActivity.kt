package io.agaghd.machinegunkiss

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.util.SparseArray
import android.view.MotionEvent
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var soundPool: SoundPool
    lateinit var soundThread: HandlerThread
    lateinit var soundHandler: Handler
    lateinit var vibrator: Vibrator
    val soundMap = SparseArray<Int>()
    var playLoop = false
    var playingBolt = false
    val gunDelay = 100L
    var bullet = 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        setListeners()
    }

    fun init() {
        bullet_tv.text = bullet.toString()
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        soundPool = SoundPool.Builder().setMaxStreams(5)
            .setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).build()).build()
        soundThread = HandlerThread("soundThread")
        soundThread.start()
        soundHandler = Handler(soundThread.looper, Handler.Callback {
            when (it.what) {
                R.raw.ak47_1 -> {
                    if (bullet > 0) {
                        bullet--
                        soundPool.play(soundMap.get(it.what), 1.0f, 1.0f, 0, 0, 1.0f)
                        vibrator.vibrate(gunDelay * 8 / 10)
                        runOnUiThread { bullet_tv.text = bullet.toString() }
                        if (playLoop) {
                            val what = if (bullet > 0) R.raw.ak47_1 else R.raw.dryfire_pistol
                            soundHandler.sendEmptyMessageDelayed(what, gunDelay)
                        }
                    }
                }
                R.raw.dryfire_pistol -> {
                    soundPool.play(soundMap.get(it.what), 1.0f, 1.0f, 0, 0, 1.0f)
                    if (playLoop) {
                        val what = if (bullet > 0) R.raw.ak47_1 else R.raw.dryfire_pistol
                        soundHandler.sendEmptyMessageDelayed(what, gunDelay)
                    }
                }
                R.raw.ak47_boltpull -> {
                    soundPool.play(soundMap.get(it.what), 1.0f, 1.0f, 0, 0, 1.0f)
                }
            }
            true
        })
        soundMap.put(R.raw.ak47_1, soundPool.load(this@MainActivity, R.raw.ak47_1, 1))
        soundMap.put(R.raw.ak47_boltpull, soundPool.load(this@MainActivity, R.raw.ak47_boltpull, 1))
        soundMap.put(R.raw.dryfire_pistol, soundPool.load(this@MainActivity, R.raw.dryfire_pistol, 1))
    }

    private fun setListeners() {
        fire_btn.setOnTouchListener { v, event ->
            val what: Int
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    what = if (bullet > 0) R.raw.ak47_1 else R.raw.dryfire_pistol
                    playLoop = true
                    soundHandler.sendEmptyMessage(what)
                }
                MotionEvent.ACTION_UP -> {
                    soundHandler.removeMessages(R.raw.ak47_1)
                    soundHandler.removeMessages(R.raw.dryfire_pistol)
                    playLoop = false
                }

            }
            true
        }
        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    if (seekBar.progress >= 80) {
                        if (!playingBolt) {
                            playingBolt = true
                            soundHandler.sendEmptyMessage(R.raw.ak47_boltpull)
                            bullet = 30
                            bullet_tv.text = bullet.toString()
                            playingBolt = false
                        }
                    }
                    seek_bar.progress = 0
                }
            }

        })
    }

}
