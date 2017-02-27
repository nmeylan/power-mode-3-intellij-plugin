package de.ax.powermode.power.element

import java.awt._
import java.awt.image.BufferedImage

import com.intellij.openapi.editor.Editor
import de.ax.powermode.power.ElementOfPower
import de.ax.powermode.{PowerMode, Util}

import scala.annotation.tailrec
import scala.collection.mutable

/**
  * Created by nyxos on 25.02.17.
  */

object PowerIndicator {
  var indicators = mutable.Queue.empty[PowerIndicator]

  def addIndicator(i: PowerIndicator): Unit = {
    this.synchronized {
      PowerIndicator.indicators += i
      PowerIndicator.indicators = PowerIndicator.indicators.filter(i => i.alive)
    }
  }

  val grands = Seq("perfect",
    "excellent",
    "superb",
    "sublime",
    "dominating",
    "marvelous",
    "splendid",
    "majestic",
    "unreal",
    "fabulous",
    "great")

  var lastGrand = Option.empty[String]

  def genGrand: String = {
    @tailrec
    def nextGrand(lastGrand: Option[String]): String = {
      val grand = grands((Math.random() * 100023451).toInt % grands.length) + "!"
      if (lastGrand.contains(grand)) {
        nextGrand(lastGrand)
      } else {
        grand
      }
    }

    if (math.random < Seq(0.01, 0.2 * PowerMode.getInstance.valueFactor).max) {
      val grand = nextGrand(lastGrand)
      lastGrand = Some(grand)
      grand
    } else {
      ""
    }
  } 
}

case class PowerIndicator(_x: Float, _y: Float, _width: Float, _height: Float, initLife: Long, editor: Editor) extends ElementOfPower {
  val identifier = System.currentTimeMillis() + (Math.random() * 1000000)
  var diffLife = Option.empty[Long]
  var x: Double = _x
  var y: Double = _y
  var width: Double = 0
  var height: Double = 0
  PowerIndicator.addIndicator(this)
  val life2 = System.currentTimeMillis() + initLife
  val grand=PowerIndicator.genGrand

  override def life = {

    if (isLast) {
      math.max(life2, System.currentTimeMillis() + (initLife * 0.75)) toLong
    } else {
      diffLife = Some(diffLife.getOrElse(System.currentTimeMillis() + (initLife * 0.75) toLong))
      diffLife.get
    }
  }


  def isLast: Boolean = {
    PowerIndicator.indicators.lastOption.exists(_.identifier == identifier)
  }

  //
  //  override def lifeFactor: Float = {
  //    if(isLast) {
  //      math.max(super.lifeFactor,0.5)
  //    } else{
  //      super.lifeFactor
  //    }
  //
  //  }

  override def update(delta: Float): Boolean = {
    if (alive) {
      x = _x + (0.5 * _width) - (0.5 * _width * (1 - lifeFactor))
      y = _y + (0.5 * _height) - (0.5 * _height * (1 - lifeFactor))
      width = _width * (1 - lifeFactor)
      height = _height * (1 - lifeFactor)
    }
    !alive
  }

  var lastScrollPosition = Option.empty[(Int, Int)]

  override def render(g: Graphics, _dxx: Int, _dyy: Int): Unit = {
    def limit(v: Int, max: Int): Int = {
      //      if (v > max) max else if (v < -max) -max else v
      v
    }

    if (alive) {
      val Some((dxx, dyy)) = lastScrollPosition.map(lp => {
        val (nx, ny) = (editor.getScrollingModel.getHorizontalScrollOffset, editor.getScrollingModel.getVerticalScrollOffset)
        (lp._1 - nx, lp._2 - ny)
      }).orElse(Some(0, 0)).map { case (x, y) => (limit(x, 100), limit(y, 100)) }
      //val Some((dxx, dyy)) =Some((0,0))
      val g2d: Graphics2D = g.create.asInstanceOf[Graphics2D]
      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
        Util.alpha(1f * (1 - lifeFactor) * (1 - lifeFactor))))
      //      println(s"${this.identifier} alife $alive last $isLast $x $y $width $height #### ${_width} ${_height}")

      val bufferedImage = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB)
      val graphics = bufferedImage.getGraphics
      //      graphics.drawImage(Util.powerBamImage, 0, 0, null)
      drawIndicator(graphics.asInstanceOf[Graphics2D], bufferedImage.getWidth, bufferedImage.getHeight)
      g2d.drawImage(bufferedImage, math.max(x, 0) - dxx toInt, math.max(y, 0) - dyy toInt, width toInt, height toInt, null)
      g2d.dispose()
      lastScrollPosition = Some((editor.getScrollingModel.getHorizontalScrollOffset, editor.getScrollingModel.getVerticalScrollOffset))
    }
  }

  private def drawIndicator(graphics: Graphics2D, width: Int, height: Int) = {
    graphics.setColor(Color.darkGray)
    graphics.fillRect(10, 10, width - 10, 200)
    graphics.setColor(Color.white)
    graphics.setFont(new Font("Dialog", Font.PLAIN, 100))
    graphics.drawString((powerMode.rawValueFactor * 100).toInt.toString + " %", 10, 100)
    graphics.setColor(Color.white)
    graphics.drawString(grand, 10, 200)
    graphics.setColor(Color.white)
    var f = math.min(powerMode.rawValueFactor, 20 + (powerMode.rawValueFactor % 1))
    var max: Double = math.ceil(f)
    val maxLines = 8

    val maxYSpace = 30 * maxLines / max
    var barHeight = math.min(50, 0.75 * maxYSpace) toInt
    var barSpace = math.min(17, 0.25 * maxYSpace) toInt

    while (f > 0) {
      graphics.setColor(Color.white)
      graphics.fillRect(10, height - (((max.toInt + 1) - math.ceil(f)) * (barSpace + barHeight)) toInt, width * (if (f >= 1) 1 else f) toInt, barHeight)
      graphics.setColor(Color.black)
      graphics.setStroke(new BasicStroke(10))
      graphics.drawRect(9, height - (((max.toInt + 1) - math.ceil(f)) * (barSpace + barHeight)) - 1 toInt, width * (if (f >= 1) 1 else f) - 1 toInt, barHeight - 1)
      f -= 1
    }
  }
}
