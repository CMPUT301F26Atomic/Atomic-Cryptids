# Atomic-Cryptids

## Team Members
* Colton Tumoth
* Seps Sepahvand
* Anshul Sharma
* Khush Harman Singh Sekhon
* Mason Fenna
* Luke Frayne

## AI Tools

Various generative AI tools such as OpenAI ChatGPT, Anthropic Claude, and Google Gemini were used for development assistant throughout the project to support the following activities:

- Code development: Assisted with implementing and refining Kotlin functionality, including campsite filtering, campsite management, lottery editing, and Firestore integration.

- Debugging: Helped identify compilation errors, resolve Git merge conflicts, and troubleshoot Android Studio and Firebase issues.

- Code review: Reviewed implementations for potential bugs, incomplete functionality, and consistency with user-story acceptance criteria.

Disclosure for specific sections of code can be found within the comments in the kotlin code.
## Project Documentation
All design deliverables, user stories, mockups, and CRC cards can be found on our [Wiki Page](https://github.com/CMPUT301F26Atomic/Atomic-Cryptids/wiki)


## Android Application Setup



### Requirements

- Android Studio
- Android SDK (API 24 or higher)
- Java Development Kit (JDK) compatible with the project's Gradle configuration
- Access to the Atomic-Cryptids Firebase project

### 1. Clone the Repository

Clone the repository using Git:


git clone https://github.com/CMPUT301F26Atomic/Atomic-Cryptids.git


### 2. Open the Android Project

1. Launch Android Studio.
2. Select Open
3. Navigate to the repository.
4. Open the App directory.
5. Sync gradle and download the required dependencies.

### 3. Configure Firebase

The application uses Firebase Authentication and Cloud Firestore.

The `google-services.json` configuration file is excluded from Git and must be downloaded separately.

1. Open the Firebase Console
2. Select the Atomic-Cryptids project
3. Navigate to Project Settings → General
4. Under "Your apps", select Campsite App
5. Download `google-services.json`
6. Place the file at:

   `App/app/google-services.json`



### 4. Build and Run

1. Open the App project in Android Studio
2. Sync Gradle if prompted
3. Select Build → Make Project to verify compilation
4. Start an Android emulator or connect a physical Android device
5. Click Run

### 5. Firebase and Firestore

The application is configured to use:

- Firebase Authentication: Anonymous authentication
- Cloud Firestore: Database storage
- Firebase project: Atomic-Cryptids
- Android application ID: `com.atomiccryptids.campsiteapp`

