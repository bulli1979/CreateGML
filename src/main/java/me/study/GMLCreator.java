package me.study;

public class GMLCreator {

	public static void main(String[] args) {
		FileHandler fileHandler = new FileHandler();
		fileHandler.initialize();
		fileHandler.writeGML();
	}

}
