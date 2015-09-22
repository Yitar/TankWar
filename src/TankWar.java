import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class TankWar {

	public static void main(String[] args) {
		new TankClient().launchFrame();

	}

}

class TankClient extends Frame {
	public static final int GAME_WIDTH = 800;
	public static final int GAME_HEIGHT = 600;

	Wall w1 = new Wall(100, 200, 20, 150, this);
	Wall w2 = new Wall(300, 100, 300, 20, this);
	Blood b =new Blood();
	Tank myTank = new Tank(750, 550, true, Tank.Direction.STOP, this);

	List<Tank> enemyTanks = new ArrayList<Tank>();
	List<Explode> explodes = new ArrayList<Explode>();
	List<Missile> missiles = new ArrayList<Missile>();

	public void launchFrame() {

		for (int i = 0; i < 10; i++) {
			enemyTanks.add(new Tank(50 + 40 * (i + 1), 50, false, Tank.Direction.D, this));
		}

		this.setTitle("TankWar");
		this.setBounds(500, 300, GAME_WIDTH, GAME_HEIGHT);
		this.setBackground(Color.green);
		this.addKeyListener(new KeyMonitor());
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				System.exit(0);
			}

		});
		this.setResizable(false);
		this.setVisible(true);
		new Thread(new PaintThread()).start();

	}

	public void paint(Graphics g) {
		g.drawString("missles count: " + missiles.size(), 680, 50);
		g.drawString("explodes count: " + explodes.size(), 680, 70);
		g.drawString("enemyTanks count: " + enemyTanks.size(), 660, 90);
		g.drawString("Tanks	life : " +myTank.getLife(), 680, 110);
		w1.draw(g);
		w2.draw(g);
		b.draw(g);
		for (int i = 0; i < missiles.size(); i++) {
			Missile m = missiles.get(i);
			m.draw(g);
			m.hitWall(w1);
			m.hitWall(w2);
			m.hitTank(myTank);
			m.hitTanks(enemyTanks);
		}

		for (int i = 0; i < explodes.size(); i++) {
			Explode e = explodes.get(i);
			e.draw(g);
		}

		for (int i = 0; i < enemyTanks.size(); i++) {
			Tank t = enemyTanks.get(i);
			t.collidesWithWall(w1);
			t.collidesWithWall(w2);
			t.draw(g);
			t.colliedesWithTanks(enemyTanks);
		}

		myTank.draw(g);
		myTank.eat(b);
		
		if(enemyTanks.size()<=0){
			for (int i = 0; i < 5; i++) {
				enemyTanks.add(new Tank(50 + 40 * (i + 1), 50, false, Tank.Direction.D, this));
			}
		}
	}

	class PaintThread implements Runnable {
		public void run() {
			while (true) {
				repaint();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
	}

	private class KeyMonitor extends KeyAdapter {

		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);
		}

		public void keyPressed(KeyEvent e) {
			myTank.KeyPressed(e);
		}
	}

}
