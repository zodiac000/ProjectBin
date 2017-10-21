# Architecture and API documentation
## Acknowledgements

Code has been derived from the following sources:
- OpenCV - https://github.com/opencv/opencv
- LIBSVM - https://github.com/cjlin1/libsvm
- AndroidLibSvm - https://github.com/yctung/AndroidLibSvm
- TensorFlow - https://github.com/tensorflow/tensorflow
- facenet - https://github.com/davidsandberg/facenet/pull/240
- Caffe - https://github.com/BVLC/caffe
- caffe-android-demo - https://github.com/sh1r0/caffe-android-demo
- caffe-android-lib - https://github.com/sh1r0/caffe-android-lib

## Library
The core functions can be found under facial recognition library.

## App architecture
![alt tag](https://github.com/Bin2015/ProjectBin/blob/master/Android-Face-Recognition-with-Deep-Learning-Test-Framework-master/AppArchitecture.png)

## Detection
### Viola-Jones-Algorithm

## Preprocessing
### Crop.java
> Libraries used: OpenCV (Mat, Rect), ch.zhaw (Command, Preprocessor)

```java
/**
 * Converts images into Mat objects then uses submat to reformat them.
 * 
 * @param preProcessor object
 * @return preProcessor with cropped images
 */
```

### Eye Alignment.java
> Libraries used: OpenCV (Mat, MatOfFloat, Point, Size, Imgproc), ch.zhaw (Eyes, Command, Preprocessor)
```java
private static final double DESIRED_RIGHT_EYE_X = 0.24;
private static final double DESIRED_RIGHT_EYE_Y = 0.30;
private static final double DESIRED_LEFT_EYE_X = (1.0 - DESIRED_RIGHT_EYE_X);
/**
 * 
 * 
 * @param preProcessor object
 * @return preProcessor object
 */
```

### Grayscale
> Libraries used: OpenCV (Mat, Imgproc), ch.zhaw (Command, Preprocessor)

### Gamma Correction
> Libraries used: OpenCV (Mat, Core, CvType, Scalar), ch.zhaw (Command, Preprocessor)

### Difference of Gaussians
> Libraries used: OpenCV (Mat, MatOfFloat, Point, Size, Imgproc), ch.zhaw (Eyes, Command, Preprocessor)


### Local Binary Pattern
> Libraries used: OpenCV (Mat, Size, Imgproc), ch.zhaw (PreferencesHelper, Command, Preprocessor)


### Canny-Filter
> Not found.

### Histogram Equalization
> Libraries used: OpenCV (Mat, CvType, Imgproc), ch.zhaw (Command, Preprocessor)


### Resize
> Libraries used: OpenCV (Mat, Size, Imgproc), ch.zhaw (PreferencesHelper, Command, Preprocessor)

## OpenCV
### Mat
Mat is basically a class with two data parts: the matrix header (containing information such as the size of the matrix, the method used for storing, at which address is the matrix stored, and so on) and a pointer to the matrix containing the pixel values (taking any dimensionality depending on the method chosen for storing). The matrix header size is constant, however the size of the matrix itself may vary from image to image and usually is larger by orders of magnitude.


