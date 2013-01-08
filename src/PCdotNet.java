import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

public class PCdotNet
{
	public static int charID;
	public static String str;
	public static ObjectOutputStream out;
	public static ObjectInputStream in;
	public static ServerSocket server;
	public static Socket socket;

	public static String temp;

	public PCdotNet()
	{
	}

	public static void assign(int id)
	{
		charID = id;
	}

	public static void wipeFile(int wipe) throws Exception
	{
		URL url = new URL("http://tyuo9980.koding.com/noob.php?w=" + Integer.toString(wipe));
		url.openStream();
	}

	public static void send(String data) throws Exception
	{
		URL url = new URL("http://tyuo9980.koding.com/noob.php?s=" + data);
		url.openStream();
	}

	public static String retrieve() throws Exception
	{
		// creates new URL and BufferedReader to download information
		URL url = new URL("http://tyuo9980.koding.com/file.php");
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

		String temp;

		/*
		 * while ((temp = in.readLine()) != null) // reads line
		 * {
		 * str = temp;
		 * }
		 */

		if ((temp = in.readLine()) != null)
			return temp;

		return "";
	}

	public static char getChar(String s)
	{
		char ch = s.charAt(charID - 1);

		return ch;
	}

	public static String encrypt(String data)
	{
		char charArray[] = new char[data.length()];

		for (int i = 0; i < data.length(); i++)
		{
			charArray[i] = (char) (data.charAt(i) - 1);
		}

		return new String(charArray);
	}

	public static String decrypt(String data)
	{
		char charArray[] = new char[data.length()];

		for (int i = 0; i < data.length(); i++)
		{
			charArray[i] = (char) (data.charAt(i) + 1);
		}

		return new String(charArray);
	}

	public static void save(String s, String file) throws Exception
	{
		BufferedWriter wr = new BufferedWriter(new FileWriter(file, true));

		wr.write(s);
		wr.newLine();
		wr.flush();
		wr.close();
	}

	// host stuff
	public static void waitForConnection() throws Exception
	{
		System.out.println("waiting for connection...");
		socket = server.accept();
		System.out.println("connected to " + socket.getInetAddress().getHostName());
	}

	public static void setupStreams() throws Exception
	{
		try
		{
			out = new ObjectOutputStream(socket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(socket.getInputStream());
			System.out.println("setup complete");
		} catch (Exception e)
		{
		}
	}

	public static void closeStreams() throws Exception
	{
		out.close();
		in.close();
		socket.close();
		server.close();
	}

	public static void receiveMessage()
	{
		String message = "";
		temp = message;
		try
		{
			message = (String) in.readObject();

			if (temp != message)
			{
				System.out.println(message);
			}
		} catch (Exception e)
		{
			System.out.print(e);
		}
	}

	public static void sendMessage(String name, String s)
	{
		try
		{
			out.writeObject(name + ": " + s);
			out.flush();
			System.out.println(name + ": " + s);
		} catch (Exception e)
		{
			System.out.print(e);
		}
	}

	// client stuff
	public static void connectToServer(String ip) throws Exception
	{
		System.out.println("connecting...");
		socket = new Socket(InetAddress.getByName(ip), 1337);
		System.out.println("connected to " + socket.getInetAddress().getHostName());
	}

	public static void AA(int x1, int y1, int x2, int y2, PCPC c, Graphics g)
	{
		Color cr, clr = Color.BLACK;

		for (int y = y1; y <= y2; y++)
		{
			for (int x = x1; x <= x2; x++)
			{
				if (c.getPixelColor(x, y).equals(clr))
				{
					cr = c.getPixelColor(x, y - 1);
					if (!cr.equals(clr))
					{
						g.setColor(new Color(cr.getRed() - 50, cr.getGreen() - 50, cr.getBlue() - 50));
						g.drawLine(x, y - 1, x, y - 1);
					}

					cr = c.getPixelColor(x, y + 1);
					if (!cr.equals(clr))
					{
						g.setColor(new Color(cr.getRed() - 50, cr.getGreen() - 50, cr.getBlue() - 50));
						g.drawLine(x, y + 1, x, y + 1);
					}

					cr = c.getPixelColor(x - 1, y);
					if (!cr.equals(clr))
					{
						g.setColor(new Color(cr.getRed() - 50, cr.getGreen() - 50, cr.getBlue() - 50));
						g.drawLine(x - 1, y, x - 1, y);
					}

					cr = c.getPixelColor(x + 1, y);
					if (!cr.equals(clr))
					{
						g.setColor(new Color(cr.getRed() - 50, cr.getGreen() - 50, cr.getBlue() - 50));
						g.drawLine(x + 1, y, x + 1, y);
					}
				}
			}
		}
	}
}
