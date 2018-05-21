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

  void draw() {
    fill(150, 250, 100);
    rect(this.x, this.y, this.w, this.h);
  }
}
