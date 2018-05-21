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

  void setup() {
    for (int i = 0; i < this.jumpers.length; i++) {
      this.jumpers[i] = new Jumper(this.startPoint.x, this.startPoint.y, this.reachPoint.x, this.reachPoint.y);
    }
  }

  void update() {
    for (int i = 0; i < this.jumpers.length; i++) {
      this.jumpers[i].update();
      this.jumpers[i].jump();
    }
  }

  void draw() {
    for (int i = 0; i < this.jumpers.length; i++) {
      this.jumpers[i].draw();
    }
  }
  
  boolean isAllDead(){
    for (int i = 0; i < this.jumpers.length; i++) {
      if(!this.jumpers[i].dead){
        return false;
      }
    }
    return true;
  }
  
  float calculateFitnessSum(){
    float sum = 0;
    for (int i = 0; i < this.jumpers.length; i++) {
      sum += this.jumpers[i].calculateFitness();
    }
    return sum;
  }
  
  Jumper getAParent(){
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
  
  void mutate(){
    for(int i = 0; i < this.jumpers.length; i++){
      this.jumpers[i].brain.mutate();
    }
  }
  
  void nextGeneration(){
    Jumper[] newGeneration = new Jumper[this.jumpers.length];
    for(int i = 0; i < newGeneration.length; i++){
      newGeneration[i] = this.getAParent().clone();
  }
    this.jumpers = newGeneration.clone();
    this.generation += 1;
  }
}
