#include <ESP8266WiFi.h>
#include <WiFiClientSecure.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>
#include <time.h>
#include "secrets.h"
#include <EEPROM.h>
// #include <WebServer.h>
#include <ESP8266WebServer.h>
#include <DoubleResetDetect.h>

#define DRD_TIMEOUT 3.0
#define DRD_ADDRESS 0x54// of the address block// change it if it collides with another usage // address to the block in the RTC user memory
DoubleResetDetect drd(DRD_TIMEOUT, DRD_ADDRESS);

#define Digital_Sensor_pin 4
#define Analog_Sensor_pin 0
#define Driver_ON_pin 14

//steper
#include <AccelStepper.h>
AccelStepper myStepper(FULLSTEP, 13, 5, 12, 16);//15, 14, 13, 16

ESP8266WebServer server(80);

WiFiClientSecure net;
BearSSL::X509List cert(cacert);
BearSSL::X509List client_crt(client_cert);
BearSSL::PrivateKey key(privkey);
PubSubClient client(net);
time_t now;
time_t nowish = 1510592825;

uint addr = 5;
bool set = 0;

int tims = 5;
uint32_t return_message_time = 0;

long pos = 0;

int last_sensor = 1000;

uint32_t timer2 = 0;

uint32_t timer3 = 0;

struct data{//структура даних для збурігання в постійній пам'яті
  bool  val = 0;
  char name[20] = "";
  char pass[20] = "";
  char sub[50] = "";
  int16_t speed_up = 100;
  int16_t speed_down  = 300;
  int32_t home_pos = 0;
  int32_t length = 35000;
  bool sensor_on = 0;
  int16_t sensor = 600;
  int alarm[10][4] = {{0,11,00,0},{1,11,00,0},{2,11,00,0},{3,11,00,0},{4,11,00,0},{5,11,00,0},{6,11,00,0}};
};
data data_p;

void ICACHE_RAM_ATTR Top(){//stop hole
  stop();
  myStepper.setCurrentPosition(0);
  Serial.println("Stop_holl");
}

void setup() {//викликається при запуску
  Serial.begin(115200);
  Serial.println(" ");
  //double boot


  //stepper
  myStepper.setMaxSpeed(300.0);
  myStepper.setAcceleration(50.0);
  // myStepper.setSpeed(200);

  eepom_get();
  myStepper.setCurrentPosition(pos);

  pinMode(2,OUTPUT);
  digitalWrite(2,1);
  pinMode(Driver_ON_pin,OUTPUT);//sleep)driver
  digitalWrite(Driver_ON_pin,LOW);

  pinMode(Digital_Sensor_pin,INPUT);
  attachInterrupt(digitalPinToInterrupt(Digital_Sensor_pin), Top, FALLING);///////////////////////////////////stop hole
  //digitalWrite(2,0);

  if (drd.detect()){
    data_p.val = 0; 
    Serial.println("RESET");
  }

 
  if(data_p.val){// перевіряємо чи пристрій налаштовано
    connectAWS();
    //digitalWrite(2,1);
  }else{
    //digitalWrite(2,0);
    WiFi.softAP("ESP82");
    server.on("/", handleRoot);
    server.on("/get", HTTP_GET, handleGet);
    server.on("/post", HTTP_POST, handlePost, handleUpload);
    server.begin();
  }

  
}

void loop() {
  Serial.println(digitalRead(Digital_Sensor_pin));
  if(data_p.val){//якщо пристрій налаштовано
    if (!client.connected()) {
      connectAWS();        
    }else{
    client.loop();
    //stepper
    if(tims<3){
      if(millis() > return_message_time){
        pub_data();
        return_message_time = millis()+1000; 
        tims++; 
        Serial.print("Resend: ");
        Serial.println(tims);             
      }
    }
    if(millis()>timer3){
      timer3 = millis() + 30000;
      Serial.print("time:"); 
      time_t now;
      struct tm * timeinfo;
      time(&now);
      timeinfo = localtime(&now);  

      Serial.print(" m: "); 
      Serial.print(timeinfo->tm_min);
      Serial.print(" h: "); 
      Serial.print(timeinfo->tm_hour);
      Serial.print(" d: "); 
      Serial.println(timeinfo->tm_wday);  

      for(int i = 0;i < sizeof(data_p.alarm)/16;i++){
        if((timeinfo->tm_min == data_p.alarm[i][2]) && (timeinfo->tm_hour == data_p.alarm[i][1])){
          if(((data_p.alarm[i][0] <  7) && (timeinfo->tm_wday == data_p.alarm[i][0])) || 
              (data_p.alarm[i][0] == 7) && (timeinfo->tm_wday < 5) ||
              (data_p.alarm[i][0] == 8) && (timeinfo->tm_wday > 4) ||
              (data_p.alarm[i][0] == 9)){

            if(!data_p.alarm[i][3]){
              on();
            }else{
              off();
            }
            Serial.print(data_p.alarm[i][2]);
            Serial.print("\t");
            Serial.print(data_p.alarm[i][1]);
            Serial.print("\t");
            Serial.print(data_p.alarm[i][0]);
            Serial.print("\t");
            Serial.print(data_p.alarm[i][3]);
            Serial.println("");
          }
        }
      }
    }
    if(!pos && data_p.sensor_on){//автоматичне закриття
      if(millis()>timer2){
        time_t now;
        struct tm * timeinfo;
        time(&now);
        timeinfo = localtime(&now);
        
        timer2 = millis() + 500;
        if((timeinfo->tm_hour)>16){//включення при збільшені освітлення
          if(analogRead(Analog_Sensor_pin)-50 > last_sensor){
            delay(1000);
            if(analogRead(Analog_Sensor_pin)-200 > last_sensor){
              timer2 = millis() + 21600000;
              on();
              Serial.println("Sensor_light"); 
            }
          }else{
            delay(10);
          }
        }
        if((analogRead(Analog_Sensor_pin)<data_p.sensor)){
            timer2 = millis() + 21600000;
            on();
            Serial.println("Sensor_night"); 
        }
        last_sensor = analogRead(Analog_Sensor_pin);

        Serial.print(" pos: "); 
        Serial.print(pos); 
        Serial.print(" timer2: "); 
        Serial.print(timer2); 
        Serial.print(" millis: "); 
        Serial.print(millis()); 
        Serial.print(" analogRead: "); 
        Serial.print(analogRead(Analog_Sensor_pin)); 
        Serial.print(" sensor: "); 
        Serial.println(data_p.sensor);
      }
    }
    
    
      if(myStepper.distanceToGo()){//якщо морор крутиться
        digitalWrite(Driver_ON_pin,HIGH);
        myStepper.run();
        // digitalRead(5);
      }else{
        digitalWrite(Driver_ON_pin,LOW);
      }
    }
  }else{
    server.handleClient();
  }
}

void messageReceived(char *topic, byte *payload, unsigned int length) {//коли прийшло повідомлення
  //виводимо повідомлення в термінал
  Serial.print("Received [");
  Serial.print(topic);
  Serial.print("]: ");
  for (int i = 0; i < length; i++) {
    Serial.print((char)payload[i]);
  }
    Serial.println("");  

    String int_data = "";
    for (int i = 0; i < length; i++) {
        int_data+=(int)payload[i]-48;
      }
    Serial.print("int_data:");
    Serial.println(int_data);

  //перевіряємо на який топік прийшло повідомлення
  if (strcmp(topic,AWS_IOT_SUBSCRIBE_TOPIC)==0){
    //відкритись/закритись повністю
    if((char)payload[0] == '1'){
      //відкритись
      on();
    }else{
      //закритись
      off();
    }
  }else if (strcmp(topic,AWS_IOT_SUBSCRIBE_TOPIC_ACTIVE)==0) {
    //повертаємо, що пристрій в мережі
    if(tims<3){
      tims=5;
    }else{
      pub_data();
      return_message_time = millis()+1000;
      tims=0;
      Serial.println("Resend: -1");
    }
    Serial.println(AWS_IOT_PUBLISH_TOPIC);
  }else if (strcmp(topic,AWS_IOT_SUBSCRIBE_TOPIC_SPEED)==0) { 
    //Встановлюємо швидкість
    if(set){
      data_p.speed_up = int_data.toInt();
      myStepper.setMaxSpeed(data_p.speed_up);

      Serial.println("speed_up: ");
      Serial.println(data_p.speed_up);
    }else{
      data_p.speed_down = int_data.toInt();
      myStepper.setMaxSpeed(data_p.speed_down);
      Serial.println("speed_down: ");
      Serial.println(data_p.speed_down);      
    }
    
  }else if (strcmp(topic,AWS_IOT_SUBSCRIBE_TOPIC_ACCELERATION)==0) {
    //прискорення
  }else if (strcmp(topic,AWS_IOT_SUBSCRIBE_TOPIC_SET_HOME)==0) {
    //при налаштуванні пристрою
    if((char)payload[0] == '0'){
      //зупинити мотор
      stop();      
    }else if((char)payload[0] == '1'){
      //рухатись верх
      // myStepper.setAcceleration(500);
      myStepper.setAcceleration(200);
      myStepper.setMaxSpeed(data_p.speed_up);
      myStepper.move(-100000);
      set = 1;
      Serial.println("UP");
    }else if((char)payload[0] == '2'){
      //рухатись вниз
      // myStepper.setAcceleration(500);
      myStepper.setAcceleration(300);
      myStepper.setMaxSpeed(data_p.speed_down);
      myStepper.move(100000);
      set = 0;
      Serial.println("DOWN");      
    }else if((char)payload[0] == '3'){
      //Auto Home
      myStepper.setAcceleration(200);
      myStepper.setMaxSpeed(data_p.speed_up);
      myStepper.move(-1000000);
      Serial.println("Auto Home");
    }else if((char)payload[0] == '4'){
      //Save Top
      stop();
      myStepper.setCurrentPosition(0);
    }else if((char)payload[0] == '5'){
      //Save Bottom
      stop();
      data_p.length = myStepper.currentPosition();
      //Save Setings
      EEPROM.put(addr,data_p);
      EEPROM.commit(); 

      Serial.println("Save Setings"); 
    }else if((char)payload[0] == '6'){
      //Auto light
      data_p.sensor_on = (char)payload[2] == '1';
      Serial.println("sensor_on: "); 
      Serial.println(data_p.sensor_on); 
      EEPROM.put(addr,data_p);
      EEPROM.commit(); 
      timer2 = millis() + 1000;
      last_sensor = analogRead(Analog_Sensor_pin);
    }else if((char)payload[0] == '7'){
      
    }else{
      Serial.println("Else");      
    }

    
  }else if (strcmp(topic,AWS_IOT_SUBSCRIBE_TOPIC_POSITION)==0) {
    //переміститись до певного місця
    pos = map(int_data.toInt(),0,10,0,data_p.length);
    writeLongIntoEEPROM(0,pos);
    myStepper.moveTo(pos);
    Serial.print("Move_to: "); 
    Serial.println(pos); 
  }else if (strcmp(topic,AWS_IOT_SUBSCRIBE_TOPIC_SENSOR)==0) {
    //встановсюємо яскравість спрацювання
    data_p.sensor = int_data.toInt();
    EEPROM.put(addr,data_p);
    EEPROM.commit(); 

  }else if (strcmp(topic,AWS_IOT_SUBSCRIBE_TOPIC_ALARM)==0) {
      //отримуємо дані про будільник
      String in = String((char*)payload);

  // Count the number of rows by counting semicolons
    int numRows = 1;
    for (int i = 0; i < in.length(); i++) {
      if (in.charAt(i) == ';') {
        numRows++;
      }
    }
    int out[10][4] = {};

    // Split the input string by semicolons to get individual rows
    String rows[numRows];
    int startIndex = 0;
    int endIndex = 0;
    for (int i = 0; i < numRows; i++) {
      endIndex = in.indexOf(';', startIndex);
      if (endIndex == -1) {
        endIndex = in.length();
      }
      rows[i] = in.substring(startIndex, endIndex);
      startIndex = endIndex + 1;
    }
    for (int i = 0; i < 10; i++) {
    }
    // Split each row by commas and convert to integers
    for (int i = 0; i < numRows; i++) {
      String row = rows[i];
      int commaIndex = 0;
      for (int j = 0; j < 4; j++) {
        int nextCommaIndex = row.indexOf(',', commaIndex);
        if (nextCommaIndex == -1) {
          nextCommaIndex = row.length();
        }
        out[i][j] = row.substring(commaIndex, nextCommaIndex).toInt();
        commaIndex = nextCommaIndex + 1;
      }
    }


    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 4; j++) {
          data_p.alarm[i][j] = out[i][j];
          Serial.print(out[i][j]);
      Serial.print("\t");
      }
      Serial.println();
    } 
    EEPROM.put(addr,data_p);
    EEPROM.commit(); 
  }

}///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



void NTPConnect(void) {//сихронізує час мікроконтролера з реальним часом
  Serial.println("");
  Serial.print("Setting time using SNTP");
  // configTime(TIME_ZONE * 3600, 0 * 3600, "pool.ntp.org", "time.nist.gov");

#define MY_NTP_SERVER "at.pool.ntp.org"           
#define MY_TZ "EET-2EEST,M3.5.0/3,M10.5.0/4"   
configTime(MY_TZ, MY_NTP_SERVER);

  now = time(nullptr);

  while (now < nowish) {
    delay(500);
    Serial.print(".");
    now = time(nullptr);
    //digitalWrite(2,!digitalRead(2));
  }
  Serial.println("done!");
    tm timeinfo;
  gmtime_r(&now, &timeinfo);
  Serial.print("Current time: ");
  Serial.print(asctime(&timeinfo));
}

void connectAWS() {//приєднується до AWS iot
  delay(3000);
  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);

  Serial.println(String("Attempting to connect to SSID: ") + String(WIFI_SSID));

  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(1000);
    //digitalWrite(2,!digitalRead(2));
  }
  

  NTPConnect();

  net.setTrustAnchors(&cert);
  net.setClientRSACert(&client_crt, &key);

  client.setServer(MQTT_HOST, 8883);
  client.setCallback(messageReceived);

  Serial.println("Connecting to AWS IOT");

  while (!client.connect(THINGNAME)) {
    Serial.print(".");
    delay(1000);
    //digitalWrite(2,!digitalRead(2));
  }

  if (!client.connected()) {
    Serial.println("AWS IoT Timeout!");
    return;
  }
  // Subscribe to a topic
  client.subscribe(AWS_IOT_SUBSCRIBE_TOPIC, 1);
  client.subscribe(AWS_IOT_SUBSCRIBE_TOPIC_ACTIVE, 1);
  client.subscribe(AWS_IOT_SUBSCRIBE_TOPIC_SPEED, 1);
  client.subscribe(AWS_IOT_SUBSCRIBE_TOPIC_ACCELERATION, 1);
  client.subscribe(AWS_IOT_SUBSCRIBE_TOPIC_SET_HOME, 1);
  client.subscribe(AWS_IOT_SUBSCRIBE_TOPIC_POSITION, 1);
  client.subscribe(AWS_IOT_SUBSCRIBE_TOPIC_SENSOR, 1);
  client.subscribe(AWS_IOT_SUBSCRIBE_TOPIC_ALARM, 1);
  Serial.println("AWS IoT Connected!");
}
///////////////////////////////////////

void handleRoot(){//надсилаєм на телефон зерез точку доступу тещо ми готові приймати пароль до wifi
  server.send(200, "text/plain", "Ready");
}

void handleGet() {//коли прийшли дані надсилаємо підтвердження
  String data = "";
  if (server.hasArg("data")) {
    data = server.arg("data");
    Serial.println("Data: " + data);
  }
  first_conect(data);
  server.send(200, F("text/plain"), "Sended_____");
}

void handlePost() {
  server.send(200, "text/plain", "Processing Data");
}

void handleUpload() {//друкуємо отримані дані
  HTTPUpload& upload = server.upload();
  if (upload.status == UPLOAD_FILE_START) {
    Serial.println("Receiving data:");
  } else if (upload.status == UPLOAD_FILE_WRITE) {
    Serial.write(upload.buf, upload.currentSize);
  } else if (upload.status == UPLOAD_FILE_END) {
    server.send(200, "text/plain", "Data: ");
  }
}
void first_conect(String data1){//після отримання даних перетворюєм їх і зберігаємо
  int st = 1;
  String name = "";
  String pass = "";
  String sub = "";
  for(int i = 0;i<data1.length();i++){
    if(st == 1){
      if(data1[i] == '/' && data1[i+1] == 'i' && data1[i+2] == '/'){
        st = 2;
        i += 2;
      }else{
        name += data1[i];
      }
    }else if(st == 2){
      if(data1[i] == '/' && data1[i+1] == 's' && data1[i+2] == '/'){
        st = 0;
        i += 2;
      }else{
        pass += data1[i];
      }
    }else{
      sub += data1[i];
    }
  }

  data_p.val = 1;

  char name1[20];
  name.toCharArray(name1, 20);
  char pass1[20];
  pass.toCharArray(pass1, 20);
  char sub1[50];
  sub.toCharArray(sub1, 50);

  Serial.print("sub1: ");
  Serial.println(sub1);

  strncpy(data_p.name, name1,20);
  strncpy(data_p.pass, pass1,20);
  strncpy(data_p.sub, sub1,50);

  Serial.print("data_p.name: ");
  Serial.println(data_p.name);
  Serial.print("data_p.pass: ");
  Serial.println(data_p.pass);
  Serial.print("data_p.sub: ");
  Serial.println(data_p.sub); 

  EEPROM.put(addr,data_p);
  EEPROM.commit(); 

  eepom_get();

  server.stop();
  //digitalWrite(2,1);
} 
void eepom_get(){//отримуємо дані з постійної памяті
  EEPROM.begin(512);

  // pos = (EEPROM.read(0) << 8) + EEPROM.read(1);
  pos = readLongFromEEPROM(0);
  Serial.print("pos: ");
  Serial.println(pos);

  EEPROM.get(addr,data_p);


  for(int  i = 0;i<20;i++){
    WIFI_SSID[i] = data_p.name[i];
  }
  for(int  i = 0;i<20;i++){
    WIFI_PASSWORD[i] = data_p.pass[i];
  }

  for(int  i = 0;i<50;i++){
    THINGNAME[i] = data_p.sub[i];
  }

  char sub[50] = ""; 
  strncpy(sub,data_p.sub,50);
  strcat(sub,"/sub");
  for(int  i = 0;i<50;i++){
    AWS_IOT_SUBSCRIBE_TOPIC[i] = sub[i];
  }
  
  
  char sub1[50] = ""; 
  strncpy(sub1,data_p.sub,50);
  strcat(sub1,"/pub");
  for(int  i = 0;i<50;i++){
    AWS_IOT_PUBLISH_TOPIC[i] = sub1[i];
  }

  char sub2[60] = ""; 
  strncpy(sub2,data_p.sub,60);
  strcat(sub2,"/sub_A");
  for(int  i = 0;i<60;i++){
    AWS_IOT_SUBSCRIBE_TOPIC_ACTIVE[i] = sub2[i];
  }

    char sub3[60] = ""; 
  strncpy(sub3,data_p.sub,60);
  strcat(sub3,"/sub_SPEED");
  for(int  i = 0;i<60;i++){
    AWS_IOT_SUBSCRIBE_TOPIC_SPEED[i] = sub3[i];
  }

  char sub4[60] = ""; 
  strncpy(sub4,data_p.sub,60);
  strcat(sub4,"/sub_ACCEL");
  for(int  i = 0;i<60;i++){
    AWS_IOT_SUBSCRIBE_TOPIC_ACCELERATION[i] = sub4[i];
  }

    char sub5[60] = ""; 
  strncpy(sub5,data_p.sub,60);
  strcat(sub5,"/sub_SET_HOME");
  for(int  i = 0;i<60;i++){
    AWS_IOT_SUBSCRIBE_TOPIC_SET_HOME[i] = sub5[i];
  }
  char sub6[60] = ""; 
  strncpy(sub6,data_p.sub,60);
  strcat(sub6,"/sub_POSITION");
  for(int  i = 0;i<60;i++){
    AWS_IOT_SUBSCRIBE_TOPIC_POSITION[i] = sub6[i];
  }
  char sub7[60] = ""; 
  strncpy(sub7,data_p.sub,60);
  strcat(sub7,"/sub_SENSOR");
  for(int  i = 0;i<60;i++){
    AWS_IOT_SUBSCRIBE_TOPIC_SENSOR[i] = sub7[i];
  }
  char sub8[60] = ""; 
  strncpy(sub8,data_p.sub,60);
  strcat(sub8,"/sub_ALARM");
  for(int  i = 0;i<60;i++){
    AWS_IOT_SUBSCRIBE_TOPIC_ALARM[i] = sub8[i];
  }

  //виводимо дані
  Serial.println("");
  Serial.print("WIFI_SSID: ");
  Serial.println(WIFI_SSID);
  Serial.print("WIFI_PASSWORD: ");
  Serial.println(WIFI_PASSWORD);
  Serial.print("THINGNAME: ");
  Serial.println(THINGNAME);
  Serial.print("SUBSCRIBE_TOPIC: ");
  Serial.println(AWS_IOT_SUBSCRIBE_TOPIC);
  Serial.print("PUBLISH_TOPIC: ");
  Serial.println(AWS_IOT_PUBLISH_TOPIC);
  Serial.print("SUBSCRIBE_ACTIVE: ");
  Serial.println(AWS_IOT_SUBSCRIBE_TOPIC_ACTIVE);
  Serial.print("SUBSCRIBE_SPEED: ");
  Serial.println(AWS_IOT_SUBSCRIBE_TOPIC_SPEED);
  Serial.print("SUBSCRIBE_ACCELERATION: ");
  Serial.println(AWS_IOT_SUBSCRIBE_TOPIC_ACCELERATION);
  Serial.print("SUBSCRIBE_SET_HOME: ");
  Serial.println(AWS_IOT_SUBSCRIBE_TOPIC_SET_HOME);
  Serial.print("SUBSCRIBE_POSITION: ");
  Serial.println(AWS_IOT_SUBSCRIBE_TOPIC_POSITION);
  Serial.print("SUBSCRIBE_TOPIC_SENSOR: ");
  Serial.println(AWS_IOT_SUBSCRIBE_TOPIC_SENSOR);
  Serial.print("SUBSCRIBE_TOPIC_ALARM: ");
  Serial.println(AWS_IOT_SUBSCRIBE_TOPIC_ALARM);

}

void pub_data(){//надсилаємо на телефон дані про пристрій(AWS)
  String st = String(myStepper.currentPosition()) + "/" + String(data_p.speed_up) + "/" + String(data_p.speed_down) + "/" + String(data_p.length) + "/" 
            + String(analogRead(Analog_Sensor_pin)) + "/" + String(data_p.sensor_on) + "/" + String(data_p.sensor); 
  char dat[20];
  st.toCharArray(dat, 50);
  client.publish(AWS_IOT_PUBLISH_TOPIC,dat);

  Serial.println(st);
}

void stop(){
  myStepper.setAcceleration(2000);
  myStepper.stop();
  Serial.println("Stop");   
}

void writeLongIntoEEPROM(int address, long number){//записуємо дані про місце без використання структури
  EEPROM.write(address, (number >> 24) & 0xFF);
  EEPROM.write(address + 1, (number >> 16) & 0xFF);
  EEPROM.write(address + 2, (number >> 8) & 0xFF);
  EEPROM.write(address + 3, number & 0xFF);
  EEPROM.commit(); 
}
long readLongFromEEPROM(int address){
  return ((long)EEPROM.read(address) << 24) +
         ((long)EEPROM.read(address + 1) << 16) +
         ((long)EEPROM.read(address + 2) << 8) +
         (long)EEPROM.read(address + 3);
}

void on(){
  timer2 = millis() + 1800000;
  pos = data_p.length;
  writeLongIntoEEPROM(0,pos);
  //stepper
  myStepper.setAcceleration(300);
  myStepper.setMaxSpeed(data_p.speed_down);
  myStepper.moveTo(data_p.length);
  Serial.print("ON ");
  Serial.println(data_p.length);
}

void off(){
  //steper
  if(myStepper.currentPosition()>500){
    pos = 0;
    writeLongIntoEEPROM(0,pos);
    myStepper.setAcceleration(200);
    myStepper.setMaxSpeed(data_p.speed_up);
    myStepper.moveTo(-data_p.length);
  }else{
    pos = 0;
    writeLongIntoEEPROM(0,pos);
    myStepper.setAcceleration(200);
    myStepper.setMaxSpeed(data_p.speed_up);
    myStepper.moveTo(0);
  }
  Serial.println("OFF");
}
