package com.nmeylan.powermode;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ImageUtil {
  public static List<BufferedImage> imagesForPath(Optional<File> folder) {
    try {
      if (folder.isPresent()) {
        if (folder.get().exists()) {
          if (folder.get().isDirectory()) {
            return Files.walk(folder.get().toPath())
              .map(p -> {
                try {
                  return ImageIO.read(p.toFile());
                } catch (IOException e) {
                  return null;
                }
              })
              .filter(i -> i != null)
              .collect(Collectors.toList());

          } else {
            return Arrays.asList(ImageIO.read(folder.get()));
          }
        } else {
          if (folder.get().getPath().contains("bam")) {
            return Arrays.asList(ImageIO.read(ImageUtil.class.getResourceAsStream("/bam/bam.png")));
          } else if(folder.get().getPath().contains("fire")) {
            return Files.walk(Paths.get(ImageUtil.class.getClassLoader().getResource("fire/animated/256").toURI()))
              .map(p -> {
                try {
                  return ImageIO.read(p.toFile());
                } catch (IOException e) {
                  e.printStackTrace();
                  return null;
                }
              })
              .filter(i -> i != null)
              .collect(Collectors.toList());
          }
        }
      }
    } catch (IOException | URISyntaxException e) {
      e.printStackTrace();
      return null;
    }
    return null;
  }
}
