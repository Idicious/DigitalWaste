package core;

import controller.Controller;
import filewalkers.WalkerThreadHandeler;
import gui.GUI;

public class Main 
{

	public static void main(String[] args) 
	{
		 WalkerThreadHandeler walker = new WalkerThreadHandeler();
		 GUI gui = new GUI();
		 
		 Controller.initialize(gui, walker);
	}
}
