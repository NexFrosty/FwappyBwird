import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.ArrayList;
import java.util.Random;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.Timer;

public class FlappyBird implements ActionListener, MouseListener, KeyListener {

	public static FlappyBird flappyBird;
	private JFrame jframe;
	public Renderer renderer;
	public Bird bird;
	public ArrayList<Rectangle> columns;
	public int ticks, yMotion, score, highScore;
	public boolean gameOver, started;
	public Random rand;

	public final int WIDTH = 700, HEIGHT = 700;
	public boolean easy = false, hard = false, impossible = false;
	public boolean wasColliding;

	public FlappyBird() {
		flappyBird=this;
		jframe = new JFrame();
		Timer timer = new Timer(20, this);

		renderer = new Renderer();
		rand = new Random();

		jframe.setTitle("Flappy Bird");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setSize(WIDTH, HEIGHT);
		jframe.addMouseListener(this); // components being listened to any mouse events
		jframe.addKeyListener(this); // components being listened to any key events
		jframe.setResizable(false);

		bird = new Bird(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
		columns = new ArrayList<Rectangle>();

		addColumn(true); // background
		addColumn(true); // btm layer
		addColumn(true); // 2nd btm layer
		addColumn(true); // bird

		timer.start();
		jframe.add(renderer);
	}

	public void addColumn(boolean start) { // add pillars
		int space = 300;
		int width = 100;
		int height = 50 + rand.nextInt(300);

		if (impossible) {
			if (start) {
				columns.add(new Rectangle(WIDTH + width + columns.size() * 300, HEIGHT -
						height - 200, width, height));
				columns.add(
						new Rectangle(WIDTH + width + (columns.size() - 1) * 300, 0, width, HEIGHT -
								height - space));
			} else {
				columns.add(
						new Rectangle(columns.get(columns.size() - 1).x + 600, HEIGHT - height - 200,
								width, height));
				columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, width, HEIGHT
						- height - space));
			}
		}
		if (start) {
			columns.add(new Rectangle(WIDTH + width + columns.size() * 300, HEIGHT - height - 120, width, height));
			columns.add(new Rectangle(WIDTH + width + (columns.size() - 1) * 300, 0, width, HEIGHT - height - space));

		} else {
			columns.add(new Rectangle(columns.get(columns.size() - 1).x + 600, HEIGHT - height - 120, width, height));
			columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, width, HEIGHT - height - space));

		}
	}

	public void paintColumn(Graphics g, Rectangle column) { // pillars
		g.setColor(Color.green.darker());
		g.fillRect(column.x, column.y, column.width, column.height);
		if (score > 3 || hard) {
			g.setColor(Color.red.darker());
			g.fillRect(column.x, column.y, column.width, column.height);
		}
	}

	public void jump() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		if (gameOver) {
			bird = new Bird(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
			columns.clear();
			yMotion = 0;
			score = 0;

			addColumn(true);
			addColumn(true);
			addColumn(true);
			addColumn(true);

			gameOver = false;
		}

		if (!started) {
			started = true;
		} else if (!gameOver) {
			if (yMotion > 0) {
				yMotion = 0;
			}

			yMotion -= 10;
		}
		jumpSound();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		Rectangle rectBird = this.bird.getBounds();
		int speed = 10;

		ticks++;

		if (started) {
			for (int i = 0; i < columns.size(); i++) {
				Rectangle column = columns.get(i);

				column.x -= speed;
				if (score > 3 || hard) {
					speed = 15;
				}
			}

			if (ticks % 2 == 0 && yMotion < 15) {
				yMotion += 2;
			}

			for (int i = 0; i < columns.size(); i++) {
				Rectangle column = columns.get(i);

				if (column.x + column.width < 0) {
					columns.remove(column);

					if (column.y == 0) {
						addColumn(false);
					}
				}
			}

			bird.y += yMotion;
			boolean isColliding = false;
            for (Rectangle column : columns) {
                if (column.y == 0 && bird.x + bird.width / 2 > column.x + column.width / 2 - 10
                        && bird.x + bird.width / 2 < column.x + column.width / 2 + 10) {
                    score++;
                    try {
                        pointsSound();
                    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
                        e1.printStackTrace();
                    }
                }
                
                isColliding |= column.intersects(rectBird);
                if (column.intersects(rectBird)) {
                    if (bird.x <= column.x) {
                        bird.x = column.x - bird.width;
                    } else {
                        if (column.y != 0) {
                            bird.y = column.y - bird.height;
                        } else if (bird.y < column.height) {
                            bird.y = column.height;
                        }
                    }
                }
            }
            if(isColliding && !this.wasColliding) {
                gameOver = true;
                try {
                    deathSound();
                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
                    e1.printStackTrace();
                }
            }
            this.wasColliding = isColliding;			
            if (bird.y > HEIGHT - 120 || bird.y < 0) {
				gameOver = true;
			}
			if (bird.y + yMotion >= HEIGHT - 120) {
				bird.y = HEIGHT - 120 - bird.height;
				gameOver = true;
			}

			if (impossible) {
				if (bird.y > HEIGHT - 200 || bird.y < 0) {
					gameOver = true;
				}
				if (bird.y + yMotion >= HEIGHT - 200) {
					bird.y = HEIGHT - 200 - bird.height;
					gameOver = true;
				}
			}
		}
		if (score > highScore) {
			highScore = score;
		}
		renderer.repaint();
	}

	private void pointsSound() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		URL sound=getClass().getResource("points.wav");
		AudioInputStream ais = AudioSystem.getAudioInputStream(sound);
		Clip clip = AudioSystem.getClip();
		clip.open(ais);
		clip.setFramePosition(0);
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(-30.0f);
		clip.start();
	}

	private void jumpSound() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		URL sound=getClass().getResource("jump.wav");
		AudioInputStream ais = AudioSystem.getAudioInputStream(sound);
		Clip clip = AudioSystem.getClip();
		clip.open(ais);
		clip.setFramePosition(0);
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(-30.0f);
		clip.start();
	}

	private void deathSound() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		URL sound=getClass().getResource("dead.wav");
		AudioInputStream ais = AudioSystem.getAudioInputStream(sound);
		Clip clip = AudioSystem.getClip();
		clip.open(ais);
		clip.setFramePosition(0);
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(-30.0f);
		clip.start();
	}
	
	public void repaint(Graphics g) {

		g.setColor(Color.cyan); // column3 - 2nd btm layer
		g.fillRect(0, 0, WIDTH, HEIGHT);

		g.setColor(Color.orange); // column2 - btm layer
		if (score > 3 || hard) {
			g.setColor(Color.pink); // column3 - 2nd btm layer
		}
		g.fillRect(0, HEIGHT - 120, WIDTH, 120);
		if (impossible) {
			g.fillRect(0, HEIGHT - 200, WIDTH, 170);
		}

		g.setColor(Color.green); // column3 - 2nd btm layer
		if (score > 3 || hard) {
			g.setColor(Color.magenta); // column3 - 2nd btm layer
		}
		g.fillRect(0, HEIGHT - 120, WIDTH, 20);
		if (impossible) {
			g.fillRect(0, HEIGHT - 200, WIDTH, 20);
		}

		this.bird.draw(g, score, hard); // column4 - bird

		for (Rectangle column : columns) {
			paintColumn(g, column);
		}

		g.setColor(Color.white);
		g.setFont(new Font("Arial", 1, 80));

		if (!started) {
			g.drawString("Click to start!", 90, HEIGHT / 2 - 50);
			g.setColor(Color.red);
			g.setFont(new Font("Arial", 1, 20));
			g.drawString("Press 0 to Reset", 30, 640);
			g.drawString("Press 1 for Hard", 240, 640);
			g.drawString("Press 2 for Impossible", 440, 640);
		}

		// int counter = 0;
		if (gameOver) {
			g.drawString("Game Over!", 110, HEIGHT / 2 - 50);
			// while (counter < 1) {
			// deathSound();
			// counter++;
			// }
		}

		if (!gameOver && started) {
			g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
		}
		g.setColor(Color.black);
		g.setFont(new Font("Arial", 1, 30));
		g.drawString("High Score: " + String.valueOf(highScore), 450, 50);
	}

	public static void main(String[] args) {
		flappyBird = new FlappyBird();
		flappyBird.jframe.setVisible(true);
		JFrame j = new JFrame();
		JButton b = new JButton();
		Message message = new Message(j);
		b.setText("<html>Welcome to our Flappy Bird game<br/><br/><center>Click here for Credits</html>");
		j.setSize(300, 150);
		j.add(b);
		j.setVisible(true);
		b.addActionListener(message);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		try {
			jump();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			try {
				jump();
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
				e1.printStackTrace();
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_1) {
			hard = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_2) {
			impossible = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_0) {
			main(null);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

}
