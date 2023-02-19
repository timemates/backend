package io.timemates.backend.data.users

import io.timemates.backend.users.repositories.ImageCompressorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import javax.imageio.ImageIO

class ImageIOCompressorRepository : ImageCompressorRepository {
    override suspend fun compress(
        inputStream: InputStream,
        width: Int,
        height: Int,
    ): OutputStream? = try {
        withContext(Dispatchers.IO) {
            // Read the input image from the InputStream
            val inputImage = ImageIO.read(inputStream)

            // Create a new BufferedImage with the desired size
            val outputImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

            // Draw the input image onto the new BufferedImage
            val graphics2D = outputImage.createGraphics()
            graphics2D.drawImage(inputImage, 0, 0, width, height, null)
            graphics2D.dispose()

            // Write the compressed image to a new ByteArrayOutputStream
            val outputStream = ByteArrayOutputStream()
            ImageIO.write(outputImage, "jpeg", outputStream)

            return@withContext outputStream
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}