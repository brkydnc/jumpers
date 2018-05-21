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

  void update() {
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
      
      if(this.location.dist(this.reachPoint) < 7.5){
        this.dead = true;
        this.reached = true;
      }
    }
  }

  void draw() {
    fill(255);
    stroke(0);
    ellipse(this.location.x, this.location.y, this.size*2, this.size*2);
  }

  void jump() {
    if (this.brain.directions.length > this.brain.counter && this.landed && !dead) {
      this.velocity.add(this.brain.directions[this.brain.counter]);
      this.brain.counter++;
    }
    if (this.location.y + this.size >= height-50) {
      this.landed = false;
    }
  }
  
  float calculateFitness(){
    float dist = this.location.dist(this.reachPoint);
    if(this.reached){
      return 10000 + (500 *(1 / (this.brain.counter * this.brain.counter)));
    }else {
      return 1 / (dist * dist);
    }  
  }
  
  Jumper clone(){
    Jumper jumper = new Jumper(this.startPoint.x,this.startPoint.y,this.reachPoint.x,this.reachPoint.y);
    jumper.brain = this.brain.clone();
    return jumper;
  }
}
