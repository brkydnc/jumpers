class Brain {
  PVector[] directions;
  int counter;


  Brain(int size) {
    this.directions = new PVector[size];
    this.counter = 0;
    this.setup();
  }

  void setup() {
    for (int i = 0; i < this.directions.length; i++) {
      this.directions[i] = PVector.fromAngle(random(PI)); 
      this.directions[i].y *= -7;
      this.directions[i].x *= 2;
    }
  }

  Brain clone() {
    Brain brain = new Brain(this.directions.length);
    for (int i = 0; i < brain.directions.length; i++) {
      brain.directions[i] = this.directions[i].copy();
    }
    return brain;
  }

  void mutate() {
    float mutateRatio = 0.05;
    for (int i = 0; i < this.directions.length; i++) {
      if (random(1) < mutateRatio) {
        this.directions[i] = PVector.fromAngle(random(PI)); 
        this.directions[i].y *= -7;
        this.directions[i].x *= 2;
      }
    }
  }
}
