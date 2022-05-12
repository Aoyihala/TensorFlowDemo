package com.aoyihala.screenmediaplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.SurfaceView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.aoyihala.screenmediaplayer.databinding.ActivityMainBinding
import com.aoyihala.screenmediaplayer.service.MediaScreenService
import com.aoyihala.screenmediaplayer.util.FloatUtil
import com.aoyihala.screenmediaplayer.util.ImageProcess
import com.aoyihala.screenmediaplayer.util.ScreenUtil
import java.nio.ByteBuffer


class MainActivity : AppCompatActivity() ,ImageReader.OnImageAvailableListener{
    private var personClick: Boolean = false
    private lateinit var detector: TfliteRunner
    private val TF_OD_API_INPUT_SIZE = 640
    private val MODE = TfliteRunMode.Mode.NNAPI_GPU_FP16
    private var mediaProjection: MediaProjection?=null
    private lateinit var projectionManager: MediaProjectionManager
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageReader:ImageReader
    private lateinit var surfaceView:SurfaceView
    private lateinit var dispalyD:VirtualDisplay
    private val handlerDelayImage = Handler { p0 ->
        when (p0.what) {
            0xdd -> {
                startCacheAndSetInput(p0.obj as ImageReader)
            }

        }
        true
    }
    private var contentImageView: ImageView?=null


    /**
     * 开始截屏并识别
     */
    private fun startCacheAndSetInput(p0: ImageReader) {
        var nowImage:Image?=null
        try {
            nowImage = p0.acquireLatestImage()

        }catch (e:Exception){
            nowImage = p0.acquireLatestImage()
        }
        nowImage?.apply {
            val width = 640 //可选
            val height = 640
            val planes = planes
            val buffer: ByteBuffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * width
            var bitmap: Bitmap =
                Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888)
            bitmap.copyPixelsFromBuffer(buffer)
            //这就是初始截图
            //这就是初始截图
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height)
            detector.setInput(bitmap)
            val bboxes: List<TfliteRunner.Recognition> = detector.runInference()
            val resBitmap: Bitmap =ImageProcess.drawBboxes(bboxes, bitmap, 640)
            if (bboxes.size>0){
                //对人点击
                if (bboxes[bboxes.lastIndex].title=="person"){
                    if (!personClick){
                        //人
                        //ScreenUtil.click(bboxes[bboxes.lastIndex].location.centerX(), bboxes[bboxes.lastIndex].location.centerY())
                        personClick = true
                    }
                }else{
                    personClick = false
                }
            }else{
                personClick = false
            }

            runOnUiThread {
                contentImageView?.apply {
                    setImageBitmap(resBitmap)
                }

            }
            close()
        }


    }

    private val handler = Handler { p0 ->
        Log.e("handler回调信息", p0.what.toString())
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //启动前台服务
        startService(Intent(this,MediaScreenService::class.java))
        binding.btnRecord.setOnClickListener {
             projectionManager =
                getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            val captureIntent= projectionManager.createScreenCaptureIntent();
            startActivityForResult(captureIntent,20);
        }
    }

    private fun seeContent() {
        mediaProjection?.apply {
            createDetector()//创建检测器
            createImageReader() //创建imageReader
            registerCallback(object :MediaProjection.Callback(){
                override fun onStop() {
                    super.onStop()
                }
            },handler)

            dispalyD = createVirtualDisplay("ScreenImageReader",640,640,1000
            , DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,imageReader.surface,object :VirtualDisplay.Callback(){
                    override fun onResumed() {
                        super.onResumed()
                    }

                    override fun onPaused() {
                        super.onPaused()
                    }

                    override fun onStopped() {
                        super.onStopped()
                    }
                },handler)
    /*        surfaceView.holder.addCallback(object :SurfaceHolder.Callback{
                override fun surfaceCreated(p0: SurfaceHolder) {

                }

                override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
                    dispalyD = createVirtualDisplay("ScreenImageReader",320,320,100
                        , DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,surfaceView.holder.surface,object :VirtualDisplay.Callback(){
                            override fun onResumed() {
                                super.onResumed()
                            }

                            override fun onPaused() {
                                super.onPaused()
                            }

                            override fun onStopped() {
                                super.onStopped()
                            }
                        },handler)
                }

                override fun surfaceDestroyed(p0: SurfaceHolder) {

                }

            })*/

        }


    }

    private fun createDetector() {
        detector = TfliteRunner(
            this,
           MODE,
         TF_OD_API_INPUT_SIZE,
            0.20f,
            0.40f
        )
    }

    override fun onResume() {
        super.onResume()

    }

    @SuppressLint("WrongConstant")
    private fun createImageReader() {
        imageReader = ImageReader.newInstance(640,640,
            PixelFormat.RGBA_8888, 10)
        imageReader.setOnImageAvailableListener(this,handler)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 20 && resultCode == RESULT_OK) {
           FloatUtil.showFloatWindow(application).apply {
               contentImageView = this
           }
            mediaProjection = projectionManager.getMediaProjection(resultCode, data!!);
            seeContent()
        }
    }

    /**
     * 图片更新
     */
    override fun onImageAvailable(p0: ImageReader?) {
        //利用handler
            p0?.apply {
                val message = Message()
                message.what = 0xdd
                message.obj = this
                handlerDelayImage.sendMessage(message)
                handlerDelayImage.obtainMessage()
            }
    }
}