import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.*;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;

public class Game {
    	
	private JFrame frame;
    private JPanel panel;
    private JButton[] buttons;
    private JButton startButton, resetButton;
    private int moves;//��ǰ����
    private int emptyIndex; // �հ׸������
    private ArrayList<Integer> numbers; // �洢����
    private Map<String, Integer> scoreBoard; // ��¼��ҵ÷�
    private String playerName; //����ǳ�
    private JLabel statusLabel; // ������ʾ״̬��Ϣ�ı�ǩ
	public static int[][] a;//���ڴ��4*4��ά���飬�������puzzlesolver
	public static int minSteps; //���ڴ��PuzzleSolver�м������minSteps
	
    private void initialize() {
    	a = new int[4][4];
        // ��������
        frame = new JFrame("Puzzle Game");
        frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    	// �����������ְ�ť�����
    	JPanel gridPanel = new JPanel(new GridLayout(4, 4));
    	// �������ڿ��ư�ť�����
    	JPanel controlPanel = new JPanel(new FlowLayout());

        // ��ʼ����ť
        buttons = new JButton[16];
        for (int i = 0; i < 16; i++) {
            buttons[i] = new JButton();
            buttons[i].setFont(new Font("Arial", Font.BOLD, 40));
            gridPanel.add(buttons[i]);
        }

        startButton = new JButton("��ʼ");
        resetButton = new JButton("����");
        
        startButton.setFont(new Font("����", Font.BOLD, 30));
		resetButton.setFont(new Font("����", Font.BOLD, 30));
		startButton.setPreferredSize(new Dimension(120, 50));
		resetButton.setPreferredSize(new Dimension(120, 50));
        
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetGame();
            }
        });
        
    	controlPanel.add(startButton);
    	controlPanel.add(resetButton);
    	
        frame.add(gridPanel, BorderLayout.CENTER);
    	frame.add(controlPanel, BorderLayout.SOUTH);
    	
    	// ����״̬��ǩ
    	statusLabel = new JLabel("Moves");
    	statusLabel.setFont(new Font("����", Font.BOLD, 25));
    	controlPanel.add(statusLabel);
        
        // ��ʼ�������б�
        numbers = new ArrayList<Integer>();
		PuzzleGenerator.generate();
		numbers.clear();
		for (int i = 0; i < 4; i++) {
		    for (int j = 0; j < 4; j++) {
		        numbers.add(a[i][j]);
		    }
		}
	    PuzzleSolver.solve(); //�����С�ƶ�����minSteps
	    emptyIndex = numbers.indexOf(0);
		moves = 0;
        scoreBoard = new LinkedHashMap<String, Integer>();
        startButton.setVisible(true);
        frame.setVisible(true);
	    updateButtons();
		updateStatus();
	    updateStartButtonState();
	    
	    for (int i = 0; i < 16; i++) {
            buttons[i].setEnabled(false);
		}
	
	    
	    // ����¼�������
        for (int i = 0; i < 16; i++) {
            final int index = i;
            buttons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (canMove(index)) {
                        move(index);
                        if (isSolved()) {
                            JOptionPane.showMessageDialog(frame, "��ϲ������Ϸ��ɣ�");
                            //ѯ���ǳ�
                            askForPlayerName();
					        // ���µ÷�
					        updateScoreBoard();
					        // ���غ���ʾ���а�
					        loadScoreBoard();
					        showScoreBoard();
					        // �������а�
					        saveScoreBoard();
                        }
                    }
                }
            });
        }
    	


    }

    private void askForPlayerName() {
        playerName = JOptionPane.showInputDialog(frame, "���������Ϸ�ǳƣ��������а�:");
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Unknown Player";
        }
    }
    
	private boolean isSolvable() {
	    int inversions = 0;
	    int tot = 0;
	    int[] puz = new int[16];
    	for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 4; ++j) {
                puz[tot] = a[i][j];
                tot++;
            }
        }
        for (int i = 0; i < 16; ++i) {
            if (puz[i] == 0)
                inversions += 3 - i / 4;
            else {
		        for (int j = 0; j < i; j++) {
	                if (puz[j] > puz[i] && puz[j] != 0) {
	                    inversions++;
		            }
		        }
    		}
        }
    	return (inversions % 2 == 0);
    	// �����������ż������������ǿɽ��
	}

    private void startGame() {
		startButton.setVisible(false);
		for (int i = 0; i < 16; i++) {
            buttons[i].setEnabled(true);
		}
    }
    
	private void updateStartButtonState() {
    if (isSolvable()&&(minSteps < 45)) {
        startButton.setEnabled(true);
        startButton.setBackground(Color.GREEN);
    } else {
        startButton.setEnabled(false);
        startButton.setBackground(Color.GRAY);
    	}
	}
	
	private void resetGame() {
	    PuzzleGenerator.generate();
	    numbers.clear();
		for (int i = 0; i < 4; i++) {
		    for (int j = 0; j < 4; j++) {
		        numbers.add(a[i][j]);
		    }
		}
	    PuzzleSolver.solve(); //�����С�ƶ�����minSteps
	    
	    for (int i = 0; i < 16; i++) {
            buttons[i].setEnabled(false);
		}
        emptyIndex = numbers.indexOf(0);
	    moves = 0;
	    updateButtons();
	    startButton.setVisible(true);
	    updateStartButtonState();
	    updateStatus();
	}

	private void enableButtons(boolean enable) {
	    for (JButton button : buttons) {
	        button.setEnabled(enable);
	    }
	}

    private boolean canMove(int index) {
        // ����Ƿ�����ƶ�
        return Math.abs(index - emptyIndex) == 1 || Math.abs(index - emptyIndex) == 4;
    }

    private void move(int index) {

        // ����λ��
        Collections.swap(numbers, index, emptyIndex);
        emptyIndex = index;
        updateButtons();
        moves++;
        updateStatus();
    }
    
    private void updateScoreBoard() {
	    int score = (100 * minSteps) / Math.max(moves, 1); // ȷ����������
	    scoreBoard.put(playerName, score);
	}
	
    private void loadScoreBoard() {
	    try {
	        File file = new File("scoreboard.txt");
	        if (file.exists()) {
	            BufferedReader reader = new BufferedReader(new FileReader(file));
	            String line;
	            while ((line = reader.readLine()) != null) {
	                String[] parts = line.split(",");
	                if (parts.length == 2) {
	                    scoreBoard.put(parts[0], Integer.parseInt(parts[1]));
	                }
	            }
	            reader.close();
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
    private void showScoreBoard() {

		java.util.List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>();
		entries.addAll(scoreBoard.entrySet());
	    // ���շ�����������
	    Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
		    @Override
		    public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
		        return o2.getValue().compareTo(o1.getValue());
		    }
		});
	
	    StringBuilder sb = new StringBuilder("<html><h1 style='text-align: center;font-size: 30px;'>���а�</h1><table>");
	    for (Map.Entry<String, Integer> entry : entries) {
	        sb.append(entry.getKey()).append("</td><td style='text-align: right;'>").append(entry.getValue()).append("</td></tr>");
	    }
		sb.append("</table></html>");
		
	    // ��ʾ���а�
	    JOptionPane.showMessageDialog(frame, sb.toString(), "Score Board", JOptionPane.INFORMATION_MESSAGE);
        
    }
    
    private void saveScoreBoard() {
	    try {
	        File file = new File("scoreboard.txt");
	        FileWriter writer = new FileWriter(file, false); // false ��ʾ��׷�ӣ�ÿ�ζ���д�ļ�
	
	        for (Map.Entry<String, Integer> entry : scoreBoard.entrySet()) {
	            writer.write(entry.getKey() + "," + entry.getValue() + "\n");
	        }
	
	        writer.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	
	private void updateButtons() {
        // ���°�ť��ʾ
        for (int i = 0; i < 16; i++) {
            if (numbers.get(i) == 0) {
                buttons[i].setText("");
            } else {
                buttons[i].setText(String.valueOf(numbers.get(i)));
            }
        }
    }
    
    private void updateStatus() {
	    // ����״̬��ǩ���ı�
	    if (isSolvable()&&(minSteps < 45)){
	    	statusLabel.setText("    ��ǰ����: " + moves+"    ��С����: " + minSteps);
	    }else{
	    	statusLabel.setText("  ��ǰ״̬���ɽ����С����������Χ  " );
	    }
	}


    private boolean isSolved() {
        // ����Ƿ���
        for (int i = 0; i < 15; i++) {
            if (numbers.get(i) != i + 1) {
                return false;
            }
        }
        return true;
    }

    public void show() {
        frame.setVisible(true);
    }
    
    public Game() {
        initialize();
    }
    
    public static void main(String[] args) {
    	// ���������С
    	UIManager.put("OptionPane.messageFont", new Font("����", Font.BOLD, 30));
        UIManager.put("OptionPane.buttonFont", new Font("����", Font.PLAIN, 25));
        UIManager.put("OptionPane.textFieldFont", new Font("Arial", Font.BOLD, 25));
        // ���¼������߳�������Ӧ�ó���
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Game().show();
            }
        });
    }
}

