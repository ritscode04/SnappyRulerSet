package com.example.snappyrulerset.export

import android.graphics.Bitmap
import java.io.FileOutputStream

object ExportUtils {
    fun saveBitmapToPNG(bmp: Bitmap, path: String) {
        val fos = FileOutputStream(path)
        bmp.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.flush(); fos.close()
    }
}
