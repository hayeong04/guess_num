package guess_num;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.plaf.FontUIResource;

@SuppressWarnings("serial")
public class GuseeNumGame extends JFrame implements MouseListener {
    private Container con = getContentPane();
    GuessNumManager manager = GuessNumManager.getInstance();
    public static final String MESSAGE = "1부터 100사이의 숫자를 맞춰보세요.";

    // north
    private JPanel palNorth = new JPanel();
    private JTextField tfInput = new JTextField(7); // 입력
    private JButton btnInput = new JButton("입력");
    private JTextField tfRecord = new JTextField("30000", 10); // 기록
    private JButton btnNewgame = new JButton("새게임");

    // center
    private TextArea taMessage = new TextArea(MESSAGE);

    // south
    private JPanel palSouth = new JPanel();
    private JTextField tfHeart = new JTextField(7); // 하트
    private MyLabel lblBar = new MyLabel();
    private int barSize = 150;
    private int maxBarSize = 150;
    private long endTime;
    private long startTime;
    private int heartCount = 5; // 남은 하트 수
    private boolean isRunning = false;

    public GuseeNumGame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("guess_num");
        setSize(500, 500);
        setNorth();
        setCenter();
        setSouth();
        setListener();
        setVisible(true);
    }

    private void setListener() {
        btnInput.addMouseListener(this);
        btnNewgame.addMouseListener(this);
    }

    private void setCenter() {
        taMessage.setFont(new FontUIResource("맑은 고딕", Font.BOLD, 20));
        con.add(new JScrollPane(taMessage));
        taMessage.setEditable(false);
    }

    private void setNorth() {
        JLabel lblInput = new JLabel("입력 : ");
        JLabel lblRecord = new JLabel("기록 : ");
        tfRecord.setEditable(false);
        tfInput.setEditable(false);
        btnInput.setEnabled(false);
        palNorth.add(lblInput);
        palNorth.add(tfInput);
        palNorth.add(btnInput);
        palNorth.add(lblRecord);
        palNorth.add(tfRecord);
        palNorth.add(btnNewgame);
        palNorth.setBackground(Color.MAGENTA);
        con.add(palNorth, BorderLayout.NORTH);
        tfInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                appendMessage();
            }
        });
    }

    private void setSouth() {
        JPanel palCount = new JPanel();
        JLabel lblCount = new JLabel("남은횟수:");
        tfHeart.setEditable(false);
        palCount.add(lblCount);
        palCount.add(tfHeart);
        palCount.setBackground(Color.CYAN);
        palSouth.add(palCount);
        palSouth.setLayout(new GridLayout(1, 2));
        con.add(palSouth, BorderLayout.SOUTH);
        // bar
        JPanel palBar = new JPanel();
        palBar.setLayout(null);
        lblBar.setOpaque(true);
        lblBar.setBackground(Color.YELLOW);
        lblBar.setSize(200, 20);
        lblBar.setLocation(4, 4);
        palBar.add(lblBar);
        palSouth.add(palBar);

        Thread th = new Thread(lblBar);
        th.start();
    }

    public class MyLabel extends JLabel implements Runnable {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.GREEN);
            g.fillRect(0, 0, barSize, 20);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000); //1초
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (this) {
                    if (isRunning) {
                        barSize -=2.5;
                        repaint();
                        if (barSize <= 0) {
                            heartCount--;
                            if (heartCount <= 0) {
                                gameEnd();
                            } else {
                                barSize = maxBarSize; // 막대기 초기화
                                paintHeart();
                            }
                        }
                    }
                }
            }
        }
    }

    private void appendMessage() {
        String textInput = tfInput.getText();
        int result = -1; // 결과를 저장할 변수 초기화
        try {
            int userNum = Integer.parseInt(textInput);
            if (userNum < 1 || userNum > 100) {
                JOptionPane.showMessageDialog(GuseeNumGame.this, "1~100사이의 숫자를 입력해주세요", "알림",
                        JOptionPane.ERROR_MESSAGE);
                tfInput.setText("");
                return;
            }
            result = manager.judge(userNum); // GuessNumManager 클래스의 judge() 메서드를 호출하여 결과를 받아옴
            inputMessage(result, userNum);
            tfInput.setText("");

        } catch (NumberFormatException f) {
            JOptionPane.showMessageDialog(GuseeNumGame.this, "숫자만 입력해주세요", "알림", JOptionPane.ERROR_MESSAGE);
            tfInput.setText("");
            return;
        }
        int count = manager.getcount();
        if (count == 0) {
            JOptionPane.showMessageDialog(GuseeNumGame.this, "기회를 모두 사용하였습니다.", "알림", JOptionPane.ERROR_MESSAGE);
            gameEnd();
		} /*
			 * else if (count == 4) { taMessage.append(" \n기회는 4번 남았습니다."); } else if (count
			 * == 3) { taMessage.append(" \n기회는 3번 남았습니다."); } else if (count == 2) {
			 * taMessage.append(" \n기회는 2번 남았습니다."); } else if (count == 1) {
			 * taMessage.append(" \n기회는 1번 남았습니다."); }
			 */
        paintHeart();
        if (result != GuessNumManager.RESULT_OK && result != -1) { // 정답이 아닐 때만 막대기 초기화
            barSize = maxBarSize;
        }
        if (result != GuessNumManager.RESULT_OK) { // 정답이 아닐 때 하트 감소
            barSize--;
            if (barSize <= 0) { // 막대기가 다 없어질 때 하트 감소
                heartCount--;
                count--; // 남은 횟수 감소
                if (heartCount <= 0) {
                    gameEnd();
                }
            }
        }
    }

    private void reStart() {
        tfInput.setEditable(true);
        btnInput.setEnabled(true);
        taMessage.setText("");
        taMessage.setText(MESSAGE);
        taMessage.append("\n기회는 5번입니다.");
        manager.init();
        heartCount = 5; // 남은 하트 수 초기화
        barSize = maxBarSize; // 막대 크기 초기화
        isRunning = true; // 게임 상태 활성화
        startTime = System.currentTimeMillis();
        paintHeart();
        
     // 숫자 입력 창에 포커스를 설정
        tfInput.requestFocus();
    }

    private void paintHeart() {
        String heart = "";
        for (int i = 0; i < heartCount; i++) {
            heart += "♥";
        }
        tfHeart.setText(heart);
    }

    private void gameEnd() {
        tfInput.setEditable(false);
        btnInput.setEnabled(false);
        isRunning = false; // 게임 상태 비활성화
        JOptionPane.showMessageDialog(GuseeNumGame.this, "게임 종료! 기회를 모두 사용하였습니다.", "알림", JOptionPane.ERROR_MESSAGE);
    }

    private void countTime() {
    	endTime = System.currentTimeMillis();
        long leadTime = endTime - startTime;
        String textTime = tfRecord.getText();
        long time = Long.parseLong(textTime);
        if (leadTime < time) {
            tfRecord.setText(String.valueOf(leadTime));
        }
    }

    private void inputMessage(int result, int userNum) {
        switch (result) {
            case GuessNumManager.RESULT_OK:
                JOptionPane.showMessageDialog(GuseeNumGame.this, "정답입니다.", "알림", JOptionPane.OK_CANCEL_OPTION);
                gameEnd();
                countTime();
             // endTime을 사용하여 정답을 맞춘 시간을 계산
                long answerTime = endTime - startTime;
                JOptionPane.showMessageDialog(GuseeNumGame.this, "정답을 맞춘 시간은 " + answerTime / 1000 + "초 입니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
                break;
                
            case GuessNumManager.RESULT_BIG:
                taMessage.append("\n" + userNum + "보다 큽니다.");
                heartCount--; // 하트 감소
                break;
            case GuessNumManager.RESULT_SMALL:
                taMessage.append("\n" + userNum + "보다 작습니다.");
                heartCount--; // 하트 감소
                break;
        }
    }

    public static void main(String[] args) {
        new GuseeNumGame();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Object obj = e.getSource();
        if (obj == btnNewgame) {
            reStart();
        } else if (obj == btnInput) {
            appendMessage();
        }
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
}
