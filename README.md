# AI Powered Keyboard 🤖⌨️

An intelligent Android keyboard application that leverages AI to enhance typing experience with smart suggestions, grammar correction, and multiple keyboard layouts.

## 🌟 Features

### 🧠 AI-Powered Intelligence
- **Smart Suggestions**: Context-aware word predictions powered by AI
- **Grammar Correction**: Real-time grammar and spelling fixes
- **Next Word Prediction**: Intelligent word completion based on context
- **Content Rephrasing**: Rephrase text with different tones and styles
- **Smart Replies**: Quick response suggestions for messages

### ⌨️ Multiple Keyboard Layouts
- **Alphabetic Keyboard**: Standard QWERTY layout with uppercase/lowercase/caps lock modes
- **Numeric Keyboard**: Dedicated number pad for easy numeric input
- **Symbol Keyboard**: Common symbols and punctuation marks
- **Extended Symbol Keyboard**: Mathematical symbols, currency, and special characters

### 🎨 Customization
- **Multiple Themes**: Light and Purple themes with customizable colors
- **Haptic Feedback**: Configurable vibration on key press
- **Sound Effects**: Optional key press sounds
- **Responsive Design**: Adaptive layout for different screen sizes

### 🔧 Advanced Features
- **Cursor Movement**: Swipe on spacebar to move cursor
- **Auto-correction**: Built-in spelling correction
- **Suggestion Bar**: Real-time word suggestions above keyboard
- **Multi-language Support**: Extensible language support system

## 🏗️ Architecture

The project follows **Clean Architecture** principles with clear separation of concerns

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Koin
- **Networking**: Ktor Client
- **Data Storage**: DataStore Preferences
- **State Management**: StateFlow & Compose State
- **Build System**: Gradle with Kotlin DSL

## 📱 Installation

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24+ (Android 7.0)
- Kotlin 1.9+

### Setup
1. Clone the repository:
```bash
git clone https://github.com/yourusername/AIPoweredKeyboard.git
cd AIPoweredKeyboard
```

2. Create a `local.properties` file in the root directory and add your API key:
```properties
API_KEY=your_api_key_here
```

3. Open the project in Android Studio

4. Build and run the application

### Enable the Keyboard
1. Install the app on your device
2. Go to **Settings > System > Languages & input > Virtual keyboard**
3. Tap **Manage keyboards** and enable **AI Powered Keyboard**
4. Select the keyboard when typing in any app

## 🎯 Usage

### Basic Typing
- Tap keys to type characters
- Use shift key to toggle between lowercase/uppercase/caps lock
- Tap "123" to switch to numeric keyboard
- Tap "!@#" to access symbol keyboards

### AI Features
- **Suggestions**: Tap on suggestion chips above the keyboard
- **Grammar Correction**: Automatic corrections are applied as you type
- **Next Word Prediction**: Suggestions appear based on context
- **Cursor Movement**: Swipe left/right on spacebar to move cursor

### Customization
- Access settings through the main app
- Choose between Light and Purple themes
- Toggle haptic feedback and sound effects
- Adjust AI suggestion preferences

## 🔧 Configuration

### Dependencies
Key dependencies include:
- Jetpack Compose BOM
- Koin for dependency injection
- Ktor for networking
- DataStore for preferences
- Material Icons Extended

### Build Configuration
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 36
- **Compile SDK**: 36
- **Java Version**: 11

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Style
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add comments for complex logic
- Ensure proper error handling

## 🙏 Acknowledgments

- Android Jetpack Compose team for the modern UI toolkit
- Koin team for the lightweight dependency injection framework
- Ktor team for the networking client
- The Android community for continuous support and inspiration

## 📞 Support

If you encounter any issues or have questions:
1. Check the [Issues](https://github.com/ronyaburaihan/AIPoweredKeyboard/issues) page
2. Create a new issue with detailed description
3. Contact the maintainers

---

**Made with ❤️ using Jetpack Compose**