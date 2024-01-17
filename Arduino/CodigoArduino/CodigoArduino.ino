#include <AccelStepper.h>
#include <MultiStepper.h>


const byte enablePin = 8;

//All measurments are done in mm
//Motors and mechanisms specifications
const int stepsPerRevolution = 200;
const int beltPitch = 2;
const int pulleyTooth = 20;
const int rodLengthPerRevolution = 8;
const int YRodLenght = 100;

//Shelf Specifications
const int shelfHeight = 175;
const int shelfLength = 650;
const int shelfWidth = 100;
const int firstShelfHeight = 0;

//Column and Row start at 0
const int MaxColumn = 3;
const int MaxRow = 3;

const int endstopX = 11;  // x era 9
const int endstopY = 9;   // y era 10
const int endstopZ = 10;  // z era 11

const int byteOffset = 48;

AccelStepper stepperX(1, 4, 7);  // x era 2,5
AccelStepper stepperY(1, 2, 5);  // y era 3,6
AccelStepper stepperZ(1, 3, 6);  // z era 4,7

int lengthToStepsBelt(int length) {
  int stepsPermm = stepsPerRevolution / (beltPitch * pulleyTooth);
  return (length * stepsPermm);
}

int lengthToStepsRod(int length) {
  int stepsPermm = stepsPerRevolution / rodLengthPerRevolution;
  return length * stepsPermm;
}

void initializeStepper(AccelStepper* stepper, int endstop, int speed, int offset) {
  digitalWrite(enablePin, LOW);
  stepper->setMaxSpeed(speed);
  stepper->setAcceleration(speed * 100);
  stepper->move(-1000000);
  int count = 0;
  
  while (count < 5) {
    if(digitalRead(endstop)==HIGH){
      count = 0;
      stepper->run();
    } else
      count ++;
  }

  stepper->stop();
  stepper->move(offset);
  stepper->runToPosition();
  stepper->setCurrentPosition(0);
  digitalWrite(enablePin, HIGH);
}

void moveStepperToPosition(AccelStepper* stepper, int position) {
  digitalWrite(enablePin, LOW);
  stepper->moveTo(position);
  stepper->runToPosition();
  digitalWrite(enablePin, HIGH);
}

void moveStepper(AccelStepper* stepper, int distance) {
  digitalWrite(enablePin, LOW);
  stepper->move(distance);
  stepper->runToPosition();
  digitalWrite(enablePin, HIGH);
}

void moveCurrentTrayToPosition(int row, int column) {
  moveStepperToPosition(&stepperY, lengthToStepsRod(YRodLenght));
  moveStepper(&stepperZ, 2*stepsPerRevolution);
  moveStepperToPosition(&stepperY, 0);
  moveStepperToPosition(&stepperX, lengthToStepsBelt((column * shelfLength) / MaxColumn));
  moveStepperToPosition(&stepperZ, lengthToStepsRod(firstShelfHeight + row * shelfHeight + 2*stepsPerRevolution));
  moveStepperToPosition(&stepperY, lengthToStepsRod(YRodLenght));
  moveStepper(&stepperZ, -2*stepsPerRevolution);
  moveStepperToPosition(&stepperY, 0);
}

void goToCell(int row, int column) {
  moveStepperToPosition(&stepperX, lengthToStepsBelt((column * shelfLength) / MaxColumn));
  moveStepperToPosition(&stepperZ, lengthToStepsRod(firstShelfHeight + row * shelfHeight));
}

void setup() {
  Serial.begin(9600);
  Serial.setTimeout(50);
  pinMode(enablePin, OUTPUT);
  digitalWrite(enablePin, HIGH);

  pinMode(endstopX, INPUT_PULLUP);
  pinMode(endstopY, INPUT_PULLUP);
  pinMode(endstopZ, INPUT_PULLUP);

  initializeStepper(&stepperY, endstopY, 800, 75);
  stepperY.setMaxSpeed(1250);
  stepperY.setAcceleration(25000);

  initializeStepper(&stepperX, endstopX, 400, 50);
  stepperX.setMaxSpeed(1000);
  stepperX.setAcceleration(20000);

  initializeStepper(&stepperZ, endstopZ, 800, 200);
  stepperZ.setMaxSpeed(1250);
  stepperZ.setAcceleration(25000);
}

bool checkMessage(int command,int row,int column,int check){
  return  command+row+column==check &&
          (command==1 || command==2) &&
          (row>=0 && row<=MaxRow) &&
          (column>=0 && column<=MaxColumn);
}

void loop() {
  delay(250);
  int command = -1;
  int row = -1;
  int column = -1;
  int check = -1;

  command = Serial.read();
  row = Serial.read();
  column = Serial.read();
  check = Serial.read();

  //1 ACK CONFIRMACION


  if (checkMessage(command,row,column,check)){

    Serial.print(1);

    if (command == 1) 
      goToCell(row, column);
    else if (command == 2) 
      moveCurrentTrayToPosition(row, column);
    
    check = -1;

    while(check!=10) { //10 == AC
      check = Serial.read();

      if(check<10)
        Serial.print(1);
      else
        Serial.print(2);
    }

    check = -1;

    while (check != 11){ //9 == ACK Final
      check = Serial.read();
      if(check == 10)
        Serial.print(2);
      else
        Serial.print(3);
    }

  }
}
