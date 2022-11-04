package practice;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;


class Game extends Frame implements MouseListener{
	
	private static final long serialVersionUID = 1L;
	
	public JFrame jFrame = new JFrame("MineSweeper");
	private static int mapWidth = 9, mapHeight = 9, bombNum = 10;
	private static int frameWidth = 400, frameHeight = 400;
	private int flaged;
	private boolean gameRunning;
	private boolean gameOver;
	private boolean[][] isBomb;
	private boolean[][] isTurned;
	private boolean[][] isFlag;
	private int[][] aroundBomb;
	private int[][] direct = {{0, 1}, {1, 0}, {-1, 0}, {0, -1}, {1, -1}, {-1, 1}, {1, 1}, {-1, -1}};
	private JButton[][] buttons;
	private JPanel centerButtonPanel;
	private JLabel gameMessage;
		
	public Game() {
		jFrame.setVisible(true);
		jFrame.setSize(frameWidth, frameHeight);
		jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);;
		jFrame.setLayout(new BorderLayout());
		gameMessage = new JLabel("Game Continuing.");
		jFrame.add(gameMessage, BorderLayout.NORTH);
		
		centerButtonPanel = new JPanel();
		centerButtonPanel.setLayout(new GridLayout(mapWidth, mapHeight));
		buttons = new JButton[mapWidth][mapHeight];
		
		for(int i=0; i<mapWidth; i++) {
			for(int j=0; j<mapHeight; j++) {
				buttons[i][j] = new JButton();
				buttons[i][j].addMouseListener(this);
				buttons[i][j].setBackground(Color.WHITE);
				buttons[i][j].setActionCommand(i + " " + j);
				centerButtonPanel.add(buttons[i][j]);
			}
		}
		jFrame.add(centerButtonPanel, BorderLayout.CENTER);
		
		JButton rebtn = new JButton("Restart");
		rebtn.setActionCommand("restart");
		rebtn.addMouseListener(this);
		jFrame.add(rebtn, BorderLayout.SOUTH);
		
		gameRunning = false;
	}
	
	private void resetGame(int tx, int ty) {
		flaged = 0;
		gameOver = false;
		aroundBomb = new int[mapWidth][mapHeight];
		isBomb = new boolean[mapWidth][mapHeight];
		isTurned = new boolean[mapWidth][mapHeight];
		isFlag = new boolean[mapWidth][mapHeight];
		
		int bombCount = 0;
		while(bombCount < bombNum) {
			int x = (int)(Math.random()*mapWidth);
			int y = (int)(Math.random()*mapHeight);
			if(!isBomb[x][y]) {
				bombCount++;
				isBomb[x][y] = true;
				for(int i=0; i<8; i++) {
					int fx = x + direct[i][0];
					int fy = y + direct[i][1];
					if(inRange(fx, fy)) {
						aroundBomb[fx][fy]++;
					}
				}
			}
		}
		System.out.println("Map Created.");
		printMap();
	}
	
	private void printMap() {
		System.out.println("Bombs");
		for(int i=0; i<mapWidth; i++) {
			for(int j=0; j<mapHeight; j++) {
				if(isBomb[i][j]) System.out.print("*");
				else System.out.print("-");
			}
			System.out.println();
		}
		System.out.println("Around");
		for(int i=0; i<mapWidth; i++) {
			for(int j=0; j<mapHeight; j++) {
				System.out.print(aroundBomb[i][j]);
				
			}
			System.out.println();
		}
	}
	
	private void clean() {
		for(int i=0; i<mapWidth; i++) {
			for(int j=0; j<mapHeight; j++) {
				buttons[i][j].setText("");
				buttons[i][j].setBackground(Color.WHITE);
			}
		}
	}
	
	private void click(int x, int y) {
		if(isBomb[x][y]) {
			gameMessage.setText("Bomb!!!!");
			JOptionPane.showMessageDialog(null, gameMessage);
			buttons[x][y].setBackground(Color.RED);
			gameOver = true;
			return;
		} else {
			if(aroundBomb[x][y] != 0) {
				turn(x, y);
			}
			else {
				int[] queue_x = new int[mapWidth*mapHeight];
				int[] queue_y = new int[mapHeight*mapWidth];
				int pop = 0, push = 0;
				queue_x[push] = x;
				queue_y[push] = y;
				push++;
				
				while(pop < push) {
					int tx = queue_x[pop];
					int ty = queue_y[pop];
					
					if(aroundBomb[tx][ty] == 0) {
						for(int i=0; i<8; i++) {
							int fx = tx + direct[i][0];
							int fy = ty + direct[i][1];
							
							if(inRange(fx, fy) && !isTurned[fx][fy]) {
								isTurned[fx][fy] = true;
								queue_x[push] = fx;
								queue_y[push] = fy;
								push++;
							}
						}
					}
					pop++;
				}
				for(int i=0; i<push; i++) {
					turn(queue_x[i], queue_y[i]);
				}
			}
		}
	}
	
	private boolean inRange(int x, int y) {
		return (0 <= x && x < mapWidth) && (0 <= y && y < mapHeight); 
	}
	
	private void turn(int x, int y) {
		buttons[x][y].setBackground(Color.LIGHT_GRAY);
		if(aroundBomb[x][y] > 0) {
			buttons[x][y].setText(Integer.toString(aroundBomb[x][y]));
		}
		isTurned[x][y] = true;
	}
	
	public void checkGameOver() {
		flaged = 0;
		for(int i=0; i<mapWidth; i++) {
			for(int j=0; j<mapHeight; j++) {
				if(isBomb[i][j] && isFlag[i][j]) {
					flaged++;
				}
			}
		}
		
		if(flaged == bombNum) {
			gameMessage.setText("Congraulation!!");
			JOptionPane.showMessageDialog(null, gameMessage);
			gameOver = true;
			return;
		} else return;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		String[] command = ((JButton) e.getSource()).getActionCommand().split(" ");
		
		if(command[0].equals("restart")) {
			clean();
			gameRunning = false;
			gameMessage.setText("Game Running...");
		}
		
		int x = Integer.parseInt(command[0]);
		int y = Integer.parseInt(command[1]);
		if(!gameRunning) {
			resetGame(x, y);
			gameRunning = true;
		}
		
		if(e.getButton() == MouseEvent.BUTTON1) {
			if(!isTurned[x][y] && !gameOver) {
				System.out.println("click " + x + " " + y);
				click(x, y);
			}
		} else {
			if(!isFlag[x][y]) {
				isFlag[x][y] = true;
				((JButton) e.getSource()).setText("f");
			} else {
				isFlag[x][y] = false;
				((JButton) e.getSource()).setText("");
			}
			checkGameOver();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}

public class Main {
	public static void main(String[] args) {
		Game game = new Game();
		game.jFrame.setVisible(true);
	}
}
