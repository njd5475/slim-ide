package com.njd5475.github.slim.view;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Robot;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.njd5475.github.slim.controller.SlimController;
import com.njd5475.github.slim.model.SlimFileContext;
import com.njd5475.github.slim.model.SlimFileWrapper;

public class SlimIDE {

	public static final boolean DEVELOPMENT = true;

	static {
		System.setProperty("sun.java2d.opengl", "True");
	}

	public static long start = System.currentTimeMillis();
	private static JFrame frame;

	public static void main(String[] args) {
		SlimFileContext fileContext = new SlimFileContext(args);
		SlimSettings defaults = SlimSettings.loadDefaults();
		frame = new JFrame("SlimIDE");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		DefaultAttachableRenderer renderer = new DefaultAttachableRenderer();
		SlimController controller = new SlimController(renderer, fileContext);
		SlimEditor editor = new SlimEditor(defaults, controller);
		editor.addEditorListener(new EditorListener() {
			@Override
			public void filesShownChanged(Set<SlimFileWrapper> filesShown2) {
				String files = "";
				int i = 0;
				for(SlimFileWrapper file : filesShown2) {
					if(i == 0) {
						files += " - ";
					}
					files += file.getFile().getName();
					if(i < filesShown2.size()-1) {
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
