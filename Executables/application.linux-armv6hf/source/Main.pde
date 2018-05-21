PVector gravity;
Population pop;
ArrayList<Obst> obsts = new ArrayList<Obst>();
int r, g, b;
boolean paused = false;

void setup() {
  size(800, 300);
  frameRate(1000);
  ellipseMode(CENTER);
  gravity = new PVector(0, 0.1);
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

void draw() {
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

void keyPressed(){
  if(keyCode == 32){
    paused = !paused;
  }if(keyCode == 82){
    for(Jumper j : pop.jumpers){
      j.dead = true;
    }
  }
}
