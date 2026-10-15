# Photoshop & Multiservice — Android starter project

## काय समाविष्ट आहे
- Home dashboard
- सेवा निवडून बिल तयार करण्याचा स्थानिक डेमो
- फक्त एकूण रक्कम टाकून Quick Bill
- नवीन सेवा जोडणे
- दुकानाचा खर्च नोंदवणे
- डेमो रिपोर्ट
- स्टॉक व्यवस्थापन नाही

## महत्त्वाची मर्यादा
ही प्रारंभिक Android source project आहे, तयार APK नाही. सध्याचा डेटा अॅप बंद केल्यावर कायम राहत नाही; Firebase cloud sync, दोन पार्टनरचे लॉगिन आणि Google Sheets backup अजून जोडलेले नाहीत. त्यासाठी Firebase project configuration, database rules आणि Google Sheets साठी सुरक्षित server-side integration आवश्यक आहे. गुप्त API keys APK मध्ये ठेवू नयेत.

## APK कसे तयार करावे
1. संगणकावर Android Studio स्थापित करा.
2. Android Studio मध्ये `Photoshop_Multiservice_Android_Project` फोल्डर उघडा.
3. Gradle sync पूर्ण होऊ द्या; आवश्यक Android SDK डाउनलोड करा.
4. `Build > Build Bundle(s) / APK(s) > Build APK(s)` निवडा.
5. APK सामान्यतः `app/build/outputs/apk/debug/app-debug.apk` येथे मिळेल.

## पुढील production काम
- Firebase Authentication: दोन पार्टनरचे स्वतंत्र लॉगिन
- Cloud Firestore: विक्री/खर्च/सेवा यांचे real-time sync
- Google Sheets backup: सुरक्षित server-side function, प्रत्येक नोंदीसाठी unique ID, retry queue आणि sync status
- कायमस्वरूपी स्थानिक डेटाबेस व error handling
- प्रत्यक्ष दोन मोबाइलवर चाचणी आणि signed release APK

हे source starter आहे; production-ready अॅप किंवा Google Sheets शी जोडलेले असल्याचा दावा नाही.
