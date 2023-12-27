import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.awt.Dimension;
import java.awt.Font;

public class DrawingApp extends JFrame {
    private JButton pointButton, lineButton, arcButton, circleButton, rectangleButton, keyboardDrawingButton;
    private JComboBox<String> lineStyleComboBox;
    private JSlider lineWidthSlider;
    private JButton colorButton;
    private Color selectedColor;
    private List<Shape> shapes;
    private Shape currentShape;
    private JButton saveButton, openButton;
    private BufferedImage canvasImage; // �������������ڴ洢�Ͳ���ͼ��
    private JPanel drawingPanel;
    private boolean showCoordinates = false;

    public DrawingApp() {
        setTitle("��ͼӦ��");
        setLayout(new BorderLayout());
        setSize(1800,1400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// ��������
        Font largeFont = new Font("Serif", Font.BOLD, 30); // ����һ���µ��������

		JPanel filePanel = new JPanel(); // ���ڱ���ʹ��ļ��İ�ť
        JPanel shapePanel = new JPanel(); // ���ڻ�����״�İ�ť
        JPanel optionsPanel = new JPanel(); // ����ѡ������ͺ��߿�

        filePanel.setLayout(new FlowLayout());
        shapePanel.setLayout(new FlowLayout());
        optionsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        pointButton = new JButton("����");
        lineButton = new JButton("����");
        arcButton = new JButton("����");
        circleButton = new JButton("��Բ");
        rectangleButton = new JButton("���ƾ���");

		saveButton = new JButton("�����ļ�");
		openButton = new JButton("���ļ�");
		
        // ������ʹ򿪰�ť��ӵ� filePanel
        filePanel.add(saveButton);
        filePanel.add(openButton);

        // ��������״�İ�ť��ӵ� shapePanel
        shapePanel.add(pointButton);
        shapePanel.add(lineButton);
        shapePanel.add(arcButton);
        shapePanel.add(circleButton);
        shapePanel.add(rectangleButton);

        lineStyleComboBox = new JComboBox<>(new String[]{"ʵ��", "����"});
        lineWidthSlider = new JSlider(1, 10, 1);
        colorButton = new JButton("ѡ����ɫ");

 		gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 120, 5); // ����������
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lineWidthLabel = new JLabel("����:");
        lineWidthLabel.setFont(largeFont); 
        optionsPanel.add(lineWidthLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0;
        optionsPanel.add(lineStyleComboBox, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel lineWidthLabel2 = new JLabel("�߿�:");
        lineWidthLabel2.setFont(largeFont); // Ϊ��ǩ����������
        optionsPanel.add(lineWidthLabel2, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.5;
        optionsPanel.add(lineWidthSlider, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        optionsPanel.add(colorButton, gbc);
        
        keyboardDrawingButton = new JButton("��ȷ���㣨���̽������ƣ�");
        keyboardDrawingButton.setFont(largeFont);
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		optionsPanel.add(keyboardDrawingButton, gbc);
        
        // ����һ���ܵĹ�������岢������洴�����������
        JPanel toolbarPanel = new JPanel(new BorderLayout());
        toolbarPanel.add(filePanel, BorderLayout.CENTER);
        toolbarPanel.add(shapePanel, BorderLayout.NORTH);
        toolbarPanel.add(optionsPanel, BorderLayout.SOUTH);
        // �������������ӵ�������
        add(toolbarPanel, BorderLayout.NORTH);

        
        // Ϊ��ť���������������������
        pointButton.setFont(largeFont);
        lineButton.setFont(largeFont);
        arcButton.setFont(largeFont);
        circleButton.setFont(largeFont);
        rectangleButton.setFont(largeFont);
        saveButton.setFont(largeFont);
        openButton.setFont(largeFont);
        colorButton.setFont(largeFont);

        lineWidthSlider.setFont(largeFont);
        lineStyleComboBox.setFont(largeFont);
        
        canvasImage = new BufferedImage(1800, 1400, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = canvasImage.createGraphics();
		g2d.setColor(Color.WHITE); // ���ñ�����ɫΪ��ɫ
		g2d.fillRect(0, 0, getWidth(), getHeight()); // �������ͼ��
		g2d.dispose();

        // ��ͼ����
        drawingPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
			    super.paintComponent(g);
			    g.drawImage(canvasImage, 0, 0, null);
			    if (showCoordinates) {
			        drawCoordinates(g);
			    }
			}
        };
        drawingPanel.setBackground(Color.WHITE);
        drawingPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (pointButton.isSelected()) {
                    currentShape = new PointShape(e.getX(), e.getY(), selectedColor);
                } else if (lineButton.isSelected()) {
                    currentShape = new LineShape(e.getX(), e.getY(), selectedColor, getSelectedLineStyle(), lineWidthSlider.getValue());
                } else if (arcButton.isSelected()) {
                   currentShape = new ArcShape(e.getX(), e.getY(), selectedColor, getSelectedLineStyle(), lineWidthSlider.getValue());
                } else if (circleButton.isSelected()) {
                    currentShape = new CircleShape(e.getX(), e.getY(), selectedColor, getSelectedLineStyle(), lineWidthSlider.getValue());
                } else if (rectangleButton.isSelected()) {
                    currentShape = new RectangleShape(e.getX(), e.getY(), selectedColor, getSelectedLineStyle(), lineWidthSlider.getValue());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
				if (currentShape != null) {
				        Graphics2D g2d = canvasImage.createGraphics();
				        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				        currentShape.draw(g2d);
				        g2d.dispose();
				        shapes.add(currentShape);
				        currentShape = null;
				        drawingPanel.repaint();
				}
            }
        });
        drawingPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
			    if (currentShape != null) {
			        currentShape.updateEndPoint(e.getX(), e.getY());
			        drawingPanel.repaint(); // ֻ�ػ���壬���޸� canvasImage
			    }
            }
        });

        // ��������������
        add(toolbarPanel, BorderLayout.NORTH);
        add(optionsPanel, BorderLayout.WEST);
        add(drawingPanel, BorderLayout.CENTER);

        // ����¼�������
        pointButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetButtons();
                pointButton.setSelected(true);
            }
        });

        lineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetButtons();
                lineButton.setSelected(true);
            }
        });

		arcButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		        resetButtons();
		        arcButton.setSelected(true);
		    }
		});
        
        circleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetButtons();
                circleButton.setSelected(true);
            }
        });

        rectangleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetButtons();
                rectangleButton.setSelected(true);
            }
        });

        colorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(DrawingApp.this, "ѡ����ɫ", selectedColor);
                if (newColor != null) {
                    selectedColor = newColor;
                }
            }
        });
        
        keyboardDrawingButton.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	showCoordinates = true;
        		drawingPanel.repaint();
		        String inputX = JOptionPane.showInputDialog(DrawingApp.this, "������X����:");
		        String inputY = JOptionPane.showInputDialog(DrawingApp.this, "������Y����:");
		        try {
		            int x = Integer.parseInt(inputX);
		            int y = Integer.parseInt(inputY);
		            Graphics2D g2d = canvasImage.createGraphics();
		            g2d.setColor(selectedColor);
		            g2d.fillOval(x - 4, y - 4, 8, 8);
		            g2d.dispose();
		            drawingPanel.repaint();
		            showCoordinates = false;
		        } catch (NumberFormatException ex) {
		            JOptionPane.showMessageDialog(DrawingApp.this, "��������Ч�����꣡");
		        }
		    }
		});
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
		        if (canvasImage != null) {
		            JFileChooser fileChooser = new JFileChooser();
		            fileChooser.setDialogTitle("����ͼ��");
		            fileChooser.setFileFilter(new FileNameExtensionFilter("PNG ͼƬ", "png"));
		            if (fileChooser.showSaveDialog(DrawingApp.this) == JFileChooser.APPROVE_OPTION) {
		                File file = fileChooser.getSelectedFile();
		                String filePath = file.getAbsolutePath();
		                if (!filePath.toLowerCase().endsWith(".png")) {
		                    filePath += ".png";
		                }
		                try {
		                    ImageIO.write(canvasImage, "PNG", new File(filePath));
		                } catch (IOException ex) {
		                    ex.printStackTrace();
		                }
		            }
		        }
		    }
		});
			
		openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("��ͼ��");
                fileChooser.setFileFilter(new FileNameExtensionFilter("PNG ͼƬ", "png"));
                if (fileChooser.showOpenDialog(DrawingApp.this) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        canvasImage = ImageIO.read(file); // ��ȡͼ���ļ�
                        drawingPanel.repaint(); // �ػ��������ʾͼ��
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        setVisible(true);

        shapes = new ArrayList<>();
        selectedColor = Color.BLACK;
    }
    
    public BufferedImage getCanvasImage() {
        return canvasImage;
    }

    private void resetButtons() {
        pointButton.setSelected(false);
        lineButton.setSelected(false);
        arcButton.setSelected(false);
        circleButton.setSelected(false);
        rectangleButton.setSelected(false);
    }

    private int getSelectedLineStyle() {
        return lineStyleComboBox.getSelectedIndex() == 0 ? BasicStroke.CAP_ROUND : BasicStroke.CAP_BUTT;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DrawingApp();
            }
        });
    }

    private interface Shape extends Serializable{
        void draw(Graphics2D g2d);

        void updateEndPoint(int x, int y);
    }
    
    private void drawCoordinates(Graphics g) {
	    int width = drawingPanel.getWidth();
	    int height = drawingPanel.getHeight();
	
	    // ��������
	    Font font = new Font("Serif", Font.PLAIN, 12);
	    g.setFont(font);
	    
	    // ����������
	    g.setColor(Color.GRAY);
	    g.drawLine(0, 0, width, 0); // X��
	    g.drawLine(0, 0, 0, height); // Y��
	
	    // ���ƿ̶ȣ��ɸ�����Ҫ�����̶ȼ��ʹ�С��
	    for (int i = 0; i < width; i += 50) {
	        g.drawLine(i, 0, i, 10);
	        g.drawString(String.valueOf(i), i - 4, 20); // X��
	    }
	    for (int i = 0; i < height; i += 50) {
	        g.drawLine(0, i, 10, i);
	        g.drawString(String.valueOf(i), 15, i + 4); // Y��
	    }
	}

    private class PointShape implements Shape {
        private transient int x, y;
        private Color color;

        public PointShape(int x, int y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }

        @Override
        public void draw(Graphics2D g2d) {
            int pointSize = 8; // ���Ը�����Ҫ������Ĵ�С
        	g2d.setColor(color);
        	g2d.fillOval(x - pointSize / 2, y - pointSize / 2, pointSize, pointSize);
        }

        @Override
        public void updateEndPoint(int x, int y) {
            // ����Ҫ����
        }
    }

    private class LineShape implements Shape {
        private int startX, startY, endX, endY;
        private Color color;
        private int lineStyle;
        private int lineWidth;

        public LineShape(int startX, int startY, Color color, int lineStyle, int lineWidth) {
            this.startX = startX;
            this.startY = startY;
            this.endX = startX;
            this.endY = startY;
            this.color = color;
            this.lineStyle = lineStyle;
            this.lineWidth = lineWidth;
        }

        @Override
        public void draw(Graphics2D g2d) {
            g2d.setColor(color);
            // ��������
            if (lineStyle == BasicStroke.CAP_BUTT) {
                float[] dashPattern = {10, 10}; // ���Ը�����Ҫ�������ߵ�ģʽ
                g2d.setStroke(new BasicStroke(lineWidth, lineStyle, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, dashPattern, 0));
            } else {
                g2d.setStroke(new BasicStroke(lineWidth, lineStyle, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            }
			g2d.drawLine(startX, startY, endX, endY);
        }

        @Override
        public void updateEndPoint(int x, int y) {
            endX = x;
            endY = y;
        }
    }

    private class ArcShape implements Shape {
        private int startX, startY, endX, endY;
        private Color color;
        private int lineStyle;
        private int lineWidth;
        //private int startAngle;
    	//private int arcAngle;

        public ArcShape(int startX, int startY, Color color, int lineStyle, int lineWidth) {
        	this.startX = startX;
            this.startY = startY;
            this.endX = startX;
            this.endY = startY;
            this.color = color;
            this.lineStyle = lineStyle;
            this.lineWidth = lineWidth;
            //this.startAngle = startAngle;
        	//this.arcAngle = arcAngle;
        }

        @Override
        public void draw(Graphics2D g2d) {
            g2d.setColor(color);
            // ��������
            if (lineStyle == BasicStroke.CAP_BUTT) {
                float[] dashPattern = {10, 10}; // ���Ը�����Ҫ�������ߵ�ģʽ
                g2d.setStroke(new BasicStroke(lineWidth, lineStyle, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, dashPattern, 0));
            } else {
                g2d.setStroke(new BasicStroke(lineWidth, lineStyle, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            }
            int width = Math.abs(endX - startX);
            int height = Math.abs(endY - startY);
            int x = Math.min(startX, endX);
            int y = Math.min(startY, endY);
            g2d.drawArc(x, y, width, height, 0, 180);
        }

        @Override
        public void updateEndPoint(int x, int y) {
            endX = x;
            endY = y;
        }
    }
    
    private class CircleShape implements Shape {
        private int startX, startY, endX, endY;
        private Color color;
        private int lineStyle;
        private int lineWidth;

        public CircleShape(int startX, int startY, Color color, int lineStyle, int lineWidth) {
            this.startX = startX;
            this.startY = startY;
            this.endX = startX;
            this.endY = startY;
            this.color = color;
            this.lineStyle = lineStyle;
            this.lineWidth = lineWidth;
        }

        @Override
        public void draw(Graphics2D g2d) {
            g2d.setColor(color);
            // ��������
            if (lineStyle == BasicStroke.CAP_BUTT) {
                float[] dashPattern = {10, 10}; // ���Ը�����Ҫ�������ߵ�ģʽ
                g2d.setStroke(new BasicStroke(lineWidth, lineStyle, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, dashPattern, 0));
            } else {
                g2d.setStroke(new BasicStroke(lineWidth, lineStyle, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            }
            int width = Math.abs(endX - startX);
            int height = Math.abs(endY - startY);
            int x = Math.min(startX, endX);
            int y = Math.min(startY, endY);
            g2d.drawArc(x, y, width, height, 0, 360);
        }

        @Override
        public void updateEndPoint(int x, int y) {
            endX = x;
            endY = y;
        }
    }

    private class RectangleShape implements Shape {
        private int startX, startY, endX, endY;
        private Color color;
        private int lineStyle;
        private int lineWidth;

        public RectangleShape(int startX, int startY, Color color, int lineStyle, int lineWidth) {
            this.startX = startX;
            this.startY = startY;
            this.endX = startX;
            this.endY = startY;
            this.color = color;
            this.lineStyle = lineStyle;
            this.lineWidth = lineWidth;
        }

        @Override
        public void draw(Graphics2D g2d) {
            g2d.setColor(color);
            // ��������
            if (lineStyle == BasicStroke.CAP_BUTT) {
                float[] dashPattern = {10, 10}; // ���Ը�����Ҫ�������ߵ�ģʽ
                g2d.setStroke(new BasicStroke(lineWidth, lineStyle, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, dashPattern, 0));
            } else {
                g2d.setStroke(new BasicStroke(lineWidth, lineStyle, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            }
            int width = Math.abs(endX - startX);
            int height = Math.abs(endY - startY);
            int x = Math.min(startX, endX);
            int y = Math.min(startY, endY);
            g2d.drawRect(x, y, width, height);
        }

        @Override
        public void updateEndPoint(int x, int y) {
            endX = x;
            endY = y;
        }
    }
}