package fr.g123k.torch_compat

import android.content.Context
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import fr.g123k.torch_compat.impl.BaseTorch
import fr.g123k.torch_compat.impl.TorchCamera2Impl
import fr.g123k.torch_compat.impl.TorchCamera1Impl
import fr.g123k.torch_compat.utils.ActivityLifecycleCallbacks
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.platform.PlatformViewRegistry


class TorchCompatPlugin : FlutterPlugin, ActivityAware, MethodCallHandler {

    private var hasLamp = false
    private var torchImpl: BaseTorch? = null
    private var activity: Activity? = null

    /** Plugin registration embedding v1 */
    companion object {}

    /** Plugin registration embedding v2 */
    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        // your plugin is now attached to a Flutter experience.
        setTorchImplemenation(flutterPluginBinding.getApplicationContext())
        val messenger = flutterPluginBinding.binaryMessenger
        var methodChannel = MethodChannel(messenger, "g123k/torch_compat")
        methodChannel!!.setMethodCallHandler(this)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        // your plugin is no longer attached to a Flutter experience.
    }

    override fun onAttachedToActivity(activityPluginBinding: ActivityPluginBinding) {
        val activity = activityPluginBinding.activity
        this.activity = activity

        hasLamp = activity.applicationContext.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        this.activity = null
        torchImpl?.dispose()
    }

    override fun onReattachedToActivityForConfigChanges(activityPluginBinding: ActivityPluginBinding) {
        this.activity = activityPluginBinding.activity
    }

    override fun onDetachedFromActivity() {
        this.activity = null
        torchImpl?.dispose()
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        if (call.method == "turnOn") {
            if (!hasLamp) {
                result.error("NOTORCH", "This device does not have a torch", null)
            } else {
                torchImpl?.turnOn()
                result.success(true)
            }
        } else if (call.method == "turnOff") {
            if (!hasLamp) {
                result.error("NOTORCH", "This device does not have a torch", null)
            } else {
                torchImpl?.turnOff()
                result.success(true)
            }
        } else if (call.method == "hasTorch") {
            result.success(hasLamp)
        } else if (call.method == "dispose") {
            torchImpl?.dispose()
            result.success(true)
        } else {
            result.notImplemented()
        }
    }

    /** Sets torchImplementation based on adroid build_version */
    private fun setTorchImplemenation(context: Context) {
        torchImpl = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> TorchCamera2Impl(context)
            else -> TorchCamera1Impl(context)
        }
    }

}
