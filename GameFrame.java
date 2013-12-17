package mainFrame;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class GameFrame extends JFrame
{

	private static final long serialVersionUID = 1L;
	/* 图形组件 */
	ChessBoardPanel panChessboard = new ChessBoardPanel();
	JPanel panOption = new JPanel();
	JLabel lbFirstPlayerChessColor = new JLabel("先手棋子颜色");
	JRadioButton rbtBlack = new JRadioButton("黑子");
	JRadioButton rbtWhite = new JRadioButton("白子");
	ButtonGroup bgChessColor = new ButtonGroup();
	JButton btnStart = new JButton("开始游戏");
	JButton btnGiveIn = new JButton("认输");
	JButton btnOver = new JButton("结束");
	JRadioButton rbtPlayers = new JRadioButton("双人对战");
	JRadioButton rbtManVSRobot = new JRadioButton("人机对战");
	JRadioButton rbtRobots = new JRadioButton("双机对战");
	ButtonGroup bgGameMode = new ButtonGroup();
	JRadioButton rbtPlayerFirst = new JRadioButton("玩家先走");
	JRadioButton rbtRobotFirst = new JRadioButton("电脑先走");
	ButtonGroup bgFirst = new ButtonGroup();
	JLabel lbCurrentPlayerChessColor = new JLabel("HELLO");

	public GameFrame() throws Exception
	{
		/* 图形界面搭建 */
		this.setTitle("五子棋游戏");
		this.setSize(655, 566);
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
		Container gfc = this.getContentPane();
		gfc.add(panChessboard);
		gfc.add(panOption);
		panOption.setPreferredSize(new Dimension(100,
				ChessBoardPanel.TABLE_HIGHT));
		panOption.setLayout(new BoxLayout(panOption, BoxLayout.Y_AXIS));
		panOption.add(Box.createVerticalStrut(90), FlowLayout.LEFT);
		lbCurrentPlayerChessColor.setFont(new Font(null,Font.PLAIN,30));
		panOption.add(lbCurrentPlayerChessColor, FlowLayout.LEFT);
		panOption.add(rbtRobotFirst, FlowLayout.LEFT);
		panOption.add(rbtPlayerFirst, FlowLayout.LEFT);
		bgFirst.add(rbtRobotFirst);
		bgFirst.add(rbtPlayerFirst);
		panOption.add(Box.createVerticalStrut(5), FlowLayout.LEFT);
		panOption.add(rbtRobots,FlowLayout.LEFT);
		panOption.add(rbtManVSRobot, FlowLayout.LEFT);
		panOption.add(rbtPlayers, FlowLayout.LEFT);
		bgGameMode.add(rbtRobots);
		bgGameMode.add(rbtManVSRobot);
		bgGameMode.add(rbtPlayers);
		panOption.add(Box.createVerticalStrut(5), FlowLayout.LEFT);
		panOption.add(btnGiveIn, FlowLayout.LEFT);
		panOption.add(Box.createVerticalStrut(5), FlowLayout.LEFT);
		panOption.add(btnOver, FlowLayout.LEFT);
		panOption.add(Box.createVerticalStrut(5), FlowLayout.LEFT);
		panOption.add(btnStart, FlowLayout.LEFT);
		panOption.add(Box.createVerticalStrut(5), FlowLayout.LEFT);
		panOption.add(rbtBlack, FlowLayout.LEFT);
		panOption.add(rbtWhite, FlowLayout.LEFT);
		bgChessColor.add(rbtBlack);
		bgChessColor.add(rbtWhite);
		panOption.add(Box.createVerticalStrut(5), FlowLayout.LEFT);
		panOption.add(lbFirstPlayerChessColor, FlowLayout.LEFT);
		
		rbtBlack.setSelected(true);
		rbtPlayers.setSelected(true);

		/* 各组件响应事件的操作定义 */
		btnStart.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				game=new Game();
				game.start();
			}

		});
		btnOver.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				game.isPlaying=false;
			}

		});
		btnGiveIn.addActionListener(btnOver.getActionListeners()[0]);
		rbtPlayers.addItemListener(new ItemListener()
		{

			public void itemStateChanged(ItemEvent arg0)
			{
				rbtPlayerFirst.setSelected(true);

			}

		});
		rbtManVSRobot.addItemListener(rbtPlayers.getItemListeners()[0]);
		rbtRobots.addItemListener(new ItemListener()
		{

			public void itemStateChanged(ItemEvent arg0)
			{
				rbtRobotFirst.setSelected(true);
			}

		});

	}

	class ChessBoardPanel extends JPanel// 重载JPanel的paint方法，实现绘制盘面
	{
		private static final long serialVersionUID = 1L;

		BufferedImage IMAGE_TABLE; // 棋盘图像
		// BufferedImage IMAGE_BLACK; //黑子图像
		// BufferedImage IMAGE_WHITE; //白子图像
		// BufferedImage IMAGE_SELECT; //鼠标移动时候的选择框图像
		BufferedImage[] IMAGE_CHESS = new BufferedImage[4];
		public static final byte EMPTY = 0;
		public static final byte SELECT = 1;
		public static final byte BLACK = 2;
		public static final byte WHITE = 3;
		//棋盘图像大小
		public static final int TABLE_WIDTH = 535, TABLE_HIGHT = 536;
		public static final int GRID_SPACE = 35;		//网格间距
		public static final int X_SET = 6, Y_SET = 7;	//原点偏移量
		public static final int CHESSBOARD_SIZE = 15; // 定义棋盘的大小
		//游戏中玩家在棋盘上移动鼠标时的鼠标的棋盘坐标
		Point curPoint = new Point(CHESSBOARD_SIZE / 2, CHESSBOARD_SIZE / 2);
		//棋盘
		public byte chessboard[][] = new byte[CHESSBOARD_SIZE][CHESSBOARD_SIZE];// 设置棋盘棋子状态，0：无子，1：黑子，2：白子
		// 指示当前是否有玩家占用棋盘
		public boolean busy=true;
		
		public ChessBoardPanel()
		{
			try
			{
				IMAGE_TABLE = ImageIO.read(new File("image/table.jpg"));
				IMAGE_CHESS[EMPTY] = null;
				IMAGE_CHESS[SELECT] = ImageIO.read(new File(
						"image/selecting.gif"));
				IMAGE_CHESS[BLACK] = ImageIO.read(new File("image/black.gif"));
				IMAGE_CHESS[WHITE] = ImageIO.read(new File("image/white.gif"));
			} catch (IOException ioe)
			{
				JOptionPane.showMessageDialog(null, "读取图像文件失败！", "错误",
						JOptionPane.ERROR_MESSAGE);
			}
			this.setPreferredSize(new Dimension(TABLE_WIDTH, TABLE_HIGHT));
			for (int i = 0; i < chessboard.length; i++)
				for (int j = 0; j < chessboard.length; j++)
				{
					chessboard[i][j] = EMPTY;
				}
		}

		/** 清理棋盘上棋子 */
		public void clearBoard()
		{
			int i, j;
			for (i = 0; i < chessboard.length; i++)
				for (j = 0; j < chessboard.length; j++)
				{
					chessboard[i][j] = EMPTY;
				}
		}

		/** 设置棋盘对鼠标事件的玩家响应 */
		public void setPlayerListener()
		{
			this.addMouseMotionListener(new MouseMotionAdapter()
			{
				public void mouseMoved(MouseEvent e)
				{
					Point tPoint = ChessPoint(e.getPoint());
					if (!tPoint.equals(curPoint))
					{
						if (SELECT == chessboard[curPoint.x][curPoint.y])
						{
							chessboard[curPoint.x][curPoint.y] = EMPTY;
						}
						if (isInBoard(tPoint))
						{
							curPoint.setLocation(tPoint);
							if (chessboard[tPoint.x][tPoint.y] == EMPTY)
							{
								chessboard[tPoint.x][tPoint.y] = SELECT;
							}
						} else
						{
							curPoint.setLocation(CHESSBOARD_SIZE / 2,
									CHESSBOARD_SIZE / 2);
						}
						repaint();
					}
				}
			});
			this.addMouseListener(new MouseAdapter()
			{
				/* 对当前玩家的动作进行监听*/
				public void mouseReleased(MouseEvent e)
				{
					if (ChessBoardPanel.SELECT == panChessboard.chessboard[curPoint.x][curPoint.y])
					{
						players[current].listenBoard(curPoint);
						busy=false;					
					}
				}

			});
		}

//		/** 设置棋盘对鼠标事件的机器人响应 */
//		public void setRobotListener()
//		{
//			this.addMouseListener(new MouseAdapter()
//			{
//
//				public void mouseReleased(MouseEvent e)
//				{
//					players[current].listenBoard(curPoint);
//				}
//
//			});
//		}

		/** 清除棋盘对鼠标事件的响应 */
		public void clearListener()
		{
			int i;
			MouseMotionListener[] mml = this.getMouseMotionListeners();
			for (i = 0; i < mml.length; i++)
			{
				this.removeMouseMotionListener(mml[i]);
			}
			MouseListener[] ml = this.getMouseListeners();
			for (i = 0; i < ml.length; i++)
			{
				this.removeMouseListener(ml[i]);
			}
		}

		/** 绘制棋盘 */
		public void paint(Graphics G)
		{
			G.drawImage(IMAGE_TABLE, 0, 0, null);// 将绘制五子棋棋盘
			// 遍历数组，绘制棋子
			int i, j, m, n;
			for (i = 0, m = X_SET; i < CHESSBOARD_SIZE; i++, m += GRID_SPACE)
			{
				for (j = 0, n = Y_SET; j < CHESSBOARD_SIZE; j++, n += GRID_SPACE)
				{
					G.drawImage(IMAGE_CHESS[chessboard[i][j]], m, n, null);
				}
			}
		}

		/** 落子
		 * @param chesspoint 棋子坐标
		 * @param chesscolor 棋子颜色
		 */
		public void putChess(Point chesspoint,byte chesscolor)
		{
			if(isInBoard(chesspoint)){
				chessboard[chesspoint.x][chesspoint.y] = chesscolor;
				repaint();
			}
		}
		
		/** 判断某一坐标是否在棋盘上  */
		public final boolean isInBoard(Point pt)
		{
			if (pt.x < 0 || pt.x >= CHESSBOARD_SIZE 
					|| pt.y < 0 || pt.y >= CHESSBOARD_SIZE)
			{
				return false;
			} else
			{
				return true;
			}
		}

		/** 物理坐标转换为棋盘上的逻辑坐标 */
		public Point ChessPoint(Point ImagePoint)
		{
			Point cp = new Point();
			cp.x = (ImagePoint.x - X_SET + GRID_SPACE) / GRID_SPACE - 1;
			cp.y = (ImagePoint.y - Y_SET + GRID_SPACE) / GRID_SPACE - 1;
			return cp;
		}

		/** 逻辑坐标转换为棋盘上图形绘制所需的物理坐标 */
		public Point ImagePoint(Point ChessPoint)
		{
			Point ip = new Point();
			ip.x = ChessPoint.x * GRID_SPACE + X_SET;
			ip.y = ChessPoint.y * GRID_SPACE + Y_SET;
			return ip;
		}
		
		/** 返回棋子颜色的字符串*/
		public final String getChessColor(byte chesscolor)
		{
			switch (chesscolor)
			{
			case BLACK:
				return "黑";
			case WHITE:
				return "白";
			default:
				return "无";
			}		
		}
	
		/** 判断棋盘上某点是否能落子 */
		public final boolean canPutChess(Point chesspoint)
		{
			if(isInBoard(chesspoint)
					&&(SELECT == chessboard[chesspoint.x][chesspoint.y]
					|| EMPTY == chessboard[chesspoint.x][chesspoint.y])){
				return true;
			}else{
				return false;
			}
		}
		/** 判断棋盘上是否有空位可以落子 */
		public final boolean haveSpace()
		{
			boolean have=false;
			for(int i=0;i<CHESSBOARD_SIZE && !have;i++)
				for(int j=0;j<CHESSBOARD_SIZE && !have;j++)
				{
					if(EMPTY==chessboard[i][j] || SELECT==chessboard[i][j]){
						have=true;
					}
				}
			return have;
		}
	}
	/* 游戏控制 */
	Player[] players;
	int current;// 当前下棋手

	Game game=null;
	
	/** 游戏控制类
	 * 控制游戏的开始，进行和结束的流程
	 */
	public class Game extends Thread
	{
		public boolean isPlaying=true;//指示游戏是否在进行中
		/**
		 * 开始游戏前的游戏初始化
		 */
		public void gameSet()
		{
			players = new Player[2];
			if(rbtRobots.isSelected()){
				players[0]= new Robot();
				players[1]= new Robot();
			}else{
				if (rbtManVSRobot.isSelected())
				{
					if (rbtPlayerFirst.isSelected())
					{
						players[0] = new Player();
						players[1] = new Robot();
					} else {
						players[0] = new Robot();
						players[1] = new Player();
					}
				} else {
					players[0] = new Player();
					players[1] = new Player();
				}
			}
			//设置玩家棋子颜色
			if (rbtBlack.isSelected())
			{
				players[0].chesscolor = ChessBoardPanel.BLACK;
				players[1].chesscolor = ChessBoardPanel.WHITE;
			} else {
				players[0].chesscolor = ChessBoardPanel.WHITE;
				players[1].chesscolor = ChessBoardPanel.BLACK;
			}
			panChessboard.busy=true;
			current = 0;
			isPlaying=true;
			panChessboard.clearBoard();
			panChessboard.setPlayerListener();
			panChessboard.repaint();
			lbCurrentPlayerChessColor.setText(
					panChessboard.getChessColor(players[current].chesscolor)+"棋");
		}
		/** 游戏结束 */
		public void gameOver()
		{
			int i;
			for (i = 0; i < players.length; i++)
			{
				players[i] = null;
			}
			System.gc();
			panChessboard.clearListener();
			isPlaying=false;
			lbCurrentPlayerChessColor.setText("结束");

		}		
		/** 游戏进行时 
		 * @流程  各玩家轮流落子下棋，下棋后判断是否有五子连成一线
		 * 若无，则判断棋盘是否还有空白的位置，若有，则游戏继续。
		 * 若有五子连成一线，则游戏以对应的玩家胜利结束。
		 * 若棋盘上无空白位置而无人胜，则游戏以平局结束。
		 */
		public void run()
		{
			gameSet();
			//玩家落子
			while(players[current].putingChess())
			{
				if (haveFiveInARow(players[current]))
				{
					JOptionPane.showMessageDialog(null,
							panChessboard.getChessColor(players[current].chesscolor) + "棋胜！",
							"游戏结束",
							JOptionPane.INFORMATION_MESSAGE);
					break;
				}
				if(!panChessboard.haveSpace()){
					JOptionPane.showMessageDialog(null,
							"黑白平局",
							"游戏结束",
							JOptionPane.INFORMATION_MESSAGE);					
					break;
				}
				for(int i=(current+1)%players.length;i!=current;i=(i+1)%players.length)
				{
					players[i].listenBoard(players[current].curChess);
				}
				current = (current + 1) % players.length;
				lbCurrentPlayerChessColor.setText(
						panChessboard.getChessColor(players[current].chesscolor)+"棋");

				panChessboard.busy=true;
			}
			gameOver();
		}	
		/**
		 * 判断游戏玩家落下某一棋子后，是否有连成五子
		 * @param zhao  玩家
		 * @param cp  棋子坐标
		 * @return 有连成五子则返回true，否则返回false
		 */
		public boolean haveFiveInARow(Player zhao)
		{
			//定义四个方向的斜率
			int dirc[][] = { { 1, 0 }, { 1, 1 }, { 0, 1 }, { -1, 1 } };
			int judge[] = { 0, 0, 0, 0 };
			int i, j, m;
			Point cP = new Point(zhao.curChess);
			m = 0;
			for (i = 0; i < 4; i++)
			{
				cP.translate(-4 * dirc[i][0], -4 * dirc[i][1]);
				for (j = 0; j < 9; j++)
				{
					if (panChessboard.isInBoard(cP)
							&& zhao.chesscolor == panChessboard.chessboard[cP.x][cP.y])
					{
						judge[i]++;
					} else {
						m = m < judge[i] ? judge[i] : m;
						judge[i] = 0;
					}
					cP.x += dirc[i][0];
					cP.y += dirc[i][1];
				}
				m = m < judge[i] ? judge[i] : m;
				cP.setLocation(zhao.curChess);
			}
			if (m >= 5)
				return true;
			else
				return false;
		}
	}
	/**
	 * 游戏玩家
	 */
	 class Player
	 {
		public byte chesscolor;
		public Point curChess=new Point();

		public void listenBoard(Point tPoint)
		{
			if(players[current]!=this){
				return;
			}
			curChess.setLocation(tPoint);
			panChessboard.busy=false;
		}
		public boolean putingChess()
		{
			while(panChessboard.busy && game.isPlaying){}
			if(!game.isPlaying){
				return false;
			}
			panChessboard.putChess(curChess, chesscolor);
			return true;
		}
	 }

	/**
	 * 电脑玩家
	 */
	 class Robot extends Player
	 {
		public final int[][] badboard =
					new int[ChessBoardPanel.CHESSBOARD_SIZE][ChessBoardPanel.CHESSBOARD_SIZE];
		 
		public final int[][] goodboard =
				new int[ChessBoardPanel.CHESSBOARD_SIZE][ChessBoardPanel.CHESSBOARD_SIZE];
		 
		public void listenBoard(Point tPoint)
		{
			goodboard[tPoint.x][tPoint.y]=players[current].chesscolor;
			badboard[tPoint.x][tPoint.y]=players[current].chesscolor;
			recompute(badboard,tPoint,players[current].chesscolor);
			recompute(goodboard,tPoint,chesscolor);
			panChessboard.busy=false;
		}

		/** 找寻算法最优点
		 * @return 最优点
		 */
		public Point findBestPoint()
		{
			Point bestP=new Point();
			Point tP=new Point();
			int i, j,minvalue=0,value,n=0;
			for(i=0;i<ChessBoardPanel.CHESSBOARD_SIZE;i++)
				for(j=0;j<ChessBoardPanel.CHESSBOARD_SIZE;j++)
				{
					tP.setLocation(i, j);
					if(panChessboard.canPutChess(tP)){
						value=goodboard[i][j]+badboard[i][j];
						if(minvalue>value){
							n=0;
							minvalue=value;
						}else if(minvalue==value){
							n++;
						}
					}
				}
			n=(int)(n*Math.random());
			for(i=0;i<ChessBoardPanel.CHESSBOARD_SIZE;i++)
				for(j=0;j<ChessBoardPanel.CHESSBOARD_SIZE;j++)
				{
					tP.setLocation(i, j);
					if(panChessboard.canPutChess(tP)){
						value=goodboard[i][j]+badboard[i][j];
						if(minvalue==value && 0==(n--)){
							bestP.setLocation(i,j);
							break;
						}
					}
				}
			return bestP;
		}
		
		/** 重新计算以某点的关联区域中的各个点的关联权值
		 * @param valueboard 关联权值矩阵
		 * @param tP 点坐标
		 * @param chesscolor 棋子颜色
		 */
		private void recompute(int[][] valueboard,Point tP,byte chesscolor)
		{
			int dirc[][] = { { 1, 0 }, { 1, 1 }, { 0, 1 }, { -1, 1 } };
			int i, j,value;
			Point cP = new Point(tP);
			for (i = 0; i < 4; i++)
			{
				cP.translate(-4 * dirc[i][0], -4 * dirc[i][1]);
				for (j = 0; j < 9; j++)
				{
					if (panChessboard.canPutChess(cP))
					{
						value=valueOfOnePoint(valueboard,cP,chesscolor);
						valueboard[cP.x][cP.y]=value;
					}
					cP.x += dirc[i][0];
					cP.y += dirc[i][1];
				}
				cP.setLocation(tP);
			}
			//延时
			for (i = 1; i < 1000; i++)
			{
				for (j = 1; j < 10000; j++)
				{
					value=i*j/j;
				}
			}
			
//调试用		for (i = 0; i < 15; i++)
//			{
//				for (j = 0; j < 15; j++)
//				{
//					System.out.printf("%3d", goodboard[i][j]);
//				}
//				System.out.print("   ");
//				for (j = 0; j < 15; j++)
//				{
//					System.out.printf("%3d", badboard[i][j]);
//				}
//				System.out.println();
//			}
//			System.out.println();

		}
		
		/** 计算某点的关联权值
		 * @param valueboard 关联权值矩阵
		 * @param tP 点坐标
		 * @param chesscolor 棋子颜色
		 * @return 关联权值
		 */
		private int valueOfOnePoint(int[][] valueboard,Point tP,byte chesscolor)
		{
			int dirc[][] = { { 1, 0 }, { 1, 1 }, { 0, 1 }, { -1, 1 } };
			int judge[] = { 0, 0, 0, 0 };
			int i, j, m;
			Point cP = new Point(tP);
			m = 0;
			for (i = 0; i < 4; i++)
			{
				cP.translate(-4 * dirc[i][0], -4 * dirc[i][1]);
				for (j = 0; j < 9; j++)
				{
					if (panChessboard.isInBoard(cP)
							&& (chesscolor == valueboard[cP.x][cP.y] 
							|| 0>=valueboard[cP.x][cP.y])
							)
					{
						if(0<valueboard[cP.x][cP.y]){
							judge[i]++;							
						}
					} else if(j<4){
						judge[i] = 0;
					} else {
						j=9;
					}
					cP.x += dirc[i][0];
					cP.y += dirc[i][1];
				}
				m=m<judge[i]?judge[i]:m;
				cP.setLocation(tP);
			}

			return -m*m*m;
		}

		/* (non-Javadoc)
		 * @see mainFrame.GameFrame.Player#putingChess()
		 */
		public boolean putingChess()
		{
			if(!game.isPlaying){
				return false;
			}
			curChess=findBestPoint();
			panChessboard.putChess(curChess, chesscolor);
			goodboard[curChess.x][curChess.y]=chesscolor;
			badboard[curChess.x][curChess.y]=chesscolor;
			recompute(goodboard,curChess,chesscolor);
			for(int i=(current+1)%players.length;i!=current;i=(i+1)%players.length)
			{
				recompute(badboard,curChess,players[i].chesscolor);				
			}
			return true;
		}
	 }
	
	/**
	 * @param 主程序
	 */
	public static void main(String[] args) throws Exception
	{
		GameFrame wzq = new GameFrame();
		wzq.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		wzq.setVisible(true);
	}

}
