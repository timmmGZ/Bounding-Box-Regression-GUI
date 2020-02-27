# Bounding-Box-Regression-GUI
This program shows how Bounding-Box-Regression works in a visual form.
## Updating soon for Digit-Recognition-CNN-and-ANN-using-Mnist-with-GUI
https://github.com/timmmGZ/Digit-Recognition-CNN-and-ANN-using-Mnist-with-GUI  
I am going to modify the above program in my free time, add this Bounding Box Regression along with ROI Align layer(https://github.com/timmmGZ/ROIAlign-Bounding-Box-ROI-Align-of-Mask-RCNN-GUI) in it, make it become a MNIST object detection.
## First of all, Let's see how it works
![image](https://github.com/timmmGZ/Bounding-Box-Regression-GUI/blob/master/images/BoundingBoxRegression.gif)
## Predefine
Download the MNIST digit 60000 train set and 10000 test set here:  
https://drive.google.com/open?id=1VwABcxX0DaQakPpHbMaRQJHlJf3mVONf  
1. Put both files to ../dataset/, and then go to "tool" package.  
2. Run the "CreatePictureForObjectDetectionFromMNIST.java", it will create random pictures base on MNIST datasets in ../dataset/pictures.  
3. After step 2, run the "CreateForeOrBackgroundSample.java", it will create random foreground(in ../dataset/foreground) and background(in ../dataset/background) datasets based on output pictures of step 2, you could see it more clearly in ../dataset/groundTruthExamples.  
as below:  
![image](https://github.com/timmmGZ/Bounding-Box-Regression-GUI/blob/master/images/files.jpg)

4. Both step 2 and 3 will create Label-files in ../dataset/standardOutput, you could see the column names in first line of each Label-files  
5. Make sure you have big enough RAM if you want to store more datasets in RAM, watch below picture:  
![image](https://github.com/timmmGZ/Bounding-Box-Regression-GUI/blob/master/images/predefine.jpg)
## Start
Run the MainFrame.java, if you don't want to train the model, click "Menu" then "Read Weight", that is my trained weights around 94% accuracy on both train and test set(70000 datasets in total, I define it is true prediction if Predicted-Bounding-Box has higher IOU with Ground-Truth-Bounding-Box than it has with input Bounding-Box).  
## Warning
Actually the number of predicted boxes should = the number of classes(e.g. one Bounding-Box can have both Apple and Bird inside it), but this program is just a Digit-Detection, for convenience and higher FPS in real-time detection, I make it have only one predicted box, see the advantage of having normal number of predicted boxes as below picture:  
![image](https://raw.githubusercontent.com/timmmGZ/Bounding-Box-Regression-GUI/master/images/multi%20predicted%20box.bmp)
## Bounding-Box-Regression is used after NMS(Non Maximum Suppression)
For each picture, we will get so many Bounding-Box(Region-Propasal), NMS is used for filtering out the best Bounding-Boxes, below gif shows what will look like if we only use ROI-Align-layer(not necessary but better use it) and NMS but not use Bounding-Box-Regression:  
Predefine about ROI-Align-layer: sample size=1, output size=7, feature maps=16(https://github.com/timmmGZ/ROIAlign-Bounding-Box-ROI-Align-of-Mask-RCNN-GUI)
![image](https://github.com/timmmGZ/Bounding-Box-Regression-GUI/blob/master/images/after%20NMS%20before%20BBOX%20Regression.gif)
Let's cut one picture from the gif, when the object is small like below picture, and the number of objects is big, obviously we need to do Bounding-Box-Regression, or it will be a mess.
![image](https://raw.githubusercontent.com/timmmGZ/Bounding-Box-Regression-GUI/master/images/example.bmp)
