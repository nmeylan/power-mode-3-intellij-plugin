package de.ax.powermode.power.sound

import java.io.File
import javafx.embed.swing.JFXPanel
import javafx.scene.media.{Media, MediaPlayer}

import de.ax.powermode.PowerMode

/**
  * Created by nyxos on 03.10.16.
  */

class PowerSound(folder: => File, valueFactor: => Double) {
  val ResetPlaying: Runnable = new Runnable {
    override def run(): Unit = playing = false
  }

  def files = Option(folder.listFiles()).getOrElse(Array.empty[File]).filter(_.isFile)

  var playing = false

  var current = 1


  def setVolume(v: Double) = {
    mediaPlayer.foreach(_.setVolume((0.75*v*v)+(0.25*v)))
  }

  def stop() = {
    mediaPlayer.foreach(_.stop())
    playing = false
  }

  var mediaPlayer = Option.empty[MediaPlayer]

  var index = 0

  def play() = {
    if (!playing && files != null && !files.isEmpty) {
      try {
        playing = true
        index = (Math.random() * (200 * files.length)).toInt % files.length
        new JFXPanel
        val f = files(index)
        val hit = new Media(f.toURI.toString)
        mediaPlayer = Some {
          val mediaPlayer = new MediaPlayer(hit)
          mediaPlayer.setOnError(ResetPlaying)
          mediaPlayer.setOnStopped(ResetPlaying)
          mediaPlayer.setOnEndOfMedia(ResetPlaying)
          mediaPlayer.setVolume(valueFactor)
          mediaPlayer.play()
          mediaPlayer
        }
      } catch {
        case e =>
          PowerMode.logger.error(e.getMessage, e)
          playing = false
      }
    }
  }
}



