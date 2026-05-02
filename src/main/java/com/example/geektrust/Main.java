package com.example.geektrust;

import com.example.geektrust.exception.FamilyException;
import com.example.geektrust.service.Family;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
	public static void main(String[] args) throws IOException {
		// Cargar la familia desde el archivo data/family.txt (si existe) o caer
		// en un array mínimo embebido.
		Path familyPath = Path.of("data", "family.txt");
		String[] inputFamily;
		if (Files.exists(familyPath)) {
			List<String> lines = Files.readAllLines(familyPath);
			inputFamily = lines.toArray(new String[0]);
		} else {
			// Fallback: si no existe el archivo, usar datos embebidos mínimos
			inputFamily = new String[]{"Shan Anga Chit Male", "Shan Anga Ish Male"};
		}

		Family family = Family.getFamilyInstance(inputFamily);

		// Leer consultas: si se pasa argumento usar ese archivo, si no intentar data/queries.txt
		File file;
		if (args != null && args.length > 0) {
			file = new File(args[0]);
		} else {
			file = Path.of("data", "queries.txt").toFile();
		}

		if (!file.exists()) {
			System.err.println("No hay archivo de consultas para procesar: " + file.getAbsolutePath());
			return;
		}

		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			String query;
			while ((query = bufferedReader.readLine()) != null) {
				String[] command = query.split(" ");
				try {
					if (command.length == 3) {
						family.getRelationship(command[1], command[2]);
					} else if (command.length == 4) {
						family.addMember(command[1], command[2], command[3]);
					}
				} catch (FamilyException e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}
}
