
# PhysioConsult mobile app


PhysioConsult is a mobile application designed 
to speed up the consultation process between physiotherapists
and patients. It allows patients to create an assessment by 
adding or taking photos of anterior, posterior and side view 
of their posture. They can share these assessments with
their physiotherapists via generated access code with expiration date.
Physiotherapists can access these assessments via access codes 
given by the patient during consultations and save a patient 
to their patients' list.


## Requirements

- Android Studio
- Android SDK (Minimum SDK 24, Compile SDK 34)
- Kotlin
- Java 17
- Permissions for Camera and Storage access
- Google Play Services for Firebase Authentication


## Building

1. Clone the repository (git clone https://github.com/Jula-g/PhysioConsult.git) or download the source code.
2. Open the project in Android Studio.
3. Make sure you have the required SDK and dependencies installed.
4. Sync the project with Gradle files.
5. Run the app on an emulator or a physical device.

## Features

- Firebase Authentication (Email & Google Sign-In)
- Firestore Database 
- UI Jetpack Compose  
- CameraX for capturing images
- ML Kit for image processing 
