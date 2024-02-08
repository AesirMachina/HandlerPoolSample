package com.aesirmachina.handlerpool

import android.content.Context
import android.os.Bundle
import android.os.Message
import android.os.SystemClock
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LifecycleOwner
import com.aesirmachina.handler.HandlerPool
import com.aesirmachina.handler.HandlerPool.HandlerPoolFactory
import com.aesirmachina.handler.HandlerPoolCallback
import com.aesirmachina.handlerpool.ui.theme.HandlerPoolSampleTheme

class MainActivity : ComponentActivity() {
    companion object{
        var LOG_TAG = "POOLHANDLER"
    }

    var pool: HandlerPool? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val builder = HandlerPool.Builder(applicationContext)
        builder.poolSize(5);
        builder.lifecycleOwner = this

        builder.setHandlerPoolFactory(HandlerPoolFactory { ctx, poolSize ->
            MyHandlerPool(ctx, poolSize)
        })

        builder.callback = object : HandlerPoolCallback {
            override fun handleMessage(ctx: Context, msg: Message) {
                Log.i(LOG_TAG,
                    "handleMessage:${msg.arg1+100}:${Thread.currentThread().name}:${SystemClock.elapsedRealtimeNanos()}");

            }

            override fun onWarn(ctx: Context, msg: Message, duration: Long) {
            }

            override fun onCritical(ctx: Context, msg: Message, duration: Long) {
            }

            override fun onFailure(ctx: Context, msg: Message, duration: Long) {
            }

            override fun onContextDestroyed() {
                Log.i(LOG_TAG,
                    "context destroyed:${Thread.currentThread().name}:${SystemClock.elapsedRealtimeNanos()}");
            }
        }

        // format the logcat output so that it can be imported into a spreadsheet.
        Log.i(LOG_TAG, "event:count:thread:nanotime");
        pool = builder.build();


        setContent {
            HandlerPoolSampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }

    override fun onResume(){
        super.onResume();
        var delay = 0L
        pool?.let{
            for (i in 1..100){
                val m = Message();
                m.arg1 = i

                val sent = it.sendMessageDelayed(m, delay)
                if (sent){
                    Log.i(LOG_TAG,
                        "send:${m.arg1}:${Thread.currentThread().name}:${SystemClock.elapsedRealtimeNanos()}");
                } else {
                    Log.i(LOG_TAG,
                        "not sent:${m.arg1}:${Thread.currentThread().name}:${SystemClock.elapsedRealtimeNanos()}");
                }

                /* capture logs with the delay and without the delay and it's possible to see the
                 * influence of the scheduler.
                 */
                delay+=10
            }
        }

    }

    class MyHandlerPool(ctx: Context, poolSize: Int) : HandlerPool(ctx, poolSize){
        var countOnCreate = 0
        var countOnDestroy = 0
        var countOnPause = 0
        var countOnResume = 0
        var countOnStart = 0
        var countOnStop = 0
        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)
            Log.i(LOG_TAG,
                "pool onCreate:${countOnCreate++}:${Thread.currentThread().name}:${SystemClock.elapsedRealtimeNanos()}");

        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            Log.i(LOG_TAG,
                "pool onDestroy:${countOnDestroy++}:${Thread.currentThread().name}:${SystemClock.elapsedRealtimeNanos()}");
        }

        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            Log.i(LOG_TAG,
                "pool onPause:${countOnPause++}:${Thread.currentThread().name}:${SystemClock.elapsedRealtimeNanos()}");
        }

        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            Log.i(LOG_TAG,
                "pool onResume:${countOnResume++}:${Thread.currentThread().name}:${SystemClock.elapsedRealtimeNanos()}");
        }

        override fun onStart(owner: LifecycleOwner) {
            super.onStart(owner)
            Log.i(LOG_TAG,
                "pool onStart:${countOnStart++}:${Thread.currentThread().name}:${SystemClock.elapsedRealtimeNanos()}");
        }

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
            Log.i(LOG_TAG,
                "pool onStop:${countOnStop++}:${Thread.currentThread().name}:${SystemClock.elapsedRealtimeNanos()}");
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HandlerPoolSampleTheme {
        Greeting("Android")
    }
}