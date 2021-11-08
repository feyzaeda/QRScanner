package com.appricot.qrscanner

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appricot.qrscanner.databinding.ActivityMainBinding
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.CaptureActivity
import org.json.JSONException
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    private lateinit var binding: ActivityMainBinding
    var hide: Animation? = null
    var reveal: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI() {
        hide = AnimationUtils.loadAnimation(binding.root.context, android.R.anim.fade_out)
        reveal = AnimationUtils.loadAnimation(binding.root.context, android.R.anim.fade_in)

        binding.apply {
            btnScanCode.setOnClickListener {
                btnScanCodeClick()
            }
            btnEnterCode.setOnClickListener {
                btnEnterCodeClick()
            }
            btnEnterCodeL.setOnClickListener {
                btnEnterCodeLClick()
            }
            cardViewImg.setOnClickListener { cameraTask() }
        }
    }

    private fun btnEnterCodeLClick() {
        if (binding.edtCode!!.text.toString().isNullOrEmpty()) {
            Toast.makeText(this, "Please enter code", Toast.LENGTH_SHORT).show()
        } else {
            var value = binding.edtCode!!.text.toString()
            Toast.makeText(this, value, Toast.LENGTH_SHORT).show()
        }
    }

    private fun btnScanCodeClick() {
        binding.apply {
            tv!!.startAnimation(reveal)
            cardViewImg!!.startAnimation(reveal)
            cardViewText.startAnimation(hide)
            cardViewImg!!.visibility = View.VISIBLE
            cardViewText!!.visibility =View.GONE
            tv!!.setText("Scan QR Code")
        }
    }

    private fun btnEnterCodeClick() {
        binding.apply {
            tv!!.startAnimation(reveal)
            cardViewImg!!.startAnimation(hide)
            cardViewText.startAnimation(reveal)
            cardViewImg!!.visibility = View.GONE
            cardViewText!!.visibility =View.VISIBLE
            tv!!.setText("Enter QR Code")
        }
    }

    private fun hasCameraAccess(): Boolean{
        return EasyPermissions.hasPermissions(this, android.Manifest.permission.CAMERA)
    }

    private fun cameraTask(){
        if (hasCameraAccess()){
            var qrScanner = IntentIntegrator(this)
            qrScanner.setPrompt("scan a QR code")
            qrScanner.setCameraId(0)
            qrScanner.setOrientationLocked(true)
            qrScanner.setBeepEnabled(true)
            qrScanner.captureActivity = CaptureActivity::class.java
            qrScanner.initiateScan()
        }else{
            EasyPermissions.requestPermissions(
                this,
                "This app needs access to your camera so you can take pictures.",
                123,
                android.Manifest.permission.CAMERA
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        var result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_SHORT).show()
                binding.edtCode!!.setText("")
            } else {
                try {
                    binding.cardViewText!!.startAnimation(reveal)
                    binding.cardViewImg!!.startAnimation(hide)
                    binding.cardViewText!!.visibility = View.VISIBLE
                    binding.cardViewImg!!.visibility = View.GONE
                    binding.edtCode!!.setText(result.contents.toString())
                } catch (exception: JSONException) {
                    Toast.makeText(this, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                    binding.edtCode!!.setText("")
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults,this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onRationaleAccepted(requestCode: Int) {
    }

    override fun onRationaleDenied(requestCode: Int) {
    }
}