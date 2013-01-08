import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;

import javax.imageio.ImageIO;

/* Peter Li
 * Checkers Game
 * 
 * This is a checkers game made by Peter Li.
 * You play as white and move diagonally.
 * Jump over enemy pieces to capture them.
 * Game ends when all pieces are captured.
 */

public class Checkers
{
	public static PCPC c;				// console object
	public static PCdotNet net;			// network object
	public static Graphics g;			// graphics object

	public static Image board;			// board image
	public static Image light;			// spotlight image
	public static Image black, bKing;	// black piece images
	public static Image white, wKing; 	// white piece images

	public static int numBlack = 12, numWhite = 12; // number of pieces
	public static int mX, mY;						// mouse coordinates
	public static int sX = 0, sY = 0; 				// selection coordinates
	public static int moveX, moveY; 				// movement coordinates
	public static int selection = -1, sel = -1; 	// selection states
	public static boolean selected;
	public static int turn = 1;
	public static String OPmoveInfo;				// opponent movement info
	public static String PLmoveInfo;				// player movement info
	public static Piece piece[] = new Piece[24]; 	// piece objects
	public static boolean moves[][] = new boolean[8][8]; // array of possible moves based on grid

	public static int fps = 60; 					// loop limiting vars
	public static float delay = 1000f / fps;

	public static void main(String args[]) throws Exception
	{
		c = new PCPC(856, 671);		// creates a new PCPC console
		net = new PCdotNet();		// creates a new PCdotNet object
		g = c.getGraphics();		// creates a new graphics object
		g.setColor(Color.white);	// sets default color to white

		// starts game
		init();
		start();
	}

	// initializes the game
	public static void init() throws Exception
	{
		System.out.println("Welcome to Checkers!");
		System.out.println("You are playing as White.");
		System.out.println("It is your turn. Please move.");

		// loads the images from file
		board = ImageIO.read(new File("image\\board.png"));
		light = ImageIO.read(new File("image\\light.png"));
		black = ImageIO.read(new File("image\\black.png"));
		bKing = ImageIO.read(new File("image\\bKing.png"));
		white = ImageIO.read(new File("image\\white.png"));
		wKing = ImageIO.read(new File("image\\wKing.png"));

		// places the pieces in the correct start positions
		for (int i = 0; i < 12; i++)
		{
			int x = (i * 2) % 8;
			int y = i * 2 / 8;
			int y2 = y + 5;

			if (y % 2 == 0)
			{
				piece[i] = new Piece(x, y2, "white");
				piece[i + 12] = new Piece(x + 1, y, "black");
			} else
			{
				piece[i] = new Piece(x + 1, y2, "white");
				piece[i + 12] = new Piece(x, y, "black");
			}
		}
	}

	// draws all the pieces to the screen
	public static void drawPieces()
	{
		for (int i = 0; i < 24; i++)
		{
			if (piece[i].color.equals("white")) // draws white pieces
			{
				if (piece[i].king)
					g.drawImage(wKing, piece[i].gridX * 75, piece[i].gridY * 75, null);
				else
					g.drawImage(white, piece[i].gridX * 75, piece[i].gridY * 75, null);
			} else
			// draws black pieces
			{
				if (piece[i].king)
					g.drawImage(bKing, piece[i].gridX * 75, piece[i].gridY * 75, null);
				else
					g.drawImage(black, piece[i].gridX * 75, piece[i].gridY * 75, null);
			}
		}
	}

	// draws the image of the board
	public static void drawBoard()
	{
		g.drawImage(board, 0, 0, null);
	}

	// draws the spotlight image effect
	public static void drawLight()
	{
		g.drawImage(light, mX - 600, mY - 600, null);
	}

	// resets variables every move
	public static void resetMove()
	{
		// sets variables to default values
		moveX = -1;
		moveY = -1;
		turn *= -1;
		selection = -1;

		// clears possible moves array
		for (int i = 0; i < 8; i++)
		{
			for (int j = 0; j < 8; j++)
				moves[j][i] = false;
		}
	}

	// highlights possible immediate moves
	public static void drawMoves(int s)
	{
		// possible moves for white pieces
		if (s >= 0 && s < 12)
		{
			// top left corner
			if (getAll(sX - 1, sY - 1) >= 0 && getEnemy(sX - 1, sY - 1) >= 0)
			{
				if (getAll(sX - 2, sY - 2) == -1)
				{
					g.drawRect((sX - 2) * 75, (sY - 2) * 75, 75, 75);

					if (sX - 2 >= 0 && sY - 2 >= 0)
						moves[sX - 2][sY - 2] = true;
				}
			} else if (getPiece(sX - 1, sY - 1) == -1)
			{
				g.drawRect((sX - 1) * 75, (sY - 1) * 75, 75, 75);

				if (sX - 1 >= 0 && sY - 1 >= 0)
					moves[sX - 1][sY - 1] = true;
			}

			// top right corner
			if (getAll(sX + 1, sY - 1) >= 0 && getEnemy(sX + 1, sY - 1) >= 0)
			{
				if (getAll(sX + 2, sY - 2) == -1)
				{
					g.drawRect((sX + 2) * 75, (sY - 2) * 75, 75, 75);

					if (sX + 2 < 8 && sY - 2 >= 0)
						moves[sX + 2][sY - 2] = true;
				}
			} else if (getPiece(sX + 1, sY - 1) == -1)
			{
				g.drawRect((sX + 1) * 75, (sY - 1) * 75, 75, 75);

				if (sX + 1 < 8 && sY - 1 >= 0)
					moves[sX + 1][sY - 1] = true;
			}

			if (piece[s].king)
			{
				// bottom left corner
				if (getAll(sX - 1, sY + 1) >= 0 && getEnemy(sX - 1, sY + 1) >= 0)
				{
					if (getAll(sX - 2, sY + 2) == -1)
					{
						g.drawRect((sX - 2) * 75, (sY + 2) * 75, 75, 75);

						if (sX - 2 >= 0 && sY + 2 < 8)
							moves[sX - 2][sY + 2] = true;
					}
				} else if (getPiece(sX - 1, sY + 1) == -1)
				{
					g.drawRect((sX - 1) * 75, (sY + 1) * 75, 75, 75);

					if (sX - 1 >= 0 && sY + 1 < 8)
						moves[sX - 1][sY + 1] = true;
				}

				// bottom right corner
				if (getAll(sX + 1, sY + 1) >= 0 && getEnemy(sX + 1, sY + 1) >= 0)
				{
					if (getAll(sX + 2, sY + 2) == -1)
					{
						g.drawRect((sX + 2) * 75, (sY + 2) * 75, 75, 75);

						if (sX + 2 < 8 && sY + 2 < 8)
							moves[sX + 2][sY + 2] = true;
					}
				} else if (getPiece(sX + 1, sY + 1) == -1)
				{
					g.drawRect((sX + 1) * 75, (sY + 1) * 75, 75, 75);

					if (sX + 1 < 8 && sY + 1 < 8)
						moves[sX + 1][sY + 1] = true;
				}
			}
		} else if (s >= 12) // possible movement for black pieces
		{
			// bottom left corner
			if (getAll(sX - 1, sY + 1) >= 0 && getPiece(sX - 1, sY + 1) >= 0)
			{
				if (getAll(sX - 2, sY + 2) == -1)
				{
					g.drawRect((sX - 2) * 75, (sY + 2) * 75, 75, 75);

					if (sX - 2 >= 0 && sY + 2 < 8)
						moves[sX - 2][sY + 2] = true;
				}
			} else if (getEnemy(sX - 1, sY + 1) == -1)
			{
				g.drawRect((sX - 1) * 75, (sY + 1) * 75, 75, 75);

				if (sX - 1 >= 0 && sY + 1 < 8)
					moves[sX - 1][sY + 1] = true;
			}

			// bottom right corner
			if (getAll(sX + 1, sY + 1) >= 0 && getPiece(sX + 1, sY + 1) >= 0)
			{
				if (getAll(sX + 2, sY + 2) == -1)
				{
					g.drawRect((sX + 2) * 75, (sY + 2) * 75, 75, 75);

					if (sX + 2 < 8 && sY + 2 < 8)
						moves[sX + 2][sY + 2] = true;
				}
			} else if (getEnemy(sX + 1, sY + 1) == -1)
			{
				g.drawRect((sX + 1) * 75, (sY + 1) * 75, 75, 75);

				if (sX + 1 < 8 && sY + 1 < 8)
					moves[sX + 1][sY + 1] = true;
			}

			if (piece[s].king)
			{
				// top left corner
				if (getAll(sX - 1, sY - 1) >= 0 && getPiece(sX - 1, sY - 1) >= 0)
				{
					if (getAll(sX - 2, sY - 2) == -1)
					{
						g.drawRect((sX - 2) * 75, (sY - 2) * 75, 75, 75);

						if (sX - 2 >= 0 && sY - 2 >= 0)
							moves[sX - 2][sY - 2] = true;
					}
				} else if (getEnemy(sX - 1, sY - 1) == -1)
				{
					g.drawRect((sX - 1) * 75, (sY - 1) * 75, 75, 75);

					if (sX - 1 >= 0 && sY - 1 >= 0)
						moves[sX - 1][sY - 1] = true;
				}

				// top right corner
				if (getAll(sX + 1, sY - 1) >= 0 && getPiece(sX + 1, sY - 1) >= 0)
				{
					if (getAll(sX + 2, sY - 2) == -1)
					{
						g.drawRect((sX + 2) * 75, (sY - 2) * 75, 75, 75);

						if (sX + 2 < 8 && sY - 2 >= 0)
							moves[sX + 2][sY - 2] = true;
					}
				} else if (getEnemy(sX + 1, sY - 1) == -1)
				{
					g.drawRect((sX + 1) * 75, (sY - 1) * 75, 75, 75);

					if (sX + 1 < 8 && sY - 1 >= 0)
						moves[sX + 1][sY - 1] = true;
				}
			}
		}
	}

	// moves the pieces
	public static void move(int x, int y, int s) throws Exception
	{
		if (turn == 1)
		{
			if (moves[x][y])
			{
				int a = piece[s].gridX - x;
				int b = piece[s].gridY - y;
				int c = getAll(piece[s].gridX - a / 2, piece[s].gridY - b / 2);

				// captures jumped piece
				if (Math.abs(a) == 2)
				{
					piece[c].gridX = -1;
					piece[c].gridY = -1;

					if (turn == 1)
						numBlack--;
					else
						numWhite--;
				}

				// assigns new location
				piece[s].gridX = x;
				piece[s].gridY = y;

				// checks if reached king
				if (turn == 1)
				{
					PLmoveInfo = (turn + "_" + x + "_" + y + "_" + s).toString();
					net.send(PLmoveInfo);

					if (piece[s].gridY == 0)
						piece[s].king = true;
				} else
				{
					if (piece[s].gridY == 7)
						piece[s].king = true;
				}

				// resets movement variables
				resetMove();
			}
		} else
		{
			int a = piece[s].gridX - x;
			int b = piece[s].gridY - y;
			int c = getAll(piece[s].gridX - a / 2, piece[s].gridY - b / 2);

			// captures jumped piece
			if (Math.abs(a) == 2)
			{
				piece[c].gridX = -1;
				piece[c].gridY = -1;

				if (turn == 1)
					numBlack--;
				else
					numWhite--;
			}

			// assigns new location
			piece[s].gridX = x;
			piece[s].gridY = y;

			// checks if reached king
			if (turn == 1)
			{
				PLmoveInfo = (turn + "_" + x + "_" + y + "_" + s).toString();
				net.send(PLmoveInfo);

				if (piece[s].gridY == 0)
					piece[s].king = true;
			} else
			{
				if (piece[s].gridY == 7)
					piece[s].king = true;
			}

			// resets movement variables
			resetMove();
		}
	}

	// returns the piece id of the selected piece
	public static int getAll(int mx, int my)
	{
		for (int i = 0; i < 24; i++)
		{
			if (piece[i].gridX == mx && piece[i].gridY == my)
				return i;
		}

		return -1;
	}

	// returns the piece id of the selected enemy piece
	public static int getEnemy(int mx, int my)
	{
		for (int i = 12; i < 24; i++)
		{
			if (piece[i].gridX == mx && piece[i].gridY == my)
				return i;
		}

		return -1;
	}

	// returns the piece id of the selected friendly piece
	public static int getPiece(int mx, int my)
	{
		for (int i = 0; i < 12; i++)
		{
			if (piece[i].gridX == mx && piece[i].gridY == my)
				return i;
		}

		return -1;
	}

	// gets the mouse input from user
	public static void getInput() throws Exception
	{
		// gets mouse coordinates
		mX = c.getMouseX();
		mY = c.getMouseY();

		if (turn == 1)
		{
			if (c.getClick())
			{
				// moves piece if one is selected
				if (selected)
				{
					moveX = mX / 75;
					moveY = mY / 75;
					selected = false;
					move(moveX, moveY, selection);
				} else
				// selects piece if none is selected
				{
					if (turn == 1)
						selection = getPiece(mX / 75, mY / 75);
					else
						selection = getEnemy(mX / 75, mY / 75);

					if (selection >= 0)
					{
						sX = mX / 75;
						sY = mY / 75;
						selected = true;
					}
				}
			}
		}

		// draws selection box outline
		if (selection >= 0)
			g.drawRect(sX * 75, sY * 75, 75, 75);
		else
			g.drawRect(mX / 75 * 75, mY / 75 * 75, 75, 75);
	}

	// retrieves movement info from server
	public static void getInfo() throws Exception
	{
		if (turn == -1)
		{
			OPmoveInfo = net.retrieve();	// gets info

			if (!OPmoveInfo.equals(""))
			{
				String s[] = OPmoveInfo.split("_");

				if (s[0].equals("-1"))
				{
					// moves piece
					move(Integer.parseInt(s[1]), Integer.parseInt(s[2]), Integer.parseInt(s[3]));
					net.wipeFile(1);
					System.out.println("Black moved. It is your turn.");
				}
			}
		}
	}

	// checks for if pieces are captured
	public static void winCheck()
	{
		if (numBlack == 0)
			g.drawString("WHITE WINS!", 260, 300);
		else if (numWhite == 0)
			g.drawString("BLACK WINS!", 260, 300);
	}

	// main game loop
	public static void start() throws Exception
	{
		long l = System.currentTimeMillis();
		long l2 = l;

		while (true)
		{
			Thread.sleep(1);

			long now = System.currentTimeMillis();

			if (now - l > delay)
			{
				l = now;

				c.cls();

				drawBoard();
				drawPieces();
				drawMoves(selection);
				drawLight();
				winCheck();
				getInput();

				c.ViewUpdate();		// updates screen
			}

			if (now - l2 > 1000)
			{
				l2 = now;

				getInfo();
			}
		}
	}
}
