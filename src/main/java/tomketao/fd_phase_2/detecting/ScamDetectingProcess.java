package tomketao.fd_phase_2.detecting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tomketao.fd_phase_2.data.FeatureKnowledge;
import tomketao.fd_phase_2.data.TrainingSetting;

public class ScamDetectingProcess {
	public static final Logger LOGGER = LoggerFactory.getLogger(ScamDetectingProcess.class);
	static FeatureKnowledge knowledge = new FeatureKnowledge();
	private static TrainingSetting trainingSetting;
	
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			LOGGER.error("usage: ScamTraningProcess <config-path> <input.path>");
			return;
		}
		
		trainingSetting = TrainingSetting.loadFromFile(args[0]);
		
		LOGGER.info(trainingSetting.getStoreMetaDataUrl());
		LOGGER.info(trainingSetting.getStoreFeatureDataUrl());
		
		knowledge.load(trainingSetting);

		detectingProcess(knowledge, trainingSetting, args[1]);
	}
	
	public static void detectingProcess(FeatureKnowledge knowledge, TrainingSetting trainingSetting, String inputFile) throws IOException {
		FileReader fileReader = new FileReader(inputFile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;

		while ((line = bufferedReader.readLine()) != null) {

			// read input to get recordid
			String[] fields = line.split(trainingSetting.getInputDelimiter());
			if (fields.length == 2) {
				Map<String, Float> result = knowledge.feature_probalities(fields[1], trainingSetting);
			}

			LOGGER.info(line);
		}

		// Always close files.
		fileReader.close();
		bufferedReader.close();
	}
}
