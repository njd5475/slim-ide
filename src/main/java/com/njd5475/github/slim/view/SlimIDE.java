package com.njd5475.github.slim.view;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.njd5475.github.slim.controller.SlimController;
import com.njd5475.github.slim.material.IMaterial;
import com.njd5475.github.slim.material.Screen;
import com.njd5475.github.slim.material.builder.MaterialWidgetBuilder;
import com.njd5475.github.slim.model.SlimFileWrapper;
import com.njd5475.github.slim.model.SlimFilesContext;

public class SlimIDE {

  public static final boolean DEVELOPMENT = true;

  static {
    System.setProperty("sun.java2d.opengl", "True");
  }

  public static long    start = System.currentTimeMillis();
  private static JFrame frame;

  public static void main(String[] args) {
    SlimFilesContext fileContext = new SlimFilesContext(args);
    SlimSettings defaults = SlimSettings.loadDefaults();
    frame = new JFrame("SlimIDE");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    DefaultAttachableRenderer renderer = new DefaultAttachableRenderer();
    SlimController controller = new SlimController(renderer, fileContext);
    SlimEditor editor = new SlimEditor(defaults, controller);
    editor.addWidgets(loadWidgets(frame, editor));
    editor.addEditorListener(new EditorListener() {
      @Override
      public void filesShownChanged(Set<SlimFileWrapper> filesShown2) {
        String files = "";
        int i = 0;
        for (SlimFileWrapper file : filesShown2) {
          if (i == 0) {
            files += " - ";
          }
          files += file.getFile().getName();
          if (i < filesShown2.size() - 1) {
            files += ", ";
          }
          ++i;
        }
        frame.setTitle("SlimIDE" + files);
      }
    });
    frame.setLayout(new BorderLayout());
    frame.add(editor);
    frame.pack();
    if (defaults.isCentered()) {
      frame.setLocationRelativeTo(null);
    } else {
      frame.setLocation(defaults.getWindowLocation());
    }
    frame.setVisible(true);
    editor.requestFocus();
    new Thread(fileContext).start();
  }

  private static Set<IMaterial> loadWidgets(JFrame frame, SlimEditor editor) {
    Set<IMaterial> materials = new LinkedHashSet<>();
    MaterialWidgetBuilder builder = new MaterialWidgetBuilder(frame, materials);
    builder.top(10);
    builder.minHeight(25);
    builder.fill(new Color(255, 0, 0, 100));
    builder.dynamicText((new File(".").getAbsolutePath()));
    builder.build();
    return materials;
  }

  public static void takeScreenshot() {
    try {
      Robot robot = new Robot();

      // The hard part is knowing WHERE to capture the screen shot from
      BufferedImage screenShot = robot.createScreenCapture(frame.getBounds());

      // Save your screen shot with its label
      System.out.println("Taking the screenshot");
      ImageIO.write(screenShot, "png", new File("./images/screenshot.png"));
    } catch (IOException e1) {
      e1.printStackTrace();
    } catch (AWTException e2) {
      e2.printStackTrace();
    }
  }

}
