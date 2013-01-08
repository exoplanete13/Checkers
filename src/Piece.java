public class Piece
{
	int gridX, gridY;
	boolean king;
	String color;

	public Piece(int X, int Y, String c)
	{
		gridX = X;
		gridY = Y;
		color = c;
		king = false;
	}
}
