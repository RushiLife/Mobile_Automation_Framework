## Key Features 
1. **Platform Switching from Hooks** 
- Pass platform dynamically (Android or iOS) from the **Hook class** before test execution. 
Example:
String platform = System.getProperty("platform", "Android");

2. **Android Execution** 
- Requires **Appium Desktop GUI Server**. ;


3. **iOS Execution** - Requires **Appium server via terminal**.
- Run: appium --allow-cors
- This ensures iOS WDA sessions work seamlessly. 


4. **Automatic Emulator/Simulator Handling** 
- No need to **Manually launch Android Emulators or iOS Simulators**. 
- Framework automatically starts the respective emulator/simulator if not running.

## Notes:

1. Touch Action API is deprecated
2. No Need to Pass "/wd/hub" remote path for iOS