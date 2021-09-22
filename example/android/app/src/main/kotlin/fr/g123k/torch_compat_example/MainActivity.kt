package fr.g123k.torch_compat_example

import android.os.Bundle
import io.flutter.embedding.android.FlutterFragmentActivity
import io.flutter.embedding.engine.FlutterEngine

import io.flutter.app.FlutterActivity
import io.flutter.plugins.GeneratedPluginRegistrant
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterFragmentActivity() {
    private val CHANNEL: String = "fr.g123k.torch_compat_example"
    private lateinit var mResult: MethodChannel.Result

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine?.dartExecutor, CHANNEL).setMethodCallHandler(object : MethodChannel.MethodCallHandler {
            override fun onMethodCall(methodCall: MethodCall, result: MethodChannel.Result) {
                mResult = result;
            }
        })
    }
}
