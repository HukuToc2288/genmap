import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import java.util.concurrent.ThreadLocalRandom
import javax.imageio.ImageIO
import kotlin.math.*


fun main(args: Array<String>) {
    Main(args)
}

class Main(args: Array<String>) {
    val mapSize = 200
    val fieldSize = mapSize*2+1
    val mapArray = Array(mapSize * 2 + 1){Array(mapSize * 2 + 1){'.'} }

    var currentPosX = mapSize
    var currentPosY = ThreadLocalRandom.current().nextInt(mapSize, mapSize * 2)

    var currentAbsoluteAngle = 0.0

    init {
        mapArray[currentPosX][currentPosY] ='@'
        makeNextDot(mapSize * 6)
        for (a in mapArray){
            for (b in a){
                print(b)
                print(' ')
            }
            println()
        }
        writeToImage(mapArray)
    }

    fun makeNextDot(remains: Int){
        var distanceFromCenter = calculateDistanceFromCenter(currentPosX, currentPosY)
        var currentRelativeAngle = ThreadLocalRandom.current().nextDouble((calculateFarCorrection(distanceFromCenter)), calculateBaseCorrection(distanceFromCenter))
        var nextDistanceFromCenter = calculateNextDistanceFromCenter(distanceFromCenter, PI - currentRelativeAngle)
        currentAbsoluteAngle -= calculateNextRelativeAngle(distanceFromCenter, nextDistanceFromCenter)
        currentPosX = calculateNextX(nextDistanceFromCenter, currentAbsoluteAngle)+mapSize
        currentPosY = calculateNextY(nextDistanceFromCenter, currentAbsoluteAngle)+mapSize
        mapArray[currentPosX][currentPosY] = '@'
        if (currentPosX == mapSize && currentPosY>mapSize)
            return
        makeNextDot(remains - 1)
    }

    fun calculateDistanceFromCenter(x: Int, y: Int): Int{
        return sqrt((x - mapSize).toDouble().pow(2) + (y - mapSize).toDouble().pow(2)).toInt()
    }

    fun calculateAbsoluteAngle(x: Int, y: Int): Double {
        return atan2(mapSize - x.toDouble(), mapSize - y.toDouble())+PI
    }

    fun calculateBaseCorrection(distance: Int): Double{
        return PI/mapSize*distance
    }

    fun calculateFarCorrection(distance: Int): Double{
        val cor = PI/mapSize*(distance-mapSize/2)
        return if (cor>0) cor else 0.0
    }

    fun toDegrees(radians: Double): Double{
        return radians*180/ PI
    }

    fun calculateNextDistanceFromCenter(currentDistanceFromCenter: Int, currentRelativeAngle: Double): Double{
        return sqrt(currentDistanceFromCenter * currentDistanceFromCenter + 1 - 2 * currentDistanceFromCenter * cos(currentRelativeAngle));
    }

    fun calculateNextRelativeAngle(currentDistanceFromCenter: Int, nextDistanceFromCenter: Double): Double{
        return acos((currentDistanceFromCenter * currentDistanceFromCenter + nextDistanceFromCenter * nextDistanceFromCenter - 1).toDouble() /
                (2 * currentDistanceFromCenter * nextDistanceFromCenter).toDouble())
    }

    fun calculateNextY(nextDistanceFromCenter: Double, absoluteAngle: Double): Int{
        return (nextDistanceFromCenter* cos(absoluteAngle)).roundToInt()
    }

    fun calculateNextX(nextDistanceFromCenter: Double, absoluteAngle: Double): Int{
        return (nextDistanceFromCenter* sin(absoluteAngle)).roundToInt()
    }

    fun writeToImage(array: Array<Array<Char>>){
        val image = BufferedImage(array.size, array[0].size, BufferedImage.TYPE_INT_RGB)
        for (i in array.indices){
            for (j in array[0].indices){
                image.setRGB(j, i, if (array[i][j]=='@') (0xffff0000).toInt() else -1)
            }
        }
        val ImageFile: File = File("/home/huku/Pictures/genmap/"+System.currentTimeMillis()+".png")
        try {
            ImageIO.write(image, "png", ImageFile)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}