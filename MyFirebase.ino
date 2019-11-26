//
// Copyright 2015 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

// FirebaseDemo_ESP8266 is a sample that demo the different functions
// of the FirebaseArduino API.

#include <FirebaseArduino.h>
#include <ESP8266WiFi.h>
#include <WiFiClientSecure.h>


// Set these to run example.
#define FIREBASE_HOST "yourfirebase.firebaseio.com" 
#define FIREBASE_AUTH "feKveojNCxxxxxxxxKUZtSOkHJZ61O3r3"
#define WIFI_SSID "your_ssid"
#define WIFI_PASSWORD "your_password"

String serve = "<server_key_here>"; //new changes
String reg = "<device_token_here>"; //new changes

//Global declaration and initialization of variables
const int LED1 = 4;     //D2 GPIO4 
const int LED2 = 14;    //D5 GPIO14
const int LED3 = 15;    //D8 GPIO15
const int button = 16;  //D0 GPIO16
int temp = 0;           //initialize temporary variable


void setup() {
  Serial.begin(115200); // Communication at 115200 Bd with Serial Monitor
  
//initialize digital pin as an input/output
  pinMode(LED1,OUTPUT);
  pinMode(LED2,OUTPUT);
  pinMode(LED3,OUTPUT);
  pinMode(button,INPUT);
  
  // connect to wifi.
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("connecting");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.print("connected: ");
  Serial.println(WiFi.localIP());
  
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  delay(1000);

}

void sendDataToFirebase() {  //new changes

  WiFiClientSecure client;
  String data = "{";
  data = data + "\"to\": \"/topics/SampleTopic" "\",";
  data = data + "\"notification\": {";
  data = data + "\"body\": \"Some pressed your doorbell.\",";
  data = data + "\"title\" : \"Info\" ";
  data = data + "} }";
  Serial.println("Send data...");
  if (client.connect("fcm.googleapis.com", 443)) {
    Serial.println("Connected to the server..");
    client.println("POST /fcm/send HTTP/1.1");
    client.println("Authorization: key=" + serve + "");
    client.println("Content-Type: application/json");
    client.println("Host: fcm.googleapis.com");
    client.print("Content-Length: ");
    client.println(data.length());
    client.print("\n");
    client.print(data);
  }
  Serial.println("Data sent...Reading response..");
  while (client.available()) {
    char c = client.read();
    Serial.print(c);
  }
  Serial.println("Finished!");
  client.flush();
  client.stop();
}

void loop() {
  
  // get value from firebase
  String led1_status=(Firebase.getString("/LED1/status"));
  //Serial.println(led1_status);
  String led2_status=(Firebase.getString("/LED2/status"));
  //Serial.println(led2_status);
  String led3_status=(Firebase.getString("/LED3/status"));
  //Serial.println(led3_status);

 
  if(led1_status=="ON"){
    digitalWrite(LED1,HIGH);
  }else{
    digitalWrite(LED1,LOW);
  }
  if(led2_status=="ON"){
    digitalWrite(LED2,HIGH);
  }else{
    digitalWrite(LED2,LOW);
  }
  if(led3_status=="ON"){
    digitalWrite(LED3,HIGH);
  }else{
    digitalWrite(LED3,LOW);
  }
  
  temp = digitalRead(button);
    if(temp==HIGH){
        // set value of PushButton to HIGH
        Firebase.setString("/PushButton/status", "HIGH");
        Serial.println("HIGH");
       
        //send notification
        sendDataToFirebase();
        
        // handle error
        if (Firebase.failed()) {
            Serial.print("setting /BUTTON/status failed:");
            Serial.println(Firebase.error());  
            return;
        }
      }
    else{
      // set value of PushButton to LOW
        Firebase.setString("/PushButton/status", "LOW");
        Serial.println("LOW");
        // handle error
        if (Firebase.failed()) {
            Serial.print("setting /BUTTON/status failed:");
            Serial.println(Firebase.error());  
            return;
        }
      }
}
