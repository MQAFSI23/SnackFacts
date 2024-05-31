package utils;

import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.awt.image.WritableRaster;
import java.util.concurrent.ScheduledExecutorService;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXTextField;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;

public class BarcodeScanner {
    private final Audio beep;
    private final String audioFile;
    private JFXTextField textFieldToFill;
    private OpenCVFrameGrabber grabber;
    private ScheduledExecutorService timer;
    private Canvas canvas;
    private Stage scanStage;
    private boolean scanLineDirection = true;
    private double scanLineY = 0;

    public BarcodeScanner() {
        this.audioFile = "src/main/resources/audio/beep.wav";
        this.beep = new Audio(audioFile);
    }

    public void scanBarcode(JFXTextField textFieldToFill) {
        this.textFieldToFill = textFieldToFill;
        startScanStage();
    }

    private void startScanStage() {
        scanStage = new Stage();
        scanStage.initModality(Modality.APPLICATION_MODAL);
        scanStage.setTitle("SnackFacts - Scan Barcode");

        canvas = new Canvas(350, 350);
        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, 350, 350);
        scanStage.setScene(scene);
        scanStage.setResizable(false);
        scanStage.setOnCloseRequest(event -> stopVideoStream());
        scanStage.show();

        startVideoStream();
    }

    private void startVideoStream() {
        grabber = new OpenCVFrameGrabber(0);

        try {
            grabber.start();
            adjustCameraSettings(grabber);
            timer = Executors.newSingleThreadScheduledExecutor();
            timer.scheduleAtFixedRate(this::grabFrame, 0, 33, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void adjustCameraSettings(OpenCVFrameGrabber grabber) {
        grabber.setImageWidth(480);
        grabber.setImageHeight(480);
        grabber.setFrameRate(30);
    }

    private void stopVideoStream() {
        if (timer != null) {
            timer.shutdown();
            timer = null;
        }
        if (grabber != null) {
            try {
                grabber.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            grabber = null;
        }
    }

    private void grabFrame() {
        try {
            Frame frame = grabber.grab();
            if (frame != null) {
                OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
                Mat mat = converter.convert(frame);

                if (mat != null) {
                    Mat matFlip = new Mat();
                    opencv_core.flip(mat, matFlip, 1);

                    Mat matBGR = new Mat();
                    opencv_imgproc.cvtColor(matFlip, matBGR, opencv_imgproc.COLOR_BGR2RGB);

                    Image image = matToImage(matBGR);
                    updateImageView(image);

                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                    Result result = decodeQRCode(bufferedImage);
                    if (result != null) {
                        Platform.runLater(() -> {
                            textFieldToFill.setText(result.getText());
                            stopVideoStream();
                            scanStage.close();
                          });
                        beep.play();
                    }
                }
                converter.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Image matToImage(Mat mat) {
        BufferedImage bufferedImage = new BufferedImage(mat.cols(), mat.rows(), BufferedImage.TYPE_3BYTE_BGR);
        byte[] data = new byte[mat.cols() * mat.rows() * (int) mat.elemSize()];
        mat.data().get(data);
        bufferedImage.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    private void updateImageView(Image image) {
        Platform.runLater(() -> {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.drawImage(image, 0, 0, canvas.getWidth(), canvas.getHeight());
            drawScanLine(gc);
        });
    }

    private void drawScanLine(GraphicsContext gc) {
        gc.setStroke(javafx.scene.paint.Color.WHITE);
        gc.setLineWidth(2);
        if (scanLineDirection) {
            scanLineY += 5;
            if (scanLineY > canvas.getHeight()) {
                scanLineY = canvas.getHeight();
                scanLineDirection = false;
            }
        } else {
            scanLineY -= 5;
            if (scanLineY < 0) {
                scanLineY = 0;
                scanLineDirection = true;
            }
        }
        gc.strokeLine(0, scanLineY, canvas.getWidth(), scanLineY);
    }

    private Result decodeQRCode(BufferedImage bufferedImage) {
        try {
            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            return new MultiFormatReader().decode(bitmap);
        } catch (NotFoundException e) {
            return null;
        }
    }
}

/*
 * Copyright 2009 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 /**
 * This LuminanceSource implementation is meant for J2SE clients and our blackbox unit tests.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 * @author code@elektrowolle.de (Wolfgang Jung)
 */

 final class BufferedImageLuminanceSource extends LuminanceSource {

    private static final double MINUS_45_IN_RADIANS = -0.7853981633974483; // Math.toRadians(-45.0)
  
    private final BufferedImage image;
    private final int left;
    private final int top;
  
    public BufferedImageLuminanceSource(BufferedImage image) {
      this(image, 0, 0, image.getWidth(), image.getHeight());
    }
  
    public BufferedImageLuminanceSource(BufferedImage image, int left, int top, int width, int height) {
      super(width, height);
  
      if (image.getType() == BufferedImage.TYPE_BYTE_GRAY) {
        this.image = image;
      } else {
        int sourceWidth = image.getWidth();
        int sourceHeight = image.getHeight();
        if (left + width > sourceWidth || top + height > sourceHeight) {
          throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
        }
  
        this.image = new BufferedImage(sourceWidth, sourceHeight, BufferedImage.TYPE_BYTE_GRAY);
  
        WritableRaster raster = this.image.getRaster();
        int[] buffer = new int[width];
        for (int y = top; y < top + height; y++) {
          image.getRGB(left, y, width, 1, buffer, 0, sourceWidth);
          for (int x = 0; x < width; x++) {
            int pixel = buffer[x];
  
            // The color of fully-transparent pixels is irrelevant. They are often, technically, fully-transparent
            // black (0 alpha, and then 0 RGB). They are often used, of course as the "white" area in a
            // barcode image. Force any such pixel to be white:
            if ((pixel & 0xFF000000) == 0) {
              // white, so we know its luminance is 255
              buffer[x] = 0xFF;
            } else {
              // .299R + 0.587G + 0.114B (YUV/YIQ for PAL and NTSC),
              // (306*R) >> 10 is approximately equal to R*0.299, and so on.
              // 0x200 >> 10 is 0.5, it implements rounding.
              buffer[x] =
                (306 * ((pixel >> 16) & 0xFF) +
                  601 * ((pixel >> 8) & 0xFF) +
                  117 * (pixel & 0xFF) +
                  0x200) >> 10;
            }
          }
          raster.setPixels(left, y, width, 1, buffer);
        }
  
      }
      this.left = left;
      this.top = top;
    }
  
    @Override
    public byte[] getRow(int y, byte[] row) {
      if (y < 0 || y >= getHeight()) {
        throw new IllegalArgumentException("Requested row is outside the image: " + y);
      }
      int width = getWidth();
      if (row == null || row.length < width) {
        row = new byte[width];
      }
      // The underlying raster of image consists of bytes with the luminance values
      image.getRaster().getDataElements(left, top + y, width, 1, row);
      return row;
    }
  
    @Override
    public byte[] getMatrix() {
      int width = getWidth();
      int height = getHeight();
      int area = width * height;
      byte[] matrix = new byte[area];
      // The underlying raster of image consists of area bytes with the luminance values
      image.getRaster().getDataElements(left, top, width, height, matrix);
      return matrix;
    }
  
    @Override
    public boolean isCropSupported() {
      return true;
    }
  
    @Override
    public LuminanceSource crop(int left, int top, int width, int height) {
      return new BufferedImageLuminanceSource(image, this.left + left, this.top + top, width, height);
    }
  
    /**
     * This is always true, since the image is a gray-scale image.
     *
     * @return true
     */
    @Override
    public boolean isRotateSupported() {
      return true;
    }
  
    @Override
    public LuminanceSource rotateCounterClockwise() {
      int sourceWidth = image.getWidth();
      int sourceHeight = image.getHeight();
  
      // Rotate 90 degrees counterclockwise.
      AffineTransform transform = new AffineTransform(0.0, -1.0, 1.0, 0.0, 0.0, sourceWidth);
  
      // Note width/height are flipped since we are rotating 90 degrees.
      BufferedImage rotatedImage = new BufferedImage(sourceHeight, sourceWidth, BufferedImage.TYPE_BYTE_GRAY);
  
      // Draw the original image into rotated, via transformation
      Graphics2D g = rotatedImage.createGraphics();
      g.drawImage(image, transform, null);
      g.dispose();
  
      // Maintain the cropped region, but rotate it too.
      int width = getWidth();
      return new BufferedImageLuminanceSource(rotatedImage, top, sourceWidth - (left + width), getHeight(), width);
    }
  
    @Override
    public LuminanceSource rotateCounterClockwise45() {
      int width = getWidth();
      int height = getHeight();
  
      int oldCenterX = left + width / 2;
      int oldCenterY = top + height / 2;
  
      // Rotate 45 degrees counterclockwise.
      AffineTransform transform = AffineTransform.getRotateInstance(MINUS_45_IN_RADIANS, oldCenterX, oldCenterY);
  
      int sourceDimension = Math.max(image.getWidth(), image.getHeight());
      BufferedImage rotatedImage = new BufferedImage(sourceDimension, sourceDimension, BufferedImage.TYPE_BYTE_GRAY);
  
      // Draw the original image into rotated, via transformation
      Graphics2D g = rotatedImage.createGraphics();
      g.drawImage(image, transform, null);
      g.dispose();
  
      int halfDimension = Math.max(width, height) / 2;
      int newLeft = Math.max(0, oldCenterX - halfDimension);
      int newTop = Math.max(0, oldCenterY - halfDimension);
      int newRight = Math.min(sourceDimension - 1, oldCenterX + halfDimension);
      int newBottom = Math.min(sourceDimension - 1, oldCenterY + halfDimension);
  
      return new BufferedImageLuminanceSource(rotatedImage, newLeft, newTop, newRight - newLeft, newBottom - newTop);
    }
  
  }
