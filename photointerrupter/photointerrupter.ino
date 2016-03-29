#include <SoftwareSerial.h>
#define sensorPin A0
#define pinLed     13
SoftwareSerial BT(10,9);
  String val;
  char a[5];
void setup() {

  Serial.begin( 9600 );
  BT.begin(9600);
  //BT.print("BT is ready!!");
  pinMode( sensorPin,INPUT );
  pinMode( pinLed,OUTPUT );

  digitalWrite( sensorPin,LOW );
  //analogWrite(sensorPin,LOW);
  digitalWrite( pinLed,LOW );

  Serial.println( "Fine Setup" );  
}

void loop() {
  //int sensorVal = analogRead( sensorPin );
  int sensorVal = digitalRead(sensorPin);
  //digitalWrite( pinLed,LOW );

  Serial.print( "Sensor value: " );
  Serial.println( sensorVal );
  
  if(sensorVal==0){
    BT.println("0");
    do{
      delay(10000);
      sensorVal = digitalRead(sensorPin);  
    }
    while(sensorVal==0);
  }
  
/*
  if ( sensorVal < 900 ) {
    //digitalWrite( pinLed,HIGH );
    //delay(30);
    
  }
  else{
    digitalWrite(pinLed,LOW);
    delay( 600 );
  }
  
  val = String(sensorVal) + "\n";
  val.toCharArray(a,5);
  BT.print(a);*/
}

