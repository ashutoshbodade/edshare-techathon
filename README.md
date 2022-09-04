# Ed-Share tech-a-thon

Ed-Share is a media sharing app for student where student can share assignment, notes, tutorial, notice, etc in any format with fellow students publically specific to their institute or in a private chat.

Edshare app is build for android using firebase as backend, kotlin as prog language, fcm and firebase cloud functions for push notification and jetpack components like navigation graph, view binding, etc.

If any students uploads a media content then all the students in a institute will get notified this we have done using firebase cloud messaging service.
Students can chat or share any other media in private chat room, and they will get realtime send, read, delivered receipt.

to use the application student need to follow this steps
-login with mobile number and otp
-if student is not registered then he need to enter all other basic details like name, institute name, stream, profile pic, etc.
-once student registered he will get automatically subscribed to selected univercity for push notification and he will receive all the notifications.
-now student can see the institute wall where he can browse all the media content uploaded by fellow student and he can download that by clicking on image / document view
-student can click onsend new file to all button to post media publically in a perticular univercity, he need to upload the file in any type document, video, image, etc and need to selet the purpose like  assignment, notes, notice, etc and then he can publish the media content to every student in a institute.
-if student want to chat to user then he can click on student name on wall and then he can chat in private and also can share media content in private.
-in chat fragment student can see all the recent chat unread messages count and he can also search the student in same institute and then they chat and share media regarding assignment, notes, tutorial, notice, etc, and if private chat then student will get realtime chat receipt send, delivered and read

Technlogies used
- Kotlin as programming languange
- Android as app platform
- Firebase for storage and database
- FCM for push notifications
- Firebase Cloud Functions for pushing notification to subscribed topic

# key features
- chat privately to all students in perticular institute
- realtime chat receipts like send, delivered, read
- send or post publically any type of file like document, video, image, audio, ppt, docx, etc
- login using mobile and otp so need to remember password

# demo

[Youtube Demo Video](https://youtu.be/lG9Qc--hbJA) to view app demo

you can also download apk [click here to download apk](https://github.com/ashutoshbodade/edshare-techathon/blob/main/Ed-Share%20Ver%201.0%20Release.apk)
 
[click here to view app screenshots](https://github.com/ashutoshbodade/edshare-techathon/tree/main/screenshots)

# project setup
you need to clone this project and cofigure firebase for firestore database, storage, authentication using mobile and firebase cloud messanging.
