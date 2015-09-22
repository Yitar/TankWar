import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;

public class Tank {
	public static final int XSPEED = 5;
	public static final int YSPEED = 5;
	public static final int WIDTH = 30;
	public static final int HEIGHT = 30;

	private boolean good = true;
	private boolean Live = true;
	private BloodBar bb= new BloodBar();
	
	private int life = 100;

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	private int oldx, oldy;

	private static Random r = new Random();

	private int step = r.nextInt(12) + 3;

	public void setLive(boolean live) {
		Live = live;
	}

	public boolean isLive() {
		return Live;
	}

	private int x, y;
	TankClient tc;

	private boolean bL = false, bU = false, bR = false, bD = false;

	enum Direction {
		L, LU, U, RU, R, RD, D, LD, STOP
	};

	private Direction dir = Direction.STOP;
	private Direction ptDir = Direction.U;

	public Tank(int x, int y, boolean good) {
		this.x = x;
		this.y = y;
		this.oldx = x;
		this.oldy = y;
		this.good = good;
	}

	public Tank(int x, int y, boolean good, Direction dir, TankClient tc) {
		this(x, y, good);
		this.dir = dir;
		this.tc = tc;
	}

	public void draw(Graphics g) {
		if (!Live) {
			if (!good) {
				tc.enemyTanks.remove(this);
			}
			return;
		}

		Color c = g.getColor();
		if (good)
			g.setColor(Color.gray);
		else
			g.setColor(Color.red);
		g.fillOval(x, y, WIDTH, HEIGHT);
		g.setColor(c);
		if (good)
		bb.draw(g);

		switch (ptDir) {
		case L:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x, y + Tank.HEIGHT / 2);
			break;
		case LU:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x, y);
			break;
		case U:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x + Tank.WIDTH / 2, y);
			break;
		case RU:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x + Tank.WIDTH, y);
			break;
		case R:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x + Tank.WIDTH, y + Tank.HEIGHT / 2);
			break;
		case RD:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x + Tank.WIDTH, y + Tank.HEIGHT);
			break;
		case D:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x + Tank.WIDTH / 2, y + Tank.HEIGHT);
			break;
		case LD:
			g.drawLine(x + Tank.WIDTH / 2, y + Tank.HEIGHT / 2, x, y + Tank.HEIGHT);
			break;
		}

		move();
	}

	void move() {
		this.oldx = x;
		this.oldy = y;
		switch (dir) {
		case L:
			x -= XSPEED;
			break;
		case LU:
			x -= XSPEED;
			y -= YSPEED;
			break;
		case U:
			y -= YSPEED;
			break;
		case RU:
			x += XSPEED;
			y -= YSPEED;
			break;
		case R:
			x += XSPEED;
			break;
		case RD:
			x += XSPEED;
			y += YSPEED;
			break;
		case D:
			y += YSPEED;
			break;
		case LD:
			x -= XSPEED;
			y += YSPEED;
			break;
		case STOP:
			break;
		}

		if (this.dir != Direction.STOP) {
			this.ptDir = this.dir;
		}

		if (x < 0)
			x = 0;
		if (y < 30)
			y = 30;
		if (x + Tank.WIDTH > TankClient.GAME_WIDTH)
			x = TankClient.GAME_WIDTH - Tank.WIDTH;
		if (y + Tank.HEIGHT > TankClient.GAME_HEIGHT)
			y = TankClient.GAME_HEIGHT - Tank.HEIGHT;
		if (!good) {
			Direction[] dirs = Direction.values();
			if (step == 0) {
				int rn = r.nextInt(dirs.length);
				dir = dirs[rn];
				step = r.nextInt(12) + 3;
			}
			step--;
			if (r.nextInt(40) > 35)
				this.fire();
		}
	}

	void locateDirection() {
		if (bL && !bU && !bD && !bR)
			dir = Direction.L;
		else if (!bL && bU && !bD && !bR)
			dir = Direction.U;
		else if (!bL && !bU && bD && !bR)
			dir = Direction.D;
		else if (!bL && !bU && !bD && bR)
			dir = Direction.R;
		else if (bL && bU && !bD && !bR)
			dir = Direction.LU;
		else if (!bL && bU && !bD && bR)
			dir = Direction.RU;
		else if (bL && !bU && bD && !bR)
			dir = Direction.LD;
		else if (!bL && !bU && bD && bR)
			dir = Direction.RD;
		else if (!bL && !bU && !bD && !bR)
			dir = Direction.STOP;
	}

	public void KeyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_F2:
			if(!this.Live) {
				this.Live = true;
				this.life = 100;
			}
			break;
		case KeyEvent.VK_UP:
			bU = true;
			break;
		case KeyEvent.VK_DOWN:
			bD = true;
			break;
		case KeyEvent.VK_LEFT:
			bL = true;
			break;
		case KeyEvent.VK_RIGHT:
			bR = true;
			break;
		}
		locateDirection();

	}

	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		switch (key) {
		case KeyEvent.VK_A:
			superFire();
		case KeyEvent.VK_CONTROL:
			fire();
			break;
		case KeyEvent.VK_UP:
			bU = false;
			break;
		case KeyEvent.VK_DOWN:
			bD = false;
			break;
		case KeyEvent.VK_LEFT:
			bL = false;
			break;
		case KeyEvent.VK_RIGHT:
			bR = false;
			break;
		}
		locateDirection();
	}


	public boolean isGood() {
		return good;
	}

	public Rectangle getRect() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}

	public boolean collidesWithWall(Wall w) {
		if (this.Live && this.getRect().intersects(w.getRect())) {
			this.stay();
			return true;
		}
		return false;
	}
	
	public boolean colliedesWithTanks(List<Tank> enemyTanks) {
		for(int i = 0; i < enemyTanks.size(); i++) {
			Tank t = enemyTanks.get(i);
			if(this != t){
				if (this.Live && t.isLive() && this.getRect().intersects(t.getRect())) {
					this.stay();
					t.stay();
					return true;
				}
			}
		}
		return false;
	}

	
	private void stay() {
		x = oldx;
		y = oldy;
	}
	
	public Missile fire() {
		if (!Live)
			return null;
		Missile m = new Missile(x + Tank.WIDTH / 2 - Missile.WIDTH / 2, y + Tank.HEIGHT / 2 - Missile.HEIGHT / 2, good,
				ptDir, this.tc);
		tc.missiles.add(m);
		return m;
	}
	
	public Missile fire(Direction dir) {
		int x = this.x + Tank.WIDTH / 2 - Missile.WIDTH / 2;
		int y = this.y + Tank.HEIGHT / 2 - Missile.HEIGHT / 2;
		if (!Live)
			return null;
		Missile m = new Missile(x, y, good,
				dir, this.tc);
		tc.missiles.add(m);
		return m;
	}

	public void superFire() {
		Direction[] dirs = Direction.values();
		for(int i = 0; i < 8; i++){
			fire(dirs[i]);
		}
	}
	
	private class BloodBar {
		public void draw(Graphics g) {
			Color c = g.getColor();
			g.drawRect(x, y-10, WIDTH, 10);
			int w = WIDTH*life/100;
			g.setColor(Color.red);
			g.fillRect(x, y-10, w, 10);
			g.setColor(c);
		} 
	}
	
	public boolean eat(Blood b) {
		if(this.Live && b.isLive() && this.getRect().intersects(b.getRect())) {
			this.life = 100;
			b.setLive(false);
			return true;
		}
		return false;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
