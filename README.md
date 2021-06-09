<h1><b>Bangkit Capstone Project</b></h1>
This is a Capstone Project for Bangkit Program 2021

- <b>Project’s Name</b>	: TrashEd (Trash Education)
- <b>Theme</b>			: Education & Research

<b>Our teams:</b>
- Hans Anderson – Mobile Programming (Android)
- Vony Ayu Anggraeni – Mobile Programming (Android)
- Qolby Fahrul Rizky – Cloud Computing
- Berlian Alvina – Cloud Computing
- Hansen Hanestan – Machine Learning
- Achmad Fachturrohman – Machine Learning

In this project, we use garbage classification data to distinguish the types of garbage according to the photo or image that was inputted. The output of our application is the classification types of the garbage as well as articles and supporting educational video.

We use transfer learning for our image classification methods. The model has been trained using ResNet50V2 architecture. Also we added data augmentation and dropout layers to avoid the overfitting. Furthermore, the callback has been added to to reach up to 99% accuracy in the training session. The model was built to obtain up to 93% validation accuracy.

Dataset that we used from: https://www.kaggle.com/asdasdasasdas/garbage-classification

Our project has 3 workflow environments to produce usable applications. The explanations of each are follows:

<b>Machine Learning Workflow:</b>
1. Download the datasers        
2. The machine learning model was done in google colaboratory, you can see the process of our model making in [Final_Project_Bangkit_Resnet_Tuning_Process_V4.ipynb](https://github.com/HansAnderson19/Trash-education/blob/Machine_Learning/Final_Project_Bangkit_Resnet_Tuning_Process_V4.ipynb). 
3. Splitting the dataset into train, validation and test split, see the datasets that already splitted on [Machine_Learning](https://github.com/HansAnderson19/Trash-education/tree/Machine_Learning) branch.
4. At first, our model could classify 6 image categories, but when we did some experimental tests, there were many misclassifications on the “Trash” category. So, we choose to only use 5 image categories which are “Cardboard”, “Paper”, “Metal”, “Plastic” and “Glass”.
5. Designing ResNet50V2 architecture using transfer learning. On the last layer, we added dropout layers, a dense layer with 1024 neurons and a dense layer of 5 neurons as the classification layer. We also used callbacks to to reach up to 99% training accuracy.
6. The model generates 99% accuracy on training and 92% on validation dataset.
7. Next, the model was evaluated on a testing dataset and produced 93% accuracy. There is little difference on the accuracy between validation dataset and testing dataset.
8. The ResNet50V2 model is then saved as a TFlite extension using a TFlite converter provided by TensorFlow. This TFlite model is the one to be deployed on android and firebase.
9. The Dataset, notebook and the saved tflite model has been uploaded to our GitHub repository in [Machine_Learning](https://github.com/HansAnderson19/Trash-education/tree/Machine_Learning) branch.

<b>Cloud Computing Workflow:</b>
1. Making a new Project on Firebase.
2. Adding an App to Firebase Project.
    - Register App by inputting package name
    - Download config file
    - Adding Firebase SDK
    - you can see the documentation [here.](https://firebase.google.com/docs/ml/android/use-custom-models)
3. Setting up the service that we want to use, in this case we use <b>Cloud Storage</b> for saving image and ML model, <b>Real-time Database</b> to store Article data and Image URI, <b>Cloud Function</b> to trigger Firebase Storage whenever a new image uploaded will upload Image data Real-time Database.
See the documentation for each service here:
    - [Cloud Storage](https://cloud.google.com/storage/docs/introduction)
    - [Firebase Realtime Database](https://firebase.google.com/docs/database)
    - [Cloud Functions](https://cloud.google.com/functions/docs/quickstarts)
4. Deploying custom model with tflite format to <b>ML Kit</b> in Firebase. See the documentation for [Firebase ML_Kit](https://firebase.google.com/docs/ml-kit)
5. Making Function to download the model in Android app.
6. Making Predict Function to send image to ML model and retrieve data from ML model in Android App.
> You can see how we implement the function [here](https://github.com/HansAnderson19/Trash-education/blob/integrate-frontend-backend/app/src/main/java/com/trashed/trasheducation/ui/MainActivity.kt). Start from line 88 to see how to download the model, and from line 230 for the predict functions.

<b>Mobile Programming Workflow:</b>
1. Set Up some dependencies that will be used for application.
2. Make a layout prototype for our UI android, also make SplashScreen for application. See our prototype design [here](https://github.com/HansAnderson19/Trash-education/blob/front-end/TrashEd%20Assets/TrashEd%20Assets.xd). Use adobe XD to see the file.
3. Make camera and gallery function to take a picture for trash predict function.
4. Install download model and predict function into activity for main function in application.
    > you can see the implemented function for camera, gallery, and predict in this [file](https://github.com/HansAnderson19/Trash-education/blob/integrate-frontend-backend/app/src/main/java/com/trashed/trasheducation/ui/MainActivity.kt)
5. Create Remote Storage that get data source from RealTime Database from Firebase.
6. Set ViewModel, ViewModel Factory and Injection for data distributor.
7. SetUp library and Create VideoPlayer function for youtube player, we use this library to create the video player.
    -  youtube video player library: https://github.com/PierfrancescoSoffritti/android-youtube-player
8. Create WebView layout for article material from link.
    > see how we make the article activity in this [file](https://github.com/HansAnderson19/Trash-education/blob/integrate-frontend-backend/app/src/main/java/com/trashed/trasheducation/ui/ArticleActivity.kt).
9. Improved layout and clean some unused layout.
10. Make Loading function for download model and Article layout.
