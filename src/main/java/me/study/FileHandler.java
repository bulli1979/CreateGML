package me.study;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.io.gml.GMLWriter;

public class FileHandler {
	File haltestellen;
	File fahrplan;

	public void initialize() {
		ClassLoader classLoader = getClass().getClassLoader();
		haltestellen = new File(classLoader.getResource("haltestellen.csv").getFile());
		fahrplan = new File(classLoader.getResource("fahrplan.csv").getFile());
	}

	public void writeGML() {
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";";
		String name;
		String didok;
		String lat;
		String lon;
		String[] geopos;

		try {
			br = new BufferedReader(new FileReader(haltestellen));
			int count = 0;
			OutputStream out = new FileOutputStream("sbb.gml");
	        final Graph graph = new TinkerGraph();
	        
			while ((line = br.readLine()) != null) {
				if (count > 0) {
					Vertex v = graph.addVertex(null);				
					
					String[] haltestelle = line.split(cvsSplitBy);
					name = haltestelle[0];
					didok = haltestelle[1];
					v.setProperty("key", didok);
					v.setProperty("label", name);
				
				}
				count++;

			}
			br.close();
			br = new BufferedReader(new FileReader(fahrplan));
		
			String currentLine = "-1";
			ArrayList<Vertex> linien = new ArrayList<Vertex>();
			while ((line = br.readLine()) != null) {
				String[] streckenPunkt = line.split(cvsSplitBy);
				if (count > 0) {
					if(currentLine.equals(streckenPunkt[5])){
						
						Vertex nach = getVertex(graph,streckenPunkt[0]);
						if(nach != null){
							Iterator<Vertex> it = linien.iterator();
							while(it.hasNext()){
								Vertex von = it.next();
								if(von!=null && nach != null){
									graph.addEdge(null, von, nach, "Linie: " + currentLine).setProperty("direction", "both");
								}
							}
							linien.add(nach);
						}
					}else{
						currentLine = streckenPunkt[5];
						linien.clear();
						Vertex von = getVertex(graph,streckenPunkt[0]);
						if(von!=null){
							linien.add(von);
						}
					}
				}
				count++;

			}
			br.close();
			GMLWriter writer = new GMLWriter(graph);
			writer.outputGraph(out);
			out.close();
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}

	}

	private Vertex getVertex(Graph graph,String key){
		Iterator<Vertex> it = graph.getVertices().iterator();
		while(it.hasNext()){
			Vertex v = it.next();
			if(v.getProperty("key") != null && v.getProperty("key").equals(key)){
				return v;
			}
		}
		return null;
	}
	
}
