import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Main extends PApplet {

PVector gravity;
Population pop;
ArrayList<Obst> obsts = new ArrayList<Obst>();
int r, g, b;
boolean paused = false;

public void setup() {
  
  frameRate(1000);
  ellipseMode(CENTER);
  gravity = new PVector(0, 0.1f);
  pop = new Population(60, height - 60, width - 60, height - 80, 1000);
  obsts = new ArrayList<Obst>();

  //Up
  obsts.add(new Obst(125, 0, 20, 50));
  obsts.add(new Obst(225, 0, 20, 120));
  obsts.add(new Obst(325, 0, 20, 100));
  obsts.add(new Obst(425, 0, 20, 80));
  obsts.add(new Obst(525, 0, 20, 90));
  obsts.add(new Obst(625, 0, 20, 65));
  //Down
  obsts.add(new Obst(125, height-200, 20, 200));
  obsts.add(new Obst(225, height-100, 20, 100));
  obsts.add(new Obst(325, height-160, 20, 170));
  obsts.add(new Obst(425, height-140, 20, 140));
  obsts.add(new Obst(525, height-80, 20, 80));
  obsts.add(new Obst(625, height-120, 20, 190));
}

public void draw() {
  if (!paused) {
    background(255);

    for (Obst obst : obsts) {
      obst.draw();
    }

    //Ground
    fill(0);
    rect(0, height - 50, width, 50);
    stroke(0);
    fill(255, 23, 212);
    textSize(15);
    textAlign(LEFT, TOP);
    text("Generation: "+pop.generation, 5, 5);
    fill(255, 0, 0);
    ellipse(pop.reachPoint.x, pop.reachPoint.y, 15, 15);

    pop.update();
    pop.draw();

    if (pop.isAllDead()) {
      pop.nextGeneration();
      pop.mutate();
    }
  }
}

public void keyPressed(){
  if(keyCode == 32){
    paused = !paused;
  }if(keyCode == 82){
    for(Jumper j : pop.jumpers){
      j.dead = true;
    }
  }
}
class Brain {
  PVector[] directions;
  int counter;


  Brain(int size) {
    this.directions = new PVector[size];
    this.counter = 0;
    this.setup();
  }

  public void setup() {
    for (int i = 0; i < this.directions.length; i++) {
      this.directions[i] = PVector.fromAngle(random(PI)); 
      this.directions[i].y *= -7;
      this.directions[i].x *= 2;
    }
  }

  public Brain clone() {
    Brain brain = new Brain(this.directions.length);
    for (int i = 0; i < brain.directions.length; i++) {
      brain.directions[i] = this.directions[i].copy();
    }
    return brain;
  }

  public void mutate() {
    float mutateRatio = 0.05f;
    for (int i = 0; i < this.directions.length; i++) {
      if (random(1) < mutateRatio) {
        this.directions[i] = PVector.fromAngle(random(PI)); 
        this.directions[i].y *= -7;
        this.directions[i].x *= 2;
      }
    }
  }
}
class Jumper {  
  PVector velocity;
  PVector location;
  PVector startPoint;
  PVector reachPoint;
  Brain brain;
  boolean landed;
  boolean dead;
  boolean reached;
  int size;

  Jumper(float x, float y,float rx,float ry) {
    this.location = new PVector(x, y);
    this.velocity = new PVector(0, 0);
    this.startPoint = new PVector(x,y);
    this.reachPoint = new PVector(rx,ry);
    this.brain = new Brain(1000);
    this.landed = false;
    this.size = 1;
    this.reached = false;
  }

  public void update() {
    if (!dead) {
      //Gravity
      if (!landed) {
        this.velocity.add(gravity);
      }

      this.location.add(this.velocity);

      //Check if its below the ground
      if (this.location.y + this.size > height - 50) {
        this.landed = true;
        this.location.y = height - 50 - this.size;
        this.velocity.y = 0;
        this.velocity.x = 0;
      }

      for (Obst obst : obsts) {
        if (this.location.x > obst.x && this.location.x < obst.wx && this.location.y > obst.y && this.location.y < obst.hy) {
          this.dead = true;
        }
      }

      if (this.location.x < 0 || this.location.x > width || this.location.y < 0 || this.location.y > height) {
        this.dead = true;
      }
      
      if(this.location.dist(this.reachPoint) < 7.5f){
        this.dead = true;
        this.reached = true;
      }
    }
  }

  public void draw() {
    fill(255);
    stroke(0);
    ellipse(this.location.x, this.location.y, this.size*2, this.size*2);
  }

  public void jump() {
    if (this.brain.directions.length > this.brain.counter && this.landed && !dead) {
      this.velocity.add(this.brain.directions[this.brain.counter]);
      this.brain.counter++;
    }
    if (this.location.y + this.size >= height-50) {
      this.landed = false;
    }
  }
  
  public float calculateFitness(){
    float dist = this.location.dist(this.reachPoint);
    if(this.reached){
      return 10000 + (500 *(1 / (this.brain.counter * this.brain.counter)));
    }else {
      return 1 / (dist * dist);
    }  
  }
  
  public Jumper clone(){
    Jumper jumper = new Jumper(this.startPoint.x,this.startPoint.y,this.reachPoint.x,this.reachPoint.y);
    jumper.brain = this.brain.clone();
    return jumper;
  }
}
class Obst {
  int x, y, w, h, wx, hy;
  Obst(int x, int y, int w, int h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.wx = x + w;
    this.hy = y + h;
  }

  public void draw() {
    fill(150, 250, 100);
    rect(this.x, this.y, this.w, this.h);
  }
}
class Population {
  Jumper[] jumpers;
  PVector startPoint;
  PVector reachPoint;
  float fitnessSum;
  int generation;
  
  Population(int stx, int sty, int rx, int ry, int size) {
    this.jumpers = new Jumper[size];
    this.startPoint = new PVector(stx, sty);
    this.reachPoint = new PVector(rx, ry);
    this.fitnessSum = 0;
    this.generation = 1;
    setup();
  }

  public void setup() {
    for (int i = 0; i < this.jumpers.length; i++) {
      this.jumpers[i] = new Jumper(this.startPoint.x, this.startPoint.y, this.reachPoint.x, this.reachPoint.y);
    }
  }

  public void update() {
    for (int i = 0; i < this.jumpers.length; i++) {
      this.jumpers[i].update();
      this.jumpers[i].jump();
    }
  }

  public void draw() {
    for (int i = 0; i < this.jumpers.length; i++) {
      this.jumpers[i].draw();
    }
  }
  
  public boolean isAllDead(){
    for (int i = 0; i < this.jumpers.length; i++) {
      if(!this.jumpers[i].dead){
        return false;
      }
    }
    return true;
  }
  
  public float calculateFitnessSum(){
    float sum = 0;
    for (int i = 0; i < this.jumpers.length; i++) {
      sum += this.jumpers[i].calculateFitness();
    }
    return sum;
  }
  
  public Jumper getAParent(){
    float random = random(this.calculateFitnessSum());
    float processSum = 0;
    for(int i = 0; i < this.jumpers.length; i++){
      processSum += this.jumpers[i].calculateFitness();
      if(processSum > random){
        return this.jumpers[i].clone();
      }
    }
    return null;
  }
  
  public void mutate(){
    for(int i = 0; i < this.jumpers.length; i++){
      this.jumpers[i].brain.mutate();
    }
  }
  
  public void nextGeneration(){
    Jumper[] newGeneration = new Jumper[this.jumpers.length];
    for(int i = 0; i < newGeneration.length; i++){
      newGeneration[i] = this.getAParent().clone();
  }
    this.jumpers = newGeneration.clone();
    this.generation += 1;
  }
}
  public void settings() {  size(800, 300); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Main" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
