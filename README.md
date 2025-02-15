# CompanionEK

CompanionEK is an AI-powered Android application that recognizes real-time facial emotions using deep learning. It provides an interactive chatbot experience and helps users track their emotional states.

## Features

- **Real-Time Facial Emotion Recognition**: Uses ML Kit's FaceDetection API and a MobileNetV2-based model for accurate emotion classification.
- **AI Chatbot**: Powered by a fine-tuned LLaMA 3.2-3B-Instruct model to provide interactive conversations.
- **User-Friendly Interface**: A simple and intuitive UI for easy navigation.
- **Data Persistence**: Integrated with Firebase Firestore for storing user interactions and insights.

## Technology Stack

- **Frontend**: Kotlin (Android)
- **Machine Learning**: TensorFlow Lite, MobileNetV2
- **Backend**: Firebase Firestore
- **AI Model**: LLaMA 3.2-3B-Instruct (Fine-tuned)

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/CompanionEK.git
   ```
2. Open the project in **Android Studio**.
3. Sync the Gradle dependencies.
4. Set up Firebase Firestore and update `google-services.json`.
5. Run the app on an Android device or emulator.

## Usage

- Launch the app and grant camera permissions.
- The app will detect facial emotions in real-time and display predictions.
- Interact with the chatbot for emotional support and insights.

## Model Details

- Trained on **FER2013 dataset**.
- Achieved **92% training accuracy** and **68.2% test accuracy**.
- Optimized for mobile inference using TensorFlow Lite.

## Contribution

Feel free to contribute by submitting issues, feature requests, or pull requests. Follow the standard GitHub workflow:

1. Fork the repository.
2. Create a new branch (`feature-xyz`).
3. Commit your changes and push to the branch.
4. Submit a pull request.

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

## Contact

For queries, feel free to reach out:
- **Developer**: Muhammad Asif
- **Email**: cop21cancer@example.com
- **LinkedIn**: https://www.linkedin.com/in/muhammad-asif923321377445/

---

Feel free to modify this README as needed!

