# UI Extraction with Screenshot Upload

## Summary

I've successfully implemented the functionality to capture screenshots and send extracted UI data to a server matching your specified curl format. Here's what was added:

### 1. New API Service and Models
- **GuidedFlowApiService.kt**: Retrofit interface for multipart form upload
- **GuidedFlowClient.kt**: Client implementation that captures screenshots and uploads data
- **Updated UIExtractionSDK**: Added method `sendSnapshotWithScreenshot()`

### 2. UI Updates
- Added a floating action button (FAB) with share icon in DemoScreen
- Shows upload progress dialog
- Displays success/failure messages

### 3. Network Configuration
- Configured for local server testing (http://10.0.2.2:8000 for Android emulator)
- Added network security configuration for cleartext traffic
- Added OkHttp logging for debugging

## How to Use

1. **Extract UI Elements**: Click the search icon in the app bar
2. **Upload with Screenshot**: After extraction, click the share FAB button
3. **View Upload Status**: A dialog will show the upload progress and result

## Server Format

The upload matches your exact curl format:
```bash
curl --location --request POST 'http://127.0.0.1:8000/api/guided-flow/upload/' \
--form 'screenshot=@"screenshot.png"' \
--form 'screen_name="test_screen"' \
--form 'timestamp="1753958398302"' \
--form 'screen_info="{\"width\":1080,\"height\":2376}"' \
--form 'elements="[{\"tag\":\"test.text.title\",\"elementType\":\"text\",\"tagComponents\":{\"screen\":\"test\",\"type\":\"text\",\"id\":\"title\"},\"bounds\":{\"position\":{\"x\":100,\"y\":100},\"size\":{\"width\":200,\"height\":50}},\"properties\":{\"isClickable\":false,\"isVisible\":true}}]"'
```

## Testing

1. Start your local server on port 8000
2. Run the app on an Android emulator
3. Extract UI elements
4. Click the upload button
5. Check server logs for the received data

## Configuration

To change the server URL, modify:
- `/app/src/main/java/com/cd/extracttagapp/di/UIExtractionModule.kt` - Change `serverUrl`
- `/app/src/main/java/com/cd/extracttagapp/sdk/network/GuidedFlowClient.kt` - Change `baseUrl` default

## Troubleshooting

- If using a physical device instead of emulator, replace `10.0.2.2` with your computer's local IP
- Ensure your server is running and accessible
- Check Android Studio logcat for detailed HTTP request/response logs